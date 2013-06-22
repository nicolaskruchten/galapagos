package ca.utoronto.civ.its.galapagos.controller;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;


public class ContainerTalker extends Thread
{
    private String command;
    private String containerHostname;
    private String params;

    public ContainerTalker(String containerHostname, String command, String params)
    {
        this.containerHostname = containerHostname;
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
                connection = new Socket(containerHostname, 7075);
                new PrintStream(connection.getOutputStream()).println(command + " " + params);
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
            System.out.println("[" + command + "] to " + containerHostname);
        }
        else
        {
            System.out.println("***Message NOT sent [" + command + "] to " + containerHostname);
        }
    }
}
