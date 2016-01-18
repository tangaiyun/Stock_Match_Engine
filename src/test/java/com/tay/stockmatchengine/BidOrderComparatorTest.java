package com.tay.stockmatchengine;

import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

public class BidOrderComparatorTest {

	@Test
	public void test() {
		BidOrderComparator bc = new BidOrderComparator();
		TreeSet<Order> treeSet = new TreeSet<Order>(bc);
		// 市价优先
		Order order1 = new Order("001", "PUER1", System.currentTimeMillis(), 95.12, 300, 0, true, true);
		Order order2 = new Order("001", "PUER1", System.currentTimeMillis(), 95.12, 300, 0, true, false);

		treeSet.add(order2);
		treeSet.add(order1);
		Assert.assertEquals(order1, treeSet.first());
		treeSet.clear();
		// 同是市价，时间优先，先来的排在前面
		long time = System.currentTimeMillis();
		Order order3 = new Order("001", "PUER1", time + 5, 95.12, 300, 0, true, true);
		Order order4 = new Order("001", "PUER1", time + 10, 95.12, 300, 0, true, true);
		Order order5 = new Order("001", "PUER1", time, 95.12, 300, 0, true, true);
		treeSet.add(order3);
		treeSet.add(order4);
		treeSet.add(order5);
		Assert.assertEquals(order5, treeSet.first());
		treeSet.clear();

		// 同是市价，时间相同，数量多得排前面

		time = System.currentTimeMillis();
		Order order6 = new Order("001", "PUER1", time, 95.12, 500, 0, true, true);
		Order order7 = new Order("001", "PUER1", time, 95.12, 300, 0, true, true);
		Order order8 = new Order("001", "PUER1", time, 95.12, 800, 0, true, true);
		treeSet.add(order6);
		treeSet.add(order7);
		treeSet.add(order8);
		Assert.assertEquals(order8, treeSet.first());
		treeSet.clear();
		// 同是指价,价格高的排在前面
		Order order9 = new Order("001", "PUER1", time, 93.12, 500, 0, true, false);
		Order order10 = new Order("001", "PUER1", time, 97.12, 300, 0, true, false);
		Order order11 = new Order("001", "PUER1", time, 95.12, 800, 0, true, false);
		treeSet.add(order9);
		treeSet.add(order10);
		treeSet.add(order11);
		System.out.println(treeSet);
		Assert.assertEquals(order10, treeSet.first());
		treeSet.clear();
		// 同是指价，价格相同，时间先的排前面
		time = System.currentTimeMillis();
		Order order12 = new Order("001", "PUER1", time + 11, 97.00, 500, 0, true, false);
		Order order13 = new Order("001", "PUER1", time - 3, 97.00, 300, 0, true, false);
		Order order14 = new Order("001", "PUER1", time, 97.00, 800, 0, true, false);
		treeSet.add(order12);
		treeSet.add(order13);
		treeSet.add(order14);
		Assert.assertEquals(order13, treeSet.first());
		treeSet.clear();
		// 同是指价，价格相同，时间相同，数量大的排前面
		time = System.currentTimeMillis();
		Order order15 = new Order("001", "PUER1", time, 97.00, 700, 0, true, false);
		Order order16 = new Order("001", "PUER1", time, 97.00, 1000, 0, true, false);
		Order order17 = new Order("001", "PUER1", time, 97.00, 300, 0, true, false);
		treeSet.add(order15);
		treeSet.add(order16);
		treeSet.add(order17);
		Assert.assertEquals(order16, treeSet.first());
		treeSet.clear();
	}

}
