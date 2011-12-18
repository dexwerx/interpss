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

import java.util.Vector;

import javax.swing.JDialog;

import org.ieee.odm.model.ODMModelParser;
import org.ieee.odm.model.scenario.IpssScenarioHelper;
import org.ieee.odm.schema.BaseBranchXmlType;
import org.ieee.odm.schema.DclfBranchSensitivityXmlType;
import org.ieee.odm.schema.DclfSenAnalysisXmlType;
import org.ieee.odm.schema.LineBranchXmlType;
import org.ieee.odm.schema.SenAnalysisBusXmlType;
import org.ieee.odm.schema.SenBusAnalysisEnumType;
import org.ieee.odm.schema.SensitivityEnumType;
import org.ieee.odm.schema.DclfBranchSensitivityXmlType.BranchSFactor;
import org.interpss.editor.jgraph.GraphSpringFactory;
import org.interpss.editor.jgraph.ui.app.IAppStatus;
import org.interpss.editor.jgraph.ui.edit.IFormDataPanel;
import org.interpss.editor.ui.RunUIUtilFunc;
import org.interpss.numeric.datatype.Unit.UnitType;
import org.interpss.numeric.util.Number2String;
import org.interpss.spring.UISpringFactory;

import com.interpss.common.datatype.Constants;
import com.interpss.common.exp.InterpssRuntimeException;
import com.interpss.common.msg.IpssMessage;
import com.interpss.common.msg.IpssMsgListener;
import com.interpss.common.util.IpssLogger;
import com.interpss.common.util.NetUtilFunc;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.datatype.DblBranchValue;
import com.interpss.datatype.DblBusValue;
import com.interpss.pssl.common.PSSLException;
import com.interpss.pssl.simu.IpssPTrading;
import com.interpss.pssl.simu.IpssPTrading.DclfAlgorithmDSL;
import com.interpss.pssl.simu.impl.SenAnalysisOutput;
import com.interpss.pssl.simu.impl.DclfDslODMRunner.DclfAnalysisType;
import com.interpss.simu.SimuContext;

public class NBDclfCasePanel extends javax.swing.JPanel implements IFormDataPanel, IpssMsgListener {
	private static final long serialVersionUID = 1;
	private JDialog parent;
	
    // private GFormContainer _netContainer = null;
    private SimuContext _simuCtx = null;

    // holds the current case data being edited
    //private DclfBranchSensitivityXmlType tdFactor = null;
	//private AreaTransferAnalysisXmlType areaTransfer = null;;
    
	private ODMModelParser odmParser = new ODMModelParser();
    private DclfSenAnalysisXmlType xmlCaseData;
    
    /** Creates new form NBAclfCasePanel */
    public NBDclfCasePanel(JDialog parent) {
    	initComponents();
    	this.parent = parent;

        //DataVerifier verifier = new DataVerifier();
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
	    //initLodfTabPanel();
	    
	    this.runDclfTabbedPane.setEnabledAt(2, false);
	    this.runDclfTabbedPane.setEnabledAt(3, false);
	    this.runDclfTabbedPane.setEnabledAt(4, false);
	}
    
    private void initGsfTabPanel() {
    	if (_simuCtx != null) {   // this method might be called be init(), when thre 0-panel is selected
    		this.gsfInjectBusComboBox.setModel(new javax.swing.DefaultComboBoxModel(
    				RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.GenBus).toArray()));
    		this.gsfBranchListComboBox.setModel(new javax.swing.DefaultComboBoxModel(
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
/*
    private void initPtdfTabPanel() {
		this.ptdfInjectBusComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.GenBus).toArray()));
		this.ptdfWithdrawBusComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.LoadBus).toArray()));
		this.ptdfBranchListComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.AllBranch).toArray()));
    }    

    private void initAtTabPanel() {
		this.atFromAreaComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.AreaNo).toArray()));
		this.atToAreaComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.AreaNo).toArray()));
		this.atBranchListComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.AllBranch).toArray()));
    }    
*/
    public void setODMParser(ODMModelParser parser) {
    	this.odmParser = parser;
    }
   
    public void setXmlCaseData(DclfSenAnalysisXmlType xmlCaseData) {
    	this.xmlCaseData = xmlCaseData;
//    	if (xmlCaseData.getPTransferDistFactor().size() == 0)
//    		StudyCaseHanlder.addNewTDFactor(xmlCaseData);
//    	this.tdFactor = xmlCaseData.getPTransferDistFactor().get(0);
//    	if (xmlCaseData.getAreaTransferAnalysis().size() == 0) {
//    		xmlCaseData.getAreaTransferAnalysis().add(IpssXmlParser.getFactory().createAreaTransferAnalysisXmlType());
//    		StudyCaseHanlder.setNewAreaTransfer(xmlCaseData.getAreaTransferAnalysis().get(0));
//    	}
//    	this.areaTransfer = xmlCaseData.getAreaTransferAnalysis().get(0);
    }    
    
	/**
	*	Set form data to the editor
	*
	* @return false if there is any problem
	*/
	public boolean setForm2Editor() {
		IpssLogger.getLogger().info("NBAclfCasePanel setForm2Editor() called");
		
		if (!setGSF2Editor())
			return false;

		//if (!setTDFactor2Editor())
		//	return false;

		//if (!setAreaTransfer2Editor())
		//	return false;
		
		return true;
	}

	public boolean setGSF2Editor() {
		IpssLogger.getLogger().info("NBAclfCasePanel setForm2Editor() called");
		
		if (this.xmlCaseData.getGenShiftFactor().size() > 0) {
			DclfBranchSensitivityXmlType gsf = this.xmlCaseData.getGenShiftFactor().get(0);
			
			SenAnalysisBusXmlType bus = gsf.getInjectBusList().getInjectBuses().get(0);
			this.gsfInjectBusComboBox.setSelectedItem(bus.getBusId());
			
			this.gsfLoadThreshholdTextField.setText(
					Number2String.toStr(gsf.getMinLoadForDistFactor().getValue(), "#0.0"));
			
			String[] ary = new String[gsf.getBranchSFactor().size()];
			int cnt = 0;
			for ( BranchSFactor sf : gsf.getBranchSFactor()) {
				BaseBranchXmlType branch = sf.getBranch();
				ary[cnt++] = "b:" + NetUtilFunc.formBranchId(branch.getFromBusId(), branch.getToBusId(), branch.getCircuitId());
			}
	    	gsfMonitorBranchList.setModel(new javax.swing.DefaultComboBoxModel(ary));    	
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
		
		//saveEditor2TDFactor();
		//saveEditor2AreaTransfer();
		return errMsg.size() == 0;
	}

	public boolean saveEditor2GSF() {
		IpssLogger.getLogger().info("NBAclfCasePanel saveEditor2GSF() called");

		IpssScenarioHelper helper = new IpssScenarioHelper(this.odmParser);
		
		this.xmlCaseData.getGenShiftFactor().clear();
		DclfBranchSensitivityXmlType gsf = helper.createGSF(this.xmlCaseData);
		
		gsf.setSenType(SensitivityEnumType.P_ANGLE);
		
		gsf.setInjectBusType(SenBusAnalysisEnumType.SINGLE_BUS);
		SenAnalysisBusXmlType bus = helper.createSenAnalysisBus(gsf.getInjectBusList().getInjectBuses());
		bus.setBusId((String)this.gsfInjectBusComboBox.getSelectedItem());
		
		gsf.setWithdrawBusType(SenBusAnalysisEnumType.LOAD_DISTRIBUTION);
		
		gsf.setMinLoadForDistFactor(helper.createActivePower(new Double(this.gsfLoadThreshholdTextField.getText()).doubleValue(), "MW"));

		gsf.getBranchSFactor().clear();
			
    	int size = gsfMonitorBranchList.getModel().getSize();
    	for (int i = 0; i < size; i++) {
    		String id = (String)gsfMonitorBranchList.getModel().getElementAt(i);
    		if (id.startsWith("b:")) { // branch
    			String braId = id.substring(2);
    			String fromId = NetUtilFunc.findFromID(braId);
    			String toId = NetUtilFunc.findToID(braId);
    			String cirId = NetUtilFunc.findCirNo(braId);
    			
	    		BranchSFactor sf = helper.createBranchSFactor(gsf.getBranchSFactor());
				LineBranchXmlType line = helper.createLineBranchXmlType();
				sf.setBranch(line);
				line.setId(braId);
				line.setFromBusId(fromId);
				line.setToBusId(toId);
				line.setCircuitId(cirId);
    		}
    		else {  // interface
    			
    		}
    	}
		
		return true;
	}

	public boolean saveEditor2LODF() {
		IpssLogger.getLogger().info("NBAclfCasePanel saveEditor2GSF() called");
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
        gsfBranchListComboBox = new javax.swing.JComboBox();
        gsfAddBranchButton = new javax.swing.JButton();
        gsfBranchLargetGSFButton = new javax.swing.JButton();
        gsfInterfaceListComboBox = new javax.swing.JComboBox();
        gsfAddInterfaceButton = new javax.swing.JButton();
        gsdfScrollPane = new javax.swing.JScrollPane();
        gsfMonitorBranchList = new javax.swing.JList();
        gsfRemoveBranchButton = new javax.swing.JButton();
        gsfCalculateButton = new javax.swing.JButton();
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
        lodfCalculateButton = new javax.swing.JButton();
        lodfMonitorBranchPanel = new javax.swing.JPanel();
        lodfMonitorBranchListComboBox = new javax.swing.JComboBox();
        lodfMonitorAddBranchButton = new javax.swing.JButton();
        lodfMonitorInterfaceListComboBox = new javax.swing.JComboBox();
        lodfMonitorAddInterfaceButton = new javax.swing.JButton();
        lodfMonitorScrollPane = new javax.swing.JScrollPane();
        lodfMonitorBranchInterfaceList = new javax.swing.JList();
        lodfMonitorRemoveButton = new javax.swing.JButton();
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
        lossFactorPanel = new javax.swing.JPanel();
        lossInjectionPanel = new javax.swing.JPanel();
        lossInjBusSelPanel = new javax.swing.JPanel();
        lossGenBusLabel = new javax.swing.JLabel();
        lossInjectBusComboBox = new javax.swing.JComboBox();
        lossWithdrawPanel = new javax.swing.JPanel();
        lossSingleMultiBusPanel = new javax.swing.JPanel();
        ptdfWithSingleBusRadioButton2 = new javax.swing.JRadioButton();
        ptdfWithMultiBusRadioButton2 = new javax.swing.JRadioButton();
        lossWithBusSelectPanel = new javax.swing.JPanel();
        ptdfWithdrawBusComboBox2 = new javax.swing.JComboBox();
        ptdfWithLoadBusRadioButton2 = new javax.swing.JRadioButton();
        ptdfWithAllBusRadioButton2 = new javax.swing.JRadioButton();
        lossWithMultiBusPanel = new javax.swing.JPanel();
        ptdfWithBusScrollPane2 = new javax.swing.JScrollPane();
        ptdfWithdarwBusList2 = new javax.swing.JList();
        ptdfLoadDistFactorLabel2 = new javax.swing.JLabel();
        ptdfDistFactorTextField2 = new javax.swing.JTextField();
        ptdfPercentLabel2 = new javax.swing.JLabel();
        ptdfAddWithBusButton2 = new javax.swing.JButton();
        ptdfRemoveWithBusButton2 = new javax.swing.JButton();
        lossMeasBranchPanel = new javax.swing.JPanel();
        lossBranchListComboBox = new javax.swing.JComboBox();
        lossAddBranchButton = new javax.swing.JButton();
        lossInterfaceListComboBox = new javax.swing.JComboBox();
        lossAddInterfaceButton = new javax.swing.JButton();
        lossScrollPane = new javax.swing.JScrollPane();
        ptdfMeasBranchList2 = new javax.swing.JList();
        lossRemoveBranchButton = new javax.swing.JButton();
        lossCalculateButton = new javax.swing.JButton();
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
        gsfLoadDFactorRadioButton.setText("Load DFactor");
        gsfLoadDFactorRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gsfLoadDFactorRadioButtonActionPerformed(evt);
            }
        });
        gsfWithdrawiBusPanel.add(gsfLoadDFactorRadioButton);

        gsfWithdrawButtonGroup.add(gsfLoadSingleBusRadioButton);
        gsfLoadSingleBusRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        gsfLoadSingleBusRadioButton.setText("Single");
        gsfLoadSingleBusRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gsfLoadSingleBusRadioButtonActionPerformed(evt);
            }
        });
        gsfWithdrawiBusPanel.add(gsfLoadSingleBusRadioButton);

        gsfWithdrawButtonGroup.add(gsfLoadMultiBusRadioButton);
        gsfLoadMultiBusRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        gsfLoadMultiBusRadioButton.setText("Multi");
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
                    .add(gsfWithBusScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                    .add(gsfWithMultiBusPanelLayout.createSequentialGroup()
                        .add(gsfAddWithBusButton)
                        .add(18, 18, 18)
                        .add(gsfRemoveWithBusButton)
                        .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        gsfWithMultiBusPanelLayout.setVerticalGroup(
            gsfWithMultiBusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(gsfWithMultiBusPanelLayout.createSequentialGroup()
                .add(gsfWithBusScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 96, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gsfWithMultiBusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(gsfRemoveWithBusButton)
                    .add(gsfAddWithBusButton))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gsfWithdrawPanel.add(gsfWithMultiBusPanel, gridBagConstraints);

        gsfMonitorBranchPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Monitor Branch/Interface", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N

        gsfBranchListComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        gsfBranchListComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        gsfAddBranchButton.setFont(new java.awt.Font("Dialog", 0, 10));
        gsfAddBranchButton.setText("Add Branch");
        gsfAddBranchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gsfAddBranchButtonActionPerformed(evt);
            }
        });

        gsfBranchLargetGSFButton.setFont(new java.awt.Font("Dialog", 0, 10));
        gsfBranchLargetGSFButton.setText("Largest GSF");
        gsfBranchLargetGSFButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gsfBranchLargetGSFButtonActionPerformed(evt);
            }
        });

        gsfInterfaceListComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        gsfInterfaceListComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gsfInterfaceListComboBox.setEnabled(false);

        gsfAddInterfaceButton.setFont(new java.awt.Font("Dialog", 0, 10));
        gsfAddInterfaceButton.setText("Add Interface");
        gsfAddInterfaceButton.setEnabled(false);
        gsfAddInterfaceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gsfAddInterfaceButtonActionPerformed(evt);
            }
        });

        gsfMonitorBranchList.setFont(new java.awt.Font("Dialog", 0, 12));
        gsdfScrollPane.setViewportView(gsfMonitorBranchList);

        gsfRemoveBranchButton.setFont(new java.awt.Font("Dialog", 0, 10));
        gsfRemoveBranchButton.setText("Remove");
        gsfRemoveBranchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gsfRemoveBranchButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout gsfMonitorBranchPanelLayout = new org.jdesktop.layout.GroupLayout(gsfMonitorBranchPanel);
        gsfMonitorBranchPanel.setLayout(gsfMonitorBranchPanelLayout);
        gsfMonitorBranchPanelLayout.setHorizontalGroup(
            gsfMonitorBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, gsfMonitorBranchPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(gsfMonitorBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(gsfBranchListComboBox, 0, 219, Short.MAX_VALUE)
                    .add(gsfMonitorBranchPanelLayout.createSequentialGroup()
                        .add(gsfAddBranchButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(gsfBranchLargetGSFButton))
                    .add(gsfInterfaceListComboBox, 0, 219, Short.MAX_VALUE)
                    .add(gsfAddInterfaceButton)
                    .add(gsfRemoveBranchButton)
                    .add(gsdfScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE))
                .addContainerGap())
        );
        gsfMonitorBranchPanelLayout.setVerticalGroup(
            gsfMonitorBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(gsfMonitorBranchPanelLayout.createSequentialGroup()
                .add(gsfBranchListComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gsfMonitorBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(gsfAddBranchButton)
                    .add(gsfBranchLargetGSFButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gsfInterfaceListComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gsfAddInterfaceButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gsdfScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 101, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gsfRemoveBranchButton)
                .addContainerGap(31, Short.MAX_VALUE))
        );

        gsfCalculateButton.setFont(new java.awt.Font("Dialog", 0, 10));
        gsfCalculateButton.setText("Calculate");
        gsfCalculateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gsfCalculateButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout gsfPanelLayout = new org.jdesktop.layout.GroupLayout(gsfPanel);
        gsfPanel.setLayout(gsfPanelLayout);
        gsfPanelLayout.setHorizontalGroup(
            gsfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, gsfPanelLayout.createSequentialGroup()
                .add(20, 20, 20)
                .add(gsfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(gsfInjectionPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                    .add(gsfWithdrawPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 253, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gsfMonitorBranchPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(19, 19, 19))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, gsfPanelLayout.createSequentialGroup()
                .addContainerGap(246, Short.MAX_VALUE)
                .add(gsfCalculateButton)
                .add(224, 224, 224))
        );
        gsfPanelLayout.setVerticalGroup(
            gsfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(gsfPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(gsfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(gsfPanelLayout.createSequentialGroup()
                        .add(gsfInjectionPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(gsfWithdrawPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE))
                    .add(gsfMonitorBranchPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(18, 18, 18)
                .add(gsfCalculateButton)
                .add(15, 15, 15))
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
                .add(24, 24, 24)
                .add(lodfSingleTypeRadioButton)
                .add(18, 18, 18)
                .add(lodfMultiTypeRadioButton)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        lodfTypePanelLayout.setVerticalGroup(
            lodfTypePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lodfTypePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(lodfSingleTypeRadioButton)
                .add(lodfMultiTypeRadioButton))
        );

        lodfBranchListComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        lodfBranchListComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lodfBranchListLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        lodfBranchListLabel.setText("Branch");

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
                .add(lodfOutageBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(lodfOutageBranchPanelLayout.createSequentialGroup()
                        .add(8, 8, 8)
                        .add(lodfBranchListLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(lodfBranchListComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 188, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(lodfOutageBranchPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(lodfOutageBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lodfOutageBranchPanelLayout.createSequentialGroup()
                                .add(lodfAddBranchButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(lodfRemoveBranchButton))
                            .add(lodfMultiBranchScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 197, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(lodfTypePanel, 0, 253, Short.MAX_VALUE))
                .addContainerGap(6, Short.MAX_VALUE))
        );
        lodfOutageBranchPanelLayout.setVerticalGroup(
            lodfOutageBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lodfOutageBranchPanelLayout.createSequentialGroup()
                .add(lodfTypePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(12, 12, 12)
                .add(lodfOutageBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lodfBranchListLabel)
                    .add(lodfBranchListComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(lodfMultiBranchScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 108, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lodfOutageBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lodfAddBranchButton)
                    .add(lodfRemoveBranchButton))
                .add(75, 75, 75))
        );

        lodfCalculateButton.setFont(new java.awt.Font("Dialog", 0, 12));
        lodfCalculateButton.setText("Calculate");
        lodfCalculateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lodfCalculateButtonActionPerformed(evt);
            }
        });

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
        lodfMonitorInterfaceListComboBox.setEnabled(false);

        lodfMonitorAddInterfaceButton.setFont(new java.awt.Font("Dialog", 0, 10));
        lodfMonitorAddInterfaceButton.setText("Add Interface");
        lodfMonitorAddInterfaceButton.setEnabled(false);
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
                    .add(lodfMonitorScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                    .add(lodfMonitorBranchListComboBox, 0, 227, Short.MAX_VALUE)
                    .add(lodfMonitorAddBranchButton)
                    .add(lodfMonitorInterfaceListComboBox, 0, 227, Short.MAX_VALUE)
                    .add(lodfMonitorAddInterfaceButton)
                    .add(lodfMonitorRemoveButton))
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
                .add(lodfMonitorScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lodfMonitorRemoveButton)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout lodfPanelLayout = new org.jdesktop.layout.GroupLayout(lodfPanel);
        lodfPanel.setLayout(lodfPanelLayout);
        lodfPanelLayout.setHorizontalGroup(
            lodfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lodfPanelLayout.createSequentialGroup()
                .add(lodfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lodfPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(lodfOutageBranchPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(lodfMonitorBranchPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(lodfPanelLayout.createSequentialGroup()
                        .add(229, 229, 229)
                        .add(lodfCalculateButton)))
                .addContainerGap())
        );
        lodfPanelLayout.setVerticalGroup(
            lodfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, lodfPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(lodfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lodfOutageBranchPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lodfPanelLayout.createSequentialGroup()
                        .add(lodfMonitorBranchPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(20, 20, 20)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lodfCalculateButton)
                .add(23, 23, 23))
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
                .addContainerGap(73, Short.MAX_VALUE)
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
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(ptdfCalculateButton)
                .add(26, 26, 26))
        );

        runDclfTabbedPane.addTab("PTDF", ptdfPanel);

        lossFactorPanel.setEnabled(false);

        lossInjectionPanel.setLayout(new java.awt.GridBagLayout());

        lossGenBusLabel.setText("Gen Bus    ");
        lossInjBusSelPanel.add(lossGenBusLabel);

        lossInjectBusComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        lossInjectBusComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        lossInjBusSelPanel.add(lossInjectBusComboBox);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        lossInjectionPanel.add(lossInjBusSelPanel, gridBagConstraints);

        lossWithdrawPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Load Bus", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 12))); // NOI18N
        lossWithdrawPanel.setLayout(new java.awt.GridBagLayout());

        ptdfWithSingleBusRadioButton2.setFont(new java.awt.Font("Dialog", 0, 12));
        ptdfWithSingleBusRadioButton2.setText("Single Bus");
        ptdfWithSingleBusRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ptdfWithSingleBusRadioButton2ActionPerformed(evt);
            }
        });
        lossSingleMultiBusPanel.add(ptdfWithSingleBusRadioButton2);

        ptdfWithMultiBusRadioButton2.setFont(new java.awt.Font("Dialog", 0, 12));
        ptdfWithMultiBusRadioButton2.setText("Multi-Buses");
        ptdfWithMultiBusRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ptdfWithMultiBusRadioButton2ActionPerformed(evt);
            }
        });
        lossSingleMultiBusPanel.add(ptdfWithMultiBusRadioButton2);

        lossWithdrawPanel.add(lossSingleMultiBusPanel, new java.awt.GridBagConstraints());

        ptdfWithdrawBusComboBox2.setFont(new java.awt.Font("Dialog", 0, 12));
        ptdfWithdrawBusComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        lossWithBusSelectPanel.add(ptdfWithdrawBusComboBox2);

        ptdfWithLoadBusRadioButton2.setFont(new java.awt.Font("Dialog", 0, 10));
        ptdfWithLoadBusRadioButton2.setText("Load ");
        ptdfWithLoadBusRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ptdfWithLoadBusRadioButton2ActionPerformed(evt);
            }
        });
        lossWithBusSelectPanel.add(ptdfWithLoadBusRadioButton2);

        ptdfWithAllBusRadioButton2.setFont(new java.awt.Font("Dialog", 0, 10));
        ptdfWithAllBusRadioButton2.setText("All Buses");
        ptdfWithAllBusRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ptdfWithAllBusRadioButton2ActionPerformed(evt);
            }
        });
        lossWithBusSelectPanel.add(ptdfWithAllBusRadioButton2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        lossWithdrawPanel.add(lossWithBusSelectPanel, gridBagConstraints);

        ptdfWithdarwBusList2.setFont(new java.awt.Font("Dialog", 0, 12));
        ptdfWithdarwBusList2.setEnabled(false);
        ptdfWithBusScrollPane2.setViewportView(ptdfWithdarwBusList2);

        ptdfLoadDistFactorLabel2.setFont(new java.awt.Font("Dialog", 0, 12));
        ptdfLoadDistFactorLabel2.setText("Load Disttribution Factor");
        ptdfLoadDistFactorLabel2.setEnabled(false);

        ptdfDistFactorTextField2.setText("100.0");
        ptdfDistFactorTextField2.setEnabled(false);

        ptdfPercentLabel2.setText("%");
        ptdfPercentLabel2.setEnabled(false);

        ptdfAddWithBusButton2.setFont(new java.awt.Font("Dialog", 0, 10));
        ptdfAddWithBusButton2.setText("Add");
        ptdfAddWithBusButton2.setEnabled(false);
        ptdfAddWithBusButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ptdfAddWithBusButton2ActionPerformed(evt);
            }
        });

        ptdfRemoveWithBusButton2.setFont(new java.awt.Font("Dialog", 0, 10));
        ptdfRemoveWithBusButton2.setText("Remove");
        ptdfRemoveWithBusButton2.setEnabled(false);
        ptdfRemoveWithBusButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ptdfRemoveWithBusButton2ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout lossWithMultiBusPanelLayout = new org.jdesktop.layout.GroupLayout(lossWithMultiBusPanel);
        lossWithMultiBusPanel.setLayout(lossWithMultiBusPanelLayout);
        lossWithMultiBusPanelLayout.setHorizontalGroup(
            lossWithMultiBusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lossWithMultiBusPanelLayout.createSequentialGroup()
                .add(lossWithMultiBusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lossWithMultiBusPanelLayout.createSequentialGroup()
                        .add(32, 32, 32)
                        .add(ptdfWithBusScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 133, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(lossWithMultiBusPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(lossWithMultiBusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(lossWithMultiBusPanelLayout.createSequentialGroup()
                                .add(ptdfAddWithBusButton2)
                                .add(18, 18, 18)
                                .add(ptdfRemoveWithBusButton2))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, lossWithMultiBusPanelLayout.createSequentialGroup()
                                .add(ptdfLoadDistFactorLabel2)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(ptdfDistFactorTextField2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(ptdfPercentLabel2)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        lossWithMultiBusPanelLayout.setVerticalGroup(
            lossWithMultiBusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lossWithMultiBusPanelLayout.createSequentialGroup()
                .add(lossWithMultiBusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(ptdfLoadDistFactorLabel2)
                    .add(ptdfDistFactorTextField2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(ptdfPercentLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ptdfWithBusScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 96, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(lossWithMultiBusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(ptdfRemoveWithBusButton2)
                    .add(ptdfAddWithBusButton2))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        lossWithdrawPanel.add(lossWithMultiBusPanel, gridBagConstraints);

        lossMeasBranchPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Monitor Branch", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 12))); // NOI18N

        lossBranchListComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        lossBranchListComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lossAddBranchButton.setFont(new java.awt.Font("Dialog", 0, 10));
        lossAddBranchButton.setText("Add Branch");
        lossAddBranchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lossAddBranchButtonActionPerformed(evt);
            }
        });

        lossInterfaceListComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        lossInterfaceListComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        lossInterfaceListComboBox.setEnabled(false);

        lossAddInterfaceButton.setFont(new java.awt.Font("Dialog", 0, 10));
        lossAddInterfaceButton.setText("Add Interface");
        lossAddInterfaceButton.setEnabled(false);
        lossAddInterfaceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lossAddInterfaceButtonActionPerformed(evt);
            }
        });

        ptdfMeasBranchList2.setFont(new java.awt.Font("Dialog", 0, 12));
        lossScrollPane.setViewportView(ptdfMeasBranchList2);

        lossRemoveBranchButton.setFont(new java.awt.Font("Dialog", 0, 10));
        lossRemoveBranchButton.setText("Remove");
        lossRemoveBranchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lossRemoveBranchButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout lossMeasBranchPanelLayout = new org.jdesktop.layout.GroupLayout(lossMeasBranchPanel);
        lossMeasBranchPanel.setLayout(lossMeasBranchPanelLayout);
        lossMeasBranchPanelLayout.setHorizontalGroup(
            lossMeasBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lossMeasBranchPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(lossMeasBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lossBranchListComboBox, 0, 138, Short.MAX_VALUE)
                    .add(lossScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 138, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lossAddBranchButton)
                    .add(lossRemoveBranchButton)
                    .add(lossInterfaceListComboBox, 0, 138, Short.MAX_VALUE)
                    .add(lossAddInterfaceButton))
                .addContainerGap())
        );
        lossMeasBranchPanelLayout.setVerticalGroup(
            lossMeasBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lossMeasBranchPanelLayout.createSequentialGroup()
                .add(lossBranchListComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lossAddBranchButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lossInterfaceListComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lossAddInterfaceButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lossScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 140, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lossRemoveBranchButton)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lossCalculateButton.setFont(new java.awt.Font("Dialog", 0, 10));
        lossCalculateButton.setText("Calculate");
        lossCalculateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lossCalculateButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout lossFactorPanelLayout = new org.jdesktop.layout.GroupLayout(lossFactorPanel);
        lossFactorPanel.setLayout(lossFactorPanelLayout);
        lossFactorPanelLayout.setHorizontalGroup(
            lossFactorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lossFactorPanelLayout.createSequentialGroup()
                .add(26, 26, 26)
                .add(lossFactorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(lossInjectionPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                    .add(lossWithdrawPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lossMeasBranchPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(98, 98, 98))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, lossFactorPanelLayout.createSequentialGroup()
                .addContainerGap(217, Short.MAX_VALUE)
                .add(lossCalculateButton)
                .add(247, 247, 247))
        );
        lossFactorPanelLayout.setVerticalGroup(
            lossFactorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lossFactorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(lossFactorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lossFactorPanelLayout.createSequentialGroup()
                        .add(lossInjectionPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(lossWithdrawPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE))
                    .add(lossMeasBranchPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lossCalculateButton)
                .add(65, 65, 65))
        );

        runDclfTabbedPane.addTab("LossFactor", lossFactorPanel);

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
                .addContainerGap(54, Short.MAX_VALUE))
        );
        areaTransPanelLayout.setVerticalGroup(
            areaTransPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(areaTransPanelLayout.createSequentialGroup()
                .add(20, 20, 20)
                .add(atInfoEditPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(29, 29, 29))
        );

        runDclfTabbedPane.addTab("Area Transfer", areaTransPanel);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(runDclfTabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(runDclfTabbedPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 399, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(59, 59, 59))
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
    private void gsfCalculateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsfCalculateButtonActionPerformed
        IpssLogger.getLogger().info("gsfCalculateButtonActionPerformed() called");
        saveEditor2GSF();
        
    	this.parent.setAlwaysOnTop(false);

    	if (!saveEditor2GSF())
    		return;
    	
		DclfAlgorithmDSL algoDsl = IpssPTrading.createDclfAlgorithm(_simuCtx.getAclfNet())
				.setRefBus();

		String outText = "";
		try {
			algoDsl.runDclfXmlCase(xmlCaseData, DclfAnalysisType.GSF);
			outText = SenAnalysisOutput.outGSF(xmlCaseData.getGenShiftFactor().get(0)).toString();
		} catch (PSSLException e) {
			IpssLogger.getLogger().severe(e.toString());
			outText = e.toString();
		}
		algoDsl.destroy();    	
    	
		UISpringFactory.getOutputTextDialog("GSF Calculation Results")
			.display(outText);    	
    }//GEN-LAST:event_gsfCalculateButtonActionPerformed

    private void gsfLoadDFactorRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsfLoadDFactorRadioButtonActionPerformed
        IpssLogger.getLogger().info("gsfLoadDFactorRadioButtonActionPerformed() called");
        setLoadDFactorRadioButton(this.gsfLoadDFactorRadioButton.isSelected());
    }//GEN-LAST:event_gsfLoadDFactorRadioButtonActionPerformed

    private void setLoadDFactorRadioButton(boolean checked) {
    	this.gsfLoadThreshholdLabel.setEnabled(checked);
    	this.gsfLoadThreshholdTextField.setEnabled(checked);
    }
    
    private void gsfLoadSingleBusRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsfLoadSingleBusRadioButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_gsfLoadSingleBusRadioButtonActionPerformed

    private void gsfLoadMultiBusRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsfLoadMultiBusRadioButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_gsfLoadMultiBusRadioButtonActionPerformed

    private void gsfAddWithBusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsfAddWithBusButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_gsfAddWithBusButtonActionPerformed

    private void gsfRemoveWithBusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsfRemoveWithBusButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_gsfRemoveWithBusButtonActionPerformed

    private void gsfAddBranchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsfAddBranchButtonActionPerformed
        IpssLogger.getLogger().info("gsfAddBranchButtonActionPerformed() called");
    	String id = (String)gsfBranchListComboBox.getSelectedItem();
    	RunUIUtilFunc.addItemJList(gsfMonitorBranchList, "b:"+id);
    }//GEN-LAST:event_gsfAddBranchButtonActionPerformed

    private void gsfAddInterfaceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsfAddInterfaceButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_gsfAddInterfaceButtonActionPerformed

    private void gsfRemoveBranchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsfRemoveBranchButtonActionPerformed
        IpssLogger.getLogger().info("gsfRemoveBranchButtonActionPerformed() called");
        RunUIUtilFunc.removeItemJList(this.gsfMonitorBranchList);
    }//GEN-LAST:event_gsfRemoveBranchButtonActionPerformed

    private void gsfBranchLargetGSFButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsfBranchLargetGSFButtonActionPerformed
        IpssLogger.getLogger().info("gsfBranchLargetGSFButtonActionPerformed() called");
    	this.parent.setAlwaysOnTop(false);

    	final DclfAlgorithmDSL algoDsl = IpssPTrading.createDclfAlgorithm(_simuCtx.getAclfNet())
				.setRefBus()
				.addLoadWithdrawBus(new Double(this.gsfLoadThreshholdTextField.getText()).doubleValue(), UnitType.mW);
    	
    	String id = (String)gsfBranchListComboBox.getSelectedItem();
		final String fromId = NetUtilFunc.findFromID(id);
		final String toId = NetUtilFunc.findToID(id);
		final String cirId = NetUtilFunc.findCirNo(id);    	
		final AclfBranch outageBranch = _simuCtx.getAclfNet().getAclfBranch(fromId, toId, cirId);
		
		new Thread() {
			public void run() {
				IAppStatus appStatus = GraphSpringFactory.getIpssGraphicEditor().getAppStatus();
				appStatus.busyStart(Constants.StatusBusyIndicatorPeriod,
						"Run Largest GSF Analysis ...", "Run SenAnalysis");
				String outText = "";
				try {
					algoDsl.outageBranch(fromId, toId, cirId);
					DblBranchValue maxLODF = algoDsl.largestLODF();
					DblBusValue maxGSF = algoDsl.monitorBranch(maxLODF.branch).largestGSF();
					outText = SenAnalysisOutput.outLargestGSF(outageBranch, maxLODF, maxGSF).toString();
				} catch (PSSLException e) {
					IpssLogger.getLogger().severe(e.toString());
					outText = e.toString();
				}
				algoDsl.destroy();
				UISpringFactory.getOutputTextDialog("Largest GSF Calculation Results").display(outText);   

				appStatus.busyStop("Run Largest GSF Analysis finished");			
			}
		}.start();
}//GEN-LAST:event_gsfBranchLargetGSFButtonActionPerformed

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

    private void lodfMonitorAddInterfaceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lodfMonitorAddInterfaceButtonActionPerformed
        IpssLogger.getLogger().info("lodfMonitorAddInterfaceButtonActionPerformed() called");
    }//GEN-LAST:event_lodfMonitorAddInterfaceButtonActionPerformed

    private void lodfMonitorRemoveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lodfMonitorRemoveButtonActionPerformed
        IpssLogger.getLogger().info("lodfMonitorRemoveButtonActionPerformed() called");
        RunUIUtilFunc.removeItemJList(this.lodfMonitorBranchInterfaceList);
    }//GEN-LAST:event_lodfMonitorRemoveButtonActionPerformed
    
    private void lodfCalculateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lodfCalculateButtonActionPerformed
        IpssLogger.getLogger().info("lodfCalculateButtonActionPerformed() called");
    	this.parent.setAlwaysOnTop(false);
    	
    	if (!saveEditor2LODF())
    		return;

    	String outText = "...";
		UISpringFactory.getOutputTextDialog("LODF Calculation Results").display(outText);   
    }//GEN-LAST:event_lodfCalculateButtonActionPerformed

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

private void ptdfWithSingleBusRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ptdfWithSingleBusRadioButton2ActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_ptdfWithSingleBusRadioButton2ActionPerformed

private void ptdfWithMultiBusRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ptdfWithMultiBusRadioButton2ActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_ptdfWithMultiBusRadioButton2ActionPerformed

private void ptdfWithLoadBusRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ptdfWithLoadBusRadioButton2ActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_ptdfWithLoadBusRadioButton2ActionPerformed

private void ptdfWithAllBusRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ptdfWithAllBusRadioButton2ActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_ptdfWithAllBusRadioButton2ActionPerformed

private void ptdfAddWithBusButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ptdfAddWithBusButton2ActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_ptdfAddWithBusButton2ActionPerformed

private void ptdfRemoveWithBusButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ptdfRemoveWithBusButton2ActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_ptdfRemoveWithBusButton2ActionPerformed

private void lossAddBranchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lossAddBranchButtonActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_lossAddBranchButtonActionPerformed

private void lossAddInterfaceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lossAddInterfaceButtonActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_lossAddInterfaceButtonActionPerformed

private void lossRemoveBranchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lossRemoveBranchButtonActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_lossRemoveBranchButtonActionPerformed

private void lossCalculateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lossCalculateButtonActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_lossCalculateButtonActionPerformed

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
    private javax.swing.JScrollPane gsdfScrollPane;
    private javax.swing.JButton gsfAddBranchButton;
    private javax.swing.JButton gsfAddInterfaceButton;
    private javax.swing.JButton gsfAddWithBusButton;
    private javax.swing.JButton gsfBranchLargetGSFButton;
    private javax.swing.JComboBox gsfBranchListComboBox;
    private javax.swing.JButton gsfCalculateButton;
    private javax.swing.JTextField gsfDistFactorTextField;
    private javax.swing.JLabel gsfGenBusLabel;
    private javax.swing.JPanel gsfInjBusSelPanel;
    private javax.swing.JComboBox gsfInjectBusComboBox;
    private javax.swing.JPanel gsfInjectionPanel;
    private javax.swing.JComboBox gsfInterfaceListComboBox;
    private javax.swing.JComboBox gsfLoadBusComboBox;
    private javax.swing.JLabel gsfLoadBusLabel;
    private javax.swing.JRadioButton gsfLoadDFactorRadioButton;
    private javax.swing.JRadioButton gsfLoadMultiBusRadioButton;
    private javax.swing.JRadioButton gsfLoadSingleBusRadioButton;
    private javax.swing.JLabel gsfLoadThreshholdLabel;
    private javax.swing.JTextField gsfLoadThreshholdTextField;
    private javax.swing.JList gsfMonitorBranchList;
    private javax.swing.JPanel gsfMonitorBranchPanel;
    private javax.swing.JPanel gsfPanel;
    private javax.swing.JLabel gsfPercentLabel;
    private javax.swing.JButton gsfRemoveBranchButton;
    private javax.swing.JButton gsfRemoveWithBusButton;
    private javax.swing.JScrollPane gsfWithBusScrollPane;
    private javax.swing.JPanel gsfWithBusSelectPanel;
    private javax.swing.JPanel gsfWithMultiBusPanel;
    private javax.swing.ButtonGroup gsfWithdrawButtonGroup;
    private javax.swing.JPanel gsfWithdrawMarginPanel;
    private javax.swing.JPanel gsfWithdrawPanel;
    private javax.swing.JPanel gsfWithdrawiBusPanel;
    private javax.swing.JButton lodfAddBranchButton;
    private javax.swing.JComboBox lodfBranchListComboBox;
    private javax.swing.JLabel lodfBranchListLabel;
    private javax.swing.JButton lodfCalculateButton;
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
    private javax.swing.JRadioButton lodfSingleTypeRadioButton;
    private javax.swing.ButtonGroup lodfTypeButtonGroup;
    private javax.swing.JPanel lodfTypePanel;
    private javax.swing.JButton lossAddBranchButton;
    private javax.swing.JButton lossAddInterfaceButton;
    private javax.swing.JComboBox lossBranchListComboBox;
    private javax.swing.JButton lossCalculateButton;
    private javax.swing.JPanel lossFactorPanel;
    private javax.swing.JLabel lossGenBusLabel;
    private javax.swing.JPanel lossInjBusSelPanel;
    private javax.swing.JComboBox lossInjectBusComboBox;
    private javax.swing.JPanel lossInjectionPanel;
    private javax.swing.JComboBox lossInterfaceListComboBox;
    private javax.swing.JPanel lossMeasBranchPanel;
    private javax.swing.JButton lossRemoveBranchButton;
    private javax.swing.JScrollPane lossScrollPane;
    private javax.swing.JPanel lossSingleMultiBusPanel;
    private javax.swing.JPanel lossWithBusSelectPanel;
    private javax.swing.JPanel lossWithMultiBusPanel;
    private javax.swing.JPanel lossWithdrawPanel;
    private javax.swing.JButton ptdfAddBranchButton;
    private javax.swing.JButton ptdfAddInterfaceButton;
    private javax.swing.JButton ptdfAddWithBusButton;
    private javax.swing.JButton ptdfAddWithBusButton2;
    private javax.swing.JComboBox ptdfBranchListComboBox;
    private javax.swing.JButton ptdfCalculateButton;
    private javax.swing.JTextField ptdfDistFactorTextField;
    private javax.swing.JTextField ptdfDistFactorTextField2;
    private javax.swing.JPanel ptdfInjBusPanel;
    private javax.swing.JPanel ptdfInjBusSelPanel;
    private javax.swing.JRadioButton ptdfInjectAllBusRadioButton;
    private javax.swing.JComboBox ptdfInjectBusComboBox;
    private javax.swing.JRadioButton ptdfInjectGenBusRadioButton;
    private javax.swing.JPanel ptdfInjectionPanel;
    private javax.swing.JComboBox ptdfInterfaceListComboBox;
    private javax.swing.JLabel ptdfLoadDistFactorLabel;
    private javax.swing.JLabel ptdfLoadDistFactorLabel2;
    private javax.swing.JList ptdfMeasBranchList;
    private javax.swing.JList ptdfMeasBranchList2;
    private javax.swing.JPanel ptdfMeasBranchPanel;
    private javax.swing.JRadioButton ptdfMultiInjectBusRadioButton;
    private javax.swing.JPanel ptdfPanel;
    private javax.swing.JLabel ptdfPercentLabel;
    private javax.swing.JLabel ptdfPercentLabel2;
    private javax.swing.JButton ptdfRemoveBranchButton;
    private javax.swing.JButton ptdfRemoveWithBusButton;
    private javax.swing.JButton ptdfRemoveWithBusButton2;
    private javax.swing.JScrollPane ptdfScrollPane;
    private javax.swing.JRadioButton ptdfSingleInjectBusRadioButton;
    private javax.swing.JPanel ptdfSingleMultiBusPanel;
    private javax.swing.JRadioButton ptdfWithAllBusRadioButton;
    private javax.swing.JRadioButton ptdfWithAllBusRadioButton2;
    private javax.swing.JScrollPane ptdfWithBusScrollPane;
    private javax.swing.JScrollPane ptdfWithBusScrollPane2;
    private javax.swing.JPanel ptdfWithBusSelectPanel;
    private javax.swing.JRadioButton ptdfWithLoadBusRadioButton;
    private javax.swing.JRadioButton ptdfWithLoadBusRadioButton2;
    private javax.swing.JPanel ptdfWithMultiBusPanel;
    private javax.swing.JRadioButton ptdfWithMultiBusRadioButton;
    private javax.swing.JRadioButton ptdfWithMultiBusRadioButton2;
    private javax.swing.JRadioButton ptdfWithSingleBusRadioButton;
    private javax.swing.JRadioButton ptdfWithSingleBusRadioButton2;
    private javax.swing.JList ptdfWithdarwBusList;
    private javax.swing.JList ptdfWithdarwBusList2;
    private javax.swing.JList ptdfWithdarwBusList3;
    private javax.swing.JComboBox ptdfWithdrawBusComboBox;
    private javax.swing.JComboBox ptdfWithdrawBusComboBox2;
    private javax.swing.JPanel ptdfWithdrawPanel;
    private javax.swing.JTabbedPane runDclfTabbedPane;
    private javax.swing.JLabel space;
    // End of variables declaration//GEN-END:variables
    
	class DataVerifier extends javax.swing.InputVerifier {
		public boolean verify(javax.swing.JComponent input) {
			if (input == null)
				return false;
			return true;
		}
	}
}
