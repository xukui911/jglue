package com.xw.glue.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LoadUtils {
	public static String getContentByFileAbsolutePath(String filePath) {
		BufferedReader br = null;
		File file = new File(filePath);
		if (!file.exists()) {
			return null;
		}
		try {
			StringBuilder builder = new StringBuilder();			
			br = new BufferedReader(new FileReader(file));
			String line = null;
			while((line=br.readLine())!=null) {
				builder.append(line).append("\n");
			}
			return builder.toString();
		} catch(Exception ex){
			throw new RuntimeException(ex);
		}finally {
			try {
				if(br!=null) {
					br.close();
				}
			}catch(Exception e) {
				
			}
		}
	}
	
	public static String getContentByClassPath(String filePath) {
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
		if (is==null) {
			return null;
		}
		BufferedReader br = null;
		StringBuilder builder = new StringBuilder();
		try {
			br = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while((line=br.readLine())!=null) {
				builder.append(line).append("\n");
			}
			return builder.toString();
		} catch(Exception ex){
			throw new RuntimeException(ex);
		}finally {
			try {
				if(br!=null) {
					br.close();
				}
				if(is!=null) {
					is.close();
				}
			}catch(Exception e) {
				
			}
		}
	}
	
	public static String getContentByPath(String filePath) {
		String content = getContentByFileAbsolutePath(filePath);
		if (content==null) {
			content = getContentByClassPath(filePath);
		}
		return content;
	}
	
	public static String getFileSimpleName(String fileName, boolean withPostfix) {
		fileName = fileName.replace("\\", "/");
		String[] names = fileName.split("/");
		String simpleName = names[names.length-1];
		if (!withPostfix) {
			return simpleName.split("\\.")[0];
		}
		return simpleName;
	}
	
	public static void main(String[] args) {
		//String content = getContentByPath("E:\\Asiainfo\\Shanghai\\Project\\AIFGW-ZJ\\aifgw-web-front\\package.json");
		//System.out.println(content);
	}
}
