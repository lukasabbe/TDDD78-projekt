package se.liu.lukha243.both;

import java.io.Serializable;

/**
 * An request to get more messages at once. Used to load old messages
 */
public class RequestMessagesData implements Serializable, Packet
{
    private int pointer;
    private int amount;

    private MessageData[] returnData = null;

    /**
     * creates an request to the server
     * @param pointer
     * @param amount
     */
    public RequestMessagesData(final int pointer, final int amount) {
	this.pointer = pointer;
	this.amount = amount;
    }

    /**
     * Gets the pointer
     * @return
     */
    public int getPointer() {
	return pointer;
    }

    /**
     * Get the amount of messages
     * @return
     */
    public int getAmount() {
	return amount;
    }

    public MessageData[] getReturnData() {
	return returnData;
    }

    public void setReturnData(final MessageData[] returnData) {
	this.returnData = returnData;
    }

    /**
     * Handler for request to server
     * @param handler
     */
    @Override public void trigger(final PacketHandler handler) {
	handler.handle(this);
    }
}
