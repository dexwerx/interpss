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
import static org.interpss.CorePluginFunction.DclfGSFBranchInterfaceFlow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFileChooser;

import org.eclipse.emf.ecore.change.util.ChangeRecorder;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.base.BaseJaxbHelper;
import org.ieee.odm.schema.ActivePowerUnitType;
import org.ieee.odm.schema.ActivePowerXmlType;
import org.ieee.odm.schema.BranchRefXmlType;
import org.ieee.odm.schema.FactorUnitType;
import org.ieee.odm.schema.GenLossFactorXmlType;
import org.ieee.odm.schema.LfResultFormatEnumType;
import org.ieee.odm.schema.PTradingEDHourlyAnalysisXmlType;
import org.ieee.odm.schema.PowerTradingInfoXmlType;
import org.ieee.odm.schema.PtAclfAnalysisXmlType;
import org.ieee.odm.schema.PtAnalysisOutputXmlType;
import org.ieee.odm.schema.PtBranchAnalysisEnumType;
import org.ieee.odm.schema.PtBranchAnalysisXmlType;
import org.ieee.odm.schema.PtCaseDataXmlType;
import org.ieee.odm.schema.PtGenLossFactorXmlType;
import org.ieee.odm.schema.PtLoadDistributionXmlType;
import org.ieee.odm.schema.SenAnalysisBusEnumType;
import org.ieee.odm.schema.SenAnalysisBusXmlType;
import org.interpss.datatype.DblBusValue;
import org.interpss.editor.jgraph.GraphSpringFactory;
import org.interpss.editor.jgraph.ui.app.IAppStatus;
import org.interpss.editor.jgraph.ui.edit.IFormDataPanel;
import org.interpss.editor.ui.RunUIUtilFunc;
import org.interpss.editor.ui.util.IpssFileFilter;
import org.interpss.numeric.util.Number2String;
import org.interpss.spring.EditorPluginSpringFactory;
import org.interpss.spring.UISpringFactory;
import org.interpss.ui.SwingInputVerifyUtil;

import com.interpss.common.datatype.Constants;
import com.interpss.common.exp.InterpssException;
import com.interpss.common.exp.InterpssRuntimeException;
import com.interpss.common.func.FunctionAdapter;
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
import com.interpss.pssl.adpter.dclf.OutageScheduleFileProcessor;
import com.interpss.pssl.display.PtAclfOutput;
import com.interpss.pssl.display.SenAnalysisOutput;
import com.interpss.pssl.file.ExcelFileReader;
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

    private Object[] allBranchIdAry = null;
    private Object[] genBusIdAry = null;
    private Object[] loadBusIdAry = null;

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
	    
	     allBranchIdAry = RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.AllBranch).toArray();
	     genBusIdAry = RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.GenBus).toArray();
	     loadBusIdAry = RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.LoadBus).toArray();
	    
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
		
		this.braAnaBranchListComboBox.setModel(new javax.swing.DefaultComboBoxModel(this.allBranchIdAry));
	    this.glFactorGenBusListComboBox.setModel(new javax.swing.DefaultComboBoxModel(genBusIdAry));
	    this.glFactorLoadDistLoadBusComboBox.setModel(new javax.swing.DefaultComboBoxModel(loadBusIdAry));
	}
    
    public void setXmlCaseData(PTradingEDHourlyAnalysisXmlType pt, PowerTradingInfoXmlType ptInfo) {
    	this._ptXml = pt;
    	this._ptInfoXml = ptInfo;
    }

// TODO
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
			else if (braAnalysis.getType() == PtBranchAnalysisEnumType.GEN_CONTRIBUTION)
				this.braAnaGenContibRadioButton.setSelected(true);

		if (this.braAnaOutageSingleRadioButton.isSelected()) {
			this.braAnaOutageSingleRadioButtonActionPerformed(null);
		}
		else if (this.braAnaOutageMultiRadioButton.isSelected()) {
			braAnaOutageMultiRadioButtonActionPerformed(null);
			String[] ary = StringUtil.getIdNameAry(braAnalysis.getBranch(), new FunctionAdapter<Object,String>() {
				@Override public String f(Object value) {return ((BranchRefXmlType)value).getBranchId();	}});
			this.braAnaMultiOutageBranchList.setModel(new javax.swing.DefaultComboBoxModel(ary));    			
	    	this.braAnaOutageFileTextField.setText(braAnalysis.getOutageScheduleFilename());
	    }
		else if (this.braAnaGenContibRadioButton.isSelected()) {
			braAnaGenContibRadioButtonActionPerformed(null);
		}
		
		if (braAnalysis.getBranch() != null && braAnalysis.getBranch().size() > 0) {
			BranchRefXmlType branch = braAnalysis.getBranch().get(0);
			this.braAnaBranchListComboBox.setSelectedItem(branch.getBranchId());
		}
	
		// Gen Loss Factor
		// ===============
		
		PtGenLossFactorXmlType glfactor = this._ptXml.getGenLossFactorAnalysis();
		if (glfactor != null) {
			this.glFactorEdHourComboBox.setSelectedItem(glfactor.getHour() == null? "12:00" : aclfXml.getHour());
			if (glfactor.getGenLossFactors().size() > 0) {
				String[] sAry = StringUtil.getIdNameAry(glfactor.getGenLossFactors(), new FunctionAdapter<Object,String>() {
					@Override public String f(Object value) { return ((GenLossFactorXmlType)value).getInjectBus().get(0).getBusId(); } });
				this.glFactorGenBusList.setModel(new javax.swing.DefaultComboBoxModel(sAry));   
				
				GenLossFactorXmlType gen = glfactor.getGenLossFactors().get(0);
				if (gen.getWithdrawBusType() == SenAnalysisBusEnumType.SINGLE_BUS) {
					this.glFactorLoadDistBusRadioButton.setSelected(true);
					glFactorLoadDistBusRadioButtonActionPerformed(null);
					String id = gen.getWithdrawBus().get(0).getBusId();
					this.glFactorLoadDistLoadBusComboBox.setSelectedItem(id);
				}
				else if (gen.getWithdrawBusType() == SenAnalysisBusEnumType.LOAD_DISTRIBUTION) {
					this.glFactorLoadDistBasecaseRadioButton.setSelected(true);
					this.glFactorLoadDistBasecaseRadioButtonActionPerformed(null);
				}
				else {
					this.glFactorLoadDistUserRadioButton.setSelected(true);
					this.glFactorLoadDistUserRadioButtonActionPerformed(null);
					this.glFactorLoadDistUserFileTextField.setText(gen.getUserFilename());
				}
			}		
		}
		
		// output panel
		// ============
		
		PtAnalysisOutputXmlType outOpt = this._ptXml.getOutputOption();
		if (outOpt.getLargeGSFPoints() != null)
			outLargeGSFPointsTextField.setText(
					outOpt.getLargeGSFPoints().toString());
		if (outOpt.getBranchFlowOutPoints() != null)
			this.outFlowOutPointsTextField.setText(
				outOpt.getBranchFlowOutPoints().toString());
		if (outOpt.getViolationThreshhold() != null)
			this.outViolationThreshholdTextField.setText(
				Number2String.toStr(outOpt.getViolationThreshhold().getValue(), "0.0"));
		if (outOpt.getOutageShiftedFlowThreshhold() != null)
			this.outOutageShiftedFlowThreshholdTextField.setText(
				Number2String.toStr(outOpt.getOutageShiftedFlowThreshhold().getValue(), "0.0"));
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
		saveOutageAnalysis(errMsg, run);
		//saveGenAnalysis(errMsg, run);
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

	public boolean saveOutageAnalysis(Vector<String> errMsg, boolean run) {
		boolean noError = true;
		
		if (this._ptXml.getBranchAnalysis() == null) {
			this._ptXml.setBranchAnalysis(odmObjFactory.createPtBranchAnalysisXmlType());
		}
		PtBranchAnalysisXmlType braAnalysis = this._ptXml.getBranchAnalysis();
		
		braAnalysis.setHour((String)this.braAnaEdHourComboBox.getSelectedItem());
		braAnalysis.setType(this.braAnaOutageSingleRadioButton.isSelected()? PtBranchAnalysisEnumType.SINGLE_BRANCH_OUTAGE :
			(this.braAnaOutageMultiRadioButton.isSelected()? PtBranchAnalysisEnumType.MULTI_BRANCH_OUTAGE :
					PtBranchAnalysisEnumType.GEN_CONTRIBUTION));

		braAnalysis.getBranch().clear();
		if (this.braAnaOutageSingleRadioButton.isSelected() ||
				this.braAnaGenContibRadioButton.isSelected()) {
			String braId = (String)this.braAnaBranchListComboBox.getSelectedItem();
			BranchRefXmlType outage = BaseJaxbHelper.creatBranchRef(braAnalysis.getBranch());
			RunUIUtilFunc.setBranchIdInfo(outage, braId);
		}
		else if (this.braAnaOutageMultiRadioButton.isSelected()) {
	    	String[] braIdAry = RunUIUtilFunc.getJListItemAry(this.braAnaMultiOutageBranchList);
	    	if (braIdAry.length == 0 && run) {
	    		errMsg.add("Please select at least one outage branch for multi-branch outage analysis");
	    		return false;
	    	}
	    	for (String braId : braIdAry) {
				BranchRefXmlType outage = BaseJaxbHelper.creatBranchRef(braAnalysis.getBranch());
				RunUIUtilFunc.setBranchIdInfo(outage, braId);
	    	}
	    	if (this.braAnaOutageFileTextField.getText() != null)
	    		braAnalysis.setOutageScheduleFilename(this.braAnaOutageFileTextField.getText());
		}
		
	    return noError;
	}
	
	public boolean saveEditor2LossFactor(Vector<String> errMsg, boolean run) {
		boolean noError = true;

		if (this._ptXml.getGenLossFactorAnalysis() == null)
			this._ptXml.setGenLossFactorAnalysis(odmObjFactory.createPtGenLossFactorXmlType());
		PtGenLossFactorXmlType glfactor = this._ptXml.getGenLossFactorAnalysis();
		
		glfactor.setHour((String)this.glFactorEdHourComboBox.getSelectedItem());
		glfactor.getGenLossFactors().clear();
		int size = this.glFactorGenBusList.getModel().getSize();
		if (size == 0 && run) {
			errMsg.add("Please select at least one generator for gen loss factor calculation");
			noError = false;
		}
		else {
			for (int i = 0; i < size; i++) {
				String id = (String)this.glFactorGenBusList.getModel().getElementAt(i);
				GenLossFactorXmlType gen = odmObjFactory.createGenLossFactorXmlType();
				glfactor.getGenLossFactors().add(gen);
				gen.setInjectBusType(SenAnalysisBusEnumType.SINGLE_BUS);
				SenAnalysisBusXmlType bus = odmObjFactory.createSenAnalysisBusXmlType();
				bus.setBusId(id);
				gen.getInjectBus().add(bus);
			
				if (this.glFactorLoadDistBusRadioButton.isSelected()) {
					gen.setWithdrawBusType(SenAnalysisBusEnumType.SINGLE_BUS);
					id = (String)this.glFactorLoadDistLoadBusComboBox.getSelectedItem();
					bus = odmObjFactory.createSenAnalysisBusXmlType();
					bus.setBusId(id);
					gen.getWithdrawBus().add(bus);
				}
				else if (this.glFactorLoadDistBasecaseRadioButton.isSelected()) {
					gen.setWithdrawBusType(SenAnalysisBusEnumType.LOAD_DISTRIBUTION);
				}
				else {
					gen.setWithdrawBusType(SenAnalysisBusEnumType.USER_FILE);
					gen.setUserFilename(this.glFactorLoadDistUserFileTextField.getText());
				}
			}
		}

		return noError;
	}
	

	public boolean saveOutputConfig(Vector<String> errMsg) {
		boolean noError = true;

		if (this._ptXml.getOutputOption() == null) {
			this._ptXml.setOutputOption(odmObjFactory.createPtAnalysisOutputXmlType());
		}
		PtAnalysisOutputXmlType outOpt = this._ptXml.getOutputOption();
		
		outOpt.setLargeGSFPoints(
					new Integer(outLargeGSFPointsTextField.getText()).intValue());
	    
		if (outOpt.getViolationThreshhold() == null)
	    	outOpt.setViolationThreshhold(odmObjFactory.createFactorXmlType());
	    outOpt.getViolationThreshhold().setValue(
	    		new Double(this.outViolationThreshholdTextField.getText()).intValue());
	    outOpt.getViolationThreshhold().setUnit(FactorUnitType.PERCENT);
	    
		if (outOpt.getOutageShiftedFlowThreshhold() == null)
	    	outOpt.setOutageShiftedFlowThreshhold(odmObjFactory.createActivePowerXmlType());
	    outOpt.getOutageShiftedFlowThreshhold().setValue(
	    		new Double(this.outOutageShiftedFlowThreshholdTextField.getText()));
	    outOpt.getOutageShiftedFlowThreshhold().setUnit(ActivePowerUnitType.MW);

	    outOpt.setBranchFlowOutPoints(
	    		new Integer(this.outFlowOutPointsTextField.getText()).intValue());

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
        glFactorLoadDistButtonGroup = new javax.swing.ButtonGroup();
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
        lfAnalysisPanel = new javax.swing.JPanel();
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
        runDclfAnalysisButton = new javax.swing.JButton();
        run24HrDclfAnalysisButton = new javax.swing.JButton();
        branchAnalysisPanel = new javax.swing.JPanel();
        braAnaEdHourLabel = new javax.swing.JLabel();
        braAnaEdHourComboBox = new javax.swing.JComboBox();
        braAnaBranchListComboBox = new javax.swing.JComboBox();
        braAnaBranchLabel = new javax.swing.JLabel();
        braAnaOutageSingleRadioButton = new javax.swing.JRadioButton();
        braAnaOutageMultiRadioButton = new javax.swing.JRadioButton();
        braAnaGenContibRadioButton = new javax.swing.JRadioButton();
        braAnaOutageAnalysisPanel = new javax.swing.JPanel();
        braAnaMultiOutageBranchScrollPane = new javax.swing.JScrollPane();
        braAnaMultiOutageBranchList = new javax.swing.JList();
        braAnaAddOutageBranchButton = new javax.swing.JButton();
        braAnaRemoveOutageBranchButton = new javax.swing.JButton();
        braAnaOutageFileLabel = new javax.swing.JLabel();
        braAnaOutageFileTextField = new javax.swing.JTextField();
        braAnaOutageFileSelectButton = new javax.swing.JButton();
        braAnaImportOutageBranchButton = new javax.swing.JButton();
        runBranchAnalysisButton = new javax.swing.JButton();
        genLossFactorPanel = new javax.swing.JPanel();
        glFactorEdHourLabel = new javax.swing.JLabel();
        glFactorEdHourComboBox = new javax.swing.JComboBox();
        glFactorGenBusLabel = new javax.swing.JLabel();
        glFactorGenBusListComboBox = new javax.swing.JComboBox();
        glFactorAddGenButton = new javax.swing.JButton();
        glFactorRemoveGenButton = new javax.swing.JButton();
        glFactorGenScrollPane = new javax.swing.JScrollPane();
        glFactorGenBusList = new javax.swing.JList();
        glFactorLoadDisPanel = new javax.swing.JPanel();
        glFactorLoadDistBusRadioButton = new javax.swing.JRadioButton();
        glFactorLoadDistBasecaseRadioButton = new javax.swing.JRadioButton();
        glFactorLoadDistUserRadioButton = new javax.swing.JRadioButton();
        glFactorLoadDistLoadBusLabel = new javax.swing.JLabel();
        glFactorLoadDistLoadBusComboBox = new javax.swing.JComboBox();
        glFactorLoadDistUserFileTextField = new javax.swing.JTextField();
        glFactorLoadDistUserFileButton = new javax.swing.JButton();
        runGLFactorButton = new javax.swing.JButton();
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
        outLfResultCheckBox = new javax.swing.JCheckBox();
        outLfSummaryRadioButton = new javax.swing.JRadioButton();
        outLfBusStyleRadioButton = new javax.swing.JRadioButton();
        outLfResultFormatLabel = new javax.swing.JLabel();
        outToekn_Label = new javax.swing.JLabel();
        outLargeGSFPointsLabel = new javax.swing.JLabel();
        outLargeGSFPointsTextField = new javax.swing.JTextField();
        outViolationThreshholdLabel = new javax.swing.JLabel();
        outViolationThreshholdTextField = new javax.swing.JTextField();
        outFlowOutPointsLabel = new javax.swing.JLabel();
        outFlowOutPointsTextField = new javax.swing.JTextField();
        outOutageShiftedFlowThreshholdLabel = new javax.swing.JLabel();
        outOutageShiftedFlowThreshholdTextField = new javax.swing.JTextField();

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
                .addContainerGap(19, Short.MAX_VALUE))
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
        loadDistThreshholdLabel.setText("Min Load Threshhold (Mw)   ");

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
                .add(67, 67, 67)
                .add(loadDistThreshholdTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 51, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(156, Short.MAX_VALUE))
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
            .add(interfaceFilePanelLayout.createSequentialGroup()
                .addContainerGap()
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
                .addContainerGap(19, Short.MAX_VALUE))
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

        lfAssistGenPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "MustRun Gen", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N

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
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                    .add(lfAssistGenPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(caseDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, interfaceFilePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, loadDistPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, edFilePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .add(12, 12, 12))
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
                .add(23, 23, 23))
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
        runAclfAnalysisButton.setText("Aclf Analysis");
        runAclfAnalysisButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runAclfAnalysisButtonActionPerformed(evt);
            }
        });

        runDclfAnalysisButton.setFont(new java.awt.Font("Dialog", 0, 12));
        runDclfAnalysisButton.setText("Dclf (SelHr)");
        runDclfAnalysisButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runDclfAnalysisButtonActionPerformed(evt);
            }
        });

        run24HrDclfAnalysisButton.setFont(new java.awt.Font("Dialog", 0, 12));
        run24HrDclfAnalysisButton.setText("Dclf (24Hr)");
        run24HrDclfAnalysisButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                run24HrDclfAnalysisButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout lfAnalysisPanelLayout = new org.jdesktop.layout.GroupLayout(lfAnalysisPanel);
        lfAnalysisPanel.setLayout(lfAnalysisPanelLayout);
        lfAnalysisPanelLayout.setHorizontalGroup(
            lfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lfAnalysisPanelLayout.createSequentialGroup()
                .add(lfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lfAnalysisPanelLayout.createSequentialGroup()
                        .add(26, 26, 26)
                        .add(lfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(swingAllocCheckBox)
                            .add(lfAnalysisPanelLayout.createSequentialGroup()
                                .add(32, 32, 32)
                                .add(lfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(swingAllocZoneLabel)
                                    .add(swingAllocMaxStepsLabel)
                                    .add(swingAllocToleraceLabel))
                                .add(29, 29, 29)
                                .add(lfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(swingAllocZoneComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 71, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(lfAnalysisPanelLayout.createSequentialGroup()
                                        .add(lfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                            .add(swingAllocMaxStepsTextField)
                                            .add(swingAllocToleranceTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE))
                                        .add(33, 33, 33)
                                        .add(swingAllocAccFactorLabel)
                                        .add(18, 18, 18)
                                        .add(swingAllocAccFactorTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))))
                    .add(lfAnalysisPanelLayout.createSequentialGroup()
                        .add(lfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lfAnalysisPanelLayout.createSequentialGroup()
                                .add(154, 154, 154)
                                .add(aclfEdHourLabel))
                            .add(lfAnalysisPanelLayout.createSequentialGroup()
                                .add(58, 58, 58)
                                .add(aclfToleranceLabel)
                                .add(29, 29, 29)
                                .add(aclfToleranceTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 59, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .add(26, 26, 26)
                        .add(lfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(aclfUseCachedVoltCheckBox)
                            .add(aclfEdHourComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 77, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(lfAnalysisPanelLayout.createSequentialGroup()
                        .add(85, 85, 85)
                        .add(runAclfAnalysisButton)
                        .add(18, 18, 18)
                        .add(runDclfAnalysisButton)
                        .add(18, 18, 18)
                        .add(run24HrDclfAnalysisButton)))
                .addContainerGap(69, Short.MAX_VALUE))
        );
        lfAnalysisPanelLayout.setVerticalGroup(
            lfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, lfAnalysisPanelLayout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .add(lfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(aclfEdHourComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(aclfEdHourLabel))
                .add(18, 18, 18)
                .add(lfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(aclfToleranceTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(aclfToleranceLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(aclfUseCachedVoltCheckBox))
                .add(18, 18, 18)
                .add(swingAllocCheckBox)
                .add(lfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lfAnalysisPanelLayout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(swingAllocZoneComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(lfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(swingAllocMaxStepsTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(swingAllocMaxStepsLabel)
                            .add(swingAllocAccFactorLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(swingAllocAccFactorTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(lfAnalysisPanelLayout.createSequentialGroup()
                        .add(13, 13, 13)
                        .add(swingAllocZoneLabel)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(lfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(swingAllocToleranceTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(swingAllocToleraceLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(108, 108, 108)
                .add(lfAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(runAclfAnalysisButton)
                    .add(runDclfAnalysisButton)
                    .add(run24HrDclfAnalysisButton))
                .add(42, 42, 42))
        );

        pTradingAnalysisTabbedPane.addTab("LF Analysis", lfAnalysisPanel);

        braAnaEdHourLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        braAnaEdHourLabel.setText("ED Hour");

        braAnaEdHourComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        braAnaEdHourComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0:00", "1:00", "2:00", "3:00", "4:00", "5:00", "6:00", "7:00", "8:00", "9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00", " " }));

        braAnaBranchListComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        braAnaBranchListComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        braAnaBranchLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        braAnaBranchLabel.setText("Outage Branch");

        branchAnalysisTypeButtonGroup.add(braAnaOutageSingleRadioButton);
        braAnaOutageSingleRadioButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        braAnaOutageSingleRadioButton.setText("Single");
        braAnaOutageSingleRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                braAnaOutageSingleRadioButtonActionPerformed(evt);
            }
        });

        branchAnalysisTypeButtonGroup.add(braAnaOutageMultiRadioButton);
        braAnaOutageMultiRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        braAnaOutageMultiRadioButton.setText("Multiple");
        braAnaOutageMultiRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                braAnaOutageMultiRadioButtonActionPerformed(evt);
            }
        });

        branchAnalysisTypeButtonGroup.add(braAnaGenContibRadioButton);
        braAnaGenContibRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        braAnaGenContibRadioButton.setText("GenContrib");
        braAnaGenContibRadioButton.setToolTipText("Calculate gen contribution to a branch flow");
        braAnaGenContibRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                braAnaGenContibRadioButtonActionPerformed(evt);
            }
        });

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

        braAnaImportOutageBranchButton.setFont(new java.awt.Font("Dialog", 0, 12));
        braAnaImportOutageBranchButton.setText("Import");
        braAnaImportOutageBranchButton.setEnabled(false);
        braAnaImportOutageBranchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                braAnaImportOutageBranchButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout braAnaOutageAnalysisPanelLayout = new org.jdesktop.layout.GroupLayout(braAnaOutageAnalysisPanel);
        braAnaOutageAnalysisPanel.setLayout(braAnaOutageAnalysisPanelLayout);
        braAnaOutageAnalysisPanelLayout.setHorizontalGroup(
            braAnaOutageAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(braAnaOutageAnalysisPanelLayout.createSequentialGroup()
                .add(braAnaOutageAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(braAnaOutageAnalysisPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(braAnaOutageFileLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(braAnaOutageFileTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(braAnaOutageFileSelectButton))
                    .add(braAnaOutageAnalysisPanelLayout.createSequentialGroup()
                        .add(33, 33, 33)
                        .add(braAnaMultiOutageBranchScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 249, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(braAnaOutageAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(braAnaImportOutageBranchButton)
                            .add(braAnaRemoveOutageBranchButton)
                            .add(braAnaAddOutageBranchButton))))
                .addContainerGap())
        );
        braAnaOutageAnalysisPanelLayout.setVerticalGroup(
            braAnaOutageAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(braAnaOutageAnalysisPanelLayout.createSequentialGroup()
                .add(braAnaOutageAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(braAnaOutageAnalysisPanelLayout.createSequentialGroup()
                        .add(braAnaAddOutageBranchButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(braAnaRemoveOutageBranchButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(braAnaImportOutageBranchButton)
                        .add(18, 18, 18))
                    .add(braAnaOutageAnalysisPanelLayout.createSequentialGroup()
                        .add(braAnaMultiOutageBranchScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)))
                .add(braAnaOutageAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(braAnaOutageFileLabel)
                    .add(braAnaOutageFileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(braAnaOutageFileSelectButton)))
        );

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
                        .add(88, 88, 88)
                        .add(braAnaBranchLabel)
                        .add(27, 27, 27)
                        .add(braAnaBranchListComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 184, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(branchAnalysisPanelLayout.createSequentialGroup()
                        .add(32, 32, 32)
                        .add(braAnaOutageAnalysisPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(branchAnalysisPanelLayout.createSequentialGroup()
                        .add(103, 103, 103)
                        .add(braAnaOutageSingleRadioButton)
                        .add(18, 18, 18)
                        .add(braAnaOutageMultiRadioButton)
                        .add(18, 18, 18)
                        .add(braAnaGenContibRadioButton))
                    .add(branchAnalysisPanelLayout.createSequentialGroup()
                        .add(195, 195, 195)
                        .add(runBranchAnalysisButton)))
                .addContainerGap(38, Short.MAX_VALUE))
        );
        branchAnalysisPanelLayout.setVerticalGroup(
            branchAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, branchAnalysisPanelLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(branchAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(braAnaEdHourComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(braAnaEdHourLabel))
                .add(18, 18, 18)
                .add(branchAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(braAnaOutageSingleRadioButton)
                    .add(braAnaOutageMultiRadioButton)
                    .add(braAnaGenContibRadioButton))
                .add(13, 13, 13)
                .add(branchAnalysisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(braAnaBranchListComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(braAnaBranchLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(braAnaOutageAnalysisPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(58, 58, 58)
                .add(runBranchAnalysisButton)
                .add(184, 184, 184))
        );

        pTradingAnalysisTabbedPane.addTab("Outage Analysis", branchAnalysisPanel);

        glFactorEdHourLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        glFactorEdHourLabel.setText("ED Hour");

        glFactorEdHourComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        glFactorEdHourComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0:00", "1:00", "2:00", "3:00", "4:00", "5:00", "6:00", "7:00", "8:00", "9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00", " " }));

        glFactorGenBusLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        glFactorGenBusLabel.setText("Gen Bus");

        glFactorGenBusListComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        glFactorGenBusListComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        glFactorAddGenButton.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        glFactorAddGenButton.setText("Add");
        glFactorAddGenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                glFactorAddGenButtonActionPerformed(evt);
            }
        });

        glFactorRemoveGenButton.setFont(new java.awt.Font("Dialog", 0, 10));
        glFactorRemoveGenButton.setText("Remove");
        glFactorRemoveGenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                glFactorRemoveGenButtonActionPerformed(evt);
            }
        });

        glFactorGenBusList.setFont(new java.awt.Font("Dialog", 0, 12));
        glFactorGenScrollPane.setViewportView(glFactorGenBusList);

        glFactorLoadDisPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Load Distribution", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N

        glFactorLoadDistButtonGroup.add(glFactorLoadDistBusRadioButton);
        glFactorLoadDistBusRadioButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        glFactorLoadDistBusRadioButton.setText("Load Bus");
        glFactorLoadDistBusRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                glFactorLoadDistBusRadioButtonActionPerformed(evt);
            }
        });

        glFactorLoadDistButtonGroup.add(glFactorLoadDistBasecaseRadioButton);
        glFactorLoadDistBasecaseRadioButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        glFactorLoadDistBasecaseRadioButton.setSelected(true);
        glFactorLoadDistBasecaseRadioButton.setText("Basecase");
        glFactorLoadDistBasecaseRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                glFactorLoadDistBasecaseRadioButtonActionPerformed(evt);
            }
        });

        glFactorLoadDistButtonGroup.add(glFactorLoadDistUserRadioButton);
        glFactorLoadDistUserRadioButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        glFactorLoadDistUserRadioButton.setText("User");
        glFactorLoadDistUserRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                glFactorLoadDistUserRadioButtonActionPerformed(evt);
            }
        });

        glFactorLoadDistLoadBusLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        glFactorLoadDistLoadBusLabel.setText("Load Bus");
        glFactorLoadDistLoadBusLabel.setEnabled(false);

        glFactorLoadDistLoadBusComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        glFactorLoadDistLoadBusComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        glFactorLoadDistLoadBusComboBox.setEnabled(false);

        glFactorLoadDistUserFileTextField.setColumns(25);
        glFactorLoadDistUserFileTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        glFactorLoadDistUserFileTextField.setText("User defined file ...");
        glFactorLoadDistUserFileTextField.setEnabled(false);

        glFactorLoadDistUserFileButton.setFont(new java.awt.Font("Dialog", 0, 10));
        glFactorLoadDistUserFileButton.setText("Select");
        glFactorLoadDistUserFileButton.setEnabled(false);
        glFactorLoadDistUserFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                glFactorLoadDistUserFileButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout glFactorLoadDisPanelLayout = new org.jdesktop.layout.GroupLayout(glFactorLoadDisPanel);
        glFactorLoadDisPanel.setLayout(glFactorLoadDisPanelLayout);
        glFactorLoadDisPanelLayout.setHorizontalGroup(
            glFactorLoadDisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(glFactorLoadDisPanelLayout.createSequentialGroup()
                .add(glFactorLoadDisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, glFactorLoadDisPanelLayout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(glFactorLoadDistUserFileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 248, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 28, Short.MAX_VALUE)
                        .add(glFactorLoadDistUserFileButton))
                    .add(glFactorLoadDisPanelLayout.createSequentialGroup()
                        .add(42, 42, 42)
                        .add(glFactorLoadDisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, glFactorLoadDistBusRadioButton)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, glFactorLoadDistLoadBusLabel))
                        .add(21, 21, 21)
                        .add(glFactorLoadDistLoadBusComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 105, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, glFactorLoadDisPanelLayout.createSequentialGroup()
                .addContainerGap(137, Short.MAX_VALUE)
                .add(glFactorLoadDistBasecaseRadioButton)
                .add(16, 16, 16)
                .add(glFactorLoadDistUserRadioButton)
                .add(82, 82, 82))
        );
        glFactorLoadDisPanelLayout.setVerticalGroup(
            glFactorLoadDisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(glFactorLoadDisPanelLayout.createSequentialGroup()
                .add(glFactorLoadDisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(glFactorLoadDistUserRadioButton)
                    .add(glFactorLoadDistBasecaseRadioButton)
                    .add(glFactorLoadDistBusRadioButton))
                .add(14, 14, 14)
                .add(glFactorLoadDisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(glFactorLoadDistLoadBusComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(glFactorLoadDistLoadBusLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(glFactorLoadDisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(glFactorLoadDistUserFileButton)
                    .add(glFactorLoadDistUserFileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        runGLFactorButton.setFont(new java.awt.Font("Dialog", 0, 12));
        runGLFactorButton.setText("Calculate ");
        runGLFactorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runGLFactorButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout genLossFactorPanelLayout = new org.jdesktop.layout.GroupLayout(genLossFactorPanel);
        genLossFactorPanel.setLayout(genLossFactorPanelLayout);
        genLossFactorPanelLayout.setHorizontalGroup(
            genLossFactorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(genLossFactorPanelLayout.createSequentialGroup()
                .add(167, 167, 167)
                .add(glFactorEdHourLabel)
                .add(26, 26, 26)
                .add(glFactorEdHourComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 77, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(179, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, genLossFactorPanelLayout.createSequentialGroup()
                .addContainerGap(215, Short.MAX_VALUE)
                .add(runGLFactorButton)
                .add(194, 194, 194))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, genLossFactorPanelLayout.createSequentialGroup()
                .addContainerGap(62, Short.MAX_VALUE)
                .add(genLossFactorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(genLossFactorPanelLayout.createSequentialGroup()
                        .add(70, 70, 70)
                        .add(glFactorGenScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 148, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(genLossFactorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(glFactorRemoveGenButton)
                            .add(glFactorAddGenButton)))
                    .add(glFactorLoadDisPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(genLossFactorPanelLayout.createSequentialGroup()
                        .add(91, 91, 91)
                        .add(glFactorGenBusLabel)
                        .add(18, 18, 18)
                        .add(glFactorGenBusListComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(53, 53, 53))
        );
        genLossFactorPanelLayout.setVerticalGroup(
            genLossFactorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, genLossFactorPanelLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(genLossFactorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(glFactorEdHourComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(glFactorEdHourLabel))
                .add(18, 18, 18)
                .add(genLossFactorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(glFactorGenBusListComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(glFactorGenBusLabel))
                .add(18, 18, 18)
                .add(genLossFactorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(genLossFactorPanelLayout.createSequentialGroup()
                        .add(glFactorAddGenButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(glFactorRemoveGenButton))
                    .add(glFactorGenScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 65, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(glFactorLoadDisPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(39, 39, 39)
                .add(runGLFactorButton)
                .add(159, 159, 159))
        );

        pTradingAnalysisTabbedPane.addTab("Gen Loss Factor", genLossFactorPanel);

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

        outLargeGSFPointsLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        outLargeGSFPointsLabel.setText("Large GSF Output Points");

        outLargeGSFPointsTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        outLargeGSFPointsTextField.setText("5");

        outViolationThreshholdLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        outViolationThreshholdLabel.setText("Flow Violation Threshhold (%)");

        outViolationThreshholdTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        outViolationThreshholdTextField.setText("90");

        outFlowOutPointsLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        outFlowOutPointsLabel.setText("Gen Contrib Analysis Output Points");

        outFlowOutPointsTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        outFlowOutPointsTextField.setText("5");

        outOutageShiftedFlowThreshholdLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        outOutageShiftedFlowThreshholdLabel.setText("Outage ShiftedFlow Threshhold (Mw)");

        outOutageShiftedFlowThreshholdTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        outOutageShiftedFlowThreshholdTextField.setText("0.1");

        org.jdesktop.layout.GroupLayout outputConfigPanelLayout = new org.jdesktop.layout.GroupLayout(outputConfigPanel);
        outputConfigPanel.setLayout(outputConfigPanelLayout);
        outputConfigPanelLayout.setHorizontalGroup(
            outputConfigPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(outputConfigPanelLayout.createSequentialGroup()
                .add(outputConfigPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(outputConfigPanelLayout.createSequentialGroup()
                        .add(58, 58, 58)
                        .add(outputConfigPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(outFlowOutPointsLabel)
                            .add(outViolationThreshholdLabel)
                            .add(outOutageShiftedFlowThreshholdLabel)
                            .add(outLargeGSFPointsLabel))
                        .add(41, 41, 41)
                        .add(outputConfigPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(outLargeGSFPointsTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(outFlowOutPointsTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(outViolationThreshholdTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(outOutageShiftedFlowThreshholdTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(outputConfigPanelLayout.createSequentialGroup()
                        .add(37, 37, 37)
                        .add(outputConfigPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(outZoneSummaryCheckBox)
                            .add(outAreaSummaryCheckBox)
                            .add(outLfResultCheckBox)
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
                                .add(voltLowerLimitLabel)
                                .add(18, 18, 18)
                                .add(voltLowerLimitTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(outputConfigPanelLayout.createSequentialGroup()
                                .add(44, 44, 44)
                                .add(outLfResultFormatLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(outLfSummaryRadioButton)
                                .add(18, 18, 18)
                                .add(outLfBusStyleRadioButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(outToekn_Label)))))
                .addContainerGap(123, Short.MAX_VALUE))
        );
        outputConfigPanelLayout.setVerticalGroup(
            outputConfigPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(outputConfigPanelLayout.createSequentialGroup()
                .add(23, 23, 23)
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
                .add(outLfResultCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(outputConfigPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(outLfResultFormatLabel)
                    .add(outLfSummaryRadioButton)
                    .add(outToekn_Label)
                    .add(outLfBusStyleRadioButton))
                .add(18, 18, 18)
                .add(outputConfigPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(outLargeGSFPointsLabel)
                    .add(outLargeGSFPointsTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(outputConfigPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(outFlowOutPointsLabel)
                    .add(outFlowOutPointsTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(13, 13, 13)
                .add(outputConfigPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(outViolationThreshholdLabel)
                    .add(outViolationThreshholdTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(outputConfigPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(outOutageShiftedFlowThreshholdLabel)
                    .add(outOutageShiftedFlowThreshholdTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(41, 41, 41))
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
    		EditorPluginSpringFactory.getEditorDialogUtil().showMsgDialog(this.parent, "Input Data Error", errMsg);
			return false;
    	}
    	
    	if (!this.aclfUseCachedVoltCheckBox.isSelected() ) {
    		this.genPVSwingBusVoltCacheList.clear();
    	}
    } catch (Exception e) {
    	IpssLogger.logErr(e);
    	EditorPluginSpringFactory.getEditorDialogUtil().showMsgDialog(this.parent, "Input Data Error", e.toString());
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
 *  LF Analysis
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
					"Run PowerTrading Aclf Analysis ...", "Run Analysis");
			// Book marked the AclfNetwork object
			ChangeRecorder recorderBaseNet = new ChangeRecorder(net);	
			try {
				PTradingDslODMRunner runner = new PTradingDslODMRunner(net);
				runner.runPtAclfAnalysis(ptXml, ptInfoXml, genPVSwingBusList);
				UISpringFactory.getOutputTextDialog("BaseCase Aclf Analysis Results")
					.display(PtAclfOutput.outHourLoaflowResult(net, ptXml, ptInfoXml, 
							runner.getHrLoadflow().getLfAssistGenList(),
							runner.getHrLoadflow().getAclfQAdjusttor().getMustRunGenList()));
			} catch (InterpssException e) {
				EditorPluginSpringFactory.getEditorDialogUtil().showMsgDialog(parent, "Analysis Error", e.toString());
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


private void runDclfAnalysisButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runDclfAnalysisButtonActionPerformed
	ipssLogger.info("runDclfAnalysisButtonActionPerformed");
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
					"Run Dclf Analysis ...", "Run Analysis");

			// Book marked the AclfNetwork object
			ChangeRecorder recorderBaseNet = new ChangeRecorder(net);	

			String hr = ptXml.getAclfAnalysis().getHour();
			String outText = "";
			try {
				Object rtn = new PTradingDslODMRunner(net)
										.runPTradingAnalysis(ptXml, ptInfoXml, PtAnalysisType.Dclf);
				outText = "\n  Hour : " + hr + "\n" + "  ============\n\n" + 
						      rtn.toString();
			} catch (Exception e) {
				EditorPluginSpringFactory.getEditorDialogUtil().showMsgDialog(parent, "Analysis Error", e.toString());
				recorderBaseNet.endRecording().apply();
				return;
			}
			
			// roll-back AclfNet to the bookmarked point
			recorderBaseNet.endRecording().apply();		
			
			UISpringFactory.getOutputTextDialog("Dclf Analysis Results")
				.display(outText);   	

			appStatus.busyStop("Run Dclf Analysis finished");			
		}
	}.start();
}//GEN-LAST:event_runDclfAnalysisButtonActionPerformed

private void run24HrDclfAnalysisButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_run24HrDclfAnalysisButtonActionPerformed
	ipssLogger.info("run24HrDclfAnalysisButtonActionPerformed");
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
					"Run 24Hr Dclf Analysis ...", "Run Analysis");

			String outText = "";
			try {
				Object rtn = new PTradingDslODMRunner(net)
										.runPTradingAnalysis(ptXml, ptInfoXml, PtAnalysisType.Dclf24Hr);
				outText = rtn.toString();
			} catch (Exception e) {
				EditorPluginSpringFactory.getEditorDialogUtil().showMsgDialog(parent, "Analysis Error", e.toString());
				return;
			}
			
			UISpringFactory.getOutputTextDialog("24 Hr Dclf Analysis Results")
				.display(outText);   	

			appStatus.busyStop("Run Dclf Analysis finished");			
		}
	}.start();
}//GEN-LAST:event_run24HrDclfAnalysisButtonActionPerformed


//TODO
/* 888888888888888888888888
 *  Outage Analysis
 8888888888888888888888888888*/

private void runBranchAnalysisButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runBranchAnalysisButtonActionPerformed
	ipssLogger.info("Line outage analysis button selected");
	
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
					"Run branch analysis ...", "Run Analysis");

			// Book marked the AclfNetwork object
			ChangeRecorder recorderBaseNet = new ChangeRecorder(net);	

			String outText = "";

			PtBranchAnalysisXmlType braAnalysis = ptXml.getBranchAnalysis();
			String hr = braAnalysis.getHour();
			try {
				if (braAnaGenContibRadioButton.isSelected()) {
					Object rtn = new PTradingDslODMRunner(net)
										.runPTradingAnalysis(ptXml, ptInfoXml, PtAnalysisType.BranchOutage);
					String braId = ptXml.getBranchAnalysis().getBranch().get(0).getBranchId();
					outText = "\n  Hour : " + hr + "\n" + "  ============\n\n\n" +
							  DclfGSFBranchInterfaceFlow.f(net, braId, (List<DblBusValue>)rtn, false).toString();
				}
				else {
					Object rtn = new PTradingDslODMRunner(net)
						.runPTradingAnalysis(ptXml, ptInfoXml, PtAnalysisType.BranchOutage);
					outText = "\n  Hour : " + hr + "\n" + "  ============\n\n\n"; 
					int cnt = 0;
					for (BranchRefXmlType braXml : ptXml.getBranchAnalysis().getBranch())
						outText += "Outage Branch [" + ++cnt + "]: " + braXml.getBranchId() + "\n";
					outText += "\n\n" + rtn.toString();
				}
			} catch (Exception e) {
				e.printStackTrace();
				EditorPluginSpringFactory.getEditorDialogUtil().showMsgDialog(parent, "Analysis Error", e.toString());
				recorderBaseNet.endRecording().apply();
				return;
			}
			
			// roll-back AclfNet to the bookmarked point
			recorderBaseNet.endRecording().apply();		
			
			UISpringFactory.getOutputTextDialog("Outage Analysis Results")
				.display(outText);   	
			
			appStatus.busyStop("Run Outage Analysis finished");			
		}
	}.start();
	
}//GEN-LAST:event_runBranchAnalysisButtonActionPerformed

private void braAnaOutageSingleRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_braAnaOutageSingleRadioButtonActionPerformed
	ipssLogger.info("outageSingleRadioButtonActionPerformed() called");
	disableOutageAnalysisCompoment(false, false);
	this.braAnaBranchLabel.setText("Outage Branch");
}//GEN-LAST:event_braAnaOutageSingleRadioButtonActionPerformed

private void braAnaOutageMultiRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_braAnaOutageMultiRadioButtonActionPerformed
	ipssLogger.info("outageMultiRadioButtonActionPerformed() called");
	disableOutageAnalysisCompoment(true, true);
	this.braAnaBranchLabel.setText("Outage Branch");
}//GEN-LAST:event_braAnaOutageMultiRadioButtonActionPerformed

private void braAnaGenContibRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_braAnaGenContibRadioButtonActionPerformed
	ipssLogger.info("branchFlowRadioButtonActionPerformed() called");
	disableOutageAnalysisCompoment(false, false);
	this.braAnaBranchLabel.setText("Monitor Branch");
}//GEN-LAST:event_braAnaGenContibRadioButtonActionPerformed

private void disableOutageAnalysisCompoment(boolean multi, boolean file) {
	this.braAnaMultiOutageBranchList.setEnabled(multi);
	this.braAnaAddOutageBranchButton.setEnabled(multi);
	this.braAnaRemoveOutageBranchButton.setEnabled(multi);
	this.braAnaOutageFileLabel.setEnabled(file);
	this.braAnaOutageFileSelectButton.setEnabled(file);
	this.braAnaOutageFileTextField.setEnabled(file);
	this.braAnaImportOutageBranchButton.setEnabled(file);
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
	JFileChooser fc = getExcelFileChooser();
	if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
		File file = fc.getSelectedFile();
		this.braAnaOutageFileTextField.setText(file.getAbsolutePath());
	}
}//GEN-LAST:event_braAnaOutageFileSelectButtonActionPerformed

private void braAnaImportOutageBranchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_braAnaImportOutageBranchButtonActionPerformed
	ipssLogger.info("braAnaImportOutageBranchButtonActionPerformed() called");
	try {
		AclfModelParser parser = new AclfModelParser();
		ExcelFileReader reader = new ExcelFileReader(this.braAnaOutageFileTextField.getText(), 0, "Outage schedule");
		OutageScheduleFileProcessor proc = new OutageScheduleFileProcessor(parser);		
		reader.processFile(proc);		
		String dateStr = this._ptXml.getCaseData().getEdFile().getDate();
		String[] braIdAry = proc.getOutageBranch(dateStr);
		braAnaMultiOutageBranchList.setModel(new javax.swing.DefaultComboBoxModel(braIdAry));
	} catch (InterpssException e) {
		ipssLogger.severe(e.toString());
		braAnaMultiOutageBranchList.setModel(new javax.swing.DefaultComboBoxModel(new String[] {"File import error"}));
	}
	
}//GEN-LAST:event_braAnaImportOutageBranchButtonActionPerformed

//TODO
/* 888888888888888888888888
*  Gen loss factor
8888888888888888888888888888*/

private void runGLFactorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runGLFactorButtonActionPerformed
    ipssLogger.info("runGLFactorButtonActionPerformed() called");
    
	this.parent.setAlwaysOnTop(false);
    
	Vector<String> errMsg = new Vector<String>();    	
	saveEditor2LossFactor(errMsg, true);
	if (errMsg.size() > 0) {
		EditorPluginSpringFactory.getEditorDialogUtil().showMsgDialog(this.parent, "Data Error", errMsg);
		return;
	}

	final PTradingEDHourlyAnalysisXmlType ptXml = this._ptXml;
	
	new Thread() {
		public void run() {
			IAppStatus appStatus = GraphSpringFactory.getIpssGraphicEditor().getAppStatus();
			appStatus.busyStart(Constants.StatusBusyIndicatorPeriod,
					"Run GenLossFactor Analysis ...", "Run PtAnalysis");

			String outText = "";
			try {
				new PTradingDslODMRunner(_simuCtx.getAclfNet()).runPtGenLossFactorAnalysis(ptXml, _ptInfoXml);
		    	outText = SenAnalysisOutput.outLossFactor(ptXml.getGenLossFactorAnalysis().getGenLossFactors(),  _ptInfoXml).toString(); 
			} catch (InterpssException e) {
				ipssLogger.severe(e.toString());
				outText = e.toString();
			}		
			
			UISpringFactory.getOutputTextDialog("LODF Calculation Results").display(outText, 60, 15);   
			
			appStatus.busyStop("Run LODF GSF Analysis finished");			
		}
	}.start();
    
}//GEN-LAST:event_runGLFactorButtonActionPerformed

private void glFactorAddGenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_glFactorAddGenButtonActionPerformed
    ipssLogger.info("glFactorAddGenButtonActionPerformed() called");
    String id = (String)this.glFactorGenBusListComboBox.getSelectedItem();
    RunUIUtilFunc.addItemJList(this.glFactorGenBusList, id);
}//GEN-LAST:event_glFactorAddGenButtonActionPerformed

private void glFactorRemoveGenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_glFactorRemoveGenButtonActionPerformed
    ipssLogger.info("glFactorRemoveGenButtonActionPerformed() called");
    RunUIUtilFunc.removeItemJList(this.glFactorGenBusList);
}//GEN-LAST:event_glFactorRemoveGenButtonActionPerformed

private void glFactorLoadDistBusRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_glFactorLoadDistBusRadioButtonActionPerformed
    ipssLogger.info("glFactorLoadDistBusRadioButtonActionPerformed() called");
    this.glFactorLoadDistLoadBusLabel.setEnabled(true);
    this.glFactorLoadDistLoadBusComboBox.setEnabled(true);
    this.glFactorLoadDistUserFileTextField.setEnabled(false);
    this.glFactorLoadDistUserFileButton.setEnabled(false);
}//GEN-LAST:event_glFactorLoadDistBusRadioButtonActionPerformed

private void glFactorLoadDistBasecaseRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_glFactorLoadDistBasecaseRadioButtonActionPerformed
    ipssLogger.info("glFactorLoadDistBasecaseRadioButtonActionPerformed() called");
    this.glFactorLoadDistLoadBusLabel.setEnabled(false);
    this.glFactorLoadDistLoadBusComboBox.setEnabled(false);
    this.glFactorLoadDistUserFileTextField.setEnabled(false);
    this.glFactorLoadDistUserFileButton.setEnabled(false);
}//GEN-LAST:event_glFactorLoadDistBasecaseRadioButtonActionPerformed

private void glFactorLoadDistUserRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_glFactorLoadDistUserRadioButtonActionPerformed
    ipssLogger.info("glFactorLoadDistUserRadioButtonActionPerformed() called");
    this.glFactorLoadDistLoadBusLabel.setEnabled(false);
    this.glFactorLoadDistLoadBusComboBox.setEnabled(false);
    this.glFactorLoadDistUserFileTextField.setEnabled(true);
    this.glFactorLoadDistUserFileButton.setEnabled(true);
}//GEN-LAST:event_glFactorLoadDistUserRadioButtonActionPerformed

private void glFactorLoadDistUserFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_glFactorLoadDistUserFileButtonActionPerformed
    ipssLogger.info("glFactorLoadDistUserFileButtonActionPerformed() called");
    JFileChooser fc = getExcelFileChooser();
    if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        File file = fc.getSelectedFile();
        this.glFactorLoadDistUserFileTextField.setText(file.getAbsolutePath());
    }
}//GEN-LAST:event_glFactorLoadDistUserFileButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    private javax.swing.JRadioButton braAnaGenContibRadioButton;
    private javax.swing.JButton braAnaImportOutageBranchButton;
    private javax.swing.JList braAnaMultiOutageBranchList;
    private javax.swing.JScrollPane braAnaMultiOutageBranchScrollPane;
    private javax.swing.JPanel braAnaOutageAnalysisPanel;
    private javax.swing.JLabel braAnaOutageFileLabel;
    private javax.swing.JButton braAnaOutageFileSelectButton;
    private javax.swing.JTextField braAnaOutageFileTextField;
    private javax.swing.JRadioButton braAnaOutageMultiRadioButton;
    private javax.swing.JRadioButton braAnaOutageSingleRadioButton;
    private javax.swing.JButton braAnaRemoveOutageBranchButton;
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
    private javax.swing.JPanel genLossFactorPanel;
    private javax.swing.JButton glFactorAddGenButton;
    private javax.swing.JComboBox glFactorEdHourComboBox;
    private javax.swing.JLabel glFactorEdHourLabel;
    private javax.swing.JLabel glFactorGenBusLabel;
    private javax.swing.JList glFactorGenBusList;
    private javax.swing.JComboBox glFactorGenBusListComboBox;
    private javax.swing.JScrollPane glFactorGenScrollPane;
    private javax.swing.JPanel glFactorLoadDisPanel;
    private javax.swing.JRadioButton glFactorLoadDistBasecaseRadioButton;
    private javax.swing.JRadioButton glFactorLoadDistBusRadioButton;
    private javax.swing.ButtonGroup glFactorLoadDistButtonGroup;
    private javax.swing.JComboBox glFactorLoadDistLoadBusComboBox;
    private javax.swing.JLabel glFactorLoadDistLoadBusLabel;
    private javax.swing.JButton glFactorLoadDistUserFileButton;
    private javax.swing.JTextField glFactorLoadDistUserFileTextField;
    private javax.swing.JRadioButton glFactorLoadDistUserRadioButton;
    private javax.swing.JButton glFactorRemoveGenButton;
    private javax.swing.JLabel interfaceFileLabel;
    private javax.swing.JPanel interfaceFilePanel;
    private javax.swing.JTextField interfaceFileTextField;
    private javax.swing.JLabel interfaceLimitLabel;
    private javax.swing.JTextField interfaceLimitTextField;
    private javax.swing.JPanel lfAnalysisPanel;
    private javax.swing.JLabel lfAssistGenFileLabel;
    private javax.swing.JButton lfAssistGenFileSelectButton;
    private javax.swing.JTextField lfAssistGenFileTextField;
    private javax.swing.JPanel lfAssistGenPanel;
    private javax.swing.JLabel lfAssistGenQAdjStepsLabel;
    private javax.swing.JTextField lfAssistGenQAdjStespTextField;
    private javax.swing.JLabel lfAssistGenQAdjToleranceLabel;
    private javax.swing.JTextField lfAssistGenQAdjToleranceTextField;
    private javax.swing.ButtonGroup lfResultButtonGroup;
    private javax.swing.JPanel loadDistPanel;
    private javax.swing.JLabel loadDistThreshholdLabel;
    private javax.swing.JTextField loadDistThreshholdTextField;
    private javax.swing.JCheckBox outAreaSummaryCheckBox;
    private javax.swing.JCheckBox outBranchViolationCheckBox;
    private javax.swing.JLabel outFlowOutPointsLabel;
    private javax.swing.JTextField outFlowOutPointsTextField;
    private javax.swing.JCheckBox outInterfaceViolationCheckBox;
    private javax.swing.JLabel outLargeGSFPointsLabel;
    private javax.swing.JTextField outLargeGSFPointsTextField;
    private javax.swing.JRadioButton outLfBusStyleRadioButton;
    private javax.swing.JCheckBox outLfResultCheckBox;
    private javax.swing.JLabel outLfResultFormatLabel;
    private javax.swing.JRadioButton outLfSummaryRadioButton;
    private javax.swing.JLabel outOutageShiftedFlowThreshholdLabel;
    private javax.swing.JTextField outOutageShiftedFlowThreshholdTextField;
    private javax.swing.JLabel outToekn_Label;
    private javax.swing.JLabel outViolationThreshholdLabel;
    private javax.swing.JTextField outViolationThreshholdTextField;
    private javax.swing.JCheckBox outVoltViolationCheckBox;
    private javax.swing.JCheckBox outZoneSummaryCheckBox;
    private javax.swing.JPanel outputConfigPanel;
    private javax.swing.JTabbedPane pTradingAnalysisTabbedPane;
    private javax.swing.JButton run24HrDclfAnalysisButton;
    private javax.swing.JButton runAclfAnalysisButton;
    private javax.swing.JButton runBranchAnalysisButton;
    private javax.swing.JButton runDclfAnalysisButton;
    private javax.swing.JButton runGLFactorButton;
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
	    outFlowOutPointsTextField.setInputVerifier(v);
	    
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
				if (input == outFlowOutPointsTextField)
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
