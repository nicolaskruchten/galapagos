package ca.utoronto.civ.its.galapagos.operators.selectors;

import java.util.Arrays;

import ca.utoronto.civ.its.galapagos.Chromosome;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;
import ca.utoronto.civ.its.galapagos.templates.Selector;


public class BestSelector extends Selector
{
    public void setParameters(ParamTreeNode params)
    {
    }

    public Chromosome[] nextChromosomes(Chromosome[] population, int numChromosomes)
    {
        Arrays.sort(population);

        Chromosome[] theChromosomes = new Chromosome[numChromosomes];

        for(int i = 0; i < numChromosomes; i++)
        {
            theChromosomes[i] = population[population.length - 1 - i];
        }

        return theChromosomes;
    }
}
