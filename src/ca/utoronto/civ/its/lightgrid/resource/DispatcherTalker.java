package ca.utoronto.civ.its.lightgrid.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.StringTokenizer;


public class DispatcherTalker extends Thread
{
    private String command;
    private Object args;
    private String dispatcherHostname;
    private Resource resource;

    public DispatcherTalker(String dispatcherHostname, String command, Object args, Resource resource)
    {
        this.command = command;
        this.args = args;
        this.dispatcherHostname = dispatcherHostname;
        this.resource = resource;
    }

    public void run()
    {
        boolean successful = false;
        String message = null;

        for(int i = 0; i < 50; i++)
        {
            try
            {
                //Socket connection = new Socket(dispatcherHostname, 7071);
                Socket connection = new Socket();
                connection.setReuseAddress(true);
                connection.connect(new InetSocketAddress(dispatcherHostname, 7071));

                ObjectOutputStream oos = new ObjectOutputStream(connection.getOutputStream());

                if(command.equals("reset"))
                {
                    oos.writeObject("reset " + resource.getResourceId() + " " + resource.getJobId());
                    resource.setReset(false);
                    message = "reset";
                }
                else if(command.equals("result"))
                {
                    oos.writeObject("result " + resource.getResourceId() + " " + resource.getJobId());
                    oos.writeObject(args);
                    resource.setReset(false);
                    message = "result";
                }
                else if(command.equals("hello"))
                {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    oos.writeObject("hello " + (String)args);
                    oos.flush();

                    String input = in.readLine();
                    StringTokenizer tokenizer = new StringTokenizer(input);
                    tokenizer.nextToken();
                    resource.setResourceId(tokenizer.nextToken());
                    resource.setDispatcherHostname(dispatcherHostname);

                    message = "hello";
                }
                else if(command == null)
                {
                    ;
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

        String temp = "";

        if(!command.equals("hello"))
        {
            temp = resource.getJobId() + " ";
        }

        if(successful)
        {
            System.out.println("[" + temp + message + "] from " + resource.getLocalHostname() + " to " + dispatcherHostname);
        }
        else
        {
            System.out.println("***Message NOT sent [" + temp + message + "] from " + resource.getLocalHostname() + " to " + dispatcherHostname);
            resource.setReset(false);
        }
    }
}
