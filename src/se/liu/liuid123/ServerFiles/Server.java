package se.liu.liuid123.ServerFiles;

import se.liu.liuid123.Both.UserInfo;

import javax.imageio.IIOException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server
{
    private ServerSocket serverSocket = null;
    private List<ClientData> connectedClientData = new ArrayList<>();

    private List<Thread> clientThreads = new ArrayList<>();

    private Thread serverThreed = null;

    public Server (int port){
	try{
	    this.serverSocket = new ServerSocket(port);
	}catch (IOException e){
	    e.printStackTrace();
	}
    }

    public void startServer(){
	serverThreed = new Thread(new Connector());
	serverThreed.start();
	System.out.println("Server truned on!");
    }

    public void closeServer(){
	try{
	    serverSocket.close();
	    clientThreads.forEach(Thread::interrupt);
	    serverThreed.interrupt();
	}catch (IOException e){
	    e.printStackTrace();
	}
    }

    private class Connector implements Runnable {
	@Override public void run() {
	    while (true){
		try {
		    Socket client = serverSocket.accept();
		    System.out.println("New client connected");
		    PrintWriter clientOut = new PrintWriter(client.getOutputStream());
		    InputStream inputStream = client.getInputStream();
		    BufferedReader clientIn = new BufferedReader(new InputStreamReader(inputStream));
		    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
		    UserInfo userInfo = (UserInfo) objectInputStream.readObject();
		    ClientData clientData = new ClientData(client, clientOut, clientIn, userInfo);
		    connectedClientData.add(clientData);
		    Thread clientThread = new Thread(new Reciever(clientData));
		    clientThread.start();
		    clientThreads.add(clientThread);
		} catch (IOException | ClassNotFoundException e) {
		    e.printStackTrace();
		}
	    }
	}
    }


    private class Reciever implements Runnable {
	ClientData clientData;
	private Reciever(ClientData clientData){
	    this.clientData = clientData;
	}
	@Override public void run() {
	    try {
		String msg = "";
		while (true){
		    msg = clientData.in.readLine();
		    for (ClientData connectedClient : connectedClientData) {
			connectedClient.out.println(clientData.userInfo.getUserName()+" : "+msg);
			connectedClient.out.flush();
		    }
		}
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }

}


