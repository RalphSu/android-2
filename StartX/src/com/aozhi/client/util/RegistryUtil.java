package com.aozhi.client.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ice.jni.registry.NoSuchKeyException;
import com.ice.jni.registry.NoSuchValueException;
import com.ice.jni.registry.RegStringValue;
import com.ice.jni.registry.Registry;
import com.ice.jni.registry.RegistryException;
import com.ice.jni.registry.RegistryKey;

public class RegistryUtil {

	private static final String USER_GUID = "USER_GUID";
	private static final String SOFTWARE = "SOFTWARE";
	private static final String BOOTPARAMETERS = "BootParameters";
	private static final String BLUESTACKS = "BlueStacks";
	private static Logger logger = LoggerFactory.getLogger(RegistryUtil.class);

	/**
	 * 设置 BootParameters的值
	 * 
	 * @param guid
	 * @return
	 */
	public static boolean setBootParameters(String guid) {
		try {
			// [HKEY_LOCAL_MACHINE\SOFTWARE\BlueStacks\Guests\Android] "BootParameters"=
			RegistryKey software = Registry.HKEY_LOCAL_MACHINE.openSubKey(SOFTWARE);
			RegistryKey subKey = software.createSubKey(BLUESTACKS, "").createSubKey("Guests", "")
					.createSubKey("Android", "");
			subKey.setValue(new RegStringValue(subKey, BOOTPARAMETERS, buildBootParameters(guid)));
			subKey.closeKey();
			return true;
		} catch (NoSuchKeyException e) {
			logger.error(e.getMessage());
		} catch (NoSuchValueException e) {
			logger.error(e.getMessage());
		} catch (RegistryException e) {
			logger.error(e.getMessage());
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
	 * 设置 USER_GUID的值
	 * 
	 * @param guid
	 * @return
	 */
	public static boolean setUSERGUID(String guid) {
		try {
			// [HKEY_LOCAL_MACHINE\SOFTWARE\BlueStacks] "USER_GUID"
			RegistryKey software = Registry.HKEY_LOCAL_MACHINE.openSubKey(SOFTWARE);
			RegistryKey subKey = software.createSubKey(BLUESTACKS, "");
			subKey.setValue(new RegStringValue(subKey, USER_GUID, guid));
			subKey.closeKey();
			return true;
		} catch (NoSuchKeyException e) {
			logger.error(e.getMessage());
		} catch (NoSuchValueException e) {
			logger.error(e.getMessage());
		} catch (RegistryException e) {
			logger.error(e.getMessage());
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
		} catch (NoSuchValueException e) {
			value = "NoSuchValue";
		} catch (RegistryException e) {
			logger.error(e.getMessage());
		}
		return value;
	}

	/**
	 * 读取GUID文件
	 * 
	 * @param name
	 * @return
	 */
	public static List<String> read(String... names) {

		List<String> list = new ArrayList<String>();
		BufferedReader br = null;
		try {
			String sCurrentLine;
			for (String name : names) {
				br = new BufferedReader(new FileReader(name));
				while ((sCurrentLine = br.readLine()) != null) {
					list.add(sCurrentLine);
				}
			}

		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return list;
	}
}
