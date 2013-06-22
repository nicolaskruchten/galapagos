package ca.utoronto.civ.its.galapagos.genomes;

import ca.utoronto.civ.its.galapagos.Chromosome;
import ca.utoronto.civ.its.galapagos.ParamException;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;
import ca.utoronto.civ.its.galapagos.templates.Genome;


public class RealGenome extends Genome
{
    private static int numGenes;
    private static float[] minGeneValues;
    private static float[] maxGeneValues;
    private float[] genes;

    public RealGenome()
    {
        genes = new float[numGenes];

        for(int i = 0; i < numGenes; i++)
        {
            genes[i] = minGeneValues[i] + (float)(Chromosome.r.nextDouble() * (maxGeneValues[i] - minGeneValues[i]));
        }
    }

    public void setStaticParameters(ParamTreeNode params) throws ParamException
    {
        int max = 0;
        int i;
        float temp = 0;

        numGenes = params.getChild("numgenes").getInt();
        minGeneValues = new float[numGenes];
        maxGeneValues = new float[numGenes];

        i = 0;

        for(int j = 0;
                j < params.getChild("geneminvalues").getNumChildren("range");
                j++)
        {
            ParamTreeNode theRangeNode = params.getChild("geneminvalues").getChild("range", j);
            max = theRangeNode.getChild("to").getInt();
            temp = theRangeNode.getChild("value").getFloat();

            for(; i < max; i++)
            {
                minGeneValues[i] = temp;
            }
        }

        i = 0;

        for(int j = 0;
                j < params.getChild("genemaxvalues").getNumChildren("range");
                j++)
        {
            ParamTreeNode theRangeNode = params.getChild("genemaxvalues").getChild("range", j);
            max = theRangeNode.getChild("to").getInt();
            temp = theRangeNode.getChild("value").getFloat();

            for(; i < max; i++)
            {
                maxGeneValues[i] = temp;
            }
        }
    }

    public float getGene(int i)
    {
        return genes[i];
    }

    public Object getGenes()
    {
        return genes;
    }

    public static int getNumGenes()
    {
        return numGenes;
    }

    public static float getMaxValue(int i)
    {
        return maxGeneValues[i];
    }

    public static float getMinValue(int i)
    {
        return minGeneValues[i];
    }

    public synchronized void setGene(float gene, int index) throws ArrayIndexOutOfBoundsException
    {
        genes[index] = gene;
        chromosome.setEvaluated(false);
    }

    public synchronized void setGenes(Object genes)
    {
        this.genes = (float[])genes;
        chromosome.setEvaluated(false);
    }

    public String toFileString()
    {
        String output = "";

        for(int i = 0; i < numGenes; i++)
        {
            output += (genes[i] + "\n");
        }

        return output;
    }
}
