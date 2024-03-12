package se.liu.lukha243.both.requests;

import se.liu.lukha243.both.Packet;
import se.liu.lukha243.client_files.Client;
import se.liu.lukha243.server_files.ClientData;
import se.liu.lukha243.server_files.Server;

/**
 * The user data pcket. Its when the server has uppdated the user object. Like changed name or channel
 */
public class UserDataPacket extends Packet
{
    private String userName;

    private int currentChannel;

    private int[] ownedChannels;

    private int id;

    @Override public void runServer(final ClientData clientData, final Server server) {

    }

    @Override public void runClient(final Client client) {
        client.setUserInfo(this);
    }
    /**
     * Creates an userinfo object
     * @param userName the username of the user
     */
    public UserDataPacket(final String userName) {
	this.userName = userName;
        this.ownedChannels = new int[0];
    }
    public UserDataPacket(final String userName, int id) {
        this.userName = userName;
        this.ownedChannels = new int[0];
        this.id = id;
    }
    public UserDataPacket(UserDataPacket userDataPacket){
        this.userName = userDataPacket.userName;
        this.currentChannel = userDataPacket.currentChannel;
        this.ownedChannels = userDataPacket.ownedChannels;
        this.id = userDataPacket.id;
    }

    /**
     * Set the username of this object
     * @param userName the username of the user
     */
    public void setUserName(final String userName) {
	this.userName = userName;
    }

    /**
     * Gets the username of the user
     * @return username
     */
    public String getUserName() {
	return userName;
    }

    /**
     * Gets the current channel that the user is in
     * @return the id of the channel
     */
    public int getCurrentChannel() {
	return currentChannel;
    }

    /**
     * Sets the current channel of the user
     * @param currentChannel channel id
     */
    public void setCurrentChannel(final int currentChannel) {
	this.currentChannel = currentChannel;
    }

    /**
     * Addds new channel to owenership - Only ment to be called from the server
     * @param channelId the channel that gets added
     */
    public void addNewChannelOwnerShip(int channelId){
        int[] newOwnedChannels = new int[ownedChannels.length + 1];
        int i = 0;
	for(int ownedChannel : ownedChannels){
            newOwnedChannels[i] = ownedChannel;
            i++;
        }
        newOwnedChannels[ownedChannels.length] = channelId;
        ownedChannels = newOwnedChannels;
    }
    public boolean isOwner(int channelId){
        for(int ownedChannel : ownedChannels){
            if(ownedChannel == channelId) return true;
        }
        return false;
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }
}
