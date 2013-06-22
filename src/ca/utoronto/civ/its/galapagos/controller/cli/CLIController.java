package ca.utoronto.civ.its.galapagos.controller.cli;

import ca.utoronto.civ.its.galapagos.ParamTreeNode;


public class CLIController extends CLIWrapper
{
    public CLIController()
    {
        super();
    }

    public void handleLoadEvent2(ParamTreeNode params)
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
        (new CLIController()).waitForUserInput();
    }
}
