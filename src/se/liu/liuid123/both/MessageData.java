package se.liu.liuid123.both;

import java.io.Serializable;

public class MessageData implements Serializable
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
}
