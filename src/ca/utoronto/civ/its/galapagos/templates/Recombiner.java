package ca.utoronto.civ.its.galapagos.templates;

import ca.utoronto.civ.its.galapagos.Chromosome;


/**
 * Recombiner operators define the rules whereby parent chromosomes combine to form child chromosomes.
 *
 * Possible such operators include all GA and ES crossovers and recombinations. A recombiner will usually take a selector as a parameter.
 *
 */
public abstract class Recombiner extends Operator
{
    /**
     * This function returns a new Chromosome that is a combination of parents according to some rule.
     *
     * @return the recombined child
     */
    public abstract Chromosome nextChromosome();
}
