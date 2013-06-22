package ca.utoronto.civ.its.galapagos.templates;

import ca.utoronto.civ.its.galapagos.Chromosome;


/**
 * Selector operators are key operators in any evolutionary algorithm. Given a certain number of chromosomes, a smaller number is selected according to some rule.
 *
 * Possible operators include the basic rank-based, best, random etc selection schemes. These operators are used in generating new chromosomes, selecting new chromosomes for entrance into the generation, and for selecting migrants.
 *
 */
public abstract class Selector extends Operator
{
    /**
     * This function will select a given number of chromosomes from a given set according to some rule.
     *
     * @param population the array to select from
     * @param number the number of Chromosomes to select and return
     *
     * @return the selected Chromosomes
     */
    public abstract Chromosome[] nextChromosomes(Chromosome[] population, int number);
}
