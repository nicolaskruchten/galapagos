package ca.utoronto.civ.its.galapagos.controller.gui;

import ca.utoronto.civ.its.galapagos.ParamException;
import ca.utoronto.civ.its.galapagos.ParamTreeNode;


public class GUIController extends GUIWrapper
{
    public GUIController()
    {
        super();
    }

    public void handleLoadEvent2(ParamTreeNode params) throws ParamException
    {
    }

    public void handlePopulationSynchEvent2(String populationId, String eventType)
    {
    }

    public void handleOutMigrationEvent2(String populationId)
    {
    }

    public void handleStopEvent2()
    {
    }

    public void handleStartEvent2()
    {
    }

    public void handleConvergenceEvent2()
    {
    }

    public static void main(String[] args)
    {
        (new GUIController()).waitForUserInput();
    }

	public void handleResetButtonClickEvent() 
	{	
	}
}
