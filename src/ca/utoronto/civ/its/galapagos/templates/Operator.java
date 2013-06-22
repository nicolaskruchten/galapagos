package ca.utoronto.civ.its.galapagos.templates;

import java.util.Random;

import ca.utoronto.civ.its.galapagos.ParamException;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;
import ca.utoronto.civ.its.galapagos.container.Population;


/**
 * Operators are the building blocks of Galapagos GAs and all have these basic properties.
 *
 */
public abstract class Operator
{
    protected static Random r = new Random(System.currentTimeMillis());
    protected Population p;

    /**
     * This function sets the parameter string for the operator.
     *
     * @param params the parameter string
     */
    public abstract void setParameters(ParamTreeNode params) throws ParamException;

    /**
     * Setter function
     *
     * @param p the Population
     */
    public void setPopulation(Population p)
    {
        this.p = p;
    }
}
