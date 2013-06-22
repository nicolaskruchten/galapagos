package ca.utoronto.civ.its.galapagos.operators.migrators;

import java.util.Collections;
import java.util.Hashtable;

import ca.utoronto.civ.its.galapagos.Chromosome;
import ca.utoronto.civ.its.galapagos.ParamException;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;
import ca.utoronto.civ.its.galapagos.templates.Assembler;
import ca.utoronto.civ.its.galapagos.templates.Epoch;
import ca.utoronto.civ.its.galapagos.templates.Migrator;
import ca.utoronto.civ.its.galapagos.templates.Selector;
import ca.utoronto.civ.its.galapagos.templates.Topology;


public class StandardMigrator extends Migrator
{
    private Assembler theAssembler;
    private Epoch theEpoch;
    private Selector theSelector;
    private Topology theTopology;

    public void setParameters(ParamTreeNode params) throws ParamException
    {
        try
        {
            theAssembler = (Assembler)Class.forName(params.getChild("assembler").getChild("name").getString()).newInstance();
            theSelector = (Selector)Class.forName(params.getChild("selector").getChild("name").getString()).newInstance();
            theEpoch = (Epoch)Class.forName(params.getChild("epoch").getChild("name").getString()).newInstance();
            theTopology = (Topology)Class.forName(params.getChild("topology").getChild("name").getString()).newInstance();
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

        theTopology.setPopulation(p);
        theTopology.setParameters(params.getChild("topology"));
    }

    public String[] getTargets()
    {
        Hashtable targetHash = theTopology.nextTargetHash();

        return (String[])Collections.list(targetHash.keys()).toArray(new String[0]);
    }

    public Chromosome[] getMigrants(String target)
    {
        Hashtable targetHash = theTopology.nextTargetHash();

        return theSelector.nextChromosomes(p.getCurrentPopulation(), ((Integer)targetHash.get(target)).intValue());
    }

    public boolean epochIsOver()
    {
        return theEpoch.epochIsOver();
    }

    public Chromosome[] handleMigrants(Chromosome[] g)
    {
        return theAssembler.newPopulation(g);
    }
}
