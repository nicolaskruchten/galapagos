package ca.utoronto.civ.its.galapagos.controller.gui;

import java.awt.Component;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;

import org.jfree.data.XYSeries;
import org.jfree.data.XYSeriesCollection;


public class SaveableXYSeriesCollection extends XYSeriesCollection
{
    public void saveDataToFile(Component component)
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new CustomFileFilter("csv", "Comma-Separated Value (.csv)"));
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setCurrentDirectory(new File("."));

        int returnVal = chooser.showSaveDialog(component);

        if(returnVal == JFileChooser.APPROVE_OPTION)
        {
            if(chooser.getSelectedFile().exists())
            {
                chooser.getSelectedFile().delete();
            }

            try
            {
                FileWriter log = new FileWriter(chooser.getSelectedFile(), true);
                XYSeries[] theSeries = (XYSeries[])this.getSeries().toArray(new XYSeries[1]);

                log.write("Xseries,");

                int maxItemCount = -1;
                int biggestSeries = -1;

                for(int i = 0; i < theSeries.length; i++)
                {
                    log.write(theSeries[i].getName() + ",");

                    if(theSeries[i].getItemCount() > maxItemCount)
                    {
                        maxItemCount = theSeries[i].getItemCount();
                        biggestSeries = i;
                    }
                }

                log.write("\n");

                for(int i = 0; i < maxItemCount; i++)
                {
                    log.write(theSeries[biggestSeries].getDataItem(i).getX() + ",");

                    for(int j = 0; j < theSeries.length; j++)
                    {
                        if(theSeries[j].getItemCount() > i)
                        {
                            log.write(theSeries[j].getDataItem(i).getY() + ",");
                        }
                        else
                        {
                            log.write(" ,");
                        }
                    }

                    log.write("\n");
                }

                log.close();
            }
            catch(IOException ioe)
            {
                System.err.println("Could not write to data file");
                ioe.printStackTrace();
            }
        }
    }
}
