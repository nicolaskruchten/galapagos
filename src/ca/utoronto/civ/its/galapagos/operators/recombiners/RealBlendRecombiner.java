package ca.utoronto.civ.its.galapagos.operators.recombiners;

import ca.utoronto.civ.its.galapagos.Chromosome;
import ca.utoronto.civ.its.galapagos.ParamException;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;
import ca.utoronto.civ.its.galapagos.genomes.RealGenome;
import ca.utoronto.civ.its.galapagos.templates.Operator;
import ca.utoronto.civ.its.galapagos.templates.Recombiner;
import ca.utoronto.civ.its.galapagos.templates.Selector;


public class RealBlendRecombiner extends Recombiner
{
    private Selector theSelector;

    public void setParameters(ParamTreeNode params) throws ParamException
    {
        try
        {
            theSelector = (Selector)Class.forName(params.getChild("selector").getChild("name").getString()).newInstance();
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

        theSelector.setPopulation(p);
        theSelector.setParameters(params.getChild("selector"));
    }

    public Chromosome nextChromosome()
    {
        Chromosome child = new Chromosome();

        Chromosome[] parents = theSelector.nextChromosomes(p.getCurrentPopulation(), 2);

        for(int j = 0; j < RealGenome.getNumGenes(); j++)
        {
            if(((RealGenome)parents[0].getGenome()).getGene(j) > ((RealGenome)parents[1].getGenome()).getGene(j))
            {
                ((RealGenome)child.getGenome()).setGene(((RealGenome)parents[1].getGenome()).getGene(j) + (Operator.r.nextFloat() * (((RealGenome)parents[0].getGenome()).getGene(j) - ((RealGenome)parents[1].getGenome()).getGene(j))), j);
            }
            else
            {
                ((RealGenome)child.getGenome()).setGene(((RealGenome)parents[0].getGenome()).getGene(j) + (Operator.r.nextFloat() * (((RealGenome)parents[1].getGenome()).getGene(j) - ((RealGenome)parents[0].getGenome()).getGene(j))), j);
            }
        }

        return child;
    }
}
