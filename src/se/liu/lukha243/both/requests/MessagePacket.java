package se.liu.lukha243.both.requests;

import se.liu.lukha243.both.Packet;
import se.liu.lukha243.client_files.Client;
import se.liu.lukha243.server_files.ClientData;
import se.liu.lukha243.server_files.Server;

import java.io.IOException;
import java.util.logging.Level;

/**
 * Messages packet is sent with a messages and the person who sent it.
 * Its also handels sending out messages to the rest of the clients connected to the server
 */
public class MessagePacket extends Packet
{
    private String message;
    private UserDataPacket userInfo;

    public MessagePacket(final String message, final UserDataPacket userInfo) {
	this.message = message;
	this.userInfo = userInfo;
    }

    @Override public void runServer(final ClientData clientData, final Server server) {
	try {
	    final int currentChannel = clientData.userInfo.getCurrentChannel();
	    server.getChannels().get(currentChannel).addMessage(this);
	    for (ClientData connectedClient : server.getConnectedClientData()) {
		if(currentChannel == connectedClient.userInfo.getCurrentChannel())
		    connectedClient.objectOutputStream.writeObject(new MessagePacket(message, userInfo));
	    }
	}catch (IOException e){
	    if(!clientData.isConnectionOn) return;
	    logger.log(Level.WARNING, e.toString(), e);
	    logger.log(Level.INFO, "Turning off client due to IO error");
	    server.disconnectClient(clientData);
	}
    }

    @Override public void runClient(final Client client) {
	new Thread(client::notifyAllMessageListeners).start();
    }

    public String getMessage() {
	return message;
    }

    public UserDataPacket getUserInfo() {
	return userInfo;
    }
}
