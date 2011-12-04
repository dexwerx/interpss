 /*
  * @(#)NBAcscCasePanel.java   
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

package org.interpss.editor.ui.run;

import java.util.Vector;

import javax.swing.JDialog;

import org.interpss.editor.app.AppSimuContextImpl;
import org.interpss.editor.jgraph.ui.edit.IFormDataPanel;
import org.interpss.editor.ui.run.common.NBFaultLocDataPanel;
import org.interpss.numeric.util.Number2String;
import org.interpss.spring.BasePluginSpringFactory;
import org.interpss.spring.EditorSimuSpringFactory;
import org.interpss.ui.SwingInputVerifyUtil;
import org.interpss.xml.schema.AcscFaultDataType;
import org.interpss.xml.schema.AcscStudyCaseXmlType;
import org.interpss.xml.schema.BusAcscInitVoltDataType;

import com.interpss.common.util.IpssLogger;

public class NBAcscCasePanel extends javax.swing.JPanel implements IFormDataPanel {
	private static final long serialVersionUID = 1;

//	private AcscCaseData _caseData = null;
	private AppSimuContextImpl _appCtx = null;
	
    private AcscStudyCaseXmlType xmlCaseData;
	
	private NBFaultLocDataPanel _faultLocDataPanel = new NBFaultLocDataPanel();
	private JDialog parentDialog = null;
    
    /** Creates new form NBCaseInfoDialog */
    public NBAcscCasePanel(JDialog parent) {
    	this.parentDialog = parent;
        initComponents();

        faultLocPanel.add(_faultLocDataPanel);
        
        DataVerifier verifier = new DataVerifier();
		this.mFactorTextField.setInputVerifier(verifier);
    }
    
    public void init(Object netContainer, Object appCtx) {
		IpssLogger.getLogger().info("NBAcscCasePanel init() called");
	    _appCtx = (AppSimuContextImpl)appCtx;
	    
        _faultLocDataPanel.init(netContainer, _appCtx.getSimuCtx());
        _faultLocDataPanel.setBranchReclosureStatus(false);
    }

    /*
    public void setCaseData(AcscCaseData data) {
    	_caseData = data;
        _faultLocDataPanel.setFaultData(data.getFaultData());
    }
    */
    
	/**
	 * this function might be called by setForm2Edtior (saveData = false) or 
	 * saveEditor2Form (saveData = true))
	 */
    public void setXmlCaseDatax(AcscStudyCaseXmlType data, boolean saveData) {
    	this.xmlCaseData = data;
        _faultLocDataPanel.setFaultData(data.getFaultData(), saveData);
    }
    
	/**
	*	Set form data to the editor
	*
	* @return false if there is any problem
	*/
	public boolean setForm2Editor() {
		IpssLogger.getLogger().info("NBAcscCasePanel setForm2Editor() called");

		if (xmlCaseData.getFaultData().getFaultType() == AcscFaultDataType.BUS_FAULT) {
            this.busFaultRadioButton.setSelected(true);
            // refresh the fault data editing pandel
            busFaultRadioButtonActionPerformed(null);
        }	
        else{
            this.branchFaultRadioButton.setSelected(true);
            branchFaultRadioButtonActionPerformed(null);
        }
		
        if (xmlCaseData.getBusAcscInitVolt() == BusAcscInitVoltDataType.UNIT_VOLT) {
            this.fixedVoltRadioButton.setSelected(true);
            this.mFactorLabel.setEnabled(true);
            this.mFactorTextField.setEnabled(true);
            this.mFactorTextField.setText(Number2String.toStr(xmlCaseData.getMultiFactor(), "#0.##"));
        } 
        else
            this.loadflowVoltRadioButton.setSelected(true);

        _faultLocDataPanel.setForm2Editor();

        return true;
	}
    
	/**
	*	Save editor screen data to the form
	*
	* @param errMsg error messages during the saving process.
	* @return false if there is any problem
	*/
	public boolean saveEditor2Form(Vector<String> errMsg) throws Exception {
		IpssLogger.getLogger().info("NBAcscCasePanel saveEditor2Form() called");

		if (this.busFaultRadioButton.isSelected()) 
			xmlCaseData.getFaultData().setFaultType(AcscFaultDataType.BUS_FAULT);
	    else 
	    	xmlCaseData.getFaultData().setFaultType(AcscFaultDataType.BRANCH_FAULT);
			
		if (this.fixedVoltRadioButton.isSelected()) {
			xmlCaseData.setBusAcscInitVolt(BusAcscInitVoltDataType.UNIT_VOLT);
			if (!SwingInputVerifyUtil.largeThan(this.mFactorTextField, 50.0d) || 
				 SwingInputVerifyUtil.largeThan(this.mFactorTextField, 150.0d)) {
				errMsg.add("Prefault bus voltage multiplying factor out of range < 50% or > 150%");
			}
			xmlCaseData.setMultiFactor(SwingInputVerifyUtil.getDouble(this.mFactorTextField));
	    }
	    else
	    	xmlCaseData.setBusAcscInitVolt(BusAcscInitVoltDataType.LOADFLOW_VOLT);

        _faultLocDataPanel.saveEditor2Form(errMsg);

		return errMsg.size() == 0;
	}
    

    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        faultTypeButtonGroup = new javax.swing.ButtonGroup();
        initVoltButtonGroup = new javax.swing.ButtonGroup();
        faultTypePanel = new javax.swing.JPanel();
        busFaultRadioButton = new javax.swing.JRadioButton();
        branchFaultRadioButton = new javax.swing.JRadioButton();
        faultLocPanel = new javax.swing.JPanel();
        initVoltPanel = new javax.swing.JPanel();
        loadflowVoltRadioButton = new javax.swing.JRadioButton();
        fixedVoltRadioButton = new javax.swing.JRadioButton();
        mfactorPanel = new javax.swing.JPanel();
        mFactorLabel = new javax.swing.JLabel();
        mFactorTextField = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

        faultTypePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Fault Type", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10)));

        faultTypeButtonGroup.add(busFaultRadioButton);
        busFaultRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        busFaultRadioButton.setSelected(true);
        busFaultRadioButton.setText("Bus Fault");
        busFaultRadioButton.setName("busFaultRadioButton"); // NOI18N
        busFaultRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                busFaultRadioButtonActionPerformed(evt);
            }
        });
        faultTypePanel.add(busFaultRadioButton);

        faultTypeButtonGroup.add(branchFaultRadioButton);
        branchFaultRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        branchFaultRadioButton.setText("Branch Fault");
        branchFaultRadioButton.setName("branchFaultRadioButton"); // NOI18N
        branchFaultRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                branchFaultRadioButtonActionPerformed(evt);
            }
        });
        faultTypePanel.add(branchFaultRadioButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 20);
        add(faultTypePanel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(10, 30, 10, 30);
        add(faultLocPanel, gridBagConstraints);

        initVoltPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Prefault Bus Voltage", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10)));
        initVoltPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 20, 5));

        initVoltButtonGroup.add(loadflowVoltRadioButton);
        loadflowVoltRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        loadflowVoltRadioButton.setSelected(true);
        loadflowVoltRadioButton.setText("Loadflow");
        loadflowVoltRadioButton.setName("loadflowVoltRadioButton"); // NOI18N
        loadflowVoltRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadflowVoltRadioButtonActionPerformed(evt);
            }
        });
        initVoltPanel.add(loadflowVoltRadioButton);

        initVoltButtonGroup.add(fixedVoltRadioButton);
        fixedVoltRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        fixedVoltRadioButton.setText("Fixed");
        fixedVoltRadioButton.setName("fixedVoltRadioButton"); // NOI18N
        fixedVoltRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fixedVoltRadioButtonActionPerformed(evt);
            }
        });
        initVoltPanel.add(fixedVoltRadioButton);

        mFactorLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        mFactorLabel.setText("MFactor(%)");
        mFactorLabel.setEnabled(false);
        mfactorPanel.add(mFactorLabel);

        mFactorTextField.setColumns(4);
        mFactorTextField.setText("100.0");
        mFactorTextField.setEnabled(false);
        mFactorTextField.setName("mFactorTextField"); // NOI18N
        mfactorPanel.add(mFactorTextField);

        initVoltPanel.add(mfactorPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        add(initVoltPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void branchFaultRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_branchFaultRadioButtonActionPerformed
    	IpssLogger.getLogger().info("Branch Fault Type selected");
    	xmlCaseData.getFaultData().setFaultType(AcscFaultDataType.BRANCH_FAULT);
    	// refresh the fault data editing screen, which is depending on the caseData.faulData object
    	_faultLocDataPanel.setBusBranchFaultPanel();
    	EditorSimuSpringFactory.getCaseInfoDialog().pack();
    }//GEN-LAST:event_branchFaultRadioButtonActionPerformed

    private void busFaultRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_busFaultRadioButtonActionPerformed
    	IpssLogger.getLogger().info("Bus Fault Type selected");
    	xmlCaseData.getFaultData().setFaultType(AcscFaultDataType.BUS_FAULT);
    	// refresh the fault data editing screen, which is depending on the caseData.faulData object
    	_faultLocDataPanel.setBusBranchFaultPanel();
    	EditorSimuSpringFactory.getCaseInfoDialog().pack();
    }//GEN-LAST:event_busFaultRadioButtonActionPerformed

    private void fixedVoltRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fixedVoltRadioButtonActionPerformed
        this.mFactorLabel.setEnabled(true);
        this.mFactorTextField.setEnabled(true);
    }//GEN-LAST:event_fixedVoltRadioButtonActionPerformed

    private void loadflowVoltRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadflowVoltRadioButtonActionPerformed
		if (_appCtx.isLfConverged()) {
	        this.mFactorLabel.setEnabled(false);
	        this.mFactorTextField.setEnabled(false);
		}
		else {
			BasePluginSpringFactory.getEditorDialogUtil().showMsgDialog(parentDialog, "Warning", 
					"Loadflow not run yet. Please run load flow analysis first");
	        this.mFactorLabel.setEnabled(true);
	        this.mFactorTextField.setEnabled(true);
	        fixedVoltRadioButton.setSelected(true);
		}	
	}//GEN-LAST:event_loadflowVoltRadioButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton branchFaultRadioButton;
    private javax.swing.JRadioButton busFaultRadioButton;
    private javax.swing.JPanel faultLocPanel;
    private javax.swing.ButtonGroup faultTypeButtonGroup;
    private javax.swing.JPanel faultTypePanel;
    private javax.swing.JRadioButton fixedVoltRadioButton;
    private javax.swing.ButtonGroup initVoltButtonGroup;
    private javax.swing.JPanel initVoltPanel;
    private javax.swing.JRadioButton loadflowVoltRadioButton;
    private javax.swing.JLabel mFactorLabel;
    private javax.swing.JTextField mFactorTextField;
    private javax.swing.JPanel mfactorPanel;
    // End of variables declaration//GEN-END:variables
    
	class DataVerifier extends javax.swing.InputVerifier {
		public boolean verify(javax.swing.JComponent input) {
			if (input == null)
				return false;
			try {
       			if (input == mFactorTextField )
 	       			return SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) >= 0.0;
			} catch (Exception e) {
				return false;
			}		
			return true;
		}
	}
}
