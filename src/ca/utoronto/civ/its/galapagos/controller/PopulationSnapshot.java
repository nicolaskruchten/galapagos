package ca.utoronto.civ.its.galapagos.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import ca.utoronto.civ.its.galapagos.Chromosome;


public class PopulationSnapshot
{
    private Chromosome[] thePopulation;

    public PopulationSnapshot(Chromosome[] thePopulation)
    {
        this.thePopulation = thePopulation;
    }

    public float getMeanFitness()
    {
        double average = 0.0;

        for(int i = 0; i < thePopulation.length; i++)
        {
            average += thePopulation[i].getFitness();
        }

        average /= thePopulation.length;

        return (float)average;
    }

    public float getFitnessStd()
    {
        double average = 0.0;
        double sampleSigma = 0.0;

        for(int i = 0; i < thePopulation.length; i++)
        {
            average += thePopulation[i].getFitness();
        }

        average /= thePopulation.length;

        for(int i = 0; i < thePopulation.length; i++)
        {
            sampleSigma += Math.pow(thePopulation[i].getFitness() - average, 2);
        }

        sampleSigma /= thePopulation.length;
        sampleSigma = Math.sqrt(sampleSigma);

        return (float)sampleSigma;
    }

    public boolean isPopulationFullyEvaluated()
    {
        return thePopulation[0].isEvaluated();
    }

    public float getMaxFitness()
    {
        return thePopulation[thePopulation.length - 1].getFitness();
    }

    public float getMinFitness()
    {
        return thePopulation[0].getFitness();
    }

    public double[] getFitnesses()
    {
        double[] values = new double[thePopulation.length];

        for(int i = 0; i < thePopulation.length; i++)
        {
            values[i] = thePopulation[i].getFitness();
        }

        return values;
    }

    public Chromosome[] getChromosomes()
    {
        return thePopulation;
    }

    public void dumpToFile(String path)
    {
        File theDir = new File(path);

        if(!theDir.exists())
        {
            theDir.mkdir();
        }

        try
        {
            for(int i = 0; i < thePopulation.length; i++)
            {
                FileWriter snapshot = new FileWriter(path + "/" + i + ".txt", false);
                snapshot.write(thePopulation[i].toFileString());
                snapshot.close();
            }
        }
        catch(IOException x)
        {
            x.printStackTrace();
        }
    }
}
