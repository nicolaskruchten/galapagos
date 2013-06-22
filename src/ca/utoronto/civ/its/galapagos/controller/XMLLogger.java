package ca.utoronto.civ.its.galapagos.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import ca.utoronto.civ.its.galapagos.ParamTreeNode;


public class XMLLogger
{
    private String fileName;
    private Controller theController;

    public XMLLogger(String dirName, String fileName, Controller theController)
    {
        if(!(new File(dirName)).exists())
        {
            (new File(dirName)).mkdir();
        }

        this.fileName = dirName + "/" + fileName;
        this.theController = theController;
    }

    public void logOutMigrationEvent(String populationId)
    {
        try
        {
            FileWriter log = new FileWriter(fileName, true);
            log.write("    <event type=\"outmigration\" date=\"" + new java.util.Date() + "\" popID=\"" + populationId + "\" num=\"" + theController.getNumOutMigrations(populationId) + "\" />\n");
            log.close();
        }
        catch(IOException ioe)
        {
            System.err.println("Could not write to log file");
            ioe.printStackTrace();
        }
    }

    public void logPopulationUpdateEvent(String populationId, String eventType)
    {
        int number = 0;

        if(eventType.equals("generation"))
        {
            number = theController.getNumGenerations(populationId);
        }
        else if(eventType.equals("inmigration"))
        {
            number = theController.getNumInMigrations(populationId);
        }
        else if(eventType.equals("hybridization"))
        {
            number = theController.getNumHybridizations(populationId);
        }

        try
        {
            PopulationSnapshot thePopulation = theController.getPopulation(populationId);

            FileWriter log = new FileWriter(fileName, true);
            log.write("    <event type=\"" + eventType + "\" date=\"" + new java.util.Date() + "\" popID=\"" + populationId + "\" num=\"" + number + "\">\n");
            log.write("      <min>     " + thePopulation.getMinFitness() + "  </min>\n");
            log.write("      <max>     " + thePopulation.getMaxFitness() + "  </max>\n");
            log.write("      <average> " + thePopulation.getMeanFitness() + "  </average>\n");
            log.write("      <stdev>   " + thePopulation.getFitnessStd() + "  </stdev>\n");

            thePopulation = theController.getGlobalPopulation();

            if(thePopulation.isPopulationFullyEvaluated())
            {
                log.write("      <globalmin>     " + thePopulation.getMinFitness() + "  </globalmin>\n");
                log.write("      <globalmax>     " + thePopulation.getMaxFitness() + "  </globalmax>\n");
                log.write("      <globalaverage> " + thePopulation.getMeanFitness() + "  </globalaverage>\n");
                log.write("      <globalstdev>   " + thePopulation.getFitnessStd() + "  </globalstdev>\n");
            }

            log.write("    </event>\n");
            log.close();
        }
        catch(IOException ioe)
        {
            System.err.println("Could not write to log file");
            ioe.printStackTrace();
        }
    }

    public void logLoadEvent(ParamTreeNode paramRoot)
    {
        try
        {
            FileWriter log = new FileWriter(fileName, true);
            log.write("<?xml version=\"1.0\"?>\n");
            log.write("<galapagos-run>\n<parameters>\n");
            log.write(paramRoot.toXMLString());
            log.write("</parameters>\n");
            log.flush();
            log.close();
        }
        catch(IOException ioe)
        {
            System.err.println("trouble opening logfile");
        }
    }

    public void logStartEvent(Date start)
    {
        try
        {
            FileWriter log = new FileWriter(fileName, true);
            log.write("  <start>" + start + "</start>\n");
            log.write("  <events>\n");

            log.close();
        }
        catch(IOException ioe)
        {
            System.err.println("Could not write to log file");
        }
    }

    public void logEndEvent(Date end, Date start, String stopType)
    {
        try
        {
            FileWriter log = new FileWriter(fileName, true);

            PopulationSnapshot globalPop = theController.getGlobalPopulation();

            log.write("  </events>\n");
            log.write("  <end>" + end + "</end>\n");
            log.write("  <running-time units=\"seconds\">" + ((end.getTime() - start.getTime()) / 1000) + "</running-time>\n");
            log.write("  <stopcondition>" + stopType + "</stopcondition>\n");
            log.write("</galapagos-run>\n");
            log.close();
        }
        catch(IOException ioe)
        {
            System.err.println("Could not print to log file");
        }
    }
}
