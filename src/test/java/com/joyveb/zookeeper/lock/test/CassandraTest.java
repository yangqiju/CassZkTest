package com.joyveb.zookeeper.lock.test;
import java.util.UUID;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SimpleStatement;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.Batch;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
/**  
 * Copyright © 2014畅享互联.
 *
 * @Title: CassandraTest.java
 * @Prject: CassZkTest
 * @Package: com.joyveb.zookeeper.lock.test
 * @date: 2014年5月23日 上午10:50:47
 * @author: yangqiju 
 */
public class CassandraTest {
	private static final String CLUSTER_NAME = "Test Cluster";
	private static final String[] CONTACTPOINT = {"192.168.3.197"} ;
	private static final String KEYSPACE = "lottery";
	private static final String TABLE = "t_core_vo";
	public static void main(String[] args) {
		Cluster cluster = Cluster.builder().withClusterName(CLUSTER_NAME).addContactPoints(CONTACTPOINT).build();
		try{
			Session session = cluster.connect(KEYSPACE);
			System.out.println("connected");
//			select(session);
//			update(session);
//			select(session);
			batch(session);
		}finally{
			cluster.close();
			System.out.println("end");
		}
	}

	public static void select(Session session){
		Select select = QueryBuilder.select().all().from(TABLE);
		for(Row row : session.execute(select).all()){
			System.out.println(row.toString());
		}
	}

	public static void update(Session session){
		//UPDATE test SET name = 'abc' where id = 'id1';
		Statement update = QueryBuilder.update(TABLE).with(QueryBuilder.set("name", "yang")).where(QueryBuilder.eq("id", "id1"));
		System.out.println(update.toString());
		session.execute(update);
	}
	
	public static void batch(Session session){
		 long start = System.currentTimeMillis();
		 for(int n = 0;n<100;n++){
			 Batch batch = QueryBuilder.batch();
			 for(int i=0;i<30000 ;i++){
				 String caskey = UUID.randomUUID().toString()+System.currentTimeMillis();
				 String insert =  String.format("INSERT INTO t_core_vo (ltype, period , merchantid , messageid , orderno , caskey    , mobile ) VALUES ( 'QGLOTO','2012001','888888','MESS1111','ORD11','%s','180');", caskey);
				 SimpleStatement statement = new SimpleStatement(insert); 
				 batch.add(statement);
			 }
			 session.execute(batch);
		 }
		 System.out.println("batch end::"+(System.currentTimeMillis()-start));
	}
	
}
