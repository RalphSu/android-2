package com.aozhi.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrushClient {
	private static Logger logger = LoggerFactory.getLogger(BrushClient.class);
	
	public static void main(String[] args) {
		logger.info("启动刷机程序...");
		 BrushService service=new BrushService();
		 service.start2(args);
		logger.info("退出刷机程序!");
	}
}
