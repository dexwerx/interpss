 /*
  * @(#)NBXformerInputPanel.java   
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

package org.interpss.editor.ui.edit.dist.branch;

import java.util.Vector;

import javax.swing.JDialog;

import org.interpss.editor.data.dist.DistBranchData;
import org.interpss.editor.form.GBranchForm;
import org.interpss.editor.form.GFormContainer;
import org.interpss.editor.jgraph.ui.edit.IFormDataDialog;
import org.interpss.editor.jgraph.ui.edit.IFormDataPanel;
import org.interpss.editor.ui.edit.common.NBGConnectionPanel;
import org.interpss.editor.ui.util.NetDataUtil;
import org.interpss.numeric.util.Number2String;
import org.interpss.spring.UISpringFactory;
import org.interpss.ui.SwingInputVerifyUtil;
   

public class NBXformerInputPanel extends javax.swing.JPanel implements IFormDataPanel {
	private static final long serialVersionUID = 1;
	private JDialog parent = null;

	private GFormContainer netContainer = null;
	private GBranchForm  _form = null;
    private DistBranchData  _data = null;

    private static NBGConnectionPanel fromXfrConPanel = new NBGConnectionPanel();
    private static NBGConnectionPanel toXfrConPanel = new NBGConnectionPanel();

	public void initPanel(JDialog aParent) {
		parent = aParent;
		
	    fromSideGPanel.add(fromXfrConPanel, java.awt.BorderLayout.NORTH);
		fromXfrConPanel.initPanel(parent, "Primary Grounding");

	    toSideGPanel.add(toXfrConPanel, java.awt.BorderLayout.SOUTH);
		toXfrConPanel.initPanel(parent, "Secondary Grounding");

		DataVerifier verifier = new DataVerifier();
	    this.xfrRatingField.setInputVerifier(verifier);
	    this.fromRatedVTextField.setInputVerifier(verifier);
	    this.toRatedVTextField.setInputVerifier(verifier);
	    this.xField.setInputVerifier(verifier);
	    this.rField.setInputVerifier(verifier);
	    this.x_rField.setInputVerifier(verifier);
	    this.x0_x1Field.setInputVerifier(verifier);
	    this.r0_r1Field.setInputVerifier(verifier);
	    this.fromTapField.setInputVerifier(verifier);
	    this.toTapField.setInputVerifier(verifier);
	}

	public void init(Object aNetContainer, Object form) {
		netContainer = (GFormContainer)aNetContainer;
		_form = (GBranchForm)form;
		_data = _form.getDistBranchData();
      
        fromXfrConPanel.init(netContainer, _data.getFromXfrConnectData());
		toXfrConPanel.init(netContainer, _data.getToXfrConnectData());
	}
	
    public boolean setForm2Editor() {
        this.xfrNameComboBox.setSelectedItem(_form.getName());
        this.ratingUnitComboBox.setSelectedItem(_data.getXfrRatingUnit());
        this.zUnitComboBox.setSelectedItem(_data.getZUnit());
        this.tapUnitComboBox.setSelectedItem(_data.getXfrTapUnit());

        this.fromRatedVTextField.setText(Number2String.toStr(_data.getFromRatedVolt(), "#0.0#"));
        this.toRatedVTextField.setText(Number2String.toStr(_data.getToRatedVolt(), "#0.0#"));
        this.voltUnitComboBox.setSelectedItem(_data.getRatedVoltUnit());

        this.xfrRatingField.setText(Number2String.toStr(_data.getXfrRating(), "#0.0#"));
        this.xField.setText(Number2String.toStr(_data.getZX(), "#0.0####"));
        this.rField.setText(Number2String.toStr(_data.getZR(), "#0.0####"));
        
        double x_r = NetDataUtil.ratio(_data.getZX(), _data.getZR()),
				   x0_x1 = NetDataUtil.ratio(_data.getZ0X(), _data.getZX()),
				   r0_r1 = NetDataUtil.ratio(_data.getZ0R(), _data.getZR());
        this.x_rField.setText(Number2String.toStr(x_r, "#0.0##"));
        this.x0_x1Field.setText(Number2String.toStr(x0_x1, "#0.0##"));
        this.r0_r1Field.setText(Number2String.toStr(r0_r1, "#0.0##"));

        this.fromTapField.setText(Number2String.toStr(_data.getXfrTapFromSideTap(), "#.0##"));
        this.toTapField.setText(Number2String.toStr(_data.getXfrTapToSideTap(), "#.0##"));

        fromXfrConPanel.setForm2Editor();
        toXfrConPanel.setForm2Editor();

        includeSScheduleCheckBox.setSelected(_data.isHasServiceSchedule());

        return true;
	}
    
    public boolean saveEditor2Form(Vector errMsg) throws Exception {
        boolean ok = true;

        _form.setName((String)this.xfrNameComboBox.getSelectedItem());
        _data.setXfrRatingUnit((String)this.ratingUnitComboBox.getSelectedItem());
        _data.setRatedVoltUnit((String)this.voltUnitComboBox.getSelectedItem());
        _data.setZUnit((String)this.zUnitComboBox.getSelectedItem());
        _data.setZ0Unit((String)this.zUnitComboBox.getSelectedItem());
        _data.setXfrTapUnit((String)this.tapUnitComboBox.getSelectedItem());

        if (!SwingInputVerifyUtil.largeThan(this.xfrRatingField, 0.0d)) {
            errMsg.add("Transformer rating <= 0.0");
            ok = false;
        }
        _data.setXfrRating(SwingInputVerifyUtil.getDouble(this.xfrRatingField));

        if (!SwingInputVerifyUtil.largeThan(this.fromRatedVTextField, 0.0d)) {
            errMsg.add("Transformer from side rated voltage <= 0.0");
            ok = false;
        }
        _data.setFromRatedVolt(SwingInputVerifyUtil.getDouble(this.fromRatedVTextField));

        if (!SwingInputVerifyUtil.largeThan(this.toRatedVTextField, 0.0d)) {
            errMsg.add("Transformer to side rated voltage <= 0.0");
            ok = false;
        }
        _data.setToRatedVolt(SwingInputVerifyUtil.getDouble(this.toRatedVTextField));

        if (!SwingInputVerifyUtil.largeThan(this.xField, 0.0d)) {
            errMsg.add("Transformer X <= 0.0");
            ok = false;
        }
        _data.setZX(SwingInputVerifyUtil.getDouble(this.xField));

        if (!SwingInputVerifyUtil.largeEqualThan(this.rField, 0.0d)) {
            errMsg.add("Transformer R < 0.0");
            ok = false;
        }
        _data.setZR(SwingInputVerifyUtil.getDouble(this.rField));

        if (!SwingInputVerifyUtil.largeThan(this.x0_x1Field, 0.0d)) {
            errMsg.add("Transformer x0/x1 <= 0.0");
            ok = false;
        }
        _data.setZ0X(NetDataUtil.calValue(_data.getZX(),
    					SwingInputVerifyUtil.getDouble(this.x0_x1Field)));

        if (!SwingInputVerifyUtil.largeEqualThan(this.r0_r1Field, 0.0d)) {
            errMsg.add("Transformer r0/r1 < 0.0");
            ok = false;
        }
        _data.setZ0R(NetDataUtil.calValue(_data.getZR(), SwingInputVerifyUtil.getDouble(this.r0_r1Field)));

        if (!SwingInputVerifyUtil.largeThan(this.fromTapField, 0.0d)) {
            errMsg.add("Transformer to side tap <= 0.0");
            ok = false;
        }
        _data.setXfrTapFromSideTap(SwingInputVerifyUtil.getDouble(this.fromTapField));

        if (!SwingInputVerifyUtil.largeThan(this.toTapField, 0.0d)) {
            errMsg.add("Transformer to side tap <= 0.0");
            ok = false;
        }
        _data.setXfrTapToSideTap(SwingInputVerifyUtil.getDouble(this.toTapField));

        if (!((IFormDataPanel)fromXfrConPanel).saveEditor2Form(errMsg))
            ok = false;

        if (!((IFormDataPanel)toXfrConPanel).saveEditor2Form(errMsg))
            ok = false;

    	_data.setHasServiceSchedule(includeSScheduleCheckBox.isSelected());

    	return ok;
    }

	// The following code is controlled by NetBean IDE
	// ===============================================
    
    /** Creates new form NBXformerInputPanel */
    public NBXformerInputPanel() {
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

        paramPanel = new javax.swing.JPanel();
        nameLabel = new javax.swing.JLabel();
        xfrNameComboBox = new javax.swing.JComboBox();
        ratingLabel = new javax.swing.JLabel();
        xfrRatingField = new javax.swing.JTextField();
        ratingUnitComboBox = new javax.swing.JComboBox();
        fromRatedVLabel = new javax.swing.JLabel();
        fromRatedVTextField = new javax.swing.JTextField();
        toRatedVLabel = new javax.swing.JLabel();
        toRatedVTextField = new javax.swing.JTextField();
        voltUnitComboBox = new javax.swing.JComboBox();
        xLabel = new javax.swing.JLabel();
        xField = new javax.swing.JTextField();
        rLabel = new javax.swing.JLabel();
        rField = new javax.swing.JTextField();
        zUnitComboBox = new javax.swing.JComboBox();
        x_rLabel = new javax.swing.JLabel();
        x_rField = new javax.swing.JTextField();
        x0_x1Label = new javax.swing.JLabel();
        x0_x1Field = new javax.swing.JTextField();
        r0_r1Label = new javax.swing.JLabel();
        r0_r1Field = new javax.swing.JTextField();
        fromTapLabel = new javax.swing.JLabel();
        fromTapField = new javax.swing.JTextField();
        toTapLabel = new javax.swing.JLabel();
        toTapField = new javax.swing.JTextField();
        tapUnitComboBox = new javax.swing.JComboBox();
        includeSScheduleCheckBox = new javax.swing.JCheckBox();
        serviceScheduleButton = new javax.swing.JButton();
        groundPanel = new javax.swing.JPanel();
        fromSideGPanel = new javax.swing.JPanel();
        toSideGPanel = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        paramPanel.setLayout(new java.awt.GridBagLayout());

        nameLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        nameLabel.setText("Xformer Name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 5);
        paramPanel.add(nameLabel, gridBagConstraints);

        xfrNameComboBox.setEditable(true);
        xfrNameComboBox.setFont(new java.awt.Font("Dialog", 0, 10));
        xfrNameComboBox.setName("xfrNameComboBox");
        paramPanel.add(xfrNameComboBox, new java.awt.GridBagConstraints());

        ratingLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        ratingLabel.setText("     Rating");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 5);
        paramPanel.add(ratingLabel, gridBagConstraints);

        xfrRatingField.setColumns(8);
        xfrRatingField.setFont(new java.awt.Font("Dialog", 0, 12));
        xfrRatingField.setText("0.0");
        xfrRatingField.setName("xfrRatingTextField");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        paramPanel.add(xfrRatingField, gridBagConstraints);

        ratingUnitComboBox.setFont(new java.awt.Font("Dialog", 0, 10));
        ratingUnitComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "MVA", "KVA" }));
        ratingUnitComboBox.setName("ratingUnitComboBox");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        paramPanel.add(ratingUnitComboBox, gridBagConstraints);

        fromRatedVLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        fromRatedVLabel.setText("Primary Rated V");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 5);
        paramPanel.add(fromRatedVLabel, gridBagConstraints);

        fromRatedVTextField.setColumns(8);
        fromRatedVTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        fromRatedVTextField.setText(" ");
        fromRatedVTextField.setName("fromRatedVTextField");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        paramPanel.add(fromRatedVTextField, gridBagConstraints);

        toRatedVLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        toRatedVLabel.setText("Secondary");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 5);
        paramPanel.add(toRatedVLabel, gridBagConstraints);

        toRatedVTextField.setColumns(8);
        toRatedVTextField.setFont(new java.awt.Font("Dialog", 0, 12));
        toRatedVTextField.setName("toRatedVTextField");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        paramPanel.add(toRatedVTextField, gridBagConstraints);

        voltUnitComboBox.setFont(new java.awt.Font("Dialog", 0, 10));
        voltUnitComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Volt", "KV" }));
        voltUnitComboBox.setName("voltUnitComboBox");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        paramPanel.add(voltUnitComboBox, gridBagConstraints);

        xLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        xLabel.setText("X");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 5);
        paramPanel.add(xLabel, gridBagConstraints);

        xField.setColumns(8);
        xField.setFont(new java.awt.Font("Dialog", 0, 12));
        xField.setText("0.0");
        xField.setName("xTextField");
        xField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                xFieldActionHandler(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        paramPanel.add(xField, gridBagConstraints);

        rLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        rLabel.setText("R");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 30, 0, 5);
        paramPanel.add(rLabel, gridBagConstraints);

        rField.setColumns(8);
        rField.setFont(new java.awt.Font("Dialog", 0, 12));
        rField.setText("0.0");
        rField.setName("rTextField");
        rField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                rFieldActionHandler(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        paramPanel.add(rField, gridBagConstraints);

        zUnitComboBox.setFont(new java.awt.Font("Dialog", 0, 10));
        zUnitComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "%", "PU" }));
        zUnitComboBox.setName("zUnitComboBox");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        paramPanel.add(zUnitComboBox, gridBagConstraints);

        x_rLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        x_rLabel.setText("X/R");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 5);
        paramPanel.add(x_rLabel, gridBagConstraints);

        x_rField.setColumns(4);
        x_rField.setFont(new java.awt.Font("Dialog", 0, 12));
        x_rField.setText("20");
        x_rField.setName("x_rTextField");
        x_rField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                x_rFieldActionHandler(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        paramPanel.add(x_rField, gridBagConstraints);

        x0_x1Label.setFont(new java.awt.Font("Dialog", 0, 12));
        x0_x1Label.setText("X0/X1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 5);
        paramPanel.add(x0_x1Label, gridBagConstraints);

        x0_x1Field.setColumns(4);
        x0_x1Field.setFont(new java.awt.Font("Dialog", 0, 12));
        x0_x1Field.setText("1.0");
        x0_x1Field.setName("x0_x1TextField");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        paramPanel.add(x0_x1Field, gridBagConstraints);

        r0_r1Label.setFont(new java.awt.Font("Dialog", 0, 12));
        r0_r1Label.setText("R0/R1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        paramPanel.add(r0_r1Label, gridBagConstraints);

        r0_r1Field.setColumns(4);
        r0_r1Field.setFont(new java.awt.Font("Dialog", 0, 12));
        r0_r1Field.setText("0.0");
        r0_r1Field.setName("r0_r1TextField");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        paramPanel.add(r0_r1Field, gridBagConstraints);

        fromTapLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        fromTapLabel.setText("Primary TurnRatio");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 5);
        paramPanel.add(fromTapLabel, gridBagConstraints);

        fromTapField.setColumns(4);
        fromTapField.setFont(new java.awt.Font("Dialog", 0, 12));
        fromTapField.setText("1.0");
        fromTapField.setName("fromTapTextField");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        paramPanel.add(fromTapField, gridBagConstraints);

        toTapLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        toTapLabel.setText("Secondary");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        paramPanel.add(toTapLabel, gridBagConstraints);

        toTapField.setColumns(4);
        toTapField.setFont(new java.awt.Font("Dialog", 0, 12));
        toTapField.setText("1.0");
        toTapField.setName("toTapTextField");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        paramPanel.add(toTapField, gridBagConstraints);

        tapUnitComboBox.setFont(new java.awt.Font("Dialog", 0, 10));
        tapUnitComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "PU", "%" }));
        tapUnitComboBox.setName("tapUnitComboBox");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        paramPanel.add(tapUnitComboBox, gridBagConstraints);

        includeSScheduleCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        includeSScheduleCheckBox.setText("Include Service Schedule");
        includeSScheduleCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        includeSScheduleCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        includeSScheduleCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                includeSScheduleCheckBoxItemStateChanged(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        paramPanel.add(includeSScheduleCheckBox, gridBagConstraints);

        serviceScheduleButton.setFont(new java.awt.Font("Dialog", 0, 10));
        serviceScheduleButton.setText("Service Schedule ...");
        serviceScheduleButton.setEnabled(false);
        serviceScheduleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                serviceScheduleButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        paramPanel.add(serviceScheduleButton, gridBagConstraints);

        add(paramPanel, java.awt.BorderLayout.NORTH);

        groundPanel.setLayout(new java.awt.BorderLayout());

        groundPanel.add(fromSideGPanel, java.awt.BorderLayout.NORTH);

        groundPanel.add(toSideGPanel, java.awt.BorderLayout.SOUTH);

        add(groundPanel, java.awt.BorderLayout.SOUTH);

    }
    // </editor-fold>//GEN-END:initComponents

    private void serviceScheduleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_serviceScheduleButtonActionPerformed
    	IFormDataDialog dialog = UISpringFactory.getServiceScheduleDialog();
    	dialog.init(netContainer, _data);
    }//GEN-LAST:event_serviceScheduleButtonActionPerformed

    private void includeSScheduleCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_includeSScheduleCheckBoxItemStateChanged
    	if (includeSScheduleCheckBox.isSelected())
    		serviceScheduleButton.setEnabled(true);
    	else
    		serviceScheduleButton.setEnabled(false);
    }//GEN-LAST:event_includeSScheduleCheckBoxItemStateChanged

    private void x_rFieldActionHandler(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_x_rFieldActionHandler
    	try {
    		double x = SwingInputVerifyUtil.getDouble(this.xField);
			double r = NetDataUtil.calValue(x, 1.0/SwingInputVerifyUtil.getDouble(this.x_rField));
        	this.rField.setText(Number2String.toStr(r, "#0.0####"));
        } catch (Exception e) {}	
    }//GEN-LAST:event_x_rFieldActionHandler

    private void rFieldActionHandler(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_rFieldActionHandler
		try {
			double x_r = NetDataUtil.ratio(SwingInputVerifyUtil.getDouble(this.xField), 
						SwingInputVerifyUtil.getDouble(this.rField));
        	this.x_rField.setText(Number2String.toStr(x_r, "#0.0##"));
        } catch (Exception e) {}	
    }//GEN-LAST:event_rFieldActionHandler

    private void xFieldActionHandler(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_xFieldActionHandler
    	rFieldActionHandler(evt);
    }//GEN-LAST:event_xFieldActionHandler
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel fromRatedVLabel;
    private javax.swing.JTextField fromRatedVTextField;
    private javax.swing.JPanel fromSideGPanel;
    private javax.swing.JTextField fromTapField;
    private javax.swing.JLabel fromTapLabel;
    private javax.swing.JPanel groundPanel;
    private javax.swing.JCheckBox includeSScheduleCheckBox;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JPanel paramPanel;
    private javax.swing.JTextField r0_r1Field;
    private javax.swing.JLabel r0_r1Label;
    private javax.swing.JTextField rField;
    private javax.swing.JLabel rLabel;
    private javax.swing.JLabel ratingLabel;
    private javax.swing.JComboBox ratingUnitComboBox;
    private javax.swing.JButton serviceScheduleButton;
    private javax.swing.JComboBox tapUnitComboBox;
    private javax.swing.JLabel toRatedVLabel;
    private javax.swing.JTextField toRatedVTextField;
    private javax.swing.JPanel toSideGPanel;
    private javax.swing.JTextField toTapField;
    private javax.swing.JLabel toTapLabel;
    private javax.swing.JComboBox voltUnitComboBox;
    private javax.swing.JTextField x0_x1Field;
    private javax.swing.JLabel x0_x1Label;
    private javax.swing.JTextField xField;
    private javax.swing.JLabel xLabel;
    private javax.swing.JTextField x_rField;
    private javax.swing.JLabel x_rLabel;
    private javax.swing.JComboBox xfrNameComboBox;
    private javax.swing.JTextField xfrRatingField;
    private javax.swing.JComboBox zUnitComboBox;
    // End of variables declaration//GEN-END:variables

 	class DataVerifier extends javax.swing.InputVerifier {
    	public boolean verify(javax.swing.JComponent input) {
			if (input == null)
				return false;
       		try {
       			if (input == xfrRatingField ||
                   input == fromRatedVTextField ||
                   input == toRatedVTextField ||
       			    input == xField ||
       			    input == x_rField ||
       			    input == fromTapField ||
       			    input == toTapField )
 	       			return SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) > 0.0;
				else if (input == xfrRatingField ||
					input == rField ||
					input == x0_x1Field ||
					input == r0_r1Field )
 	       			return SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) >= 0.0;
 	       	} catch (Exception e) {
 	    		return false;
 	       	}		
			return true;
        }
    }
}
