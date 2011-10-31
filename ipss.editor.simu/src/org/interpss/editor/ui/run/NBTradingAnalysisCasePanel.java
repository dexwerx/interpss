 /*
  * @(#)NBTradingAnalysisCasePanel.java   
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
import org.interpss.editor.ui.UISpringAppContext;
import org.interpss.xml.IpssXmlHelper;
import org.interpss.xml.IpssXmlParser;
import org.interpss.xml.StudyCaseHanlder;
import org.interpss.xml.schema.AreaRecXmlType;
import org.interpss.xml.schema.AreaTransferAnalysisXmlType;
import org.interpss.xml.schema.BranchRecXmlType;
import org.interpss.xml.schema.SenAnalysisBusRecXmlType;
import org.interpss.xml.schema.TradingStudyCaseXmlType;

import com.interpss.common.exp.InvalidOperationException;
import com.interpss.common.msg.IpssMessage;
import com.interpss.common.msg.IpssMsgListener;
import com.interpss.common.util.IpssLogger;
import com.interpss.core.DclfObjectFactory;
import com.interpss.core.dclf.DclfAlgorithm;
import com.interpss.simu.SimuContext;

public class NBTradingAnalysisCasePanel extends javax.swing.JPanel implements IFormDataPanel, IpssMsgListener {
	private static final long serialVersionUID = 1;
	private JDialog parent;
	
    // private GFormContainer _netContainer = null;
    private SimuContext _simuCtx = null;

    // holds the current case data being edited
	private TradingStudyCaseXmlType tradingCase = null;;
	private AreaTransferAnalysisXmlType areaTransfer = null;;
    
    /** Creates new form NBAclfCasePanel */
    public NBTradingAnalysisCasePanel(JDialog parent) {
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
  	   throw new InvalidOperationException("Method not implemented");
     }
     
    public void init(Object netContainer, Object simuCtx) {
    	// for non-graphic file, netContainer == null
		IpssLogger.getLogger().info("NBAclfCasePanel init() called");
	    //_netContainer = (GFormContainer)netContainer;
	    _simuCtx = (SimuContext)simuCtx;
	    
		this.tradeFromAreaComboBox.setModel(new javax.swing.DefaultComboBoxModel(
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
    public void setXmlCaseData(TradingStudyCaseXmlType xmlCaseData) {
    	if (xmlCaseData.getAreaTransferAnalysis().get(0) != null) {
    		AreaTransferAnalysisXmlType areaTrans = IpssXmlParser.getFactory().createAreaTransferAnalysisXmlType();
    		xmlCaseData.getAreaTransferAnalysis().add(areaTrans);
    		StudyCaseHanlder.setNewAreaTransfer(areaTrans);
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
		
		if (!setAreaTransfer2Editor())
			return false;
		
		return true;
	}

	public boolean setAreaTransfer2Editor() {
		this.tradeTransAmtTextField.setText(String.format("%4.2f", this.areaTransfer.getTransderAmountMW()));
		this.tradeTransAmtUnitComboBox.setSelectedItem("MW");
		
		AreaRecXmlType area = this.areaTransfer.getFromArea(); 
		if (area == null) {
			area = IpssXmlParser.getFactory().createAreaRecXmlType();
			this.areaTransfer.setFromArea(area);
			String no = (String)this.tradeFromAreaComboBox.getSelectedItem();
			area.setAreaNo(new Integer(no).intValue());
		}
		this.tradeFromAreaComboBox.setSelectedItem(new Integer(area.getAreaNo()).toString());
			
		area = this.areaTransfer.getToArea();
		if (area == null) {
			area = IpssXmlParser.getFactory().createAreaRecXmlType();
			this.areaTransfer.setToArea(area);
			String no = (String)this.atToAreaComboBox.getSelectedItem();
			area.setAreaNo(new Integer(no).intValue());
		}
		this.atToAreaComboBox.setSelectedItem(new Integer(area.getAreaNo()).toString());

		if (this.areaTransfer.getDeratingFactor() != 0.0)
			tradeDeratingFactorTextField.setText(String.format("%4.2f", this.areaTransfer.getDeratingFactor()));
		else
			tradeDeratingFactorTextField.setText("1.00");
			
		if (this.areaTransfer.getInjectBusList() != null && this.areaTransfer.getInjectBusList().getInjectBus().size() > 0) {
			tradeFromAreaBusList.setModel(new javax.swing.DefaultComboBoxModel(
	    			IpssXmlHelper.getSenAnalysisBusItemList(areaTransfer.getInjectBusList().getInjectBus())));
		}
		else
			tradeFromAreaBusList.setModel(new javax.swing.DefaultComboBoxModel(
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

    	tradeFromDFactorEditTextField.setEnabled(false);
    	tradeFromAreaUpdateButton.setEnabled(false);
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
		saveEditor2AreaTransfer();
		return errMsg.size() == 0;
	}

	public boolean saveEditor2AreaTransfer() {
		double amt = new Double(this.tradeTransAmtTextField.getText()).doubleValue();
		this.areaTransfer.setTransderAmountMW(((String)this.tradeTransAmtUnitComboBox.getSelectedItem()).equals("MW")?
							amt : amt * _simuCtx.getNetwork().getBaseKva()/1000.0);

		String no = (String)this.tradeFromAreaComboBox.getSelectedItem();
		this.areaTransfer.getFromArea().setAreaNo(new Integer(no).intValue());
			
		no = (String)this.atToAreaComboBox.getSelectedItem();
		this.areaTransfer.getToArea().setAreaNo(new Integer(no).intValue());

		this.areaTransfer.setDeratingFactor(
				new Double(tradeDeratingFactorTextField.getText()).doubleValue());

		saveFromAreaBusList2AreaTransfer();
		saveToAreaBusList2AreaTransfer();

		return true;
	}

	private void saveFromAreaBusList2AreaTransfer() {
		areaTransfer.setInjectBusList(null);
		areaTransfer.setInjectBusList(IpssXmlParser.getFactory().createDclfSensitivityXmlTypeInjectBusList());
		for(int i = 0; i < tradeFromAreaBusList.getModel().getSize(); i++) {
			SenAnalysisBusRecXmlType busRec = IpssXmlParser.getFactory().createSenAnalysisBusRecXmlType();
			areaTransfer.getInjectBusList().getInjectBus().add(busRec);
			String elem = (String)tradeFromAreaBusList.getModel().getElementAt(i);
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

        tradingTypeButtonGroup = new javax.swing.ButtonGroup();
        tradeLabel = new javax.swing.JLabel();
        tradeComboBox = new javax.swing.JComboBox();
        tradeAddButton = new javax.swing.JButton();
        tradeDeleteButton = new javax.swing.JButton();
        zonalRadioButton = new javax.swing.JRadioButton();
        nodalRadioButton = new javax.swing.JRadioButton();
        tradeInfoEditPanel = new javax.swing.JPanel();
        tradeTransAmtLabel = new javax.swing.JLabel();
        tradeTransAmtTextField = new javax.swing.JTextField();
        tradeTransAmtUnitComboBox = new javax.swing.JComboBox();
        tradeDeratingFactorLabel = new javax.swing.JLabel();
        tradeDeratingFactorTextField = new javax.swing.JTextField();
        tradeFromDFPanel = new javax.swing.JPanel();
        tradeFromAreaLabel = new javax.swing.JLabel();
        tradeFromAreaComboBox = new javax.swing.JComboBox();
        tradeFromAreaScrollPane = new javax.swing.JScrollPane();
        tradeFromAreaBusList = new javax.swing.JList();
        tradeFromAreaEditButton = new javax.swing.JButton();
        tradeFromAreaUpdateButton = new javax.swing.JButton();
        tradeFromDFactorEditTextField = new javax.swing.JTextField();
        tradeFromAreaRemoveButton = new javax.swing.JButton();
        tradeToDFPanel = new javax.swing.JPanel();
        atToAreaScrollPane = new javax.swing.JScrollPane();
        atToAreaBusList = new javax.swing.JList();
        atToAreaEditButton = new javax.swing.JButton();
        atToDFactorEditTextField = new javax.swing.JTextField();
        atToAreaUpdateButton = new javax.swing.JButton();
        atToAreaRemoveButton = new javax.swing.JButton();
        atToAreaLabel = new javax.swing.JLabel();
        atToAreaComboBox = new javax.swing.JComboBox();
        tradeMeasBranchPanel = new javax.swing.JPanel();
        atBranchListComboBox = new javax.swing.JComboBox();
        atAddBranchButton = new javax.swing.JButton();
        atBranchListScrollPane = new javax.swing.JScrollPane();
        atMeasBranchList = new javax.swing.JList();
        atRemoveBranchButton = new javax.swing.JButton();
        tradeMeasInterfacePanel = new javax.swing.JPanel();
        atAddBranchButton1 = new javax.swing.JButton();
        atBranchListScrollPane1 = new javax.swing.JScrollPane();
        atMeasBranchList1 = new javax.swing.JList();
        atRemoveBranchButton1 = new javax.swing.JButton();
        atInterfaceListComboBox = new javax.swing.JComboBox();
        tradeCalculateButton = new javax.swing.JButton();
        tradeAclfCalculateButton = new javax.swing.JButton();
        tradeSeAssessButton = new javax.swing.JButton();

        tradeLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        tradeLabel.setText("Trade");

        tradeComboBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        tradeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        tradeComboBox.setEnabled(false);
        tradeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tradeComboBoxActionPerformed(evt);
            }
        });

        tradeAddButton.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        tradeAddButton.setText("Add");
        tradeAddButton.setEnabled(false);
        tradeAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tradeAddButtonActionPerformed(evt);
            }
        });

        tradeDeleteButton.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        tradeDeleteButton.setText("Delete");
        tradeDeleteButton.setEnabled(false);
        tradeDeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tradeDeleteButtonActionPerformed(evt);
            }
        });

        tradingTypeButtonGroup.add(zonalRadioButton);
        zonalRadioButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        zonalRadioButton.setSelected(true);
        zonalRadioButton.setText("Zonal");

        tradingTypeButtonGroup.add(nodalRadioButton);
        nodalRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        nodalRadioButton.setText("Nodal");

        tradeTransAmtLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        tradeTransAmtLabel.setText("Transfer Amount");

        tradeTransAmtTextField.setColumns(5);
        tradeTransAmtTextField.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        tradeTransAmtTextField.setText("100.0");

        tradeTransAmtUnitComboBox.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        tradeTransAmtUnitComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "MW", "PU" }));

        tradeDeratingFactorLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        tradeDeratingFactorLabel.setText("Derating Factor");

        tradeDeratingFactorTextField.setColumns(3);
        tradeDeratingFactorTextField.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        tradeDeratingFactorTextField.setText("1.00");

        tradeFromDFPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "From Area DistFactor", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 12))); // NOI18N

        tradeFromAreaLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        tradeFromAreaLabel.setText("From Area   ");

        tradeFromAreaComboBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        tradeFromAreaComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1" }));
        tradeFromAreaComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tradeFromAreaComboBoxActionPerformed(evt);
            }
        });

        tradeFromAreaBusList.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        tradeFromAreaScrollPane.setViewportView(tradeFromAreaBusList);

        tradeFromAreaEditButton.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        tradeFromAreaEditButton.setText("Edit");
        tradeFromAreaEditButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tradeFromAreaEditButtonActionPerformed(evt);
            }
        });

        tradeFromAreaUpdateButton.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        tradeFromAreaUpdateButton.setText("U");
        tradeFromAreaUpdateButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        tradeFromAreaUpdateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tradeFromAreaUpdateButtonActionPerformed(evt);
            }
        });

        tradeFromDFactorEditTextField.setColumns(5);
        tradeFromDFactorEditTextField.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        tradeFromDFactorEditTextField.setText("100.0");

        tradeFromAreaRemoveButton.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        tradeFromAreaRemoveButton.setText("Remove");
        tradeFromAreaRemoveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tradeFromAreaRemoveButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout tradeFromDFPanelLayout = new org.jdesktop.layout.GroupLayout(tradeFromDFPanel);
        tradeFromDFPanel.setLayout(tradeFromDFPanelLayout);
        tradeFromDFPanelLayout.setHorizontalGroup(
            tradeFromDFPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tradeFromDFPanelLayout.createSequentialGroup()
                .add(tradeFromDFPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(tradeFromDFPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(tradeFromAreaScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 133, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(6, 6, 6)
                        .add(tradeFromDFPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(tradeFromAreaRemoveButton)
                            .add(tradeFromDFPanelLayout.createSequentialGroup()
                                .add(tradeFromDFPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                    .add(tradeFromDFactorEditTextField, 0, 0, Short.MAX_VALUE)
                                    .add(tradeFromAreaEditButton))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(tradeFromAreaUpdateButton))))
                    .add(tradeFromDFPanelLayout.createSequentialGroup()
                        .add(11, 11, 11)
                        .add(tradeFromAreaLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(tradeFromAreaComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 72, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        tradeFromDFPanelLayout.setVerticalGroup(
            tradeFromDFPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, tradeFromDFPanelLayout.createSequentialGroup()
                .add(tradeFromDFPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(tradeFromAreaLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                    .add(tradeFromAreaComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(12, 12, 12)
                .add(tradeFromDFPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(tradeFromAreaScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 96, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(tradeFromDFPanelLayout.createSequentialGroup()
                        .add(tradeFromAreaEditButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(tradeFromDFPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(tradeFromDFactorEditTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(tradeFromAreaUpdateButton))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(tradeFromAreaRemoveButton)))
                .addContainerGap())
        );

        tradeToDFPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("To Area DistFactor"));

        atToAreaBusList.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
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

        atToAreaLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        atToAreaLabel.setText("To Area   ");

        atToAreaComboBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        atToAreaComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1" }));
        atToAreaComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                atToAreaComboBoxActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout tradeToDFPanelLayout = new org.jdesktop.layout.GroupLayout(tradeToDFPanel);
        tradeToDFPanel.setLayout(tradeToDFPanelLayout);
        tradeToDFPanelLayout.setHorizontalGroup(
            tradeToDFPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tradeToDFPanelLayout.createSequentialGroup()
                .add(tradeToDFPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, tradeToDFPanelLayout.createSequentialGroup()
                        .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(atToAreaScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 133, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(tradeToDFPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(atToAreaRemoveButton)
                            .add(tradeToDFPanelLayout.createSequentialGroup()
                                .add(tradeToDFPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                    .add(atToDFactorEditTextField, 0, 0, Short.MAX_VALUE)
                                    .add(atToAreaEditButton))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(atToAreaUpdateButton))))
                    .add(tradeToDFPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(atToAreaLabel)
                        .add(18, 18, 18)
                        .add(atToAreaComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 63, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        tradeToDFPanelLayout.setVerticalGroup(
            tradeToDFPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, tradeToDFPanelLayout.createSequentialGroup()
                .add(tradeToDFPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(atToAreaLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                    .add(atToAreaComboBox))
                .add(13, 13, 13)
                .add(tradeToDFPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(atToAreaScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 96, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(tradeToDFPanelLayout.createSequentialGroup()
                        .add(tradeToDFPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(tradeToDFPanelLayout.createSequentialGroup()
                                .add(atToAreaEditButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(atToDFactorEditTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(atToAreaUpdateButton))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(atToAreaRemoveButton)))
                .addContainerGap())
        );

        tradeMeasBranchPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Meas Branch", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 12))); // NOI18N

        atBranchListComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        atBranchListComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        atAddBranchButton.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        atAddBranchButton.setText("Add");
        atAddBranchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                atAddBranchButtonActionPerformed(evt);
            }
        });

        atMeasBranchList.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        atBranchListScrollPane.setViewportView(atMeasBranchList);

        atRemoveBranchButton.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        atRemoveBranchButton.setText("Remove");
        atRemoveBranchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                atRemoveBranchButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout tradeMeasBranchPanelLayout = new org.jdesktop.layout.GroupLayout(tradeMeasBranchPanel);
        tradeMeasBranchPanel.setLayout(tradeMeasBranchPanelLayout);
        tradeMeasBranchPanelLayout.setHorizontalGroup(
            tradeMeasBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tradeMeasBranchPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(tradeMeasBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(tradeMeasBranchPanelLayout.createSequentialGroup()
                        .add(atAddBranchButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(atRemoveBranchButton))
                    .add(atBranchListComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 192, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(atBranchListScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 172, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(33, Short.MAX_VALUE))
        );
        tradeMeasBranchPanelLayout.setVerticalGroup(
            tradeMeasBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tradeMeasBranchPanelLayout.createSequentialGroup()
                .add(atBranchListComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(tradeMeasBranchPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(atAddBranchButton)
                    .add(atRemoveBranchButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(atBranchListScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 105, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        tradeMeasInterfacePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Meas Interface", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 12))); // NOI18N

        atAddBranchButton1.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        atAddBranchButton1.setText("Add");
        atAddBranchButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                atAddBranchButton1ActionPerformed(evt);
            }
        });

        atMeasBranchList1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        atBranchListScrollPane1.setViewportView(atMeasBranchList1);

        atRemoveBranchButton1.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        atRemoveBranchButton1.setText("Remove");
        atRemoveBranchButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                atRemoveBranchButton1ActionPerformed(evt);
            }
        });

        atInterfaceListComboBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        atInterfaceListComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        org.jdesktop.layout.GroupLayout tradeMeasInterfacePanelLayout = new org.jdesktop.layout.GroupLayout(tradeMeasInterfacePanel);
        tradeMeasInterfacePanel.setLayout(tradeMeasInterfacePanelLayout);
        tradeMeasInterfacePanelLayout.setHorizontalGroup(
            tradeMeasInterfacePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tradeMeasInterfacePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(tradeMeasInterfacePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(atInterfaceListComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 191, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(tradeMeasInterfacePanelLayout.createSequentialGroup()
                        .add(atAddBranchButton1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(atRemoveBranchButton1)
                        .add(12, 12, 12))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, tradeMeasInterfacePanelLayout.createSequentialGroup()
                        .add(atBranchListScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 171, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        tradeMeasInterfacePanelLayout.setVerticalGroup(
            tradeMeasInterfacePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tradeMeasInterfacePanelLayout.createSequentialGroup()
                .add(atInterfaceListComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(6, 6, 6)
                .add(tradeMeasInterfacePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(atAddBranchButton1)
                    .add(atRemoveBranchButton1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(atBranchListScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 105, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout tradeInfoEditPanelLayout = new org.jdesktop.layout.GroupLayout(tradeInfoEditPanel);
        tradeInfoEditPanel.setLayout(tradeInfoEditPanelLayout);
        tradeInfoEditPanelLayout.setHorizontalGroup(
            tradeInfoEditPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tradeInfoEditPanelLayout.createSequentialGroup()
                .add(tradeInfoEditPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(tradeInfoEditPanelLayout.createSequentialGroup()
                        .add(30, 30, 30)
                        .add(tradeTransAmtLabel)
                        .add(18, 18, 18)
                        .add(tradeTransAmtTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(tradeTransAmtUnitComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(31, 31, 31)
                        .add(tradeDeratingFactorLabel)
                        .add(29, 29, 29)
                        .add(tradeDeratingFactorTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(tradeInfoEditPanelLayout.createSequentialGroup()
                        .add(tradeInfoEditPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(tradeMeasBranchPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(tradeFromDFPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(tradeInfoEditPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(tradeMeasInterfacePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(tradeToDFPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tradeInfoEditPanelLayout.setVerticalGroup(
            tradeInfoEditPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, tradeInfoEditPanelLayout.createSequentialGroup()
                .add(tradeInfoEditPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(tradeTransAmtLabel)
                    .add(tradeInfoEditPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(tradeDeratingFactorLabel)
                        .add(tradeDeratingFactorTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(tradeTransAmtTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(tradeTransAmtUnitComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(tradeInfoEditPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(tradeToDFPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(tradeFromDFPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(tradeInfoEditPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(tradeMeasBranchPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(tradeMeasInterfacePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        tradeCalculateButton.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        tradeCalculateButton.setText("Calculate");
        tradeCalculateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tradeCalculateButtonActionPerformed(evt);
            }
        });

        tradeAclfCalculateButton.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        tradeAclfCalculateButton.setText("AC Loadflow");
        tradeAclfCalculateButton.setEnabled(false);
        tradeAclfCalculateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tradeAclfCalculateButtonActionPerformed(evt);
            }
        });

        tradeSeAssessButton.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        tradeSeAssessButton.setText("Sec Assess");
        tradeSeAssessButton.setEnabled(false);
        tradeSeAssessButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tradeSeAssessButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(46, 46, 46)
                        .add(tradeLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(tradeComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(tradeAddButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(tradeDeleteButton)
                        .add(29, 29, 29)
                        .add(zonalRadioButton)
                        .add(12, 12, 12)
                        .add(nodalRadioButton))
                    .add(layout.createSequentialGroup()
                        .add(21, 21, 21)
                        .add(tradeInfoEditPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 513, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createSequentialGroup()
                        .add(130, 130, 130)
                        .add(tradeCalculateButton)
                        .add(16, 16, 16)
                        .add(tradeAclfCalculateButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(tradeSeAssessButton)))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(tradeDeleteButton)
                        .add(nodalRadioButton)
                        .add(zonalRadioButton))
                    .add(tradeLabel)
                    .add(tradeComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(tradeAddButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(tradeInfoEditPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(tradeAclfCalculateButton)
                        .add(tradeSeAssessButton))
                    .add(tradeCalculateButton))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
 
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

private void atRemoveBranchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_atRemoveBranchButtonActionPerformed
	if (!atMeasBranchList.isSelectionEmpty()) {
		areaTransfer.getBranch().remove(atMeasBranchList.getSelectedIndex());
		atMeasBranchList.setModel(new javax.swing.DefaultComboBoxModel(
				IpssXmlHelper.getBranchIdAry(areaTransfer.getBranch())));
	}
}//GEN-LAST:event_atRemoveBranchButtonActionPerformed

private void tradeFromAreaRemoveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tradeFromAreaRemoveButtonActionPerformed
	if (!tradeFromAreaBusList.isSelectionEmpty()) {
		areaTransfer.getInjectBusList().getInjectBus().remove(tradeFromAreaBusList.getSelectedIndex());
		tradeFromAreaBusList.setModel(new javax.swing.DefaultComboBoxModel(
				IpssXmlHelper.getSenAnalysisBusItemList(areaTransfer.getInjectBusList().getInjectBus())));
	}
}//GEN-LAST:event_tradeFromAreaRemoveButtonActionPerformed

private void atToAreaRemoveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_atToAreaRemoveButtonActionPerformed
	if (!atToAreaBusList.isSelectionEmpty()) {
		areaTransfer.getWithdrawBusList().getWithdrawBus().remove(atToAreaBusList.getSelectedIndex());
		atToAreaBusList.setModel(new javax.swing.DefaultComboBoxModel(
				IpssXmlHelper.getSenAnalysisBusItemList(areaTransfer.getWithdrawBusList().getWithdrawBus())));
	}
}//GEN-LAST:event_atToAreaRemoveButtonActionPerformed

private void tradeFromAreaComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tradeFromAreaComboBoxActionPerformed
	String no = (String)this.tradeFromAreaComboBox.getSelectedItem();
	this.areaTransfer.getFromArea().setAreaNo(new Integer(no).intValue());
	tradeFromAreaBusList.setModel(new javax.swing.DefaultComboBoxModel(
			RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.GenInAreaDFactor, 
					this.areaTransfer.getFromArea().getAreaNo()).toArray()));
}//GEN-LAST:event_tradeFromAreaComboBoxActionPerformed

private void atToAreaComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_atToAreaComboBoxActionPerformed
	String no = (String)this.atToAreaComboBox.getSelectedItem();
	this.areaTransfer.getToArea().setAreaNo(new Integer(no).intValue());
	atToAreaBusList.setModel(new javax.swing.DefaultComboBoxModel(
			RunUIUtilFunc.getIdArray(_simuCtx.getAclfNet(), RunUIUtilFunc.NetIdType.GenInAreaDFactor, 
					this.areaTransfer.getToArea().getAreaNo()).toArray()));
}//GEN-LAST:event_atToAreaComboBoxActionPerformed

private void tradeFromAreaEditButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tradeFromAreaEditButtonActionPerformed
	if (!tradeFromAreaBusList.isSelectionEmpty()) {
		tradeFromDFactorEditTextField.setEnabled(true);
		tradeFromAreaUpdateButton.setEnabled(true);
		String s = (String)tradeFromAreaBusList.getSelectedValue();
		tradeFromDFactorEditTextField.setText(new Double(RunUIUtilFunc.getPercent_IdPercent(s)).toString());
	}
}//GEN-LAST:event_tradeFromAreaEditButtonActionPerformed

private void tradeFromAreaUpdateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tradeFromAreaUpdateButtonActionPerformed
	if (!tradeFromAreaBusList.isSelectionEmpty()) {
		// the Bus list might have been changed due to changing the area number
		saveFromAreaBusList2AreaTransfer();
		double percent = new Double(tradeFromDFactorEditTextField.getText()).doubleValue();
		String s = (String)tradeFromAreaBusList.getSelectedValue();
		String id = RunUIUtilFunc.getId_IdPercent(s);
		SenAnalysisBusRecXmlType bus = (SenAnalysisBusRecXmlType)IpssXmlHelper.getBusRecord(id, areaTransfer.getInjectBusList().getInjectBus());
		bus.setPercent(percent);
		tradeFromAreaBusList.setModel(new javax.swing.DefaultComboBoxModel(
    			IpssXmlHelper.getSenAnalysisBusItemList(areaTransfer.getInjectBusList().getInjectBus())));
		tradeFromDFactorEditTextField.setEnabled(false);
		tradeFromAreaUpdateButton.setEnabled(false);
	}
}//GEN-LAST:event_tradeFromAreaUpdateButtonActionPerformed

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

private void tradeCalculateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tradeCalculateButtonActionPerformed
	this.parent.setAlwaysOnTop(false);
	DclfAlgorithm algo = DclfObjectFactory.createDclfAlgorithm(_simuCtx.getAclfNet());
	_simuCtx.setDclfAlgorithm(algo);
	if (!algo.checkCondition())
		return;
	if (!saveEditor2AreaTransfer())
		return;
	XmlScriptDclfRun.calAreaTransferFactor(areaTransfer, algo, _simuCtx.getMsgHub());
	IOutputTextDialog dialog = UISpringAppContext.getOutputTextDialog("Area Transfer Analysis Results");
	String str = DclfOutFunc.areaTransferAnalysisResults(areaTransfer, _simuCtx.getDclfAlgorithm());
	dialog.display(str);
}//GEN-LAST:event_tradeCalculateButtonActionPerformed

private void tradeAclfCalculateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tradeAclfCalculateButtonActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_tradeAclfCalculateButtonActionPerformed

private void tradeSeAssessButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tradeSeAssessButtonActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_tradeSeAssessButtonActionPerformed

private void tradeAddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tradeAddButtonActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_tradeAddButtonActionPerformed

private void tradeDeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tradeDeleteButtonActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_tradeDeleteButtonActionPerformed

private void tradeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tradeComboBoxActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_tradeComboBoxActionPerformed

private void atAddBranchButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_atAddBranchButton1ActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_atAddBranchButton1ActionPerformed

private void atRemoveBranchButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_atRemoveBranchButton1ActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_atRemoveBranchButton1ActionPerformed

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton atAddBranchButton;
    private javax.swing.JButton atAddBranchButton1;
    private javax.swing.JComboBox atBranchListComboBox;
    private javax.swing.JScrollPane atBranchListScrollPane;
    private javax.swing.JScrollPane atBranchListScrollPane1;
    private javax.swing.JComboBox atInterfaceListComboBox;
    private javax.swing.JList atMeasBranchList;
    private javax.swing.JList atMeasBranchList1;
    private javax.swing.JButton atRemoveBranchButton;
    private javax.swing.JButton atRemoveBranchButton1;
    private javax.swing.JList atToAreaBusList;
    private javax.swing.JComboBox atToAreaComboBox;
    private javax.swing.JButton atToAreaEditButton;
    private javax.swing.JLabel atToAreaLabel;
    private javax.swing.JButton atToAreaRemoveButton;
    private javax.swing.JScrollPane atToAreaScrollPane;
    private javax.swing.JButton atToAreaUpdateButton;
    private javax.swing.JTextField atToDFactorEditTextField;
    private javax.swing.JRadioButton nodalRadioButton;
    private javax.swing.JButton tradeAclfCalculateButton;
    private javax.swing.JButton tradeAddButton;
    private javax.swing.JButton tradeCalculateButton;
    private javax.swing.JComboBox tradeComboBox;
    private javax.swing.JButton tradeDeleteButton;
    private javax.swing.JLabel tradeDeratingFactorLabel;
    private javax.swing.JTextField tradeDeratingFactorTextField;
    private javax.swing.JList tradeFromAreaBusList;
    private javax.swing.JComboBox tradeFromAreaComboBox;
    private javax.swing.JButton tradeFromAreaEditButton;
    private javax.swing.JLabel tradeFromAreaLabel;
    private javax.swing.JButton tradeFromAreaRemoveButton;
    private javax.swing.JScrollPane tradeFromAreaScrollPane;
    private javax.swing.JButton tradeFromAreaUpdateButton;
    private javax.swing.JPanel tradeFromDFPanel;
    private javax.swing.JTextField tradeFromDFactorEditTextField;
    private javax.swing.JPanel tradeInfoEditPanel;
    private javax.swing.JLabel tradeLabel;
    private javax.swing.JPanel tradeMeasBranchPanel;
    private javax.swing.JPanel tradeMeasInterfacePanel;
    private javax.swing.JButton tradeSeAssessButton;
    private javax.swing.JPanel tradeToDFPanel;
    private javax.swing.JLabel tradeTransAmtLabel;
    private javax.swing.JTextField tradeTransAmtTextField;
    private javax.swing.JComboBox tradeTransAmtUnitComboBox;
    private javax.swing.ButtonGroup tradingTypeButtonGroup;
    private javax.swing.JRadioButton zonalRadioButton;
    // End of variables declaration//GEN-END:variables
    
	class DataVerifier extends javax.swing.InputVerifier {
		public boolean verify(javax.swing.JComponent input) {
			if (input == null)
				return false;
			return true;
		}
	}
}
