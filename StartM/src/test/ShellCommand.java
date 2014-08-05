package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
//import com.symbio.ltp.model.ConfigPropertiesData;

public class ShellCommand {
	private String name;
	private Process process;
	private BufferedWriter writer;
	private BufferedReader reader;
	private BufferedReader errorReader;
	private List<String> list;
	private String[] returnValue;

	public ShellCommand(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Process getProcess() {
		return process;
	}

	public BufferedWriter getOutputWriter() {
		return writer;
	}

	public BufferedReader getInputReader() {
		return reader;
	}

	public BufferedReader getErrorReader() {
		return errorReader;
	}

	public boolean start(String cmd) {
		try {
			process = Runtime.getRuntime().exec(cmd);
			writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
			reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public boolean exec(String cmd) {
		String line;

		try {
			writer.write(cmd + "\n");
			writer.flush();

			while ((line = reader.readLine()) != null) {
//				if (line.equals(ConfigPropertiesData.ltp_success)) {
//					return true;
//				} else if (line.equals(ConfigPropertiesData.ltp_fail)) {
//					return false;
//				}
			}
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public String[] execReturn(String cmd) {
		String line;
		list = new ArrayList<String>();
		try {
			writer.write(cmd + "\n");
			writer.flush();
			line = reader.readLine();
			while ((line = reader.readLine()) != null) {
				if (line.length() > 0 && !(line.startsWith("#"))) {
					list.add(line);
//					if (line.equals(ConfigPropertiesData.ltp_success)) {
//						break;
//					} else if (line.equals(ConfigPropertiesData.ltp_fail)) {
//						break;
//					}
				}
			}

			int size = list.size();
			returnValue = new String[size];
			for (int i = 0; i < size; i++) {
				returnValue[i] = list.get(i);
			}
		} catch (IOException e) {
			return null;
		}
		return returnValue;
	}

	public void terminate() {
		try {
			writer.write(0x03);
			writer.flush();
		} catch (IOException e) {
		}
	}
}