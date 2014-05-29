package com.joyveb.zookeeper.lock.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;

public class BlockingWritePresure2 {

	protected static String dir = "/org.apache.zookeeper.recipes.lock.WriteLockTest";
	private String host167 = "192.168.3.167:2181";
	private String host168 = "192.168.3.168:2181";
	private String host205 = "192.168.3.205:2181";
	private String host207 = "192.168.3.207:2181";
	private int timeout = 5000;
	public AtomicLong num = new AtomicLong();
	
	@Test
	public void test() throws InterruptedException, IOException {
		ZooKeeper keeper = new ZooKeeper(host167, timeout, null);
		int threadnum = 100;
		for(int i=0;i<threadnum;i++){
			LockThread thread167 = new LockThread(host167,keeper);
			thread167.start();
//			LockThread thread168 = new LockThread(host168);
//			thread168.start();
//			LockThread thread205 = new LockThread(host205);
//			thread205.start();
//			LockThread thread207 = new LockThread(host207);
//			thread207.start();
			
		}
		int timeSeconde = 1000;
		int sleepTime = 10;
		int time = 0;
		while(true){
			TimeUnit.SECONDS.sleep(sleepTime);
			time = time + sleepTime;
			System.out.println("Thread["+threadnum*4+"]"+"time["+time+"] count["+num.get()+"] TPS["+(num.get()/time)+"]");
			if(time>=timeSeconde){
				break;
			}
		}
	}
	
	class LockThread extends Thread {
		private String hostPort;
		private ZooKeeper keeper;
		public LockThread(String host_port,ZooKeeper keeper) {
			this.hostPort = host_port;
			this.keeper = keeper;
		}
		public void run() {
			while (true) {
				BlockingWriteLock lock = new BlockingWriteLock("", keeper, "/key");
				try {
					if(lock.lock(1,TimeUnit.MILLISECONDS)){
						lock.unlock();
						num.incrementAndGet();
					}
				} catch (InterruptedException | KeeperException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static String  getLockKey(){
//		return keys.get(random.nextInt(keys.size()));
		return dir;
	}
	
	private static List<String> keys = new ArrayList<>();
	private static Random random  = new Random();
	
	static {
		String[] merchantids = {"866130","866131","866132","866133","866134","86615","866136","866137"};
		String[] ltypes = {"GXPCK3","QGSLTO","QGLOTO","GXPCK3","TJSSC","TJKLSF"};
		
		for(String merchantid : merchantids){
			for(String ltype : ltypes){
				keys.add("/"+ltype+"_"+merchantid);
			}
		}
	}
}
