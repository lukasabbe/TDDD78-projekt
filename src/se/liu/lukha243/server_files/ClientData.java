package se.liu.lukha243.server_files;

import se.liu.lukha243.both.UserInfo;

import java.io.BufferedReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientData
{
    public Socket socket;
    public PrintWriter out = null;
    public BufferedReader in = null;
    public ObjectOutputStream objectOutputStream = null;
    public ObjectInputStream objectInputStream = null;
    public UserInfo userInfo;

    public ClientData(final Socket socket, final PrintWriter out, final BufferedReader in, final UserInfo userInfo) {
	this.socket = socket;
	this.out = out;
	this.in = in;
	this.userInfo = userInfo;
    }

    public ClientData(final Socket socket, final ObjectOutputStream objectOutputStream, final ObjectInputStream objectInputStream, final UserInfo userInfo)
    {
	this.socket = socket;
	this.objectOutputStream = objectOutputStream;
	this.objectInputStream = objectInputStream;
	this.userInfo = userInfo;
    }
}
