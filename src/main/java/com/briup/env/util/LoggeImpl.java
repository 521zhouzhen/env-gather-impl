package com.briup.env.util;



import org.apache.log4j.Category;
import org.apache.log4j.Logger;

public class LoggeImpl  implements Log{

	private Logger logger = Logger.getRootLogger();

/**
 * Log接口是物联网数据中心项目日志模块的规范
 * 日志模块将日志信息划分为五种级别,不同情况可以使用不同级别的日志进行记录
 */
	public void debug(String message) {
		
		logger.debug(message);
	}


	public void info(String message) {
		logger.info(message);
		
	}

	
	public void warn(String message) {
		logger.warn(message);
		
	}

	public void error(String message) {
	
		logger.error(message);
	}

	public void fatal(String message) {

		logger.error(message );
	}

}
