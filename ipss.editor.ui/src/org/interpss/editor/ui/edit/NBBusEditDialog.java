 /*
  * @(#)NBBusEditDialog.java   
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

package org.interpss.editor.ui.edit;

import java.util.Vector;

import org.interpss.editor.DataChangeMessage;
import org.interpss.editor.form.GBusForm;
import org.interpss.editor.form.GFormContainer;
import org.interpss.editor.form.InitDataUtil;
import org.interpss.editor.jgraph.GraphSpringFactory;
import org.interpss.editor.jgraph.ui.IGraphicEditor;
import org.interpss.editor.jgraph.ui.edit.IFormDataDialog;
import org.interpss.editor.jgraph.ui.form.IGNetForm;
import org.interpss.editor.ui.edit.dist.bus.NBDistBusEditPanel;
import org.interpss.editor.ui.edit.trans.bus.NBAclfTransBusEditPanel;
import org.interpss.editor.ui.edit.trans.bus.NBAcscTransBusEditPanel;
import org.interpss.editor.ui.edit.trans.bus.NBDStabTransBusEditPanel;
import org.interpss.numeric.util.Number2String;
import org.interpss.spring.EditorPluginSpringFactory;
import org.interpss.ui.SwingInputVerifyUtil;
import org.interpss.ui.WinUtilities;

import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.common.util.IpssLogger;
import com.interpss.common.util.NetUtilFunc;
  

public class NBBusEditDialog extends javax.swing.JDialog  implements IFormDataDialog {
	private static final long serialVersionUID = 1;

	private IPSSMsgHub msgHub = null;
	private GFormContainer _netContainer = null;
    private GBusForm _form = null;
    
    private NBDistBusEditPanel       _distBusEditPanel = new NBDistBusEditPanel();
    private NBAclfTransBusEditPanel  _transAclfBusEditPanel = new NBAclfTransBusEditPanel();
    private NBAcscTransBusEditPanel  _transAcscBusEditPanel = new NBAcscTransBusEditPanel();
    private NBDStabTransBusEditPanel _transDStabBusEditPanel = new NBDStabTransBusEditPanel();
    
	/**
	*	Constructor
	*
	* @parem parent the parent Frame object	
	*/
	public NBBusEditDialog(java.awt.Frame parent, IPSSMsgHub aMsgHub) {
        super(parent, "Bus Data Editor", true);
        initComponents();
        WinUtilities.center(this);

        this.msgHub = aMsgHub;

        DataVerifier verifier = new DataVerifier();
    	this.busNameField.setInputVerifier(verifier);
    	this.zoneField.setInputVerifier(verifier);
    	this.baseVoltComboBox.setInputVerifier(verifier);

    	_distBusEditPanel.initPanel(this);
    	_transAclfBusEditPanel.initPanel(this);
    	_transAcscBusEditPanel.initPanel(this);
    	_transDStabBusEditPanel.initPanel(this);
    }

	public NBBusEditDialog(IGraphicEditor parent, IPSSMsgHub aMsg) {
        this(parent.getFrame(), aMsg);
    }
    
	public boolean isReturnOk() {
		return true;
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
		IpssLogger.getLogger().info("NBBusEditDialog init() called");
		
		_netContainer = (GFormContainer)netContainer;
		_form = (GBusForm)form;
		
        if (_netContainer.getGNetForm().getAppType().equals(IGNetForm.AppType_Distribution)) {
        	_distBusEditPanel.init(netContainer, form);
        }	
        else {
            if (_netContainer.getGNetForm().getNetType().equals(IGNetForm.NetType_AcscNetwork))
            	_transAcscBusEditPanel.init(netContainer, form);
            else if (_netContainer.getGNetForm().getNetType().equals(IGNetForm.NetType_DStabilityNet))
            	_transDStabBusEditPanel.init(netContainer, form);
            else
            	_transAclfBusEditPanel.init(netContainer, form);
        }

		setForm2Editor();
		pack();
        WinUtilities.center(this);
        setVisible(true);
	}
	
	/**
	*	Set form data to the editor
	*
	* @return false if there is any problem
	*/
	public boolean setForm2Editor() {
      this.busNumberLabel.setText("Bus [" + _form.getId() + "]");
    	this.busNameField.setText(_form.getName());

    	this.zoneField.setText(Number2String.toStr(_form.getZone()));
    	if (_form.getStatus())
    		this.inServiceCheckBox.setSelected(true);
    	else	
    		this.inServiceCheckBox.setSelected(false);

		this.baseVoltComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				_netContainer.getBaseVoltArray()));
		this.baseVoltComboBox.setSelectedItem(Number2String.toStr(
				_form.getBaseVoltage(), "#0.00"));
    	this.baseUnitComboBox.setSelectedItem(_form.getBaseVoltUnit());
    	
    	editContainerPanel.removeAll();
        if (_netContainer.getGNetForm().getAppType().equals(IGNetForm.AppType_Distribution)) {
        	editContainerPanel.add(_distBusEditPanel, java.awt.BorderLayout.CENTER);
        	_distBusEditPanel.setForm2Editor();
        }
        else {
            if (_netContainer.getGNetForm().getNetType().equals(IGNetForm.NetType_AcscNetwork)) {
            	editContainerPanel.add(_transAcscBusEditPanel, java.awt.BorderLayout.CENTER);
            	_transAcscBusEditPanel.setForm2Editor();
            }	
            else if (_netContainer.getGNetForm().getNetType().equals(IGNetForm.NetType_DStabilityNet)) {
            	editContainerPanel.add(_transDStabBusEditPanel, java.awt.BorderLayout.CENTER);
            	_transDStabBusEditPanel.setForm2Editor();
            }	
            else {
            	editContainerPanel.add(_transAclfBusEditPanel, java.awt.BorderLayout.CENTER);
            	_transAclfBusEditPanel.setForm2Editor();
            }	
        }
    	
    	return true;
	}

	/**
	*	Save editor screen data to the form
	*
	* @param errMsg error messages during the saving process.
	* @return false if there is any problem
	*/
    public boolean saveEditor2Form(Vector errMsg) throws Exception {
		boolean ok = true;

    	if (!NetUtilFunc.isValidBusid(busNameField.getText())) {
			errMsg.add("Bus name field is empty or invalid");
			ok = false;
    	}
        else if (_netContainer.hasBusName(_form.getId(), busNameField.getText())) {
			errMsg.add("Bus name already exits, use a different name instead");
			ok = false;
    	}
    	_form.setName(this.busNameField.getText());
    	
    	if (!SwingInputVerifyUtil.largeThan(this.areaField, 0)) {
			errMsg.add("Area <= 0");
			ok = false;
		}
    	_form.setArea(SwingInputVerifyUtil.getInt(this.areaField));

    	if (!SwingInputVerifyUtil.largeThan(this.zoneField, 0)) {
			errMsg.add("Zone <= 0");
			ok = false;
		}
    	_form.setZone(SwingInputVerifyUtil.getInt(this.zoneField));
    	
    	if (this.inServiceCheckBox.isSelected())
    		_form.setStatus(true);
    	else	
    		_form.setStatus(false);
		
		if (!SwingInputVerifyUtil.largeThan(this.baseVoltComboBox, 0.0d)) {
			errMsg.add("Bus base voltage <= 0.0");
			ok = false;
		}

		_form.setBaseVoltage(SwingInputVerifyUtil.getDouble(this.baseVoltComboBox));
    	_form.setBaseVoltUnit((String)this.baseUnitComboBox.getSelectedItem());
        if (_netContainer.getGNetForm().getAppType().equals(IGNetForm.AppType_Distribution)) {
        	if (!_distBusEditPanel.saveEditor2Form(errMsg))
        		ok = false;
        }
        else {
            if (_netContainer.getGNetForm().getNetType().equals(IGNetForm.NetType_AcscNetwork)) {
            	if (!_transAcscBusEditPanel.saveEditor2Form(errMsg))
            		ok = false;
            }	
            else if (_netContainer.getGNetForm().getNetType().equals(IGNetForm.NetType_DStabilityNet)) {
            	if (!_transDStabBusEditPanel.saveEditor2Form(errMsg))
            		ok = false;
            }	
            else {
            	if (!_transAclfBusEditPanel.saveEditor2Form(errMsg))
            		ok = false;
            }	
        }

    	return ok;
    }
    
	// The following code is controlled by NetBean IDE
	// ===============================================
	
    /** Creates new form NBBusEditDialog */
    public NBBusEditDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        WinUtilities.center(this);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        inputPanel = new javax.swing.JPanel();
        idPanel = new javax.swing.JPanel();
        busNumberLabel = new javax.swing.JLabel();
        busNameLabel = new javax.swing.JLabel();
        busNameField = new javax.swing.JTextField();
        baseVoltLabel = new javax.swing.JLabel();
        baseVoltComboBox = new javax.swing.JComboBox();
        baseUnitComboBox = new javax.swing.JComboBox();
        areaLabel = new javax.swing.JLabel();
        areaField = new javax.swing.JTextField();
        zoneLabel = new javax.swing.JLabel();
        zoneField = new javax.swing.JTextField();
        inServiceCheckBox = new javax.swing.JCheckBox();
        editContainerPanel = new javax.swing.JPanel();
        controlPanel = new javax.swing.JPanel();
        saveButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.BorderLayout(5, 5));

        setTitle("Bus Data Editor");
        setModal(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        inputPanel.setLayout(new java.awt.BorderLayout(5, 5));

        inputPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 20, 0, 20));
        busNumberLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        busNumberLabel.setText("Bus#: 0001");
        idPanel.add(busNumberLabel);

        busNameLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        busNameLabel.setText("   BusName");
        idPanel.add(busNameLabel);

        busNameField.setColumns(10);
        busNameField.setFont(new java.awt.Font("Dialog", 0, 10));
        busNameField.setName("busNameTextField");
        idPanel.add(busNameField);

        baseVoltLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        baseVoltLabel.setText("   Base Volt");
        idPanel.add(baseVoltLabel);

        baseVoltComboBox.setEditable(true);
        baseVoltComboBox.setFont(new java.awt.Font("Dialog", 0, 10));
        baseVoltComboBox.setName("baseVoltComboBox");
        baseVoltComboBox.setPreferredSize(new java.awt.Dimension(80, 25));
        idPanel.add(baseVoltComboBox);

        baseUnitComboBox.setFont(new java.awt.Font("Dialog", 0, 10));
        baseUnitComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Volt", "KV" }));
        baseUnitComboBox.setName("baseUnitComboBox");
        idPanel.add(baseUnitComboBox);

        areaLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        areaLabel.setText("   Area");
        idPanel.add(areaLabel);

        areaField.setColumns(2);
        areaField.setFont(new java.awt.Font("Dialog", 0, 10));
        areaField.setText("1");
        areaField.setName("areaTextField");
        idPanel.add(areaField);

        zoneLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        zoneLabel.setText("   Zone");
        idPanel.add(zoneLabel);

        zoneField.setColumns(2);
        zoneField.setFont(new java.awt.Font("Dialog", 0, 10));
        zoneField.setText("1");
        zoneField.setName("zoneTextField");
        idPanel.add(zoneField);

        inServiceCheckBox.setFont(new java.awt.Font("Dialog", 0, 10));
        inServiceCheckBox.setSelected(true);
        inServiceCheckBox.setText("In Service");
        inServiceCheckBox.setName("inServiceCheckBox");
        idPanel.add(inServiceCheckBox);

        inputPanel.add(idPanel, java.awt.BorderLayout.NORTH);
        idPanel.getAccessibleContext().setAccessibleParent(idPanel);

        editContainerPanel.setLayout(new java.awt.BorderLayout(5, 5));

        editContainerPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        inputPanel.add(editContainerPanel, java.awt.BorderLayout.CENTER);

        getContentPane().add(inputPanel, java.awt.BorderLayout.CENTER);

        controlPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 20, 10));

        controlPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 10, 1));
        saveButton.setFont(new java.awt.Font("Dialog", 0, 12));
        saveButton.setText("Save");
        saveButton.setName("saveButton");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveActionHandler(evt);
            }
        });

        controlPanel.add(saveButton);

        cancelButton.setFont(new java.awt.Font("Dialog", 0, 12));
        cancelButton.setText("Cancel");
        cancelButton.setName("cancelButton");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelActionHandler(evt);
            }
        });

        controlPanel.add(cancelButton);

        getContentPane().add(controlPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents
 
    private void saveActionHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveActionHandler
		IpssLogger.getLogger().info("NBBusEditDialog.saveActionHandler() called");
		Vector errMsg = new Vector();
		try {
        	if (!saveEditor2Form(errMsg)) {
        		EditorPluginSpringFactory.getEditorDialogUtil().showMsgDialog(this, "Bus Input Data Error", errMsg);
        		IpssLogger.getLogger().info("Bus Input Data Error" + errMsg.toString());
				return;
        	}
        } catch (Exception e) {
      		IpssLogger.logErr(e);
      		EditorPluginSpringFactory.getEditorDialogUtil().showMsgDialog(this, "Bus Input Data Error", e.toString());
			return;
        }	
        _netContainer.addBaseVolt(_form.getBaseVoltage());
        InitDataUtil.setLastEntered_BusData(_form);
		_form.setNewState(false);
        msgHub.sendMsg(new DataChangeMessage(DataChangeMessage.DataChangeMsg, 
				"Bus Info Updated, busName:" + _form.getName()));
        _netContainer.setDataDirty(true);
        GraphSpringFactory.getIpssGraphicEditor().refreshCurrentDocumentEditorPanel();        
        setVisible(false);
    }//GEN-LAST:event_saveActionHandler

    private void cancelActionHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelActionHandler
        setVisible(false);
    }//GEN-LAST:event_cancelActionHandler
    
    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
    }//GEN-LAST:event_closeDialog
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new NBBusEditDialog(new javax.swing.JFrame(), true).show();
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField areaField;
    private javax.swing.JLabel areaLabel;
    private javax.swing.JComboBox baseUnitComboBox;
    private javax.swing.JComboBox baseVoltComboBox;
    private javax.swing.JLabel baseVoltLabel;
    private javax.swing.JTextField busNameField;
    private javax.swing.JLabel busNameLabel;
    private javax.swing.JLabel busNumberLabel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JPanel editContainerPanel;
    private javax.swing.JPanel idPanel;
    private javax.swing.JCheckBox inServiceCheckBox;
    private javax.swing.JPanel inputPanel;
    private javax.swing.JButton saveButton;
    private javax.swing.JTextField zoneField;
    private javax.swing.JLabel zoneLabel;
    // End of variables declaration//GEN-END:variables
    
	class DataVerifier extends javax.swing.InputVerifier {
    	public boolean verify(javax.swing.JComponent input) {
			if (input == null)
				return false;
       		try {
       			if (input == busNameField) 
 	       			return NetUtilFunc.isValidBusid(((javax.swing.JTextField)input).getText());
       			else if (input == zoneField)
 	       			return SwingInputVerifyUtil.getInt((javax.swing.JTextField)input) > 0;
       			else if (input == baseVoltComboBox)
 	       			return SwingInputVerifyUtil.getDouble((javax.swing.JComboBox)input) > 0.0;
 	       	} catch (Exception e) {
 	    		return false;
 	       	}		
			return true;
        }
    }
}
