package no.hig.hers.ludoserver;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;

import org.junit.Test;

/**
 * Testing if the server can create a file, append to it and see that it exists.
 * @author bne9988
 *
 */
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
	
	/**
	 * Code for writing to file:
	 * http://stackoverflow.com/questions/2885173/
	 * http://stackoverflow.com/questions/1625234/
	 * 
	 * This is the logging system for the GlobalServer.
	 * @param fileName The name of the file that will be written to
	 * @param data The data that will be written
	 */
	static void writeToFile(String fileName, String data) {
		PrintWriter writer = null;
		String timeStamp = timeStamp();
		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
			writer.println(timeStamp + " " + data);
		} catch (IOException e) {
			GlobalServer.LOGGER.log(Level.SEVERE, "Error writing to file", e);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
	
	/**
	 * http://stackoverflow.com/questions/5175728/
	 * Used to create the timestamp when writing to file.
	 * @return The timestamp
	 */
	private static String timeStamp() {
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
		return timeStamp;
	}
}
