package se.liu.lukha243.client_files;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Its a GUI for the chat. It interacts with the client and uses its functions
 */
public class Gui
{
    private static final Logger LOGGER = Logger.getLogger(Gui.class.getName());
    private JFrame frame;
    private Client client = null;

    /**
     * Open GUI frame
     */
    public void open(){
	frame = new JFrame("Lurres chat");
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	frame.setSize((int)(screenSize.getWidth() / 4), ((int)screenSize.getHeight() / 2)); //We want the witdh to be a 4 of the screen and the hight to be half

	connectToServer();
	ChatComponent chatComponent = new ChatComponent(client);
	frame.getRootPane().setDefaultButton(chatComponent.getSendButton());
	client.addChatChangeListener(chatComponent);
	frame.setLayout(new BorderLayout());
	frame.add(chatComponent, BorderLayout.CENTER);
	frame.pack();
	frame.setVisible(true);
    }
    private void close(){
	frame.dispose();
	System.exit(0);
    }
    private void connectToServer() {
	while (true) {
	    String ip = JOptionPane.showInputDialog("Skriv in en IP");
	    String userName = JOptionPane.showInputDialog("Skriv in ett användarnamn");
	    try {
		client = new Client(ip, 5000);
		client.setUserName(userName);
		client.startClient();
	    } catch (UnknownHostException e) {
		LOGGER.log(Level.WARNING, e.toString(), e);
		LOGGER.log(Level.INFO, "Can't connect to IP, Port or ip is wrong\n Trying agian");
		continue;
	    } catch (IOException e) {
		LOGGER.log(Level.SEVERE, e.toString(), e);
		LOGGER.log(Level.INFO, "Turning off program");
		close();
	    }
	    return;
	}
    }
}
