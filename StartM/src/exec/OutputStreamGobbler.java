package exec;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

class OutputStreamGobbler extends Thread {
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
				System.out.println("-------->exec: "+line);
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
		}
	}
}