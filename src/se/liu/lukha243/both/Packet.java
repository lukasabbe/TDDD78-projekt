package se.liu.lukha243.both;

import se.liu.lukha243.client_files.Client;
import se.liu.lukha243.logg_files.MyLogger;
import se.liu.lukha243.server_files.ClientData;
import se.liu.lukha243.server_files.Server;

import java.io.Serializable;

/**
 * Packet is used to extend all other packets. It has things like the packets logger.
 */
public abstract class Packet extends MyLogger implements Serializable
{
    /**
     * The shared logger for all packets
     */
    public abstract void runServer(ClientData clientData, Server server);
    public abstract void runClient(Client client);
}
