package se.liu.lukha243.both.requests;

import se.liu.lukha243.both.Packet;
import se.liu.lukha243.client_files.Client;
import se.liu.lukha243.server_files.ClientData;
import se.liu.lukha243.server_files.Server;

import javax.swing.*;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Sends an kick request to another user. To kick them out of a channel back du deafult
 */
public class KickRequestPacket extends Packet
{
    private UserDataPacket kicedUser;

    public KickRequestPacket(final UserDataPacket kickedUser) {
	this.kicedUser = kickedUser;
    }

    @Override public void runServer(final ClientData clientData, final Server server) {
	try {
	    ClientData kickedClient = server.getClientData(kicedUser);
	    kickedClient.objectOutputStream.writeObject(new KickRequestPacket(kicedUser));

	}catch (IOException e){
	    logger.log(Level.WARNING, e.toString(), e);
	    logger.log(Level.INFO, "Disconnecting user");
	    server.disconnectClient(clientData);
	}

    }

    @Override public void runClient(final Client client) {
	JOptionPane.showMessageDialog(new JPanel(), "You have been kicked by the owner of the channel");
	new Thread(() -> client.joinChannel(0) ).start();
    }
}
