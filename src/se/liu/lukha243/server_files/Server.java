package se.liu.lukha243.server_files;

import se.liu.lukha243.both.ChannelData;
import se.liu.lukha243.both.MessageData;
import se.liu.lukha243.both.Packet;
import se.liu.lukha243.both.PacketHandler;
import se.liu.lukha243.both.RequestMessagesData;
import se.liu.lukha243.both.UserInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
Saker att fråga:
1. Var ska man skapa sin logger enl er?
 */


/**
 * Create a server for the chat serivice
 */
public class Server
{
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    private ServerSocket serverSocket = null;
    private List<ChannelData> channels = new ArrayList<>();
    private int currentChannelId = 0;
    private List<ClientData> connectedClientData = new ArrayList<>();

    private List<Thread> clientThreads = new ArrayList<>();

    private Thread serverThread = null;

    /**
     * Creates the server object
     * @param port the port you want the server to run on
     */
    public Server (int port) throws IOException{
	this.serverSocket = new ServerSocket(port);
    }

    /**
     * Starts the server
     */
    public void startServer(){
	Runtime.getRuntime().addShutdownHook(new Thread(this::closeServer));
	channels.add(new ChannelData(createChannelId()));
	serverThread = new Thread(new Connector());
	serverThread.start();
	System.out.println("Server truned on!");
    }

    /**
     * Turn of the server and all its threeds
     */
    public void closeServer(){
	try{
	    serverSocket.close();
	    connectedClientData.forEach(this::disconectClient);
	    clientThreads.forEach(Thread::interrupt);
	    serverThread.interrupt();
	    System.exit(0);
	}catch (IOException e){
	    LOGGER.log(Level.SEVERE, e.toString(), e);
	}
    }

    private class Connector implements Runnable {
	@Override public void run() {
	    while (true){
		try {
		    Socket client = serverSocket.accept();
		    System.out.println("New client connected");
		    InputStream inputStream = client.getInputStream();
		    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
		    ObjectOutputStream objectOutputStream = new ObjectOutputStream(client.getOutputStream());
		    UserInfo userInfo = (UserInfo) objectInputStream.readObject();
		    int mainChannel = 0;
		    userInfo.setCurrentChannel(mainChannel);
		    ClientData clientData = new ClientData(client,objectOutputStream, objectInputStream, userInfo);
		    connectedClientData.add(clientData);
		    Thread clientThread = new Thread(new Receiver(clientData));
		    clientThread.start();
		    clientThreads.add(clientThread);
		} catch (IOException | ClassNotFoundException e) {
		    LOGGER.log(Level.SEVERE, e.toString(), e);
		    LOGGER.log(Level.INFO, "Turning off server due to error");
		    closeServer();
		}
	    }
	}
    }


    private class Receiver implements Runnable, PacketHandler
    {
	private ClientData clientData;
	private Receiver(ClientData clientData) {
	    this.clientData = clientData;
	}
	@Override public void run() {
	    try {
		while (true){
		    if(clientData.socket.isClosed()) disconectClient(clientData);
		    Object userRequest = clientData.objectInputStream.readObject();
		    ((Packet) userRequest).dispatch(this);
		}
	    } catch (IOException ignored) {
		disconectClient(clientData);
	    }catch (ClassNotFoundException e){
		e.printStackTrace();
	    }
	}

	@Override public void handle(final MessageData msg) {
	    try {
		channels.get(clientData.userInfo.getCurrentChannel()).addMessage(msg);
		for (ClientData connectedClient : connectedClientData) {
		    connectedClient.objectOutputStream.writeObject(new MessageData(msg.getMessage(),msg.getUserInfo()));
		}
	    }catch (IOException e){
		LOGGER.log(Level.WARNING, e.toString(), e);
		LOGGER.log(Level.INFO, "Turning off client due to IO error");
		disconectClient(clientData);
	    }
	}

	@Override public void handle(final ChannelData packet) {

	}

	@Override public void handle(final RequestMessagesData requestMessagesData) {
	    try{
		clientData.objectOutputStream.writeObject(
			channels.get(clientData.userInfo.getCurrentChannel())
				.getMessage(requestMessagesData)
		);
	    }catch (IOException e){
		LOGGER.log(Level.WARNING, e.toString(), e);
		LOGGER.log(Level.INFO, "Turning off client due to IO error");
		disconectClient(clientData);
	    }
	}
    }

    public void disconectClient(ClientData clientData){
	try {
	    clientData.socket.close();
	    clientData.objectOutputStream.close();
	    clientData.objectInputStream.close();
	    int index = connectedClientData.indexOf(clientData);
	    connectedClientData.remove(index);
	    clientThreads.get(index).interrupt();
	    clientThreads.remove(index);
	    System.out.println("Client disconected");
	} catch (IOException e) {
	    e.printStackTrace();
	    System.out.println("Problem happend when client disconected");
	}
    }

    public int createChannelId() {
	currentChannelId++;
	return currentChannelId-1;
    }
}


