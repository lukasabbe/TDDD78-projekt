package se.liu.lukha243.server_files;

import se.liu.lukha243.both.requests.MessagePacket;
import se.liu.lukha243.both.requests.RequestOldMessagesPacket;
import se.liu.lukha243.both.requests.UserDataPacket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The serialzable channel data. That is used to tell servern and client the data of them both
 */
public class ChannelData implements Serializable
{
    private final int id;
    private List<MessagePacket> messages = new ArrayList<>();
    private String passsword = "";
    private boolean locked = false;
    private UserDataPacket ownerUser;

    /**
     * Creates a new channel data object
     * @param id id of channel
     * @param ownerUser the owner of the channel
     */
    public ChannelData(int id, UserDataPacket ownerUser){
	this.id = id;
	this.ownerUser = ownerUser;
    }

    /**
     * Add a message to the channel. It will be saved here
     * @param message Message object that you want to save
     */
    public void addMessage(MessagePacket message){
	messages.add(message);
    }

    /**
     * get selected amount of messages from channel. This is to limting it from sending all messages at once.
     * @param requestMessagesData An object were you can spefic how many messages and from where you want them
     * @return returns the selected messages
     */
    public MessagePacket[] getMessage(RequestOldMessagesPacket requestMessagesData){
	int pointer = requestMessagesData.getPointer();
	int amount = requestMessagesData.getAmount();
	final int size = this.messages.size();
	if(this.messages.isEmpty() || size < pointer){
	    return null;
	}
	if(size < amount){
	     amount = size;
	}
	if(size < amount + pointer){
	    amount = size - pointer;
	}
	MessagePacket[] messages = new MessagePacket[amount];
	int amountCounter = amount-1;
	for(int i = size - pointer - 1; i >= size - (amount + pointer); i--){
	    messages[amountCounter] = this.messages.get(i);
	    amountCounter--;
	}
	return messages;
    }

    public String getPasssword() {
	return passsword;
    }
    public void setPasssword(String passsword){
	this.passsword = passsword;
    }

    public boolean isLocked() {
	return locked;
    }
    public void setLocked(final boolean locked) {
	this.locked = locked;
    }

    public int getId() {
	return id;
    }

    public UserDataPacket getOwnerUser() {
	return ownerUser;
    }
}
