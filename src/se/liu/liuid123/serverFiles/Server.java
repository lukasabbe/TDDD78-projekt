package se.liu.liuid123.serverFiles;

import se.liu.liuid123.both.ChannelData;
import se.liu.liuid123.both.MessageData;
import se.liu.liuid123.both.RequestMessagesData;
import se.liu.liuid123.both.UserInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server
{
    private ServerSocket serverSocket = null;
    private List<ChannelData> channels = new ArrayList<>();
    private int currentChannelId = 0;
    private List<ClientData> connectedClientData = new ArrayList<>();

    private List<Thread> clientThreads = new ArrayList<>();

    private Thread serverThreed = null;
    private final int MAIN_CHANNEL = 0;

    public Server (int port){
	try{
	    this.serverSocket = new ServerSocket(port);
	    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
		try {
		    serverSocket.close();
		    serverThreed.interrupt();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }));
	}catch (IOException e){
	    e.printStackTrace();
	}
    }

    public void startServer(){
	channels.add( new ChannelData(createChannelId()));
	serverThreed = new Thread(new Connector());
	serverThreed.start();
	System.out.println("Server truned on!");
    }

    public void closeServer(){
	try{
	    serverSocket.close();
	    clientThreads.forEach(Thread::interrupt);
	    serverThreed.interrupt();
	}catch (IOException e){
	    e.printStackTrace();
	}
    }

    private class Connector implements Runnable {
	@Override public void run() {
	    while (true){
		try {
		    Socket client = serverSocket.accept();
		    System.out.println("New client connected");
		    PrintWriter clientOut = new PrintWriter(client.getOutputStream());
		    InputStream inputStream = client.getInputStream();
		    BufferedReader clientIn = new BufferedReader(new InputStreamReader(inputStream));
		    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
		    ObjectOutputStream objectOutputStream = new ObjectOutputStream(client.getOutputStream());
		    UserInfo userInfo = (UserInfo) objectInputStream.readObject();
		    userInfo.setCurrentChannel(MAIN_CHANNEL);
		    ClientData clientData = new ClientData(client,objectOutputStream, objectInputStream, userInfo);
		    connectedClientData.add(clientData);
		    Thread clientThread = new Thread(new Reciever(clientData));
		    clientThread.start();
		    clientThreads.add(clientThread);
		} catch (IOException | ClassNotFoundException e) {
		    e.printStackTrace();
		}
	    }
	}
    }


    private class Reciever implements Runnable {
	ClientData clientData;
	private Reciever(ClientData clientData) throws IOException {
	    this.clientData = clientData;
	}
	@Override public void run() {
	    try {
		while (true){
		    Object userRequest = clientData.objectInputStream.readObject();
		    if(userRequest instanceof MessageData){
			MessageData msg = (MessageData) userRequest;
			channels.get(clientData.userInfo.getCurrentChannel()).addMessage(msg);
			for (ClientData connectedClient : connectedClientData) {
			    connectedClient.objectOutputStream.writeObject(new MessageData(msg.getMessage(),msg.getUserInfo()));
			}
		    }else if(userRequest instanceof RequestMessagesData){
			RequestMessagesData requestMessagesData = (RequestMessagesData) userRequest;
			clientData.objectOutputStream.writeObject(
				channels.get(clientData.userInfo.getCurrentChannel())
					.getMessage(requestMessagesData)
			);
		    }
		}
	    } catch (IOException | ClassNotFoundException e) {
		e.printStackTrace();
	    }
	}
    }

    public int createChannelId() {
	currentChannelId++;
	return currentChannelId-1;
    }
}


