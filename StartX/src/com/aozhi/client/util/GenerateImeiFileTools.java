package com.aozhi.client.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class GenerateImeiFileTools {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		String filename = "";
		System.out.print("--->请输入IMEI个数:");
		String number = scanner.nextLine();
		List<String> pList = read("product.txt");
		if (pList!=null) {
			System.out.println("----------------产品列表------------------");
			System.out.println("产品编号 \t厂商  \t型号");
			for (String line : pList) {
				String[] items=line.split("#");
				String[] p=items[1].split(",");
				System.out.println("  "+items[0]+" \t "+p[0]+" \t"+p[1]);
			}
			System.out.println("---------------------------------------");
		}
		System.out.print("--->请输入产品编号:");
		String productId = scanner.nextLine();
		filename = getFileName(productId);
		scanner.close();
		StringBuffer buf = new StringBuffer();
		try {

			int num = Integer.parseInt(number);
			boolean isFirst=true;
			for (int i = 0; i < num; i++) {
				if (isFirst) {
					isFirst=false;
				}else {
					buf.append("\r\n");
				}
				buf.append(productId).append(",").append(UUID.randomUUID());
			}
		} catch (Exception e) {
			System.err.println("IMEI个数参数为数字！");
		}
		genFile(filename, buf.toString());
		System.out.println("--------------->文件生成成功，文件名称:" + filename);
	}

	public static String getFileName(String id) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHssmm");
		return "IMEI" + sdf.format(new Date()) + "-" + id + ".txt";
	}

	public static void genFile(String filename, String content) {
		try {
			File file = new File(filename);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
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
	
	public int getRandomList(List<Integer> list) {
		 
	    //0-4
	    int index = ThreadLocalRandom.current().nextInt(list.size());		
	    System.out.println("\nIndex :" + index );
	    return list.get(index);
 
	}
}
