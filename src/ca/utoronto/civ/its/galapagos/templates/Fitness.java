package ca.utoronto.civ.its.galapagos.templates;

import java.io.Serializable;

import ca.utoronto.civ.its.galapagos.ParamException;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;


public abstract class Fitness implements Serializable
{
    public abstract float objectivesToFitness(float[] objectives);

    public abstract void setStaticParameters(ParamTreeNode params) throws ParamException;
}
