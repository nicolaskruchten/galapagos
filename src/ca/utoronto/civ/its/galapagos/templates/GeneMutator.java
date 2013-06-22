package ca.utoronto.civ.its.galapagos.templates;


/**
 * GeneMutator operators define how a given gene is mutated, usually given that the parent Mutator has determined that is is to be mutated.
 *
 * A huge variety of mutation operators exist and can be formulated, static, dynamic, adaptive etc.
 *
 */
public abstract class GeneMutator extends Operator
{
    /**
     * This function mutates a given gene and returns it.
     *
     * @param gene the gene's value
     * @param geneNumber the gene number (according to the Chromosome object)
     *
     * @return the mutated gene
     */
    public abstract float mutateGene(float gene, int geneNumber);
}
