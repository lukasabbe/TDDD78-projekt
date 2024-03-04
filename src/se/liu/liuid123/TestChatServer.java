package se.liu.liuid123;

import se.liu.liuid123.serverFiles.Server;

public class TestChatServer
{
    public static void main(String[] args) {
	Server server = new Server(5000);
	server.startServer();


    }
}
