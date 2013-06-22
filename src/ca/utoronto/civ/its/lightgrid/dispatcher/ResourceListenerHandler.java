package ca.utoronto.civ.its.lightgrid.dispatcher;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;


public class ResourceListenerHandler extends Thread
{
    private Dispatcher dispatcher;
    private Socket connection;

    public ResourceListenerHandler(Dispatcher dispatcher, Socket connection)
    {
        this.dispatcher = dispatcher;
        this.connection = connection;
    }

    public void run()
    {
        StringTokenizer tokenizer = null;
        String jobId = null;
        String resourceId = null;

        try
        {
            PrintWriter talkback = new PrintWriter(new OutputStreamWriter(connection.getOutputStream()));

            ObjectInputStream ois = new ObjectInputStream(connection.getInputStream());
            tokenizer = new StringTokenizer((String)ois.readObject());

            String command = tokenizer.nextToken();

            if(command.equals("result"))
            {
                resourceId = tokenizer.nextToken();
                jobId = tokenizer.nextToken();

                dispatcher.processResult(resourceId, jobId, ois.readObject());
            }
            else if(command.equals("hello"))
            {
                talkback.print("welcome " + dispatcher.addResource(tokenizer.nextToken()));
            }
            else if(command.equals("reset"))
            {
                resourceId = tokenizer.nextToken();
                jobId = tokenizer.nextToken();
                dispatcher.processReset(resourceId, jobId);
            }

            talkback.close();
            connection.close();
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
        catch(ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }
}
