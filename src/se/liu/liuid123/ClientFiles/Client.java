package se.liu.liuid123.ClientFiles;

import se.liu.liuid123.Both.UserInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class Client
{
    private Socket serverSocet = null;
    private BufferedReader in = null;
    private PrintWriter out = null;
    private UserInfo userInfo = null;

    public Client(String ip, int port) {
	try {
	    serverSocet = new Socket(ip, port);
	    OutputStream outputStream = serverSocet.getOutputStream();
	    userInfo = new UserInfo("");
	    out = new PrintWriter(outputStream);
	    in = new BufferedReader(new InputStreamReader(serverSocet.getInputStream()));

	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public void setUserName(String userName){
	userInfo.setUserName(userName);
    }

    public void startClient(){
	OutputStream outputStream = null;
	try {
	    outputStream = serverSocet.getOutputStream();
	    ObjectOutputStream objectOut = new ObjectOutputStream(outputStream);
	    objectOut.writeObject(userInfo);
	    Thread reciver = new Thread(new Recevier());
	    reciver.start();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public void closeClient(){
	try {
	    sendMessage("[Client] left server room");
	    out.flush();
	    in.close();
	    out.close();
	    serverSocet.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public void sendMessage(String message){
	out.println(message);
	out.flush();
    }

    private class Recevier implements Runnable{
	@Override public void run() {
	    try {
		String msg = in.readLine();
		while (msg != null){
		    System.out.println(msg);
		    msg = in.readLine();
		}
		System.out.println("Server is turned off");
		out.close();
		in.close();
		serverSocet.close();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }
}

