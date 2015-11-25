package no.hig.hers.ludoshared;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MyLogger {
	private static SimpleFormatter formatterTxt;
	private static FileHandler fileHandler;
	
	public static void setupLogger() throws IOException {	
		Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		LOGGER.setLevel(Level.WARNING);
		
		Logger rootLogger = Logger.getLogger("");
		Handler[] handlers = rootLogger.getHandlers();
		if (handlers[0] instanceof ConsoleHandler) {
			rootLogger.removeHandler(handlers[0]);
		}
		
		LOGGER.setLevel(Level.INFO);
		fileHandler = new FileHandler("Logging.txt");

	    // create a TXT formatter
	    formatterTxt = new SimpleFormatter();
	    fileHandler.setFormatter(formatterTxt);
	    LOGGER.addHandler(fileHandler);
	}
}
