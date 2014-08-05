package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Snippet {
	private static String exec(String command) {
		try {
			Process process = Runtime.getRuntime().exec(command);
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			int read;
			char[] buffer = new char[4096];
			StringBuffer output = new StringBuffer();
			while ((read = reader.read(buffer)) > 0) {
				output.append(buffer, 0, read);
			}
			reader.close();
			process.waitFor();
			return output.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	
	public static void main(String[] args) throws Throwable {
		String cmd = "cmd.exe /C   dir";
		// cmd="cmd.exe";
		//System.out.println(exec(cmd));

		 readConsole(cmd, false);
	}

	public static String readConsole(String cmd, Boolean isPrettify) throws IOException {
		StringBuffer cmdout = new StringBuffer();
		System.out.println("执行命令：" + cmd);
		Process process = Runtime.getRuntime().exec(cmd); // 执行一个系统命令
		InputStream fis = process.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String line = null;
		if (isPrettify == null || isPrettify) {
			while ((line = br.readLine()) != null) {
				cmdout.append(line);
			}
		} else {
			while ((line = br.readLine()) != null) {
				cmdout.append(line).append(System.getProperty("line.separator"));
			}
		}
		System.out.println("执行系统命令后的结果为：\n" + cmdout.toString());
		return cmdout.toString().trim();
	}
}
