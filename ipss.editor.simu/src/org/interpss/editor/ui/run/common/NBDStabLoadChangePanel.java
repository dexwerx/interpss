 /*
  * @(#)NBDStabLoadChangePanel.java   
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

import org.ieee.odm.schema.DStabLoadChangeEnumType;
import org.ieee.odm.schema.DStabLoadChangeXmlType;
import org.interpss.editor.form.GFormContainer;
import org.interpss.editor.jgraph.ui.edit.IFormDataPanel;
import org.interpss.numeric.util.Number2String;
import org.interpss.ui.SwingInputVerifyUtil;

import com.interpss.common.util.IpssLogger;
import com.interpss.simu.SimuContext;
import com.interpss.simu.util.SimuCtxHelper;

/**
 *
 * @author  mzhou
 */

public class NBDStabLoadChangePanel extends javax.swing.JPanel implements IFormDataPanel{
	private static final long serialVersionUID = 1;

	private GFormContainer _netContainer = null;
	private SimuContext    _simuCtx = null;
	private DStabLoadChangeXmlType xmlLoadChangeData = null;
	
    /** Creates new form FaultLocDataPanel */
    public NBDStabLoadChangePanel() {
        initComponents();

  		DataVerifier verifier = new DataVerifier();
      	this.changeFactorTextField.setInputVerifier(verifier);
    }
    
	public void init(Object netContainer, Object simuCtx) {
		IpssLogger.getLogger().info("NBDStabLoadChangePanel init() called");

		_netContainer = (GFormContainer)netContainer;
		_simuCtx = (SimuContext)simuCtx;
      
    	if (_netContainer != null)
    		this.loadBusComboBox.setModel(new javax.swing.DefaultComboBoxModel(
    				_netContainer.getLoadBusNameIdArray()));
    	else	
    		this.loadBusComboBox.setModel(new javax.swing.DefaultComboBoxModel(
    				SimuCtxHelper.getLoadBusNameIdArray(_simuCtx)));
	}
	
	public void setLoadChangeData(DStabLoadChangeXmlType data) {
		xmlLoadChangeData = data;
	}
	
	/**
	*	Set form data to the editor
	*
	* @return false if there is any problem
	*/
    public boolean setForm2Editor() {
		IpssLogger.getLogger().info("NBDStabLoadChangePanel setForm2Editor() called");

        if (xmlLoadChangeData.getRefBus().getBusId().equals("")) {
            IpssLogger.getLogger().info("loadBusComboBox.getSelectedItem() -> " + loadBusComboBox.getSelectedItem());
            this.loadBusComboBox.setSelectedIndex(0);
        }    
        else
             this.loadBusComboBox.setSelectedItem(xmlLoadChangeData.getRefBus().getBusId());
        
       	changeFactorTextField.setText(Number2String.toStr(xmlLoadChangeData.getChangeFactor().getFactor(), "#0.0"));   

       	if (xmlLoadChangeData.getLoadChangeType() == DStabLoadChangeEnumType.LOW_FREQUENCY) {
       		lowfreqRadioButton.setSelected(true);
       		threshholdTextField.setText(Number2String.toStr(xmlLoadChangeData.getThreshhold(), "#0.000"));   
           	delaySecTextField.setText(Number2String.toStr(xmlLoadChangeData.getDelayTime(), "#0.000"));   
       	}
       	else if (xmlLoadChangeData.getLoadChangeType() == DStabLoadChangeEnumType.LOW_VOLTAGE) {
       		lowVoltRadioButton.setSelected(true);
       		threshholdTextField.setText(Number2String.toStr(xmlLoadChangeData.getThreshhold(), "#0.000"));   
           	delaySecTextField.setText(Number2String.toStr(xmlLoadChangeData.getDelayTime(), "#0.000"));   
       	}
       	else {
       		fixedTimeRadioButton.setSelected(true);
       		threshholdTextField.setText(Number2String.toStr(xmlLoadChangeData.getThreshhold(), "#0.000"));   
       	}

       	return true;
	}
    
	/**
	*	Save editor screen data to the form
	*
	* @param errMsg error messages during the saving process.
	* @return false if there is any problem
	*/
    public boolean saveEditor2Form(Vector<String> errMsg) throws Exception {
		IpssLogger.getLogger().info("NBDStabLoadChangePanel saveEditor2Form() called");

		xmlLoadChangeData.getRefBus().setBusId((String)this.loadBusComboBox.getSelectedItem());
		
		if (this.changeFactorTextField.isEnabled()) {
			if (SwingInputVerifyUtil.largeThan(this.changeFactorTextField, 0.0d,
					errMsg, "Branch fault distance < 0.0"))
				xmlLoadChangeData.getChangeFactor().setFactor(SwingInputVerifyUtil.getDouble(this.changeFactorTextField));
		}

       	if (lowfreqRadioButton.isSelected()) {
       		xmlLoadChangeData.setLoadChangeType(DStabLoadChangeEnumType.LOW_FREQUENCY);
			if (SwingInputVerifyUtil.largeThan(this.threshholdTextField, 0.0d,
					errMsg, "Load change freq < 0.0"))
				xmlLoadChangeData.setThreshhold(SwingInputVerifyUtil.getDouble(this.threshholdTextField));
			if (SwingInputVerifyUtil.largeThan(this.delaySecTextField, 0.0d,
					errMsg, "Load change delay time < 0.0"))
				xmlLoadChangeData.setDelayTime(SwingInputVerifyUtil.getDouble(this.delaySecTextField));
       	}
       	else if (lowVoltRadioButton.isSelected()) {
       		xmlLoadChangeData.setLoadChangeType(DStabLoadChangeEnumType.LOW_VOLTAGE);
			if (SwingInputVerifyUtil.largeThan(this.threshholdTextField, 0.0d,
					errMsg, "Load change voltage < 0.0"))
				xmlLoadChangeData.setThreshhold(SwingInputVerifyUtil.getDouble(this.threshholdTextField));
			if (SwingInputVerifyUtil.largeThan(this.delaySecTextField, 0.0d,
					errMsg, "Load change delay time < 0.0"))
				xmlLoadChangeData.setDelayTime(SwingInputVerifyUtil.getDouble(this.delaySecTextField));
       	}
       	else {
       		xmlLoadChangeData.setLoadChangeType(DStabLoadChangeEnumType.FIXED_TIME);
			if (SwingInputVerifyUtil.largeThan(this.threshholdTextField, 0.0d,
					errMsg, "Load change time < 0.0"))
				xmlLoadChangeData.setThreshhold(SwingInputVerifyUtil.getDouble(this.threshholdTextField));
       	}

		return errMsg.size() == 0;
	}
	
	/** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        loadBusComboBox = new javax.swing.JComboBox();
        loadBusLabel = new javax.swing.JLabel();
        changeFactorGLabel = new javax.swing.JLabel();
        changeFactorTextField = new javax.swing.JTextField();
        changeFactorUnitLabel1 = new javax.swing.JLabel();
        threshholdLabel = new javax.swing.JLabel();
        threshholdTextField = new javax.swing.JTextField();
        threshholdUnitLabel = new javax.swing.JLabel();
        delaySecLabel = new javax.swing.JLabel();
        delaySecTextField = new javax.swing.JTextField();
        loadChangeTypePanel = new javax.swing.JPanel();
        lowfreqRadioButton = new javax.swing.JRadioButton();
        lowVoltRadioButton = new javax.swing.JRadioButton();
        fixedTimeRadioButton = new javax.swing.JRadioButton();

        loadBusComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        loadBusComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "No Load Bus" }));
        loadBusComboBox.setName("faultBusComboBox"); // NOI18N

        loadBusLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        loadBusLabel.setText("Load Bus   ");
        loadBusLabel.setPreferredSize(new java.awt.Dimension(70, 25));

        changeFactorGLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        changeFactorGLabel.setText("Change Factor     ");

        changeFactorTextField.setColumns(8);
        changeFactorTextField.setText("100.0");
        changeFactorTextField.setName("rLGTextField"); // NOI18N

        changeFactorUnitLabel1.setFont(new java.awt.Font("Dialog", 0, 12));
        changeFactorUnitLabel1.setText("%");

        threshholdLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        threshholdLabel.setText("Label  ");

        threshholdTextField.setColumns(4);
        threshholdTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        threshholdTextField.setText("0.0");
        threshholdTextField.setDragEnabled(true);
        threshholdTextField.setName("threshholdTextField"); // NOI18N

        threshholdUnitLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        threshholdUnitLabel.setText("unit");

        delaySecLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        delaySecLabel.setText("Delay(sec)");

        delaySecTextField.setColumns(4);
        delaySecTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        delaySecTextField.setText("0.0");
        delaySecTextField.setDragEnabled(true);
        delaySecTextField.setName("threshholdTextField"); // NOI18N

        loadChangeTypePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 15, 10));

        buttonGroup1.add(lowfreqRadioButton);
        lowfreqRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        lowfreqRadioButton.setSelected(true);
        lowfreqRadioButton.setText("Low Frequency");
        lowfreqRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        lowfreqRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        lowfreqRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lowfreqRadioButtonActionPerformed(evt);
            }
        });
        loadChangeTypePanel.add(lowfreqRadioButton);

        buttonGroup1.add(lowVoltRadioButton);
        lowVoltRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        lowVoltRadioButton.setText("Low Voltage");
        lowVoltRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        lowVoltRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        lowVoltRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lowVoltRadioButtonActionPerformed(evt);
            }
        });
        loadChangeTypePanel.add(lowVoltRadioButton);

        buttonGroup1.add(fixedTimeRadioButton);
        fixedTimeRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        fixedTimeRadioButton.setText("Fixed Time");
        fixedTimeRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        fixedTimeRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        fixedTimeRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fixedTimeRadioButtonActionPerformed(evt);
            }
        });
        loadChangeTypePanel.add(fixedTimeRadioButton);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(31, 31, 31)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(42, 42, 42)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(changeFactorGLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(changeFactorTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 44, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(changeFactorUnitLabel1))
                            .add(layout.createSequentialGroup()
                                .add(loadBusLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(30, 30, 30)
                                .add(loadBusComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 183, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(31, 31, 31)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(10, 10, 10)
                                .add(threshholdLabel)
                                .add(30, 30, 30)
                                .add(threshholdTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(threshholdUnitLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 41, Short.MAX_VALUE)
                                .add(delaySecLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 77, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(delaySecTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(loadChangeTypePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))))
                .add(81, 81, 81))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.CENTER, loadBusLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.CENTER, loadBusComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(15, 15, 15)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.CENTER, changeFactorGLabel)
                    .add(org.jdesktop.layout.GroupLayout.CENTER, changeFactorTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.CENTER, changeFactorUnitLabel1))
                .add(17, 17, 17)
                .add(loadChangeTypePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.CENTER, threshholdTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.CENTER, threshholdLabel)
                    .add(org.jdesktop.layout.GroupLayout.CENTER, threshholdUnitLabel)
                    .add(org.jdesktop.layout.GroupLayout.CENTER, delaySecLabel)
                    .add(org.jdesktop.layout.GroupLayout.CENTER, delaySecTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void lowVoltRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lowVoltRadioButtonActionPerformed
	if (lowVoltRadioButton.isSelected()) {
		setLableTextFiels("Volt Below", "pu", true);
	}
}//GEN-LAST:event_lowVoltRadioButtonActionPerformed

private void fixedTimeRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fixedTimeRadioButtonActionPerformed
    if (fixedTimeRadioButton.isSelected()) {
    	threshholdLabel.setText("Change Time");
        threshholdUnitLabel.setText("sec");
        setLableTextFiels("Change Time", "sec", false);
    }
}//GEN-LAST:event_fixedTimeRadioButtonActionPerformed

private void lowfreqRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lowfreqRadioButtonActionPerformed
    if (lowfreqRadioButton.isSelected()) {
    	setLableTextFiels("Freq Below", "hz", true);
    }
}//GEN-LAST:event_lowfreqRadioButtonActionPerformed
    
private void setLableTextFiels(String thLabel, String thUnit, boolean delayEnable) {
    threshholdLabel.setText(thLabel);
    threshholdUnitLabel.setText(thUnit);
    delaySecLabel.setEnabled(delayEnable);
    delaySecTextField.setEditable(delayEnable);
}
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel changeFactorGLabel;
    private javax.swing.JTextField changeFactorTextField;
    private javax.swing.JLabel changeFactorUnitLabel1;
    private javax.swing.JLabel delaySecLabel;
    private javax.swing.JTextField delaySecTextField;
    private javax.swing.JRadioButton fixedTimeRadioButton;
    private javax.swing.JComboBox loadBusComboBox;
    private javax.swing.JLabel loadBusLabel;
    private javax.swing.JPanel loadChangeTypePanel;
    private javax.swing.JRadioButton lowVoltRadioButton;
    private javax.swing.JRadioButton lowfreqRadioButton;
    private javax.swing.JLabel threshholdLabel;
    private javax.swing.JTextField threshholdTextField;
    private javax.swing.JLabel threshholdUnitLabel;
    // End of variables declaration//GEN-END:variables

	class DataVerifier extends javax.swing.InputVerifier {
    	public boolean verify(javax.swing.JComponent input) {
			if (input == null)
				return false;
       		try {
         	   	if (input == changeFactorTextField )
 	       			return SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) > 0.0;
 	       	} catch (Exception e) {
 	    		return false;
 	       	}		
			return true;
        }
    }
}
