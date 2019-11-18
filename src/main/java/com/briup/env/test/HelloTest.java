package com.briup.env.test;

import java.util.Collection;

import com.briup.env.bean.Environment;
import com.briup.env.client.Client;
import com.briup.env.client.ClientImpl;
import com.briup.env.client.GatherImpl1;
import com.briup.env.server.DBStore;
import com.briup.env.server.DBstoreImpl;

public class HelloTest {
	public static void main(String[] args) throws Exception {
		GatherImpl1 g=new GatherImpl1();
		Collection<Environment> en = g.gather();
		DBstoreImpl db=new DBstoreImpl();
		db.saveDB(en);
		
		ClientImpl c=new ClientImpl();
		c.send(en);
		System.out.println("hello world");
		
	}
}
