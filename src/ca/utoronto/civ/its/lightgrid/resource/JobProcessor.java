package ca.utoronto.civ.its.lightgrid.resource;

public abstract class JobProcessor extends Thread
{
    protected Resource resource;
    protected Object input;
    private boolean busy;
    private boolean die = false;

    public void setInput(Object input)
    {
        this.input = input;
    }

    public void setResource(Resource resource)
    {
        this.resource = resource;
    }

    public void die() //this only will get called while this thread is waiting...
    {
        die = true;

        synchronized(this)
        {
            this.notifyAll();
        }
    }

    public void run()
    {
        while(!die)
        {
            busy = true;
            evaluate(); //takes a while! calls sendResult or acknowledgeReset
            busy = false;

            synchronized(this)
            {
                try
                {
                    this.wait();
                }
                catch(InterruptedException ie)
                {
                    ie.printStackTrace();
                }
            }
        }
    }

    public void acknowledgeReset()
    {
        (new DispatcherTalker(resource.getDispatcherHostname(), "reset", "", resource)).start();
    }

    public void sendResult(Object result)
    {
        (new DispatcherTalker(resource.getDispatcherHostname(), "result", result, resource)).start();
    }

    protected abstract void evaluate();

    public boolean isBusy()
    {
        return busy;
    }

    public boolean isReset()
    {
        return resource.isReset();
    }
}
