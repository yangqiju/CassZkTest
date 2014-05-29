package com.joyveb.zookeeper.lock.test;

import java.io.IOException;
import java.util.Random;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.recipes.lock.WriteLock;

/**
 * Copyright © 2014畅享互联.
 * 
 * @Title: LockHandler.java
 * @Prject: DBCassandra
 * @Package: com.joyveb.db.cassandra.lock
 * @date: 2014年5月27日 下午6:36:00
 * @author: yangqiju
 */
public class LockHandler {
	private String[] ips = null;
	private Random random = new Random();
	private int timeout = 5000;

	public LockHandler(String... ips) {
		this.ips = ips;
	}

	public WriteLock getLock(String key) throws IOException {
		key = "/"+key;
		String ip = ips[random.nextInt(ips.length)];
		Watcher watcher = new Watcher() {
			@Override
			public void process(WatchedEvent event) {
			}
		};
		ZooKeeper keeper = new ZooKeeper(ip, timeout, watcher);
		return new WriteLock(keeper, key, null);
	}
}
