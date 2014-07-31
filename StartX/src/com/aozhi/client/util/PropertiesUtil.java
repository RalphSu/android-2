package com.aozhi.client.util;

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.support.PropertiesLoaderUtils;

public class PropertiesUtil {
	private static Properties props;
	static {
		try {
			props = PropertiesLoaderUtils.loadAllProperties("run.properties");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param name
	 * @return
	 */
	public static String getProperty(String name) {
		return props.getProperty(name);
	}

}
