package ca.utoronto.civ.its.lightgrid.dispatcher;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ResourceListener extends Thread
{
    private Dispatcher dispatcher;
    private ServerSocket server;

    public ResourceListener(Dispatcher dispatcher)
    {
        try
        {
            server = new ServerSocket(7071, 100);
        }
        catch(IOException e)
        {
            System.err.println("resource listener can't open server socket!\n");
            e.printStackTrace();
            System.exit(0);
        }

        this.dispatcher = dispatcher;
    }

    public void run()
    {
        Socket connection;

        while(true)
        {
            try
            {
                connection = server.accept();
                (new ResourceListenerHandler(dispatcher, connection)).start();
            }
            catch(IOException ioe)
            {
                System.err.println("resource listener can't accept connection!");

                continue;
            }
        }
    }
}
