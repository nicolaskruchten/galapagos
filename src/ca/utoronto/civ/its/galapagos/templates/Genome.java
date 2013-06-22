package ca.utoronto.civ.its.galapagos.templates;

import java.io.Serializable;

import ca.utoronto.civ.its.galapagos.Chromosome;
import ca.utoronto.civ.its.galapagos.ParamException;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;


public abstract class Genome implements Serializable
{
    protected Chromosome chromosome;

    public abstract void setStaticParameters(ParamTreeNode params) throws ParamException;

    public abstract Object getGenes();

    public abstract void setGenes(Object genes);

    public abstract String toFileString();

    public void setTheChromosome(Chromosome chromosome)
    {
        this.chromosome = chromosome;
    }
}
