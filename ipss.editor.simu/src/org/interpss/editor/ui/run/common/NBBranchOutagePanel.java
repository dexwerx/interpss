 /*
  * @(#)NBFaultLocDataPanel.java   
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

package org.interpss.editor.ui.run.common;

import java.util.Vector;

import org.ieee.odm.schema.AcscBranchFaultXmlType;
import org.ieee.odm.schema.AcscFaultCategoryEnumType;
import org.ieee.odm.schema.DynamicEventXmlType;
import org.interpss.editor.form.GFormContainer;
import org.interpss.editor.jgraph.ui.edit.IFormDataPanel;
import org.interpss.numeric.util.Number2String;
import org.interpss.ui.SwingInputVerifyUtil;

import com.interpss.common.util.IpssLogger;
import com.interpss.simu.SimuContext;
import com.interpss.simu.util.SimuCtxHelper;

public class NBBranchOutagePanel extends javax.swing.JPanel implements IFormDataPanel{
	private static final long serialVersionUID = 1;

	private GFormContainer _netContainer = null;
    private SimuContext _simuCtx = null;	
	//private DStabDEventData _eventData = null;  // current event data
	
	private DynamicEventXmlType xmlEventData = null;  // current event data

	/** Creates new form FaultLocDataPanel */
    public NBBranchOutagePanel() {
        initComponents();

        DataVerifier verifier = new DataVerifier();
        stratTimeTextField.setInputVerifier(verifier);
    }
    
	public void init(Object netContainer, Object simuCtx) {
		// for non-graph file, netContainer = null
		IpssLogger.getLogger().info("NBFaultLocDataPanel init() called");

		_netContainer = (GFormContainer)netContainer;
	    _simuCtx = (SimuContext)simuCtx;
      
    	if (_netContainer != null) {
    		Object[] branchNameId = _netContainer.getBranchNameIdArrayNoXfr(_netContainer.getGNetForm().getAppType());
    		if (branchNameId.length > 0)
    			this.faultBranchComboBox.setModel(new javax.swing.DefaultComboBoxModel(branchNameId));
    	}
    	else {
    		Object[] branchNameId = SimuCtxHelper.getBranchNameIdArrayNoXfr(_simuCtx);
    		if (branchNameId.length > 0)
    			this.faultBranchComboBox.setModel(new javax.swing.DefaultComboBoxModel(branchNameId));
    	}
	}
	
	public void setDStabDEventData(DynamicEventXmlType data) {
		xmlEventData = data;
	}
	
	/**
	*	Set form data to the editor
	*
	* @return false if there is any problem
	*/
    public boolean setForm2Editor() {
		IpssLogger.getLogger().info("NBBranchOutagePanel setForm2Editor() called");
		
		AcscBranchFaultXmlType braFault = (AcscBranchFaultXmlType)xmlEventData.getFault();

        if (braFault.getRefBranch().getBranchId().equals("")) {
            IpssLogger.getLogger().info("faultBranchComboBox.getSelectedItem() -> " + this.faultBranchComboBox.getSelectedItem());
            this.faultBranchComboBox.setSelectedIndex(0);
        }    
        else
             this.faultBranchComboBox.setSelectedItem(braFault.getRefBranch().getBranchId());
        
        stratTimeTextField.setText(Number2String.toStr(xmlEventData.getStartTime().getValue(), "#0.000"));   
        
		if (xmlEventData.getFault().getFaultCategory() == AcscFaultCategoryEnumType.OUTAGE_3_PHASE)
			branchOutage3PRadioButton.setSelected(true);
		else if (xmlEventData.getFault().getFaultCategory() == AcscFaultCategoryEnumType.OUTAGE_1_PHASE)
			branchOutage1PRadioButton.setSelected(true);
		else if (xmlEventData.getFault().getFaultCategory() == AcscFaultCategoryEnumType.OUTAGE_2_PHASE)
			branchOutage2PRadioButton.setSelected(true);

		return true;
	}
    
	/**
	*	Save editor screen data to the form
	*
	* @param errMsg error messages during the saving process.
	* @return false if there is any problem
	*/
    public boolean saveEditor2Form(Vector<String> errMsg) throws Exception {
		IpssLogger.getLogger().info("NBBranchOutagePanel saveEditor2Form() called");

		AcscBranchFaultXmlType braFault = (AcscBranchFaultXmlType)xmlEventData.getFault();
		
		if (SwingInputVerifyUtil.largeThan(this.stratTimeTextField, 0.0d,
						errMsg, "Branch outage start time < 0.0"))
			xmlEventData.getStartTime().setValue(SwingInputVerifyUtil.getDouble(this.stratTimeTextField));

		braFault.getRefBranch().setBranchId((String)this.faultBranchComboBox.getSelectedItem());
		if (branchOutage3PRadioButton.isSelected())
			xmlEventData.getFault().setFaultCategory(AcscFaultCategoryEnumType.OUTAGE_3_PHASE);
		else if (branchOutage1PRadioButton.isSelected())
			xmlEventData.getFault().setFaultCategory(AcscFaultCategoryEnumType.OUTAGE_1_PHASE);
		else if (branchOutage2PRadioButton.isSelected())
			xmlEventData.getFault().setFaultCategory(AcscFaultCategoryEnumType.OUTAGE_2_PHASE);

		return errMsg.size() == 0;
	}
    
	/** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        outageButtonGroup = new javax.swing.ButtonGroup();
        faultBranchComboBox = new javax.swing.JComboBox();
        faultBranchLabel = new javax.swing.JLabel();
        branchOutage3PRadioButton = new javax.swing.JRadioButton();
        branchOutage1PRadioButton = new javax.swing.JRadioButton();
        branchOutage2PRadioButton = new javax.swing.JRadioButton();
        startTimeLabel = new javax.swing.JLabel();
        stratTimeTextField = new javax.swing.JTextField();

        faultBranchComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        faultBranchComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Branch id list" }));
        faultBranchComboBox.setName("faultBranchComboBox"); // NOI18N

        faultBranchLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        faultBranchLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        faultBranchLabel.setText("Outage Branch     ");
        faultBranchLabel.setPreferredSize(new java.awt.Dimension(90, 25));

        outageButtonGroup.add(branchOutage3PRadioButton);
        branchOutage3PRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        branchOutage3PRadioButton.setSelected(true);
        branchOutage3PRadioButton.setText("Three-Phase");
        branchOutage3PRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        branchOutage3PRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        outageButtonGroup.add(branchOutage1PRadioButton);
        branchOutage1PRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        branchOutage1PRadioButton.setText("Single-Phase");
        branchOutage1PRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        branchOutage1PRadioButton.setEnabled(false);
        branchOutage1PRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        outageButtonGroup.add(branchOutage2PRadioButton);
        branchOutage2PRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        branchOutage2PRadioButton.setText("Double-Phase");
        branchOutage2PRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        branchOutage2PRadioButton.setEnabled(false);
        branchOutage2PRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        startTimeLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        startTimeLabel.setText("          Start Time(sec)   ");

        stratTimeTextField.setColumns(4);
        stratTimeTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        stratTimeTextField.setText("0.0");
        stratTimeTextField.setDragEnabled(true);
        stratTimeTextField.setName("stratTimeTextField"); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(52, 52, 52)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(branchOutage3PRadioButton)
                                .add(45, 45, 45)
                                .add(branchOutage1PRadioButton)
                                .add(32, 32, 32)
                                .add(branchOutage2PRadioButton))
                            .add(layout.createSequentialGroup()
                                .add(faultBranchLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 111, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(faultBranchComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 192, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                    .add(layout.createSequentialGroup()
                        .add(114, 114, 114)
                        .add(startTimeLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(stratTimeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(63, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap(35, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(stratTimeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(startTimeLabel))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(faultBranchLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(faultBranchComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(branchOutage2PRadioButton)
                    .add(branchOutage1PRadioButton)
                    .add(branchOutage3PRadioButton))
                .add(23, 23, 23))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton branchOutage1PRadioButton;
    private javax.swing.JRadioButton branchOutage2PRadioButton;
    private javax.swing.JRadioButton branchOutage3PRadioButton;
    private javax.swing.JComboBox faultBranchComboBox;
    private javax.swing.JLabel faultBranchLabel;
    private javax.swing.ButtonGroup outageButtonGroup;
    private javax.swing.JLabel startTimeLabel;
    private javax.swing.JTextField stratTimeTextField;
    // End of variables declaration//GEN-END:variables
    
	class DataVerifier extends javax.swing.InputVerifier {
		public boolean verify(javax.swing.JComponent input) {
			if (input == null)
				return false;
			try {
       			if (input == stratTimeTextField )
     	       			return SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) >= 0.0;
			} catch (Exception e) {
				return false;
			}		
			return true;
		}
	}
    
}
