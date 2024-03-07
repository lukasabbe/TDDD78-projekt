package se.liu.lukha243.both;

/**
 * Its an interface thats on every object that is going to be sent over an socket
 * This to lower same type of code in all of them
 */
public interface Packet
{
    public void trigger(PacketHandler handler);
}
