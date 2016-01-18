package com.tay.stockmatchengine;

public interface OrderHandler<T> {
	public void handle(T order);
}
