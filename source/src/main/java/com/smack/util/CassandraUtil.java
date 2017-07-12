package com.smack.util;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.Session;

public class CassandraUtil {

	public static void initTable() {
		PoolingOptions poolingOptions = new PoolingOptions();
		poolingOptions.setMaxRequestsPerConnection(HostDistance.LOCAL, 32);
		poolingOptions.setCoreConnectionsPerHost(HostDistance.LOCAL, 2)
				.setMaxConnectionsPerHost(HostDistance.LOCAL, 4)
				.setCoreConnectionsPerHost(HostDistance.REMOTE, 2)
				.setMaxConnectionsPerHost(HostDistance.REMOTE, 4);
		Cluster cluster = Cluster.builder().addContactPoints("cassandra")
				.withPort(9042)
				.withPoolingOptions(poolingOptions).build();
		Session session = cluster.connect();
		String cql = "CREATE KEYSPACE if not exists mydb WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}";
		session.execute(cql);
		String cql1 = "CREATE TABLE if not exists mydb.nginx_log (namespace varchar,servername varchar,create_time timestamp,log varchar,primary key(namespace,servername,create_time))";
		session.execute(cql1);
		
		String cql2 = "CREATE TABLE if not exists mydb.nginx_base_log (namespace varchar,servername varchar,remoteaddr varchar,remoteuser varchar,timeLocal varchar,request varchar,status int,bodySize int,httpreferer varchar,httpuseragent varchar,httpxforwardedfor varchar,requestlength int,upstreamresponsetime double,upstreamaddr varchar,create_time timestamp,update_time timestamp, primary key(namespace,servername,create_time,update_time))";
		session.execute(cql2);
		
		String cql3 = "CREATE TABLE if not exists mydb.status_real_statics (namespace varchar,servername varchar,status int,start_time varchar,end_time varchar,num int,create_time timestamp,primary key(namespace,servername,create_time,status))";
		session.execute(cql3);
		
		String cql4 = "CREATE TABLE if not exists mydb.request_real_statics (namespace varchar,servername varchar,request varchar,start_time varchar,end_time varchar,num int,avg_time double,create_time timestamp,primary key(namespace,servername,create_time,request))";
		session.execute(cql4);
		
		String cql5 = "CREATE TABLE if not exists mydb.request_bath_statics (namespace varchar,servername varchar,request varchar,num int,create_time timestamp,primary key(namespace,servername,create_time,request))";
		session.execute(cql5);
		
		session.close();
		
		cluster.close();
		
	}
}
