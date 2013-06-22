package ca.utoronto.civ.its.galapagos.operators.topologies;

import java.util.Hashtable;

import ca.utoronto.civ.its.galapagos.ParamException;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;
import ca.utoronto.civ.its.galapagos.templates.Topology;


public class StaticTopology extends Topology
{
    private Hashtable targetHash;

    public void setParameters(ParamTreeNode params) throws ParamException
    {
        targetHash = new Hashtable();

        String popId;

        for(int i = 0; i < params.getNumChildren("target"); i++)
        {
            ParamTreeNode theTargetNode = params.getChild("target", i);
            popId = theTargetNode.getChild("populationid").getString();
            targetHash.put(popId, new Integer(theTargetNode.getChild("nummigrants").getString()));
        }
    }

    public Hashtable nextTargetHash()
    {
        return targetHash;
    }
}
