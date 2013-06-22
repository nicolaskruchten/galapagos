package ca.utoronto.civ.its.galapagos.templates;

import ca.utoronto.civ.its.galapagos.Chromosome;


/**
 * Mutator operators define how a chromosome is mutated, both in terms of which genes, how and how much.
 *
 * Mutator operators are meant to take a GeneMutator operator as a parameter, which will govern how to mutate a given gene. This leaves the choice of which gene to mutate up to the Mutator. Possible examples include uniform, single-gene, static, dynamic, adaptive etc.
 *
 */
public abstract class Mutator extends Operator
{
    /**
     * This function mutates a chromosome according to some rule
     *
     * @param g the Chromosome to mutate
     *
     * @return the mutated Chromosome
     */
    public abstract Chromosome mutate(Chromosome g);
}
