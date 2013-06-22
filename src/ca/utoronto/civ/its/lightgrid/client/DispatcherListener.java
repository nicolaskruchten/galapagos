package ca.utoronto.civ.its.lightgrid.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class DispatcherListener extends Thread
{
    private Client client;
    private ServerSocket server;

    public DispatcherListener(Client client)
    {
        try
        {
            server = new ServerSocket(7072, 100);
        }
        catch(IOException e)
        {
            System.err.println("dispatcher listener can't open server socket!\n");
            e.printStackTrace();
            System.exit(0);
        }

        this.client = client;
    }

    public void run()
    {
        Socket connection;

        while(true)
        {
            String input = null;
            Object payload = null;

            try
            {
                connection = server.accept();

                ObjectInputStream ois = new ObjectInputStream(connection.getInputStream());
                input = (String)ois.readObject();
                payload = ois.readObject();

                connection.close();
            }
            catch(IOException ioe)
            {
                System.err.println("dispatcher listener can't accept connection!");

                continue;
            }
            catch(ClassNotFoundException e)
            {
                e.printStackTrace();
            }

            client.processResult(input, payload);
        }
    }
}
