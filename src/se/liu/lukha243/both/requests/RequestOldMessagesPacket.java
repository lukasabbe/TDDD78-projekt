package se.liu.lukha243.both.requests;

import se.liu.lukha243.both.Packet;
import se.liu.lukha243.client_files.Client;
import se.liu.lukha243.server_files.ClientData;
import se.liu.lukha243.server_files.Server;

import java.io.IOException;
import java.util.logging.Level;

/**
 * Request older messages. To load more than the 20 deafult messages
 */
public class RequestOldMessagesPacket extends Packet
{
    private int pointer;
    private int amount;
    private MessagePacket[] returnData = null;

    public RequestOldMessagesPacket(final int pointer, final int amount) {
	this.pointer = pointer;
	this.amount = amount;
    }

    @Override public void runServer(final ClientData clientData, final Server server) {
	try{
	    MessagePacket[] oldMessages = server.getChannels().get(clientData.userInfo.getCurrentChannel())
		    .getMessage(this);
	    returnData = oldMessages;
	    clientData.objectOutputStream.writeObject(this);
	}catch (IOException e){
	    LOGGER.log(Level.WARNING, e.toString(), e);
	    LOGGER.log(Level.INFO, "Turning off client due to IO error");
	    server.disconnectClient(clientData);
	}
    }

    @Override public void runClient(final UserDataPacket userInfo, final Client client) {
	client.setData(this);
    }

    public int getPointer() {
	return pointer;
    }

    public int getAmount() {
	return amount;
    }

    public MessagePacket[] getReturnData() {
	return returnData;
    }
}
