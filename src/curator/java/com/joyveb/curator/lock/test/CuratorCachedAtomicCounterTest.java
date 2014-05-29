package com.joyveb.curator.lock.test;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;
import org.apache.curator.retry.RetryOneTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**  
 * Copyright © 2014畅享互联.
 *
 * @Title: CuratorCachedAtomicCounterTEst.java
 * @Prject: CassZkTest
 * @Package: com.joyveb.curator.lock.test
 * @date: 2014年5月29日 上午12:03:20
 * @author: yangqiju 
 * @Description: TODO
 */
public class CuratorCachedAtomicCounterTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		CuratorFramework client = CuratorFrameworkFactory.newClient("", new RetryOneTime(1));
        client.start();
        DistributedAtomicLong dal = new DistributedAtomicLong(client, "/", null, null);
	}

}
