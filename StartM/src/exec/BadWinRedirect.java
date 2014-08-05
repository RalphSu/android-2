package exec;

import java.util.*;
import java.io.*;

// StreamGobbler omitted for brevity
public class BadWinRedirect {
	public static void main(String args[]) {
		try {
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec("java jecho 'Hello World' > test.txt");
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