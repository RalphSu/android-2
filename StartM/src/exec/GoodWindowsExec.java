package exec;

import java.util.*;
import java.io.*;



public class GoodWindowsExec {
	public static void main(String args[]) {
		if (args.length < 1) {
			System.out.println("USAGE: java GoodWindowsExec <cmd>");
			System.exit(1);
		}

		try {
			String osName = System.getProperty("os.name");
			String[] cmd = new String[3];
			if (osName.equals("Windows 7")) {
				cmd[0] = "cmd.exe";
				cmd[1] = "/C";
				cmd[2] = args[0];
			}

			Runtime rt = Runtime.getRuntime();
			System.out.println("Execing " + cmd[0] + " " + cmd[1] + " " + cmd[2]);
			Process proc = rt.exec(cmd);
			// any error message?
			InputStreamGobbler errorGobbler = new InputStreamGobbler(proc.getErrorStream(), "ERROR");

			// any output?
			InputStreamGobbler outputGobbler = new InputStreamGobbler(proc.getInputStream(), "OUTPUT");

			// kick them off
			errorGobbler.start();
			outputGobbler.start();

			// any error???
			int exitVal = proc.waitFor();
			System.out.println("ExitValue: " + exitVal);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}