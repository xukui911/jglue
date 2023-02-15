package com.xw.glue.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

public class JVMUtils {
	public static boolean isWindowsOs() {
		return System.getProperty("os.name").toLowerCase().indexOf("windows")!=-1;
	}
	
	public static String execCommand(String command) {
		try {
			Process process = Runtime.getRuntime().exec(command);
			return readProcess(process);
		} catch (IOException e) {
			String errorMsg = "error when execCommand(" +command +"):" + JVMUtils.exceptionStackTrace(e);
			return errorMsg;
		}
	}
	
	public static String readProcess(Process process) throws IOException {
		BufferedInputStream in = new BufferedInputStream(process.getInputStream());
		BufferedReader br = new BufferedReader(new InputStreamReader(in, JVMUtils.isWindowsOs() ? "GBK" : "UTF-8"));
		String line = null;
		String result = "";
		while ((line = br.readLine()) != null) {
			result += line + "\n";
		}
		return result;
	}
	
	public static String exceptionStackTrace(Throwable t) {
		StringWriter sw = null;
		PrintWriter pw = null;
		String errMsg = null;
		try {
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			t.printStackTrace(pw);
			errMsg = sw.toString();
		} finally {
			if (sw != null) {
				try {
					sw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (pw != null) {
				pw.close();
			}
		}
		return errMsg;
	}
}
