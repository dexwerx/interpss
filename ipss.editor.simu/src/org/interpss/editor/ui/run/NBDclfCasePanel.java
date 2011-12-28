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

import java.util.List;
import java.util.Vector;

import javax.swing.JDialog;

import org.ieee.odm.model.ODMModelParser;
import org.ieee.odm.model.ext.ipss.IpssScenarioHelper;
import org.ieee.odm.schema.BranchRefXmlType;
import org.ieee.odm.schema.BranchShiftFactorXmlType;
import org.ieee.odm.schema.DclfBranchSensitivityXmlType;
import org.ieee.odm.schema.DclfSenAnalysisXmlType;
import org.ieee.odm.schema.FlowInterfaceRecXmlType;
import org.ieee.odm.schema.InterfaceShiftFactorXmlType;
import org.ieee.odm.schema.LODFMonitorBranchXmlType;
import org.ieee.odm.schema.LineOutageDFactorXmlType;
import org.ieee.odm.schema.PTradingAnalysisXmlType;
import org.ieee.odm.schema.SenAnalysisBusXmlType;
import org.ieee.odm.schema.SenAnalysisOutOptionXmlType;
import org.ieee.odm.schema.SenBusAnalysisEnumType;
import org.ieee.odm.schema.SensitivityEnumType;
import org.interpss.editor.jgraph.GraphSpringFactory;
import org.interpss.editor.jgraph.ui.app.IAppStatus;
import org.interpss.editor.jgraph.ui.edit.IFormDataPanel;
import org.interpss.editor.ui.RunUIUtilFunc;
import org.interpss.numeric.datatype.Unit.UnitType;
import org.interpss.numeric.util.Number2String;
import org.interpss.spring.UISpringFactory;
import org.interpss.ui.SwingInputVerifyUtil;

import com.interpss.common.datatype.Constants;
import com.interpss.common.exp.InterpssRuntimeException;
import com.interpss.common.msg.IpssMessage;
import com.interpss.common.msg.IpssMsgListener;
import com.interpss.common.util.IpssLogger;
import com.interpss.common.util.NetUtilFunc;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.flow.FlowInterface;
import com.interpss.pssl.common.DblBranchValue;
import com.interpss.pssl.common.DblBusValue;
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

	private ODMModelParser odmParser = new ODMModelParser();
    public void setODMParser(ODMModelParser parser) { 	this.odmParser = parser;   }

    private DclfSenAnalysisXmlType _senXml = null;
    private PTradingAnalysisXmlType _ptXml = null;    
    
    /** Creates new form NBAclfCasePanel */
    public NBDclfCasePanel(JDialog parent) {
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
		IpssLogger.getLogger().info("NBAclfCasePanel init() called");
	    //_netContainer = (GFormContainer)netContainer;
	    _simuCtx = (SimuContext)simuCtx;
	    
	    this.runDclfTabbedPane.setSelectedIndex(0);
	    initGsfTabPanel();
	    initLodfTabPanel();
	    
	    this.runDclfTabbedPane.setEnabledAt(2, false);
	    this.runDclfTabbedPane.setEnabledAt(3, false);
	}
    
    private void initGsfTabPanel() {
    	if (_simuCtx != null) {   // this method might be called be init(), when thre 0-panel is selected
    		this.gsfInjectBusComboBox.setModel(new javax.swing.DefaultComboBoxModel(
    				RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.GenBus).toArray()));
    		this.gsfMonitorBranchListComboBox.setModel(new javax.swing.DefaultComboBoxModel(
    				RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.AllBranch).toArray()));
    		this.gsfLoadBusComboBox.setModel(new javax.swing.DefaultComboBoxModel(
    				RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.LoadBus).toArray()));    	
    	}
    }

    private void initLodfTabPanel() {
    	if (_simuCtx != null) {   // this method might be called be init(), when thre 0-panel is selected
    		this.lodfBranchListComboBox.setModel(new javax.swing.DefaultComboBoxModel(
    				RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.AllBranch).toArray()));
    		this.lodfMonitorBranchListComboBox.setModel(new javax.swing.DefaultComboBoxModel(
    				RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.AllBranch).toArray()));
    	}
    }
   
    public void setXmlCaseData(DclfSenAnalysisXmlType senXml, PTradingAnalysisXmlType pt) {
    	this._senXml = senXml;
    	this._ptXml = pt;
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
		IpssLogger.getLogger().info("NBAclfCasePanel setForm2Editor() called");
		
		// load FlowInterface if necessary
		if (!RunUIUtilFunc.loadFlowInterfaceFiles(this._simuCtx.getAclfNet(), this._ptXml))
			return false;

		if (this._simuCtx.getAclfNet().isFlowInterfaceLoaded()) {
			setFlowInterfaceStatus(true);
			this.gsfMonitorInterfaceListComboBox.setModel(new javax.swing.DefaultComboBoxModel(
					RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.FlowInterface).toArray()));
		}
		else
			setFlowInterfaceStatus(false);
		
		if (!setGSF2Editor())
			return false;

		if (!setLODF2Editor())
			return false;
		
		if (!setOutConfig2Editor())
			return false;
		
		return true;
	}
	
	private void setFlowInterfaceStatus(boolean b) {
		this.gsfMonitorInterfaceListComboBox.setEnabled(b);
		this.gsfMonitorAddInterfaceButton.setEnabled(b);
	}

	public boolean setGSF2Editor() {
		IpssLogger.getLogger().info("NBAclfCasePanel setGSF2Editor() called");
		
		if (this._senXml.getGenShiftFactor().size() > 0) {
			DclfBranchSensitivityXmlType gsf = this._senXml.getGenShiftFactor().get(0);
			
			SenAnalysisBusXmlType bus = gsf.getInjectBus().get(0);
			this.gsfInjectBusComboBox.setSelectedItem(bus.getBusId());
			
			this.gsfLoadThreshholdTextField.setText(
					Number2String.toStr(gsf.getMinLoadForDistFactor().getValue(), "#0.0"));
			
			String[] ary = new String[gsf.getBranchSFactor().size()+gsf.getInterfaceSFactor().size()];
			int cnt = 0;
			for ( BranchShiftFactorXmlType sf : gsf.getBranchSFactor()) {
				BranchRefXmlType branch = sf.getBranch();
				ary[cnt++] = "b:" + NetUtilFunc.formBranchId(branch.getFromBusId(), branch.getToBusId(), branch.getCircuitId());
			}
			for ( InterfaceShiftFactorXmlType inf : gsf.getInterfaceSFactor()) {
				FlowInterfaceRecXmlType in = inf.getInterface();
				ary[cnt++] = "i:" + in.getId();
			}
	    	gsfMonitorBranchList.setModel(new javax.swing.DefaultComboBoxModel(ary));    	
		}
		
		return true;
	}

	public boolean setLODF2Editor() {
		IpssLogger.getLogger().info("NBAclfCasePanel setLODF2Editor() called");
		
		LineOutageDFactorXmlType lodf = 
			this._senXml.getLineOutageDFactor() != null && this._senXml.getLineOutageDFactor().size() > 0?
				this._senXml.getLineOutageDFactor().get(0) : null;

		if ( lodf.getOutageBranch() != null ) {
			if (lodf.getOutageBranch().size() <= 1) { 
				this.lodfSingleTypeRadioButton.setSelected(true);
				if (lodf.getOutageBranch().size() == 1) { 
					String braId = lodf.getOutageBranch().get(0).getBranchId();
					this.lodfBranchListComboBox.setSelectedItem(braId);
				}
			}
			else {
				this.lodfMultiTypeRadioButton.setSelected(true);
				// TODO
			}
		}

		if (lodf.getMonitorBranch() != null) {
			String[] ary = new String[lodf.getMonitorBranch().size()];
			int cnt = 0;
			for (LODFMonitorBranchXmlType monitor : lodf.getMonitorBranch()) {
		    	String id = monitor.getBranch().getBranchId();
				ary[cnt++] = "b:" + id;
			}
			lodfMonitorBranchInterfaceList.setModel(new javax.swing.DefaultComboBoxModel(ary));    			
		}
		
		return true;
	}

	public boolean setOutConfig2Editor() {
		IpssLogger.getLogger().info("NBAclfCasePanel setOutConfig2Editor() called");
		
		SenAnalysisOutOptionXmlType outConfig = this._senXml.getOutOption();
		if (outConfig != null) {
			this.outAllBranchPointsTextField.setText(outConfig.getAllBranchPoints().toString());
			this.outAllInterfacePointsTextField.setText(outConfig.getAllInterfacePoints().toString());
		}
		
		return true;
	}

	public boolean setTDFactor2Editor() {
/*
		if (tdFactor.getInjectBusType() == SenBusAnalysisDataType.SINGLE_BUS) {
		    this.ptdfSingleInjectBusRadioButton.setSelected(true);
			ptdfSingleInjectBusRadioButtonActionPerformed(null);
			String inBusId = tdFactor.getInjectBusList().getInjectBus().get(0).getBusId();
			ptdfInjectBusComboBox.setSelectedItem(inBusId);
		} else {
			this.ptdfMultiInjectBusRadioButton.setSelected(true);
		    ptdfMultiInjectBusRadioButtonActionPerformed(null);
		}
				
		if (tdFactor.getWithdrawBusType() == SenBusAnalysisDataType.SINGLE_BUS) {
			String wdBusId = tdFactor.getWithdrawBusList().getWithdrawBus().get(0).getBusId();
			ptdfWithdrawBusComboBox.setSelectedItem(wdBusId);
			ptdfWithSingleBusRadioButton.setSelected(true);
			ptdfWithSingleBusRadioButtonActionPerformed(null);
	    	ptdfWithdarwBusList.setModel(new javax.swing.DefaultComboBoxModel( new String[] {}));
		}
		else {
			ptdfWithMultiBusRadioButton.setSelected(true);
			ptdfWithMultiBusRadioButtonActionPerformed(null);
	    	ptdfWithdarwBusList.setModel(new javax.swing.DefaultComboBoxModel(
	    			IpssXmlHelper.getSenAnalysisBusItemList(tdFactor.getWithdrawBusList().getWithdrawBus())));
		}
					
    	ptdfMeasBranchList.setModel(new javax.swing.DefaultComboBoxModel(
		    			IpssXmlHelper.getBranchIdAry(tdFactor.getBranch())));
*/
		return true;
	}

	public boolean setAreaTransfer2Editor() {
/*		
		this.atTransAmtTextField.setText(String.format("%4.2f", this.areaTransfer.getTransderAmountMW()));
		this.atTransAmtUnitComboBox.setSelectedItem("MW");
		
		AreaRecXmlType area = this.areaTransfer.getFromArea(); 
		if (area == null) {
			area = IpssXmlParser.getFactory().createAreaRecXmlType();
			this.areaTransfer.setFromArea(area);
			String no = (String)this.atFromAreaComboBox.getSelectedItem();
			area.setAreaNo(new Integer(no).intValue());
		}
		this.atFromAreaComboBox.setSelectedItem(new Integer(area.getAreaNo()).toString());
			
		area = this.areaTransfer.getToArea();
		if (area == null) {
			area = IpssXmlParser.getFactory().createAreaRecXmlType(); 
			this.areaTransfer.setToArea(area);
			String no = (String)this.atToAreaComboBox.getSelectedItem();
			area.setAreaNo(new Integer(no).intValue());
		}
		this.atToAreaComboBox.setSelectedItem(new Integer(area.getAreaNo()).toString());

		if (this.areaTransfer.getDeratingFactor() != 0.0)
			atDeratingFactorTextField.setText(String.format("%4.2f", this.areaTransfer.getDeratingFactor()));
		else
			atDeratingFactorTextField.setText("1.00");
			
		if (this.areaTransfer.getInjectBusList() != null && this.areaTransfer.getInjectBusList().getInjectBus().size() > 0) {
			atFromAreaBusList.setModel(new javax.swing.DefaultComboBoxModel(
	    			IpssXmlHelper.getSenAnalysisBusItemList(areaTransfer.getInjectBusList().getInjectBus())));
		}
		else
			atFromAreaBusList.setModel(new javax.swing.DefaultComboBoxModel(
				RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.GenInAreaDFactor, 
						this.areaTransfer.getFromArea().getAreaNo()).toArray()));

		if (this.areaTransfer.getWithdrawBusList() != null && this.areaTransfer.getWithdrawBusList().getWithdrawBus().size() > 0) {
			atToAreaBusList.setModel(new javax.swing.DefaultComboBoxModel(
	    			IpssXmlHelper.getSenAnalysisBusItemList(areaTransfer.getWithdrawBusList().getWithdrawBus())));
		}
		else
			atToAreaBusList.setModel(new javax.swing.DefaultComboBoxModel( 				
				RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.GenInAreaDFactor, 
						this.areaTransfer.getToArea().getAreaNo()).toArray()));
		
    	atMeasBranchList.setModel(new javax.swing.DefaultComboBoxModel(
		    			IpssXmlHelper.getBranchIdAry(areaTransfer.getBranch())));

    	atFromDFactorEditTextField.setEnabled(false);
    	atFromAreaUpdateButton.setEnabled(false);
    	atToDFactorEditTextField.setEnabled(false);
    	atToAreaUpdateButton.setEnabled(false);
*/
    	return true;
	}

////////////////////////////////////////////////////////////////////
//////            Set Data to ODM Xml ptXml                   //////    
////////////////////////////////////////////////////////////////////
	
	/**
	*	Save editor screen data to the form
	*
	* @param errMsg error messages during the saving process.
	* @return false if there is any problem
	*/
	public boolean saveEditor2Form(Vector<String> errMsg) throws Exception {
		IpssLogger.getLogger().info("NBAclfCasePanel saveEditor2Form() called");
		
		saveEditor2GSF();

		saveEditor2LODF();
		
		saveEditor2OutConfig();

		//saveEditor2TDFactor();
		//saveEditor2AreaTransfer();
		return errMsg.size() == 0;
	}

	public boolean saveEditor2GSF() {
		IpssLogger.getLogger().info("NBAclfCasePanel saveEditor2GSF() called");

		IpssScenarioHelper helper = new IpssScenarioHelper(this.odmParser);
		
		this._senXml.getGenShiftFactor().clear();
		DclfBranchSensitivityXmlType gsf = helper.createGSF(this._senXml);
		
		gsf.setSenType(SensitivityEnumType.P_ANGLE);
		
		gsf.setInjectBusType(SenBusAnalysisEnumType.SINGLE_BUS);
		SenAnalysisBusXmlType bus = helper.createSenAnalysisBus(gsf.getInjectBus());
		bus.setBusId((String)this.gsfInjectBusComboBox.getSelectedItem());
		
		gsf.setWithdrawBusType(SenBusAnalysisEnumType.LOAD_DISTRIBUTION);
		
		gsf.setMinLoadForDistFactor(helper.createActivePower(
				new Double(this.gsfLoadThreshholdTextField.getText()).doubleValue(), "MW"));

		gsf.getBranchSFactor().clear();
			
    	for (String id : RunUIUtilFunc.getJListItemAry(gsfMonitorBranchList)) {
    		if (id != null) {
        		if (id.startsWith("b:")) { // branch
        			String braId = id.substring(2);
    	    		BranchShiftFactorXmlType sf = helper.createBranchSFactor(gsf.getBranchSFactor());
    	    		BranchRefXmlType line = helper.createBranchRefXmlType();
    				sf.setBranch(line);
    				RunUIUtilFunc.setBranchIdInfo(line, braId);				
        		}
        		else {  // interface
        			String braId = id.substring(2);
    	    		InterfaceShiftFactorXmlType sf = helper.createInterfaceSFactor(gsf.getInterfaceSFactor());
    	    		FlowInterfaceRecXmlType inf = helper.createInterface();
    				sf.setInterface(inf);
    				inf.setId(braId);
        		}
    		}
    	}
		
		return true;
	}

	public boolean saveEditor2LODF() {
		IpssLogger.getLogger().info("NBAclfCasePanel saveEditor2LODF() called");
		IpssScenarioHelper helper = new IpssScenarioHelper(this.odmParser);
		
		this._senXml.getLineOutageDFactor().clear();
		LineOutageDFactorXmlType lodf = helper.createLODF(this._senXml);

		if (this.lodfMultiTypeRadioButton.isSelected()) {
			
		}
		else {  // single outage branch
	    	String braId = (String)this.lodfBranchListComboBox.getSelectedItem();
	    	BranchRefXmlType outage = helper.creatBranchRef(lodf.getOutageBranch());
			RunUIUtilFunc.setBranchIdInfo(outage, braId);
		}

		for (String id : RunUIUtilFunc.getJListItemAry(this.lodfMonitorBranchInterfaceList)) {
    		if (id.startsWith("b:")) { // branch
    			String braId = id.substring(2);
    			BranchRefXmlType monitor = helper.createMonitorBranch(lodf.getMonitorBranch());
    			RunUIUtilFunc.setBranchIdInfo(monitor, braId);
    		}
		}
		
		return true;
	}
	
	public boolean saveEditor2OutConfig() {
		IpssLogger.getLogger().info("NBAclfCasePanel saveEditor2OutConfig() called");
		IpssScenarioHelper helper = new IpssScenarioHelper(this.odmParser);
		
		SenAnalysisOutOptionXmlType outConfig = this._senXml.getOutOption();
		if (outConfig == null)
			outConfig = helper.createSenAnalysisOutConfig(this._senXml);
		
		outConfig.setAllBranchPoints(new Integer(this.outAllBranchPointsTextField.getText()));
		outConfig.setAllInterfacePoints(new Integer(this.outAllInterfacePointsTextField.getText()));
		
		return true;
	}

	public boolean saveEditor2TDFactor() {
/*		
		if (tdFactor.getInjectBusList() == null) {  // for converting old version data
			tdFactor.setInjectBusList(IpssXmlParser.getFactory().createDclfSensitivityXmlTypeInjectBusList());
			tdFactor.getInjectBusList().getInjectBus().add(IpssXmlParser.getFactory().createSenAnalysisBusRecXmlType());
		}
		if ( tdFactor.getInjectBusType() == SenBusAnalysisDataType.SINGLE_BUS) {
			while (tdFactor.getInjectBusList().getInjectBus().size() > 0)
				tdFactor.getInjectBusList().getInjectBus().remove(0);
			tdFactor.getInjectBusList().getInjectBus().add(IpssXmlParser.getFactory().createSenAnalysisBusRecXmlType());
			tdFactor.getInjectBusList().getInjectBus().get(0).setBusId((String)ptdfInjectBusComboBox.getSelectedItem());
		} 
		else {
			int cnt = ptdfInjectBusComboBox.getItemCount();
			for (int i = 0; i < cnt; i++) {
				BusRecXmlType busRec;
				if ( tdFactor.getInjectBusList().getInjectBus().size() > i )
					busRec = tdFactor.getInjectBusList().getInjectBus().get(i);
				else {
					busRec = IpssXmlParser.getFactory().createSenAnalysisBusRecXmlType();
					tdFactor.getInjectBusList().getInjectBus().add((SenAnalysisBusRecXmlType)busRec);
				}
				busRec.setBusId((String)ptdfInjectBusComboBox.getItemAt(i));
			}
		}
		
		if (ptdfWithSingleBusRadioButton.isSelected()) {
			tdFactor.setWithdrawBusType(SenBusAnalysisDataType.SINGLE_BUS);
			tdFactor.getWithdrawBusList().getWithdrawBus().get(0).setBusId((String)ptdfWithdrawBusComboBox.getSelectedItem());
			tdFactor.getWithdrawBusList().getWithdrawBus().get(0).setPercent(100.0);
		}
		else {	
			tdFactor.setWithdrawBusType(SenBusAnalysisDataType.MULTIPLE_BUS);
		}
*/		
		return true;
	}
    
	public boolean saveEditor2AreaTransfer() {
/*		
		double amt = new Double(this.atTransAmtTextField.getText()).doubleValue();
		this.areaTransfer.setTransderAmountMW(((String)this.atTransAmtUnitComboBox.getSelectedItem()).equals("MW")?
							amt : amt * _simuCtx.getNetwork().getBaseKva()/1000.0);

		String no = (String)this.atFromAreaComboBox.getSelectedItem();
		this.areaTransfer.getFromArea().setAreaNo(new Integer(no).intValue());
			
		no = (String)this.atToAreaComboBox.getSelectedItem();
		this.areaTransfer.getToArea().setAreaNo(new Integer(no).intValue());

		this.areaTransfer.setDeratingFactor(
				new Double(atDeratingFactorTextField.getText()).doubleValue());

		saveFromAreaBusList2AreaTransfer();
		saveToAreaBusList2AreaTransfer();
*/
		return true;
	}

	private void saveFromAreaBusList2AreaTransfer() {
/*
		areaTransfer.setInjectBusList(null);
		areaTransfer.setInjectBusList(IpssXmlParser.getFactory().createDclfSensitivityXmlTypeInjectBusList());
		for(int i = 0; i < atFromAreaBusList.getModel().getSize(); i++) {
			SenAnalysisBusRecXmlType busRec = IpssXmlParser.getFactory().createSenAnalysisBusRecXmlType(); 
			areaTransfer.getInjectBusList().getInjectBus().add(busRec);
			String elem = (String)atFromAreaBusList.getModel().getElementAt(i);
			busRec.setBusId(RunUIUtilFunc.getId_IdPercent(elem));
			busRec.setPercent(RunUIUtilFunc.getPercent_IdPercent(elem));
		}
*/		
	}
	
	private void saveToAreaBusList2AreaTransfer() {
/*		
		areaTransfer.setWithdrawBusList(null);
		areaTransfer.setWithdrawBusList(IpssXmlParser.getFactory().createDclfSensitivityXmlTypeWithdrawBusList());
		for(int i = 0; i < atToAreaBusList.getModel().getSize(); i++) {
			SenAnalysisBusRecXmlType busRec = IpssXmlParser.getFactory().createSenAnalysisBusRecXmlType(); 
			areaTransfer.getWithdrawBusList().getWithdrawBus().add(busRec);
			String elem = (String)atToAreaBusList.getModel().getElementAt(i);
			busRec.setBusId(RunUIUtilFunc.getId_IdPercent(elem));
			busRec.setPercent(RunUIUtilFunc.getPercent_IdPercent(elem));
		}
*/		
	}
	
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        gsfWithdrawButtonGroup = new javax.swing.ButtonGroup();
        lodfTypeButtonGroup = new javax.swing.ButtonGroup();
        runDclfTabbedPane = new javax.swing.JTabbedPane();
        gsfPanel = new javax.swing.JPanel();
        gsfInjectionPanel = new javax.swing.JPanel();
        gsfInjBusSelPanel = new javax.swing.JPanel();
        gsfGenBusLabel = new javax.swing.JLabel();
        gsfInjectBusComboBox = new javax.swing.JComboBox();
        gsfWithdrawPanel = new javax.swing.JPanel();
        gsfWithdrawiBusPanel = new javax.swing.JPanel();
        gsfLoadDFactorRadioButton = new javax.swing.JRadioButton();
        gsfLoadSingleBusRadioButton = new javax.swing.JRadioButton();
        gsfLoadMultiBusRadioButton = new javax.swing.JRadioButton();
        gsfWithdrawMarginPanel = new javax.swing.JPanel();
        gsfLoadThreshholdLabel = new javax.swing.JLabel();
        gsfLoadThreshholdTextField = new javax.swing.JTextField();
        gsfWithBusSelectPanel = new javax.swing.JPanel();
        gsfLoadBusLabel = new javax.swing.JLabel();
        gsfLoadBusComboBox = new javax.swing.JComboBox();
        space = new javax.swing.JLabel();
        gsfDistFactorTextField = new javax.swing.JTextField();
        gsfPercentLabel = new javax.swing.JLabel();
        gsfWithMultiBusPanel = new javax.swing.JPanel();
        gsfWithBusScrollPane = new javax.swing.JScrollPane();
        ptdfWithdarwBusList3 = new javax.swing.JList();
        gsfAddWithBusButton = new javax.swing.JButton();
        gsfRemoveWithBusButton = new javax.swing.JButton();
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
        lodfMonitorScrollPane = new javax.swing.JScrollPane();
        lodfMonitorBranchInterfaceList = new javax.swing.JList();
        lodfMonitorRemoveButton = new javax.swing.JButton();
        lodfAllLODFButton = new javax.swing.JButton();
        lodfSelectedLODFButton = new javax.swing.JButton();
        lodfLargetLODFButton = new javax.swing.JButton();
        ptdfPanel = new javax.swing.JPanel();
        ptdfInjectionPanel = new javax.swing.JPanel();
        ptdfInjBusPanel = new javax.swing.JPanel();
        ptdfSingleInjectBusRadioButton = new javax.swing.JRadioButton();
        ptdfMultiInjectBusRadioButton = new javax.swing.JRadioButton();
        ptdfInjBusSelPanel = new javax.swing.JPanel();
        ptdfInjectBusComboBox = new javax.swing.JComboBox();
        ptdfInjectGenBusRadioButton = new javax.swing.JRadioButton();
        ptdfInjectAllBusRadioButton = new javax.swing.JRadioButton();
        ptdfWithdrawPanel = new javax.swing.JPanel();
        ptdfSingleMultiBusPanel = new javax.swing.JPanel();
        ptdfWithSingleBusRadioButton = new javax.swing.JRadioButton();
        ptdfWithMultiBusRadioButton = new javax.swing.JRadioButton();
        ptdfWithBusSelectPanel = new javax.swing.JPanel();
        ptdfWithdrawBusComboBox = new javax.swing.JComboBox();
        ptdfWithLoadBusRadioButton = new javax.swing.JRadioButton();
        ptdfWithAllBusRadioButton = new javax.swing.JRadioButton();
        ptdfWithMultiBusPanel = new javax.swing.JPanel();
        ptdfWithBusScrollPane = new javax.swing.JScrollPane();
        ptdfWithdarwBusList = new javax.swing.JList();
        ptdfLoadDistFactorLabel = new javax.swing.JLabel();
        ptdfDistFactorTextField = new javax.swing.JTextField();
        ptdfPercentLabel = new javax.swing.JLabel();
        ptdfAddWithBusButton = new javax.swing.JButton();
        ptdfRemoveWithBusButton = new javax.swing.JButton();
        ptdfMeasBranchPanel = new javax.swing.JPanel();
        ptdfBranchListComboBox = new javax.swing.JComboBox();
        ptdfAddBranchButton = new javax.swing.JButton();
        ptdfInterfaceListComboBox = new javax.swing.JComboBox();
        ptdfAddInterfaceButton = new javax.swing.JButton();
        ptdfScrollPane = new javax.swing.JScrollPane();
        ptdfMeasBranchList = new javax.swing.JList();
        ptdfRemoveBranchButton = new javax.swing.JButton();
        ptdfCalculateButton = new javax.swing.JButton();
        areaTransPanel = new javax.swing.JPanel();
        atInfoEditPanel = new javax.swing.JPanel();
        atTransAmtLabel = new javax.swing.JLabel();
        atTransAmtTextField = new javax.swing.JTextField();
        atTransAmtUnitComboBox = new javax.swing.JComboBox();
        atFromAreaLabel = new javax.swing.JLabel();
        atFromAreaComboBox = new javax.swing.JComboBox();
        atToAreaLabel = new javax.swing.JLabel();
        atToAreaComboBox = new javax.swing.JComboBox();
        atDeratingFactorLabel = new javax.swing.JLabel();
        atDeratingFactorTextField = new javax.swing.JTextField();
        atFromDFPanel = new javax.swing.JPanel();
        atFromAreaScrollPane = new javax.swing.JScrollPane();
        atFromAreaBusList = new javax.swing.JList();
        atFromAreaUpdateButton = new javax.swing.JButton();
        atFromDFactorEditTextField = new javax.swing.JTextField();
        atFromAreaEditButton = new javax.swing.JButton();
        atFromAreaRemoveButton = new javax.swing.JButton();
        atToDFPanel = new javax.swing.JPanel();
        atToAreaScrollPane = new javax.swing.JScrollPane();
        atToAreaBusList = new javax.swing.JList();
        atToAreaEditButton = new javax.swing.JButton();
        atToDFactorEditTextField = new javax.swing.JTextField();
        atToAreaUpdateButton = new javax.swing.JButton();
        atToAreaRemoveButton = new javax.swing.JButton();
        atMeasBranchPanel = new javax.swing.JPanel();
        atBranchListComboBox = new javax.swing.JComboBox();
        atAddBranchButton = new javax.swing.JButton();
        atInterfaceListComboBox = new javax.swing.JComboBox();
        atAddInterfaceButton = new javax.swing.JButton();
        atBranchListScrollPane = new javax.swing.JScrollPane();
        atMeasBranchList = new javax.swing.JList();
        atRemoveBranchButton = new javax.swing.JButton();
        outConfigPanel = new javax.swing.JPanel();
        outAllBranchPointsLabel = new javax.swing.JLabel();
        outAllBranchPointsTextField = new javax.swing.JTextField();
        outAllInterfacePointsLabel = new javax.swing.JLabel();
        outAllInterfacePointsTextField = new javax.swing.JTextField();

        runDclfTabbedPane.setFont(new java.awt.Font("Dialog", 0, 12));
        runDclfTabbedPane.setMinimumSize(new java.awt.Dimension(80, 48));
        runDclfTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                panelSelectionChanged(evt);
            }
        });

        gsfInjectionPanel.setLayout(new java.awt.GridBagLayout());

        gsfGenBusLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        gsfGenBusLabel.setText("Gen Bus    ");
        gsfInjBusSelPanel.add(gsfGenBusLabel);

        gsfInjectBusComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        gsfInjectBusComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gsfInjBusSelPanel.add(gsfInjectBusComboBox);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gsfInjectionPanel.add(gsfInjBusSelPanel, gridBagConstraints);

        gsfWithdrawPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Load Bus", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        gsfWithdrawPanel.setLayout(new java.awt.GridBagLayout());

        gsfWithdrawButtonGroup.add(gsfLoadDFactorRadioButton);
        gsfLoadDFactorRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        gsfLoadDFactorRadioButton.setSelected(true);
        gsfLoadDFactorRadioButton.setText("DFactor");
        gsfLoadDFactorRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gsfLoadDFactorRadioButtonActionPerformed(evt);
            }
        });
        gsfWithdrawiBusPanel.add(gsfLoadDFactorRadioButton);

        gsfWithdrawButtonGroup.add(gsfLoadSingleBusRadioButton);
        gsfLoadSingleBusRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        gsfLoadSingleBusRadioButton.setText("Single");
        gsfLoadSingleBusRadioButton.setEnabled(false);
        gsfLoadSingleBusRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gsfLoadSingleBusRadioButtonActionPerformed(evt);
            }
        });
        gsfWithdrawiBusPanel.add(gsfLoadSingleBusRadioButton);

        gsfWithdrawButtonGroup.add(gsfLoadMultiBusRadioButton);
        gsfLoadMultiBusRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        gsfLoadMultiBusRadioButton.setText("Multi");
        gsfLoadMultiBusRadioButton.setEnabled(false);
        gsfLoadMultiBusRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gsfLoadMultiBusRadioButtonActionPerformed(evt);
            }
        });
        gsfWithdrawiBusPanel.add(gsfLoadMultiBusRadioButton);

        gsfWithdrawPanel.add(gsfWithdrawiBusPanel, new java.awt.GridBagConstraints());

        gsfLoadThreshholdLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        gsfLoadThreshholdLabel.setText("Threshhold (Mw)   ");

        gsfLoadThreshholdTextField.setColumns(3);
        gsfLoadThreshholdTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        gsfLoadThreshholdTextField.setText("5.0");

        org.jdesktop.layout.GroupLayout gsfWithdrawMarginPanelLayout = new org.jdesktop.layout.GroupLayout(gsfWithdrawMarginPanel);
        gsfWithdrawMarginPanel.setLayout(gsfWithdrawMarginPanelLayout);
        gsfWithdrawMarginPanelLayout.setHorizontalGroup(
            gsfWithdrawMarginPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(gsfWithdrawMarginPanelLayout.createSequentialGroup()
                .add(5, 5, 5)
                .add(gsfLoadThreshholdLabel)
                .add(5, 5, 5)
                .add(gsfLoadThreshholdTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        gsfWithdrawMarginPanelLayout.setVerticalGroup(
            gsfWithdrawMarginPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(gsfWithdrawMarginPanelLayout.createSequentialGroup()
                .add(8, 8, 8)
                .add(gsfLoadThreshholdLabel))
            .add(gsfWithdrawMarginPanelLayout.createSequentialGroup()
                .add(5, 5, 5)
                .add(gsfLoadThreshholdTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        gsfWithdrawPanel.add(gsfWithdrawMarginPanel, gridBagConstraints);

        gsfLoadBusLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        gsfLoadBusLabel.setText("Load Bus    ");
        gsfLoadBusLabel.setEnabled(false);
        gsfWithBusSelectPanel.add(gsfLoadBusLabel);

        gsfLoadBusComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        gsfLoadBusComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gsfLoadBusComboBox.setEnabled(false);
        gsfWithBusSelectPanel.add(gsfLoadBusComboBox);

        space.setText("  ");
        gsfWithBusSelectPanel.add(space);

        gsfDistFactorTextField.setText("100.0");
        gsfDistFactorTextField.setEnabled(false);
        gsfWithBusSelectPanel.add(gsfDistFactorTextField);

        gsfPercentLabel.setText("%");
        gsfPercentLabel.setEnabled(false);
        gsfWithBusSelectPanel.add(gsfPercentLabel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gsfWithdrawPanel.add(gsfWithBusSelectPanel, gridBagConstraints);

        ptdfWithdarwBusList3.setFont(new java.awt.Font("Dialog", 0, 12));
        ptdfWithdarwBusList3.setEnabled(false);
        gsfWithBusScrollPane.setViewportView(ptdfWithdarwBusList3);

        gsfAddWithBusButton.setFont(new java.awt.Font("Dialog", 0, 10));
        gsfAddWithBusButton.setText("Add");
        gsfAddWithBusButton.setEnabled(false);
        gsfAddWithBusButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gsfAddWithBusButtonActionPerformed(evt);
            }
        });

        gsfRemoveWithBusButton.setFont(new java.awt.Font("Dialog", 0, 10));
        gsfRemoveWithBusButton.setText("Remove");
        gsfRemoveWithBusButton.setEnabled(false);
        gsfRemoveWithBusButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gsfRemoveWithBusButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout gsfWithMultiBusPanelLayout = new org.jdesktop.layout.GroupLayout(gsfWithMultiBusPanel);
        gsfWithMultiBusPanel.setLayout(gsfWithMultiBusPanelLayout);
        gsfWithMultiBusPanelLayout.setHorizontalGroup(
            gsfWithMultiBusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(gsfWithMultiBusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(gsfWithMultiBusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(gsfWithMultiBusPanelLayout.createSequentialGroup()
                        .add(gsfAddWithBusButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(gsfRemoveWithBusButton))
                    .add(gsfWithBusScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 153, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        gsfWithMultiBusPanelLayout.setVerticalGroup(
            gsfWithMultiBusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(gsfWithMultiBusPanelLayout.createSequentialGroup()
                .add(gsfWithBusScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 96, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gsfWithMultiBusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(gsfAddWithBusButton)
                    .add(gsfRemoveWithBusButton))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gsfWithdrawPanel.add(gsfWithMultiBusPanel, gridBagConstraints);

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
            .add(org.jdesktop.layout.GroupLayout.TRAILING, gsfMonitorBranchPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(gsfMonitorBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(gsfMonitorAddBranchButton)
                    .add(gsfMonitorAddInterfaceButton)
                    .add(gsfMonitorRemoveBranchButton)
                    .add(gsfMonitorInterfaceListComboBox, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(gsfMonitorScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                    .add(gsfMonitorBranchListComboBox, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .add(gsfMonitorScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 101, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gsfMonitorRemoveBranchButton)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gsfSelectedGSFButton.setFont(new java.awt.Font("Dialog", 0, 12));
        gsfSelectedGSFButton.setText("Selected GSF");
        gsfSelectedGSFButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gsfSelectedGSFButtonActionPerformed(evt);
            }
        });

        gsfAllBranchGSFButton.setFont(new java.awt.Font("Dialog", 0, 12));
        gsfAllBranchGSFButton.setText("All Branch GSF");
        gsfAllBranchGSFButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gsfAllBranchGSFButtonActionPerformed(evt);
            }
        });

        gsfLargetBranchGSFButton.setFont(new java.awt.Font("Dialog", 0, 12));
        gsfLargetBranchGSFButton.setText("Larget Branch GSF");
        gsfLargetBranchGSFButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gsfLargetBranchGSFButtonActionPerformed(evt);
            }
        });

        gsfAllInterfaceGSFButton.setFont(new java.awt.Font("Dialog", 0, 12));
        gsfAllInterfaceGSFButton.setText("All Interface GSF");
        gsfAllInterfaceGSFButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gsfAllInterfaceGSFButtonActionPerformed(evt);
            }
        });

        gsfLargetInterfaceGSFButton.setFont(new java.awt.Font("Dialog", 0, 12));
        gsfLargetInterfaceGSFButton.setText("Largest Interface GSF");
        gsfLargetInterfaceGSFButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gsfLargetInterfaceGSFButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout gsfPanelLayout = new org.jdesktop.layout.GroupLayout(gsfPanel);
        gsfPanel.setLayout(gsfPanelLayout);
        gsfPanelLayout.setHorizontalGroup(
            gsfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, gsfPanelLayout.createSequentialGroup()
                .add(gsfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(gsfPanelLayout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(gsfInjectionPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE))
                    .add(gsfPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(gsfWithdrawPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 237, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(gsfMonitorBranchPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(4, 4, 4))
            .add(gsfPanelLayout.createSequentialGroup()
                .add(51, 51, 51)
                .add(gsfSelectedGSFButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gsfLargetBranchGSFButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gsfAllBranchGSFButton)
                .addContainerGap(68, Short.MAX_VALUE))
            .add(gsfPanelLayout.createSequentialGroup()
                .add(99, 99, 99)
                .add(gsfLargetInterfaceGSFButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gsfAllInterfaceGSFButton)
                .addContainerGap(111, Short.MAX_VALUE))
        );
        gsfPanelLayout.setVerticalGroup(
            gsfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(gsfPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(gsfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(gsfPanelLayout.createSequentialGroup()
                        .add(gsfInjectionPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(gsfWithdrawPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(gsfMonitorBranchPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(gsfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(gsfLargetBranchGSFButton)
                    .add(gsfSelectedGSFButton)
                    .add(gsfAllBranchGSFButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(gsfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(gsfLargetInterfaceGSFButton)
                    .add(gsfAllInterfaceGSFButton))
                .addContainerGap(26, Short.MAX_VALUE))
        );

        runDclfTabbedPane.addTab("GSF", gsfPanel);

        lodfPanel.setEnabled(false);

        lodfTypePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Outage Type", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N

        lodfTypeButtonGroup.add(lodfSingleTypeRadioButton);
        lodfSingleTypeRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        lodfSingleTypeRadioButton.setSelected(true);
        lodfSingleTypeRadioButton.setText("Single Branch");
        lodfSingleTypeRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lodfSingleTypeRadioButtonActionPerformed(evt);
            }
        });

        lodfTypeButtonGroup.add(lodfMultiTypeRadioButton);
        lodfMultiTypeRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        lodfMultiTypeRadioButton.setText("Multii-Branch");
        lodfMultiTypeRadioButton.setEnabled(false);
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
                        .add(lodfOutageBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lodfOutageBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, lodfMultiBranchScrollPane, 0, 0, Short.MAX_VALUE)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, lodfBranchListComboBox, 0, 208, Short.MAX_VALUE))
                            .add(lodfOutageBranchPanelLayout.createSequentialGroup()
                                .add(lodfAddBranchButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(lodfRemoveBranchButton)))))
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
                .add(lodfMultiBranchScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 108, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lodfOutageBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lodfAddBranchButton)
                    .add(lodfRemoveBranchButton))
                .addContainerGap())
        );

        lodfMonitorBranchPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Monitor Branch", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N

        lodfMonitorBranchListComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        lodfMonitorBranchListComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lodfMonitorAddBranchButton.setFont(new java.awt.Font("Dialog", 0, 10));
        lodfMonitorAddBranchButton.setText("Add Branch");
        lodfMonitorAddBranchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lodfMonitorAddBranchButtonActionPerformed(evt);
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
                    .add(lodfMonitorBranchListComboBox, 0, 199, Short.MAX_VALUE)
                    .add(lodfMonitorBranchPanelLayout.createSequentialGroup()
                        .add(lodfMonitorAddBranchButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lodfMonitorRemoveButton))
                    .add(lodfMonitorScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 199, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        lodfMonitorBranchPanelLayout.setVerticalGroup(
            lodfMonitorBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lodfMonitorBranchPanelLayout.createSequentialGroup()
                .add(lodfMonitorBranchListComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(lodfMonitorScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lodfMonitorBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lodfMonitorAddBranchButton)
                    .add(lodfMonitorRemoveButton))
                .addContainerGap())
        );

        lodfAllLODFButton.setFont(new java.awt.Font("Dialog", 0, 12));
        lodfAllLODFButton.setText("All LODF");
        lodfAllLODFButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lodfAllLODFButtonActionPerformed(evt);
            }
        });

        lodfSelectedLODFButton.setFont(new java.awt.Font("Dialog", 0, 12));
        lodfSelectedLODFButton.setText("Selected LODF");
        lodfSelectedLODFButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lodfSelectedLODFButtonActionPerformed(evt);
            }
        });

        lodfLargetLODFButton.setFont(new java.awt.Font("Dialog", 0, 12));
        lodfLargetLODFButton.setText("Largest LODF");
        lodfLargetLODFButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lodfLargestLODFButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout lodfPanelLayout = new org.jdesktop.layout.GroupLayout(lodfPanel);
        lodfPanel.setLayout(lodfPanelLayout);
        lodfPanelLayout.setHorizontalGroup(
            lodfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lodfPanelLayout.createSequentialGroup()
                .add(lodfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lodfPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(lodfOutageBranchPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(lodfMonitorBranchPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 223, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(lodfPanelLayout.createSequentialGroup()
                        .add(68, 68, 68)
                        .add(lodfLargetLODFButton)
                        .add(18, 18, 18)
                        .add(lodfSelectedLODFButton)
                        .add(18, 18, 18)
                        .add(lodfAllLODFButton)))
                .addContainerGap(11, Short.MAX_VALUE))
        );
        lodfPanelLayout.setVerticalGroup(
            lodfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, lodfPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(lodfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lodfOutageBranchPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lodfMonitorBranchPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(40, 40, 40)
                .add(lodfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lodfSelectedLODFButton)
                    .add(lodfLargetLODFButton)
                    .add(lodfAllLODFButton))
                .add(19, 19, 19))
        );

        runDclfTabbedPane.addTab("LODF", lodfPanel);

        ptdfPanel.setEnabled(false);

        ptdfInjectionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Injection Bus", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 12))); // NOI18N
        ptdfInjectionPanel.setLayout(new java.awt.GridBagLayout());

        ptdfSingleInjectBusRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        ptdfSingleInjectBusRadioButton.setText("Single Bus");
        ptdfSingleInjectBusRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ptdfSingleInjectBusRadioButtonActionPerformed(evt);
            }
        });
        ptdfInjBusPanel.add(ptdfSingleInjectBusRadioButton);

        ptdfMultiInjectBusRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        ptdfMultiInjectBusRadioButton.setText("All Gen Buses");
        ptdfMultiInjectBusRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ptdfMultiInjectBusRadioButtonActionPerformed(evt);
            }
        });
        ptdfInjBusPanel.add(ptdfMultiInjectBusRadioButton);

        ptdfInjectionPanel.add(ptdfInjBusPanel, new java.awt.GridBagConstraints());

        ptdfInjectBusComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        ptdfInjectBusComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        ptdfInjBusSelPanel.add(ptdfInjectBusComboBox);

        ptdfInjectGenBusRadioButton.setFont(new java.awt.Font("Dialog", 0, 10));
        ptdfInjectGenBusRadioButton.setText("Gen Buses");
        ptdfInjectGenBusRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ptdfInjectGenBusRadioButtonActionPerformed(evt);
            }
        });
        ptdfInjBusSelPanel.add(ptdfInjectGenBusRadioButton);

        ptdfInjectAllBusRadioButton.setFont(new java.awt.Font("Dialog", 0, 10));
        ptdfInjectAllBusRadioButton.setText("All Buses");
        ptdfInjectAllBusRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ptdfInjectAllBusRadioButtonActionPerformed(evt);
            }
        });
        ptdfInjBusSelPanel.add(ptdfInjectAllBusRadioButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        ptdfInjectionPanel.add(ptdfInjBusSelPanel, gridBagConstraints);

        ptdfWithdrawPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "WithdrawBus(es)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 12))); // NOI18N
        ptdfWithdrawPanel.setLayout(new java.awt.GridBagLayout());

        ptdfWithSingleBusRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        ptdfWithSingleBusRadioButton.setText("Single Bus");
        ptdfWithSingleBusRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ptdfWithSingleBusRadioButtonActionPerformed(evt);
            }
        });
        ptdfSingleMultiBusPanel.add(ptdfWithSingleBusRadioButton);

        ptdfWithMultiBusRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        ptdfWithMultiBusRadioButton.setText("Multi-Buses");
        ptdfWithMultiBusRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ptdfWithMultiBusRadioButtonActionPerformed(evt);
            }
        });
        ptdfSingleMultiBusPanel.add(ptdfWithMultiBusRadioButton);

        ptdfWithdrawPanel.add(ptdfSingleMultiBusPanel, new java.awt.GridBagConstraints());

        ptdfWithdrawBusComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        ptdfWithdrawBusComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        ptdfWithBusSelectPanel.add(ptdfWithdrawBusComboBox);

        ptdfWithLoadBusRadioButton.setFont(new java.awt.Font("Dialog", 0, 10));
        ptdfWithLoadBusRadioButton.setText("Load ");
        ptdfWithLoadBusRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ptdfWithLoadBusRadioButtonActionPerformed(evt);
            }
        });
        ptdfWithBusSelectPanel.add(ptdfWithLoadBusRadioButton);

        ptdfWithAllBusRadioButton.setFont(new java.awt.Font("Dialog", 0, 10));
        ptdfWithAllBusRadioButton.setText("All Buses");
        ptdfWithAllBusRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ptdfWithAllBusRadioButtonActionPerformed(evt);
            }
        });
        ptdfWithBusSelectPanel.add(ptdfWithAllBusRadioButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        ptdfWithdrawPanel.add(ptdfWithBusSelectPanel, gridBagConstraints);

        ptdfWithdarwBusList.setFont(new java.awt.Font("Dialog", 0, 12));
        ptdfWithdarwBusList.setEnabled(false);
        ptdfWithBusScrollPane.setViewportView(ptdfWithdarwBusList);

        ptdfLoadDistFactorLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        ptdfLoadDistFactorLabel.setText("Load Disttribution Factor");
        ptdfLoadDistFactorLabel.setEnabled(false);

        ptdfDistFactorTextField.setText("100.0");
        ptdfDistFactorTextField.setEnabled(false);

        ptdfPercentLabel.setText("%");
        ptdfPercentLabel.setEnabled(false);

        ptdfAddWithBusButton.setFont(new java.awt.Font("Dialog", 0, 10));
        ptdfAddWithBusButton.setText("Add");
        ptdfAddWithBusButton.setEnabled(false);
        ptdfAddWithBusButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ptdfAddWithBusButtonActionPerformed(evt);
            }
        });

        ptdfRemoveWithBusButton.setFont(new java.awt.Font("Dialog", 0, 10));
        ptdfRemoveWithBusButton.setText("Remove");
        ptdfRemoveWithBusButton.setEnabled(false);
        ptdfRemoveWithBusButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ptdfRemoveWithBusButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout ptdfWithMultiBusPanelLayout = new org.jdesktop.layout.GroupLayout(ptdfWithMultiBusPanel);
        ptdfWithMultiBusPanel.setLayout(ptdfWithMultiBusPanelLayout);
        ptdfWithMultiBusPanelLayout.setHorizontalGroup(
            ptdfWithMultiBusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ptdfWithMultiBusPanelLayout.createSequentialGroup()
                .add(ptdfWithMultiBusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(ptdfWithMultiBusPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(ptdfLoadDistFactorLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(ptdfDistFactorTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(ptdfPercentLabel))
                    .add(ptdfWithMultiBusPanelLayout.createSequentialGroup()
                        .add(32, 32, 32)
                        .add(ptdfWithMultiBusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(ptdfWithMultiBusPanelLayout.createSequentialGroup()
                                .add(ptdfAddWithBusButton)
                                .add(35, 35, 35)
                                .add(ptdfRemoveWithBusButton))
                            .add(ptdfWithBusScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 133, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        ptdfWithMultiBusPanelLayout.setVerticalGroup(
            ptdfWithMultiBusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ptdfWithMultiBusPanelLayout.createSequentialGroup()
                .add(ptdfWithMultiBusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(ptdfLoadDistFactorLabel)
                    .add(ptdfDistFactorTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(ptdfPercentLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ptdfWithBusScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 55, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(ptdfWithMultiBusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(ptdfRemoveWithBusButton)
                    .add(ptdfAddWithBusButton))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        ptdfWithdrawPanel.add(ptdfWithMultiBusPanel, gridBagConstraints);

        ptdfMeasBranchPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Measurement Branches", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 12))); // NOI18N

        ptdfBranchListComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        ptdfBranchListComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        ptdfAddBranchButton.setFont(new java.awt.Font("Dialog", 0, 10));
        ptdfAddBranchButton.setText("Add Branch");
        ptdfAddBranchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ptdfAddBranchButtonActionPerformed(evt);
            }
        });

        ptdfInterfaceListComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        ptdfInterfaceListComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        ptdfInterfaceListComboBox.setEnabled(false);

        ptdfAddInterfaceButton.setFont(new java.awt.Font("Dialog", 0, 10));
        ptdfAddInterfaceButton.setText("Add Interface");
        ptdfAddInterfaceButton.setEnabled(false);
        ptdfAddInterfaceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ptdfAddInterfaceButtonActionPerformed(evt);
            }
        });

        ptdfMeasBranchList.setFont(new java.awt.Font("Dialog", 0, 12));
        ptdfScrollPane.setViewportView(ptdfMeasBranchList);

        ptdfRemoveBranchButton.setFont(new java.awt.Font("Dialog", 0, 10));
        ptdfRemoveBranchButton.setText("Remove");
        ptdfRemoveBranchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ptdfRemoveBranchButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout ptdfMeasBranchPanelLayout = new org.jdesktop.layout.GroupLayout(ptdfMeasBranchPanel);
        ptdfMeasBranchPanel.setLayout(ptdfMeasBranchPanelLayout);
        ptdfMeasBranchPanelLayout.setHorizontalGroup(
            ptdfMeasBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ptdfMeasBranchPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(ptdfMeasBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(ptdfBranchListComboBox, 0, 138, Short.MAX_VALUE)
                    .add(ptdfScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 138, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(ptdfAddBranchButton)
                    .add(ptdfRemoveBranchButton)
                    .add(ptdfInterfaceListComboBox, 0, 138, Short.MAX_VALUE)
                    .add(ptdfAddInterfaceButton))
                .addContainerGap())
        );
        ptdfMeasBranchPanelLayout.setVerticalGroup(
            ptdfMeasBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ptdfMeasBranchPanelLayout.createSequentialGroup()
                .add(ptdfBranchListComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ptdfAddBranchButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ptdfInterfaceListComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ptdfAddInterfaceButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ptdfScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 140, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ptdfRemoveBranchButton)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        ptdfCalculateButton.setFont(new java.awt.Font("Dialog", 0, 10));
        ptdfCalculateButton.setText("Calculate");
        ptdfCalculateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ptdfCalculateButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout ptdfPanelLayout = new org.jdesktop.layout.GroupLayout(ptdfPanel);
        ptdfPanel.setLayout(ptdfPanelLayout);
        ptdfPanelLayout.setHorizontalGroup(
            ptdfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ptdfPanelLayout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .add(ptdfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, ptdfPanelLayout.createSequentialGroup()
                        .add(ptdfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(ptdfWithdrawPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                            .add(ptdfInjectionPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(ptdfMeasBranchPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(51, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, ptdfPanelLayout.createSequentialGroup()
                        .add(ptdfCalculateButton)
                        .add(200, 200, 200))))
        );
        ptdfPanelLayout.setVerticalGroup(
            ptdfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ptdfPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(ptdfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(ptdfPanelLayout.createSequentialGroup()
                        .add(ptdfInjectionPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(ptdfWithdrawPanel, 0, 0, Short.MAX_VALUE))
                    .add(ptdfMeasBranchPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 32, Short.MAX_VALUE)
                .add(ptdfCalculateButton)
                .add(26, 26, 26))
        );

        runDclfTabbedPane.addTab("PTDF", ptdfPanel);

        areaTransPanel.setEnabled(false);

        atTransAmtLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        atTransAmtLabel.setText("Transfer Amount");

        atTransAmtTextField.setColumns(5);
        atTransAmtTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        atTransAmtTextField.setText("100.0");

        atTransAmtUnitComboBox.setFont(new java.awt.Font("Dialog", 0, 10));
        atTransAmtUnitComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "MW", "PU" }));

        atFromAreaLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        atFromAreaLabel.setText("From Area   ");

        atFromAreaComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        atFromAreaComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1" }));
        atFromAreaComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                atFromAreaComboBoxActionPerformed(evt);
            }
        });

        atToAreaLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        atToAreaLabel.setText("To Area   ");

        atToAreaComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        atToAreaComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1" }));
        atToAreaComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                atToAreaComboBoxActionPerformed(evt);
            }
        });

        atDeratingFactorLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        atDeratingFactorLabel.setText("Derating Factor");

        atDeratingFactorTextField.setColumns(3);
        atDeratingFactorTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        atDeratingFactorTextField.setText("1.00");

        atFromDFPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "From Area DistFactor", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 12))); // NOI18N

        atFromAreaBusList.setFont(new java.awt.Font("Dialog", 0, 12));
        atFromAreaScrollPane.setViewportView(atFromAreaBusList);

        atFromAreaUpdateButton.setFont(new java.awt.Font("Dialog", 0, 10));
        atFromAreaUpdateButton.setText("U");
        atFromAreaUpdateButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        atFromAreaUpdateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                atFromAreaUpdateButtonActionPerformed(evt);
            }
        });

        atFromDFactorEditTextField.setColumns(5);
        atFromDFactorEditTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        atFromDFactorEditTextField.setText("100.0");

        atFromAreaEditButton.setFont(new java.awt.Font("Dialog", 0, 10));
        atFromAreaEditButton.setText("Edit");
        atFromAreaEditButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                atFromAreaEditButtonActionPerformed(evt);
            }
        });

        atFromAreaRemoveButton.setFont(new java.awt.Font("Dialog", 0, 10));
        atFromAreaRemoveButton.setText("Remove");
        atFromAreaRemoveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                atFromAreaRemoveButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout atFromDFPanelLayout = new org.jdesktop.layout.GroupLayout(atFromDFPanel);
        atFromDFPanel.setLayout(atFromDFPanelLayout);
        atFromDFPanelLayout.setHorizontalGroup(
            atFromDFPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(atFromDFPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(atFromAreaScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 133, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(atFromDFPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(atFromDFPanelLayout.createSequentialGroup()
                        .add(atFromDFactorEditTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(atFromAreaUpdateButton))
                    .add(atFromAreaRemoveButton)
                    .add(atFromAreaEditButton))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        atFromDFPanelLayout.setVerticalGroup(
            atFromDFPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(atFromDFPanelLayout.createSequentialGroup()
                .add(atFromDFPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(atFromDFPanelLayout.createSequentialGroup()
                        .add(atFromAreaEditButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(atFromDFactorEditTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(atFromAreaUpdateButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(atFromAreaRemoveButton))
            .add(atFromAreaScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 69, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        atToDFPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("To Area DistFactor"));

        atToAreaBusList.setFont(new java.awt.Font("Dialog", 0, 12));
        atToAreaScrollPane.setViewportView(atToAreaBusList);

        atToAreaEditButton.setFont(new java.awt.Font("Dialog", 0, 10));
        atToAreaEditButton.setText("Edit");
        atToAreaEditButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                atToAreaEditButtonActionPerformed(evt);
            }
        });

        atToDFactorEditTextField.setColumns(5);
        atToDFactorEditTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        atToDFactorEditTextField.setText("100.0");

        atToAreaUpdateButton.setFont(new java.awt.Font("Dialog", 0, 10));
        atToAreaUpdateButton.setText("U");
        atToAreaUpdateButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        atToAreaUpdateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                atToAreaUpdateButtonActionPerformed(evt);
            }
        });

        atToAreaRemoveButton.setFont(new java.awt.Font("Dialog", 0, 10));
        atToAreaRemoveButton.setText("Remove");
        atToAreaRemoveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                atToAreaRemoveButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout atToDFPanelLayout = new org.jdesktop.layout.GroupLayout(atToDFPanel);
        atToDFPanel.setLayout(atToDFPanelLayout);
        atToDFPanelLayout.setHorizontalGroup(
            atToDFPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, atToDFPanelLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(atToAreaScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 133, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(atToDFPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(atToAreaEditButton)
                    .add(atToDFPanelLayout.createSequentialGroup()
                        .add(atToDFactorEditTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(atToAreaUpdateButton))
                    .add(atToAreaRemoveButton))
                .addContainerGap())
        );
        atToDFPanelLayout.setVerticalGroup(
            atToDFPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(atToDFPanelLayout.createSequentialGroup()
                .add(atToDFPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(atToAreaScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 96, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(atToDFPanelLayout.createSequentialGroup()
                        .add(atToAreaEditButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(atToDFPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(atToDFactorEditTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(atToAreaUpdateButton))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(atToAreaRemoveButton)))
                .addContainerGap())
        );

        atMeasBranchPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Measurement Branches", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 12))); // NOI18N

        atBranchListComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        atBranchListComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        atAddBranchButton.setFont(new java.awt.Font("Dialog", 0, 10));
        atAddBranchButton.setText("Add Branch");
        atAddBranchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                atAddBranchButtonActionPerformed(evt);
            }
        });

        atInterfaceListComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        atInterfaceListComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        atInterfaceListComboBox.setEnabled(false);

        atAddInterfaceButton.setFont(new java.awt.Font("Dialog", 0, 10));
        atAddInterfaceButton.setText("Add Interface");
        atAddInterfaceButton.setEnabled(false);
        atAddInterfaceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                atAddInterfaceButtonActionPerformed(evt);
            }
        });

        atMeasBranchList.setFont(new java.awt.Font("Dialog", 0, 12));
        atBranchListScrollPane.setViewportView(atMeasBranchList);

        atRemoveBranchButton.setFont(new java.awt.Font("Dialog", 0, 10));
        atRemoveBranchButton.setText("Remove");
        atRemoveBranchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                atRemoveBranchButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout atMeasBranchPanelLayout = new org.jdesktop.layout.GroupLayout(atMeasBranchPanel);
        atMeasBranchPanel.setLayout(atMeasBranchPanelLayout);
        atMeasBranchPanelLayout.setHorizontalGroup(
            atMeasBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(atMeasBranchPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(atMeasBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(atBranchListScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 138, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(atBranchListComboBox, 0, 138, Short.MAX_VALUE)
                    .add(atAddBranchButton)
                    .add(atInterfaceListComboBox, 0, 138, Short.MAX_VALUE)
                    .add(atAddInterfaceButton)
                    .add(atRemoveBranchButton))
                .addContainerGap())
        );
        atMeasBranchPanelLayout.setVerticalGroup(
            atMeasBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(atMeasBranchPanelLayout.createSequentialGroup()
                .add(atBranchListComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(atAddBranchButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(atInterfaceListComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(atAddInterfaceButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(atBranchListScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 79, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(atRemoveBranchButton)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout atInfoEditPanelLayout = new org.jdesktop.layout.GroupLayout(atInfoEditPanel);
        atInfoEditPanel.setLayout(atInfoEditPanelLayout);
        atInfoEditPanelLayout.setHorizontalGroup(
            atInfoEditPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(atInfoEditPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(atInfoEditPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(atInfoEditPanelLayout.createSequentialGroup()
                        .add(atInfoEditPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(atInfoEditPanelLayout.createSequentialGroup()
                                .add(111, 111, 111)
                                .add(atDeratingFactorLabel)
                                .add(29, 29, 29)
                                .add(atDeratingFactorTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(atFromDFPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(atToDFPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(atMeasBranchPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(24, Short.MAX_VALUE))
                    .add(atInfoEditPanelLayout.createSequentialGroup()
                        .add(atTransAmtLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(atTransAmtTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(atTransAmtUnitComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 42, Short.MAX_VALUE)
                        .add(atFromAreaLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(atFromAreaComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(atToAreaLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(atToAreaComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(25, 25, 25))))
        );
        atInfoEditPanelLayout.setVerticalGroup(
            atInfoEditPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, atInfoEditPanelLayout.createSequentialGroup()
                .add(atInfoEditPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(atTransAmtLabel)
                    .add(atTransAmtTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(atTransAmtUnitComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(atFromAreaLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
                    .add(atFromAreaComboBox)
                    .add(atToAreaLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
                    .add(atToAreaComboBox))
                .add(atInfoEditPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(atInfoEditPanelLayout.createSequentialGroup()
                        .add(15, 15, 15)
                        .add(atInfoEditPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(atDeratingFactorLabel)
                            .add(atDeratingFactorTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(atFromDFPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(atToDFPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 123, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, atInfoEditPanelLayout.createSequentialGroup()
                        .add(6, 6, 6)
                        .add(atMeasBranchPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(42, 42, 42))
        );

        org.jdesktop.layout.GroupLayout areaTransPanelLayout = new org.jdesktop.layout.GroupLayout(areaTransPanel);
        areaTransPanel.setLayout(areaTransPanelLayout);
        areaTransPanelLayout.setHorizontalGroup(
            areaTransPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(areaTransPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(atInfoEditPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        areaTransPanelLayout.setVerticalGroup(
            areaTransPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(areaTransPanelLayout.createSequentialGroup()
                .add(20, 20, 20)
                .add(atInfoEditPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(67, 67, 67))
        );

        runDclfTabbedPane.addTab("Area Transfer", areaTransPanel);

        outConfigPanel.setEnabled(false);

        outAllBranchPointsLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        outAllBranchPointsLabel.setText("All Branch Output Points");

        outAllBranchPointsTextField.setColumns(5);
        outAllBranchPointsTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        outAllBranchPointsTextField.setText("50");

        outAllInterfacePointsLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        outAllInterfacePointsLabel.setText("All Interface Output Points");

        outAllInterfacePointsTextField.setColumns(5);
        outAllInterfacePointsTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        outAllInterfacePointsTextField.setText("10");

        org.jdesktop.layout.GroupLayout outConfigPanelLayout = new org.jdesktop.layout.GroupLayout(outConfigPanel);
        outConfigPanel.setLayout(outConfigPanelLayout);
        outConfigPanelLayout.setHorizontalGroup(
            outConfigPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(outConfigPanelLayout.createSequentialGroup()
                .add(46, 46, 46)
                .add(outConfigPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(outAllBranchPointsLabel)
                    .add(outAllInterfacePointsLabel))
                .add(44, 44, 44)
                .add(outConfigPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(outAllInterfacePointsTextField, 0, 0, Short.MAX_VALUE)
                    .add(outAllBranchPointsTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE))
                .addContainerGap(224, Short.MAX_VALUE))
        );
        outConfigPanelLayout.setVerticalGroup(
            outConfigPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(outConfigPanelLayout.createSequentialGroup()
                .add(33, 33, 33)
                .add(outConfigPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(outAllBranchPointsLabel)
                    .add(outAllBranchPointsTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(outConfigPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(outAllInterfacePointsLabel)
                    .add(outAllInterfacePointsTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(327, Short.MAX_VALUE))
        );

        runDclfTabbedPane.addTab("OutConfig", outConfigPanel);

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
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(runDclfTabbedPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 445, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(58, 58, 58))
        );
    }// </editor-fold>//GEN-END:initComponents
 
    private void panelSelectionChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_panelSelectionChanged
    	if ( runDclfTabbedPane.getSelectedIndex() == 0 ) {
        	IpssLogger.getLogger().info("Panel selection changed - GF Panel");
        	initGsfTabPanel();
    	}
    	else if ( runDclfTabbedPane.getSelectedIndex() == 1 ) {
        	IpssLogger.getLogger().info("Panel selection changed - LODF Panel");
    	    initLodfTabPanel();
    	}	
    }//GEN-LAST:event_panelSelectionChanged

/*888888888888888888888
 *  GSF    
 8888888888888888888888*/

    private void gsfLoadDFactorRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsfLoadDFactorRadioButtonActionPerformed
        IpssLogger.getLogger().info("gsfLoadDFactorRadioButtonActionPerformed() called");
        setLoadDFactorRadioButton(this.gsfLoadDFactorRadioButton.isSelected());
    }//GEN-LAST:event_gsfLoadDFactorRadioButtonActionPerformed

    private void setLoadDFactorRadioButton(boolean checked) {
    	this.gsfLoadThreshholdLabel.setEnabled(checked);
    	this.gsfLoadThreshholdTextField.setEnabled(checked);
    }
    
    private void gsfLoadSingleBusRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsfLoadSingleBusRadioButtonActionPerformed
        IpssLogger.getLogger().info("gsfLoadSingleBusRadioButtonActionPerformed() called");
    }//GEN-LAST:event_gsfLoadSingleBusRadioButtonActionPerformed

    private void gsfLoadMultiBusRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsfLoadMultiBusRadioButtonActionPerformed
        IpssLogger.getLogger().info("gsfLoadMultiBusRadioButtonActionPerformed() called");
    }//GEN-LAST:event_gsfLoadMultiBusRadioButtonActionPerformed

    private void gsfAddWithBusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsfAddWithBusButtonActionPerformed
        IpssLogger.getLogger().info("gsfAddWithBusButtonActionPerformed() called");
    }//GEN-LAST:event_gsfAddWithBusButtonActionPerformed

    private void gsfRemoveWithBusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsfRemoveWithBusButtonActionPerformed
        IpssLogger.getLogger().info("gsfRemoveWithBusButtonActionPerformed() called");
    }//GEN-LAST:event_gsfRemoveWithBusButtonActionPerformed

    private void gsfMonitorAddBranchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsfMonitorAddBranchButtonActionPerformed
        IpssLogger.getLogger().info("gsfAddBranchButtonActionPerformed() called");
    	String id = (String)gsfMonitorBranchListComboBox.getSelectedItem();
    	RunUIUtilFunc.addItemJList(gsfMonitorBranchList, "b:"+id);
    }//GEN-LAST:event_gsfMonitorAddBranchButtonActionPerformed

    private void gsfMonitorAddInterfaceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsfMonitorAddInterfaceButtonActionPerformed
        IpssLogger.getLogger().info("gsfAddInterfaceButtonActionPerformed() called");
    	String id = (String)gsfMonitorInterfaceListComboBox.getSelectedItem();
    	RunUIUtilFunc.addItemJList(gsfMonitorBranchList, "i:"+id);        
    }//GEN-LAST:event_gsfMonitorAddInterfaceButtonActionPerformed

    private void gsfMonitorRemoveBranchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsfMonitorRemoveBranchButtonActionPerformed
        IpssLogger.getLogger().info("gsfRemoveBranchButtonActionPerformed() called");
        RunUIUtilFunc.removeItemJList(this.gsfMonitorBranchList);
    }//GEN-LAST:event_gsfMonitorRemoveBranchButtonActionPerformed

    private void gsfSelectedGSFButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsfSelectedGSFButtonActionPerformed
        IpssLogger.getLogger().info("gsfCalculateButtonActionPerformed() called");
        saveEditor2GSF();
        
    	this.parent.setAlwaysOnTop(false);

    	if (!saveEditor2GSF())
    		return;
    	
		final DclfAlgorithmDSL algoDsl = IpssPTrading.createDclfAlgorithm(_simuCtx.getAclfNet())
				.setRefBus();
		
		new Thread() {
			public void run() {
				IAppStatus appStatus = GraphSpringFactory.getIpssGraphicEditor().getAppStatus();
				appStatus.busyStart(Constants.StatusBusyIndicatorPeriod,
						"Run GSF Analysis ...", "Run SenAnalysis");

				String outText = "";
				try {
					new DclfDslODMRunner(algoDsl).runDclfCase(_senXml, DclfAnalysisType.GSF);
					outText = SenAnalysisOutput.outGSF(_senXml.getGenShiftFactor().get(0)).toString();
				} catch (PSSLException e) {
					IpssLogger.getLogger().severe(e.toString());
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
        IpssLogger.getLogger().info("gsfBranchLargetGSFButtonActionPerformed() called");
    	this.parent.setAlwaysOnTop(false);

    	final DclfAlgorithmDSL algoDsl = IpssPTrading.createDclfAlgorithm(_simuCtx.getAclfNet())
				.setRefBus()
				.addLoadWithdrawBus(new Double(this.gsfLoadThreshholdTextField.getText()).doubleValue(), UnitType.mW);
    	
    	String id = (String)gsfMonitorBranchListComboBox.getSelectedItem();
		final String fromId = NetUtilFunc.findFromID(id);
		final String toId = NetUtilFunc.findToID(id);
		final String cirId = NetUtilFunc.findCirNo(id);    	
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
        IpssLogger.getLogger().info("gsfBranchLargetGSFButtonActionPerformed() called");
    	this.parent.setAlwaysOnTop(false);

    	final DclfAlgorithmDSL algoDsl = IpssPTrading.createDclfAlgorithm(_simuCtx.getAclfNet())
				.setRefBus()
				.addLoadWithdrawBus(new Double(this.gsfLoadThreshholdTextField.getText()).doubleValue(), UnitType.mW);
    	
    	String id = (String)gsfMonitorBranchListComboBox.getSelectedItem();
		final String fromId = NetUtilFunc.findFromID(id);
		final String toId = NetUtilFunc.findToID(id);
		final String cirId = NetUtilFunc.findCirNo(id);    	
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
        IpssLogger.getLogger().info("gsfAllInterfaceGSFButtonActionPerformed() called");
    	this.parent.setAlwaysOnTop(false);

    	final DclfAlgorithmDSL algoDsl = IpssPTrading.createDclfAlgorithm(_simuCtx.getAclfNet())
				.setRefBus()
				.addLoadWithdrawBus(new Double(this.gsfLoadThreshholdTextField.getText()).doubleValue(), UnitType.mW);
    	
    	String id = (String)gsfMonitorInterfaceListComboBox.getSelectedItem();
		final FlowInterface monitorInf = _simuCtx.getAclfNet().getFlowInterface(id);
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
        IpssLogger.getLogger().info("gsfLargetInterfaceGSFButtonActionPerformed() called");
    	this.parent.setAlwaysOnTop(false);

    	final DclfAlgorithmDSL algoDsl = IpssPTrading.createDclfAlgorithm(_simuCtx.getAclfNet())
				.setRefBus()
				.addLoadWithdrawBus(new Double(this.gsfLoadThreshholdTextField.getText()).doubleValue(), UnitType.mW);
    	
    	String id = (String)gsfMonitorInterfaceListComboBox.getSelectedItem();
		final FlowInterface monitorInf = _simuCtx.getAclfNet().getFlowInterface(id);
		
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
    
/*888888888888888888888
 *  LODF    
 888888888888888888888 */
    
    private void lodfSingleTypeRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lodfSingleTypeRadioButtonActionPerformed
        IpssLogger.getLogger().info("lodfSingleTypeRadioButtonActionPerformed() called");
        setLodfComponentStatus(false);
    }//GEN-LAST:event_lodfSingleTypeRadioButtonActionPerformed

    private void lodfMultiTypeRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lodfMultiTypeRadioButtonActionPerformed
        IpssLogger.getLogger().info("lodfMultiTypeRadioButtonActionPerformed() called");
        setLodfComponentStatus(true);
    }//GEN-LAST:event_lodfMultiTypeRadioButtonActionPerformed
    
    private void setLodfComponentStatus(boolean multi) {
    	this.lodfAddBranchButton.setEnabled(multi);
    	this.lodfRemoveBranchButton.setEnabled(multi);
    	this.lodfMultiOutageBranchList.setEnabled(multi);
    }
    
    private void lodfAddBranchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lodfAddBranchButtonActionPerformed
        IpssLogger.getLogger().info("lodfAddBranchButtonActionPerformed() called");
    	String id = (String)this.lodfBranchListComboBox.getSelectedItem();
    	RunUIUtilFunc.addItemJList(lodfMultiOutageBranchList, id);
    }//GEN-LAST:event_lodfAddBranchButtonActionPerformed

    private void lodfRemoveBranchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lodfRemoveBranchButtonActionPerformed
        IpssLogger.getLogger().info("lodfRemoveBranchButtonActionPerformed() called");
        RunUIUtilFunc.removeItemJList(this.lodfMultiOutageBranchList);
    }//GEN-LAST:event_lodfRemoveBranchButtonActionPerformed

    private void lodfMonitorAddBranchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lodfMonitorAddBranchButtonActionPerformed
        IpssLogger.getLogger().info("lodfMonitorAddBranchButtonActionPerformed() called");
    	String id = (String)this.lodfMonitorBranchListComboBox.getSelectedItem();
    	RunUIUtilFunc.addItemJList(lodfMonitorBranchInterfaceList, "b:"+id);
    }//GEN-LAST:event_lodfMonitorAddBranchButtonActionPerformed

    private void lodfMonitorRemoveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lodfMonitorRemoveButtonActionPerformed
        IpssLogger.getLogger().info("lodfMonitorRemoveButtonActionPerformed() called");
        RunUIUtilFunc.removeItemJList(this.lodfMonitorBranchInterfaceList);
    }//GEN-LAST:event_lodfMonitorRemoveButtonActionPerformed
    
    private void lodfSelectedLODFButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lodfSelectedLODFButtonActionPerformed
        IpssLogger.getLogger().info("lodfCalculateButtonActionPerformed() called");
    	this.parent.setAlwaysOnTop(false);
    	
    	if (!saveEditor2LODF())
    		return;

		final DclfAlgorithmDSL algoDsl = IpssPTrading.createDclfAlgorithm(_simuCtx.getAclfNet());
		final DclfSenAnalysisXmlType senXml = this._senXml;
		
		new Thread() {
			public void run() {
				IAppStatus appStatus = GraphSpringFactory.getIpssGraphicEditor().getAppStatus();
				appStatus.busyStart(Constants.StatusBusyIndicatorPeriod,
						"Run LODF Analysis ...", "Run SenAnalysis");

				String outText = "";
				try {
					new DclfDslODMRunner(algoDsl).runDclfCase(senXml, DclfAnalysisType.LODF);
					LineOutageDFactorXmlType lodf = senXml.getLineOutageDFactor().get(0);
			    	outText = SenAnalysisOutput.outLODF(lodf).toString(); // this.odmParser.toXmlDoc(false);
				} catch (PSSLException e) {
					IpssLogger.getLogger().severe(e.toString());
					outText = e.toString();
				}		
				
				algoDsl.destroy();
				
				UISpringFactory.getOutputTextDialog("LODF Calculation Results").display(outText, 60, 15);   
				
				appStatus.busyStop("Run LODF GSF Analysis finished");			
			}
		}.start();
    }//GEN-LAST:event_lodfSelectedLODFButtonActionPerformed

    private void lodfLargestLODFButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                     
        IpssLogger.getLogger().info("lodfLargetLODFButtonActionPerformed() called");
    	this.parent.setAlwaysOnTop(false);
    	
    	final DclfAlgorithmDSL algoDsl = IpssPTrading.createDclfAlgorithm(_simuCtx.getAclfNet());
		
    	final String id = (String)this.lodfBranchListComboBox.getSelectedItem();
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
					outText = SenAnalysisOutput.outLargestLODF(id, maxLODF).toString();
				} catch (PSSLException e) {
					IpssLogger.getLogger().severe(e.toString());
					outText = e.toString();
				}		
				
				algoDsl.destroy();
				
				UISpringFactory.getOutputTextDialog("LODF Calculation Results").display(outText); 
				
				appStatus.busyStop("Run LODF GSF Analysis finished");			
			}
		}.start();
    }      

    private void lodfAllLODFButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        IpssLogger.getLogger().info("lodfAllLODFButtonActionPerformed() called");
    	this.parent.setAlwaysOnTop(false);
    	
		final DclfAlgorithmDSL algoDsl = IpssPTrading.createDclfAlgorithm(_simuCtx.getAclfNet());
		
    	final String id = (String)this.lodfBranchListComboBox.getSelectedItem();
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
					outText = SenAnalysisOutput.outLODFList(id, lodfList).toString();
				} catch (PSSLException e) {
					IpssLogger.getLogger().severe(e.toString());
					outText = e.toString();
				}		
				
				algoDsl.destroy();
				
				UISpringFactory.getOutputTextDialog("LODF Calculation Results").display(outText); 
				
				appStatus.busyStop("Run LODF GSF Analysis finished");			
			}
		}.start();
    }                                                     
    
    
    /*
     *  PTDF    
     */
    
    private void ptdfWithSingleBusRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ptdfWithSingleBusRadioButtonActionPerformed
//    	setMultiBusWithdrawStatus(false);
//    	tdFactor.setWithdrawBusType(SenBusAnalysisDataType.SINGLE_BUS);
//    	while (tdFactor.getWithdrawBusList().getWithdrawBus().size() > 0)
//    		tdFactor.getWithdrawBusList().getWithdrawBus().remove(0);
//    	tdFactor.getWithdrawBusList().getWithdrawBus().add(
//    			IpssXmlParser.getFactory().createSenAnalysisBusRecXmlType());
//    	tdFactor.getWithdrawBusList().getWithdrawBus().get(0).setBusId((String)ptdfWithdrawBusComboBox.getSelectedItem());
}//GEN-LAST:event_ptdfWithSingleBusRadioButtonActionPerformed

    private void ptdfRemoveBranchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ptdfRemoveBranchButtonActionPerformed
//    	if (!ptdfMeasBranchList.isSelectionEmpty()) {
//    		tdFactor.getBranch().remove(ptdfMeasBranchList.getSelectedIndex());
//        	ptdfMeasBranchList.setModel(new javax.swing.DefaultComboBoxModel(
//        			IpssXmlHelper.getBranchIdAry(tdFactor.getBranch())));
//    	}
}//GEN-LAST:event_ptdfRemoveBranchButtonActionPerformed

    private void ptdfInjectGenBusRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ptdfInjectGenBusRadioButtonActionPerformed
		this.ptdfInjectBusComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.GenBus).toArray()));
}//GEN-LAST:event_ptdfInjectGenBusRadioButtonActionPerformed

    private void ptdfInjectAllBusRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ptdfInjectAllBusRadioButtonActionPerformed
		this.ptdfInjectBusComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.AllBus).toArray()));
}//GEN-LAST:event_ptdfInjectAllBusRadioButtonActionPerformed

    private void ptdfWithMultiBusRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ptdfWithMultiBusRadioButtonActionPerformed
//    	setMultiBusWithdrawStatus(true);
//    	tdFactor.setWithdrawBusType(SenBusAnalysisDataType.MULTIPLE_BUS);
//    	if (tdFactor.getWithdrawBusList() == null)
//    		tdFactor.setWithdrawBusList(IpssXmlParser.getFactory().createDclfSensitivityXmlTypeWithdrawBusList());
}//GEN-LAST:event_ptdfWithMultiBusRadioButtonActionPerformed

    private void ptdfWithAllBusRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ptdfWithAllBusRadioButtonActionPerformed
		this.ptdfWithdrawBusComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.AllBus).toArray()));
}//GEN-LAST:event_ptdfWithAllBusRadioButtonActionPerformed

    private void ptdfWithLoadBusRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ptdfWithLoadBusRadioButtonActionPerformed
		this.ptdfWithdrawBusComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.LoadBus).toArray()));
}//GEN-LAST:event_ptdfWithLoadBusRadioButtonActionPerformed

    private void ptdfRemoveWithBusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ptdfRemoveWithBusButtonActionPerformed
//    	if (!ptdfWithdarwBusList.isSelectionEmpty()) {
//    		tdFactor.getWithdrawBusList().getWithdrawBus().remove(ptdfWithdarwBusList.getSelectedIndex());
//    		ptdfWithdarwBusList.setModel(new javax.swing.DefaultComboBoxModel(
//    			IpssXmlHelper.getSenAnalysisBusItemList(tdFactor.getWithdrawBusList().getWithdrawBus())));
//    	}
}//GEN-LAST:event_ptdfRemoveWithBusButtonActionPerformed

    private void ptdfAddWithBusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ptdfAddWithBusButtonActionPerformed
//    	String id = (String)ptdfWithdrawBusComboBox.getSelectedItem();
//    	double percent = new Double(this.ptdfDistFactorTextField.getText()).doubleValue();
//    	SenAnalysisBusRecXmlType bus = IpssXmlParser.getFactory().createSenAnalysisBusRecXmlType(); 
//    	tdFactor.getWithdrawBusList().getWithdrawBus().add(bus);
//    	bus.setBusId(id);
//    	bus.setPercent(percent);
//    	ptdfWithdarwBusList.setModel(new javax.swing.DefaultComboBoxModel(
//    			IpssXmlHelper.getSenAnalysisBusItemList(tdFactor.getWithdrawBusList().getWithdrawBus())));
}//GEN-LAST:event_ptdfAddWithBusButtonActionPerformed

    private void ptdfAddBranchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ptdfAddBranchButtonActionPerformed
//    	String id = (String)ptdfBranchListComboBox.getSelectedItem();
//    	BranchRecXmlType braRec = IpssXmlParser.getFactory().createBranchRecXmlType();
//    	tdFactor.getBranch().add(braRec);
//    	IpssXmlHelper.setBranchRec(braRec, id);
//    	ptdfMeasBranchList.setModel(new javax.swing.DefaultComboBoxModel(
//    			IpssXmlHelper.getBranchIdAry(tdFactor.getBranch())));
}//GEN-LAST:event_ptdfAddBranchButtonActionPerformed

    private void ptdfMultiInjectBusRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ptdfMultiInjectBusRadioButtonActionPerformed
//    	tdFactor.setInjectBusType(SenBusAnalysisDataType.MULTIPLE_BUS);
//        ptdfInjectGenBusRadioButtonActionPerformed(null);
//    	ptdfInjectAllBusRadioButton.setEnabled(false);
}//GEN-LAST:event_ptdfMultiInjectBusRadioButtonActionPerformed

    private void ptdfSingleInjectBusRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ptdfSingleInjectBusRadioButtonActionPerformed
//    	tdFactor.setInjectBusType(SenBusAnalysisDataType.SINGLE_BUS);
//    	ptdfInjectAllBusRadioButton.setEnabled(true);
}//GEN-LAST:event_ptdfSingleInjectBusRadioButtonActionPerformed

    private void ptdfAddInterfaceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ptdfAddInterfaceButtonActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_ptdfAddInterfaceButtonActionPerformed

    private void ptdfCalculateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ptdfCalculateButtonActionPerformed
//    	this.parent.setAlwaysOnTop(false);
//    	DclfAlgorithm algo = DclfObjectFactory.createDclfAlgorithm(_simuCtx.getAclfNet());
//    	_simuCtx.setDclfAlgorithm(algo);
//    	if (!algo.checkCondition())
//    		return;
//    	if (!saveEditor2TDFactor())
//    		return;
//    	XmlScriptDclfRun.calPTDistFactor(tdFactor, algo);
//    	IOutputTextDialog dialog = UISpringFactory.getOutputTextDialog("Sensitivity Analysis Results");
//    	String str = DclfOutFunc.pTransferDistFactorResults(tdFactor, _simuCtx.getDclfAlgorithm());
//    	dialog.display(str);
    }//GEN-LAST:event_ptdfCalculateButtonActionPerformed

    private void setMultiBusWithdrawStatus(boolean b) {
        this.ptdfWithdarwBusList.setEnabled(b);
        this.ptdfAddWithBusButton.setEnabled(b);
        this.ptdfRemoveWithBusButton.setEnabled(b);
        this.ptdfLoadDistFactorLabel.setEnabled(b);
        this.ptdfPercentLabel.setEnabled(b);
        this.ptdfDistFactorTextField.setEnabled(b);
    }

    /*
     * Area Transfer Analysis
     */
    
private void atAddBranchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_atAddBranchButtonActionPerformed
//	String id = (String)atBranchListComboBox.getSelectedItem();
//	BranchRecXmlType braRec = IpssXmlParser.getFactory().createBranchRecXmlType(); 
//	areaTransfer.getBranch().add(braRec);
//	IpssXmlHelper.setBranchRec(braRec, id);
//	atMeasBranchList.setModel(new javax.swing.DefaultComboBoxModel(
//			IpssXmlHelper.getBranchIdAry(areaTransfer.getBranch())));
}//GEN-LAST:event_atAddBranchButtonActionPerformed

private void atAddInterfaceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_atAddInterfaceButtonActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_atAddInterfaceButtonActionPerformed

private void atRemoveBranchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_atRemoveBranchButtonActionPerformed
//	if (!atMeasBranchList.isSelectionEmpty()) {
//		areaTransfer.getBranch().remove(atMeasBranchList.getSelectedIndex());
//		atMeasBranchList.setModel(new javax.swing.DefaultComboBoxModel(
//				IpssXmlHelper.getBranchIdAry(areaTransfer.getBranch())));
//	}
}//GEN-LAST:event_atRemoveBranchButtonActionPerformed

private void atFromAreaRemoveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_atFromAreaRemoveButtonActionPerformed
//	if (!atFromAreaBusList.isSelectionEmpty()) {
//		areaTransfer.getInjectBusList().getInjectBus().remove(atFromAreaBusList.getSelectedIndex());
//		atFromAreaBusList.setModel(new javax.swing.DefaultComboBoxModel(
//				IpssXmlHelper.getSenAnalysisBusItemList(areaTransfer.getInjectBusList().getInjectBus())));
//	}
}//GEN-LAST:event_atFromAreaRemoveButtonActionPerformed

private void atToAreaRemoveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_atToAreaRemoveButtonActionPerformed
//	if (!atToAreaBusList.isSelectionEmpty()) {
//		areaTransfer.getWithdrawBusList().getWithdrawBus().remove(atToAreaBusList.getSelectedIndex());
//		atToAreaBusList.setModel(new javax.swing.DefaultComboBoxModel(
//				IpssXmlHelper.getSenAnalysisBusItemList(areaTransfer.getWithdrawBusList().getWithdrawBus())));
//	}
}//GEN-LAST:event_atToAreaRemoveButtonActionPerformed

private void atFromAreaComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_atFromAreaComboBoxActionPerformed
//	String no = (String)this.atFromAreaComboBox.getSelectedItem();
//	this.areaTransfer.getFromArea().setAreaNo(new Integer(no).intValue());
//	atFromAreaBusList.setModel(new javax.swing.DefaultComboBoxModel(
//			RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.GenInAreaDFactor, 
//					this.areaTransfer.getFromArea().getAreaNo()).toArray()));
}//GEN-LAST:event_atFromAreaComboBoxActionPerformed

private void atToAreaComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_atToAreaComboBoxActionPerformed
//	String no = (String)this.atToAreaComboBox.getSelectedItem();
//	this.areaTransfer.getToArea().setAreaNo(new Integer(no).intValue());
//	atToAreaBusList.setModel(new javax.swing.DefaultComboBoxModel(
//			RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.GenInAreaDFactor, 
//					this.areaTransfer.getToArea().getAreaNo()).toArray()));
}//GEN-LAST:event_atToAreaComboBoxActionPerformed

private void atFromAreaEditButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_atFromAreaEditButtonActionPerformed
	if (!atFromAreaBusList.isSelectionEmpty()) {
		atFromDFactorEditTextField.setEnabled(true);
		atFromAreaUpdateButton.setEnabled(true);
		String s = (String)atFromAreaBusList.getSelectedValue();
		atFromDFactorEditTextField.setText(new Double(RunUIUtilFunc.getPercent_IdPercent(s)).toString());
	}
	}//GEN-LAST:event_atFromAreaEditButtonActionPerformed

private void atFromAreaUpdateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_atFromAreaUpdateButtonActionPerformed
//	if (!atFromAreaBusList.isSelectionEmpty()) {
//		// the Bus list might have been changed due to changing the area number
//		saveFromAreaBusList2AreaTransfer();
//		double percent = new Double(atFromDFactorEditTextField.getText()).doubleValue();
//		String s = (String)atFromAreaBusList.getSelectedValue();
//		String id = RunUIUtilFunc.getId_IdPercent(s);
//		SenAnalysisBusRecXmlType bus = (SenAnalysisBusRecXmlType)IpssXmlHelper.getBusRecord(id, areaTransfer.getInjectBusList().getInjectBus());
//		bus.setPercent(percent);
//		atFromAreaBusList.setModel(new javax.swing.DefaultComboBoxModel(
//    			IpssXmlHelper.getSenAnalysisBusItemList(areaTransfer.getInjectBusList().getInjectBus())));
//		atFromDFactorEditTextField.setEnabled(false);
//		atFromAreaUpdateButton.setEnabled(false);
//	}
}//GEN-LAST:event_atFromAreaUpdateButtonActionPerformed

private void atToAreaEditButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_atToAreaEditButtonActionPerformed
	if (!atToAreaBusList.isSelectionEmpty()) {
		atToDFactorEditTextField.setEnabled(true);
		atToAreaUpdateButton.setEnabled(true);
		String s = (String)atToAreaBusList.getSelectedValue();
		atToDFactorEditTextField.setText(new Double(RunUIUtilFunc.getPercent_IdPercent(s)).toString());
	}
	}//GEN-LAST:event_atToAreaEditButtonActionPerformed

private void atToAreaUpdateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_atToAreaUpdateButtonActionPerformed
//	if (!atToAreaBusList.isSelectionEmpty()) {
//		// the Bus list might have been changed due to changing the area number
//		saveToAreaBusList2AreaTransfer();
//		double percent = new Double(atToDFactorEditTextField.getText()).doubleValue();
//		String s = (String)atToAreaBusList.getSelectedValue();
//		String id = RunUIUtilFunc.getId_IdPercent(s);
//		SenAnalysisBusRecXmlType bus = (SenAnalysisBusRecXmlType)IpssXmlHelper.getBusRecord(id, 
//						areaTransfer.getWithdrawBusList().getWithdrawBus());
//		bus.setPercent(percent);
//		atToAreaBusList.setModel(new javax.swing.DefaultComboBoxModel(
//	    			IpssXmlHelper.getSenAnalysisBusItemList(areaTransfer.getWithdrawBusList().getWithdrawBus())));
//		atToDFactorEditTextField.setEnabled(false);
//		atToAreaUpdateButton.setEnabled(false);
//	}
}//GEN-LAST:event_atToAreaUpdateButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel areaTransPanel;
    private javax.swing.JButton atAddBranchButton;
    private javax.swing.JButton atAddInterfaceButton;
    private javax.swing.JComboBox atBranchListComboBox;
    private javax.swing.JScrollPane atBranchListScrollPane;
    private javax.swing.JLabel atDeratingFactorLabel;
    private javax.swing.JTextField atDeratingFactorTextField;
    private javax.swing.JList atFromAreaBusList;
    private javax.swing.JComboBox atFromAreaComboBox;
    private javax.swing.JButton atFromAreaEditButton;
    private javax.swing.JLabel atFromAreaLabel;
    private javax.swing.JButton atFromAreaRemoveButton;
    private javax.swing.JScrollPane atFromAreaScrollPane;
    private javax.swing.JButton atFromAreaUpdateButton;
    private javax.swing.JPanel atFromDFPanel;
    private javax.swing.JTextField atFromDFactorEditTextField;
    private javax.swing.JPanel atInfoEditPanel;
    private javax.swing.JComboBox atInterfaceListComboBox;
    private javax.swing.JList atMeasBranchList;
    private javax.swing.JPanel atMeasBranchPanel;
    private javax.swing.JButton atRemoveBranchButton;
    private javax.swing.JList atToAreaBusList;
    private javax.swing.JComboBox atToAreaComboBox;
    private javax.swing.JButton atToAreaEditButton;
    private javax.swing.JLabel atToAreaLabel;
    private javax.swing.JButton atToAreaRemoveButton;
    private javax.swing.JScrollPane atToAreaScrollPane;
    private javax.swing.JButton atToAreaUpdateButton;
    private javax.swing.JPanel atToDFPanel;
    private javax.swing.JTextField atToDFactorEditTextField;
    private javax.swing.JLabel atTransAmtLabel;
    private javax.swing.JTextField atTransAmtTextField;
    private javax.swing.JComboBox atTransAmtUnitComboBox;
    private javax.swing.JButton gsfAddWithBusButton;
    private javax.swing.JButton gsfAllBranchGSFButton;
    private javax.swing.JButton gsfAllInterfaceGSFButton;
    private javax.swing.JTextField gsfDistFactorTextField;
    private javax.swing.JLabel gsfGenBusLabel;
    private javax.swing.JPanel gsfInjBusSelPanel;
    private javax.swing.JComboBox gsfInjectBusComboBox;
    private javax.swing.JPanel gsfInjectionPanel;
    private javax.swing.JButton gsfLargetBranchGSFButton;
    private javax.swing.JButton gsfLargetInterfaceGSFButton;
    private javax.swing.JComboBox gsfLoadBusComboBox;
    private javax.swing.JLabel gsfLoadBusLabel;
    private javax.swing.JRadioButton gsfLoadDFactorRadioButton;
    private javax.swing.JRadioButton gsfLoadMultiBusRadioButton;
    private javax.swing.JRadioButton gsfLoadSingleBusRadioButton;
    private javax.swing.JLabel gsfLoadThreshholdLabel;
    private javax.swing.JTextField gsfLoadThreshholdTextField;
    private javax.swing.JButton gsfMonitorAddBranchButton;
    private javax.swing.JButton gsfMonitorAddInterfaceButton;
    private javax.swing.JList gsfMonitorBranchList;
    private javax.swing.JComboBox gsfMonitorBranchListComboBox;
    private javax.swing.JPanel gsfMonitorBranchPanel;
    private javax.swing.JComboBox gsfMonitorInterfaceListComboBox;
    private javax.swing.JButton gsfMonitorRemoveBranchButton;
    private javax.swing.JScrollPane gsfMonitorScrollPane;
    private javax.swing.JPanel gsfPanel;
    private javax.swing.JLabel gsfPercentLabel;
    private javax.swing.JButton gsfRemoveWithBusButton;
    private javax.swing.JButton gsfSelectedGSFButton;
    private javax.swing.JScrollPane gsfWithBusScrollPane;
    private javax.swing.JPanel gsfWithBusSelectPanel;
    private javax.swing.JPanel gsfWithMultiBusPanel;
    private javax.swing.ButtonGroup gsfWithdrawButtonGroup;
    private javax.swing.JPanel gsfWithdrawMarginPanel;
    private javax.swing.JPanel gsfWithdrawPanel;
    private javax.swing.JPanel gsfWithdrawiBusPanel;
    private javax.swing.JButton lodfAddBranchButton;
    private javax.swing.JButton lodfAllLODFButton;
    private javax.swing.JComboBox lodfBranchListComboBox;
    private javax.swing.JLabel lodfBranchListLabel;
    private javax.swing.JButton lodfLargetLODFButton;
    private javax.swing.JButton lodfMonitorAddBranchButton;
    private javax.swing.JList lodfMonitorBranchInterfaceList;
    private javax.swing.JComboBox lodfMonitorBranchListComboBox;
    private javax.swing.JPanel lodfMonitorBranchPanel;
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
    private javax.swing.ButtonGroup lodfTypeButtonGroup;
    private javax.swing.JPanel lodfTypePanel;
    private javax.swing.JLabel outAllBranchPointsLabel;
    private javax.swing.JTextField outAllBranchPointsTextField;
    private javax.swing.JLabel outAllInterfacePointsLabel;
    private javax.swing.JTextField outAllInterfacePointsTextField;
    private javax.swing.JPanel outConfigPanel;
    private javax.swing.JButton ptdfAddBranchButton;
    private javax.swing.JButton ptdfAddInterfaceButton;
    private javax.swing.JButton ptdfAddWithBusButton;
    private javax.swing.JComboBox ptdfBranchListComboBox;
    private javax.swing.JButton ptdfCalculateButton;
    private javax.swing.JTextField ptdfDistFactorTextField;
    private javax.swing.JPanel ptdfInjBusPanel;
    private javax.swing.JPanel ptdfInjBusSelPanel;
    private javax.swing.JRadioButton ptdfInjectAllBusRadioButton;
    private javax.swing.JComboBox ptdfInjectBusComboBox;
    private javax.swing.JRadioButton ptdfInjectGenBusRadioButton;
    private javax.swing.JPanel ptdfInjectionPanel;
    private javax.swing.JComboBox ptdfInterfaceListComboBox;
    private javax.swing.JLabel ptdfLoadDistFactorLabel;
    private javax.swing.JList ptdfMeasBranchList;
    private javax.swing.JPanel ptdfMeasBranchPanel;
    private javax.swing.JRadioButton ptdfMultiInjectBusRadioButton;
    private javax.swing.JPanel ptdfPanel;
    private javax.swing.JLabel ptdfPercentLabel;
    private javax.swing.JButton ptdfRemoveBranchButton;
    private javax.swing.JButton ptdfRemoveWithBusButton;
    private javax.swing.JScrollPane ptdfScrollPane;
    private javax.swing.JRadioButton ptdfSingleInjectBusRadioButton;
    private javax.swing.JPanel ptdfSingleMultiBusPanel;
    private javax.swing.JRadioButton ptdfWithAllBusRadioButton;
    private javax.swing.JScrollPane ptdfWithBusScrollPane;
    private javax.swing.JPanel ptdfWithBusSelectPanel;
    private javax.swing.JRadioButton ptdfWithLoadBusRadioButton;
    private javax.swing.JPanel ptdfWithMultiBusPanel;
    private javax.swing.JRadioButton ptdfWithMultiBusRadioButton;
    private javax.swing.JRadioButton ptdfWithSingleBusRadioButton;
    private javax.swing.JList ptdfWithdarwBusList;
    private javax.swing.JList ptdfWithdarwBusList3;
    private javax.swing.JComboBox ptdfWithdrawBusComboBox;
    private javax.swing.JPanel ptdfWithdrawPanel;
    private javax.swing.JTabbedPane runDclfTabbedPane;
    private javax.swing.JLabel space;
    // End of variables declaration//GEN-END:variables
    
	private void initInputVerifier(DataVerifier v) {
	    gsfDistFactorTextField.setInputVerifier(v);
	    gsfLoadThreshholdTextField.setInputVerifier(v);
		this.outAllBranchPointsTextField.setInputVerifier(v);
		this.outAllInterfacePointsTextField.setInputVerifier(v);
	}
	
	class DataVerifier extends javax.swing.InputVerifier {
		public boolean verify(javax.swing.JComponent input) {
			if (input == null)
				return false;
       		try {
				if (input == gsfDistFactorTextField)
					return SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) > 0.0 && 
							SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) <= 100.0;
				else if (input == gsfLoadThreshholdTextField)
					return SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) > 0.0 && 
							SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) <= 100.0;
						
				else if (input == outAllBranchPointsTextField)
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
