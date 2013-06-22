package ca.utoronto.civ.its.galapagos.samples;

import ca.utoronto.civ.its.galapagos.Chromosome;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;
import ca.utoronto.civ.its.galapagos.templates.Evaluator;


public class NoisyParabola extends Evaluator
{
    public void setParameters(ParamTreeNode params)
    {
    }

    public float[] getObjectiveFunctions(Chromosome input)
    {
    	try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        float[] coordinates = (float[])input.getGenome().getGenes();

        float[] result = new float[1];
        result[0] = 1;

        for(int i = 0; i < coordinates.length; i++)
        {
            result[0] *= Math.sin(coordinates[i]);
        }

        result[0] *= (5 * result[0]);

        for(int i = 0; i < coordinates.length; i++)
        {
            result[0] += ((coordinates[i] * coordinates[i]) / 10);
        }

        result[0] = 0 - result[0];

        return result;
    }
}
