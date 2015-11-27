package no.hig.hers.ludoshared;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
/**
 * Class of the logger functionality. 
 */
public class MyLogger {
	private static SimpleFormatter formatterTxt;
	private static FileHandler fileHandler;
	/**
	 * Method that set up the logger. 
	 * @throws IOException Throws any I/O exception. 
	 */
	public static void setupLogger() throws IOException {	
		Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		LOGGER.setLevel(Level.WARNING);
		
		Logger rootLogger = Logger.getLogger("");
		Handler[] handlers = rootLogger.getHandlers();
		if (handlers[0] instanceof ConsoleHandler) {
			rootLogger.removeHandler(handlers[0]);
		}
		
		LOGGER.setLevel(Level.INFO);
		fileHandler = new FileHandler("Errorlog_" + timeStamp() + ".log");

	    // create a TXT formatter
	    formatterTxt = new SimpleFormatter();
	    fileHandler.setFormatter(formatterTxt);
	    LOGGER.addHandler(fileHandler);
	}
	
	/**
	 * Return the data timestamp.
	 * @return the timestamp.
	 */
	private static String timeStamp() {
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
		return timeStamp;
	}
	
}
