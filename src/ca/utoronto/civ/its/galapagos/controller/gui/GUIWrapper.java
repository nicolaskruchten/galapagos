package ca.utoronto.civ.its.galapagos.controller.gui;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Date;

import javax.swing.UnsupportedLookAndFeelException;

import ca.utoronto.civ.its.galapagos.ParamException;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;
import ca.utoronto.civ.its.galapagos.controller.Controller;
import ca.utoronto.civ.its.galapagos.controller.XMLLogger;


public abstract class GUIWrapper extends Controller
{
    protected Date start;
    protected MainWindow theGUI;
    protected XMLLogger theLogger;
    private String configFilePath;

    public GUIWrapper()
    {
        try
        {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        }
        catch(ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch(InstantiationException e)
        {
            e.printStackTrace();
        }
        catch(IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch(UnsupportedLookAndFeelException e)
        {
            e.printStackTrace();
        }

        theGUI = new MainWindow();
        theGUI.setController(this);
        theGUI.enableLoad();
    }

    public void setConfigFilePath(String string)
    {
        configFilePath = string;
    }

    public void waitForUserInput()
    {
        ParamTreeNode paramRoot = null;

        theGUI.setMessage("Hit Load to send parameters to containers.");

        while(true)
        {
            synchronized(this)
            {
                try
                {
                    this.wait();
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

            paramRoot = null;

            try
            {
                paramRoot = ParamTreeNode.parse(new BufferedReader(new FileReader(configFilePath)));

                theLogger = new XMLLogger("logs", "galapagoslog" + System.currentTimeMillis() + ".xml", this);

                theLogger.logLoadEvent(paramRoot);

                if(load(paramRoot))
                {
                    handleLoadEvent(paramRoot);
                    waitForConvergence();
                }
            }
            catch(Exception ioe)
            {
                ioe.printStackTrace();
                handleLoadError();
            }
        }
    }

    public void handlePopulationSynchEvent(String populationId, String eventType)
    {
        theGUI.updateGraphs(populationId, eventType);

        theLogger.logPopulationUpdateEvent(populationId, eventType);
        this.handlePopulationSynchEvent2(populationId, eventType);
    }

    public void handleOutMigrationEvent(String populationId)
    {
        theLogger.logOutMigrationEvent(populationId);
        this.handleOutMigrationEvent2(populationId);
    }

    public void handleLoadEvent(ParamTreeNode params) throws ParamException
    {
        theGUI.initGraphs();
        this.handleLoadEvent2(params);
    }

    public void handleStopEvent()
    {
        Date end = new Date();

        theLogger.logEndEvent(end, start, "stop");

        this.handleStopEvent2();
    }

    public void handleStartEvent()
    {
        start = new Date();

        theLogger.logStartEvent(start);

        this.handleStartEvent2();
    }

    public void handleConvergenceEvent()
    {
        Date end = new Date();

        theLogger.logEndEvent(end, start, "convergence");

        theGUI.enableReset();
        theGUI.disableStop();
        theGUI.setMessage("Run converged! Hit Reset to clear graphs.");

        this.handleConvergenceEvent2();
    }

    public void handleParamLoadEvent(String containerId)
    {
        if(this.allParamsLoaded())
        {
            theGUI.setMessage("Hit Start to begin run.");
            theGUI.enableStart();
        }
    }

    public void handleLoadError()
    {
        theGUI.setMessage("Load error! Please fix & reload.");
        theGUI.enableLoad();
    }

    public abstract void handleOutMigrationEvent2(String populationId);

    public abstract void handlePopulationSynchEvent2(String populationId, String eventType);

    public abstract void handleLoadEvent2(ParamTreeNode params) throws ParamException;

    public abstract void handleStopEvent2();

    public abstract void handleStartEvent2();

    public abstract void handleConvergenceEvent2();
    
    public abstract void handleResetButtonClickEvent();
}
