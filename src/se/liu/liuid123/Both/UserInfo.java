package se.liu.liuid123.Both;

import java.io.Serializable;

public class UserInfo implements Serializable
{
    private String userName;
    private int userId;

    public UserInfo(final String userName, final int userId) {
	this.userName = userName;
	this.userId = userId;
    }

    public UserInfo(final String userName) {
	this.userName = userName;
    }

    public void setUserName(final String userName) {
	this.userName = userName;
    }

    public String getUserName() {
	return userName;
    }

    public int getUserId() {
	return userId;
    }

    public void setUserId(final int userId) {
	this.userId = userId;
    }
}
