package ca.utoronto.civ.its.galapagos.container;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

import ca.utoronto.civ.its.galapagos.ParamException;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;


public class ControllerListener extends Thread
{
    private Container container;
    private ServerSocket server;

    public ControllerListener(Container container)
    {
        this.container = container;

        try
        {
            server = new ServerSocket(7075, 100);
        }
        catch(IOException e)
        {
            System.err.println("controller listener can't open server socket!\n");
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void run()
    {
        Socket connection;
        StringTokenizer tokenizer = null;

        while(true)
        {
            try
            {
                connection = server.accept();

                tokenizer = new StringTokenizer((new BufferedReader(new InputStreamReader(connection.getInputStream()))).readLine());

                String command = tokenizer.nextToken();

                if(command.equals("params"))
                {
                    String params = "<parameters>";

                    while(tokenizer.hasMoreTokens())
                    {
                        params += (tokenizer.nextToken() + " ");
                    }

                    params += "</parameters>";

                    if(container.isReset())
                    {
                        try
                        {
                            container.setParameters(ParamTreeNode.parse(params));
                        }
                        catch(ParamException e)
                        {
                            e.printStackTrace();
                            System.out.println("XML parse error!");
                        }
                    }
                }
                else if(command.equals("start"))
                {
                    container.start();
                }
                else if(command.equals("die"))
                {
                    if(container.isReset())
                    {
                        System.exit(0);
                    }
                }
                else if(command.equals("reset"))
                {
                    container.reset();
                }

                connection.close();
            }
            catch(IOException ioe)
            {
                System.err.println("controller listener can't accept connection!");

                continue;
            }
        }
    }
}
