package com.aozhi.client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.aozhi.client.util.BluestacksPropService;
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
	private String bluestackprop;
	private String productFile;
	private boolean isModifyProduct = false;
	private Map<String, String[]> productMap = null;
	private String[] RUNACTIVITYS = null;
	private String[] IMEIFILES = null;
	private List<String> imeiList = null;
	private List<String> busyHours;
	private final String separator = ",";

	private int delayStart = 30;
	private int delayExit = 10;
	private int delayApp = 4;

	public void start(String[] args) {
		String productID = "1";
		int startIndex = 0;
		logger.info("======> ������������");
		loadProperties(args);
		logger.info("======> ����IMEI�ļ�");
		loadImeiFiles();
		logger.info("======> ���ز�Ʒ�ͺ��ļ�");
		loadProductFiles();
		logger.info("======> ��ѯ��ǰϵͳ��IMEI");

		startIndex = getStartImeiIndex();
		for (int i = startIndex; i < imeiList.size(); i++) {
			String guid = imeiList.get(i);
			String[] items = guid.split(separator);
			if (items.length != 2) {
				continue;
			}

			logger.info("======> �޸�IMEI(" + (i + 1) + " : " + items[1] + ")");
			setUserGuid(items[1]);
			logger.info("======> ����ADB����");
			startAdbService();
			logger.info("======> ����blueStacks");
			startBlueStacks();
			logger.info("======> ��Ӧ��");
			startApp();
			if (!productID.equals(items[0])) {
				logger.info("======> �޸�blueStacks�����ļ�");
				productID = items[0];
				modifyProductName(items[0]);
			}
			logger.info("======> �˳�blueStacks");
			exitBlueStacks();
			if (!busyHours.contains(getHour())) {
				delay(ThreadLocalRandom.current().nextInt(5) * 60);
			}
		}
	}

	/**
	 * ����ADB����
	 */
	public void startAdbService() {
		runCommand(buildAdbCommand("kill-server"));
		delay(1);
		runCommand(buildAdbCommand("start-server"));
		delay(1);
	}

	/**
	 * �޸Ļ�������
	 */
	public void modifyProductName(String productId) {
		String[] para = productMap.get(productId);
		BluestacksPropService propService = new BluestacksPropService(bluestackprop);
		propService.generateBluestackProp(para[0], para[1]);
		enterAdbShell();
		runCommand(buildAdbCommand("push") + bluestackprop + " /data/bluestacks.prop");
	}

	/**
	 * ����adb shell����,���¹����ļ�ϵͳΪ��д
	 */
	private void enterAdbShell() {
		try {
			runCommand(buildAdbCommand("shell"), new FileInputStream("command.txt"));
			delay(3);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ����ģ����
	 */
	public void startBlueStacks() {
		runCommand(STARTLAUNCHER);
		delay(delayStart);
	}

	/**
	 * �˳�ģ����
	 */
	public void exitBlueStacks() {
		runCommand(STOPLAUNCHER);
		delay(delayExit);
	}

	private void startApp() {
		for (String activity : RUNACTIVITYS) {
			runCommand(buildActivityRunCommand(activity));
			delay(delayApp);
		}
	}

	private void loadProperties(String[] resourcesPaths) {

		propertiesLoder = new PropertiesLoder(resourcesPaths);
		ADB = propertiesLoder.getProperty("ADB.Path");
		STARTLAUNCHER = propertiesLoder.getProperty("BlueStacks.Start");
		STOPLAUNCHER = propertiesLoder.getProperty("BlueStacks.Exit");
		RUNACTIVITYS = propertiesLoder.getProperty("Run.Activitys").split(separator);
		IMEIFILES = propertiesLoder.getProperty("IMEI.FILES").split(separator);
		String enable = propertiesLoder.getProperty("Product.enable");
		if ("true".equalsIgnoreCase(enable)) {
			isModifyProduct = true;
			productFile = propertiesLoder.getProperty("Product.name");
		}
		bluestackprop = propertiesLoder.getProperty("BlueStacks.prop") + "/bulestacks.prop";

		delayStart = Integer.parseInt(propertiesLoder.getProperty("delay.start"));
		delayExit = Integer.parseInt(propertiesLoder.getProperty("delay.exit"));
		delayApp = Integer.parseInt(propertiesLoder.getProperty("delay.app"));

		String hours = propertiesLoder.getProperty("busy.hour");
		busyHours = getBusyHourList(hours);
	}

	private List<String> getBusyHourList(String hours) {
		List<String> list = new ArrayList<>();
		if (hours != null) {
			String[] items = hours.split(",");
			for (int i = 0; i < items.length; i++) {
				if (items[i].indexOf("-") != -1) {
					String[] h = items[i].split("-");
					for (int j = Integer.parseInt(h[0]); j <= Integer.parseInt(h[1]); j++) {
						list.add(String.valueOf(j));
					}
				} else {
					list.add(items[i]);
				}
			}
		}
		return list;
	}

	private void loadImeiFiles() {
		imeiList = RegistryUtil.read(IMEIFILES);
	}

	private void loadProductFiles() {
		if (!isModifyProduct) {
			return;
		}
		productMap = new HashMap<String, String[]>();
		List<String> pList = RegistryUtil.read(productFile);
		for (String line : pList) {
			String[] items = line.split("#");
			if (items.length == 2) {
				productMap.put(items[0].trim(), items[1].split(separator));
			}
		}
	}

	/**
	 * �޸�ע���GUID��
	 * 
	 * @param guid
	 */
	private void setUserGuid(String guid) {
		RegistryUtil.setUSERGUID(guid);
		RegistryUtil.setBootParameters(guid);
	}

	private void runCommand(String cmd) {
		Process process = null;
		try {
			logger.debug(" ------->ִ������:" + cmd);
			process = Runtime.getRuntime().exec(cmd);
			new InputStreamGobbler(process.getErrorStream(), "ERROR").start();
			new InputStreamGobbler(process.getInputStream(), "OUTPUT").start();
			int exitVal = process.waitFor();
			logger.debug(" ------->ִ������ش���:" + exitVal);
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (null != process) {
				process.destroy();
			}
		}
	}

	public void runCommand(String cmd, InputStream is) {
		try {
			Process proc = Runtime.getRuntime().exec(cmd);
			new InputStreamGobbler(proc.getErrorStream(), "ERROR").start();
			new InputStreamGobbler(proc.getInputStream(), "OUTPUT").start();
			if (is != null) {
				new OutputStreamGobbler(is, proc.getOutputStream()).start();
			}
			int exitVal = proc.waitFor();
			logger.debug(" ------->ִ������ش���:" + exitVal);
		} catch (Throwable t) {
			t.printStackTrace();
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
	 * ��������Activity�ű�
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
		buf.append(" ");

		return buf.toString();
	}

	/**
	 * ��ȡ��ǰϵͳ��IMEI
	 * 
	 * @return
	 */
	private int getStartImeiIndex() {
		int startIndex = 0;
		String current = RegistryUtil.getGUID();
		if (imeiList.contains(current)) {
			int index = imeiList.indexOf(current);
			if (index + 1 != imeiList.size()) {
				startIndex = index + 1;
			}
		}
		return startIndex;
	}

	private static int getHour() {
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

}
