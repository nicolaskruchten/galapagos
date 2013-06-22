package ca.utoronto.civ.its.galapagos.container;

import java.io.IOException;
import java.io.StreamTokenizer;


public class UserInputListener extends Thread
{
    private Container container = null;

    public UserInputListener(Container container)
    {
        this.container = container;
    }

    public void run()
    {
        StreamTokenizer userInput = new StreamTokenizer(System.in);

        try
        {
            while(true)
            {
                userInput.nextToken();

                if(userInput.sval.equals("reset"))
                {
                    if(!container.isReset())
                    {
                        container.reset();
                    }
                }
                else if(userInput.sval.equals("quit"))
                {
                    if(container.isReset())
                    {
                        System.exit(0);
                    }
                    else
                    {
                        System.out.println("please reset this process first (type 'reset')");
                    }
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}
