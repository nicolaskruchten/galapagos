package ca.utoronto.civ.its.galapagos;

import java.io.Serializable;
import java.util.Random;

import ca.utoronto.civ.its.galapagos.templates.Fitness;
import ca.utoronto.civ.its.galapagos.templates.Genome;


public class Chromosome implements Serializable, Comparable
{
    private float[] objectives;
    private boolean evaluated;
    private String evaluatorParams = null;
    private String localSearcherParams = null;
    private static int numObjectives;
    private static String chromosomeParams;
    private static Fitness theFitness;
    private Genome genome;
    private static String genomeClass;
    public static Random r = new Random(System.currentTimeMillis());

    public Chromosome()
    {
        objectives = new float[numObjectives];
        evaluated = false;

        try
        {
            genome = (Genome)Class.forName(genomeClass).newInstance();
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
        }

        genome.setTheChromosome(this);
    }

    public float getFitness()
    {
        if(evaluated)
        {
            return theFitness.objectivesToFitness(objectives);
        }
        else
        {
            return Float.NaN;
        }
    }

    public boolean isEvaluated()
    {
        return evaluated;
    }

    public void setEvaluated(boolean temp)
    {
        evaluated = temp;
    }

    public void setObjectives(float[] objectives)
    {
        for(int i = 0; i < numObjectives; i++)
        {
            this.objectives[i] = objectives[i];
        }

        setEvaluated(true);
    }

    public float getObjective(int i)
    {
        return objectives[i];
    }

    public float[] getObjectives()
    {
        return objectives;
    }

    public String getEvaluatorParams()
    {
        return evaluatorParams;
    }

    public String getLocalSearcherParams()
    {
        return localSearcherParams;
    }

    public void setEvaluatorParams(String string)
    {
        evaluatorParams = "<evaluator>" + string + "</evaluator> <chromosome>" + chromosomeParams + "</chromosome>";
    }

    public void setLocalSearcherParams(String string)
    {
        localSearcherParams = string;
    }

    public static int getNumObjectives()
    {
        return numObjectives;
    }

    public static void setParameters(ParamTreeNode params) throws ParamException
    {
        numObjectives = params.getChild("numobjectives").getInt();
        genomeClass = params.getChild("genome").getChild("name").getString();

        Genome theGenome = null;

        try
        {
            theFitness = (Fitness)Class.forName(params.getChild("fitness").getChild("name").getString()).newInstance();
            theGenome = (Genome)Class.forName(params.getChild("genome").getChild("name").getString()).newInstance();
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

        theFitness.setStaticParameters(params.getChild("fitness"));
        theGenome.setStaticParameters(params.getChild("genome"));

        chromosomeParams = params.toXMLString();
    }

    public static float objectivesToFitness(float[] objectives)
    {
        return theFitness.objectivesToFitness(objectives);
    }

    public int compareTo(Object o)
    {
        if(this.equals(o))
        {
            return 0;
        }
        else if(!this.isEvaluated())
        {
            return -1;
        }
        else if(!((Chromosome)o).isEvaluated())
        {
            return 1;
        }
        else if(this.getFitness() > ((Chromosome)o).getFitness())
        {
            return 1;
        }
        else if(this.getFitness() < ((Chromosome)o).getFitness())
        {
            return -1;
        }
        else
        {
            System.err.println("***Chromosome comparison error!");

            return 0; ///SHOULD NEVER HIT THIS
        }
    }

    public boolean equals(Object o)
    {
        return ((this.isEvaluated() == ((Chromosome)o).isEvaluated()) && (this.getFitness() == ((Chromosome)o).getFitness()));
    }

    public String toFileString()
    {
        String output = "Objective Function Values:\n";

        for(int i = 0; i < getNumObjectives(); i++)
        {
            output += (getObjective(i) + "\n");
        }

        return output + "\nGenes:\n" + genome.toFileString();
    }

    public Genome getGenome()
    {
        return genome;
    }
}
