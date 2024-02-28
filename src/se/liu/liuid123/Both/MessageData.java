package se.liu.liuid123.Both;

import java.io.Serializable;

public class MessageData implements Serializable
{
    private String message;
    private int clientId;
    public MessageData(String message){
	this.message = message;
    }
    public String getMessage() {
	return message;
    }

    public void setMessage(final String message) {
	this.message = message;
    }
}
