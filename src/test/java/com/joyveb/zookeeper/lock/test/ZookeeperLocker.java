package com.joyveb.zookeeper.lock.test;

import java.io.IOException;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.recipes.lock.LockListener;
import org.apache.zookeeper.recipes.lock.WriteLock;

public class ZookeeperLocker {

	protected String dir = "/" + getClass().getName();
	protected String host_port = "192.168.3.167:2181";
	protected int sessionTimeout = 20000;
	
	/**
	 * 获得同步锁的操作者
	 */
	public WriteLock getWriteLocker() throws IOException{
		ZooKeeper keeper = new ZooKeeper(host_port, sessionTimeout, getWatcher());
		WriteLock locker = new WriteLock(keeper, dir, null);
		locker.setLockListener(new LockCallback());
		return locker;
	}
	
	
	private Watcher getWatcher(){
//		Watcher wh = new Watcher() {
//			public void process(WatchedEvent event) {
//				System.out.println("Watcher process::" + event.toString());
//			}
//		};
		return null;
	}
	
	class LockCallback implements LockListener {
		public void lockAcquired() {
//			System.out.println(name + "::Acquired !!");
		}
		public void lockReleased() {
//			System.out.println(name + "::Released !!");
		}
	}
}
