package se.liu.lukha243.client_files;

import se.liu.lukha243.both.ChatSocket;
import se.liu.lukha243.both.requests.ChannelChangePacket;
import se.liu.lukha243.both.requests.CreateNewChannelPacket;
import se.liu.lukha243.both.requests.DisconnectPacket;
import se.liu.lukha243.both.requests.GetCurrentUsersPacket;
import se.liu.lukha243.both.requests.JoinChannelPacket;
import se.liu.lukha243.both.requests.KickRequestPacket;
import se.liu.lukha243.both.requests.ChannelPackets;
import se.liu.lukha243.both.requests.MessagePacket;
import se.liu.lukha243.both.requests.RequestOldMessagesPacket;
import se.liu.lukha243.both.requests.UserDataPacket;
import se.liu.lukha243.logg_files.MyLogger;
import se.liu.lukha243.both.Receiver;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * The client class. The class that interacts with the server.
 * Its main jobb is to send messages and recive them
 */
public class Client extends ChatSocket
{
    private Socket serverSocket;
    private volatile UserDataPacket userInfo;
    private ObjectOutputStream objectOutputStream;
    /**
     * For outgoing packets. Used bye reciver
     */
    public ObjectInputStream objectInputStream;
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
	if(IS_LOGGIN_ON)
		MyLogger.initLogger();
	serverSocket = new Socket(ip, port);
	OutputStream outputStream = serverSocket.getOutputStream();
	objectOutputStream = new ObjectOutputStream(outputStream);
	objectInputStream = new ObjectInputStream(serverSocket.getInputStream());
	userInfo = new UserDataPacket("");
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
	    Thread receiver = new Thread(new Receiver(this));
	    receiver.start();
	    objectOutputStream.writeObject(userInfo);
	    notifyAllMessageListeners();
	} catch (IOException e) {
	    logger.log(Level.SEVERE, e.toString(), e);
	    logger.log(Level.INFO, "Turning of client socket");
	    closeChatSocket();
	}
    }


    /**
     * Sends a messages to the server.
     * @param message the string messages
     */
    public void sendMessage(String message){
	try {
	    if(message.isBlank()) return;
	    objectOutputStream.writeObject(new MessagePacket(message, userInfo));
	} catch (IOException e) {
	    logger.log(Level.SEVERE, e.toString(), e);
	    logger.log(Level.INFO, "Turning of client socket");
	    closeChatSocket();
	}
    }

    /**
     * Creates an channel for the user
     */
    public void createChannel(){
	try{
	    userInfo = null;
	    objectOutputStream.writeObject(new CreateNewChannelPacket());
	    while (userInfo == null){
		Thread.onSpinWait();
	    }
	    notifyAllMessageListeners();
	    notifyAllChannelListeners();
	}catch (IOException e){
	    if(isClosed) return;
	    logger.log(Level.SEVERE, e.toString(), e);
	    logger.log(Level.INFO, "Turning of client");
	    closeChatSocket();
	}
    }

    /**
     * Joins a chossen channel
     * @param channelId the channel id you want to connect to
     */
    public void joinChannel(int channelId){
	try {
	    objectOutputStream.writeObject(new JoinChannelPacket(channelId));
	    while (true){
		final JoinChannelPacket joinChannelPacket = getDataFromServer(JoinChannelPacket.class);
		if(joinChannelPacket.hasJoined()) {
		    notifyAllMessageListeners();
		    notifyAllChannelListeners();
		    break;
		}else{
		    String passwordAttempt = JOptionPane.showInputDialog("This channel needs an password! Enter one");
		    if(passwordAttempt == null)
			return;
		    objectOutputStream.writeObject(new JoinChannelPacket(channelId, passwordAttempt));
		}
	    }
	} catch (IOException e) {
	    logger.log(Level.SEVERE, e.toString(), e);
	    logger.log(Level.INFO, "Turning of client");
	    closeChatSocket();
	}
    }

    /**
     * Set data for an channel.
     * @param channelId the channel id you want to set to
     * @param password password you want to set
     * @param lockedMode if you want the channel to be locked
     */
    public void setChannelData(int channelId, String password, boolean lockedMode){
	try{
	    objectOutputStream.writeObject(new ChannelChangePacket(channelId, password, lockedMode));
	}catch (IOException e){
	    logger.log(Level.SEVERE, e.toString(), e);
	    logger.log(Level.INFO, "Turning of client");
	    closeChatSocket();
	}
    }

    /**
     * Get all channels in a string formated style
     * @return
     */
    public String getAllChannels(){
	try{
	    objectOutputStream.writeObject(new ChannelPackets());
	    ChannelPackets channelPackets = getDataFromServer(ChannelPackets.class);
	    String allChannelString = channelPackets.getFormatedChannelString();
	    return allChannelString;
	}catch (IOException e){
	    logger.log(Level.SEVERE, e.toString(),e);
	    logger.log(Level.INFO, "Turning of client");
	    closeChatSocket();
	}
	return "";
    }

    /**
     * Gets the channels messages.
     * @param pointer from where to look in all messages
     * @param amount the amount to load
     * @return The messages
     */
    public MessagePacket[] getMessagesFromServer(int pointer, int amount) {
	try{
	    objectOutputStream.writeObject(new RequestOldMessagesPacket(pointer, amount));

	    final RequestOldMessagesPacket requestOldMessagesPacket = getDataFromServer(RequestOldMessagesPacket.class);
	    final MessagePacket[] returnData = requestOldMessagesPacket.getReturnData();
	    return returnData;
	}catch (IOException e){
	    if(isClosed) return null;
	    logger.log(Level.SEVERE, e.toString(), e);
	    logger.log(Level.INFO, "Turning of client");
	    closeChatSocket();
	}
	return null;
    }
    public List<UserDataPacket> getAllUsers(){
	try{
	    objectOutputStream.writeObject(new GetCurrentUsersPacket());
	    final GetCurrentUsersPacket getCurrentUsersPacket = getDataFromServer(GetCurrentUsersPacket.class);
	    final List<UserDataPacket> returnData = getCurrentUsersPacket.getUserDataPackets();
	    return returnData;
	}catch (IOException e){
	    logger.log(Level.SEVERE, e.toString(), e);
	    logger.log(Level.INFO, "Turning of client");
	    closeChatSocket();
	}
	return null;
    }

    public void kickUser(UserDataPacket user){
	try{
	    objectOutputStream.writeObject(new KickRequestPacket(user));
	}catch (IOException e){
	    logger.log(Level.SEVERE, e.toString(), e);
	    logger.log(Level.INFO, "Turning of client");
	    closeChatSocket();
	}
    }

    /**
     * Addes a new Listener. Runs whenever a the chat gets a new message
     * @param listener The listener you want to be called
     */
    public void addChatChangeListener(ChatChangeListener listener){
	listeners.add(listener);
    }

    public <T> T getDataFromServer(Class<T> clazz){
	while (data == null) {
	    Thread.onSpinWait();
	}
	if(!clazz.isInstance(data)){
	    throw new IllegalStateException("Data is not right");
	}
	T returnData = clazz.cast(data);
	data = null;
	return returnData;
    }


    /**
     * Notifyes all listenrs to a new message
     */
    public void notifyAllMessageListeners(){
	listeners.forEach(ChatChangeListener :: chatChange);
    }
    /**
     * Notifyes all listenrs to uppdate chat
     */
    public void notifyAllChannelListeners(){
	listeners.forEach(ChatChangeListener :: channelChange);
    }

    /**
     * Set the data of temp object
     * @param data
     */
    public void setData(final Object data) {
	this.data = data;
    }

    /**
     * Set user packet for changes
     * @param userInfo
     */
    public void setUserInfo(final UserDataPacket userInfo) {
	this.userInfo = userInfo;
    }

    /**
     * Gets user data
     * @return
     */
    public UserDataPacket getUserInfo() {
	return userInfo;
    }

    public boolean getIsClosed() {
	return isClosed;
    }

    @Override public boolean isServer() {
	return false;
    }

    /**
     * Close the client and all sockets.
     * If you close your socket is not usebel and shoud not be used
     */
    @Override public void closeChatSocket() {
	try {
	    objectOutputStream.writeObject(new DisconnectPacket());
	    objectInputStream.close();
	    objectOutputStream.close();
	    serverSocket.close();
	    isClosed = true;
	} catch (IOException e) {
	    logger.log(Level.SEVERE, e.toString(), e);
	    System.exit(1); // turn of program if it fails to close down
	}
    }
}

