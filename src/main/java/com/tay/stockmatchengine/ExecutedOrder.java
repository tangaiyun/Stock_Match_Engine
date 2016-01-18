package com.tay.stockmatchengine;

public class ExecutedOrder {
	// 成交id
	private long executedOrderId;
	// 商品代码
	private String commodityCode;
	// 买方id
	private String buyerId;
	// 卖房id
	private String sellerId;
	// 买方委托id
	private long bidOrderId;
	// 卖方委托id
	private long askOrderId;
	// 成交时间
	private long dealTime;
	// 成交价格
	private double dealPrice;
	// 成交数量
	private int quantity;

	public ExecutedOrder(String commodityCode, String buyerId, String sellerId, long bidOrderId, long askOrderId,
			long dealTime, double dealPrice, int quantity) {
		this.commodityCode = commodityCode;
		this.buyerId = buyerId;
		this.sellerId = sellerId;
		this.bidOrderId = bidOrderId;
		this.askOrderId = askOrderId;
		this.dealTime = dealTime;
		this.dealPrice = dealPrice;
		this.quantity = quantity;
	}

	public ExecutedOrder(long executedOrderId, String commodityCode, String buyerId, String sellerId, long bidOrderId,
			long askOrderId, long dealTime, double dealPrice, int quantity) {
		this.executedOrderId = executedOrderId;
		this.commodityCode = commodityCode;
		this.buyerId = buyerId;
		this.sellerId = sellerId;
		this.bidOrderId = bidOrderId;
		this.askOrderId = askOrderId;
		this.dealTime = dealTime;
		this.dealPrice = dealPrice;
		this.quantity = quantity;
	}

	public long getBidOrderId() {
		return bidOrderId;
	}

	public long getAskOrderId() {
		return askOrderId;
	}

	public String getCommodityCode() {
		return commodityCode;
	}

	public String getBuyerId() {
		return buyerId;
	}

	public String getSellerId() {
		return sellerId;
	}

	public long getDealTime() {
		return dealTime;
	}

	public double getDealPrice() {
		return dealPrice;
	}

	public int getQuantity() {
		return quantity;
	}

	public long getExecutedOrderId() {
		return executedOrderId;
	}
}
