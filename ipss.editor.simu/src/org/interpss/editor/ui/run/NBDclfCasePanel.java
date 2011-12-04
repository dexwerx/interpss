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

import org.interpss.display.DclfOutFunc;
import org.interpss.editor.jgraph.ui.edit.IFormDataPanel;
import org.interpss.editor.runAct.xml.XmlScriptDclfRun;
import org.interpss.editor.ui.IOutputTextDialog;
import org.interpss.editor.ui.RunUIUtilFunc;
import org.interpss.spring.UISpringFactory;
import org.interpss.xml.IpssXmlHelper;
import org.interpss.xml.IpssXmlParser;
import org.interpss.xml.StudyCaseHanlder;
import org.interpss.xml.schema.AreaRecXmlType;
import org.interpss.xml.schema.AreaTransferAnalysisXmlType;
import org.interpss.xml.schema.BranchRecXmlType;
import org.interpss.xml.schema.BusRecXmlType;
import org.interpss.xml.schema.DclfBranchSensitivityXmlType;
import org.interpss.xml.schema.DclfStudyCaseXmlType;
import org.interpss.xml.schema.SenAnalysisBusRecXmlType;
import org.interpss.xml.schema.SenBusAnalysisDataType;

import com.interpss.common.exp.InterpssRuntimeException;
import com.interpss.common.msg.IpssMessage;
import com.interpss.common.msg.IpssMsgListener;
import com.interpss.common.util.IpssLogger;
import com.interpss.core.DclfObjectFactory;
import com.interpss.core.dclf.DclfAlgorithm;
import com.interpss.simu.SimuContext;

public class NBDclfCasePanel extends javax.swing.JPanel implements IFormDataPanel, IpssMsgListener {
	private static final long serialVersionUID = 1;
	private JDialog parent;
	
    // private GFormContainer _netContainer = null;
    private SimuContext _simuCtx = null;

    // holds the current case data being edited
    private DclfBranchSensitivityXmlType tdFactor = null;
	private AreaTransferAnalysisXmlType areaTransfer = null;;
    
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
	    
	    //this.runDclfTabbedPane.setEnabledAt(1, _simuCtx.getAclfNet().isLfConverged());
	    this.runDclfTabbedPane.setEnabledAt(3, false);
	    this.runDclfTabbedPane.setEnabledAt(4, false);

		this.ptdfInjectBusComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.GenBus).toArray()));
		this.ptdfWithdrawBusComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.LoadBus).toArray()));
		this.ptdfBranchListComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.AllBranch).toArray()));
		
		this.atFromAreaComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.AreaNo).toArray()));
		this.atToAreaComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.AreaNo).toArray()));
		this.atBranchListComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.AllBranch).toArray()));
	}
/*
    public void setCaseData(DclfCaseData data) {
    	_caseData = data;
    }
 */   
    public void setXmlCaseData(DclfStudyCaseXmlType xmlCaseData) {
    	if (xmlCaseData.getPTransferDistFactor().size() == 0)
    		StudyCaseHanlder.addNewTDFactor(xmlCaseData);
    	this.tdFactor = xmlCaseData.getPTransferDistFactor().get(0);
    	if (xmlCaseData.getAreaTransferAnalysis().size() == 0) {
    		xmlCaseData.getAreaTransferAnalysis().add(IpssXmlParser.getFactory().createAreaTransferAnalysisXmlType());
    		StudyCaseHanlder.setNewAreaTransfer(xmlCaseData.getAreaTransferAnalysis().get(0));
    	}
    	this.areaTransfer = xmlCaseData.getAreaTransferAnalysis().get(0);
    }    
    
	/**
	*	Set form data to the editor
	*
	* @return false if there is any problem
	*/
	public boolean setForm2Editor() {
		IpssLogger.getLogger().info("NBAclfCasePanel setForm2Editor() called");
		
		if (!setTDFactor2Editor())
			return false;

		if (!setAreaTransfer2Editor())
			return false;
		
		return true;
	}

	public boolean setTDFactor2Editor() {
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

		return true;
	}

	public boolean setAreaTransfer2Editor() {
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
		saveEditor2TDFactor();
		saveEditor2AreaTransfer();
		return errMsg.size() == 0;
	}

	public boolean saveEditor2TDFactor() {
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
		return true;
	}
    
	public boolean saveEditor2AreaTransfer() {
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

		return true;
	}

	private void saveFromAreaBusList2AreaTransfer() {
		areaTransfer.setInjectBusList(null);
		areaTransfer.setInjectBusList(IpssXmlParser.getFactory().createDclfSensitivityXmlTypeInjectBusList());
		for(int i = 0; i < atFromAreaBusList.getModel().getSize(); i++) {
			SenAnalysisBusRecXmlType busRec = IpssXmlParser.getFactory().createSenAnalysisBusRecXmlType(); 
			areaTransfer.getInjectBusList().getInjectBus().add(busRec);
			String elem = (String)atFromAreaBusList.getModel().getElementAt(i);
			busRec.setBusId(RunUIUtilFunc.getId_IdPercent(elem));
			busRec.setPercent(RunUIUtilFunc.getPercent_IdPercent(elem));
		}
	}
	
	private void saveToAreaBusList2AreaTransfer() {
		areaTransfer.setWithdrawBusList(null);
		areaTransfer.setWithdrawBusList(IpssXmlParser.getFactory().createDclfSensitivityXmlTypeWithdrawBusList());
		for(int i = 0; i < atToAreaBusList.getModel().getSize(); i++) {
			SenAnalysisBusRecXmlType busRec = IpssXmlParser.getFactory().createSenAnalysisBusRecXmlType(); 
			areaTransfer.getWithdrawBusList().getWithdrawBus().add(busRec);
			String elem = (String)atToAreaBusList.getModel().getElementAt(i);
			busRec.setBusId(RunUIUtilFunc.getId_IdPercent(elem));
			busRec.setPercent(RunUIUtilFunc.getPercent_IdPercent(elem));
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

        lossInjectionBusButtonGroup = new javax.swing.ButtonGroup();
        lossInjectButtonGroup = new javax.swing.ButtonGroup();
        lossWithdrawBusButtonGroup = new javax.swing.ButtonGroup();
        lossWithdrawButtonGroup = new javax.swing.ButtonGroup();
        gsfInjectionBusButtonGroup = new javax.swing.ButtonGroup();
        gsfInjectButtonGroup = new javax.swing.ButtonGroup();
        gsfWithdrawBusButtonGroup = new javax.swing.ButtonGroup();
        gsfWithdrawButtonGroup = new javax.swing.ButtonGroup();
        ptdfInjectionBusButtonGroup = new javax.swing.ButtonGroup();
        ptdfInjectButtonGroup = new javax.swing.ButtonGroup();
        ptdfWithdrawBusButtonGroup = new javax.swing.ButtonGroup();
        ptdfWithdrawButtonGroup = new javax.swing.ButtonGroup();
        lodfInjectionBusButtonGroup = new javax.swing.ButtonGroup();
        lodfInjectButtonGroup = new javax.swing.ButtonGroup();
        lodfWithdrawBusButtonGroup = new javax.swing.ButtonGroup();
        lodfWithdrawButtonGroup = new javax.swing.ButtonGroup();
        runDclfTabbedPane = new javax.swing.JTabbedPane();
        lodfPanel = new javax.swing.JPanel();
        ptdfInjectionPanel1 = new javax.swing.JPanel();
        ptdfInjectBusComboBox1 = new javax.swing.JComboBox();
        ptdfMeasBranchPanel1 = new javax.swing.JPanel();
        ptdfBranchListComboBox1 = new javax.swing.JComboBox();
        ptdfAddBranchButton1 = new javax.swing.JButton();
        ptdfInterfaceListComboBox1 = new javax.swing.JComboBox();
        ptdfAddInterfaceButton1 = new javax.swing.JButton();
        ptdfScrollPane1 = new javax.swing.JScrollPane();
        ptdfMeasBranchList1 = new javax.swing.JList();
        ptdfRemoveBranchButton1 = new javax.swing.JButton();
        ptdfCalculateButton1 = new javax.swing.JButton();
        gsfPanel = new javax.swing.JPanel();
        gsfInjectionPanel = new javax.swing.JPanel();
        gsfInjBusSelPanel = new javax.swing.JPanel();
        gsfGenBusLabel = new javax.swing.JLabel();
        gsfInjectBusComboBox = new javax.swing.JComboBox();
        gsfWithdrawPanel = new javax.swing.JPanel();
        gsfSingleMultiBusPanel = new javax.swing.JPanel();
        ptdfWithSingleBusRadioButton3 = new javax.swing.JRadioButton();
        ptdfWithMultiBusRadioButton3 = new javax.swing.JRadioButton();
        gsfWithBusSelectPanel = new javax.swing.JPanel();
        ptdfWithdrawBusComboBox3 = new javax.swing.JComboBox();
        ptdfWithLoadBusRadioButton3 = new javax.swing.JRadioButton();
        ptdfWithAllBusRadioButton3 = new javax.swing.JRadioButton();
        gsfWithMultiBusPanel = new javax.swing.JPanel();
        ptdfWithBusScrollPane3 = new javax.swing.JScrollPane();
        ptdfWithdarwBusList3 = new javax.swing.JList();
        ptdfLoadDistFactorLabel3 = new javax.swing.JLabel();
        ptdfDistFactorTextField3 = new javax.swing.JTextField();
        ptdfPercentLabel3 = new javax.swing.JLabel();
        ptdfAddWithBusButton3 = new javax.swing.JButton();
        ptdfRemoveWithBusButton3 = new javax.swing.JButton();
        gsfMeasBranchPanel = new javax.swing.JPanel();
        gsfBranchListComboBox = new javax.swing.JComboBox();
        gsfAddBranchButton = new javax.swing.JButton();
        gsfInterfaceListComboBox = new javax.swing.JComboBox();
        gsfAddInterfaceButton = new javax.swing.JButton();
        gsdfScrollPane = new javax.swing.JScrollPane();
        ptdfMeasBranchList3 = new javax.swing.JList();
        gsfRemoveBranchButton = new javax.swing.JButton();
        gsfCalculateButton = new javax.swing.JButton();
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
        atCalculateButton = new javax.swing.JButton();
        atAclfCalculateButton = new javax.swing.JButton();
        atSeAssessButton = new javax.swing.JButton();

        runDclfTabbedPane.setFont(new java.awt.Font("Dialog", 0, 12));
        runDclfTabbedPane.setMinimumSize(new java.awt.Dimension(80, 48));
        runDclfTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                panelSelectionChanged(evt);
            }
        });

        ptdfInjectionPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Injection Bus", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 12))); // NOI18N

        ptdfInjectBusComboBox1.setFont(new java.awt.Font("Dialog", 0, 12));
        ptdfInjectBusComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        org.jdesktop.layout.GroupLayout ptdfInjectionPanel1Layout = new org.jdesktop.layout.GroupLayout(ptdfInjectionPanel1);
        ptdfInjectionPanel1.setLayout(ptdfInjectionPanel1Layout);
        ptdfInjectionPanel1Layout.setHorizontalGroup(
            ptdfInjectionPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, ptdfInjectionPanel1Layout.createSequentialGroup()
                .addContainerGap(26, Short.MAX_VALUE)
                .add(ptdfInjectBusComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 145, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        ptdfInjectionPanel1Layout.setVerticalGroup(
            ptdfInjectionPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ptdfInjectionPanel1Layout.createSequentialGroup()
                .add(ptdfInjectBusComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        ptdfMeasBranchPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Measurement Branches", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 12))); // NOI18N

        ptdfBranchListComboBox1.setFont(new java.awt.Font("Dialog", 0, 12));
        ptdfBranchListComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        ptdfAddBranchButton1.setFont(new java.awt.Font("Dialog", 0, 10));
        ptdfAddBranchButton1.setText("Add Branch");
        ptdfAddBranchButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ptdfAddBranchButton1ActionPerformed(evt);
            }
        });

        ptdfInterfaceListComboBox1.setFont(new java.awt.Font("Dialog", 0, 12));
        ptdfInterfaceListComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        ptdfInterfaceListComboBox1.setEnabled(false);

        ptdfAddInterfaceButton1.setFont(new java.awt.Font("Dialog", 0, 10));
        ptdfAddInterfaceButton1.setText("Add Interface");
        ptdfAddInterfaceButton1.setEnabled(false);
        ptdfAddInterfaceButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ptdfAddInterfaceButton1ActionPerformed(evt);
            }
        });

        ptdfMeasBranchList1.setFont(new java.awt.Font("Dialog", 0, 12));
        ptdfScrollPane1.setViewportView(ptdfMeasBranchList1);

        ptdfRemoveBranchButton1.setFont(new java.awt.Font("Dialog", 0, 10));
        ptdfRemoveBranchButton1.setText("Remove");
        ptdfRemoveBranchButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ptdfRemoveBranchButton1ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout ptdfMeasBranchPanel1Layout = new org.jdesktop.layout.GroupLayout(ptdfMeasBranchPanel1);
        ptdfMeasBranchPanel1.setLayout(ptdfMeasBranchPanel1Layout);
        ptdfMeasBranchPanel1Layout.setHorizontalGroup(
            ptdfMeasBranchPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ptdfMeasBranchPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(ptdfMeasBranchPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(ptdfBranchListComboBox1, 0, 179, Short.MAX_VALUE)
                    .add(ptdfScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 138, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(ptdfAddBranchButton1)
                    .add(ptdfRemoveBranchButton1)
                    .add(ptdfInterfaceListComboBox1, 0, 179, Short.MAX_VALUE)
                    .add(ptdfAddInterfaceButton1))
                .addContainerGap())
        );
        ptdfMeasBranchPanel1Layout.setVerticalGroup(
            ptdfMeasBranchPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ptdfMeasBranchPanel1Layout.createSequentialGroup()
                .add(ptdfBranchListComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ptdfAddBranchButton1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ptdfInterfaceListComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ptdfAddInterfaceButton1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ptdfScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 140, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ptdfRemoveBranchButton1)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        ptdfCalculateButton1.setFont(new java.awt.Font("Dialog", 0, 10));
        ptdfCalculateButton1.setText("Calculate");
        ptdfCalculateButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ptdfCalculateButton1ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout lodfPanelLayout = new org.jdesktop.layout.GroupLayout(lodfPanel);
        lodfPanel.setLayout(lodfPanelLayout);
        lodfPanelLayout.setHorizontalGroup(
            lodfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lodfPanelLayout.createSequentialGroup()
                .addContainerGap(23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(lodfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lodfPanelLayout.createSequentialGroup()
                        .add(ptdfInjectionPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(ptdfMeasBranchPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lodfPanelLayout.createSequentialGroup()
                        .add(ptdfCalculateButton1)
                        .add(186, 186, 186))))
        );
        lodfPanelLayout.setVerticalGroup(
            lodfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lodfPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(lodfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(ptdfInjectionPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lodfPanelLayout.createSequentialGroup()
                        .add(ptdfMeasBranchPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(ptdfCalculateButton1)))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        runDclfTabbedPane.addTab("LODF", lodfPanel);

        gsfInjectionPanel.setLayout(new java.awt.GridBagLayout());

        gsfGenBusLabel.setText("Gen Bus    ");
        gsfInjBusSelPanel.add(gsfGenBusLabel);

        gsfInjectBusComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        gsfInjectBusComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gsfInjBusSelPanel.add(gsfInjectBusComboBox);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gsfInjectionPanel.add(gsfInjBusSelPanel, gridBagConstraints);

        gsfWithdrawPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Load Bus", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 12))); // NOI18N
        gsfWithdrawPanel.setLayout(new java.awt.GridBagLayout());

        ptdfWithdrawButtonGroup.add(ptdfWithSingleBusRadioButton3);
        ptdfWithSingleBusRadioButton3.setFont(new java.awt.Font("Dialog", 0, 12));
        ptdfWithSingleBusRadioButton3.setText("Single Bus");
        ptdfWithSingleBusRadioButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ptdfWithSingleBusRadioButton3ActionPerformed(evt);
            }
        });
        gsfSingleMultiBusPanel.add(ptdfWithSingleBusRadioButton3);

        ptdfWithdrawButtonGroup.add(ptdfWithMultiBusRadioButton3);
        ptdfWithMultiBusRadioButton3.setFont(new java.awt.Font("Dialog", 0, 12));
        ptdfWithMultiBusRadioButton3.setText("Multi-Buses");
        ptdfWithMultiBusRadioButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ptdfWithMultiBusRadioButton3ActionPerformed(evt);
            }
        });
        gsfSingleMultiBusPanel.add(ptdfWithMultiBusRadioButton3);

        gsfWithdrawPanel.add(gsfSingleMultiBusPanel, new java.awt.GridBagConstraints());

        ptdfWithdrawBusComboBox3.setFont(new java.awt.Font("Dialog", 0, 12));
        ptdfWithdrawBusComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gsfWithBusSelectPanel.add(ptdfWithdrawBusComboBox3);

        ptdfWithdrawBusButtonGroup.add(ptdfWithLoadBusRadioButton3);
        ptdfWithLoadBusRadioButton3.setFont(new java.awt.Font("Dialog", 0, 10));
        ptdfWithLoadBusRadioButton3.setText("Load ");
        ptdfWithLoadBusRadioButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ptdfWithLoadBusRadioButton3ActionPerformed(evt);
            }
        });
        gsfWithBusSelectPanel.add(ptdfWithLoadBusRadioButton3);

        ptdfWithdrawBusButtonGroup.add(ptdfWithAllBusRadioButton3);
        ptdfWithAllBusRadioButton3.setFont(new java.awt.Font("Dialog", 0, 10));
        ptdfWithAllBusRadioButton3.setText("All Buses");
        ptdfWithAllBusRadioButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ptdfWithAllBusRadioButton3ActionPerformed(evt);
            }
        });
        gsfWithBusSelectPanel.add(ptdfWithAllBusRadioButton3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gsfWithdrawPanel.add(gsfWithBusSelectPanel, gridBagConstraints);

        ptdfWithdarwBusList3.setFont(new java.awt.Font("Dialog", 0, 12));
        ptdfWithdarwBusList3.setEnabled(false);
        ptdfWithBusScrollPane3.setViewportView(ptdfWithdarwBusList3);

        ptdfLoadDistFactorLabel3.setFont(new java.awt.Font("Dialog", 0, 12));
        ptdfLoadDistFactorLabel3.setText("Load Disttribution Factor");
        ptdfLoadDistFactorLabel3.setEnabled(false);

        ptdfDistFactorTextField3.setText("100.0");
        ptdfDistFactorTextField3.setEnabled(false);

        ptdfPercentLabel3.setText("%");
        ptdfPercentLabel3.setEnabled(false);

        ptdfAddWithBusButton3.setFont(new java.awt.Font("Dialog", 0, 10));
        ptdfAddWithBusButton3.setText("Add");
        ptdfAddWithBusButton3.setEnabled(false);
        ptdfAddWithBusButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ptdfAddWithBusButton3ActionPerformed(evt);
            }
        });

        ptdfRemoveWithBusButton3.setFont(new java.awt.Font("Dialog", 0, 10));
        ptdfRemoveWithBusButton3.setText("Remove");
        ptdfRemoveWithBusButton3.setEnabled(false);
        ptdfRemoveWithBusButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ptdfRemoveWithBusButton3ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout gsfWithMultiBusPanelLayout = new org.jdesktop.layout.GroupLayout(gsfWithMultiBusPanel);
        gsfWithMultiBusPanel.setLayout(gsfWithMultiBusPanelLayout);
        gsfWithMultiBusPanelLayout.setHorizontalGroup(
            gsfWithMultiBusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(gsfWithMultiBusPanelLayout.createSequentialGroup()
                .add(gsfWithMultiBusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(gsfWithMultiBusPanelLayout.createSequentialGroup()
                        .add(32, 32, 32)
                        .add(ptdfWithBusScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 133, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(gsfWithMultiBusPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(gsfWithMultiBusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(gsfWithMultiBusPanelLayout.createSequentialGroup()
                                .add(ptdfAddWithBusButton3)
                                .add(18, 18, 18)
                                .add(ptdfRemoveWithBusButton3))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, gsfWithMultiBusPanelLayout.createSequentialGroup()
                                .add(ptdfLoadDistFactorLabel3)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(ptdfDistFactorTextField3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(ptdfPercentLabel3)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        gsfWithMultiBusPanelLayout.setVerticalGroup(
            gsfWithMultiBusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(gsfWithMultiBusPanelLayout.createSequentialGroup()
                .add(gsfWithMultiBusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(ptdfLoadDistFactorLabel3)
                    .add(ptdfDistFactorTextField3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(ptdfPercentLabel3))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ptdfWithBusScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 96, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(gsfWithMultiBusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(ptdfRemoveWithBusButton3)
                    .add(ptdfAddWithBusButton3))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gsfWithdrawPanel.add(gsfWithMultiBusPanel, gridBagConstraints);

        gsfMeasBranchPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Monitor Branch", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 12))); // NOI18N

        gsfBranchListComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        gsfBranchListComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        gsfAddBranchButton.setFont(new java.awt.Font("Dialog", 0, 10));
        gsfAddBranchButton.setText("Add Branch");
        gsfAddBranchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gsfAddBranchButtonActionPerformed(evt);
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

        ptdfMeasBranchList3.setFont(new java.awt.Font("Dialog", 0, 12));
        gsdfScrollPane.setViewportView(ptdfMeasBranchList3);

        gsfRemoveBranchButton.setFont(new java.awt.Font("Dialog", 0, 10));
        gsfRemoveBranchButton.setText("Remove");
        gsfRemoveBranchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gsfRemoveBranchButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout gsfMeasBranchPanelLayout = new org.jdesktop.layout.GroupLayout(gsfMeasBranchPanel);
        gsfMeasBranchPanel.setLayout(gsfMeasBranchPanelLayout);
        gsfMeasBranchPanelLayout.setHorizontalGroup(
            gsfMeasBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(gsfMeasBranchPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(gsfMeasBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(gsfBranchListComboBox, 0, 138, Short.MAX_VALUE)
                    .add(gsdfScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 138, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(gsfAddBranchButton)
                    .add(gsfRemoveBranchButton)
                    .add(gsfInterfaceListComboBox, 0, 138, Short.MAX_VALUE)
                    .add(gsfAddInterfaceButton))
                .addContainerGap())
        );
        gsfMeasBranchPanelLayout.setVerticalGroup(
            gsfMeasBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(gsfMeasBranchPanelLayout.createSequentialGroup()
                .add(gsfBranchListComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gsfAddBranchButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gsfInterfaceListComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gsfAddInterfaceButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gsdfScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 140, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gsfRemoveBranchButton)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
            .add(gsfPanelLayout.createSequentialGroup()
                .add(26, 26, 26)
                .add(gsfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(gsfInjectionPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                    .add(gsfWithdrawPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gsfMeasBranchPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(51, 51, 51))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, gsfPanelLayout.createSequentialGroup()
                .addContainerGap(217, Short.MAX_VALUE)
                .add(gsfCalculateButton)
                .add(200, 200, 200))
        );
        gsfPanelLayout.setVerticalGroup(
            gsfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(gsfPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(gsfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(gsfPanelLayout.createSequentialGroup()
                        .add(gsfInjectionPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(gsfWithdrawPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE))
                    .add(gsfMeasBranchPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(gsfCalculateButton)
                .add(65, 65, 65))
        );

        runDclfTabbedPane.addTab("GSF", gsfPanel);

        ptdfInjectionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Injection Bus", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 12))); // NOI18N
        ptdfInjectionPanel.setLayout(new java.awt.GridBagLayout());

        ptdfInjectButtonGroup.add(ptdfSingleInjectBusRadioButton);
        ptdfSingleInjectBusRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        ptdfSingleInjectBusRadioButton.setText("Single Bus");
        ptdfSingleInjectBusRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ptdfSingleInjectBusRadioButtonActionPerformed(evt);
            }
        });
        ptdfInjBusPanel.add(ptdfSingleInjectBusRadioButton);

        ptdfInjectButtonGroup.add(ptdfMultiInjectBusRadioButton);
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

        ptdfInjectionBusButtonGroup.add(ptdfInjectGenBusRadioButton);
        ptdfInjectGenBusRadioButton.setFont(new java.awt.Font("Dialog", 0, 10));
        ptdfInjectGenBusRadioButton.setText("Gen Buses");
        ptdfInjectGenBusRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ptdfInjectGenBusRadioButtonActionPerformed(evt);
            }
        });
        ptdfInjBusSelPanel.add(ptdfInjectGenBusRadioButton);

        ptdfInjectionBusButtonGroup.add(ptdfInjectAllBusRadioButton);
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

        ptdfWithdrawButtonGroup.add(ptdfWithSingleBusRadioButton);
        ptdfWithSingleBusRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        ptdfWithSingleBusRadioButton.setText("Single Bus");
        ptdfWithSingleBusRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ptdfWithSingleBusRadioButtonActionPerformed(evt);
            }
        });
        ptdfSingleMultiBusPanel.add(ptdfWithSingleBusRadioButton);

        ptdfWithdrawButtonGroup.add(ptdfWithMultiBusRadioButton);
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

        ptdfWithdrawBusButtonGroup.add(ptdfWithLoadBusRadioButton);
        ptdfWithLoadBusRadioButton.setFont(new java.awt.Font("Dialog", 0, 10));
        ptdfWithLoadBusRadioButton.setText("Load ");
        ptdfWithLoadBusRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ptdfWithLoadBusRadioButtonActionPerformed(evt);
            }
        });
        ptdfWithBusSelectPanel.add(ptdfWithLoadBusRadioButton);

        ptdfWithdrawBusButtonGroup.add(ptdfWithAllBusRadioButton);
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
                        .add(32, 32, 32)
                        .add(ptdfWithBusScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 133, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(ptdfWithMultiBusPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(ptdfWithMultiBusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(ptdfWithMultiBusPanelLayout.createSequentialGroup()
                                .add(ptdfAddWithBusButton)
                                .add(18, 18, 18)
                                .add(ptdfRemoveWithBusButton))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, ptdfWithMultiBusPanelLayout.createSequentialGroup()
                                .add(ptdfLoadDistFactorLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(ptdfDistFactorTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 36, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(ptdfPercentLabel)))
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
                .add(ptdfWithBusScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 96, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
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
                .add(26, 26, 26)
                .add(ptdfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(ptdfWithdrawPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                    .add(ptdfInjectionPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ptdfMeasBranchPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(51, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, ptdfPanelLayout.createSequentialGroup()
                .addContainerGap(217, Short.MAX_VALUE)
                .add(ptdfCalculateButton)
                .add(200, 200, 200))
        );
        ptdfPanelLayout.setVerticalGroup(
            ptdfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ptdfPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(ptdfPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(ptdfPanelLayout.createSequentialGroup()
                        .add(ptdfInjectionPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(ptdfWithdrawPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(ptdfMeasBranchPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ptdfCalculateButton)
                .add(26, 26, 26))
        );

        runDclfTabbedPane.addTab("PTDF", ptdfPanel);

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

        ptdfWithdrawButtonGroup.add(ptdfWithSingleBusRadioButton2);
        ptdfWithSingleBusRadioButton2.setFont(new java.awt.Font("Dialog", 0, 12));
        ptdfWithSingleBusRadioButton2.setText("Single Bus");
        ptdfWithSingleBusRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ptdfWithSingleBusRadioButton2ActionPerformed(evt);
            }
        });
        lossSingleMultiBusPanel.add(ptdfWithSingleBusRadioButton2);

        ptdfWithdrawButtonGroup.add(ptdfWithMultiBusRadioButton2);
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

        ptdfWithdrawBusButtonGroup.add(ptdfWithLoadBusRadioButton2);
        ptdfWithLoadBusRadioButton2.setFont(new java.awt.Font("Dialog", 0, 10));
        ptdfWithLoadBusRadioButton2.setText("Load ");
        ptdfWithLoadBusRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ptdfWithLoadBusRadioButton2ActionPerformed(evt);
            }
        });
        lossWithBusSelectPanel.add(ptdfWithLoadBusRadioButton2);

        ptdfWithdrawBusButtonGroup.add(ptdfWithAllBusRadioButton2);
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
                .add(51, 51, 51))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, lossFactorPanelLayout.createSequentialGroup()
                .addContainerGap(217, Short.MAX_VALUE)
                .add(lossCalculateButton)
                .add(200, 200, 200))
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
            .add(atFromAreaScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 96, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(atFromDFPanelLayout.createSequentialGroup()
                .add(atFromDFPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(atFromDFPanelLayout.createSequentialGroup()
                        .add(atFromAreaEditButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(atFromDFactorEditTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(atFromAreaUpdateButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(atFromAreaRemoveButton))
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
                .add(atBranchListScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 128, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(atRemoveBranchButton)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout atInfoEditPanelLayout = new org.jdesktop.layout.GroupLayout(atInfoEditPanel);
        atInfoEditPanel.setLayout(atInfoEditPanelLayout);
        atInfoEditPanelLayout.setHorizontalGroup(
            atInfoEditPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(atInfoEditPanelLayout.createSequentialGroup()
                .add(atInfoEditPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(atInfoEditPanelLayout.createSequentialGroup()
                        .add(2, 2, 2)
                        .add(atInfoEditPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(atToDFPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(atFromDFPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(atInfoEditPanelLayout.createSequentialGroup()
                        .add(53, 53, 53)
                        .add(atDeratingFactorLabel)
                        .add(29, 29, 29)
                        .add(atDeratingFactorTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(atMeasBranchPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
            .add(atInfoEditPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(atTransAmtLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(atTransAmtTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(atTransAmtUnitComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 36, Short.MAX_VALUE)
                .add(atFromAreaLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(atFromAreaComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(atToAreaLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(atToAreaComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(17, 17, 17))
        );
        atInfoEditPanelLayout.setVerticalGroup(
            atInfoEditPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, atInfoEditPanelLayout.createSequentialGroup()
                .add(atInfoEditPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(atTransAmtLabel)
                    .add(atTransAmtTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(atTransAmtUnitComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(atFromAreaLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                    .add(atFromAreaComboBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                    .add(atToAreaLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                    .add(atToAreaComboBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE))
                .add(6, 6, 6)
                .add(atInfoEditPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(atMeasBranchPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(atInfoEditPanelLayout.createSequentialGroup()
                        .add(atInfoEditPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(atDeratingFactorLabel)
                            .add(atDeratingFactorTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(atFromDFPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 129, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(atToDFPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(11, 11, 11))
        );

        atCalculateButton.setFont(new java.awt.Font("Dialog", 0, 10));
        atCalculateButton.setText("Calculate");
        atCalculateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                atCalculateButtonActionPerformed(evt);
            }
        });

        atAclfCalculateButton.setFont(new java.awt.Font("Dialog", 0, 10));
        atAclfCalculateButton.setText("AC Loadflow");
        atAclfCalculateButton.setEnabled(false);
        atAclfCalculateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                atAclfCalculateButtonActionPerformed(evt);
            }
        });

        atSeAssessButton.setFont(new java.awt.Font("Dialog", 0, 10));
        atSeAssessButton.setText("Sec Assess");
        atSeAssessButton.setEnabled(false);
        atSeAssessButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                atSeAssessButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout areaTransPanelLayout = new org.jdesktop.layout.GroupLayout(areaTransPanel);
        areaTransPanel.setLayout(areaTransPanelLayout);
        areaTransPanelLayout.setHorizontalGroup(
            areaTransPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(areaTransPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(areaTransPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(atInfoEditPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, areaTransPanelLayout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 118, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(atCalculateButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(atAclfCalculateButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(atSeAssessButton)
                        .add(80, 80, 80)))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        areaTransPanelLayout.setVerticalGroup(
            areaTransPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(areaTransPanelLayout.createSequentialGroup()
                .add(20, 20, 20)
                .add(atInfoEditPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(areaTransPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(atSeAssessButton)
                    .add(atAclfCalculateButton)
                    .add(atCalculateButton))
                .add(26, 26, 26))
        );

        runDclfTabbedPane.addTab("Area Transfer", areaTransPanel);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(runDclfTabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .add(runDclfTabbedPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 423, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(35, 35, 35))
        );
    }// </editor-fold>//GEN-END:initComponents
 
    private void panelSelectionChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_panelSelectionChanged
    	if ( runDclfTabbedPane.getSelectedIndex() == 0 )
        	IpssLogger.getLogger().info("Panel selection changed - PTDF Panel");
    	else if ( runDclfTabbedPane.getSelectedIndex() == 1 ) {
        	IpssLogger.getLogger().info("Panel selection changed - Area Transfer Analysis Panel");
    	}	
    }//GEN-LAST:event_panelSelectionChanged

    private void ptdfWithSingleBusRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ptdfWithSingleBusRadioButtonActionPerformed
    	setMultiBusWithdrawStatus(false);
    	tdFactor.setWithdrawBusType(SenBusAnalysisDataType.SINGLE_BUS);
    	while (tdFactor.getWithdrawBusList().getWithdrawBus().size() > 0)
    		tdFactor.getWithdrawBusList().getWithdrawBus().remove(0);
    	tdFactor.getWithdrawBusList().getWithdrawBus().add(
    			IpssXmlParser.getFactory().createSenAnalysisBusRecXmlType());
    	tdFactor.getWithdrawBusList().getWithdrawBus().get(0).setBusId((String)ptdfWithdrawBusComboBox.getSelectedItem());
}//GEN-LAST:event_ptdfWithSingleBusRadioButtonActionPerformed

    private void ptdfRemoveBranchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ptdfRemoveBranchButtonActionPerformed
    	if (!ptdfMeasBranchList.isSelectionEmpty()) {
    		tdFactor.getBranch().remove(ptdfMeasBranchList.getSelectedIndex());
        	ptdfMeasBranchList.setModel(new javax.swing.DefaultComboBoxModel(
        			IpssXmlHelper.getBranchIdAry(tdFactor.getBranch())));
    	}
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
    	setMultiBusWithdrawStatus(true);
    	tdFactor.setWithdrawBusType(SenBusAnalysisDataType.MULTIPLE_BUS);
    	if (tdFactor.getWithdrawBusList() == null)
    		tdFactor.setWithdrawBusList(IpssXmlParser.getFactory().createDclfSensitivityXmlTypeWithdrawBusList());
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
    	if (!ptdfWithdarwBusList.isSelectionEmpty()) {
    		tdFactor.getWithdrawBusList().getWithdrawBus().remove(ptdfWithdarwBusList.getSelectedIndex());
    		ptdfWithdarwBusList.setModel(new javax.swing.DefaultComboBoxModel(
    			IpssXmlHelper.getSenAnalysisBusItemList(tdFactor.getWithdrawBusList().getWithdrawBus())));
    	}
}//GEN-LAST:event_ptdfRemoveWithBusButtonActionPerformed

    private void ptdfAddWithBusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ptdfAddWithBusButtonActionPerformed
    	String id = (String)ptdfWithdrawBusComboBox.getSelectedItem();
    	double percent = new Double(this.ptdfDistFactorTextField.getText()).doubleValue();
    	SenAnalysisBusRecXmlType bus = IpssXmlParser.getFactory().createSenAnalysisBusRecXmlType(); 
    	tdFactor.getWithdrawBusList().getWithdrawBus().add(bus);
    	bus.setBusId(id);
    	bus.setPercent(percent);
    	ptdfWithdarwBusList.setModel(new javax.swing.DefaultComboBoxModel(
    			IpssXmlHelper.getSenAnalysisBusItemList(tdFactor.getWithdrawBusList().getWithdrawBus())));
}//GEN-LAST:event_ptdfAddWithBusButtonActionPerformed

    private void ptdfAddBranchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ptdfAddBranchButtonActionPerformed
    	String id = (String)ptdfBranchListComboBox.getSelectedItem();
    	BranchRecXmlType braRec = IpssXmlParser.getFactory().createBranchRecXmlType();
    	tdFactor.getBranch().add(braRec);
    	IpssXmlHelper.setBranchRec(braRec, id);
    	ptdfMeasBranchList.setModel(new javax.swing.DefaultComboBoxModel(
    			IpssXmlHelper.getBranchIdAry(tdFactor.getBranch())));
}//GEN-LAST:event_ptdfAddBranchButtonActionPerformed

    private void ptdfMultiInjectBusRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ptdfMultiInjectBusRadioButtonActionPerformed
    	tdFactor.setInjectBusType(SenBusAnalysisDataType.MULTIPLE_BUS);
        ptdfInjectGenBusRadioButtonActionPerformed(null);
    	ptdfInjectAllBusRadioButton.setEnabled(false);
}//GEN-LAST:event_ptdfMultiInjectBusRadioButtonActionPerformed

    private void ptdfSingleInjectBusRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ptdfSingleInjectBusRadioButtonActionPerformed
    	tdFactor.setInjectBusType(SenBusAnalysisDataType.SINGLE_BUS);
    	ptdfInjectAllBusRadioButton.setEnabled(true);
}//GEN-LAST:event_ptdfSingleInjectBusRadioButtonActionPerformed

    private void ptdfAddInterfaceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ptdfAddInterfaceButtonActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_ptdfAddInterfaceButtonActionPerformed

    private void ptdfCalculateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ptdfCalculateButtonActionPerformed
    	this.parent.setAlwaysOnTop(false);
    	DclfAlgorithm algo = DclfObjectFactory.createDclfAlgorithm(_simuCtx.getAclfNet());
    	_simuCtx.setDclfAlgorithm(algo);
    	if (!algo.checkCondition())
    		return;
    	if (!saveEditor2TDFactor())
    		return;
    	XmlScriptDclfRun.calPTDistFactor(tdFactor, algo);
    	IOutputTextDialog dialog = UISpringFactory.getOutputTextDialog("Sensitivity Analysis Results");
    	String str = DclfOutFunc.pTransferDistFactorResults(tdFactor, _simuCtx.getDclfAlgorithm());
    	dialog.display(str);
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
	String id = (String)atBranchListComboBox.getSelectedItem();
	BranchRecXmlType braRec = IpssXmlParser.getFactory().createBranchRecXmlType(); 
	areaTransfer.getBranch().add(braRec);
	IpssXmlHelper.setBranchRec(braRec, id);
	atMeasBranchList.setModel(new javax.swing.DefaultComboBoxModel(
			IpssXmlHelper.getBranchIdAry(areaTransfer.getBranch())));
}//GEN-LAST:event_atAddBranchButtonActionPerformed

private void atAddInterfaceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_atAddInterfaceButtonActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_atAddInterfaceButtonActionPerformed

private void atRemoveBranchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_atRemoveBranchButtonActionPerformed
	if (!atMeasBranchList.isSelectionEmpty()) {
		areaTransfer.getBranch().remove(atMeasBranchList.getSelectedIndex());
		atMeasBranchList.setModel(new javax.swing.DefaultComboBoxModel(
				IpssXmlHelper.getBranchIdAry(areaTransfer.getBranch())));
	}
}//GEN-LAST:event_atRemoveBranchButtonActionPerformed

private void atFromAreaRemoveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_atFromAreaRemoveButtonActionPerformed
	if (!atFromAreaBusList.isSelectionEmpty()) {
		areaTransfer.getInjectBusList().getInjectBus().remove(atFromAreaBusList.getSelectedIndex());
		atFromAreaBusList.setModel(new javax.swing.DefaultComboBoxModel(
				IpssXmlHelper.getSenAnalysisBusItemList(areaTransfer.getInjectBusList().getInjectBus())));
	}
}//GEN-LAST:event_atFromAreaRemoveButtonActionPerformed

private void atToAreaRemoveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_atToAreaRemoveButtonActionPerformed
	if (!atToAreaBusList.isSelectionEmpty()) {
		areaTransfer.getWithdrawBusList().getWithdrawBus().remove(atToAreaBusList.getSelectedIndex());
		atToAreaBusList.setModel(new javax.swing.DefaultComboBoxModel(
				IpssXmlHelper.getSenAnalysisBusItemList(areaTransfer.getWithdrawBusList().getWithdrawBus())));
	}
}//GEN-LAST:event_atToAreaRemoveButtonActionPerformed

private void atFromAreaComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_atFromAreaComboBoxActionPerformed
	String no = (String)this.atFromAreaComboBox.getSelectedItem();
	this.areaTransfer.getFromArea().setAreaNo(new Integer(no).intValue());
	atFromAreaBusList.setModel(new javax.swing.DefaultComboBoxModel(
			RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.GenInAreaDFactor, 
					this.areaTransfer.getFromArea().getAreaNo()).toArray()));
}//GEN-LAST:event_atFromAreaComboBoxActionPerformed

private void atToAreaComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_atToAreaComboBoxActionPerformed
	String no = (String)this.atToAreaComboBox.getSelectedItem();
	this.areaTransfer.getToArea().setAreaNo(new Integer(no).intValue());
	atToAreaBusList.setModel(new javax.swing.DefaultComboBoxModel(
			RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.GenInAreaDFactor, 
					this.areaTransfer.getToArea().getAreaNo()).toArray()));
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
	if (!atFromAreaBusList.isSelectionEmpty()) {
		// the Bus list might have been changed due to changing the area number
		saveFromAreaBusList2AreaTransfer();
		double percent = new Double(atFromDFactorEditTextField.getText()).doubleValue();
		String s = (String)atFromAreaBusList.getSelectedValue();
		String id = RunUIUtilFunc.getId_IdPercent(s);
		SenAnalysisBusRecXmlType bus = (SenAnalysisBusRecXmlType)IpssXmlHelper.getBusRecord(id, areaTransfer.getInjectBusList().getInjectBus());
		bus.setPercent(percent);
		atFromAreaBusList.setModel(new javax.swing.DefaultComboBoxModel(
    			IpssXmlHelper.getSenAnalysisBusItemList(areaTransfer.getInjectBusList().getInjectBus())));
		atFromDFactorEditTextField.setEnabled(false);
		atFromAreaUpdateButton.setEnabled(false);
	}
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
	if (!atToAreaBusList.isSelectionEmpty()) {
		// the Bus list might have been changed due to changing the area number
		saveToAreaBusList2AreaTransfer();
		double percent = new Double(atToDFactorEditTextField.getText()).doubleValue();
		String s = (String)atToAreaBusList.getSelectedValue();
		String id = RunUIUtilFunc.getId_IdPercent(s);
		SenAnalysisBusRecXmlType bus = (SenAnalysisBusRecXmlType)IpssXmlHelper.getBusRecord(id, 
						areaTransfer.getWithdrawBusList().getWithdrawBus());
		bus.setPercent(percent);
		atToAreaBusList.setModel(new javax.swing.DefaultComboBoxModel(
	    			IpssXmlHelper.getSenAnalysisBusItemList(areaTransfer.getWithdrawBusList().getWithdrawBus())));
		atToDFactorEditTextField.setEnabled(false);
		atToAreaUpdateButton.setEnabled(false);
	}
}//GEN-LAST:event_atToAreaUpdateButtonActionPerformed

private void atCalculateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_atCalculateButtonActionPerformed
	this.parent.setAlwaysOnTop(false);
	DclfAlgorithm algo = DclfObjectFactory.createDclfAlgorithm(_simuCtx.getAclfNet());
	_simuCtx.setDclfAlgorithm(algo);
	if (!algo.checkCondition())
		return;
	if (!saveEditor2AreaTransfer())
		return;
	XmlScriptDclfRun.calAreaTransferFactor(areaTransfer, algo, _simuCtx.getMsgHub());
	IOutputTextDialog dialog = UISpringFactory.getOutputTextDialog("Area Transfer Analysis Results");
	String str = DclfOutFunc.areaTransferAnalysisResults(areaTransfer, _simuCtx.getDclfAlgorithm());
	dialog.display(str);
}//GEN-LAST:event_atCalculateButtonActionPerformed

private void atAclfCalculateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_atAclfCalculateButtonActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_atAclfCalculateButtonActionPerformed

private void atSeAssessButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_atSeAssessButtonActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_atSeAssessButtonActionPerformed

private void ptdfAddBranchButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ptdfAddBranchButton1ActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_ptdfAddBranchButton1ActionPerformed

private void ptdfAddInterfaceButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ptdfAddInterfaceButton1ActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_ptdfAddInterfaceButton1ActionPerformed

private void ptdfRemoveBranchButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ptdfRemoveBranchButton1ActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_ptdfRemoveBranchButton1ActionPerformed

private void ptdfCalculateButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ptdfCalculateButton1ActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_ptdfCalculateButton1ActionPerformed

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

private void ptdfWithSingleBusRadioButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ptdfWithSingleBusRadioButton3ActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_ptdfWithSingleBusRadioButton3ActionPerformed

private void ptdfWithMultiBusRadioButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ptdfWithMultiBusRadioButton3ActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_ptdfWithMultiBusRadioButton3ActionPerformed

private void ptdfWithLoadBusRadioButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ptdfWithLoadBusRadioButton3ActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_ptdfWithLoadBusRadioButton3ActionPerformed

private void ptdfWithAllBusRadioButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ptdfWithAllBusRadioButton3ActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_ptdfWithAllBusRadioButton3ActionPerformed

private void ptdfAddWithBusButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ptdfAddWithBusButton3ActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_ptdfAddWithBusButton3ActionPerformed

private void ptdfRemoveWithBusButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ptdfRemoveWithBusButton3ActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_ptdfRemoveWithBusButton3ActionPerformed

private void gsfAddBranchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsfAddBranchButtonActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_gsfAddBranchButtonActionPerformed

private void gsfAddInterfaceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsfAddInterfaceButtonActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_gsfAddInterfaceButtonActionPerformed

private void gsfRemoveBranchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsfRemoveBranchButtonActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_gsfRemoveBranchButtonActionPerformed

private void gsfCalculateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gsfCalculateButtonActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_gsfCalculateButtonActionPerformed

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel areaTransPanel;
    private javax.swing.JButton atAclfCalculateButton;
    private javax.swing.JButton atAddBranchButton;
    private javax.swing.JButton atAddInterfaceButton;
    private javax.swing.JComboBox atBranchListComboBox;
    private javax.swing.JScrollPane atBranchListScrollPane;
    private javax.swing.JButton atCalculateButton;
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
    private javax.swing.JButton atSeAssessButton;
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
    private javax.swing.JComboBox gsfBranchListComboBox;
    private javax.swing.JButton gsfCalculateButton;
    private javax.swing.JLabel gsfGenBusLabel;
    private javax.swing.JPanel gsfInjBusSelPanel;
    private javax.swing.JComboBox gsfInjectBusComboBox;
    private javax.swing.ButtonGroup gsfInjectButtonGroup;
    private javax.swing.ButtonGroup gsfInjectionBusButtonGroup;
    private javax.swing.JPanel gsfInjectionPanel;
    private javax.swing.JComboBox gsfInterfaceListComboBox;
    private javax.swing.JPanel gsfMeasBranchPanel;
    private javax.swing.JPanel gsfPanel;
    private javax.swing.JButton gsfRemoveBranchButton;
    private javax.swing.JPanel gsfSingleMultiBusPanel;
    private javax.swing.JPanel gsfWithBusSelectPanel;
    private javax.swing.JPanel gsfWithMultiBusPanel;
    private javax.swing.ButtonGroup gsfWithdrawBusButtonGroup;
    private javax.swing.ButtonGroup gsfWithdrawButtonGroup;
    private javax.swing.JPanel gsfWithdrawPanel;
    private javax.swing.ButtonGroup lodfInjectButtonGroup;
    private javax.swing.ButtonGroup lodfInjectionBusButtonGroup;
    private javax.swing.JPanel lodfPanel;
    private javax.swing.ButtonGroup lodfWithdrawBusButtonGroup;
    private javax.swing.ButtonGroup lodfWithdrawButtonGroup;
    private javax.swing.JButton lossAddBranchButton;
    private javax.swing.JButton lossAddInterfaceButton;
    private javax.swing.JComboBox lossBranchListComboBox;
    private javax.swing.JButton lossCalculateButton;
    private javax.swing.JPanel lossFactorPanel;
    private javax.swing.JLabel lossGenBusLabel;
    private javax.swing.JPanel lossInjBusSelPanel;
    private javax.swing.JComboBox lossInjectBusComboBox;
    private javax.swing.ButtonGroup lossInjectButtonGroup;
    private javax.swing.ButtonGroup lossInjectionBusButtonGroup;
    private javax.swing.JPanel lossInjectionPanel;
    private javax.swing.JComboBox lossInterfaceListComboBox;
    private javax.swing.JPanel lossMeasBranchPanel;
    private javax.swing.JButton lossRemoveBranchButton;
    private javax.swing.JScrollPane lossScrollPane;
    private javax.swing.JPanel lossSingleMultiBusPanel;
    private javax.swing.JPanel lossWithBusSelectPanel;
    private javax.swing.JPanel lossWithMultiBusPanel;
    private javax.swing.ButtonGroup lossWithdrawBusButtonGroup;
    private javax.swing.ButtonGroup lossWithdrawButtonGroup;
    private javax.swing.JPanel lossWithdrawPanel;
    private javax.swing.JButton ptdfAddBranchButton;
    private javax.swing.JButton ptdfAddBranchButton1;
    private javax.swing.JButton ptdfAddInterfaceButton;
    private javax.swing.JButton ptdfAddInterfaceButton1;
    private javax.swing.JButton ptdfAddWithBusButton;
    private javax.swing.JButton ptdfAddWithBusButton2;
    private javax.swing.JButton ptdfAddWithBusButton3;
    private javax.swing.JComboBox ptdfBranchListComboBox;
    private javax.swing.JComboBox ptdfBranchListComboBox1;
    private javax.swing.JButton ptdfCalculateButton;
    private javax.swing.JButton ptdfCalculateButton1;
    private javax.swing.JTextField ptdfDistFactorTextField;
    private javax.swing.JTextField ptdfDistFactorTextField2;
    private javax.swing.JTextField ptdfDistFactorTextField3;
    private javax.swing.JPanel ptdfInjBusPanel;
    private javax.swing.JPanel ptdfInjBusSelPanel;
    private javax.swing.JRadioButton ptdfInjectAllBusRadioButton;
    private javax.swing.JComboBox ptdfInjectBusComboBox;
    private javax.swing.JComboBox ptdfInjectBusComboBox1;
    private javax.swing.ButtonGroup ptdfInjectButtonGroup;
    private javax.swing.JRadioButton ptdfInjectGenBusRadioButton;
    private javax.swing.ButtonGroup ptdfInjectionBusButtonGroup;
    private javax.swing.JPanel ptdfInjectionPanel;
    private javax.swing.JPanel ptdfInjectionPanel1;
    private javax.swing.JComboBox ptdfInterfaceListComboBox;
    private javax.swing.JComboBox ptdfInterfaceListComboBox1;
    private javax.swing.JLabel ptdfLoadDistFactorLabel;
    private javax.swing.JLabel ptdfLoadDistFactorLabel2;
    private javax.swing.JLabel ptdfLoadDistFactorLabel3;
    private javax.swing.JList ptdfMeasBranchList;
    private javax.swing.JList ptdfMeasBranchList1;
    private javax.swing.JList ptdfMeasBranchList2;
    private javax.swing.JList ptdfMeasBranchList3;
    private javax.swing.JPanel ptdfMeasBranchPanel;
    private javax.swing.JPanel ptdfMeasBranchPanel1;
    private javax.swing.JRadioButton ptdfMultiInjectBusRadioButton;
    private javax.swing.JPanel ptdfPanel;
    private javax.swing.JLabel ptdfPercentLabel;
    private javax.swing.JLabel ptdfPercentLabel2;
    private javax.swing.JLabel ptdfPercentLabel3;
    private javax.swing.JButton ptdfRemoveBranchButton;
    private javax.swing.JButton ptdfRemoveBranchButton1;
    private javax.swing.JButton ptdfRemoveWithBusButton;
    private javax.swing.JButton ptdfRemoveWithBusButton2;
    private javax.swing.JButton ptdfRemoveWithBusButton3;
    private javax.swing.JScrollPane ptdfScrollPane;
    private javax.swing.JScrollPane ptdfScrollPane1;
    private javax.swing.JRadioButton ptdfSingleInjectBusRadioButton;
    private javax.swing.JPanel ptdfSingleMultiBusPanel;
    private javax.swing.JRadioButton ptdfWithAllBusRadioButton;
    private javax.swing.JRadioButton ptdfWithAllBusRadioButton2;
    private javax.swing.JRadioButton ptdfWithAllBusRadioButton3;
    private javax.swing.JScrollPane ptdfWithBusScrollPane;
    private javax.swing.JScrollPane ptdfWithBusScrollPane2;
    private javax.swing.JScrollPane ptdfWithBusScrollPane3;
    private javax.swing.JPanel ptdfWithBusSelectPanel;
    private javax.swing.JRadioButton ptdfWithLoadBusRadioButton;
    private javax.swing.JRadioButton ptdfWithLoadBusRadioButton2;
    private javax.swing.JRadioButton ptdfWithLoadBusRadioButton3;
    private javax.swing.JPanel ptdfWithMultiBusPanel;
    private javax.swing.JRadioButton ptdfWithMultiBusRadioButton;
    private javax.swing.JRadioButton ptdfWithMultiBusRadioButton2;
    private javax.swing.JRadioButton ptdfWithMultiBusRadioButton3;
    private javax.swing.JRadioButton ptdfWithSingleBusRadioButton;
    private javax.swing.JRadioButton ptdfWithSingleBusRadioButton2;
    private javax.swing.JRadioButton ptdfWithSingleBusRadioButton3;
    private javax.swing.JList ptdfWithdarwBusList;
    private javax.swing.JList ptdfWithdarwBusList2;
    private javax.swing.JList ptdfWithdarwBusList3;
    private javax.swing.ButtonGroup ptdfWithdrawBusButtonGroup;
    private javax.swing.JComboBox ptdfWithdrawBusComboBox;
    private javax.swing.JComboBox ptdfWithdrawBusComboBox2;
    private javax.swing.JComboBox ptdfWithdrawBusComboBox3;
    private javax.swing.ButtonGroup ptdfWithdrawButtonGroup;
    private javax.swing.JPanel ptdfWithdrawPanel;
    private javax.swing.JTabbedPane runDclfTabbedPane;
    // End of variables declaration//GEN-END:variables
    
	class DataVerifier extends javax.swing.InputVerifier {
		public boolean verify(javax.swing.JComponent input) {
			if (input == null)
				return false;
			return true;
		}
	}
}
