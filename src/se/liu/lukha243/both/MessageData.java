package se.liu.lukha243.both;

import java.io.Serializable;
import java.util.logging.Handler;

public class MessageData implements Serializable, Packet
{
    private String message;
    private UserInfo userInfo;
    public MessageData(String message){
	this.message = message;
    }
    public MessageData(String message, UserInfo userInfo){
	this.message = message;
	this.userInfo = userInfo;
    }
    public String getMessage() {
	return message;
    }

    public void setMessage(final String message) {
	this.message = message;
    }

    public UserInfo getUserInfo() {
	return userInfo;
    }

    public void setUserInfo(final UserInfo userInfo) {
	this.userInfo = userInfo;
    }

    @Override
    public void dispatch(final PacketHandler handler) {
	handler.handle(this);
    }
}
