package se.liu.lukha243.both;

import java.io.IOException;

public interface PacketHandler
{
    public void handle(MessageData packet);
    public void handle(ChannelData packet);
    public void handle(RequestMessagesData packet);
    public void handle(UserInfo packet);
}
