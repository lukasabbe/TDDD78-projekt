package se.liu.lukha243.both;

import se.liu.lukha243.both.requests.UserDataPacket;
import se.liu.lukha243.client_files.Client;
import se.liu.lukha243.server_files.ClientData;
import se.liu.lukha243.server_files.Server;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Packet is used to extend all other packets. It has things like the packets logger.
 */
public abstract class Packet implements Serializable
{
    /**
     * The shared logger for all packets
     */
    protected static final Logger LOGGER = Logger.getLogger(Packet.class.getName());
    public abstract void runServer(ClientData clientData, Server server);
    public abstract void runClient(UserDataPacket userInfo, Client client);
}
