package ca.utoronto.civ.its.galapagos.templates;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import ca.utoronto.civ.its.galapagos.Chromosome;
import ca.utoronto.civ.its.galapagos.ParamException;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;
import ca.utoronto.civ.its.lightgrid.resource.JobProcessor;


public abstract class Evaluator extends JobProcessor
{
    public void evaluate()
    {
        try
        {
            Chromosome theChromosome = (Chromosome)(new ObjectInputStream(new ByteArrayInputStream((byte[])input))).readObject();
            ParamTreeNode params = ParamTreeNode.parse("<a>" + theChromosome.getEvaluatorParams() + "</a>");
            Chromosome.setParameters(params.getChild("chromosome"));
            this.setParameters(params.getChild("evaluator"));

            float[] result = getObjectiveFunctions(theChromosome);

            if(result != null)
            {
                sendResult(result);
            }
            else
            {
                acknowledgeReset();
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        catch(ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch(ParamException e)
        {
            e.printStackTrace();
        }
    }

    public abstract void setParameters(ParamTreeNode params) throws ParamException;

    public abstract float[] getObjectiveFunctions(Chromosome input);
}
