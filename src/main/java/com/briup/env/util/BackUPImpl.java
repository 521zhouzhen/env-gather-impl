package com.briup.env.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;

import com.briup.env.support.PropertiesAware;

//文件备份
public class BackUPImpl implements Backup,PropertiesAware{

	private String  filePath;
	
	
	/**
	 * 读取备份文件,返回集合对象
	 */
	public Object load(String fileName, boolean del) throws Exception {
		File file=new File(filePath+fileName);
		if(!file.exists()) {
			System.out.println("备份文件不存在"+fileName);
		 return null;
		}
		FileInputStream fis=new FileInputStream(file);
		ObjectInputStream in=new ObjectInputStream(fis);
		Object obj=in.readObject();
		
        if(del) {
        	file.delete();
        }
        if(in!=null)in.close();
        if(fis!=null)fis.close();
		return obj;
	}

	
	
	/**
	 * 将需要备份的集合对象写入到备份文件
	 */
public void store(String fileName, Object obj, boolean append) throws Exception {
	FileOutputStream fos = new FileOutputStream(filePath+fileName,append);
	ObjectOutputStream out = new ObjectOutputStream(fos);
	
	out.writeObject(obj);
	out.flush();
	
	if(fos!=null)fos.close();
	if(out!=null)out.close();
}



	@Override
	public void init(Properties p) throws Exception {
	filePath=p.getProperty("file-path");
		
	}

}
