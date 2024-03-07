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
    private UserDataPacket client = null;
    private List<MessagePacket> messages = new ArrayList<>();

    /**
     * Creates a new channel data object
     * @param id id of channel
     */
    public ChannelData(int id) {
	this.id = id;
    }

    public ChannelData(int id, UserDataPacket userInfo){
	this.id = id;
	this.client = userInfo;
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
	if(this.messages.isEmpty() || this.messages.size() < pointer){
	    return null;
	}
	if(this.messages.size() < amount){
	     amount = this.messages.size();
	}
	if(this.messages.size() < amount+pointer){
	    amount = this.messages.size() - pointer;
	}
	MessagePacket[] messages = new MessagePacket[amount];
	int amountCounter = amount-1;
	for(int i = this.messages.size()-pointer-1; i >= this.messages.size()-(amount+pointer); i--){
	    messages[amountCounter] = this.messages.get(i);
	    amountCounter--;
	}
	return messages;
    }

}
