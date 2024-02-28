package se.liu.liuid123;

import se.liu.liuid123.ClientFiles.Client;

import java.util.Scanner;

public class TestChatClient
{
    public static void main(String[] args) {
	Client client = new Client("127.0.0.1",5000);
	client.setUserName("Lurre");
	client.startClient();

	final Scanner sc = new Scanner(System.in);

	while (true){
	    String msg = sc.nextLine();
	    if(msg.equals("close()"))
		client.closeClient();
	    client.sendMessage(msg);
	}

    }
}
