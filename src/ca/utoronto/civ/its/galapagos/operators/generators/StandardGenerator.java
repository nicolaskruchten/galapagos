package ca.utoronto.civ.its.galapagos.operators.generators;

import ca.utoronto.civ.its.galapagos.Chromosome;
import ca.utoronto.civ.its.galapagos.ParamException;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;
import ca.utoronto.civ.its.galapagos.templates.Generator;
import ca.utoronto.civ.its.galapagos.templates.Mutator;
import ca.utoronto.civ.its.galapagos.templates.Recombiner;


public class StandardGenerator extends Generator
{
    private Mutator theMutator;
    private Recombiner theRecombiner;

    public void setParameters(ParamTreeNode params) throws ParamException
    {
        try
        {
            theRecombiner = (Recombiner)Class.forName(params.getChild("recombiner").getChild("name").getString()).newInstance();
            theMutator = (Mutator)Class.forName(params.getChild("mutator").getChild("name").getString()).newInstance();
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

        theRecombiner.setPopulation(p);
        theRecombiner.setParameters(params.getChild("recombiner"));

        theMutator.setPopulation(p);
        theMutator.setParameters(params.getChild("mutator"));
    }

    public Chromosome[] nextChromosomes()
    {
        Chromosome[] theGeneration = new Chromosome[p.getSendAtOnce()];

        for(int i = 0; i < p.getSendAtOnce(); i++)
        {
            theGeneration[i] = theMutator.mutate(theRecombiner.nextChromosome());
        }

        return theGeneration;
    }
}
