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
    public ChatComponent(Client client){
	this.client = client;
	textArea = new JTextArea();
	textArea.setEditable(false);
	sendChat = new JTextField("Write your message here");
	sendButton = new JButton("Send message");
	setLayout(new BorderLayout());
	JPanel inputTextPanel = new JPanel();
	add(textArea, BorderLayout.NORTH);
	inputTextPanel.setLayout(new BorderLayout());
	inputTextPanel.add(sendChat, BorderLayout.CENTER);
	inputTextPanel.add(sendButton, BorderLayout.EAST);
	add(inputTextPanel, BorderLayout.SOUTH);
	sendChat.addFocusListener(new FocusListener()
	{
	    @Override public void focusGained(final FocusEvent e) {
		sendChat.setText("");
	    }

	    @Override public void focusLost(final FocusEvent e) {
		if(sendChat.getText().isEmpty())
		    sendChat.setText("Write your message here");
	    }
	});
	sendButton.setDefaultCapable(true);
	sendButton.addActionListener(event ->{
	    client.sendMessage(sendChat.getText());
	    sendChat.setText("");
	});

	repaint();
    }
    @Override
    public Dimension getPreferredSize() {
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	return new Dimension((int)(screenSize.getWidth()/4), ((int)screenSize.getHeight()/2)); //We want the witdh to be a 4 of the screen and the hight to be half
    }

    @Override protected void paintComponent(final Graphics g) {
	textArea.setText(generateMessageString());
    }

    private String generateMessageString(){
	StringBuilder chatString = new StringBuilder();
	chatString.append("Chat \n");
	for(MessageData message : client.getMessages()){
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
