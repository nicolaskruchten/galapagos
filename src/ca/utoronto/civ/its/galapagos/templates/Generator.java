package ca.utoronto.civ.its.galapagos.templates;

import ca.utoronto.civ.its.galapagos.Chromosome;


/**
 * Generator operators are essentially container operators for a mutators and recombiners.
 *
 * They define how new chromosomes are generated.
 *
 */
public abstract class Generator extends Operator
{
    /**
     * This function manages all of the mutation/recombination/selection that takes place in generating new chromosomes.
     *
     * @return the next set of genomes to be evaluated
     */
    public abstract Chromosome[] nextChromosomes();
}
