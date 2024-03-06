package se.liu.lukha243.client_files;

import se.liu.lukha243.both.Packet;
import se.liu.lukha243.both.PacketHandler;
import se.liu.lukha243.both.Request;
import se.liu.lukha243.both.MessageData;
import se.liu.lukha243.both.RequestMessagesData;
import se.liu.lukha243.both.RequestType;
import se.liu.lukha243.both.UserInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The client class. The class that interacts with the server.
 * Its main jobb is to send messages and recive them
 */
public class Client
{
    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
    /**
     * The deafult amount of messages sent over the socket
     */
    public static final int DEAFULT_AMOUNT_MESSAGES = 20;
    private Socket serverSocket;
    private volatile UserInfo userInfo;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private List<MessageData> messages = new ArrayList<>();
    private List<ChatChangeListener> listeners = new ArrayList<>();
    private boolean isClosed;
    private volatile Object data = null;

    /**
     * Creates a new client object
     * @param ip The ip of the server you want to connect to
     * @param port The port of the server you want to connect to
     * @throws IOException
     * @throws UnknownHostException
     */
    public Client(String ip, int port) throws IOException, UnknownHostException {
	serverSocket = new Socket(ip, port);
	OutputStream outputStream = serverSocket.getOutputStream();
	objectOutputStream = new ObjectOutputStream(outputStream);
	objectInputStream = new ObjectInputStream(serverSocket.getInputStream());
	userInfo = new UserInfo("");
	isClosed = false;
    }

    /**
     * Set clients user a name. Call this before starting the client.
     * @param userName The user name
     */
    public void setUserName(String userName){
	userInfo.setUserName(userName);
    }

    /**
     * Start the client and starts to await messages from server
     */
    public void startClient(){
	try {
	    Thread receiver = new Thread(new Receiver());
	    receiver.start();
	    objectOutputStream.writeObject(userInfo);
	    MessageData[] oldMessages = getMessagesFromServer(0,20);
	    if(oldMessages != null){
		messages.addAll(List.of(oldMessages));
	    }
	    notifyAllListeners();
	} catch (IOException e) {
	    LOGGER.log(Level.SEVERE, e.toString(), e);
	    LOGGER.log(Level.INFO, "Turning of client socket");
	    closeClient();
	}
    }

    /**
     * Close the client and all sockets.
     * If you close your socket is not usebel and shoud not be used
     */
    public void closeClient(){
	try {
	    objectOutputStream.writeObject(new Request(RequestType.DISCONNECT_REQUEST));
	    objectInputStream.close();
	    objectOutputStream.close();
	    serverSocket.close();
	    isClosed = true;
	} catch (IOException e) {
	   LOGGER.log(Level.SEVERE, e.toString(), e);
	}
    }

    /**
     * Sends a messages to the server.
     * @param message the string messages
     */
    public void sendMessage(String message){
	try {
	    if(message.isBlank()) return;
	    objectOutputStream.writeObject(new MessageData(message, userInfo));
	} catch (IOException e) {
	    LOGGER.log(Level.SEVERE, e.toString(), e);
	    LOGGER.log(Level.INFO, "Turning of client socket");
	    closeClient();
	}
    }

    /**
     * Creates an channel for the user
     */
    public void createChannel(){
	try{
	    userInfo = null;
	    objectOutputStream.writeObject(new Request(RequestType.CREATE_NEW_CHANNEL));
	    while (userInfo == null) {
		Thread.onSpinWait();
	    }
	    messages.clear();
	    MessageData[] channelMessages = getMessagesFromServer(0, 20);
	    if(channelMessages != null)
	    	messages.addAll(List.of(channelMessages));
	    notifyAllListeners();
	}catch (IOException e){
	    if(isClosed) return;
	    LOGGER.log(Level.SEVERE, e.toString(), e);
	    LOGGER.log(Level.INFO, "Turning of client");
	    closeClient();
	}
    }

    /**
     * Joins a chossen channel
     * @param channelId the channel id you want to connect to
     */
    public void joinChannel(int channelId){
	try {
	    userInfo = null;
	    objectOutputStream.writeObject(new Request(RequestType.JOIN_CHANNEL,new int[]{channelId}));
	    messages.clear();
	    while (userInfo == null) {
		Thread.onSpinWait();
	    }
	    MessageData[] channelMessages = getMessagesFromServer(0, 20);
	    if(channelMessages != null)
		messages.addAll(List.of(channelMessages));
	    notifyAllListeners();
	} catch (IOException e) {
	    LOGGER.log(Level.SEVERE, e.toString(),e);
	    closeClient();
	}
    }

    /**
     * Gets the channels messages.
     * @param pointer from where to look in all messages
     * @param amount the amount to load
     * @return The messages
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public MessageData[] getMessagesFromServer(int pointer, int amount) {
	try{
	    objectOutputStream.writeObject(new RequestMessagesData(pointer,amount));
	    while (data == null) {
		Thread.onSpinWait();
	    }
	    Object copyData = data;
	    data = null;
	    return ((RequestMessagesData) copyData).getReturnData();
	}catch (IOException e){
	    if(isClosed) return null;
	    LOGGER.log(Level.SEVERE, e.toString(), e);
	    LOGGER.log(Level.INFO, "Turning of client");
	    closeClient();
	}
	return null;
    }

    /**
     * Addes a new Listener. Runs whenever a the chat gets a new message
     * @param listener The listener you want to be called
     */
    public void addChatChangeListener(ChatChangeListener listener){
	listeners.add(listener);
    }

    private class Receiver implements Runnable, PacketHandler
    {
	@Override public void run() {
	    try {
		while (true){
		    ((Packet) objectInputStream.readObject()).dispatchHandler(this);
		}
	    } catch (IOException | ClassNotFoundException e) {
		if(isClosed) return;
		LOGGER.log(Level.SEVERE, e.toString(), e);
		LOGGER.log(Level.INFO, "Turning of client");
		closeClient();
	    }
	}

	@Override public void handle(final MessageData packet) {
	    notifyAllListeners();
	}

	@Override public void handle(final RequestMessagesData packet) {
	    data = packet;
	}

	@Override public void handle(final UserInfo packet) {
	    userInfo = packet;
	}

	@Override public void handle(final Request packet) {

	}
    }
    private void notifyAllListeners(){
	listeners.forEach(ChatChangeListener :: chatChange);
    }
}

