package ca.utoronto.civ.its.galapagos.operators.genemutators;

import ca.utoronto.civ.its.galapagos.ParamException;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;
import ca.utoronto.civ.its.galapagos.templates.GeneMutator;
import ca.utoronto.civ.its.galapagos.templates.Operator;


public class CreepGeneMutator extends GeneMutator
{
    private float mutationRange;

    public void setParameters(ParamTreeNode params) throws ParamException
    {
        mutationRange = params.getChild("mutationrange").getFloat();
    }

    public float mutateGene(float gene, int geneNumber)
    {
        gene += ((1 - (2 * Operator.r.nextFloat())) * mutationRange);

        return gene;
    }
}
