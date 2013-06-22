package ca.utoronto.civ.its.lightgrid.dispatcher;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

import ca.utoronto.civ.its.lightgrid.Job;


public class ClientListenerHandler extends Thread
{
    private Dispatcher dispatcher;
    Socket connection;

    public ClientListenerHandler(Dispatcher dispatcher, Socket connection) throws IOException
    {
        this.connection = connection;
        this.dispatcher = dispatcher;
    }

    public void run()
    {
        StringTokenizer tokenizer = null;

        try
        {
            PrintWriter talkback = new PrintWriter(new OutputStreamWriter(connection.getOutputStream()));
            ObjectInputStream ois = new ObjectInputStream(connection.getInputStream());
            tokenizer = new StringTokenizer((String)ois.readObject());

            String command = tokenizer.nextToken();

            if(command.equals("jobs"))
            {
                Job[] theJobs = new Job[Integer.parseInt(tokenizer.nextToken())];

                for(int i = 0; i < theJobs.length; i++)
                {
                    tokenizer = new StringTokenizer((String)ois.readObject());
                    tokenizer.nextToken(); //skip job

                    String jobid = tokenizer.nextToken();
                    String clientid = tokenizer.nextToken();
                    int priority = Integer.parseInt(tokenizer.nextToken());
                    String jobProcessorClass = tokenizer.nextToken();
                    theJobs[i] = new Job(jobid, ois.readObject(), dispatcher.getClientHandle(clientid), priority, jobProcessorClass);
                }

                dispatcher.addJobs(theJobs);
            }
            else if(command.equals("hello"))
            {
                talkback.print("welcome " + dispatcher.addClient(tokenizer.nextToken()));
            }
            else if(command.equals("reset"))
            {
                dispatcher.resetClientById(tokenizer.nextToken());
            }

            talkback.close();
            ois.close();
            connection.close();
        }
        catch(UnknownHostException e)
        {
            e.printStackTrace();
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
        catch(ClassNotFoundException e1)
        {
            e1.printStackTrace();
        }
    }
}
