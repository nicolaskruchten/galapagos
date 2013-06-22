package ca.utoronto.civ.its.galapagos.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ContainerListener extends Thread
{
    private Controller controller;
    private ServerSocket server;

    public ContainerListener(Controller controller)
    {
        this.controller = controller;

        try
        {
            server = new ServerSocket(7076, 100);
        }
        catch(IOException e)
        {
            System.err.println("container listener can't open server socket!\n");
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
                (new ContainerListenerHandler(controller, connection)).start();
            }
            catch(IOException ioe)
            {
                System.err.println("container listener can't accept connection!");

                continue;
            }
        }
    }
}
