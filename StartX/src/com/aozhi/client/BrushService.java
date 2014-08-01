package com.aozhi.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.aozhi.client.util.PropertiesLoder;
import com.aozhi.client.util.RegistryUtil;
/**
 * 
 * @author wuyicun
 *
 */
public class BrushService {
	private Logger logger = LoggerFactory.getLogger(BrushService.class);
	private PropertiesLoder propertiesLoder = null;

	private String ADB = null;
	private String STARTLAUNCHER = "";
	private String STOPLAUNCHER = "";
	private List<String> guidList = null;
	private String[] RUNACTIVITYS = null;
	private String[] FILES = null;
	private final String separator = ",";

	public void start(String[] args) {
		logger.info("======> 加载启动参数");
		loadProperties(args);
		logger.info("======> 加载IMEI文件");
		loadImeiFiles();
		logger.info("======> 加载当前注册表的IMEI");
		int startIndex=getStartImeiIndex();
		for(int i=startIndex;i<guidList.size();i++) {
			String guid=guidList.get(i);
			logger.info("======> 修改IMEI("+(i+1)+" : " + guid+")");
			setUserGuid(guid);
			logger.info("======> 启动ADB服务");
			startAdbService();
			logger.info("======> 启动blueStacks");
			startBlueStacks();
			logger.info("======> 打开应用");
			startApp();
			logger.info("======> 退出blueStacks");
			exitBlueStacks();
		}
	}
	public void start2(String[] args) {
		logger.info("======> 加载启动参数");
		loadProperties(args);
		logger.info("======> 加载IMEI文件");
		loadImeiFiles();
		logger.info("======> 加载当前注册表的IMEI");
			logger.info("======> 启动ADB服务");
			startAdbService();
			logger.info("======> 启动blueStacks");
			//startBlueStacks();
			logger.info("======> 退出blueStacks");
			exitBlueStacks();
	}

	/**
	 * 获取启动时的IMEI
	 * @return
	 */
	private int getStartImeiIndex() {
		int startIndex=0;
		String current=RegistryUtil.getGUID();
		if (guidList.contains(current)) {
			int index=guidList.indexOf(current);
			if (index+1!=guidList.size()) {
				startIndex=index+1;
			}
		}
		return startIndex;
	}

	/**
	 * 启动ADB服务
	 */
	public void startAdbService() {
		runCommand(buildAdbCommand("kill-server"));
		delay(1);
		runCommand(buildAdbCommand("start-server"));
		delay(1);
	}

	/**
	 * 启动模拟器
	 */
	public void startBlueStacks() {
		runCommand(STARTLAUNCHER);
		delay(30);
	}

	/**
	 * 退出模拟器
	 */
	public void exitBlueStacks() {
		runCommand(STOPLAUNCHER);
		delay(10);
	}

	private void startApp() {
		for (String activity : RUNACTIVITYS) {
			runCommand(buildActivityRunCommand(activity));
			delay(4);
		}
	}

	private void loadProperties(String[] resourcesPaths) {

		propertiesLoder = new PropertiesLoder(resourcesPaths);
		ADB = propertiesLoder.getProperty("ADB.Path");
		STARTLAUNCHER = propertiesLoder.getProperty("BlueStacks.Start");
		STOPLAUNCHER = propertiesLoder.getProperty("BlueStacks.Exit");
		RUNACTIVITYS = propertiesLoder.getProperty("Run.Activitys").split(separator);
		FILES = propertiesLoder.getProperty("GUID.FILES").split(separator);
	}

	private void loadImeiFiles() {

		guidList = RegistryUtil.read(FILES);
	}

	/**
	 * 修改注册表GUID项
	 * 
	 * @param guid
	 */
	private void setUserGuid(String guid) {
		RegistryUtil.setUSERGUID(guid);
		RegistryUtil.setBootParameters(guid);
	}

	private void runCommand(String cmd) {
		try {
			logger.debug(" ------->执行命令:" + cmd);
			StringBuffer buffer = new StringBuffer();
			Process process = Runtime.getRuntime().exec(cmd);
			InputStream fis = process.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line = null;
			String lineSeparator = System.getProperty("line.separator");
			while ((line = br.readLine()) != null) {
				buffer.append(line).append(lineSeparator);
			}
			logger.debug(buffer.toString());
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	private void delay(int seccond) {
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
	private String buildActivityRunCommand(String activity) {
		// adb shell am start -n com.demo.app1/.activity.AppMain
		StringBuffer buf = new StringBuffer();
		buf.append(ADB);
		buf.append(" shell am start -n ");
		buf.append(activity);

		return buf.toString();
	}

	private String buildAdbCommand(String cmd) {
		StringBuffer buf = new StringBuffer();
		buf.append(ADB);
		buf.append(" ");
		buf.append(cmd);

		return buf.toString();
	}

public static void pull(){//手机到android
		String cmd="adb pull default.prop c:\\test.txt ";
		Process p;
		try {
			p = Runtime.getRuntime().exec(cmd);
			System.out.println(":::::::::::::::::::::::::::::::::::::::::>>>>>>");
			p.waitFor();
			Scanner sc = new Scanner(p.getErrorStream());
			if(sc.hasNext())
				System.out.println(sc.next());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
public static void main(String[] args) {
	pull();
}

}
