package se.liu.lukha243.logg_files;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

/**
 * Creates a new handler for the logging system.
 * It prints all to the console so you only need to use LOGGER.log
 */
public class PrintHandler extends StreamHandler
{
    @Override public void publish(final LogRecord record) {
	if(record.getLevel().equals(Level.FINE)){
	    System.out.println(record.getMessage());
	}
    }
}
