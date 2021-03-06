package com.joyveb.zookeeper.lock.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.recipes.lock.WriteLock;
import org.junit.Test;
public class LockPresure {

	protected String dir = "/" + getClass().getName();
	private String host167 = "192.168.3.167:2181";
	public AtomicLong num = new AtomicLong();
	@Test
	public void test() throws InterruptedException {
		int threadnum = 100;
		for(int i=0;i<threadnum;i++){
			LockThread thread167 = new LockThread(host167,dir);
			thread167.start();
		}
		int timeSeconde = 100;
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
		private String hostPort;
		private String lockKey;
		public LockThread(String host_port,String lockKey) {
			this.hostPort = host_port;
			this.lockKey = lockKey;
		}
		public void run() {
			try {
				ZooKeeper keeper = new ZooKeeper(hostPort, 5000, null);
				WriteLock leader = new WriteLock(keeper, lockKey, null);
				while (true) {
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
	
	private String  getLockKey(){
		return keys.get(random.nextInt(keys.size()));
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
