package se.liu.lukha243.logg_files;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class MyLogger
{
    public MyLogger(){
	try {
	    FileHandler fileHandler = new FileHandler(String.format("log-%s.txt", LocalDateTime.now())); 
	    Logger mainLogger = Logger.getLogger("");
	    mainLogger.addHandler(new PrintHandler());
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}
