package ca.utoronto.civ.its.galapagos.templates;

import ca.utoronto.civ.its.galapagos.Chromosome;


/**
 * Initializer operators define the initial seeding of a population.
 *
 * They can be very powerful in directing the evolution of a solution, if problem- or domain-specific knowledge is available.
 *
 * @author $author$
 * @version $Revision$
 */
public abstract class Initializer extends Operator
{
    /**
     * This function generates the initial population.
     *
     * @return the initial population
     */
    public abstract Chromosome[] firstGeneration();

    public abstract Chromosome[] nextReplacement();
}
