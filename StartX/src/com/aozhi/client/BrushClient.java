package com.aozhi.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrushClient {
	private static Logger logger = LoggerFactory.getLogger(BrushClient.class);

	public static void main(String[] args) {
		logger.info("����ˢ������...");
		BrushService service = new BrushService();
		service.start(args);
		logger.info("�˳�ˢ������!");
		System.exit(0);
	}
}
