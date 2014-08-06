package com.aozhi.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class InputStreamGobbler extends Thread {
	private Logger logger = LoggerFactory.getLogger(InputStreamGobbler.class);
	InputStream is;
	String type;

	InputStreamGobbler(InputStream is, String type) {
		this.is = is;
		this.type = type;
	}

	public void run() {
		BufferedReader br=null;
		try {
			StringBuffer buffer = new StringBuffer();
			br = new BufferedReader(new InputStreamReader(is));
			String line = null;
			String lineSeparator = System.getProperty("line.separator");
			while ((line = br.readLine()) != null) {
				buffer.append(line).append(lineSeparator);
			}
			logger.debug(type+":"+buffer.toString());
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}finally {
			IOUtils.closeQuietly(br);
		}
	}
}

class OutputStreamGobbler extends Thread {
	private Logger logger = LoggerFactory.getLogger(OutputStreamGobbler.class);
	BufferedReader br;
	BufferedWriter bw;

	OutputStreamGobbler(InputStream is, OutputStream os) {
		br = new BufferedReader(new InputStreamReader(is));
		bw = new BufferedWriter(new OutputStreamWriter(os));
	}

	public void run() {
		try {
			String line;
			while ((line = br.readLine()) != null) {
				logger.debug("enter:"+line);
				bw.write(line + "\r\n");
				bw.flush();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(bw);
		}
	}
}