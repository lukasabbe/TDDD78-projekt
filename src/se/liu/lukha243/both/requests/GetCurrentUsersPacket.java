package se.liu.lukha243.both.requests;

import se.liu.lukha243.both.Packet;
import se.liu.lukha243.client_files.Client;
import se.liu.lukha243.server_files.ClientData;
import se.liu.lukha243.server_files.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Gets all users for a sellected channel. It returns them in list form
 */
public class GetCurrentUsersPacket extends Packet
{
    private List<UserDataPacket> userDataPacketList = null;

    public GetCurrentUsersPacket() {
    }

    public GetCurrentUsersPacket(final List<UserDataPacket> userDataPacketList) {
	this.userDataPacketList = userDataPacketList;
    }

    @Override public void runServer(final ClientData clientData, final Server server) {
	try{
	    final List<ClientData> connectedClientData = server.getConnectedClientData();
	    final List<UserDataPacket> users = connectedClientData.stream()
		    .filter(user -> user.userInfo.getCurrentChannel() == clientData.userInfo.getCurrentChannel())
		    .map(clientData1 -> clientData1.userInfo)
		    .toList();
	    final GetCurrentUsersPacket obj = new GetCurrentUsersPacket(users);
	    clientData.objectOutputStream.writeObject(obj);
	}catch (IOException e){
	    LOGGER.log(Level.WARNING, e.toString(), e);
	    LOGGER.log(Level.INFO, "Disconnecting user");
	    server.disconnectClient(clientData);
	}
    }

    @Override public void runClient(final Client client) {
	client.setData(this);
    }
    public List<UserDataPacket> getUserDataPacketList() {
	return userDataPacketList;
    }
}
