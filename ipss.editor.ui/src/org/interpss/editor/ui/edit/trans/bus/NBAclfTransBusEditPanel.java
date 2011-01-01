 /*
  * @(#)NBAclfTransBusEditPanel.java   
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

package org.interpss.editor.ui.edit.trans.bus;
  
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;

import org.interpss.editor.data.aclf.AclfAdjBusData;
import org.interpss.editor.data.aclf.AclfBusData;
import org.interpss.editor.form.GBusForm;
import org.interpss.editor.form.GFormContainer;
import org.interpss.editor.form.GNetForm;
import org.interpss.editor.jgraph.ui.edit.IFormDataPanel;
import org.interpss.editor.ui.edit.common.NBCustomScriptEditPanel;
import org.interpss.editor.ui.util.EditUIEvent;
import org.interpss.editor.ui.util.EditUIEventContainer;
import org.interpss.numeric.util.Number2String;
import org.interpss.ui.SwingInputVerifyUtil;

import com.interpss.common.util.IpssLogger;
 
public class NBAclfTransBusEditPanel extends javax.swing.JPanel implements IFormDataPanel {
	private static final long serialVersionUID = 1;
	private JDialog parent = null;
	
	public static final String BUS_TYPE_PV = "PV";
	public static final String BUS_TYPE_PQ = "PQ";
	public static final String BUS_TYPE_ReQVolt = "ReQVolt";
	public static final String BUS_TYPE_ReQMvar = "ReQMvar";
	
    private GFormContainer _netContainer = null;
    private GBusForm _form = null;
    private AclfAdjBusData _data = null;

    private NBCustomScriptEditPanel customScriptEditPanel = new NBCustomScriptEditPanel();
    
    private EditUIEventContainer editUIEventContainer = new EditUIEventContainer();
    	// listeners : NBDStabTransBusEditPanel
    
    public EditUIEventContainer getEditUIEventContainer() {
    	return editUIEventContainer;
    }
    
	public void initPanel(JDialog aParent) {
		parent = aParent;

		customScriptEditPanel.initPanel(aParent);
		this.scriptPanel.add(customScriptEditPanel);
		
		DataVerifier verifier = new DataVerifier();
	    constI_PTextField.setInputVerifier(verifier);
	    constI_QTextField.setInputVerifier(verifier);
	    constP_PTextField.setInputVerifier(verifier);
	    constP_QTextField.setInputVerifier(verifier);
	    constZ_PTextField.setInputVerifier(verifier);
	    constZ_QTextField.setInputVerifier(verifier);
	    maxTextField.setInputVerifier(verifier);
	    minTextField.setInputVerifier(verifier);
	    pGenTextField.setInputVerifier(verifier);
	    pLoadTextField.setInputVerifier(verifier);
	    qGenTextField.setInputVerifier(verifier);
	    qLoadTextField.setInputVerifier(verifier);
	    shuntBTextField.setInputVerifier(verifier);
	    shuntGTextField.setInputVerifier(verifier);
	}
    
	public void init(Object netContainer, Object form) {
		IpssLogger.getLogger().info("NBAclfTransBusEditPanel init() called");
		_netContainer = (GFormContainer)netContainer;
		_form = (GBusForm)form;
		_data = _form.getAcscBusData();
		
		customScriptEditPanel.init(_data, NBCustomScriptEditPanel.Type.AclfBus);
		
		// prepare for network with adjustment
		if (((GNetForm)((GFormContainer)netContainer).getGNetForm()).getAcscNetData().isHasAdjustment()) {
		    remoteQRadioButton.setEnabled(true);
		    if (adjustCheckBox.isEnabled() && adjustCheckBox.isSelected()) {
			    remoteBusComboBox.setEnabled(true);
			    remoteBusLabel.setEnabled(true);
		    }
		    funcLoadRadioButton.setEnabled(true);
		}
		else {
		    remoteQRadioButton.setEnabled(false);
		    remoteBusComboBox.setEnabled(false);
		    remoteBusLabel.setEnabled(false);
		    adjustCheckBox.setEnabled(false);
		    funcLoadRadioButton.setEnabled(false);
		}
		
		// disable the scripting panel
		busTabbedPane.setEnabledAt(1, false);
	}
	
	public void disableScripting() {
		scriptLoadRadioButton.setEnabled(false);
		scriptGenRadioButton.setEnabled(false);
	}
	
    public boolean setForm2Editor() {
		IpssLogger.getLogger().info("NBAclfTransBusEditPanel setForm2Editor() called");

		// set gen data panel
		if (_data.getGenCode().equals(AclfBusData.GenCode_PQ)) {
	    	pqRadioButton.setSelected(true);
	        pqRadioButtonSelected(null);
	    	pGenTextField.setText(Number2String.toStr(_data.getGenP(), "#0.0####"));
		    qGenTextField.setText(Number2String.toStr(_data.getGenQ(), "#0.0####"));
	    }
	    else if (_data.getGenCode().equals(AclfBusData.GenCode_PV)) {
	    	pvRadioButton.setSelected(true);
	        pvRadioButtonSelected(null);
	    	pGenTextField.setText(Number2String.toStr(_data.getGenP(), "#0.0####"));
		    qGenTextField.setText(Number2String.toStr(_data.getVoltageMag(), "#0.0####"));
	    }
	    else if (_data.getGenCode().equals(AclfBusData.GenCode_Swing)) {
	    	swingRadioButton.setSelected(true);
	        swingRadioButtonSelected(null);
	    	pGenTextField.setText(Number2String.toStr(_data.getVoltageMag(), "#0.0####"));
		    qGenTextField.setText(Number2String.toStr(_data.getVoltageAng(), "#0.0####"));
	    }
	    else if (_data.getGenCode().equals(AclfBusData.GenCode_Capacitor)) {
	    	capRadioButton.setSelected(true);
	        capRadioButtonSelected(null);
	    	pGenTextField.setText(Number2String.toStr(_data.getCapQ(), "#0.0####"));
		    qGenTextField.setText(Number2String.toStr(0.0, "#0.0####"));
	    }
	    else {
		    if (_data.getGenCode().equals(AclfBusData.GenCode_GenScripting)) {
		    	scriptGenRadioButton.setSelected(true);
		    	setScriptPanel();
		    }
		    else {
		    	nonGenRadioButton.setSelected(true);
		        nonGenRadioButtonSelected(null);
		    	pGenTextField.setText(Number2String.toStr(0.0, "#0.0####"));
			    qGenTextField.setText(Number2String.toStr(0.0, "#0.0####"));
		    }
	    }
	    
		// set load data panel
	    if ( _data.getLoadCode().equals(AclfBusData.LoadCode_NonLoad) || 
	    	 _data.getLoadCode().equals(AclfBusData.LoadCode_LoadScripting)	) {
		    if ( _data.getLoadCode().equals(AclfBusData.LoadCode_LoadScripting)) { 
		    	scriptLoadRadioButton.setSelected(true);
		    	setScriptPanel();
		    }
		    else {
		    	nonLoadRadioButton.setSelected(true);
		        nonLoadRadioButtonSelected(null);
			    pLoadTextField.setText(Number2String.toStr(0.0, "#0.0####"));
		    	qLoadTextField.setText(Number2String.toStr(0.0, "#0.0####"));
		    }
	    } 
	    else {
	    	if (_data.getLoadCode().equals(AclfBusData.LoadCode_ConstP)) {
	    		constPRadioButton.setSelected(true);
		        constPRadioButtonSelected(null);
	    	}	
	    	else if (_data.getLoadCode().equals(AclfBusData.LoadCode_ConstI)) {
	    		constIRadioButton.setSelected(true);
		        constIRadioButtonSelected(null);
	    	}	
	    	else if (_data.getLoadCode().equals(AclfBusData.LoadCode_ConstZ)) {
	    		constZRadioButton.setSelected(true);
		        constZRadioButtonSelected(null);
	    	}	
	    	pLoadTextField.setText(Number2String.toStr(_data.getLoadP(), "#0.0####"));
		    qLoadTextField.setText(Number2String.toStr(_data.getLoadQ(), "#0.0####"));
	    }

	    // set shunt G+jB fields
	    shuntBTextField.setText(Number2String.toStr(_data.getShuntB(), "#0.0####"));
	    shuntGTextField.setText(Number2String.toStr(_data.getShuntG(), "#0.0####"));

	    // set Bus adjustment data
	    if (((GNetForm)_netContainer.getGNetForm()).getAcscNetData().isHasAdjustment()) {
	    	maxTextField.setText("0.0");
		    minTextField.setText("0.0");
	    	adjustCheckBox.setSelected(false);
		    // set RemoteQ bus ajustment
	    	if (_data.isHasRemoteVControl()) {
		    	adjustCheckBox.setSelected(true);
		    	remoteQRadioButton.setSelected(true);
		    	remoteQRadioButtonActionPerformed(null);
		    	pGenTextField.setText(Number2String.toStr(_data.getGenP(), "#0.0####"));
			    qGenTextField.setText(Number2String.toStr(_data.getVoltageMag(), "#0.0####"));
		    	maxTextField.setText(Number2String.toStr(_data.getMaxGenQ(), "#0.0####"));
			    minTextField.setText(Number2String.toStr(_data.getMinGenQ(), "#0.0####"));
			    /*
			    setAdjLabelText(true, _data.getReQControlType()==AclfAdjBusData.ReQControlType_Voltage?
    	        		BUS_TYPE_ReQVolt : BUS_TYPE_ReQMvar);
			    voltageRadioButton.setSelected(_data.getReQControlType()==AclfAdjBusData.ReQControlType_Voltage);
			    */
			    if (voltageRadioButton.isSelected()) {
			    	if (_data.getRemoteControlBusId() != null && !_data.getRemoteControlBusId().equals("")) {
			    		remoteBusComboBox.setSelectedItem(_data.getRemoteControlBusId());
			    	}
			    }
			    else {
			    	from2ToRadioButton.setSelected(_data.isFlowFrom2To());
			    	to2FromRadioButton.setSelected(!_data.isFlowFrom2To());
			    	mvarOnFromSideRadioButton.setSelected(_data.isMvarControlOnFromSide());
			    	mvarOnToSideRadioButton.setSelected(!_data.isMvarControlOnFromSide());
			    	if (_data.getRemoteControlBranchId() != null && !_data.getRemoteControlBranchId().equals("")) {
			    		remoteBusComboBox.setSelectedItem(_data.getRemoteControlBranchId());
			    	}
			    }
		    }
		    // set PQ bus with limit adjustment
		    else if (_data.getGenCode().equals(AclfBusData.GenCode_PQ) && _data.isHasLimitControl()) {
		    	adjustCheckBox.setSelected(true);
		    	maxTextField.setText(Number2String.toStr(_data.getMaxVoltMag(), "#0.0####"));
			    minTextField.setText(Number2String.toStr(_data.getMinVoltMag(), "#0.0####"));
			    setAdjLabelText(true, BUS_TYPE_PQ);
		    }
		    // set PV bus with limit adjustment
		    else if (_data.getGenCode().equals(AclfBusData.GenCode_PV) && _data.isHasLimitControl()) {
		    	adjustCheckBox.setSelected(true);
			    maxTextField.setText(Number2String.toStr(_data.getMaxGenQ(), "#0.0####"));
			    minTextField.setText(Number2String.toStr(_data.getMinGenQ(), "#0.0####"));
			    setAdjLabelText(true, BUS_TYPE_PV);
		    }
	    	
		    // set func load data
	    	if (_data.getLoadCode().equals(AclfBusData.LoadCode_FuncLoad)) {
		    	funcLoadRadioButton.setSelected(true);
			    funcLoadRadioButtonSelected(null);
		    	pLoadTextField.setText(Number2String.toStr(_data.getLoadP(), "#0.0####"));
			    qLoadTextField.setText(Number2String.toStr(_data.getLoadQ(), "#0.0####"));
			    constP_PTextField.setText(Number2String.toStr(_data.getLoadP_PPct(), "#0.#"));
			    constP_QTextField.setText(Number2String.toStr(_data.getLoadQ_PPct(), "#0.#"));
		    	constI_PTextField.setText(Number2String.toStr(_data.getLoadP_IPct(), "#0.#"));
			    constI_QTextField.setText(Number2String.toStr(_data.getLoadQ_IPct(), "#0.#"));
			    constZ_PTextField.setText(Number2String.toStr(100.0-_data.getLoadP_PPct()-_data.getLoadP_IPct(), "#0.#"));
			    constZ_QTextField.setText(Number2String.toStr(100.0-_data.getLoadQ_PPct()-_data.getLoadQ_IPct(), "#0.#"));
	    	}	
	    }

	    // set enable/disable the script panel
	    setScriptPanel();

    	return true;
	}
    
    public boolean saveEditor2Form(Vector<String> errMsg) throws Exception {
		IpssLogger.getLogger().info("NBAclfTransBusEditPanel saveEditor2Form() called");
		boolean ok = true;

		// save Gen data
	    if (pqRadioButton.isSelected()) {
	    	_data.setGenCode(AclfBusData.GenCode_PQ);
	    	_data.setGenP(SwingInputVerifyUtil.getDouble(pGenTextField));
	    	_data.setGenQ(SwingInputVerifyUtil.getDouble(qGenTextField));
	    }
	    else if (pvRadioButton.isSelected()) {
	    	_data.setGenCode(AclfBusData.GenCode_PV);
	    	_data.setGenP(SwingInputVerifyUtil.getDouble(pGenTextField));
	    	_data.setVoltageMag(SwingInputVerifyUtil.getDouble(qGenTextField));
	    }
	    else if (swingRadioButton.isSelected()) {
	    	_data.setGenCode(AclfBusData.GenCode_Swing);
	    	_data.setVoltageMag(SwingInputVerifyUtil.getDouble(pGenTextField));
	    	_data.setVoltageAng(SwingInputVerifyUtil.getDouble(qGenTextField));
	    }
	    else if (capRadioButton.isSelected()) {
	    	_data.setGenCode(AclfBusData.GenCode_Capacitor);
	    	_data.setCapQ(SwingInputVerifyUtil.getDouble(pGenTextField));
	    }
	    else if (scriptGenRadioButton.isSelected()) {
	    	_data.setGenCode(AclfBusData.GenCode_GenScripting);
	    }
	    else {
	    	_data.setGenCode(AclfBusData.GenCode_NonGen);
	    	_data.setGenP(0.0);
	    	_data.setGenP(0.0);
	    }
	    
	    // save Load data
	    if (nonLoadRadioButton.isSelected()) {
	    	_data.setLoadCode(AclfBusData.LoadCode_NonLoad);
	    	_data.setLoadP(0.0);
	    	_data.setLoadQ(0.0);
	    } 
	    else if (scriptLoadRadioButton.isSelected()) {
	    	_data.setLoadCode(AclfBusData.LoadCode_LoadScripting);
	    } 
	    else {
	    	if (constPRadioButton.isSelected())
	    		_data.setLoadCode(AclfBusData.LoadCode_ConstP);
	    	else if (constIRadioButton.isSelected())
	    		_data.setLoadCode(AclfBusData.LoadCode_ConstI);
	    	else if (constZRadioButton.isSelected())
	    		_data.setLoadCode(AclfBusData.LoadCode_ConstZ);
	    	_data.setLoadP(SwingInputVerifyUtil.getDouble(pLoadTextField));
	    	_data.setLoadQ(SwingInputVerifyUtil.getDouble(qLoadTextField));
	    }

	    // Save adjustment data
	    if (!scriptLoadRadioButton.isSelected()) {
	    	_data.setShuntB(SwingInputVerifyUtil.getDouble(shuntBTextField));
	    	_data.setShuntG(SwingInputVerifyUtil.getDouble(shuntGTextField));
	    	_data.setShuntYUnit("PU");

	    	if (((GNetForm)_netContainer.getGNetForm()).getAcscNetData().isHasAdjustment()) {
		    	if (remoteQRadioButton.isSelected() ) {
		    		_data.setHasRemoteVControl(true);
			    	_data.setGenCode(AclfBusData.GenCode_PQ);
			    	_data.setGenP(SwingInputVerifyUtil.getDouble(pGenTextField));
			    	_data.setGenQ(0.0);
			    	// VoltgeMsg is used to hold PV-VSpec, ReQVolt-VSpec and ReQMvarFlow-MvarSpec
			    	_data.setVoltageMag(SwingInputVerifyUtil.getDouble(qGenTextField));
		    		_data.setMaxGenQ(SwingInputVerifyUtil.getDouble(maxTextField));
		    		_data.setMinGenQ(SwingInputVerifyUtil.getDouble(minTextField));
		    		if (voltageRadioButton.isSelected()) {
		    			_data.setReQControlType(AclfAdjBusData.ReQControlType_Voltage);
		    			_data.setRemoteControlBusId((String)remoteBusComboBox.getSelectedItem());
		    		}
		    		else {
		    			_data.setReQControlType(AclfAdjBusData.ReQControlType_MvarFlow);
		    			_data.setFlowFrom2To(from2ToRadioButton.isSelected());
		    			_data.setMvarControlOnFromSide(mvarOnFromSideRadioButton.isSelected());
		    			_data.setRemoteControlBusId((String)remoteBusComboBox.getSelectedItem());
		    		}
			    }	
		    	else if (adjustCheckBox.isSelected()) {
		    		_data.setHasRemoteVControl(false);
			    	if (pqRadioButton.isSelected() ) {
			    		_data.setHasLimitControl(true);
			    		_data.setMaxVoltMag(SwingInputVerifyUtil.getDouble(maxTextField));
			    		_data.setMinVoltMag(SwingInputVerifyUtil.getDouble(minTextField));
				    }	
				    else if (pvRadioButton.isSelected()) {
			    		_data.setHasLimitControl(true);
			    		_data.setMaxGenQ(SwingInputVerifyUtil.getDouble(maxTextField));
			    		_data.setMinGenQ(SwingInputVerifyUtil.getDouble(minTextField));
				    }	
			    }
		    	else {
		    		_data.setHasLimitControl(false);
		    		_data.setHasRemoteVControl(false);
		    	}
		    	
		    	if (funcLoadRadioButton.isSelected()) {
		    		_data.setLoadCode(AclfBusData.LoadCode_FuncLoad);
					_data.setLoadP_PPct(SwingInputVerifyUtil.getDouble(constP_PTextField));
		    		_data.setLoadQ_PPct(SwingInputVerifyUtil.getDouble(constP_QTextField));
		    		_data.setLoadP_IPct(SwingInputVerifyUtil.getDouble(constI_PTextField));
		    		_data.setLoadQ_IPct(SwingInputVerifyUtil.getDouble(constI_QTextField));
		    	}	
		    }
	    }
	    
    	if (_data.getLoadCode().equals(AclfBusData.LoadCode_LoadScripting) ||
    		_data.getGenCode().equals(AclfBusData.GenCode_GenScripting)	) {
    		this.customScriptEditPanel.saveEditor2Form(errMsg);
    	}

	    return ok;
    }

    // set adjustment field label according to adj type 
    private void setAdjLabelText(boolean adjEnabled, String type ) {  // "PV", "PQ", "ReQVolt", "ReQMvar"
    	IpssLogger.getLogger().info("Set Adjustment type: " + type);
    	adjustCheckBox.setEnabled(adjEnabled);
    	boolean enableSelected = adjustCheckBox.isEnabled() && adjustCheckBox.isSelected();
    	
    	// common for all types
    	if (type.equals(BUS_TYPE_PQ)) {
            maxLabel.setText("Vmax(pu)");
            minLabel.setText("Vmin(pu)");
    	}
	    else  {
            maxLabel.setText("Qmax(pu)");
            minLabel.setText("Qmin(pu)");
	    }
    	maxLabel.setEnabled(enableSelected);
        maxTextField.setEnabled(enableSelected);
        minLabel.setEnabled(enableSelected);
        minTextField.setEnabled(enableSelected);

        // only apply to ReQ*
    	voltageRadioButton.setEnabled(false);
    	mvaFlowRadioButton.setEnabled(false);
	    remoteBusComboBox.setEnabled(false);
	    remoteBusLabel.setEnabled(false);
	    from2ToRadioButton.setEnabled(false);
	    to2FromRadioButton.setEnabled(false);
	    mvarOnFromSideRadioButton.setEnabled(false);
	    mvarOnToSideRadioButton.setEnabled(false);
    	if (type.equals(BUS_TYPE_PQ)) {
    		adjustCheckBox.setText("PQ Bus Limit Adjustment");
    	}
	    else if (type.equals(BUS_TYPE_PV)) {
    		adjustCheckBox.setText("PV Bus Limit Adjustment");
	    }
	    else if (type.equals(BUS_TYPE_ReQVolt)) {
	    	adjustCheckBox.setText("Remote Voltage Control");
    	    remoteBusComboBox.setEnabled(enableSelected);
    	    remoteBusLabel.setEnabled(enableSelected);
    	    remoteBusLabel.setText("Remote Bus     ");
		    remoteBusComboBox.setModel(new DefaultComboBoxModel(_netContainer.getTargetVCBusNameIdArray()));
            voltageRadioButton.setEnabled(enableSelected);
            mvaFlowRadioButton.setEnabled(enableSelected);
            parent.pack();
	    }
	    else if (type.equals(BUS_TYPE_ReQMvar)) {
    		adjustCheckBox.setText("Remote Mvar Flow Control");
    	    remoteBusComboBox.setEnabled(enableSelected);
    	    remoteBusLabel.setEnabled(enableSelected);
    	    remoteBusLabel.setText("   Remote Branch  ");
		    remoteBusComboBox.setModel(new DefaultComboBoxModel(_netContainer.getBranchNameIdArray()));
            voltageRadioButton.setEnabled(enableSelected);
            mvaFlowRadioButton.setEnabled(enableSelected);
    	    from2ToRadioButton.setEnabled(enableSelected);
    	    to2FromRadioButton.setEnabled(enableSelected);
    	    mvarOnFromSideRadioButton.setEnabled(enableSelected);
    	    mvarOnToSideRadioButton.setEnabled(enableSelected);
            parent.pack();
	    }
    }

    // set load field label
    private void setLoadLabelText(boolean funcLoad, boolean nonLoad) {
        pLoadLabel.setEnabled(!nonLoad);
        pLoadTextField.setEnabled(!nonLoad);
        qLoadLabel.setEnabled(!nonLoad);
        qLoadTextField.setEnabled(!nonLoad);
        if (nonLoad) {
            pLoadTextField.setText("0.0");
            qLoadTextField.setText("0.0");
        }
        
        constP_PLabel.setEnabled(funcLoad);
        constP_PTextField.setEnabled(funcLoad);
        constI_PLabel.setEnabled(funcLoad);
        constI_PTextField.setEnabled(funcLoad);
        constZ_PLabel.setEnabled(funcLoad);
        constZ_PTextField.setEnabled(funcLoad);
        constP_QLabel.setEnabled(funcLoad);
        constP_QTextField.setEnabled(funcLoad);
        constI_QLabel.setEnabled(funcLoad);
        constI_QTextField.setEnabled(funcLoad);
        constZ_QLabel.setEnabled(funcLoad);
        constZ_QTextField.setEnabled(funcLoad);
        if (!funcLoad) {
            constP_PTextField.setText("0.0");
            constI_PTextField.setText("0.0");
            constZ_PTextField.setText("0.0");
            constP_QTextField.setText("0.0");
            constI_QTextField.setText("0.0");
            constZ_QTextField.setText("0.0");
        }
    }

    // set script panel status
    private void setScriptPanel() {
		if (!scriptLoadRadioButton.isSelected() && !scriptGenRadioButton.isSelected())
			busTabbedPane.setEnabledAt(1, false);
		else
			busTabbedPane.setEnabledAt(1, true);
		
		if (scriptLoadRadioButton.isSelected() || scriptGenRadioButton.isSelected()) {
			busTabbedPane.setSelectedIndex(1);
			this.customScriptEditPanel.setForm2Editor();
		}
		else
			busTabbedPane.setSelectedIndex(0);
    }

    // set Gen data field lable and status
    private void setGenLabelText(String pLabel, boolean pEnabled, String pValue, 
    		                     String qLabel, boolean qEnabled, String qValue) {
    	pGenLabel.setText(pLabel);
    	pGenLabel.setEnabled(pEnabled);
    	pGenTextField.setEnabled(pEnabled);
    	if (pEnabled)
    		pGenTextField.setText(pValue);
    	else
    		pGenTextField.setText("0.0");
    		
    	qGenLabel.setText(qLabel);
    	qGenLabel.setEnabled(qEnabled);
    	qGenTextField.setEnabled(qEnabled);
    	if (qEnabled)
    		qGenTextField.setText(qValue);
    	else
    		qGenTextField.setText("0.0");
    }    

    /** Creates new form AclfEditPanel */
    public NBAclfTransBusEditPanel() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        genTypeButtonGroup = new javax.swing.ButtonGroup();
        loadTypeButtonGroup = new javax.swing.ButtonGroup();
        reQControlButtonGroup = new javax.swing.ButtonGroup();
        flowDirectionButtonGroup = new javax.swing.ButtonGroup();
        mvarSideButtonGroup = new javax.swing.ButtonGroup();
        busTabbedPane = new javax.swing.JTabbedPane();
        lfDataPanel = new javax.swing.JPanel();
        genTypePanel = new javax.swing.JPanel();
        swingRadioButton = new javax.swing.JRadioButton();
        pvRadioButton = new javax.swing.JRadioButton();
        pqRadioButton = new javax.swing.JRadioButton();
        remoteQRadioButton = new javax.swing.JRadioButton();
        capRadioButton = new javax.swing.JRadioButton();
        nonGenRadioButton = new javax.swing.JRadioButton();
        scriptGenRadioButton = new javax.swing.JRadioButton();
        getInfoPanel = new javax.swing.JPanel();
        pGenLabel = new javax.swing.JLabel();
        pGenTextField = new javax.swing.JTextField();
        qGenLabel = new javax.swing.JLabel();
        qGenTextField = new javax.swing.JTextField();
        adjustCheckBox = new javax.swing.JCheckBox();
        reQControlTypePanel = new javax.swing.JPanel();
        reControl1Label = new javax.swing.JLabel();
        voltageRadioButton = new javax.swing.JRadioButton();
        mvaFlowRadioButton = new javax.swing.JRadioButton();
        reQControl2Label = new javax.swing.JLabel();
        maxLabel = new javax.swing.JLabel();
        maxTextField = new javax.swing.JTextField();
        minLabel = new javax.swing.JLabel();
        minTextField = new javax.swing.JTextField();
        remoteBusLabel = new javax.swing.JLabel();
        remoteBusComboBox = new javax.swing.JComboBox();
        flowDirectionPanel = new javax.swing.JPanel();
        from2ToRadioButton = new javax.swing.JRadioButton();
        to2FromRadioButton = new javax.swing.JRadioButton();
        mvarSodePanel = new javax.swing.JPanel();
        mvarOnFromSideRadioButton = new javax.swing.JRadioButton();
        mvarOnToSideRadioButton = new javax.swing.JRadioButton();
        loadTypePanel = new javax.swing.JPanel();
        constPRadioButton = new javax.swing.JRadioButton();
        constIRadioButton = new javax.swing.JRadioButton();
        constZRadioButton = new javax.swing.JRadioButton();
        funcLoadRadioButton = new javax.swing.JRadioButton();
        nonLoadRadioButton = new javax.swing.JRadioButton();
        scriptLoadRadioButton = new javax.swing.JRadioButton();
        loadInfoPanel = new javax.swing.JPanel();
        pLoadLabel = new javax.swing.JLabel();
        pLoadTextField = new javax.swing.JTextField();
        qLoadLabel = new javax.swing.JLabel();
        qLoadTextField = new javax.swing.JTextField();
        constP_PLabel = new javax.swing.JLabel();
        constP_PTextField = new javax.swing.JTextField();
        constP_QLabel = new javax.swing.JLabel();
        constP_QTextField = new javax.swing.JTextField();
        constI_PLabel = new javax.swing.JLabel();
        constI_PTextField = new javax.swing.JTextField();
        constI_QLabel = new javax.swing.JLabel();
        constI_QTextField = new javax.swing.JTextField();
        constZ_PLabel = new javax.swing.JLabel();
        constZ_PTextField = new javax.swing.JTextField();
        constZ_QLabel = new javax.swing.JLabel();
        constZ_QTextField = new javax.swing.JTextField();
        shuntYPanel = new javax.swing.JPanel();
        shutYLabel = new javax.swing.JLabel();
        shuntGTextField = new javax.swing.JTextField();
        shuntBTextField = new javax.swing.JTextField();
        scriptPanel = new javax.swing.JPanel();

        busTabbedPane.setFont(new java.awt.Font("Dialog", 0, 12));

        lfDataPanel.setLayout(new java.awt.GridBagLayout());

        genTypePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Generator Type", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10)));
        genTypePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 0));

        genTypeButtonGroup.add(swingRadioButton);
        swingRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        swingRadioButton.setText("Swing");
        swingRadioButton.setName("swingRadioButton"); // NOI18N
        swingRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                swingRadioButtonSelected(evt);
            }
        });
        genTypePanel.add(swingRadioButton);

        genTypeButtonGroup.add(pvRadioButton);
        pvRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        pvRadioButton.setText("PV");
        pvRadioButton.setName("pvRadioButton"); // NOI18N
        pvRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pvRadioButtonSelected(evt);
            }
        });
        genTypePanel.add(pvRadioButton);

        genTypeButtonGroup.add(pqRadioButton);
        pqRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        pqRadioButton.setSelected(true);
        pqRadioButton.setText("PQ");
        pqRadioButton.setName("pqRadioButton"); // NOI18N
        pqRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pqRadioButtonSelected(evt);
            }
        });
        genTypePanel.add(pqRadioButton);

        genTypeButtonGroup.add(remoteQRadioButton);
        remoteQRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        remoteQRadioButton.setText("RemoteControl");
        remoteQRadioButton.setToolTipText("Control a remote bus voltage by adjusting the current bus Q. The remote bus can not be a PV, PQ or Swing bus. ");
        remoteQRadioButton.setEnabled(false);
        remoteQRadioButton.setName("remoteQRadioButton"); // NOI18N
        remoteQRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                remoteQRadioButtonActionPerformed(evt);
            }
        });
        genTypePanel.add(remoteQRadioButton);

        genTypeButtonGroup.add(capRadioButton);
        capRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        capRadioButton.setText("Capacitor");
        capRadioButton.setName("capRadioButton"); // NOI18N
        capRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                capRadioButtonSelected(evt);
            }
        });
        genTypePanel.add(capRadioButton);

        genTypeButtonGroup.add(nonGenRadioButton);
        nonGenRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        nonGenRadioButton.setText("NonGen");
        nonGenRadioButton.setName("nonGenRadioButton"); // NOI18N
        nonGenRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nonGenRadioButtonSelected(evt);
            }
        });
        genTypePanel.add(nonGenRadioButton);

        genTypeButtonGroup.add(scriptGenRadioButton);
        scriptGenRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        scriptGenRadioButton.setText("GenScripting");
        scriptGenRadioButton.setName("nonGenRadioButton"); // NOI18N
        scriptGenRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scriptGenRadioButtonSelected(evt);
            }
        });
        genTypePanel.add(scriptGenRadioButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        lfDataPanel.add(genTypePanel, gridBagConstraints);

        getInfoPanel.setLayout(new java.awt.GridBagLayout());

        pGenLabel.setFont(new java.awt.Font("Default", 0, 12));
        pGenLabel.setText("Pgen(pu)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        getInfoPanel.add(pGenLabel, gridBagConstraints);

        pGenTextField.setColumns(8);
        pGenTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        pGenTextField.setText("0.0");
        pGenTextField.setName("pGenTextField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 20, 10, 0);
        getInfoPanel.add(pGenTextField, gridBagConstraints);

        qGenLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        qGenLabel.setText("Qgen(pu)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        getInfoPanel.add(qGenLabel, gridBagConstraints);

        qGenTextField.setColumns(8);
        qGenTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        qGenTextField.setText("0.0");
        qGenTextField.setName("qGenTextField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
        getInfoPanel.add(qGenTextField, gridBagConstraints);

        adjustCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        adjustCheckBox.setText("PV Limit Control");
        adjustCheckBox.setEnabled(false);
        adjustCheckBox.setName("adjustCheckBox"); // NOI18N
        adjustCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adjustCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        getInfoPanel.add(adjustCheckBox, gridBagConstraints);

        reControl1Label.setFont(new java.awt.Font("Dialog", 0, 12));
        reControl1Label.setText("[");
        reControl1Label.setEnabled(false);
        reQControlTypePanel.add(reControl1Label);

        reQControlButtonGroup.add(voltageRadioButton);
        voltageRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        voltageRadioButton.setSelected(true);
        voltageRadioButton.setText("Voltage   ");
        voltageRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        voltageRadioButton.setEnabled(false);
        voltageRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        voltageRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                voltageRadioButtonActionPerformed(evt);
            }
        });
        reQControlTypePanel.add(voltageRadioButton);

        reQControlButtonGroup.add(mvaFlowRadioButton);
        mvaFlowRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        mvaFlowRadioButton.setText("Mvar Flow   ");
        mvaFlowRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        mvaFlowRadioButton.setEnabled(false);
        mvaFlowRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        mvaFlowRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mvaFlowRadioButtonActionPerformed(evt);
            }
        });
        reQControlTypePanel.add(mvaFlowRadioButton);

        reQControl2Label.setFont(new java.awt.Font("Dialog", 0, 12));
        reQControl2Label.setText("]");
        reQControl2Label.setEnabled(false);
        reQControlTypePanel.add(reQControl2Label);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        getInfoPanel.add(reQControlTypePanel, gridBagConstraints);

        maxLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        maxLabel.setText("Max(pu)");
        maxLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        getInfoPanel.add(maxLabel, gridBagConstraints);

        maxTextField.setColumns(8);
        maxTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        maxTextField.setText("0.0");
        maxTextField.setEnabled(false);
        maxTextField.setName("maxTextField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
        getInfoPanel.add(maxTextField, gridBagConstraints);

        minLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        minLabel.setText("Min(pu)");
        minLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        getInfoPanel.add(minLabel, gridBagConstraints);

        minTextField.setColumns(8);
        minTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        minTextField.setText("0.0");
        minTextField.setEnabled(false);
        minTextField.setName("minTextField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
        getInfoPanel.add(minTextField, gridBagConstraints);

        remoteBusLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        remoteBusLabel.setText("Remote Bus     ");
        remoteBusLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        getInfoPanel.add(remoteBusLabel, gridBagConstraints);

        remoteBusComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        remoteBusComboBox.setToolTipText("Remote bus cannot be a Swing, PV or PQ bus");
        remoteBusComboBox.setEnabled(false);
        remoteBusComboBox.setName("remoteBusComboBox"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        getInfoPanel.add(remoteBusComboBox, gridBagConstraints);

        flowDirectionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Mvar Flow Direction", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10)));

        flowDirectionButtonGroup.add(from2ToRadioButton);
        from2ToRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        from2ToRadioButton.setSelected(true);
        from2ToRadioButton.setText("From->To     ");
        from2ToRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        from2ToRadioButton.setEnabled(false);
        from2ToRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        flowDirectionPanel.add(from2ToRadioButton);

        flowDirectionButtonGroup.add(to2FromRadioButton);
        to2FromRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        to2FromRadioButton.setText("To->From");
        to2FromRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        to2FromRadioButton.setEnabled(false);
        to2FromRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        flowDirectionPanel.add(to2FromRadioButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        getInfoPanel.add(flowDirectionPanel, gridBagConstraints);

        mvarSodePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Mvar Spec On", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10)));

        mvarSideButtonGroup.add(mvarOnFromSideRadioButton);
        mvarOnFromSideRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        mvarOnFromSideRadioButton.setSelected(true);
        mvarOnFromSideRadioButton.setText("From Side     ");
        mvarOnFromSideRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        mvarOnFromSideRadioButton.setEnabled(false);
        mvarOnFromSideRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        mvarSodePanel.add(mvarOnFromSideRadioButton);

        mvarSideButtonGroup.add(mvarOnToSideRadioButton);
        mvarOnToSideRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        mvarOnToSideRadioButton.setText("To Side");
        mvarOnToSideRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        mvarOnToSideRadioButton.setEnabled(false);
        mvarOnToSideRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        mvarSodePanel.add(mvarOnToSideRadioButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 20);
        getInfoPanel.add(mvarSodePanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        lfDataPanel.add(getInfoPanel, gridBagConstraints);

        loadTypePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Load Type", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10)));
        loadTypePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 0));

        loadTypeButtonGroup.add(constPRadioButton);
        constPRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        constPRadioButton.setSelected(true);
        constPRadioButton.setText("Const-P");
        constPRadioButton.setName("constPRadioButton"); // NOI18N
        constPRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                constPRadioButtonSelected(evt);
            }
        });
        loadTypePanel.add(constPRadioButton);

        loadTypeButtonGroup.add(constIRadioButton);
        constIRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        constIRadioButton.setText("Const-I");
        constIRadioButton.setName("constIRadioButton"); // NOI18N
        constIRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                constIRadioButtonSelected(evt);
            }
        });
        loadTypePanel.add(constIRadioButton);

        loadTypeButtonGroup.add(constZRadioButton);
        constZRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        constZRadioButton.setText("Const-Z");
        constZRadioButton.setName("constZRadioButton"); // NOI18N
        constZRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                constZRadioButtonSelected(evt);
            }
        });
        loadTypePanel.add(constZRadioButton);

        loadTypeButtonGroup.add(funcLoadRadioButton);
        funcLoadRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        funcLoadRadioButton.setText("Functional");
        funcLoadRadioButton.setEnabled(false);
        funcLoadRadioButton.setName("funcLoadRadioButton"); // NOI18N
        funcLoadRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                funcLoadRadioButtonSelected(evt);
            }
        });
        loadTypePanel.add(funcLoadRadioButton);

        loadTypeButtonGroup.add(nonLoadRadioButton);
        nonLoadRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        nonLoadRadioButton.setText("NonLoad");
        nonLoadRadioButton.setName("nonLoadRadioButton"); // NOI18N
        nonLoadRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nonLoadRadioButtonSelected(evt);
            }
        });
        loadTypePanel.add(nonLoadRadioButton);

        loadTypeButtonGroup.add(scriptLoadRadioButton);
        scriptLoadRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        scriptLoadRadioButton.setText("LoadScripting");
        scriptLoadRadioButton.setName("nonLoadRadioButton"); // NOI18N
        scriptLoadRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scriptLoadRadioButtonSelected(evt);
            }
        });
        loadTypePanel.add(scriptLoadRadioButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        lfDataPanel.add(loadTypePanel, gridBagConstraints);

        loadInfoPanel.setLayout(new java.awt.GridBagLayout());

        pLoadLabel.setFont(new java.awt.Font("Default", 0, 12));
        pLoadLabel.setText("Pload(pu)  ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        loadInfoPanel.add(pLoadLabel, gridBagConstraints);

        pLoadTextField.setColumns(8);
        pLoadTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        pLoadTextField.setText("0.0");
        pLoadTextField.setName("pLoadTextField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        loadInfoPanel.add(pLoadTextField, gridBagConstraints);

        qLoadLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        qLoadLabel.setText("Qload(pu)  ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 20, 0);
        loadInfoPanel.add(qLoadLabel, gridBagConstraints);

        qLoadTextField.setColumns(8);
        qLoadTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        qLoadTextField.setText("0.0");
        qLoadTextField.setName("qLoadTextField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 20, 0);
        loadInfoPanel.add(qLoadTextField, gridBagConstraints);

        constP_PLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        constP_PLabel.setText("          Const-P(%)  ");
        constP_PLabel.setEnabled(false);
        loadInfoPanel.add(constP_PLabel, new java.awt.GridBagConstraints());

        constP_PTextField.setColumns(5);
        constP_PTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        constP_PTextField.setText("100.0");
        constP_PTextField.setEnabled(false);
        constP_PTextField.setName("constP_PTextField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        loadInfoPanel.add(constP_PTextField, gridBagConstraints);

        constP_QLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        constP_QLabel.setText("          Const-P(%)  ");
        constP_QLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 20, 0);
        loadInfoPanel.add(constP_QLabel, gridBagConstraints);

        constP_QTextField.setColumns(5);
        constP_QTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        constP_QTextField.setText("100.0");
        constP_QTextField.setEnabled(false);
        constP_QTextField.setName("constP_QTextField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 20, 0);
        loadInfoPanel.add(constP_QTextField, gridBagConstraints);

        constI_PLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        constI_PLabel.setText("     I(%)  ");
        constI_PLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        loadInfoPanel.add(constI_PLabel, gridBagConstraints);

        constI_PTextField.setColumns(5);
        constI_PTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        constI_PTextField.setText("0.0");
        constI_PTextField.setEnabled(false);
        constI_PTextField.setName("constI_PTextField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        loadInfoPanel.add(constI_PTextField, gridBagConstraints);

        constI_QLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        constI_QLabel.setText("     I(%)  ");
        constI_QLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 20, 0);
        loadInfoPanel.add(constI_QLabel, gridBagConstraints);

        constI_QTextField.setColumns(5);
        constI_QTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        constI_QTextField.setText("0.0");
        constI_QTextField.setEnabled(false);
        constI_QTextField.setName("constI_QTextField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 20, 0);
        loadInfoPanel.add(constI_QTextField, gridBagConstraints);

        constZ_PLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        constZ_PLabel.setText("     Z(%)  ");
        constZ_PLabel.setEnabled(false);
        constZ_PLabel.setFocusable(false);
        loadInfoPanel.add(constZ_PLabel, new java.awt.GridBagConstraints());

        constZ_PTextField.setColumns(5);
        constZ_PTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        constZ_PTextField.setText("0.0");
        constZ_PTextField.setEnabled(false);
        constZ_PTextField.setName("constZ_PTextField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        loadInfoPanel.add(constZ_PTextField, gridBagConstraints);

        constZ_QLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        constZ_QLabel.setText("     Z(%)  ");
        constZ_QLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 20, 0);
        loadInfoPanel.add(constZ_QLabel, gridBagConstraints);

        constZ_QTextField.setColumns(5);
        constZ_QTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        constZ_QTextField.setText("0.0");
        constZ_QTextField.setEnabled(false);
        constZ_QTextField.setName("constZ_QTextField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 20, 0);
        loadInfoPanel.add(constZ_QTextField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        lfDataPanel.add(loadInfoPanel, gridBagConstraints);

        shuntYPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 5));

        shutYLabel.setFont(new java.awt.Font("Default", 0, 12));
        shutYLabel.setText("Shunt G+jB (pu) ");
        shuntYPanel.add(shutYLabel);

        shuntGTextField.setColumns(8);
        shuntGTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        shuntGTextField.setText("0.0");
        shuntGTextField.setName("pLoadTextField"); // NOI18N
        shuntYPanel.add(shuntGTextField);

        shuntBTextField.setColumns(8);
        shuntBTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        shuntBTextField.setText("0.0");
        shuntBTextField.setName("constP_PTextField"); // NOI18N
        shuntYPanel.add(shuntBTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        lfDataPanel.add(shuntYPanel, gridBagConstraints);

        busTabbedPane.addTab("Bus LF Data", lfDataPanel);
        busTabbedPane.addTab("Bus LF Scripting", scriptPanel);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(busTabbedPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 669, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(busTabbedPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        busTabbedPane.getAccessibleContext().setAccessibleName("");
    }// </editor-fold>//GEN-END:initComponents

    private void scriptLoadRadioButtonSelected(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scriptLoadRadioButtonSelected
    	_data.setLoadCode(AclfBusData.LoadCode_LoadScripting);
        setLoadLabelText(false, true);
        setScriptPanel();
}//GEN-LAST:event_scriptLoadRadioButtonSelected

    private void scriptGenRadioButtonSelected(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scriptGenRadioButtonSelected
        setGenLabelText("Pgen(pu)", false, "0.0", "    Qgen(pu)", false, "0.0");
        setAdjLabelText(false, BUS_TYPE_PV);
        setScriptPanel();
}//GEN-LAST:event_scriptGenRadioButtonSelected

    private void mvaFlowRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mvaFlowRadioButtonActionPerformed
    	IpssLogger.getLogger().info("mvaFlowRadioButtonActionPerformed() called");
        setGenLabelText("Pgen(pu)", true, "0.0", "    MvarFlowVspec(pu)", true, "1.0");
        setAdjLabelText(true, BUS_TYPE_ReQMvar);
    }//GEN-LAST:event_mvaFlowRadioButtonActionPerformed

    private void voltageRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_voltageRadioButtonActionPerformed
    	IpssLogger.getLogger().info("voltageRadioButtonActionPerformed() called");
        setGenLabelText("Pgen(pu)", true, "0.0", "    ReVspec(pu)", true, "1.0");
        setAdjLabelText(true, BUS_TYPE_ReQVolt);
    }//GEN-LAST:event_voltageRadioButtonActionPerformed
    
    private void remoteQRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_remoteQRadioButtonActionPerformed
    	IpssLogger.getLogger().info("remoteQRadioButtonActionPerformed() called");
    	adjustCheckBox.setSelected(true);
        if (_data.getReQControlType()==AclfAdjBusData.ReQControlType_Voltage) {
            voltageRadioButton.setSelected(true);
            voltageRadioButtonActionPerformed(evt);
        }
        else {
            mvaFlowRadioButton.setSelected(true);
            mvaFlowRadioButtonActionPerformed(evt);
        }
    	adjustCheckBox.setEnabled(false);
    }//GEN-LAST:event_remoteQRadioButtonActionPerformed
    
    private void adjustCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adjustCheckBoxActionPerformed
        setAdjLabelText(true, pqRadioButton.isSelected()?BUS_TYPE_PQ : 
        	     (pvRadioButton.isSelected()? BUS_TYPE_PV : 
        	    	 (_data.getReQControlType()==AclfAdjBusData.ReQControlType_Voltage?
        	        		BUS_TYPE_ReQVolt : BUS_TYPE_ReQMvar)));
    }//GEN-LAST:event_adjustCheckBoxActionPerformed

    private void nonLoadRadioButtonSelected(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nonLoadRadioButtonSelected
    	_data.setLoadCode(AclfBusData.LoadCode_NonLoad);
        setLoadLabelText(false, true);
        editUIEventContainer.fireEvent(new EditUIEvent(EditUIEvent.BusCodeChanged));
        setScriptPanel();
    }//GEN-LAST:event_nonLoadRadioButtonSelected

    private void funcLoadRadioButtonSelected(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_funcLoadRadioButtonSelected
    	_data.setLoadCode(AclfBusData.LoadCode_FuncLoad);
        setLoadLabelText(true, false);
        editUIEventContainer.fireEvent(new EditUIEvent(EditUIEvent.BusCodeChanged));
        setScriptPanel();
    }//GEN-LAST:event_funcLoadRadioButtonSelected

    private void constZRadioButtonSelected(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_constZRadioButtonSelected
    	_data.setLoadCode(AclfBusData.LoadCode_ConstZ);
        setLoadLabelText(false, false);
        editUIEventContainer.fireEvent(new EditUIEvent(EditUIEvent.BusCodeChanged));
        setScriptPanel();
    }//GEN-LAST:event_constZRadioButtonSelected

    private void constIRadioButtonSelected(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_constIRadioButtonSelected
    	_data.setLoadCode(AclfBusData.LoadCode_ConstI);
        setLoadLabelText(false, false);
        editUIEventContainer.fireEvent(new EditUIEvent(EditUIEvent.BusCodeChanged));
        setScriptPanel();
    }//GEN-LAST:event_constIRadioButtonSelected

    private void constPRadioButtonSelected(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_constPRadioButtonSelected
    	_data.setLoadCode(AclfBusData.LoadCode_ConstP);
        setLoadLabelText(false, false);
        editUIEventContainer.fireEvent(new EditUIEvent(EditUIEvent.BusCodeChanged));
        setScriptPanel();
    }//GEN-LAST:event_constPRadioButtonSelected

    private void nonGenRadioButtonSelected(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nonGenRadioButtonSelected
    	_data.setGenCode(AclfBusData.GenCode_NonGen);
        setGenLabelText("Pgen(pu)", false, "0.0", "    Qgen(pu)", false, "0.0");
        setAdjLabelText(false, BUS_TYPE_PV);
        editUIEventContainer.fireEvent(new EditUIEvent(EditUIEvent.BusCodeChanged));
        setScriptPanel();
    }//GEN-LAST:event_nonGenRadioButtonSelected

    private void capRadioButtonSelected(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_capRadioButtonSelected
    	_data.setGenCode(AclfBusData.GenCode_Capacitor);
        setGenLabelText("Qcap(pu)", true, "0.0", "  ", false, "0.0");
        setAdjLabelText(false, BUS_TYPE_PV);
        editUIEventContainer.fireEvent(new EditUIEvent(EditUIEvent.BusCodeChanged));
        setScriptPanel();
    }//GEN-LAST:event_capRadioButtonSelected

    private void pqRadioButtonSelected(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pqRadioButtonSelected
    	_data.setGenCode(AclfBusData.GenCode_PQ);
        setGenLabelText("Pgen(pu)", true, "0.0", "    Qgen(pu)", true, "0.0");
        setAdjLabelText(((GNetForm)_netContainer.getGNetForm()).getAcscNetData().isHasAdjustment(), BUS_TYPE_PQ);
        editUIEventContainer.fireEvent(new EditUIEvent(EditUIEvent.BusCodeChanged));
        setScriptPanel();
    }//GEN-LAST:event_pqRadioButtonSelected

    private void pvRadioButtonSelected(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pvRadioButtonSelected
    	_data.setGenCode(AclfBusData.GenCode_PV);
        setGenLabelText("Pgen(pu)", true, "0.0", "    Vspec(pu)", true, "1.0");
        setAdjLabelText(((GNetForm)_netContainer.getGNetForm()).getAcscNetData().isHasAdjustment(), BUS_TYPE_PV);
        editUIEventContainer.fireEvent(new EditUIEvent(EditUIEvent.BusCodeChanged));
        setScriptPanel();
    }//GEN-LAST:event_pvRadioButtonSelected

    private void swingRadioButtonSelected(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_swingRadioButtonSelected
    	_data.setGenCode(AclfBusData.GenCode_Swing);
        setGenLabelText("Vspec(pu)", true, "1.0", "    Angle(deg)", true, "0.0");
        setAdjLabelText(false, BUS_TYPE_PV);
        editUIEventContainer.fireEvent(new EditUIEvent(EditUIEvent.BusCodeChanged));
        setScriptPanel();
    }//GEN-LAST:event_swingRadioButtonSelected
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox adjustCheckBox;
    private javax.swing.JTabbedPane busTabbedPane;
    private javax.swing.JRadioButton capRadioButton;
    private javax.swing.JRadioButton constIRadioButton;
    private javax.swing.JLabel constI_PLabel;
    private javax.swing.JTextField constI_PTextField;
    private javax.swing.JLabel constI_QLabel;
    private javax.swing.JTextField constI_QTextField;
    private javax.swing.JRadioButton constPRadioButton;
    private javax.swing.JLabel constP_PLabel;
    private javax.swing.JTextField constP_PTextField;
    private javax.swing.JLabel constP_QLabel;
    private javax.swing.JTextField constP_QTextField;
    private javax.swing.JRadioButton constZRadioButton;
    private javax.swing.JLabel constZ_PLabel;
    private javax.swing.JTextField constZ_PTextField;
    private javax.swing.JLabel constZ_QLabel;
    private javax.swing.JTextField constZ_QTextField;
    private javax.swing.ButtonGroup flowDirectionButtonGroup;
    private javax.swing.JPanel flowDirectionPanel;
    private javax.swing.JRadioButton from2ToRadioButton;
    private javax.swing.JRadioButton funcLoadRadioButton;
    private javax.swing.ButtonGroup genTypeButtonGroup;
    private javax.swing.JPanel genTypePanel;
    private javax.swing.JPanel getInfoPanel;
    private javax.swing.JPanel lfDataPanel;
    private javax.swing.JPanel loadInfoPanel;
    private javax.swing.ButtonGroup loadTypeButtonGroup;
    private javax.swing.JPanel loadTypePanel;
    private javax.swing.JLabel maxLabel;
    private javax.swing.JTextField maxTextField;
    private javax.swing.JLabel minLabel;
    private javax.swing.JTextField minTextField;
    private javax.swing.JRadioButton mvaFlowRadioButton;
    private javax.swing.JRadioButton mvarOnFromSideRadioButton;
    private javax.swing.JRadioButton mvarOnToSideRadioButton;
    private javax.swing.ButtonGroup mvarSideButtonGroup;
    private javax.swing.JPanel mvarSodePanel;
    private javax.swing.JRadioButton nonGenRadioButton;
    private javax.swing.JRadioButton nonLoadRadioButton;
    private javax.swing.JLabel pGenLabel;
    private javax.swing.JTextField pGenTextField;
    private javax.swing.JLabel pLoadLabel;
    private javax.swing.JTextField pLoadTextField;
    private javax.swing.JRadioButton pqRadioButton;
    private javax.swing.JRadioButton pvRadioButton;
    private javax.swing.JLabel qGenLabel;
    private javax.swing.JTextField qGenTextField;
    private javax.swing.JLabel qLoadLabel;
    private javax.swing.JTextField qLoadTextField;
    private javax.swing.JLabel reControl1Label;
    private javax.swing.JLabel reQControl2Label;
    private javax.swing.ButtonGroup reQControlButtonGroup;
    private javax.swing.JPanel reQControlTypePanel;
    private javax.swing.JComboBox remoteBusComboBox;
    private javax.swing.JLabel remoteBusLabel;
    private javax.swing.JRadioButton remoteQRadioButton;
    private javax.swing.JRadioButton scriptGenRadioButton;
    private javax.swing.JRadioButton scriptLoadRadioButton;
    private javax.swing.JPanel scriptPanel;
    private javax.swing.JTextField shuntBTextField;
    private javax.swing.JTextField shuntGTextField;
    private javax.swing.JPanel shuntYPanel;
    private javax.swing.JLabel shutYLabel;
    private javax.swing.JRadioButton swingRadioButton;
    private javax.swing.JRadioButton to2FromRadioButton;
    private javax.swing.JRadioButton voltageRadioButton;
    // End of variables declaration//GEN-END:variables
    
	class DataVerifier extends javax.swing.InputVerifier {
    	public boolean verify(javax.swing.JComponent input) {
			if (input == null)
				return false;
       		try {
               if ( input == constP_PTextField ||
          		    input == constI_PTextField ||
          		    input == constP_QTextField ||
          		    input == constI_QTextField )
 	       			return SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) >= 0.0 && SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) <= 100.0;
               else if ( input == constZ_PTextField )
 	       			return SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) >= 0.0 &&
					    	(SwingInputVerifyUtil.getDouble(constP_PTextField)+SwingInputVerifyUtil.getDouble(constI_PTextField)+
					    			SwingInputVerifyUtil.getDouble(constZ_PTextField) == 100.0);
               else if ( input == constZ_QTextField )
       				return SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) >= 0.0 &&
       						(SwingInputVerifyUtil.getDouble(constP_QTextField)+SwingInputVerifyUtil.getDouble(constI_QTextField)+
       								SwingInputVerifyUtil.getDouble(constZ_QTextField) == 100.0);
 	       	} catch (Exception e) {
 	    		return false;
 	       	}		
			return true;
        }
    }
}
