package ca.utoronto.civ.its.galapagos.container;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

import ca.utoronto.civ.its.galapagos.Chromosome;


public class MigrantListenerHandler extends Thread
{
    private Container container;
    private Socket connection;

    public MigrantListenerHandler(Container container, Socket connection)
    {
        this.container = container;
        this.connection = connection;
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
            catch(ClassNotFoundException e1)
            {
                e1.printStackTrace();
            }

            String command = tokenizer.nextToken();
            String toId = tokenizer.nextToken();
            int numMigrants = Integer.parseInt(tokenizer.nextToken());
            Chromosome[] theChromosomes = new Chromosome[numMigrants];

            if(command.equals("migrants"))
            {
                for(int i = 0; i < numMigrants; i++)
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

                container.processMigrants(toId, theChromosomes);
            }

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
