package com.joyveb.curator.presure;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;
import org.apache.curator.retry.RetryOneTime;
import org.junit.Test;

/**  
 * Copyright © 2014畅享互联.
 *
 * @Title: AtomicLongPresure.java
 * @Prject: CassZkTest
 * @Package: com.joyveb.curator.presure
 * @date: 2014年5月30日 下午5:33:10
 * @author: yangqiju 
 * @Description: TODO
 */
public class AtomicLongPresure {

	private Random random = new Random();
	private static List<CuratorFramework> clients = new LinkedList<>();
	public AtomicLong num = new AtomicLong();
	
	static{
		String host167 = "192.168.3.167:2181";
		String host168 = "192.168.3.168:2181";
		String host205 = "192.168.3.205:2181";
		String host207 = "192.168.3.207:2181";
		
		String[] ips = {host167,host168,host205,host207};
		for(String ip : ips){
			CuratorFramework client = CuratorFrameworkFactory.newClient(ip, new RetryOneTime(1));
			client.start();
			clients.add(client);
		}
	}
	
	public void add(Long count,String key) throws Exception{
		DistributedAtomicLong dal = new DistributedAtomicLong(getClient(),changeKey(key), new RetryOneTime(1));
		AtomicValue<Long> result = null;
		do{
			result = dal.add(count);
		}while(!result.succeeded());
	}
	
	private String changeKey(String key){
		if(!StringUtils.startsWith(key, "/")){
			key  = "/" + key;
		}
		key = key + "Counter";
		return key;
	}
	
	public CuratorFramework getClient(){
		//TODO state:start
//		client.getState().compareTo(CuratorFrameworkState.STARTED);
		CuratorFramework client = clients.get(random.nextInt(clients.size()));
		return client;
	}
	
	@Test
	public void test() throws InterruptedException, IOException {
		int threadnum = 100;
		for(int i=0;i<threadnum;i++){
			WorkThread worker = new WorkThread();
			worker.start();
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
		AtomicLongPresure presure = new AtomicLongPresure();
	}
	
	class WorkThread extends Thread {
		public void run() {
			try {
				while(true){
					AtomicLongPresure presure = new AtomicLongPresure();
					presure.add(1L, "test");
					num.incrementAndGet();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
