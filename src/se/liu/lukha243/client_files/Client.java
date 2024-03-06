package se.liu.lukha243.client_files;

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

    private Socket serverSocket;
    private UserInfo userInfo;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private List<MessageData> messages = new ArrayList<>();
    private List<ChatChangeListener> listeners = new ArrayList<>();
    private boolean isClosed;

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
	    objectOutputStream.writeObject(userInfo);
	    MessageData[] oldMessages = getMessagesFromServer(0,20);
	    System.out.println(Arrays.toString(oldMessages));
	    if(oldMessages != null){
		messages.addAll(List.of(oldMessages));
	    }
	    Thread receiver = new Thread(new Receiver());
	    receiver.start();
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
	    objectOutputStream.writeObject(new MessageData(message, userInfo));
	} catch (IOException e) {
	    LOGGER.log(Level.SEVERE, e.toString(), e);
	    LOGGER.log(Level.INFO, "Turning of client socket");
	    closeClient();
	}
    }

    public void createChannel(){
	try{
	    objectOutputStream.writeObject(new Request(RequestType.CREATE_NEW_CHANNEL));
	    userInfo = (UserInfo)objectInputStream.readObject();
	    MessageData[] channelMessages = getMessagesFromServer(0, 20);
	    messages.clear();
	    if(channelMessages != null)
	    	messages.addAll(List.of(channelMessages));
	    notifyAllListeners();
	}catch (IOException | ClassNotFoundException e){
	    if(isClosed) return;
	    LOGGER.log(Level.SEVERE, e.toString(), e);
	    LOGGER.log(Level.INFO, "Turning of client");
	    closeClient();
	}
    }

    public void joinChannel(){

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
	    return (MessageData[]) objectInputStream.readObject();
	}catch (IOException | ClassNotFoundException e){
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

    /**
     * Gets all messages
     * @return all messages
     */
    public List<MessageData> getMessages() {
	return messages;
    }
    private class Receiver implements Runnable{
	@Override public void run() {
	    try {
		while (true){
		    MessageData message = (MessageData) objectInputStream.readObject();
		    messages.add(message);
		    notifyAllListeners();
		    String msg = message.getMessage();
		    System.out.println(msg); // REMOVE WHEN DONE
		}
	    } catch (IOException | ClassNotFoundException e) {
		if(isClosed) return;
		LOGGER.log(Level.SEVERE, e.toString(), e);
		LOGGER.log(Level.INFO, "Turning of client");
		closeClient();
	    }
	}
    }
    private void notifyAllListeners(){
	listeners.forEach(ChatChangeListener :: chatChange);
    }
}

