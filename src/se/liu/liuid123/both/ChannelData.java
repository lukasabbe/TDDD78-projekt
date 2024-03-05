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
	int amountCounter = 0;
	for(int i = this.messages.size()-pointer; i >= this.messages.size()-(amount+pointer); i--){
	    messages[amountCounter] = this.messages.get(i);
	    amountCounter++;
	}
	return messages;

    }
    public MessageData[] getMessage(RequestMessagesData requestMessagesData){
	int pointer = requestMessagesData.getPointer();
	int amount = requestMessagesData.getAmount();
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
	int amountCounter = amount-1;
	for(int i = this.messages.size()-pointer-1; i >= this.messages.size()-(amount+pointer); i--){
	    System.out.println(i);
	    messages[amountCounter] = this.messages.get(i);
	    amountCounter--;
	}
	return messages;
    }
}
