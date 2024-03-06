package se.liu.lukha243.both;

import java.io.Serializable;

/**
 * Sends a request to the server to disconnect the client
 */
public class DisconnectRequest implements Serializable, Packet
{
    @Override public void dispatchHandler(final PacketHandler handler) {
        handler.handle(this);
    }
}
