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

import static com.interpss.common.util.IpssLogger.ipssLogger;
import static org.ieee.odm.ODMObjectFactory.odmObjFactory;
import static org.interpss.CorePluginFunction.DclfGSFBranchFlow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFileChooser;

import org.eclipse.emf.ecore.change.util.ChangeRecorder;
import org.ieee.odm.model.base.BaseJaxbHelper;
import org.ieee.odm.schema.ActivePowerUnitType;
import org.ieee.odm.schema.ActivePowerXmlType;
import org.ieee.odm.schema.BranchRefXmlType;
import org.ieee.odm.schema.IDRecordXmlType;
import org.ieee.odm.schema.LfResultFormatEnumType;
import org.ieee.odm.schema.PTradingEDHourlyAnalysisXmlType;
import org.ieee.odm.schema.PowerTradingInfoXmlType;
import org.ieee.odm.schema.PtAclfAnalysisXmlType;
import org.ieee.odm.schema.PtAclfOutputXmlType;
import org.ieee.odm.schema.PtBranchAnalysisEnumType;
import org.ieee.odm.schema.PtBranchAnalysisXmlType;
import org.ieee.odm.schema.PtCaseDataXmlType;
import org.ieee.odm.schema.PtGenAnalysisEnumType;
import org.ieee.odm.schema.PtGenAnalysisXmlType;
import org.ieee.odm.schema.PtLoadDistributionXmlType;
import org.ieee.odm.schema.SenAnalysisBusEnumType;
import org.interpss.datatype.DblBusValue;
import org.interpss.editor.jgraph.GraphSpringFactory;
import org.interpss.editor.jgraph.ui.app.IAppStatus;
import org.interpss.editor.jgraph.ui.edit.IFormDataPanel;
import org.interpss.editor.ui.RunUIUtilFunc;
import org.interpss.editor.ui.util.IpssFileFilter;
import org.interpss.numeric.util.Number2String;
import org.interpss.spring.PluginSpringFactory;
import org.interpss.spring.UISpringFactory;
import org.interpss.ui.SwingInputVerifyUtil;

import com.interpss.common.datatype.Constants;
import com.interpss.common.exp.InterpssException;
import com.interpss.common.exp.InterpssRuntimeException;
import com.interpss.common.func.IFunction;
import com.interpss.common.msg.IpssMessage;
import com.interpss.common.msg.IpssMsgListener;
import com.interpss.common.util.IpssLogger;
import com.interpss.common.util.StringUtil;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.funcImpl.AclfNetHelper;
import com.interpss.core.funcImpl.CoreUtilFunc;
import com.interpss.core.net.Bus;
import com.interpss.core.net.Zone;
import com.interpss.pssl.display.PTradingOutput;
import com.interpss.pssl.odm.PTradingDslODMRunner;
import com.interpss.pssl.odm.PTradingDslODMRunner.PtAnalysisType;
import com.interpss.simu.SimuContext;

public class NBPTradingCasePanel extends javax.swing.JPanel implements IFormDataPanel, IpssMsgListener {
	private static final long serialVersionUID = 1;
	private JDialog parent;
	
    private SimuContext _simuCtx = null;
    
    private PTradingEDHourlyAnalysisXmlType _ptXml = null;
    private PowerTradingInfoXmlType _ptInfoXml = null;

    // for cache PV/Swing bus voltage of the previous run
    private List<DblBusValue> genPVSwingBusVoltCacheList = null;

    /** Creates new form NBAclfCasePanel */
    public NBPTradingCasePanel(JDialog parent) {
    	initComponents();
    	this.parent = parent;
    	
    	initInputVerifier(new DataVerifier());
    }
    
     @Override public void onMsgEvent(IpssMessage msg) {
    	 // do nothing
     }

     @Override public boolean onMsgEventStatus(IpssMessage msg) {
  	   throw new InterpssRuntimeException("Method not implemented");
     }
     
    public void init(Object netContainer, Object simuCtx) {
    	// for non-graphic file, netContainer == null
		ipssLogger.info("NBPTradingCasePanel init() called");
	    //_netContainer = (GFormContainer)netContainer;
	    _simuCtx = (SimuContext)simuCtx;
	    
	    this.pTradingAnalysisTabbedPane.setSelectedIndex(1);
	    
	    // populate the Swing alloc zone
	    AclfNetwork net = _simuCtx.getAclfNet();
	    try {
	    	AclfBus swingBus = net.getAclfBus(new AclfNetHelper(net).getSwingBusId());
	    	Zone zone = swingBus.getZone();
	    	// get the neighboring zones of the swing bus
	    	List<String> list = StringUtil.convertLongAry2StrList(CoreUtilFunc.getZoneNumberAry(net.neighborZones(zone)));
	    	// add swing bus zone to the list
	    	list.add(new Long(zone.getNumber()).toString());
		    this.swingAllocZoneComboBox.setModel(new javax.swing.DefaultComboBoxModel(list.toArray()));
	    } catch (InterpssException e) {
		    this.swingAllocZoneComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] {"Error"}));
	    }
	    
	    this.genPVSwingBusVoltCacheList = new ArrayList<DblBusValue>();
		this.aclfUseCachedVoltCheckBox.setSelected(false);
		this.aclfUseCachedVoltCheckBox.setEnabled(false);
		
		this.braAnaBranchListComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.AllBranch).toArray()));

		this.genAnalysisGenBusListComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.GenBus).toArray()));
		this.genLoadDistLoadBusComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.LoadBus).toArray()));
		
	}
    
    public void setXmlCaseData(PTradingEDHourlyAnalysisXmlType pt, PowerTradingInfoXmlType ptInfo) {
    	this._ptXml = pt;
    	this._ptInfoXml = ptInfo;
    }

// TODO
////////////////////////////////////////////////////////////////////
//////            Set Data to Editor                          //////    
////////////////////////////////////////////////////////////////////    
    
	/**
	*	Set form data to the editor
	*
	* @return false if there is any problem
	*/
	public boolean setForm2Editor() {
		ipssLogger.info("NBPTradingCasePanel setForm2Editor() called");
		
		if (this._ptInfoXml.getLoadDist() != null) {
			if (this._ptInfoXml.getLoadDist().getMinLoadForDistFactor() != null)
				this.loadDistThreshholdTextField.setText(
						Number2String.toStr(this._ptInfoXml.getLoadDist().getMinLoadForDistFactor().getValue(), "#0.0"));
		}

		PtAclfAnalysisXmlType aclfXml = this._ptXml.getAclfAnalysis();
		
		// Case Data Panel
		// ===============
		
		PtCaseDataXmlType casedata = this._ptXml.getCaseData();
		edFileTextField.setText(casedata.getEdFile().getFilename());
		
		edGenPFacorTextField.setText(Number2String.toStr(casedata.getEdFile().getGenPFactor(), "#0.00"));
		edLossPercentTextField.setText(Number2String.toStr(casedata.getEdFile().getLossPercent(), "#0.00"));
		edLoadPFacorTextField.setText(Number2String.toStr(casedata.getEdFile().getLoadPFactor(), "#0.00"));
		this.edDateTextField.setText(casedata.getEdFile().getDate());
		
		interfaceFileTextField.setText(this._ptInfoXml.getInterfaceFilename());
		interfaceLimitTextField.setText(casedata.getInterfaceLimitFilename());

		//aclfXml.setGenQAdjustment(true);
		if (aclfXml.getGenQAdjOption() != null) {
			lfAssistGenQAdjStespTextField.setText(
					new Integer(aclfXml.getGenQAdjOption().getNoRunsLfAssist()).toString());
			lfAssistGenFileTextField.setText(aclfXml.getGenQAdjOption().getLfAssistGenFilename());
			if (aclfXml.getGenQAdjOption().getLfAssistAdjTolerance() != null)
				this.lfAssistGenQAdjToleranceTextField.setText(
						aclfXml.getGenQAdjOption().getLfAssistAdjTolerance().toString());
		}
		
		// Aclf Analysis
		// ==============
			
		if (this._ptXml.getAclfAnalysis() == null)
			this._ptXml.setAclfAnalysis(odmObjFactory.createPtAclfAnalysisXmlType());

		aclfEdHourComboBox.setSelectedItem(aclfXml.getHour() == null? "12:00" : aclfXml.getHour());

		if (aclfXml.getTolerance() != null)
			this.aclfToleranceTextField.setText(Number2String.toStr(aclfXml.getTolerance(), "0.0000"));
		
		swingAllocCheckBox.setSelected(aclfXml.isSwingBusPQAlloc());
		if (aclfXml.isSwingBusPQAlloc()) {
			String n = new Long(aclfXml.getGenSwingAllocOption().getZoneNumber()).toString();
			this.swingAllocZoneComboBox.setSelectedItem(n);
			swingAllocMaxStepsTextField.setText(
					new Integer(aclfXml.getGenSwingAllocOption().getSteps()).toString());
			swingAllocAccFactorTextField.setText(
					new Double(aclfXml.getGenSwingAllocOption().getAccFactor()).toString());
			if(aclfXml.getGenSwingAllocOption().getAllocTolerance() != null)
				this.swingAllocToleranceTextField.setText(
					new Double(aclfXml.getGenSwingAllocOption().getAllocTolerance()).toString());
		}
		
		// Branch analysis Analysis
		// ========================
		
		if (this._ptXml.getBranchAnalysis() == null) 
			this._ptXml.setBranchAnalysis(odmObjFactory.createPtBranchAnalysisXmlType());
		PtBranchAnalysisXmlType braAnalysis = this._ptXml.getBranchAnalysis();
		this.braAnaEdHourComboBox.setSelectedItem(braAnalysis.getHour() == null? "12:00" : braAnalysis.getHour());
		if (braAnalysis.getType() != null) 
			if (braAnalysis.getType() == PtBranchAnalysisEnumType.SINGLE_BRANCH_OUTAGE)
				this.braAnaOutageSingleRadioButton.setSelected(true);
			else if (braAnalysis.getType() == PtBranchAnalysisEnumType.MULTI_BRANCH_OUTAGE)
				this.braAnaOutageMultiRadioButton.setSelected(true);
			else if (braAnalysis.getType() == PtBranchAnalysisEnumType.OUTAGE_SCHEDULE)
				this.braAnaOutageScheduleRadioButton.setSelected(true);
			else if (braAnalysis.getType() == PtBranchAnalysisEnumType.BRANCH_FLOW)
				this.braAnaFlowRadioButton.setSelected(true);

		if (this.braAnaOutageMultiRadioButton.isSelected()) {
			braAnaOutageMultiRadioButtonActionPerformed(null);
			// TODO
		}
		else if (this.braAnaOutageScheduleRadioButton.isSelected()) {
			braAnaOutageScheduleRadioButtonActionPerformed(null);
		}
		else if (this.braAnaFlowRadioButton.isSelected()) {
			braAnaFlowRadioButtonActionPerformed(null);
			this.braAnaFlowOutPointsTextField.setText(
					braAnalysis.getBranchFlowOutPoints().toString());
		}
		
		if (braAnalysis.getBranch() != null && braAnalysis.getBranch().size() > 0) {
			BranchRefXmlType branch = braAnalysis.getBranch().get(0);
			this.braAnaBranchListComboBox.setSelectedItem(branch.getBranchId());
		}
		
		// Gen analysis Analysis
		// =====================
		
		if (this._ptXml.getGenAnalysis() == null) 
			this._ptXml.setGenAnalysis(odmObjFactory.createPtGenAnalysisXmlType());
		PtGenAnalysisXmlType genAnalysis = this._ptXml.getGenAnalysis();
		if (genAnalysis.getGenBus() != null) {
			String[] sAry = StringUtil.getIdNameAry(genAnalysis.getGenBus(), new IFunction<Object,String>() {
				@Override public String f(Object value) { return ((IDRecordXmlType)value).getId(); } });
			this.genAnalysisGenBusList.setModel(new javax.swing.DefaultComboBoxModel(sAry));    	
		}
		
		if (genAnalysis.getLoadBusType() == SenAnalysisBusEnumType.SINGLE_BUS) {
			this.genLoadDistBusRadioButton.setSelected(true);
			genLoadDistBusRadioButtonActionPerformed(null);
			String id = genAnalysis.getLoadBusId();
			this.genLoadDistLoadBusComboBox.setSelectedItem(id);
		}
		else if (genAnalysis.getLoadBusType() == SenAnalysisBusEnumType.LOAD_DISTRIBUTION) {
			this.genLoadDistBasecaseRadioButton.setSelected(true);
			this.genLoadDistBasecaseRadioButtonActionPerformed(null);
		}
		else {
			this.genLoadDistUserRadioButton.setSelected(true);
			this.genLoadDistUserRadioButtonActionPerformed(null);
			this.genLoadDistUserFileTextField.setText(genAnalysis.getUserFilename());
		}

		// output panel
		// ============
		
		PtAclfOutputXmlType outOpt = this._ptXml.getAclfAnalysis().getOutputOption();
		outLargeGSFPointsTextField.setText(
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
		this.outLfResultCheckBox.setSelected(outOpt.isLfResult());
		if (outOpt.getLfResultFormat() == LfResultFormatEnumType.BUS_STYLE)
			this.outLfBusStyleRadioButton.setSelected(true);
		else
			this.outLfSummaryRadioButton.setSelected(true);

		return true;
	}

// TODO
////////////////////////////////////////////////////////////////////
//////            Set Data to ODM Xml                         //////    
////////////////////////////////////////////////////////////////////	

	/**
	*	Save editor screen data to the form
	*
	* @param errMsg error messages during the saving process.
	* @return false if there is any problem
	*/
	public boolean saveEditor2Form(Vector<String> errMsg) throws Exception {
		return saveEditor2Form(errMsg, false);
	}
	public boolean saveEditor2Form(Vector<String> errMsg, boolean run) throws Exception {
		ipssLogger.info("NBPTradingCasePanel saveEditor2Form() called");
		
		if (this._ptInfoXml.getLoadDist() != null &&
				this._ptInfoXml.getLoadDist().getAggregateGen() != null)
			this._ptInfoXml.getLoadDist().getAggregateGen().getApGroup().clear();

		saveCaseData(errMsg);
		saveAclfAnalysis(errMsg, run);
		saveBranchAnalysis(errMsg);
		saveGenAnalysis(errMsg, run);
		saveOutputConfig(errMsg);

		return errMsg.size() == 0;
	}

	public boolean saveCaseData(Vector<String> errMsg) {
		boolean noError = true;
		if (this._ptXml.getCaseData() == null) {
			this._ptXml.setCaseData(odmObjFactory.createPtCaseDataXmlType());
		}
		PtCaseDataXmlType casedata = this._ptXml.getCaseData();
		
		casedata.getEdFile().setFilename(
				edFileTextField.getText());
		casedata.getEdFile().setGenPFactor(
				new Double(edGenPFacorTextField.getText()).doubleValue());
		casedata.getEdFile().setLossPercent(
				new Double(edLossPercentTextField.getText()).doubleValue());
		casedata.getEdFile().setLoadPFactor(
				new Double(edLoadPFacorTextField.getText()).doubleValue());
		casedata.getEdFile().setDate(this.edDateTextField.getText());
		
		if (this._ptInfoXml.getLoadDist() == null) {
			PtLoadDistributionXmlType load = odmObjFactory.createPtLoadDistributionXmlType();
			this._ptInfoXml.setLoadDist(load);
			ActivePowerXmlType p = odmObjFactory.createActivePowerXmlType();
			load.setMinLoadForDistFactor(p);
		}
		this._ptInfoXml.getLoadDist().getMinLoadForDistFactor().setValue(
				new Double(this.loadDistThreshholdTextField.getText()).doubleValue());
		this._ptInfoXml.getLoadDist().getMinLoadForDistFactor().setUnit(ActivePowerUnitType.MW);

		if (this._ptInfoXml.getLoadDist().getAggregateGen() == null)
			this._ptInfoXml.getLoadDist().setAggregateGen(odmObjFactory.createAggregateGenXmlType());
		
		this._ptInfoXml.setInterfaceFilename(
				interfaceFileTextField.getText());
		casedata.setInterfaceLimitFilename(
				interfaceLimitTextField.getText());

		if (this._ptXml.getAclfAnalysis() == null) {
			this._ptXml.setAclfAnalysis(odmObjFactory.createPtAclfAnalysisXmlType());
		}
		PtAclfAnalysisXmlType aclfXml = this._ptXml.getAclfAnalysis();
		
		if (aclfXml.getGenQAdjOption() == null) {
			aclfXml.setGenQAdjOption(odmObjFactory.createPtGenQAdjustXmlType());
		}

		String filename = lfAssistGenFileTextField.getText();
		aclfXml.setGenQAdjustment(filename != null & !filename.equals(""));
		if (aclfXml.isGenQAdjustment()) {
			aclfXml.getGenQAdjOption().setNoRunsLfAssist(
					new Integer(lfAssistGenQAdjStespTextField.getText()).intValue());
			aclfXml.getGenQAdjOption().setLfAssistGenFilename(filename);
			aclfXml.getGenQAdjOption().setLfAssistAdjTolerance(
					new Double(this.lfAssistGenQAdjToleranceTextField.getText()).doubleValue());
		}
		
		return noError;
	}

	public boolean saveAclfAnalysis(Vector<String> errMsg, boolean run) {
		boolean noError = true;

		if (this._ptXml.getAclfAnalysis() == null) {
			this._ptXml.setAclfAnalysis(odmObjFactory.createPtAclfAnalysisXmlType());
			this._ptXml.getAclfAnalysis().setGenSwingAllocOption(odmObjFactory.createPtSwingAllocXmlType());
		}
		PtAclfAnalysisXmlType aclfXml = this._ptXml.getAclfAnalysis();
	 	
		aclfXml.setHour(this.aclfEdHourComboBox.getSelectedItem().toString());
		
		aclfXml.setTolerance(new Double(this.aclfToleranceTextField.getText()).doubleValue());
		
		aclfXml.setSwingBusPQAlloc(swingAllocCheckBox.isSelected());
		if (aclfXml.isSwingBusPQAlloc()) {
			if (this.swingAllocZoneComboBox.getSelectedItem() == null && run) {
				errMsg.add(new String("Please select swing bus power allocation zone"));
				noError = false;
			}
			String n = (String)this.swingAllocZoneComboBox.getSelectedItem();
			aclfXml.getGenSwingAllocOption().setZoneNumber(
					new Long(n).longValue());
			aclfXml.getGenSwingAllocOption().setSteps(
					new Integer(swingAllocMaxStepsTextField.getText()).intValue());
			aclfXml.getGenSwingAllocOption().setAccFactor(
					new Double(swingAllocAccFactorTextField.getText()).doubleValue());
		}
		
		return noError;
	}

	public boolean saveBranchAnalysis(Vector<String> errMsg) {
		boolean noError = true;
		
		if (this._ptXml.getBranchAnalysis() == null) {
			this._ptXml.setBranchAnalysis(odmObjFactory.createPtBranchAnalysisXmlType());
		}
		PtBranchAnalysisXmlType braAnalysis = this._ptXml.getBranchAnalysis();
		
		braAnalysis.setHour((String)this.braAnaEdHourComboBox.getSelectedItem());
		braAnalysis.setType(this.braAnaOutageSingleRadioButton.isSelected()? PtBranchAnalysisEnumType.SINGLE_BRANCH_OUTAGE :
			(this.braAnaOutageMultiRadioButton.isSelected()? PtBranchAnalysisEnumType.MULTI_BRANCH_OUTAGE :
				(this.braAnaOutageScheduleRadioButton.isSelected()? PtBranchAnalysisEnumType.SINGLE_BRANCH_OUTAGE :
					PtBranchAnalysisEnumType.BRANCH_FLOW)));

		if (this.braAnaOutageSingleRadioButton.isSelected() ||
				this.braAnaFlowRadioButton.isSelected()) {
			braAnalysis.getBranch().clear();
			String braId = (String)this.braAnaBranchListComboBox.getSelectedItem();
			BranchRefXmlType outage = BaseJaxbHelper.creatBranchRef(braAnalysis.getBranch());
			RunUIUtilFunc.setBranchIdInfo(outage, braId);
			if (this.braAnaFlowRadioButton.isSelected()) {
			    braAnalysis.setBranchFlowOutPoints(
			    		new Integer(this.braAnaFlowOutPointsTextField.getText()).intValue());
			}
		}
		else if (this.braAnaOutageMultiRadioButton.isSelected()) {
		}
		else if (this.braAnaOutageScheduleRadioButton.isSelected()) {
		}
		
		return noError;
	}

	public boolean saveGenAnalysis(Vector<String> errMsg, boolean run) {
		boolean noError = true;
		
		if (this._ptXml.getGenAnalysis() == null) {
			this._ptXml.setGenAnalysis(odmObjFactory.createPtGenAnalysisXmlType());
		}
		PtGenAnalysisXmlType genAnalysis = this._ptXml.getGenAnalysis();
		
		int size = this.genAnalysisGenBusList.getModel().getSize();
		if (size == 0 && run) {
			errMsg.add("Please select at least one generator");
			noError = false;
		}
		else {
			genAnalysis.getGenBus().clear();
			for (int i = 0; i < size; i++) {
				String id = (String)this.genAnalysisGenBusList.getModel().getElementAt(i);
				IDRecordXmlType bus = odmObjFactory.createIDRecordXmlType();
				genAnalysis.getGenBus().add(bus);
				bus.setId(id);	
			}
		}
		
		if (this.genLoadDistBusRadioButton.isSelected()) {
			genAnalysis.setLoadBusType(SenAnalysisBusEnumType.SINGLE_BUS);
			String id = (String)this.genLoadDistLoadBusComboBox.getSelectedItem();
			genAnalysis.setLoadBusId(id);
		}
		else if (this.genLoadDistBasecaseRadioButton.isSelected()) {
			genAnalysis.setLoadBusType(SenAnalysisBusEnumType.LOAD_DISTRIBUTION);
		}
		else {
			genAnalysis.setLoadBusType(SenAnalysisBusEnumType.USER_FILE);
			genAnalysis.setUserFilename(this.genLoadDistUserFileTextField.getText());
		}

		return noError;
	}

	public boolean saveOutputConfig(Vector<String> errMsg) {
		boolean noError = true;

		if (this._ptXml.getAclfAnalysis().getOutputOption() == null) {
			this._ptXml.getAclfAnalysis().setOutputOption(odmObjFactory.createPtAclfOutputXmlType());
		}
		PtAclfOutputXmlType outOpt = this._ptXml.getAclfAnalysis().getOutputOption();
		
		outOpt.setLargeGSFPoints(
					new Integer(outLargeGSFPointsTextField.getText()).intValue());
		outOpt.setBusVoltageViolation(this.outVoltViolationCheckBox.isSelected());
		outOpt.setBusVoltageUpperLimitPU(
				new Double(this.voltUpperLimitTextField.getText()).doubleValue());
		outOpt.setBusVoltageLowerLimitPU(
				new Double(this.voltLowerLimitTextField.getText()).doubleValue());
		outOpt.setBranchLimitViolation(this.outBranchViolationCheckBox.isSelected());
		outOpt.setInterfaceLimitViolation(this.outInterfaceViolationCheckBox.isSelected());
		outOpt.setZoneSummary(this.outZoneSummaryCheckBox.isSelected());
		outOpt.setAreaSummary(this.outAreaSummaryCheckBox.isSelected());
		outOpt.setLfResult(this.outLfResultCheckBox.isSelected());
		outOpt.setLfResultFormat(this.outLfBusStyleRadioButton.isSelected()? 
				LfResultFormatEnumType.BUS_STYLE : LfResultFormatEnumType.SUMMARY);
		return noError;
	}

	/** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lfResultButtonGroup = new javax.swing.ButtonGroup();
        branchAnalysisTypeButtonGroup = new javax.swing.ButtonGroup();
        loadDistButtonGroup = new javax.swing.ButtonGroup();
        pTradingAnalysisTabbedPane = new javax.swing.JTabbedPane();
        caseDataPanel = new javax.swing.JPanel();
        edFilePanel = new javax.swing.JPanel();
        edFileLabel = new javax.swing.JLabel();
        edFileTextField = new javax.swing.JTextField();
        selectEdFileButton = new javax.swing.JButton();
        edGenPFacorTextField = new javax.swing.JTextField();
        edGenPFactorLabel = new javax.swing.JLabel();
        edDateLabel = new javax.swing.JLabel();
        edDateTextField = new javax.swing.JTextField();
        edLoadPFacorTextField = new javax.swing.JTextField();
        edLoadPFactorLabel = new javax.swing.JLabel();
        edLossPercentTextField = new javax.swing.JTextField();
        edLossPercentLabel = new javax.swing.JLabel();
        loadDistPanel = new javax.swing.JPanel();
        loadDistThreshholdLabel = new javax.swing.JLabel();
        loadDistThreshholdTextField = new javax.swing.JTextField();
        interfaceFilePanel = new javax.swing.JPanel();
        interfaceFileLabel = new javax.swing.JLabel();
        interfaceFileTextField = new javax.swing.JTextField();
        selectInterfaceFileButton = new javax.swing.JButton();
        interfaceLimitLabel = new javax.swing.JLabel();
        interfaceLimitTextField = new javax.swing.JTextField();
        selectInterfaceLimitButton = new javax.swing.JButton();
        lfAssistGenPanel = new javax.swing.JPanel();
        lfAssistGenFileLabel = new javax.swing.JLabel();
        lfAssistGenFileTextField = new javax.swing.JTextField();
        lfAssistGenFileSelectButton = new javax.swing.JButton();
        lfAssistGenQAdjStepsLabel = new javax.swing.JLabel();
        lfAssistGenQAdjStespTextField = new javax.swing.JTextField();
        lfAssistGenQAdjToleranceLabel = new javax.swing.JLabel();
        lfAssistGenQAdjToleranceTextField = new javax.swing.JTextField();
        aclfAnalysisPanel = new javax.swing.JPanel();
        aclfEdHourLabel = new javax.swing.JLabel();
        aclfEdHourComboBox = new javax.swing.JComboBox();
        aclfToleranceLabel = new javax.swing.JLabel();
        aclfToleranceTextField = new javax.swing.JTextField();
        aclfUseCachedVoltCheckBox = new javax.swing.JCheckBox();
        swingAllocCheckBox = new javax.swing.JCheckBox();
        swingAllocZoneLabel = new javax.swing.JLabel();
        swingAllocZoneComboBox = new javax.swing.JComboBox();
        swingAllocMaxStepsLabel = new javax.swing.JLabel();
        swingAllocMaxStepsTextField = new javax.swing.JTextField();
        swingAllocAccFactorLabel = new javax.swing.JLabel();
        swingAllocAccFactorTextField = new javax.swing.JTextField();
        swingAllocToleraceLabel = new javax.swing.JLabel();
        swingAllocToleranceTextField = new javax.swing.JTextField();
        runAclfAnalysisButton = new javax.swing.JButton();
        branchAnalysisPanel = new javax.swing.JPanel();
        braAnaEdHourLabel = new javax.swing.JLabel();
        braAnaEdHourComboBox = new javax.swing.JComboBox();
        braAnaBranchListComboBox = new javax.swing.JComboBox();
        braAnaBranchLabel = new javax.swing.JLabel();
        braAnaTypePanel = new javax.swing.JPanel();
        braAnaOutageSingleRadioButton = new javax.swing.JRadioButton();
        braAnaOutageMultiRadioButton = new javax.swing.JRadioButton();
        braAnaOutageScheduleRadioButton = new javax.swing.JRadioButton();
        braAnaFlowRadioButton = new javax.swing.JRadioButton();
        braAnaOutageAnalysisPanel = new javax.swing.JPanel();
        braAnaMultiOutageBranchScrollPane = new javax.swing.JScrollPane();
        braAnaMultiOutageBranchList = new javax.swing.JList();
        braAnaAddOutageBranchButton = new javax.swing.JButton();
        braAnaRemoveOutageBranchButton = new javax.swing.JButton();
        braAnaOutageFileLabel = new javax.swing.JLabel();
        braAnaOutageFileTextField = new javax.swing.JTextField();
        braAnaOutageFileSelectButton = new javax.swing.JButton();
        braAnaFlowOutPointsLabel = new javax.swing.JLabel();
        braAnaFlowOutPointsTextField = new javax.swing.JTextField();
        runBranchAnalysisButton = new javax.swing.JButton();
        genAnalysisPanel = new javax.swing.JPanel();
        genAnalysisGenBusLabel = new javax.swing.JLabel();
        genAnalysisGenBusListComboBox = new javax.swing.JComboBox();
        genAnalysisAddGenButton = new javax.swing.JButton();
        genAnalysisRemoveGenButton = new javax.swing.JButton();
        genAnalysisGenScrollPane = new javax.swing.JScrollPane();
        genAnalysisGenBusList = new javax.swing.JList();
        genLoadDisPanel = new javax.swing.JPanel();
        genLoadDistBusRadioButton = new javax.swing.JRadioButton();
        genLoadDistBasecaseRadioButton = new javax.swing.JRadioButton();
        genLoadDistUserRadioButton = new javax.swing.JRadioButton();
        genLoadDistLoadBusLabel = new javax.swing.JLabel();
        genLoadDistLoadBusComboBox = new javax.swing.JComboBox();
        genLoadDistUserFileTextField = new javax.swing.JTextField();
        genLoadDistUserFileButton = new javax.swing.JButton();
        runCalLossFactorsButton = new javax.swing.JButton();
        outputConfigPanel = new javax.swing.JPanel();
        outVoltViolationCheckBox = new javax.swing.JCheckBox();
        voltUpperLimitLabel = new javax.swing.JLabel();
        voltUpperLimitTextField = new javax.swing.JTextField();
        voltLowerLimitLabel = new javax.swing.JLabel();
        voltLowerLimitTextField = new javax.swing.JTextField();
        outLargeGSFPointsLabel = new javax.swing.JLabel();
        outLargeGSFPointsTextField = new javax.swing.JTextField();
        outBranchViolationCheckBox = new javax.swing.JCheckBox();
        outInterfaceViolationCheckBox = new javax.swing.JCheckBox();
        outZoneSummaryCheckBox = new javax.swing.JCheckBox();
        outAreaSummaryCheckBox = new javax.swing.JCheckBox();
        outLfResultCheckBox = new javax.swing.JCheckBox();
        outLfSummaryRadioButton = new javax.swing.JRadioButton();
        outLfBusStyleRadioButton = new javax.swing.JRadioButton();
        outLfResultFormatLabel = new javax.swing.JLabel();
        outToekn_Label = new javax.swing.JLabel();

        setFont(new java.awt.Font("Dialog", 0, 12));

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

        edFileTextField.setColumns(25);
        edFileTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        edFileTextField.setText("ED file");
        edFileTextField.setMaximumSize(new java.awt.Dimension(70, 20));

        selectEdFileButton.setFont(new java.awt.Font("Dialog", 0, 12));
        selectEdFileButton.setText("Select");
        selectEdFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectEdFileButtonActionPerformed(evt);
            }
        });

        edGenPFacorTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        edGenPFacorTextField.setText("0.90");

        edGenPFactorLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        edGenPFactorLabel.setText("Gen PFator (PU)");

        edDateLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        edDateLabel.setText("Date (mm/dd/yyyy)");

        edDateTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        edDateTextField.setText("mm/dd/yyyy");

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
                .add(7, 7, 7)
                .add(edFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(edFilePanelLayout.createSequentialGroup()
                        .add(edFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(edFileLabel)
                            .add(edGenPFactorLabel))
                        .add(14, 14, 14))
                    .add(edFilePanelLayout.createSequentialGroup()
                        .add(edLoadPFactorLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)))
                .add(edFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(edFilePanelLayout.createSequentialGroup()
                        .add(edFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(edLoadPFacorTextField)
                            .add(edGenPFacorTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE))
                        .add(35, 35, 35)
                        .add(edFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(edDateLabel)
                            .add(edLossPercentLabel))
                        .add(18, 18, 18)
                        .add(edFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(edLossPercentTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 42, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(edDateTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 84, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(edFilePanelLayout.createSequentialGroup()
                        .add(edFileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 242, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(selectEdFileButton)))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        edFilePanelLayout.setVerticalGroup(
            edFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(edFilePanelLayout.createSequentialGroup()
                .add(edFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(edFileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(selectEdFileButton)
                    .add(edFileLabel))
                .add(10, 10, 10)
                .add(edFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(edFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(edGenPFacorTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(edGenPFactorLabel))
                    .add(edFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(edDateLabel)
                        .add(edDateTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(edFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(edFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(edLoadPFacorTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(edLoadPFactorLabel))
                    .add(edLossPercentLabel)
                    .add(edLossPercentTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        loadDistPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Load Distribution", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N

        loadDistThreshholdLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        loadDistThreshholdLabel.setText("Threshhold (Mw)   ");

        loadDistThreshholdTextField.setColumns(3);
        loadDistThreshholdTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        loadDistThreshholdTextField.setText("5.0");

        org.jdesktop.layout.GroupLayout loadDistPanelLayout = new org.jdesktop.layout.GroupLayout(loadDistPanel);
        loadDistPanel.setLayout(loadDistPanelLayout);
        loadDistPanelLayout.setHorizontalGroup(
            loadDistPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(loadDistPanelLayout.createSequentialGroup()
                .add(19, 19, 19)
                .add(loadDistThreshholdLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(loadDistThreshholdTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 51, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(281, Short.MAX_VALUE))
        );
        loadDistPanelLayout.setVerticalGroup(
            loadDistPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(loadDistPanelLayout.createSequentialGroup()
                .add(loadDistPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(loadDistThreshholdLabel)
                    .add(loadDistThreshholdTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        interfaceFilePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Interface Defintion", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N

        interfaceFileLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        interfaceFileLabel.setText("Interface File");

        interfaceFileTextField.setColumns(25);
        interfaceFileTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        interfaceFileTextField.setText("Interface File");
        interfaceFileTextField.setMaximumSize(new java.awt.Dimension(70, 20));

        selectInterfaceFileButton.setFont(new java.awt.Font("Dialog", 0, 12));
        selectInterfaceFileButton.setText("Select");
        selectInterfaceFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectInterfaceFileButtonActionPerformed(evt);
            }
        });

        interfaceLimitLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        interfaceLimitLabel.setText("Limit File");

        interfaceLimitTextField.setColumns(25);
        interfaceLimitTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        interfaceLimitTextField.setText("Interface Limit file");
        interfaceLimitTextField.setMaximumSize(new java.awt.Dimension(70, 20));

        selectInterfaceLimitButton.setFont(new java.awt.Font("Dialog", 0, 12));
        selectInterfaceLimitButton.setText("Select");
        selectInterfaceLimitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectInterfaceLimitButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout interfaceFilePanelLayout = new org.jdesktop.layout.GroupLayout(interfaceFilePanel);
        interfaceFilePanel.setLayout(interfaceFilePanelLayout);
        interfaceFilePanelLayout.setHorizontalGroup(
            interfaceFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, interfaceFilePanelLayout.createSequentialGroup()
                .addContainerGap(23, Short.MAX_VALUE)
                .add(interfaceFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(interfaceFileLabel)
                    .add(interfaceLimitLabel))
                .add(18, 18, 18)
                .add(interfaceFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(interfaceFileTextField, 0, 0, Short.MAX_VALUE)
                    .add(interfaceLimitTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 256, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(interfaceFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(selectInterfaceFileButton)
                    .add(selectInterfaceLimitButton))
                .addContainerGap())
        );
        interfaceFilePanelLayout.setVerticalGroup(
            interfaceFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(interfaceFilePanelLayout.createSequentialGroup()
                .add(interfaceFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(interfaceFileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(interfaceFileLabel)
                    .add(selectInterfaceFileButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(interfaceFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(interfaceLimitTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(interfaceLimitLabel)
                    .add(selectInterfaceLimitButton))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lfAssistGenPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Lf Assistance Generator", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N

        lfAssistGenFileLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        lfAssistGenFileLabel.setText("LF Assist File");

        lfAssistGenFileTextField.setColumns(25);
        lfAssistGenFileTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        lfAssistGenFileTextField.setText("LF Assistance Gen File");
        lfAssistGenFileTextField.setMaximumSize(new java.awt.Dimension(70, 20));

        lfAssistGenFileSelectButton.setFont(new java.awt.Font("Dialog", 0, 12));
        lfAssistGenFileSelectButton.setText("Select");
        lfAssistGenFileSelectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lfAssistGenFileSelectButtonActionPerformed(evt);
            }
        });

        lfAssistGenQAdjStepsLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        lfAssistGenQAdjStepsLabel.setText("Q Adj Run Steps");

        lfAssistGenQAdjStespTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        lfAssistGenQAdjStespTextField.setText("3");

        lfAssistGenQAdjToleranceLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        lfAssistGenQAdjToleranceLabel.setText("Q Adj Tolerance");

        lfAssistGenQAdjToleranceTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        lfAssistGenQAdjToleranceTextField.setText("0.1");

        org.jdesktop.layout.GroupLayout lfAssistGenPanelLayout = new org.jdesktop.layout.GroupLayout(lfAssistGenPanel);
        lfAssistGenPanel.setLayout(lfAssistGenPanelLayout);
        lfAssistGenPanelLayout.setHorizontalGroup(
            lfAssistGenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lfAssistGenPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(lfAssistGenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lfAssistGenFileLabel)
                    .add(lfAssistGenQAdjStepsLabel))
                .add(lfAssistGenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lfAssistGenPanelLayout.createSequentialGroup()
                        .add(33, 33, 33)
                        .add(lfAssistGenQAdjStespTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 33, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(59, 59, 59)
                        .add(lfAssistGenQAdjToleranceLabel)
                        .add(33, 33, 33)
                        .add(lfAssistGenQAdjToleranceTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 33, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(lfAssistGenPanelLayout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(lfAssistGenFileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 257, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(lfAssistGenFileSelectButton)))
                .addContainerGap(11, Short.MAX_VALUE))
        );
        lfAssistGenPanelLayout.setVerticalGroup(
            lfAssistGenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lfAssistGenPanelLayout.createSequentialGroup()
                .add(lfAssistGenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lfAssistGenFileLabel)
                    .add(lfAssistGenFileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lfAssistGenFileSelectButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lfAssistGenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lfAssistGenQAdjToleranceLabel)
                    .add(lfAssistGenQAdjToleranceTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lfAssistGenQAdjStespTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lfAssistGenQAdjStepsLabel))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout caseDataPanelLayout = new org.jdesktop.layout.GroupLayout(caseDataPanel);
        caseDataPanel.setLayout(caseDataPanelLayout);
        caseDataPanelLayout.setHorizontalGroup(
            caseDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(caseDataPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(caseDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(caseDataPanelLayout.createSequentialGroup()
                        .add(lfAssistGenPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(11, 11, 11))
                    .add(caseDataPanelLayout.createSequentialGroup()
                        .add(caseDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, interfaceFilePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, loadDistPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, edFilePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        caseDataPanelLayout.setVerticalGroup(
            caseDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(caseDataPanelLayout.createSequentialGroup()
                .add(edFilePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 114, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(1, 1, 1)
                .add(loadDistPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(4, 4, 4)
                .add(interfaceFilePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lfAssistGenPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(33, 33, 33))
        );

        pTradingAnalysisTabbedPane.addTab("Case Data", caseDataPanel);

        aclfEdHourLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        aclfEdHourLabel.setText("ED Hour");

        aclfEdHourComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        aclfEdHourComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0:00", "1:00", "2:00", "3:00", "4:00", "5:00", "6:00", "7:00", "8:00", "9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00", " " }));

        aclfToleranceLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        aclfToleranceLabel.setText("Lf Tolerance");

        aclfToleranceTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        aclfToleranceTextField.setText("0.0001");

        aclfUseCachedVoltCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        aclfUseCachedVoltCheckBox.setText("Use Previous Run PV Voltage");

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
        swingAllocAccFactorTextField.setText("1.5");

        swingAllocToleraceLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        swingAllocToleraceLabel.setText("Alloc Tolerance");

        swingAllocToleranceTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        swingAllocToleranceTextField.setText("0.2");

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
            .add(aclfAnalysisPanelLayout.createSequentialGroup()
                .add(aclfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(aclfAnalysisPanelLayout.createSequentialGroup()
                        .add(26, 26, 26)
                        .add(aclfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(swingAllocCheckBox)
                            .add(aclfAnalysisPanelLayout.createSequentialGroup()
                                .add(32, 32, 32)
                                .add(aclfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(swingAllocZoneLabel)
                                    .add(swingAllocMaxStepsLabel)
                                    .add(swingAllocToleraceLabel))
                                .add(29, 29, 29)
                                .add(aclfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(swingAllocZoneComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 71, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(aclfAnalysisPanelLayout.createSequentialGroup()
                                        .add(aclfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                            .add(swingAllocMaxStepsTextField)
                                            .add(swingAllocToleranceTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE))
                                        .add(33, 33, 33)
                                        .add(swingAllocAccFactorLabel)
                                        .add(18, 18, 18)
                                        .add(swingAllocAccFactorTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))))
                    .add(aclfAnalysisPanelLayout.createSequentialGroup()
                        .add(184, 184, 184)
                        .add(runAclfAnalysisButton))
                    .add(aclfAnalysisPanelLayout.createSequentialGroup()
                        .add(aclfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(aclfAnalysisPanelLayout.createSequentialGroup()
                                .add(154, 154, 154)
                                .add(aclfEdHourLabel))
                            .add(aclfAnalysisPanelLayout.createSequentialGroup()
                                .add(58, 58, 58)
                                .add(aclfToleranceLabel)
                                .add(29, 29, 29)
                                .add(aclfToleranceTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 59, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .add(26, 26, 26)
                        .add(aclfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(aclfUseCachedVoltCheckBox)
                            .add(aclfEdHourComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 77, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(69, Short.MAX_VALUE))
        );
        aclfAnalysisPanelLayout.setVerticalGroup(
            aclfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, aclfAnalysisPanelLayout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .add(aclfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(aclfEdHourComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(aclfEdHourLabel))
                .add(18, 18, 18)
                .add(aclfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(aclfToleranceTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(aclfToleranceLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(aclfUseCachedVoltCheckBox))
                .add(18, 18, 18)
                .add(swingAllocCheckBox)
                .add(aclfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(aclfAnalysisPanelLayout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(swingAllocZoneComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(aclfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(swingAllocMaxStepsTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(swingAllocMaxStepsLabel)
                            .add(swingAllocAccFactorLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(swingAllocAccFactorTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(aclfAnalysisPanelLayout.createSequentialGroup()
                        .add(13, 13, 13)
                        .add(swingAllocZoneLabel)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(aclfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(aclfAnalysisPanelLayout.createSequentialGroup()
                        .add(149, 149, 149)
                        .add(runAclfAnalysisButton))
                    .add(aclfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(swingAllocToleranceTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(swingAllocToleraceLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(23, 23, 23))
        );

        pTradingAnalysisTabbedPane.addTab("Aclf Analysis", aclfAnalysisPanel);

        braAnaEdHourLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        braAnaEdHourLabel.setText("ED Hour");

        braAnaEdHourComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        braAnaEdHourComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0:00", "1:00", "2:00", "3:00", "4:00", "5:00", "6:00", "7:00", "8:00", "9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00", " " }));

        braAnaBranchListComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        braAnaBranchListComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        braAnaBranchLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        braAnaBranchLabel.setText("Branch");

        braAnaTypePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Analysis Type", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N

        branchAnalysisTypeButtonGroup.add(braAnaOutageSingleRadioButton);
        braAnaOutageSingleRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        braAnaOutageSingleRadioButton.setSelected(true);
        braAnaOutageSingleRadioButton.setText("Single Outage");
        braAnaOutageSingleRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                braAnaOutageSingleRadioButtonActionPerformed(evt);
            }
        });

        branchAnalysisTypeButtonGroup.add(braAnaOutageMultiRadioButton);
        braAnaOutageMultiRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        braAnaOutageMultiRadioButton.setText("Multi Outage");
        braAnaOutageMultiRadioButton.setEnabled(false);
        braAnaOutageMultiRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                braAnaOutageMultiRadioButtonActionPerformed(evt);
            }
        });

        branchAnalysisTypeButtonGroup.add(braAnaOutageScheduleRadioButton);
        braAnaOutageScheduleRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        braAnaOutageScheduleRadioButton.setText("Outage Schedule");
        braAnaOutageScheduleRadioButton.setEnabled(false);
        braAnaOutageScheduleRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                braAnaOutageScheduleRadioButtonActionPerformed(evt);
            }
        });

        branchAnalysisTypeButtonGroup.add(braAnaFlowRadioButton);
        braAnaFlowRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        braAnaFlowRadioButton.setText("Branch Flow");
        braAnaFlowRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                braAnaFlowRadioButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout braAnaTypePanelLayout = new org.jdesktop.layout.GroupLayout(braAnaTypePanel);
        braAnaTypePanel.setLayout(braAnaTypePanelLayout);
        braAnaTypePanelLayout.setHorizontalGroup(
            braAnaTypePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(braAnaTypePanelLayout.createSequentialGroup()
                .add(15, 15, 15)
                .add(braAnaOutageSingleRadioButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(braAnaOutageMultiRadioButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(braAnaOutageScheduleRadioButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(braAnaFlowRadioButton)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        braAnaTypePanelLayout.setVerticalGroup(
            braAnaTypePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(braAnaTypePanelLayout.createSequentialGroup()
                .add(braAnaTypePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(braAnaOutageSingleRadioButton)
                    .add(braAnaOutageMultiRadioButton)
                    .add(braAnaOutageScheduleRadioButton)
                    .add(braAnaFlowRadioButton))
                .addContainerGap(7, Short.MAX_VALUE))
        );

        braAnaOutageAnalysisPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        braAnaMultiOutageBranchList.setFont(new java.awt.Font("Dialog", 0, 12));
        braAnaMultiOutageBranchList.setEnabled(false);
        braAnaMultiOutageBranchScrollPane.setViewportView(braAnaMultiOutageBranchList);

        braAnaAddOutageBranchButton.setFont(new java.awt.Font("Dialog", 0, 12));
        braAnaAddOutageBranchButton.setText("Add");
        braAnaAddOutageBranchButton.setEnabled(false);
        braAnaAddOutageBranchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                braAnaAddOutageBranchButtonActionPerformed(evt);
            }
        });

        braAnaRemoveOutageBranchButton.setFont(new java.awt.Font("Dialog", 0, 12));
        braAnaRemoveOutageBranchButton.setText("Remove");
        braAnaRemoveOutageBranchButton.setEnabled(false);
        braAnaRemoveOutageBranchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                braAnaRemoveOutageBranchButtonActionPerformed(evt);
            }
        });

        braAnaOutageFileLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        braAnaOutageFileLabel.setText("Outage File");
        braAnaOutageFileLabel.setEnabled(false);

        braAnaOutageFileTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        braAnaOutageFileTextField.setText("outage file");
        braAnaOutageFileTextField.setEnabled(false);

        braAnaOutageFileSelectButton.setFont(new java.awt.Font("Dialog", 0, 10));
        braAnaOutageFileSelectButton.setText("Select ...");
        braAnaOutageFileSelectButton.setEnabled(false);
        braAnaOutageFileSelectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                braAnaOutageFileSelectButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout braAnaOutageAnalysisPanelLayout = new org.jdesktop.layout.GroupLayout(braAnaOutageAnalysisPanel);
        braAnaOutageAnalysisPanel.setLayout(braAnaOutageAnalysisPanelLayout);
        braAnaOutageAnalysisPanelLayout.setHorizontalGroup(
            braAnaOutageAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(braAnaOutageAnalysisPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(braAnaOutageAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(braAnaOutageAnalysisPanelLayout.createSequentialGroup()
                        .add(braAnaMultiOutageBranchScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 249, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(braAnaOutageAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(braAnaRemoveOutageBranchButton)
                            .add(braAnaAddOutageBranchButton)))
                    .add(braAnaOutageAnalysisPanelLayout.createSequentialGroup()
                        .add(braAnaOutageFileLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(braAnaOutageFileTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(braAnaOutageFileSelectButton)))
                .addContainerGap())
        );
        braAnaOutageAnalysisPanelLayout.setVerticalGroup(
            braAnaOutageAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(braAnaOutageAnalysisPanelLayout.createSequentialGroup()
                .add(braAnaOutageAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(braAnaMultiOutageBranchScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 82, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(braAnaOutageAnalysisPanelLayout.createSequentialGroup()
                        .add(braAnaAddOutageBranchButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(braAnaRemoveOutageBranchButton)))
                .add(18, 18, 18)
                .add(braAnaOutageAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(braAnaOutageFileLabel)
                    .add(braAnaOutageFileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(braAnaOutageFileSelectButton)))
        );

        braAnaFlowOutPointsLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        braAnaFlowOutPointsLabel.setText("BranchFlow Output Points");
        braAnaFlowOutPointsLabel.setEnabled(false);

        braAnaFlowOutPointsTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        braAnaFlowOutPointsTextField.setText("20");
        braAnaFlowOutPointsTextField.setEnabled(false);

        runBranchAnalysisButton.setFont(new java.awt.Font("Dialog", 0, 12));
        runBranchAnalysisButton.setText("Run Analysis");
        runBranchAnalysisButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runBranchAnalysisButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout branchAnalysisPanelLayout = new org.jdesktop.layout.GroupLayout(branchAnalysisPanel);
        branchAnalysisPanel.setLayout(branchAnalysisPanelLayout);
        branchAnalysisPanelLayout.setHorizontalGroup(
            branchAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(branchAnalysisPanelLayout.createSequentialGroup()
                .add(branchAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(branchAnalysisPanelLayout.createSequentialGroup()
                        .add(167, 167, 167)
                        .add(braAnaEdHourLabel)
                        .add(26, 26, 26)
                        .add(braAnaEdHourComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 77, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(branchAnalysisPanelLayout.createSequentialGroup()
                        .add(102, 102, 102)
                        .add(braAnaBranchLabel)
                        .add(33, 33, 33)
                        .add(braAnaBranchListComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 184, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(138, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, branchAnalysisPanelLayout.createSequentialGroup()
                .addContainerGap(205, Short.MAX_VALUE)
                .add(runBranchAnalysisButton)
                .add(186, 186, 186))
            .add(branchAnalysisPanelLayout.createSequentialGroup()
                .add(23, 23, 23)
                .add(branchAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(branchAnalysisPanelLayout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(braAnaOutageAnalysisPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(braAnaTypePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(26, Short.MAX_VALUE))
            .add(branchAnalysisPanelLayout.createSequentialGroup()
                .add(49, 49, 49)
                .add(braAnaFlowOutPointsLabel)
                .add(18, 18, 18)
                .add(braAnaFlowOutPointsTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(251, Short.MAX_VALUE))
        );
        branchAnalysisPanelLayout.setVerticalGroup(
            branchAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, branchAnalysisPanelLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(branchAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(braAnaEdHourComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(braAnaEdHourLabel))
                .add(18, 18, 18)
                .add(branchAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(braAnaBranchListComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(braAnaBranchLabel))
                .add(4, 4, 4)
                .add(braAnaTypePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(braAnaOutageAnalysisPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(branchAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(braAnaFlowOutPointsLabel)
                    .add(braAnaFlowOutPointsTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(40, 40, 40)
                .add(runBranchAnalysisButton)
                .add(139, 139, 139))
        );

        pTradingAnalysisTabbedPane.addTab("Branch Analysis", branchAnalysisPanel);

        genAnalysisGenBusLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        genAnalysisGenBusLabel.setText("Gen Bus");

        genAnalysisGenBusListComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        genAnalysisGenBusListComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        genAnalysisAddGenButton.setFont(new java.awt.Font("Dialog", 0, 10));
        genAnalysisAddGenButton.setText("Add");
        genAnalysisAddGenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genAnalysisAddGenButtonActionPerformed(evt);
            }
        });

        genAnalysisRemoveGenButton.setFont(new java.awt.Font("Dialog", 0, 10));
        genAnalysisRemoveGenButton.setText("Remove");
        genAnalysisRemoveGenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genAnalysisRemoveGenButtonActionPerformed(evt);
            }
        });

        genAnalysisGenBusList.setFont(new java.awt.Font("Dialog", 0, 12));
        genAnalysisGenScrollPane.setViewportView(genAnalysisGenBusList);

        genLoadDisPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Load Distribution Factor", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N

        loadDistButtonGroup.add(genLoadDistBusRadioButton);
        genLoadDistBusRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        genLoadDistBusRadioButton.setText("Load Bus");
        genLoadDistBusRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genLoadDistBusRadioButtonActionPerformed(evt);
            }
        });

        loadDistButtonGroup.add(genLoadDistBasecaseRadioButton);
        genLoadDistBasecaseRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        genLoadDistBasecaseRadioButton.setSelected(true);
        genLoadDistBasecaseRadioButton.setText("Basecase");
        genLoadDistBasecaseRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genLoadDistBasecaseRadioButtonActionPerformed(evt);
            }
        });

        loadDistButtonGroup.add(genLoadDistUserRadioButton);
        genLoadDistUserRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        genLoadDistUserRadioButton.setText("User");
        genLoadDistUserRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genLoadDistUserRadioButtonActionPerformed(evt);
            }
        });

        genLoadDistLoadBusLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        genLoadDistLoadBusLabel.setText("Load Bus");
        genLoadDistLoadBusLabel.setEnabled(false);

        genLoadDistLoadBusComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        genLoadDistLoadBusComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        genLoadDistLoadBusComboBox.setEnabled(false);

        genLoadDistUserFileTextField.setColumns(25);
        genLoadDistUserFileTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        genLoadDistUserFileTextField.setText("User defined file ...");
        genLoadDistUserFileTextField.setEnabled(false);

        genLoadDistUserFileButton.setFont(new java.awt.Font("Dialog", 0, 10));
        genLoadDistUserFileButton.setText("Select");
        genLoadDistUserFileButton.setEnabled(false);
        genLoadDistUserFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genLoadDistUserFileButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout genLoadDisPanelLayout = new org.jdesktop.layout.GroupLayout(genLoadDisPanel);
        genLoadDisPanel.setLayout(genLoadDisPanelLayout);
        genLoadDisPanelLayout.setHorizontalGroup(
            genLoadDisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(genLoadDisPanelLayout.createSequentialGroup()
                .add(genLoadDisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, genLoadDisPanelLayout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(genLoadDistUserFileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 248, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 28, Short.MAX_VALUE)
                        .add(genLoadDistUserFileButton))
                    .add(genLoadDisPanelLayout.createSequentialGroup()
                        .add(42, 42, 42)
                        .add(genLoadDisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, genLoadDistBusRadioButton)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, genLoadDistLoadBusLabel))
                        .add(21, 21, 21)
                        .add(genLoadDistLoadBusComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 105, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, genLoadDisPanelLayout.createSequentialGroup()
                .addContainerGap(137, Short.MAX_VALUE)
                .add(genLoadDistBasecaseRadioButton)
                .add(16, 16, 16)
                .add(genLoadDistUserRadioButton)
                .add(82, 82, 82))
        );
        genLoadDisPanelLayout.setVerticalGroup(
            genLoadDisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(genLoadDisPanelLayout.createSequentialGroup()
                .add(genLoadDisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(genLoadDistUserRadioButton)
                    .add(genLoadDistBasecaseRadioButton)
                    .add(genLoadDistBusRadioButton))
                .add(14, 14, 14)
                .add(genLoadDisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(genLoadDistLoadBusComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(genLoadDistLoadBusLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(genLoadDisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(genLoadDistUserFileButton)
                    .add(genLoadDistUserFileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        runCalLossFactorsButton.setFont(new java.awt.Font("Dialog", 0, 12));
        runCalLossFactorsButton.setText("Cal LossFactor");
        runCalLossFactorsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runCalLossFactorsButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout genAnalysisPanelLayout = new org.jdesktop.layout.GroupLayout(genAnalysisPanel);
        genAnalysisPanel.setLayout(genAnalysisPanelLayout);
        genAnalysisPanelLayout.setHorizontalGroup(
            genAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(genAnalysisPanelLayout.createSequentialGroup()
                .addContainerGap(60, Short.MAX_VALUE)
                .add(genAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, genAnalysisPanelLayout.createSequentialGroup()
                        .add(genAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(genAnalysisPanelLayout.createSequentialGroup()
                                .add(70, 70, 70)
                                .add(genAnalysisGenScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 148, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(genAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(genAnalysisRemoveGenButton)
                                    .add(genAnalysisAddGenButton))
                                .add(82, 82, 82))
                            .add(genLoadDisPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(genAnalysisPanelLayout.createSequentialGroup()
                                .add(91, 91, 91)
                                .add(genAnalysisGenBusLabel)
                                .add(18, 18, 18)
                                .add(genAnalysisGenBusListComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 114, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .add(55, 55, 55))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, genAnalysisPanelLayout.createSequentialGroup()
                        .add(runCalLossFactorsButton)
                        .add(179, 179, 179))))
        );
        genAnalysisPanelLayout.setVerticalGroup(
            genAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(genAnalysisPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(genAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(genAnalysisGenBusListComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(genAnalysisGenBusLabel))
                .add(18, 18, 18)
                .add(genAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(genAnalysisPanelLayout.createSequentialGroup()
                        .add(genAnalysisAddGenButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(genAnalysisRemoveGenButton))
                    .add(genAnalysisGenScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 65, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(genLoadDisPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 67, Short.MAX_VALUE)
                .add(runCalLossFactorsButton)
                .add(38, 38, 38))
        );

        pTradingAnalysisTabbedPane.addTab("Gen Analysis", genAnalysisPanel);

        outVoltViolationCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        outVoltViolationCheckBox.setSelected(true);
        outVoltViolationCheckBox.setText("Bus Voltage Violation Report");

        voltUpperLimitLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        voltUpperLimitLabel.setText("Upper Limit");

        voltUpperLimitTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        voltUpperLimitTextField.setText("1.15");

        voltLowerLimitLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        voltLowerLimitLabel.setText("Lower Limit");

        voltLowerLimitTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        voltLowerLimitTextField.setText("0.85");

        outLargeGSFPointsLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        outLargeGSFPointsLabel.setText("Large GSF Points");

        outLargeGSFPointsTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        outLargeGSFPointsTextField.setText("5");

        outBranchViolationCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        outBranchViolationCheckBox.setSelected(true);
        outBranchViolationCheckBox.setText("Branch Limit Violation Report");

        outInterfaceViolationCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        outInterfaceViolationCheckBox.setSelected(true);
        outInterfaceViolationCheckBox.setText("Interface Limit Violation Report");

        outZoneSummaryCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        outZoneSummaryCheckBox.setSelected(true);
        outZoneSummaryCheckBox.setText("Zone Summary");

        outAreaSummaryCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        outAreaSummaryCheckBox.setSelected(true);
        outAreaSummaryCheckBox.setText("Area Summary");

        outLfResultCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        outLfResultCheckBox.setText("Loadflow Result");

        lfResultButtonGroup.add(outLfSummaryRadioButton);
        outLfSummaryRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        outLfSummaryRadioButton.setSelected(true);
        outLfSummaryRadioButton.setText("Summary");

        lfResultButtonGroup.add(outLfBusStyleRadioButton);
        outLfBusStyleRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        outLfBusStyleRadioButton.setText("BusStyle");

        outLfResultFormatLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        outLfResultFormatLabel.setText("LF Rsult Format   [");

        outToekn_Label.setFont(new java.awt.Font("Dialog", 0, 12));
        outToekn_Label.setText("]");

        org.jdesktop.layout.GroupLayout outputConfigPanelLayout = new org.jdesktop.layout.GroupLayout(outputConfigPanel);
        outputConfigPanel.setLayout(outputConfigPanelLayout);
        outputConfigPanelLayout.setHorizontalGroup(
            outputConfigPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(outputConfigPanelLayout.createSequentialGroup()
                .add(34, 34, 34)
                .add(outputConfigPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(outZoneSummaryCheckBox)
                    .add(outAreaSummaryCheckBox)
                    .add(outLfResultCheckBox)
                    .add(outputConfigPanelLayout.createSequentialGroup()
                        .add(44, 44, 44)
                        .add(outLfResultFormatLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(outLfSummaryRadioButton)
                        .add(18, 18, 18)
                        .add(outLfBusStyleRadioButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(outToekn_Label))
                    .add(outInterfaceViolationCheckBox)
                    .add(outputConfigPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(outputConfigPanelLayout.createSequentialGroup()
                            .add(voltUpperLimitLabel)
                            .add(18, 18, 18)
                            .add(voltUpperLimitTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(outVoltViolationCheckBox))
                    .add(outputConfigPanelLayout.createSequentialGroup()
                        .add(outBranchViolationCheckBox)
                        .add(33, 33, 33)
                        .add(outputConfigPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(outputConfigPanelLayout.createSequentialGroup()
                                .add(voltLowerLimitLabel)
                                .add(18, 18, 18)
                                .add(voltLowerLimitTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(outputConfigPanelLayout.createSequentialGroup()
                                .add(10, 10, 10)
                                .add(outLargeGSFPointsLabel)
                                .add(18, 18, 18)
                                .add(outLargeGSFPointsTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(83, Short.MAX_VALUE))
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
                .add(outputConfigPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(outBranchViolationCheckBox)
                    .add(outLargeGSFPointsLabel)
                    .add(outLargeGSFPointsTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(outInterfaceViolationCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(outZoneSummaryCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(outAreaSummaryCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(outLfResultCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(outputConfigPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(outLfResultFormatLabel)
                    .add(outLfSummaryRadioButton)
                    .add(outToekn_Label)
                    .add(outLfBusStyleRadioButton))
                .addContainerGap(154, Short.MAX_VALUE))
        );

        pTradingAnalysisTabbedPane.addTab("Output Config", outputConfigPanel);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(pTradingAnalysisTabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(pTradingAnalysisTabbedPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 428, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
 
    private void panelSelectionChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_panelSelectionChanged
    	if ( pTradingAnalysisTabbedPane.getSelectedIndex() == 0 )
        	ipssLogger.info("Panel selection changed - Case Data Panel");
    	else if ( pTradingAnalysisTabbedPane.getSelectedIndex() == 1 ) {
        	ipssLogger.info("Panel selection changed - Aclf Analysis Panel");
    	}	
    	else if ( pTradingAnalysisTabbedPane.getSelectedIndex() == 2 ) {
        	ipssLogger.info("Panel selection changed - Line Outage Analysis Panel");
    	}	
    	else if ( pTradingAnalysisTabbedPane.getSelectedIndex() == 3 ) {
        	ipssLogger.info("Panel selection changed - Output Config Panel");
    	}	
    }//GEN-LAST:event_panelSelectionChanged

private boolean saveInputData() {
	Vector<String> errMsg = new Vector<String>();
	try {
    	if (!saveEditor2Form(errMsg, true)) {
    		PluginSpringFactory.getEditorDialogUtil().showMsgDialog(this.parent, "Input Data Error", errMsg);
			return false;
    	}
    	
    	if (!this.aclfUseCachedVoltCheckBox.isSelected() ) {
    		this.genPVSwingBusVoltCacheList.clear();
    	}
    } catch (Exception e) {
    	IpssLogger.logErr(e);
    	PluginSpringFactory.getEditorDialogUtil().showMsgDialog(this.parent, "Input Data Error", e.toString());
		return false;
    }
    return true;
}

//TODO
/* 888888888888888888888888
 *  Case data
 8888888888888888888888888888*/

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

private void lfAssistGenFileSelectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lfAssistGenFileSelectButtonActionPerformed
	JFileChooser fc = getExcelFileChooser();
	if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
		File file = fc.getSelectedFile();
		lfAssistGenFileTextField.setText(file.getAbsolutePath());
	}
}//GEN-LAST:event_lfAssistGenFileSelectButtonActionPerformed

// TODO
/*88888888888888888888888888888888888
 *  Aclf Analysis
 88888888888888888888888888888888888*/

private void runAclfAnalysisButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runAclfAnalysisButtonActionPerformed
	ipssLogger.info("Aclf analysis button selected");
	
	this.parent.setAlwaysOnTop(false);

	if (!saveInputData())
		return;

	// running the analysis in a separate thread, so that it won't block
	// the main UI window
	final JDialog parent = this.parent;
	final AclfNetwork net = this._simuCtx.getAclfNet();
	final PTradingEDHourlyAnalysisXmlType ptXml = this._ptXml;
	final PowerTradingInfoXmlType ptInfoXml = this._ptInfoXml;
	final List<DblBusValue> genPVSwingBusList = this.genPVSwingBusVoltCacheList;
    final javax.swing.JCheckBox _useCachedVoltCheckBox = this.aclfUseCachedVoltCheckBox;
    
	new Thread() {
		public void run() {
			IAppStatus appStatus = GraphSpringFactory.getIpssGraphicEditor().getAppStatus();
			appStatus.busyStart(Constants.StatusBusyIndicatorPeriod,
					"Run PowerTrading Aclf Analysis ...", "Run PTraing");
			// Book marked the AclfNetwork object
			ChangeRecorder recorderBaseNet = new ChangeRecorder(net);	
			try {
				PTradingDslODMRunner runner = new PTradingDslODMRunner(net);
				runner.runPtAclfAnalysis(ptXml, ptInfoXml, genPVSwingBusList);
				UISpringFactory.getOutputTextDialog("BaseCase Aclf Analysis Results")
					.display(PTradingOutput.outHourLoaflowResult(net, ptXml, 
							runner.getHrLoadflow().getLfAssistGenList()));
			} catch (InterpssException e) {
				PluginSpringFactory.getEditorDialogUtil().showMsgDialog(parent, "Analysis Error", e.toString());
				recorderBaseNet.endRecording().apply();	
				return;
			}
			// cache PV/Swing bus voltage
			genPVSwingBusList.clear();
			for (Bus b : net.getBusList()) {
				AclfBus bus = (AclfBus)b;
				if (b.isActive() && ( bus.isGenPV() || bus.isSwing()))  {
						genPVSwingBusVoltCacheList.add(new DblBusValue(bus.getId(), bus.getVoltageMag()));
				}
			}
			_useCachedVoltCheckBox.setEnabled(true);
			_useCachedVoltCheckBox.setSelected(true);
			// roll-back AclfNet to the bookmarked point
			recorderBaseNet.endRecording().apply();	
			appStatus.busyStop("Run PowerTrading Aclf Analysis finished");			
		}
	}.start();
}//GEN-LAST:event_runAclfAnalysisButtonActionPerformed

//TODO
/* 888888888888888888888888
 *  Branch Analysis
 8888888888888888888888888888*/

private void runBranchAnalysisButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runBranchAnalysisButtonActionPerformed
	ipssLogger.info("Line outage analysis button selected");
	
	this.parent.setAlwaysOnTop(false);

	if (!saveInputData())
		return;
	
	String outText = "";
	
	final AclfNetwork net = this._simuCtx.getAclfNet();
	final PTradingEDHourlyAnalysisXmlType ptXml = this._ptXml;
	final PowerTradingInfoXmlType ptInfoXml = this._ptInfoXml;

	// Book marked the AclfNetwork object
	ChangeRecorder recorderBaseNet = new ChangeRecorder(net);	

	try {
		if (this.braAnaFlowRadioButton.isSelected()) {
			Object rtn = new PTradingDslODMRunner(net)
								.runPTradingAnalysis(ptXml, ptInfoXml, PtAnalysisType.Branch);
			String braId = ptXml.getBranchAnalysis().getBranch().get(0).getBranchId();
			outText = DclfGSFBranchFlow.f(net, braId, (List<DblBusValue>)rtn).toString();
		}
		else if (this.braAnaOutageSingleRadioButton.isSelected()) {
			Object rtn = new PTradingDslODMRunner(net)
				.runPTradingAnalysis(ptXml, ptInfoXml, PtAnalysisType.Branch);
			String braId = ptXml.getBranchAnalysis().getBranch().get(0).getBranchId();
			outText = "Outage Branch:" + braId+ "\n\n" + rtn.toString();
		}
		else {
			// not implemented
		}
	} catch (Exception e) {
		PluginSpringFactory.getEditorDialogUtil().showMsgDialog(parent, "Analysis Error", e.toString());
		recorderBaseNet.endRecording().apply();
		return;
	}
	
	// roll-back AclfNet to the bookmarked point
	recorderBaseNet.endRecording().apply();		
	
	UISpringFactory.getOutputTextDialog("Branch Analysis Results")
		.display(outText);   	
   	
}//GEN-LAST:event_runBranchAnalysisButtonActionPerformed

private void braAnaOutageSingleRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_braAnaOutageSingleRadioButtonActionPerformed
	ipssLogger.info("outageSingleRadioButtonActionPerformed() called");
	disableOutageAnalysisCompoment(false, false, false);
}//GEN-LAST:event_braAnaOutageSingleRadioButtonActionPerformed

private void braAnaOutageMultiRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_braAnaOutageMultiRadioButtonActionPerformed
	ipssLogger.info("outageMultiRadioButtonActionPerformed() called");
	disableOutageAnalysisCompoment(true, false, false);
}//GEN-LAST:event_braAnaOutageMultiRadioButtonActionPerformed

private void braAnaOutageScheduleRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_braAnaOutageScheduleRadioButtonActionPerformed
	ipssLogger.info("outageScheduleRadioButtonActionPerformed() called");
	disableOutageAnalysisCompoment(false, true, false);
}//GEN-LAST:event_braAnaOutageScheduleRadioButtonActionPerformed

private void braAnaFlowRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_braAnaFlowRadioButtonActionPerformed
	ipssLogger.info("branchFlowRadioButtonActionPerformed() called");
	disableOutageAnalysisCompoment(false, false, true);
}//GEN-LAST:event_braAnaFlowRadioButtonActionPerformed

private void disableOutageAnalysisCompoment(boolean multi, boolean file, boolean outPoints) {
	this.braAnaMultiOutageBranchList.setEnabled(multi);
	this.braAnaAddOutageBranchButton.setEnabled(multi);
	this.braAnaRemoveOutageBranchButton.setEnabled(multi);
	this.braAnaOutageFileLabel.setEnabled(file);
	this.braAnaOutageFileSelectButton.setEnabled(file);
	this.braAnaOutageFileTextField.setEnabled(file);
	this.braAnaFlowOutPointsLabel.setEnabled(outPoints);
	this.braAnaFlowOutPointsTextField.setEnabled(outPoints);
}

private void braAnaAddOutageBranchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_braAnaAddOutageBranchButtonActionPerformed
	ipssLogger.info("addOutageBranchButtonActionPerformed() called");
	String id = (String)this.braAnaBranchListComboBox.getSelectedItem();
	RunUIUtilFunc.addItemJList(braAnaMultiOutageBranchList, id);
}//GEN-LAST:event_braAnaAddOutageBranchButtonActionPerformed

private void braAnaRemoveOutageBranchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_braAnaRemoveOutageBranchButtonActionPerformed
	ipssLogger.info("removeOutageBranchButtonActionPerformed() called");
    RunUIUtilFunc.removeItemJList(this.braAnaMultiOutageBranchList);
}//GEN-LAST:event_braAnaRemoveOutageBranchButtonActionPerformed

private void braAnaOutageFileSelectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_braAnaOutageFileSelectButtonActionPerformed
	ipssLogger.info("outageFileSelectButtonActionPerformed() called");
}//GEN-LAST:event_braAnaOutageFileSelectButtonActionPerformed

//TODO
/* 888888888888888888888888
 *  Gen Analysis
 8888888888888888888888888888*/

private void runCalLossFactorsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runCalLossFactorsButtonActionPerformed
	ipssLogger.info("runCalLossFactorsButtonActionPerformed() called");
	
	this.parent.setAlwaysOnTop(false);
	if (!saveInputData())
		return;

	final AclfNetwork net = this._simuCtx.getAclfNet();
	final PTradingEDHourlyAnalysisXmlType ptXml = this._ptXml;
	final PowerTradingInfoXmlType ptInfoXml = this._ptInfoXml;
	
	new Thread() {
		public void run() {
			IAppStatus appStatus = GraphSpringFactory.getIpssGraphicEditor().getAppStatus();
			appStatus.busyStart(Constants.StatusBusyIndicatorPeriod,
					"Run PowerTrading Analysis ...", "Run PTraing");

			// Book marked the AclfNetwork object
			ChangeRecorder recorderBaseNet = new ChangeRecorder(net);	

			try {
				ptXml.getGenAnalysis().setType(PtGenAnalysisEnumType.LOSS_FACTOR);
				double[] lfactors = (double[])new PTradingDslODMRunner(net)
					.runPTradingAnalysis(ptXml, ptInfoXml, PtAnalysisType.Gen);
				
				String str = "LossFactor: \n";
				int cnt = 0;
				for ( IDRecordXmlType genBus : ptXml.getGenAnalysis().getGenBus() ) {
					str += "gen@" + genBus.getId() + " " + Number2String.toStr(lfactors[cnt++]) + "\n";
				}
				
				UISpringFactory.getOutputTextDialog("Gen Loss Factor Results").display(str);
			} catch (InterpssException e) {
				PluginSpringFactory.getEditorDialogUtil().showMsgDialog(parent, "Analysis Error", e.toString());
				recorderBaseNet.endRecording().apply();	
				return;
			}

			// roll-back AclfNet to the bookmarked point
			recorderBaseNet.endRecording().apply();	

			appStatus.busyStop("Run PowerTrading Analysis finished");			
		}
	}.start();
}//GEN-LAST:event_runCalLossFactorsButtonActionPerformed

private void genAnalysisAddGenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genAnalysisAddGenButtonActionPerformed
    ipssLogger.info("genAddGenButtonActionPerformed() called");
	String id = (String)this.genAnalysisGenBusListComboBox.getSelectedItem();
	RunUIUtilFunc.addItemJList(this.genAnalysisGenBusList, id);
}//GEN-LAST:event_genAnalysisAddGenButtonActionPerformed

private void genAnalysisRemoveGenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genAnalysisRemoveGenButtonActionPerformed
    ipssLogger.info("genRemoveGenButtonActionPerformed() called");
    RunUIUtilFunc.removeItemJList(this.genAnalysisGenBusList);
}//GEN-LAST:event_genAnalysisRemoveGenButtonActionPerformed

private void genLoadDistBusRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genLoadDistBusRadioButtonActionPerformed
	ipssLogger.info("genLoadDistBusRadioButtonActionPerformed() called");
	this.genLoadDistLoadBusLabel.setEnabled(true);
	this.genLoadDistLoadBusComboBox.setEnabled(true);
	this.genLoadDistUserFileTextField.setEnabled(false);
	this.genLoadDistUserFileButton.setEnabled(false);
}//GEN-LAST:event_genLoadDistBusRadioButtonActionPerformed

private void genLoadDistBasecaseRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genLoadDistBasecaseRadioButtonActionPerformed
	ipssLogger.info("genLoadDistBasecaseRadioButtonActionPerformed() called");
	this.genLoadDistLoadBusLabel.setEnabled(false);
	this.genLoadDistLoadBusComboBox.setEnabled(false);
	this.genLoadDistUserFileTextField.setEnabled(false);
	this.genLoadDistUserFileButton.setEnabled(false);
}//GEN-LAST:event_genLoadDistBasecaseRadioButtonActionPerformed

private void genLoadDistUserRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genLoadDistUserRadioButtonActionPerformed
	ipssLogger.info("genLoadDistAPNodeRadioButtonActionPerformed() called");
	this.genLoadDistLoadBusLabel.setEnabled(false);
	this.genLoadDistLoadBusComboBox.setEnabled(false);
	this.genLoadDistUserFileTextField.setEnabled(true);
	this.genLoadDistUserFileButton.setEnabled(true);
}//GEN-LAST:event_genLoadDistUserRadioButtonActionPerformed

private void genLoadDistUserFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genLoadDistUserFileButtonActionPerformed
	ipssLogger.info("genLoadDistUserFileButtonActionPerformed() called");
	JFileChooser fc = getExcelFileChooser();
	if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
		File file = fc.getSelectedFile();
		this.genLoadDistUserFileTextField.setText(file.getAbsolutePath());
	}
}//GEN-LAST:event_genLoadDistUserFileButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel aclfAnalysisPanel;
    private javax.swing.JComboBox aclfEdHourComboBox;
    private javax.swing.JLabel aclfEdHourLabel;
    private javax.swing.JLabel aclfToleranceLabel;
    private javax.swing.JTextField aclfToleranceTextField;
    private javax.swing.JCheckBox aclfUseCachedVoltCheckBox;
    private javax.swing.JButton braAnaAddOutageBranchButton;
    private javax.swing.JLabel braAnaBranchLabel;
    private javax.swing.JComboBox braAnaBranchListComboBox;
    private javax.swing.JComboBox braAnaEdHourComboBox;
    private javax.swing.JLabel braAnaEdHourLabel;
    private javax.swing.JLabel braAnaFlowOutPointsLabel;
    private javax.swing.JTextField braAnaFlowOutPointsTextField;
    private javax.swing.JRadioButton braAnaFlowRadioButton;
    private javax.swing.JList braAnaMultiOutageBranchList;
    private javax.swing.JScrollPane braAnaMultiOutageBranchScrollPane;
    private javax.swing.JPanel braAnaOutageAnalysisPanel;
    private javax.swing.JLabel braAnaOutageFileLabel;
    private javax.swing.JButton braAnaOutageFileSelectButton;
    private javax.swing.JTextField braAnaOutageFileTextField;
    private javax.swing.JRadioButton braAnaOutageMultiRadioButton;
    private javax.swing.JRadioButton braAnaOutageScheduleRadioButton;
    private javax.swing.JRadioButton braAnaOutageSingleRadioButton;
    private javax.swing.JButton braAnaRemoveOutageBranchButton;
    private javax.swing.JPanel braAnaTypePanel;
    private javax.swing.JPanel branchAnalysisPanel;
    private javax.swing.ButtonGroup branchAnalysisTypeButtonGroup;
    private javax.swing.JPanel caseDataPanel;
    private javax.swing.JLabel edDateLabel;
    private javax.swing.JTextField edDateTextField;
    private javax.swing.JLabel edFileLabel;
    private javax.swing.JPanel edFilePanel;
    private javax.swing.JTextField edFileTextField;
    private javax.swing.JTextField edGenPFacorTextField;
    private javax.swing.JLabel edGenPFactorLabel;
    private javax.swing.JTextField edLoadPFacorTextField;
    private javax.swing.JLabel edLoadPFactorLabel;
    private javax.swing.JLabel edLossPercentLabel;
    private javax.swing.JTextField edLossPercentTextField;
    private javax.swing.JButton genAnalysisAddGenButton;
    private javax.swing.JLabel genAnalysisGenBusLabel;
    private javax.swing.JList genAnalysisGenBusList;
    private javax.swing.JComboBox genAnalysisGenBusListComboBox;
    private javax.swing.JScrollPane genAnalysisGenScrollPane;
    private javax.swing.JPanel genAnalysisPanel;
    private javax.swing.JButton genAnalysisRemoveGenButton;
    private javax.swing.JPanel genLoadDisPanel;
    private javax.swing.JRadioButton genLoadDistBasecaseRadioButton;
    private javax.swing.JRadioButton genLoadDistBusRadioButton;
    private javax.swing.JComboBox genLoadDistLoadBusComboBox;
    private javax.swing.JLabel genLoadDistLoadBusLabel;
    private javax.swing.JButton genLoadDistUserFileButton;
    private javax.swing.JTextField genLoadDistUserFileTextField;
    private javax.swing.JRadioButton genLoadDistUserRadioButton;
    private javax.swing.JLabel interfaceFileLabel;
    private javax.swing.JPanel interfaceFilePanel;
    private javax.swing.JTextField interfaceFileTextField;
    private javax.swing.JLabel interfaceLimitLabel;
    private javax.swing.JTextField interfaceLimitTextField;
    private javax.swing.JLabel lfAssistGenFileLabel;
    private javax.swing.JButton lfAssistGenFileSelectButton;
    private javax.swing.JTextField lfAssistGenFileTextField;
    private javax.swing.JPanel lfAssistGenPanel;
    private javax.swing.JLabel lfAssistGenQAdjStepsLabel;
    private javax.swing.JTextField lfAssistGenQAdjStespTextField;
    private javax.swing.JLabel lfAssistGenQAdjToleranceLabel;
    private javax.swing.JTextField lfAssistGenQAdjToleranceTextField;
    private javax.swing.ButtonGroup lfResultButtonGroup;
    private javax.swing.ButtonGroup loadDistButtonGroup;
    private javax.swing.JPanel loadDistPanel;
    private javax.swing.JLabel loadDistThreshholdLabel;
    private javax.swing.JTextField loadDistThreshholdTextField;
    private javax.swing.JCheckBox outAreaSummaryCheckBox;
    private javax.swing.JCheckBox outBranchViolationCheckBox;
    private javax.swing.JCheckBox outInterfaceViolationCheckBox;
    private javax.swing.JLabel outLargeGSFPointsLabel;
    private javax.swing.JTextField outLargeGSFPointsTextField;
    private javax.swing.JRadioButton outLfBusStyleRadioButton;
    private javax.swing.JCheckBox outLfResultCheckBox;
    private javax.swing.JLabel outLfResultFormatLabel;
    private javax.swing.JRadioButton outLfSummaryRadioButton;
    private javax.swing.JLabel outToekn_Label;
    private javax.swing.JCheckBox outVoltViolationCheckBox;
    private javax.swing.JCheckBox outZoneSummaryCheckBox;
    private javax.swing.JPanel outputConfigPanel;
    private javax.swing.JTabbedPane pTradingAnalysisTabbedPane;
    private javax.swing.JButton runAclfAnalysisButton;
    private javax.swing.JButton runBranchAnalysisButton;
    private javax.swing.JButton runCalLossFactorsButton;
    private javax.swing.JButton selectEdFileButton;
    private javax.swing.JButton selectInterfaceFileButton;
    private javax.swing.JButton selectInterfaceLimitButton;
    private javax.swing.JLabel swingAllocAccFactorLabel;
    private javax.swing.JTextField swingAllocAccFactorTextField;
    private javax.swing.JCheckBox swingAllocCheckBox;
    private javax.swing.JLabel swingAllocMaxStepsLabel;
    private javax.swing.JTextField swingAllocMaxStepsTextField;
    private javax.swing.JLabel swingAllocToleraceLabel;
    private javax.swing.JTextField swingAllocToleranceTextField;
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
	    braAnaFlowOutPointsTextField.setInputVerifier(v);
	    
	    edDateTextField.setInputVerifier(v);
		edGenPFacorTextField.setInputVerifier(v);
		edLossPercentTextField.setInputVerifier(v);
		edLoadPFacorTextField.setInputVerifier(v);

		outLargeGSFPointsTextField.setInputVerifier(v);
	    aclfToleranceTextField.setInputVerifier(v);
	    lfAssistGenQAdjToleranceTextField.setInputVerifier(v);
		lfAssistGenQAdjStespTextField.setInputVerifier(v);
	    loadDistThreshholdTextField.setInputVerifier(v);
		
		swingAllocZoneComboBox.setInputVerifier(v);
		swingAllocMaxStepsTextField.setInputVerifier(v);
		swingAllocAccFactorTextField.setInputVerifier(v);
		voltUpperLimitTextField.setInputVerifier(v);
		voltLowerLimitTextField.setInputVerifier(v);
	}
	
	class DataVerifier extends javax.swing.InputVerifier {
		public boolean verify(javax.swing.JComponent input) {
			if (input == null)
				return false;
       		try {
				if (input == braAnaFlowOutPointsTextField)
					return SwingInputVerifyUtil.getInt((javax.swing.JTextField)input) > 0;

				else if (input == edDateTextField )
					return !SwingInputVerifyUtil.isEmptyStr((javax.swing.JTextField)input);
			    else if (input == edGenPFacorTextField)
					return SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) > 0.0 && 
							SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) <= 1.0;
				else if (input == edLossPercentTextField)
					return SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) > 0.0 && 
					       SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) <= 100.0;
				else if (input == edLoadPFacorTextField)
					return SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) > 0.0 && 
						   SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) <= 1.0;

				else if (input == aclfEdHourComboBox )
					return !SwingInputVerifyUtil.isEmptyStr((javax.swing.JComboBox)input);
				else if (input == aclfToleranceTextField)
					return SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) > 0.0;
				else if (input == lfAssistGenQAdjToleranceTextField)
					return SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) > 0.0;
				else if (input == lfAssistGenQAdjStespTextField)
					return SwingInputVerifyUtil.getInt((javax.swing.JTextField)input) > 0;
				else if (input == swingAllocZoneComboBox )
					return !SwingInputVerifyUtil.isEmptyStr((javax.swing.JComboBox)input);
				else if (input == swingAllocMaxStepsTextField)
					return SwingInputVerifyUtil.getInt((javax.swing.JTextField)input) > 0;
				else if (input == swingAllocAccFactorTextField)
					return SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) > 0.0;
					
				else if (input == outLargeGSFPointsTextField)
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
