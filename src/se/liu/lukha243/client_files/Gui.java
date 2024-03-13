package se.liu.lukha243.client_files;
import se.liu.lukha243.logg_files.MyLogger;
import se.liu.lukha243.both.requests.UserDataPacket;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Level;

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

	frame.addWindowListener(new WindowAdapter()
	{
	    @Override public void windowClosing(final WindowEvent e) {
		close();
	    }
	});

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
	client.closeChatSocket();
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
	client.closeChatSocket();
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
	JMenuItem menuItemGetAllChannels = new JMenuItem("Display all channels");
	JPanel allChannelsPane = new JPanel();
	menuItemGetAllChannels.addActionListener(ignore -> JOptionPane.showMessageDialog(allChannelsPane,client.getAllChannels()));

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
	menuFileConnection.add(menuItemGetAllChannels);
	menuBar.add(menuFileOptions);
	menuBar.add(menuFileConnection);
	final int currentChannel = client.getUserInfo().getCurrentChannel();
	if(client.getUserInfo().isOwner(currentChannel)){
	    JMenu channelOwnerMenu = new JMenu("Channel");
	    JMenuItem passwordItem = new JMenuItem("Set password");
	    passwordItem.addActionListener(l -> {
		currentPassword = JOptionPane.showInputDialog("Skriv in ditt lössen");
		client.setChannelData(currentChannel, currentPassword, currentChannelLocked);
	    });
	    JMenuItem lockItem = new JMenuItem("lock channel");
	    lockItem.addActionListener(l -> {
		currentChannelLocked = true;
		client.setChannelData(currentChannel, currentPassword, true);
	    });
	    JMenuItem unlockItem = new JMenuItem("unlock channel");
	    unlockItem.addActionListener(l -> {
		currentChannelLocked = false;
		client.setChannelData(currentChannel, currentPassword, false);
	    });

	    JMenuItem menuItemShowAllUsers = new JMenuItem("all users");

	    menuItemShowAllUsers.addActionListener(ignore ->openUserMenu());

	    channelOwnerMenu.add(passwordItem);
	    channelOwnerMenu.add(lockItem);
	    channelOwnerMenu.add(unlockItem);
	    channelOwnerMenu.add(menuItemShowAllUsers);
	    menuBar.add(channelOwnerMenu);
	}

	return menuBar;
    }

    public void openUserMenu(){
	JPanel userMenuPane = new JPanel();
	final int marginComponent = 20;
	final int marginPanel = 20;
	final int marginBetweenPanel = 5;
	userMenuPane.setLayout(new BorderLayout(marginBetweenPanel,marginBetweenPanel));
	Border paddingComponent = BorderFactory.createEmptyBorder(marginComponent, marginComponent,marginComponent,marginComponent);
	Border paddingPane = BorderFactory.createEmptyBorder(marginPanel, marginPanel,marginPanel,marginPanel);
	userMenuPane.setBorder(paddingPane);
	List<UserDataPacket> users = client.getAllUsers();
	System.out.println(users);
	boolean first = true;
	int counter = 0;
	for(UserDataPacket user : users){
	    JPanel userPanel = new JPanel();
	    userPanel.setLayout(new BorderLayout());
	    JTextField username = new JTextField(user.getUserName());
	    username.setEditable(false);
	    JButton kickButton = new JButton("kick");
	    kickButton.addActionListener(action -> client.kickUser(user));
	    username.setBorder(paddingComponent);
	    kickButton.setBorder(paddingComponent);
	    userPanel.add(username,BorderLayout.WEST);
	    userPanel.add(kickButton, BorderLayout.EAST);
	    if(first){
		first = false;
		userMenuPane.add(userPanel,BorderLayout.PAGE_START);
	    }else if(counter == users.size()-1){
		userMenuPane.add(userPanel,BorderLayout.PAGE_END);
	    }else{
		userMenuPane.add(userPanel,BorderLayout.LINE_START);
	    }
	    counter++;
	}
	Frame popUpFrame = new JFrame();
	popUpFrame.add(userMenuPane);
	popUpFrame.pack();
	popUpFrame.setVisible(true);
    }

    @Override public void chatChange() {
	chatComponent.importMessagePackets();
	chatComponent.repaint();
    }

    @Override public void channelChange() {
	frame.setJMenuBar(createTopMenu());
	frame.pack();
	frame.setVisible(true);
    }

}
