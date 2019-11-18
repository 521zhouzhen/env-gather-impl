package com.briup.env.server;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import com.briup.env.Configuration;
import com.briup.env.bean.Environment;
import com.briup.env.support.PropertiesAware;
import com.briup.env.util.LoggeImpl;

/**
 * DBStore接口是物联网数据中心项目入库模块的规范 该模块负责对Environment集合进行持久化操作
 */
public class DBstoreImpl implements DBStore,PropertiesAware {

	private String DriverClass;
	private String url;
	private String user;
	private String password;
	private int batchSize;
	
	private LoggeImpl  logger;
	Connection conn;
	PreparedStatement ps;
	private String sql;
//	批处理容量



	public void saveDB(Collection<Environment> c) throws Exception {
		List<Environment> list = (List<Environment>) c;
		Class.forName(DriverClass);
		conn = DriverManager.getConnection(url, user, password);
//		设定自动提交为false
		conn.setAutoCommit(false);
		
//	批处理计数
		int day=0;
		int count=0; 
		int batchSize=20;
//		上一个 day  是否换表   是否重新传SQL了语句
		 int dayOfPrefixData=-1;
		ps = conn.prepareStatement(sql);
		for (Environment env : c) {
//			根据时间的day  进行分类
			Timestamp date = env.getGather_date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(date.getTime());
			 day=calendar.get(calendar.DATE);

			if(day != dayOfPrefixData) {
				if(dayOfPrefixData!=-1) {
					ps.executeBatch();
					if(ps != null)ps.close();
				}
			String tableName = "e_detail_"+day;
			String sql = "insert into "+tableName+"(name,srcId,desId,devId,sersorAddress,count,cmd,status,data,gather_date) values(?,?,?,?,?,?,?,?,?,?)";
			ps = conn.prepareStatement(sql);
		
			}
		ps.setString(1, env.getName());
		ps.setString(2, env.getSrcId());
		ps.setString(3, env.getDesId());
		ps.setString(4, env.getDevId());
		ps.setString(5, env.getSersorAddress());
		ps.setInt(6, env.getCount());
		ps.setString(7, env.getCmd());
		ps.setInt(8, env.getStatus());
		ps.setFloat(9, env.getData());
		ps.setTimestamp(10, env.getGather_date());
			
		ps.addBatch();
		if(count==batchSize) {
			ps.executeBatch();
			count=0;
		}
dayOfPrefixData=day;
	}

		ps.executeBatch();
		conn.commit();
		
		if(ps!=null)ps.close();
		if(conn!=null)conn.close();
		
		
}


	@Override
	public void init(Properties p) throws Exception {
		DriverClass = p.getProperty("driver");
		url = p.getProperty("url");
		user = p.getProperty("username");
		password = p.getProperty("password");
		batchSize = Integer.parseInt(p.getProperty("batch-size"));
		
	}

public void setConfiguration(Configuration configuration) throws Exception {
	logger = (LoggeImpl) configuration.getLogger();
}
}
