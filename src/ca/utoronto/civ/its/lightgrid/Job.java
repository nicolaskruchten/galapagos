package ca.utoronto.civ.its.lightgrid;

import java.util.Enumeration;
import java.util.Vector;


public class Job
{
    private boolean resultSent;
    private ClientHandle client;
    private String jobId;
    private Object parameters;
    private String jobProcessorClass;
    private Vector assignedResources;
    private int priority;

    public Job(String jobId, Object parameters, ClientHandle c, int priority, String jobProcessorClass)
    {
        this.parameters = parameters;
        this.client = c;
        this.jobId = jobId;
        this.priority = priority;
        this.jobProcessorClass = jobProcessorClass;
        assignedResources = new Vector();
        resultSent = false;
    }

    public Enumeration getAssignedResources()
    {
        return assignedResources.elements();
    }

    public ClientHandle getClient()
    {
        return client;
    }

    public String getJobId()
    {
        return jobId;
    }

    public Object getParameters()
    {
        return parameters;
    }

    public int getPriority()
    {
        return priority;
    }

    public void assignResource(ResourceHandle r)
    {
        assignedResources.addElement(r);
    }

    public void deAssignResource(ResourceHandle r)
    {
        assignedResources.removeElement(r);
    }

    public boolean hasAssignedResources()
    {
        return (assignedResources.size() != 0);
    }

    public boolean isResultSent()
    {
        return resultSent;
    }

    public void setResultSent(boolean b)
    {
        resultSent = b;
    }

    public String getJobProcessorClass()
    {
        return jobProcessorClass;
    }
}
