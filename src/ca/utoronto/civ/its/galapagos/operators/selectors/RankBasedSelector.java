package ca.utoronto.civ.its.galapagos.operators.selectors;

import java.util.Arrays;

import ca.utoronto.civ.its.galapagos.Chromosome;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;
import ca.utoronto.civ.its.galapagos.templates.Operator;
import ca.utoronto.civ.its.galapagos.templates.Selector;


public class RankBasedSelector extends Selector
{
    public void setParameters(ParamTreeNode params)
    {
    }

    public Chromosome[] nextChromosomes(Chromosome[] population, int numChromosomes)
    {
        Arrays.sort(population);

        int sum = (population.length * (population.length + 1)) / 2;

        Chromosome[] theChromosomes = new Chromosome[numChromosomes];

        float randomNumber = 0;

        float sum2 = 0;

        for(int i = 0; i < numChromosomes; i++)
        {
            randomNumber = Operator.r.nextInt(sum);
            sum2 = 0;

            for(int j = 0; j < population.length; j++)
            {
                sum2 += (1 + j);

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
