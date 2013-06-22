package ca.utoronto.civ.its.galapagos.operators.assemblers;

import java.util.Arrays;

import ca.utoronto.civ.its.galapagos.Chromosome;
import ca.utoronto.civ.its.galapagos.ParamException;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;
import ca.utoronto.civ.its.galapagos.templates.Assembler;
import ca.utoronto.civ.its.galapagos.templates.Selector;


public class IntermediatePopulationCrowdingAssembler extends Assembler
{
    private Selector theSelector;
    private Selector thePopSelector;
    private Selector theNewSelector;
    private int eliteNumber;
    private int numFromPop;
    private int numFromNew;

    public void setParameters(ParamTreeNode params) throws ParamException
    {
        eliteNumber = params.getChild("elitenumber").getInt();
        numFromPop = params.getChild("numfrompop").getInt();
        numFromNew = params.getChild("numfromnew").getInt();

        try
        {
            theSelector = (Selector)Class.forName(params.getChild("selector").getChild("name").getString()).newInstance();
            theNewSelector = (Selector)Class.forName(params.getChild("newselector").getChild("name").getString()).newInstance();
            thePopSelector = (Selector)Class.forName(params.getChild("popselector").getChild("name").getString()).newInstance();
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

        theNewSelector.setPopulation(p);
        theNewSelector.setParameters(params.getChild("newselector"));

        thePopSelector.setPopulation(p);
        thePopSelector.setParameters(params.getChild("popselector"));
    }

    public Chromosome[] newPopulation(Chromosome[] newChromosomes)
    {
        Arrays.sort(newChromosomes);

        Chromosome[] population = p.getCurrentPopulation();

        Chromosome[] popMinusElite = new Chromosome[population.length - eliteNumber];

        for(int i = 0; i < (population.length - eliteNumber); i++)
        {
            popMinusElite[i] = population[i];
        }

        Chromosome[] fromPop = thePopSelector.nextChromosomes(popMinusElite, numFromPop);
        Chromosome[] fromNew = theNewSelector.nextChromosomes(newChromosomes, numFromNew);

        Chromosome[] combined = new Chromosome[numFromPop + numFromNew];

        for(int i = 0; i < numFromPop; i++)
        {
            combined[i] = fromPop[i];
        }

        for(int i = 0; i < numFromNew; i++)
        {
            combined[numFromPop + i] = fromNew[i];
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
