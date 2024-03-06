package se.liu.lukha243;

import se.liu.lukha243.client_files.Gui;
import se.liu.lukha243.logg_files.MyLogger;

/**
 * This is an test client.
 * Its used for testing client socket code
 */
public class TestChatClient
{
    public static void main(String[] args) {
	MyLogger.initLogger();
	Gui gui = new Gui();
	gui.open();
    }
}
