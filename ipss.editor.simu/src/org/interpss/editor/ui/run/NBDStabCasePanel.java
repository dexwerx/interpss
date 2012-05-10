 /*
  * @(#)NBDStabCasePanel.java   
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

import static org.ieee.odm.ODMObjectFactory.odmObjFactory;
import static com.interpss.dstab.funcImpl.DStabFunction.*;

import java.util.List;
import java.util.Vector;

import javax.swing.JDialog;

import org.ieee.odm.schema.DStabMethodEnumType;
import org.ieee.odm.schema.DStabSimulationXmlType;
import org.ieee.odm.schema.DynamicEventEnumType;
import org.ieee.odm.schema.DynamicEventXmlType;
import org.ieee.odm.schema.DynamicStaticLoadEnumType;
import org.ieee.odm.schema.GridComputingXmlType;
import org.ieee.odm.schema.MachineControllerEnumType;
import org.ieee.odm.schema.SetPointChangeEnumType;
import org.interpss.editor.form.GFormContainer;
import org.interpss.editor.jgraph.ui.edit.IFormDataPanel;
import org.interpss.editor.ui.IOutputTextDialog;
import org.interpss.editor.ui.run.common.NBDynaEventPanel;
import org.interpss.editor.ui.run.common.NBGridComputingPanel;
import org.interpss.editor.ui.util.GUIFileUtil;
import org.interpss.numeric.util.Number2String;
import org.interpss.spring.UISpringFactory;
import org.interpss.ui.SwingInputVerifyUtil;

import com.interpss.common.datatype.Constants;
import com.interpss.common.util.IpssLogger;
import com.interpss.common.util.StringUtil;
import com.interpss.core.funcImpl.CoreUtilFunc;
import com.interpss.simu.SimuContext;
import com.interpss.simu.util.SimuCtxHelper;

public class NBDStabCasePanel extends javax.swing.JPanel implements IFormDataPanel {
	public static String OutpuScriptTemplateFilename = "template/DStabOutputScriptTemplate.txt";

	private static final long serialVersionUID = 1;

	private NBDynaEventPanel dynaEventPanel = null;
	private NBAclfCasePanel aclfCasePanel = null;
	private NBGridComputingPanel gridPanel = null;

	private GFormContainer netContainer = null;
	private SimuContext simuCtx = null;
	
	private String dstabOutputScriptFilename;
	
    private DStabSimulationXmlType xmlCaseData;
//	private AclfCaseData    aclfCaseData = null;  // current case data
//	private DStabCaseData   dstabCaseData = null;  // current case data
	private String machIdLargestInertia = "";

    /** Creates new form NBCaseInfoDialog */
    public NBDStabCasePanel(JDialog parent) {
        initComponents();

        dynaEventPanel = new NBDynaEventPanel(parent);
    	aclfCasePanel = new NBAclfCasePanel(parent);
    	gridPanel = new NBGridComputingPanel();

    	this.gridComputingPanel.add(gridPanel);

        dynamicEventPanel.add(dynaEventPanel);
        loadflowPanel.add(aclfCasePanel);
    	this.gridComputingPanel.removeAll();
    	this.gridComputingPanel.add(gridPanel);
        //otherInfoPanel
        
        DataVerifier verifier = new DataVerifier();
        totalTimeTextField.setInputVerifier(verifier);
        simuStepTextField.setInputVerifier(verifier);
        staticLoadSwitchVoltTextField.setInputVerifier(verifier);
    }
    
    public void init(Object netCtr, Object simuCtx) {
		IpssLogger.getLogger().info("NBDStabCasePanel init() called");

		this.netContainer = (GFormContainer)netCtr;
	    this.simuCtx = (SimuContext)simuCtx;	
    	if (this.netContainer != null) {
            refMachComboBox.setModel(new javax.swing.DefaultComboBoxModel(this.netContainer.getMachIdArray()));
            setPointMachineComboBox.setModel(new javax.swing.DefaultComboBoxModel(this.netContainer.getMachIdArray()));
            machIdLargestInertia = this.netContainer.getMachIdLargestInertia();
    	}
    	else {
            refMachComboBox.setModel(new javax.swing.DefaultComboBoxModel(SimuCtxHelper.getMachIdArray(this.simuCtx)));
            setPointMachineComboBox.setModel(new javax.swing.DefaultComboBoxModel(SimuCtxHelper.getMachIdArray(this.simuCtx)));
            machIdLargestInertia = SimuCtxHelper.getMachIdLargestInertia(this.simuCtx);
    	}
         
        setSetPointControllerList();
        
        setOutputFilterPanel(false);
        setOutputScriptingPanel(false);
        setSetpointPanel(false);
        
        dynaEventPanel.init(this.netContainer, simuCtx);
        aclfCasePanel.init(this.netContainer, simuCtx);

        // always set the first tab-panel active
        detailInfoTabbedPane.setSelectedIndex(0);
    }

    
    public void setDStabOutputScriptFilename(String filename) {
    	this.dstabOutputScriptFilename = filename;    
    }
    
    private void setOutputFilterPanel(boolean status) {
    	detailInfoTabbedPane.setEnabledAt(3, status);
    	if (status) {
    	    setOutputVarList();
    	    setMachOutVarList();
    	    setBusOutVarList();
    	    setBranchOutVarList();
    	}
    }
    
    private void setOutputScriptingPanel(boolean status) {
    	detailInfoTabbedPane.setEnabledAt(4, status);
    }

    private void setSetpointPanel(boolean status) {
    	detailInfoTabbedPane.setEnabledAt(5, status);
    }
    
    private void setStaticLoadCPStatus(boolean status) {
        staticLoadSwitchVoltLabel.setEnabled(status);
        staticLoadSwitchVoltTextField.setEnabled(status);
        staticLoadSwitchDeadZoneLabel.setEnabled(status);
        staticLoadSwitchDeadZoneTextField.setEnabled(status);
    }
    
    private void setSetPointControllerList() {
    	String machId = (String)setPointMachineComboBox.getSelectedItem();
    	if (this.netContainer != null) {
    		setPointControllerComboBox.setModel(new javax.swing.DefaultComboBoxModel(
    			this.netContainer.getMachContrllerList(machId)));
    	}
    	else {
    		setPointControllerComboBox.setModel(new javax.swing.DefaultComboBoxModel(
    				SimuCtxHelper.getMachContrllerList(machId, this.simuCtx)));
    	}
    }

    private void setOutputVarList() {
   		outputVarList.setModel(new javax.swing.DefaultComboBoxModel(
   			this.xmlCaseData.getOutputConfig().getOutputVariable().toArray()));
    }

    private void setMachOutVarList() {
    	if (this.netContainer != null) {
    		machOutVarList.setModel(new javax.swing.DefaultComboBoxModel(
    			this.netContainer.getMachIdArray()));
    	}
    	else {
    		machOutVarList.setModel(new javax.swing.DefaultComboBoxModel(
    				GetMachineIdAry.f(simuCtx.getDStabilityNet())));
    	}
    }
    
    private void setBusOutVarList() {
    	if (this.netContainer != null) {
    		busOutVarList.setModel(new javax.swing.DefaultComboBoxModel(
    			this.netContainer.getBusIdArray()));
    	}
    	else {
    		busOutVarList.setModel(new javax.swing.DefaultComboBoxModel(
    				CoreUtilFunc.getBusIdArray(simuCtx.getDStabilityNet())));
    	}
    }

    private void setBranchOutVarList() {
    	if (this.netContainer != null) {
    		branchOutVarList.setModel(new javax.swing.DefaultComboBoxModel(
    			this.netContainer.getBranchIdArray()));
    	}
    	else {
    		branchOutVarList.setModel(new javax.swing.DefaultComboBoxModel(
    				CoreUtilFunc.getBranchIdArray(simuCtx.getDStabilityNet())));
    	}
    }
/*
    public void setCaseData(CaseData caseData) {
    	dstabCaseData = caseData.getDStabCaseData();
    	if (caseData.getAclfCaseData() == null)
    		caseData.setAclfCaseData(new AclfCaseData());
    	aclfCaseData = caseData.getAclfCaseData();
    	dynaEventPanel.setCaseData(dstabCaseData);
    	//aclfCasePanel.setCaseData(aclfCaseData);
    	//gridPanel.setCaseData(dstabCaseData);
    }
*/    
    public void setXmlCaseData(DStabSimulationXmlType data, GridComputingXmlType xmlGridOpt) {
    	this.xmlCaseData = data;
    	dynaEventPanel.setCaseData(data.getDynamicEvent());
    	// contingency analysis is not used for DStab
    	aclfCasePanel.setXmlCaseData(data.getAclfInitialization(), null, xmlGridOpt);
    }
    
    /**
	*	Set form data to the editor
	*
	* @return false if there is any problem
	*/
	public boolean setForm2Editor() {
		IpssLogger.getLogger().info("NBDStabCasePanel setForm2Editor() called");

		// set Aclf panel 
		aclfCasePanel.setForm2Editor();
		
        /* Now there is only one available method
         * methodComboBox.setSelectedItem(xmlCaseData.getSimuConfig().getSimuMethod().toString());
         */
        totalTimeTextField.setText(Number2String.toStr(xmlCaseData.getSimulationSetting().getTotalTime().getValue(), "#0.00"));
        simuStepTextField.setText(Number2String.toStr(xmlCaseData.getSimulationSetting().getStep().getValue(), "#0.00#"));
        disableEventCheckBox.setSelected(xmlCaseData.isDisableDynEvents());
        
        if (xmlCaseData.getSimulationSetting().isAbsMachineAngle()) {
            absMachCheckBox.setSelected(true);
            absMachCheckBoxActionPerformed(null);
        }
        else {
        	if (xmlCaseData.getSimulationSetting().getRefMachineBus().getBusId().equals("")) {
        		// select machine with the largest inertia
        		xmlCaseData.getSimulationSetting().getRefMachineBus().setBusId(machIdLargestInertia);
        	}
       		refMachComboBox.setSelectedItem(xmlCaseData.getSimulationSetting().getRefMachineBus().getBusId());
        }
        
        if (xmlCaseData.getStaticLoadModel() !=  null && 
        		xmlCaseData.getStaticLoadModel().getStaticLoadType() == DynamicStaticLoadEnumType.CONST_P) {
            staticLoadCPRadioButton.setSelected(true);
            setStaticLoadCPStatus(true);
            staticLoadSwitchVoltTextField.setText(Number2String.toStr(xmlCaseData.getStaticLoadModel().getSwitchVolt(), "#0.00"));
            staticLoadSwitchDeadZoneTextField.setText(Number2String.toStr(xmlCaseData.getStaticLoadModel().getSwitchDeadZone(), "#0.00"));
        }
        else {
            staticLoadCZRadioButton.setSelected(true);
            setStaticLoadCPStatus(false);
        }
        
        if(!xmlCaseData.isDisableDynEvents()) {
        	disableEventCheckBox.setSelected(false);
        	dynaEventPanel.setForm2Editor();
            setSetpointPanel(false);
        }
        else {
        	// disable dynamic event
        	disableEventCheckBox.setSelected(true);
        
    		if (xmlCaseData.getDynamicEvent().size() > 0) {
            	DynamicEventXmlType event = xmlCaseData.getDynamicEvent().get(0); 
    			if (event.getEventType() == DynamicEventEnumType.SET_POINT_CHANGE) {
    				setPointCheckBox.setSelected(true);
                    setPointMachineComboBox.setSelectedItem(event.getSetPointChangeData().getRefGenBus().getBusId());
                	setPointControllerComboBox.setSelectedItem(event.getSetPointChangeData().getControllerType().toString());
                    setPointValueTextField.setText(Number2String.toStr(event.getSetPointChangeData().getChangeValue(), "#0.00"));
                    setPointAbsoluteRadioButton.setSelected(event.getSetPointChangeData().getValueChangeType() == 
                    	SetPointChangeEnumType.ABSOLUTE);
    			}
        	}
        }
        disableEventCheckBoxActionPerformed(null);

        outputFilterCheckBox.setSelected(xmlCaseData.getOutputConfig().isOutputFilter());
        setOutputFilterPanel(outputFilterCheckBox.isSelected());

        outputScriptCheckBox.setSelected(xmlCaseData.getOutputScripting() != null && xmlCaseData.getOutputScripting().isScripting());
        setOutputScriptingPanel(outputScriptCheckBox.isSelected());
        if (outputScriptCheckBox.isSelected()) {
            if (dstabOutputScriptFilename != null) {
            	IpssLogger.getLogger().info("scriptFilename: " + dstabOutputScriptFilename);
            	GUIFileUtil.readFile2TextareaAbsolutePath(dstabOutputScriptFilename, scriptTextArea);
            	if (scriptTextArea.getText().trim().equals(""))
                	GUIFileUtil.readFile2TextareaRativePath(OutpuScriptTemplateFilename, scriptTextArea);
            }         
        }

        gridPanel.setForm2Editor();

        return true;
	}
    
	/**
	*	Save editor screen data to the form
	*
	* @param errMsg error messages during the saving process.
	* @return false if there is any problem
	*/
	public boolean saveEditor2Form(Vector<String> errMsg) throws Exception {
		IpssLogger.getLogger().info("NBDStabCasePanel saveEditor2Form() called");

		// save aclf panel data
		aclfCasePanel.saveEditor2Form(errMsg);
		
		xmlCaseData.getSimulationSetting().setDstabMethod(DStabMethodEnumType.MODIFIED_EULER);

        if (SwingInputVerifyUtil.largeThan(totalTimeTextField, 0.0d, errMsg, "Total Simulation time < 0.0"))
        	xmlCaseData.getSimulationSetting().getTotalTime().setValue(SwingInputVerifyUtil.getDouble(totalTimeTextField));
        
        if (SwingInputVerifyUtil.largeThan(simuStepTextField, 0.0d, errMsg, "Simulation step <= 0.0"))
        	xmlCaseData.getSimulationSetting().getStep().setValue(SwingInputVerifyUtil.getDouble(simuStepTextField));
        
        if (xmlCaseData.getSimulationSetting().getTotalTime().getValue() < xmlCaseData.getSimulationSetting().getStep().getValue()) {
			errMsg.add("Total simu time < simulation step");
        }
        
        if (absMachCheckBox.isSelected()) {
        	xmlCaseData.getSimulationSetting().setAbsMachineAngle(true);
        }
        else {
        	xmlCaseData.getSimulationSetting().setAbsMachineAngle(false);
       		xmlCaseData.getSimulationSetting().getRefMachineBus().setBusId((String)refMachComboBox.getSelectedItem());
        }

        if (xmlCaseData.getNetEqnSolveConfig() == null)
        	xmlCaseData.setNetEqnSolveConfig(odmObjFactory.createDynamicNetEqnSolveConfigXmlType());
        
        if (SwingInputVerifyUtil.largeThan(netEqnItrNoEventTextField, 0, errMsg,
        		"Network equation solution iteration count (no event) <= 0"))
        	xmlCaseData.getNetEqnSolveConfig().setNetEqnItrNoEvent(SwingInputVerifyUtil.getInt(netEqnItrNoEventTextField));
        
        if (SwingInputVerifyUtil.largeThan(netEqnItrWithEventTextField, 0, errMsg,
        		"Network equation solution iteration count (with event) <= 0"))
        	xmlCaseData.getNetEqnSolveConfig().setNetEqnItrWithEvent(SwingInputVerifyUtil.getInt(netEqnItrWithEventTextField));

        if (xmlCaseData.getStaticLoadModel() == null)
        	xmlCaseData.setStaticLoadModel(odmObjFactory.createDynamicStaticLoadModelXmlType());

        if (staticLoadCZRadioButton.isSelected()) {
        	xmlCaseData.getStaticLoadModel().setStaticLoadType(DynamicStaticLoadEnumType.CONST_Z);
        }
        else {
        	xmlCaseData.getStaticLoadModel().setStaticLoadType(DynamicStaticLoadEnumType.CONST_P);
            if (SwingInputVerifyUtil.largeEqualThan(staticLoadSwitchVoltTextField, 0.4d, errMsg,
            		"Static load model switching voltage < 0.4 pu"))
            	xmlCaseData.getStaticLoadModel().setSwitchVolt(SwingInputVerifyUtil.getDouble(staticLoadSwitchVoltTextField));

            if (SwingInputVerifyUtil.largeEqualThan(staticLoadSwitchDeadZoneTextField, 0.0d, errMsg,
    				"Static load model switching voltage dead zone< 0.0 pu"))
            	xmlCaseData.getStaticLoadModel().setSwitchDeadZone(SwingInputVerifyUtil.getDouble(staticLoadSwitchDeadZoneTextField));
        }

        xmlCaseData.setDisableDynEvents(disableEventCheckBox.isSelected());
        if(!xmlCaseData.isDisableDynEvents()) {
        	dynaEventPanel.saveEditor2Form(errMsg);
        }
        else {
        	if (xmlCaseData.getDynamicEvent().size() == 0)
        		xmlCaseData.getDynamicEvent().add(odmObjFactory.createDynamicEventXmlType());
        	DynamicEventXmlType event = xmlCaseData.getDynamicEvent().get(0); 
        	if (setPointCheckBox.isSelected()) {
        		event.setEventType(DynamicEventEnumType.SET_POINT_CHANGE);
        		event.getSetPointChangeData().getRefGenBus().setBusId((String)setPointMachineComboBox.getSelectedItem());
        		event.getSetPointChangeData().setControllerType(MachineControllerEnumType.fromValue((String)setPointControllerComboBox.getSelectedItem()));
        		event.getSetPointChangeData().setChangeValue(SwingInputVerifyUtil.getDouble(setPointValueTextField));
       			event.getSetPointChangeData().setValueChangeType(setPointAbsoluteRadioButton.isSelected()?
       					SetPointChangeEnumType.ABSOLUTE : SetPointChangeEnumType.DELTA	);
        	}
        	else {
        		event.setEventType(DynamicEventEnumType.NONE);
        	}
        }
        //IpssLogger.getLogger().info("" + xmlCaseData);

        if (xmlCaseData.getOutputConfig() == null)
        	xmlCaseData.setOutputConfig(odmObjFactory.createDynamicOutputConfigXmlType());

        xmlCaseData.getOutputConfig().setOutputFilter(outputFilterCheckBox.isSelected());

        if (xmlCaseData.getOutputScripting() == null)
        	xmlCaseData.setOutputScripting(odmObjFactory.createDynamicOutputScriptingXmlType());

        xmlCaseData.getOutputScripting().setScripting(outputScriptCheckBox.isSelected());
        if (outputScriptCheckBox.isSelected() && dstabOutputScriptFilename != null) {
        	GUIFileUtil.writeTextarea2FileAbsolutePath(dstabOutputScriptFilename, scriptTextArea);
        	xmlCaseData.getOutputScripting().setScriptingFilename(dstabOutputScriptFilename);
    	}        
        
		gridPanel.saveEditor2Form(errMsg);

		return errMsg.size() == 0;
	}
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        staticLoadButtonGroup = new javax.swing.ButtonGroup();
        setPointChangeButtonGroup = new javax.swing.ButtonGroup();
        detailInfoTabbedPane = new javax.swing.JTabbedPane();
        simulationPanel = new javax.swing.JPanel();
        simuMathodPanel = new javax.swing.JPanel();
        methodLabel = new javax.swing.JLabel();
        methodComboBox = new javax.swing.JComboBox();
        disableEventCheckBox = new javax.swing.JCheckBox();
        totalTImeLabel = new javax.swing.JLabel();
        totalTimeTextField = new javax.swing.JTextField();
        simuStepLabel = new javax.swing.JLabel();
        simuStepTextField = new javax.swing.JTextField();
        refMachinejPanel = new javax.swing.JPanel();
        refMachLabel = new javax.swing.JLabel();
        refMachComboBox = new javax.swing.JComboBox();
        absMachCheckBox = new javax.swing.JCheckBox();
        netEqnItrPanel = new javax.swing.JPanel();
        netEqnItrLabel = new javax.swing.JLabel();
        netEqnItrNoEventTextField = new javax.swing.JTextField();
        netEqnItrNoEventLabel = new javax.swing.JLabel();
        netEqnItrWithEventLabel = new javax.swing.JLabel();
        netEqnItrWithEventTextField = new javax.swing.JTextField();
        outputOptPanel = new javax.swing.JPanel();
        outputFilterCheckBox = new javax.swing.JCheckBox();
        outputScriptCheckBox = new javax.swing.JCheckBox();
        staticLoadPanel = new javax.swing.JPanel();
        staticLoadCZRadioButton = new javax.swing.JRadioButton();
        staticLoadCPRadioButton = new javax.swing.JRadioButton();
        staticLoadSwitchVoltLabel = new javax.swing.JLabel();
        staticLoadSwitchVoltTextField = new javax.swing.JTextField();
        staticLoadSwitchDeadZoneLabel = new javax.swing.JLabel();
        staticLoadSwitchDeadZoneTextField = new javax.swing.JTextField();
        gridComputingPanel = new javax.swing.JPanel();
        dynamicEventPanel = new javax.swing.JPanel();
        loadflowPanel = new javax.swing.JPanel();
        outputFilterPanel = new javax.swing.JPanel();
        outputVarLabel = new javax.swing.JLabel();
        outputVarScrollPane = new javax.swing.JScrollPane();
        outputVarList = new javax.swing.JList();
        machVarLabel = new javax.swing.JLabel();
        machOutVarScrollPane = new javax.swing.JScrollPane();
        machOutVarList = new javax.swing.JList();
        busOutVarLabel = new javax.swing.JLabel();
        busOutVarScrollPane = new javax.swing.JScrollPane();
        busOutVarList = new javax.swing.JList();
        branchOutVarLabel = new javax.swing.JLabel();
        branchVarScrollPane = new javax.swing.JScrollPane();
        branchOutVarList = new javax.swing.JList();
        addOutVarButton = new javax.swing.JButton();
        removeOurVarButton = new javax.swing.JButton();
        addAllOutVarButton = new javax.swing.JButton();
        outScriptingjPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        scriptTextArea = new javax.swing.JTextArea();
        validatePanel = new javax.swing.JPanel();
        setPointChangePanel = new javax.swing.JPanel();
        setPointCheckBox = new javax.swing.JCheckBox();
        setPointMachineLabel = new javax.swing.JLabel();
        setPointMachineComboBox = new javax.swing.JComboBox();
        setPointControllerLabel = new javax.swing.JLabel();
        setPointControllerComboBox = new javax.swing.JComboBox();
        setPointValueLabel = new javax.swing.JLabel();
        setPointValueTextField = new javax.swing.JTextField();
        setPointAbsoluteRadioButton = new javax.swing.JRadioButton();
        setPointDeltaRadioButton = new javax.swing.JRadioButton();

        setLayout(new java.awt.BorderLayout());

        detailInfoTabbedPane.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        detailInfoTabbedPane.setName("detailInfoTabbedPane"); // NOI18N

        simulationPanel.setFont(new java.awt.Font("Dialog", 0, 12));
        simulationPanel.setName("advancedPanel"); // NOI18N

        methodLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        methodLabel.setText("Simulation Method       ");

        methodComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        methodComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Modified Euler" }));
        methodComboBox.setName("methodComboBox"); // NOI18N

        disableEventCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        disableEventCheckBox.setText("Disable Dynamic Events");
        disableEventCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        disableEventCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        disableEventCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                disableEventCheckBoxActionPerformed(evt);
            }
        });

        totalTImeLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        totalTImeLabel.setText("Total Time(sec)   ");

        totalTimeTextField.setColumns(5);
        totalTimeTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        totalTimeTextField.setText("10.0");
        totalTimeTextField.setName("totalTimeTextField"); // NOI18N

        simuStepLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        simuStepLabel.setText("Simu Step(sec)   ");

        simuStepTextField.setColumns(4);
        simuStepTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        simuStepTextField.setText("0.05");
        simuStepTextField.setName("simuStepTextField"); // NOI18N

        org.jdesktop.layout.GroupLayout simuMathodPanelLayout = new org.jdesktop.layout.GroupLayout(simuMathodPanel);
        simuMathodPanel.setLayout(simuMathodPanelLayout);
        simuMathodPanelLayout.setHorizontalGroup(
            simuMathodPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(simuMathodPanelLayout.createSequentialGroup()
                .add(22, 22, 22)
                .add(simuMathodPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(methodLabel)
                    .add(totalTImeLabel))
                .add(32, 32, 32)
                .add(simuMathodPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(methodComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(totalTimeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(54, 54, 54)
                .add(simuMathodPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(simuMathodPanelLayout.createSequentialGroup()
                        .add(simuStepLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(simuStepTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(disableEventCheckBox))
                .add(25, 25, 25))
        );
        simuMathodPanelLayout.setVerticalGroup(
            simuMathodPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(simuMathodPanelLayout.createSequentialGroup()
                .add(7, 7, 7)
                .add(simuMathodPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(methodLabel)
                    .add(methodComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(disableEventCheckBox))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(simuMathodPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(simuMathodPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(simuStepLabel)
                        .add(simuStepTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(simuMathodPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(totalTImeLabel)
                        .add(totalTimeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        refMachinejPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 20, 5));

        refMachLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        refMachLabel.setText("Ref Machine     ");
        refMachinejPanel.add(refMachLabel);

        refMachComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        refMachComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        refMachinejPanel.add(refMachComboBox);

        absMachCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        absMachCheckBox.setText("Absolute Machine Angle");
        absMachCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        absMachCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        absMachCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                absMachCheckBoxActionPerformed(evt);
            }
        });
        refMachinejPanel.add(absMachCheckBox);

        netEqnItrLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        netEqnItrLabel.setText("Net Eqn Iterations      ");
        netEqnItrPanel.add(netEqnItrLabel);

        netEqnItrNoEventTextField.setColumns(2);
        netEqnItrNoEventTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        netEqnItrNoEventTextField.setText("3");
        netEqnItrNoEventTextField.setName("totalTimeTextField"); // NOI18N
        netEqnItrPanel.add(netEqnItrNoEventTextField);

        netEqnItrNoEventLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        netEqnItrNoEventLabel.setText("(No Event)      ");
        netEqnItrPanel.add(netEqnItrNoEventLabel);

        netEqnItrWithEventLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        netEqnItrWithEventLabel.setText("(With Event)   ");
        netEqnItrPanel.add(netEqnItrWithEventLabel);

        netEqnItrWithEventTextField.setColumns(2);
        netEqnItrWithEventTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        netEqnItrWithEventTextField.setText("5");
        netEqnItrWithEventTextField.setName("simuStepTextField"); // NOI18N
        netEqnItrPanel.add(netEqnItrWithEventTextField);

        outputOptPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Output Options", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        outputOptPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 30, 5));

        outputFilterCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        outputFilterCheckBox.setText("Output state/variable Filter");
        outputFilterCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        outputFilterCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        outputFilterCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outputFilterCheckBoxActionPerformed(evt);
            }
        });
        outputOptPanel.add(outputFilterCheckBox);

        outputScriptCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        outputScriptCheckBox.setText("Output Result Scripting");
        outputScriptCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        outputScriptCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        outputScriptCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outputScriptCheckBoxActionPerformed(evt);
            }
        });
        outputOptPanel.add(outputScriptCheckBox);

        staticLoadPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "static Load model", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N

        staticLoadButtonGroup.add(staticLoadCZRadioButton);
        staticLoadCZRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        staticLoadCZRadioButton.setSelected(true);
        staticLoadCZRadioButton.setText("Const-Z");
        staticLoadCZRadioButton.setName("staticLoadCZRadioButton"); // NOI18N
        staticLoadCZRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                staticLoadCZRadioButtonActionPerformed(evt);
            }
        });

        staticLoadButtonGroup.add(staticLoadCPRadioButton);
        staticLoadCPRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        staticLoadCPRadioButton.setText("Const-P");
        staticLoadCPRadioButton.setName("staticLoadCPRadioButton"); // NOI18N
        staticLoadCPRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                staticLoadCPRadioButtonActionPerformed(evt);
            }
        });

        staticLoadSwitchVoltLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        staticLoadSwitchVoltLabel.setText("Switch Volt(pu)   ");
        staticLoadSwitchVoltLabel.setToolTipText("Constant-P model will be switched to the Constant-Z model when the voltage is lower than the switch voltage.");
        staticLoadSwitchVoltLabel.setEnabled(false);

        staticLoadSwitchVoltTextField.setColumns(4);
        staticLoadSwitchVoltTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        staticLoadSwitchVoltTextField.setText("0.65");
        staticLoadSwitchVoltTextField.setEnabled(false);
        staticLoadSwitchVoltTextField.setName("staticLoadSwitchVoltTextField"); // NOI18N

        staticLoadSwitchDeadZoneLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        staticLoadSwitchDeadZoneLabel.setText("     DeadZone(pu)   ");
        staticLoadSwitchDeadZoneLabel.setToolTipText("Constant-P model will be switched to the Constant-Z model when the voltage is lower than the switch voltage.");
        staticLoadSwitchDeadZoneLabel.setAlignmentY(0.05F);
        staticLoadSwitchDeadZoneLabel.setEnabled(false);

        staticLoadSwitchDeadZoneTextField.setColumns(4);
        staticLoadSwitchDeadZoneTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        staticLoadSwitchDeadZoneTextField.setText("0.05");
        staticLoadSwitchDeadZoneTextField.setAlignmentY(0.05F);
        staticLoadSwitchDeadZoneTextField.setEnabled(false);
        staticLoadSwitchDeadZoneTextField.setName("staticLoadSwitchVoltTextField"); // NOI18N

        org.jdesktop.layout.GroupLayout staticLoadPanelLayout = new org.jdesktop.layout.GroupLayout(staticLoadPanel);
        staticLoadPanel.setLayout(staticLoadPanelLayout);
        staticLoadPanelLayout.setHorizontalGroup(
            staticLoadPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, staticLoadPanelLayout.createSequentialGroup()
                .addContainerGap(95, Short.MAX_VALUE)
                .add(staticLoadPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(staticLoadPanelLayout.createSequentialGroup()
                        .add(50, 50, 50)
                        .add(staticLoadCZRadioButton)
                        .add(58, 58, 58)
                        .add(staticLoadCPRadioButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 53, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, staticLoadPanelLayout.createSequentialGroup()
                        .add(staticLoadSwitchVoltLabel)
                        .add(24, 24, 24)
                        .add(staticLoadSwitchVoltTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(staticLoadSwitchDeadZoneLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(staticLoadSwitchDeadZoneTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(51, 51, 51))
        );
        staticLoadPanelLayout.setVerticalGroup(
            staticLoadPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(staticLoadPanelLayout.createSequentialGroup()
                .add(staticLoadPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(staticLoadCZRadioButton)
                    .add(staticLoadCPRadioButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(staticLoadPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(staticLoadSwitchDeadZoneTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(staticLoadPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(staticLoadSwitchVoltLabel)
                        .add(staticLoadSwitchVoltTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(staticLoadPanelLayout.createSequentialGroup()
                        .add(2, 2, 2)
                        .add(staticLoadSwitchDeadZoneLabel)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridComputingPanel.setEnabled(false);
        gridComputingPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        org.jdesktop.layout.GroupLayout simulationPanelLayout = new org.jdesktop.layout.GroupLayout(simulationPanel);
        simulationPanel.setLayout(simulationPanelLayout);
        simulationPanelLayout.setHorizontalGroup(
            simulationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(simulationPanelLayout.createSequentialGroup()
                .add(29, 29, 29)
                .add(simulationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(gridComputingPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 544, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(staticLoadPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(outputOptPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(netEqnItrPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(refMachinejPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(simuMathodPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 512, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(38, 38, 38))
        );
        simulationPanelLayout.setVerticalGroup(
            simulationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(simulationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(simuMathodPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(refMachinejPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(netEqnItrPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(2, 2, 2)
                .add(outputOptPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(staticLoadPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 78, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gridComputingPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 109, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(119, Short.MAX_VALUE))
        );

        detailInfoTabbedPane.addTab("Simulation", simulationPanel);

        dynamicEventPanel.setFont(new java.awt.Font("Dialog", 0, 12));
        dynamicEventPanel.setName("dynamicEventPanel"); // NOI18N
        detailInfoTabbedPane.addTab("Dynamic Events", dynamicEventPanel);

        loadflowPanel.setFont(new java.awt.Font("Dialog", 0, 12));
        detailInfoTabbedPane.addTab("Initialization", loadflowPanel);

        outputFilterPanel.setEnabled(false);
        outputFilterPanel.setFont(new java.awt.Font("Dialog", 0, 12));

        outputVarLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        outputVarLabel.setText("Output States/Variables");

        outputVarList.setFont(new java.awt.Font("Dialog", 0, 12));
        outputVarList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        outputVarScrollPane.setViewportView(outputVarList);

        machVarLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        machVarLabel.setText("Machine States");

        machOutVarList.setFont(new java.awt.Font("Dialog", 0, 12));
        machOutVarList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        machOutVarScrollPane.setViewportView(machOutVarList);

        busOutVarLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        busOutVarLabel.setText("Bus Variables");

        busOutVarList.setFont(new java.awt.Font("Dialog", 0, 12));
        busOutVarList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        busOutVarScrollPane.setViewportView(busOutVarList);

        branchOutVarLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        branchOutVarLabel.setText("Branch Variables");
        branchOutVarLabel.setEnabled(false);

        branchOutVarList.setFont(new java.awt.Font("Dialog", 0, 12));
        branchOutVarList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        branchOutVarList.setEnabled(false);
        branchVarScrollPane.setViewportView(branchOutVarList);

        addOutVarButton.setFont(new java.awt.Font("Dialog", 0, 12));
        addOutVarButton.setText(">");
        addOutVarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addOutVarButtonActionPerformed(evt);
            }
        });

        removeOurVarButton.setFont(new java.awt.Font("Dialog", 0, 12));
        removeOurVarButton.setText("<");
        removeOurVarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeOurVarButtonActionPerformed(evt);
            }
        });

        addAllOutVarButton.setFont(new java.awt.Font("Dialog", 0, 12));
        addAllOutVarButton.setText("All > ");
        addAllOutVarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addAllOutVarButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout outputFilterPanelLayout = new org.jdesktop.layout.GroupLayout(outputFilterPanel);
        outputFilterPanel.setLayout(outputFilterPanelLayout);
        outputFilterPanelLayout.setHorizontalGroup(
            outputFilterPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(outputFilterPanelLayout.createSequentialGroup()
                .add(25, 25, 25)
                .add(outputFilterPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(outputFilterPanelLayout.createSequentialGroup()
                        .add(machVarLabel)
                        .add(240, 240, 240))
                    .add(outputFilterPanelLayout.createSequentialGroup()
                        .add(outputFilterPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(machOutVarScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                            .add(busOutVarLabel)
                            .add(busOutVarScrollPane)
                            .add(outputFilterPanelLayout.createSequentialGroup()
                                .add(branchOutVarLabel)
                                .add(101, 101, 101))
                            .add(branchVarScrollPane))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 36, Short.MAX_VALUE)
                        .add(outputFilterPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(addAllOutVarButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(removeOurVarButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(addOutVarButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .add(34, 34, 34)))
                .add(4, 4, 4)
                .add(outputFilterPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(outputVarLabel)
                    .add(outputVarScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 211, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(56, 56, 56))
        );
        outputFilterPanelLayout.setVerticalGroup(
            outputFilterPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(outputFilterPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(outputFilterPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(outputFilterPanelLayout.createSequentialGroup()
                        .add(outputFilterPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(machVarLabel)
                            .add(outputVarLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(outputFilterPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(outputFilterPanelLayout.createSequentialGroup()
                                .add(machOutVarScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 102, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(busOutVarLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(busOutVarScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(18, 18, 18)
                                .add(branchOutVarLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(branchVarScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(outputVarScrollPane)))
                    .add(outputFilterPanelLayout.createSequentialGroup()
                        .add(159, 159, 159)
                        .add(addOutVarButton)
                        .add(22, 22, 22)
                        .add(removeOurVarButton)
                        .add(20, 20, 20)
                        .add(addAllOutVarButton)))
                .addContainerGap(124, Short.MAX_VALUE))
        );

        detailInfoTabbedPane.addTab("Output Filter", outputFilterPanel);

        outScriptingjPanel.setFont(new java.awt.Font("Dialog", 0, 12));

        scriptTextArea.setColumns(20);
        scriptTextArea.setFont(new java.awt.Font("Courier New", 0, 12));
        scriptTextArea.setRows(5);
        jScrollPane1.setViewportView(scriptTextArea);

        org.jdesktop.layout.GroupLayout outScriptingjPanelLayout = new org.jdesktop.layout.GroupLayout(outScriptingjPanel);
        outScriptingjPanel.setLayout(outScriptingjPanelLayout);
        outScriptingjPanelLayout.setHorizontalGroup(
            outScriptingjPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(outScriptingjPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 578, Short.MAX_VALUE)
                .addContainerGap())
        );
        outScriptingjPanelLayout.setVerticalGroup(
            outScriptingjPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(outScriptingjPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 402, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(119, Short.MAX_VALUE))
        );

        detailInfoTabbedPane.addTab("Output Scripting", outScriptingjPanel);

        setPointChangePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Controller SetPoint Change", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        setPointChangePanel.setEnabled(false);

        setPointCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        setPointCheckBox.setText("SetPoint Change");
        setPointCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setPointCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        setPointCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setPointCheckBoxActionPerformed(evt);
            }
        });

        setPointMachineLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        setPointMachineLabel.setText("Machine");

        setPointMachineComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        setPointMachineComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        setPointMachineComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setPointMachineComboBoxsetPointMachineListActionPerformed(evt);
            }
        });

        setPointControllerLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        setPointControllerLabel.setText("Controller");

        setPointControllerComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        setPointControllerComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        setPointValueLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        setPointValueLabel.setText("SetPoint Value Change(pu)");

        setPointValueTextField.setColumns(3);
        setPointValueTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        setPointValueTextField.setText("0.0");

        setPointChangeButtonGroup.add(setPointAbsoluteRadioButton);
        setPointAbsoluteRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        setPointAbsoluteRadioButton.setText("Absolute");
        setPointAbsoluteRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setPointAbsoluteRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        setPointChangeButtonGroup.add(setPointDeltaRadioButton);
        setPointDeltaRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        setPointDeltaRadioButton.setSelected(true);
        setPointDeltaRadioButton.setText("Delta");
        setPointDeltaRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setPointDeltaRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        org.jdesktop.layout.GroupLayout setPointChangePanelLayout = new org.jdesktop.layout.GroupLayout(setPointChangePanel);
        setPointChangePanel.setLayout(setPointChangePanelLayout);
        setPointChangePanelLayout.setHorizontalGroup(
            setPointChangePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(setPointChangePanelLayout.createSequentialGroup()
                .add(setPointChangePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(setPointChangePanelLayout.createSequentialGroup()
                        .add(30, 30, 30)
                        .add(setPointCheckBox)
                        .add(29, 29, 29)
                        .add(setPointMachineLabel)
                        .add(26, 26, 26)
                        .add(setPointMachineComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(35, 35, 35)
                        .add(setPointControllerLabel)
                        .add(23, 23, 23)
                        .add(setPointControllerComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(setPointChangePanelLayout.createSequentialGroup()
                        .add(57, 57, 57)
                        .add(setPointValueLabel)
                        .add(40, 40, 40)
                        .add(setPointValueTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(22, 22, 22)
                        .add(setPointAbsoluteRadioButton)
                        .add(35, 35, 35)
                        .add(setPointDeltaRadioButton)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        setPointChangePanelLayout.setVerticalGroup(
            setPointChangePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(setPointChangePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(setPointChangePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(setPointCheckBox)
                    .add(setPointControllerComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(setPointControllerLabel)
                    .add(setPointMachineLabel)
                    .add(setPointMachineComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(setPointChangePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(setPointValueLabel)
                    .add(setPointValueTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(setPointDeltaRadioButton)
                    .add(setPointAbsoluteRadioButton))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout validatePanelLayout = new org.jdesktop.layout.GroupLayout(validatePanel);
        validatePanel.setLayout(validatePanelLayout);
        validatePanelLayout.setHorizontalGroup(
            validatePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(validatePanelLayout.createSequentialGroup()
                .add(42, 42, 42)
                .add(setPointChangePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(126, 126, 126))
        );
        validatePanelLayout.setVerticalGroup(
            validatePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(validatePanelLayout.createSequentialGroup()
                .add(26, 26, 26)
                .add(setPointChangePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(404, Short.MAX_VALUE))
        );

        detailInfoTabbedPane.addTab("Advanced", validatePanel);

        add(detailInfoTabbedPane, java.awt.BorderLayout.NORTH);
    }// </editor-fold>//GEN-END:initComponents

    private void addOutVarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addOutVarButtonActionPerformed
    	List<String> outVarList = this.xmlCaseData.getOutputConfig().getOutputVariable();
    	for (Object obj : this.machOutVarList.getSelectedValues()) {
    		String id = Constants.Token_FilterMachVar + (String)obj;
    		if (!StringUtil.contain(outVarList, id))
    			outVarList.add(id);
    	}
    	
    	for (Object obj : this.busOutVarList.getSelectedValues()) {
    		String id = Constants.Token_FilterBusVar + (String)obj;
    		if (!StringUtil.contain(outVarList, id))
    			outVarList.add(id);
    	}
    	
    	for (Object obj : this.branchOutVarList.getSelectedValues()) {
    		String id = Constants.Token_FilterBranchVar + (String)obj;
    		if (!StringUtil.contain(outVarList, id))
    			outVarList.add(id);
    	}
    	
    	setOutputVarList();
    }//GEN-LAST:event_addOutVarButtonActionPerformed

    private void removeOurVarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeOurVarButtonActionPerformed
    	List<String> outVarList = this.xmlCaseData.getOutputConfig().getOutputVariable();
    	for (Object obj : this.outputVarList.getSelectedValues())
    		outVarList.remove(obj);
    	setOutputVarList();
    }//GEN-LAST:event_removeOurVarButtonActionPerformed

    private void addAllOutVarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAllOutVarButtonActionPerformed
    	List<String> outVarList = this.xmlCaseData.getOutputConfig().getOutputVariable();
    	if (this.netContainer != null) {
        	for (Object obj : this.netContainer.getMachIdArray())
        		if (!StringUtil.contain(outVarList, (String)obj))
        			this.xmlCaseData.getOutputConfig().getOutputVariable().add((String)obj);
        	for (Object obj : this.netContainer.getBusIdArray())
        		if (!StringUtil.contain(outVarList, (String)obj))
        			this.xmlCaseData.getOutputConfig().getOutputVariable().add((String)obj);
    	}
    	else {
        	for (Object obj : GetMachineIdAry.f(simuCtx.getDStabilityNet()))
        		if (!StringUtil.contain(outVarList, (String)obj))
        			this.xmlCaseData.getOutputConfig().getOutputVariable().add((String)obj);
        	for (Object obj : CoreUtilFunc.getBusIdArray(simuCtx.getDStabilityNet()))
        		if (!StringUtil.contain(outVarList, (String)obj))
        			this.xmlCaseData.getOutputConfig().getOutputVariable().add((String)obj);
    	}
    	setOutputVarList();
    }//GEN-LAST:event_addAllOutVarButtonActionPerformed

    private void outputScriptCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outputScriptCheckBoxActionPerformed
        setOutputScriptingPanel(outputScriptCheckBox.isSelected());
        if (outputScriptCheckBox.isSelected()) {
            if (dstabOutputScriptFilename != null) {
            	IpssLogger.getLogger().info("scriptFilename: " + dstabOutputScriptFilename);
            	GUIFileUtil.readFile2TextareaRativePath(dstabOutputScriptFilename, scriptTextArea);
            	if (scriptTextArea.getText().trim().equals(""))
                	GUIFileUtil.readFile2TextareaRativePath(OutpuScriptTemplateFilename, scriptTextArea);
            }         
        }
    }//GEN-LAST:event_outputScriptCheckBoxActionPerformed

    private void outputFilterCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outputFilterCheckBoxActionPerformed
        setOutputFilterPanel(outputFilterCheckBox.isSelected());
    }//GEN-LAST:event_outputFilterCheckBoxActionPerformed

    private void absMachCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_absMachCheckBoxActionPerformed
        refMachLabel.setEnabled(!absMachCheckBox.isSelected());
        refMachComboBox.setEnabled(!absMachCheckBox.isSelected());
    }//GEN-LAST:event_absMachCheckBoxActionPerformed

    private void disableEventCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_disableEventCheckBoxActionPerformed
       	detailInfoTabbedPane.setEnabledAt(1, !disableEventCheckBox.isSelected());
        setSetpointPanel(disableEventCheckBox.isSelected());
    }//GEN-LAST:event_disableEventCheckBoxActionPerformed

    private void staticLoadCPRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_staticLoadCPRadioButtonActionPerformed
        setStaticLoadCPStatus(true);
    }//GEN-LAST:event_staticLoadCPRadioButtonActionPerformed

    private void staticLoadCZRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_staticLoadCZRadioButtonActionPerformed
        setStaticLoadCPStatus(false);
    }//GEN-LAST:event_staticLoadCZRadioButtonActionPerformed

    private void setPointCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setPointCheckBoxActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_setPointCheckBoxActionPerformed

    private void setPointMachineComboBoxsetPointMachineListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setPointMachineComboBoxsetPointMachineListActionPerformed
    	IpssLogger.getLogger().info("Details ...");
  		IOutputTextDialog dialog = UISpringFactory.getOutputTextDialog("Loadflow Analysis Info");
  		dialog.display("TODO ....");
  	}//GEN-LAST:event_setPointMachineComboBoxsetPointMachineListActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox absMachCheckBox;
    private javax.swing.JButton addAllOutVarButton;
    private javax.swing.JButton addOutVarButton;
    private javax.swing.JLabel branchOutVarLabel;
    private javax.swing.JList branchOutVarList;
    private javax.swing.JScrollPane branchVarScrollPane;
    private javax.swing.JLabel busOutVarLabel;
    private javax.swing.JList busOutVarList;
    private javax.swing.JScrollPane busOutVarScrollPane;
    private javax.swing.JTabbedPane detailInfoTabbedPane;
    private javax.swing.JCheckBox disableEventCheckBox;
    private javax.swing.JPanel dynamicEventPanel;
    private javax.swing.JPanel gridComputingPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel loadflowPanel;
    private javax.swing.JList machOutVarList;
    private javax.swing.JScrollPane machOutVarScrollPane;
    private javax.swing.JLabel machVarLabel;
    private javax.swing.JComboBox methodComboBox;
    private javax.swing.JLabel methodLabel;
    private javax.swing.JLabel netEqnItrLabel;
    private javax.swing.JLabel netEqnItrNoEventLabel;
    private javax.swing.JTextField netEqnItrNoEventTextField;
    private javax.swing.JPanel netEqnItrPanel;
    private javax.swing.JLabel netEqnItrWithEventLabel;
    private javax.swing.JTextField netEqnItrWithEventTextField;
    private javax.swing.JPanel outScriptingjPanel;
    private javax.swing.JCheckBox outputFilterCheckBox;
    private javax.swing.JPanel outputFilterPanel;
    private javax.swing.JPanel outputOptPanel;
    private javax.swing.JCheckBox outputScriptCheckBox;
    private javax.swing.JLabel outputVarLabel;
    private javax.swing.JList outputVarList;
    private javax.swing.JScrollPane outputVarScrollPane;
    private javax.swing.JComboBox refMachComboBox;
    private javax.swing.JLabel refMachLabel;
    private javax.swing.JPanel refMachinejPanel;
    private javax.swing.JButton removeOurVarButton;
    private javax.swing.JTextArea scriptTextArea;
    private javax.swing.JRadioButton setPointAbsoluteRadioButton;
    private javax.swing.ButtonGroup setPointChangeButtonGroup;
    private javax.swing.JPanel setPointChangePanel;
    private javax.swing.JCheckBox setPointCheckBox;
    private javax.swing.JComboBox setPointControllerComboBox;
    private javax.swing.JLabel setPointControllerLabel;
    private javax.swing.JRadioButton setPointDeltaRadioButton;
    private javax.swing.JComboBox setPointMachineComboBox;
    private javax.swing.JLabel setPointMachineLabel;
    private javax.swing.JLabel setPointValueLabel;
    private javax.swing.JTextField setPointValueTextField;
    private javax.swing.JPanel simuMathodPanel;
    private javax.swing.JLabel simuStepLabel;
    private javax.swing.JTextField simuStepTextField;
    private javax.swing.JPanel simulationPanel;
    private javax.swing.ButtonGroup staticLoadButtonGroup;
    private javax.swing.JRadioButton staticLoadCPRadioButton;
    private javax.swing.JRadioButton staticLoadCZRadioButton;
    private javax.swing.JPanel staticLoadPanel;
    private javax.swing.JLabel staticLoadSwitchDeadZoneLabel;
    private javax.swing.JTextField staticLoadSwitchDeadZoneTextField;
    private javax.swing.JLabel staticLoadSwitchVoltLabel;
    private javax.swing.JTextField staticLoadSwitchVoltTextField;
    private javax.swing.JLabel totalTImeLabel;
    private javax.swing.JTextField totalTimeTextField;
    private javax.swing.JPanel validatePanel;
    // End of variables declaration//GEN-END:variables
    
	class DataVerifier extends javax.swing.InputVerifier {
		public boolean verify(javax.swing.JComponent input) {
			if (input == null)
				return false;
			try {
       			if (input == totalTimeTextField ||
           			    input == simuStepTextField ||
           			    input == staticLoadSwitchVoltTextField)
     	       			return SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) > 0.0;
			} catch (Exception e) {
				return false;
			}		
			return true;
		}
	}
}
