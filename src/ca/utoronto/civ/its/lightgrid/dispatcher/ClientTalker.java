package ca.utoronto.civ.its.lightgrid.dispatcher;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import ca.utoronto.civ.its.lightgrid.ClientHandle;


public class ClientTalker extends Thread
{
    private ClientHandle client;
    private String command;
    private Object params;

    public ClientTalker(ClientHandle client, String command, Object params)
    {
        this.command = command;
        this.params = params;
        this.client = client;
    }

    public void run()
    {
        Socket connection = null;
        boolean successful = false;

        for(int i = 0; i < 10; i++)
        {
            try
            {
                connection = new Socket(client.getAddress(), 7072);

                ObjectOutputStream oos = new ObjectOutputStream(connection.getOutputStream());
                oos.writeObject(command);
                oos.writeObject(params);
                connection.close();
                successful = true;

                break;
            }
            catch(IOException ioe)
            {
                try
                {
                    synchronized(this)
                    {
                        this.wait(500);
                    }
                }
                catch(InterruptedException ie)
                {
                    ;
                }
            }
        }

        if(successful)
        {
            System.out.println("[" + command + "] to " + client.getAddress().getHostName());
        }
        else
        {
            System.out.println("***Message NOT sent [" + command + "] to " + client.getAddress().getHostName());
        }
    }
}
