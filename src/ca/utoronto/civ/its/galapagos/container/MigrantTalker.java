package ca.utoronto.civ.its.galapagos.container;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import ca.utoronto.civ.its.galapagos.Chromosome;


public class MigrantTalker extends Thread
{
    private String containerHostname;
    private Chromosome[] payload;
    private String targetId;

    public MigrantTalker(String containerHostname, Chromosome[] payload, String targetId)
    {
        this.payload = payload;
        this.targetId = targetId;
        this.containerHostname = containerHostname;
    }

    public void run()
    {
        boolean successful = false;

        for(int i = 0; i < 10; i++)
        {
            try
            {
                Socket connection = new Socket(containerHostname, 7074);
                ObjectOutputStream oos = new ObjectOutputStream(connection.getOutputStream());

                oos.writeObject("migrants " + targetId + " " + payload.length);

                for(int j = 0; j < payload.length; j++)
                {
                    oos.writeObject(payload[j]);
                }

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
            System.out.println("[migrants " + targetId + "] to " + containerHostname);
        }
        else
        {
            System.out.println("***Message  NOT sent [migrants " + targetId + "] to " + containerHostname);
        }
    }
}
