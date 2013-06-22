package ca.utoronto.civ.its.galapagos.templates;

import ca.utoronto.civ.its.galapagos.ParamException;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;
import ca.utoronto.civ.its.galapagos.controller.Controller;


/**
 * Terminators define when the EA run is over.
 *
 */
public abstract class Terminator
{
    protected Controller theController;

    public void setController(Controller theController)
    {
        this.theController = theController;
    }

    public abstract boolean hasConverged();

    public abstract void setParameters(ParamTreeNode params) throws ParamException;
}
