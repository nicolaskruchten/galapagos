package ca.utoronto.civ.its.galapagos.operators.genemutators;

import ca.utoronto.civ.its.galapagos.ParamException;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;
import ca.utoronto.civ.its.galapagos.templates.GeneMutator;
import ca.utoronto.civ.its.galapagos.templates.Operator;


public class FitnessDifferenceAdaptiveGaussianGeneMutator extends GeneMutator
{
    private float mutationRange;

    public void setParameters(ParamTreeNode params) throws ParamException
    {
        mutationRange = params.getChild("mutationrange").getFloat();
    }

    public float mutateGene(float gene, int geneNumber)
    {
        float scoreRange = p.getBestChromosome().getFitness() - p.getChromosome(0).getFitness();

        gene += (Operator.r.nextGaussian() * scoreRange * mutationRange);

        return gene;
    }
}
