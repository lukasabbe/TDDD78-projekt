package se.liu.lukha243.both;

import java.io.Serializable;

/**
 * Sends a request to the server to disconnect the client
 */
public class Request implements Serializable, Packet
{
    private RequestType requestType;
    private int[] args = null;

    /**
     * Creates an request to the server
     * @param requestType the type of the request
     */
    public Request(RequestType requestType){
        this.requestType = requestType;
    }

    /**
     * Creates an request to the server
     * @param requestType the type of the request
     * @param args extra data to send with it
     */
    public Request(RequestType requestType, int[] args){
        this.requestType = requestType;
        this.args = args;
    }
    /**
     * Handler for request to server
     * @param handler
     */
    @Override public void dispatchHandler(final PacketHandler handler) {
        handler.handle(this);
    }

    /**
     * get the request type
     * @return
     */
    public RequestType getRequestType() {
        return requestType;
    }

    /**
     * Get the argument that has been sent to the server
     * @return
     */
    public int[] getArgs() {
        return args;
    }
}
