package ca.utoronto.civ.its.galapagos.templates;

import java.util.Hashtable;


/**
 * Topology operators define migration target populations and how many migrants should be sent to each.
 *
 * Potential topologies include static, dynamic and adaptive variations.
 *
 */
public abstract class Topology extends Operator
{
    /**
     * This function returns a hash mapping populationIds as Strings to numbers of migrants as ints
     *
     * @return keys are populationId Strings and values are numbers of migrants as ints
     */
    public abstract Hashtable nextTargetHash();
}
