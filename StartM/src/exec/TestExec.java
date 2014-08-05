package exec;

import java.io.FileInputStream;


// class StreamGobbler omitted for brevity
public class TestExec {
	public static void main(String args[]) {
		try {
			
			//adb shell 
			//
			
			String cmd = " adb shell ";
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec(cmd);

			// any error message?
			InputStreamGobbler errorGobbler = new InputStreamGobbler(proc.getErrorStream(), "ERR");

			// any output?
			InputStreamGobbler outputGobbler = new InputStreamGobbler(proc.getInputStream(), "OUT");
			
			OutputStreamGobbler outputStreamGobbler=new OutputStreamGobbler(new FileInputStream("command.txt"), proc.getOutputStream());
			// kick them off
			errorGobbler.start();
			outputGobbler.start();
			outputStreamGobbler.start();

			// any error???
			int exitVal = proc.waitFor();
			System.out.println("ExitValue: " + exitVal);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}