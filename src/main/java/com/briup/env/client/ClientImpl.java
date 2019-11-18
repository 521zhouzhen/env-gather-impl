package com.briup.env.client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.briup.env.bean.Environment;
import com.briup.env.support.PropertiesAware;
import com.briup.env.util.BackUPImpl;
import com.briup.env.util.Backup;
import com.briup.env.util.LoggeImpl;

/**
 * Client接口是物联网数据中心项目网络模块(客户端)的规范
 * Client的作用就是与服务器进行通信传递信息
 */

public class ClientImpl implements Client,PropertiesAware{
	private LoggeImpl logger;
	private String host;
	private int port;
	private Backup backup;
	private String backFile="src/test/resources/clientBackup";
	public void send(Collection<Environment> c) throws Exception {
			
		try {
//			读取备份文件
			Collection<Environment> col= (Collection<Environment>) backup.load(backFile, true);
			if(col!=null) {
				logger.info("找到备份文件");
				c.addAll(col);
				logger.info("装载完成");
			}
			Socket socket = new Socket(host, port);
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(c);
			out.flush();
			out.close();
			if(out!=null)out.close();
			if(socket!=null) socket.close();
		} catch (Exception e) {
	logger.info("正在备份文件");
			backup.store(backFile, c, false);
			
			
			e.printStackTrace();
		}
		
	}
	@Override
	public void init(Properties properties) throws Exception {
	
		
	}

}
