package ca.utoronto.civ.its.galapagos.operators.selectors;

import java.util.Arrays;

import ca.utoronto.civ.its.galapagos.Chromosome;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;
import ca.utoronto.civ.its.galapagos.templates.Operator;
import ca.utoronto.civ.its.galapagos.templates.Selector;


public class RouletteWheelSelector extends Selector
{
    public void setParameters(ParamTreeNode params)
    {
    }

    public Chromosome[] nextChromosomes(Chromosome[] population, int numChromosomes)
    {
        Arrays.sort(population);

        float sum = 0;

        float minFitness = population[0].getFitness();

        for(int i = 0; i < population.length; i++)
        {
            sum += (population[i].getFitness() - minFitness);
        }

        Chromosome[] theChromosomes = new Chromosome[numChromosomes];

        float randomNumber = 0;

        float sum2 = 0;

        for(int i = 0; i < numChromosomes; i++)
        {
            randomNumber = Operator.r.nextFloat() * sum;
            sum2 = 0;

            for(int j = 0; j < population.length; j++)
            {
                sum2 += (population[j].getFitness() - minFitness);

                if(randomNumber < sum2)
                {
                    theChromosomes[i] = population[j];

                    break;
                }
            }
        }

        return theChromosomes;
    }
}
