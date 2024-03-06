package se.liu.lukha243.both;

import java.io.Serializable;

/**
 * An request to get more messages at once. Used to load old messages
 */
public class RequestMessagesData implements Serializable, Packet
{
    private int pointer;
    private int amount;

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

    /**
     * Handler for request to server
     * @param handler
     */
    @Override public void dispatchHandler(final PacketHandler handler) {
	handler.handle(this);
    }
}
