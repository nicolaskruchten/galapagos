package ca.utoronto.civ.its.galapagos.operators.mutators;

import ca.utoronto.civ.its.galapagos.Chromosome;
import ca.utoronto.civ.its.galapagos.ParamException;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;
import ca.utoronto.civ.its.galapagos.genomes.RealGenome;
import ca.utoronto.civ.its.galapagos.templates.GeneMutator;
import ca.utoronto.civ.its.galapagos.templates.Mutator;
import ca.utoronto.civ.its.galapagos.templates.Operator;


public class SingleGeneUniformMutator extends Mutator
{
    private GeneMutator theGeneMutator;

    public Chromosome mutate(Chromosome c)
    {
        int theGeneNumber = Operator.r.nextInt(RealGenome.getNumGenes());

        for(int i = 0; i < RealGenome.getNumGenes(); i++)
        {
            if(i == theGeneNumber)
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
