package ca.utoronto.civ.its.lightgrid.dispatcher;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ClientListener extends Thread
{
    private Dispatcher dispatcher;
    private ServerSocket server;

    public ClientListener(Dispatcher dispatcher)
    {
        try
        {
            server = new ServerSocket(7073, 100);
        }
        catch(IOException e)
        {
            System.err.println("client listener can't open server socket!\n");
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

                (new ClientListenerHandler(dispatcher, connection)).start();
            }
            catch(IOException ioe)
            {
                System.err.println("client listener can't accept connection!");

                continue;
            }
        }
    }
}
