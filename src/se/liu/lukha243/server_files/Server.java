package se.liu.lukha243.server_files;

import se.liu.lukha243.both.requests.JoinChannelPacket;
import se.liu.lukha243.both.requests.MessagePacket;
import se.liu.lukha243.both.requests.UserDataPacket;

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
	serverThread = new Thread(new Connector(this));
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
	private Server server;
	private Connector(Server server){
	    this.server = server;
	}
	@Override public void run() {
	    while (true){
		try {
		    Socket client = serverSocket.accept();
		    System.out.println("New client connected");
		    InputStream inputStream = client.getInputStream();
		    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
		    ObjectOutputStream objectOutputStream = new ObjectOutputStream(client.getOutputStream());
		    UserDataPacket userInfo = (UserDataPacket) objectInputStream.readObject();
		    int mainChannel = 0;
		    userInfo.setCurrentChannel(mainChannel);
		    ClientData clientData = new ClientData(client,objectOutputStream, objectInputStream, userInfo);
		    connectedClientData.add(clientData);
		    Thread clientThread = new Thread(new Receiver(clientData, server));
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


    /**
     * Disconnect the client you want
     * @param clientData the client you want to disconnect
     */
    public void disconnectClient(ClientData clientData){
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

    public void createNewChannel(ClientData clientData){
	try {
	    int newChannel = createChannelId();
	    clientData.userInfo.setCurrentChannel(newChannel);
	    channels.add(new ChannelData(newChannel, clientData.userInfo));
	    clientData.objectOutputStream.writeObject(clientData.userInfo);
	    final MessagePacket
		    serverMessage = new MessagePacket("Vällkomen till din nya chat\nID:" + newChannel + ". Andra kan joina med det ID:t",
						    new UserDataPacket("[SERVER]"));
	    channels.get(newChannel).addMessage(serverMessage);
	}catch (IOException e){
	    if(!clientData.isConnectionOn) return;
	    LOGGER.log(Level.WARNING, e.toString(), e);
	    LOGGER.log(Level.INFO, "Disconnecting user");
	    disconnectClient(clientData);
	}

    }

    public void joinChannel(ClientData clientData, int channelId){
	try{
	    if(channels.size() >= channelId){
		clientData.userInfo.setCurrentChannel(channelId);
		clientData.objectOutputStream.writeObject(new JoinChannelPacket(channelId));
	    }else{
		clientData.userInfo.setCurrentChannel(MAIN_CHANNEL);
		clientData.objectOutputStream.writeObject(new JoinChannelPacket(channelId));
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

    public List<ChannelData> getChannels() {
	return channels;
    }

    public List<Thread> getClientThreads() {
	return clientThreads;
    }

    public List<ClientData> getConnectedClientData() {
	return connectedClientData;
    }
}


