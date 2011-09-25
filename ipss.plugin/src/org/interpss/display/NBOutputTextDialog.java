 /*
  * @(#)NBOutputTextDialog.java   
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

package org.interpss.display;

import java.awt.Frame;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JFileChooser;

import org.interpss.dstab.output.DStabOutFunc;
import org.interpss.dstab.output.DStabSimuDBRecord;
import org.interpss.editor.jgraph.ui.IGraphicEditor;
import org.interpss.editor.ui.IOutputTextDialog;
import org.interpss.editor.ui.UISpringAppContext;
import org.interpss.editor.ui.util.GUIFileUtil;
import org.interpss.editor.ui.util.IpssFileFilter;
import org.interpss.ui.WinUtilities;

import com.interpss.QA.QAObjectFactory;
import com.interpss.QA.rfile.QAFileReader;
import com.interpss.QA.rfile.IQAFileProcessor;
import com.interpss.common.exp.InvalidOperationException;
import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.common.msg.IpssMessage;
import com.interpss.common.msg.SimuMessage;
import com.interpss.common.util.IpssLogger;
import com.interpss.common.util.StringUtil;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.dist.DistNetwork;
import com.interpss.dstab.algo.DynamicSimuAlgorithm;
import com.interpss.simu.multicase.MultiStudyCase;
import com.interpss.simu.multicase.aclf.ContingencyAnalysis;

public class NBOutputTextDialog extends javax.swing.JDialog implements IOutputTextDialog {
	private static final long serialVersionUID = 1;
	
	private IPSSMsgHub msg = null;
	private AclfNetwork aclfAdjNet = null;
	private Object data = null;

	private static JFileChooser saveTextFileChooser = null;

	/**
	*	Constructor for browser dialog
	*/		
    public NBOutputTextDialog(Frame parent, IPSSMsgHub aMsg) {
        super(parent, "", true);
        initDialog("");
        this.msg = aMsg;
    }
    
    public NBOutputTextDialog(IGraphicEditor parent, IPSSMsgHub aMsg) {
        this(parent.getFrame(), aMsg);
    }
    
    @Override
	public void display(File file) {
    	this.data = file;
        showDialog();
	}

    @Override
    public void disableFeature(String featureName) {
    	if (featureName.equals("busStyleRadioButton"))
    		this.busStyleRadioButton.setEnabled(false);
    	else if (featureName.equals("summaryRadioButton"))
    	    this.summaryRadioButton.setEnabled(false);
//        private javax.swing.JButton saveAsButton;
//        private javax.swing.JButton secMarginButton;
    }

	public void display(Object aData) {
    	this.data = aData;
        showDialog();
    }
	
    public void showDialog() {
        if (data instanceof File) {
  			this.textArea.setText("");
    		try {
    			final InputStream stream = new FileInputStream((File)data);
        		final BufferedReader din = new BufferedReader(new InputStreamReader(stream));
          		String str;
              	do {
              		str = din.readLine();
              		if (str != null)
              			this.textArea.append(str + "\n");
              	} while (str != null);
    		} catch (Exception e) {
            	textArea.setText("Error: " + e.toString());
    		}
        }
        else if (data instanceof DistNetwork) {
        	DistNetwork distNet = (DistNetwork)data;
        	textArea.setText(DistOutFunc.lfSummary(distNet, msg));
        }
        else if (data instanceof DynamicSimuAlgorithm) {
        	DynamicSimuAlgorithm algo = (DynamicSimuAlgorithm)data;
        	textArea.setText(DStabOutFunc.initConditionSummary(algo));
            busStyleRadioButton.setEnabled(false);
            summaryRadioButton.setEnabled(false);
        }
        else if (data instanceof AclfNetwork) {
        	aclfAdjNet = (AclfNetwork)data;
        	textArea.setText(AclfOutFunc.loadFlowSummary(aclfAdjNet));
            busStyleRadioButton.setEnabled(true);
            summaryRadioButton.setEnabled(true);
            summaryRadioButton.setSelected(true);
            this.compareButton.setEnabled(true);
        }
        else if (data instanceof MultiStudyCase) {
        	textArea.setText("");
        	if (data instanceof ContingencyAnalysis) {
        		secMarginButton.setEnabled(true);
/* TODO        		
        		IRemoteResult resultHandler = RemoteResultFactory.createHandler(ContingencyAnaysisJob.class);
        		textArea.append(resultHandler
        				.toString(IRemoteResult.DisplayType_SecViolation, (MultiStudyCase)data)
        				.toString());
        				*/
        	}
        	else {
/* TODO        		IRemoteResult resultHandler = RemoteResultFactory.createHandler(GridAclfJob.class);
        		textArea.append(resultHandler
        				.toString(IRemoteResult.DisplayType_NoUsed, (MultiStudyCase)data)
        				.toString());
        				*/
        	}
            busStyleRadioButton.setEnabled(false);
            summaryRadioButton.setEnabled(false);
        }
        // for DStab machine state output
        else if (data instanceof List) {
			appendText(DStabOutFunc.getStateTitleStr()+"\n");
    		List machRecList = (List)data;
        	for (int i = 0; i < machRecList.size(); i++) {
    			DStabSimuDBRecord machRec = (DStabSimuDBRecord)machRecList.get(i);
    			Hashtable machStates = StringUtil.parseStr2Hashtable(machRec.getSimuRec());
            	try {
    				appendText(DStabOutFunc.getStateStr(machStates));
    				IpssLogger.getLogger().fine(DStabOutFunc.getStateStr(machStates));
    			} catch (Exception ex) {
    				IpssLogger.logErr(ex);
    			}
        	}
		}
        else {
        	if (data != null)
        		textArea.setText(data.toString());
        }
        pack();
        setVisible(true);	
    }

    public void onMsgEvent(IpssMessage event) {
   		if (event instanceof SimuMessage) {
   			if (event.getType() == SimuMessage.TYPE_CONSOLE_MSG) {
   				appendText(((SimuMessage)event).getStringData()+"\n"); 
   			}	
   		}    	
    }

    public boolean onMsgEventStatus(IpssMessage aMsg) {
 	   throw new InvalidOperationException("Method not implemented");
    }    

    public NBOutputTextDialog(Frame parent, String title, boolean model) {
        super(parent, title, model);
        initDialog(title);
        pack();
        setVisible(true);	
	}
    
    private void initDialog(String title) {
        initComponents();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); 
        WinUtilities.center(this);
        textArea.setRows(UISpringAppContext.BrowserDialog_TextRows);
        textArea.setColumns(UISpringAppContext.BrowserDialog_TextColumns);
        busStyleRadioButton.setEnabled(false);
        summaryRadioButton.setEnabled(false);
        setTitle(title);

	}

    public void appendText(String text) {
		textArea.append(text);
	}

    public void clearTextArea() {
    	this.data = null;  // when clear the text area text, we assume that more text will writen to the area
		textArea.setText("");
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lfFormatButtonGroup = new javax.swing.ButtonGroup();
        textAreaPanel = new javax.swing.JPanel();
        textAreaScrollPane = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();
        controlPanel = new javax.swing.JPanel();
        lfFormatPanel = new javax.swing.JPanel();
        summaryRadioButton = new javax.swing.JRadioButton();
        busStyleRadioButton = new javax.swing.JRadioButton();
        saveAsButton = new javax.swing.JButton();
        secMarginButton = new javax.swing.JButton();
        compareButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();

        setTitle("");
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
			public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });
        getContentPane().setLayout(new java.awt.BorderLayout(5, 5));

        textAreaPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 20, 0, 20));
        textAreaPanel.setLayout(new java.awt.BorderLayout(5, 5));

        textArea.setColumns(120);
        textArea.setEditable(false);
        textArea.setFont(new java.awt.Font("Courier New", 0, 12));
        textArea.setRows(30);
        textAreaScrollPane.setViewportView(textArea);

        textAreaPanel.add(textAreaScrollPane, java.awt.BorderLayout.EAST);

        getContentPane().add(textAreaPanel, java.awt.BorderLayout.CENTER);

        controlPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 10, 1));
        controlPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 20, 10));

        lfFormatPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        lfFormatPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 5));

        lfFormatButtonGroup.add(summaryRadioButton);
        summaryRadioButton.setFont(new java.awt.Font("Dialog", 0, 10));
        summaryRadioButton.setSelected(true);
        summaryRadioButton.setText("LF Summay");
        summaryRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        summaryRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        summaryRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                summaryRadioButtonActionPerformed(evt);
            }
        });
        lfFormatPanel.add(summaryRadioButton);

        lfFormatButtonGroup.add(busStyleRadioButton);
        busStyleRadioButton.setFont(new java.awt.Font("Dialog", 0, 10));
        busStyleRadioButton.setText("Bus Style");
        busStyleRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        busStyleRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        busStyleRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                busStyleRadioButtonActionPerformed(evt);
            }
        });
        lfFormatPanel.add(busStyleRadioButton);

        controlPanel.add(lfFormatPanel);

        saveAsButton.setFont(new java.awt.Font("Dialog", 0, 12));
        saveAsButton.setText("SaveAs...");
        saveAsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsButtonActionPerformed(evt);
            }
        });
        controlPanel.add(saveAsButton);

        secMarginButton.setFont(new java.awt.Font("Dialog", 0, 12));
        secMarginButton.setText("SecMargin");
        secMarginButton.setEnabled(false);
        secMarginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                secMarginButtonActionPerformed(evt);
            }
        });
        controlPanel.add(secMarginButton);

        compareButton.setFont(new java.awt.Font("Dialog", 0, 12));
        compareButton.setText("Compare");
        compareButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	compareButtonActionPerformed(evt);
            }
        });
        compareButton.setEnabled(false);
        controlPanel.add(compareButton);

        closeButton.setFont(new java.awt.Font("Dialog", 0, 12));
        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        controlPanel.add(closeButton);

        getContentPane().add(controlPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void saveAsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsButtonActionPerformed
    	//IpssLogger.getLogger().info(textArea.getText());
    	JFileChooser fChooser = getSaveTextFileChooser();
    	int retValue = fChooser.showSaveDialog(this);
		if (retValue == JFileChooser.APPROVE_OPTION) {
			File file = fChooser.getSelectedFile();
			String filename = file.getPath();
			if (!filename.endsWith(".txt"))
				filename += ".txt";
		    IpssLogger.getLogger().info("Textarea text saved to file: " + filename);
		    GUIFileUtil.writeTextarea2FileAbsolutePath(filename, textArea);
		}    
	}//GEN-LAST:event_saveAsButtonActionPerformed
	
	private JFileChooser getSaveTextFileChooser() {
		if (saveTextFileChooser == null) {
			saveTextFileChooser = new JFileChooser();
			saveTextFileChooser.addChoosableFileFilter(new IpssFileFilter("txt", "Text File"));
			saveTextFileChooser.setCurrentDirectory(new File(IpssFileFilter.OUTPUT_DEFAULT_DIR));
		}
    	return saveTextFileChooser;
	}	
	
    private void busStyleRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_busStyleRadioButtonActionPerformed
    	textArea.setText(AclfOutFunc.lfResultsBusStyle(aclfAdjNet));
    }//GEN-LAST:event_busStyleRadioButtonActionPerformed

    private void summaryRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_summaryRadioButtonActionPerformed
    	textArea.setText(AclfOutFunc.loadFlowSummary(aclfAdjNet));
    }//GEN-LAST:event_summaryRadioButtonActionPerformed

    private void compareButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
    	JFileChooser fChooser = getCompareFileChooser();
    	int retValue = fChooser.showOpenDialog(this);
		if (retValue == JFileChooser.APPROVE_OPTION) {
			File file = fChooser.getSelectedFile();
			String filename = file.getPath();
		    IpssLogger.getLogger().info("Result file selected: " + filename);
		    IQAFileProcessor proc = null;
		    if (filename.endsWith("psse")) {
		    	proc = QAObjectFactory.createFileProcessor(aclfAdjNet, QAFileReader.Type.PSSEAclfResult);
		    }
		    else if (filename.endsWith("bpa") || filename.endsWith("pfo")) {
		    	proc = QAObjectFactory.createFileProcessor(aclfAdjNet, QAFileReader.Type.BPAAclfResult);
		    }
	    	if (proc != null) {
	    		new QAFileReader(filename).processFile(proc);	
	    		textArea.setText("Result comparison:\n" + 
	    				(proc.getErrMsgList().size() > 0?
	    						proc.getErrMsgList().toString() :
	    						"No mismatch of LF results found")	);
	    	}
		}    
    }//GEN-LAST:event_closeButtonActionPerformed

	private JFileChooser getCompareFileChooser() {
		if (saveTextFileChooser == null) {
			saveTextFileChooser = new JFileChooser();
			saveTextFileChooser.addChoosableFileFilter(new IpssFileFilter("psse", "PSS/E result file"));
			saveTextFileChooser.addChoosableFileFilter(new IpssFileFilter("bpa", "BPA result file"));
			saveTextFileChooser.addChoosableFileFilter(new IpssFileFilter("pfo", "BPA result file"));
			saveTextFileChooser.setCurrentDirectory(new File(IpssFileFilter.OUTPUT_DEFAULT_DIR));
		}
    	return saveTextFileChooser;
	}	

	private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        setVisible(false);
    }//GEN-LAST:event_closeButtonActionPerformed
     
    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
    }//GEN-LAST:event_closeDialog

    private void secMarginButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_secMarginButtonActionPerformed
		textArea.setText("");
/* TODO		
		IRemoteResult resultHandler = RemoteResultFactory.createHandler(ContingencyAnaysisJob.class);
		textArea.append(resultHandler
				.toString(IRemoteResult.DisplayType_SecAssessment, (MultiStudyCase)data)
				.toString());
				*/
    }//GEN-LAST:event_secMarginButtonActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton busStyleRadioButton;
    private javax.swing.JButton closeButton;
    private javax.swing.JButton compareButton;
    private javax.swing.JPanel controlPanel;
    private javax.swing.ButtonGroup lfFormatButtonGroup;
    private javax.swing.JPanel lfFormatPanel;
    private javax.swing.JButton saveAsButton;
    private javax.swing.JButton secMarginButton;
    private javax.swing.JRadioButton summaryRadioButton;
    private javax.swing.JTextArea textArea;
    private javax.swing.JPanel textAreaPanel;
    private javax.swing.JScrollPane textAreaScrollPane;
    // End of variables declaration//GEN-END:variables

}
