package se.liu.lukha243.logg_files;

import se.liu.lukha243.client_files.Client;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * A class for all setings for logging
 */
public class MyLogger
{
    private static final Logger LOGGER = Logger.getLogger(MyLogger.class.getName());
    /**
     * Initialize logger system
     */
    public static void initLogger(){
	try {
	    File logsDir = new File(System.getProperty("user.home") + "/logs");
	    if(!logsDir.exists()){
		logsDir.mkdirs();
	    }
	    FileHandler fileHandler = new FileHandler("%h/logs/"+String.format("log-%s.txt", DateTimeFormatter.ofPattern("dd-MM-yyyy-hh-mm").format(LocalDateTime.now())));
	    SimpleFormatter formatter = new SimpleFormatter();
	    fileHandler.setFormatter(formatter);
	    Logger mainLogger = Logger.getLogger("");
	    mainLogger.addHandler(new PrintHandler());
	    mainLogger.addHandler(fileHandler);
	} catch (IOException e) {
	    LOGGER.log(Level.SEVERE, e.toString(), e);
	}
    }

}
