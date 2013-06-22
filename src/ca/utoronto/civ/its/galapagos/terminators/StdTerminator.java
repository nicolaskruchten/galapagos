package ca.utoronto.civ.its.galapagos.terminators;

import ca.utoronto.civ.its.galapagos.ParamException;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;
import ca.utoronto.civ.its.galapagos.controller.PopulationSnapshot;
import ca.utoronto.civ.its.galapagos.templates.Terminator;


public class StdTerminator extends Terminator
{
    private float minStd;

    public boolean hasConverged()
    {
        PopulationSnapshot globalPop = theController.getGlobalPopulation();

        if(!globalPop.isPopulationFullyEvaluated())
        {
            return false;
        }
        else
        {
            return (globalPop.getFitnessStd() < minStd);
        }
    }

    public void setParameters(ParamTreeNode params) throws ParamException
    {
        minStd = params.getChild("minstd").getFloat();
    }
}
