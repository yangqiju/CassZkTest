package com.joyveb.curator.lock.test;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.shared.SharedCount;
import org.apache.curator.retry.RetryOneTime;
import org.junit.Test;

/**  
 * Copyright © 2014畅享互联.
 *
 * @Title: CuratorSharedCountTest.java
 * @Prject: CassZkTest
 * @Package: com.joyveb.curator.lock.test
 * @date: 2014年5月28日 下午11:55:48
 * @author: yangqiju 
 * @Description: TODO
 */
public class CuratorSharedCountTest {

	@Test
	public void test() throws Exception{
		 CuratorFramework client = CuratorFrameworkFactory.newClient("", new RetryOneTime(1));
         client.start();
         SharedCount count = new SharedCount(client, "/count", 10);
         count.start();
         int num = count.getCount();
         num = num + 10;
         count.setCount(num);
         count.close();
	}
}
