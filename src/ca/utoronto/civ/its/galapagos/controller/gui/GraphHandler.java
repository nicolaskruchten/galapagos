package ca.utoronto.civ.its.galapagos.controller.gui;

import java.util.Date;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.HistogramDataset;
import org.jfree.data.XYSeries;

import ca.utoronto.civ.its.galapagos.controller.Controller;
import ca.utoronto.civ.its.galapagos.controller.PopulationSnapshot;


public class GraphHandler
{
    MainWindow theWindow;
    private Controller controller;
    private javax.swing.JPanel globalFitnessGraphPanel = null;
    private javax.swing.JPanel globalStdGraphPanel = null;
    private javax.swing.JPanel localFitnessGraphPanel = null;
    private javax.swing.JPanel localStdGraphPanel = null;
    private javax.swing.JPanel fitnessHistogramPanel = null;
    SaveableXYSeriesCollection globalFitnessData;
    SaveableXYSeriesCollection globalStdData;
    SaveableXYSeriesCollection localFitnessData;
    SaveableXYSeriesCollection localStdData;
    private HistogramDataset histogramData;
    private double minFitness;
    private double maxFitness;
    private Date start;

    public GraphHandler(MainWindow theWindow2)
    {
        this.theWindow = theWindow2;
        globalStdData = new SaveableXYSeriesCollection();
        globalFitnessData = new SaveableXYSeriesCollection();
        localStdData = new SaveableXYSeriesCollection();
        localFitnessData = new SaveableXYSeriesCollection();

        globalFitnessGraphPanel = new ChartPanel(ChartFactory.createXYLineChart("Global Fitness vs Time", "Seconds", "Fitness", globalFitnessData, PlotOrientation.VERTICAL, true, false, false));
        globalStdGraphPanel = new ChartPanel(ChartFactory.createXYLineChart("Global Standard Deviation vs Time", "Seconds", "StdDev", globalStdData, PlotOrientation.VERTICAL, true, false, false));
        fitnessHistogramPanel = new ChartPanel(ChartFactory.createHistogram("Global Fitness Histogram", "Fitness", "Number of Chromosomes", histogramData, PlotOrientation.VERTICAL, true, false, false));
        localFitnessGraphPanel = new ChartPanel(ChartFactory.createXYLineChart("Population Fitnesses vs Generations", "Generations", "Fitness", localFitnessData, PlotOrientation.VERTICAL, true, false, false));
        localStdGraphPanel = new ChartPanel(ChartFactory.createXYLineChart("Population Standard Deviations vs Generations", "Generations", "StdDev", localStdData, PlotOrientation.VERTICAL, true, false, false));

        ((ChartPanel)globalFitnessGraphPanel).getPopupMenu().add("Save data...").addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    globalFitnessData.saveDataToFile(theWindow);
                }
            });

        ((ChartPanel)globalStdGraphPanel).getPopupMenu().add("Save data...").addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    globalStdData.saveDataToFile(theWindow);
                }
            });

        ((ChartPanel)localStdGraphPanel).getPopupMenu().add("Save data...").addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    localStdData.saveDataToFile(theWindow);
                }
            });

        ((ChartPanel)localFitnessGraphPanel).getPopupMenu().add("Save data...").addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    localFitnessData.saveDataToFile(theWindow);
                }
            });

        resetGraphs();
    }

    public void addTabs()
    {
        theWindow.addTab("Global Fitness", globalFitnessGraphPanel);
        theWindow.addTab("Global StdDev", globalStdGraphPanel);
        theWindow.addTab("Local Fitnesses", localFitnessGraphPanel);
        theWindow.addTab("Local StdDevs", localStdGraphPanel);
        theWindow.addTab("Histogram", fitnessHistogramPanel);
    }

    public void initGraphs()
    {
        resetGraphs();
        globalFitnessData.addSeries(new XYSeries("min"));
        globalFitnessData.addSeries(new XYSeries("mean"));
        globalFitnessData.addSeries(new XYSeries("max"));
        globalStdData.addSeries(new XYSeries("stdev"));

        String[] theIds = controller.getPopulationIds();

        for(int i = 0; i < theIds.length; i++)
        {
            localFitnessData.addSeries(new XYSeries(theIds[i] + " mean"));
            localFitnessData.addSeries(new XYSeries(theIds[i] + " max"));
            localStdData.addSeries(new XYSeries(theIds[i] + " stdev"));
        }
    }

    public synchronized void updateGraphs(String populationId, String eventType)
    {
        PopulationSnapshot thePopulation = controller.getGlobalPopulation();

        // update global graphs
        if(thePopulation.isPopulationFullyEvaluated())
        {
            if(globalFitnessData.getItemCount(0) == 0)
            {
                start = new Date();
            }

            double average = thePopulation.getMeanFitness();
            double sampleSigma = thePopulation.getFitnessStd();
            double min = thePopulation.getMinFitness();
            double max = thePopulation.getMaxFitness();

            if((min < minFitness) || (globalFitnessData.getItemCount(0) == 0))
            {
                minFitness = min;
            }

            if((max > maxFitness) || (globalFitnessData.getItemCount(2) == 0))
            {
                maxFitness = max;
            }

            Date now = new Date();
            double nowtime = ((now.getTime() - start.getTime()) / 1000.0);

            globalFitnessData.getSeries(0).add(nowtime, min, true);
            globalFitnessData.getSeries(1).add(nowtime, average, true);
            globalFitnessData.getSeries(2).add(nowtime, max, true);
            globalStdData.getSeries(0).add(nowtime, sampleSigma, true);

            double[] values = thePopulation.getFitnesses();
            histogramData = new HistogramDataset();
            histogramData.setType(HistogramDataset.FREQUENCY);
            histogramData.addSeries("fitness", values, 10);
            ((ChartPanel)fitnessHistogramPanel).setChart(ChartFactory.createHistogram("Global Fitness Histogram", "Fitness", "Number of Chromosomes", histogramData, PlotOrientation.VERTICAL, true, false, false));

            double lower = values[0] - ((values[values.length - 1] - values[0]) / 3);
            double upper = values[values.length - 1] + ((values[values.length - 1] - values[0]) / 3);
            ((ChartPanel)fitnessHistogramPanel).getChart().getXYPlot().getDomainAxis().setLowerBound(lower);
            ((ChartPanel)fitnessHistogramPanel).getChart().getXYPlot().getDomainAxis().setUpperBound(upper);

            upper = maxFitness + ((maxFitness - minFitness) / 3);
            lower = minFitness - ((maxFitness - minFitness) / 3);
            ((ChartPanel)globalFitnessGraphPanel).getChart().getXYPlot().getRangeAxis().setUpperBound(upper);
            ((ChartPanel)globalFitnessGraphPanel).getChart().getXYPlot().getRangeAxis().setLowerBound(lower);

            ((ChartPanel)localFitnessGraphPanel).getChart().getXYPlot().getRangeAxis().setUpperBound(upper);
            ((ChartPanel)localFitnessGraphPanel).getChart().getXYPlot().getRangeAxis().setLowerBound(lower);
        }

        // update local graphs
        if(eventType.equals("generation"))
        {
            thePopulation = controller.getPopulation(populationId);

            String[] thePopulationIds = controller.getPopulationIds();

            for(int i = 0; i < thePopulationIds.length; i++)
            {
                if(thePopulationIds[i].equals(populationId))
                {
                    if(localFitnessData.getSeries(2 * i).getItemCount() <= controller.getNumGenerations(populationId))
                    {
                        localFitnessData.getSeries(2 * i).add(controller.getNumGenerations(populationId), thePopulation.getMeanFitness());
                    }

                    if(localFitnessData.getSeries((2 * i) + 1).getItemCount() <= controller.getNumGenerations(populationId))
                    {
                        localFitnessData.getSeries((2 * i) + 1).add(controller.getNumGenerations(populationId), thePopulation.getMaxFitness());
                    }

                    if(localStdData.getSeries(i).getItemCount() <= controller.getNumGenerations(populationId))
                    {
                        localStdData.getSeries(i).add(controller.getNumGenerations(populationId), thePopulation.getFitnessStd());
                    }

                    break;
                }
            }
        }
    }

    public void resetGraphs()
    {
        globalFitnessData.removeAllSeries();
        globalStdData.removeAllSeries();
        localFitnessData.removeAllSeries();
        localStdData.removeAllSeries();

        histogramData = new HistogramDataset();
        histogramData.setType(HistogramDataset.FREQUENCY);
        ((ChartPanel)fitnessHistogramPanel).setChart(ChartFactory.createHistogram("Global Fitness Histogram", "Fitness", "Number of Chromosomes", histogramData, PlotOrientation.VERTICAL, true, false, false));
    }

    public void setController(Controller controller)
    {
        this.controller = controller;
    }
}
