package se.liu.lukha243.both;

import se.liu.lukha243.client_files.Client;
import se.liu.lukha243.logg_files.MyLogger;
import se.liu.lukha243.server_files.ClientData;
import se.liu.lukha243.server_files.Server;

import java.io.IOException;
import java.util.logging.Level;

/**
 * The reciver that takes in all new packtes being sent to the server
 */
public class Receiver extends MyLogger implements Runnable
{
    private ClientData clientData = null;
    private ChatSocket reciver;
    public Receiver(ClientData clientData, ChatSocket reciver) {
	this.clientData = clientData;
	this.reciver = reciver;
    }
    public Receiver(ChatSocket reciver){
	this.reciver = reciver;
    }
    @Override public void run() {
	if(reciver.isServer())
	    runServer((Server) reciver);
	else
	    runClient((Client) reciver);
    }

    private void runServer(Server server){
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
	    server.closeChatSocket();
	}
    }
    private void runClient(Client client){
	try {
	    while (true){
		final Packet packet = (Packet) client.objectInputStream.readObject();
		packet.runClient(client);
	    }
	} catch (IOException | ClassNotFoundException e) {
	    if(client.getIsClosed()) return;
	    logger.log(Level.SEVERE, e.toString(), e);
	    logger.log(Level.INFO, "Turning of client");
	    client.closeChatSocket();
	}
    }

}