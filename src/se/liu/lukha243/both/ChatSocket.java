package se.liu.lukha243.both;

import se.liu.lukha243.logg_files.MyLogger;

/**
 * Chat socket is an interface thats on every socket commponment
 */
public abstract class ChatSocket extends MyLogger
{
    /**
     * Main channel of the chat. Its the one you log in to.
     */
    protected static final int MAIN_CHANNEL = 0;
    /**
     * if loggin is on for the chat socket
     */
    protected static final boolean IS_LOGGIN_ON = true;
    /**
     * The deafult amount of messages sent over the socket
     */
    public static final int DEFAULT_AMOUNT_MESSAGES = 20;
    public abstract boolean isServer();

    public abstract void closeChatSocket();
}
