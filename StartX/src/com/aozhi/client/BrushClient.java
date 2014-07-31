package com.aozhi.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aozhi.client.util.PropertiesUtil;
import com.aozhi.client.util.RegistryUtil;

public class BrushClient {
	private static Logger logger = LoggerFactory.getLogger(BrushClient.class);

	private static String ADB = null;
	private static String STARTLAUNCHER = "";
	private static String STOPLAUNCHER = "";
	private static String RESTARTLAUNCHER = "";
	private static List<String> guidList = null;
	private static String[] RUNACTIVITYS = null;
	private static String[] FILES = null;
	// " shell am start -n com.demo.app1/.activity.AppMain ";
	private static final String separator = ",";

	public static void main(String[] args) {
		loadProperties();
		loadFiles();
		for (String guid : guidList) {

			// setUserGuid(guid);
			logger.info("======>启动blueStacks");
			//runCommand(STARTLAUNCHER);
			runCommand("adb devices");
			delay(3);
			// 启动Activity
			for (String activity : RUNACTIVITYS) {
				logger.info("==========>打开应用: " + activity);
				runCommand(buildActivityRunCommand(activity));
				delay(4);
			}
			logger.info("======>退出blueStacks");
			// runCommand(STOPLAUNCHER);
			delay(10);

		}

	}

	public static void loadProperties() {
		logger.info("======> 加载启动参数");
		ADB = PropertiesUtil.getProperty("ADB.Path");
		STARTLAUNCHER = PropertiesUtil.getProperty("BlueStacks.Start");
		STOPLAUNCHER = PropertiesUtil.getProperty("BlueStacks.Exit");
		RESTARTLAUNCHER = PropertiesUtil.getProperty("BlueStacks.Restart");
		RUNACTIVITYS = PropertiesUtil.getProperty("Run.Activitys").split(separator);
		FILES = PropertiesUtil.getProperty("GUID.FILES").split(separator);
	}

	public static void loadFiles() {
		logger.info("======> 加载GUID文件");
		guidList = RegistryUtil.read(FILES);
	}

	public static void setUserGuid(String guid) {
		RegistryUtil.setUSERGUID(guid);
		RegistryUtil.setBootParameters(guid);
		logger.info("======>修改IMEI (" + RegistryUtil.getGUID() + " -> " + guid + " ");
	}

	public static void runCommand(String cmd) {
		try {
			logger.debug("======>执行命令:" + cmd);
			Process process = Runtime.getRuntime().exec(cmd);
			InputStream fis = process.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line = null;
			while ((line = br.readLine()) != null) {
				logger.debug("  ------>"+line);
			}
			// (System.getProperty("line.separator"));
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public static void delay(int seccond) {
		try {
			Thread.sleep(seccond * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 生成运行Activity脚本
	 * 
	 * @param activity
	 * @return
	 */
	private static String buildActivityRunCommand(String activity) {
		// adb shell am start -n com.demo.app1/.activity.AppMain
		StringBuffer buf = new StringBuffer();
		buf.append(ADB);
		buf.append(" shell am start -n ");
		buf.append(activity);

		return buf.toString();
	}

}
