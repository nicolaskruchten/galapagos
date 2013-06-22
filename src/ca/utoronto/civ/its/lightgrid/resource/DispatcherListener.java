package ca.utoronto.civ.its.lightgrid.resource;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;


public class DispatcherListener extends Thread
{
    private Resource resource;
    private ServerSocket server;

    public DispatcherListener(Resource resource)
    {
        try
        {
            server = new ServerSocket(7070, 100);
        }
        catch(IOException e)
        {
            System.err.println("dispatcher listener can't open server socket!\n");
            e.printStackTrace();
            System.exit(0);
        }

        this.resource = resource;
    }

    public void run()
    {
        Socket connection;

        while(true)
        {
            try
            {
                connection = server.accept();

                ObjectInputStream ois = new ObjectInputStream(connection.getInputStream());

                StringTokenizer tokenizer = new StringTokenizer((String)ois.readObject());

                String command = tokenizer.nextToken();

                if(command.equals("die"))
                {
                    System.out.println("[" + resource.getLocalHostname() + "] dying now");
                    System.exit(1);
                }
                else if(command.equals("reset"))
                {
                    if(tokenizer.nextToken().equals(resource.getJobId()) && resource.isProcessing())
                    {
                        resource.setReset(true);
                    }
                }
                else if(command.equals("sayhello"))
                {
                    if(!resource.isProcessing())
                    {
                        (new DispatcherTalker(tokenizer.nextToken(), "hello", resource.getLocalHostname(), resource)).start();
                    }
                }
                else if(command.equals("job"))
                {
                    resource.processJob(ois.readObject(), tokenizer.nextToken(), tokenizer.nextToken());
                }

                ois.close();
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
        }
    }
}
