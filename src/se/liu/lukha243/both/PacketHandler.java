package se.liu.lukha243.both;

/**
 * Its used for the packet so we can ditermen what type of packet it is
 */
public interface PacketHandler
{
    public void handle(MessageData packet);
    public void handle(ChannelData packet);
    public void handle(RequestMessagesData packet);
    public void handle(UserInfo packet);
}
