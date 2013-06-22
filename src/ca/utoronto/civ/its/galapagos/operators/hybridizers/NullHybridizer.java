package ca.utoronto.civ.its.galapagos.operators.hybridizers;

import ca.utoronto.civ.its.galapagos.Chromosome;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;
import ca.utoronto.civ.its.galapagos.templates.Hybridizer;


public class NullHybridizer extends Hybridizer
{
    public Chromosome[] getHybridSeeds2()
    {
        return null;
    }

    public String getJobProcessorClassName()
    {
        return null;
    }

    public boolean epochIsOver()
    {
        return false;
    }

    public boolean hybridizationInProgress()
    {
        return false;
    }

    public Chromosome[] handleHybrids(Chromosome[] parent, Chromosome[] result)
    {
        return p.getCurrentPopulation();
    }

    public void setParameters(ParamTreeNode params)
    {
    }

    public ParamTreeNode getJobProcessorParams()
    {
        return null;
    }
}
