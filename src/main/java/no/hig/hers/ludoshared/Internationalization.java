package no.hig.hers.ludoshared;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for finding the computer language and country so the correct
 * language will be displayed to the user.
 * 
 * @author Henrik on 26.11.2015
 */
public class Internationalization {
	
	private static ResourceBundle messages;
	static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	/**
	 * Sets the the messages resourceboundle for the correct language.
	 * @throws IOException Throws exception if some I/O exception has occured.
	 */
	private static void setupInternationalize() throws IOException {
		Locale no = new Locale("no", "NO");
    	Locale us = new Locale("en", "US");
    	
    	if(no.equals(Locale.getDefault())) {
    		messages = ResourceBundle.getBundle("Resources/MessagesBundle", no);
    	}
    	else {
    		messages = ResourceBundle.getBundle("Resources/MessagesBundle", us);	
    	}
	}
	
    /**
     * Method to get the internationalized messages of the default language.
     * 
     * @return The ResourceBundle with the messages
     */
    public static ResourceBundle getMessages() {   
    	try {
			setupInternationalize();
		} catch (IOException e) {
			LOGGER.log(Level.INFO, "Could not retrieve ResourceBundle", e);
		}
		return messages;
    }
}
