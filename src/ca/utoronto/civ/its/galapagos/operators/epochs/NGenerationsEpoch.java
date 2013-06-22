package ca.utoronto.civ.its.galapagos.operators.epochs;

import ca.utoronto.civ.its.galapagos.ParamException;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;
import ca.utoronto.civ.its.galapagos.templates.Epoch;


public class NGenerationsEpoch extends Epoch
{
    private int numGenerations;
    private int lastGeneration = 0;

    public void setParameters(ParamTreeNode params) throws ParamException
    {
        numGenerations = params.getChild("numgenerations").getInt();
    }

    public boolean epochIsOver()
    {
        if((p.getGenerationCounter() != lastGeneration) && ((p.getGenerationCounter() % numGenerations) == 0))
        {
            lastGeneration = p.getGenerationCounter();

            return true;
        }
        else
        {
            return false;
        }
    }
}
