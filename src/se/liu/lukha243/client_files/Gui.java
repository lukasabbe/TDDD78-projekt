package se.liu.lukha243.client_files;

import se.liu.lukha243.logg_files.MyLogger;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Its a GUI for the chat. It interacts with the client and uses its functions
 */
public class Gui extends MyLogger implements ChatChangeListener
{
    private JFrame frame = null;
    private Client client = null;
    private ChatComponent chatComponent = null;

    private boolean currentChannelLocked = false;

    private String currentPassword = "";

    /**
     * Open GUI frame
     */
    public void open(){
	frame = new JFrame("Lurres chat");
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	final int aQuarter = 4;
	final int aHalf = 2;
	frame.setSize((int)(screenSize.getWidth() / aQuarter), ((int)screenSize.getHeight() / aHalf));

	connectToServer();
	chatComponent = new ChatComponent(client);
	frame.getRootPane().setDefaultButton(chatComponent.getSendButton());
	client.addChatChangeListener(this);
	frame.setLayout(new BorderLayout());
	frame.add(chatComponent, BorderLayout.CENTER);
	frame.pack();
	frame.setJMenuBar(createTopMenu());
	frame.setVisible(true);
	client.notifyAllMessageListeners();
    }
    private void close(){
	frame.dispose();
	client.closeClient();
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
		logger.log(Level.WARNING, e.toString(), e);
		logger.log(Level.INFO, "Can't connect to IP, Port or ip is wrong\n Trying agian");
		continue;
	    } catch (IOException e) {
		logger.log(Level.SEVERE, e.toString(), e);
		logger.log(Level.INFO, "Turning off program");
		close();
	    }
	    return;
	}
    }

    public void reconnect(){
	client.closeClient();
	client = null;
	frame.dispose();
	open();
    }

    private JMenuBar createTopMenu(){
	JMenuBar menuBar = new JMenuBar();
	JMenu menuFileOptions = new JMenu("Settings");
	JMenuItem menuItemReconnect = new JMenuItem("Reconnect",'R');
	JMenuItem menuItemQuit = new JMenuItem("Exit",'E');
	menuItemReconnect.addActionListener(igonore -> reconnect());
	menuItemQuit.addActionListener(igonre -> close());
	menuFileOptions.add(menuItemReconnect);
	menuFileOptions.add(menuItemQuit);
	JMenu menuFileConnection = new JMenu("View");
	JMenuItem menuItemJoinChannel = new JMenuItem("Join channel");
	JMenuItem menuItemCreateChannel = new JMenuItem("Create channel");
	menuItemCreateChannel.addActionListener(igonore -> client.createChannel());
	menuItemJoinChannel.addActionListener(igonore -> {
	    while (true){
		try{
		    int channelId = Integer.parseInt(JOptionPane.showInputDialog("Skriv in ett nummer"));
		    client.joinChannel(channelId);
		    break;
		}catch (NumberFormatException e){
		    logger.log(Level.WARNING, e.toString(), e);
		}
	    }
	});
	menuFileConnection.add(menuItemJoinChannel);
	menuFileConnection.add(menuItemCreateChannel);
	menuBar.add(menuFileOptions);
	menuBar.add(menuFileConnection);
	if(client.getUserInfo().isOwner(client.getUserInfo().getCurrentChannel())){
	    JMenu channelOwnerMenu = new JMenu("Channel");
	    JMenuItem setPasswordItem = new JMenuItem("Set password");
	    setPasswordItem.addActionListener(l -> {
		currentPassword = JOptionPane.showInputDialog("Skriv in ditt lössen");
		client.setChannelData(client.getUserInfo().getCurrentChannel(),currentPassword, currentChannelLocked);
	    });
	    JMenuItem lockItem = new JMenuItem("lock channel");
	    lockItem.addActionListener(l -> client.setChannelData(client.getUserInfo().getCurrentChannel(),currentPassword, true));
	    JMenuItem unlockItem = new JMenuItem("unlock channel");
	    unlockItem.addActionListener(l -> client.setChannelData(client.getUserInfo().getCurrentChannel(),currentPassword, false));
	    channelOwnerMenu.add(setPasswordItem);
	    channelOwnerMenu.add(lockItem);
	    channelOwnerMenu.add(unlockItem);
	    menuBar.add(channelOwnerMenu);
	}

	return menuBar;
    }

    @Override public void chatChange() {
	chatComponent.requestMessagePackets();
	chatComponent.repaint();
    }

    @Override public void channelChange() {
	frame.setJMenuBar(createTopMenu());
	frame.pack();
	frame.setVisible(true);
    }
}
