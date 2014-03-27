package com.joyveb.zookeeper.lock.test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.recipes.lock.LockListener;
import org.apache.zookeeper.recipes.lock.WriteLock;
import org.junit.Test;

public class LockTest {
	protected String dir = "/" + getClass().getName();
	private String host_port = "192.168.3.167:2181";
	public final int count = 4;
	public CountDownLatch latch = new CountDownLatch(count);

	class LockCallback implements LockListener {
		private String name;

		public LockCallback(String name) {
			this.name = name;
		}

		public void lockAcquired() {
//			System.out.println(name + "::Acquired !!");
		}

		public void lockReleased() {
//			System.out.println(name + "::Released !!");
		}

	}

	@Test
	public void testRun() throws Exception {
		String[] strs = {"one..","two..","three","four."};
		for(int i=0;i<count;i++){
			LockThread thread = new LockThread(strs[i]);
			thread.start();
		}
		latch.await(2, TimeUnit.MINUTES);
		System.out.println("end.....");
	}

	class LockThread extends Thread {
		private String name ;
		public LockThread(String name) {
			this.name = name;
		}
		public void run() {
			Watcher wh = new Watcher() {
				public void process(WatchedEvent event) {
//					System.out.println("Watcher process::" + event.toString());
				}
			};
			try {
				ZooKeeper keeper = new ZooKeeper(host_port, 20000, wh);
				WriteLock leader = new WriteLock(keeper, dir, null);
				leader.setLockListener(new LockCallback(leader.getId()));
				
				while(true){
					if(leader.lock()){
						dumpNodes(leader);
						System.out.println();
						System.out.println(name+"::获得锁..处理..");
						TimeUnit.SECONDS.sleep(1);
						leader.unlock();
//						dumpNodes(leader);
						latch.countDown();
						System.out.println();
						System.out.println(name+"::获得锁..处理..完成..");
						leader.close();
						break;
					}else{
//						System.out.print(".");
//						System.out.println(name+"::没有获得锁..等待");
//						latch.await(3, TimeUnit.MILLISECONDS);
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
	
	protected void dumpNodes(WriteLock node) {
		System.out.println(" id: " + node.getId() + " is leader: "
				+ node.isOwner());
	}

}
