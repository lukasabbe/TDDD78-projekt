package se.liu.lukha243.both;

import java.io.Serializable;

/**
 * An Serializable object to send over user info to the server
 */
public class UserInfo implements Serializable, Packet
{
    private String userName;

    private int currentChannel;

    /**
     * Creates an userinfo object
     * @param userName the username of the user
     */
    public UserInfo(final String userName) {
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
    /**
     * Handler for request to server
     * @param handler
     */
    @Override public void trigger(final PacketHandler handler) {
	handler.handle(this);
    }
}
