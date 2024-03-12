package se.liu.lukha243.server_files;

import se.liu.lukha243.both.Packet;
import se.liu.lukha243.logg_files.MyLogger;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The reciver that takes in all new packtes being sent to the server
 */
public class Receiver extends MyLogger implements Runnable
{
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
	    logger.log(Level.WARNING, e.toString(), e);
	    logger.log(Level.INFO, "Due to IOerror we disconect the user");
	    server.disconnectClient(clientData);
	}catch (ClassNotFoundException e){
	    logger.log(Level.SEVERE, e.toString(), e);
	    server.closeServer();
	}
    }

}