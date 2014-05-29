package com.joyveb.zookeeper.lock.test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;
public class WriterAndReadPresure {

	protected String dir = "/org.apache.zookeeper.recipes.lock.WriteLockTest";
	private String host167 = "192.168.3.167:2181";
	private String host168 = "192.168.3.168:2181";
	private String host205 = "192.168.3.205:2181";
	private String host207 = "192.168.3.207:2181";
	private int timeout = 5000;
	public AtomicLong num = new AtomicLong();
	private int byteSize = 1024;
	private byte[] bytes = new byte[byteSize]; 
	
	@Test
	public void test() throws InterruptedException {
		for(int i=0 ;i<byteSize;i++){
			bytes[i] = 'A';
		}
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
		int timeSeconde = 100;
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
		public LockThread(String host_port) {
			this.hostPort = host_port;
		}
		public void run() {
			try {
				ZooKeeper keeper = new ZooKeeper(hostPort, timeout, null);
				String data = hostPort;
				while (true) {
					keeper.setData(dir, data.getBytes(), -1);
//					keeper.getData(dir, false, null);
					num.incrementAndGet();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
