package ca.utoronto.civ.its.galapagos.operators.genemutators;

import ca.utoronto.civ.its.galapagos.ParamTreeNode;
import ca.utoronto.civ.its.galapagos.genomes.RealGenome;
import ca.utoronto.civ.its.galapagos.templates.GeneMutator;
import ca.utoronto.civ.its.galapagos.templates.Operator;


public class BoundaryGeneMutator extends GeneMutator
{
    public void setParameters(ParamTreeNode params)
    {
    }

    public float mutateGene(float gene, int geneNumber)
    {
        if(Operator.r.nextFloat() > 0.5)
        {
            return RealGenome.getMaxValue(geneNumber);
        }
        else
        {
            return RealGenome.getMinValue(geneNumber);
        }
    }
}
