package no.hig.hers.ludoserver;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.junit.Test;

public class WriteToFileTest {
	private final String FILE_NAME = "fileTestChatLog.log";
	private final String FILE_DATA = "This is a test.";

	/**
	 * http://stackoverflow.com/questions/1816673/
	 */
	@Test
	public void test() {
		writeToFile(FILE_NAME, FILE_DATA);
		
		File file = new File(FILE_NAME);
		if (!file.exists()) {
			fail("The file didn't exist.");
		}
	}
	
	private void writeToFile(String fileName, String data) {
		PrintWriter writer = null;
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
			writer.println(timeStamp + " " + data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

}
