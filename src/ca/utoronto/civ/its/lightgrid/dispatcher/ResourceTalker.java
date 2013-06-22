package ca.utoronto.civ.its.lightgrid.dispatcher;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import ca.utoronto.civ.its.lightgrid.ResourceHandle;


public class ResourceTalker extends Thread
{
    private ResourceHandle resource;
    private String command;
    private Object params;

    public ResourceTalker(ResourceHandle resource, String command, Object params)
    {
        this.resource = resource;
        this.command = command;
        this.params = params;
    }

    public void run()
    {
        Socket connection = null;
        boolean successful = false;

        for(int i = 0; i < 10; i++)
        {
            try
            {
                connection = new Socket(resource.getAddress(), 7070);

                ObjectOutputStream oos = new ObjectOutputStream(connection.getOutputStream());
                oos.writeObject(command);
                oos.writeObject(params);

                oos.close();
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
            System.out.println("[" + command + "] to " + resource.getAddress().getHostName());
        }
        else
        {
            System.out.println("***Message NOT sent [" + command + "] to " + resource.getAddress().getHostName());
        }
    }
}
