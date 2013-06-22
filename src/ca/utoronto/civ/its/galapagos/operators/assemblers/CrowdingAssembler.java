package ca.utoronto.civ.its.galapagos.operators.assemblers;

import java.util.Arrays;

import ca.utoronto.civ.its.galapagos.Chromosome;
import ca.utoronto.civ.its.galapagos.ParamException;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;
import ca.utoronto.civ.its.galapagos.templates.Assembler;
import ca.utoronto.civ.its.galapagos.templates.Selector;


public class CrowdingAssembler extends Assembler
{
    private Selector theSelector;
    private int eliteNumber;

    public void setParameters(ParamTreeNode params) throws ParamException
    {
        eliteNumber = params.getChild("elitenumber").getInt();

        try
        {
            theSelector = (Selector)Class.forName(params.getChild("selector").getChild("name").getString()).newInstance();
        }
        catch(InstantiationException e)
        {
            e.printStackTrace();
        }
        catch(IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch(ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        theSelector.setPopulation(p);
        theSelector.setParameters(params.getChild("selector"));
    }

    public Chromosome[] newPopulation(Chromosome[] newChromosomes)
    {
        Arrays.sort(newChromosomes);

        Chromosome[] population = p.getCurrentPopulation();
        Chromosome[] combined = new Chromosome[(population.length + newChromosomes.length) - eliteNumber];

        for(int i = 0; i < (population.length - eliteNumber); i++)
        {
            combined[i] = population[i];
        }

        for(int i = 0; i < newChromosomes.length; i++)
        {
            combined[population.length - eliteNumber + i] = newChromosomes[i];
        }

        Chromosome[] newpopulation = theSelector.nextChromosomes(combined, population.length);

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
