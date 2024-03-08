package se.liu.lukha243.both.requests;

import se.liu.lukha243.both.Packet;
import se.liu.lukha243.client_files.Client;
import se.liu.lukha243.server_files.ClientData;
import se.liu.lukha243.server_files.Server;


/**
 * Packet for creating a new channel for the user
 * If you sends it you will return an User packet.
 */
public class CreateNewChannelPacket extends Packet
{

    @Override public void runServer(final ClientData clientData, final Server server) {
        server.createNewChannel(clientData);
    }

    @Override public void runClient(final Client client) {
    }
}
