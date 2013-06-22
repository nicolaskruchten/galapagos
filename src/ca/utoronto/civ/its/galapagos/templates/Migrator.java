package ca.utoronto.civ.its.galapagos.templates;

import ca.utoronto.civ.its.galapagos.Chromosome;


/**
 * Migrator operators are basically containers for other operators, they govern migration epoch-length, topology, and incoming and outgoing selection.
 *
 * These operators will usually simply contain a selector, assembler, epoch and topology.
 *
 */
public abstract class Migrator extends Operator
{
    /**
     * This function will send the correct number of chromosomes to the right populations according to some rules, usually set by a selector and a topology operator.
     */
    public abstract String[] getTargets();

    /**
     * This function will send the correct number of chromosomes to the right populations according to some rules, usually set by a selector and a topology operator.
     */
    public abstract Chromosome[] getMigrants(String target);

    /**
     * This function defines the duration of a migration epoch and is usually driven by an epoch operator.
     *
     * @return whether or not the epoch has expired.
     */
    public abstract boolean epochIsOver();

    /**
     * This function will handle a set of migrants similarly to a new generation, usually using an assembler.
     *
     * @param g the incoming migrants
     */
    public abstract Chromosome[] handleMigrants(Chromosome[] g);
}
