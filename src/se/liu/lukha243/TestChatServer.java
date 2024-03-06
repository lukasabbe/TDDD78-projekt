package se.liu.lukha243;

import se.liu.lukha243.server_files.Server;
/**
 * This is an test Server.
 * Its used for testing Server socket code
 */
public class TestChatServer
{
    public static void main(String[] args) {
	Server server = new Server(5000);
	server.startServer();


    }
}
