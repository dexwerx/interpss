 /*
  * @(#)NBStatusPanel.java   
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

package org.interpss.editor.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JToggleButton;
import javax.swing.border.BevelBorder;

import org.interpss.editor.jgraph.ui.app.IAppStatus;

import com.interpss.common.exp.InterpssRuntimeException;
import com.interpss.common.msg.IpssMessage;
import com.interpss.common.msg.IpssMsgListener;
import com.interpss.common.msg.SimuMessage;

public class NBStatusPanel extends JPanel implements IAppStatus, IpssMsgListener {
	private static final long serialVersionUID = 1;

	/** contains the message at the current
	 *  Status bar
	 */
	protected JLabel message;
	
	/** contains the scale for the current
	 *  graph
	 */
	protected JLabel scale;
	
    private JLabel progressLabel;

    private JLabel statusLabel;
	
    private JProgressBar progressBar;
    private JPanel progressPanel;
    private JToggleButton runToggleButton;

    private JPanel graphPanel;
	
    public static int ProcessBarMax = 10;
    public static int ProcessBarMin = 1;
    
    private Timer timer = null;
    
    // we do not need console concept anymore
    //private IOutputTextDialog console = null;
    //private boolean autoClose = false;
    
    private int cnt = 0;
	
	private boolean busy = false;
	private String  progressMsg = "";
	
	
	/**
	 * Constructor for StatusBar.
	 *
	 */
	public NBStatusPanel() {
		super();
		initComponents();
    	progressLabel.setText("");
        progressBar.setMinimum(ProcessBarMin);
        progressBar.setMaximum(ProcessBarMax);
	}
	/**
	 * Returns the message.
	 *
	 * @return The message from the status bar
	 */
	public String getMessage() {
		return message.getText() ;
	}
	
	/**
	 * Returns the scale.
	 * @return JLabel
	 */
	public String getScale() {
		return scale.getText() ;
	}
	
	/**
	 * Sets the message.
	 * @param message The message to set
	 */
	public void setMessage(String message) {
		this.message.setText(message);
	}
	
	/**
	 * Sets the scale.
	 * @param scale The scale to set
	 */
	public void setScale(String scale) {
		this.scale.setText(scale);
	}
	
    public JLabel getStatusLabel() {
    	return statusLabel;
    }
    
    /*
    public IOutputTextDialog getConsole() {
    	return this.console;
    }
    */
    public void busyStart(long period, String aStatusLabel, String progLabel) {
    	progressBar.setEnabled(true);
    	progressLabel.setText(progLabel+"  ");
    	statusLabel.setText(aStatusLabel);
    	runToggleButton.setEnabled(true);
    	this.busy = true;
    	this.progressMsg = progLabel;
    	timer = new Timer();
    	timer.schedule(
    			new TimerTask() {
    				public void run() {
    					updateProgressBar();
    				}
    			}, 0, period);
    }
    
    public void busyStop(String aStatusLabel) {
    	timer.cancel();
    	this.busy = false;
    	progressLabel.setText("");
   		progressBar.setValue(ProcessBarMin);
    	progressBar.setEnabled(false);
    	statusLabel.setText(aStatusLabel);
    	runToggleButton.setEnabled(false);
    	cnt = 0;
    	/*
    	if (console != null && autoClose) {
    		((JDialog)console).setVisible(false);
    		((JDialog)console).dispose();
    	}
    	*/
    }
    
    public boolean isBusy() {
    	return this.busy;
    }

    public void setPosition(int percent) {
    	progressLabel.setText(this.progressMsg + " (" + percent + "%) ");
    }
	
    public String getBusyMsg() {
    	return this.progressMsg;
    }
    
    private void updateProgressBar() {
   		cnt++;
   		if (cnt > ProcessBarMax)
   			cnt = ProcessBarMin;
   		//IpssLogger.getLogger().info("progress bar cnt: " + cnt);
   		progressBar.setValue(cnt);
    }
    
	private void runToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {
		if (JOptionPane.showConfirmDialog(
				null, 
				"Do you want to stop the project running indicator?", 
				"Stop Runninng Indicator", JOptionPane.YES_NO_OPTION) 
			== JOptionPane.YES_OPTION)
			busyStop("Stoped by User");
	}
    
    private void initComponents() {
    	
		setBorder(new BevelBorder(BevelBorder.LOWERED));
		setLayout(new BorderLayout());

		progressPanel = new JPanel();
		progressPanel.setLayout(new FlowLayout());
//		progressPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		add(progressPanel, BorderLayout.EAST);

        progressLabel = new JLabel("progress string");
        progressLabel.setFont(new java.awt.Font("Dialog", 0, 10));
		progressPanel.add(progressLabel);

        progressBar = new JProgressBar();
        progressBar.setEnabled(false);
        progressBar.setMinimumSize(new java.awt.Dimension(50, 8));
        progressBar.setPreferredSize(new java.awt.Dimension(50, 16));
		progressPanel.add(progressBar);

        runToggleButton = new JToggleButton();
		progressPanel.add(runToggleButton);
        runToggleButton.setEnabled(false);
        runToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/interpss/editor/resources/runActive.jpg"))); // NOI18N
        runToggleButton.setDisabledSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/interpss/editor/resources/runStopped.jpg"))); // NOI18N
        runToggleButton.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/org/interpss/editor/resources/runStopped.jpg"))); // NOI18N
        runToggleButton.setPreferredSize(new java.awt.Dimension(16, 16));
        runToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	runToggleButtonActionPerformed(evt);
            }
        });
        
		graphPanel = new JPanel();
		graphPanel.setLayout(new BorderLayout());
		add(graphPanel, BorderLayout.WEST);

		message = new JLabel("Ready.");
		message.setFont(new java.awt.Font("Dialog", 0, 10));
		message.setBorder(new BevelBorder(BevelBorder.LOWERED));
		graphPanel.add(message);

		scale = new JLabel("100%");
		scale.setFont(new java.awt.Font("Dialog", 0, 10));
		graphPanel.add(scale, BorderLayout.EAST);
		scale.setBorder(new BevelBorder(BevelBorder.LOWERED));

		statusLabel = new JLabel("Status: ");
		statusLabel.setFont(new java.awt.Font("Dialog", 0, 10));
		statusLabel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		add(statusLabel, BorderLayout.CENTER);
    }
	
	/**
	 * Implementation of the onMsgEvent method.
	 * 
	 * @param msg the msg object
	 */
	public void onMsgEvent(IpssMessage msg) {
		if (msg instanceof SimuMessage) {
			if (msg.getType() == SimuMessage.TYPE_PROGRESS_STATUS) {
				SimuMessage simuMsg = (SimuMessage)msg;
			    setPosition(simuMsg.getIntData());
			}
		}
	}	
	
	public boolean onMsgEventStatus(IpssMessage msg) {
		throw new InterpssRuntimeException("Method not implemented");
	}	
}
