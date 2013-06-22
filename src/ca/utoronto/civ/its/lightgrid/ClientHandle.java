package ca.utoronto.civ.its.lightgrid;

import java.net.InetAddress;
import java.net.UnknownHostException;


public class ClientHandle
{
    private InetAddress address;
    private String id;

    public ClientHandle(String hostname, String id) throws UnknownHostException
    {
        address = InetAddress.getByName(hostname);
        this.id = id;
    }

    public InetAddress getAddress()
    {
        return address;
    }

    public String getId()
    {
        return id;
    }
}
