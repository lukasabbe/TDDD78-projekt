package se.liu.lukha243.both.requests;

import se.liu.lukha243.both.Packet;
import se.liu.lukha243.client_files.Client;
import se.liu.lukha243.server_files.ChannelData;
import se.liu.lukha243.server_files.ClientData;
import se.liu.lukha243.server_files.Server;

/**
 * Send an channel request to the server
 */
public class ChannelChangePacket extends Packet
{
    private int channelId;
    private String password;
    private boolean lockedMode;
    public ChannelChangePacket(int channelId, String password, boolean lockedMode){
        this.channelId = channelId;
        this.password = password;
        this.lockedMode = lockedMode;
    }
    @Override public void runServer(final ClientData clientData, final Server server) {
        if(clientData.userInfo.isOwner(channelId)){
            ChannelData channel = server.getChannels().get(channelId);
            channel.setLocked(lockedMode);
            channel.setPasssword(password);
            server.setChannel(channelId, channel);
        }
    }

    @Override public void runClient(final Client client) {
    }
}
