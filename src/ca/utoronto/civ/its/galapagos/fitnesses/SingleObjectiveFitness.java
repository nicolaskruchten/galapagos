package ca.utoronto.civ.its.galapagos.fitnesses;

import ca.utoronto.civ.its.galapagos.ParamException;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;
import ca.utoronto.civ.its.galapagos.templates.Fitness;


public class SingleObjectiveFitness extends Fitness
{
    public float objectivesToFitness(float[] objectives)
    {
        return objectives[0];
    }

    public void setStaticParameters(ParamTreeNode params) throws ParamException
    {
    }
}
