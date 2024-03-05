package se.liu.liuid123.clientFiles;

import se.liu.liuid123.both.MessageData;
import se.liu.liuid123.both.RequestMessagesData;
import se.liu.liuid123.both.UserInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Client
{
    private Socket serverSocet = null;
    private BufferedReader in = null;
    private PrintWriter out = null;
    private UserInfo userInfo = null;
    private ObjectOutputStream objectOutputStream = null;
    private ObjectInputStream objectInputStream = null;
    public List<MessageData> messages = new ArrayList<>();
    private List<ChatChangeListener> listeners = new ArrayList<>();

    public Client(String ip, int port) {
	try {
	    serverSocet = new Socket(ip, port);
	    OutputStream outputStream = serverSocet.getOutputStream();
	    objectOutputStream = new ObjectOutputStream(outputStream);
	    objectInputStream = new ObjectInputStream(serverSocet.getInputStream());
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
	try {
	    objectOutputStream.writeObject(userInfo);
	    objectOutputStream.writeObject(new RequestMessagesData(0,10));
	    MessageData[] oldMessages = (MessageData[])objectInputStream.readObject();
	    System.out.println(Arrays.toString(oldMessages));
	    if(oldMessages != null){
		messages.addAll(List.of(oldMessages));
	    }
	    Thread reciver = new Thread(new Recevier());
	    reciver.start();
	} catch (IOException | ClassNotFoundException e) {
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
	try {
	    objectOutputStream.writeObject(new MessageData(message, userInfo));
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    private class Recevier implements Runnable{
	@Override public void run() {
	    try {
		MessageData message = (MessageData) objectInputStream.readObject();
		messages.add(message);
		notifyAllListners();
		String msg = message.getMessage();
		while (msg != null){
		    System.out.println(msg);
		    message = (MessageData) objectInputStream.readObject();
		    messages.add(message);
		    notifyAllListners();
		    msg = message.getMessage();
		}
		System.out.println("Server is turned off");
		out.close();
		in.close();
		serverSocet.close();
	    } catch (IOException | ClassNotFoundException e) {
		e.printStackTrace();
	    }
	}
    }
    public void addChatChangeListner(ChatChangeListener listener){
	listeners.add(listener);
    }
    public void notifyAllListners(){
	listeners.forEach(ChatChangeListener :: chatChange);
    }
}

