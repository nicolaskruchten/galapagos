package ca.utoronto.civ.its.galapagos.container;

import java.util.Arrays;
import java.util.Vector;

import ca.utoronto.civ.its.galapagos.Chromosome;
import ca.utoronto.civ.its.galapagos.ParamException;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;
import ca.utoronto.civ.its.galapagos.templates.Assembler;
import ca.utoronto.civ.its.galapagos.templates.Generator;
import ca.utoronto.civ.its.galapagos.templates.Hybridizer;
import ca.utoronto.civ.its.galapagos.templates.Initializer;
import ca.utoronto.civ.its.galapagos.templates.Migrator;


public class Population
{
    private Container container;
    private Assembler assembler;
    private Generator generator;
    private Initializer initializer;
    private Migrator migrator;
    private Hybridizer hybridizer;
    private Vector currentGeneration;
    private Chromosome[] currentPopulation;
    private int generationCounter = 0;
    private int generationIndex = 0;
    private int generationSize;
    private int outMigrationCounter = 0;
    private int inMigrationCounter = 0;
    private int hybridizationCounter = 0;
    private int populationSize;
    private int sendAtOnce;
    private int priority;

    public Population(ParamTreeNode params, Container container) throws ParamException
    {
        this.container = container;

        sendAtOnce = params.getChild("sendatonce").getInt();
        priority = params.getChild("priority").getInt();
        generationSize = params.getChild("generationsize").getInt();
        populationSize = params.getChild("populationsize").getInt();

        currentGeneration = new Vector();
        currentPopulation = new Chromosome[populationSize];

        for(int i = 0; i < currentPopulation.length; i++)
        {
            currentPopulation[i] = new Chromosome();
        }

        try
        {
            migrator = (Migrator)Class.forName(params.getChild("migrator").getChild("name").getString()).newInstance();
            assembler = (Assembler)Class.forName(params.getChild("assembler").getChild("name").getString()).newInstance();
            initializer = (Initializer)Class.forName(params.getChild("initializer").getChild("name").getString()).newInstance();
            generator = (Generator)Class.forName(params.getChild("generator").getChild("name").getString()).newInstance();
            hybridizer = (Hybridizer)Class.forName(params.getChild("hybridizer").getChild("name").getString()).newInstance();
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

        migrator.setPopulation(this);
        migrator.setParameters(params.getChild("migrator"));

        assembler.setPopulation(this);
        assembler.setParameters(params.getChild("assembler"));

        initializer.setPopulation(this);
        initializer.setParameters(params.getChild("initializer"));

        generator.setPopulation(this);
        generator.setParameters(params.getChild("generator"));

        hybridizer.setPopulation(this);
        hybridizer.setParameters(params.getChild("hybridizer"));
    }

    public void insertChromosome(Chromosome g)
    {
        synchronized(currentPopulation)
        {
            if(currentPopulation[0].isEvaluated()) //if this is not the initial population
            {
                if(g.getFitness() != Float.NEGATIVE_INFINITY)
                {
                    currentGeneration.add(g);
                }

                generationIndex++;

                if(generationIndex == generationSize)
                {
                    generationIndex = 0;

                    currentPopulation = assembler.newPopulation((Chromosome[])currentGeneration.toArray((new Chromosome[1])));
                    Arrays.sort(currentPopulation);
					currentGeneration.clear();
                    generationCounter++;

                    container.sendPopulationToController(this, "generation");

                    if(migrator.epochIsOver())
                    {
                        outMigrationCounter++;

                        String[] targets = migrator.getTargets();

                        for(int i = 0; i < targets.length; i++)
                        {
                            container.sendMigrantsToPopulation(migrator.getMigrants(targets[i]), targets[i]);
                        }

                        container.notifyControllerOfOutMigration(this);
                    }

                    if(hybridizer.epochIsOver())
                    {
                        hybridizationCounter++;
                        container.evaluateChromosomes(hybridizer.getHybridSeeds(), this, priority, hybridizer.getJobProcessorClassName());
                    }
                }

                if(((((generationSize * generationCounter) + generationIndex) % sendAtOnce) == 0) && (!hybridizer.hybridizationInProgress()))
                {
                    container.evaluateChromosomes(generator.nextChromosomes(), this, priority, container.getEvaluatorClass());
                }
            }
            else //this is still the initial population
            {
                if(g.getFitness() == Float.NEGATIVE_INFINITY)
                {
                    container.evaluateChromosomes(initializer.nextReplacement(), this, priority, container.getEvaluatorClass());
                }
                else
                {
                    currentPopulation[0] = g;
                    Arrays.sort(currentPopulation);

                    if(currentPopulation[0].isEvaluated())
                    {
                        container.evaluateChromosomes(generator.nextChromosomes(), this, priority, container.getEvaluatorClass());
                        container.sendPopulationToController(this, "generation");
                    }
                }
            }
        }
    }

    public void insertMigrants(Chromosome[] g)
    {
        synchronized(currentPopulation)
        {
            currentPopulation = migrator.handleMigrants(g);
            Arrays.sort(currentPopulation);
            inMigrationCounter++;

            container.sendPopulationToController(this, "inmigration");
        }
    }

    public void insertHybrid(Chromosome parent, Chromosome result)
    {
        synchronized(currentPopulation)
        {
            currentPopulation = hybridizer.handleHybrid(parent, result);
            Arrays.sort(currentPopulation);

            if(!hybridizer.hybridizationInProgress())
            {
                container.sendPopulationToController(this, "hybridization");

                if((((generationSize * generationCounter) + generationIndex) % sendAtOnce) == 0)
                {
                    container.evaluateChromosomes(generator.nextChromosomes(), this, priority, container.getEvaluatorClass());
                }
            }
        }
    }

    public Chromosome[] getInitialPopulation()
    {
        return initializer.firstGeneration();
    }

    public int getGenerationCounter()
    {
        return generationCounter;
    }

    public int getGenerationIndex()
    {
        return generationIndex;
    }

    public int getGenerationSize()
    {
        return generationSize;
    }

    public Chromosome getChromosome(int index) throws ArrayIndexOutOfBoundsException
    {
        return currentPopulation[index];
    }

    public Chromosome getBestChromosome()
    {
        return currentPopulation[populationSize - 1];
    }

    public int getOutMigrationCounter()
    {
        return outMigrationCounter;
    }

    public int getNumEvaluations()
    {
        return ((generationSize * generationCounter) + generationIndex);
    }

    public int getPopulationSize()
    {
        return populationSize;
    }

    public int getSendAtOnce()
    {
        return sendAtOnce;
    }

    public Chromosome[] getCurrentPopulation()
    {
        return currentPopulation;
    }

    public int getHybridizationEpochCounter()
    {
        return hybridizationCounter;
    }

    public int getInMigrationCounter()
    {
        return inMigrationCounter;
    }

    public int getPriority()
    {
        return priority;
    }
}
