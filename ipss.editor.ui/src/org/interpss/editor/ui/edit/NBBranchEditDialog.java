 /*
  * @(#)NBBranchEditDialog.java   
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

/**
	Branch form data editor
*/

import java.util.Vector;

import org.interpss.editor.DataChangeMessage;
import org.interpss.editor.data.dist.DistBranchData;
import org.interpss.editor.form.GBranchForm;
import org.interpss.editor.form.GFormContainer;
import org.interpss.editor.form.InitDataUtil;
import org.interpss.editor.jgraph.GraphSpringFactory;
import org.interpss.editor.jgraph.ui.IGraphicEditor;
import org.interpss.editor.jgraph.ui.edit.IFormDataDialog;
import org.interpss.editor.jgraph.ui.form.IGBranchForm;
import org.interpss.editor.jgraph.ui.form.IGNetForm;
import org.interpss.editor.ui.edit.dist.branch.NBDistBranchEditPanel;
import org.interpss.editor.ui.edit.trans.branch.NBAclfTransBranchEditPanel;
import org.interpss.editor.ui.edit.trans.branch.NBAcscTransBranchEditPanel;
import org.interpss.editor.ui.edit.trans.branch.NBDStabTransBranchEditPanel;
import org.interpss.numeric.util.Number2String;
import org.interpss.spring.EditorPluginSpringFactory;
import org.interpss.ui.SwingInputVerifyUtil;
import org.interpss.ui.WinUtilities;

import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.common.util.IpssLogger;
   

public class NBBranchEditDialog extends javax.swing.JDialog  implements IFormDataDialog {
	private static final long serialVersionUID = 1;

	private IPSSMsgHub msgHub = null;
	private GFormContainer _netContainer = null;
    private GBranchForm  _form = null;
    private DistBranchData  _dataDist = null;

    private NBDistBranchEditPanel       _distBranchEditPanel = new NBDistBranchEditPanel();
    private NBAclfTransBranchEditPanel  _transAclfBranchEditPanel = new NBAclfTransBranchEditPanel();
    private NBAcscTransBranchEditPanel  _transAcscBranchEditPanel = new NBAcscTransBranchEditPanel();
    private NBDStabTransBranchEditPanel _transDStabBranchEditPanel = new NBDStabTransBranchEditPanel();
    
	/**
	*	Constructor
	*
	* @parem parent the parent Frame object	
	*/
    public NBBranchEditDialog(java.awt.Frame parent, IPSSMsgHub aMsgHub) {
        super(parent, "Branch Data Editor", true);
        initComponents();
        WinUtilities.center(this);

        this.msgHub = aMsgHub;

        DataVerifier verifier = new DataVerifier();
    	this.zoneField.setInputVerifier(verifier);

    	 _distBranchEditPanel.initPanel(this);
    	 _transAclfBranchEditPanel.initPanel(this);
    	 _transAcscBranchEditPanel.initPanel(this);
    	 _transDStabBranchEditPanel.initPanel(this);
    }


	public NBBranchEditDialog(IGraphicEditor parent, IPSSMsgHub aMsg) {
        this(parent.getFrame(), aMsg);
    }
	
	public boolean isReturnOk() {
		return true;
	}
    
	/**
	*	For performance reason, this editor object is a static member of the 
	*   CellEditorFactory. This method is for init the editor for the form
	* 	object to be edtied
	*
	* @param netContainer the NetContainer object
	* @param form the form object to be edited
	*/
	public void init(Object netContainer, Object form) {
		_netContainer = (GFormContainer)netContainer;
		_form = (GBranchForm)form;
		
       if (_netContainer.getGNetForm().getAppType().equals(IGNetForm.AppType_Distribution)) {
    		_dataDist = _form.getDistBranchData();
    		_distBranchEditPanel.init(netContainer, form);
       }
       else {
            if (_netContainer.getGNetForm().getNetType().equals(IGNetForm.NetType_AcscNetwork))
            	_transAcscBranchEditPanel.init(netContainer, form);
            else if (_netContainer.getGNetForm().getNetType().equals(IGNetForm.NetType_DStabilityNet))
               	_transDStabBranchEditPanel.init(netContainer, form);
            else
            	_transAclfBranchEditPanel.init(netContainer, form);
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
    	this.branchNumberLabel.setText("Branch [" + _form.getBranchNumber() +"]");
        this.branchNameField.setText(_form.getName());
    	this.fromNameField.setText(_form.getFromBusName());
    	this.toNameField.setText(_form.getToBusName());
    	this.zoneField.setText(Number2String.toStr(_form.getZone()));
    	
    	if (_form.getStatus())
    		this.inServiceCheckBox.setSelected(true);
    	else	
    		this.inServiceCheckBox.setSelected(false);
    	
       	editContainerPanel.removeAll();
        if (_netContainer.getGNetForm().getAppType().equals(IGNetForm.AppType_Distribution)) {
            this.branchNameField.setEnabled(true);
        	if (_dataDist.getBranchCode().equals(IGBranchForm.DistBranchCode_Feeder)) {
                this.fromNameLabel.setText("     From  ");
                this.toNameLabel.setText("     To  ");
                this.inServiceCheckBox.setText("In Service");
           	}	
           	else if (_dataDist.getBranchCode().equals(IGBranchForm.DistBranchCode_Xfr)) {
                this.fromNameLabel.setText("     Pri  ");
                this.toNameLabel.setText("     Sec  ");
                this.inServiceCheckBox.setText("In Service");
           	}	
           	else if (_dataDist.getBranchCode().equals(IGBranchForm.DistBranchCode_Breaker)) {
                this.fromNameLabel.setText("     From  ");
                this.toNameLabel.setText("     To  ");
                this.inServiceCheckBox.setText("Close");
           	}	
           	else { 
           	}
           	editContainerPanel.add(_distBranchEditPanel, java.awt.BorderLayout.CENTER);
           	_distBranchEditPanel.setForm2Editor();
        }
        else {
            if (_netContainer.getGNetForm().getNetType().equals(IGNetForm.NetType_AcscNetwork)) {
            	editContainerPanel.add(_transAcscBranchEditPanel, java.awt.BorderLayout.CENTER);
            	_transAcscBranchEditPanel.setForm2Editor();
            }	
            else if (_netContainer.getGNetForm().getNetType().equals(IGNetForm.NetType_DStabilityNet)) {
                editContainerPanel.add(_transDStabBranchEditPanel, java.awt.BorderLayout.CENTER);
                _transDStabBranchEditPanel.setForm2Editor();
            }    
            else {
            	editContainerPanel.add(_transAclfBranchEditPanel, java.awt.BorderLayout.CENTER);
            	_transAclfBranchEditPanel.setForm2Editor();
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
    public boolean saveEditor2Form(Vector<String> errMsg) throws Exception {
		errMsg.clear();
		boolean ok = true;

    	_form.setName(this.branchNameField.getText());

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

        if (_netContainer.getGNetForm().getAppType().equals(IGNetForm.AppType_Distribution)) {
           	if (!_distBranchEditPanel.saveEditor2Form(errMsg))
				ok = false;
        }
        else {
            if (_netContainer.getGNetForm().getNetType().equals(IGNetForm.NetType_AcscNetwork)) {
            	if (!_transAcscBranchEditPanel.saveEditor2Form(errMsg))
            		ok = false;
            }
            else if (_netContainer.getGNetForm().getNetType().equals(IGNetForm.NetType_DStabilityNet)) {
            	if (!_transDStabBranchEditPanel.saveEditor2Form(errMsg))
            		ok = false;
            }
            else {
            	if (!_transAclfBranchEditPanel.saveEditor2Form(errMsg))
            		ok = false;
            }
        }
    	
		return ok;
    }

	// The following code is controlled by NetBean IDE
	// ===============================================
	
    /** Creates new form NBBranchEditDialog */
    public NBBranchEditDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
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
        branchNumberLabel = new javax.swing.JLabel();
        fromNameLabel = new javax.swing.JLabel();
        fromNameField = new javax.swing.JTextField();
        toNameLabel = new javax.swing.JLabel();
        toNameField = new javax.swing.JTextField();
        branchNameLabel = new javax.swing.JLabel();
        branchNameField = new javax.swing.JTextField();
        areaLabel = new javax.swing.JLabel();
        areaField = new javax.swing.JTextField();
        zoneLabel = new javax.swing.JLabel();
        zoneField = new javax.swing.JTextField();
        inServiceCheckBox = new javax.swing.JCheckBox();
        editContainerPanel = new javax.swing.JPanel();
        controlPanel = new javax.swing.JPanel();
        saveButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.BorderLayout(10, 10));

        setModal(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        inputPanel.setLayout(new java.awt.BorderLayout());

        inputPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 20, 0, 20));
        branchNumberLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        branchNumberLabel.setText("Branch#: 0001");
        idPanel.add(branchNumberLabel);

        fromNameLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        fromNameLabel.setText("   From");
        idPanel.add(fromNameLabel);

        fromNameField.setColumns(8);
        fromNameField.setFont(new java.awt.Font("Dialog", 0, 10));
        fromNameField.setEnabled(false);
        fromNameField.setName("fromNameTextField");
        idPanel.add(fromNameField);

        toNameLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        toNameLabel.setText("   To");
        idPanel.add(toNameLabel);

        toNameField.setColumns(8);
        toNameField.setFont(new java.awt.Font("Dialog", 0, 10));
        toNameField.setEnabled(false);
        toNameField.setName("toNameTextField");
        idPanel.add(toNameField);

        branchNameLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        branchNameLabel.setText("   Name");
        idPanel.add(branchNameLabel);

        branchNameField.setColumns(10);
        branchNameField.setFont(new java.awt.Font("Dialog", 0, 10));
        branchNameField.setName("branchNameTextField");
        idPanel.add(branchNameField);

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

    private void cancelActionHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelActionHandler
        setVisible(false);
    }//GEN-LAST:event_cancelActionHandler

    private void saveActionHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveActionHandler
		Vector<String> errMsg = new Vector<String>();
		try {
        	if (!saveEditor2Form(errMsg)) {
        		EditorPluginSpringFactory.getEditorDialogUtil().showMsgDialog(this, "Branch Input Data Error", errMsg);
				return;
        	}
        } catch (Exception e) {
      		IpssLogger.logErr(e);
      		EditorPluginSpringFactory.getEditorDialogUtil().showMsgDialog(this, "Branch Input Data Error", e.toString());
			return;
        }	
        InitDataUtil.setLastEntered_BranchData(_form);
		_form.setNewState(false);
        msgHub.sendMsg(new DataChangeMessage(DataChangeMessage.DataChangeMsg, 
				"Branch Info Updated, branchName:" + _form.getName()));
        _netContainer.setDataDirty(true);
        GraphSpringFactory.getIpssGraphicEditor().refreshCurrentDocumentEditorPanel();        
		setVisible(false);
    }//GEN-LAST:event_saveActionHandler
    
    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
    }//GEN-LAST:event_closeDialog
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new NBBranchEditDialog(new javax.swing.JFrame(), true).show();
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField areaField;
    private javax.swing.JLabel areaLabel;
    private javax.swing.JTextField branchNameField;
    private javax.swing.JLabel branchNameLabel;
    private javax.swing.JLabel branchNumberLabel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JPanel editContainerPanel;
    private javax.swing.JTextField fromNameField;
    private javax.swing.JLabel fromNameLabel;
    private javax.swing.JPanel idPanel;
    private javax.swing.JCheckBox inServiceCheckBox;
    private javax.swing.JPanel inputPanel;
    private javax.swing.JButton saveButton;
    private javax.swing.JTextField toNameField;
    private javax.swing.JLabel toNameLabel;
    private javax.swing.JTextField zoneField;
    private javax.swing.JLabel zoneLabel;
    // End of variables declaration//GEN-END:variables
    
	class DataVerifier extends javax.swing.InputVerifier {
    	public boolean verify(javax.swing.JComponent input) {
			if (input == null)
				return false;
       		try {
       			if (input == zoneField)
 	       			return SwingInputVerifyUtil.getInt((javax.swing.JTextField)input) > 0;
 	       	} catch (Exception e) {
 	    		return false;
 	       	}		
			return true;
        }
    }
}
