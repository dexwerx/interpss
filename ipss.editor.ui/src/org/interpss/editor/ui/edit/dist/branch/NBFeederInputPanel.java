 /*
  * @(#)NBFeederInputPanel.java   
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

import org.interpss.editor.data.dist.DistBranchData;
import org.interpss.editor.form.GBranchForm;
import org.interpss.editor.jgraph.ui.edit.IFormDataPanel;
import org.interpss.editor.ui.util.NetDataUtil;
import org.interpss.numeric.util.Number2String;
import org.interpss.ui.SwingInputVerifyUtil;
   

public class NBFeederInputPanel extends javax.swing.JPanel implements IFormDataPanel {
	private static final long serialVersionUID = 1;

	private GBranchForm  _form = null;
    private DistBranchData  _data = null;

	public void initPanel() {
		DataVerifier verifier = new DataVerifier();
    	this.lengthField.setInputVerifier(verifier);
    	this.x_1000Field.setInputVerifier(verifier);
    	this.r_1000Field.setInputVerifier(verifier);
    	this.x0_x1Field.setInputVerifier(verifier);
    	this.r0_r1Field.setInputVerifier(verifier);
    	this.b1_1000Field.setInputVerifier(verifier);
    	this.b0_b1Field.setInputVerifier(verifier);
	}

	public void init(Object netContainer, Object form) {
		_form = (GBranchForm)form;
		_data = _form.getDistBranchData();
	}
	
    public boolean setForm2Editor() {
    	this.nameComboBox.setSelectedItem(_form.getName());
    	this.lengthField.setText(Number2String.toStr(_data.getLength(), "#.0"));
    	this.lengthUnitComboBox.setSelectedItem(_data.getLengthUnit());
    	this.x_1000Field.setText(Number2String.toStr(_data.getZX(), "#0.0####"));
    	this.r_1000Field.setText(Number2String.toStr(_data.getZR(), "#0.0####"));
    	this.b1_1000Field.setText(Number2String.toStr(2.0*_data.getHalfShuntB(), "#0.0####"));
		double 	x0_x1 = NetDataUtil.ratio(_data.getZ0X(), _data.getZX()),
				r0_r1 = NetDataUtil.ratio(_data.getZ0R(), _data.getZR()),
				b0_b1 = NetDataUtil.ratio(_data.getHalfShuntB0(), _data.getHalfShuntB());
    	this.x0_x1Field.setText(Number2String.toStr(x0_x1, "#0.0##"));
    	this.r0_r1Field.setText(Number2String.toStr(r0_r1, "#0.0##"));
    	this.b0_b1Field.setText(Number2String.toStr(b0_b1, "#0.0##"));
    	return true;
	}
    
    public boolean saveEditor2Form(Vector errMsg) throws Exception {
		boolean ok = true;
		
		_form.setName((String)this.nameComboBox.getSelectedItem());
		_data.setLengthUnit((String)this.lengthUnitComboBox.getSelectedItem());
		
		if (!SwingInputVerifyUtil.largeThan(this.lengthField, 0.0d)) {
			errMsg.add("Feeder length <= 0.0");
			ok = false;
		}
    	_data.setLength(SwingInputVerifyUtil.getDouble(this.lengthField));

		if (!SwingInputVerifyUtil.largeThan(this.x_1000Field, 0.0d)) {
			errMsg.add("Feeder x/1000 <= 0.0");
			ok = false;
		}
    	_data.setZX(SwingInputVerifyUtil.getDouble(this.x_1000Field));

		if (!SwingInputVerifyUtil.largeEqualThan(this.r_1000Field, 0.0d)) {
			errMsg.add("Feeder r/1000 < 0.0");
			ok = false;
		}
    	_data.setZR(SwingInputVerifyUtil.getDouble(this.r_1000Field));

		if (!SwingInputVerifyUtil.largeEqualThan(this.b1_1000Field, 0.0d)) {
			errMsg.add("Feeder r/1000 < 0.0");
			ok = false;
		}
    	_data.setHalfShuntB(0.5*SwingInputVerifyUtil.getDouble(this.b1_1000Field));

		if (!SwingInputVerifyUtil.largeEqualThan(this.x0_x1Field, 0.0d)) {
			errMsg.add("Feeder x0/x1 < 0.0");
			ok = false;
		}
    	_data.setZ0X(NetDataUtil.calValue(_data.getZX(),
    					SwingInputVerifyUtil.getDouble(this.x0_x1Field)));

		if (!SwingInputVerifyUtil.largeEqualThan(this.r0_r1Field, 0.0d)) {
			errMsg.add("Feeder r0/r1 < 0.0");
			ok = false;
		}
    	_data.setZ0R(NetDataUtil.calValue(_data.getZR(),
    					SwingInputVerifyUtil.getDouble(this.r0_r1Field)));

		if (!SwingInputVerifyUtil.largeEqualThan(this.b0_b1Field, 0.0d)) {
			errMsg.add("Feeder b0/b1 < 0.0");
			ok = false;
		}
    	_data.setHalfShuntB0(NetDataUtil.calValue(_data.getHalfShuntB(),
    					SwingInputVerifyUtil.getDouble(this.b0_b1Field)));

		return ok;
    }

	// The following code is controlled by NetBean IDE
	// ===============================================
    
    /** Creates new form NBFeederInputPanel */
    public NBFeederInputPanel() {
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

        nameLabel = new javax.swing.JLabel();
        nameComboBox = new javax.swing.JComboBox();
        lengthLabel = new javax.swing.JLabel();
        lengthField = new javax.swing.JTextField();
        lengthUnitComboBox = new javax.swing.JComboBox();
        x_1000Label = new javax.swing.JLabel();
        x_1000Field = new javax.swing.JTextField();
        r_1000Label = new javax.swing.JLabel();
        r_1000Field = new javax.swing.JTextField();
        zUnitLabel = new javax.swing.JLabel();
        x0_x1Label = new javax.swing.JLabel();
        x0_x1Field = new javax.swing.JTextField();
        r0_r1Label = new javax.swing.JLabel();
        r0_r1Field = new javax.swing.JTextField();
        b1_1000Label = new javax.swing.JLabel();
        b1_1000Field = new javax.swing.JTextField();
        b0_1000Label = new javax.swing.JLabel();
        b0_b1Field = new javax.swing.JTextField();
        bUnitLabel = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        nameLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        nameLabel.setText("Feeder Name  ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(nameLabel, gridBagConstraints);

        nameComboBox.setEditable(true);
        nameComboBox.setFont(new java.awt.Font("Dialog", 0, 10));
        nameComboBox.setName("nameComboBox");
        add(nameComboBox, new java.awt.GridBagConstraints());

        lengthLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        lengthLabel.setText("Length");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 5);
        add(lengthLabel, gridBagConstraints);

        lengthField.setColumns(8);
        lengthField.setText("1000");
        lengthField.setName("lengthTextField");
        add(lengthField, new java.awt.GridBagConstraints());

        lengthUnitComboBox.setFont(new java.awt.Font("Dialog", 0, 10));
        lengthUnitComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Ft", "M" }));
        lengthUnitComboBox.setName("lengthUnitComboBox");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        add(lengthUnitComboBox, gridBagConstraints);

        x_1000Label.setFont(new java.awt.Font("Dialog", 0, 12));
        x_1000Label.setText("X/1000");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        add(x_1000Label, gridBagConstraints);

        x_1000Field.setColumns(8);
        x_1000Field.setText("0.0");
        x_1000Field.setName("x_1000TextField");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(x_1000Field, gridBagConstraints);

        r_1000Label.setFont(new java.awt.Font("Dialog", 0, 12));
        r_1000Label.setText("R/1000");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 5);
        add(r_1000Label, gridBagConstraints);

        r_1000Field.setColumns(8);
        r_1000Field.setText("0.0");
        r_1000Field.setName("r_1000TextField");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        add(r_1000Field, gridBagConstraints);

        zUnitLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        zUnitLabel.setText("Ohms/1000");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(zUnitLabel, gridBagConstraints);

        x0_x1Label.setFont(new java.awt.Font("Dialog", 0, 12));
        x0_x1Label.setText("X0/X1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        add(x0_x1Label, gridBagConstraints);

        x0_x1Field.setColumns(4);
        x0_x1Field.setText("1.0");
        x0_x1Field.setName("x0_x1TextField");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(x0_x1Field, gridBagConstraints);

        r0_r1Label.setFont(new java.awt.Font("Dialog", 0, 12));
        r0_r1Label.setText("R0/R1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        add(r0_r1Label, gridBagConstraints);

        r0_r1Field.setColumns(4);
        r0_r1Field.setText("0.0");
        r0_r1Field.setName("r0_r1TextField");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(r0_r1Field, gridBagConstraints);

        b1_1000Label.setFont(new java.awt.Font("Dialog", 0, 12));
        b1_1000Label.setText("B1/1000");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        add(b1_1000Label, gridBagConstraints);

        b1_1000Field.setColumns(8);
        b1_1000Field.setText("0.0");
        b1_1000Field.setName("b1_1000TextField");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(b1_1000Field, gridBagConstraints);

        b0_1000Label.setFont(new java.awt.Font("Dialog", 0, 12));
        b0_1000Label.setText("B0/B1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        add(b0_1000Label, gridBagConstraints);

        b0_b1Field.setColumns(4);
        b0_b1Field.setText("0.0");
        b0_b1Field.setName("b0_b1TextField");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(b0_b1Field, gridBagConstraints);

        bUnitLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        bUnitLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        bUnitLabel.setText("microMhos/1000");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(bUnitLabel, gridBagConstraints);

    }
    // </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel b0_1000Label;
    private javax.swing.JTextField b0_b1Field;
    private javax.swing.JTextField b1_1000Field;
    private javax.swing.JLabel b1_1000Label;
    private javax.swing.JLabel bUnitLabel;
    private javax.swing.JTextField lengthField;
    private javax.swing.JLabel lengthLabel;
    private javax.swing.JComboBox lengthUnitComboBox;
    private javax.swing.JComboBox nameComboBox;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField r0_r1Field;
    private javax.swing.JLabel r0_r1Label;
    private javax.swing.JTextField r_1000Field;
    private javax.swing.JLabel r_1000Label;
    private javax.swing.JTextField x0_x1Field;
    private javax.swing.JLabel x0_x1Label;
    private javax.swing.JTextField x_1000Field;
    private javax.swing.JLabel x_1000Label;
    private javax.swing.JLabel zUnitLabel;
    // End of variables declaration//GEN-END:variables

	class DataVerifier extends javax.swing.InputVerifier {
    	public boolean verify(javax.swing.JComponent input) {
			if (input == null)
				return false;
       		try {
    			if (input == b0_b1Field)
 	       			return SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) >= 0.0;
    			if (input == b1_1000Field)
 	       			return SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) >= 0.0;
    			if (input == lengthField)
 	       			return SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) > 0.0;
    			if (input == r0_r1Field)
 	       			return SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) >= 0.0;
    			if (input == r_1000Field)
 	       			return SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) >= 0.0;
    			if (input == x0_x1Field)
 	       			return SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) >= 0.0;
    			if (input == x_1000Field)
 	       			return SwingInputVerifyUtil.getDouble((javax.swing.JTextField)input) > 0.0;
 	       	} catch (Exception e) {
 	    		return false;
 	       	}		
			return true;
        }
    }
}
