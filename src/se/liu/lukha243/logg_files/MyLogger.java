package se.liu.lukha243.logg_files;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * A class that is used for Initializeing the logger system
 */
public abstract class MyLogger
{
    /**
     * Logger for class.
     */
    protected final Logger logger;

    protected MyLogger(){
	logger = Logger.getLogger(this.getClass().getName());
    }

    /**
     * Initialize logger system
     */
    public static void initLogger(){
	try {
	    File logsDir = new File(System.getProperty("user.home") + File.separator +"logs");
	    if(!logsDir.exists()){
		boolean createdDir = logsDir.mkdirs();
		if(!createdDir)
		    printErrorLog();
	    }
	    FileHandler fileHandler = new FileHandler(
		    "%h"+
		    File.separator+
		    "logs"+
		    File.separator+
		    String.format(
			    "log-%s.txt",
			    DateTimeFormatter.ofPattern("dd-MM-yyyy-hh-mm").format(LocalDateTime.now())));
	    SimpleFormatter formatter = new SimpleFormatter();
	    fileHandler.setFormatter(formatter);
	    Logger mainLogger = Logger.getLogger("");
	    mainLogger.addHandler(new PrintHandler());
	    mainLogger.addHandler(fileHandler);
	} catch (IOException ignore) {
	    printErrorLog();
	}
    }

    private static void printErrorLog() {
	Logger.getLogger(MyLogger.class.getName()).log(Level.SEVERE, "Somting went wrong in init function for logger");
    }

}
