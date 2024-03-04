package se.liu.liuid123.both;

import java.io.Serializable;

public class RequestMessagesData implements Serializable
{
    private int pointer;
    private int amount;

    public RequestMessagesData(final int pointer, final int amount) {
	this.pointer = pointer;
	this.amount = amount;
    }

    public int getPointer() {
	return pointer;
    }

    public void setPointer(final int pointer) {
	this.pointer = pointer;
    }

    public int getAmount() {
	return amount;
    }

    public void setAmount(final int amount) {
	this.amount = amount;
    }
}
