package com.joyveb.curator.lock.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.RetryOneTime;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.recipes.lock.WriteLock;
import org.junit.Test;

public class MutexLockPresure {

	protected static String dir = "/org.apache.zookeeper.recipes.lock.WriteLockTest";
	private String host167 = "192.168.3.167:2181";
	private String host168 = "192.168.3.168:2181";
	private String host205 = "192.168.3.205:2181";
	private String host207 = "192.168.3.207:2181";
	private int timeout = 5000;
	public AtomicLong num = new AtomicLong();
	private int byteSize = 1024;
	
	@Test
	public void test() throws InterruptedException {
		
		String connectString = "192.168.3.167";
		final CuratorFramework client = CuratorFrameworkFactory.newClient(connectString, new RetryOneTime(1));
		client.start();
		InterProcessMutex lock = new InterProcessMutex(client, dir);
		int threadnum = 100;
		for(int i=0;i<threadnum;i++){
			LockThread thread167 = new LockThread(host167,client,lock);
			thread167.start();
//			LockThread thread168 = new LockThread(host168,client);
//			thread168.start();
//			LockThread thread205 = new LockThread(host205,client);
//			thread205.start();
//			LockThread thread207 = new LockThread(host207,client);
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
		private CuratorFramework client;
		private InterProcessMutex lock;
		public LockThread(String host_port,CuratorFramework client,InterProcessMutex lock) {
			this.hostPort = host_port;
			this.client = client;
			this.lock = lock;
		}
		public void run() {
			try {
//				String connectString = "192.168.3.167";
//				final CuratorFramework client = CuratorFrameworkFactory.newClient(hostPort, new RetryOneTime(1));
//				client.start();
				while (true) {
//					InterProcessMutex lock = new InterProcessMutex(client, dir);
					lock.acquire();
					lock.release();
					num.incrementAndGet();
				}
			} catch (Exception e) {
				e.printStackTrace();
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
