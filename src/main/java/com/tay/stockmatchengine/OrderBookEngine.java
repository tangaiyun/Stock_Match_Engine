package com.dyyt.matchengine;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;

public class OrderBookEngine {
	// 买委托队列
	private final TreeSet<Order> bidOffers = new TreeSet<Order>(new BidOrderComparator());
	// 卖委托队列
	private final TreeSet<Order> askOffers = new TreeSet<Order>(new AskOrderComparator());
	// 成交记录队列
	private final List<ExecutedOrderHandler> executedOrderHandlers = new ArrayList<ExecutedOrderHandler>();
	// 委托撤销队列
	private final List<CancelledOrderHandler> cancelledOrderHandlers = new ArrayList<CancelledOrderHandler>();
	
	private final AtomicReference<Double> lastBargainPrice = new AtomicReference<Double>(new Double(0));
	
	public void addExecutedOrderHandler(ExecutedOrderHandler eOrderHandler) {
		executedOrderHandlers.add(eOrderHandler);
	}
	
	public void addCancelledOrderHandler(CancelledOrderHandler cOrderHandler) {
		cancelledOrderHandlers.add(cOrderHandler);
	}
	
	
	// 增加买向委托到队列
	private synchronized void addBidOrder(Order order) {
		if (order.isBuy()) {
			bidOffers.add(order);
		} else {
			throw new java.lang.IllegalArgumentException("not bid order");
		}
	}

	// 从买向委托队列里移除委托
	private synchronized void removeBidOrder(Order order) {
		bidOffers.remove(order);
	}

	// 增加委托到卖向队列
	private synchronized void addAskOffer(Order order) {
		if (!order.isBuy()) {
			askOffers.add(order);
		} else {
			throw new java.lang.IllegalArgumentException("not bid order");
		}
	}

	// 从卖向队列里移除委托
	private synchronized void removeAskOrder(Order order) {
		askOffers.remove(order);
	}
	
	/**
	 * 
	 * @param lastPrice 最近一笔成交价
	 * @param bidPrice 买价
	 * @param askPrice 卖价
	 * @return
	 */
	private double getDealPrice(double lastPrice, double bidPrice, double askPrice) {
		if(lastPrice <0 || bidPrice<0 || askPrice<0 || bidPrice<askPrice) {
			throw new java.lang.IllegalArgumentException();
		}
		double dealPrice = 0;
		//当买入价等于卖出价时，成交价就是买入价或卖出价
		if(bidPrice == askPrice) {
			dealPrice =  bidPrice;
		}
		//如果前一笔成交价低于或等于卖出价，则最新成交价就是卖出价
		else if(lastPrice <= askPrice) {
			dealPrice =  askPrice;
		}
		//如果前一笔成交价高于或等于买入价，则最新成交价就是买入价
		else if(lastPrice >= bidPrice) {
			dealPrice =  bidPrice;
		}
		//如果前一笔成交价在卖出价与买入价之间，则最新成交价就是前一笔的成交价
		else if(askPrice<lastPrice && lastPrice<bidPrice) {
			dealPrice =  lastPrice;
		}
		return dealPrice;
	}
	/**
	 * 接受新委托
	 * @param order
	 */
	public synchronized void receiveOrder(Order order) {
		boolean isBuy = order.isBuy();
		if (isBuy) {
			// 新进委托是买向委托
			Order inputBidOrder = order;
			int inputBidQuantity = inputBidOrder.getQuantity();
			double inputBidPrice = inputBidOrder.getPrice();
			for (Order askOrder : askOffers) {
				double askPrice = askOrder.getPrice();
				int askQuantity = askOrder.getQuantity();
				// 买方价格大于卖方价，成交条件成立
				if (inputBidQuantity > 0 && inputBidPrice >= askPrice) {
					// 新买方委托的数量大于当前一个卖方委托数量，此卖方委托将被完全消耗掉
					if (inputBidQuantity >= askQuantity) {
						inputBidQuantity = inputBidQuantity - askQuantity;
						double lastPrice = lastBargainPrice.get();
						// 计算成交价格
						double dealPrice = getDealPrice(lastPrice, inputBidOrder.getPrice(), askOrder.getPrice());
						// 生成成交记录
						ExecutedOrder exeOrder = new ExecutedOrder(inputBidOrder.getCommodityCode(),
								inputBidOrder.getTraderId(), askOrder.getTraderId(), inputBidOrder.getOrderId(),
								askOrder.getOrderId(), System.currentTimeMillis(), dealPrice, askQuantity);
						for(ExecutedOrderHandler eHandler : executedOrderHandlers) {
							eHandler.handle(exeOrder);
						}
						// 把卖单从队列里移除
						removeAskOrder(askOrder);
						//更新最后成交价
						lastBargainPrice.compareAndSet(lastPrice, dealPrice);
					}
					// 当前循环的卖单数量大于新进买单的数量，卖单将会被部分消耗
					else {
						double lastPrice = lastBargainPrice.get();
						// 计算成交价格
						double dealPrice = getDealPrice(lastPrice, inputBidOrder.getPrice(), askOrder.getPrice());
						// 生成成交记录
						ExecutedOrder exeOrder = new ExecutedOrder(inputBidOrder.getCommodityCode(),
								inputBidOrder.getTraderId(), askOrder.getTraderId(), inputBidOrder.getOrderId(),
								askOrder.getOrderId(), System.currentTimeMillis(), dealPrice, inputBidQuantity);
						for(ExecutedOrderHandler eHandler : executedOrderHandlers) {
							eHandler.handle(exeOrder);
						}
						// 移除原有的卖单
						removeAskOrder(askOrder);
						//更新最后成交价
						lastBargainPrice.compareAndSet(lastPrice, dealPrice);
						// 把当前卖单中剩余的数目形成一个新的卖单放入卖单队列
						int leftQuantity = askOrder.getQuantity() - inputBidQuantity;
						Order leftAskOrder = new Order(askOrder.getOrderId(), askOrder.getTraderId(),
								askOrder.getCommodityCode(), askOrder.getTime(), askOrder.getPrice(),
								askOrder.getQuantity(), leftQuantity, askOrder.isBuy(), askOrder.isMarketPrice());
						addAskOffer(leftAskOrder);
						inputBidQuantity = 0;
						break;
					}
				}
			}
			// 新进的买向委托无法完全消耗，剩余数量形成新的买单放入买单队列
			if (inputBidQuantity > 0) {
				Order leftBidOrder = new Order(inputBidOrder.getOrderId(), inputBidOrder.getTraderId(),
						inputBidOrder.getCommodityCode(), inputBidOrder.getTime(), inputBidOrder.getPrice(),
						inputBidOrder.getQuantity(), inputBidQuantity, inputBidOrder.isBuy(),
						inputBidOrder.isMarketPrice());
				addBidOrder(leftBidOrder);
			}
		} else {
			// 新进委托是买向委托
			Order inputAskOrder = order;
			int inputAskQuantity = inputAskOrder.getQuantity();
			double inputAskPrice = inputAskOrder.getPrice();
			for (Order bidOrder : bidOffers) {
				double bidPrice = bidOrder.getPrice();
				int bidQuantity = bidOrder.getQuantity();
				// 买方价格大于卖方价，成交条件成立
				if (inputAskQuantity > 0 && inputAskPrice <= bidPrice) {
					// 新卖方委托的数量大于当前一个买方委托数量，此买方委托将被完全消耗掉
					if (inputAskQuantity >= bidQuantity) {
						inputAskQuantity = inputAskQuantity - bidQuantity;
						double lastPrice = lastBargainPrice.get();
						// 计算成交价格
						double dealPrice = getDealPrice(lastPrice, bidOrder.getPrice(), inputAskOrder.getPrice());
						// 生成成交记录
						ExecutedOrder exeOrder = new ExecutedOrder(inputAskOrder.getCommodityCode(),
								bidOrder.getTraderId(), inputAskOrder.getTraderId(), bidOrder.getOrderId(),
								inputAskOrder.getOrderId(), System.currentTimeMillis(), dealPrice, bidQuantity);
						for(ExecutedOrderHandler eHandler : executedOrderHandlers) {
							eHandler.handle(exeOrder);
						}
						// 把买单从队列里移除
						removeBidOrder(bidOrder);
						//更新最后成交价
						lastBargainPrice.compareAndSet(lastPrice, dealPrice);
					}
					// 当前循环的买单数量大于新进卖单的数量，买单将会被部分消耗
					else {
						double lastPrice = lastBargainPrice.get();
						// 计算成交价格
						double dealPrice = getDealPrice(lastPrice, bidOrder.getPrice(), inputAskOrder.getPrice());
						// 生成成交记录
						ExecutedOrder exeOrder = new ExecutedOrder(inputAskOrder.getCommodityCode(),
								bidOrder.getTraderId(), inputAskOrder.getTraderId(), bidOrder.getOrderId(),
								inputAskOrder.getOrderId(), System.currentTimeMillis(), dealPrice, inputAskQuantity);
						for(ExecutedOrderHandler eHandler : executedOrderHandlers) {
							eHandler.handle(exeOrder);
						}
						// 移除原有的买单
						removeBidOrder(bidOrder);
						//更新最后成交价
						lastBargainPrice.compareAndSet(lastPrice, dealPrice);
						// 把当前买单中剩余的数目形成一个新的买单放入买单队列
						int leftQuantity = bidOrder.getQuantity() - inputAskQuantity;
						Order leftBidOrder = new Order(bidOrder.getOrderId(), bidOrder.getTraderId(),
								bidOrder.getCommodityCode(), bidOrder.getTime(), bidOrder.getPrice(),
								bidOrder.getQuantity(), leftQuantity, bidOrder.isBuy(), bidOrder.isMarketPrice());
						addBidOrder(leftBidOrder);
						inputAskQuantity = 0;
						break;
					}
				}
			}
			// 新进的卖向委托无法完全消耗，剩余数量形成新的卖单放入卖单队列
			if (inputAskQuantity > 0) {
				Order leftAskOrder = new Order(inputAskOrder.getOrderId(), inputAskOrder.getTraderId(),
						inputAskOrder.getCommodityCode(), inputAskOrder.getTime(), inputAskOrder.getPrice(),
						inputAskOrder.getQuantity(), inputAskQuantity, inputAskOrder.isBuy(),
						inputAskOrder.isMarketPrice());
				addBidOrder(leftAskOrder);
			}
		}
	}
	/**
	 * 撤销已有委托
	 * @param orderId
	 */
	public synchronized void cancelOrder(long orderId) {
		//遍历卖单队列
		for (Order askOrder : askOffers) {
			if (askOrder.getOrderId() == orderId) {
				//生成撤单记录
				CancelledOrder cancelledOrder = new CancelledOrder(askOrder.getOrderId(), askOrder.getTraderId(),
						askOrder.getCommodityCode(), System.currentTimeMillis(), askOrder.getPrice(),
						askOrder.getQuantity(), askOrder.getRemainedQuantity(), askOrder.isBuy(),
						askOrder.isMarketPrice());
				for(CancelledOrderHandler cHandler : cancelledOrderHandlers) {
					cHandler.handle(cancelledOrder);
				}
				//从卖单队列里面移除委托
				removeAskOrder(askOrder);
				break;
			}
		}
		//遍历买单队列
		for (Order bidOrder : bidOffers) {
			if (bidOrder.getOrderId() == orderId) {
				//生成撤单记录
				CancelledOrder cancelledOrder = new CancelledOrder(bidOrder.getOrderId(), bidOrder.getTraderId(),
						bidOrder.getCommodityCode(), System.currentTimeMillis(), bidOrder.getPrice(),
						bidOrder.getQuantity(), bidOrder.getRemainedQuantity(), bidOrder.isBuy(),
						bidOrder.isMarketPrice());
				for(CancelledOrderHandler cHandler : cancelledOrderHandlers) {
					cHandler.handle(cancelledOrder);
				}
				//从买单队列里移除该委托
				removeBidOrder(bidOrder);
				break;
			}
		}
	}

	// 卖价级数
	public int getAskLevel() {
		return askOffers.size();
	}

	// 买家级数
	public int getBidLevel() {
		return bidOffers.size();
	}

	// 得到一个价格之上的所有买向委托总量
	public int getBidQuantity(double bestPrice) {
		int bidQuantity = 0;
		for (Order order : bidOffers) {
			if (order.isBuy()) {
				if (order.getPrice() > bestPrice) {
					bidQuantity += order.getQuantity();
				}
			}
		}

		return bidQuantity;
	}

	// 得到一个价格之下的所有卖向委托总量
	public int getAskQuantity(double bestPrice) {
		int askQuantity = 0;
		for (Order order : askOffers) {
			if (!order.isBuy()) {
				if (order.getPrice() < bestPrice) {
					askQuantity += order.getQuantity();
				}
			}
		}
		return askQuantity;
	}

	// 得到所有买向委托总量
	public int getBidQuantity() {
		return getBidQuantity(Integer.MAX_VALUE);
	}

	// 得到所有卖向委托总量
	public int getAskQuantity() {
		return getAskQuantity(Integer.MAX_VALUE);
	}

	// 引擎重置
	public void reset() {
		askOffers.clear();
		bidOffers.clear();
	}

}
