package com.joyveb.zookeeper.lock.test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.recipes.lock.WriteLock;
import org.junit.Test;

public class LockPresure {

	protected String dir = "/" + getClass().getName();
	private String host_port = "192.168.3.167:2181";
	public AtomicLong num = new AtomicLong();

	@Test
	public void test() throws InterruptedException {
		for(int i=0;i<5;i++){
			LockThread thread = new LockThread();
			thread.start();
		}
		int timeSeconde = 10;
		TimeUnit.SECONDS.sleep(timeSeconde);
		System.out.println("times::"+num.get());
		System.out.println("TPS::"+(num.get()/timeSeconde));
	}

	class LockThread extends Thread {
		public void run() {
			try {
				ZooKeeper keeper = new ZooKeeper(host_port, 20000, null);
				WriteLock leader = new WriteLock(keeper, dir, null);
				while (true) {
					if (leader.lock()) {
						leader.unlock();
						num.incrementAndGet();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (KeeperException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
