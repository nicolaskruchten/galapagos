package ca.utoronto.civ.its.galapagos.operators.selectors;

import ca.utoronto.civ.its.galapagos.Chromosome;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;
import ca.utoronto.civ.its.galapagos.templates.Operator;
import ca.utoronto.civ.its.galapagos.templates.Selector;


public class RandomSelector extends Selector
{
    public void setParameters(ParamTreeNode params)
    {
    }

    public Chromosome[] nextChromosomes(Chromosome[] population, int numChromosomes)
    {
        Chromosome[] theChromosomes = new Chromosome[numChromosomes];

        for(int i = 0; i < numChromosomes; i++)
        {
            theChromosomes[i] = population[Operator.r.nextInt(population.length)];
        }

        return theChromosomes;
    }
}
