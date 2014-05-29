package com.joyveb.zookeeper.lock.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.recipes.lock.WriteLock;
import org.junit.Test;

public class LockHandlerPresure {

	protected static String dir = "/org.apache.zookeeper.recipes.lock.WriteLockTest";
	private String host167 = "192.168.3.167:2181";
	private String host168 = "192.168.3.168:2181";
	private String host205 = "192.168.3.205:2181";
	private String host207 = "192.168.3.207:2181";
	private int timeout = 5000;
	public AtomicLong num = new AtomicLong();
	public AtomicLong count = new AtomicLong();
	
	@Test
	public void test() throws InterruptedException, IOException {
		int threadnum = 30;
		LockHandler lockHandler = new LockHandler(host167,host168,host205,host207);
		for(int i=0;i<threadnum;i++){
			LockThread thread167 = new LockThread(lockHandler);
			thread167.start();
		}
		int timeSeconde = 1000;
		int sleepTime = 10;
		int time = 0;
		while(true){
			TimeUnit.SECONDS.sleep(sleepTime);
			time = time + sleepTime;
			System.out.println("Thread["+threadnum+"]"+"time["+time+"] count["+num.get()+"] TPS["+(num.get()/time)+"]");
			if(time>=timeSeconde){
				break;
			}
		}
//		System.out.println("Thread["+threadnum+"]"+"time["+timeSeconde+"] count["+num.get()+"] TPS["+(num.get()/timeSeconde)+"]");
	}
	
	class LockThread extends Thread {
		private LockHandler lockHandler;
		public LockThread(LockHandler lockHandler ) {
			this.lockHandler = lockHandler;
		}
		public void run() {
			try {
				while(true){
					ZooKeeper keeper = new ZooKeeper(host168, timeout, null);
					WriteLock lock = new WriteLock(keeper, "/key", null);
//					WriteLock lock =lockHandler.getLock("key");
					System.out.println(lock);
					System.out.println(count.addAndGet(1));
					while (true) {
						if (lock.lock()) {
							lock.unlock();
							num.incrementAndGet();
							break;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static String  getLockKey(){
		return keys.get(random.nextInt(keys.size()));
//		return dir;
	}
	
	private static List<String> keys = new ArrayList<>();
	private static Random random  = new Random();
	
	static {
		String[] merchantids = {"866130","866131","866132","866133","866134","86615","866136","866137"};
		String[] ltypes = {"GXPCK3","QGSLTO","QGLOTO","GXPCK3","TJSSC","TJKLSF"};
		
		for(String merchantid : merchantids){
			for(String ltype : ltypes){
				keys.add(ltype+"_"+merchantid);
			}
		}
	}
}
