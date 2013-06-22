package ca.utoronto.civ.its.galapagos.fitnesses;

import ca.utoronto.civ.its.galapagos.Chromosome;
import ca.utoronto.civ.its.galapagos.ParamException;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;
import ca.utoronto.civ.its.galapagos.templates.Fitness;


public class WeightedObjectiveSumFitness extends Fitness
{
    private static float[] weights;

    public void setStaticParameters(ParamTreeNode params) throws ParamException
    {
        weights = new float[Chromosome.getNumObjectives()];

        for(int i = 0; i < Chromosome.getNumObjectives(); i++)
        {
            weights[i] = (params.getChild("weights").getChild("weight", i)).getFloat();
        }
    }

    public float objectivesToFitness(float[] objectives)
    {
        float fitness = 0;

        for(int i = 0; i < Chromosome.getNumObjectives(); i++)
        {
            if(objectives[i] == Float.NaN)
            {
                return Float.NaN;
            }

            if(objectives[i] == Float.NEGATIVE_INFINITY)
            {
                return Float.NEGATIVE_INFINITY;
            }

            fitness += (objectives[i] * weights[i]);
        }

        return fitness;
    }
}
