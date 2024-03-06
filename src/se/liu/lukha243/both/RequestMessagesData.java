package se.liu.lukha243.both;

import java.io.Serializable;

public class RequestMessagesData implements Serializable, Packet
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

    @Override public void dispatch(final PacketHandler handler) {
	handler.handle(this);
    }
}
