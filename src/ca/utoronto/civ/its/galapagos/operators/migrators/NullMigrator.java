package ca.utoronto.civ.its.galapagos.operators.migrators;

import ca.utoronto.civ.its.galapagos.Chromosome;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;
import ca.utoronto.civ.its.galapagos.templates.Migrator;


public class NullMigrator extends Migrator
{
    public void setParameters(ParamTreeNode params)
    {
    }

    public String[] getTargets()
    {
        return null;
    }

    public Chromosome[] getMigrants(String target)
    {
        return null;
    }

    public boolean epochIsOver()
    {
        return false;
    }

    public Chromosome[] handleMigrants(Chromosome[] g)
    {
        return p.getCurrentPopulation();
    }
}
