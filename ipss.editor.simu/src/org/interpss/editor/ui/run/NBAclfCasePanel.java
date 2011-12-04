 /*
  * @(#)NBAclfCasePanel.java   
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

import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JDialog;

import org.interpss.editor.form.GFormContainer;
import org.interpss.editor.form.GNetForm;
import org.interpss.editor.jgraph.ui.edit.IFormDataPanel;
import org.interpss.editor.runAct.RunActUtilFunc;
import org.interpss.editor.ui.IOutputTextDialog;
import org.interpss.editor.ui.UISpringAppContext;
import org.interpss.editor.ui.run.common.NBGridComputingPanel;
import org.interpss.numeric.util.Number2String;
import org.interpss.ui.SwingInputVerifyUtil;
import org.interpss.xml.schema.AclfAlgorithmXmlType;
import org.interpss.xml.schema.AclfMethodDataType;
import org.interpss.xml.schema.ContingencyAnalysisDataType;
import org.interpss.xml.schema.ContingencyAnalysisXmlType;
import org.interpss.xml.schema.GridComputingXmlType;
import org.interpss.xml.schema.UnitDataType;

import com.interpss.common.exp.InterpssRuntimeException;
import com.interpss.common.msg.IpssMessage;
import com.interpss.common.msg.IpssMsgListener;
import com.interpss.common.msg.SimuMessage;
import com.interpss.common.util.IpssLogger;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.adj.FunctionLoad;
import com.interpss.core.algo.AclfMethod;
import com.interpss.core.net.Area;
import com.interpss.core.net.reg.IRegulationDevice;
import com.interpss.simu.SimuContext;
import com.interpss.spring.CoreCommonSpringFactory;

public class NBAclfCasePanel extends javax.swing.JPanel implements IFormDataPanel, IpssMsgListener {
	private static final long serialVersionUID = 1;
	
	private JDialog parent = null;
	private static NBGridComputingPanel gridLfPanel = new NBGridComputingPanel();
	private static NBGridComputingPanel gridContinPanel = new NBGridComputingPanel();
	private boolean gridComputing = false;

    private GFormContainer _netContainer = null;
    private SimuContext _simuCtx = null;

    // holds the current case data being edited
    private AclfAlgorithmXmlType xmlCaseAlgo;
    private ContingencyAnalysisXmlType xmlAnalysis;
    
    public static final int LoadflowPanel = 1,
    					ContingencyPanel = 2,
    					AdvancedPanel = 3;
    private int panelSelected = LoadflowPanel; 
    public int getPanelSelected() { return this.panelSelected; }
	
    /** Creates new form NBAclfCasePanel */
    public NBAclfCasePanel(JDialog parent) {
    	this.parent = parent;
    	initComponents();

        DataVerifier verifier = new DataVerifier();
        this.accFactorTextField.setInputVerifier(verifier);
        this.errKVATextField.setInputVerifier(verifier);
        this.errPUTextField.setInputVerifier(verifier);
        this.maxItrTextField.setInputVerifier(verifier);
    }
    
    /**
     * Implementation of the onMsgEvent method.
  	* 
  	* @param msg the msg object
     */
     public void onMsgEvent(IpssMessage msg) {
    	 if (msg instanceof SimuMessage) {
      	  	if (msg.getType() == SimuMessage.TYPE_LFLAB_MSG) {
    			IpssLogger.getLogger().info(msg.getMsgString());
    			msgOutTextArea.append(msg.getMsgString() + "\n");
      	  	}
    	 }
     }

     public boolean onMsgEventStatus(IpssMessage msg) {
  	   throw new InterpssRuntimeException("Method not implemented");
     }
     
    public void init(Object netContainer, Object simuCtx) {
    	// for non-graphic file, netContainer == null
		IpssLogger.getLogger().info("NBAclfCasePanel init() called");
	    _netContainer = (GFormContainer)netContainer;
	    _simuCtx = (SimuContext)simuCtx;
    	_simuCtx.getLoadflowAlgorithm().setInitBusVoltage(false);
    	if (_simuCtx.getLoadflowAlgorithm() != null) {
    		_simuCtx.getLoadflowAlgorithm().getLfAdjAlgo().setActivateAllAdjust(false);
//        	initAdvanceControlPanel();
    	}
		msgOutTextArea.setText("");
    }

    public void init(Object netContainer, Object simuCtx, boolean gridComputing) {
        init(netContainer, simuCtx);
		if (gridComputing) {
			this.gridComputing = gridComputing;
			this.gridComputingPanel.add(gridLfPanel);
			this.gridContingencyPanel.add(gridContinPanel);
			if (!this.continCaseCheckBox.isSelected())
				this.continCaseTextField.setEnabled(false);
		}
    }
    
	private void initAdvanceControlPanel() {
		Object[] list = RunActUtilFunc.getFunctionLoadList(_simuCtx.getAclfNet(), 
				_simuCtx.getLoadflowAlgorithm().getTolerance(),
				_simuCtx.getMsgHub());
		if (list.length > 1) {
			funcLoadComboBox.setModel(new javax.swing.DefaultComboBoxModel(list));
		}
		else {
			funcLoadComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] {"No Adjustment Needed"}));
		}
		funcLoadComboBox.setEnabled(list.length > 1);
		funcLoadButton.setEnabled(list.length > 1);

		list = null;
		if (_simuCtx.getLoadflowAlgorithm() != null)
			list = RunActUtilFunc.getXfrTapControlList(_simuCtx.getAclfNet(),		
				_simuCtx.getLoadflowAlgorithm().getTolerance()*
				        _simuCtx.getLoadflowAlgorithm().getLfAdjAlgo().getVoltAdjToleranceFactor(),
				_simuCtx.getMsgHub());
		if (list != null && list.length > 1) {
			xfrTapControlComboBox.setModel(new javax.swing.DefaultComboBoxModel(list));
		}
		else {
			xfrTapControlComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] {"No Adjustment Needed"}));
		}
		xfrTapControlComboBox.setEnabled(list != null && list.length > 1);
		xfrTapControlButton.setEnabled(list != null && list.length > 1);
		xfrTapControlXLabel.setEnabled(list != null && list.length > 1);
		xfrTapControlTextField.setEnabled(list != null && list.length > 1);

		list = RunActUtilFunc.getPSXfrPControlList(_simuCtx.getAclfNet(), 
				_simuCtx.getLoadflowAlgorithm().getTolerance(),
				_simuCtx.getMsgHub());
		if (list.length > 1) {
			psXfrPControlComboBox.setModel(new javax.swing.DefaultComboBoxModel(list));
		}
		else {
			psXfrPControlComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] {"No Adjustment Needed"}));
		}
		psXfrPControlComboBox.setEnabled(list.length > 1);
		psXfrPControlButton.setEnabled(list.length > 1);
		psXfrPControlXLabel.setEnabled(list.length > 1);
		psXfrPControlTextField.setEnabled(list.length > 1);

		list = RunActUtilFunc.getInterareaPControlList(_simuCtx.getAclfNet(), _simuCtx.getMsgHub());
		if (list.length > 1) {
			interPControlComboBox.setModel(new javax.swing.DefaultComboBoxModel(list));
		}
		else {
			interPControlComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] {"No Adjustment Needed"}));
		}
		interPControlComboBox.setEnabled(list.length > 1);
		interPControlButton.setEnabled(list.length > 1);
		interPControlXLabel.setEnabled(list.length > 1);
		interPControlTextField.setEnabled(list.length > 1);
		
		list = null;
		if (_simuCtx.getLoadflowAlgorithm() != null)
			list = RunActUtilFunc.getRemoteQBusList(_simuCtx.getAclfNet(), 
				_simuCtx.getLoadflowAlgorithm().getTolerance()*
				_simuCtx.getLoadflowAlgorithm().getLfAdjAlgo().getVoltAdjToleranceFactor(),
				_simuCtx.getMsgHub());
		if (list != null && list.length > 1) {
			remoteQBusComboBox.setModel(new javax.swing.DefaultComboBoxModel(list));
		}
		else {
			remoteQBusComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] {"No Adjustment Needed"}));
		}
		remoteQBusComboBox.setEnabled(list != null && list.length > 1);
		remoteQBusButton.setEnabled(list != null && list.length > 1);
		remoteQBusXLabel.setEnabled(list != null && list.length > 1);
		remoteQBusTextField.setEnabled(list != null && list.length > 1);
		
		list = RunActUtilFunc.getPQBusLimitList(_simuCtx.getAclfNet(), _simuCtx.getMsgHub());
		if (list.length > 1) {
			pqBusLimitComboBox.setModel(new javax.swing.DefaultComboBoxModel(list));
		}
		else {
			pqBusLimitComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] {"No Adjustment Needed"}));
		}
		pqBusLimitComboBox.setEnabled(list.length > 1);
		pqBusLimitButton.setEnabled(list.length > 1);
		
		list = RunActUtilFunc.getPVBusLimitList(_simuCtx.getAclfNet(), _simuCtx.getMsgHub());
		if (list.length > 1) {
			pvBusLimitComboBox.setModel(new javax.swing.DefaultComboBoxModel(list));
		}
		else {
			pvBusLimitComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] {"No Adjustment Needed"}));
		}    	
		pvBusLimitComboBox.setEnabled(list.length > 1);
		pvBusLimitButton.setEnabled(list.length > 1);
    }

    public void setXmlCaseData(AclfAlgorithmXmlType data, GridComputingXmlType xmlGridOpt) {
    	this.xmlCaseAlgo = data;
    	if (gridComputing)
			gridLfPanel.setXmlCaseData(xmlGridOpt);
    }
   
    public void setXmlCaseData(ContingencyAnalysisXmlType analysis, GridComputingXmlType xmlGridOpt) {
    	this.xmlCaseAlgo = analysis.getDefaultAclfAlgorithm();
    	this.xmlAnalysis = analysis;
    	analysis.setLimitRunCases(false);
    	if (gridComputing) {
    		gridContinPanel.setXmlCaseData(xmlGridOpt);
    	}
    }

    /**
	*	Set form data to the editor
	*
	* @return false if there is any problem
	*/
	public boolean setForm2Editor() {
		IpssLogger.getLogger().info("NBAclfCasePanel setForm2Editor() called");
     
    	if (xmlCaseAlgo.getLfMethod() == AclfMethodDataType.NR)
        	this.nrRadioButton.setSelected(true);
    	else if (xmlCaseAlgo.getLfMethod() == AclfMethodDataType.PQ)
        	this.pqRadioButton.setSelected(true);
    	else if (xmlCaseAlgo.getLfMethod() == AclfMethodDataType.GS)
        	this.gsRadioButton.setSelected(true);
    	else if (xmlCaseAlgo.getLfMethod() == AclfMethodDataType.CUSTOM)
        	this.customRadioButton.setSelected(true);
    	
    	//this.nonDivergeCheckBox.setEnabled(xmlCaseData.getNonDivergent());

    	this.accFactorTextField.setText(Number2String.toStr(xmlCaseAlgo.getAccFactor()==null?1.0:xmlCaseAlgo.getAccFactor(), "#0.0#"));
        this.errPUTextField.setText(Number2String.toStr(xmlCaseAlgo.getTolerance(), "#0.#####"));
        double baseKva = _netContainer != null? ((GNetForm)_netContainer.getGNetForm()).getBaseKVA() : 100000.0;
        this.errKVATextField.setText(Number2String.toStr(xmlCaseAlgo.getTolerance()*baseKva, "#0.####"));
        this.maxItrTextField.setText(new Integer(xmlCaseAlgo.getMaxIterations()).toString());
        this.nonDivergeCheckBox.setSelected(xmlCaseAlgo.isNonDivergent() == null? false : xmlCaseAlgo.isNonDivergent());
        this.initVoltCheckBox.setSelected(xmlCaseAlgo.isInitBusVoltage()==null?false:xmlCaseAlgo.isInitBusVoltage());
		this.lfSummaryCheckBox.setSelected(xmlCaseAlgo.isDisplaySummary()==null?false:xmlCaseAlgo.isDisplaySummary());
		
		if (gridComputing) {
			gridLfPanel.setForm2Editor();
			gridContinPanel.setForm2Editor();
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
		IpssLogger.getLogger().info("NBAclfCasePanel saveEditor2Form() called");
		
        if (this.nrRadioButton.isSelected())
        	xmlCaseAlgo.setLfMethod(AclfMethodDataType.NR);
        else if (this.pqRadioButton.isSelected())
        	xmlCaseAlgo.setLfMethod(AclfMethodDataType.PQ);
        else if (this.gsRadioButton.isSelected())
        	xmlCaseAlgo.setLfMethod(AclfMethodDataType.GS);
        else
        	xmlCaseAlgo.setLfMethod(AclfMethodDataType.CUSTOM);
        
        if (SwingInputVerifyUtil.largeThan(this.errPUTextField, 0.0d, errMsg, "Error tolerance <= 0.0")) {
        	xmlCaseAlgo.setTolerance(SwingInputVerifyUtil.getDouble(this.errPUTextField));
        	xmlCaseAlgo.setToleranceUnit(UnitDataType.PU);
        }

        if (SwingInputVerifyUtil.largeThan(this.maxItrTextField, 0, errMsg, "Max iterations <= 0") )
        	xmlCaseAlgo.setMaxIterations(SwingInputVerifyUtil.getInt(this.maxItrTextField));

        if (xmlCaseAlgo.getLfMethod() == AclfMethodDataType.GS)
        	if (SwingInputVerifyUtil.largeThan(this.accFactorTextField, 0.0d, errMsg, "GS acceleration factor <= 0.0"))
        		xmlCaseAlgo.setAccFactor(SwingInputVerifyUtil.getDouble(this.accFactorTextField));

        xmlCaseAlgo.setNonDivergent(this.nonDivergeCheckBox.isSelected());
        xmlCaseAlgo.setInitBusVoltage(this.initVoltCheckBox.isSelected());
        xmlCaseAlgo.setDisplaySummary(this.lfSummaryCheckBox.isSelected());
	        
		if (this.panelSelected == LoadflowPanel) {
			if (gridComputing)
				gridLfPanel.saveEditor2Form(errMsg);
		}
		else if (this.panelSelected == ContingencyPanel) {
	        if (this.n1ContingencyRadioButton.isSelected())
	        	this.xmlAnalysis.setType(ContingencyAnalysisDataType.N_1);
	        else if (this.n11ContingencyRadioButton.isSelected())
	        	this.xmlAnalysis.setType(ContingencyAnalysisDataType.N_1_1);
	        else
	        	this.xmlAnalysis.setType(ContingencyAnalysisDataType.N_2);
	        
	        if (this.continCaseCheckBox.isSelected()) {
	        	this.xmlAnalysis.setLimitRunCases(true);
	        	this.xmlAnalysis.setMaxRunCases(SwingInputVerifyUtil.getInt(this.continCaseTextField));
	        }
	        
	        this.xmlAnalysis.setDefaultAclfAlgorithm(xmlCaseAlgo);

	        if (gridComputing)
				gridContinPanel.saveEditor2Form(errMsg);
		}
		
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

        methodButtonGroup = new javax.swing.ButtonGroup();
        contingencyButtonGroup = new javax.swing.ButtonGroup();
        runAclfTabbedPane = new javax.swing.JTabbedPane();
        mainPanel = new javax.swing.JPanel();
        methodPanel = new javax.swing.JPanel();
        nrRadioButton = new javax.swing.JRadioButton();
        pqRadioButton = new javax.swing.JRadioButton();
        gsRadioButton = new javax.swing.JRadioButton();
        customRadioButton = new javax.swing.JRadioButton();
        paramPanel = new javax.swing.JPanel();
        toleranceLabel = new javax.swing.JLabel();
        errPUTextField = new javax.swing.JTextField();
        errPUUnitLabel = new javax.swing.JLabel();
        errKVATextField = new javax.swing.JTextField();
        errKVAUnitLabel = new javax.swing.JLabel();
        maxItrLabel = new javax.swing.JLabel();
        maxItrTextField = new javax.swing.JTextField();
        accFactorLabel = new javax.swing.JLabel();
        accFactorTextField = new javax.swing.JTextField();
        nonDivergeCheckBox = new javax.swing.JCheckBox();
        initVoltCheckBox = new javax.swing.JCheckBox();
        lfSummaryCheckBox = new javax.swing.JCheckBox();
        gridComputingPanel = new javax.swing.JPanel();
        contingencyPanel = new javax.swing.JPanel();
        contingencyMethodPanel = new javax.swing.JPanel();
        n1ContingencyRadioButton = new javax.swing.JRadioButton();
        n11ContingencyRadioButton = new javax.swing.JRadioButton();
        n2ContingencyRadioButton = new javax.swing.JRadioButton();
        ContingencyDataPanel = new javax.swing.JPanel();
        continCaseCheckBox = new javax.swing.JCheckBox();
        continCaseTextField = new javax.swing.JTextField();
        gridContingencyPanel = new javax.swing.JPanel();
        advancedPanel = new javax.swing.JPanel();
        misTitleLabel = new javax.swing.JLabel();
        mismatchLabel = new javax.swing.JLabel();
        stepRunPanel = new javax.swing.JPanel();
        nrStepButton = new javax.swing.JButton();
        pqPStepButton = new javax.swing.JButton();
        pqQStepButton = new javax.swing.JButton();
        gsStepButton = new javax.swing.JButton();
        detailsButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();
        controlPanel = new javax.swing.JPanel();
        pvBusLimitLabel = new javax.swing.JLabel();
        pvBusLimitComboBox = new javax.swing.JComboBox();
        pvBusLimitPanel = new javax.swing.JPanel();
        pvBusLimitButton = new javax.swing.JButton();
        pqBusLimitLabel = new javax.swing.JLabel();
        pqBusLimitComboBox = new javax.swing.JComboBox();
        pqBusLimitPanel = new javax.swing.JPanel();
        pqBusLimitButton = new javax.swing.JButton();
        remoteQBusLabel = new javax.swing.JLabel();
        remoteQBusComboBox = new javax.swing.JComboBox();
        remoteQBusPanel = new javax.swing.JPanel();
        remoteQBusButton = new javax.swing.JButton();
        remoteQBusXLabel = new javax.swing.JLabel();
        remoteQBusTextField = new javax.swing.JTextField();
        funcLoadLabel = new javax.swing.JLabel();
        funcLoadComboBox = new javax.swing.JComboBox();
        funcLoadPanel = new javax.swing.JPanel();
        funcLoadButton = new javax.swing.JButton();
        xfrTapControlLabel = new javax.swing.JLabel();
        xfrTapControlComboBox = new javax.swing.JComboBox();
        xfrTapControlPanel = new javax.swing.JPanel();
        xfrTapControlButton = new javax.swing.JButton();
        xfrTapControlXLabel = new javax.swing.JLabel();
        xfrTapControlTextField = new javax.swing.JTextField();
        psXfrPControlLabel = new javax.swing.JLabel();
        psXfrPControlComboBox = new javax.swing.JComboBox();
        psXfrPControlPanel = new javax.swing.JPanel();
        psXfrPControlButton = new javax.swing.JButton();
        psXfrPControlXLabel = new javax.swing.JLabel();
        psXfrPControlTextField = new javax.swing.JTextField();
        interPControlLabel = new javax.swing.JLabel();
        interPControlComboBox = new javax.swing.JComboBox();
        interPControlPanel = new javax.swing.JPanel();
        interPControlButton = new javax.swing.JButton();
        interPControlXLabel = new javax.swing.JLabel();
        interPControlTextField = new javax.swing.JTextField();
        msgScrollPane = new javax.swing.JScrollPane();
        msgOutTextArea = new javax.swing.JTextArea();

        setLayout(new java.awt.GridBagLayout());

        runAclfTabbedPane.setFont(new java.awt.Font("Dialog", 0, 12));
        runAclfTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                panelSelectionChanged(evt);
            }
        });

        mainPanel.setLayout(new java.awt.GridBagLayout());

        methodPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Loadflow Method", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        methodPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 30, 5));

        methodButtonGroup.add(nrRadioButton);
        nrRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        nrRadioButton.setText("NR");
        nrRadioButton.setName("nrRadioButton"); // NOI18N
        nrRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nrRadioButtonActionPerformed(evt);
            }
        });
        methodPanel.add(nrRadioButton);

        methodButtonGroup.add(pqRadioButton);
        pqRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        pqRadioButton.setText("PQ");
        pqRadioButton.setName("pqRadioButton"); // NOI18N
        pqRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pqRadioButtonActionPerformed(evt);
            }
        });
        methodPanel.add(pqRadioButton);

        methodButtonGroup.add(gsRadioButton);
        gsRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        gsRadioButton.setText("GS");
        gsRadioButton.setName("gsRadioButton"); // NOI18N
        gsRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gsRadioButtonActionPerformed(evt);
            }
        });
        methodPanel.add(gsRadioButton);

        methodButtonGroup.add(customRadioButton);
        customRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        customRadioButton.setText("Custom");
        customRadioButton.setToolTipText("run Custom Loadflow");
        customRadioButton.setName("customRadioButton"); // NOI18N
        customRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                customRadioButtonActionPerformed(evt);
            }
        });
        methodPanel.add(customRadioButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 10, 0);
        mainPanel.add(methodPanel, gridBagConstraints);

        paramPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
        paramPanel.setLayout(new java.awt.GridBagLayout());

        toleranceLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        toleranceLabel.setText("Error Tolerance");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        paramPanel.add(toleranceLabel, gridBagConstraints);

        errPUTextField.setColumns(5);
        errPUTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        errPUTextField.setText("0.0001");
        errPUTextField.setName("errPUTextField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        paramPanel.add(errPUTextField, gridBagConstraints);

        errPUUnitLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        errPUUnitLabel.setText("(pu)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        paramPanel.add(errPUUnitLabel, gridBagConstraints);

        errKVATextField.setColumns(5);
        errKVATextField.setEditable(false);
        errKVATextField.setFont(new java.awt.Font("Dialog", 0, 12));
        errKVATextField.setText("0.1");
        errKVATextField.setName("errKVATextField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
        paramPanel.add(errKVATextField, gridBagConstraints);

        errKVAUnitLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        errKVAUnitLabel.setText("(KVA)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        paramPanel.add(errKVAUnitLabel, gridBagConstraints);

        maxItrLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        maxItrLabel.setText("Max Iterations");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 20);
        paramPanel.add(maxItrLabel, gridBagConstraints);

        maxItrTextField.setColumns(5);
        maxItrTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        maxItrTextField.setText("20");
        maxItrTextField.setName("maxItrTextField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        paramPanel.add(maxItrTextField, gridBagConstraints);

        accFactorLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        accFactorLabel.setText("GS Acc Factor");
        accFactorLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        paramPanel.add(accFactorLabel, gridBagConstraints);

        accFactorTextField.setColumns(5);
        accFactorTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        accFactorTextField.setText("1.0");
        accFactorTextField.setEnabled(false);
        accFactorTextField.setName("accFactorTextField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        paramPanel.add(accFactorTextField, gridBagConstraints);

        nonDivergeCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        nonDivergeCheckBox.setSelected(true);
        nonDivergeCheckBox.setText("Non-divergent");
        nonDivergeCheckBox.setName("initVoltCheckBox"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 40, 0, 0);
        paramPanel.add(nonDivergeCheckBox, gridBagConstraints);

        initVoltCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        initVoltCheckBox.setSelected(true);
        initVoltCheckBox.setText("Initialize Bus Voltage");
        initVoltCheckBox.setName("initVoltCheckBox"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 40, 5, 0);
        paramPanel.add(initVoltCheckBox, gridBagConstraints);

        lfSummaryCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        lfSummaryCheckBox.setSelected(true);
        lfSummaryCheckBox.setText("Show Loadflow Summary");
        lfSummaryCheckBox.setName("lfSummaryCheckBox"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 40, 5, 0);
        paramPanel.add(lfSummaryCheckBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        mainPanel.add(paramPanel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        mainPanel.add(gridComputingPanel, gridBagConstraints);

        runAclfTabbedPane.addTab("Run Loadflow", mainPanel);

        contingencyPanel.setLayout(new java.awt.GridBagLayout());

        contingencyMethodPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Contingency Analysis Method", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        contingencyMethodPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 30, 5));

        contingencyButtonGroup.add(n1ContingencyRadioButton);
        n1ContingencyRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        n1ContingencyRadioButton.setSelected(true);
        n1ContingencyRadioButton.setText("N-1");
        n1ContingencyRadioButton.setName("nrRadioButton"); // NOI18N
        n1ContingencyRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                n1ContingencyRadioButtonActionPerformed(evt);
            }
        });
        contingencyMethodPanel.add(n1ContingencyRadioButton);

        contingencyButtonGroup.add(n11ContingencyRadioButton);
        n11ContingencyRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        n11ContingencyRadioButton.setText("N-1-1");
        n11ContingencyRadioButton.setEnabled(false);
        n11ContingencyRadioButton.setName("pqRadioButton"); // NOI18N
        n11ContingencyRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                n11ContingencyRadioButtonActionPerformed(evt);
            }
        });
        contingencyMethodPanel.add(n11ContingencyRadioButton);

        contingencyButtonGroup.add(n2ContingencyRadioButton);
        n2ContingencyRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        n2ContingencyRadioButton.setText("N-2");
        n2ContingencyRadioButton.setEnabled(false);
        n2ContingencyRadioButton.setName("gsRadioButton"); // NOI18N
        n2ContingencyRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                n2ContingencyRadioButtonActionPerformed(evt);
            }
        });
        contingencyMethodPanel.add(n2ContingencyRadioButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 10, 0);
        contingencyPanel.add(contingencyMethodPanel, gridBagConstraints);

        continCaseCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        continCaseCheckBox.setLabel("Specify # of Cases       ");
        continCaseCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                continCaseCheckBoxActionPerformed(evt);
            }
        });
        ContingencyDataPanel.add(continCaseCheckBox);

        continCaseTextField.setColumns(3);
        continCaseTextField.setText("100");
        continCaseTextField.setEnabled(false);
        ContingencyDataPanel.add(continCaseTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 20, 0);
        contingencyPanel.add(ContingencyDataPanel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        contingencyPanel.add(gridContingencyPanel, gridBagConstraints);

        runAclfTabbedPane.addTab("Contingency Analysis", contingencyPanel);

        advancedPanel.setLayout(new java.awt.GridBagLayout());

        misTitleLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        misTitleLabel.setText("Power Mismatch");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        advancedPanel.add(misTitleLabel, gridBagConstraints);

        mismatchLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        mismatchLabel.setText("mismatch");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        advancedPanel.add(mismatchLabel, gridBagConstraints);

        stepRunPanel.setLayout(new java.awt.GridBagLayout());

        nrStepButton.setFont(new java.awt.Font("Dialog", 0, 12));
        nrStepButton.setText("NR >");
        nrStepButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nrStepButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 30, 0, 20);
        stepRunPanel.add(nrStepButton, gridBagConstraints);

        pqPStepButton.setFont(new java.awt.Font("Dialog", 0, 12));
        pqPStepButton.setText("PQ-P >");
        pqPStepButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pqPStepButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        stepRunPanel.add(pqPStepButton, gridBagConstraints);

        pqQStepButton.setFont(new java.awt.Font("Dialog", 0, 12));
        pqQStepButton.setText("PQ-Q >");
        pqQStepButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pqQStepButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        stepRunPanel.add(pqQStepButton, gridBagConstraints);

        gsStepButton.setFont(new java.awt.Font("Dialog", 0, 12));
        gsStepButton.setText("GS >");
        gsStepButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gsStepButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        stepRunPanel.add(gsStepButton, gridBagConstraints);

        detailsButton.setFont(new java.awt.Font("Dialog", 0, 12));
        detailsButton.setText("Details ...");
        detailsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detailsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        stepRunPanel.add(detailsButton, gridBagConstraints);

        resetButton.setFont(new java.awt.Font("Dialog", 0, 12));
        resetButton.setText("Reset");
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 30);
        stepRunPanel.add(resetButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        advancedPanel.add(stepRunPanel, gridBagConstraints);

        controlPanel.setLayout(new java.awt.GridBagLayout());

        pvBusLimitLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        pvBusLimitLabel.setText("PV Bus Limit Control");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 10, 1, 10);
        controlPanel.add(pvBusLimitLabel, gridBagConstraints);

        pvBusLimitComboBox.setFont(new java.awt.Font("Dialog", 0, 10));
        pvBusLimitComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All" }));
        pvBusLimitComboBox.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 10, 1, 10);
        controlPanel.add(pvBusLimitComboBox, gridBagConstraints);

        pvBusLimitPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        pvBusLimitButton.setFont(new java.awt.Font("Dialog", 0, 10));
        pvBusLimitButton.setText("Apply");
        pvBusLimitButton.setEnabled(false);
        pvBusLimitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pvBusLimitButtonActionPerformed(evt);
            }
        });
        pvBusLimitPanel.add(pvBusLimitButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 10, 1, 10);
        controlPanel.add(pvBusLimitPanel, gridBagConstraints);

        pqBusLimitLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        pqBusLimitLabel.setText("PQ Bus Limit Control");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 10, 1, 10);
        controlPanel.add(pqBusLimitLabel, gridBagConstraints);

        pqBusLimitComboBox.setFont(new java.awt.Font("Dialog", 0, 10));
        pqBusLimitComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All" }));
        pqBusLimitComboBox.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 10, 1, 10);
        controlPanel.add(pqBusLimitComboBox, gridBagConstraints);

        pqBusLimitButton.setFont(new java.awt.Font("Dialog", 0, 10));
        pqBusLimitButton.setText("Apply");
        pqBusLimitButton.setEnabled(false);
        pqBusLimitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pqBusLimitButtonActionPerformed(evt);
            }
        });
        pqBusLimitPanel.add(pqBusLimitButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 10, 1, 10);
        controlPanel.add(pqBusLimitPanel, gridBagConstraints);

        remoteQBusLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        remoteQBusLabel.setText("Remote Q Bus Adjustment");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 10, 1, 10);
        controlPanel.add(remoteQBusLabel, gridBagConstraints);

        remoteQBusComboBox.setFont(new java.awt.Font("Dialog", 0, 10));
        remoteQBusComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All" }));
        remoteQBusComboBox.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 10, 1, 10);
        controlPanel.add(remoteQBusComboBox, gridBagConstraints);

        remoteQBusButton.setFont(new java.awt.Font("Dialog", 0, 10));
        remoteQBusButton.setText("Apply");
        remoteQBusButton.setEnabled(false);
        remoteQBusButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                remoteQBusButtonActionPerformed(evt);
            }
        });
        remoteQBusPanel.add(remoteQBusButton);

        remoteQBusXLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        remoteQBusXLabel.setText("x");
        remoteQBusXLabel.setEnabled(false);
        remoteQBusPanel.add(remoteQBusXLabel);

        remoteQBusTextField.setFont(new java.awt.Font("Dialog", 0, 10));
        remoteQBusTextField.setText("1.00");
        remoteQBusTextField.setEnabled(false);
        remoteQBusPanel.add(remoteQBusTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 10, 1, 10);
        controlPanel.add(remoteQBusPanel, gridBagConstraints);

        funcLoadLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        funcLoadLabel.setText("Functional Load Adjustment");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 10, 1, 10);
        controlPanel.add(funcLoadLabel, gridBagConstraints);

        funcLoadComboBox.setFont(new java.awt.Font("Dialog", 0, 10));
        funcLoadComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All" }));
        funcLoadComboBox.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 10, 1, 10);
        controlPanel.add(funcLoadComboBox, gridBagConstraints);

        funcLoadButton.setFont(new java.awt.Font("Dialog", 0, 10));
        funcLoadButton.setText("Apply");
        funcLoadButton.setEnabled(false);
        funcLoadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                funcLoadButtonActionPerformed(evt);
            }
        });
        funcLoadPanel.add(funcLoadButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 10, 1, 10);
        controlPanel.add(funcLoadPanel, gridBagConstraints);

        xfrTapControlLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        xfrTapControlLabel.setText("Transformer Tap Adjustment");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 10, 1, 10);
        controlPanel.add(xfrTapControlLabel, gridBagConstraints);

        xfrTapControlComboBox.setFont(new java.awt.Font("Dialog", 0, 10));
        xfrTapControlComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All" }));
        xfrTapControlComboBox.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 10, 1, 10);
        controlPanel.add(xfrTapControlComboBox, gridBagConstraints);

        xfrTapControlButton.setFont(new java.awt.Font("Dialog", 0, 10));
        xfrTapControlButton.setText("Apply");
        xfrTapControlButton.setEnabled(false);
        xfrTapControlButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xfrTapControlButtonActionPerformed(evt);
            }
        });
        xfrTapControlPanel.add(xfrTapControlButton);

        xfrTapControlXLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        xfrTapControlXLabel.setText("x");
        xfrTapControlXLabel.setEnabled(false);
        xfrTapControlPanel.add(xfrTapControlXLabel);

        xfrTapControlTextField.setFont(new java.awt.Font("Dialog", 0, 10));
        xfrTapControlTextField.setText("1.00");
        xfrTapControlTextField.setEnabled(false);
        xfrTapControlPanel.add(xfrTapControlTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 10, 1, 10);
        controlPanel.add(xfrTapControlPanel, gridBagConstraints);

        psXfrPControlLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        psXfrPControlLabel.setText("PS-Transformer Angle Adjustment");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 10, 1, 10);
        controlPanel.add(psXfrPControlLabel, gridBagConstraints);

        psXfrPControlComboBox.setFont(new java.awt.Font("Dialog", 0, 10));
        psXfrPControlComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All" }));
        psXfrPControlComboBox.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 10, 1, 10);
        controlPanel.add(psXfrPControlComboBox, gridBagConstraints);

        psXfrPControlButton.setFont(new java.awt.Font("Dialog", 0, 10));
        psXfrPControlButton.setText("Apply");
        psXfrPControlButton.setEnabled(false);
        psXfrPControlButton.setIconTextGap(5);
        psXfrPControlButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                psXfrPControlButtonActionPerformed(evt);
            }
        });
        psXfrPControlPanel.add(psXfrPControlButton);

        psXfrPControlXLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        psXfrPControlXLabel.setText("x");
        psXfrPControlXLabel.setEnabled(false);
        psXfrPControlPanel.add(psXfrPControlXLabel);

        psXfrPControlTextField.setFont(new java.awt.Font("Dialog", 0, 10));
        psXfrPControlTextField.setText("1.00");
        psXfrPControlTextField.setEnabled(false);
        psXfrPControlPanel.add(psXfrPControlTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 10, 1, 10);
        controlPanel.add(psXfrPControlPanel, gridBagConstraints);

        interPControlLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        interPControlLabel.setText("Interarea Power Exchange Control");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 10, 1, 10);
        controlPanel.add(interPControlLabel, gridBagConstraints);

        interPControlComboBox.setFont(new java.awt.Font("Dialog", 0, 10));
        interPControlComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All" }));
        interPControlComboBox.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 10, 1, 10);
        controlPanel.add(interPControlComboBox, gridBagConstraints);

        interPControlButton.setFont(new java.awt.Font("Dialog", 0, 10));
        interPControlButton.setText("Apply");
        interPControlButton.setEnabled(false);
        interPControlButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                interPControlButtonActionPerformed(evt);
            }
        });
        interPControlPanel.add(interPControlButton);

        interPControlXLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        interPControlXLabel.setText("x");
        interPControlXLabel.setEnabled(false);
        interPControlPanel.add(interPControlXLabel);

        interPControlTextField.setFont(new java.awt.Font("Dialog", 0, 10));
        interPControlTextField.setText("1.00");
        interPControlTextField.setEnabled(false);
        interPControlPanel.add(interPControlTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 10, 1, 10);
        controlPanel.add(interPControlPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        advancedPanel.add(controlPanel, gridBagConstraints);

        msgScrollPane.setPreferredSize(new java.awt.Dimension(480, 60));

        msgOutTextArea.setColumns(50);
        msgOutTextArea.setEditable(false);
        msgOutTextArea.setFont(new java.awt.Font("Dialog", 0, 10));
        msgOutTextArea.setRows(4);
        msgOutTextArea.setText("Line-1\nLine-1\nLine-1\nLine-1\nLine-1");
        msgScrollPane.setViewportView(msgOutTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        advancedPanel.add(msgScrollPane, gridBagConstraints);

        runAclfTabbedPane.addTab("Loadflow Lab", advancedPanel);

        add(runAclfTabbedPane, new java.awt.GridBagConstraints());
    }// </editor-fold>//GEN-END:initComponents

    private void nrRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nrRadioButtonActionPerformed
    	accFactorTextField.setEnabled(false);
    	accFactorLabel.setEnabled(false);
    }//GEN-LAST:event_nrRadioButtonActionPerformed

    private void pqRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pqRadioButtonActionPerformed
    	accFactorTextField.setEnabled(false);
    	accFactorLabel.setEnabled(false);
    	if (_netContainer != null && _netContainer.isBranchR_LT_X()) {
    		CoreCommonSpringFactory.getEditorDialogUtil().showMsgDialog("Warning",
                "You have branch(es) R > X in your next work, PQ method may diverge. Use NR is recommended");
    	}
    }//GEN-LAST:event_pqRadioButtonActionPerformed

    private void gsRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsRadioButtonActionPerformed
    	accFactorTextField.setEnabled(true);
    	accFactorLabel.setEnabled(true);
	}//GEN-LAST:event_gsRadioButtonActionPerformed

    private void pqQStepButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pqQStepButtonActionPerformed
    	IpssLogger.getLogger().info("PQ-Q Step run");
    	_simuCtx.getLoadflowAlgorithm().setLfMethod(AclfMethod.PQ_QSTEP);
    	_simuCtx.getLoadflowAlgorithm().loadflow();
        initAdvanceControlPanel();
    	mismatchLabel.setText(_simuCtx.getAclfNet().maxMismatch(AclfMethod.NR).toString());
    }//GEN-LAST:event_pqQStepButtonActionPerformed

    private void panelSelectionChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_panelSelectionChanged
    	if ( runAclfTabbedPane.getSelectedIndex() == 0 ) {
        	IpssLogger.getLogger().info("Panel selection changed - Main Panel");
        	this.panelSelected = LoadflowPanel;
    	}
    	else if ( runAclfTabbedPane.getSelectedIndex() == 1 ) {
        	IpssLogger.getLogger().info("Panel selection changed - Contingency Panel");
        	this.panelSelected = ContingencyPanel;
    	}	
    	else if ( runAclfTabbedPane.getSelectedIndex() == 2 ) {
        	IpssLogger.getLogger().info("Panel selection changed - Advanced Panel");
        	this.panelSelected = AdvancedPanel;
            initAdvanceControlPanel();
        	mismatchLabel.setText(_simuCtx.getAclfNet().maxMismatch(AclfMethod.NR).toString());
    	}	
    }//GEN-LAST:event_panelSelectionChanged

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
    	IpssLogger.getLogger().info("Reset ...");
    	_simuCtx.getAclfNet().initializeBusVoltage();
    	_simuCtx.getAclfNet().getAclfNetAdjust().activateAllAdjust();
		msgOutTextArea.setText("");
        initAdvanceControlPanel();
    	mismatchLabel.setText(_simuCtx.getAclfNet().maxMismatch(AclfMethod.NR).toString());
    }//GEN-LAST:event_resetButtonActionPerformed

    private void detailsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_detailsButtonActionPerformed
    	this.parent.setAlwaysOnTop(false);
    	IpssLogger.getLogger().info("Details ...");
  		IOutputTextDialog dialog = UISpringAppContext.getOutputTextDialog("Loadflow Analysis Info");
  		dialog.display(_simuCtx.getAclfNet());
    }//GEN-LAST:event_detailsButtonActionPerformed

    private void funcLoadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_funcLoadButtonActionPerformed
        String selected = (String)funcLoadComboBox.getSelectedItem();
        if (selected.equals(RunActUtilFunc.AllControlDevices)) {
        	IpssLogger.getLogger().info("Apply All Function load adjustment");
        	//_simuCtx.getLoadflowAlgorithm().getAdjAlgorithm().doFuncLoadAdjust();
        }
        else {
        	String id = new StringTokenizer(selected).nextToken();
        	FunctionLoad load = _simuCtx.getAclfNet().getAclfBus(id).getFunctionLoad();
        	load.performAdjust();
        	IpssLogger.getLogger().info("Apply Function load adjustment: " + id);
        }
        initAdvanceControlPanel();
    	mismatchLabel.setText(_simuCtx.getAclfNet().maxMismatch(AclfMethod.NR).toString());
    }//GEN-LAST:event_funcLoadButtonActionPerformed

    private void xfrTapControlButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xfrTapControlButtonActionPerformed
        String selected = (String)xfrTapControlComboBox.getSelectedItem();
//        if (selected.equals(RunActUtilFunc.AllControlDevices)) {
//        	IpssLogger.getLogger().info("Apply All Xfr Tap Controls");
//        	_simuCtx.getLoadflowAlgorithm().getAdjAlgorithm().doTapVControl();
//        }
//        else {
//        	String id = new StringTokenizer(selected).nextToken();
//        	TapControl xfr = _simuCtx.getAclfAdjNet().getTapControl(id);
//        	xfr.performAdjust();
//        	IpssLogger.getLogger().info("Apply Xfr Tap Control: " + id);
//        }
        initAdvanceControlPanel();
    	mismatchLabel.setText(_simuCtx.getAclfNet().maxMismatch(AclfMethod.NR).toString());
    }//GEN-LAST:event_xfrTapControlButtonActionPerformed

    private void psXfrPControlButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_psXfrPControlButtonActionPerformed
        String selected = (String)psXfrPControlComboBox.getSelectedItem();
//        if (selected.equals(RunActUtilFunc.AllControlDevices)) {
//        	IpssLogger.getLogger().info("Apply All PS Xfr PControls");
//        	_simuCtx.getLoadflowAlgorithm().getAdjAlgorithm().doPSXfrPControl();
//        }
//        else {
//        	String id = new StringTokenizer(selected).nextToken();
//        	PSXfrPControl psXfr = _simuCtx.getAclfAdjNet().getPSXfrPControl(id);
//        	psXfr.performAdjust();
//        	IpssLogger.getLogger().info("Apply PS Xfr PControl: " + id);
//        }
        initAdvanceControlPanel();
    	mismatchLabel.setText(_simuCtx.getAclfNet().maxMismatch(AclfMethod.NR).toString());
    }//GEN-LAST:event_psXfrPControlButtonActionPerformed

    private void interPControlButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_interPControlButtonActionPerformed
        String selected = (String)interPControlComboBox.getSelectedItem();
        if (selected.equals(RunActUtilFunc.AllControlDevices)) {
        	IpssLogger.getLogger().info("Apply All Interarea exchagnge controls");
        	_simuCtx.getLoadflowAlgorithm().getNetAdjAlgo().doInterAreaPowerAdjust();
        }
        else {
        	String no = new StringTokenizer(selected).nextToken();
        	Area area = _simuCtx.getAclfNet().getArea(new Integer(no).intValue());
			if (area.getRegDeviceList().size() > 0) {
				// there should be only one controller per area
				IRegulationDevice regDevice = (IRegulationDevice)area.getRegDeviceList().get(0);
				regDevice.performAdjusment(area, _simuCtx.getAclfNet());
			}
			IpssLogger.getLogger().info("Apply Interarea exchagnge controls: " + no);
        }
        initAdvanceControlPanel();
    	mismatchLabel.setText(_simuCtx.getAclfNet().maxMismatch(AclfMethod.NR).toString());
    }//GEN-LAST:event_interPControlButtonActionPerformed

    private void remoteQBusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_remoteQBusButtonActionPerformed
        String selected = (String)remoteQBusComboBox.getSelectedItem();
        if (selected.equals(RunActUtilFunc.AllControlDevices)) {
        	IpssLogger.getLogger().info("Apply All Remote Q Bus adjustment");
        	//_simuCtx.getLoadflowAlgorithm().getAdjAlgorithm().doRemoteQVAdjust();
        }
        else {
        	String id = new StringTokenizer(selected).nextToken();
    		AclfBus bus = _simuCtx.getAclfNet().getAclfBus(id);
    		bus.getRemoteQBus().performAdjust();
        	IpssLogger.getLogger().info("Apply Remote Q Bus adjustment: " + id);
        }
        initAdvanceControlPanel();
    	mismatchLabel.setText(_simuCtx.getAclfNet().maxMismatch(AclfMethod.NR).toString());
    }//GEN-LAST:event_remoteQBusButtonActionPerformed

    private void pqBusLimitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pqBusLimitButtonActionPerformed
        String selected = (String)pqBusLimitComboBox.getSelectedItem();
        if (selected.equals(RunActUtilFunc.AllControlDevices)) {
        	IpssLogger.getLogger().info("Apply All PQ Bus Limit");
        	//_simuCtx.getLoadflowAlgorithm().getAdjAlgorithm().doPQBusLimitAdjust(AclfMethod.PQ);
        }
        else {
        	String id = new StringTokenizer(selected).nextToken();
    		AclfBus bus = _simuCtx.getAclfNet().getAclfBus(id);
        	bus.getPQBusLimit().performAdjust();
        	IpssLogger.getLogger().info("Apply PQ Bus Limit: " + id);
        }
        initAdvanceControlPanel();
    	mismatchLabel.setText(_simuCtx.getAclfNet().maxMismatch(AclfMethod.NR).toString());
    }//GEN-LAST:event_pqBusLimitButtonActionPerformed

    private void pvBusLimitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pvBusLimitButtonActionPerformed
        String selected = (String)pvBusLimitComboBox.getSelectedItem();
        if (selected.equals(RunActUtilFunc.AllControlDevices)) {
        	IpssLogger.getLogger().info("Apply All PV Bus Limit");
        	//_simuCtx.getLoadflowAlgorithm().getAdjAlgorithm().doPVBusLimitAdjust(AclfMethod.PQ);
        }
        else {
        	String id = new StringTokenizer(selected).nextToken();
    		AclfBus bus = _simuCtx.getAclfNet().getAclfBus(id);
        	bus.getPVBusLimit().performAdjust();
        	IpssLogger.getLogger().info("Apply PV Bus Limit: " + id);
        }
        initAdvanceControlPanel();
        mismatchLabel.setText(_simuCtx.getAclfNet().maxMismatch(AclfMethod.NR).toString());
    }//GEN-LAST:event_pvBusLimitButtonActionPerformed

    private void gsStepButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsStepButtonActionPerformed
    	IpssLogger.getLogger().info("GS Step run");
    	_simuCtx.getLoadflowAlgorithm().setLfMethod(AclfMethod.GS_STEP);
    	_simuCtx.getLoadflowAlgorithm().loadflow();
        initAdvanceControlPanel();
    	mismatchLabel.setText(_simuCtx.getAclfNet().maxMismatch(AclfMethod.NR).toString());
    }//GEN-LAST:event_gsStepButtonActionPerformed

    private void nrStepButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nrStepButtonActionPerformed
    	IpssLogger.getLogger().info("NR Step run");
    	_simuCtx.getLoadflowAlgorithm().setLfMethod(AclfMethod.NR_STEP);
    	_simuCtx.getLoadflowAlgorithm().loadflow();
        initAdvanceControlPanel();
    	mismatchLabel.setText(_simuCtx.getAclfNet().maxMismatch(AclfMethod.NR).toString());
    }//GEN-LAST:event_nrStepButtonActionPerformed

    private void pqPStepButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pqPStepButtonActionPerformed
    	IpssLogger.getLogger().info("PQ-P Step run");
    	_simuCtx.getLoadflowAlgorithm().setLfMethod(AclfMethod.PQ_PSTEP);
    	_simuCtx.getLoadflowAlgorithm().loadflow();
        initAdvanceControlPanel();
    	mismatchLabel.setText(_simuCtx.getAclfNet().maxMismatch(AclfMethod.NR).toString());
    }//GEN-LAST:event_pqPStepButtonActionPerformed

    private void customRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_customRadioButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_customRadioButtonActionPerformed

    private void n1ContingencyRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_n1ContingencyRadioButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_n1ContingencyRadioButtonActionPerformed

    private void n11ContingencyRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_n11ContingencyRadioButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_n11ContingencyRadioButtonActionPerformed

    private void n2ContingencyRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_n2ContingencyRadioButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_n2ContingencyRadioButtonActionPerformed

    private void continCaseCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_continCaseCheckBoxActionPerformed
    	continCaseTextField.setEnabled(true);
    }//GEN-LAST:event_continCaseCheckBoxActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ContingencyDataPanel;
    private javax.swing.JLabel accFactorLabel;
    private javax.swing.JTextField accFactorTextField;
    private javax.swing.JPanel advancedPanel;
    private javax.swing.JCheckBox continCaseCheckBox;
    private javax.swing.JTextField continCaseTextField;
    private javax.swing.ButtonGroup contingencyButtonGroup;
    private javax.swing.JPanel contingencyMethodPanel;
    private javax.swing.JPanel contingencyPanel;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JRadioButton customRadioButton;
    private javax.swing.JButton detailsButton;
    private javax.swing.JTextField errKVATextField;
    private javax.swing.JLabel errKVAUnitLabel;
    private javax.swing.JTextField errPUTextField;
    private javax.swing.JLabel errPUUnitLabel;
    private javax.swing.JButton funcLoadButton;
    private javax.swing.JComboBox funcLoadComboBox;
    private javax.swing.JLabel funcLoadLabel;
    private javax.swing.JPanel funcLoadPanel;
    private javax.swing.JPanel gridComputingPanel;
    private javax.swing.JPanel gridContingencyPanel;
    private javax.swing.JRadioButton gsRadioButton;
    private javax.swing.JButton gsStepButton;
    private javax.swing.JCheckBox initVoltCheckBox;
    private javax.swing.JButton interPControlButton;
    private javax.swing.JComboBox interPControlComboBox;
    private javax.swing.JLabel interPControlLabel;
    private javax.swing.JPanel interPControlPanel;
    private javax.swing.JTextField interPControlTextField;
    private javax.swing.JLabel interPControlXLabel;
    private javax.swing.JCheckBox lfSummaryCheckBox;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JLabel maxItrLabel;
    private javax.swing.JTextField maxItrTextField;
    private javax.swing.ButtonGroup methodButtonGroup;
    private javax.swing.JPanel methodPanel;
    private javax.swing.JLabel misTitleLabel;
    private javax.swing.JLabel mismatchLabel;
    private javax.swing.JTextArea msgOutTextArea;
    private javax.swing.JScrollPane msgScrollPane;
    private javax.swing.JRadioButton n11ContingencyRadioButton;
    private javax.swing.JRadioButton n1ContingencyRadioButton;
    private javax.swing.JRadioButton n2ContingencyRadioButton;
    private javax.swing.JCheckBox nonDivergeCheckBox;
    private javax.swing.JRadioButton nrRadioButton;
    private javax.swing.JButton nrStepButton;
    private javax.swing.JPanel paramPanel;
    private javax.swing.JButton pqBusLimitButton;
    private javax.swing.JComboBox pqBusLimitComboBox;
    private javax.swing.JLabel pqBusLimitLabel;
    private javax.swing.JPanel pqBusLimitPanel;
    private javax.swing.JButton pqPStepButton;
    private javax.swing.JButton pqQStepButton;
    private javax.swing.JRadioButton pqRadioButton;
    private javax.swing.JButton psXfrPControlButton;
    private javax.swing.JComboBox psXfrPControlComboBox;
    private javax.swing.JLabel psXfrPControlLabel;
    private javax.swing.JPanel psXfrPControlPanel;
    private javax.swing.JTextField psXfrPControlTextField;
    private javax.swing.JLabel psXfrPControlXLabel;
    private javax.swing.JButton pvBusLimitButton;
    private javax.swing.JComboBox pvBusLimitComboBox;
    private javax.swing.JLabel pvBusLimitLabel;
    private javax.swing.JPanel pvBusLimitPanel;
    private javax.swing.JButton remoteQBusButton;
    private javax.swing.JComboBox remoteQBusComboBox;
    private javax.swing.JLabel remoteQBusLabel;
    private javax.swing.JPanel remoteQBusPanel;
    private javax.swing.JTextField remoteQBusTextField;
    private javax.swing.JLabel remoteQBusXLabel;
    private javax.swing.JButton resetButton;
    private javax.swing.JTabbedPane runAclfTabbedPane;
    private javax.swing.JPanel stepRunPanel;
    private javax.swing.JLabel toleranceLabel;
    private javax.swing.JButton xfrTapControlButton;
    private javax.swing.JComboBox xfrTapControlComboBox;
    private javax.swing.JLabel xfrTapControlLabel;
    private javax.swing.JPanel xfrTapControlPanel;
    private javax.swing.JTextField xfrTapControlTextField;
    private javax.swing.JLabel xfrTapControlXLabel;
    // End of variables declaration//GEN-END:variables
    
	class DataVerifier extends javax.swing.InputVerifier {
		public boolean verify(javax.swing.JComponent input) {
			if (input == null)
				return false;
			try {
       			if (input == maxItrTextField )
 	       			return SwingInputVerifyUtil.getInt((javax.swing.JTextField)input) > 0;
       			else if (input == accFactorTextField )
 	       			return SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) > 0.0;
       			else if (input == errPUTextField) {
 	       			if (SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) > 0.0) {
                     double err = SwingInputVerifyUtil.getDouble(errPUTextField);
                     errKVATextField.setEditable(true);
                     errKVATextField.setText(Number2String.toStr(err*((GNetForm)_netContainer.getGNetForm()).getBaseKVA(), "#0.####"));
                     errKVATextField.setEditable(false);
                     return true;
                  }
                  return false;
               }  
			} catch (Exception e) {
				return false;
			}		
			return true;
		}
	}
}
