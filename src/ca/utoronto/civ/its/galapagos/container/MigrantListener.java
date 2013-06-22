package ca.utoronto.civ.its.galapagos.container;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class MigrantListener extends Thread
{
    private Container container;
    private ServerSocket server;

    public MigrantListener(Container container)
    {
        this.container = container;

        try
        {
            server = new ServerSocket(7074, 100);
        }
        catch(IOException e)
        {
            System.err.println("migrant listener can't open server socket!\n");
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void run()
    {
        Socket connection;

        while(true)
        {
            try
            {
                connection = server.accept();
                (new MigrantListenerHandler(container, connection)).start();
            }
            catch(IOException ioe)
            {
                System.err.println("migrant listener can't accept connection!");

                continue;
            }
        }
    }
}
