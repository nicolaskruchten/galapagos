package ca.utoronto.civ.its.galapagos.templates;


/**
 * Epoch operators define a certain period of time, usually to do with migrations.
 *
 * Possible other uses for epochs include any periodic GA or EA event, such as hybridization local searches etc...
 *
 */
public abstract class Epoch extends Operator
{
    /**
     * This function governs whether or not the epoch has expired.
     *
     * @return whether or not the epoch has expired.
     */
    public abstract boolean epochIsOver();
}
