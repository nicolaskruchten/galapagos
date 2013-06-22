package ca.utoronto.civ.its.lightgrid.resource;

import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class Resource
{
    private String jobProcessorClass;
    private JobProcessor jobProcessor;
    private String dispatcherHostname;
    private String localHostname;
    private String resourceId;
    private String jobId;
    private boolean reset;

    public Resource()
    {
        reset = false;

        try
        {
            localHostname = InetAddress.getLocalHost().getCanonicalHostName();
        }
        catch(UnknownHostException uhe)
        {
            System.err.println("Cannot find host: " + uhe.getMessage());
            localHostname = null;
        }

        System.out.println(localHostname + " alive");

        try
        {
            StreamTokenizer dispatcherFileStream = new StreamTokenizer(new FileReader("dispatcher.txt"));
            dispatcherFileStream.resetSyntax();
            dispatcherFileStream.whitespaceChars(0, ' ');
            dispatcherFileStream.wordChars(33, 255);
            dispatcherFileStream.eolIsSignificant(false);

            dispatcherFileStream.nextToken();
            this.dispatcherHostname = dispatcherFileStream.sval;

            (new DispatcherTalker(dispatcherHostname, "hello", localHostname, this)).start();
        }
        catch(IOException ioe)
        {
            System.err.println("didn't find a dispatcher file, waiting for 'sayhello'");
        }
    }

    protected void processJob(Object input, String jobId, String jobProcessorClass)
    {
        this.jobId = jobId;

        if((this.jobProcessorClass != null) && this.jobProcessorClass.equals(jobProcessorClass)) // no need to reload
        {
            jobProcessor.setInput(input);

            synchronized(jobProcessor)
            {
                jobProcessor.notifyAll();
            }
        }
        else
        {
            if(this.jobProcessor != null)
            {
                this.jobProcessor.die();
            }

            this.jobProcessorClass = jobProcessorClass;

            try
            {
                this.jobProcessor = (JobProcessor)Class.forName(jobProcessorClass).newInstance();
            }
            catch(InstantiationException e)
            {
                e.printStackTrace();
            }
            catch(IllegalAccessException e)
            {
                e.printStackTrace();
            }
            catch(ClassNotFoundException e)
            {
                e.printStackTrace();
            }

            this.jobProcessor.setResource(this);
            jobProcessor.setInput(input);
            this.jobProcessor.start();
        }
    }

    public boolean isProcessing()
    {
        if(jobProcessor == null)
        {
            return false;
        }
        else
        {
            return jobProcessor.isBusy();
        }
    }

    public boolean isReset()
    {
        return reset;
    }

    public void setReset(boolean b)
    {
        reset = b;
    }

    public String getLocalHostname()
    {
        return localHostname;
    }

    public String getResourceId()
    {
        return resourceId;
    }

    public String getJobId()
    {
        return jobId;
    }

    public void setResourceId(String resourceId)
    {
        this.resourceId = resourceId;
    }

    public String getDispatcherHostname()
    {
        return dispatcherHostname;
    }

    public void setDispatcherHostname(String string)
    {
        dispatcherHostname = string;
    }

    public static void main(String[] args)
    {
        (new DispatcherListener((new Resource()))).start();
    }
}
