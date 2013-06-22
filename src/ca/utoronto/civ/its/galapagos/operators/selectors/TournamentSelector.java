package ca.utoronto.civ.its.galapagos.operators.selectors;

import ca.utoronto.civ.its.galapagos.Chromosome;
import ca.utoronto.civ.its.galapagos.ParamException;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;
import ca.utoronto.civ.its.galapagos.templates.Operator;
import ca.utoronto.civ.its.galapagos.templates.Selector;


public class TournamentSelector extends Selector
{
    private int tournamentSize;

    public void setParameters(ParamTreeNode params) throws ParamException
    {
        tournamentSize = params.getChild("tournamentsize").getInt();
    }

    public Chromosome[] nextChromosomes(Chromosome[] population, int numChromosomes)
    {
        Chromosome[] theChromosomes = new Chromosome[numChromosomes];

        int maxIndex = 0;

        for(int i = 0; i < numChromosomes; i++)
        {
            maxIndex = Operator.r.nextInt(population.length);

            for(int j = 0; j < (tournamentSize - 1); j++)
            {
                int trial = Operator.r.nextInt(population.length);

                if(population[trial].getFitness() > population[maxIndex].getFitness())
                {
                    maxIndex = trial;
                }
            }

            theChromosomes[i] = population[maxIndex];
        }

        return theChromosomes;
    }
}
