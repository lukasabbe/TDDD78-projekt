package se.liu.lukha243.server_files;

import se.liu.lukha243.both.requests.UserDataPacket;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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
    public UserDataPacket userInfo;
    /**
     * Is connection on or off
     */
    public boolean isConnectionOn;

    /**
     * Creates client data for the server
     * @param socket socket for the connection to the client
     * @param objectOutputStream Output stream to the client
     * @param objectInputStream Input stream from the client
     * @param userInfo Client data for the user
     */
    public ClientData(final Socket socket, final ObjectOutputStream objectOutputStream, final ObjectInputStream objectInputStream, final UserDataPacket userInfo)
    {
	this.socket = socket;
	this.objectOutputStream = objectOutputStream;
	this.objectInputStream = objectInputStream;
	this.userInfo = userInfo;
	this.isConnectionOn = true;
    }
}
