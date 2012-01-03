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

import org.ieee.odm.schema.AcscBaseFaultXmlType;
import org.ieee.odm.schema.AcscBranchFaultXmlType;
import org.ieee.odm.schema.AcscBusFaultXmlType;
import org.ieee.odm.schema.AcscFaultCategoryEnumType;
import org.ieee.odm.schema.AcscFaultTypeEnumType;
import org.ieee.odm.schema.TimePeriodUnitType;
import org.interpss.editor.form.GFormContainer;
import org.interpss.editor.jgraph.ui.edit.IFormDataPanel;
import org.interpss.numeric.util.Number2String;
import org.interpss.ui.SwingInputVerifyUtil;

import com.interpss.common.util.IpssLogger;
import com.interpss.simu.SimuContext;
import com.interpss.simu.util.SimuCtxHelper;

public class NBFaultLocDataPanel extends javax.swing.JPanel implements IFormDataPanel{
	private static final long serialVersionUID = 1;

	private GFormContainer _netContainer = null;
    private SimuContext _simuCtx = null;	
    
	//private AcscFaultData _faultData = null;
	private AcscBaseFaultXmlType xmlFaultData = null;
	
    /** Creates new form FaultLocDataPanel */
    public NBFaultLocDataPanel() {
        initComponents();

  		DataVerifier verifier = new DataVerifier();
      	this.distanceTextField.setInputVerifier(verifier);
      	this.atReclosureTimeTextField.setInputVerifier(verifier);
      	this.rLGTextField.setInputVerifier(verifier);
      	this.xLGTextField.setInputVerifier(verifier);
      	this.rLLTextField.setInputVerifier(verifier);
      	this.xLLTextField.setInputVerifier(verifier);
    }
    
	public void init(Object netContainer, Object simuCtx) {
		// for non-graph file, netContainer = null
		IpssLogger.getLogger().info("NBFaultLocDataPanel init() called");

		_netContainer = (GFormContainer)netContainer;
	    _simuCtx = (SimuContext)simuCtx;
      
    	if (_netContainer != null) {
    		this.faultBusComboBox.setModel(new javax.swing.DefaultComboBoxModel(_netContainer.getBusNameIdArray()));
    		Object[] branchNameId = _netContainer.getBranchNameIdArrayNoXfr(_netContainer.getGNetForm().getAppType());
    		if (branchNameId.length > 0)
    			this.faultBranchComboBox.setModel(new javax.swing.DefaultComboBoxModel(branchNameId));
    	}
    	else {
    		this.faultBusComboBox.setModel(new javax.swing.DefaultComboBoxModel(SimuCtxHelper.getBusNameIdArray(_simuCtx)));
    		Object[] branchNameId = SimuCtxHelper.getBranchNameIdArrayNoXfr(_simuCtx);
    		if (branchNameId.length > 0)
    			this.faultBranchComboBox.setModel(new javax.swing.DefaultComboBoxModel(branchNameId));
    	}
	}
/*	
	public void setFaultData(AcscFaultData data) {
		_faultData = data;
        setBusBranchFaultPanel();
	}
*/	
	/**
	 * this function might be called by setForm2Edtior (saveData = false) or 
	 * saveEditor2Form (saveData = true))
	 */
	public void setFaultData(AcscBaseFaultXmlType data, boolean saveData) {
		this.xmlFaultData = data;
		if (!saveData) {
//			if (xmlFaultData.getZLG() == null) {
//				xmlFaultData.setZLG(IpssXmlDataSetter.createComplexXmlType(0.0, 0.0));
//			}
//			if (xmlFaultData.getZLL() == null) {
//				xmlFaultData.setZLL(IpssXmlDataSetter.createComplexXmlType(0.0, 0.0));
//			}
	        setBusBranchFaultPanel();
		}
	}

	public void setBranchReclosureStatus(boolean status) {
        reclosureCheckBox.setEnabled(status);
        if (!status) {
            reclosureCheckBox.setSelected(status);
            branchReclosureCheckboxActionPerformed(null);
        }
	}
	
    public void setBusBranchFaultPanel() {
        faultLocPanel.remove(busFaultPanel);
        faultLocPanel.remove(branchFaultPanel);
        if (xmlFaultData.getFaultType() == AcscFaultTypeEnumType.BUS_FAULT) {
			AcscBusFaultXmlType busFault = (AcscBusFaultXmlType)xmlFaultData;
            faultLocPanel.add(busFaultPanel, java.awt.BorderLayout.NORTH);
            if (busFault.getRefBus() == null || busFault.getRefBus().getBusId().equals("")) {
                IpssLogger.getLogger().info("faultBusComboBox.getSelectedItem() -> " + this.faultBusComboBox.getSelectedItem());
                if (this.faultBusComboBox.getSelectedItem() != null)
                	this.faultBusComboBox.setSelectedIndex(0);
            }    
            else
                this.faultBusComboBox.setSelectedItem(busFault.getRefBus().getBusId());
            this.distanceTextField.setEnabled(false);
            IpssLogger.getLogger().info("Bus Fault input panel added");
        }	
        else{
			AcscBranchFaultXmlType braFault = (AcscBranchFaultXmlType)xmlFaultData;
            faultLocPanel.add(branchFaultPanel, java.awt.BorderLayout.NORTH);
            if (braFault.getRefBranch() == null || braFault.getRefBranch().getBranchId().equals("")) {
            	IpssLogger.getLogger().info("faultBranchComboBox.getSelectedItem() -> " + this.faultBranchComboBox.getSelectedItem());
            	this.faultBranchComboBox.setSelectedIndex(0);
            }    
            else
                this.faultBranchComboBox.setSelectedItem(braFault.getRefBranch().getBranchId());
            this.distanceTextField.setEnabled(true);
            IpssLogger.getLogger().info("Branch Fault input panel added");
        }
    }
    
	/**
	*	Set form data to the editor
	*
	* @return false if there is any problem
	*/
    public boolean setForm2Editor() {
		IpssLogger.getLogger().info("NBFaultLocDataPanel setForm2Editor() called");
		
		if (xmlFaultData.getFaultType() == AcscFaultTypeEnumType.BUS_FAULT) {
			
		}
		else {
			AcscBranchFaultXmlType braFault = (AcscBranchFaultXmlType)xmlFaultData;
		    reclosureCheckBox.setSelected(braFault.isBranchReclosure() != null && braFault.isBranchReclosure());		
	        branchReclosureCheckboxActionPerformed(null);
		    atReclosureTimeTextField.setText(Number2String.toStr(braFault.getReclosureTime().getValue(), "0.00"));

		    if (this.distanceTextField.isEnabled()) {
	        	this.distanceTextField.setText(Number2String.toStr(braFault.getDistance(), "#0.##"));
		    }
		}

        if (xmlFaultData.getFaultCategory() == AcscFaultCategoryEnumType.FAULT_3_PHASE) 
            this.type3PRadioButton.setSelected(true);
        else if (xmlFaultData.getFaultCategory() == AcscFaultCategoryEnumType.LINE_TO_GROUND) 
            this.typeLGRadioButton.setSelected(true);
        else if (xmlFaultData.getFaultCategory() == AcscFaultCategoryEnumType.LINE_TO_LINE) 
            this.typeLLRadioButton.setSelected(true);
        else if (xmlFaultData.getFaultCategory() == AcscFaultCategoryEnumType.LINE_LINE_TO_GROUND) 
            this.typeLLGRadioButton.setSelected(true);
        else 
            this.typeAllRadioButton.setSelected(true);

        setLabelText();
		
		if (this.rLGTextField.isEnabled()) {
			double r = 0.0, i = 0.0;
			if (xmlFaultData.getZLG() != null) {
				r = xmlFaultData.getZLG().getRe();
				i = xmlFaultData.getZLG().getIm();
			}
			this.rLGTextField.setText(Number2String.toStr(r, "#0.0000"));
        	this.xLGTextField.setText(Number2String.toStr(i, "#0.0000"));
		}

		if (this.rLLTextField.isEnabled()) {
			double r = 0.0, i = 0.0;
			if (xmlFaultData.getZLL() != null) {
				r = xmlFaultData.getZLL().getRe();
				i = xmlFaultData.getZLL().getIm();
			}
        	this.rLLTextField.setText(Number2String.toStr(r, "#0.0000"));
        	this.xLLTextField.setText(Number2String.toStr(i, "#0.0000"));
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
		IpssLogger.getLogger().info("NBFaultLocDataPanel saveEditor2Form() called");

		boolean ok = true;

		if (xmlFaultData.getFaultType() == AcscFaultTypeEnumType.BUS_FAULT) {
			String id = (String)this.faultBusComboBox.getSelectedItem();
			
			AcscBusFaultXmlType busFault = (AcscBusFaultXmlType)xmlFaultData;
			busFault.getRefBus().setBusId(id);
		}
		else { 
			String id = (String)this.faultBranchComboBox.getSelectedItem();
			AcscBranchFaultXmlType braFault = (AcscBranchFaultXmlType)xmlFaultData;
			braFault.getRefBranch().setBranchId(id);
// TODO  setBranch fromId, toId, cirId
			
			braFault.setBranchReclosure(reclosureCheckBox.isSelected());		
			if (reclosureCheckBox.isSelected()) {
				if (!SwingInputVerifyUtil.largeThan(this.atReclosureTimeTextField, 0.0d)) {
					errMsg.add("Branch reclosure at Time <= 0.0");
					ok = false;
				}
				braFault.getReclosureTime().setValue((SwingInputVerifyUtil.getDouble(this.atReclosureTimeTextField)));
				braFault.getReclosureTime().setUnit(TimePeriodUnitType.SEC);
			}

		    if (this.distanceTextField.isEnabled()) {
				if (!SwingInputVerifyUtil.largeEqualThan(this.distanceTextField, 0.0d)) {
					errMsg.add("Branch fault distance < 0.0");
					ok = false;
				}
				braFault.setDistance(SwingInputVerifyUtil.getDouble(this.distanceTextField));
			}
		}
		
		if (this.type3PRadioButton.isSelected())
			xmlFaultData.setFaultCategory(AcscFaultCategoryEnumType.FAULT_3_PHASE);
		else if (this.typeLGRadioButton.isSelected())
			xmlFaultData.setFaultCategory(AcscFaultCategoryEnumType.LINE_TO_GROUND);
		else if (this.typeLLRadioButton.isSelected())
			xmlFaultData.setFaultCategory(AcscFaultCategoryEnumType.LINE_TO_LINE);
		else if (this.typeLLGRadioButton.isSelected())
			xmlFaultData.setFaultCategory(AcscFaultCategoryEnumType.LINE_LINE_TO_GROUND);

		if (this.rLGTextField.isEnabled()) {
			if (!SwingInputVerifyUtil.largeEqualThan(this.rLGTextField, 0.0d)) {
				errMsg.add("Fault L-G R < 0.0");
				ok = false;
			}
			xmlFaultData.getZLG().setRe(SwingInputVerifyUtil.getDouble(this.rLGTextField));
		}

		if (this.xLGTextField.isEnabled()) {
			if (!SwingInputVerifyUtil.largeEqualThan(this.xLGTextField, 0.0d)) {
				errMsg.add("Fault L-G X < 0.0");
				ok = false;
			}
			xmlFaultData.getZLG().setIm(SwingInputVerifyUtil.getDouble(this.xLGTextField));
		}

		if (this.rLLTextField.isEnabled()) {
			if (!SwingInputVerifyUtil.largeEqualThan(this.rLLTextField, 0.0d)) {
				errMsg.add("Fault L-L R < 0.0");
				ok = false;
			}
			xmlFaultData.getZLL().setRe(SwingInputVerifyUtil.getDouble(this.rLLTextField));
		}

		if (this.xLLTextField.isEnabled()) {
			if (!SwingInputVerifyUtil.largeEqualThan(this.xLLTextField, 0.0d)) {
				errMsg.add("Fault L-L X < 0.0");
				ok = false;
			}
			xmlFaultData.getZLL().setIm(SwingInputVerifyUtil.getDouble(this.xLLTextField));
		}

		return ok;
	}
	
    private void setLabelText() {
		if (this.typeLLGRadioButton.isSelected() || this.typeAllRadioButton.isSelected()) {
			zLGLabel.setEnabled(true);
			rLGTextField.setEnabled(true);
			xLGTextField.setEnabled(true);
			rLLTextField.setEnabled(true);
			xLLTextField.setEnabled(true);
			zLLLabel.setEnabled(true);
		}	
		else if (this.type3PRadioButton.isSelected() ||
				this.typeLGRadioButton.isSelected() ||
				this.typeAllRadioButton.isSelected()) {
			zLGLabel.setEnabled(true);
			rLGTextField.setEnabled(true);
			xLGTextField.setEnabled(true);
			rLLTextField.setEnabled(false);
			xLLTextField.setEnabled(false);
			zLLLabel.setEnabled(false);
		}	
		else if (this.typeLLRadioButton.isSelected()) {
			zLGLabel.setEnabled(false);
			rLGTextField.setEnabled(false);
			xLGTextField.setEnabled(false);
			rLLTextField.setEnabled(true);
			xLLTextField.setEnabled(true);
			zLLLabel.setEnabled(true);
		}	
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
        faultLocPanel = new javax.swing.JPanel();
        busFaultPanel = new javax.swing.JPanel();
        faultBusLabel = new javax.swing.JLabel();
        faultBusComboBox = new javax.swing.JComboBox();
        branchFaultPanel = new javax.swing.JPanel();
        faultBranchLabel = new javax.swing.JLabel();
        faultBranchComboBox = new javax.swing.JComboBox();
        distanceLabel = new javax.swing.JLabel();
        distanceTextField = new javax.swing.JTextField();
        disUnitLabel = new javax.swing.JLabel();
        reclosureCheckBox = new javax.swing.JCheckBox();
        atReclosureTimeLabel = new javax.swing.JLabel();
        atReclosureTimeTextField = new javax.swing.JTextField();
        faultTypePanel = new javax.swing.JPanel();
        typePanel = new javax.swing.JPanel();
        type3PRadioButton = new javax.swing.JRadioButton();
        typeLGRadioButton = new javax.swing.JRadioButton();
        typeLLRadioButton = new javax.swing.JRadioButton();
        typeLLGRadioButton = new javax.swing.JRadioButton();
        typeAllRadioButton = new javax.swing.JRadioButton();
        faultZPanel = new javax.swing.JPanel();
        zLGLabel = new javax.swing.JLabel();
        rLGTextField = new javax.swing.JTextField();
        xLGTextField = new javax.swing.JTextField();
        zLGUnitLabel = new javax.swing.JLabel();
        zLLLabel = new javax.swing.JLabel();
        rLLTextField = new javax.swing.JTextField();
        xLLTextField = new javax.swing.JTextField();
        zLLUnitLabel = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        faultLocPanel.setLayout(new java.awt.BorderLayout());

        faultBusLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        faultBusLabel.setText("Fault Bus   ");
        faultBusLabel.setPreferredSize(new java.awt.Dimension(70, 25));
        busFaultPanel.add(faultBusLabel);

        faultBusComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        faultBusComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Bus id list" }));
        faultBusComboBox.setName("faultBusComboBox"); // NOI18N
        busFaultPanel.add(faultBusComboBox);

        faultLocPanel.add(busFaultPanel, java.awt.BorderLayout.NORTH);

        faultBranchLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        faultBranchLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        faultBranchLabel.setText("Fault Branch     ");
        faultBranchLabel.setPreferredSize(new java.awt.Dimension(90, 25));

        faultBranchComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        faultBranchComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Branch id list" }));
        faultBranchComboBox.setName("faultBranchComboBox"); // NOI18N

        distanceLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        distanceLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        distanceLabel.setText("Fault Distance     ");
        distanceLabel.setPreferredSize(new java.awt.Dimension(100, 25));

        distanceTextField.setColumns(5);
        distanceTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        distanceTextField.setText("0.0");
        distanceTextField.setName("distanceTextField"); // NOI18N

        disUnitLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        disUnitLabel.setText("(% from FromSide)");

        reclosureCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        reclosureCheckBox.setText("Branch Reclosure");
        reclosureCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        reclosureCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        reclosureCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                branchReclosureCheckboxActionPerformed(evt);
            }
        });

        atReclosureTimeLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        atReclosureTimeLabel.setText("at(sec)");
        atReclosureTimeLabel.setEnabled(false);

        atReclosureTimeTextField.setColumns(3);
        atReclosureTimeTextField.setText("0.0");
        atReclosureTimeTextField.setEnabled(false);

        org.jdesktop.layout.GroupLayout branchFaultPanelLayout = new org.jdesktop.layout.GroupLayout(branchFaultPanel);
        branchFaultPanel.setLayout(branchFaultPanelLayout);
        branchFaultPanelLayout.setHorizontalGroup(
            branchFaultPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(branchFaultPanelLayout.createSequentialGroup()
                .add(38, 38, 38)
                .add(branchFaultPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(branchFaultPanelLayout.createSequentialGroup()
                        .add(110, 110, 110)
                        .add(distanceLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(distanceTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(branchFaultPanelLayout.createSequentialGroup()
                        .add(faultBranchLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(faultBranchComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(10, 10, 10)
                .add(branchFaultPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(branchFaultPanelLayout.createSequentialGroup()
                        .add(19, 19, 19)
                        .add(reclosureCheckBox)
                        .add(12, 12, 12)
                        .add(atReclosureTimeLabel)
                        .add(4, 4, 4)
                        .add(atReclosureTimeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(disUnitLabel))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        branchFaultPanelLayout.setVerticalGroup(
            branchFaultPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, branchFaultPanelLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(branchFaultPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(faultBranchLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(faultBranchComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(reclosureCheckBox)
                    .add(atReclosureTimeLabel)
                    .add(atReclosureTimeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(10, 10, 10)
                .add(branchFaultPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(distanceLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(branchFaultPanelLayout.createSequentialGroup()
                        .add(2, 2, 2)
                        .add(distanceTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(branchFaultPanelLayout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(disUnitLabel))))
        );

        faultLocPanel.add(branchFaultPanel, java.awt.BorderLayout.SOUTH);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        add(faultLocPanel, gridBagConstraints);

        faultTypePanel.setLayout(new java.awt.GridBagLayout());

        typePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Fault Type", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10)));
        typePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 20, 0));

        faultTypeButtonGroup.add(type3PRadioButton);
        type3PRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        type3PRadioButton.setSelected(true);
        type3PRadioButton.setText("3P");
        type3PRadioButton.setName("type3PRadioButton"); // NOI18N
        type3PRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                type3PRadioButtonActionPerformed(evt);
            }
        });
        typePanel.add(type3PRadioButton);

        faultTypeButtonGroup.add(typeLGRadioButton);
        typeLGRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        typeLGRadioButton.setText("L-G");
        typeLGRadioButton.setName("typeLGRadioButton"); // NOI18N
        typeLGRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeLGRadioButtonActionPerformed(evt);
            }
        });
        typePanel.add(typeLGRadioButton);

        faultTypeButtonGroup.add(typeLLRadioButton);
        typeLLRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        typeLLRadioButton.setText("L-L");
        typeLLRadioButton.setName("typeLLRadioButton"); // NOI18N
        typeLLRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeLLRadioButtonActionPerformed(evt);
            }
        });
        typePanel.add(typeLLRadioButton);

        faultTypeButtonGroup.add(typeLLGRadioButton);
        typeLLGRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        typeLLGRadioButton.setText("LL-G");
        typeLLGRadioButton.setName("typeLLGRadioButton"); // NOI18N
        typeLLGRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeLLGRadioButtonActionPerformed(evt);
            }
        });
        typePanel.add(typeLLGRadioButton);

        faultTypeButtonGroup.add(typeAllRadioButton);
        typeAllRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        typeAllRadioButton.setText("All");
        typeAllRadioButton.setEnabled(false);
        typeAllRadioButton.setName("typeAllRadioButton"); // NOI18N
        typeAllRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeAllRadioButtonActionPerformed(evt);
            }
        });
        typePanel.add(typeAllRadioButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 30, 0, 30);
        faultTypePanel.add(typePanel, gridBagConstraints);

        faultZPanel.setLayout(new java.awt.GridBagLayout());

        zLGLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        zLGLabel.setText("L-G(r+jx)     ");
        faultZPanel.add(zLGLabel, new java.awt.GridBagConstraints());

        rLGTextField.setColumns(8);
        rLGTextField.setText("0.0");
        rLGTextField.setName("rLGTextField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 5, 0);
        faultZPanel.add(rLGTextField, gridBagConstraints);

        xLGTextField.setColumns(8);
        xLGTextField.setText("0.0");
        xLGTextField.setName("xLGTextField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(10, 20, 5, 0);
        faultZPanel.add(xLGTextField, gridBagConstraints);

        zLGUnitLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        zLGUnitLabel.setText("     (Ohms)");
        faultZPanel.add(zLGUnitLabel, new java.awt.GridBagConstraints());

        zLLLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        zLLLabel.setText("L-L(r+jx)     ");
        zLLLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        faultZPanel.add(zLLLabel, gridBagConstraints);

        rLLTextField.setColumns(8);
        rLLTextField.setText("0.0");
        rLLTextField.setEnabled(false);
        rLLTextField.setName("rLLTextField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        faultZPanel.add(rLLTextField, gridBagConstraints);

        xLLTextField.setColumns(8);
        xLLTextField.setText("0.0");
        xLLTextField.setEnabled(false);
        xLLTextField.setName("xLLTextField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
        faultZPanel.add(xLLTextField, gridBagConstraints);

        zLLUnitLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        zLLUnitLabel.setText("     (Ohms)");
        zLLUnitLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        faultZPanel.add(zLLUnitLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        faultTypePanel.add(faultZPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        add(faultTypePanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void branchReclosureCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_branchReclosureCheckboxActionPerformed
        if (reclosureCheckBox.isSelected()) {
        	atReclosureTimeLabel.setEnabled(true);
        	atReclosureTimeTextField.setEnabled(true);
        }
        else {
        	atReclosureTimeLabel.setEnabled(false);
        	atReclosureTimeTextField.setEnabled(false);
        }
    }//GEN-LAST:event_branchReclosureCheckboxActionPerformed

    private void typeAllRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeAllRadioButtonActionPerformed
        setLabelText();
    }//GEN-LAST:event_typeAllRadioButtonActionPerformed

    private void typeLLGRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeLLGRadioButtonActionPerformed
        setLabelText();
    }//GEN-LAST:event_typeLLGRadioButtonActionPerformed

    private void typeLLRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeLLRadioButtonActionPerformed
        setLabelText();
    }//GEN-LAST:event_typeLLRadioButtonActionPerformed

    private void typeLGRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeLGRadioButtonActionPerformed
        setLabelText();
    }//GEN-LAST:event_typeLGRadioButtonActionPerformed

    private void type3PRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_type3PRadioButtonActionPerformed
        setLabelText();
    }//GEN-LAST:event_type3PRadioButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel atReclosureTimeLabel;
    private javax.swing.JTextField atReclosureTimeTextField;
    private javax.swing.JPanel branchFaultPanel;
    private javax.swing.JPanel busFaultPanel;
    private javax.swing.JLabel disUnitLabel;
    private javax.swing.JLabel distanceLabel;
    private javax.swing.JTextField distanceTextField;
    private javax.swing.JComboBox faultBranchComboBox;
    private javax.swing.JLabel faultBranchLabel;
    private javax.swing.JComboBox faultBusComboBox;
    private javax.swing.JLabel faultBusLabel;
    private javax.swing.JPanel faultLocPanel;
    private javax.swing.ButtonGroup faultTypeButtonGroup;
    private javax.swing.JPanel faultTypePanel;
    private javax.swing.JPanel faultZPanel;
    private javax.swing.JTextField rLGTextField;
    private javax.swing.JTextField rLLTextField;
    private javax.swing.JCheckBox reclosureCheckBox;
    private javax.swing.JRadioButton type3PRadioButton;
    private javax.swing.JRadioButton typeAllRadioButton;
    private javax.swing.JRadioButton typeLGRadioButton;
    private javax.swing.JRadioButton typeLLGRadioButton;
    private javax.swing.JRadioButton typeLLRadioButton;
    private javax.swing.JPanel typePanel;
    private javax.swing.JTextField xLGTextField;
    private javax.swing.JTextField xLLTextField;
    private javax.swing.JLabel zLGLabel;
    private javax.swing.JLabel zLGUnitLabel;
    private javax.swing.JLabel zLLLabel;
    private javax.swing.JLabel zLLUnitLabel;
    // End of variables declaration//GEN-END:variables

	class DataVerifier extends javax.swing.InputVerifier {
    	public boolean verify(javax.swing.JComponent input) {
			if (input == null)
				return false;
       		try {
       			if (input == distanceTextField ||
       			    input == rLGTextField ||
       			    input == xLGTextField ||
       			    input == rLLTextField ||
       			    input == xLLTextField )
 	       			return SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) >= 0.0;
 	       		else if (input == atReclosureTimeTextField)
   	       			return SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) > 0.0;
 	       	} catch (Exception e) {
 	    		return false;
 	       	}		
			return true;
        }
    }
}
