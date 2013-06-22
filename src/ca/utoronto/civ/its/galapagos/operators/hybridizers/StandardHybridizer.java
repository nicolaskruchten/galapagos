package ca.utoronto.civ.its.galapagos.operators.hybridizers;

import ca.utoronto.civ.its.galapagos.Chromosome;
import ca.utoronto.civ.its.galapagos.ParamException;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;
import ca.utoronto.civ.its.galapagos.templates.Assembler;
import ca.utoronto.civ.its.galapagos.templates.Epoch;
import ca.utoronto.civ.its.galapagos.templates.Hybridizer;
import ca.utoronto.civ.its.galapagos.templates.Selector;


public class StandardHybridizer extends Hybridizer
{
    private Assembler theAssembler;
    private Epoch theEpoch;
    private Selector theSelector;
    private int numHybrids;
    private ParamTreeNode jpParams;
    private String jpClassName;

    public Chromosome[] getHybridSeeds2()
    {
        return theSelector.nextChromosomes(p.getCurrentPopulation(), numHybrids);
    }

    public String getJobProcessorClassName()
    {
        return jpClassName;
    }

    public boolean epochIsOver()
    {
        return theEpoch.epochIsOver();
    }

    public Chromosome[] handleHybrids(Chromosome[] parents, Chromosome[] results)
    {
        return theAssembler.newPopulation(results);
    }

    public ParamTreeNode getJobProcessorParams()
    {
        return jpParams;
    }

    public void setParameters(ParamTreeNode params) throws ParamException
    {
        jpParams = params.getChild("jobprocessor");
        jpClassName = jpParams.getChild("name").getString();
        numHybrids = params.getChild("numhybrids").getInt();

        try
        {
            theAssembler = (Assembler)Class.forName(params.getChild("assembler").getChild("name").getString()).newInstance();
            theSelector = (Selector)Class.forName(params.getChild("selector").getChild("name").getString()).newInstance();
            theEpoch = (Epoch)Class.forName(params.getChild("epoch").getChild("name").getString()).newInstance();
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

        theAssembler.setPopulation(p);
        theAssembler.setParameters(params.getChild("assembler"));

        theSelector.setPopulation(p);
        theSelector.setParameters(params.getChild("selector"));

        theEpoch.setPopulation(p);
        theEpoch.setParameters(params.getChild("epoch"));
    }
}
