package com.aozhi.client;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import com.ice.jni.registry.NoSuchKeyException;
import com.ice.jni.registry.NoSuchValueException;
import com.ice.jni.registry.RegStringValue;
import com.ice.jni.registry.Registry;
import com.ice.jni.registry.RegistryException;
import com.ice.jni.registry.RegistryKey;

public class BrushClient {

	private static final String USER_GUID = "USER_GUID";
	private static final String SOFTWARE = "SOFTWARE";
	private static final String BOOTPARAMETERS = "BootParameters";
	private static final String BLUESTACKS = "BlueStacks";
	
	private static final String STARTLAUNCHER="C:/Program Files (x86)/BlueStacks/HD-StartLauncher.exe";
	private static final String STOPLAUNCHER="C:/Program Files (x86)/BlueStacks/HD-Quit.exe";
	private static final String RESTARTLAUNCHER="C:/Program Files (x86)/BlueStacks/HD-Restart.exe";
	private static final String RUNACTIVITY=" D:/Android/android-sdk/platform-tools/adb shell am start -n com.demo.app1/.activity.AppMain ";
	
	
	public static void main(String[] args) {

		// 修改注册表
		// 启动BS
		// 运行Activity
		// 停止BS

		//setUSERGUID("97424cc7-8502-4d58-b7ab-080126549600");
		//setBootParameters("97424cc7-8502-4d58-b7ab-080126549600");
		//System.out.println(getGUID());
		//read(null);
		startBS();
	}

	public static void startBS() {
		try {
			Runtime.getRuntime().exec(STARTLAUNCHER);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置 BootParameters的值 [HKEY_LOCAL_MACHINE\SOFTWARE\BlueStacks\Guests\Android] "BootParameters"=
	 * 
	 * @param guid
	 * @return
	 */
	public static boolean setBootParameters(String guid) {
		try {
			RegistryKey software = Registry.HKEY_LOCAL_MACHINE.openSubKey(SOFTWARE);
			RegistryKey subKey = software.createSubKey(BLUESTACKS, "").createSubKey("Guests", "").createSubKey("Android", "");
			subKey.setValue(new RegStringValue(subKey, BOOTPARAMETERS, buildBootParameters(guid)));
			subKey.closeKey();
			return true;
		} catch (NoSuchKeyException e) {
			e.printStackTrace();
		} catch (NoSuchValueException e) {
			e.printStackTrace();
		} catch (RegistryException e) {
			e.printStackTrace();
		}
		return false;
	}

	// ROOT=/dev/sda1 SRC=/android DATA=/dev/sdb1 SDCARD=/dev/sdc1 PREBUNDLEDAPPSFS=/dev/sdd1 HOST=WIN
	// GUID=c4bf39eb-a63d-42ae-a761-2c7771239375 VERSION=0.8.12.3122 OEM=China LANG=en_US armApps=true GlMode=1 P2DM=1
	// caCode=392 country=US
	private static String buildBootParameters(String guid) {
		StringBuffer buf = new StringBuffer();
		buf.append("ROOT=/dev/sda1 SRC=/android DATA=/dev/sdb1 SDCARD=/dev/sdc1 PREBUNDLEDAPPSFS=/dev/sdd1 HOST=WIN  ");
		buf.append("GUID=").append(guid).append("  ");
		buf.append("VERSION=0.8.12.3122 OEM=China LANG=en_US armApps=true GlMode=1 P2DM=1 caCode=392 country=US  ");
		return buf.toString();
	}

	/**
	 * 设置 USER_GUID的值 [HKEY_LOCAL_MACHINE\SOFTWARE\BlueStacks] "USER_GUID"
	 * 
	 * @param guid
	 * @return
	 */
	public static boolean setUSERGUID(String guid) {
		try {
			RegistryKey software = Registry.HKEY_LOCAL_MACHINE.openSubKey(SOFTWARE);
			RegistryKey subKey = software.createSubKey(BLUESTACKS, "");
			subKey.setValue(new RegStringValue(subKey, USER_GUID, guid));
			subKey.closeKey();
			return true;
		} catch (NoSuchKeyException e) {
			e.printStackTrace();
		} catch (NoSuchValueException e) {
			e.printStackTrace();
		} catch (RegistryException e) {
			e.printStackTrace();
		}
		return false;
	}

	// 打开注册表项并读出相应的变量名的值
	public static String getGUID() {
		String value = "";
		try {
			RegistryKey software = Registry.HKEY_LOCAL_MACHINE.openSubKey(SOFTWARE);
			RegistryKey subKey = software.openSubKey(BLUESTACKS);
			value = subKey.getStringValue(USER_GUID);
			subKey.closeKey();
		} catch (NoSuchKeyException e) {
			value = "NoSuchKey";
			// e.printStackTrace();
		} catch (NoSuchValueException e) {
			value = "NoSuchValue";
			// e.printStackTrace();
		} catch (RegistryException e) {
			e.printStackTrace();
		}
		return value;
	}
	public static void read(String[] args) {
		 
		File file = new File("D:\\History.txt");
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;
 
		try {
			fis = new FileInputStream(file);
 
			bis = new BufferedInputStream(fis);
			dis = new DataInputStream(bis);
 
			while (dis.available() != 0) {
				System.out.println(dis.readLine());
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
				bis.close();
				dis.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
