package ca.utoronto.civ.its.galapagos.operators.mutators;

import ca.utoronto.civ.its.galapagos.Chromosome;
import ca.utoronto.civ.its.galapagos.ParamException;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;
import ca.utoronto.civ.its.galapagos.genomes.RealGenome;
import ca.utoronto.civ.its.galapagos.templates.GeneMutator;
import ca.utoronto.civ.its.galapagos.templates.Mutator;
import ca.utoronto.civ.its.galapagos.templates.Operator;


public class StaticMutator extends Mutator
{
    private float[] mutationProbabilities = new float[RealGenome.getNumGenes()];
    private GeneMutator theGeneMutator;

    public Chromosome mutate(Chromosome c)
    {
        for(int i = 0; i < RealGenome.getNumGenes(); i++)
        {
            if(Operator.r.nextFloat() < mutationProbabilities[i])
            {
                ((RealGenome)c.getGenome()).setGene(theGeneMutator.mutateGene(((RealGenome)c.getGenome()).getGene(i), i), i);

                if(((RealGenome)c.getGenome()).getGene(i) > RealGenome.getMaxValue(i))
                {
                    ((RealGenome)c.getGenome()).setGene(RealGenome.getMaxValue(i), i);
                }
                else if(((RealGenome)c.getGenome()).getGene(i) < RealGenome.getMinValue(i))
                {
                    ((RealGenome)c.getGenome()).setGene(RealGenome.getMinValue(i), i);
                }
            }
        }

        return c;
    }

    public void setParameters(ParamTreeNode params) throws ParamException
    {
        int i = 0;
        float prob = 0;
        int max = 0;

        for(int j = 0;
                j < params.getChild("mutationprobabilities").getNumChildren("range");
                j++)
        {
            ParamTreeNode theRangeNode = params.getChild("mutationprobabilities").getChild("range", j);
            max = theRangeNode.getChild("to").getInt();
            prob = theRangeNode.getChild("value").getFloat();

            for(; i < max; i++)
            {
                mutationProbabilities[i] = prob;
            }
        }

        try
        {
            theGeneMutator = (GeneMutator)Class.forName(params.getChild("genemutator").getChild("name").getString()).newInstance();
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

        theGeneMutator.setPopulation(p);
        theGeneMutator.setParameters(params.getChild("genemutator"));
    }
}
