package se.liu.lukha243.both;

import java.io.Serializable;

/**
 * Is data for a message in serializable form.
 * It stores the messages and the sender of the message.
 */
public class MessageData implements Serializable, Packet
{
    private String message;
    private UserInfo userInfo;

    /**
     * Creates a new message data object.
     * @param message the message you want to send
     * @param userInfo the sender of the message
     */
    public MessageData(String message, UserInfo userInfo){
	this.message = message;
	this.userInfo = userInfo;
    }

    /**
     * Gets the message
     * @return message
     */
    public String getMessage() {
	return message;
    }

    /**
     * Get the user that has sent this message
     * @return user object
     */
    public UserInfo getUserInfo() {
	return userInfo;
    }

    /**
     * Handler for request to server
     * @param handler
     */
    @Override
    public void trigger(final PacketHandler handler) {
	handler.handle(this);
    }
}
