package se.liu.lukha243.both;

public interface PacketHandler
{
    public void handle(MessageData packet);
    public void handle(RequestMessagesData packet);
    public void handle(UserInfo packet);
    public void handle(Request packet);

}
