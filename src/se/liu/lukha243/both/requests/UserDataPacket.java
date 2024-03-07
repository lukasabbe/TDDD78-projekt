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
    @Override public void runServer(final ClientData clientData, final Server server) {

    }

    @Override public void runClient(final UserDataPacket userInfo, final Client client) {
        client.setUserInfo(this);
    }
    /**
     * Creates an userinfo object
     * @param userName the username of the user
     */
    public UserDataPacket(final String userName) {
	this.userName = userName;
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
}
