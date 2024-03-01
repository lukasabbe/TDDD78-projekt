package se.liu.liuid123.ClientFiles;

import javax.swing.*;
import java.awt.*;

public class Gui
{
    private JFrame frame = null;
    private Client client = null;

    public Gui() {
	this.frame = new JFrame("Lurres chat");
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	frame.setSize((int)(screenSize.getWidth()/4), ((int)screenSize.getHeight()/2));
    }
    public void open(){
	connectToServer();
	frame.pack();
	frame.setVisible(true);
	ChatComponent chatComponent = new ChatComponent(client);
	client.addChatChangeListner(chatComponent);
	frame.setLayout(new BorderLayout());
	frame.add(chatComponent,BorderLayout.CENTER);
	frame.pack();
	frame.setVisible(true);
    }
    private void connectToServer(){
	String ip = JOptionPane.showInputDialog("Skriv in en IP");
	String userName = JOptionPane.showInputDialog("Skriv in ett användarnamn");

	client = new Client(ip, 5000);
	client.setUserName(userName);
	client.startClient();

    }
}
