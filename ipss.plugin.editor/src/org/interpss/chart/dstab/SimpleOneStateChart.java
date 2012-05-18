 /*
  * @(#)SimpleOneStateChart.java   
  *
  * Copyright (C) 2006 www.interpss.org
  *
  * This program is free software; you can redistribute it and/or
  * modify it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE
  * as published by the Free Software Foundation; either version 2.1
  * of the License, or (at your option) any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  *
  * @Author Mike Zhou
  * @Version 1.0
  * @Date 09/15/2006
  * 
  *   Revision History
  *   ================
  *
  */

package org.interpss.chart.dstab;

import java.awt.Color;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

import org.interpss.ui.WinUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * Demo for {@link XYSeries}, where all the y values are the same.
 */
public class SimpleOneStateChart extends JDialog {
	private static final long serialVersionUID = 1;

	public static int Chart_Width = 800;
	public static int Chart_Height = 500;
	
	private String plotTitle = "Machine State";

	private double[] xDataAry;
	private String xLabel = "Time in Sec";

	private double[] yDataAry;
	private String   yDataLabel;
	private String   yLabel = "State";
	private final Color    yColor = Color.BLACK;
	
	private double autoRangeMinimumSize = 0.2;
	
    /**
     * A demonstration application showing an {@link XYSeries} where all the y-values are the same.
     *
     * @param parent 
     * @param model  
     * @param title  
     */
    public SimpleOneStateChart(java.awt.Frame parent, boolean modal, final String title) {
        super(parent, modal);
        setTitle("Transient Stability Curve Plotting");
        setPlotTitle(title);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}
    
    /**
     * After constructing the chart object properly, call this method to show the chart dialog box
     */
    public void showChart() {
        pack();
        WinUtilities.center(this);
        setVisible(true);
    }
    
    /**
     * Set chart plot data
     * 
     * @param yLabel
     * @param yDataLabel
     * @param xData
     * @param yData
     * @param autoRangeMinimumSize
     */
    public void setPlotData(final String yLabel, final String yDataLabel, 
    		final double[] xData, final double[] yData, double autoRangeMinimumSize) {
    	this.yLabel = yLabel;
    	this.yDataLabel = yDataLabel;
    	this.xDataAry = xData;
    	this.yDataAry = yData;
    	this.autoRangeMinimumSize = autoRangeMinimumSize;
    }
    
    /**
     * Set y-axis label
     * 
     * @param label
     */
	public void setYLabel(final String label) {
		yLabel = label;
	}

	/**
	 * Set x-axis label
	 * 
	 * @param label
	 */
	public void setXLabel(final String label) {
		xLabel = label;
	}
	
	/**
	 * set chart plot title
	 * 
	 * @param title plot title string
	 */
	public void setPlotTitle(final String title) {
		plotTitle = title;
	}

	/**
	 * Set chart plot height and width in pixel
	 * 
	 * @param h
	 * @param w
	 */
	public void setDimension(final int h, final int w) {
		Chart_Height = h;
		Chart_Width = w;
	}
	
	/**
	 * create a XYDataSet object
	 * 
	 * @param xData x-axis data points
	 * @param yData y-axis data points
	 * @param label y-axis data label
	 * @return
	 */
    public static XYDataset createXYDataSet(final double[] xData, final double[] yData, final String label) {
        final XYSeries series = new XYSeries(label);
        for (int i = 0; i < xData.length; i++) {
            series.add(xData[i], yData[i]);
        }
        return new XYSeriesCollection(series);
    }
        
    /**
     * create the chart based on the data attributes
     * 
     */
    public void createChart() {
        final JFreeChart chart = ChartFactory.createXYLineChart(
            	plotTitle,
                xLabel, 
                yLabel, 
                createXYDataSet(xDataAry, yDataAry, yDataLabel),
                PlotOrientation.VERTICAL,
                true,
                false,
                false
            );

        
        final XYPlot plot = (XYPlot) chart.getPlot();
        final StandardXYItemRenderer renderer = new StandardXYItemRenderer();
        renderer.setToolTipGenerator(new StandardXYToolTipGenerator());
        plot.setRenderer(renderer);        
        
        //NumberAxis axis_x = (NumberAxis) plot.getDomainAxis();
        //axis_x.setRangeAboutValue(12.0, 24.0);
        
        final NumberAxis axisLeft = (NumberAxis) plot.getRangeAxis();
        axisLeft.setAutoRangeIncludesZero(false);
        axisLeft.setAutoRangeMinimumSize(autoRangeMinimumSize);

        final XYItemRenderer v_renderer = plot.getRenderer(0);
        v_renderer.setSeriesPaint(0, yColor);

        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(Chart_Width, Chart_Height));
        setContentPane(chartPanel);
    }

    /**
     * Starting point for the demonstration application. Only for debug
     *
     * @param args  ignored.
     */
    public static void main(final String[] args) {
    	final SimpleOneStateChart plot = new SimpleOneStateChart(null, true, "Bus Load Schedule");
    	
    	final double[] xData = new double[100];
    	final double[] yData = new double[100];
    	for (int i = 0; i < 100; i++) {
    		xData[i] = 10.0 * (100-i) / 100.0;
    		yData[i] = i;
    	}
    	plot.setPlotData("Machine State", "Machine State Data lable", xData, yData, 0.2);
    	
    	plot.createChart();
    	plot.showChart();
    }
}
