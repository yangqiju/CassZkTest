package com.joyveb.zookeeper.lock.test;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.recipes.lock.WriteLock;

public class DistributedLock {

	private WriteLock lock;
	private String lockPath = "/lock";
	private ZooKeeper zooKeeper ;

	public DistributedLock(ZooKeeper zooKeeper){
		this.zooKeeper = zooKeeper;
	}

	/**
	 * 获得锁
	 */
	public boolean lock(){
		lock = new WriteLock(zooKeeper, lockPath, null);
		try {
			while (true) {
				if (lock.lock()) {
					return true;
				}

			}
		} catch (KeeperException e) {
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * 解锁
	 */
	public void unlock(){
		lock.unlock();
	}
	
	public static void main(String args[]){

		try {
			Watcher wh=new Watcher(){
				public void process(org.apache.zookeeper.WatchedEvent event)
				{
					System.out.println(event.toString());
				}
			};

			ZooKeeper zooKeeper = new ZooKeeper("192.168.3.167:2181", 20000, wh);
			final DistributedLock distributedLock = new DistributedLock(zooKeeper);
			
//			for(int i = 0; i < 10 ; i ++){
				Thread thread = new Thread(new Runnable(){
					public void run() {
						if(distributedLock.lock()){
							System.out.println("获得锁---------------");
						}
						distributedLock.unlock();
						System.out.println("解锁---------------");
					}
				});
				thread.start();
//			}

			Thread.sleep(2000*1000);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
}
