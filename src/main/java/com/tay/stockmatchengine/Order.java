package com.tay.stockmatchengine;

public class Order {
	// 委托单号
	private long orderId;
	// 交易商id
	private String traderId;
	// 商品代码
	private String commodityCode;
	// 下单时间
	private long time;
	// 下单价格
	private long price;
	// 下单数量
	private int quantity;
	// 未成交数量
	private int remainedQuantity;
	// 买卖方向 true-买， false-卖
	private boolean isBuy;
	// 是否市价下单
	private boolean isMarketPrice;

	public Order(long orderId, String tradeId, String commodityCode, long time, long price, int quantity,
			int remainedQuantity, boolean isBuy, boolean isMarketPrice) {
		this.orderId = orderId;
		this.traderId = tradeId;
		this.commodityCode = commodityCode;
		this.time = time;
		this.price = price;
		this.quantity = quantity;
		this.remainedQuantity = remainedQuantity;
		this.isBuy = isBuy;
		this.isMarketPrice = isMarketPrice;
	}
	
	public Order(String tradeId, String commodityCode, long time, long price, int quantity,
			int remainedQuantity, boolean isBuy, boolean isMarketPrice) {
		this.traderId = tradeId;
		this.commodityCode = commodityCode;
		this.time = time;
		this.price = price;
		this.quantity = quantity;
		this.remainedQuantity = remainedQuantity;
		this.isBuy = isBuy;
		this.isMarketPrice = isMarketPrice;
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

	public boolean isBuy() {
		return isBuy;
	}

	public boolean isMarketPrice() {
		return isMarketPrice;
	}

	public long getOrderId() {
		return orderId;
	}

	public int getRemainedQuantity() {
		return remainedQuantity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((commodityCode == null) ? 0 : commodityCode.hashCode());
		result = prime * result + (isBuy ? 1231 : 1237);
		result = prime * result + (isMarketPrice ? 1231 : 1237);
		result = prime * result + (int) (orderId ^ (orderId >>> 32));
		long temp;
		temp = Double.doubleToLongBits(price);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + quantity;
		result = prime * result + remainedQuantity;
		result = prime * result + (int) (time ^ (time >>> 32));
		result = prime * result + ((traderId == null) ? 0 : traderId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Order other = (Order) obj;
		if (commodityCode == null) {
			if (other.commodityCode != null)
				return false;
		} else if (!commodityCode.equals(other.commodityCode))
			return false;
		if (isBuy != other.isBuy)
			return false;
		if (isMarketPrice != other.isMarketPrice)
			return false;
		if (orderId != other.orderId)
			return false;
		if (Double.doubleToLongBits(price) != Double.doubleToLongBits(other.price))
			return false;
		if (quantity != other.quantity)
			return false;
		if (remainedQuantity != other.remainedQuantity)
			return false;
		if (time != other.time)
			return false;
		if (traderId == null) {
			if (other.traderId != null)
				return false;
		} else if (!traderId.equals(other.traderId))
			return false;
		return true;
	}
}
