package ca.utoronto.civ.its.galapagos.container;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import ca.utoronto.civ.its.galapagos.Chromosome;
import ca.utoronto.civ.its.galapagos.ParamException;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;
import ca.utoronto.civ.its.lightgrid.client.Client;
import ca.utoronto.civ.its.lightgrid.client.DispatcherListener;
import ca.utoronto.civ.its.lightgrid.client.DispatcherTalker;


public class Container extends Client
{
    private Hashtable populationIdToHostname;
    private Hashtable populationIdToPopulation;
    private Hashtable jobidToChromosome;
    private Hashtable jobidToPopulation;
    private Hashtable populationToPopulationId;
    private Hashtable populationToLightGridId;
    private Hashtable populationToNextJobid;
    private String containerId;
    private String evaluatorClass;
    private String controllerHostname;
    private ParamTreeNode evaluatorParams;
    private boolean reset = true;

    public void setParameters(ParamTreeNode params)
    {
        jobidToChromosome = new Hashtable();
        jobidToPopulation = new Hashtable();
        populationToLightGridId = new Hashtable();
        populationToPopulationId = new Hashtable();
        populationToNextJobid = new Hashtable();
        populationIdToHostname = new Hashtable();
        populationIdToPopulation = new Hashtable();

        try
        {
            this.containerId = params.getChild("container").getChild("containerid").getString();
            this.controllerHostname = params.getChild("controllerhostname").getString();

            this.dispatcherHostname = params.getChild("container").getChild("dispatcherhostname").getString();
            this.evaluatorClass = params.getChild("evaluator").getChild("name").getString();
            this.evaluatorParams = params.getChild("evaluator");

            Chromosome.setParameters(params.getChild("chromosome"));

            for(int i = 0;
                    i < params.getChild("populationids").getNumChildren("pair");
                    i++)
            {
                ParamTreeNode thePairNode = params.getChild("populationids").getChild("pair", i);
                populationIdToHostname.put(thePairNode.getChild("id").getString(), thePairNode.getChild("hostname").getString());
            }

            for(int i = 0;
                    i < params.getChild("container").getNumChildren("population");
                    i++)
            {
                ParamTreeNode thePopulationNode = params.getChild("container").getChild("population", i);
                Population thePopulation = new Population(thePopulationNode, this);

                populationIdToPopulation.put(thePopulationNode.getChild("populationid").getString(), thePopulation);
                populationToPopulationId.put(thePopulation, thePopulationNode.getChild("populationid").getString());

                String[] talkerParams = new String[1];
                talkerParams[0] = localHostname;

                (new DispatcherTalker(this, "hello", talkerParams, (new Object[0]))).start();

                populationToNextJobid.put(thePopulation, new Integer(0));
            }

            System.out.println("Parameters sucessfully loaded");

            (new ControllerTalker(controllerHostname, "paramsloaded " + containerId, null, null)).start();
        }
        catch(ParamException e)
        {
            e.printStackTrace();
            System.out.println("Parameters NOT sucessfully loaded!");
            System.out.println("Recovered OK...\n");
            (new ControllerTalker(controllerHostname, "paramsnotloaded " + containerId, null, null)).start();
        }
    }

    public synchronized void evaluateChromosomes(Chromosome[] chromosomes, Population population, int priority, String jobClass)
    {
        if(reset == true)
        {
            return;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String[] jobParams = new String[chromosomes.length];
        Object[] jobArgs = new Object[chromosomes.length];

        for(int i = 0; i < jobParams.length; i++)
        {
            Integer jobid = (Integer)populationToNextJobid.remove(population);
            String jobidString = populationToLightGridId.get(population) + "_" + jobid.toString();

            populationToNextJobid.put(population, new Integer(1 + jobid.intValue()));

            jobidToChromosome.put(jobidString, chromosomes[i]);
            jobidToPopulation.put(jobidString, population);

            jobParams[i] = jobidString + " " + populationToLightGridId.get(population) + " " + priority + " " + jobClass;

 			chromosomes[i].setEvaluatorParams(evaluatorParams.toXMLString());
            

            try
            {
                (new ObjectOutputStream(baos)).writeObject(chromosomes[i]);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }

            jobArgs[i] = baos.toByteArray();
            baos.reset();
        }

        (new DispatcherTalker(this, "jobs", jobParams, jobArgs)).start();
    }

    public synchronized void processResult(String input, Object payload)
    {
        if(reset == true)
        {
            return;
        }

        StringTokenizer tokenizer = new StringTokenizer(input);

        tokenizer.nextToken(); // skip "result"

        String id = tokenizer.nextToken();

        Population population = (Population)jobidToPopulation.remove(id);
        Chromosome g = (Chromosome)jobidToChromosome.remove(id);

        if(g != null)
        {
            if(payload.getClass().getName().equals((new byte[0]).getClass().getName()))
            {
                Chromosome theChromosome = null;

                try
                {
                    theChromosome = (Chromosome)(new ObjectInputStream(new ByteArrayInputStream((byte[])payload))).readObject();
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
                catch(ClassNotFoundException e)
                {
                    e.printStackTrace();
                }

                population.insertHybrid(g, theChromosome);
            }
            else
            {
                g.setObjectives((float[])payload);
                population.insertChromosome(g);
            }
        }
        else
        {
            System.err.println("Caught unknown jobID... recovered ok.");
        }
    }

    public synchronized void processMigrants(String toid, Chromosome[] g)
    {
        if(reset == true)
        {
            return;
        }

        ((Population)populationIdToPopulation.get(toid)).insertMigrants(g);
    }

    public void notifyControllerOfOutMigration(Population thePopulation)
    {
        (new ControllerTalker(controllerHostname, "outmigration", thePopulation, (String)populationToPopulationId.get(thePopulation))).start();
    }

    public synchronized void sendMigrantsToPopulation(Chromosome[] chromosomes, String targetId)
    {
        (new MigrantTalker((String)populationIdToHostname.get(targetId), chromosomes, targetId)).start();
    }

    public synchronized void sendPopulationToController(Population thePopulation, String eventType)
    {
        (new ControllerTalker(controllerHostname, eventType, thePopulation, (String)populationToPopulationId.get(thePopulation))).start();
    }

    public void setLightGridId(String lightGridId)
    {
        Enumeration thePopulations = populationToPopulationId.keys();

        while(thePopulations.hasMoreElements())
        {
            Population key = (Population)thePopulations.nextElement();

            if(!populationToLightGridId.containsKey(key))
            {
                populationToLightGridId.put(key, lightGridId);

                break;
            }
        }
    }

    public void start()
    {
        reset = false;
        System.out.println("\nStart command received");

        Enumeration populations = populationToPopulationId.keys();
        int numChromosomes = 0;
        Chromosome[][] initialPopulations = new Chromosome[populationToPopulationId.size()][];
        int[] priorities = new int[populationToPopulationId.size()];
        Population[] thePopulations = new Population[populationToPopulationId.size()];

        int k = 0;

        while(populations.hasMoreElements())
        {
            thePopulations[k] = (Population)populations.nextElement();
            initialPopulations[k] = thePopulations[k].getInitialPopulation();
            numChromosomes += initialPopulations[k].length;
            priorities[k] = thePopulations[k].getPriority();
            k++;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        String[] jobParams = new String[numChromosomes];
        Object[] jobArgs = new Object[numChromosomes];

        k = 0;

        for(int i = 0; i < thePopulations.length; i++)
        {
            for(int j = 0; j < initialPopulations[i].length; j++, k++)
            {
                Integer jobid = (Integer)populationToNextJobid.remove(thePopulations[i]);
                String jobidString = populationToLightGridId.get(thePopulations[i]) + "_" + jobid.toString();

                populationToNextJobid.put(thePopulations[i], new Integer(1 + jobid.intValue()));

                jobidToChromosome.put(jobidString, initialPopulations[i][j]);
                jobidToPopulation.put(jobidString, thePopulations[i]);

                jobParams[k] = jobidString + " " + populationToLightGridId.get(thePopulations[i]) + " " + priorities[i] + " " + evaluatorClass;

            	initialPopulations[i][j].setEvaluatorParams(evaluatorParams.toXMLString());

                try
                {
                    (new ObjectOutputStream(baos)).writeObject(initialPopulations[i][j]);
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }

                jobArgs[k] = baos.toByteArray();
                baos.reset();
            }
        }

        (new DispatcherTalker(this, "jobs", jobParams, jobArgs)).start();
    }

    public void reset()
    {
        reset = true;

        Enumeration populations = populationToLightGridId.keys();

        while(populations.hasMoreElements())
        {
            String[] params = new String[1];
            params[0] = (String)populationToLightGridId.get(populations.nextElement());

            (new DispatcherTalker(this, "reset", params, (new Object[0]))).start();
        }

        System.out.println("\nContainer ready, waiting for parameters");
    }

    public boolean isReset()
    {
        return reset;
    }

    public String getEvaluatorClass()
    {
        return evaluatorClass;
    }

    public static void main(String[] args)
    {
        Container theContainer = new Container();
        (new MigrantListener(theContainer)).start();
        (new ControllerListener(theContainer)).start();
        (new UserInputListener(theContainer)).start();
        (new DispatcherListener(theContainer)).start();

        System.out.println("\nContainer ready, waiting for parameters");
    }
}
