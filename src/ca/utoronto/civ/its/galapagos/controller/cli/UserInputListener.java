package ca.utoronto.civ.its.galapagos.controller.cli;

import java.io.File;
import java.io.IOException;
import java.io.StreamTokenizer;


public class UserInputListener extends Thread
{
    private CLIWrapper controller = null;

    public UserInputListener(CLIWrapper controller)
    {
        this.controller = controller;
    }

    public void run()
    {
        StreamTokenizer userInput = new StreamTokenizer(System.in);

        try
        {
            while(true)
            {
                userInput.nextToken();

                if(userInput.sval.equals("load"))
                {
                    userInput.nextToken();
                    controller.setConfigFilePath(userInput.sval);

                    if(!controller.isLoaded())
                    {
                        System.out.println(" ");
                        System.out.flush();

                        synchronized(controller)
                        {
                            //CLIWrapper should currently be in pre-load a wait loop
                            controller.notifyAll();
                        }
                    }
                }
                else if(userInput.sval.equals("start"))
                {
                    if(controller.isStopped() && controller.isLoaded() && controller.allParamsLoaded())
                    {
                        controller.start();
                    }
                }
                else if(userInput.sval.equals("stop"))
                {
                    if(!controller.isStopped())
                    {
                        controller.stop(false);
                    }
                }
                else if(userInput.sval.equals("reset"))
                {
                    if(!controller.isLoaded())
                    {
                        synchronized(controller)
                        {
                            //CLIWrapper should currently be in a post-load wait loop
                            controller.notifyAll();
                        }
                    }
                }
                else if(userInput.sval.equals("quit"))
                {
                    if(!controller.isLoaded())
                    {
                        System.exit(0);
                    }
                    else
                    {
                        System.out.println("you may not quit at this point :)");
                    }
                }
                else if(userInput.sval.equals("killcontainers"))
                {
                    if(!controller.isLoaded())
                    {
                        controller.killContainers();
                    }
                    else
                    {
                        System.out.println("please reset this system first (type 'reset')");
                    }
                }
                else if(userInput.sval.equals("savepop"))
                {
                    userInput.nextToken();

                    if((new File(userInput.sval)).exists() && !(new File(userInput.sval)).isDirectory())
                    {
                        System.out.println(" ");
                        System.out.print("hit 'savepop' followed by a path to a DIRECTORY to save the population, or hit 'reset' to do another run.\n\n");
                    }
                    else
                    {
                        controller.getGlobalPopulation().dumpToFile(userInput.sval);
                        System.out.println(" ");
                        System.out.println("Saved, hit 'reset' to do another run, or 'quit' to exit.");
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
