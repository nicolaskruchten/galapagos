package ca.utoronto.civ.its.lightgrid.dispatcher;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import ca.utoronto.civ.its.lightgrid.ClientHandle;
import ca.utoronto.civ.its.lightgrid.Job;
import ca.utoronto.civ.its.lightgrid.ResourceHandle;


public class Dispatcher
{
    private Hashtable clientIdToClientHandle;
    private String localHostname;
    private Hashtable clientIdToSentJobIdVector;
    private Hashtable jobIdToSentJob;
    private Hashtable resourceIdToResourceHandle;
    private Vector resourceQueue;
    private Vector unsentJobQueue;
    private Vector reassignableSentJobQueue;
    private Hashtable clientIdToJobQueueNum;
    private int nextClientId;
    private int nextResourceId;

    public Dispatcher()
    {
        resourceQueue = new Vector();
        unsentJobQueue = new Vector();
        reassignableSentJobQueue = new Vector();
        clientIdToJobQueueNum = new Hashtable();

        jobIdToSentJob = new Hashtable();
        clientIdToClientHandle = new Hashtable();
        resourceIdToResourceHandle = new Hashtable();
        clientIdToSentJobIdVector = new Hashtable();

        nextResourceId = 0;
        nextClientId = 0;

        (new ResourceListener(this)).start();
        (new ClientListener(this)).start();
        (new UserInputListener(this)).start();

        try
        {
            localHostname = InetAddress.getLocalHost().getCanonicalHostName();
        }
        catch(UnknownHostException uhe)
        {
            System.err.println("Cannot find host: " + uhe.getMessage());
            localHostname = null;
        }

        try
        {
            FileWriter theFile = new FileWriter("dispatcher.txt");
            theFile.write(localHostname + "\n");
            theFile.close();
        }
        catch(IOException ioe)
        {
            System.err.println("can't access dispatcher file");
            System.exit(1);
        }

        System.out.println("\nLightGrid Dispatcher booted ok.\n");

        try
        {
            StreamTokenizer resourceFileStream = new StreamTokenizer(new FileReader("resources.txt"));
            resourceFileStream.resetSyntax();
            resourceFileStream.whitespaceChars(0, ' ');
            resourceFileStream.wordChars(33, 255);
            resourceFileStream.eolIsSignificant(false);

            while(resourceFileStream.nextToken() == StreamTokenizer.TT_WORD)
            {
                (new ResourceTalker(new ResourceHandle(resourceFileStream.sval, "none"), "sayhello " + localHostname, "")).start();
            }
        }
        catch(IOException ioe)
        {
            System.err.println("can't access resources file, waiting for hello");
        }
    }

    public synchronized String addClient(String hostname) throws UnknownHostException
    {
        ClientHandle c = new ClientHandle(hostname, (new Integer(nextClientId).toString()));
        nextClientId++;
        clientIdToClientHandle.put(c.getId(), c);
        clientIdToSentJobIdVector.put(c.getId(), new Vector());
        System.out.println("Registered client " + (nextClientId - 1) + " at " + hostname);

        return c.getId();
    }

    public void addJobs(Job[] theJobs)
    {
        for(int i = 0; i < theJobs.length; i++)
        {
            if(!clientIdToJobQueueNum.containsKey(theJobs[i].getClient().getId()))
            {
                clientIdToJobQueueNum.put(theJobs[i].getClient().getId(), (new Integer(unsentJobQueue.size())));
                unsentJobQueue.addElement(new Vector());
                reassignableSentJobQueue.addElement(new Vector());
            }

            ((Vector)unsentJobQueue.elementAt(((Integer)clientIdToJobQueueNum.get(theJobs[i].getClient().getId())).intValue())).addElement(theJobs[i]);
        }

        synchronized(this)
        {
            this.notifyAll();
        }
    }

    public synchronized String addResource(String hostname) throws UnknownHostException
    {
        ResourceHandle r = new ResourceHandle(hostname, (new Integer(nextResourceId).toString()));
        nextResourceId++;
        resourceIdToResourceHandle.put(r.getId(), r);
        System.out.println("Registered resource " + (nextResourceId - 1) + " at " + hostname);
        enqueueResource(r);

        return r.getId();
    }

    public void enqueueResource(ResourceHandle s)
    {
        resourceQueue.addElement(s);

        synchronized(this)
        {
            this.notifyAll();
        }
    }

    public void enqueueResource(String resourceId)
    {
        enqueueResource((ResourceHandle)resourceIdToResourceHandle.get(resourceId));
    }

    public void processReset(String resourceId, String jobId)
    {
        ResourceHandle r = (ResourceHandle)resourceIdToResourceHandle.get(resourceId);
        Job j = (Job)jobIdToSentJob.get(jobId);

        if(j != null)
        {
            j.deAssignResource(r);

            if(!j.hasAssignedResources())
            {
                jobIdToSentJob.remove(jobId);
                ((Vector)clientIdToSentJobIdVector.get(j.getClient().getId())).remove(jobId);
            }
        }

        enqueueResource(r);
    }

    public void processResult(String resourceId, String jobId, Object result)
    {
        ResourceHandle r = (ResourceHandle)resourceIdToResourceHandle.get(resourceId);
        Job j = (Job)jobIdToSentJob.get(jobId);

        if(j != null)
        {
            j.deAssignResource(r);

            if(!j.isResultSent())
            {
                j.setResultSent(true);
                (new ClientTalker(j.getClient(), "result " + j.getJobId(), result)).start();
            }

            if(j.hasAssignedResources())
            {
                Enumeration assignedResources = j.getAssignedResources();

                while(assignedResources.hasMoreElements())
                {
                    ResourceHandle theResource = (ResourceHandle)assignedResources.nextElement();
                    (new ResourceTalker(theResource, "reset " + j.getJobId(), "")).start();
                }
            }
            else
            {
                jobIdToSentJob.remove(jobId);
                ((Vector)clientIdToSentJobIdVector.get(j.getClient().getId())).remove(jobId);
            }
        }

        enqueueResource(r);
    }

    private void dispatch()
    {
        Job job = null;
        int lastQueueNum = 0;

        while(true)
        {
            synchronized(unsentJobQueue)
            {
                synchronized(reassignableSentJobQueue)
                {
                    while((!jobQueuesEmpty()) && (resourceQueue.size() > 0))
                    {
                        job = null;

                        for(int i = 0; i < unsentJobQueue.size(); i++)
                        {
                            lastQueueNum = (lastQueueNum + 1) % unsentJobQueue.size();

                            if(((Vector)unsentJobQueue.elementAt(lastQueueNum)).size() != 0)
                            {
                                job = (Job)((Vector)unsentJobQueue.elementAt(lastQueueNum)).elementAt(0);
                                ((Vector)unsentJobQueue.elementAt(lastQueueNum)).removeElementAt(0);
                                jobIdToSentJob.put(job.getJobId(), job);
                                ((Vector)clientIdToSentJobIdVector.get(job.getClient().getId())).addElement(job.getJobId());

                                break;
                            }
                            else if(((Vector)reassignableSentJobQueue.elementAt(lastQueueNum)).size() != 0)
                            {
                                do
                                {
                                    job = (Job)((Vector)reassignableSentJobQueue.elementAt(lastQueueNum)).elementAt(0);
                                    ((Vector)reassignableSentJobQueue.elementAt(lastQueueNum)).removeElementAt(0);
                                }
                                while(((!jobIdToSentJob.containsKey(job.getJobId())) || (job.isResultSent())) && (((Vector)reassignableSentJobQueue.elementAt(lastQueueNum)).size() != 0));

                                if((jobIdToSentJob.containsKey(job.getJobId())) || (!job.isResultSent()))
                                {
                                    break;
                                }
                            }
                        }

                        if((job != null) && (jobIdToSentJob.containsKey(job.getJobId())) && (!job.isResultSent()))
                        {
                            job.assignResource(((ResourceHandle)resourceQueue.elementAt(0)));
                            (new ResourceTalker((ResourceHandle)resourceQueue.elementAt(0), "job " + job.getJobId() + " " + job.getJobProcessorClass(), job.getParameters())).start();
                            resourceQueue.removeElementAt(0);

                            if(job.getPriority() > 0)
                            {
                                ((Vector)reassignableSentJobQueue.elementAt(((Integer)clientIdToJobQueueNum.get(job.getClient().getId())).intValue())).addElement(job);
                            }
                        }
                    }
                }
            }

            try
            {
                synchronized(this)
                {
                    this.wait();
                }
            }
            catch(InterruptedException ie)
            {
                ie.printStackTrace();
            }
        }
    }

    public boolean jobQueuesEmpty()
    {
        for(int i = 0; i < unsentJobQueue.size(); i++)
        {
            if((((Vector)unsentJobQueue.elementAt(i)).size() != 0) || (((Vector)reassignableSentJobQueue.elementAt(i)).size() != 0))
            {
                return false;
            }
        }

        return true;
    }

    public void resetAll()
    {
        Enumeration clientIds = clientIdToClientHandle.keys();

        while(clientIds.hasMoreElements())
        {
            resetClientById((String)clientIds.nextElement());
        }

        jobIdToSentJob = new Hashtable();
        clientIdToJobQueueNum = new Hashtable();
        unsentJobQueue = new Vector();
        reassignableSentJobQueue = new Vector();
    }

    public void resetClientById(String clientId)
    {
        synchronized(unsentJobQueue)
        {
            synchronized(reassignableSentJobQueue)
            {
                System.out.println("resetting clientId" + clientId);

                Enumeration sentJobIdList = ((Vector)clientIdToSentJobIdVector.get(clientId)).elements();
                Enumeration assignedResources = null;

                Job theJob = null;

                while(sentJobIdList.hasMoreElements())
                {
                    theJob = ((Job)jobIdToSentJob.get((String)sentJobIdList.nextElement()));
                    theJob.setResultSent(true);
                    assignedResources = theJob.getAssignedResources();

                    while(assignedResources.hasMoreElements())
                    {
                        (new ResourceTalker((ResourceHandle)assignedResources.nextElement(), "reset " + theJob.getJobId(), "")).start();
                    }
                }

                if(clientIdToJobQueueNum.containsKey(clientId))
                {
                    ((Vector)unsentJobQueue.elementAt(((Integer)clientIdToJobQueueNum.get(clientId)).intValue())).removeAllElements();
                    ((Vector)reassignableSentJobQueue.elementAt(((Integer)clientIdToJobQueueNum.get(clientId)).intValue())).removeAllElements();
                }
            }
        }
    }

    public void killAllResources()
    {
        Enumeration allResourceIds = resourceIdToResourceHandle.keys();

        while(allResourceIds.hasMoreElements())
        {
            (new ResourceTalker((ResourceHandle)resourceIdToResourceHandle.get(allResourceIds.nextElement()), "die", "")).start();
        }
    }

    public static void main(String[] args) throws Throwable
    {
        (new Dispatcher()).dispatch();
    }

    public ClientHandle getClientHandle(String clientid)
    {
        return (ClientHandle)clientIdToClientHandle.get(clientid);
    }
}
