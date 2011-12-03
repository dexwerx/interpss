 /*
  * @(#)NBPTradingCasePanel.java   
  *
  * Copyright (C) 2006-2011 www.interpss.org
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

import java.io.File;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFileChooser;

import org.ieee.odm.schema.PTradingAnalysisXmlType;
import org.ieee.odm.schema.PtAclfAnalysisXmlType;
import org.ieee.odm.schema.PtAclfOutputXmlType;
import org.ieee.odm.schema.PtCaseDataXmlType;
import org.interpss.editor.jgraph.GraphSpringAppContext;
import org.interpss.editor.jgraph.ui.app.IAppStatus;
import org.interpss.editor.jgraph.ui.edit.IFormDataPanel;
import org.interpss.editor.ui.IOutputTextDialog;
import org.interpss.editor.ui.UISpringAppContext;
import org.interpss.editor.ui.util.IpssFileFilter;
import org.interpss.numeric.util.Number2String;
import org.interpss.ui.SwingInputVerifyUtil;

import com.interpss.common.datatype.Constants;
import com.interpss.common.exp.InterpssException;
import com.interpss.common.exp.InterpssRuntimeException;
import com.interpss.common.msg.IpssMessage;
import com.interpss.common.msg.IpssMsgListener;
import com.interpss.common.util.IpssLogger;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.pssl.simu.IpssAclf;
import com.interpss.pssl.simu.IpssAclf.LfAlgoDSL;
import com.interpss.pssl.simu.impl.AclfODMRunner;
import com.interpss.simu.SimuContext;
import com.interpss.spring.CoreCommonSpringCtx;

public class NBPTradingCasePanel extends javax.swing.JPanel implements IFormDataPanel, IpssMsgListener {
	private static final long serialVersionUID = 1;
	private JDialog parent;
	
    // private GFormContainer _netContainer = null;
    private SimuContext _simuCtx = null;
    private PTradingAnalysisXmlType _ptXml = null;

    /** Creates new form NBAclfCasePanel */
    public NBPTradingCasePanel(JDialog parent) {
    	initComponents();
    	this.parent = parent;
    	
    	initInputVerifier(new DataVerifier());
    }
    
    /**
     * Implementation of the onMsgEvent method.
  	* 
  	* @param msg the msg object
     */
     public void onMsgEvent(IpssMessage msg) {
    	 // do nothing
     }

     public boolean onMsgEventStatus(IpssMessage msg) {
  	   throw new InterpssRuntimeException("Method not implemented");
     }
     
    public void init(Object netContainer, Object simuCtx) {
    	// for non-graphic file, netContainer == null
		IpssLogger.getLogger().info("NBPTradingCasePanel init() called");
	    //_netContainer = (GFormContainer)netContainer;
	    _simuCtx = (SimuContext)simuCtx;
	    
	    // populate the Swing alloc zone
	    String[] strAry = new String[] {"1", "12"};
	    this.swingAllocZoneComboBox.setModel(new javax.swing.DefaultComboBoxModel(strAry));
	}
    
    public void setXmlCaseData(PTradingAnalysisXmlType pt) {
    	this._ptXml = pt;
    }
	/**
	*	Set form data to the editor
	*
	* @return false if there is any problem
	*/
	public boolean setForm2Editor() {
		IpssLogger.getLogger().info("NBPTradingCasePanel setForm2Editor() called");
		
		// Case Data Panel
		PtCaseDataXmlType casedata = this._ptXml.getCaseData();
		edFileTextField.setText(casedata.getEdFile().getFilename());
		
		edGenPFacorTextField.setText(Number2String.toStr(casedata.getEdFile().getGenPFactor(), "#0.00"));
		edLossPercentTextField.setText(Number2String.toStr(casedata.getEdFile().getLossPercent(), "#0.00"));
		edLoadPFacorTextField.setText(Number2String.toStr(casedata.getEdFile().getLoadPFactor(), "#0.00"));
		
		interfaceFileTextField.setText(casedata.getInterfaceFile().getInterfaceFilename());
		interfaceLimitTextField.setText(casedata.getInterfaceFile().getLimitFilename());
		
		// Aclf Analysis
		PtAclfAnalysisXmlType aclfXml = this._ptXml.getAclfAnalysis();

		edHourComboBox.setSelectedItem(aclfXml.getHour());

		incMustRunGenCheckBox.setSelected(aclfXml.isIncludeMustRun());
		if (aclfXml.isIncludeMustRun()) {
			mustRunFileTextField.setText(aclfXml.getMustRunGen().getFilename());
		}
		
		genQAdjCheckBox.setSelected(aclfXml.isIncludeGenQAdjustment());
		if (aclfXml.isIncludeGenQAdjustment()) {
			mustRunQAdjStespTextField.setText(
					new Integer(aclfXml.getGenQAdjOption().getNoRunsMustRun()).toString());
		}
		
		swingAllocCheckBox.setSelected(aclfXml.isIncludeSwingAlloc());
		if (aclfXml.isIncludeSwingAlloc()) {
			String n = new Long(aclfXml.getGenSwingAllocOption().getZoneNumber()).toString();
			this.swingAllocZoneComboBox.setSelectedItem(n);
			swingAllocMaxStepsTextField.setText(
					new Integer(aclfXml.getGenSwingAllocOption().getSteps()).toString());
			swingAllocAccFactorTextField.setText(
					new Double(aclfXml.getGenSwingAllocOption().getAccFactor()).toString());
		}
		
		// output panel
		PtAclfOutputXmlType outOpt = this._ptXml.getAclfAnalysis().getOutputOption();
		largeGSFPointsTextField.setText(
					new Integer(outOpt.getLargeGSFPoints()).toString());
		this.outVoltViolationCheckBox.setSelected(outOpt.isBusVoltageViolation());
		this.voltUpperLimitTextField.setText(
				new Double(outOpt.getBusVoltageUpperLimitPU()).toString());
		this.voltLowerLimitTextField.setText(
				new Double(outOpt.getBusVoltageLowerLimitPU()).toString());
		this.outBranchViolationCheckBox.setSelected(outOpt.isBranchLimitViolation());
		this.outInterfaceViolationCheckBox.setSelected(outOpt.isInterfaceLimitViolation());
		this.outZoneSummaryCheckBox.setSelected(outOpt.isZoneSummary());
		this.outAreaSummaryCheckBox.setSelected(outOpt.isAreaSummary());
		this.outMustRunSummaryCheckBox.setSelected(outOpt.isMustRunSummary());

		return true;
	}

	/**
	*	Save editor screen data to the form
	*
	* @param errMsg error messages during the saving process.
	* @return false if there is any problem
	*/
	public boolean saveEditor2Form(Vector<String> errMsg) throws Exception {
		IpssLogger.getLogger().info("NBPTradingCasePanel saveEditor2Form() called");
		
		saveCaseData(errMsg);
		saveAclfAnalysis(errMsg);
		saveLineOutage(errMsg);
		saveOutputConfig(errMsg);

		return errMsg.size() == 0;
	}

	public boolean saveCaseData(Vector<String> errMsg) {
		boolean noError = true;
		PtCaseDataXmlType casedata = this._ptXml.getCaseData();
		
		casedata.getEdFile().setFilename(
				edFileTextField.getText());
		casedata.getEdFile().setGenPFactor(
				new Double(edGenPFacorTextField.getText()).doubleValue());
		casedata.getEdFile().setLossPercent(
				new Double(edLossPercentTextField.getText()).doubleValue());
		casedata.getEdFile().setLoadPFactor(
				new Double(edLoadPFacorTextField.getText()).doubleValue());
		
		casedata.getInterfaceFile().setInterfaceFilename(
				interfaceFileTextField.getText());
		casedata.getInterfaceFile().setLimitFilename(
				interfaceLimitTextField.getText());

		return noError;
	}

	public boolean saveAclfAnalysis(Vector<String> errMsg) {
		PtAclfAnalysisXmlType aclfXml = this._ptXml.getAclfAnalysis();
		
		aclfXml.setHour(this.edHourComboBox.getSelectedItem().toString());

		aclfXml.setIncludeMustRun(incMustRunGenCheckBox.isSelected());
		if (aclfXml.isIncludeMustRun()) {
			aclfXml.getMustRunGen().setFilename(mustRunFileTextField.getText());
		}
		
		aclfXml.setIncludeGenQAdjustment(genQAdjCheckBox.isSelected());
		if (aclfXml.isIncludeGenQAdjustment()) {
			aclfXml.getGenQAdjOption().setNoRunsMustRun(
					new Integer(mustRunQAdjStespTextField.getText()).intValue());
		}
		
		aclfXml.setIncludeSwingAlloc(swingAllocCheckBox.isSelected());
		if (aclfXml.isIncludeSwingAlloc()) {
			String n = (String)this.swingAllocZoneComboBox.getSelectedItem();
			aclfXml.getGenSwingAllocOption().setZoneNumber(
					new Long(n).longValue());
			aclfXml.getGenSwingAllocOption().setSteps(
					new Integer(swingAllocMaxStepsTextField.getText()).intValue());
			aclfXml.getGenSwingAllocOption().setAccFactor(
					new Double(swingAllocAccFactorTextField.getText()).doubleValue());
		}
		
		boolean noError = true;
		return noError;
	}

	public boolean saveLineOutage(Vector<String> errMsg) {
		boolean noError = true;
		
		return noError;
	}

	public boolean saveOutputConfig(Vector<String> errMsg) {
		boolean noError = true;

		PtAclfOutputXmlType outOpt = this._ptXml.getAclfAnalysis().getOutputOption();
		outOpt.setLargeGSFPoints(
					new Integer(largeGSFPointsTextField.getText()).intValue());
		outOpt.setBusVoltageViolation(this.outVoltViolationCheckBox.isSelected());
		outOpt.setBusVoltageUpperLimitPU(
				new Double(this.voltUpperLimitTextField.getText()).doubleValue());
		outOpt.setBusVoltageLowerLimitPU(
				new Double(this.voltLowerLimitTextField.getText()).doubleValue());
		outOpt.setBranchLimitViolation(this.outBranchViolationCheckBox.isSelected());
		outOpt.setInterfaceLimitViolation(this.outInterfaceViolationCheckBox.isSelected());
		outOpt.setZoneSummary(this.outZoneSummaryCheckBox.isSelected());
		outOpt.setAreaSummary(this.outAreaSummaryCheckBox.isSelected());
		outOpt.setMustRunSummary(this.outMustRunSummaryCheckBox.isSelected());

		return noError;
	}

	/** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pTradingAnalysisTabbedPane = new javax.swing.JTabbedPane();
        caseDataPanel = new javax.swing.JPanel();
        edFilePanel = new javax.swing.JPanel();
        edFileLabel = new javax.swing.JLabel();
        edFileTextField = new javax.swing.JTextField();
        selectEdFileButton = new javax.swing.JButton();
        edGenPFacorTextField = new javax.swing.JTextField();
        edGenPFactorLabel = new javax.swing.JLabel();
        edLoadPFacorTextField = new javax.swing.JTextField();
        edLoadPFactorLabel = new javax.swing.JLabel();
        edLossPercentTextField = new javax.swing.JTextField();
        edLossPercentLabel = new javax.swing.JLabel();
        interfaceFilePanel = new javax.swing.JPanel();
        interfaceFileLabel = new javax.swing.JLabel();
        interfaceFileTextField = new javax.swing.JTextField();
        selectInterfaceFileButton = new javax.swing.JButton();
        interfaceLimitLabel = new javax.swing.JLabel();
        interfaceLimitTextField = new javax.swing.JTextField();
        selectInterfaceLimitButton = new javax.swing.JButton();
        aclfAnalysisPanel = new javax.swing.JPanel();
        edHourLabel = new javax.swing.JLabel();
        edHourComboBox = new javax.swing.JComboBox();
        incMustRunGenCheckBox = new javax.swing.JCheckBox();
        mustRunFileLabel = new javax.swing.JLabel();
        mustRunFileTextField = new javax.swing.JTextField();
        mustRunFileSelectButton = new javax.swing.JButton();
        genQAdjCheckBox = new javax.swing.JCheckBox();
        mustRunQAdjStepsLabel = new javax.swing.JLabel();
        mustRunQAdjStespTextField = new javax.swing.JTextField();
        swingAllocCheckBox = new javax.swing.JCheckBox();
        swingAllocZoneLabel = new javax.swing.JLabel();
        swingAllocZoneComboBox = new javax.swing.JComboBox();
        swingAllocMaxStepsLabel = new javax.swing.JLabel();
        swingAllocMaxStepsTextField = new javax.swing.JTextField();
        swingAllocAccFactorLabel = new javax.swing.JLabel();
        swingAllocAccFactorTextField = new javax.swing.JTextField();
        runAclfAnalysisButton = new javax.swing.JButton();
        lineOutagePanel = new javax.swing.JPanel();
        runLineOutgageButton = new javax.swing.JButton();
        outputConfigPanel = new javax.swing.JPanel();
        outVoltViolationCheckBox = new javax.swing.JCheckBox();
        voltUpperLimitLabel = new javax.swing.JLabel();
        voltUpperLimitTextField = new javax.swing.JTextField();
        voltLowerLimitLabel = new javax.swing.JLabel();
        voltLowerLimitTextField = new javax.swing.JTextField();
        outBranchViolationCheckBox = new javax.swing.JCheckBox();
        outInterfaceViolationCheckBox = new javax.swing.JCheckBox();
        outZoneSummaryCheckBox = new javax.swing.JCheckBox();
        outAreaSummaryCheckBox = new javax.swing.JCheckBox();
        outMustRunSummaryCheckBox = new javax.swing.JCheckBox();
        largeGSFPointsLabel = new javax.swing.JLabel();
        largeGSFPointsTextField = new javax.swing.JTextField();

        pTradingAnalysisTabbedPane.setFont(new java.awt.Font("Dialog", 0, 12));
        pTradingAnalysisTabbedPane.setMinimumSize(new java.awt.Dimension(80, 48));
        pTradingAnalysisTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                panelSelectionChanged(evt);
            }
        });

        edFilePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "EDispatch Info", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N

        edFileLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        edFileLabel.setText("Daily ED File");

        edFileTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        edFileTextField.setText("ED file");

        selectEdFileButton.setFont(new java.awt.Font("Dialog", 0, 10));
        selectEdFileButton.setText("Select ...");
        selectEdFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectEdFileButtonActionPerformed(evt);
            }
        });

        edGenPFacorTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        edGenPFacorTextField.setText("0.90");

        edGenPFactorLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        edGenPFactorLabel.setText("Gen PFator (PU)");

        edLoadPFacorTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        edLoadPFacorTextField.setText("0.98");

        edLoadPFactorLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        edLoadPFactorLabel.setText("Load PFator (PU)");

        edLossPercentTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        edLossPercentTextField.setText("2.5");

        edLossPercentLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        edLossPercentLabel.setText("Network Loss %");

        org.jdesktop.layout.GroupLayout edFilePanelLayout = new org.jdesktop.layout.GroupLayout(edFilePanel);
        edFilePanel.setLayout(edFilePanelLayout);
        edFilePanelLayout.setHorizontalGroup(
            edFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(edFilePanelLayout.createSequentialGroup()
                .add(18, 18, 18)
                .add(edFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(edFilePanelLayout.createSequentialGroup()
                        .add(edFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(edFileLabel)
                            .add(edFilePanelLayout.createSequentialGroup()
                                .add(89, 89, 89)
                                .add(edFileTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(selectEdFileButton)
                        .add(48, 48, 48))
                    .add(edFilePanelLayout.createSequentialGroup()
                        .add(edFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(edFilePanelLayout.createSequentialGroup()
                                .add(edLoadPFactorLabel)
                                .add(18, 18, 18)
                                .add(edLoadPFacorTextField))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, edFilePanelLayout.createSequentialGroup()
                                .add(edGenPFactorLabel)
                                .add(22, 22, 22)
                                .add(edGenPFacorTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 52, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .add(45, 45, 45)
                        .add(edLossPercentLabel)
                        .add(18, 18, 18)
                        .add(edLossPercentTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 42, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        edFilePanelLayout.setVerticalGroup(
            edFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(edFilePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(edFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(edFileLabel)
                    .add(edFileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(selectEdFileButton))
                .add(10, 10, 10)
                .add(edFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(edGenPFactorLabel)
                    .add(edGenPFacorTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(edLossPercentLabel)
                    .add(edLossPercentTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(edFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(edLoadPFactorLabel)
                    .add(edLoadPFacorTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        interfaceFilePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Interface Defintion", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N

        interfaceFileLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        interfaceFileLabel.setText("Interface File");

        interfaceFileTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        interfaceFileTextField.setText("Interface File");

        selectInterfaceFileButton.setFont(new java.awt.Font("Dialog", 0, 10));
        selectInterfaceFileButton.setText("Select ...");
        selectInterfaceFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectInterfaceFileButtonActionPerformed(evt);
            }
        });

        interfaceLimitLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        interfaceLimitLabel.setText("Limit File");

        interfaceLimitTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        interfaceLimitTextField.setText("Interface Limit file");

        selectInterfaceLimitButton.setFont(new java.awt.Font("Dialog", 0, 10));
        selectInterfaceLimitButton.setText("Select ...");
        selectInterfaceLimitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectInterfaceLimitButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout interfaceFilePanelLayout = new org.jdesktop.layout.GroupLayout(interfaceFilePanel);
        interfaceFilePanel.setLayout(interfaceFilePanelLayout);
        interfaceFilePanelLayout.setHorizontalGroup(
            interfaceFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(interfaceFilePanelLayout.createSequentialGroup()
                .add(20, 20, 20)
                .add(interfaceFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(interfaceFileLabel)
                    .add(interfaceLimitLabel))
                .add(18, 18, 18)
                .add(interfaceFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(interfaceLimitTextField)
                    .add(interfaceFileTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(interfaceFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(selectInterfaceFileButton)
                    .add(selectInterfaceLimitButton))
                .add(17, 17, 17))
        );
        interfaceFilePanelLayout.setVerticalGroup(
            interfaceFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(interfaceFilePanelLayout.createSequentialGroup()
                .add(interfaceFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(selectInterfaceFileButton)
                    .add(interfaceFileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(interfaceFileLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(interfaceFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(interfaceLimitLabel)
                    .add(interfaceLimitTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(selectInterfaceLimitButton))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout caseDataPanelLayout = new org.jdesktop.layout.GroupLayout(caseDataPanel);
        caseDataPanel.setLayout(caseDataPanelLayout);
        caseDataPanelLayout.setHorizontalGroup(
            caseDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(caseDataPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(edFilePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .add(caseDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(caseDataPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(interfaceFilePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE)
                    .add(19, 19, 19)))
        );
        caseDataPanelLayout.setVerticalGroup(
            caseDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(caseDataPanelLayout.createSequentialGroup()
                .add(22, 22, 22)
                .add(edFilePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(233, Short.MAX_VALUE))
            .add(caseDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(caseDataPanelLayout.createSequentialGroup()
                    .add(161, 161, 161)
                    .add(interfaceFilePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(135, Short.MAX_VALUE)))
        );

        pTradingAnalysisTabbedPane.addTab("Case Data", caseDataPanel);

        edHourLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        edHourLabel.setText("ED Hour");

        edHourComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        edHourComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00", " " }));

        incMustRunGenCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        incMustRunGenCheckBox.setSelected(true);
        incMustRunGenCheckBox.setText("Include MustRun Generators");

        mustRunFileLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        mustRunFileLabel.setText("MustRun File");

        mustRunFileTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        mustRunFileTextField.setText("MustRun File");

        mustRunFileSelectButton.setFont(new java.awt.Font("Dialog", 0, 10));
        mustRunFileSelectButton.setText("Select ...");
        mustRunFileSelectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mustRunFileSelectButtonActionPerformed(evt);
            }
        });

        genQAdjCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        genQAdjCheckBox.setSelected(true);
        genQAdjCheckBox.setText("Gen Q Adjustment");

        mustRunQAdjStepsLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        mustRunQAdjStepsLabel.setText("MustRun Gen Q Adj Steps");

        mustRunQAdjStespTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        mustRunQAdjStespTextField.setText("3");

        swingAllocCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        swingAllocCheckBox.setSelected(true);
        swingAllocCheckBox.setText("Swing Bus P&Q Allocation");

        swingAllocZoneLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        swingAllocZoneLabel.setText("Alloc Zone");

        swingAllocZoneComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        swingAllocZoneComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3" }));

        swingAllocMaxStepsLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        swingAllocMaxStepsLabel.setText("Max Steps");

        swingAllocMaxStepsTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        swingAllocMaxStepsTextField.setText("3");

        swingAllocAccFactorLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        swingAllocAccFactorLabel.setText("Acc Factor");

        swingAllocAccFactorTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        swingAllocAccFactorTextField.setText("1.75");

        runAclfAnalysisButton.setFont(new java.awt.Font("Dialog", 0, 12));
        runAclfAnalysisButton.setText("Run Analysis");
        runAclfAnalysisButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runAclfAnalysisButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout aclfAnalysisPanelLayout = new org.jdesktop.layout.GroupLayout(aclfAnalysisPanel);
        aclfAnalysisPanel.setLayout(aclfAnalysisPanelLayout);
        aclfAnalysisPanelLayout.setHorizontalGroup(
            aclfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, aclfAnalysisPanelLayout.createSequentialGroup()
                .addContainerGap(204, Short.MAX_VALUE)
                .add(runAclfAnalysisButton)
                .add(187, 187, 187))
            .add(aclfAnalysisPanelLayout.createSequentialGroup()
                .add(142, 142, 142)
                .add(edHourLabel)
                .add(39, 39, 39)
                .add(edHourComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 77, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(191, Short.MAX_VALUE))
            .add(aclfAnalysisPanelLayout.createSequentialGroup()
                .add(28, 28, 28)
                .add(aclfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(aclfAnalysisPanelLayout.createSequentialGroup()
                        .add(29, 29, 29)
                        .add(mustRunFileLabel)
                        .add(29, 29, 29)
                        .add(mustRunFileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 213, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(6, 6, 6)
                        .add(mustRunFileSelectButton))
                    .add(genQAdjCheckBox)
                    .add(incMustRunGenCheckBox)
                    .add(swingAllocCheckBox)
                    .add(aclfAnalysisPanelLayout.createSequentialGroup()
                        .add(aclfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(aclfAnalysisPanelLayout.createSequentialGroup()
                                .add(31, 31, 31)
                                .add(mustRunQAdjStepsLabel))
                            .add(aclfAnalysisPanelLayout.createSequentialGroup()
                                .add(116, 116, 116)
                                .add(swingAllocMaxStepsTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .add(24, 24, 24)
                        .add(aclfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(aclfAnalysisPanelLayout.createSequentialGroup()
                                .add(swingAllocAccFactorLabel)
                                .add(26, 26, 26)
                                .add(swingAllocAccFactorTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(mustRunQAdjStespTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 33, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(aclfAnalysisPanelLayout.createSequentialGroup()
                        .add(32, 32, 32)
                        .add(aclfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(swingAllocMaxStepsLabel)
                            .add(aclfAnalysisPanelLayout.createSequentialGroup()
                                .add(swingAllocZoneLabel)
                                .add(41, 41, 41)
                                .add(swingAllocZoneComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 71, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(44, Short.MAX_VALUE))
        );
        aclfAnalysisPanelLayout.setVerticalGroup(
            aclfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, aclfAnalysisPanelLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(aclfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(edHourComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(edHourLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(incMustRunGenCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(aclfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(mustRunFileLabel)
                    .add(mustRunFileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(mustRunFileSelectButton))
                .add(18, 18, 18)
                .add(genQAdjCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(aclfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(mustRunQAdjStespTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(mustRunQAdjStepsLabel))
                .add(18, 18, 18)
                .add(swingAllocCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(aclfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(swingAllocZoneLabel)
                    .add(swingAllocZoneComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(aclfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(swingAllocMaxStepsLabel)
                    .add(swingAllocAccFactorLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(swingAllocMaxStepsTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(swingAllocAccFactorTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(72, 72, 72)
                .add(runAclfAnalysisButton)
                .add(23, 23, 23))
        );

        pTradingAnalysisTabbedPane.addTab("Aclf Analysis", aclfAnalysisPanel);

        runLineOutgageButton.setFont(new java.awt.Font("Dialog", 0, 10));
        runLineOutgageButton.setText("Run");
        runLineOutgageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runLineOutgageButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout lineOutagePanelLayout = new org.jdesktop.layout.GroupLayout(lineOutagePanel);
        lineOutagePanel.setLayout(lineOutagePanelLayout);
        lineOutagePanelLayout.setHorizontalGroup(
            lineOutagePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lineOutagePanelLayout.createSequentialGroup()
                .add(220, 220, 220)
                .add(runLineOutgageButton)
                .addContainerGap(225, Short.MAX_VALUE))
        );
        lineOutagePanelLayout.setVerticalGroup(
            lineOutagePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, lineOutagePanelLayout.createSequentialGroup()
                .addContainerGap(334, Short.MAX_VALUE)
                .add(runLineOutgageButton)
                .add(36, 36, 36))
        );

        pTradingAnalysisTabbedPane.addTab("Line Outage", lineOutagePanel);

        outVoltViolationCheckBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        outVoltViolationCheckBox.setSelected(true);
        outVoltViolationCheckBox.setText("Bus Voltage Violation Report");

        voltUpperLimitLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        voltUpperLimitLabel.setText("Upper Limit");

        voltUpperLimitTextField.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        voltUpperLimitTextField.setText("1.15");

        voltLowerLimitLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        voltLowerLimitLabel.setText("Lower Limit");

        voltLowerLimitTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        voltLowerLimitTextField.setText("0.85");

        outBranchViolationCheckBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        outBranchViolationCheckBox.setSelected(true);
        outBranchViolationCheckBox.setText("Branch Limit Violation Report");

        outInterfaceViolationCheckBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        outInterfaceViolationCheckBox.setSelected(true);
        outInterfaceViolationCheckBox.setText("Interface Limit Violation Report");

        outZoneSummaryCheckBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        outZoneSummaryCheckBox.setSelected(true);
        outZoneSummaryCheckBox.setText("Zone Summary");

        outAreaSummaryCheckBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        outAreaSummaryCheckBox.setSelected(true);
        outAreaSummaryCheckBox.setText("Area Summary");

        outMustRunSummaryCheckBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        outMustRunSummaryCheckBox.setSelected(true);
        outMustRunSummaryCheckBox.setText("MustRun Generator Summary");

        largeGSFPointsLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        largeGSFPointsLabel.setText("Large GSF Points");

        largeGSFPointsTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        largeGSFPointsTextField.setText("5");

        org.jdesktop.layout.GroupLayout outputConfigPanelLayout = new org.jdesktop.layout.GroupLayout(outputConfigPanel);
        outputConfigPanel.setLayout(outputConfigPanelLayout);
        outputConfigPanelLayout.setHorizontalGroup(
            outputConfigPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(outputConfigPanelLayout.createSequentialGroup()
                .add(outputConfigPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(outputConfigPanelLayout.createSequentialGroup()
                        .add(34, 34, 34)
                        .add(outputConfigPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(outBranchViolationCheckBox)
                            .add(outInterfaceViolationCheckBox)
                            .add(outZoneSummaryCheckBox)
                            .add(outAreaSummaryCheckBox)
                            .add(outMustRunSummaryCheckBox)
                            .add(outputConfigPanelLayout.createSequentialGroup()
                                .add(218, 218, 218)
                                .add(voltLowerLimitLabel)
                                .add(18, 18, 18)
                                .add(voltLowerLimitTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(outputConfigPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                .add(outputConfigPanelLayout.createSequentialGroup()
                                    .add(voltUpperLimitLabel)
                                    .add(18, 18, 18)
                                    .add(voltUpperLimitTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .add(outVoltViolationCheckBox))))
                    .add(outputConfigPanelLayout.createSequentialGroup()
                        .add(59, 59, 59)
                        .add(largeGSFPointsLabel)
                        .add(18, 18, 18)
                        .add(largeGSFPointsTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(126, Short.MAX_VALUE))
        );
        outputConfigPanelLayout.setVerticalGroup(
            outputConfigPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(outputConfigPanelLayout.createSequentialGroup()
                .add(20, 20, 20)
                .add(outVoltViolationCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(outputConfigPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(voltUpperLimitLabel)
                    .add(voltUpperLimitTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(voltLowerLimitLabel)
                    .add(voltLowerLimitTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(11, 11, 11)
                .add(outBranchViolationCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(outInterfaceViolationCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(outZoneSummaryCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(outAreaSummaryCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(outMustRunSummaryCheckBox)
                .add(31, 31, 31)
                .add(outputConfigPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(largeGSFPointsLabel)
                    .add(largeGSFPointsTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(123, Short.MAX_VALUE))
        );

        pTradingAnalysisTabbedPane.addTab("Output Config", outputConfigPanel);

        pTradingAnalysisTabbedPane.setSelectedIndex(1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(pTradingAnalysisTabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(pTradingAnalysisTabbedPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 423, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(35, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
 
    private void panelSelectionChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_panelSelectionChanged
    	if ( pTradingAnalysisTabbedPane.getSelectedIndex() == 0 )
        	IpssLogger.getLogger().info("Panel selection changed - Case Data Panel");
    	else if ( pTradingAnalysisTabbedPane.getSelectedIndex() == 1 ) {
        	IpssLogger.getLogger().info("Panel selection changed - Aclf Analysis Panel");
    	}	
    	else if ( pTradingAnalysisTabbedPane.getSelectedIndex() == 2 ) {
        	IpssLogger.getLogger().info("Panel selection changed - Line Outage Analysis Panel");
    	}	
    	else if ( pTradingAnalysisTabbedPane.getSelectedIndex() == 3 ) {
        	IpssLogger.getLogger().info("Panel selection changed - Output Config Panel");
    	}	
    }//GEN-LAST:event_panelSelectionChanged

private boolean saveInputData() {
	Vector<String> errMsg = new Vector<String>();
	try {
    	if (!saveEditor2Form(errMsg)) {
    		CoreCommonSpringCtx.getEditorDialogUtil().showMsgDialog(this.parent, "Input Data Error", errMsg);
			return false;
    	}
    } catch (Exception e) {
    	IpssLogger.logErr(e);
    	CoreCommonSpringCtx.getEditorDialogUtil().showMsgDialog(this.parent, "Input Data Error", e.toString());
		return false;
    }
    return true;
}

private void runAclfAnalysisButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runAclfAnalysisButtonActionPerformed
	IpssLogger.getLogger().info("Aclf analysis button selected");
	
	this.parent.setAlwaysOnTop(false);

	if (!saveInputData())
		return;

	// running the analysis in a separate thread, so that it won't block
	// the main UI window
	final JDialog parent = this.parent;
	final AclfNetwork net = this._simuCtx.getAclfNet();
	final PTradingAnalysisXmlType ptXml = this._ptXml;
	new Thread() {
		public void run() {
			// create Loadflow algorithm DSL object
			LfAlgoDSL algoDsl = IpssAclf.createAlgo(net);
			
			IAppStatus appStatus = GraphSpringAppContext.getIpssGraphicEditor().getAppStatus();
			appStatus.busyStart(Constants.StatusBusyIndicatorPeriod,
					"Run PowerTrading Aclf Analysis ...", "Run PTraing");
			
			AclfODMRunner runner = new AclfODMRunner(algoDsl);
			try {
				StringBuffer s = runner.runPTradingAnalysis(ptXml);
				IOutputTextDialog dialog = UISpringAppContext.getOutputTextDialog("BaseCase Aclf Analysis Results");
				dialog.display(s);
			} catch (InterpssException e) {
		    	CoreCommonSpringCtx.getEditorDialogUtil().showMsgDialog(parent, "Analysis Error", e.toString());
				return;
			}
			
			appStatus.busyStop("Run PowerTrading Aclf Analysis finished");			
		}
	}.start();
}//GEN-LAST:event_runAclfAnalysisButtonActionPerformed

private void runLineOutgageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runLineOutgageButtonActionPerformed
	IpssLogger.getLogger().info("Line outage analysis button selected");
	
	this.parent.setAlwaysOnTop(false);

	if (!saveInputData())
		return;
   	
}//GEN-LAST:event_runLineOutgageButtonActionPerformed

private void selectEdFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectEdFileButtonActionPerformed
	JFileChooser fc = getExcelFileChooser();
	if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
		File file = fc.getSelectedFile();
		edFileTextField.setText(file.getAbsolutePath());
	}
}//GEN-LAST:event_selectEdFileButtonActionPerformed

private void selectInterfaceFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectInterfaceFileButtonActionPerformed
	JFileChooser fc = getExcelFileChooser();
	if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
		File file = fc.getSelectedFile();
		interfaceFileTextField.setText(file.getAbsolutePath());
	}
}//GEN-LAST:event_selectInterfaceFileButtonActionPerformed

private void selectInterfaceLimitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectInterfaceLimitButtonActionPerformed
	JFileChooser fc = getExcelFileChooser();
	if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
		File file = fc.getSelectedFile();
		interfaceLimitTextField.setText(file.getAbsolutePath());
	}
}//GEN-LAST:event_selectInterfaceLimitButtonActionPerformed

private void mustRunFileSelectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mustRunFileSelectButtonActionPerformed
	JFileChooser fc = getExcelFileChooser();
	if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
		File file = fc.getSelectedFile();
		mustRunFileTextField.setText(file.getAbsolutePath());
	}
}//GEN-LAST:event_mustRunFileSelectButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel aclfAnalysisPanel;
    private javax.swing.JPanel caseDataPanel;
    private javax.swing.JLabel edFileLabel;
    private javax.swing.JPanel edFilePanel;
    private javax.swing.JTextField edFileTextField;
    private javax.swing.JTextField edGenPFacorTextField;
    private javax.swing.JLabel edGenPFactorLabel;
    private javax.swing.JComboBox edHourComboBox;
    private javax.swing.JLabel edHourLabel;
    private javax.swing.JTextField edLoadPFacorTextField;
    private javax.swing.JLabel edLoadPFactorLabel;
    private javax.swing.JLabel edLossPercentLabel;
    private javax.swing.JTextField edLossPercentTextField;
    private javax.swing.JCheckBox genQAdjCheckBox;
    private javax.swing.JCheckBox incMustRunGenCheckBox;
    private javax.swing.JLabel interfaceFileLabel;
    private javax.swing.JPanel interfaceFilePanel;
    private javax.swing.JTextField interfaceFileTextField;
    private javax.swing.JLabel interfaceLimitLabel;
    private javax.swing.JTextField interfaceLimitTextField;
    private javax.swing.JLabel largeGSFPointsLabel;
    private javax.swing.JTextField largeGSFPointsTextField;
    private javax.swing.JPanel lineOutagePanel;
    private javax.swing.JLabel mustRunFileLabel;
    private javax.swing.JButton mustRunFileSelectButton;
    private javax.swing.JTextField mustRunFileTextField;
    private javax.swing.JLabel mustRunQAdjStepsLabel;
    private javax.swing.JTextField mustRunQAdjStespTextField;
    private javax.swing.JCheckBox outAreaSummaryCheckBox;
    private javax.swing.JCheckBox outBranchViolationCheckBox;
    private javax.swing.JCheckBox outInterfaceViolationCheckBox;
    private javax.swing.JCheckBox outMustRunSummaryCheckBox;
    private javax.swing.JCheckBox outVoltViolationCheckBox;
    private javax.swing.JCheckBox outZoneSummaryCheckBox;
    private javax.swing.JPanel outputConfigPanel;
    private javax.swing.JTabbedPane pTradingAnalysisTabbedPane;
    private javax.swing.JButton runAclfAnalysisButton;
    private javax.swing.JButton runLineOutgageButton;
    private javax.swing.JButton selectEdFileButton;
    private javax.swing.JButton selectInterfaceFileButton;
    private javax.swing.JButton selectInterfaceLimitButton;
    private javax.swing.JLabel swingAllocAccFactorLabel;
    private javax.swing.JTextField swingAllocAccFactorTextField;
    private javax.swing.JCheckBox swingAllocCheckBox;
    private javax.swing.JLabel swingAllocMaxStepsLabel;
    private javax.swing.JTextField swingAllocMaxStepsTextField;
    private javax.swing.JComboBox swingAllocZoneComboBox;
    private javax.swing.JLabel swingAllocZoneLabel;
    private javax.swing.JLabel voltLowerLimitLabel;
    private javax.swing.JTextField voltLowerLimitTextField;
    private javax.swing.JLabel voltUpperLimitLabel;
    private javax.swing.JTextField voltUpperLimitTextField;
    // End of variables declaration//GEN-END:variables
    
	private static JFileChooser excelFileChooser = null;	
	private JFileChooser getExcelFileChooser() {
		if (excelFileChooser == null) {
			excelFileChooser = new JFileChooser();
			excelFileChooser.addChoosableFileFilter(new IpssFileFilter("xlsx", "MS Excel file"));
		}
		return excelFileChooser;
	}

	
	private void initInputVerifier(DataVerifier v) {
		edGenPFacorTextField.setInputVerifier(v);
		edLossPercentTextField.setInputVerifier(v);
		edLoadPFacorTextField.setInputVerifier(v);
		edHourComboBox.setInputVerifier(v);
		mustRunQAdjStespTextField.setInputVerifier(v);
		swingAllocZoneComboBox.setInputVerifier(v);
		swingAllocMaxStepsTextField.setInputVerifier(v);
		swingAllocAccFactorTextField.setInputVerifier(v);
		largeGSFPointsTextField.setInputVerifier(v);
		this.voltUpperLimitTextField.setInputVerifier(v);
		this.voltLowerLimitTextField.setInputVerifier(v);
	}
	
	class DataVerifier extends javax.swing.InputVerifier {
		public boolean verify(javax.swing.JComponent input) {
			if (input == null)
				return false;
       		try {
				if (input == edGenPFacorTextField)
					return SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) > 0.0 && 
							SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) <= 1.0;
				else if (input == edLossPercentTextField)
					return SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) > 0.0 && 
					       SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) <= 100.0;
				else if (input == edLoadPFacorTextField)
					return SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) > 0.0 && 
						   SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) <= 1.0;

				else if (input == edHourComboBox )
					return !SwingInputVerifyUtil.isEmptyStr((javax.swing.JComboBox)input);
				else if (input == mustRunQAdjStespTextField)
					return SwingInputVerifyUtil.getInt((javax.swing.JTextField)input) > 0;
				else if (input == swingAllocZoneComboBox )
					return !SwingInputVerifyUtil.isEmptyStr((javax.swing.JComboBox)input);
				else if (input == swingAllocMaxStepsTextField)
					return SwingInputVerifyUtil.getInt((javax.swing.JTextField)input) > 0;
				else if (input == swingAllocAccFactorTextField)
					return SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) > 0.0;
					
				else if (input == largeGSFPointsTextField)
					return SwingInputVerifyUtil.getInt((javax.swing.JTextField)input) > 0;
				else if (input == voltUpperLimitTextField)
					return SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) > 0.0;
				else if (input == voltLowerLimitTextField)
					return SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) > 0.0;
 	       	} catch (Exception e) {
 	    		return false;
 	       	}				
			return true;
		}
	}
}
