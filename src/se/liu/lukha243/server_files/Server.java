package se.liu.lukha243.server_files;

import se.liu.lukha243.both.ChannelData;
import se.liu.lukha243.both.Request;
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
2. Vad fan snackar Similar children om
 */


/**
 * Create a server for the chat serivice.
 * It takes cares of incoming messages and sening them to all of the users.
 * It also takes care of alot of difrent types of requests
 */
public class Server
{
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    private static final int MAIN_CHANNEL = 0;
    private ServerSocket serverSocket;
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
		    Packet userRequest = (Packet) clientData.objectInputStream.readObject();
		    userRequest.dispatchHandler(this);
		}
	    } catch (IOException e) {
		if(!clientData.isConnectionOn) return;
		LOGGER.log(Level.WARNING, e.toString(), e);
		LOGGER.log(Level.INFO, "Due to IOerror we disconect the user");
		disconnectClient(clientData);
	    }catch (ClassNotFoundException e){
		LOGGER.log(Level.SEVERE, e.toString(), e);
		closeServer();
	    }
	}

	@Override public void handle(final MessageData msg) {
	    try {
		final int currentChannel = clientData.userInfo.getCurrentChannel();
		channels.get(currentChannel).addMessage(msg);
		for (ClientData connectedClient : connectedClientData) {
		    if(currentChannel == connectedClient.userInfo.getCurrentChannel())
		    	connectedClient.objectOutputStream.writeObject(new MessageData(msg.getMessage(),msg.getUserInfo()));
		}
	    }catch (IOException e){
		if(!clientData.isConnectionOn) return;
		LOGGER.log(Level.WARNING, e.toString(), e);
		LOGGER.log(Level.INFO, "Turning off client due to IO error");
		disconnectClient(clientData);
	    }
	}

	@Override public void handle(final RequestMessagesData requestMessagesData) {
	    try{
		MessageData[] oldMessages = channels.get(clientData.userInfo.getCurrentChannel())
			.getMessage(requestMessagesData);
		requestMessagesData.setReturnData(oldMessages);
		clientData.objectOutputStream.writeObject(requestMessagesData);
	    }catch (IOException e){
		LOGGER.log(Level.WARNING, e.toString(), e);
		LOGGER.log(Level.INFO, "Turning off client due to IO error");
		disconnectClient(clientData);
	    }
	}

	@Override public void handle(final UserInfo packet) {
	    return;
	}

	@Override public void handle(final Request packet) {
	    switch (packet.getRequestType()){
		case DISCONNECT_REQUEST -> disconnectClient(clientData);
		case CREATE_NEW_CHANNEL -> createNewChannel(clientData);
		case JOIN_CHANNEL -> joinChannel(clientData, packet.getArgs()[0]);
	    }
	}

    }

    /**
     * Disconnect the client you want
     * @param clientData the client you want to disconnect
     */
    private void disconnectClient(ClientData clientData){
	try {
	    if(!clientData.isConnectionOn) return;
	    clientData.isConnectionOn = false;
	    clientData.socket.close();
	    clientData.objectOutputStream.close();
	    clientData.objectInputStream.close();
	    int index = connectedClientData.indexOf(clientData);
	    connectedClientData.remove(index);
	    clientThreads.get(index).interrupt();
	    clientThreads.remove(index);
	    System.out.println("Client disconected");
	} catch (IOException e) {
	    LOGGER.log(Level.WARNING, e.toString(), e);
	    LOGGER.log(Level.INFO, "Problems happend when disconnecting user");
	}
    }

    private void createNewChannel(ClientData clientData){
	try {
	    int newChannel = createChannelId();
	    clientData.userInfo.setCurrentChannel(newChannel);
	    channels.add(new ChannelData(newChannel, clientData.userInfo));
	    clientData.objectOutputStream.writeObject(clientData.userInfo);
	    final MessageData serverMessage = new MessageData("Vällkomen till din nya chat\nID:" + newChannel + ". Andra kan joina med det ID:t",
						    new UserInfo("[SERVER]"));
	    channels.get(newChannel).addMessage(serverMessage);
	}catch (IOException e){
	    if(!clientData.isConnectionOn) return;
	    LOGGER.log(Level.WARNING, e.toString(), e);
	    LOGGER.log(Level.INFO, "Disconnecting user");
	    disconnectClient(clientData);
	}

    }

    private void joinChannel(ClientData clientData, int channelId){
	try{
	    if(channels.size() >= channelId){
		clientData.userInfo.setCurrentChannel(channelId);
		clientData.objectOutputStream.writeObject(clientData.userInfo);
	    }else{
		clientData.userInfo.setCurrentChannel(MAIN_CHANNEL);
		clientData.objectOutputStream.writeObject(clientData.userInfo);
	    }
	}catch (IOException e){
	    if(!clientData.isConnectionOn) return;
	    LOGGER.log(Level.WARNING, e.toString(), e);
	    LOGGER.log(Level.INFO, "Disconnecting user");
	    disconnectClient(clientData);
	}
    }

    private int createChannelId() {
	currentChannelId++;
	return currentChannelId-1;
    }
}


