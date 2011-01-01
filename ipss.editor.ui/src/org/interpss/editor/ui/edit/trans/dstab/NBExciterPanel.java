 /*
  * @(#)NBExciterPanel.java   
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
  
import java.awt.Component;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.interpss.editor.data.dstab.DStabBusData;
import org.interpss.editor.data.dstab.DStabExcData;
import org.interpss.editor.data.dstab.DStabMachData;
import org.interpss.editor.form.InitDataUtil;
import org.interpss.editor.jgraph.ui.edit.IFormDataPanel;
import org.interpss.editor.ui.edit.trans.bus.NBDStabTransBusEditPanel;
import org.interpss.ui.ICustomPluginEditor;

import com.interpss.common.util.IpssLogger;
import com.interpss.dstab.mach.MachineController;
import com.interpss.simu.util.SimuSpringAppCtxUtil;
 
public class NBExciterPanel extends javax.swing.JPanel implements IFormDataPanel {
	private static final long serialVersionUID = 1;
	private JDialog parentDialog = null;
	private JPanel parent = null;

    private DStabMachData machData = null;
	private MachineController controller = null;

	public void initPanel(JPanel p, JDialog pdialog) {
		parent = p;
		parentDialog = pdialog;
	}
    
	public void init(Object netContainer, Object busData) {
		IpssLogger.getLogger().info("NBExciterPanel init() called");
		machData = ((DStabBusData)busData).getMachData();
	    catyListComboBox.setModel(new javax.swing.DefaultComboBoxModel(
	    		SimuSpringAppCtxUtil.getExciterCategoryList()));
	    typeListComboBox.setModel(new javax.swing.DefaultComboBoxModel(
	    		SimuSpringAppCtxUtil.getExciterNameList()));
		if (machData.getHasExc()) {
			controller = SimuSpringAppCtxUtil.getExciter(machData.getExcData().getTypeName());
		}
	}

	private void setEditPanel(String typeName) {
		MachineController aController = SimuSpringAppCtxUtil.getExciter(typeName);
   		if (controller == null || aController.getClass() != controller.getClass()) {
   			IpssLogger.getLogger().info("NBExciterPanel create a new controller class, " + aController.getName());
   			controller = aController;
   		  	InitDataUtil.initDStabControllerData(machData.getExcData(), typeName, controller);
   		}	
   		controller.setData(machData.getExcData().getDataXmlStr(), controller.getDataClass());
   		controller.setScripts(machData.getExcData().getScripts());
		dataPanel.removeAll();
		dataPanel.add((Component)controller.getEditPanel());
		dataPanel.repaint();
	}
	
    public boolean setForm2Editor() {
		IpssLogger.getLogger().info("NBExciterPanel setForm2Editor() called");
		
		// select the type name with the Exc data object
		DStabExcData data = machData.getExcData();
		String typeName = data.getTypeName();
		//System.out.println("----->curent exc type name: " + typeName);
		if (typeName != null && !typeName.trim().equals("")) {
			// TODO: we need check typename collision here
	    	typeListComboBox.setSelectedItem(typeName);
		}
		else 
			typeListComboBox.setSelectedIndex(0);
		
		typeName = (String)typeListComboBox.getSelectedItem();
    	setEditPanel(typeName);  // a new controller instance is always created

    	((ICustomPluginEditor)controller.getEditPanel()).setData2Editor("desc");

    	// enable or disable the PSS editing pane
    	if (machData.getHasPss()) {
    		pssCheckBox.setSelected(true);
    		((NBDStabTransBusEditPanel)parent).setPssTabbedPaneEnabled(true);
    	}	
    	else {
    		pssCheckBox.setSelected(false);
    		((NBDStabTransBusEditPanel)parent).setPssTabbedPaneEnabled(false);
    	}	
    	return true;
	}
    
    public boolean saveEditor2Form(Vector errMsg) throws Exception {
		IpssLogger.getLogger().info("NBExciterPanel saveEditor2Form() called");

		boolean ok = true;
		
   		machData.getExcData().setTypeName((String)typeListComboBox.getSelectedItem());
   		machData.getExcData().setClassName(controller.getClass().getName());
		
    	if (!((ICustomPluginEditor)controller.getEditPanel()).saveEditorData(errMsg)) {
    		ok = false;
    	}
    	machData.getExcData().setDataXmlStr(controller.getDataXmlString());
    	machData.getExcData().setScripts(controller.getScripts());

		return ok;
    }
    
    /** Creates new form AclfEditPanel */
    public NBExciterPanel() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        selectPanel = new javax.swing.JPanel();
        catyLabel = new javax.swing.JLabel();
        catyListComboBox = new javax.swing.JComboBox();
        typeLabel = new javax.swing.JLabel();
        typeListComboBox = new javax.swing.JComboBox();
        pssCheckBox = new javax.swing.JCheckBox();
        dataPanel = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        selectPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 20, 20));

        catyLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        catyLabel.setText("Category");
        selectPanel.add(catyLabel);

        catyListComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        catyListComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All" }));
        catyListComboBox.setName("typeListComboBox");
        catyListComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                catyListComboBoxActionPerformed(evt);
            }
        });

        selectPanel.add(catyListComboBox);

        typeLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        typeLabel.setText("     Type");
        selectPanel.add(typeLabel);

        typeListComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        typeListComboBox.setName("typeListComboBox");
        typeListComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeListComboBoxActionPerformed(evt);
            }
        });

        selectPanel.add(typeListComboBox);

        pssCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        pssCheckBox.setText("Has Stabilizer");
        pssCheckBox.setName("pssCheckBox");
        pssCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pssCheckBoxActionPerformed(evt);
            }
        });

        selectPanel.add(pssCheckBox);

        add(selectPanel, java.awt.BorderLayout.NORTH);

        add(dataPanel, java.awt.BorderLayout.CENTER);

    }// </editor-fold>//GEN-END:initComponents

    private void catyListComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_catyListComboBoxActionPerformed
    	String catyName = (String)catyListComboBox.getSelectedItem();
	    if (catyName.equals("All")) {
	    	typeListComboBox.setModel(new javax.swing.DefaultComboBoxModel(
		    		SimuSpringAppCtxUtil.getExciterNameList()));	
	    }
	    else {
	    	typeListComboBox.setModel(new javax.swing.DefaultComboBoxModel(
		    		SimuSpringAppCtxUtil.getExciterNameList(catyName)));	
	    }
	    parentDialog.pack();
    }//GEN-LAST:event_catyListComboBoxActionPerformed

    private void typeListComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeListComboBoxActionPerformed
    	String typeName = (String)typeListComboBox.getSelectedItem();
    	setEditPanel(typeName);
    	parentDialog.pack();
    }//GEN-LAST:event_typeListComboBoxActionPerformed

    private void pssCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pssCheckBoxActionPerformed
    	if (pssCheckBox.isSelected()) {
    		machData.setHasPss(true);
    		((NBDStabTransBusEditPanel)parent).setPssTabbedPaneEnabled(true);
    	}	
    	else {
    		machData.setHasPss(false);
    		((NBDStabTransBusEditPanel)parent).setPssTabbedPaneEnabled(false);
    	}	
    }//GEN-LAST:event_pssCheckBoxActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel catyLabel;
    private javax.swing.JComboBox catyListComboBox;
    private javax.swing.JPanel dataPanel;
    private javax.swing.JCheckBox pssCheckBox;
    private javax.swing.JPanel selectPanel;
    private javax.swing.JLabel typeLabel;
    private javax.swing.JComboBox typeListComboBox;
    // End of variables declaration//GEN-END:variables
}
