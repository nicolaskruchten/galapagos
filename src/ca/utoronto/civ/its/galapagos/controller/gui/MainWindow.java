package ca.utoronto.civ.its.galapagos.controller.gui;

import java.awt.Component;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;


public class MainWindow extends JFrame
{
    GUIWrapper controller;
    javax.swing.JPanel mainPanel = null;
    private javax.swing.JButton loadButton = null;
    private javax.swing.JButton startButton = null;
    private javax.swing.JButton savePopButton = null;
    private javax.swing.JButton stopButton = null;
    private javax.swing.JButton resetButton = null;
    private javax.swing.JLabel messageLabel = null;
    private javax.swing.JToolBar jToolBar = null;
    private javax.swing.JTabbedPane jTabbedPane = null;
    private javax.swing.JPanel messagePanel = null;
    GraphHandler graphs;

    public MainWindow()
    {
        super();
        initialize();
    }

    private void initialize()
    {
        graphs = new GraphHandler(this);
        this.setContentPane(getMainPanel());
        this.setSize(503, 388);
        this.setTitle("Galapagos");
        this.setVisible(true);
        this.setResizable(true);
        this.addWindowListener(new java.awt.event.WindowAdapter()
            {
                public void windowClosing(java.awt.event.WindowEvent e)
                {
                    System.exit(0);
                }
            });
    }

    public void addTab(String tabName, Component theTab)
    {
        jTabbedPane.addTab(tabName, theTab);
    }

    public void setController(GUIWrapper controller)
    {
        this.controller = controller;
        graphs.setController(controller);
    }

    public void setMessage(String message)
    {
        messageLabel.setText(message);
    }

    public synchronized void updateGraphs(String populationId, String eventType)
    {
        graphs.updateGraphs(populationId, eventType);
    }

    public void resetGraphs()
    {
        graphs.resetGraphs();
    }

    public void enableLoad()
    {
        loadButton.setEnabled(true);
    }

    public void disableLoad()
    {
        loadButton.setEnabled(false);
    }

    public void enableStart()
    {
        startButton.setEnabled(true);
    }

    public void disableStart()
    {
        startButton.setEnabled(false);
    }

    public void enableSavePop()
    {
        savePopButton.setEnabled(true);
    }

    public void disableSavePop()
    {
        savePopButton.setEnabled(false);
    }

    public void enableStop()
    {
        stopButton.setEnabled(true);
    }

    public void disableStop()
    {
        stopButton.setEnabled(false);
    }

    public void enableReset()
    {
        resetButton.setEnabled(true);
    }

    public void disableReset()
    {
        resetButton.setEnabled(false);
    }

    private javax.swing.JPanel getMainPanel()
    {
        if(mainPanel == null)
        {
            mainPanel = new javax.swing.JPanel();

            java.awt.BorderLayout layBorderLayout4 = new java.awt.BorderLayout();
            layBorderLayout4.setVgap(5);
            mainPanel.setLayout(layBorderLayout4);
            mainPanel.add(getJToolBar(), java.awt.BorderLayout.NORTH);
            mainPanel.add(getJTabbedPane(), java.awt.BorderLayout.CENTER);
        }

        return mainPanel;
    }

    private javax.swing.JButton getLoadButton()
    {
        if(loadButton == null)
        {
            loadButton = new javax.swing.JButton();
            loadButton.setText("Load...");
            loadButton.setEnabled(false);
            loadButton.addActionListener(new java.awt.event.ActionListener()
                {
                    public void actionPerformed(java.awt.event.ActionEvent e)
                    {
                        JFileChooser chooser = new JFileChooser();
                        chooser.setFileFilter(new CustomFileFilter("xml", "Galapagos Configuration Files (.xml)"));
                        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                        chooser.setCurrentDirectory(new File("."));

                        int returnVal = chooser.showDialog(mainPanel, "Load");

                        if(returnVal == JFileChooser.APPROVE_OPTION)
                        {
                            try
                            {
                                controller.setConfigFilePath(chooser.getSelectedFile().getCanonicalPath());
                            }
                            catch(IOException e1)
                            {
                                e1.printStackTrace();
                            }

                            disableLoad();
                            setMessage("Processing parameters...");

                            synchronized(controller)
                            {
                                //ControllerGUIWrapper should currently be in a wait loop
                                controller.notifyAll();
                            }
                        }
                    }
                });
        }

        return loadButton;
    }

    public void initGraphs()
    {
        graphs.initGraphs();
    }

    private javax.swing.JButton getStartButton()
    {
        if(startButton == null)
        {
            startButton = new javax.swing.JButton();
            startButton.setText("Start");
            startButton.setEnabled(false);
            startButton.addActionListener(new java.awt.event.ActionListener()
                {
                    public void actionPerformed(java.awt.event.ActionEvent e)
                    {
                        if(controller.isStopped() && controller.isLoaded() && controller.allParamsLoaded())
                        {
                            controller.start();
                        }

                        enableStop();
                        enableSavePop();
                        disableStart();
                        setMessage("Run in progress...");
                    }
                });
        }

        return startButton;
    }

    private javax.swing.JButton getSavePopButton()
    {
        if(savePopButton == null)
        {
            savePopButton = new javax.swing.JButton();
            savePopButton.setText("Save population...");
            savePopButton.setEnabled(false);
            savePopButton.addActionListener(new java.awt.event.ActionListener()
                {
                    public void actionPerformed(java.awt.event.ActionEvent e)
                    {
                        JFileChooser chooser = new JFileChooser();
                        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        chooser.setCurrentDirectory(new File("."));

                        int returnVal = chooser.showSaveDialog(null);

                        if(returnVal == JFileChooser.APPROVE_OPTION)
                        {
                            if(chooser.getSelectedFile().exists())
                            {
                                chooser.getSelectedFile().delete();
                            }

                            try
                            {
                                controller.getGlobalPopulation().dumpToFile(chooser.getSelectedFile().getCanonicalPath());
                            }
                            catch(IOException x)
                            {
                                x.printStackTrace();
                            }
                        }
                    }
                });
        }

        return savePopButton;
    }

    private javax.swing.JButton getStopButton()
    {
        if(stopButton == null)
        {
            stopButton = new javax.swing.JButton();
            stopButton.setText("Stop");
            stopButton.setEnabled(false);
            stopButton.addActionListener(new java.awt.event.ActionListener()
                {
                    public void actionPerformed(java.awt.event.ActionEvent e)
                    {
                        if(!controller.isStopped())
                        {
                            controller.stop(false);
                        }

                        enableReset();
                        disableStop();
                        setMessage("Hit Reset to clear graphs.");
                    }
                });
        }

        return stopButton;
    }

    private javax.swing.JButton getResetButton()
    {
        if(resetButton == null)
        {
            resetButton = new javax.swing.JButton();
            resetButton.setText("Reset");
            resetButton.setEnabled(false);
            resetButton.addActionListener(new java.awt.event.ActionListener()
                {
                    public void actionPerformed(java.awt.event.ActionEvent e)
                    {
                        resetGraphs();
                        
                        controller.handleResetButtonClickEvent();
                        
                        disableReset();
                        disableSavePop();
                        enableLoad();
                        setMessage("Hit Load to send parameters to containers.");
                    }
                });
        }

        return resetButton;
    }

    private javax.swing.JLabel getMessageLabel()
    {
        if(messageLabel == null)
        {
            messageLabel = new javax.swing.JLabel();
            messageLabel.setText("Hit Load to send parameters to containers.");
            messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            messageLabel.setHorizontalTextPosition(javax.swing.SwingConstants.TRAILING);
        }

        return messageLabel;
    }

    private javax.swing.JToolBar getJToolBar()
    {
        if(jToolBar == null)
        {
            jToolBar = new javax.swing.JToolBar();
            jToolBar.add(getLoadButton());
            jToolBar.add(getStartButton());
            jToolBar.add(getStopButton());
            jToolBar.add(getResetButton());
            jToolBar.add(getSavePopButton());
            jToolBar.add(getMessagePanel());
        }

        return jToolBar;
    }

    private javax.swing.JTabbedPane getJTabbedPane()
    {
        if(jTabbedPane == null)
        {
            jTabbedPane = new javax.swing.JTabbedPane();
            graphs.addTabs();
        }

        return jTabbedPane;
    }

    private javax.swing.JPanel getMessagePanel()
    {
        if(messagePanel == null)
        {
            messagePanel = new javax.swing.JPanel();

            java.awt.FlowLayout layFlowLayout3 = new java.awt.FlowLayout();
            layFlowLayout3.setAlignment(java.awt.FlowLayout.LEFT);
            layFlowLayout3.setHgap(10);
            messagePanel.setLayout(layFlowLayout3);
            messagePanel.add(getMessageLabel(), null);
        }

        return messagePanel;
    }
}


//  @jve:visual-info  decl-index=0 visual-constraint="56,-62"
//  @jve:visual-info  decl-index=0 visual-constraint="10,9"
