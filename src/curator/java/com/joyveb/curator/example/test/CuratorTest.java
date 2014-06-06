package com.joyveb.curator.example.test;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.RetryOneTime;
import org.junit.Test;

/**
 * Copyright © 2014畅享互联.
 * 
 * @Title: Test.java
 * @Prject: CassZkTest
 * @Package: com.joyveb.curator.lock.test
 * @date: 2014年5月28日 下午9:05:09
 * @author: yangqiju
 */
public class CuratorTest {
	private static final String LOCK_PATH = "/locks/our-lock";

	@Test
	public void test() throws Exception {
		String connectString = "192.168.3.167";
		final CuratorFramework client = CuratorFrameworkFactory.newClient(connectString, new RetryOneTime(1));
		client.start();
		final InterProcessMutex lock = new InterProcessMutex(client, LOCK_PATH);
		lock.acquire();
		System.out.println("test");
		lock.release();
	}
}
