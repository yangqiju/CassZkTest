package com.joyveb.curator.lock.test;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;
import org.apache.curator.retry.RetryOneTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Copyright © 2014畅享互联.
 * 
 * @Title: CuratorDistributedAtomicLongTest.java
 * @Prject: CassZkTest
 * @Package: com.joyveb.curator.lock.test
 * @date: 2014年5月29日 上午12:06:48
 * @author: yangqiju
 * @Description: TODO
 */
public class CuratorDistributedAtomicLongTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test()  {
		try{
			String connectString = "192.168.3.167:2181";
			CuratorFramework client = CuratorFrameworkFactory.newClient(connectString, new RetryOneTime(1));
			client.start();
//			client.create().forPath("/counter", "foo".getBytes());
			DistributedAtomicLong dal = new DistributedAtomicLong(client,"/counter", new RetryOneTime(1));
			AtomicValue<Long> result = dal.add(10L);
			if (result.succeeded()) {
				// success
				System.out.println("success");
			}
			System.out.println("post::"+result.postValue());
			System.out.println("pre::"+result.preValue());
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
