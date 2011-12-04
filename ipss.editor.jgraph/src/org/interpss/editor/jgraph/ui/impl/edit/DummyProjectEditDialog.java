 /*
  * @(#)DummyProjectEditDialog.java   
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

package org.interpss.editor.jgraph.ui.impl.edit;

import java.util.Vector;

import org.interpss.editor.jgraph.ui.edit.IFormDataDialog;
import org.interpss.editor.jgraph.ui.form.IGNetForm;
import org.interpss.editor.jgraph.ui.impl.form.DummyFormContainer;
import org.interpss.editor.jgraph.ui.impl.form.DummyNetForm;
import org.interpss.numeric.util.Number2String;
import org.interpss.spring.BasePluginSpringFactory;
import org.interpss.ui.SwingInputVerifyUtil;
import org.interpss.ui.WinUtilities;

import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.common.util.IpssLogger;
 
public class DummyProjectEditDialog extends javax.swing.JDialog implements IFormDataDialog {
	private static final long serialVersionUID = 1;

	private DummyFormContainer _netContainer = null;
    
    private boolean returnOk = false;
    
	/**
	 * @return Returns the returnOk.
	 */
	public boolean isReturnOk() {
		return returnOk;
	}

	/**
	 * @param returnOk The returnOk to set.
	 */
	private void setReturnOk(boolean returnOk) {
		this.returnOk = returnOk;
	}

	/**
	*	Constructor
	*
	* @parem parent the parent Frame object	
	*/
	public DummyProjectEditDialog(java.awt.Frame parent, IPSSMsgHub aMsgHub) {
        super(parent, "Project Data Editor", true);
        initComponents();
        WinUtilities.center(this);
		
        DataVerifier verifier = new DataVerifier();
		this.projNameTextField.setInputVerifier(verifier);
    	this.baseKvaField.setInputVerifier(verifier);
    	this.baseFreqField.setInputVerifier(verifier);
    }

	/**
	*	For performance reason, editor objects are static member of the 
	*   CellEditorFactory. This method is for init the editor for the form
	* 	object to be edtied
	*
	* @param netContainer the NetContainer object
	* @param obj the ProjectSelect object
	*/    
	public void init(Object netContainer, Object obj) {
		IpssLogger.getLogger().info("NBProjectEditDialog.init() called");
		_netContainer = (DummyFormContainer)netContainer;
		DummyNetForm form = (DummyNetForm)((DummyFormContainer)netContainer).getGNetForm();
		if (form.isNewState()) {
			_netContainer.initGNetForm(IGNetForm.NetType_AcscNetwork, IGNetForm.AppType_Distribution);
		}	
		
		// copy data from the form to the editor input panels, including 
		// child panels 
    	setForm2Editor();
		
		// pack the form and display
		pack();
        setVisible(true);
	}

	/**
	*	Set form data to the editor
	*
	* @return false if there is any problem
	*/	
    public boolean setForm2Editor() {
		IpssLogger.getLogger().info("NBProjectEditDialog.setForm2Editor() called");
		DummyNetForm form = (DummyNetForm)_netContainer.getGNetForm();

	    this.projNameTextField.setText(form.getName());
	    this.descTextArea.setText(form.getDescription());
	    this.baseKvaField.setText(Number2String.toStr(form.getBaseKVA(), "#.0"));
    	this.baseFreqField.setText(Number2String.toStr(form.getFreqHZ(), "#.0"));
    	
		pack();
		return true;
    }
    
	/**
	*	Save editor screen data to the form
	*
	* @param errMsg error messages during the saving process.
	* @return false if there is any problem
	*/
	public boolean saveEditor2Form(Vector errMsg) throws Exception {
		IpssLogger.getLogger().info("NBProjectEditDialog.saveEditor2Form() called");
		DummyNetForm form = (DummyNetForm)_netContainer.getGNetForm();
		errMsg.clear();
		boolean ok = true;

		if (SwingInputVerifyUtil.isEmptyStr(this.projNameTextField)) {
			errMsg.add("Id field is empty");
			ok = false;
    	}
    	_netContainer.getGNetForm().setId(this.projNameTextField.getText());
    	
		if (!SwingInputVerifyUtil.largeThan(this.baseKvaField, 0.0d)) {
			errMsg.add("Base KVA <= 0.0");
			ok = false;
		}
    	form.setBaseKVA(SwingInputVerifyUtil.getDouble(this.baseKvaField));

		if (!SwingInputVerifyUtil.largeThan(this.baseFreqField, 0.0d)) {
			errMsg.add("Base Freq <= 0.0");
			ok = false;
		}
    	form.setFreqHZ(SwingInputVerifyUtil.getDouble(this.baseFreqField));
    	
		return ok;
    }
	
	// The following code is controlled by NetBean IDE
	// ===============================================
	
    /** Creates new form NBNetEditDialog */
    public DummyProjectEditDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        inputPanel = new javax.swing.JPanel();
        selectionPanel = new javax.swing.JPanel();
        projNameLabel = new javax.swing.JLabel();
        projNameTextField = new javax.swing.JTextField();
        descLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        descTextArea = new javax.swing.JTextArea();
        kvaPanel = new javax.swing.JPanel();
        baseKvaLabel = new javax.swing.JLabel();
        baseKvaField = new javax.swing.JTextField();
        kvaUnitLabel = new javax.swing.JLabel();
        baseFreqLabel = new javax.swing.JLabel();
        baseFreqField = new javax.swing.JTextField();
        fUnitLabel = new javax.swing.JLabel();
        controlPanel = new javax.swing.JPanel();
        saveButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.BorderLayout(10, 10));

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        inputPanel.setLayout(new java.awt.BorderLayout(5, 5));

        inputPanel.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(10, 20, 1, 20)));
        selectionPanel.setLayout(new java.awt.GridBagLayout());

        projNameLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        projNameLabel.setText("Project Name    ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        selectionPanel.add(projNameLabel, gridBagConstraints);

        projNameTextField.setColumns(15);
        projNameTextField.setName("projNameTextField");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        selectionPanel.add(projNameTextField, gridBagConstraints);

        descLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        descLabel.setText("Description    ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        selectionPanel.add(descLabel, gridBagConstraints);

        descTextArea.setColumns(30);
        descTextArea.setLineWrap(true);
        descTextArea.setRows(3);
        descTextArea.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153)));
        descTextArea.setName("descTextArea");
        jScrollPane1.setViewportView(descTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        selectionPanel.add(jScrollPane1, gridBagConstraints);

        baseKvaLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        baseKvaLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        baseKvaLabel.setText("BaseKVA   ");
        kvaPanel.add(baseKvaLabel);

        baseKvaField.setColumns(8);
        baseKvaField.setText("100000.0");
        baseKvaField.setName("baseKvaTextField");
        kvaPanel.add(baseKvaField);

        kvaUnitLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        kvaUnitLabel.setText("(kva)");
        kvaPanel.add(kvaUnitLabel);

        baseFreqLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        baseFreqLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        baseFreqLabel.setText("               BaseFreq");
        kvaPanel.add(baseFreqLabel);

        baseFreqField.setColumns(4);
        baseFreqField.setText("60.0");
        baseFreqField.setName("baseFreqTextField");
        kvaPanel.add(baseFreqField);

        fUnitLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        fUnitLabel.setText("(hz)");
        kvaPanel.add(fUnitLabel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        selectionPanel.add(kvaPanel, gridBagConstraints);

        inputPanel.add(selectionPanel, java.awt.BorderLayout.NORTH);

        getContentPane().add(inputPanel, java.awt.BorderLayout.CENTER);

        controlPanel.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 10, 1)));
        saveButton.setFont(new java.awt.Font("Dialog", 0, 12));
        saveButton.setText("Save");
        saveButton.setName("SaveButton");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveActionHandler(evt);
            }
        });

        controlPanel.add(saveButton);

        cancelButton.setFont(new java.awt.Font("Dialog", 0, 12));
        cancelButton.setText("Cancel");
        cancelButton.setName("CancelButton");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelActionHandler(evt);
            }
        });

        controlPanel.add(cancelButton);

        getContentPane().add(controlPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }
    // </editor-fold>//GEN-END:initComponents

    private void cancelActionHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelActionHandler
		setReturnOk(false);
        setVisible(false);
    }//GEN-LAST:event_cancelActionHandler

    private void saveActionHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveActionHandler
		Vector errMsg = new Vector();
		try {
        	if (!saveEditor2Form(errMsg)) {
        		IpssLogger.getLogger().severe(errMsg.toString());
        		BasePluginSpringFactory.getEditorDialogUtil().showMsgDialog("Network Data Error", errMsg);
				return;
        	}
        } catch (Exception e) {
      		IpssLogger.logErr(e);
			return;
        }	
        _netContainer.getGNetForm().setNewState(false);
        _netContainer.setDataDirty(true);
        
		setReturnOk(true);
        setVisible(false);
    }//GEN-LAST:event_saveActionHandler
    
    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
		setReturnOk(false);
        setVisible(false);
    }//GEN-LAST:event_closeDialog
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField baseFreqField;
    private javax.swing.JLabel baseFreqLabel;
    private javax.swing.JTextField baseKvaField;
    private javax.swing.JLabel baseKvaLabel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JLabel descLabel;
    private javax.swing.JTextArea descTextArea;
    private javax.swing.JLabel fUnitLabel;
    private javax.swing.JPanel inputPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel kvaPanel;
    private javax.swing.JLabel kvaUnitLabel;
    private javax.swing.JLabel projNameLabel;
    private javax.swing.JTextField projNameTextField;
    private javax.swing.JButton saveButton;
    private javax.swing.JPanel selectionPanel;
    // End of variables declaration//GEN-END:variables

	class DataVerifier extends javax.swing.InputVerifier {
    	public boolean verify(javax.swing.JComponent input) {
			try {
				if (input == null)
					return false;
				if (input == baseKvaField ||
		   		   	input == baseFreqField)
		    		return SwingInputVerifyUtil.largeThan((javax.swing.JTextField)input, 0.0d);
				if (input == projNameTextField)
	 	       		return !SwingInputVerifyUtil.isEmptyStr((javax.swing.JTextField)input);
 	       	} catch (Exception e) {
				return false;
 	       	}				
			return true;
        }
    }
}
