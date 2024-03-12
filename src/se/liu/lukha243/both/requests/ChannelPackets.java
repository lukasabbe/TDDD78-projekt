package se.liu.lukha243.both.requests;

import se.liu.lukha243.both.Packet;
import se.liu.lukha243.client_files.Client;
import se.liu.lukha243.server_files.ChannelData;
import se.liu.lukha243.server_files.ClientData;
import se.liu.lukha243.server_files.Server;

import java.io.IOException;
import java.util.logging.Level;

/**
 * Send an request to the server to get all channels. It returns an formated string
 */
public class ChannelPackets extends Packet
{
    private String formatedChannelString = null;
    public ChannelPackets(){}
    public ChannelPackets(String formatedChannelString){
	this.formatedChannelString = formatedChannelString;
    }
    @Override public void runServer(final ClientData clientData, final Server server) {
	try{
	    StringBuilder formatedString = new StringBuilder();
	    formatedString.append("All channels\n");
	    for(ChannelData channelData : server.getChannels()){
		formatedString
			.append("Id: ")
			.append(channelData.getId())
			.append(". Needs password: ")
			.append(channelData.isLocked())
			.append(". Owner of channel ")
			.append(channelData.getOwnerUser().getUserName())
			.append("\n");
	    }
	    final ChannelPackets channelPackets = new ChannelPackets(formatedString.toString());
	    clientData.objectOutputStream.writeObject(channelPackets);
	}catch (IOException e){
	    logger.log(Level.WARNING, e.toString(), e);
	    logger.log(Level.INFO, "Disconecting user");
	    server.disconnectClient(clientData);
	}
    }

    @Override public void runClient(final Client client) {
	client.setData(this);
    }

    public String getFormatedChannelString() {
	return formatedChannelString;
    }
}
