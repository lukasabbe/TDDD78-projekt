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
    private String passwordAttempt = "";
    private boolean hasJoined = true;
    public JoinChannelPacket(int channelId){
	this.channelId = channelId;
    }
    public JoinChannelPacket(int channelId, String passwordAttempt){
	this.channelId = channelId;
	this.passwordAttempt = passwordAttempt;
    }
    public JoinChannelPacket(int channelId, boolean hasJoined){
	this.channelId = channelId;
	this.hasJoined = hasJoined;
    }
    @Override public void runServer(final ClientData clientData, final Server server) {
	server.joinChannel(clientData, channelId, passwordAttempt);
    }

    @Override public void runClient(final Client client) {
	client.setData(this);
    }

    public boolean hasJoined() {
	return hasJoined;
    }
}
