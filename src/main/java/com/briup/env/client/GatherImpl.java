package com.briup.env.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.logging.Logger;

import com.briup.env.Configuration;
import com.briup.env.bean.Environment;
import com.briup.env.support.ConfigurationAware;
import com.briup.env.support.PropertiesAware;
import com.briup.env.util.Backup;
import com.briup.env.util.LoggeImpl;

public class GatherImpl implements Gather,PropertiesAware,
ConfigurationAware{


	private String dataFilePath;
private String datafile;
private Backup backup;
private LoggeImpl logger;
//从xml文件中采集数据   存入集合中  返回集合对象
	public Collection<Environment> gather() throws Exception {
		Collection<Environment> list=new ArrayList<Environment>();
			/*读取文件   文件有配置文件传送（datafile）*/
//	1 获取流
		InputStream is;
		BufferedReader in;
	 logger.debug("采集模块读取数据文件："+dataFilePath);
		is = getClass().getClassLoader().getResourceAsStream(datafile);
		in = new BufferedReader(new InputStreamReader(is));
		logger.debug("采集模块数据文件读取成功："+dataFilePath);
//	2 判断读取条件是否符合
//		    2.1  流可以读取的数量
		int fileLen = is.available();
	logger.debug("采集模块当前数据文件中可读取的字节数："+fileLen);
//            2.2		备份文件是否空   * 读取备份文件,返回集合对象
		Integer  len =(Integer) backup.load("length.bak",Backup.LOAD_UNREMOVE);
		if(len!=null) {
		logger.debug("采集模块读取备份数据值为："+len);
			in.skip(len);
		}else {
			logger.debug("采集模块读取备份文件为空");
		}
		logger.debug("采集模块备份当前读取的字节数："+(fileLen+2));
		backup.store("length.bak", fileLen+2, Backup.STORE_OVERRIDE);
		logger.debug("采集模块准备循环读取数据文件内容");
//	3  开始读文件 
		String line=null;
		while((line=in.readLine())!=null) {
//		4 	处理数据
			
//		4.1 先判断数据是否异常
			 String[] arr = line.split("[|]");
			 if(arr.length!=9) {
				 continue;
			 }
//			 4.2 构建environment对象
			Environment env=new Environment();
			env.setSrcId(arr[0]);
			env.setDesId(arr[1]);
			env.setDesId(arr[2]);
			env.setCount(Integer.parseInt(arr[4]));
			env.setCmd(arr[5]);
			env.setStatus(Integer.parseInt(arr[7]));
			env.setGather_date(new Timestamp(Long.parseLong(arr[8])));
			
			int data = 0;
			switch (arr[3]) {
			case "16"://温度、湿度
				env.setName("温度");
				data=Integer.parseInt(arr[6].substring(0,4),16);
				env.setData((data*(0.00268127F))-46.85F);
				list.add(env);
//			Environment e=	copyEnv(env);	
			env.setName("湿度");
			data=Integer.parseInt(arr[6].substring(4,8),16);
			env.setData((data*0.00190735F)-16);
			list.add(env);
				break;
			case"256":////光照强度
				env.setName("光照强度");
				data = Integer.parseInt(arr[6].substring(0, 4),16);
				env.setData(data);			
				list.add(env);
			case"1280"://二氧化碳
				env.setName("二氧化碳");
				data=Integer.parseInt(arr[6].substring(0,4));
				env.setData(data);
				list.add(env);
			
			default:
				System.out.println("未知类型的数据(16|256|1280) :"+arr[3]);
				break;
			}
		}

		logger.info("采集模块执行结束，共采集到数据"+list.size()+"条");
		return list;
	}
	
	
	public void setConfiguration(Configuration configuration) throws Exception {
		
	}


	public void init(Properties properties) throws Exception {
	
		
	}
	
	


	/*
	 * private Environment copyEnv(Environment env) { Environment copyEnv = new
	 * Environment(); copyEnv.setSrcId(env.getSrcId());
	 * copyEnv.setDesId(env.getDesId()); copyEnv.setDevId(env.getDevId());
	 * copyEnv.setCount(env.getCount()); copyEnv.setCmd(env.getCmd());
	 * copyEnv.setStatus(env.getStatus());
	 * copyEnv.setGather_date(env.getGather_date()); return copyEnv; }
	 */
}