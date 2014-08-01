package com.aozhi.client.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class T {
	public static void main(String[] args) {
		n2();
	}

	public static void n1() {
		try {

			Process process = Runtime.getRuntime().exec("cmd.exe");
			OutputStream outputStream = process.getOutputStream();
			final InputStream inputStream = process.getInputStream();
			new Thread(new Runnable() {
				byte[] cache = new byte[1024];
				public void run() {
					System.out.println("listener started");
					try {
						while (inputStream.read(cache) != -1) {
							System.out.println(new String(cache));
						}
					} catch (IOException e) {
						// e.printStackTrace();
					}
				}

			}).start();

			outputStream.write(new byte[] { 'd', 'i', 'r', '\n' });

			// inputStream.

			int i = process.waitFor();

			System.out.println("i=" + i);

		} catch (Exception e) {

			e.printStackTrace();

		}
	}

	// 这段代码是操作Android adb shell 的
	public static void n2() {
		try {

			Process process = Runtime.getRuntime().exec("adb shell"); // adb shell

			final BufferedWriter outputStream = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

			final BufferedReader inputStream = new BufferedReader(new InputStreamReader(process.getInputStream()));

			// 这里一定要注意错误流的读取，不然很容易阻塞，得不到你想要的结果，

			final BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

			new Thread(new Runnable() {

				String line;

				public void run() {

					System.out.println("listener started");

					try {

						while ((line = inputStream.readLine()) != null) {

							System.out.println(line);

						}

					} catch (IOException e) {

						// e.printStackTrace();

					}

				}

			}).start();

			new Thread(new Runnable() {

				final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

				public void run() {

					System.out.println("writer started");

					String line;

					try {

						while ((line = br.readLine()) != null) {

							outputStream.write(line + "\r\n");

							outputStream.flush();

						}

					} catch (IOException e) {

						// e.printStackTrace();

					}

				}

			}).start();

			int i = process.waitFor();

			System.out.println("i=" + i);

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

}
