package ca.utoronto.civ.its.galapagos.controller.cli;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Date;

import ca.utoronto.civ.its.galapagos.ParamException;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;
import ca.utoronto.civ.its.galapagos.controller.Controller;
import ca.utoronto.civ.its.galapagos.controller.PopulationSnapshot;
import ca.utoronto.civ.its.galapagos.controller.XMLLogger;


public abstract class CLIWrapper extends Controller
{
    protected Date start;
    protected XMLLogger theLogger;
    private String configFilePath;

    public void setConfigFilePath(String string)
    {
        configFilePath = string;
    }

    public void waitForUserInput()
    {
        (new UserInputListener(this)).start();

        ParamTreeNode paramRoot = null;
        System.out.println(" ");
        System.out.println("Welcome to the Galapagos controller!");

        while(true)
        {
            System.out.println(" ");
            System.out.print("hit 'load' followed by the path to a config file to assign parameters to the containers...\n\n");

            synchronized(this)
            {
                try
                {
                    this.wait();
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

            paramRoot = null;

            try
            {
                paramRoot = ParamTreeNode.parse(new BufferedReader(new FileReader(configFilePath)));

                theLogger = new XMLLogger("logs", "galapagoslog" + System.currentTimeMillis() + ".xml", this);

                theLogger.logLoadEvent(paramRoot);

                if(load(paramRoot))
                {
                    handleLoadEvent(paramRoot);
                    waitForConvergence();

                    synchronized(this)
                    {
                        try
                        {
                            this.wait();
                        }
                        catch(InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
            catch(Exception ioe)
            {
                ioe.printStackTrace();
                handleLoadError();
            }
        }
    }

    public void handlePopulationSynchEvent(String populationId, String eventType)
    {
        PopulationSnapshot theStats = getPopulation(populationId);

        System.out.println("\n" + eventType + " [" + populationId + "]:");
        System.out.println("min: " + theStats.getMinFitness());
        System.out.println("max: " + theStats.getMaxFitness());
        System.out.println("avg: " + theStats.getMeanFitness());
        System.out.println("std: " + theStats.getFitnessStd());

        theLogger.logPopulationUpdateEvent(populationId, eventType);

        this.handlePopulationSynchEvent2(populationId, eventType);
    }

    public void handleOutMigrationEvent(String populationId)
    {
        System.out.println("\noutmigration [" + populationId + "]");
        theLogger.logOutMigrationEvent(populationId);

        this.handleOutMigrationEvent2(populationId);
    }

    public void handleLoadEvent(ParamTreeNode params) throws ParamException
    {
        System.out.println("\nYou may hit 'start' when all container params have been sent...");
        this.handleLoadEvent2(params);
    }

    public void handleStopEvent()
    {
        Date end = new Date();

        theLogger.logEndEvent(end, start, "stop");
        System.out.println(" ");
        System.out.print("hit 'savepop' followed by a path to a directory to save the population, or hit 'reset' to do another run.\n\n");

        this.handleStopEvent2();
    }

    public void handleStartEvent()
    {
        start = new Date();

        theLogger.logStartEvent(start);

        this.handleStartEvent2();
    }

    public void handleConvergenceEvent()
    {
        Date end = new Date();

        theLogger.logEndEvent(end, start, "convergence");
        System.out.println(" ");
        System.out.print("hit 'savepop' followed by a path to a directory to save the population, or hit 'reset' to do another run.\n\n");

        this.handleConvergenceEvent2();
    }

    public void handleParamLoadEvent(String containerId)
    {
        System.out.println("Parameters loaded for container: " + containerId);

        if(this.allParamsLoaded())
        {
            System.out.println("You may hit start when ready...");
        }
    }

    public void handleLoadError()
    {
        System.out.println("\nContainer or controller load error! Please hit 'reset' then fix & reload.");
    }

    public abstract void handleOutMigrationEvent2(String populationId);

    public abstract void handlePopulationSynchEvent2(String populationId, String eventType);

    public abstract void handleLoadEvent2(ParamTreeNode params) throws ParamException;

    public abstract void handleStopEvent2();

    public abstract void handleStartEvent2();

    public abstract void handleConvergenceEvent2();
}
