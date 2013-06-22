package ca.utoronto.civ.its.galapagos.templates;

import java.util.Vector;

import ca.utoronto.civ.its.galapagos.Chromosome;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;


/**
 * Hybridizer operators define how/when to use traditional optimization to make an EA into a hybrid EA.
 *
 * These operators will usually simply contain a classname, selector, assembler, and epoch.
 *
 */
public abstract class Hybridizer extends Operator
{
    private Vector currentHybridizationResults;
    private Vector currentHybridizationParents;
    private int numHybridResults = 0;
    private int numHybridResultsNeeded = 0;

    public Chromosome[] handleHybrid(Chromosome parent, Chromosome result)
    {
        if(result.getFitness() != Float.NEGATIVE_INFINITY)
        {
            currentHybridizationParents.add(parent);
            currentHybridizationResults.add(result);
        }

        numHybridResults++;

        if(numHybridResults == numHybridResultsNeeded)
        {
            return handleHybrids((Chromosome[])currentHybridizationParents.toArray(new Chromosome[1]), (Chromosome[])currentHybridizationResults.toArray(new Chromosome[1]));
        }
        else
        {
            return p.getCurrentPopulation();
        }
    }

    public boolean hybridizationInProgress()
    {
        return (numHybridResults != numHybridResultsNeeded);
    }

    public Chromosome[] getHybridSeeds()
    {
        Chromosome[] hybridSeeds = getHybridSeeds2();

        for(int i = 0; i < hybridSeeds.length; i++)
        {
            hybridSeeds[i].setLocalSearcherParams(getJobProcessorParams().toXMLString());
        }

        numHybridResults = 0;
        numHybridResultsNeeded = hybridSeeds.length;
        currentHybridizationParents = new Vector();
        currentHybridizationResults = new Vector();

        return hybridSeeds;
    }

    protected abstract Chromosome[] getHybridSeeds2();

    public abstract boolean epochIsOver();

    public abstract Chromosome[] handleHybrids(Chromosome[] parents, Chromosome[] results);

    public abstract String getJobProcessorClassName();

    public abstract ParamTreeNode getJobProcessorParams();
}
