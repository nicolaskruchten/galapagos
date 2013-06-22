package ca.utoronto.civ.its.lightgrid.dispatcher;

import java.io.IOException;
import java.io.StreamTokenizer;


public class UserInputListener extends Thread
{
    private Dispatcher dispatcher;

    public UserInputListener(Dispatcher dispatcher)
    {
        this.dispatcher = dispatcher;
    }

    public void run()
    {
        StreamTokenizer userInput = new StreamTokenizer(System.in);

        try
        {
            while(true)
            {
                userInput.nextToken();

                if(userInput.sval.equals("resetall"))
                {
                    dispatcher.resetAll();
                }
                else if(userInput.sval.equals("quit"))
                {
                    System.exit(0);
                }
                else if(userInput.sval.equals("killresources"))
                {
                    dispatcher.killAllResources();
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}
