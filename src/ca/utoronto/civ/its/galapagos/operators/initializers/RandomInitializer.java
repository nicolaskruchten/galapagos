package ca.utoronto.civ.its.galapagos.operators.initializers;

import ca.utoronto.civ.its.galapagos.Chromosome;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;
import ca.utoronto.civ.its.galapagos.templates.Initializer;


public class RandomInitializer extends Initializer
{
    public void setParameters(ParamTreeNode params)
    {
    }

    public Chromosome[] firstGeneration()
    {
        Chromosome[] firstGen = new Chromosome[p.getPopulationSize()];

        for(int i = 0; i < p.getPopulationSize(); i++)
        {
            firstGen[i] = new Chromosome();
        }

        return firstGen;
    }

    public Chromosome[] nextReplacement()
    {
        Chromosome[] temp = new Chromosome[1];
        temp[0] = (new Chromosome());

        return temp;
    }
}
