package ca.utoronto.civ.its.galapagos.templates;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import ca.utoronto.civ.its.galapagos.Chromosome;
import ca.utoronto.civ.its.galapagos.ParamException;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;
import ca.utoronto.civ.its.lightgrid.resource.JobProcessor;


public abstract class LocalSearcher extends JobProcessor
{
    private Chromosome theChromosome;
    private Evaluator theEvaluator;
    private float[] outputObjectives;

    public void evaluate()
    {
        Chromosome result = null;
        outputObjectives = null;

        try
        {
            theChromosome = (Chromosome)(new ObjectInputStream(new ByteArrayInputStream((byte[])input))).readObject();

            ParamTreeNode params = ParamTreeNode.parse("<a>" + theChromosome.getEvaluatorParams() + "</a>").getChild("evaluator");
            theEvaluator = (Evaluator)Class.forName(params.getChild("name").getString()).newInstance();
            theEvaluator.setParameters(params);
            theEvaluator.setResource(resource);

            params = ParamTreeNode.parse("<a>" + theChromosome.getLocalSearcherParams() + "</a>");
            this.setParameters(params);
            result = (Chromosome)theChromosome.getClass().newInstance();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        catch(ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch(InstantiationException e)
        {
            e.printStackTrace();
        }
        catch(IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch(ParamException e)
        {
            e.printStackTrace();
        }

        Object temp = optimizeUsingStartPoint(theChromosome.getGenome().getGenes());

        if(temp != null)
        {
            result.getGenome().setGenes(temp);
            result.setEvaluatorParams(theChromosome.getEvaluatorParams());

            if(outputObjectives != null)
            {
                result.setObjectives(outputObjectives);
            }
            else
            {
                float[] objectives = theEvaluator.getObjectiveFunctions(result);

                if(temp != null)
                {
                    result.setObjectives(objectives);
                }
                else
                {
                    acknowledgeReset();
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            try
            {
                (new ObjectOutputStream(baos)).writeObject(result);
            }
            catch(IOException e1)
            {
                e1.printStackTrace();
            }

            System.out.println("\nInitial fitness: " + theChromosome.getFitness());
            System.out.println("Final fitness: " + result.getFitness() + "\n");

            sendResult(baos.toByteArray());
            theEvaluator = null;
        }
        else
        {
            acknowledgeReset();
        }
    }

    protected float[] evaluatePoint(Object thePoint)
    {
        Chromosome temp = null;

        try
        {
            temp = (Chromosome)theChromosome.getClass().newInstance();
        }
        catch(InstantiationException e)
        {
            e.printStackTrace();
        }
        catch(IllegalAccessException e)
        {
            e.printStackTrace();
        }

        temp.getGenome().setGenes(thePoint);
        temp.setEvaluatorParams(theChromosome.getEvaluatorParams());

        float[] objectives = theEvaluator.getObjectiveFunctions(temp);

        if(objectives != null)
        {
            return objectives;
        }
        else
        {
            return null;
        }
    }

    public float objectivesToFitness(float[] objectives)
    {
        return Chromosome.objectivesToFitness(objectives);
    }

    protected abstract Object optimizeUsingStartPoint(Object startPoint);

    public abstract void setParameters(ParamTreeNode params) throws ParamException;

    public Chromosome getTheChromosome()
    {
        return theChromosome;
    }

    public void setOutputObjectives(float[] data)
    {
        outputObjectives = data;
    }
}
