package com.tay.stockmatchengine;

public class CancelledOrder {
	// 取消委托单单号
	private long cancelledOrderId;
	// 原委托单号
	private long orderId;
	// 交易商id
	private String traderId;
	// 商品代码
	private String commodityCode;
	// 取消时间
	private long time;
	// 下单价格
	private long price;
	// 委托数量
	private int quantity;
	// 未成交数量
	private int remainedQuantity;
	// 买卖方向 true-买， false-卖
	private boolean isBuy;
	// 是否市价下单
	private boolean isMarketPrice;

	public CancelledOrder(long cancelledOrderId, long orderId, String traderId, String commodityCode, long time,
			long price, int quantity, int remainedQuantity, boolean isBuy, boolean isMarketPrice) {
		this.cancelledOrderId = cancelledOrderId;
		this.orderId = orderId;
		this.traderId = traderId;
		this.commodityCode = commodityCode;
		this.time = time;
		this.price = price;
		this.quantity = quantity;
		this.remainedQuantity = remainedQuantity;
		this.isBuy = isBuy;
		this.isMarketPrice = isMarketPrice;
	}

	public CancelledOrder(long orderId, String traderId, String commodityCode, long time, long price, int quantity,
			int remainedQuantity, boolean isBuy, boolean isMarketPrice) {
		this.orderId = orderId;
		this.traderId = traderId;
		this.commodityCode = commodityCode;
		this.time = time;
		this.price = price;
		this.quantity = quantity;
		this.remainedQuantity = remainedQuantity;
		this.isBuy = isBuy;
		this.isMarketPrice = isMarketPrice;
	}

	public long getCancelledOrderId() {
		return cancelledOrderId;
	}

	public long getOrderId() {
		return orderId;
	}

	public String getTraderId() {
		return traderId;
	}

	public String getCommodityCode() {
		return commodityCode;
	}

	public long getTime() {
		return time;
	}

	public long getPrice() {
		return price;
	}

	public int getQuantity() {
		return quantity;
	}

	public int getRemainedQuantity() {
		return remainedQuantity;
	}

	public boolean isBuy() {
		return isBuy;
	}

	public boolean isMarketPrice() {
		return isMarketPrice;
	}
}
