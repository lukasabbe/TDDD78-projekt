package se.liu.lukha243.both.requests;

import se.liu.lukha243.both.Packet;
import se.liu.lukha243.client_files.Client;
import se.liu.lukha243.server_files.ClientData;
import se.liu.lukha243.server_files.Server;

/**
 * Join packet, is sent to from the client to join a new channel
 */
public class JoinChannelPacket extends Packet
{
    private int channelId;
    public JoinChannelPacket(int channelId){
	this.channelId = channelId;
    }
    @Override public void runServer(final ClientData clientData, final Server server) {
	server.joinChannel(clientData, channelId);
    }

    @Override public void runClient(final Client client) {
	client.setData(this);
    }

    public int getChannelId() {
	return channelId;
    }

}
