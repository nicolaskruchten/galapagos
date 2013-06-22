package ca.utoronto.civ.its.galapagos.operators.genemutators;

import ca.utoronto.civ.its.galapagos.ParamTreeNode;
import ca.utoronto.civ.its.galapagos.genomes.RealGenome;
import ca.utoronto.civ.its.galapagos.templates.GeneMutator;
import ca.utoronto.civ.its.galapagos.templates.Operator;


public class RandomGeneMutator extends GeneMutator
{
    public void setParameters(ParamTreeNode params)
    {
    }

    public float mutateGene(float gene, int geneNumber)
    {
        return RealGenome.getMinValue(geneNumber) + (Operator.r.nextFloat() * (RealGenome.getMaxValue(geneNumber) - RealGenome.getMinValue(geneNumber)));
    }
}
