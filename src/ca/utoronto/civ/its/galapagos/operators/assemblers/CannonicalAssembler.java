package ca.utoronto.civ.its.galapagos.operators.assemblers;

import java.util.Arrays;

import ca.utoronto.civ.its.galapagos.Chromosome;
import ca.utoronto.civ.its.galapagos.ParamException;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;
import ca.utoronto.civ.its.galapagos.templates.Assembler;


public class CannonicalAssembler extends Assembler
{
    private int eliteNumber;

    public void setParameters(ParamTreeNode params) throws ParamException
    {
        eliteNumber = params.getChild("elitenumber").getInt();
    }

    public Chromosome[] newPopulation(Chromosome[] newChromosomes)
    {
        Arrays.sort(newChromosomes);

        Chromosome[] population = p.getCurrentPopulation();
        Chromosome[] newpopulation = new Chromosome[population.length];

        for(int i = 0; i < (population.length - eliteNumber); i++)
        {
            if(i < newChromosomes.length)
            {
                newpopulation[i] = newChromosomes[newChromosomes.length - i - 1];
            }
            else
            {
                newpopulation[i] = population[i - eliteNumber];
            }
        }

        for(int i = 0; i < eliteNumber; i++)
        {
            Arrays.sort(newpopulation);

            if(population[population.length - 1 - i].getFitness() > newpopulation[0].getFitness())
            {
                newpopulation[0] = population[population.length - 1 - i];
            }
            else
            {
                break;
            }
        }

        return newpopulation;
    }
}
