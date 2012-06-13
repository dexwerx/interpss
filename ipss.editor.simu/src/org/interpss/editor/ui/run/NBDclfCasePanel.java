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

import static com.interpss.common.util.IpssLogger.ipssLogger;
import static com.interpss.common.util.NetUtilFunc.ToBranchId;
import static org.ieee.odm.ODMObjectFactory.odmObjFactory;

import java.io.File;
import java.util.List;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFileChooser;

import org.ieee.odm.model.base.BaseJaxbHelper;
import org.ieee.odm.model.ext.ipss.AggregateGenHelper;
import org.ieee.odm.model.ext.ipss.IpssScenarioHelper;
import org.ieee.odm.model.ext.ipss.IpssAnalysisCaseFunc;
import org.ieee.odm.schema.BranchRefXmlType;
import org.ieee.odm.schema.BranchShiftFactorXmlType;
import org.ieee.odm.schema.DclfBranchSensitivityXmlType;
import org.ieee.odm.schema.DclfSenAnalysisXmlType;
import org.ieee.odm.schema.FlowInterfaceRefXmlType;
import org.ieee.odm.schema.InterfaceShiftFactorXmlType;
import org.ieee.odm.schema.LODFMonitorBranchXmlType;
import org.ieee.odm.schema.LODFMonitorInterfaceXmlType;
import org.ieee.odm.schema.LODFOutageEnumType;
import org.ieee.odm.schema.LineOutageDFactorXmlType;
import org.ieee.odm.schema.PowerTradingInfoXmlType;
import org.ieee.odm.schema.SenAnalysisBusEnumType;
import org.ieee.odm.schema.SenAnalysisBusXmlType;
import org.ieee.odm.schema.SenAnalysisEnumType;
import org.ieee.odm.schema.SenAnalysisOutOptionXmlType;
import org.interpss.datatype.DblBranchValue;
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
import com.interpss.common.exp.InterpssRuntimeException;
import com.interpss.common.func.FunctionAdapter;
import com.interpss.common.msg.IpssMessage;
import com.interpss.common.msg.IpssMsgListener;
import com.interpss.common.util.NetUtilFunc;
import com.interpss.common.util.StringUtil;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.flow.FlowInterface;
import com.interpss.pssl.common.PSSLException;
import com.interpss.pssl.display.SenAnalysisOutput;
import com.interpss.pssl.odm.DclfDslODMRunner;
import com.interpss.pssl.odm.DclfDslODMRunner.DclfAnalysisType;
import com.interpss.pssl.simu.IpssPTrading;
import com.interpss.pssl.simu.IpssPTrading.DclfAlgorithmDSL;
import com.interpss.simu.SimuContext;

public class NBDclfCasePanel extends javax.swing.JPanel implements IFormDataPanel, IpssMsgListener {
	private static final long serialVersionUID = 1;
	private JDialog parent;
	
    private SimuContext _simuCtx = null;

//	private ODMModelParser odmParser = new ODMModelParser();
//    public void setODMParser(ODMModelParser parser) { 	this.odmParser = parser;   }

    private DclfSenAnalysisXmlType _senXml = null;
    private PowerTradingInfoXmlType _ptInfoXml = null;

    private Object[] allBranchIdAry = null;
    private Object[] genBusIdAry = null;
    private Object[] loadBusIdAry = null;
    
    /** Creates new form NBAclfCasePanel */
    public NBDclfCasePanel(JDialog parent) {
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
		ipssLogger.info("NBAclfCasePanel init() called");
		
	    _simuCtx = (SimuContext)simuCtx;
	    
	    this.runDclfTabbedPane.setSelectedIndex(1);
	    
    	if (_simuCtx != null) {   
    	     allBranchIdAry = RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.AllBranch).toArray();
    	     genBusIdAry = RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.GenBus).toArray();
    	     loadBusIdAry = RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.LoadBus).toArray();

    	     this.gsfGenBusComboBox.setModel(new javax.swing.DefaultComboBoxModel(genBusIdAry));
    	     this.gsfMonitorBranchListComboBox.setModel(new javax.swing.DefaultComboBoxModel(this.allBranchIdAry));

    	     this.lodfBranchListComboBox.setModel(new javax.swing.DefaultComboBoxModel(this.allBranchIdAry));
    	     this.lodfMonitorBranchListComboBox.setModel(new javax.swing.DefaultComboBoxModel(this.allBranchIdAry));
    		
 //   	     this.genAnalysisGenBusListComboBox.setModel(new javax.swing.DefaultComboBoxModel(genBusIdAry));
 //   	     this.genLoadDistLoadBusComboBox.setModel(new javax.swing.DefaultComboBoxModel(loadBusIdAry));
    	}
     }
   
    public void setXmlCaseData(DclfSenAnalysisXmlType senXml, PowerTradingInfoXmlType ptInfo) {
    	this._senXml = senXml;
    	this._ptInfoXml = ptInfo;
    }    
    
 // TODO
    /**
	*	Set form data to the editor
	*
	* @return false if there is any problem
	*/
	public boolean setForm2Editor() {
		ipssLogger.info("NBAclfCasePanel setForm2Editor() called");
		
		// load FlowInterface if exists
		if (!RunUIUtilFunc.loadFlowInterfaceFiles(this._simuCtx.getAclfNet(), this._ptInfoXml))
			return false;

		if (this._simuCtx.getAclfNet().isFlowInterfaceLoaded()) {
			setFlowInterfaceStatus(true);
			Object[] ary = RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.FlowInterface).toArray();
			this.gsfMonitorInterfaceListComboBox.setModel(new javax.swing.DefaultComboBoxModel(ary));
			this.lodfMonitorInterfaceListComboBox.setModel(new javax.swing.DefaultComboBoxModel(ary));
		}
		else {
			setFlowInterfaceStatus(false);
		}
		
		if (!setConfig2Editor())
			return false;
		
		if (!setGSF2Editor())
			return false;

		if (!setLODF2Editor())
			return false;
		
//		if (!setLossFactor2Editor())
//			return false;

		return true;
	}
	
	private void setFlowInterfaceStatus(boolean b) {
		this.gsfMonitorInterfaceListComboBox.setEnabled(b);
		this.gsfMonitorAddInterfaceButton.setEnabled(b);
		this.gsfAllInterfaceGSFButton.setEnabled(b);
		this.gsfLargetInterfaceGSFButton.setEnabled(b);
	}

	public boolean setConfig2Editor() {
		ipssLogger.info("NBAclfCasePanel setOutConfig2Editor() called");
		
		interfaceFileTextField.setText(this._ptInfoXml.getInterfaceFilename());
		
		// set PowerTrading info to the configuration panel
		if (this._ptInfoXml.getLoadDist() != null) {
			if (this._ptInfoXml.getLoadDist().getMinLoadForDistFactor() != null)
				this.loadDistThreshholdTextField.setText(
						Number2String.toStr(this._ptInfoXml.getLoadDist().getMinLoadForDistFactor().getValue(), "#0.0"));

			// load and configure AP Node info if exists
			if (this._ptInfoXml.getLoadDist().getAggregateGen() != null) {
				this.genAPNodeFileTextField.setText(
						this._ptInfoXml.getLoadDist().getAggregateGen().getAggregatePricingFilename());
				if ( RunUIUtilFunc.loadAPNodeFile(_ptInfoXml)) {
					ipssLogger.info("Aggregate gen group loaded, " + _ptInfoXml.getLoadDist().getAggregateGen().getAggregatePricingFilename());
					this.gsfGenAPNodeRadioButton.setEnabled(true);
				}
				else {
					// when there is a problem loading the APNode info, disable the APNode radio button
					this._ptInfoXml.getLoadDist().setAggregateGen(null);
					this.gsfGenAPNodeRadioButton.setEnabled(false);
				}
			}
			else
				this.gsfGenAPNodeRadioButton.setEnabled(false);
		}	
		else 
			IpssAnalysisCaseFunc.setDefaultPtInfo(this._ptInfoXml);

		SenAnalysisOutOptionXmlType outConfig = this._senXml.getOutOption();
		if (outConfig != null) {
			this.outAllBranchPointsTextField.setText(outConfig.getAllBranchPoints().toString());
			this.outAllInterfacePointsTextField.setText(outConfig.getAllInterfacePoints().toString());
		}
		
		return true;
	}

	public boolean setGSF2Editor() {
		ipssLogger.info("NBAclfCasePanel setGSF2Editor() called");

		// set gen bus list
		if (this._senXml.getGenShiftFactor().size() > 0) {
			DclfBranchSensitivityXmlType gsf =  this._senXml.getGenShiftFactor().get(0);
			if (gsf.getInjectBusType() == SenAnalysisBusEnumType.AP_NODE) {
				this.gsfGenAPNodeRadioButtonActionPerformed(null);
				this.gsfGenAPNodeRadioButton.setSelected(true);
				this.gsfGenBusComboBox.setSelectedItem(gsf.getApNodeId());
			}
			else {
				this.gsfGenBusRadioButtonActionPerformed(null);
				this.gsfGenBusRadioButton.setSelected(true);
			}

			// withdraw bus info are the same for all gsf
			if (gsf.getWithdrawBusType() == SenAnalysisBusEnumType.SINGLE_BUS) {
				this.gsfLoadSingleBusRadioButtonActionPerformed(null);
				this.gsfLoadSingleBusRadioButton.setSelected(true);
				String busId = gsf.getWithdrawBus().get(0).getBusId();
				this.gsfLoadBusComboBox.setSelectedItem(busId);
			}
			else if (gsf.getWithdrawBusType() == SenAnalysisBusEnumType.USER_FILE) {
				this.gsfLoadUserDefinedRadioButtonActionPerformed(null);
				this.gsfLoadUserDefinedRadioButton.setSelected(true);
				this.gsfLoadUserFileTextField.setText(gsf.getUserFilename());
			}
			else {
				this.gsfLoadBasecaseRadioButtonActionPerformed(null);
				this.gsfLoadBasecaseRadioButton.setSelected(true);
				this.gsfLoadUserFileTextField.setText(gsf.getUserFilename());
			}
				
			// branch/interface info are the same for all gsf
			String[] braInfIdAry = new String[gsf.getBranchSFactor().size()+gsf.getInterfaceSFactor().size()];
			int cnt = 0;
			for ( BranchShiftFactorXmlType sf : gsf.getBranchSFactor()) {
				BranchRefXmlType branch = sf.getBranch();
				braInfIdAry[cnt++] = "b:" + ToBranchId.f(branch.getFromBusId(), branch.getToBusId(), branch.getCircuitId());
			}
			for ( InterfaceShiftFactorXmlType inf : gsf.getInterfaceSFactor()) {
				FlowInterfaceRefXmlType in = inf.getInterface();
				braInfIdAry[cnt++] = "i:" + in.getInterfaceId();
			}
	    	gsfMonitorBranchList.setModel(new javax.swing.DefaultComboBoxModel(braInfIdAry));    	
		}

		return true;
	}

	public boolean setLODF2Editor() {
		ipssLogger.info("NBAclfCasePanel setLODF2Editor() called");
		
		LineOutageDFactorXmlType lodf = 
			this._senXml.getLineOutageDFactor() != null && this._senXml.getLineOutageDFactor().size() > 0?
				this._senXml.getLineOutageDFactor().get(0) : null;

		if ( lodf != null && lodf.getOutageBranch() != null ) {
			if (lodf.getOutageType() == LODFOutageEnumType.SINGLE_BRANCH) { 
				this.lodfSingleTypeRadioButton.setSelected(true);
				String braId = lodf.getOutageBranch().get(0).getBranchId();
				this.lodfBranchListComboBox.setSelectedItem(braId);
				setLodfComponentStatus(false);
			}
			else {
				this.lodfMultiTypeRadioButton.setSelected(true);
				String[] ary = StringUtil.getIdNameAry(lodf.getOutageBranch(), new FunctionAdapter<Object,String>() {
					@Override public String f(Object value) { return ((BranchRefXmlType)value).getBranchId(); }});
				this.lodfMultiOutageBranchList.setModel(new javax.swing.DefaultComboBoxModel(ary));    			
				setLodfComponentStatus(true);
			}
		}

		if ( lodf != null && lodf.getMonitorBranch() != null) {
			String[] ary1 = StringUtil.getIdNameAry(lodf.getMonitorBranch(), new FunctionAdapter<Object,String>() {
				@Override public String f(Object value) {return "b:" + ((LODFMonitorBranchXmlType)value).getBranch().getBranchId();	}});
			String[] ary2 = StringUtil.getIdNameAry(lodf.getMonitorInterface(), new FunctionAdapter<Object,String>() {
				@Override public String f(Object value) {return "i:" + ((LODFMonitorInterfaceXmlType)value).getInterface().getInterfaceId(); }});
			lodfMonitorBranchInterfaceList.setModel(new javax.swing.DefaultComboBoxModel(StringUtil.merge(ary1, ary2)));    			
		}
		
		return true;
	}
/*	
	public boolean setLossFactor2Editor() {
		if (this._senXml.getGenLossFactors().size() > 0) {
			String[] sAry = StringUtil.getIdNameAry(this._senXml.getGenLossFactors(), new FunctionAdapter<Object,String>() {
				@Override public String f(Object value) { return ((GenLossFactorXmlType)value).getInjectBus().get(0).getBusId(); } });
			this.genAnalysisGenBusList.setModel(new javax.swing.DefaultComboBoxModel(sAry));   
			
			GenLossFactorXmlType gen = this._senXml.getGenLossFactors().get(0);
			if (gen.getWithdrawBusType() == SenAnalysisBusEnumType.SINGLE_BUS) {
				this.genLoadDistBusRadioButton.setSelected(true);
				genLoadDistBusRadioButtonActionPerformed(null);
				String id = gen.getWithdrawBus().get(0).getBusId();
				this.genLoadDistLoadBusComboBox.setSelectedItem(id);
			}
			else if (gen.getWithdrawBusType() == SenAnalysisBusEnumType.LOAD_DISTRIBUTION) {
				this.genLoadDistBasecaseRadioButton.setSelected(true);
				this.genLoadDistBasecaseRadioButtonActionPerformed(null);
			}
			else {
				this.genLoadDistUserRadioButton.setSelected(true);
				this.genLoadDistUserRadioButtonActionPerformed(null);
				this.genLoadDistUserFileTextField.setText(gen.getUserFilename());
			}
		}		
		return true;
	}
*/
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
	
	/**
	 * 
	 * 
	 * @param errMsg
	 * @param run boolean indicating running the case.
	 * @return
	 * @throws Exception
	 */
	public boolean saveEditor2Form(Vector<String> errMsg, boolean run) throws Exception {
		ipssLogger.info("NBAclfCasePanel saveEditor2Form() called");
		
		saveEditor2OutConfig(errMsg);

		saveEditor2GSF(errMsg, run);

		saveEditor2LODF(errMsg, run);

//		saveEditor2LossFactor(errMsg, run);

		return errMsg.size() == 0;
	}

	public boolean saveEditor2OutConfig(Vector<String> errMsg) {
		ipssLogger.info("NBAclfCasePanel saveEditor2OutConfig() called");
		//IpssScenarioHelper helper = new IpssScenarioHelper();
		
		// save interface filename
		if (interfaceFileTextField.getText() != null && !interfaceFileTextField.getText().equals(""))
			this._ptInfoXml.setInterfaceFilename(interfaceFileTextField.getText());
		
		
		// set PowerTrading info to the configuration panel
		// save load threshhold info
		this._ptInfoXml.getLoadDist().getMinLoadForDistFactor().setValue(
				new Double(this.loadDistThreshholdTextField.getText()));

		// load and configure AP Node info if exists
		if (this._ptInfoXml.getLoadDist().getAggregateGen() == null)
			this._ptInfoXml.getLoadDist().setAggregateGen(odmObjFactory.createAggregateGenXmlType());
		this._ptInfoXml.getLoadDist().getAggregateGen().setAggregatePricingFilename(this.genAPNodeFileTextField.getText());
		// clear the APNode info, since it should be stored
		if (this._ptInfoXml.getLoadDist().getAggregateGen().getApGroup() != null)
			this._ptInfoXml.getLoadDist().getAggregateGen().getApGroup().clear();
		
		// save output info
		SenAnalysisOutOptionXmlType outConfig = this._senXml.getOutOption();
		if (outConfig == null)
			outConfig = IpssScenarioHelper.createSenAnalysisOutConfig(this._senXml);
		
		outConfig.setAllBranchPoints(new Integer(this.outAllBranchPointsTextField.getText()));
		outConfig.setAllInterfacePoints(new Integer(this.outAllInterfacePointsTextField.getText()));
		
		return true;
	}

	public boolean saveEditor2GSF(Vector<String> errMsg, boolean run) {
		ipssLogger.info("NBAclfCasePanel saveEditor2GSF() called");
		
		this._senXml.getGenShiftFactor().clear();
    	String[] injectIdAry = null;
		if (this.gsfGenAPNodeRadioButton.isSelected()) {
	    	injectIdAry = new String[1];
			String id = (String)this.gsfGenBusComboBox.getSelectedItem();
	    	if (id == null && run) {
	    		errMsg.add("Please select an AP Node for GSF analysis");
	    		return false;
	    	}
			injectIdAry[0] = id;
		}
		else {
	    	injectIdAry = RunUIUtilFunc.getJListItemAry(this.gsfGenBusList);
	    	if (injectIdAry.length == 0 && run) {
	    		errMsg.add("Please select at least one gen bus for GSF analysis");
	    		return false;
	    	}
		}

		//IpssScenarioHelper helper = new IpssScenarioHelper();
		for (String selId : injectIdAry) {
			DclfBranchSensitivityXmlType gsf = IpssScenarioHelper.createGSF(this._senXml);
			gsf.setSenType(SenAnalysisEnumType.P_ANGLE);
			
			/*
			 * set Injection bus
			 */
			if (this.gsfGenAPNodeRadioButton.isSelected()) {
				// the APNode id is stored in the injectIdAry
				gsf.setInjectBusType(SenAnalysisBusEnumType.AP_NODE);
				gsf.setApNodeId(selId);
			}
			else {
				gsf.setInjectBusType(SenAnalysisBusEnumType.SINGLE_BUS);
				SenAnalysisBusXmlType bus = IpssScenarioHelper.createSenAnalysisBus(gsf.getInjectBus());
				bus.setBusId(selId);
			}
			
			// although the withdraw bus info and the monitor branch/interface info
			// are the same for all gen buses, they need to be set at each gsf recard
			// level
			
			/*
			 * set withdraw bus
			 */
			if (this.gsfLoadSingleBusRadioButton.isSelected()) { 
				gsf.setWithdrawBusType(SenAnalysisBusEnumType.SINGLE_BUS);
				gsf.getWithdrawBus().clear();
				SenAnalysisBusXmlType bus = IpssScenarioHelper.createSenAnalysisBus(gsf.getWithdrawBus());
				String busId = (String)this.gsfLoadBusComboBox.getSelectedItem();
				bus.setBusId(busId);
			}
			else if (this.gsfLoadBasecaseRadioButton.isSelected()) 
				gsf.setWithdrawBusType(SenAnalysisBusEnumType.LOAD_DISTRIBUTION);
			else {
				gsf.setWithdrawBusType(SenAnalysisBusEnumType.USER_FILE);
				gsf.setUserFilename(this.gsfLoadUserFileTextField.getText());
			}
			
			/*
			 * set Monitor branches/interfaces
			 */
			gsf.getBranchSFactor().clear();
	    	for (String id : RunUIUtilFunc.getJListItemAry(gsfMonitorBranchList)) {
	    		if (id != null) {
	        		if (id.startsWith("b:")) { // branch
	        			String braId = id.substring(2);
	    	    		BranchShiftFactorXmlType sf = IpssScenarioHelper.createBranchSFactor(gsf.getBranchSFactor());
	    	    		BranchRefXmlType line = odmObjFactory.createBranchRefXmlType();
	    				sf.setBranch(line);
	    				RunUIUtilFunc.setBranchIdInfo(line, braId);				
	        		}
	        		else {  // interface
	        			String braId = id.substring(2);
	    	    		InterfaceShiftFactorXmlType sf = IpssScenarioHelper.createInterfaceSFactor(gsf.getInterfaceSFactor());
	    	    		FlowInterfaceRefXmlType inf = odmObjFactory.createFlowInterfaceRefXmlType();
	    				sf.setInterface(inf);
	    				inf.setInterfaceId(braId);
	        		}
	    		}
	    	}
    	}
		
		return true;
	}

	public boolean saveEditor2LODF(Vector<String> errMsg, boolean run) {
		ipssLogger.info("NBAclfCasePanel saveEditor2LODF() called");
		//IpssScenarioHelper helper = new IpssScenarioHelper();
		
		this._senXml.getLineOutageDFactor().clear();
		LineOutageDFactorXmlType lodf = IpssScenarioHelper.createLODF(this._senXml);

		if (this.lodfMultiTypeRadioButton.isSelected()) {
			lodf.setOutageType(LODFOutageEnumType.MULTI_BRANCH);
	    	String[] outageBranchIdAry = RunUIUtilFunc.getJListItemAry(this.lodfMultiOutageBranchList);
	    	if (outageBranchIdAry.length == 0 && run) {
	    		errMsg.add("Select outage branch and add to the outage branch list");
	    	}
	    	for (String braId : outageBranchIdAry) {
		    	BranchRefXmlType outage = BaseJaxbHelper.creatBranchRef(lodf.getOutageBranch());
				RunUIUtilFunc.setBranchIdInfo(outage, braId);
	    	}
		}
		else {  // single outage branch
			lodf.setOutageType(LODFOutageEnumType.SINGLE_BRANCH);
	    	String braId = (String)this.lodfBranchListComboBox.getSelectedItem();
	    	if (braId == null && run) {
	    		errMsg.add("Select an outage branch");
	    	}
	    	BranchRefXmlType outage = BaseJaxbHelper.creatBranchRef(lodf.getOutageBranch());
			RunUIUtilFunc.setBranchIdInfo(outage, braId);
		}

		String[] braIntfIdAry = RunUIUtilFunc.getJListItemAry(this.lodfMonitorBranchInterfaceList);
		for (String id : braIntfIdAry) {
    		if (id.startsWith("b:")) { // branch
    			String braId = id.substring(2);
    			BranchRefXmlType monitor = IpssScenarioHelper.createMonitorBranch(lodf.getMonitorBranch());
    			RunUIUtilFunc.setBranchIdInfo(monitor, braId);
    		}
    		else {  // interface
    			String braId = id.substring(2);
    			FlowInterfaceRefXmlType intf = IpssScenarioHelper.createMonitorInterface(lodf.getMonitorInterface());
				intf.setInterfaceId(braId);
    		}
		}
		
		return true;
	}
/*	
	public boolean saveEditor2LossFactor(Vector<String> errMsg, boolean run) {
		boolean noError = true;

		this._senXml.getGenLossFactors().clear();
		
		int size = this.genAnalysisGenBusList.getModel().getSize();
		if (size == 0 && run) {
			errMsg.add("Please select at least one generator for gen loss factor calculation");
			noError = false;
		}
		else {
			for (int i = 0; i < size; i++) {
				String id = (String)this.genAnalysisGenBusList.getModel().getElementAt(i);
				GenLossFactorXmlType gen = odmObjFactory.createGenLossFactorXmlType();
				this._senXml.getGenLossFactors().add(gen);
				gen.setInjectBusType(SenAnalysisBusEnumType.SINGLE_BUS);
				SenAnalysisBusXmlType bus = odmObjFactory.createSenAnalysisBusXmlType();
				bus.setBusId(id);
				gen.getInjectBus().add(bus);
			
				if (this.genLoadDistBusRadioButton.isSelected()) {
					gen.setWithdrawBusType(SenAnalysisBusEnumType.SINGLE_BUS);
					id = (String)this.genLoadDistLoadBusComboBox.getSelectedItem();
					bus = odmObjFactory.createSenAnalysisBusXmlType();
					bus.setBusId(id);
					gen.getWithdrawBus().add(bus);
				}
				else if (this.genLoadDistBasecaseRadioButton.isSelected()) {
					gen.setWithdrawBusType(SenAnalysisBusEnumType.LOAD_DISTRIBUTION);
				}
				else {
					gen.setWithdrawBusType(SenAnalysisBusEnumType.USER_FILE);
					gen.setUserFilename(this.genLoadDistUserFileTextField.getText());
				}
			}
		}

		return noError;
	}
*/	
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        gsfWithdrawButtonGroup = new javax.swing.ButtonGroup();
        gsfLodfTypeButtonGroup = new javax.swing.ButtonGroup();
        gsfInjectionButtonGroup = new javax.swing.ButtonGroup();
        genAnalysisLodfTypeButtonGroup = new javax.swing.ButtonGroup();
        runDclfTabbedPane = new javax.swing.JTabbedPane();
        configPanel = new javax.swing.JPanel();
        interfaceFileLabel = new javax.swing.JLabel();
        interfaceFileTextField = new javax.swing.JTextField();
        selectInterfaceFileButton = new javax.swing.JButton();
        genAPNodeFileLabel = new javax.swing.JLabel();
        genAPNodeFileTextField = new javax.swing.JTextField();
        selectAPNodeFileButton = new javax.swing.JButton();
        loadDisPanel = new javax.swing.JPanel();
        loadDistThreshholdLabel = new javax.swing.JLabel();
        loadDistThreshholdTextField = new javax.swing.JTextField();
        outAllBranchPointsLabel = new javax.swing.JLabel();
        outAllBranchPointsTextField = new javax.swing.JTextField();
        outAllInterfacePointsLabel = new javax.swing.JLabel();
        outAllInterfacePointsTextField = new javax.swing.JTextField();
        gsfPanel = new javax.swing.JPanel();
        gsfInjectionPanel = new javax.swing.JPanel();
        gsfGenBusRadioButton = new javax.swing.JRadioButton();
        gsfGenAPNodeRadioButton = new javax.swing.JRadioButton();
        gsfGenBusLabel = new javax.swing.JLabel();
        gsfGenBusComboBox = new javax.swing.JComboBox();
        gsfAddGenButton = new javax.swing.JButton();
        gsfRemoveGenButton = new javax.swing.JButton();
        gsfGenScrollPane = new javax.swing.JScrollPane();
        gsfGenBusList = new javax.swing.JList();
        gsfWithdrawPanel = new javax.swing.JPanel();
        gsfLoadSingleBusRadioButton = new javax.swing.JRadioButton();
        gsfLoadBasecaseRadioButton = new javax.swing.JRadioButton();
        gsfLoadUserDefinedRadioButton = new javax.swing.JRadioButton();
        gsfLoadBusLabel = new javax.swing.JLabel();
        gsfLoadBusComboBox = new javax.swing.JComboBox();
        gsfLoadUserFileTextField = new javax.swing.JTextField();
        gsfLoadUserFileButton = new javax.swing.JButton();
        gsfMonitorBranchPanel = new javax.swing.JPanel();
        gsfMonitorBranchListComboBox = new javax.swing.JComboBox();
        gsfMonitorAddBranchButton = new javax.swing.JButton();
        gsfMonitorInterfaceListComboBox = new javax.swing.JComboBox();
        gsfMonitorAddInterfaceButton = new javax.swing.JButton();
        gsfMonitorScrollPane = new javax.swing.JScrollPane();
        gsfMonitorBranchList = new javax.swing.JList();
        gsfMonitorRemoveBranchButton = new javax.swing.JButton();
        gsfSelectedGSFButton = new javax.swing.JButton();
        gsfAllBranchGSFButton = new javax.swing.JButton();
        gsfLargetBranchGSFButton = new javax.swing.JButton();
        gsfAllInterfaceGSFButton = new javax.swing.JButton();
        gsfLargetInterfaceGSFButton = new javax.swing.JButton();
        lodfPanel = new javax.swing.JPanel();
        lodfOutageBranchPanel = new javax.swing.JPanel();
        lodfTypePanel = new javax.swing.JPanel();
        lodfSingleTypeRadioButton = new javax.swing.JRadioButton();
        lodfMultiTypeRadioButton = new javax.swing.JRadioButton();
        lodfBranchListComboBox = new javax.swing.JComboBox();
        lodfBranchListLabel = new javax.swing.JLabel();
        lodfMultiBranchScrollPane = new javax.swing.JScrollPane();
        lodfMultiOutageBranchList = new javax.swing.JList();
        lodfAddBranchButton = new javax.swing.JButton();
        lodfRemoveBranchButton = new javax.swing.JButton();
        lodfMonitorBranchPanel = new javax.swing.JPanel();
        lodfMonitorBranchListComboBox = new javax.swing.JComboBox();
        lodfMonitorAddBranchButton = new javax.swing.JButton();
        lodfMonitorInterfaceListComboBox = new javax.swing.JComboBox();
        lodfMonitorAddInterfaceButton = new javax.swing.JButton();
        lodfMonitorScrollPane = new javax.swing.JScrollPane();
        lodfMonitorBranchInterfaceList = new javax.swing.JList();
        lodfMonitorRemoveButton = new javax.swing.JButton();
        lodfAllBranchLODFButton = new javax.swing.JButton();
        lodfSelectedLODFButton = new javax.swing.JButton();
        lodfLargetBranchLODFButton = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(515, 410));

        runDclfTabbedPane.setFont(new java.awt.Font("Dialog", 0, 12));
        runDclfTabbedPane.setMinimumSize(new java.awt.Dimension(80, 48));
        runDclfTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                panelSelectionChanged(evt);
            }
        });

        configPanel.setEnabled(false);

        interfaceFileLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        interfaceFileLabel.setText("Interface File");

        interfaceFileTextField.setColumns(25);
        interfaceFileTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        interfaceFileTextField.setText("Interface File ...");
        interfaceFileTextField.setToolTipText("Interface definition file");
        interfaceFileTextField.setMaximumSize(new java.awt.Dimension(90, 20));

        selectInterfaceFileButton.setFont(new java.awt.Font("Dialog", 0, 10));
        selectInterfaceFileButton.setText("Select");
        selectInterfaceFileButton.setToolTipText("Click to load the interface definition file");
        selectInterfaceFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectInterfaceFileButtonActionPerformed(evt);
            }
        });

        genAPNodeFileLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        genAPNodeFileLabel.setText("Gen Group File");

        genAPNodeFileTextField.setColumns(25);
        genAPNodeFileTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        genAPNodeFileTextField.setText("Gen group file ...");
        genAPNodeFileTextField.setToolTipText("Aggregate pricing node file");
        genAPNodeFileTextField.setMaximumSize(new java.awt.Dimension(90, 20));

        selectAPNodeFileButton.setFont(new java.awt.Font("Dialog", 0, 10));
        selectAPNodeFileButton.setText("Select");
        selectAPNodeFileButton.setToolTipText("Click to load the aggregate pricing node file");
        selectAPNodeFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAPNodeFileButtonActionPerformed(evt);
            }
        });

        loadDisPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Load Distribution", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N

        loadDistThreshholdLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        loadDistThreshholdLabel.setText("Min Load Threshhold (Mw)   ");

        loadDistThreshholdTextField.setColumns(3);
        loadDistThreshholdTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        loadDistThreshholdTextField.setText("5.0");
        loadDistThreshholdTextField.setToolTipText("When use the basecase load for load distribution factor calculation, the threshhold for min load in Mw for the calculation.");

        org.jdesktop.layout.GroupLayout loadDisPanelLayout = new org.jdesktop.layout.GroupLayout(loadDisPanel);
        loadDisPanel.setLayout(loadDisPanelLayout);
        loadDisPanelLayout.setHorizontalGroup(
            loadDisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(loadDisPanelLayout.createSequentialGroup()
                .add(19, 19, 19)
                .add(loadDistThreshholdLabel)
                .add(71, 71, 71)
                .add(loadDistThreshholdTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(140, Short.MAX_VALUE))
        );
        loadDisPanelLayout.setVerticalGroup(
            loadDisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(loadDisPanelLayout.createSequentialGroup()
                .add(loadDisPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(loadDistThreshholdLabel)
                    .add(loadDistThreshholdTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        outAllBranchPointsLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        outAllBranchPointsLabel.setText("All Branch Output Points");

        outAllBranchPointsTextField.setColumns(5);
        outAllBranchPointsTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        outAllBranchPointsTextField.setText("50");
        outAllBranchPointsTextField.setToolTipText("Max rows for branch analysis output");

        outAllInterfacePointsLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        outAllInterfacePointsLabel.setText("All Interface Output Points");

        outAllInterfacePointsTextField.setColumns(5);
        outAllInterfacePointsTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        outAllInterfacePointsTextField.setText("10");
        outAllInterfacePointsTextField.setToolTipText("Max rows for interface analysis output");

        org.jdesktop.layout.GroupLayout configPanelLayout = new org.jdesktop.layout.GroupLayout(configPanel);
        configPanel.setLayout(configPanelLayout);
        configPanelLayout.setHorizontalGroup(
            configPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(configPanelLayout.createSequentialGroup()
                .add(configPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(configPanelLayout.createSequentialGroup()
                        .add(31, 31, 31)
                        .add(configPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(outAllBranchPointsLabel)
                            .add(outAllInterfacePointsLabel))
                        .add(44, 44, 44)
                        .add(configPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(outAllInterfacePointsTextField, 0, 0, Short.MAX_VALUE)
                            .add(outAllBranchPointsTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(configPanelLayout.createSequentialGroup()
                        .add(31, 31, 31)
                        .add(configPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(configPanelLayout.createSequentialGroup()
                                .add(genAPNodeFileLabel)
                                .add(18, 18, 18)
                                .add(genAPNodeFileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 230, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(18, 18, 18)
                                .add(selectAPNodeFileButton))
                            .add(configPanelLayout.createSequentialGroup()
                                .add(interfaceFileLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(interfaceFileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 230, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(18, 18, 18)
                                .add(selectInterfaceFileButton))))
                    .add(configPanelLayout.createSequentialGroup()
                        .add(18, 18, 18)
                        .add(loadDisPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(37, Short.MAX_VALUE))
        );
        configPanelLayout.setVerticalGroup(
            configPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(configPanelLayout.createSequentialGroup()
                .add(19, 19, 19)
                .add(configPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(configPanelLayout.createSequentialGroup()
                        .add(selectInterfaceFileButton)
                        .add(1, 1, 1))
                    .add(configPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(interfaceFileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(interfaceFileLabel)))
                .add(18, 18, 18)
                .add(configPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(genAPNodeFileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(genAPNodeFileLabel)
                    .add(selectAPNodeFileButton))
                .add(15, 15, 15)
                .add(loadDisPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(configPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(outAllBranchPointsLabel)
                    .add(outAllBranchPointsTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(configPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(outAllInterfacePointsLabel)
                    .add(outAllInterfacePointsTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(132, Short.MAX_VALUE))
        );

        runDclfTabbedPane.addTab("Configuration", configPanel);

        gsfInjectionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Injection Generator", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N

        gsfInjectionButtonGroup.add(gsfGenBusRadioButton);
        gsfGenBusRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        gsfGenBusRadioButton.setSelected(true);
        gsfGenBusRadioButton.setText("GenBus");
        gsfGenBusRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gsfGenBusRadioButtonActionPerformed(evt);
            }
        });

        gsfInjectionButtonGroup.add(gsfGenAPNodeRadioButton);
        gsfGenAPNodeRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        gsfGenAPNodeRadioButton.setText("GenGroup");
        gsfGenAPNodeRadioButton.setToolTipText("Use aggregate pricing node for load distribution factor calculation.");
        gsfGenAPNodeRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gsfGenAPNodeRadioButtonActionPerformed(evt);
            }
        });

        gsfGenBusLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        gsfGenBusLabel.setText("Gen Bus    ");

        gsfGenBusComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        gsfGenBusComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gsfGenBusComboBox.setToolTipText("Generator bus in the system for injection geneator selection");

        gsfAddGenButton.setFont(new java.awt.Font("Dialog", 0, 10));
        gsfAddGenButton.setText("Add");
        gsfAddGenButton.setToolTipText("Add a generator bus from the Gen Bus dropdown list to the injection generator bus list");
        gsfAddGenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gsfAddGenButtonActionPerformed(evt);
            }
        });

        gsfRemoveGenButton.setFont(new java.awt.Font("Dialog", 0, 10));
        gsfRemoveGenButton.setText("Remove");
        gsfRemoveGenButton.setToolTipText("Remove a generator bus from the injection generator bus list.\nYou need first select one and only one item.");
        gsfRemoveGenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gsfRemoveGenButtonActionPerformed(evt);
            }
        });

        gsfGenBusList.setFont(new java.awt.Font("Dialog", 0, 12));
        gsfGenBusList.setToolTipText("Selected injection generator buses for GSF analysis. You should select at least one gen bus.");
        gsfGenScrollPane.setViewportView(gsfGenBusList);

        org.jdesktop.layout.GroupLayout gsfInjectionPanelLayout = new org.jdesktop.layout.GroupLayout(gsfInjectionPanel);
        gsfInjectionPanel.setLayout(gsfInjectionPanelLayout);
        gsfInjectionPanelLayout.setHorizontalGroup(
            gsfInjectionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(gsfInjectionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(gsfInjectionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, gsfInjectionPanelLayout.createSequentialGroup()
                        .add(gsfGenBusRadioButton)
                        .add(18, 18, 18)
                        .add(gsfGenAPNodeRadioButton)
                        .add(46, 46, 46))
                    .add(gsfInjectionPanelLayout.createSequentialGroup()
                        .add(gsfGenScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 115, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(gsfInjectionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(gsfAddGenButton)
                            .add(gsfRemoveGenButton))
                        .addContainerGap(27, Short.MAX_VALUE))
                    .add(gsfInjectionPanelLayout.createSequentialGroup()
                        .add(gsfGenBusLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(gsfGenBusComboBox, 0, 145, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        gsfInjectionPanelLayout.setVerticalGroup(
            gsfInjectionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(gsfInjectionPanelLayout.createSequentialGroup()
                .add(gsfInjectionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(gsfGenAPNodeRadioButton)
                    .add(gsfGenBusRadioButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gsfInjectionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(gsfGenBusComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(gsfGenBusLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gsfInjectionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(gsfInjectionPanelLayout.createSequentialGroup()
                        .add(gsfAddGenButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(gsfRemoveGenButton))
                    .add(gsfGenScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 61, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        gsfWithdrawPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Load Distribution", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N

        gsfWithdrawButtonGroup.add(gsfLoadSingleBusRadioButton);
        gsfLoadSingleBusRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        gsfLoadSingleBusRadioButton.setText("Bus");
        gsfLoadSingleBusRadioButton.setToolTipText("Single bus as the withdraw bus");
        gsfLoadSingleBusRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gsfLoadSingleBusRadioButtonActionPerformed(evt);
            }
        });

        gsfWithdrawButtonGroup.add(gsfLoadBasecaseRadioButton);
        gsfLoadBasecaseRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        gsfLoadBasecaseRadioButton.setSelected(true);
        gsfLoadBasecaseRadioButton.setText("BaseCase");
        gsfLoadBasecaseRadioButton.setToolTipText("Use load buses in the network as the withdraw bus. The distribution\nfactor is calculated according relative size of the load, excluding \nthose small load less than the threshhold (normally, 5Mw)");
        gsfLoadBasecaseRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gsfLoadBasecaseRadioButtonActionPerformed(evt);
            }
        });

        gsfWithdrawButtonGroup.add(gsfLoadUserDefinedRadioButton);
        gsfLoadUserDefinedRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        gsfLoadUserDefinedRadioButton.setText("User");
        gsfLoadUserDefinedRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gsfLoadUserDefinedRadioButtonActionPerformed(evt);
            }
        });

        gsfLoadBusLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        gsfLoadBusLabel.setText("Bus");
        gsfLoadBusLabel.setEnabled(false);

        gsfLoadBusComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        gsfLoadBusComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gsfLoadBusComboBox.setToolTipText("Withdraw bus selection in case the Bus option is selected, or AP Node selection in case the APNode option is selected.");
        gsfLoadBusComboBox.setEnabled(false);

        gsfLoadUserFileTextField.setColumns(25);
        gsfLoadUserFileTextField.setFont(new java.awt.Font("Dialog", 0, 10));
        gsfLoadUserFileTextField.setText("User defined file ...");
        gsfLoadUserFileTextField.setEnabled(false);

        gsfLoadUserFileButton.setFont(new java.awt.Font("Dialog", 0, 10));
        gsfLoadUserFileButton.setText("Select");
        gsfLoadUserFileButton.setEnabled(false);
        gsfLoadUserFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gsfLoadUserFileButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout gsfWithdrawPanelLayout = new org.jdesktop.layout.GroupLayout(gsfWithdrawPanel);
        gsfWithdrawPanel.setLayout(gsfWithdrawPanelLayout);
        gsfWithdrawPanelLayout.setHorizontalGroup(
            gsfWithdrawPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(gsfWithdrawPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(gsfWithdrawPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(gsfLoadUserFileTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                    .add(gsfWithdrawPanelLayout.createSequentialGroup()
                        .add(gsfWithdrawPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(gsfLoadSingleBusRadioButton)
                            .add(gsfWithdrawPanelLayout.createSequentialGroup()
                                .add(21, 21, 21)
                                .add(gsfLoadBusLabel)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(gsfWithdrawPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(gsfWithdrawPanelLayout.createSequentialGroup()
                                .add(gsfLoadBasecaseRadioButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(gsfLoadUserDefinedRadioButton))
                            .add(gsfWithdrawPanelLayout.createSequentialGroup()
                                .add(21, 21, 21)
                                .add(gsfLoadBusComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 98, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, gsfLoadUserFileButton))
                .addContainerGap())
        );
        gsfWithdrawPanelLayout.setVerticalGroup(
            gsfWithdrawPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(gsfWithdrawPanelLayout.createSequentialGroup()
                .add(gsfWithdrawPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(gsfLoadBasecaseRadioButton)
                    .add(gsfLoadSingleBusRadioButton)
                    .add(gsfLoadUserDefinedRadioButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gsfWithdrawPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(gsfLoadBusComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(gsfLoadBusLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gsfLoadUserFileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(gsfLoadUserFileButton))
        );

        gsfMonitorBranchPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Monitor Branch/Interface", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N

        gsfMonitorBranchListComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        gsfMonitorBranchListComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        gsfMonitorAddBranchButton.setFont(new java.awt.Font("Dialog", 0, 10));
        gsfMonitorAddBranchButton.setText("Add Branch");
        gsfMonitorAddBranchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gsfMonitorAddBranchButtonActionPerformed(evt);
            }
        });

        gsfMonitorInterfaceListComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        gsfMonitorInterfaceListComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        gsfMonitorAddInterfaceButton.setFont(new java.awt.Font("Dialog", 0, 10));
        gsfMonitorAddInterfaceButton.setText("Add Interface");
        gsfMonitorAddInterfaceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gsfMonitorAddInterfaceButtonActionPerformed(evt);
            }
        });

        gsfMonitorBranchList.setFont(new java.awt.Font("Dialog", 0, 12));
        gsfMonitorScrollPane.setViewportView(gsfMonitorBranchList);

        gsfMonitorRemoveBranchButton.setFont(new java.awt.Font("Dialog", 0, 10));
        gsfMonitorRemoveBranchButton.setText("Remove");
        gsfMonitorRemoveBranchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gsfMonitorRemoveBranchButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout gsfMonitorBranchPanelLayout = new org.jdesktop.layout.GroupLayout(gsfMonitorBranchPanel);
        gsfMonitorBranchPanel.setLayout(gsfMonitorBranchPanelLayout);
        gsfMonitorBranchPanelLayout.setHorizontalGroup(
            gsfMonitorBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(gsfMonitorBranchPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(gsfMonitorBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(gsfMonitorScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
                    .add(gsfMonitorBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(gsfMonitorAddBranchButton)
                        .add(gsfMonitorAddInterfaceButton)
                        .add(gsfMonitorInterfaceListComboBox, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(gsfMonitorBranchListComboBox, 0, 172, Short.MAX_VALUE)
                        .add(gsfMonitorRemoveBranchButton)))
                .addContainerGap())
        );
        gsfMonitorBranchPanelLayout.setVerticalGroup(
            gsfMonitorBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(gsfMonitorBranchPanelLayout.createSequentialGroup()
                .add(gsfMonitorBranchListComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gsfMonitorAddBranchButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gsfMonitorInterfaceListComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gsfMonitorAddInterfaceButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gsfMonitorScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 91, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(3, 3, 3)
                .add(gsfMonitorRemoveBranchButton)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        gsfSelectedGSFButton.setFont(new java.awt.Font("Dialog", 0, 12));
        gsfSelectedGSFButton.setText("Selected GSF");
        gsfSelectedGSFButton.setToolTipText("For the selected branch/interface, calculate GSF for the select gen");
        gsfSelectedGSFButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gsfSelectedGSFButtonActionPerformed(evt);
            }
        });

        gsfAllBranchGSFButton.setFont(new java.awt.Font("Dialog", 0, 12));
        gsfAllBranchGSFButton.setText("All Branch GSF");
        gsfAllBranchGSFButton.setToolTipText("For the selected branch, calculate all GSF");
        gsfAllBranchGSFButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gsfAllBranchGSFButtonActionPerformed(evt);
            }
        });

        gsfLargetBranchGSFButton.setFont(new java.awt.Font("Dialog", 0, 12));
        gsfLargetBranchGSFButton.setText("Larget Branch GSF");
        gsfLargetBranchGSFButton.setToolTipText("For the selected branch, calculate the largest GSF");
        gsfLargetBranchGSFButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gsfLargetBranchGSFButtonActionPerformed(evt);
            }
        });

        gsfAllInterfaceGSFButton.setFont(new java.awt.Font("Dialog", 0, 12));
        gsfAllInterfaceGSFButton.setText("All Interface GSF");
        gsfAllInterfaceGSFButton.setToolTipText("For the selected interface, calculate all GSF.");
        gsfAllInterfaceGSFButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gsfAllInterfaceGSFButtonActionPerformed(evt);
            }
        });

        gsfLargetInterfaceGSFButton.setFont(new java.awt.Font("Dialog", 0, 12));
        gsfLargetInterfaceGSFButton.setText("Largest Interface GSF");
        gsfLargetInterfaceGSFButton.setToolTipText("For the selected inerface, calculate the largest GSF");
        gsfLargetInterfaceGSFButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gsfLargetInterfaceGSFButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout gsfPanelLayout = new org.jdesktop.layout.GroupLayout(gsfPanel);
        gsfPanel.setLayout(gsfPanelLayout);
        gsfPanelLayout.setHorizontalGroup(
            gsfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(gsfPanelLayout.createSequentialGroup()
                .add(gsfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(gsfPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(gsfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(gsfInjectionPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(gsfWithdrawPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(gsfMonitorBranchPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(gsfPanelLayout.createSequentialGroup()
                        .add(59, 59, 59)
                        .add(gsfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, gsfPanelLayout.createSequentialGroup()
                                .add(gsfSelectedGSFButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(gsfLargetBranchGSFButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(gsfAllBranchGSFButton))
                            .add(gsfPanelLayout.createSequentialGroup()
                                .add(44, 44, 44)
                                .add(gsfLargetInterfaceGSFButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(gsfAllInterfaceGSFButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 47, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        gsfPanelLayout.setVerticalGroup(
            gsfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(gsfPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(gsfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(gsfPanelLayout.createSequentialGroup()
                        .add(gsfInjectionPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 142, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(gsfWithdrawPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(gsfMonitorBranchPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gsfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(gsfLargetBranchGSFButton)
                    .add(gsfSelectedGSFButton)
                    .add(gsfAllBranchGSFButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gsfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(gsfLargetInterfaceGSFButton)
                    .add(gsfAllInterfaceGSFButton))
                .add(21, 21, 21))
        );

        runDclfTabbedPane.addTab("GSF", gsfPanel);

        lodfPanel.setEnabled(false);

        lodfTypePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Outage Type", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N

        gsfLodfTypeButtonGroup.add(lodfSingleTypeRadioButton);
        lodfSingleTypeRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        lodfSingleTypeRadioButton.setSelected(true);
        lodfSingleTypeRadioButton.setText("Single Branch");
        lodfSingleTypeRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lodfSingleTypeRadioButtonActionPerformed(evt);
            }
        });

        gsfLodfTypeButtonGroup.add(lodfMultiTypeRadioButton);
        lodfMultiTypeRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        lodfMultiTypeRadioButton.setText("Multii-Branch");
        lodfMultiTypeRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lodfMultiTypeRadioButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout lodfTypePanelLayout = new org.jdesktop.layout.GroupLayout(lodfTypePanel);
        lodfTypePanel.setLayout(lodfTypePanelLayout);
        lodfTypePanelLayout.setHorizontalGroup(
            lodfTypePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lodfTypePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(lodfSingleTypeRadioButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(lodfMultiTypeRadioButton)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        lodfTypePanelLayout.setVerticalGroup(
            lodfTypePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lodfTypePanelLayout.createSequentialGroup()
                .add(lodfTypePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lodfSingleTypeRadioButton)
                    .add(lodfMultiTypeRadioButton))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lodfBranchListComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        lodfBranchListComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lodfBranchListLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        lodfBranchListLabel.setText("Outage Branch");

        lodfMultiOutageBranchList.setFont(new java.awt.Font("Dialog", 0, 12));
        lodfMultiBranchScrollPane.setViewportView(lodfMultiOutageBranchList);

        lodfAddBranchButton.setFont(new java.awt.Font("Dialog", 0, 10));
        lodfAddBranchButton.setText("Add");
        lodfAddBranchButton.setEnabled(false);
        lodfAddBranchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lodfAddBranchButtonActionPerformed(evt);
            }
        });

        lodfRemoveBranchButton.setFont(new java.awt.Font("Dialog", 0, 10));
        lodfRemoveBranchButton.setText("Remove");
        lodfRemoveBranchButton.setEnabled(false);
        lodfRemoveBranchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lodfRemoveBranchButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout lodfOutageBranchPanelLayout = new org.jdesktop.layout.GroupLayout(lodfOutageBranchPanel);
        lodfOutageBranchPanel.setLayout(lodfOutageBranchPanelLayout);
        lodfOutageBranchPanelLayout.setHorizontalGroup(
            lodfOutageBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lodfOutageBranchPanelLayout.createSequentialGroup()
                .add(lodfOutageBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lodfOutageBranchPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(lodfBranchListLabel))
                    .add(lodfTypePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lodfOutageBranchPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(lodfAddBranchButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(lodfRemoveBranchButton))
                    .add(lodfOutageBranchPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(lodfOutageBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lodfMultiBranchScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 202, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(lodfBranchListComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        lodfOutageBranchPanelLayout.setVerticalGroup(
            lodfOutageBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lodfOutageBranchPanelLayout.createSequentialGroup()
                .add(lodfTypePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(5, 5, 5)
                .add(lodfBranchListLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lodfBranchListComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(lodfMultiBranchScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 63, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(lodfOutageBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lodfAddBranchButton)
                    .add(lodfRemoveBranchButton))
                .addContainerGap())
        );

        lodfMonitorBranchPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Monitor Branch/Interface", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N

        lodfMonitorBranchListComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        lodfMonitorBranchListComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lodfMonitorAddBranchButton.setFont(new java.awt.Font("Dialog", 0, 10));
        lodfMonitorAddBranchButton.setText("Add Branch");
        lodfMonitorAddBranchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lodfMonitorAddBranchButtonActionPerformed(evt);
            }
        });

        lodfMonitorInterfaceListComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        lodfMonitorInterfaceListComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lodfMonitorAddInterfaceButton.setFont(new java.awt.Font("Dialog", 0, 10));
        lodfMonitorAddInterfaceButton.setText("Add Interface");
        lodfMonitorAddInterfaceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lodfMonitorAddInterfaceButtonActionPerformed(evt);
            }
        });

        lodfMonitorBranchInterfaceList.setFont(new java.awt.Font("Dialog", 0, 12));
        lodfMonitorScrollPane.setViewportView(lodfMonitorBranchInterfaceList);

        lodfMonitorRemoveButton.setFont(new java.awt.Font("Dialog", 0, 10));
        lodfMonitorRemoveButton.setText("Remove");
        lodfMonitorRemoveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lodfMonitorRemoveButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout lodfMonitorBranchPanelLayout = new org.jdesktop.layout.GroupLayout(lodfMonitorBranchPanel);
        lodfMonitorBranchPanel.setLayout(lodfMonitorBranchPanelLayout);
        lodfMonitorBranchPanelLayout.setHorizontalGroup(
            lodfMonitorBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lodfMonitorBranchPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(lodfMonitorBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lodfMonitorScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
                    .add(lodfMonitorAddInterfaceButton)
                    .add(lodfMonitorBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, lodfMonitorBranchListComboBox, 0, 173, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, lodfMonitorAddBranchButton)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, lodfMonitorRemoveButton)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, lodfMonitorInterfaceListComboBox, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        lodfMonitorBranchPanelLayout.setVerticalGroup(
            lodfMonitorBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lodfMonitorBranchPanelLayout.createSequentialGroup()
                .add(lodfMonitorBranchListComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lodfMonitorAddBranchButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lodfMonitorInterfaceListComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lodfMonitorAddInterfaceButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lodfMonitorScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lodfMonitorRemoveButton))
        );

        lodfAllBranchLODFButton.setFont(new java.awt.Font("Dialog", 0, 12));
        lodfAllBranchLODFButton.setText("All BranchLODF");
        lodfAllBranchLODFButton.setToolTipText("For the selected outage branch, calculate all LODF");
        lodfAllBranchLODFButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lodfAllBranchLODFButtonActionPerformed(evt);
            }
        });

        lodfSelectedLODFButton.setFont(new java.awt.Font("Dialog", 0, 12));
        lodfSelectedLODFButton.setText("Selected LODF");
        lodfSelectedLODFButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lodfSelectedLODFButtonActionPerformed(evt);
            }
        });

        lodfLargetBranchLODFButton.setFont(new java.awt.Font("Dialog", 0, 12));
        lodfLargetBranchLODFButton.setText("Largest Branch LODF");
        lodfLargetBranchLODFButton.setToolTipText("For the selected outage branch, calculated the largest LODF");
        lodfLargetBranchLODFButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lodfLargestBranchLODFButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout lodfPanelLayout = new org.jdesktop.layout.GroupLayout(lodfPanel);
        lodfPanel.setLayout(lodfPanelLayout);
        lodfPanelLayout.setHorizontalGroup(
            lodfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lodfPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(lodfOutageBranchPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(lodfMonitorBranchPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
            .add(lodfPanelLayout.createSequentialGroup()
                .add(28, 28, 28)
                .add(lodfSelectedLODFButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 23, Short.MAX_VALUE)
                .add(lodfLargetBranchLODFButton)
                .add(18, 18, 18)
                .add(lodfAllBranchLODFButton)
                .add(36, 36, 36))
        );
        lodfPanelLayout.setVerticalGroup(
            lodfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, lodfPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(lodfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lodfOutageBranchPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lodfMonitorBranchPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(29, 29, 29)
                .add(lodfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lodfAllBranchLODFButton)
                    .add(lodfSelectedLODFButton)
                    .add(lodfLargetBranchLODFButton))
                .add(56, 56, 56))
        );

        runDclfTabbedPane.addTab("LODF", lodfPanel);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(runDclfTabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 495, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(runDclfTabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
 
    private void panelSelectionChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_panelSelectionChanged
//    	if ( runDclfTabbedPane.getSelectedIndex() == 0 ) {
//        	ipssLogger.info("Panel selection changed - GF Panel");
//        	initGsfTabPanel();
//    	}
//    	else if ( runDclfTabbedPane.getSelectedIndex() == 1 ) {
//        	ipssLogger.info("Panel selection changed - LODF Panel");
//    	    initLodfTabPanel();
//    	}	
    }//GEN-LAST:event_panelSelectionChanged

// TODO
/*888888888888888888888
 *  Configuration    
  8888888888888888888888*/

private void selectInterfaceFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectInterfaceFileButtonActionPerformed
    ipssLogger.info("selectInterfaceFileButtonActionPerformed() called");
	JFileChooser fc = getExcelFileChooser();
	if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
		File file = fc.getSelectedFile();
		interfaceFileTextField.setText(file.getAbsolutePath());
	}
}//GEN-LAST:event_selectInterfaceFileButtonActionPerformed

private void selectAPNodeFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAPNodeFileButtonActionPerformed
    ipssLogger.info("selectAPNodeFileButtonActionPerformed() called");
	JFileChooser fc = getExcelFileChooser();
	if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
		File file = fc.getSelectedFile();
		this.genAPNodeFileTextField.setText(file.getAbsolutePath());
	}
}//GEN-LAST:event_selectAPNodeFileButtonActionPerformed

// TODO
/*888888888888888888888
 *  GSF    
 8888888888888888888888*/

private void gsfGenBusRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsfGenBusRadioButtonActionPerformed
    ipssLogger.info("gsfGenBusRadioButtonActionPerformed() called");
	this.gsfGenBusLabel.setText("Gen Bus");
	this.gsfGenBusComboBox.setModel(new javax.swing.DefaultComboBoxModel(this.genBusIdAry));
	this.gsfGenBusList.setEnabled(true);
	this.gsfAddGenButton.setEnabled(true);
	this.gsfRemoveGenButton.setEnabled(true);
	if (this._senXml.getGenShiftFactor().size() > 0) {
		int i = 0;
		String[] genIdAry = new String[this._senXml.getGenShiftFactor().size()];
		for (DclfBranchSensitivityXmlType g : this._senXml.getGenShiftFactor()) {
			if (g.getInjectBus().size() > 0) {
				SenAnalysisBusXmlType bus = g.getInjectBus().get(0);
				genIdAry[i++] = bus.getBusId();
			}
		}
		this.gsfGenBusList.setModel(new javax.swing.DefaultComboBoxModel(StringUtil.clean(genIdAry)));    
	}
}//GEN-LAST:event_gsfGenBusRadioButtonActionPerformed

private void gsfGenAPNodeRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsfGenAPNodeRadioButtonActionPerformed
	ipssLogger.info("gsfAPNodeRadioButtonActionPerformed() called");
	this.gsfGenBusLabel.setText("Gen Group");
	AggregateGenHelper helper = new AggregateGenHelper(this._ptInfoXml.getLoadDist().getAggregateGen());
	String[] idAry = helper.getAPGroupIdAry();
	this.gsfGenBusComboBox.setModel(new javax.swing.DefaultComboBoxModel(idAry));
	this.gsfGenBusList.setEnabled(false);
	this.gsfAddGenButton.setEnabled(false);
	this.gsfRemoveGenButton.setEnabled(false);
}//GEN-LAST:event_gsfGenAPNodeRadioButtonActionPerformed

 private void gsfAddGenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsfAddGenButtonActionPerformed
        ipssLogger.info("gsfAddGenButtonActionPerformed() called");
    	String id = (String)this.gsfGenBusComboBox.getSelectedItem();
    	RunUIUtilFunc.addItemJList(this.gsfGenBusList, id);
}//GEN-LAST:event_gsfAddGenButtonActionPerformed

private void gsfRemoveGenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsfRemoveGenButtonActionPerformed
        ipssLogger.info("gsfRemoveGenButtonActionPerformed() called");
        RunUIUtilFunc.removeItemJList(this.gsfGenBusList);
}//GEN-LAST:event_gsfRemoveGenButtonActionPerformed

private void gsfLoadSingleBusRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsfLoadSingleBusRadioButtonActionPerformed
    ipssLogger.info("gsfSingleBusRadioButtonActionPerformed() called");
    this.gsfLoadBusLabel.setEnabled(true);
    this.gsfLoadBusLabel.setText("Bus");
    this.gsfLoadBusComboBox.setEnabled(true);
    this.gsfLoadBusComboBox.setModel(new javax.swing.DefaultComboBoxModel(this.loadBusIdAry));    	
	this.gsfLoadUserFileButton.setEnabled(false);
	this.gsfLoadUserFileTextField.setEnabled(false);
}//GEN-LAST:event_gsfLoadSingleBusRadioButtonActionPerformed

private void gsfLoadBasecaseRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsfLoadBasecaseRadioButtonActionPerformed
	ipssLogger.info("gsfLoadDFactorRadioButtonActionPerformed() called");
	this.gsfLoadBusLabel.setEnabled(false);
	this.gsfLoadBusComboBox.setEnabled(false);
	this.gsfLoadUserFileButton.setEnabled(false);
	this.gsfLoadUserFileTextField.setEnabled(false);
}//GEN-LAST:event_gsfLoadBasecaseRadioButtonActionPerformed

private void gsfLoadUserDefinedRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsfLoadUserDefinedRadioButtonActionPerformed
	ipssLogger.info("gsfLoadUserDefinedRadioButtonActionPerformed() called");
	this.gsfLoadBusLabel.setEnabled(false);
	this.gsfLoadBusComboBox.setEnabled(false);
	this.gsfLoadUserFileButton.setEnabled(true);
	this.gsfLoadUserFileTextField.setEnabled(true);
}//GEN-LAST:event_gsfLoadUserDefinedRadioButtonActionPerformed

private void gsfLoadUserFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsfLoadUserFileButtonActionPerformed
	ipssLogger.info("gsfLoadUserFileButtonActionPerformed() called");
	JFileChooser fc = getExcelFileChooser();
	if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
		File file = fc.getSelectedFile();
		this.gsfLoadUserFileTextField.setText(file.getAbsolutePath());
	}
}//GEN-LAST:event_gsfLoadUserFileButtonActionPerformed

private void gsfMonitorAddBranchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsfMonitorAddBranchButtonActionPerformed
    ipssLogger.info("gsfAddBranchButtonActionPerformed() called");
	String id = (String)gsfMonitorBranchListComboBox.getSelectedItem();
	RunUIUtilFunc.addItemJList(gsfMonitorBranchList, "b:"+id);
}//GEN-LAST:event_gsfMonitorAddBranchButtonActionPerformed

private void gsfMonitorAddInterfaceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsfMonitorAddInterfaceButtonActionPerformed
    ipssLogger.info("gsfAddInterfaceButtonActionPerformed() called");
	String id = (String)gsfMonitorInterfaceListComboBox.getSelectedItem();
	RunUIUtilFunc.addItemJList(gsfMonitorBranchList, "i:"+id);        
}//GEN-LAST:event_gsfMonitorAddInterfaceButtonActionPerformed

private void gsfMonitorRemoveBranchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsfMonitorRemoveBranchButtonActionPerformed
    ipssLogger.info("gsfRemoveBranchButtonActionPerformed() called");
    RunUIUtilFunc.removeItemJList(this.gsfMonitorBranchList);
}//GEN-LAST:event_gsfMonitorRemoveBranchButtonActionPerformed

    private void gsfSelectedGSFButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsfSelectedGSFButtonActionPerformed
        ipssLogger.info("gsfCalculateButtonActionPerformed() called");
    	this.parent.setAlwaysOnTop(false);

        Vector<String> errMsg = new Vector<String>();
        saveEditor2GSF(errMsg, true);
    	if (errMsg.size() > 0) {
    		EditorPluginSpringFactory.getEditorDialogUtil().showMsgDialog(this.parent, "Data Error", errMsg);
    		return;
    	}
    	
		final DclfAlgorithmDSL algoDsl = IpssPTrading.createDclfAlgorithm(_simuCtx.getAclfNet())
				.setRefBus();
		
		new Thread() {
			public void run() {
				IAppStatus appStatus = GraphSpringFactory.getIpssGraphicEditor().getAppStatus();
				appStatus.busyStart(Constants.StatusBusyIndicatorPeriod,
						"Run GSF Analysis ...", "Run SenAnalysis");

				String outText = "";
				try {
					new DclfDslODMRunner(algoDsl).runDclfCase(_senXml, DclfAnalysisType.GSF, _ptInfoXml);
					outText = SenAnalysisOutput.outGSF(_senXml.getGenShiftFactor(), _ptInfoXml).toString();
				} catch (PSSLException e) {
					ipssLogger.severe(e.toString());
					outText = e.toString();
				}
				algoDsl.destroy();    	
		    	
				UISpringFactory.getOutputTextDialog("GSF Calculation Results")
					.display(outText);  
				
				appStatus.busyStop("Run GSF Analysis finished");			
			}
		}.start();
    }//GEN-LAST:event_gsfSelectedGSFButtonActionPerformed

    private void gsfLargetBranchGSFButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsfLargetBranchGSFButtonActionPerformed
        ipssLogger.info("gsfBranchLargetGSFButtonActionPerformed() called");
    	this.parent.setAlwaysOnTop(false);

    	final DclfAlgorithmDSL algoDsl = IpssPTrading.createDclfAlgorithm(_simuCtx.getAclfNet())
				.setRefBus();
    	
    	String id = (String)this.gsfMonitorBranchList.getSelectedValue();
    	if (id == null || !id.startsWith("b:")) {
    		EditorPluginSpringFactory.getEditorDialogUtil().showMsgDialog(this.parent, "Data Error", "Select a monitor branch");
    		return;
    	}
		String braId = id.substring(2);
		
//    	String id = (String)gsfMonitorBranchListComboBox.getSelectedItem();
//    	if (id == null) 
//    		id = (String)gsfMonitorBranchListComboBox.getItemAt(0);
//    	if (id == null) {
//    		PluginSpringFactory.getEditorDialogUtil().showMsgDialog(this.parent, "Data Error", "Select a monitor branch");
//    		return;
//    	}
    	
		final String fromId = NetUtilFunc.findFromID(braId);
		final String toId = NetUtilFunc.findToID(braId);
		final String cirId = NetUtilFunc.findCirNo(braId);    	
		final AclfBranch monitorBranch = _simuCtx.getAclfNet().getAclfBranch(fromId, toId, cirId);
		
		new Thread() {
			public void run() {
				IAppStatus appStatus = GraphSpringFactory.getIpssGraphicEditor().getAppStatus();
				appStatus.busyStart(Constants.StatusBusyIndicatorPeriod,
						"Run Largest GSF Analysis ...", "Run SenAnalysis");
				DblBusValue maxGSF = algoDsl.monitorBranch(monitorBranch).largestGSF();
				String outText = SenAnalysisOutput.outLargestGSF(monitorBranch, maxGSF).toString();
				algoDsl.destroy();
				UISpringFactory.getOutputTextDialog("Largest GSF Calculation Results").display(outText);   

				appStatus.busyStop("Run Largest GSF Analysis finished");			
			}
		}.start();
}//GEN-LAST:event_gsfLargetBranchGSFButtonActionPerformed

    private void gsfAllBranchGSFButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsfAllBranchGSFButtonActionPerformed
        ipssLogger.info("gsfBranchLargetGSFButtonActionPerformed() called");
    	this.parent.setAlwaysOnTop(false);

    	final DclfAlgorithmDSL algoDsl = IpssPTrading.createDclfAlgorithm(_simuCtx.getAclfNet())
				.setRefBus();
    	
    	String id = (String)this.gsfMonitorBranchList.getSelectedValue();
    	if (id == null || !id.startsWith("b:")) {
    		EditorPluginSpringFactory.getEditorDialogUtil().showMsgDialog(this.parent, "Data Error", "Select a monitor branch");
    		return;
    	}
		String braId = id.substring(2);
		
//    	String id = (String)gsfMonitorBranchListComboBox.getSelectedItem();
//    	if (id == null) 
//    		id = (String)gsfMonitorBranchListComboBox.getItemAt(0);
//    	if (id == null) {
//    		PluginSpringFactory.getEditorDialogUtil().showMsgDialog(this.parent, "Data Error", "Select a monitor branch");
//    		return;
//    	}
    	
		final String fromId = NetUtilFunc.findFromID(braId);
		final String toId = NetUtilFunc.findToID(braId);
		final String cirId = NetUtilFunc.findCirNo(braId);    	
		final AclfBranch monitorBranch = _simuCtx.getAclfNet().getAclfBranch(fromId, toId, cirId);
		final int outPoints = this._senXml.getOutOption().getAllBranchPoints();
		
		new Thread() {
			public void run() {
				IAppStatus appStatus = GraphSpringFactory.getIpssGraphicEditor().getAppStatus();
				appStatus.busyStart(Constants.StatusBusyIndicatorPeriod,
						"Run GSF Analysis ...", "Run SenAnalysis");
				List<DblBusValue> gsfList = algoDsl.monitorBranch(monitorBranch).largestGSFs(outPoints);
				String outText = SenAnalysisOutput.outGSFList(monitorBranch, gsfList).toString();
				algoDsl.destroy();
				UISpringFactory.getOutputTextDialog("Largest GSF Calculation Results").display(outText);   

				appStatus.busyStop("Run GSF Analysis finished");			
			}
		}.start();
    }//GEN-LAST:event_gsfAllBranchGSFButtonActionPerformed

    private void gsfAllInterfaceGSFButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsfAllInterfaceGSFButtonActionPerformed
        ipssLogger.info("gsfAllInterfaceGSFButtonActionPerformed() called");
    	this.parent.setAlwaysOnTop(false);

    	final DclfAlgorithmDSL algoDsl = IpssPTrading.createDclfAlgorithm(_simuCtx.getAclfNet())
				.setRefBus();
    	
    	String id = (String)this.gsfMonitorBranchList.getSelectedValue();
    	if (id == null || !id.startsWith("i:")) {
    		EditorPluginSpringFactory.getEditorDialogUtil().showMsgDialog(this.parent, "Data Error", "Select a monitor interface");
    		return;
    	}
		String infId = id.substring(2);
		
//    	String id = (String)gsfMonitorInterfaceListComboBox.getSelectedItem();
//    	if (id == null) 
//    		id = (String)gsfMonitorInterfaceListComboBox.getItemAt(0);
//    	if (id == null) {
//    		PluginSpringFactory.getEditorDialogUtil().showMsgDialog(this.parent, "Data Error", "Select a monitor interface");
//    		return;
//    	}
    	
		final FlowInterface monitorInf = _simuCtx.getAclfNet().getFlowInterface(infId);
		final int outPoints = this._senXml.getOutOption().getAllInterfacePoints();
		
		new Thread() {
			public void run() {
				IAppStatus appStatus = GraphSpringFactory.getIpssGraphicEditor().getAppStatus();
				appStatus.busyStart(Constants.StatusBusyIndicatorPeriod,
						"Run GSF Analysis ...", "Run SenAnalysis");
				List<DblBusValue> gsfList = algoDsl.monitorFlowInterface(monitorInf).largestGSFs(outPoints);
				String outText = SenAnalysisOutput.outGSFList(monitorInf, gsfList).toString();
				algoDsl.destroy();
				UISpringFactory.getOutputTextDialog("Largest GSF Calculation Results").display(outText);   

				appStatus.busyStop("Run GSF Analysis finished");			
			}
		}.start();        
    }//GEN-LAST:event_gsfAllInterfaceGSFButtonActionPerformed

    private void gsfLargetInterfaceGSFButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                            
        ipssLogger.info("gsfLargetInterfaceGSFButtonActionPerformed() called");
    	this.parent.setAlwaysOnTop(false);

    	final DclfAlgorithmDSL algoDsl = IpssPTrading.createDclfAlgorithm(_simuCtx.getAclfNet())
				.setRefBus();
    	
    	String id = (String)this.gsfMonitorBranchList.getSelectedValue();
    	if (id == null || !id.startsWith("i:")) {
    		EditorPluginSpringFactory.getEditorDialogUtil().showMsgDialog(this.parent, "Data Error", "Select a monitor interface");
    		return;
    	}
		String infId = id.substring(2);
		
//    	String id = (String)gsfMonitorInterfaceListComboBox.getSelectedItem();
//    	if (id == null) 
//    		id = (String)gsfMonitorInterfaceListComboBox.getItemAt(0);
//    	if (id == null) {
//    		PluginSpringFactory.getEditorDialogUtil().showMsgDialog(this.parent, "Data Error", "Select a monitor interface");
//    		return;
//    	}

    	final FlowInterface monitorInf = _simuCtx.getAclfNet().getFlowInterface(infId);
		
		new Thread() {
			public void run() {
				IAppStatus appStatus = GraphSpringFactory.getIpssGraphicEditor().getAppStatus();
				appStatus.busyStart(Constants.StatusBusyIndicatorPeriod,
						"Run Largest GSF Analysis ...", "Run SenAnalysis");
				DblBusValue maxGSF = algoDsl.monitorFlowInterface(monitorInf).largestGSF();
				String outText = SenAnalysisOutput.outLargestGSF(monitorInf, maxGSF).toString();
				algoDsl.destroy();
				UISpringFactory.getOutputTextDialog("Largest GSF Calculation Results").display(outText);   

				appStatus.busyStop("Run Largest GSF Analysis finished");			
			}
		}.start();        
    }                                                               

 // TODO
/*888888888888888888888
 *  LODF    
 888888888888888888888 */
    
    private void lodfSingleTypeRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lodfSingleTypeRadioButtonActionPerformed
        ipssLogger.info("lodfSingleTypeRadioButtonActionPerformed() called");
        setLodfComponentStatus(false);
    }//GEN-LAST:event_lodfSingleTypeRadioButtonActionPerformed

    private void lodfMultiTypeRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lodfMultiTypeRadioButtonActionPerformed
        ipssLogger.info("lodfMultiTypeRadioButtonActionPerformed() called");
        setLodfComponentStatus(true);
    }//GEN-LAST:event_lodfMultiTypeRadioButtonActionPerformed
    
    private void setLodfComponentStatus(boolean multi) {
    	this.lodfAddBranchButton.setEnabled(multi);
    	this.lodfRemoveBranchButton.setEnabled(multi);
    	this.lodfMultiOutageBranchList.setEnabled(multi);
    	this.lodfAllBranchLODFButton.setEnabled(!multi);
    	this.lodfLargetBranchLODFButton.setEnabled(!multi);
    }
    
    private void lodfAddBranchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lodfAddBranchButtonActionPerformed
        ipssLogger.info("lodfAddBranchButtonActionPerformed() called");
    	String id = (String)this.lodfBranchListComboBox.getSelectedItem();
    	RunUIUtilFunc.addItemJList(lodfMultiOutageBranchList, id);
    }//GEN-LAST:event_lodfAddBranchButtonActionPerformed

    private void lodfRemoveBranchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lodfRemoveBranchButtonActionPerformed
        ipssLogger.info("lodfRemoveBranchButtonActionPerformed() called");
        RunUIUtilFunc.removeItemJList(this.lodfMultiOutageBranchList);
    }//GEN-LAST:event_lodfRemoveBranchButtonActionPerformed

    private void lodfMonitorAddBranchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lodfMonitorAddBranchButtonActionPerformed
        ipssLogger.info("lodfMonitorAddBranchButtonActionPerformed() called");
    	String id = (String)this.lodfMonitorBranchListComboBox.getSelectedItem();
    	RunUIUtilFunc.addItemJList(lodfMonitorBranchInterfaceList, "b:"+id);
    }//GEN-LAST:event_lodfMonitorAddBranchButtonActionPerformed

    private void lodfMonitorAddInterfaceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lodfMonitorAddInterfaceButtonActionPerformed
        ipssLogger.info("lodfMonitorAddInterfaceButtonActionPerformed() called");
    	String id = (String)this.lodfMonitorInterfaceListComboBox.getSelectedItem();
    	RunUIUtilFunc.addItemJList(lodfMonitorBranchInterfaceList, "i:"+id);
    }//GEN-LAST:event_lodfMonitorAddInterfaceButtonActionPerformed

    private void lodfMonitorRemoveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lodfMonitorRemoveButtonActionPerformed
        ipssLogger.info("lodfMonitorRemoveButtonActionPerformed() called");
        RunUIUtilFunc.removeItemJList(this.lodfMonitorBranchInterfaceList);
    }//GEN-LAST:event_lodfMonitorRemoveButtonActionPerformed
    
    private void lodfSelectedLODFButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lodfSelectedLODFButtonActionPerformed
        ipssLogger.info("lodfCalculateButtonActionPerformed() called");
    	this.parent.setAlwaysOnTop(false);
    	
        Vector<String> errMsg = new Vector<String>();    	
    	saveEditor2LODF(errMsg, true);
    	if (errMsg.size() > 0) {
    		EditorPluginSpringFactory.getEditorDialogUtil().showMsgDialog(this.parent, "Data Error", errMsg);
    		return;
    	}

		final DclfAlgorithmDSL algoDsl = IpssPTrading.createDclfAlgorithm(_simuCtx.getAclfNet());
		final DclfSenAnalysisXmlType senXml = this._senXml;
		
		new Thread() {
			public void run() {
				IAppStatus appStatus = GraphSpringFactory.getIpssGraphicEditor().getAppStatus();
				appStatus.busyStart(Constants.StatusBusyIndicatorPeriod,
						"Run LODF Analysis ...", "Run SenAnalysis");

				String outText = "";
				try {
					new DclfDslODMRunner(algoDsl).runDclfCase(senXml, DclfAnalysisType.LODF, _ptInfoXml);
					// ODM schema can have multiple sets of LODF calculation. For the GUI implementation
					// only the first set is used.
					LineOutageDFactorXmlType lodf = senXml.getLineOutageDFactor().get(0);
			    	outText = SenAnalysisOutput.outLODF(lodf).toString(); // this.odmParser.toXmlDoc(false);
				} catch (PSSLException e) {
					ipssLogger.severe(e.toString());
					outText = e.toString();
				}		
				
				algoDsl.destroy();
				
				UISpringFactory.getOutputTextDialog("LODF Calculation Results").display(outText, 60, 15);   
				
				appStatus.busyStop("Run LODF GSF Analysis finished");			
			}
		}.start();
    }//GEN-LAST:event_lodfSelectedLODFButtonActionPerformed

    private void lodfLargestBranchLODFButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                     
        ipssLogger.info("lodfLargetLODFButtonActionPerformed() called");
    	this.parent.setAlwaysOnTop(false);
    	
    	final DclfAlgorithmDSL algoDsl = IpssPTrading.createDclfAlgorithm(_simuCtx.getAclfNet());
		
    	String id = (String)this.lodfBranchListComboBox.getSelectedItem();
    	if (id == null) 
    		id = (String)this.lodfBranchListComboBox.getItemAt(0);
    	if (id == null) {
    		EditorPluginSpringFactory.getEditorDialogUtil().showMsgDialog(this.parent, "Data Error", "Select an outage branch");
    		return;
    	}
    	
		final String branchId = id;
		final String fromId = NetUtilFunc.findFromID(id);
		final String toId = NetUtilFunc.findToID(id);
		final String cirId = NetUtilFunc.findCirNo(id);    	
	
		new Thread() {
			public void run() {
				IAppStatus appStatus = GraphSpringFactory.getIpssGraphicEditor().getAppStatus();
				appStatus.busyStart(Constants.StatusBusyIndicatorPeriod,
						"Run LODF Analysis ...", "Run SenAnalysis");

				String outText = "";
				try {
					algoDsl.outageBranch(fromId, toId, cirId);
					DblBranchValue maxLODF = algoDsl.largestLODF();
					outText = SenAnalysisOutput.outLargestLODF(branchId, maxLODF).toString();
				} catch (PSSLException e) {
					ipssLogger.severe(e.toString());
					outText = e.toString();
				}		
				
				algoDsl.destroy();
				
				UISpringFactory.getOutputTextDialog("LODF Calculation Results").display(outText); 
				
				appStatus.busyStop("Run LODF GSF Analysis finished");			
			}
		}.start();
    }      

    private void lodfAllBranchLODFButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        ipssLogger.info("lodfAllLODFButtonActionPerformed() called");
    	this.parent.setAlwaysOnTop(false);
    	
		final DclfAlgorithmDSL algoDsl = IpssPTrading.createDclfAlgorithm(_simuCtx.getAclfNet());
		
    	String id = (String)this.lodfBranchListComboBox.getSelectedItem();
    	if (id == null) 
    		id = (String)this.lodfBranchListComboBox.getItemAt(0);
    	if (id == null) {
    		EditorPluginSpringFactory.getEditorDialogUtil().showMsgDialog(this.parent, "Data Error", "Select an outage branch");
    		return;
    	}

    	final String branchId = id;
		final String fromId = NetUtilFunc.findFromID(id);
		final String toId = NetUtilFunc.findToID(id);
		final String cirId = NetUtilFunc.findCirNo(id);    	
		final int outPoints = this._senXml.getOutOption().getAllBranchPoints();
	
		new Thread() {
			public void run() {
				IAppStatus appStatus = GraphSpringFactory.getIpssGraphicEditor().getAppStatus();
				appStatus.busyStart(Constants.StatusBusyIndicatorPeriod,
						"Run LODF Analysis ...", "Run SenAnalysis");
				
				String outText = "";
				try {
					algoDsl.outageBranch(fromId, toId, cirId);
					List<DblBranchValue> lodfList = algoDsl.largestLODFs(outPoints);
					outText = SenAnalysisOutput.outLODFList(branchId, lodfList).toString();
				} catch (PSSLException e) {
					ipssLogger.severe(e.toString());
					outText = e.toString();
				}		
				
				algoDsl.destroy();
				
				UISpringFactory.getOutputTextDialog("LODF Calculation Results").display(outText); 
				
				appStatus.busyStop("Run LODF GSF Analysis finished");			
			}
		}.start();
    }                                                     

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel configPanel;
    private javax.swing.JLabel genAPNodeFileLabel;
    private javax.swing.JTextField genAPNodeFileTextField;
    private javax.swing.ButtonGroup genAnalysisLodfTypeButtonGroup;
    private javax.swing.JButton gsfAddGenButton;
    private javax.swing.JButton gsfAllBranchGSFButton;
    private javax.swing.JButton gsfAllInterfaceGSFButton;
    private javax.swing.JRadioButton gsfGenAPNodeRadioButton;
    private javax.swing.JComboBox gsfGenBusComboBox;
    private javax.swing.JLabel gsfGenBusLabel;
    private javax.swing.JList gsfGenBusList;
    private javax.swing.JRadioButton gsfGenBusRadioButton;
    private javax.swing.JScrollPane gsfGenScrollPane;
    private javax.swing.ButtonGroup gsfInjectionButtonGroup;
    private javax.swing.JPanel gsfInjectionPanel;
    private javax.swing.JButton gsfLargetBranchGSFButton;
    private javax.swing.JButton gsfLargetInterfaceGSFButton;
    private javax.swing.JRadioButton gsfLoadBasecaseRadioButton;
    private javax.swing.JComboBox gsfLoadBusComboBox;
    private javax.swing.JLabel gsfLoadBusLabel;
    private javax.swing.JRadioButton gsfLoadSingleBusRadioButton;
    private javax.swing.JRadioButton gsfLoadUserDefinedRadioButton;
    private javax.swing.JButton gsfLoadUserFileButton;
    private javax.swing.JTextField gsfLoadUserFileTextField;
    private javax.swing.ButtonGroup gsfLodfTypeButtonGroup;
    private javax.swing.JButton gsfMonitorAddBranchButton;
    private javax.swing.JButton gsfMonitorAddInterfaceButton;
    private javax.swing.JList gsfMonitorBranchList;
    private javax.swing.JComboBox gsfMonitorBranchListComboBox;
    private javax.swing.JPanel gsfMonitorBranchPanel;
    private javax.swing.JComboBox gsfMonitorInterfaceListComboBox;
    private javax.swing.JButton gsfMonitorRemoveBranchButton;
    private javax.swing.JScrollPane gsfMonitorScrollPane;
    private javax.swing.JPanel gsfPanel;
    private javax.swing.JButton gsfRemoveGenButton;
    private javax.swing.JButton gsfSelectedGSFButton;
    private javax.swing.ButtonGroup gsfWithdrawButtonGroup;
    private javax.swing.JPanel gsfWithdrawPanel;
    private javax.swing.JLabel interfaceFileLabel;
    private javax.swing.JTextField interfaceFileTextField;
    private javax.swing.JPanel loadDisPanel;
    private javax.swing.JLabel loadDistThreshholdLabel;
    private javax.swing.JTextField loadDistThreshholdTextField;
    private javax.swing.JButton lodfAddBranchButton;
    private javax.swing.JButton lodfAllBranchLODFButton;
    private javax.swing.JComboBox lodfBranchListComboBox;
    private javax.swing.JLabel lodfBranchListLabel;
    private javax.swing.JButton lodfLargetBranchLODFButton;
    private javax.swing.JButton lodfMonitorAddBranchButton;
    private javax.swing.JButton lodfMonitorAddInterfaceButton;
    private javax.swing.JList lodfMonitorBranchInterfaceList;
    private javax.swing.JComboBox lodfMonitorBranchListComboBox;
    private javax.swing.JPanel lodfMonitorBranchPanel;
    private javax.swing.JComboBox lodfMonitorInterfaceListComboBox;
    private javax.swing.JButton lodfMonitorRemoveButton;
    private javax.swing.JScrollPane lodfMonitorScrollPane;
    private javax.swing.JScrollPane lodfMultiBranchScrollPane;
    private javax.swing.JList lodfMultiOutageBranchList;
    private javax.swing.JRadioButton lodfMultiTypeRadioButton;
    private javax.swing.JPanel lodfOutageBranchPanel;
    private javax.swing.JPanel lodfPanel;
    private javax.swing.JButton lodfRemoveBranchButton;
    private javax.swing.JButton lodfSelectedLODFButton;
    private javax.swing.JRadioButton lodfSingleTypeRadioButton;
    private javax.swing.JPanel lodfTypePanel;
    private javax.swing.JLabel outAllBranchPointsLabel;
    private javax.swing.JTextField outAllBranchPointsTextField;
    private javax.swing.JLabel outAllInterfacePointsLabel;
    private javax.swing.JTextField outAllInterfacePointsTextField;
    private javax.swing.JTabbedPane runDclfTabbedPane;
    private javax.swing.JButton selectAPNodeFileButton;
    private javax.swing.JButton selectInterfaceFileButton;
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
//	    gsfLoadThreshholdTextField.setInputVerifier(v);
		this.outAllBranchPointsTextField.setInputVerifier(v);
		this.outAllInterfacePointsTextField.setInputVerifier(v);
	}
	
	class DataVerifier extends javax.swing.InputVerifier {
		public boolean verify(javax.swing.JComponent input) {
			if (input == null)
				return false;
       		try {
//				if (input == gsfLoadThreshholdTextField)
//					return SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) > 0.0 && 
//							SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) <= 100.0;
						
				if (input == outAllBranchPointsTextField)
					return SwingInputVerifyUtil.getInt((javax.swing.JTextField)input) > 0;
				else if (input == outAllInterfacePointsTextField)
					return SwingInputVerifyUtil.getInt((javax.swing.JTextField)input) > 0;
						
 	       	} catch (Exception e) {
 	    		return false;
 	       	}				
			return true;
		}
	}
}
