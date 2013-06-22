package ca.utoronto.civ.its.galapagos.operators.recombiners;

import ca.utoronto.civ.its.galapagos.Chromosome;
import ca.utoronto.civ.its.galapagos.ParamException;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;
import ca.utoronto.civ.its.galapagos.genomes.RealGenome;
import ca.utoronto.civ.its.galapagos.templates.Operator;
import ca.utoronto.civ.its.galapagos.templates.Recombiner;
import ca.utoronto.civ.its.galapagos.templates.Selector;


public class RealAlphaBlendRecombiner extends Recombiner
{
    private Selector theSelector;
    private Chromosome reserveChild;
    private int currentGenerationNumber = 0;
    private int sendNumber;
    private float alpha;

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

        sendNumber = params.getChild("sendnumber").getInt();
        alpha = params.getChild("alpha").getFloat();
    }

    public Chromosome nextChromosome()
    {
        if(p.getGenerationCounter() != currentGenerationNumber)
        {
            reserveChild = null;
            currentGenerationNumber = p.getGenerationCounter();
        }

        Chromosome child;

        if((sendNumber == 2) && (reserveChild != null))
        {
            child = reserveChild;
            reserveChild = null;
        }
        else
        {
            child = new Chromosome();
            reserveChild = new Chromosome();

            Chromosome[] parents = theSelector.nextChromosomes(p.getCurrentPopulation(), 2);

            for(int j = 0; j < RealGenome.getNumGenes(); j++)
            {
                float gamma = ((1 + (2 * alpha)) * Operator.r.nextFloat()) - alpha;
                ((RealGenome)child.getGenome()).setGene((gamma * ((RealGenome)parents[0].getGenome()).getGene(j)) + ((1 - gamma) * ((RealGenome)parents[1].getGenome()).getGene(j)), j);
                ((RealGenome)reserveChild.getGenome()).setGene((gamma * ((RealGenome)parents[1].getGenome()).getGene(j)) + ((1 - gamma) * ((RealGenome)parents[0].getGenome()).getGene(j)), j);
            }
        }

        return child;
    }
}
