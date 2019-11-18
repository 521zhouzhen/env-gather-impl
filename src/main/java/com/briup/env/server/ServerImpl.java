
package com.briup.env.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.briup.env.Configuration;
import com.briup.env.bean.Environment;
import com.briup.env.support.ConfigurationAware;
import com.briup.env.support.PropertiesAware;
import com.briup.env.util.Log;

public class ServerImpl implements Server,PropertiesAware,ConfigurationAware{
	
	private int serverPort;
	private int poolSize;
	
	private ExecutorService pool;
	
	private static volatile boolean flag;
	
	private DBStore dbStore;
	private Log logger;
	
	@Override
	public void reciver() throws Exception {
		
		new Thread() {
			public void run() {
				ServerSocket shutdownServer = null;
				try {
					shutdownServer = new ServerSocket(9999);
					logger.debug("服务器模块的【关闭线程】正在监听端口9999");
					shutdownServer.accept();
					logger.debug("服务器模块的【关闭线程】收到客户端连接，准备关闭服务器");
					ServerImpl.this.shutdown();
//					logger.debug("服务器模块的【关闭线程】已将关闭功能设置为"+ServerImpl.flag);
					//System.out.println("1 ServerImpl.flag = "+ServerImpl.flag);
				} catch (Exception e) {
					e.printStackTrace();
				}finally {
					try {
						if(shutdownServer!=null)shutdownServer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
		
		logger.info("服务器模块已经启动，正在监听端口："+serverPort);
		ServerSocket server = new ServerSocket(serverPort);
		
		while(!flag) {
			System.out.println("2 ServerImpl.flag = "+flag);
			logger.info("服务器模块正在等待客户端连接");
			Socket socket = server.accept();
			logger.info("服务器模块已经接收到客户端连接："+socket);
			
			logger.debug("服务器模块把客户端连接交给线程池处理");
			pool.execute(new Handler(socket));
			logger.debug("服务器模块中线程池正在处理客户端连接");
		}
                 //关闭线程池
		pool.shutdown();
		if(server!=null)server.close(); 
		logger.info("服务器完全关闭");
	}
	
	private class Handler implements Runnable{
		private Socket socket;
		public Handler(Socket socket) {
			this.socket = socket;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void run() {
			ObjectInputStream in = null;
			try {
				in = new ObjectInputStream(socket.getInputStream());
				
				Collection<Environment> c = (Collection<Environment>) in.readObject();
				logger.info("服务器接收到数据："+c.size());
				if(c!=null && c.size()>0) {
					dbStore.saveDB(c);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				logger.debug("服务器模块中线程池已经处理完成，马上关闭资源");
				try {
					if(in!=null)in.close();
				} catch (IOException e) {
					e.printStackTrace();
				} 
				try {
					if(socket!=null)socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}

	@Override
	public void shutdown() throws Exception {
		ServerImpl.flag = true;
		System.out.println("3 ServerImpl.flag = "+ServerImpl.flag);
	}

	@Override
	public void init(Properties p) throws Exception {
		serverPort = Integer.parseInt(p.getProperty("server-port"));
		poolSize = Integer.parseInt(p.getProperty("pool-size"));
		pool = Executors.newFixedThreadPool(poolSize);
	}

	@Override
	public void setConfiguration(Configuration configuration) throws Exception {
		dbStore = configuration.getDbStore();
		logger = configuration.getLogger();
	}

}
