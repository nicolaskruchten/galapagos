package ca.utoronto.civ.its.galapagos.container;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class ControllerTalker extends Thread
{
    private Population thePopulation;
    private String command;
    private String controllerHostname;
    private String populationId;

    public ControllerTalker(String controllerHostname, String command, Population thePopulation, String populationId)
    {
        this.thePopulation = thePopulation;
        this.controllerHostname = controllerHostname;
        this.populationId = populationId;
        this.command = command;
    }

    public void run()
    {
        boolean successful = false;

        for(int i = 0; i < 10; i++)
        {
            try
            {
                Socket connection = new Socket(controllerHostname, 7076);

                ObjectOutputStream oos = new ObjectOutputStream(connection.getOutputStream());

                if(command.equals("inmigration") || command.equals("generation") || command.equals("hybridization"))
                {
                    oos.writeObject(command + " " + populationId + " " + thePopulation.getPopulationSize() + " ");

                    for(int j = 0; j < thePopulation.getPopulationSize();
                            j++)
                    {
                        oos.writeObject(thePopulation.getChromosome(j));
                    }
                }
                else if(command.equals("outmigration"))
                {
                    oos.writeObject("outmigration " + populationId);
                }
                else
                {
                    oos.writeObject(command);
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

        if(populationId == null)
        {
            populationId = "";
        }
        else
        {
            populationId = " " + populationId;
        }

        if(successful)
        {
            System.out.println("[" + command + populationId + "] to " + controllerHostname);
        }
        else
        {
            System.out.println("***Message NOT sent [" + command + populationId + "] to " + controllerHostname);
        }
    }
}
