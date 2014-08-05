package test;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.SocketException;
import org.apache.commons.net.telnet.TelnetClient;

public class WindowsShell {
	TelnetClient telnet = new TelnetClient();
//�ĳ�private TelnetClient telnet = new TelnetClient("VT220");
	InputStream in;
	PrintStream out;

	String prompt = ">";

	public WindowsShell(String ip, int port, String user, String password) {
		try {
			telnet.connect(ip, port);
			in = telnet.getInputStream();
			out = new PrintStream(telnet.getOutputStream());
			login(user, password);
		} catch (SocketException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

	/**
	 * ��¼
	 * 
	 * @param user
	 * @param password
	 */
	public void login(String user, String password) {
		// read()Until("login:");
		readUntil("login:");
		write(user);
		readUntil("password:");
		write(password);
		readUntil(prompt + "");
	}

	/**
	 * ��ȡ�������
	 * 
	 * @param pattern
	 * @return
	 */
	public String readUntil(String pattern) {
		try {
			char lastChar = pattern.charAt(pattern.length() - 1);
			StringBuffer sb = new StringBuffer();
			char ch = (char) in.read();
			while (true) {
				sb.append(ch);
				if (ch == lastChar) {
					if (sb.toString().endsWith(pattern)) {
						return sb.toString();
					}
				}
				ch = (char) in.read();
				// System.out.print(ch);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * д����
	 * 
	 * @param value
	 */
	public void write(String value) {
		try {
			out.println(value);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��Ŀ�귢�������ַ���
	 * 
	 * @param command
	 * @return
	 */
	public String sendCommand(String command) {
		try {
			write(command);
			return readUntil(prompt + "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * �ر�����
	 */
	public void disconnect() {
		try {
			telnet.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		WindowsShell ws = new WindowsShell("192.168.202.131", 23, "root", "123456");
		// System.out.println(ws);
		// ִ�е�����
		String str = ws.sendCommand("ipconfig");
		System.out.println(str);
		ws.disconnect();
	}

}
