package se.liu.lukha243.server_files;

import se.liu.lukha243.both.UserInfo;
import se.liu.lukha243.client_files.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data used for the server side. Saves client sockets and userinfo
 */
public class ClientData
{
    /**
     * Socket for the user connection
     */
    public Socket socket;
    /**
     * The output stream for this client
     */
    public ObjectOutputStream objectOutputStream;
    /**
     * The Input stream for this client
     */
    public ObjectInputStream objectInputStream;
    /**
     * All userinf for the client
     */
    public UserInfo userInfo;
    private static final Logger LOGGER = Logger.getLogger(ClientData.class.getName());

    /**
     * Creates client data for the server
     * @param socket socket for the connection to the client
     * @param objectOutputStream Output stream to the client
     * @param objectInputStream Input stream from the client
     * @param userInfo Client data for the user
     */
    public ClientData(final Socket socket, final ObjectOutputStream objectOutputStream, final ObjectInputStream objectInputStream, final UserInfo userInfo)
    {
	this.socket = socket;
	this.objectOutputStream = objectOutputStream;
	this.objectInputStream = objectInputStream;
	this.userInfo = userInfo;
    }

    public void turnOffClient() {
	try{
	    socket.close();
	    objectInputStream.close();
	    objectInputStream.close();
	}catch (IOException e){
	    LOGGER.log(Level.SEVERE, e.toString(), e);
	}
    }
}
