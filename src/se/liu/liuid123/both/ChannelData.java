package se.liu.liuid123.both;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChannelData implements Serializable
{
    public final int id;
    private List<MessageData> messages = new ArrayList<>();

    public ChannelData(int id) {
	this.id = id;
    }

    public void addMessage(MessageData message){
	messages.add(message);
    }
    public void addMessage(String message){
	messages.add(new MessageData(message));
    }

    public MessageData[] getMessage(int pointer, int amount){
	if(this.messages.isEmpty()){
	    return null;
	}
	if(this.messages.size() < amount){
	    amount = this.messages.size();
	}
	if(this.messages.size() < pointer){
	    return null;
	}
	if(this.messages.size() < amount+pointer){
	    amount = this.messages.size() - pointer;
	}
	MessageData[] messages = new MessageData[amount];
	for(int i = pointer; i < amount+pointer; i++){
	    messages[i-pointer] = this.messages.get(i);
	}
	return messages;
    }
    public MessageData[] getMessage(RequestMessagesData requestMessagesData){
	int pointer = requestMessagesData.getPointer();
	int amount = requestMessagesData.getAmount();
	List<MessageData> reversedList = messages.reversed();
	if(this.messages.isEmpty()){
	    return null;
	}
	if(this.messages.size() < amount){
	     amount = this.messages.size();
	}
	if(this.messages.size() < pointer){
	    return null;
	}
	if(this.messages.size() < amount+pointer){
	    amount = this.messages.size() - pointer;
	}
	MessageData[] messages = new MessageData[amount];
	for(int i = pointer; i < amount+pointer; i++){
	    messages[i-pointer] = reversedList.get(i);
	}
	return messages;
    }
}
