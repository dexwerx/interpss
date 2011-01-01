 /*
  * @(#)NBMachinePanel.java   
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

package org.interpss.editor.ui.edit.trans.dstab;
  
import java.util.Vector;

import javax.swing.JPanel;

import org.interpss.editor.data.dstab.DStabBusData;
import org.interpss.editor.data.dstab.DStabMachData;
import org.interpss.editor.form.GBusForm;
import org.interpss.editor.form.GFormContainer;
import org.interpss.editor.form.InitDataUtil;
import org.interpss.editor.jgraph.ui.edit.IFormDataPanel;
import org.interpss.editor.ui.edit.common.NBGroundInputPanel;
import org.interpss.editor.ui.edit.trans.bus.NBDStabTransBusEditPanel;
import org.interpss.numeric.util.Number2String;
import org.interpss.ui.SwingInputVerifyUtil;

import com.interpss.common.util.IpssLogger;
 
 
public class NBMachinePanel extends javax.swing.JPanel implements IFormDataPanel {
	private static final long serialVersionUID = 1;

	JPanel parent = null;
	
	private GFormContainer _netContainer = null;
    private GBusForm _form = null;
    private DStabBusData _data = null;

	private NBGroundInputPanel _groundPanel = new NBGroundInputPanel();

	public void initPanel(JPanel p) {
		parent = p;
		
		_groundPanel.initPanel();
	    groundingPanel.add(_groundPanel);

	    DataVerifier verifier = new DataVerifier();
	    ratingTextField.setInputVerifier(verifier);
	    ratedVoltTextField.setInputVerifier(verifier);
	    inertiaTextField.setInputVerifier(verifier);
	    dampingTextField.setInputVerifier(verifier);

	    x0TextField.setInputVerifier(verifier);
	    x2TextField.setInputVerifier(verifier);
	    xlTextField.setInputVerifier(verifier);
	    raTextField.setInputVerifier(verifier);
	    xqTextField.setInputVerifier(verifier);
	    xdTextField.setInputVerifier(verifier);

	    xd1TextField.setInputVerifier(verifier);
	    xq1TextField.setInputVerifier(verifier);
	    td01TextField.setInputVerifier(verifier);
	    tq01TextField.setInputVerifier(verifier);

	    xd11TextField.setInputVerifier(verifier);
	    xq11TextField.setInputVerifier(verifier);
	    td011TextField.setInputVerifier(verifier);
	    tq011TextField.setInputVerifier(verifier);

	    s100TextField.setInputVerifier(verifier);
	    s120TextField.setInputVerifier(verifier);
	    slinerTextField.setInputVerifier(verifier);
	}
    
	public void init(Object netContainer, Object form) {
		IpssLogger.getLogger().info("NBMachinePanel init() called");
		_netContainer = (GFormContainer)netContainer;
		_form = (GBusForm)form;
		_data = _form.getDStabBusData();

		_groundPanel.init(_netContainer, _data.getMachData().getGround());
	}
	
    public DStabBusData getDStabBusData() {
    		return _data;
	}

    public boolean setForm2Editor() {
		IpssLogger.getLogger().info("NBMachinePanel setForm2Editor() called");

		DStabMachData machData = _data.getMachData();
		setLabelTextField(machData.getType());
		
	    excCheckBox.setSelected(false);
		((NBDStabTransBusEditPanel)parent).setExcTabbedPaneEnabled(false);
	    govCheckBox.setSelected(false);
		((NBDStabTransBusEditPanel)parent).setGovTabbedPaneEnabled(false);
	    excCheckBox.setEnabled(false);
	    govCheckBox.setEnabled(false);

	    if (machData.getType().equals(DStabMachData.MachType_InfiniteBus)) {
			infiniteBusRadioButton.setSelected(true);
		}	
		else if (machData.getType().equals(DStabMachData.MachType_EConst)) {
			eConstantRadioButton.setSelected(true);
		}
		else if (machData.getType().equals(DStabMachData.MachType_Eq1)) {
    	    excCheckBox.setEnabled(true);
    	    govCheckBox.setEnabled(true);
		    eq1RadioButton.setSelected(true);
		}
		else if (machData.getType().equals(DStabMachData.MachType_Eq1Ed1)) {
    	    excCheckBox.setEnabled(true);
    	    govCheckBox.setEnabled(true);
		    eq1Ed1RadioButton.setSelected(true);
		}
		else if (machData.getType().equals(DStabMachData.MachType_RoundRotor)) {
    	    excCheckBox.setEnabled(true);
    	    govCheckBox.setEnabled(true);
			roundRotorRadioButton.setSelected(true);
		}
		else {
    	    excCheckBox.setEnabled(true);
    	    govCheckBox.setEnabled(true);
			salientPoleRadioButton.setSelected(true);
		}	

		if (excCheckBox.isEnabled() && _form.getDStabBusData().getMachData().getHasExc()) {
		    excCheckBox.setSelected(true);
    		((NBDStabTransBusEditPanel)parent).setExcTabbedPaneEnabled(true);
		} 
			
		if (govCheckBox.isEnabled() && _form.getDStabBusData().getMachData().getHasGov()) {
		    govCheckBox.setSelected(true);
    		((NBDStabTransBusEditPanel)parent).setGovTabbedPaneEnabled(true);
		}    

		if (machData.getType().equals(DStabMachData.MachType_InfiniteBus)) {
		    inertiaTextField.setText(Number2String.toStr(machData.getScMva3P(), "#0.00"));
		    dampingTextField.setText(Number2String.toStr(machData.getScMva1P(), "#0.00"));
		    x0TextField.setText(Number2String.toStr(machData.getX_R_3P(), "#0.00"));
		    x2TextField.setText(Number2String.toStr(machData.getX_R_1P(), "#0.00"));
		}
		else {
		    inertiaTextField.setText(Number2String.toStr(machData.getInertia(), "#0.00"));
		    dampingTextField.setText(Number2String.toStr(machData.getDamping(), "#0.0000"));
		    x2TextField.setText(Number2String.toStr(machData.getX2(), "#0.0000"));
		    x0TextField.setText(Number2String.toStr(machData.getX0(), "#0.0000"));
		}
	    machNameTextField.setText(machData.getName());
	    ratingTextField.setText(Number2String.toStr(machData.getRating(), "#0.00"));
	    ratedVoltTextField.setText(Number2String.toStr(machData.getRatedVolt(), "#0.0"));
	    polesTextField.setText(Number2String.toStr(machData.getPoles()));
		
	    xlTextField.setText(Number2String.toStr(machData.getXl(), "#0.0000"));
	    raTextField.setText(Number2String.toStr(machData.getRa(), "#0.0000"));
	    xqTextField.setText(Number2String.toStr(machData.getXq(), "#0.00"));
	    xdTextField.setText(Number2String.toStr(machData.getXd(), "#0.00"));

	    xd1TextField.setText(Number2String.toStr(machData.getXd1(), "#0.000"));
	    xq1TextField.setText(Number2String.toStr(machData.getXq1(), "#0.000"));
	    td01TextField.setText(Number2String.toStr(machData.getTd01(), "#0.000"));
	    tq01TextField.setText(Number2String.toStr(machData.getTq01(), "#0.000"));

	    xd11TextField.setText(Number2String.toStr(machData.getXd11(), "#0.0000"));
	    xq11TextField.setText(Number2String.toStr(machData.getXq11(), "#0.0000"));
	    td011TextField.setText(Number2String.toStr(machData.getTd011(), "#0.0000"));
	    tq011TextField.setText(Number2String.toStr(machData.getTq011(), "#0.0000"));

	    s100TextField.setText(Number2String.toStr(machData.getS100(), "#0.0"));
	    s120TextField.setText(Number2String.toStr(machData.getS120(), "#0.0"));
	    slinerTextField.setText(Number2String.toStr(machData.getSliner(), "#0.0"));

	    _groundPanel.setForm2Editor();
    	return true;
	}
    
    public boolean saveEditor2Form(Vector errMsg) throws Exception {
		IpssLogger.getLogger().info("NBMachinePanel saveEditor2Form() called");
		boolean ok = true;
		
		DStabMachData machData = _data.getMachData();

		if (machData.getType().equals(DStabMachData.MachType_InfiniteBus)) {
			if (!SwingInputVerifyUtil.largeThan(this.inertiaTextField, 0.0d)) {
				errMsg.add("MVA3P <= 0.0");
				ok = false;
			}
			machData.setScMva3P(SwingInputVerifyUtil.getDouble(inertiaTextField));
			
			if (!SwingInputVerifyUtil.largeEqualThan(this.dampingTextField, 0.0d)) {
				errMsg.add("MVA1P < 0.0");
				ok = false;
			}
			machData.setScMva1P(SwingInputVerifyUtil.getDouble(dampingTextField));

			if (!SwingInputVerifyUtil.largeThan(this.x0TextField, 0.0d)) {
				errMsg.add("X/R 3P <= 0.0");
				ok = false;
			}
			machData.setX_R_3P(SwingInputVerifyUtil.getDouble(x0TextField));
			
			if (!SwingInputVerifyUtil.largeThan(this.x2TextField, 0.0d)) {
				errMsg.add("X/R 1P <= 0.0");
				ok = false;
			}
			machData.setX_R_1P(SwingInputVerifyUtil.getDouble(x2TextField));
		}
		else {
			if (!SwingInputVerifyUtil.largeThan(this.inertiaTextField, 0.0d)) {
				errMsg.add("Inertia <= 0.0");
				ok = false;
			}
			machData.setInertia(SwingInputVerifyUtil.getDouble(inertiaTextField));
			
			if (!SwingInputVerifyUtil.largeThan(this.dampingTextField, 0.0d)) {
				errMsg.add("Damping <= 0.0");
				ok = false;
			}
			machData.setDamping(SwingInputVerifyUtil.getDouble(dampingTextField));
			
			if (!SwingInputVerifyUtil.largeThan(this.x0TextField, 0.0d)) {
				errMsg.add("X0 <= 0.0");
				ok = false;
			}
			machData.setX0(SwingInputVerifyUtil.getDouble(x0TextField));
			
			if (!SwingInputVerifyUtil.largeThan(this.x2TextField, 0.0d)) {
				errMsg.add("X2 <= 0.0");
				ok = false;
			}
			machData.setX2(SwingInputVerifyUtil.getDouble(x2TextField));
		}
		
		if (machNameTextField.isEnabled()) {
			machData.setName(machNameTextField.getText());
		}

		if (ratingTextField.isEnabled()) {
			if (!SwingInputVerifyUtil.largeThan(this.ratingTextField, 0.0d)) {
				errMsg.add("Rating <= 0.0");
				ok = false;
			}
			machData.setRating(SwingInputVerifyUtil.getDouble(ratingTextField));
		}

		if (ratedVoltTextField.isEnabled()) {
			if (!SwingInputVerifyUtil.largeThan(this.ratedVoltTextField, 0.0d)) {
				errMsg.add("Rated Voltage <= 0.0");
				ok = false;
			}
			machData.setRatedVolt(SwingInputVerifyUtil.getDouble(ratedVoltTextField));
		}

		if (polesTextField.isEnabled()) {
			if (!SwingInputVerifyUtil.largeThan(this.polesTextField, 0) | SwingInputVerifyUtil.getInt(polesTextField) % 2 != 0) {
				errMsg.add("Poles <= 0 or not even");
				ok = false;
			}
			machData.setPoles(SwingInputVerifyUtil.getInt(polesTextField));
		}

		if (xlTextField.isEnabled()) {
			if (!SwingInputVerifyUtil.largeThan(this.xlTextField, 0.0d)) {
				errMsg.add("Xl <= 0.0");
				ok = false;
			}
			machData.setXl(SwingInputVerifyUtil.getDouble(xlTextField));
		}

		if (raTextField.isEnabled()) {
			if (!SwingInputVerifyUtil.largeEqualThan(this.raTextField, 0.0d)) {
				errMsg.add("X2 < 0.0");
				ok = false;
			}
			machData.setRa(SwingInputVerifyUtil.getDouble(raTextField));
		}
		
		if (xqTextField.isEnabled()) {
			if (!SwingInputVerifyUtil.largeThan(this.xqTextField, 0.0d)) {
				errMsg.add("Xq <= 0.0");
				ok = false;
			}
			machData.setXq(SwingInputVerifyUtil.getDouble(xqTextField));
		}

		if (xdTextField.isEnabled()) {
			if (!SwingInputVerifyUtil.largeThan(this.xdTextField, 0.0d)) {
				errMsg.add("Xd <= 0.0");
				ok = false;
			}
			machData.setXd(SwingInputVerifyUtil.getDouble(xdTextField));
		}

		if (xd1TextField.isEnabled()) {
			if (!SwingInputVerifyUtil.largeThan(this.xd1TextField, 0.0d)) {
				errMsg.add("Xd1 <= 0.0");
				ok = false;
			}
			machData.setXd1(SwingInputVerifyUtil.getDouble(xd1TextField));
		}
		
		if (xq1TextField.isEnabled()) {
			if (!SwingInputVerifyUtil.largeThan(this.xq1TextField, 0.0d)) {
				errMsg.add("Xq1 <= 0.0");
				ok = false;
			}
			machData.setXq1(SwingInputVerifyUtil.getDouble(xq1TextField));
		}
		
		if (td01TextField.isEnabled()) {
			if (!SwingInputVerifyUtil.largeThan(this.td01TextField, 0.0d)) {
				errMsg.add("Td01 <= 0.0");
				ok = false;
			}
			machData.setTd01(SwingInputVerifyUtil.getDouble(td01TextField));
		}
		
		if (tq01TextField.isEnabled()) {
			if (!SwingInputVerifyUtil.largeThan(this.tq01TextField, 0.0d)) {
				errMsg.add("Tq01 <= 0.0");
				ok = false;
			}
			machData.setTq01(SwingInputVerifyUtil.getDouble(tq01TextField));
		}
		
		if (xd11TextField.isEnabled()) {
			if (!SwingInputVerifyUtil.largeThan(this.xd11TextField, 0.0d)) {
				errMsg.add("Xd11 <= 0.0");
				ok = false;
			}
			machData.setXd11(SwingInputVerifyUtil.getDouble(xd11TextField));
		}
		
		if (xq11TextField.isEnabled()) {
			if (!SwingInputVerifyUtil.largeThan(this.xq11TextField, 0.0d)) {
				errMsg.add("Xq11 <= 0.0");
				ok = false;
			}
			machData.setXq11(SwingInputVerifyUtil.getDouble(xq11TextField));
		}
		
		if (td011TextField.isEnabled()) {
			if (!SwingInputVerifyUtil.largeThan(this.td011TextField, 0.0d)) {
				errMsg.add("Td011 <= 0.0");
				ok = false;
			}
			machData.setTd011(SwingInputVerifyUtil.getDouble(td011TextField));
		}

		if (tq011TextField.isEnabled()) {
			if (!SwingInputVerifyUtil.largeThan(this.tq011TextField, 0.0d)) {
				errMsg.add("Tq011 <= 0.0");
				ok = false;
			}
			machData.setTq011(SwingInputVerifyUtil.getDouble(tq011TextField));
		}	

		if (s100TextField.isEnabled()) {
			if (!SwingInputVerifyUtil.largeThan(this.s100TextField, 0.0d)) {
				errMsg.add("S100 <= 0.0");
				ok = false;
			}
			machData.setS100(SwingInputVerifyUtil.getDouble(s100TextField));
		}

		if (s120TextField.isEnabled()) {
			if (!SwingInputVerifyUtil.largeThan(this.s120TextField, 0.0d)) {
				errMsg.add("S120 <= 0.0");
				ok = false;
			}
			machData.setS120(SwingInputVerifyUtil.getDouble(s120TextField));
		}

		if (slinerTextField.isEnabled()) {
			if (!SwingInputVerifyUtil.largeThan(this.slinerTextField, 0.0d)) {
				errMsg.add("SLiner <= 0.0");
				ok = false;
			}
			machData.setSliner(SwingInputVerifyUtil.getDouble(slinerTextField));
		}

	    if (!_groundPanel.saveEditor2Form(errMsg))
			ok = false;
		
		return ok;
    }
    
    private void setLabelTextField(String type) {
    	if (type.equals(DStabMachData.MachType_InfiniteBus)) {
    	    excCheckBox.setSelected(false);
    	    govCheckBox.setSelected(false);
    	    excCheckBox.setEnabled(false);
    	    govCheckBox.setEnabled(false);
    		
    	    ratingLabel.setEnabled(false);
    	    ratingTextField.setEnabled(false);

    	    ratedVoltLabel.setEnabled(true);
    	    ratedVoltTextField.setEnabled(true);

    	    polesLabel.setEnabled(false);
    	    polesTextField.setEnabled(false);
    	    
    	    inertiaLabel.setEnabled(true);
    	    inertiaLabel.setText("          ScMva(3P)   ");
    	    inertiaTextField.setEnabled(true);
    	    
    	    dampingLabel.setEnabled(true);
    	    dampingLabel.setText("ScMva(1P)   ");
    	    dampingTextField.setEnabled(true);
    	    
    	    x0Label.setEnabled(true);
    	    x0Label.setText("          X/R(3P)   ");
    	    x0TextField.setEnabled(true);

    	    x2Label.setEnabled(true);
    	    x2Label.setText("          X/R(1P)   ");
    	    x2TextField.setEnabled(true);

    	    xlLabel.setEnabled(false);
		    xlTextField.setEnabled(false);
		    
		    raLabel.setEnabled(false);
		    raTextField.setEnabled(false);
		    xqLabel.setEnabled(false);
		    xqTextField.setEnabled(false);
		    xdLabel.setEnabled(false);
		    xdTextField.setEnabled(false);

		    xd1Label.setEnabled(false);
		    xd1TextField.setEnabled(false);
		    xq1Label.setEnabled(false);
		    xq1TextField.setEnabled(false);
		    td01Label.setEnabled(false);
		    td01TextField.setEnabled(false);
		    tq01Label.setEnabled(false);
		    tq01TextField.setEnabled(false);

		    xd11Label.setEnabled(false);
		    xd11TextField.setEnabled(false);
		    xq11Label.setEnabled(false);
		    xq11TextField.setEnabled(false);
		    td011Label.setEnabled(false);
		    td011TextField.setEnabled(false);
		    tq011Label.setEnabled(false);
		    tq011TextField.setEnabled(false);

		    slinerLabel.setEnabled(false);
		    slinerTextField.setEnabled(false);
		    s100Label.setEnabled(false);
		    s100TextField.setEnabled(false);
		    s120Label.setEnabled(false);
		    s120TextField.setEnabled(false);
    		return;
    	}
    	
    	excCheckBox.setEnabled(!type.equals(DStabMachData.MachType_EConst) &&
    			               _form.getDStabBusData().getMachData().getHasExc());
	    govCheckBox.setEnabled(!type.equals(DStabMachData.MachType_EConst) &&
	    		               _form.getDStabBusData().getMachData().getHasGov());

	    ratingTextField.setEnabled(true);
	    ratedVoltTextField.setEnabled(true);
	    inertiaTextField.setEnabled(true);
	    polesLabel.setEnabled(true);
	    polesTextField.setEnabled(true);
	    dampingTextField.setEnabled(true);
	    x0TextField.setEnabled(true);
	    x2TextField.setEnabled(true);
	    ratingLabel.setEnabled(true);
	    ratedVoltLabel.setEnabled(true);
	    inertiaLabel.setEnabled(true);
	    dampingLabel.setEnabled(true);
	    x0Label.setEnabled(true);
	    x2Label.setEnabled(true);

        inertiaLabel.setText("          H(sec)   ");
	    dampingLabel.setText("D(%MW/Hz)   ");
	    x2Label.setText("          X2(pu)   ");
	    xlLabel.setText("Xl(pu)   ");
	    
	    if (type.equals(DStabMachData.MachType_EConst)) {
		    xlLabel.setEnabled(false);
		    xlTextField.setEnabled(false);
		    raLabel.setEnabled(false);
		    raTextField.setEnabled(false);
		    xqLabel.setEnabled(false);
		    xqTextField.setEnabled(false);
		    xdLabel.setEnabled(false);
		    xdTextField.setEnabled(false);

		    xd1Label.setEnabled(true);
		    xd1TextField.setEnabled(true);
		    xq1Label.setEnabled(false);
		    xq1TextField.setEnabled(false);
		    td01Label.setEnabled(false);
		    td01TextField.setEnabled(false);
		    tq01Label.setEnabled(false);
		    tq01TextField.setEnabled(false);

		    xd11Label.setEnabled(false);
		    xd11TextField.setEnabled(false);
		    xq11Label.setEnabled(false);
		    xq11TextField.setEnabled(false);
		    td011Label.setEnabled(false);
		    td011TextField.setEnabled(false);
		    tq011Label.setEnabled(false);
		    tq011TextField.setEnabled(false);

		    slinerLabel.setEnabled(false);
		    slinerTextField.setEnabled(false);
		    s100Label.setEnabled(false);
		    s100TextField.setEnabled(false);
		    s120Label.setEnabled(false);
		    s120TextField.setEnabled(false);
		}
		else {
		    raLabel.setEnabled(true);
		    raTextField.setEnabled(true);
		    xlLabel.setEnabled(true);
		    xlTextField.setEnabled(true);
		    xqLabel.setEnabled(true);
		    xqTextField.setEnabled(true);
		    xdLabel.setEnabled(true);
		    xdTextField.setEnabled(true);

		    slinerLabel.setEnabled(true);
		    slinerTextField.setEnabled(true);
		    s100Label.setEnabled(true);
		    s100TextField.setEnabled(true);
		    s120Label.setEnabled(true);
		    s120TextField.setEnabled(true);

		    if (type.equals(DStabMachData.MachType_Eq1)) {
			    xd1Label.setEnabled(true);
			    xd1TextField.setEnabled(true);
			    xq1Label.setEnabled(false);
			    xq1TextField.setEnabled(false);
			    td01Label.setEnabled(true);
			    td01TextField.setEnabled(true);
			    tq01Label.setEnabled(false);
			    tq01TextField.setEnabled(false);

			    xd11Label.setEnabled(false);
			    xd11TextField.setEnabled(false);
			    xq11Label.setEnabled(false);
			    xq11TextField.setEnabled(false);
			    td011Label.setEnabled(false);
			    td011TextField.setEnabled(false);
			    tq011Label.setEnabled(false);
			    tq011TextField.setEnabled(false);
			}    
			else if (type.equals(DStabMachData.MachType_Eq1Ed1)) {
			    xd1Label.setEnabled(true);
			    xd1TextField.setEnabled(true);
			    xq1Label.setEnabled(true);
			    xq1TextField.setEnabled(true);
			    td01Label.setEnabled(true);
			    td01TextField.setEnabled(true);
			    tq01Label.setEnabled(true);
			    tq01TextField.setEnabled(true);

			    xd11Label.setEnabled(false);
			    xd11TextField.setEnabled(false);
			    xq11Label.setEnabled(false);
			    xq11TextField.setEnabled(false);
			    td011Label.setEnabled(false);
			    td011TextField.setEnabled(false);
			    tq011Label.setEnabled(false);
			    tq011TextField.setEnabled(false);
			}    
			else if (type.equals(DStabMachData.MachType_SalientPole)) {
			    xd1Label.setEnabled(true);
			    xd1TextField.setEnabled(true);
			    xq1Label.setEnabled(false);
			    xq1TextField.setEnabled(false);
			    td01Label.setEnabled(true);
			    td01TextField.setEnabled(true);
			    tq01Label.setEnabled(false);
			    tq01TextField.setEnabled(false);

			    xd11Label.setEnabled(true);
			    xd11TextField.setEnabled(true);
			    xq11Label.setEnabled(true);
			    xq11TextField.setEnabled(true);
			    td011Label.setEnabled(true);
			    td011TextField.setEnabled(true);
			    tq011Label.setEnabled(true);
			    tq011TextField.setEnabled(true);
			}	
			else { 
			    xd1Label.setEnabled(true);
			    xd1TextField.setEnabled(true);
			    xq1Label.setEnabled(true);
			    xq1TextField.setEnabled(true);
			    td01Label.setEnabled(true);
			    td01TextField.setEnabled(true);
			    tq01Label.setEnabled(true);
			    tq01TextField.setEnabled(true);

			    xd11Label.setEnabled(true);
			    xd11TextField.setEnabled(true);
			    xq11Label.setEnabled(true);
			    xq11TextField.setEnabled(true);
			    td011Label.setEnabled(true);
			    td011TextField.setEnabled(true);
			    tq011Label.setEnabled(true);
			    tq011TextField.setEnabled(true);
			}
		}
    }
    
    /** Creates new form AclfEditPanel */
    public NBMachinePanel() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        machTypeButtonGroup = new javax.swing.ButtonGroup();
        machTypePanel = new javax.swing.JPanel();
        eConstantRadioButton = new javax.swing.JRadioButton();
        eq1RadioButton = new javax.swing.JRadioButton();
        eq1Ed1RadioButton = new javax.swing.JRadioButton();
        roundRotorRadioButton = new javax.swing.JRadioButton();
        salientPoleRadioButton = new javax.swing.JRadioButton();
        infiniteBusRadioButton = new javax.swing.JRadioButton();
        excCheckBox = new javax.swing.JCheckBox();
        govCheckBox = new javax.swing.JCheckBox();
        machInfojPanel = new javax.swing.JPanel();
        machNameLabel = new javax.swing.JLabel();
        machNameTextField = new javax.swing.JTextField();
        ratingLabel = new javax.swing.JLabel();
        ratingTextField = new javax.swing.JTextField();
        ratedVoltLabel = new javax.swing.JLabel();
        ratedVoltTextField = new javax.swing.JTextField();
        polesLabel = new javax.swing.JLabel();
        polesTextField = new javax.swing.JTextField();
        inertiaLabel = new javax.swing.JLabel();
        inertiaTextField = new javax.swing.JTextField();
        dampingLabel = new javax.swing.JLabel();
        dampingTextField = new javax.swing.JTextField();
        x0Label = new javax.swing.JLabel();
        x0TextField = new javax.swing.JTextField();
        x2Label = new javax.swing.JLabel();
        x2TextField = new javax.swing.JTextField();
        xlLabel = new javax.swing.JLabel();
        xlTextField = new javax.swing.JTextField();
        raLabel = new javax.swing.JLabel();
        raTextField = new javax.swing.JTextField();
        xdLabel = new javax.swing.JLabel();
        xdTextField = new javax.swing.JTextField();
        xqLabel = new javax.swing.JLabel();
        xqTextField = new javax.swing.JTextField();
        xd1Label = new javax.swing.JLabel();
        xd1TextField = new javax.swing.JTextField();
        xq1Label = new javax.swing.JLabel();
        xq1TextField = new javax.swing.JTextField();
        td01Label = new javax.swing.JLabel();
        td01TextField = new javax.swing.JTextField();
        tq01Label = new javax.swing.JLabel();
        tq01TextField = new javax.swing.JTextField();
        xd11Label = new javax.swing.JLabel();
        xd11TextField = new javax.swing.JTextField();
        xq11Label = new javax.swing.JLabel();
        xq11TextField = new javax.swing.JTextField();
        td011Label = new javax.swing.JLabel();
        td011TextField = new javax.swing.JTextField();
        tq011Label = new javax.swing.JLabel();
        tq011TextField = new javax.swing.JTextField();
        slinerLabel = new javax.swing.JLabel();
        slinerTextField = new javax.swing.JTextField();
        s100Label = new javax.swing.JLabel();
        s100TextField = new javax.swing.JTextField();
        s120Label = new javax.swing.JLabel();
        s120TextField = new javax.swing.JTextField();
        groundingPanel = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        machTypePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Machine Model Type", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10)));
        machTypeButtonGroup.add(eConstantRadioButton);
        eConstantRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        eConstantRadioButton.setText("EConstant");
        eConstantRadioButton.setName("eConstantRadioButton");
        eConstantRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eConstantRadioButtonActionPerformed(evt);
            }
        });

        machTypePanel.add(eConstantRadioButton);

        machTypeButtonGroup.add(eq1RadioButton);
        eq1RadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        eq1RadioButton.setSelected(true);
        eq1RadioButton.setText("Eq1 Model");
        eq1RadioButton.setAutoscrolls(true);
        eq1RadioButton.setName("eq1RadioButton");
        eq1RadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eq1RadioButtonActionPerformed(evt);
            }
        });

        machTypePanel.add(eq1RadioButton);

        machTypeButtonGroup.add(eq1Ed1RadioButton);
        eq1Ed1RadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        eq1Ed1RadioButton.setText("Eq1 Ed1 Model");
        eq1Ed1RadioButton.setAutoscrolls(true);
        eq1Ed1RadioButton.setName("eq1Ed1RadioButton");
        eq1Ed1RadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eq1Ed1RadioButtonActionPerformed(evt);
            }
        });

        machTypePanel.add(eq1Ed1RadioButton);

        machTypeButtonGroup.add(roundRotorRadioButton);
        roundRotorRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        roundRotorRadioButton.setText("E11 Round Rotor");
        roundRotorRadioButton.setAutoscrolls(true);
        roundRotorRadioButton.setName("roundRotorRadioButton");
        roundRotorRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                roundRotorRadioButtonActionPerformed(evt);
            }
        });

        machTypePanel.add(roundRotorRadioButton);

        machTypeButtonGroup.add(salientPoleRadioButton);
        salientPoleRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        salientPoleRadioButton.setText("E11 Salient Pole");
        salientPoleRadioButton.setAutoscrolls(true);
        salientPoleRadioButton.setName("salientPoleRadioButton");
        salientPoleRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                salientPoleRadioButtonActionPerformed(evt);
            }
        });

        machTypePanel.add(salientPoleRadioButton);

        machTypeButtonGroup.add(infiniteBusRadioButton);
        infiniteBusRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        infiniteBusRadioButton.setText("InfinitBus");
        infiniteBusRadioButton.setName("infiniteBusRadioButton");
        infiniteBusRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                infiniteBusRadioButtonActionPerformed(evt);
            }
        });

        machTypePanel.add(infiniteBusRadioButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(30, 0, 5, 0);
        add(machTypePanel, gridBagConstraints);

        excCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        excCheckBox.setSelected(true);
        excCheckBox.setText("Has Excitation Controller");
        excCheckBox.setName("excCheckBox");
        excCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                excCheckBoxActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 40, 15, 10);
        add(excCheckBox, gridBagConstraints);

        govCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        govCheckBox.setSelected(true);
        govCheckBox.setText("Has Governor Controller");
        govCheckBox.setName("govCheckBox");
        govCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                govCheckBoxActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 15, 0);
        add(govCheckBox, gridBagConstraints);

        machInfojPanel.setLayout(new java.awt.GridBagLayout());

        machInfojPanel.setMinimumSize(new java.awt.Dimension(630, 240));
        machInfojPanel.setPreferredSize(new java.awt.Dimension(630, 240));
        machNameLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        machNameLabel.setText("Name   ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        machInfojPanel.add(machNameLabel, gridBagConstraints);

        machNameTextField.setColumns(20);
        machNameTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        machNameTextField.setText("<Machine Name>");
        machNameTextField.setMinimumSize(new java.awt.Dimension(99, 21));
        machNameTextField.setName("machNameTextField");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 5, 0);
        machInfojPanel.add(machNameTextField, gridBagConstraints);

        ratingLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        ratingLabel.setText("     Rating(Mva)   ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        machInfojPanel.add(ratingLabel, gridBagConstraints);

        ratingTextField.setColumns(8);
        ratingTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        ratingTextField.setText("0.0");
        ratingTextField.setMinimumSize(new java.awt.Dimension(99, 21));
        ratingTextField.setName("ratingTextField");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        machInfojPanel.add(ratingTextField, gridBagConstraints);

        ratedVoltLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        ratedVoltLabel.setText("RatedVolt(V)   ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(ratedVoltLabel, gridBagConstraints);

        ratedVoltTextField.setColumns(8);
        ratedVoltTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        ratedVoltTextField.setText("0.0");
        ratedVoltTextField.setMinimumSize(new java.awt.Dimension(99, 21));
        ratedVoltTextField.setName("ratedVoltTextField");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(ratedVoltTextField, gridBagConstraints);

        polesLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        polesLabel.setText("          Poles   ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(polesLabel, gridBagConstraints);

        polesTextField.setColumns(8);
        polesTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        polesTextField.setText("2");
        polesTextField.setName("dampingTextField");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(polesTextField, gridBagConstraints);

        inertiaLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        inertiaLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        inertiaLabel.setText("          H(sec)   ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(inertiaLabel, gridBagConstraints);

        inertiaTextField.setColumns(8);
        inertiaTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        inertiaTextField.setText("0.0");
        inertiaTextField.setName("inertiaTextField");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(inertiaTextField, gridBagConstraints);

        dampingLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        dampingLabel.setText("D(%MW/Hz)   ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(dampingLabel, gridBagConstraints);

        dampingTextField.setColumns(8);
        dampingTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        dampingTextField.setText("0.0");
        dampingTextField.setName("dampingTextField");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(dampingTextField, gridBagConstraints);

        x0Label.setFont(new java.awt.Font("Dialog", 0, 12));
        x0Label.setText("          X0(pu)   ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(x0Label, gridBagConstraints);

        x0TextField.setColumns(8);
        x0TextField.setFont(new java.awt.Font("Dialog", 0, 12));
        x0TextField.setText("0.0");
        x0TextField.setName("x0TextField");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(x0TextField, gridBagConstraints);

        x2Label.setFont(new java.awt.Font("Dialog", 0, 12));
        x2Label.setText("          X2(pu)   ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(x2Label, gridBagConstraints);

        x2TextField.setColumns(8);
        x2TextField.setFont(new java.awt.Font("Dialog", 0, 12));
        x2TextField.setText("0.0");
        x2TextField.setName("x2TextField");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(x2TextField, gridBagConstraints);

        xlLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        xlLabel.setText("Xl(pu)   ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(xlLabel, gridBagConstraints);

        xlTextField.setColumns(8);
        xlTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        xlTextField.setText("0.0");
        xlTextField.setName("xlTextField");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(xlTextField, gridBagConstraints);

        raLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        raLabel.setText("          Ra(pu)   ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(raLabel, gridBagConstraints);

        raTextField.setColumns(8);
        raTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        raTextField.setText("0.0");
        raTextField.setName("raTextField");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(raTextField, gridBagConstraints);

        xdLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        xdLabel.setText("          Xd(pu)   ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(xdLabel, gridBagConstraints);

        xdTextField.setColumns(8);
        xdTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        xdTextField.setText("0.0");
        xdTextField.setName("xdTextField");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(xdTextField, gridBagConstraints);

        xqLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        xqLabel.setText("Xq(pu)   ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(xqLabel, gridBagConstraints);

        xqTextField.setColumns(8);
        xqTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        xqTextField.setText("0.0");
        xqTextField.setName("xqTextField");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(xqTextField, gridBagConstraints);

        xd1Label.setFont(new java.awt.Font("Dialog", 0, 12));
        xd1Label.setText("          Xd1(pu)   ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(xd1Label, gridBagConstraints);

        xd1TextField.setColumns(8);
        xd1TextField.setFont(new java.awt.Font("Dialog", 0, 12));
        xd1TextField.setText("0.0");
        xd1TextField.setName("xd1TextField");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(xd1TextField, gridBagConstraints);

        xq1Label.setFont(new java.awt.Font("Dialog", 0, 12));
        xq1Label.setText("          Xq1(pu)   ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(xq1Label, gridBagConstraints);

        xq1TextField.setColumns(8);
        xq1TextField.setFont(new java.awt.Font("Dialog", 0, 12));
        xq1TextField.setText("0.0");
        xq1TextField.setName("xq1TextField");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(xq1TextField, gridBagConstraints);

        td01Label.setFont(new java.awt.Font("Dialog", 0, 12));
        td01Label.setText("Td01(sec)   ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(td01Label, gridBagConstraints);

        td01TextField.setColumns(8);
        td01TextField.setFont(new java.awt.Font("Dialog", 0, 12));
        td01TextField.setText("0.0");
        td01TextField.setName("td01TextField");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(td01TextField, gridBagConstraints);

        tq01Label.setFont(new java.awt.Font("Dialog", 0, 12));
        tq01Label.setText("          Tq01(sec)   ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(tq01Label, gridBagConstraints);

        tq01TextField.setColumns(8);
        tq01TextField.setFont(new java.awt.Font("Dialog", 0, 12));
        tq01TextField.setText("0.0");
        tq01TextField.setName("tq01TextField");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(tq01TextField, gridBagConstraints);

        xd11Label.setFont(new java.awt.Font("Dialog", 0, 12));
        xd11Label.setText("          Xd11(pu)   ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(xd11Label, gridBagConstraints);

        xd11TextField.setColumns(8);
        xd11TextField.setFont(new java.awt.Font("Dialog", 0, 12));
        xd11TextField.setText("0.0");
        xd11TextField.setName("xd11TextField");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(xd11TextField, gridBagConstraints);

        xq11Label.setFont(new java.awt.Font("Dialog", 0, 12));
        xq11Label.setText("Xq11(pu)   ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(xq11Label, gridBagConstraints);

        xq11TextField.setColumns(8);
        xq11TextField.setFont(new java.awt.Font("Dialog", 0, 12));
        xq11TextField.setText("0.0");
        xq11TextField.setName("xq11TextField");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(xq11TextField, gridBagConstraints);

        td011Label.setFont(new java.awt.Font("Dialog", 0, 12));
        td011Label.setText("          Td011(sec)   ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(td011Label, gridBagConstraints);

        td011TextField.setColumns(8);
        td011TextField.setFont(new java.awt.Font("Dialog", 0, 12));
        td011TextField.setText("0.0");
        td011TextField.setName("td011TextField");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(td011TextField, gridBagConstraints);

        tq011Label.setFont(new java.awt.Font("Dialog", 0, 12));
        tq011Label.setText("          Tq011(sec)   ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(tq011Label, gridBagConstraints);

        tq011TextField.setColumns(8);
        tq011TextField.setFont(new java.awt.Font("Dialog", 0, 12));
        tq011TextField.setText("0.0");
        tq011TextField.setName("tq011TextField");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(tq011TextField, gridBagConstraints);

        slinerLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        slinerLabel.setText("Sliner(pu)   ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(slinerLabel, gridBagConstraints);

        slinerTextField.setColumns(8);
        slinerTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        slinerTextField.setText("0.0");
        slinerTextField.setName("slinerTextField");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(slinerTextField, gridBagConstraints);

        s100Label.setFont(new java.awt.Font("Dialog", 0, 12));
        s100Label.setText("          S100(%)   ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(s100Label, gridBagConstraints);

        s100TextField.setColumns(8);
        s100TextField.setFont(new java.awt.Font("Dialog", 0, 12));
        s100TextField.setText("0.0");
        s100TextField.setName("s100TextField");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(s100TextField, gridBagConstraints);

        s120Label.setFont(new java.awt.Font("Dialog", 0, 12));
        s120Label.setText("          S120(%)   ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(s120Label, gridBagConstraints);

        s120TextField.setColumns(8);
        s120TextField.setFont(new java.awt.Font("Dialog", 0, 12));
        s120TextField.setText("0.0");
        s120TextField.setName("s120TextField");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        machInfojPanel.add(s120TextField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        add(machInfojPanel, gridBagConstraints);

        groundingPanel.setLayout(new java.awt.BorderLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 20, 0);
        add(groundingPanel, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents

    private void infiniteBusRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_infiniteBusRadioButtonActionPerformed
		_data.getMachData().setType(DStabMachData.MachType_InfiniteBus);
		setForm2Editor();
    }//GEN-LAST:event_infiniteBusRadioButtonActionPerformed

    private void govCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_govCheckBoxActionPerformed
    	if (govCheckBox.isSelected()) {
    		_data.getMachData().setHasGov(true);
    		((NBDStabTransBusEditPanel)parent).setGovTabbedPaneEnabled(true);
    	}	
    	else {
    		_data.getMachData().setHasGov(false);
    		((NBDStabTransBusEditPanel)parent).setGovTabbedPaneEnabled(false);
    	}	
    }//GEN-LAST:event_govCheckBoxActionPerformed

    private void excCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_excCheckBoxActionPerformed
    	if (excCheckBox.isSelected()) {
    		_data.getMachData().setHasExc(true);
    		((NBDStabTransBusEditPanel)parent).setExcTabbedPaneEnabled(true);
    		if (_data.getMachData().getHasPss()) {
        		((NBDStabTransBusEditPanel)parent).setPssTabbedPaneEnabled(true);
    		}
    		else {
        		((NBDStabTransBusEditPanel)parent).setPssTabbedPaneEnabled(false);
    		}
    	}	
    	else {
    		_data.getMachData().setHasExc(false);
    		((NBDStabTransBusEditPanel)parent).setExcTabbedPaneEnabled(false);
    		((NBDStabTransBusEditPanel)parent).setPssTabbedPaneEnabled(false);
    	}	
    }//GEN-LAST:event_excCheckBoxActionPerformed

    private void salientPoleRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_salientPoleRadioButtonActionPerformed
		_data.getMachData().setType(DStabMachData.MachType_SalientPole);
		InitDataUtil.initDStabMachineData(_data.getMachData());
		setForm2Editor();
    }//GEN-LAST:event_salientPoleRadioButtonActionPerformed

    private void roundRotorRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_roundRotorRadioButtonActionPerformed
		_data.getMachData().setType(DStabMachData.MachType_RoundRotor);
		InitDataUtil.initDStabMachineData(_data.getMachData());
		setForm2Editor();
    }//GEN-LAST:event_roundRotorRadioButtonActionPerformed

    private void eq1Ed1RadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eq1Ed1RadioButtonActionPerformed
		_data.getMachData().setType(DStabMachData.MachType_Eq1Ed1);
		InitDataUtil.initDStabMachineData(_data.getMachData());
		setForm2Editor();
    }//GEN-LAST:event_eq1Ed1RadioButtonActionPerformed

    private void eq1RadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eq1RadioButtonActionPerformed
		_data.getMachData().setType(DStabMachData.MachType_Eq1);
		InitDataUtil.initDStabMachineData(_data.getMachData());
		setForm2Editor();
    }//GEN-LAST:event_eq1RadioButtonActionPerformed

    private void eConstantRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eConstantRadioButtonActionPerformed
		_data.getMachData().setType(DStabMachData.MachType_EConst);
		InitDataUtil.initDStabMachineData(_data.getMachData());
		setForm2Editor();
    }//GEN-LAST:event_eConstantRadioButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel dampingLabel;
    private javax.swing.JTextField dampingTextField;
    private javax.swing.JRadioButton eConstantRadioButton;
    private javax.swing.JRadioButton eq1Ed1RadioButton;
    private javax.swing.JRadioButton eq1RadioButton;
    private javax.swing.JCheckBox excCheckBox;
    private javax.swing.JCheckBox govCheckBox;
    private javax.swing.JPanel groundingPanel;
    private javax.swing.JLabel inertiaLabel;
    private javax.swing.JTextField inertiaTextField;
    private javax.swing.JRadioButton infiniteBusRadioButton;
    private javax.swing.JPanel machInfojPanel;
    private javax.swing.JLabel machNameLabel;
    private javax.swing.JTextField machNameTextField;
    private javax.swing.ButtonGroup machTypeButtonGroup;
    private javax.swing.JPanel machTypePanel;
    private javax.swing.JLabel polesLabel;
    private javax.swing.JTextField polesTextField;
    private javax.swing.JLabel raLabel;
    private javax.swing.JTextField raTextField;
    private javax.swing.JLabel ratedVoltLabel;
    private javax.swing.JTextField ratedVoltTextField;
    private javax.swing.JLabel ratingLabel;
    private javax.swing.JTextField ratingTextField;
    private javax.swing.JRadioButton roundRotorRadioButton;
    private javax.swing.JLabel s100Label;
    private javax.swing.JTextField s100TextField;
    private javax.swing.JLabel s120Label;
    private javax.swing.JTextField s120TextField;
    private javax.swing.JRadioButton salientPoleRadioButton;
    private javax.swing.JLabel slinerLabel;
    private javax.swing.JTextField slinerTextField;
    private javax.swing.JLabel td011Label;
    private javax.swing.JTextField td011TextField;
    private javax.swing.JLabel td01Label;
    private javax.swing.JTextField td01TextField;
    private javax.swing.JLabel tq011Label;
    private javax.swing.JTextField tq011TextField;
    private javax.swing.JLabel tq01Label;
    private javax.swing.JTextField tq01TextField;
    private javax.swing.JLabel x0Label;
    private javax.swing.JTextField x0TextField;
    private javax.swing.JLabel x2Label;
    private javax.swing.JTextField x2TextField;
    private javax.swing.JLabel xd11Label;
    private javax.swing.JTextField xd11TextField;
    private javax.swing.JLabel xd1Label;
    private javax.swing.JTextField xd1TextField;
    private javax.swing.JLabel xdLabel;
    private javax.swing.JTextField xdTextField;
    private javax.swing.JLabel xlLabel;
    private javax.swing.JTextField xlTextField;
    private javax.swing.JLabel xq11Label;
    private javax.swing.JTextField xq11TextField;
    private javax.swing.JLabel xq1Label;
    private javax.swing.JTextField xq1TextField;
    private javax.swing.JLabel xqLabel;
    private javax.swing.JTextField xqTextField;
    // End of variables declaration//GEN-END:variables
    
	class DataVerifier extends javax.swing.InputVerifier {
    	public boolean verify(javax.swing.JComponent input) {
			if (input == null)
				return false;
       		try {
 	       	} catch (Exception e) {
 	    		return false;
 	       	}		
			return true;
        }
    }
}
