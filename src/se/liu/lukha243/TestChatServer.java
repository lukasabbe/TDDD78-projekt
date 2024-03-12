package se.liu.lukha243;

import se.liu.lukha243.server_files.Server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is an test Server.
 * Its used for testing Server socket code
 */
public class TestChatServer
{
    private static final Logger LOGGER = Logger.getLogger(TestChatServer.class.getName());
    public static void main(String[] args) {
	try {
	    Server server = new Server(5000);
	    server.startServer();
	} catch (IOException e) {
	    LOGGER.log(Level.SEVERE, e.toString(), e);
	}
    }
}
