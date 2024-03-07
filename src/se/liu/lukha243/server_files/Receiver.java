package se.liu.lukha243.server_files;

import se.liu.lukha243.both.Packet;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Receiver implements Runnable
{
    private static final Logger LOGGER = Logger.getLogger(Receiver.class.getName());
    private ClientData clientData;
    private Server server;
    public Receiver(ClientData clientData, Server server) {
	this.clientData = clientData;
	this.server = server;
    }
    @Override public void run() {
	try {
	    while (true){
		Packet userPacket = (Packet) clientData.objectInputStream.readObject();
		userPacket.runServer(clientData, server);
	    }
	} catch (IOException e) {
	    if(!clientData.isConnectionOn) return;
	    LOGGER.log(Level.WARNING, e.toString(), e);
	    LOGGER.log(Level.INFO, "Due to IOerror we disconect the user");
	    server.disconnectClient(clientData);
	}catch (ClassNotFoundException e){
	    LOGGER.log(Level.SEVERE, e.toString(), e);
	    server.closeServer();
	}
    }

}