package ca.utoronto.civ.its.lightgrid.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.StringTokenizer;


public class DispatcherTalker extends Thread
{
    private Client theClient;
    private String command;
    private String[] params;
    private Object[] args;

    public DispatcherTalker(Client theClient, String command, String[] params, Object[] args)
    {
        this.params = params;
        this.args = args;
        this.command = command;
        this.theClient = theClient;
    }

    public void run()
    {
        boolean successful = false;

        for(int i = 0; i < 10; i++)
        {
            try
            {
                Socket connection = new Socket(theClient.getDispatcherHostname(), 7073);
                ObjectOutputStream oos = new ObjectOutputStream(connection.getOutputStream());

                if(command.equals("jobs"))
                {
                    oos.writeObject("jobs " + params.length);

                    for(int j = 0; j < params.length; j++)
                    {
                        oos.writeObject("job " + params[j]);
                        oos.writeObject(args[j]);
                    }
                }
                else if(command.equals("reset"))
                {
                    oos.writeObject("reset " + params[0]);
                }
                else if(command.equals("hello"))
                {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    oos.writeObject("hello " + params[0]);

                    String input = in.readLine();
                    StringTokenizer tokenizer = new StringTokenizer(input);
                    tokenizer.nextToken();
                    theClient.setLightGridId(tokenizer.nextToken());
                    in.close();
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
            System.out.println("[" + command + "] to " + theClient.getDispatcherHostname());
        }
        else
        {
            System.out.println("***Message NOT sent [" + command + "] to " + theClient.getDispatcherHostname());
        }
    }
}
