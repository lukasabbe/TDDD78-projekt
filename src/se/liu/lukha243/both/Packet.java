package se.liu.lukha243.both;

public interface Packet
{
    public void dispatch(PacketHandler handler);
}
