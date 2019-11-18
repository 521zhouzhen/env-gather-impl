package com.briup.env.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Array;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.apache.log4j.chainsaw.Main;

import com.briup.env.bean.Environment;

/**
 * Gather接口是物联网数据中心项目采集模块的规范 该模块对物联网数据中心项目环境信息进行采集
 * 将采集的数据封装成Collection<Environment>集合
 */
public class GatherImpl1 implements Gather {

	public Collection<Environment> gather() throws Exception {
		Collection<Environment> c = new ArrayList<Environment>();
		
		
		Environment e = null;
		File file=new  File("src/main/resources/data-file");
		FileReader fr=new FileReader(file);
		BufferedReader br=new BufferedReader(fr);
		String msg="";	
		while((msg=br.readLine())!=null) {
			String[] strings = msg.split("\\|");
			String name = null;
			 String srcId;
			 String desId;
			 String devId;
			 int count;
			String sersorAddress;
			String cmd;
			int status;
			float data = 0;
			Timestamp  gather_date;
	srcId = strings[0];
	desId = strings[1];
	devId = strings[2];
			sersorAddress = strings[3];
			count = Integer.parseInt(strings[4]);
			cmd = strings[5];
			status = Integer.parseInt(strings[7]);
			Long times=Long.parseLong(strings[8]);
			gather_date = new Timestamp(times);
			if(sersorAddress.equals("256")) {
				data =Integer.parseInt(strings[6].substring(0, 4),16);
				name="光照强度";
			}
			if(sersorAddress.equals("1280")) {
				data =Integer.parseInt(strings[6].substring(0, 4),16);
				name="二氧化碳数据";
			}		
			if(sersorAddress.equals("16")) {
				float	value;
				value = Integer.parseInt(strings[6].substring(0, 4),16);
				data = (float) ((float)(value*0.00268127)-46.85);
				name="温度";
				 Environment e1=new Environment(name, srcId,  desId, devId,
							sersorAddress, count,  cmd,  status,
							data,  gather_date);
				 c.add(e1);
				float	v2=Integer.parseInt(strings[6].substring(4,8),16);
				data = (float) (((float)v2*0.00190735)-6);
				name="湿度";
			}	
		e=new  Environment(name, srcId,  desId, devId,
				sersorAddress, count,  cmd,  status,
				data,  gather_date);
		c.add(e);
		}
		return c;
	}
}


	

