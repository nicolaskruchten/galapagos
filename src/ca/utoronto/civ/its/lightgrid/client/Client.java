package ca.utoronto.civ.its.lightgrid.client;

import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.net.InetAddress;
import java.net.UnknownHostException;


public abstract class Client
{
    protected String dispatcherHostname;
    protected String localHostname;

    public Client()
    {
        try
        {
            StreamTokenizer dispatcherFileStream = new StreamTokenizer(new FileReader("dispatcher.txt"));
            dispatcherFileStream.eolIsSignificant(false);
            dispatcherFileStream.nextToken();
            dispatcherHostname = dispatcherFileStream.sval;
        }
        catch(IOException ioe)
        {
            System.err.println("can't access dispatcher file");
        }

        try
        {
            localHostname = InetAddress.getLocalHost().getCanonicalHostName();
        }
        catch(UnknownHostException uhe)
        {
            System.err.println("Cannot find host: " + uhe.getMessage());
            localHostname = null;
        }
    }

    public String getDispatcherHostname()
    {
        return dispatcherHostname;
    }

    public abstract void processResult(String input, Object payload);

    public abstract void setLightGridId(String lightGridId);
}
