package se.liu.lukha243.server_files;

import se.liu.lukha243.both.requests.JoinChannelPacket;
import se.liu.lukha243.both.requests.MessagePacket;
import se.liu.lukha243.both.requests.UserDataPacket;
import se.liu.lukha243.logg_files.MyLogger;

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
public class Server extends MyLogger
{
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    /**
     * Main channel of the chat. Its the one you log in to.
     */
    public static final int MAIN_CHANNEL = 0;
    private static final boolean IS_LOGGIN_ON = true;

    private ServerSocket serverSocket;
    private List<ChannelData> channels = new ArrayList<>();
    private int currentChannelId = 0;
    private List<ClientData> connectedClientData = new ArrayList<>();

    private List<Thread> clientThreads = new ArrayList<>();

    private Thread serverThread = null;

    private UserDataPacket serverUser = new UserDataPacket("[SERVER]",-1);

    /**
     * Creates the server object
     * @param port the port you want the server to run on
     */
    public Server (int port) throws IOException{
	if(IS_LOGGIN_ON)
	    MyLogger.initLogger();
	this.serverSocket = new ServerSocket(port);
    }

    /**
     * Starts the server
     */
    public void startServer(){
	Runtime.getRuntime().addShutdownHook(new Thread(this::closeServer));
	channels.add(new ChannelData(createChannelId(),serverUser));
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
	    logger.log(Level.SEVERE, e.toString(), e);
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
		    userInfo.setId(connectedClientData.size());
		    ClientData clientData = new ClientData(client,objectOutputStream, objectInputStream, userInfo,connectedClientData.size());
		    connectedClientData.add(clientData);
		    Thread clientThread = new Thread(new Receiver(clientData, server));
		    clientThread.start();
		    clientThreads.add(clientThread);
		} catch (IOException | ClassNotFoundException e) {
		    logger.log(Level.SEVERE, e.toString(), e);
		    logger.log(Level.INFO, "Turning off server due to error");
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
	    logger.log(Level.WARNING, e.toString(), e);
	    logger.log(Level.INFO, "Problems happend when disconnecting user");
	}
    }

    public void createNewChannel(ClientData clientData){
	try {
	    int newChannel = createChannelId();
	    clientData.userInfo.setCurrentChannel(newChannel);
	    clientData.userInfo.addNewChannelOwnerShip(newChannel);
	    final ChannelData channelData = new ChannelData(newChannel,clientData.userInfo);
	    channelData.setLocked(false);
	    channels.add(channelData);
	    System.out.println(clientData.userInfo.isOwner(newChannel));
	    clientData.objectOutputStream.writeObject(new UserDataPacket(clientData.userInfo));
	    final MessagePacket
		    serverMessage = new MessagePacket("Vällkomen till din nya chat\nID:" + newChannel + ". Andra kan joina med det ID:t",
						    new UserDataPacket("[SERVER]",-1));
	    channels.get(newChannel).addMessage(serverMessage);
	}catch (IOException e){
	    if(!clientData.isConnectionOn) return;
	    logger.log(Level.WARNING, e.toString(), e);
	    logger.log(Level.INFO, "Disconnecting user");
	    disconnectClient(clientData);
	}

    }

    public void joinChannel(ClientData clientData, int channelId, String password){
	try{
	    if (channels.size() <= channelId)
		channelId = MAIN_CHANNEL;
	    final ChannelData channelData = channels.get(channelId);
	    if(channelData.isLocked()){
		System.out.println(clientData.userInfo.isOwner(channelId));
		if(!channelData.getPasssword().equals(password) && !clientData.userInfo.isOwner(channelId))
		    clientData.objectOutputStream.writeObject(new JoinChannelPacket(channelId,false));
		else{
		    clientData.userInfo.setCurrentChannel(channelId);
		    clientData.objectOutputStream.writeObject(new JoinChannelPacket(channelId,true));
		    clientData.objectOutputStream.writeObject(new UserDataPacket(clientData.userInfo));
		}
	    }else{
		clientData.userInfo.setCurrentChannel(channelId);
		clientData.objectOutputStream.writeObject(new JoinChannelPacket(channelId));
		clientData.objectOutputStream.writeObject(new UserDataPacket(clientData.userInfo));
	    }
	}catch (IOException e){
	    if(!clientData.isConnectionOn) return;
	    logger.log(Level.WARNING, e.toString(), e);
	    logger.log(Level.INFO, "Disconnecting user");
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
    public void setChannel(int index, ChannelData channelData){
	channels.set(index,channelData);
    }
    public List<ClientData> getConnectedClientData() {
	return connectedClientData;
    }

    public ClientData getClientData(UserDataPacket userInfo){
	for(ClientData clientData : connectedClientData){
	    if(clientData.getId() == userInfo.getId()){
		return clientData;
	    }
	}
	return null;
    }
}


