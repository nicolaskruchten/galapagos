package ca.utoronto.civ.its.galapagos.controller;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import ca.utoronto.civ.its.galapagos.Chromosome;
import ca.utoronto.civ.its.galapagos.ParamException;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;
import ca.utoronto.civ.its.galapagos.templates.Terminator;


public abstract class Controller
{
    private Terminator theTerminator;
    private String localHostname;
    private Hashtable containerIdToHostname;
    private Hashtable containerIdToParamsLoaded;
    private Hashtable populationIdToContainerId;
    private Hashtable populationIdToPopulation;
    private Hashtable populationIdToNumGenerations;
    private Hashtable populationIdToNumInMigrations;
    private Hashtable populationIdToNumOutMigrations;
    private Hashtable populationIdToNumHybridizations;
    private String[] populationIds;
    private boolean loaded = false;
    private boolean stopped = true;
    private int globalSize = 0;

    public Controller()
    {
        (new ContainerListener(this)).start();

        try
        {
            localHostname = InetAddress.getLocalHost().getCanonicalHostName();
        }
        catch(IOException ioe)
        {
            System.err.println("trouble grabbing hostname");
        }
    }

    public boolean load(ParamTreeNode params)
    {
        try
        {
            Hashtable containerIdToParamString = new Hashtable();
            populationIdToContainerId = new Hashtable();
            populationIdToPopulation = new Hashtable();
            populationIdToNumGenerations = new Hashtable();
            populationIdToNumInMigrations = new Hashtable();
            populationIdToNumOutMigrations = new Hashtable();
            populationIdToNumHybridizations = new Hashtable();
            containerIdToHostname = new Hashtable();
            containerIdToParamsLoaded = new Hashtable();

            Vector populationIdVector = new Vector();
            globalSize = 0;

            localHostname = params.getChild("controllerhostname").getString();

            try
            {
                theTerminator = (Terminator)Class.forName(params.getChild("terminator").getChild("name").getString()).newInstance();
            }
            catch(InstantiationException e)
            {
                e.printStackTrace();
            }
            catch(IllegalAccessException e)
            {
                e.printStackTrace();
            }
            catch(ClassNotFoundException e)
            {
                e.printStackTrace();
                throw new ParamException("Class not found!");
            }

            Chromosome.setParameters(params.getChild("chromosome"));

            theTerminator.setController(this);
            theTerminator.setParameters(params.getChild("terminator"));

            for(int j = 0; j < params.getNumChildren("container"); j++)
            {
                ParamTreeNode theContainerNode = params.getChild("container", j);
                String theContainerId = theContainerNode.getChild("containerid").getString();

                containerIdToHostname.put(theContainerId, theContainerNode.getChild("containerhostname").getString());

                for(int k = 0;
                        k < theContainerNode.getNumChildren("population");
                        k++)
                {
                    ParamTreeNode thePopulationNode = theContainerNode.getChild("population", k);
                    String thePopulationId = thePopulationNode.getChild("populationid").getString();
                    populationIdVector.addElement(thePopulationId);
                    populationIdToNumGenerations.put(thePopulationId, (new Integer(-1)));
                    populationIdToNumInMigrations.put(thePopulationId, (new Integer(0)));
                    populationIdToNumOutMigrations.put(thePopulationId, (new Integer(0)));
                    populationIdToNumHybridizations.put(thePopulationId, (new Integer(0)));

                    int popsize = thePopulationNode.getChild("populationsize").getInt();

                    globalSize += popsize;

                    Chromosome[] thePopulation = new Chromosome[popsize];

                    for(int i = 0; i < popsize; i++)
                    {
                        thePopulation[i] = new Chromosome();
                    }

                    populationIdToPopulation.put(thePopulationId, thePopulation);
                    populationIdToContainerId.put(thePopulationId, theContainerId);
                }

                ParamTreeNode containerParams = new ParamTreeNode("");
                containerParams.addChild("chromosome", params.getChild("chromosome"));
                containerParams.addChild("evaluator", params.getChild("evaluator"));
                containerParams.addChild("container", theContainerNode);

                containerIdToParamString.put(theContainerId, containerParams);
            }

            ParamTreeNode populationIdParams = new ParamTreeNode("");
            populationIds = (String[])(populationIdVector.toArray((new String[0])));

            for(int i = 0; i < populationIds.length; i++)
            {
                ParamTreeNode temp = new ParamTreeNode("");
                temp.addChild("id", new ParamTreeNode(populationIds[i]));
                temp.addChild("hostname", new ParamTreeNode((String)containerIdToHostname.get((String)populationIdToContainerId.get(populationIds[i]))));
                populationIdParams.addChild("pair", temp);
            }

            Enumeration containerIds = containerIdToHostname.keys();

            while(containerIds.hasMoreElements())
            {
                String theId = (String)containerIds.nextElement();
                ParamTreeNode containerParams = (ParamTreeNode)containerIdToParamString.get(theId);
                containerParams.addChild("populationids", populationIdParams);
                containerParams.addChild("controllerhostname", (new ParamTreeNode(localHostname)));

                (new ContainerTalker((String)containerIdToHostname.get(theId), "params", containerParams.toXMLString())).start();
            }

            loaded = true;

            return true;
        }
        catch(ParamException e)
        {
            loaded = false;
            handleLoadError();
            e.printStackTrace();

            return false;
        }
    }

    public void setParamsLoaded(String containerId)
    {
        containerIdToParamsLoaded.put(containerId, "yes");
        this.handleParamLoadEvent(containerId);
    }

    public void setParamsNotLoaded()
    {
        loaded = false;
        handleLoadError();
    }

    public boolean allParamsLoaded()
    {
        return (containerIdToParamsLoaded.size() == containerIdToHostname.size());
    }

    public void waitForConvergence()
    {
        while(loaded) //this will exit upon stop() which can be called here or from another thread (like a UI)
        {
            try
            {
                synchronized(this)
                {
                    this.wait(500);
                }
            }
            catch(InterruptedException e1)
            {
                e1.printStackTrace();
            }

            if(!stopped && theTerminator.hasConverged())
            {
                stop(true);
            }
        }
    }

    public void processOutMigration(String populationId)
    {
        Integer numOutMigs = new Integer(((Integer)populationIdToNumOutMigrations.get(populationId)).intValue() + 1);
        populationIdToNumOutMigrations.put(populationId, numOutMigs);
        handleOutMigrationEvent(populationId);
    }

    public synchronized void processPopulationSynch(String populationId, Chromosome[] chromosomes, String type)
    {
        Arrays.sort(chromosomes);

        synchronized(populationIdToPopulation)
        {
            populationIdToPopulation.remove(populationId);
            populationIdToPopulation.put(populationId, chromosomes);
        }

        if(type.equals("generation"))
        {
            Integer numGens = new Integer(((Integer)populationIdToNumGenerations.get(populationId)).intValue() + 1);
            populationIdToNumGenerations.put(populationId, numGens);
        }
        else if(type.equals("inmigration"))
        {
            Integer numInMigs = new Integer(((Integer)populationIdToNumInMigrations.get(populationId)).intValue() + 1);
            populationIdToNumInMigrations.put(populationId, numInMigs);
        }
        else if(type.equals("hybridization"))
        {
            Integer numHybs = new Integer(((Integer)populationIdToNumHybridizations.get(populationId)).intValue() + 1);
            populationIdToNumHybridizations.put(populationId, numHybs);
        }

        handlePopulationSynchEvent(populationId, type);
    }

    public void start()
    {
        Enumeration containerIds = containerIdToHostname.keys();

        while(containerIds.hasMoreElements())
        {
            String theId = (String)containerIds.nextElement();
            (new ContainerTalker((String)containerIdToHostname.get(theId), "start", "")).start();
        }

        stopped = false;

        handleStartEvent();
    }

    public void stop(boolean converged)
    {
        stopped = true;
        loaded = false;

        if(converged)
        {
            handleConvergenceEvent();
        }
        else
        {
            handleStopEvent();
        }

        resetContainers();
    }

    public void resetContainers()
    {
        Enumeration containerIds = containerIdToHostname.keys();

        while(containerIds.hasMoreElements())
        {
            String theId = (String)containerIds.nextElement();
            (new ContainerTalker((String)containerIdToHostname.get(theId), "reset", "")).start();
        }
    }

    public void killContainers()
    {
        Enumeration containerIds = containerIdToHostname.keys();

        while(containerIds.hasMoreElements())
        {
            String theId = (String)containerIds.nextElement();
            (new ContainerTalker((String)containerIdToHostname.get(theId), "die", "")).start();
        }
    }

    public PopulationSnapshot getGlobalPopulation()
    {
        Chromosome[] globalPop = new Chromosome[globalSize];

        synchronized(populationIdToPopulation)
        {
            Enumeration populationIds = populationIdToPopulation.keys();
            Chromosome[] thePopulation;

            int j = 0;

            populationIds = populationIdToPopulation.keys();

            while(populationIds.hasMoreElements())
            {
                thePopulation = ((Chromosome[])populationIdToPopulation.get(populationIds.nextElement()));

                for(int i = 0; i < thePopulation.length; i++, j++)
                {
                    globalPop[j] = thePopulation[i];
                }
            }
        }

        Arrays.sort(globalPop);

        return (new PopulationSnapshot(globalPop));
    }

    public PopulationSnapshot getPopulation(String popId)
    {
        Chromosome[] thePop = (Chromosome[])populationIdToPopulation.get(popId);
        Arrays.sort(thePop);

        return (new PopulationSnapshot(thePop));
    }

    public boolean isLoaded()
    {
        return loaded;
    }

    public boolean isStopped()
    {
        return stopped;
    }

    public int getNumGenerations(String populationId)
    {
        return ((Integer)populationIdToNumGenerations.get(populationId)).intValue();
    }

    public int getNumInMigrations(String populationId)
    {
        return ((Integer)populationIdToNumInMigrations.get(populationId)).intValue();
    }

    public int getNumOutMigrations(String populationId)
    {
        return ((Integer)populationIdToNumOutMigrations.get(populationId)).intValue();
    }

    public int getNumHybridizations(String populationId)
    {
        return ((Integer)populationIdToNumHybridizations.get(populationId)).intValue();
    }

    public String[] getPopulationIds()
    {
        return populationIds;
    }

    public abstract void handleParamLoadEvent(String containerId);

    public abstract void handleLoadError();

    public abstract void handleOutMigrationEvent(String populationId);

    public abstract void handlePopulationSynchEvent(String populationId, String eventType);

    public abstract void handleStopEvent();

    public abstract void handleStartEvent();

    public abstract void handleConvergenceEvent();
}
