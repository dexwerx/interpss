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
import org.ieee.odm.model.ext.ipss.AggregatePricingHelper;
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
    
//	private ODMModelParser odmParser = new ODMModelParser();
//    public void setODMParser(ODMModelParser parser) { 	this.odmParser = parser;   }
    
    private PTradingEDHourlyAnalysisXmlType _ptXml = null;
    private PowerTradingInfoXmlType _ptInfoXml = null;
    
    private List<DblBusValue> genPVSwingBusVoltCacheList = null;

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
		ipssLogger.info("NBPTradingCasePanel init() called");
	    //_netContainer = (GFormContainer)netContainer;
	    _simuCtx = (SimuContext)simuCtx;
	    
	    this.pTradingAnalysisTabbedPane.setSelectedIndex(1);
	    
	    // populate the Swing alloc zone
	    AclfNetwork net = _simuCtx.getAclfNet();
	    try {
	    	AclfBus swingBus = net.getAclfBus(new AclfNetHelper(net)
	    			.getSwingBusId());
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
		this.useCachedVoltCheckBox.setSelected(false);
		this.useCachedVoltCheckBox.setEnabled(false);
		
		this.braAnalysisBranchListComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.AllBranch).toArray()));

		this.genAnalysisGenBusListComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.GenBus).toArray()));
	}
    
    public void setXmlCaseData(PTradingEDHourlyAnalysisXmlType pt, PowerTradingInfoXmlType ptInfo) {
    	this._ptXml = pt;
    	this._ptInfoXml = ptInfo;
    }
    
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
		
		// load FlowInterface if necessary
		// 
//		if (!RunUIUtilFunc.loadFlowInterfaceFiles(this._simuCtx.getAclfNet(), this._ptXml))
//			return false;
		
		if (this._ptInfoXml.getLoadDist() != null) {
			if (this._ptInfoXml.getLoadDist().getMinLoadForDistFactor() != null)
				this.loadDistThreshholdTextField.setText(
						Number2String.toStr(this._ptInfoXml.getLoadDist().getMinLoadForDistFactor().getValue(), "#0.0"));
			if (this._ptInfoXml.getLoadDist().getAggregatePricing() != null) {
				this.loadDistAPNodeFileTextField.setText(
						this._ptInfoXml.getLoadDist().getAggregatePricing().getAggregatePricingFilename());
				if ( RunUIUtilFunc.loadAPNodeFile(_ptInfoXml)) {
					AggregatePricingHelper helper = new AggregatePricingHelper(this._ptInfoXml.getLoadDist().getAggregatePricing());
					String[] idAry = helper.getAPNodeIdAry();
					this.genLoadDistAPNodeComboBox.setModel(new javax.swing.DefaultComboBoxModel(idAry));
				}
				else {
					this.genLoadDistAPNodeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] {"No AP Node defined"}));
				}
			}
		}
		
		// Case Data Panel
		PtCaseDataXmlType casedata = this._ptXml.getCaseData();
		edFileTextField.setText(casedata.getEdFile().getFilename());
		
		edGenPFacorTextField.setText(Number2String.toStr(casedata.getEdFile().getGenPFactor(), "#0.00"));
		edLossPercentTextField.setText(Number2String.toStr(casedata.getEdFile().getLossPercent(), "#0.00"));
		edLoadPFacorTextField.setText(Number2String.toStr(casedata.getEdFile().getLoadPFactor(), "#0.00"));
		this.edDateTextField.setText(casedata.getEdFile().getDate());
		
		interfaceFileTextField.setText(this._ptInfoXml.getInterfaceFilename());
		interfaceLimitTextField.setText(casedata.getInterfaceLimitFilename());
		
		// Aclf Analysis
		if (this._ptXml.getAclfAnalysis() == null)
			this._ptXml.setAclfAnalysis(odmObjFactory.createPtAclfAnalysisXmlType());
		PtAclfAnalysisXmlType aclfXml = this._ptXml.getAclfAnalysis();

		aclfEdHourComboBox.setSelectedItem(aclfXml.getHour() == null? "12:00" : aclfXml.getHour());

		if (aclfXml.getTolerance() != null)
			this.lfToleranceTextField.setText(Number2String.toStr(aclfXml.getTolerance(), "0.0000"));
		
		//aclfXml.setGenQAdjustment(true);
		if (aclfXml.isGenQAdjustment()) {
			lfAssistGenQAdjStespTextField.setText(
					new Integer(aclfXml.getGenQAdjOption().getNoRunsLfAssist()).toString());
			lfAssistGenFileTextField.setText(aclfXml.getGenQAdjOption().getLfAssistGenFilename());
			if (aclfXml.getGenQAdjOption().getLfAssistAdjTolerance() != null)
				this.lfAssistGenQAdjToleranceTextField.setText(
						aclfXml.getGenQAdjOption().getLfAssistAdjTolerance().toString());
		}
		
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
		if (this._ptXml.getBranchAnalysis() == null) 
			this._ptXml.setBranchAnalysis(odmObjFactory.createPtBranchAnalysisXmlType());
		PtBranchAnalysisXmlType braAnalysis = this._ptXml.getBranchAnalysis();
		this.branchAnalysisEdHourComboBox.setSelectedItem(braAnalysis.getHour() == null? "12:00" : braAnalysis.getHour());
		if (braAnalysis.getType() != null) 
			if (braAnalysis.getType() == PtBranchAnalysisEnumType.SINGLE_BRANCH_OUTAGE)
				this.outageSingleRadioButton.setSelected(true);
			else if (braAnalysis.getType() == PtBranchAnalysisEnumType.MULTI_BRANCH_OUTAGE)
				this.outageMultiRadioButton.setSelected(true);
			else if (braAnalysis.getType() == PtBranchAnalysisEnumType.OUTAGE_SCHEDULE)
				this.outageScheduleRadioButton.setSelected(true);
			else if (braAnalysis.getType() == PtBranchAnalysisEnumType.BRANCH_FLOW)
				this.branchFlowRadioButton.setSelected(true);

		if (this.outageMultiRadioButton.isSelected()) {
			outageMultiRadioButtonActionPerformed(null);
			// TODO
		}
		else if (this.outageScheduleRadioButton.isSelected()) {
		    // TODO	
			outageScheduleRadioButtonActionPerformed(null);
		}
		else if (this.branchFlowRadioButton.isSelected()) {
			branchFlowRadioButtonActionPerformed(null);
			this.braFlowOutPointsTextField.setText(
					braAnalysis.getBranchFlowOutPoints().toString());
		}
		
		if (braAnalysis.getBranch() != null && braAnalysis.getBranch().size() > 0) {
			BranchRefXmlType branch = braAnalysis.getBranch().get(0);
			this.braAnalysisBranchListComboBox.setSelectedItem(branch.getBranchId());
		}
		
		// Gen analysis Analysis
		if (this._ptXml.getGenAnalysis() == null) 
			this._ptXml.setGenAnalysis(odmObjFactory.createPtGenAnalysisXmlType());
		PtGenAnalysisXmlType genAnalysis = this._ptXml.getGenAnalysis();
		this.genAnalysisEdHourComboBox.setSelectedItem(genAnalysis.getHour() == null? "12:00" : genAnalysis.getHour());
		if (genAnalysis.getGenBus() != null) {
			String[] sAry = StringUtil.getIdNameAry(genAnalysis.getGenBus(), new IFunction<Object,String>() {
				@Override public String f(Object value) { return ((IDRecordXmlType)value).getId(); } });
			this.genAnalysisGenBusList.setModel(new javax.swing.DefaultComboBoxModel(sAry));    	
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
		this.outLfResultCheckBox.setSelected(outOpt.isLfResult());
		if (outOpt.getLfResultFormat() == LfResultFormatEnumType.BUS_STYLE)
			this.lfBusStyleRadioButton.setSelected(true);
		else
			this.lfSummaryRadioButton.setSelected(true);

		return true;
	}
	
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
		ipssLogger.info("NBPTradingCasePanel saveEditor2Form() called");
		
		this._ptInfoXml.getLoadDist().getAggregatePricing().getApNode().clear();

		saveCaseData(errMsg);
		saveAclfAnalysis(errMsg);
		saveBranchAnalysis(errMsg);
		saveGenAnalysis(errMsg);
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

		if (this._ptInfoXml.getLoadDist().getAggregatePricing() == null)
			this._ptInfoXml.getLoadDist().setAggregatePricing(odmObjFactory.createAggregatePricingXmlType());
		this._ptInfoXml.getLoadDist().getAggregatePricing().setAggregatePricingFilename(
				this.loadDistAPNodeFileTextField.getText());
		
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

		aclfXml.setGenQAdjustment(true);
		if (aclfXml.isGenQAdjustment()) {
			aclfXml.getGenQAdjOption().setNoRunsLfAssist(
					new Integer(lfAssistGenQAdjStespTextField.getText()).intValue());
			aclfXml.getGenQAdjOption().setLfAssistGenFilename(lfAssistGenFileTextField.getText());
			aclfXml.getGenQAdjOption().setLfAssistAdjTolerance(
					new Double(this.lfAssistGenQAdjToleranceTextField.getText()).doubleValue());
		}
		
		return noError;
	}

	public boolean saveAclfAnalysis(Vector<String> errMsg) {
		boolean noError = true;

		if (this._ptXml.getAclfAnalysis() == null) {
			this._ptXml.setAclfAnalysis(odmObjFactory.createPtAclfAnalysisXmlType());
			this._ptXml.getAclfAnalysis().setGenSwingAllocOption(odmObjFactory.createPtSwingAllocXmlType());
		}
		PtAclfAnalysisXmlType aclfXml = this._ptXml.getAclfAnalysis();
	 	
		aclfXml.setHour(this.aclfEdHourComboBox.getSelectedItem().toString());
		
		aclfXml.setTolerance(new Double(this.lfToleranceTextField.getText()).doubleValue());
		
		aclfXml.setSwingBusPQAlloc(swingAllocCheckBox.isSelected());
		if (aclfXml.isSwingBusPQAlloc()) {
			if (this.swingAllocZoneComboBox.getSelectedItem() == null) {
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
		
//		IpssScenarioHelper helper = new IpssScenarioHelper(this.odmParser);
		
		braAnalysis.setHour((String)this.branchAnalysisEdHourComboBox.getSelectedItem());
		braAnalysis.setType(this.outageSingleRadioButton.isSelected()? PtBranchAnalysisEnumType.SINGLE_BRANCH_OUTAGE :
			(this.outageMultiRadioButton.isSelected()? PtBranchAnalysisEnumType.MULTI_BRANCH_OUTAGE :
				(this.outageScheduleRadioButton.isSelected()? PtBranchAnalysisEnumType.SINGLE_BRANCH_OUTAGE :
					PtBranchAnalysisEnumType.BRANCH_FLOW)));

		if (this.outageSingleRadioButton.isSelected() ||
				this.branchFlowRadioButton.isSelected()) {
			braAnalysis.getBranch().clear();
			String braId = (String)this.braAnalysisBranchListComboBox.getSelectedItem();
			BranchRefXmlType outage = BaseJaxbHelper.creatBranchRef(braAnalysis.getBranch());
			RunUIUtilFunc.setBranchIdInfo(outage, braId);
			if (this.branchFlowRadioButton.isSelected()) {
			    braAnalysis.setBranchFlowOutPoints(
			    		new Integer(this.braFlowOutPointsTextField.getText()).intValue());
			}
		}
		else if (this.outageMultiRadioButton.isSelected()) {
			// TODO
		}
		else if (this.outageScheduleRadioButton.isSelected()) {
		    // TODO	
		}
		
		return noError;
	}

	public boolean saveGenAnalysis(Vector<String> errMsg) {
		boolean noError = true;
		
		if (this._ptXml.getGenAnalysis() == null) {
			this._ptXml.setGenAnalysis(odmObjFactory.createPtGenAnalysisXmlType());
		}
		PtGenAnalysisXmlType genAnalysis = this._ptXml.getGenAnalysis();
		
		genAnalysis.setHour((String)this.genAnalysisEdHourComboBox.getSelectedItem());
		
		int size = this.genAnalysisGenBusList.getModel().getSize();
		if (size == 0) {
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

		return noError;
	}

	public boolean saveOutputConfig(Vector<String> errMsg) {
		boolean noError = true;

		if (this._ptXml.getAclfAnalysis().getOutputOption() == null) {
			this._ptXml.getAclfAnalysis().setOutputOption(odmObjFactory.createPtAclfOutputXmlType());
		}
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
		outOpt.setLfResult(this.outLfResultCheckBox.isSelected());
		outOpt.setLfResultFormat(this.lfBusStyleRadioButton.isSelected()? 
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
        loadDistributionPanel = new javax.swing.JPanel();
        loadDistThreshholdLabel = new javax.swing.JLabel();
        loadDistThreshholdTextField = new javax.swing.JTextField();
        loadDistAPNodeFileLabel = new javax.swing.JLabel();
        loadDistAPNodeFileTextField = new javax.swing.JTextField();
        selectLoadDistAPNodeFileButton = new javax.swing.JButton();
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
        lfToleranceLabel = new javax.swing.JLabel();
        lfToleranceTextField = new javax.swing.JTextField();
        useCachedVoltCheckBox = new javax.swing.JCheckBox();
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
        branchAnalysisEdHourLabel = new javax.swing.JLabel();
        branchAnalysisEdHourComboBox = new javax.swing.JComboBox();
        braAnalysisBranchListComboBox = new javax.swing.JComboBox();
        braAnalysisBranchLabel = new javax.swing.JLabel();
        branchAnalysisTypePanel = new javax.swing.JPanel();
        outageSingleRadioButton = new javax.swing.JRadioButton();
        outageMultiRadioButton = new javax.swing.JRadioButton();
        outageScheduleRadioButton = new javax.swing.JRadioButton();
        branchFlowRadioButton = new javax.swing.JRadioButton();
        outageAnalysisPanel = new javax.swing.JPanel();
        multiOutageBranchScrollPane = new javax.swing.JScrollPane();
        multiOutageBranchList = new javax.swing.JList();
        addOutageBranchButton = new javax.swing.JButton();
        removeOutageBranchButton = new javax.swing.JButton();
        outageFileLabel = new javax.swing.JLabel();
        outageFileTextField = new javax.swing.JTextField();
        outageFileSelectButton = new javax.swing.JButton();
        braFlowOutPointsLabel = new javax.swing.JLabel();
        braFlowOutPointsTextField = new javax.swing.JTextField();
        runBranchAnalysisButton = new javax.swing.JButton();
        genAnalysisPanel = new javax.swing.JPanel();
        genAnalysisEdHourLabel = new javax.swing.JLabel();
        genAnalysisEdHourComboBox = new javax.swing.JComboBox();
        genAnalysisGenBusLabel = new javax.swing.JLabel();
        genAnalysisGenBusListComboBox = new javax.swing.JComboBox();
        genAnalysisAddGenButton = new javax.swing.JButton();
        genAnalysisRemoveGenButton = new javax.swing.JButton();
        genAnalysisGenScrollPane = new javax.swing.JScrollPane();
        genAnalysisGenBusList = new javax.swing.JList();
        runCalLossFactorsButton = new javax.swing.JButton();
        genLoadDisPanel = new javax.swing.JPanel();
        genLoadDistBasecaseRadioButton = new javax.swing.JRadioButton();
        genLoadDistAPNodeRadioButton = new javax.swing.JRadioButton();
        genLoadDistAPNodeLabel = new javax.swing.JLabel();
        genLoadDistAPNodeComboBox = new javax.swing.JComboBox();
        outputConfigPanel = new javax.swing.JPanel();
        outVoltViolationCheckBox = new javax.swing.JCheckBox();
        voltUpperLimitLabel = new javax.swing.JLabel();
        voltUpperLimitTextField = new javax.swing.JTextField();
        voltLowerLimitLabel = new javax.swing.JLabel();
        voltLowerLimitTextField = new javax.swing.JTextField();
        largeGSFPointsLabel = new javax.swing.JLabel();
        largeGSFPointsTextField = new javax.swing.JTextField();
        outBranchViolationCheckBox = new javax.swing.JCheckBox();
        outInterfaceViolationCheckBox = new javax.swing.JCheckBox();
        outZoneSummaryCheckBox = new javax.swing.JCheckBox();
        outAreaSummaryCheckBox = new javax.swing.JCheckBox();
        outLfResultCheckBox = new javax.swing.JCheckBox();
        lfSummaryRadioButton = new javax.swing.JRadioButton();
        lfBusStyleRadioButton = new javax.swing.JRadioButton();
        lfResultFormatLabel = new javax.swing.JLabel();
        toekn_Label = new javax.swing.JLabel();

        setFont(new java.awt.Font("Dialog", 0, 12));

        pTradingAnalysisTabbedPane.setFont(new java.awt.Font("Dialog", 0, 12));
        pTradingAnalysisTabbedPane.setMinimumSize(new java.awt.Dimension(80, 48));
        pTradingAnalysisTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                panelSelectionChanged(evt);
            }
        });

        edFilePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "EDispatch Info", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N

        edFileLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        edFileLabel.setText("Daily ED File");

        edFileTextField.setFont(new java.awt.Font("Dialog", 0, 10));
        edFileTextField.setText("ED file");

        selectEdFileButton.setFont(new java.awt.Font("Dialog", 0, 10));
        selectEdFileButton.setText("Select ...");
        selectEdFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectEdFileButtonActionPerformed(evt);
            }
        });

        edGenPFacorTextField.setFont(new java.awt.Font("Dialog", 0, 10));
        edGenPFacorTextField.setText("0.90");

        edGenPFactorLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        edGenPFactorLabel.setText("Gen PFator (PU)");

        edDateLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        edDateLabel.setText("Date (mm/dd/yyyy)");

        edDateTextField.setFont(new java.awt.Font("Dialog", 0, 10));
        edDateTextField.setText("mm/dd/yyyy");

        edLoadPFacorTextField.setFont(new java.awt.Font("Dialog", 0, 10));
        edLoadPFacorTextField.setText("0.98");

        edLoadPFactorLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        edLoadPFactorLabel.setText("Load PFator (PU)");

        edLossPercentTextField.setFont(new java.awt.Font("Dialog", 0, 10));
        edLossPercentTextField.setText("2.5");

        edLossPercentLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        edLossPercentLabel.setText("Network Loss %");

        org.jdesktop.layout.GroupLayout edFilePanelLayout = new org.jdesktop.layout.GroupLayout(edFilePanel);
        edFilePanel.setLayout(edFilePanelLayout);
        edFilePanelLayout.setHorizontalGroup(
            edFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(edFilePanelLayout.createSequentialGroup()
                .add(18, 18, 18)
                .add(edFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(edFilePanelLayout.createSequentialGroup()
                        .add(edLoadPFactorLabel)
                        .add(18, 18, 18)
                        .add(edLoadPFacorTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE)
                        .add(295, 295, 295))
                    .add(edFilePanelLayout.createSequentialGroup()
                        .add(edFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(edFilePanelLayout.createSequentialGroup()
                                .add(edFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(edFileLabel)
                                    .add(edFilePanelLayout.createSequentialGroup()
                                        .add(89, 89, 89)
                                        .add(edFileTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)))
                                .add(18, 18, 18)
                                .add(selectEdFileButton))
                            .add(edFilePanelLayout.createSequentialGroup()
                                .add(edGenPFactorLabel)
                                .add(22, 22, 22)
                                .add(edGenPFacorTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 52, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(35, 35, 35)
                                .add(edFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(edDateLabel)
                                    .add(edLossPercentLabel))
                                .add(18, 18, 18)
                                .add(edFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(edLossPercentTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 42, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(edDateTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 84, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                        .add(36, 36, 36))))
        );
        edFilePanelLayout.setVerticalGroup(
            edFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(edFilePanelLayout.createSequentialGroup()
                .add(edFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(edFileLabel)
                    .add(edFileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(selectEdFileButton))
                .add(10, 10, 10)
                .add(edFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(edFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(edGenPFactorLabel)
                        .add(edGenPFacorTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(edFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(edDateLabel)
                        .add(edDateTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(edFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(edFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(edLoadPFactorLabel)
                        .add(edLoadPFacorTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(edLossPercentLabel)
                    .add(edLossPercentTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        loadDistributionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Load Distribution", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N

        loadDistThreshholdLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        loadDistThreshholdLabel.setText("Threshhold (Mw)   ");

        loadDistThreshholdTextField.setColumns(3);
        loadDistThreshholdTextField.setFont(new java.awt.Font("Dialog", 0, 10));
        loadDistThreshholdTextField.setText("5.0");

        loadDistAPNodeFileLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        loadDistAPNodeFileLabel.setText("APNode File");

        loadDistAPNodeFileTextField.setFont(new java.awt.Font("Dialog", 0, 10));
        loadDistAPNodeFileTextField.setText("Aggregate Pricing Node File");

        selectLoadDistAPNodeFileButton.setFont(new java.awt.Font("Dialog", 0, 10));
        selectLoadDistAPNodeFileButton.setText("Select ...");
        selectLoadDistAPNodeFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectLoadDistAPNodeFileButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout loadDistributionPanelLayout = new org.jdesktop.layout.GroupLayout(loadDistributionPanel);
        loadDistributionPanel.setLayout(loadDistributionPanelLayout);
        loadDistributionPanelLayout.setHorizontalGroup(
            loadDistributionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(loadDistributionPanelLayout.createSequentialGroup()
                .add(19, 19, 19)
                .add(loadDistributionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(loadDistributionPanelLayout.createSequentialGroup()
                        .add(loadDistAPNodeFileLabel)
                        .add(33, 33, 33)
                        .add(loadDistAPNodeFileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 225, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(selectLoadDistAPNodeFileButton))
                    .add(loadDistributionPanelLayout.createSequentialGroup()
                        .add(loadDistThreshholdLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(loadDistThreshholdTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 51, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        loadDistributionPanelLayout.setVerticalGroup(
            loadDistributionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(loadDistributionPanelLayout.createSequentialGroup()
                .add(loadDistributionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(loadDistThreshholdLabel)
                    .add(loadDistThreshholdTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(loadDistributionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(loadDistAPNodeFileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(selectLoadDistAPNodeFileButton)
                    .add(loadDistAPNodeFileLabel))
                .addContainerGap())
        );

        interfaceFilePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Interface Defintion", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N

        interfaceFileLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        interfaceFileLabel.setText("Interface File");

        interfaceFileTextField.setFont(new java.awt.Font("Dialog", 0, 10));
        interfaceFileTextField.setText("Interface File");

        selectInterfaceFileButton.setFont(new java.awt.Font("Dialog", 0, 10));
        selectInterfaceFileButton.setText("Select ...");
        selectInterfaceFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectInterfaceFileButtonActionPerformed(evt);
            }
        });

        interfaceLimitLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        interfaceLimitLabel.setText("Limit File");

        interfaceLimitTextField.setFont(new java.awt.Font("Dialog", 0, 10));
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
                .add(19, 19, 19)
                .add(interfaceFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(interfaceFileLabel)
                    .add(interfaceLimitLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 28, Short.MAX_VALUE)
                .add(interfaceFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(interfaceLimitTextField)
                    .add(interfaceFileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 230, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(interfaceFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(selectInterfaceLimitButton)
                    .add(selectInterfaceFileButton))
                .add(31, 31, 31))
        );
        interfaceFilePanelLayout.setVerticalGroup(
            interfaceFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(interfaceFilePanelLayout.createSequentialGroup()
                .add(interfaceFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(interfaceFilePanelLayout.createSequentialGroup()
                        .add(6, 6, 6)
                        .add(interfaceFileLabel))
                    .add(interfaceFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(selectInterfaceLimitButton)
                        .add(interfaceFilePanelLayout.createSequentialGroup()
                            .add(interfaceFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                .add(interfaceFileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(selectInterfaceFileButton))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                            .add(interfaceFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                .add(interfaceLimitTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(interfaceLimitLabel)))))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        lfAssistGenPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Lf Assistance Generator", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N

        lfAssistGenFileLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        lfAssistGenFileLabel.setText("LF Assist File");

        lfAssistGenFileTextField.setFont(new java.awt.Font("Dialog", 0, 10));
        lfAssistGenFileTextField.setText("LF Assistance Gen File");

        lfAssistGenFileSelectButton.setFont(new java.awt.Font("Dialog", 0, 10));
        lfAssistGenFileSelectButton.setText("Select ...");
        lfAssistGenFileSelectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lfAssistGenFileSelectButtonActionPerformed(evt);
            }
        });

        lfAssistGenQAdjStepsLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        lfAssistGenQAdjStepsLabel.setText("Q Adj Run Steps");

        lfAssistGenQAdjStespTextField.setFont(new java.awt.Font("Dialog", 0, 10));
        lfAssistGenQAdjStespTextField.setText("3");

        lfAssistGenQAdjToleranceLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        lfAssistGenQAdjToleranceLabel.setText("Q Adj Tolerance");

        lfAssistGenQAdjToleranceTextField.setFont(new java.awt.Font("Dialog", 0, 10));
        lfAssistGenQAdjToleranceTextField.setText("0.1");

        org.jdesktop.layout.GroupLayout lfAssistGenPanelLayout = new org.jdesktop.layout.GroupLayout(lfAssistGenPanel);
        lfAssistGenPanel.setLayout(lfAssistGenPanelLayout);
        lfAssistGenPanelLayout.setHorizontalGroup(
            lfAssistGenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lfAssistGenPanelLayout.createSequentialGroup()
                .add(18, 18, 18)
                .add(lfAssistGenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lfAssistGenPanelLayout.createSequentialGroup()
                        .add(lfAssistGenFileLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 33, Short.MAX_VALUE)
                        .add(lfAssistGenFileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 226, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(lfAssistGenFileSelectButton)
                        .add(33, 33, 33))
                    .add(lfAssistGenPanelLayout.createSequentialGroup()
                        .add(lfAssistGenQAdjStepsLabel)
                        .add(18, 18, 18)
                        .add(lfAssistGenQAdjStespTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 33, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(59, 59, 59)
                        .add(lfAssistGenQAdjToleranceLabel)
                        .add(33, 33, 33)
                        .add(lfAssistGenQAdjToleranceTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 33, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(113, Short.MAX_VALUE))))
        );
        lfAssistGenPanelLayout.setVerticalGroup(
            lfAssistGenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lfAssistGenPanelLayout.createSequentialGroup()
                .add(lfAssistGenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lfAssistGenFileLabel)
                    .add(lfAssistGenFileSelectButton)
                    .add(lfAssistGenFileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lfAssistGenPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lfAssistGenQAdjToleranceLabel)
                    .add(lfAssistGenQAdjToleranceTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lfAssistGenQAdjStepsLabel)
                    .add(lfAssistGenQAdjStespTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout caseDataPanelLayout = new org.jdesktop.layout.GroupLayout(caseDataPanel);
        caseDataPanel.setLayout(caseDataPanelLayout);
        caseDataPanelLayout.setHorizontalGroup(
            caseDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(caseDataPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(caseDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(loadDistributionPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(edFilePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, interfaceFilePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(lfAssistGenPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        caseDataPanelLayout.setVerticalGroup(
            caseDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(caseDataPanelLayout.createSequentialGroup()
                .add(edFilePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 114, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(loadDistributionPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 82, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(1, 1, 1)
                .add(interfaceFilePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lfAssistGenPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pTradingAnalysisTabbedPane.addTab("Case Data", caseDataPanel);

        aclfEdHourLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        aclfEdHourLabel.setText("ED Hour");

        aclfEdHourComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        aclfEdHourComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0:00", "1:00", "2:00", "3:00", "4:00", "5:00", "6:00", "7:00", "8:00", "9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00", " " }));

        lfToleranceLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        lfToleranceLabel.setText("Lf Tolerance");

        lfToleranceTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        lfToleranceTextField.setText("0.0001");

        useCachedVoltCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        useCachedVoltCheckBox.setText("Use Previous Run PV Voltage");

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
                                .add(lfToleranceLabel)
                                .add(29, 29, 29)
                                .add(lfToleranceTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 59, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .add(26, 26, 26)
                        .add(aclfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(useCachedVoltCheckBox)
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
                    .add(lfToleranceTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lfToleranceLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(useCachedVoltCheckBox))
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

        branchAnalysisEdHourLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        branchAnalysisEdHourLabel.setText("ED Hour");

        branchAnalysisEdHourComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        branchAnalysisEdHourComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0:00", "1:00", "2:00", "3:00", "4:00", "5:00", "6:00", "7:00", "8:00", "9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00", " " }));

        braAnalysisBranchListComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        braAnalysisBranchListComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        braAnalysisBranchLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        braAnalysisBranchLabel.setText("Branch");

        branchAnalysisTypePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Analysis Type", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N

        branchAnalysisTypeButtonGroup.add(outageSingleRadioButton);
        outageSingleRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        outageSingleRadioButton.setSelected(true);
        outageSingleRadioButton.setText("Single Outage");
        outageSingleRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outageSingleRadioButtonActionPerformed(evt);
            }
        });

        branchAnalysisTypeButtonGroup.add(outageMultiRadioButton);
        outageMultiRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        outageMultiRadioButton.setText("Multi Outage");
        outageMultiRadioButton.setEnabled(false);
        outageMultiRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outageMultiRadioButtonActionPerformed(evt);
            }
        });

        branchAnalysisTypeButtonGroup.add(outageScheduleRadioButton);
        outageScheduleRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        outageScheduleRadioButton.setText("Outage Schedule");
        outageScheduleRadioButton.setEnabled(false);
        outageScheduleRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outageScheduleRadioButtonActionPerformed(evt);
            }
        });

        branchAnalysisTypeButtonGroup.add(branchFlowRadioButton);
        branchFlowRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        branchFlowRadioButton.setText("Branch Flow");
        branchFlowRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                branchFlowRadioButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout branchAnalysisTypePanelLayout = new org.jdesktop.layout.GroupLayout(branchAnalysisTypePanel);
        branchAnalysisTypePanel.setLayout(branchAnalysisTypePanelLayout);
        branchAnalysisTypePanelLayout.setHorizontalGroup(
            branchAnalysisTypePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(branchAnalysisTypePanelLayout.createSequentialGroup()
                .add(15, 15, 15)
                .add(outageSingleRadioButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(outageMultiRadioButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(outageScheduleRadioButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(branchFlowRadioButton)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        branchAnalysisTypePanelLayout.setVerticalGroup(
            branchAnalysisTypePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(branchAnalysisTypePanelLayout.createSequentialGroup()
                .add(branchAnalysisTypePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(outageSingleRadioButton)
                    .add(outageMultiRadioButton)
                    .add(outageScheduleRadioButton)
                    .add(branchFlowRadioButton))
                .addContainerGap(7, Short.MAX_VALUE))
        );

        outageAnalysisPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        multiOutageBranchList.setFont(new java.awt.Font("Dialog", 0, 12));
        multiOutageBranchList.setEnabled(false);
        multiOutageBranchScrollPane.setViewportView(multiOutageBranchList);

        addOutageBranchButton.setFont(new java.awt.Font("Dialog", 0, 12));
        addOutageBranchButton.setText("Add");
        addOutageBranchButton.setEnabled(false);
        addOutageBranchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addOutageBranchButtonActionPerformed(evt);
            }
        });

        removeOutageBranchButton.setFont(new java.awt.Font("Dialog", 0, 12));
        removeOutageBranchButton.setText("Remove");
        removeOutageBranchButton.setEnabled(false);
        removeOutageBranchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeOutageBranchButtonActionPerformed(evt);
            }
        });

        outageFileLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        outageFileLabel.setText("Outage File");
        outageFileLabel.setEnabled(false);

        outageFileTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        outageFileTextField.setText("outage file");
        outageFileTextField.setEnabled(false);

        outageFileSelectButton.setFont(new java.awt.Font("Dialog", 0, 10));
        outageFileSelectButton.setText("Select ...");
        outageFileSelectButton.setEnabled(false);
        outageFileSelectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outageFileSelectButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout outageAnalysisPanelLayout = new org.jdesktop.layout.GroupLayout(outageAnalysisPanel);
        outageAnalysisPanel.setLayout(outageAnalysisPanelLayout);
        outageAnalysisPanelLayout.setHorizontalGroup(
            outageAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(outageAnalysisPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(outageAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(outageAnalysisPanelLayout.createSequentialGroup()
                        .add(multiOutageBranchScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 249, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(outageAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(removeOutageBranchButton)
                            .add(addOutageBranchButton)))
                    .add(outageAnalysisPanelLayout.createSequentialGroup()
                        .add(outageFileLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(outageFileTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(outageFileSelectButton)))
                .addContainerGap())
        );
        outageAnalysisPanelLayout.setVerticalGroup(
            outageAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(outageAnalysisPanelLayout.createSequentialGroup()
                .add(outageAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(multiOutageBranchScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 82, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(outageAnalysisPanelLayout.createSequentialGroup()
                        .add(addOutageBranchButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(removeOutageBranchButton)))
                .add(18, 18, 18)
                .add(outageAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(outageFileLabel)
                    .add(outageFileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(outageFileSelectButton)))
        );

        braFlowOutPointsLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        braFlowOutPointsLabel.setText("BranchFlow Output Points");
        braFlowOutPointsLabel.setEnabled(false);

        braFlowOutPointsTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        braFlowOutPointsTextField.setText("20");
        braFlowOutPointsTextField.setEnabled(false);

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
                        .add(branchAnalysisEdHourLabel)
                        .add(26, 26, 26)
                        .add(branchAnalysisEdHourComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 77, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(branchAnalysisPanelLayout.createSequentialGroup()
                        .add(102, 102, 102)
                        .add(braAnalysisBranchLabel)
                        .add(33, 33, 33)
                        .add(braAnalysisBranchListComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 184, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
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
                        .add(outageAnalysisPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(branchAnalysisTypePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(26, Short.MAX_VALUE))
            .add(branchAnalysisPanelLayout.createSequentialGroup()
                .add(49, 49, 49)
                .add(braFlowOutPointsLabel)
                .add(18, 18, 18)
                .add(braFlowOutPointsTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(251, Short.MAX_VALUE))
        );
        branchAnalysisPanelLayout.setVerticalGroup(
            branchAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, branchAnalysisPanelLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(branchAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(branchAnalysisEdHourComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(branchAnalysisEdHourLabel))
                .add(18, 18, 18)
                .add(branchAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(braAnalysisBranchListComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(braAnalysisBranchLabel))
                .add(4, 4, 4)
                .add(branchAnalysisTypePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(outageAnalysisPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(branchAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(braFlowOutPointsLabel)
                    .add(braFlowOutPointsTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(40, 40, 40)
                .add(runBranchAnalysisButton)
                .add(139, 139, 139))
        );

        pTradingAnalysisTabbedPane.addTab("Branch Analysis", branchAnalysisPanel);

        genAnalysisEdHourLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        genAnalysisEdHourLabel.setText("ED Hour");

        genAnalysisEdHourComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        genAnalysisEdHourComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0:00", "1:00", "2:00", "3:00", "4:00", "5:00", "6:00", "7:00", "8:00", "9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00", " " }));

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

        runCalLossFactorsButton.setFont(new java.awt.Font("Dialog", 0, 12));
        runCalLossFactorsButton.setText("Cal LossFactor");
        runCalLossFactorsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runCalLossFactorsButtonActionPerformed(evt);
            }
        });

        genLoadDisPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Load Distribution Factor", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N

        loadDistButtonGroup.add(genLoadDistBasecaseRadioButton);
        genLoadDistBasecaseRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        genLoadDistBasecaseRadioButton.setSelected(true);
        genLoadDistBasecaseRadioButton.setText("BasecaseLoad");
        genLoadDistBasecaseRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genLoadDistBasecaseRadioButtonActionPerformed(evt);
            }
        });

        loadDistButtonGroup.add(genLoadDistAPNodeRadioButton);
        genLoadDistAPNodeRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        genLoadDistAPNodeRadioButton.setText("AP Node");
        genLoadDistAPNodeRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genLoadDistAPNodeRadioButtonActionPerformed(evt);
            }
        });

        genLoadDistAPNodeLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        genLoadDistAPNodeLabel.setText("AP Node");
        genLoadDistAPNodeLabel.setEnabled(false);

        genLoadDistAPNodeComboBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        genLoadDistAPNodeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        genLoadDistAPNodeComboBox.setEnabled(false);

        org.jdesktop.layout.GroupLayout genLoadDisPanelLayout = new org.jdesktop.layout.GroupLayout(genLoadDisPanel);
        genLoadDisPanel.setLayout(genLoadDisPanelLayout);
        genLoadDisPanelLayout.setHorizontalGroup(
            genLoadDisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, genLoadDisPanelLayout.createSequentialGroup()
                .add(genLoadDisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, genLoadDisPanelLayout.createSequentialGroup()
                        .add(27, 27, 27)
                        .add(genLoadDistBasecaseRadioButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 28, Short.MAX_VALUE)
                        .add(genLoadDistAPNodeRadioButton))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, genLoadDisPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(genLoadDistAPNodeLabel)
                        .add(18, 18, 18)
                        .add(genLoadDistAPNodeComboBox, 0, 161, Short.MAX_VALUE)))
                .add(27, 27, 27))
        );
        genLoadDisPanelLayout.setVerticalGroup(
            genLoadDisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(genLoadDisPanelLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(genLoadDisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(genLoadDistBasecaseRadioButton)
                    .add(genLoadDistAPNodeRadioButton))
                .add(18, 18, 18)
                .add(genLoadDisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(genLoadDistAPNodeComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(genLoadDistAPNodeLabel))
                .add(17, 17, 17))
        );

        org.jdesktop.layout.GroupLayout genAnalysisPanelLayout = new org.jdesktop.layout.GroupLayout(genAnalysisPanel);
        genAnalysisPanel.setLayout(genAnalysisPanelLayout);
        genAnalysisPanelLayout.setHorizontalGroup(
            genAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(genAnalysisPanelLayout.createSequentialGroup()
                .add(genAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(genAnalysisPanelLayout.createSequentialGroup()
                        .add(83, 83, 83)
                        .add(genAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(genAnalysisPanelLayout.createSequentialGroup()
                                .add(66, 66, 66)
                                .add(genAnalysisEdHourLabel)
                                .add(18, 18, 18)
                                .add(genAnalysisEdHourComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 77, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(genAnalysisPanelLayout.createSequentialGroup()
                                .add(20, 20, 20)
                                .add(genAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, genLoadDisPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(genAnalysisPanelLayout.createSequentialGroup()
                                        .add(19, 19, 19)
                                        .add(genAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                            .add(genAnalysisPanelLayout.createSequentialGroup()
                                                .add(genAnalysisGenBusLabel)
                                                .add(18, 18, 18)
                                                .add(genAnalysisGenBusListComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 109, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                            .add(genAnalysisPanelLayout.createSequentialGroup()
                                                .add(genAnalysisGenScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 148, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                                .add(genAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                    .add(genAnalysisRemoveGenButton)
                                                    .add(genAnalysisAddGenButton)))))))))
                    .add(genAnalysisPanelLayout.createSequentialGroup()
                        .add(187, 187, 187)
                        .add(runCalLossFactorsButton)))
                .addContainerGap(117, Short.MAX_VALUE))
        );
        genAnalysisPanelLayout.setVerticalGroup(
            genAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(genAnalysisPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(genAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(genAnalysisEdHourLabel)
                    .add(genAnalysisEdHourComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(29, 29, 29)
                .add(genAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(genAnalysisGenBusListComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(genAnalysisGenBusLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(genAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(genAnalysisPanelLayout.createSequentialGroup()
                        .add(genAnalysisAddGenButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(genAnalysisRemoveGenButton))
                    .add(genAnalysisGenScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 82, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(23, 23, 23)
                .add(genLoadDisPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(18, 18, 18)
                .add(runCalLossFactorsButton)
                .add(39, 39, 39))
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

        largeGSFPointsLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        largeGSFPointsLabel.setText("Large GSF Points");

        largeGSFPointsTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        largeGSFPointsTextField.setText("5");

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

        lfResultButtonGroup.add(lfSummaryRadioButton);
        lfSummaryRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        lfSummaryRadioButton.setSelected(true);
        lfSummaryRadioButton.setText("Summary");

        lfResultButtonGroup.add(lfBusStyleRadioButton);
        lfBusStyleRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        lfBusStyleRadioButton.setText("BusStyle");

        lfResultFormatLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        lfResultFormatLabel.setText("LF Rsult Format   [");

        toekn_Label.setFont(new java.awt.Font("Dialog", 0, 12));
        toekn_Label.setText("]");

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
                        .add(lfResultFormatLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(lfSummaryRadioButton)
                        .add(18, 18, 18)
                        .add(lfBusStyleRadioButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(toekn_Label))
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
                                .add(largeGSFPointsLabel)
                                .add(18, 18, 18)
                                .add(largeGSFPointsTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
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
                    .add(largeGSFPointsLabel)
                    .add(largeGSFPointsTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
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
                    .add(lfResultFormatLabel)
                    .add(lfSummaryRadioButton)
                    .add(toekn_Label)
                    .add(lfBusStyleRadioButton))
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
    	if (!saveEditor2Form(errMsg)) {
    		PluginSpringFactory.getEditorDialogUtil().showMsgDialog(this.parent, "Input Data Error", errMsg);
			return false;
    	}
    	
    	if (!this.useCachedVoltCheckBox.isSelected() ) {
    		this.genPVSwingBusVoltCacheList.clear();
    	}
    } catch (Exception e) {
    	IpssLogger.logErr(e);
    	PluginSpringFactory.getEditorDialogUtil().showMsgDialog(this.parent, "Input Data Error", e.toString());
		return false;
    }
    return true;
}

// TODO -----------
// ----------------

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
    final javax.swing.JCheckBox _useCachedVoltCheckBox = this.useCachedVoltCheckBox;
    
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
							runner.getHrLoadflow().getLfAssitGenList()));
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

private void selectLoadDistAPNodeFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectLoadDistAPNodeFileButtonActionPerformed
	JFileChooser fc = getExcelFileChooser();
	if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
		File file = fc.getSelectedFile();
		this.loadDistAPNodeFileTextField.setText(file.getAbsolutePath());
	}
}//GEN-LAST:event_selectLoadDistAPNodeFileButtonActionPerformed

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
		if (this.branchFlowRadioButton.isSelected()) {
			Object rtn = new PTradingDslODMRunner(net)
								.runPTradingAnalysis(ptXml, ptInfoXml, PtAnalysisType.Branch);
			String braId = ptXml.getBranchAnalysis().getBranch().get(0).getBranchId();
			outText = DclfGSFBranchFlow.f(net, braId, (List<DblBusValue>)rtn).toString();
		}
		else if (this.outageSingleRadioButton.isSelected()) {
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

private void outageSingleRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outageSingleRadioButtonActionPerformed
	ipssLogger.info("outageSingleRadioButtonActionPerformed() called");
	disableOutageAnalysisCompoment(false, false, false);
}//GEN-LAST:event_outageSingleRadioButtonActionPerformed

private void outageMultiRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outageMultiRadioButtonActionPerformed
	ipssLogger.info("outageMultiRadioButtonActionPerformed() called");
	disableOutageAnalysisCompoment(true, false, false);
}//GEN-LAST:event_outageMultiRadioButtonActionPerformed

private void outageScheduleRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outageScheduleRadioButtonActionPerformed
	ipssLogger.info("outageScheduleRadioButtonActionPerformed() called");
	disableOutageAnalysisCompoment(false, true, false);
}//GEN-LAST:event_outageScheduleRadioButtonActionPerformed

private void branchFlowRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_branchFlowRadioButtonActionPerformed
	ipssLogger.info("branchFlowRadioButtonActionPerformed() called");
	disableOutageAnalysisCompoment(false, false, true);
}//GEN-LAST:event_branchFlowRadioButtonActionPerformed

private void disableOutageAnalysisCompoment(boolean multi, boolean file, boolean outPoints) {
	this.multiOutageBranchList.setEnabled(multi);
	this.addOutageBranchButton.setEnabled(multi);
	this.removeOutageBranchButton.setEnabled(multi);
	this.outageFileLabel.setEnabled(file);
	this.outageFileSelectButton.setEnabled(file);
	this.outageFileTextField.setEnabled(file);
	this.braFlowOutPointsLabel.setEnabled(outPoints);
	this.braFlowOutPointsTextField.setEnabled(outPoints);
}

private void addOutageBranchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addOutageBranchButtonActionPerformed
	ipssLogger.info("addOutageBranchButtonActionPerformed() called");
	String id = (String)this.braAnalysisBranchListComboBox.getSelectedItem();
	RunUIUtilFunc.addItemJList(multiOutageBranchList, id);
}//GEN-LAST:event_addOutageBranchButtonActionPerformed

private void removeOutageBranchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeOutageBranchButtonActionPerformed
	ipssLogger.info("removeOutageBranchButtonActionPerformed() called");
    RunUIUtilFunc.removeItemJList(this.multiOutageBranchList);
}//GEN-LAST:event_removeOutageBranchButtonActionPerformed

private void outageFileSelectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outageFileSelectButtonActionPerformed
	ipssLogger.info("outageFileSelectButtonActionPerformed() called");
}//GEN-LAST:event_outageFileSelectButtonActionPerformed

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

private void genLoadDistBasecaseRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genLoadDistBasecaseRadioButtonActionPerformed
	ipssLogger.info("genLoadDistBasecaseRadioButtonActionPerformed() called");
	this.genLoadDistAPNodeLabel.setEnabled(false);
	this.genLoadDistAPNodeComboBox.setEnabled(false);
    //setLoadDFactorRadioButton(this.genLoadDistBasecaseRadioButton.isSelected());
}//GEN-LAST:event_genLoadDistBasecaseRadioButtonActionPerformed

private void genLoadDistAPNodeRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genLoadDistAPNodeRadioButtonActionPerformed
	ipssLogger.info("genLoadDistAPNodeRadioButtonActionPerformed() called");
	this.genLoadDistAPNodeLabel.setEnabled(true);
	this.genLoadDistAPNodeComboBox.setEnabled(true);
}//GEN-LAST:event_genLoadDistAPNodeRadioButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel aclfAnalysisPanel;
    private javax.swing.JComboBox aclfEdHourComboBox;
    private javax.swing.JLabel aclfEdHourLabel;
    private javax.swing.JButton addOutageBranchButton;
    private javax.swing.JLabel braAnalysisBranchLabel;
    private javax.swing.JComboBox braAnalysisBranchListComboBox;
    private javax.swing.JLabel braFlowOutPointsLabel;
    private javax.swing.JTextField braFlowOutPointsTextField;
    private javax.swing.JComboBox branchAnalysisEdHourComboBox;
    private javax.swing.JLabel branchAnalysisEdHourLabel;
    private javax.swing.JPanel branchAnalysisPanel;
    private javax.swing.ButtonGroup branchAnalysisTypeButtonGroup;
    private javax.swing.JPanel branchAnalysisTypePanel;
    private javax.swing.JRadioButton branchFlowRadioButton;
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
    private javax.swing.JComboBox genAnalysisEdHourComboBox;
    private javax.swing.JLabel genAnalysisEdHourLabel;
    private javax.swing.JLabel genAnalysisGenBusLabel;
    private javax.swing.JList genAnalysisGenBusList;
    private javax.swing.JComboBox genAnalysisGenBusListComboBox;
    private javax.swing.JScrollPane genAnalysisGenScrollPane;
    private javax.swing.JPanel genAnalysisPanel;
    private javax.swing.JButton genAnalysisRemoveGenButton;
    private javax.swing.JPanel genLoadDisPanel;
    private javax.swing.JComboBox genLoadDistAPNodeComboBox;
    private javax.swing.JLabel genLoadDistAPNodeLabel;
    private javax.swing.JRadioButton genLoadDistAPNodeRadioButton;
    private javax.swing.JRadioButton genLoadDistBasecaseRadioButton;
    private javax.swing.JLabel interfaceFileLabel;
    private javax.swing.JPanel interfaceFilePanel;
    private javax.swing.JTextField interfaceFileTextField;
    private javax.swing.JLabel interfaceLimitLabel;
    private javax.swing.JTextField interfaceLimitTextField;
    private javax.swing.JLabel largeGSFPointsLabel;
    private javax.swing.JTextField largeGSFPointsTextField;
    private javax.swing.JLabel lfAssistGenFileLabel;
    private javax.swing.JButton lfAssistGenFileSelectButton;
    private javax.swing.JTextField lfAssistGenFileTextField;
    private javax.swing.JPanel lfAssistGenPanel;
    private javax.swing.JLabel lfAssistGenQAdjStepsLabel;
    private javax.swing.JTextField lfAssistGenQAdjStespTextField;
    private javax.swing.JLabel lfAssistGenQAdjToleranceLabel;
    private javax.swing.JTextField lfAssistGenQAdjToleranceTextField;
    private javax.swing.JRadioButton lfBusStyleRadioButton;
    private javax.swing.ButtonGroup lfResultButtonGroup;
    private javax.swing.JLabel lfResultFormatLabel;
    private javax.swing.JRadioButton lfSummaryRadioButton;
    private javax.swing.JLabel lfToleranceLabel;
    private javax.swing.JTextField lfToleranceTextField;
    private javax.swing.JLabel loadDistAPNodeFileLabel;
    private javax.swing.JTextField loadDistAPNodeFileTextField;
    private javax.swing.ButtonGroup loadDistButtonGroup;
    private javax.swing.JLabel loadDistThreshholdLabel;
    private javax.swing.JTextField loadDistThreshholdTextField;
    private javax.swing.JPanel loadDistributionPanel;
    private javax.swing.JList multiOutageBranchList;
    private javax.swing.JScrollPane multiOutageBranchScrollPane;
    private javax.swing.JCheckBox outAreaSummaryCheckBox;
    private javax.swing.JCheckBox outBranchViolationCheckBox;
    private javax.swing.JCheckBox outInterfaceViolationCheckBox;
    private javax.swing.JCheckBox outLfResultCheckBox;
    private javax.swing.JCheckBox outVoltViolationCheckBox;
    private javax.swing.JCheckBox outZoneSummaryCheckBox;
    private javax.swing.JPanel outageAnalysisPanel;
    private javax.swing.JLabel outageFileLabel;
    private javax.swing.JButton outageFileSelectButton;
    private javax.swing.JTextField outageFileTextField;
    private javax.swing.JRadioButton outageMultiRadioButton;
    private javax.swing.JRadioButton outageScheduleRadioButton;
    private javax.swing.JRadioButton outageSingleRadioButton;
    private javax.swing.JPanel outputConfigPanel;
    private javax.swing.JTabbedPane pTradingAnalysisTabbedPane;
    private javax.swing.JButton removeOutageBranchButton;
    private javax.swing.JButton runAclfAnalysisButton;
    private javax.swing.JButton runBranchAnalysisButton;
    private javax.swing.JButton runCalLossFactorsButton;
    private javax.swing.JButton selectEdFileButton;
    private javax.swing.JButton selectInterfaceFileButton;
    private javax.swing.JButton selectInterfaceLimitButton;
    private javax.swing.JButton selectLoadDistAPNodeFileButton;
    private javax.swing.JLabel swingAllocAccFactorLabel;
    private javax.swing.JTextField swingAllocAccFactorTextField;
    private javax.swing.JCheckBox swingAllocCheckBox;
    private javax.swing.JLabel swingAllocMaxStepsLabel;
    private javax.swing.JTextField swingAllocMaxStepsTextField;
    private javax.swing.JLabel swingAllocToleraceLabel;
    private javax.swing.JTextField swingAllocToleranceTextField;
    private javax.swing.JComboBox swingAllocZoneComboBox;
    private javax.swing.JLabel swingAllocZoneLabel;
    private javax.swing.JLabel toekn_Label;
    private javax.swing.JCheckBox useCachedVoltCheckBox;
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
	    braFlowOutPointsTextField.setInputVerifier(v);
	    
	    edDateTextField.setInputVerifier(v);
		edGenPFacorTextField.setInputVerifier(v);
		edLossPercentTextField.setInputVerifier(v);
		edLoadPFacorTextField.setInputVerifier(v);

		largeGSFPointsTextField.setInputVerifier(v);
	    lfToleranceTextField.setInputVerifier(v);
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
				if (input == braFlowOutPointsTextField)
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
				else if (input == lfToleranceTextField)
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
