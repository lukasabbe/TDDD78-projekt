package se.liu.lukha243.both.requests;

import se.liu.lukha243.both.Packet;
import se.liu.lukha243.client_files.Client;
import se.liu.lukha243.server_files.ClientData;
import se.liu.lukha243.server_files.Server;


/**
 * Packet for creating a new channel for the user
 */
public class CreateNewChannelPacket extends Packet
{

    @Override public void runServer(final ClientData clientData, final Server server) {
        server.createNewChannel(clientData);
    }

    @Override public void runClient(final UserDataPacket userInfo, final Client client) {

    }
}
