 /*
  * @(#)NBBranchScDataPanel.java   
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

package org.interpss.editor.ui.edit.trans.branch;
  
import java.util.Vector;

import javax.swing.JDialog;

import org.interpss.editor.data.acsc.AcscBranchData;
import org.interpss.editor.form.GBranchForm;
import org.interpss.editor.form.GFormContainer;
import org.interpss.editor.jgraph.ui.edit.IFormDataPanel;
import org.interpss.editor.jgraph.ui.form.IGBranchForm;
import org.interpss.editor.ui.edit.common.NBGConnectionPanel;
import org.interpss.numeric.util.Number2String;
import org.interpss.ui.SwingInputVerifyUtil;

import com.interpss.common.util.IpssLogger;
 
 
public class NBBranchScDataPanel extends javax.swing.JPanel implements IFormDataPanel {
	private static final long serialVersionUID = 1;
	private JDialog parentDialog = null;
	private Object  parentPanel = null;

    private GFormContainer _netContainer = null;
    private GBranchForm  _form = null;
    private AcscBranchData  _data = null;
    
    private static NBGConnectionPanel _fromXfrConnectPanel = new NBGConnectionPanel();
    private static NBGConnectionPanel _toXfrConnectPanel = new NBGConnectionPanel();
    
	public void initPanel(JDialog pDialog, Object pPanel) {
		parentDialog = pDialog;
		parentPanel =  pPanel;
		
	    _fromXfrConnectPanel.initPanel(parentDialog, "FromSide Grounding");
	    _toXfrConnectPanel.initPanel(parentDialog, "ToSide Grounding");
	    
		DataVerifier verifier = new DataVerifier();
	    fromTapTextField.setInputVerifier(verifier);
	    hB0TextField.setInputVerifier(verifier);
	    hB1TextField.setInputVerifier(verifier);
	    r0TextField.setInputVerifier(verifier);
	    r1TextField.setInputVerifier(verifier);
	    toTapTextField.setInputVerifier(verifier);
	    x0TextField.setInputVerifier(verifier);
	    x1TextField.setInputVerifier(verifier);   
	}
    
	public void init(Object netContainer, Object form) {
		IpssLogger.getLogger().info("NBBranchScPanel init() called");
		_netContainer = (GFormContainer)netContainer;
		_form = (GBranchForm)form;
		_data = _form.getAcscBranchData();
			
		_fromXfrConnectPanel.init(_netContainer, _data.getFromXfrConnectData());	    
		_toXfrConnectPanel.init(_netContainer, _data.getToXfrConnectData());	    
	}
	
	public void disableScripting() {
		scriptRadioButton.setEnabled(false);
	}
	
    public boolean setForm2Editor() {
		IpssLogger.getLogger().info("NBBranchScPanel setForm2Editor() called");
		IpssLogger.getLogger().info("NBBranchScPanel LfCode = " + _data.getLfCode());

		r1TextField.setText(Number2String.toStr(_data.getZR(), "#0.0####"));
	    x1TextField.setText(Number2String.toStr(_data.getZX(), "#0.0####"));

	    r0TextField.setText(Number2String.toStr(_data.getZ0R(), "#0.0####"));
	    x0TextField.setText(Number2String.toStr(_data.getZ0X(), "#0.0####"));

	    if (_data.getLfCode().equals(IGBranchForm.TransBranchLfCode_Xfr) || 
	    	_data.getLfCode().equals(IGBranchForm.TransBranchLfCode_PsXfr)) {
    	    setDataFieldStatus(true);
	    	fromTapTextField.setText(Number2String.toStr(_data.getXfrTapFromSideTap(), "#0.0##"));
	    	toTapTextField.setText(Number2String.toStr(_data.getXfrTapToSideTap(), "#0.0##"));
        	if (_data.getLfCode().equals(IGBranchForm.TransBranchLfCode_Xfr)) {
        		xfrRadioButton.setSelected(true);
        		IpssLogger.getLogger().info("NBBranchScPanel xfrRadioButton.setSelected(true)");
        	    setBTapLabelText(false, false);
        	}    
        	else {
        		psXfrRadioButton.setSelected(true);
        		IpssLogger.getLogger().info("NBBranchScPanel psXfrRadioButton.setSelected(true)");
        	    setBTapLabelText(false, true);
        	    hB1TextField.setText(Number2String.toStr(_data.getPhaseShiftAngle(), "#0.0##"));
        	}    

            xfrFromGroundPanel.add(_fromXfrConnectPanel);
            _fromXfrConnectPanel.setForm2Editor();
            
            xfrToGroundPanel.add(_toXfrConnectPanel);
            _toXfrConnectPanel.setForm2Editor();
    	}
	    else if (_data.getLfCode().equals(IGBranchForm.TransBranchCode_Scripting)) {
    		scriptRadioButton.setSelected(true);
    		IpssLogger.getLogger().info("NBBranchScPanel scriptRadioButton.setSelected(true)");
    	    setDataFieldStatus(false);
    	    
            xfrFromGroundPanel.remove(_fromXfrConnectPanel);
            xfrToGroundPanel.remove(_toXfrConnectPanel);
	    }
    	else { 
    		lineRadioButton.setSelected(true);
    		IpssLogger.getLogger().info("NBBranchScPanel lineRadioButton.setSelected(true)");
    	    setDataFieldStatus(true);
    	    setBTapLabelText(true, false);

    	    hB1TextField.setText(Number2String.toStr(_data.getHalfShuntB(), "#0.0####"));
    	    hB0TextField.setText(Number2String.toStr(_data.getHalfShuntB0(), "#0.0####"));
    	    
            xfrFromGroundPanel.remove(_fromXfrConnectPanel);
            xfrToGroundPanel.remove(_toXfrConnectPanel);
    	}
	    parentDialog.pack();
        return true;
	}

    private void setDataFieldStatus(boolean status) {
    	if (parentPanel instanceof NBAcscTransBranchEditPanel)
    		((NBAcscTransBranchEditPanel)parentPanel).setScriptPanelStatus(!status);
	    fromTapTextField.setEnabled(status);
	    hB0TextField.setEnabled(status);
	    hB1TextField.setEnabled(status);
	    r0TextField.setEnabled(status);
	    r1TextField.setEnabled(status);
	    toTapTextField.setEnabled(status);
	    x0TextField.setEnabled(status);
	    x1TextField.setEnabled(status);

	    fromTapLabel.setEnabled(status);
	    hB0Label.setEnabled(status);
	    hB1Label.setEnabled(status);
	    r0Label.setEnabled(status);
	    r1Label.setEnabled(status);
	    toTapLabel.setEnabled(status);
	    x0Label.setEnabled(status);
	    x1Label.setEnabled(status);
    }
    
    private void setBTapLabelText(boolean isLine, boolean isPsXfr) {
        hB0Label.setEnabled(isLine);
        hB0TextField.setEnabled(isLine);
        hB1Label.setEnabled(isLine || isPsXfr);
        hB1TextField.setEnabled(isLine || isPsXfr);
        if (!isLine && !isPsXfr) {
            hB0TextField.setText("0.0");
            hB1TextField.setText("0.0");
        }
        else if (isPsXfr) {
        	hB1Label.setText("Shift Angle(deg)  ");
            hB0TextField.setText("0.0");
        }
        else {
        	hB1Label.setText("     1/2 B1(pu)  ");
        }
        
        fromTapLabel.setEnabled(!isLine);
        fromTapTextField.setEnabled(!isLine);        
        toTapLabel.setEnabled(!isLine);
        toTapTextField.setEnabled(!isLine);
        if (isLine) {
            fromTapTextField.setText("0.0");        
            toTapTextField.setText("0.0");
        }
    }
    
    public boolean saveEditor2Form(Vector<String> errMsg) throws Exception {
		IpssLogger.getLogger().info("NBBranchScPanel saveEditor2Form() called");

    	if (scriptRadioButton.isSelected()) {
    		_data.setLfCode(IGBranchForm.TransBranchCode_Scripting);
    		// scripting data saved by the parent editing panel
    	}
    	else {
        	if (lineRadioButton.isSelected())
        		_data.setLfCode(IGBranchForm.TransBranchLfCode_Line);
        	else if (xfrRadioButton.isSelected())
        		_data.setLfCode(IGBranchForm.TransBranchLfCode_Xfr);
        	else if (psXfrRadioButton.isSelected())
        		_data.setLfCode(IGBranchForm.TransBranchLfCode_PsXfr);

    		if (SwingInputVerifyUtil.largeEqualThan(r1TextField, 0.0d, errMsg, "R1 < 0.0"))
    			_data.setZR(SwingInputVerifyUtil.getDouble(r1TextField));

    		if (SwingInputVerifyUtil.largeThan(x1TextField, 0.0d, errMsg, "X1 <= 0.0") )
    			_data.setZX(SwingInputVerifyUtil.getDouble(x1TextField));

    		if (SwingInputVerifyUtil.largeEqualThan(r0TextField, 0.0d, errMsg, "R0 < 0.0"))
    			_data.setZ0R(SwingInputVerifyUtil.getDouble(r0TextField));

    		if (SwingInputVerifyUtil.largeEqualThan(x0TextField, 0.0d, errMsg, "X0 <= 0.0"))
    			_data.setZ0X(SwingInputVerifyUtil.getDouble(x0TextField));
    	    
    	    if (_data.getLfCode().equals(IGBranchForm.TransBranchLfCode_Xfr) || 
    		    _data.getLfCode().equals(IGBranchForm.TransBranchLfCode_PsXfr)) {
    			if (SwingInputVerifyUtil.within(fromTapTextField, 0.0d, 2.0d, errMsg, "From tap < 0.0 or > 2.0"))
    				_data.setXfrTapFromSideTap(SwingInputVerifyUtil.getDouble(fromTapTextField));

    			if (SwingInputVerifyUtil.within(toTapTextField, 0.0d, 2.0d, errMsg, "To tap < 0.0 or > 2.0"))
    				_data.setXfrTapToSideTap(SwingInputVerifyUtil.getDouble(toTapTextField));

    			if (_data.getLfCode().equals(IGBranchForm.TransBranchLfCode_PsXfr)) {
        			_data.setPhaseShiftAngle(SwingInputVerifyUtil.getDouble(hB1TextField));
            	}    

                _fromXfrConnectPanel.saveEditor2Form(errMsg);
                _toXfrConnectPanel.saveEditor2Form(errMsg);
        	}
    	    else if (_data.getLfCode().equals(IGBranchForm.TransBranchLfCode_Line)) {
    			if (SwingInputVerifyUtil.largeEqualThan(hB1TextField, 0.0d, errMsg, "1/2B1 < 0.0"))
    				_data.setHalfShuntB(SwingInputVerifyUtil.getDouble(hB1TextField));

    			if (SwingInputVerifyUtil.largeEqualThan(hB0TextField, 0.0d, errMsg, "1/2B0 < 0.0"))
    				_data.setHalfShuntB0(SwingInputVerifyUtil.getDouble(hB0TextField));
        	}
    	}

	    return errMsg.size() == 0;
    }
    
    /** Creates new form AclfEditPanel */
    public NBBranchScDataPanel() {
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

        branchTypeButtonGroup = new javax.swing.ButtonGroup();
        branchTypePanel = new javax.swing.JPanel();
        lineRadioButton = new javax.swing.JRadioButton();
        xfrRadioButton = new javax.swing.JRadioButton();
        psXfrRadioButton = new javax.swing.JRadioButton();
        scriptRadioButton = new javax.swing.JRadioButton();
        branchScInfoPanel = new javax.swing.JPanel();
        r1Label = new javax.swing.JLabel();
        r1TextField = new javax.swing.JTextField();
        x1Label = new javax.swing.JLabel();
        x1TextField = new javax.swing.JTextField();
        r0Label = new javax.swing.JLabel();
        r0TextField = new javax.swing.JTextField();
        x0Label = new javax.swing.JLabel();
        x0TextField = new javax.swing.JTextField();
        hB1Label = new javax.swing.JLabel();
        hB1TextField = new javax.swing.JTextField();
        hB0Label = new javax.swing.JLabel();
        hB0TextField = new javax.swing.JTextField();
        fromTapLabel = new javax.swing.JLabel();
        fromTapTextField = new javax.swing.JTextField();
        toTapLabel = new javax.swing.JLabel();
        toTapTextField = new javax.swing.JTextField();
        xfrFromGroundPanel = new javax.swing.JPanel();
        xfrToGroundPanel = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        branchTypePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Branch Type", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10)));
        branchTypePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 15, 5));

        branchTypeButtonGroup.add(lineRadioButton);
        lineRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        lineRadioButton.setSelected(true);
        lineRadioButton.setText("Line");
        lineRadioButton.setName("lineRadioButton"); // NOI18N
        lineRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lineRadioButtonActionPerformed(evt);
            }
        });
        branchTypePanel.add(lineRadioButton);

        branchTypeButtonGroup.add(xfrRadioButton);
        xfrRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        xfrRadioButton.setText("XFormer");
        xfrRadioButton.setName("xfrRadioButton"); // NOI18N
        xfrRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xfrRadioButtonActionPerformed(evt);
            }
        });
        branchTypePanel.add(xfrRadioButton);

        branchTypeButtonGroup.add(psXfrRadioButton);
        psXfrRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        psXfrRadioButton.setText("PhaseShift-Xfr");
        psXfrRadioButton.setName("psXfrRadioButton"); // NOI18N
        psXfrRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                psXfrRadioButtonActionPerformed(evt);
            }
        });
        branchTypePanel.add(psXfrRadioButton);

        branchTypeButtonGroup.add(scriptRadioButton);
        scriptRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        scriptRadioButton.setText("Scripting");
        scriptRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        scriptRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        scriptRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scriptRadioButtonActionPerformed(evt);
            }
        });
        branchTypePanel.add(scriptRadioButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        add(branchTypePanel, gridBagConstraints);

        branchScInfoPanel.setLayout(new java.awt.GridBagLayout());

        r1Label.setFont(new java.awt.Font("Dialog", 0, 12));
        r1Label.setText("     R1(pu)  ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        branchScInfoPanel.add(r1Label, gridBagConstraints);

        r1TextField.setColumns(8);
        r1TextField.setFont(new java.awt.Font("Dialog", 0, 12));
        r1TextField.setText("0.0");
        r1TextField.setName("r1TextField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        branchScInfoPanel.add(r1TextField, gridBagConstraints);

        x1Label.setFont(new java.awt.Font("Dialog", 0, 12));
        x1Label.setText("          X1(pu)  ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        branchScInfoPanel.add(x1Label, gridBagConstraints);

        x1TextField.setColumns(8);
        x1TextField.setFont(new java.awt.Font("Dialog", 0, 12));
        x1TextField.setText("0.0");
        x1TextField.setName("x1TextField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 20);
        branchScInfoPanel.add(x1TextField, gridBagConstraints);

        r0Label.setFont(new java.awt.Font("Dialog", 0, 12));
        r0Label.setText("     R0(pu)  ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        branchScInfoPanel.add(r0Label, gridBagConstraints);

        r0TextField.setColumns(8);
        r0TextField.setFont(new java.awt.Font("Dialog", 0, 12));
        r0TextField.setText("0.0");
        r0TextField.setName("r0TextField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        branchScInfoPanel.add(r0TextField, gridBagConstraints);

        x0Label.setFont(new java.awt.Font("Dialog", 0, 12));
        x0Label.setText("          X0(pu)  ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        branchScInfoPanel.add(x0Label, gridBagConstraints);

        x0TextField.setColumns(8);
        x0TextField.setFont(new java.awt.Font("Dialog", 0, 12));
        x0TextField.setText("0.0");
        x0TextField.setName("x0TextField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 20);
        branchScInfoPanel.add(x0TextField, gridBagConstraints);

        hB1Label.setFont(new java.awt.Font("Dialog", 0, 12));
        hB1Label.setText("     1/2 B1(pu)  ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        branchScInfoPanel.add(hB1Label, gridBagConstraints);

        hB1TextField.setColumns(8);
        hB1TextField.setFont(new java.awt.Font("Dialog", 0, 12));
        hB1TextField.setText("0.0");
        hB1TextField.setName("hB1TextField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        branchScInfoPanel.add(hB1TextField, gridBagConstraints);

        hB0Label.setFont(new java.awt.Font("Dialog", 0, 12));
        hB0Label.setText("          1/2 B0(pu)  ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        branchScInfoPanel.add(hB0Label, gridBagConstraints);

        hB0TextField.setColumns(8);
        hB0TextField.setFont(new java.awt.Font("Dialog", 0, 12));
        hB0TextField.setText("0.0");
        hB0TextField.setName("hB0TextField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 20);
        branchScInfoPanel.add(hB0TextField, gridBagConstraints);

        fromTapLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        fromTapLabel.setText("     From TurnRatio(pu)  ");
        fromTapLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 10, 0);
        branchScInfoPanel.add(fromTapLabel, gridBagConstraints);

        fromTapTextField.setColumns(8);
        fromTapTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        fromTapTextField.setText("0.0");
        fromTapTextField.setEnabled(false);
        fromTapTextField.setName("fromTapTextField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 10, 0);
        branchScInfoPanel.add(fromTapTextField, gridBagConstraints);

        toTapLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        toTapLabel.setText("          To TurnRatio(pu)  ");
        toTapLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 10, 0);
        branchScInfoPanel.add(toTapLabel, gridBagConstraints);

        toTapTextField.setColumns(8);
        toTapTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        toTapTextField.setText("0.0");
        toTapTextField.setAutoscrolls(false);
        toTapTextField.setEnabled(false);
        toTapTextField.setName("toTapTextField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 10, 20);
        branchScInfoPanel.add(toTapTextField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        add(branchScInfoPanel, gridBagConstraints);

        xfrFromGroundPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        add(xfrFromGroundPanel, gridBagConstraints);

        xfrToGroundPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        add(xfrToGroundPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

private void scriptRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scriptRadioButtonActionPerformed
	IpssLogger.getLogger().info("NBBranchScDataPanel.scriptRadioButtonActionPerformed()");
	_data.setLfCode(IGBranchForm.TransBranchCode_Scripting);
	setForm2Editor();
}//GEN-LAST:event_scriptRadioButtonActionPerformed

    private void psXfrRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_psXfrRadioButtonActionPerformed
    	_data.setLfCode(IGBranchForm.TransBranchLfCode_PsXfr);
    	setForm2Editor();
    }//GEN-LAST:event_psXfrRadioButtonActionPerformed

    private void xfrRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xfrRadioButtonActionPerformed
    	_data.setLfCode(IGBranchForm.TransBranchLfCode_Xfr);
    	setForm2Editor();
    }//GEN-LAST:event_xfrRadioButtonActionPerformed

    private void lineRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lineRadioButtonActionPerformed
    	_data.setLfCode(IGBranchForm.TransBranchLfCode_Line);
    	setForm2Editor();
    }//GEN-LAST:event_lineRadioButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel branchScInfoPanel;
    private javax.swing.ButtonGroup branchTypeButtonGroup;
    private javax.swing.JPanel branchTypePanel;
    private javax.swing.JLabel fromTapLabel;
    private javax.swing.JTextField fromTapTextField;
    private javax.swing.JLabel hB0Label;
    private javax.swing.JTextField hB0TextField;
    private javax.swing.JLabel hB1Label;
    private javax.swing.JTextField hB1TextField;
    private javax.swing.JRadioButton lineRadioButton;
    private javax.swing.JRadioButton psXfrRadioButton;
    private javax.swing.JLabel r0Label;
    private javax.swing.JTextField r0TextField;
    private javax.swing.JLabel r1Label;
    private javax.swing.JTextField r1TextField;
    private javax.swing.JRadioButton scriptRadioButton;
    private javax.swing.JLabel toTapLabel;
    private javax.swing.JTextField toTapTextField;
    private javax.swing.JLabel x0Label;
    private javax.swing.JTextField x0TextField;
    private javax.swing.JLabel x1Label;
    private javax.swing.JTextField x1TextField;
    private javax.swing.JPanel xfrFromGroundPanel;
    private javax.swing.JRadioButton xfrRadioButton;
    private javax.swing.JPanel xfrToGroundPanel;
    // End of variables declaration//GEN-END:variables
 
	class DataVerifier extends javax.swing.InputVerifier {
    	public boolean verify(javax.swing.JComponent input) {
			if (input == null)
				return false;
       		try {
               if ( input == r0TextField ||
          		    input == r1TextField ||
               		input == hB0TextField )
 	       			return SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) >= 0.0;
               if ( input == fromTapTextField ||
          		    input == toTapTextField ||
          		    input == x1TextField ||
          		    input == x0TextField )
 	       			return SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) > 0.0;
 	       	} catch (Exception e) {
 	    		return false;
 	       	}		
			return true;
        }
    }
}	
