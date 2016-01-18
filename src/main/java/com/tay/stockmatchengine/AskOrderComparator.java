package com.tay.stockmatchengine;

import java.util.Comparator;

public class AskOrderComparator implements Comparator<Order>{

	public int compare(Order o1, Order o2) {
		int retval = 0;
		//市价优先
		if(o1.isMarketPrice() && !o2.isMarketPrice()) {
			retval = -1;
		}
		else if(o2.isMarketPrice() && !o1.isMarketPrice()) {
			retval = 1;
		}
		//同时是市价 
		else if (o1.isMarketPrice() && o2.isMarketPrice()) {
			//先时间优先，先来的排在前面
			if(o1.getTime() != o2.getTime()) {
				retval = (int) (o1.getTime() - o2.getTime());
			}
			//时间相同，数量大的排在前面
			else {
				retval = o2.getQuantity() - o1.getQuantity();
			}
		}
		//如果都是指价卖单
		else if(!o1.isMarketPrice() && !o2.isMarketPrice()) {
			//首先比较卖价，价格低的排在前面
			if(o1.getPrice() != o2.getPrice()) {
				if((o1.getPrice() - o2.getPrice()) < 0) {
					retval = -1;
				}
				else {
					retval = 1;
				}
			}
			//如果价格相同，比较时间，时间先的排在前面
			else{
				if(o1.getTime() != o2.getTime()) {
					retval = (int) (o1.getTime() - o2.getTime());
				}
				//时间相同，数量大的排在前面
				else {
					retval = o2.getQuantity() - o1.getQuantity();
				}
			}
		}
		return retval;
	}

}
