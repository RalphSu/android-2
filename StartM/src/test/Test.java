package test;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Test {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			while (true) {//
				eidtEmulator();// 修改imei

				Runtime rt = Runtime.getRuntime();
				String command = "C:\\Program Files\\Android\\android-sdk\\tools\\emulator -avd AVD-10";
				rt.exec(command); // 运行android模拟器
				System.out.println("success run");
				Thread.sleep(2 * 60 * 1000);// 等待2分钟后，停止android模拟器
				if (findRunningWindowsProcess("emulator-arm.exe")) {
					killRunningWindowsProcess("emulator-arm.exe");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void eidtEmulator() throws Exception {
		String oldFilePath = "C:\\Program Files\\Android\\android-sdk\\tools\\emulator-arm.exe ";
		String newFilePath = "C:\\Program Files\\Android\\android-sdk\\tools\\emulator-arm1.exe";
		FileInputStream in = new FileInputStream(oldFilePath);
		FileOutputStream out = new FileOutputStream(newFilePath);
		byte bytes[] = new byte[1];
		byte gsnbytes[] = new byte[3];
		byte imeiBytes[] = new byte[15];
		int count;
		while ((count = in.read(bytes)) != -1) {
			out.write(bytes);
			if (bytes[0] == 0x43) {// if is char 'C'
				count = in.read(gsnbytes);
				if (count == -1) {
					break;
				}
				out.write(gsnbytes);
				if (gsnbytes[0] == 0x47 && gsnbytes[1] == 0x53 && gsnbytes[2] == 0x4E) {// if is char 'GSN'
					count = in.read(bytes);// read char '.'
					if (count == -1) {
						break;
					}
					out.write(bytes);
					count = in.read(imeiBytes);// read old imei
					if (count == -1) {
						break;
					}
					byte[] imeis = getIMEIBytes();
					out.write(imeis);// write new imei;
				}
			}
		}
		in.close();
		out.close();
		File oldFile = new File(oldFilePath);
		oldFile.delete();
		File newFile = new File(newFilePath);
		newFile.renameTo(oldFile);

	}

	public static byte[] getIMEIBytes() {// 随即生成15位imei号
		StringBuffer bff = new StringBuffer();
		byte imeiBytes[] = new byte[15];
		for (int i = 0; i < imeiBytes.length; i++) {
			int num = (int) Math.round(Math.random() * 8);
			bff.append(num);
			imeiBytes[i] = Byte.parseByte("3" + num, 16);
		}
		// printArray(imeiBytes);
		System.err.println("start imei: " + bff.toString());
		return imeiBytes;
	}

	public static void printArray(byte bytes[]) {
		StringBuffer buff = new StringBuffer();
		for (byte b : bytes) {
			buff.append(String.format("%02X", b) + " ");
		}
		System.out.println(buff.toString());
	}

	public static boolean killRunningWindowsProcess(String processName) {
		try {
			Runtime.getRuntime().exec("taskkill /IM " + processName);
			System.out.println("kill process successful");
			// System.out.println("Process " + processName + " was killed. Mission completed.");
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("kill process fail");
			System.out.println("Misson failed.");
			return false;
		}
	}

	public static boolean findRunningWindowsProcess(String processName) {
		BufferedReader bufferedReader = null;
		Process proc = null;
		try {
			proc = Runtime.getRuntime().exec("tasklist /FI \"IMAGENAME eq " + processName + "\"");
			bufferedReader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				if (line.contains(processName)) {
					return true;
				}
			}
			return false;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (Exception ex) {
				}
			}
			if (proc != null) {
				try {
					proc.destroy();
				} catch (Exception ex) {
				}
			}
		}
	}
}