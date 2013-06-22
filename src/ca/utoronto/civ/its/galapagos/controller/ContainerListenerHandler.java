package ca.utoronto.civ.its.galapagos.controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

import ca.utoronto.civ.its.galapagos.Chromosome;


public class ContainerListenerHandler extends Thread
{
    private Controller controller;
    private Socket connection;

    public ContainerListenerHandler(Controller controller, Socket connection)
    {
        this.connection = connection;
        this.controller = controller;
    }

    public void run()
    {
        StringTokenizer tokenizer = null;

        try
        {
            ObjectInputStream ois = new ObjectInputStream(connection.getInputStream());

            try
            {
                tokenizer = new StringTokenizer((String)ois.readObject());
            }
            catch(ClassNotFoundException e2)
            {
                e2.printStackTrace();
            }

            PrintWriter talkback = new PrintWriter(new OutputStreamWriter(connection.getOutputStream()));

            String command = tokenizer.nextToken();

            if(command.equals("generation") || command.equals("inmigration") || command.equals("hybridization"))
            {
                if(!controller.isStopped())
                {
                    String populationId = tokenizer.nextToken();
                    int numChromosomes = Integer.parseInt(tokenizer.nextToken());
                    Chromosome[] theChromosomes = new Chromosome[numChromosomes];

                    for(int i = 0; i < numChromosomes; i++)
                    {
                        try
                        {
                            theChromosomes[i] = (Chromosome)ois.readObject();
                        }
                        catch(ClassNotFoundException e1)
                        {
                            e1.printStackTrace();
                        }
                    }

                    controller.processPopulationSynch(populationId, theChromosomes, command);
                }
            }
            else if(command.equals("paramsloaded"))
            {
                controller.setParamsLoaded(tokenizer.nextToken());
            }
            else if(command.equals("paramsnotloaded"))
            {
                controller.setParamsNotLoaded();
            }
            else if(command.equals("outmigration"))
            {
                if(!controller.isStopped())
                {
                    controller.processOutMigration(tokenizer.nextToken());
                }
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
    }
}
