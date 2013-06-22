package ca.utoronto.civ.its.galapagos.templates;

import ca.utoronto.civ.its.galapagos.Chromosome;


/**
 * Assembler operators govern how a given new set of chromosomes will be integrated into the population, usually by way of a selector.
 *
 * Possible examples include simple and crowding operators.
 *
 */
public abstract class Assembler extends Operator
{
    /**
     * This function directly modifies the parent Population object, replacing chromosomes according to some rule.
     *
     * @param newChromosomes the incoming chromosomes
     */
    public abstract Chromosome[] newPopulation(Chromosome[] newChromosomes);
}
