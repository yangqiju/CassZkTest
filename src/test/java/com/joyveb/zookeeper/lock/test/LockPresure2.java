package com.joyveb.zookeeper.lock.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.recipes.lock.WriteLock;
import org.junit.Test;

public class LockPresure2 {

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
		
		int threadnum = 50;
		for(int i=0;i<threadnum;i++){
			LockThread thread167 = new LockThread(host167);
			thread167.start();
			LockThread thread168 = new LockThread(host168);
			thread168.start();
			LockThread thread205 = new LockThread(host205);
			thread205.start();
			LockThread thread207 = new LockThread(host207);
			thread207.start();
			
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
//		System.out.println("Thread["+threadnum+"]"+"time["+timeSeconde+"] count["+num.get()+"] TPS["+(num.get()/timeSeconde)+"]");
	}
	
	class LockThread extends Thread {
		private String hostPort;
		public LockThread(String host_port) {
			this.hostPort = host_port;
		}
		public void run() {
			try {
				while (true) {
					ZooKeeper keeper = new ZooKeeper(hostPort, timeout, null);
					WriteLock leader = new WriteLock(keeper, getLockKey(), null);
					if (leader.lock()) {
						leader.unlock();
						num.incrementAndGet();
					}
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
