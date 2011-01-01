 /*
  * @(#)NBDistProjEditPanel.java   
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

package org.interpss.editor.ui.edit.dist;
    
import java.util.Vector;

import javax.swing.JDialog;

import org.interpss.editor.data.common.ScPointData;
import org.interpss.editor.data.dist.DistNetData;
import org.interpss.editor.form.GFormContainer;
import org.interpss.editor.form.GNetForm;
import org.interpss.editor.form.InitDataUtil;
import org.interpss.editor.jgraph.ui.edit.IFormDataPanel;
import org.interpss.numeric.util.Number2String;
import org.interpss.ui.SwingInputVerifyUtil;

import com.interpss.common.util.IpssLogger;
 
public class NBDistProjEditPanel extends javax.swing.JPanel implements IFormDataPanel {
	private static final long serialVersionUID = 1;
	private JDialog _parent = null;

	private GFormContainer _netContainer = null;
    
	public void initPanel(JDialog parent) {
		this._parent = parent;
		
		DataVerifier verifier = new DataVerifier();
    	this.pName_1_Field.setInputVerifier(verifier);
    	this.pName_2_Field.setInputVerifier(verifier);
    	this.pName_3_Field.setInputVerifier(verifier);
    	this.pName_4_Field.setInputVerifier(verifier);
    	this.pName_5_Field.setInputVerifier(verifier);
        this.loadPonitsTextField.setInputVerifier(verifier);
        this.periodLengthTextField.setInputVerifier(verifier);
	}

	/**
	*	For performance reason, editor objects are static member of the 
	*   CellEditorFactory. This method is for init the editor for the form
	* 	object to be edtied
	*
	* @param netContainer the NetContainer object
	* @param form the form object to be edited
	*/  
	public void init(Object netContainer, Object form) {
		IpssLogger.getLogger().info("NBDistProjEditPanel.init() called");
		_netContainer = (GFormContainer)netContainer;
		
    	this.iecStdRadioButton.setEnabled(false);
        scPanel.remove(scPointsPanel);
	}

	/**
	*	Set form data to the editor
	*
	* @return false if there is any problem
	*/		
    public boolean setForm2Editor() {
		IpssLogger.getLogger().info("NBDistProjEditPanel.setForm2Editor() called");
		GNetForm form = (GNetForm)_netContainer.getGNetForm();
    	
		if (form.getDistNetData().getLoadSchedulePoints() > 0) {
		    setLoadAnalysisFields(true);
		    loadAnalysisCheckBox.setSelected(true);
	        loadPonitsTextField.setText(Number2String.toStr(form.getDistNetData().getLoadSchedulePoints()));
	        periodLengthTextField.setText(Number2String.toStr(form.getDistNetData().getLoadSchedulePeriodLength(), "0.0"));
	        periodLengthUnitComboBox.setSelectedItem(form.getDistNetData().getLoadSchedulePeriodUnit());
		}
		else {
		    loadAnalysisCheckBox.setSelected(false);
		    setLoadAnalysisFields(false);
		}
		
		if (form.getDistNetData().getScStd().equals(DistNetData.ScStd_Generic)) {
    		this.genericStdRadioButton.setSelected(true);
		}
		else {
	        scPanel.add(scPointsPanel, java.awt.BorderLayout.CENTER);
	        setSCPointPanel();
	        if (form.getDistNetData().getScStd().equals(DistNetData.ScStd_ANSI))
	    		this.ansiStdRadioButton.setSelected(true);    // 3 ScPoints will be created
	    	else 
	    		this.iecStdRadioButton.setSelected(true);
		}
		
    	return true;
	}
    
	/**
	*	Save editor screen data to the form
	*
	* @param errMsg error messages during the saving process.
	* @return false if there is any problem
	*/
	public boolean saveEditor2Form(Vector<String> errMsg) throws Exception {
		IpssLogger.getLogger().info("NBDistProjEditPanel.saveEditor2Form() called");
		GNetForm form = (GNetForm)_netContainer.getGNetForm();
		boolean ok = true;

		form.getDistNetData().setLoadSchedulePoints(0);
		if (loadAnalysisCheckBox.isSelected()) {
			if ( SwingInputVerifyUtil.largeThan(loadPonitsTextField, 24) ) {
				errMsg.add("Load service points > 24");
				ok = false;
			}
			form.getDistNetData().setLoadSchedulePoints(SwingInputVerifyUtil.getInt(loadPonitsTextField));

			if ( !SwingInputVerifyUtil.largeThan(periodLengthTextField, 0.0) ) {
				errMsg.add("Load service length <= 0.0");
				ok = false;
			}
			form.getDistNetData().setLoadSchedulePeriodLength(SwingInputVerifyUtil.getDouble(periodLengthTextField));

			form.getDistNetData().setLoadSchedulePeriodUnit((String)periodLengthUnitComboBox.getSelectedItem());
		}
		
		if (this.genericStdRadioButton.isSelected()) {
			form.getDistNetData().setScStd(DistNetData.ScStd_Generic);
	    	form.getDistNetData().setScPointList(new Vector<ScPointData>());
	    	form.getDistNetData().setScPoints(0);
		}
		else {
			if (this.ansiStdRadioButton.isSelected())
				form.getDistNetData().setScStd(DistNetData.ScStd_ANSI);
			else 
				form.getDistNetData().setScStd(DistNetData.ScStd_IEC);
	    	
			if (form.getDistNetData().getScPointList().size() > 0) {
				if ( SwingInputVerifyUtil.isEmptyStr(this.pName_1_Field) ) {
					errMsg.add("SC Point 1 name is empty");
					ok = false;
				}
				saveScPoint( (ScPointData)form.getDistNetData().getScPointList().get(0),
	    					this.pName_1_Field, this.pDesc_1_Field,
	    					this.pEnable_1_CheckBox);
	    	}	

			if (form.getDistNetData().getScPointList().size() > 1) {
	        if ( SwingInputVerifyUtil.isEmptyStr(this.pName_2_Field) ) {
	            errMsg.add("SC Point 2 name is empty");
	            ok = false;
	        }
				saveScPoint( (ScPointData)form.getDistNetData().getScPointList().get(1),
	    					this.pName_2_Field, this.pDesc_2_Field,
	    					this.pEnable_2_CheckBox);
			}
			
			if (form.getDistNetData().getScPointList().size() > 2) {
				if ( SwingInputVerifyUtil.isEmptyStr(this.pName_3_Field) ) {
					errMsg.add("SC Point 3 name is empty");
					ok = false;
				}
				saveScPoint( (ScPointData)form.getDistNetData().getScPointList().get(2),
	    					this.pName_3_Field, this.pDesc_3_Field,
	    					this.pEnable_3_CheckBox);
			}
			
			if (form.getDistNetData().getScPointList().size() > 3) {
	        if ( SwingInputVerifyUtil.isEmptyStr(this.pName_4_Field) ) {
	            errMsg.add("SC Point 4 name is empty");
	            ok = false;
	        }
				saveScPoint( (ScPointData)form.getDistNetData().getScPointList().get(3),
	    					this.pName_4_Field, this.pDesc_4_Field,
	    					this.pEnable_4_CheckBox);
			}
			
			if (form.getDistNetData().getScPointList().size() > 4) {
	        if ( SwingInputVerifyUtil.isEmptyStr(this.pName_5_Field) ) {
	            errMsg.add("SC Point 5 name is empty");
	            ok = false;
	        }
				saveScPoint( (ScPointData)form.getDistNetData().getScPointList().get(4),
	    					this.pName_5_Field, this.pDesc_5_Field,
	    					this.pEnable_5_CheckBox);
			}
		}

		return ok;
    }

    public void setSCPointPanel() {
		GNetForm form = (GNetForm)_netContainer.getGNetForm();
    	
		ScPointData row = null;
		if (form.getDistNetData().getScPointList().size() > 0) {
			row = (ScPointData)form.getDistNetData().getScPointList().get(0);
    	}	
		setScPoint( row, this.pName_1_Field, this.pDesc_1_Field, this.pEnable_1_CheckBox);

		row = null;
		if (form.getDistNetData().getScPointList().size() > 1) {
			row = (ScPointData)form.getDistNetData().getScPointList().get(1);
		}
		setScPoint( row,	this.pName_2_Field, this.pDesc_2_Field, this.pEnable_2_CheckBox);
		
		row = null;
		if (form.getDistNetData().getScPointList().size() > 2) {
			row = (ScPointData)form.getDistNetData().getScPointList().get(2);
		}
		setScPoint( row,	this.pName_3_Field, this.pDesc_3_Field, this.pEnable_3_CheckBox);
		
		row = null;
		if (form.getDistNetData().getScPointList().size() > 3) {
			row = (ScPointData)form.getDistNetData().getScPointList().get(3);
		}
		setScPoint( row,	this.pName_4_Field, this.pDesc_4_Field, this.pEnable_4_CheckBox);
		
		row = null;
		if (form.getDistNetData().getScPointList().size() > 4) {
			row = (ScPointData)form.getDistNetData().getScPointList().get(4);
		}
		setScPoint( row,	this.pName_5_Field, this.pDesc_5_Field, this.pEnable_5_CheckBox);
	 }

    private void setScPoint( ScPointData row, 
			javax.swing.JTextField nameField, 
			javax.swing.JTextField descField, 
			javax.swing.JCheckBox checkBox) {
    	if (row != null) {
            nameField.setText(row.getName());
            descField.setText(row.getDescription());
            boolean enabled = row.getEnable();
            if (enabled) {
                checkBox.setSelected(true);
                checkBox.setEnabled(true);
                nameField.setEnabled(true);
                descField.setEnabled(true);
            }
            else {
                checkBox.setSelected(false);
                nameField.setEnabled(false);
                descField.setEnabled(false);
            }	
    	}
    	else {
            nameField.setText("");
            descField.setText("");
            checkBox.setSelected(false);
            checkBox.setEnabled(false);
            nameField.setEnabled(false);
            descField.setEnabled(false);
    	}
	}

	private void saveScPoint( ScPointData row, 
			javax.swing.JTextField nameField, 
			javax.swing.JTextField descField, 
			javax.swing.JCheckBox checkBox) {
    	row.setName(nameField.getText());
    	row.setDescription(descField.getText());
		row.setEnable(checkBox.isSelected());
	}
	
	// The following code is controlled by NetBean IDE
	// ===============================================

    /** Creates new form NBAcscEditPanel */
    public NBDistProjEditPanel() {
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

        scStdButtonGroup = new javax.swing.ButtonGroup();
        loadScheduelPanel = new javax.swing.JPanel();
        loadAnalysisCheckBox = new javax.swing.JCheckBox();
        loadPointsLabel = new javax.swing.JLabel();
        loadPonitsTextField = new javax.swing.JTextField();
        periodLengthLabel = new javax.swing.JLabel();
        periodLengthTextField = new javax.swing.JTextField();
        periodLengthUnitComboBox = new javax.swing.JComboBox();
        scPanel = new javax.swing.JPanel();
        upperPanel = new javax.swing.JPanel();
        stdPanel = new javax.swing.JPanel();
        genericStdRadioButton = new javax.swing.JRadioButton();
        ansiStdRadioButton = new javax.swing.JRadioButton();
        iecStdRadioButton = new javax.swing.JRadioButton();
        scPointsPanel = new javax.swing.JPanel();
        pNameLabel = new javax.swing.JLabel();
        pDisabledLabel = new javax.swing.JLabel();
        pDescLabel = new javax.swing.JLabel();
        pName_1_Field = new javax.swing.JTextField();
        pEnable_1_CheckBox = new javax.swing.JCheckBox();
        pDesc_1_Field = new javax.swing.JTextField();
        pName_2_Field = new javax.swing.JTextField();
        pEnable_2_CheckBox = new javax.swing.JCheckBox();
        pDesc_2_Field = new javax.swing.JTextField();
        pName_3_Field = new javax.swing.JTextField();
        pEnable_3_CheckBox = new javax.swing.JCheckBox();
        pDesc_3_Field = new javax.swing.JTextField();
        pName_4_Field = new javax.swing.JTextField();
        pEnable_4_CheckBox = new javax.swing.JCheckBox();
        pDesc_4_Field = new javax.swing.JTextField();
        pName_5_Field = new javax.swing.JTextField();
        pEnable_5_CheckBox = new javax.swing.JCheckBox();
        pDesc_5_Field = new javax.swing.JTextField();

        setLayout(new java.awt.BorderLayout());

        loadScheduelPanel.setLayout(new java.awt.GridBagLayout());

        loadAnalysisCheckBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        loadAnalysisCheckBox.setText("Load Schedule Analysis");
        loadAnalysisCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        loadAnalysisCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        loadAnalysisCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadAnalysisCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        loadScheduelPanel.add(loadAnalysisCheckBox, gridBagConstraints);

        loadPointsLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        loadPointsLabel.setText("Load Points");
        loadPointsLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 10);
        loadScheduelPanel.add(loadPointsLabel, gridBagConstraints);

        loadPonitsTextField.setColumns(3);
        loadPonitsTextField.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        loadPonitsTextField.setText("24");
        loadPonitsTextField.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        loadScheduelPanel.add(loadPonitsTextField, gridBagConstraints);

        periodLengthLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        periodLengthLabel.setText("Period Length");
        periodLengthLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 30, 5, 10);
        loadScheduelPanel.add(periodLengthLabel, gridBagConstraints);

        periodLengthTextField.setColumns(3);
        periodLengthTextField.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        periodLengthTextField.setText("1");
        periodLengthTextField.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        loadScheduelPanel.add(periodLengthTextField, gridBagConstraints);

        periodLengthUnitComboBox.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        periodLengthUnitComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Hour", "Day" }));
        periodLengthUnitComboBox.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        loadScheduelPanel.add(periodLengthUnitComboBox, gridBagConstraints);

        add(loadScheduelPanel, java.awt.BorderLayout.NORTH);

        scPanel.setLayout(new java.awt.BorderLayout());

        upperPanel.setLayout(new java.awt.GridBagLayout());

        stdPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "SC Standard", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        stdPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 20, 5));

        scStdButtonGroup.add(genericStdRadioButton);
        genericStdRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        genericStdRadioButton.setSelected(true);
        genericStdRadioButton.setText("Generic");
        genericStdRadioButton.setName("genericStdRadioButton"); // NOI18N
        genericStdRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genericStdRadioButtonActionPerformed(evt);
            }
        });
        stdPanel.add(genericStdRadioButton);

        scStdButtonGroup.add(ansiStdRadioButton);
        ansiStdRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        ansiStdRadioButton.setText("ANSI");
        ansiStdRadioButton.setName("ansiStdRadioButton"); // NOI18N
        ansiStdRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ansiStdRadioButtonActionPerformed(evt);
            }
        });
        stdPanel.add(ansiStdRadioButton);

        scStdButtonGroup.add(iecStdRadioButton);
        iecStdRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        iecStdRadioButton.setText("IEC");
        iecStdRadioButton.setName("iecStdRadioButton"); // NOI18N
        iecStdRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                iecStdRadioButtonActionPerformed(evt);
            }
        });
        stdPanel.add(iecStdRadioButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 20);
        upperPanel.add(stdPanel, gridBagConstraints);

        scPanel.add(upperPanel, java.awt.BorderLayout.NORTH);

        scPointsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "SC Calculation Points", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        scPointsPanel.setLayout(new java.awt.GridBagLayout());

        pNameLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        pNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pNameLabel.setText("Name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        scPointsPanel.add(pNameLabel, gridBagConstraints);

        pDisabledLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        pDisabledLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pDisabledLabel.setText("   Enable   ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        scPointsPanel.add(pDisabledLabel, gridBagConstraints);

        pDescLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        pDescLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pDescLabel.setText("Description");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        scPointsPanel.add(pDescLabel, gridBagConstraints);

        pName_1_Field.setColumns(8);
        pName_1_Field.setText(" ");
        pName_1_Field.setEnabled(false);
        pName_1_Field.setName("pName_1_TextField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(3, 10, 0, 0);
        scPointsPanel.add(pName_1_Field, gridBagConstraints);

        pEnable_1_CheckBox.setSelected(true);
        pEnable_1_CheckBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pEnable_1_CheckBox.setName("pEnable_1_CheckBox"); // NOI18N
        pEnable_1_CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enableSCPoint1_ActionHandler(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        scPointsPanel.add(pEnable_1_CheckBox, gridBagConstraints);

        pDesc_1_Field.setColumns(30);
        pDesc_1_Field.setText(" ");
        pDesc_1_Field.setEnabled(false);
        pDesc_1_Field.setName("pDesc_1_TextField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        scPointsPanel.add(pDesc_1_Field, gridBagConstraints);

        pName_2_Field.setColumns(8);
        pName_2_Field.setText(" ");
        pName_2_Field.setEnabled(false);
        pName_2_Field.setName("pName_2_TextField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(3, 10, 0, 0);
        scPointsPanel.add(pName_2_Field, gridBagConstraints);

        pEnable_2_CheckBox.setSelected(true);
        pEnable_2_CheckBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pEnable_2_CheckBox.setName("pEnable_2_CheckBox"); // NOI18N
        pEnable_2_CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enableSCPoint2_ActionHandler(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        scPointsPanel.add(pEnable_2_CheckBox, gridBagConstraints);

        pDesc_2_Field.setColumns(30);
        pDesc_2_Field.setText(" ");
        pDesc_2_Field.setEnabled(false);
        pDesc_2_Field.setName("pDesc_2_TextField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        scPointsPanel.add(pDesc_2_Field, gridBagConstraints);

        pName_3_Field.setColumns(8);
        pName_3_Field.setText(" ");
        pName_3_Field.setEnabled(false);
        pName_3_Field.setName("pName_3_TextField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(3, 10, 0, 0);
        scPointsPanel.add(pName_3_Field, gridBagConstraints);

        pEnable_3_CheckBox.setSelected(true);
        pEnable_3_CheckBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pEnable_3_CheckBox.setName("pEnable_3_CheckBox"); // NOI18N
        pEnable_3_CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enableSCPoint3_ActionHandler(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        scPointsPanel.add(pEnable_3_CheckBox, gridBagConstraints);

        pDesc_3_Field.setColumns(30);
        pDesc_3_Field.setText(" ");
        pDesc_3_Field.setEnabled(false);
        pDesc_3_Field.setName("pDesc_3_TextField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        scPointsPanel.add(pDesc_3_Field, gridBagConstraints);

        pName_4_Field.setColumns(8);
        pName_4_Field.setText(" ");
        pName_4_Field.setEnabled(false);
        pName_4_Field.setName("pName_4_TextField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(3, 10, 0, 0);
        scPointsPanel.add(pName_4_Field, gridBagConstraints);

        pEnable_4_CheckBox.setSelected(true);
        pEnable_4_CheckBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pEnable_4_CheckBox.setName("pEnable_4_CheckBox"); // NOI18N
        pEnable_4_CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enableSCPoint4_ActionHandler(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        scPointsPanel.add(pEnable_4_CheckBox, gridBagConstraints);

        pDesc_4_Field.setColumns(30);
        pDesc_4_Field.setText(" ");
        pDesc_4_Field.setEnabled(false);
        pDesc_4_Field.setName("pDesc_4_TextField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        scPointsPanel.add(pDesc_4_Field, gridBagConstraints);

        pName_5_Field.setColumns(8);
        pName_5_Field.setText(" ");
        pName_5_Field.setEnabled(false);
        pName_5_Field.setName("pName_5_TextField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(3, 10, 10, 0);
        scPointsPanel.add(pName_5_Field, gridBagConstraints);

        pEnable_5_CheckBox.setSelected(true);
        pEnable_5_CheckBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pEnable_5_CheckBox.setName("pEnable_5_CheckBox"); // NOI18N
        pEnable_5_CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enableSCPoint5_ActionHandler(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        scPointsPanel.add(pEnable_5_CheckBox, gridBagConstraints);

        pDesc_5_Field.setColumns(30);
        pDesc_5_Field.setText(" ");
        pDesc_5_Field.setEnabled(false);
        pDesc_5_Field.setName("pDesc_5_TextField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        scPointsPanel.add(pDesc_5_Field, gridBagConstraints);

        scPanel.add(scPointsPanel, java.awt.BorderLayout.CENTER);

        add(scPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void loadAnalysisCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadAnalysisCheckBoxActionPerformed
        setLoadAnalysisFields(loadAnalysisCheckBox.isSelected());
    }//GEN-LAST:event_loadAnalysisCheckBoxActionPerformed

    private void iecStdRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iecStdRadioButtonActionPerformed
		//GNetForm form = _netContainer.getGNetForm();
    }//GEN-LAST:event_iecStdRadioButtonActionPerformed

    private void genericStdRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genericStdRadioButtonActionPerformed
		GNetForm form = (GNetForm)_netContainer.getGNetForm();
		form.getDistNetData().setScStd(DistNetData.ScStd_Generic);
        form.getDistNetData().setScPointList(new Vector<ScPointData>());
        form.getDistNetData().setScPoints(0);
        scPanel.remove(scPointsPanel);
        _parent.pack();
    }//GEN-LAST:event_genericStdRadioButtonActionPerformed

    private void ansiStdRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ansiStdRadioButtonActionPerformed
		GNetForm form = (GNetForm)_netContainer.getGNetForm();
		form.getDistNetData().setScStd(DistNetData.ScStd_ANSI);
        form.getDistNetData().setScPointList(InitDataUtil.getAcscANSIPoints());
        form.getDistNetData().setScPoints(InitDataUtil.getAcscANSIPoints().size());
        scPanel.add(scPointsPanel, java.awt.BorderLayout.CENTER);
        setSCPointPanel();
        _parent.pack();
    }//GEN-LAST:event_ansiStdRadioButtonActionPerformed

    private void setLoadAnalysisFields(boolean isSelected) {
        loadPointsLabel.setEnabled(isSelected);
        loadPonitsTextField.setEnabled(isSelected);
        periodLengthLabel.setEnabled(isSelected);
        periodLengthTextField.setEnabled(isSelected);
        periodLengthUnitComboBox.setEnabled(isSelected);
    }

    private void enableSCPoint5_ActionHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enableSCPoint5_ActionHandler
    	if (this.pEnable_5_CheckBox.isSelected()) {
    		this.pName_5_Field.setEnabled(true); 
    		this.pDesc_5_Field.setEnabled(true);
    	}
    	else {
    		this.pName_5_Field.setEnabled(false); 
    		this.pDesc_5_Field.setEnabled(false);
    	}
    }//GEN-LAST:event_enableSCPoint5_ActionHandler

    private void enableSCPoint4_ActionHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enableSCPoint4_ActionHandler
    	if (this.pEnable_4_CheckBox.isSelected()) {
    		this.pName_4_Field.setEnabled(true); 
    		this.pDesc_4_Field.setEnabled(true);
    	}
    	else {
    		this.pName_4_Field.setEnabled(false); 
    		this.pDesc_4_Field.setEnabled(false);
    	}
    }//GEN-LAST:event_enableSCPoint4_ActionHandler

    private void enableSCPoint3_ActionHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enableSCPoint3_ActionHandler
    	if (this.pEnable_3_CheckBox.isSelected()) {
    		this.pName_3_Field.setEnabled(true); 
    		this.pDesc_3_Field.setEnabled(true);
    	}
    	else {
    		this.pName_3_Field.setEnabled(false); 
    		this.pDesc_3_Field.setEnabled(false);
    	}
    }//GEN-LAST:event_enableSCPoint3_ActionHandler

    private void enableSCPoint2_ActionHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enableSCPoint2_ActionHandler
    	if (this.pEnable_2_CheckBox.isSelected()) {
    		this.pName_2_Field.setEnabled(true); 
    		this.pDesc_2_Field.setEnabled(true);
    	}
    	else {
    		this.pName_2_Field.setEnabled(false); 
    		this.pDesc_2_Field.setEnabled(false);
    	}
    }//GEN-LAST:event_enableSCPoint2_ActionHandler

    private void enableSCPoint1_ActionHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enableSCPoint1_ActionHandler
    	if (this.pEnable_1_CheckBox.isSelected()) {
    		this.pName_1_Field.setEnabled(true); 
    		this.pDesc_1_Field.setEnabled(true);
    	}
    	else {
    		this.pName_1_Field.setEnabled(false); 
    		this.pDesc_1_Field.setEnabled(false);
    	}
    }//GEN-LAST:event_enableSCPoint1_ActionHandler
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton ansiStdRadioButton;
    private javax.swing.JRadioButton genericStdRadioButton;
    private javax.swing.JRadioButton iecStdRadioButton;
    private javax.swing.JCheckBox loadAnalysisCheckBox;
    private javax.swing.JLabel loadPointsLabel;
    private javax.swing.JTextField loadPonitsTextField;
    private javax.swing.JPanel loadScheduelPanel;
    private javax.swing.JLabel pDescLabel;
    private javax.swing.JTextField pDesc_1_Field;
    private javax.swing.JTextField pDesc_2_Field;
    private javax.swing.JTextField pDesc_3_Field;
    private javax.swing.JTextField pDesc_4_Field;
    private javax.swing.JTextField pDesc_5_Field;
    private javax.swing.JLabel pDisabledLabel;
    private javax.swing.JCheckBox pEnable_1_CheckBox;
    private javax.swing.JCheckBox pEnable_2_CheckBox;
    private javax.swing.JCheckBox pEnable_3_CheckBox;
    private javax.swing.JCheckBox pEnable_4_CheckBox;
    private javax.swing.JCheckBox pEnable_5_CheckBox;
    private javax.swing.JLabel pNameLabel;
    private javax.swing.JTextField pName_1_Field;
    private javax.swing.JTextField pName_2_Field;
    private javax.swing.JTextField pName_3_Field;
    private javax.swing.JTextField pName_4_Field;
    private javax.swing.JTextField pName_5_Field;
    private javax.swing.JLabel periodLengthLabel;
    private javax.swing.JTextField periodLengthTextField;
    private javax.swing.JComboBox periodLengthUnitComboBox;
    private javax.swing.JPanel scPanel;
    private javax.swing.JPanel scPointsPanel;
    private javax.swing.ButtonGroup scStdButtonGroup;
    private javax.swing.JPanel stdPanel;
    private javax.swing.JPanel upperPanel;
    // End of variables declaration//GEN-END:variables
    
	class DataVerifier extends javax.swing.InputVerifier {
    	public boolean verify(javax.swing.JComponent input) {
			try {
				if (input == null)
					return false;
   				else if (input == pName_1_Field ||
   					 	 input == pName_2_Field ||
   			         	 input == pName_3_Field ||
   					   	 input == pName_4_Field ||
   			         	 input == pName_5_Field )
 	       			return !SwingInputVerifyUtil.isEmptyStr((javax.swing.JTextField)input);
   				else if (input == loadPonitsTextField)
   					return !SwingInputVerifyUtil.largeThan(loadPonitsTextField, 24);
   				else if (input == periodLengthTextField )
   					return SwingInputVerifyUtil.largeThan(periodLengthTextField, 0.0);
 	       	} catch (Exception e) {
				return false;
 	       	}		
			return true;
        }
    }
}
