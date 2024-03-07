package se.liu.lukha243.both.requests;

import se.liu.lukha243.both.Packet;
import se.liu.lukha243.client_files.Client;
import se.liu.lukha243.server_files.ClientData;
import se.liu.lukha243.server_files.Server;

/**
 * Disconnect packet that is sent to the server to disconnect the user safe
 */
public class DisconnectPacket extends Packet
{

    @Override public void runServer(final ClientData clientData, Server server) {
        server.disconnectClient(clientData);
    }

    @Override public void runClient(final UserDataPacket userInfo, final Client client) {

    }
}
