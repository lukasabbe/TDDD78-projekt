package se.liu.lukha243.client_files;

import se.liu.lukha243.both.MessageData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * An JPanel commponment for the the GUI. It is used for displaying a chat GUI
 */
public class ChatComponent extends JPanel implements ChatChangeListener
{
    private Client client;
    private JTextArea textArea;
    private JTextField sendChat;
    private JButton sendButton;
    private int pointer = 0;
    private boolean hasFocus = false;
    public ChatComponent(Client client){
	this.client = client;
	textArea = new JTextArea();
	textArea.setEditable(false);
	sendChat = new JTextField("Write your message here");
	sendButton = new JButton("Send message");
	JButton loadOldMessages = new JButton("^");
	JButton loadNewMessages = new JButton("v");
	setLayout(new BorderLayout());
	JPanel inputTextPanel = new JPanel();
	JPanel textAreaPanel = new JPanel();
	textAreaPanel.setLayout(new BorderLayout());
	textAreaPanel.add(textArea,BorderLayout.CENTER);
	textAreaPanel.add(loadOldMessages, BorderLayout.WEST);
	textAreaPanel.add(loadNewMessages, BorderLayout.EAST);
	//add(textArea, BorderLayout.NORTH);
	inputTextPanel.setLayout(new BorderLayout());
	inputTextPanel.add(sendChat, BorderLayout.CENTER);
	inputTextPanel.add(sendButton, BorderLayout.EAST);
	add(inputTextPanel, BorderLayout.SOUTH);
	add(textAreaPanel);
	sendChat.addFocusListener(new FocusListener()
	{
	    @Override public void focusGained(final FocusEvent e) {
		sendChat.setText("");
		hasFocus=true;
	    }

	    @Override public void focusLost(final FocusEvent e) {
		hasFocus=false;
		if(sendChat.getText().isEmpty())
		    sendChat.setText("Write your message here");
	    }
	});
	sendButton.setDefaultCapable(true);
	sendButton.addActionListener(event ->{
	    if(!hasFocus && sendChat.getText().equals("Write your message here")) return;
	    client.sendMessage(sendChat.getText());
	    pointer = 0;
	    sendChat.setText("");
	});
	loadOldMessages.addActionListener(ignore->{
	    pointer += Client.DEFAULT_AMOUNT_MESSAGES / 2;
	    repaint();
	});
	loadNewMessages.addActionListener(ignore->{
	    pointer -= Client.DEFAULT_AMOUNT_MESSAGES / 2;
	    if(pointer < 0){
		pointer = 0;
	    }
	    repaint();
	});


	repaint();
    }
    @Override
    public Dimension getPreferredSize() {
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	int aQuarter = 4;
	int aHalf = 2;
	return new Dimension((int)(screenSize.getWidth()/aQuarter), ((int)screenSize.getHeight()/aHalf));
    }

    @Override protected void paintComponent(final Graphics g) {
	textArea.setText(generateMessageString());
    }

    private String generateMessageString(){
	StringBuilder chatString = new StringBuilder();
	chatString.append("Chat \n");
	MessageData[] messageData = client.getMessagesFromServer(pointer, Client.DEFAULT_AMOUNT_MESSAGES);
	if(messageData == null)
	    return chatString.toString();
	for(MessageData message : messageData){
	    chatString
		    .append(message.getUserInfo().getUserName())
		    .append(" : ")
		    .append(message.getMessage())
		    .append("\n");
	}
	return chatString.toString();
    }

    @Override
    public void chatChange() {
	repaint();
    }

    public JButton getSendButton(){
	return sendButton;
    }
}
