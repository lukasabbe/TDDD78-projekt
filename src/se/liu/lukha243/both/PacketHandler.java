package se.liu.lukha243.both;

/**
 * An handler for the network object sending. It detirmens what what object is called
 */
public interface PacketHandler
{
    public void handle(MessageData packet);
    public void handle(RequestMessagesData packet);
    public void handle(UserInfo packet);
    public void handle(Request packet);

}
