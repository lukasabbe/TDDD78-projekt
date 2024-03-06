package se.liu.lukha243.both;

import java.io.Serializable;

/**
 * Sends a request to the server to disconnect the client
 */
public class Request implements Serializable, Packet
{
    private RequestType requestType;
    public Request(RequestType requestType){
        this.requestType = requestType;
    }
    @Override public void dispatchHandler(final PacketHandler handler) {
        handler.handle(this);
    }

    public RequestType getRequestType() {
        return requestType;
    }
}
