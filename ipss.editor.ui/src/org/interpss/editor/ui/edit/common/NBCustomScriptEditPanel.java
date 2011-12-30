 /*
  * @(#)NBCustomScriptEditPanel.java   
  *
  * Copyright (C) 2006-2007 www.interpss.org
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
  * @Date 11/15/2007
  * 
  *   Revision History
  *   ================
  *
  */

package org.interpss.editor.ui.edit.common;
  
import java.util.Vector;

import javax.swing.JDialog;

import org.interpss.db.BaseDataBean;
import org.interpss.editor.data.proj.CaseData;
import org.interpss.editor.jgraph.ui.edit.IFormDataPanel;
import org.interpss.editor.ui.util.CoreScriptUtilFunc;
import org.interpss.editor.ui.util.GUIFileUtil;
import org.interpss.editor.ui.util.ScriptJavacUtilFunc;
import org.interpss.spring.UISpringFactory;
import org.interpss.ui.ICustomPluginEditor;
import org.interpss.ui.IScriptPluginEditing;

import com.interpss.common.util.IpssLogger;
 
public class NBCustomScriptEditPanel extends javax.swing.JPanel implements IFormDataPanel {
	private static final long serialVersionUID = 1;
	
	public static enum Type {AclfBus, AclfBranch, AcscBus, AcscBranch, DynamicBusDevice};
	
	private JDialog parent = null;

    private BaseDataBean _data = null;
	private IScriptPluginEditing plugin = null;
	private Type scriptType;
    
	public void initPanel(JDialog aParent) {
		parent = aParent;
	}
    
	public void init(Object caseData, Object type) {
		_data = (BaseDataBean)caseData;
		scriptType = (Type)type;
		if (scriptType == Type.AclfBus)
			customPluginComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				UISpringFactory.getCustomAclfBusScriptPluginNameList()));
		else if (scriptType == Type.AclfBranch)
			customPluginComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				UISpringFactory.getCustomAclfBranchScriptPluginNameList()));
		else if (scriptType == Type.AcscBus)
			customPluginComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				UISpringFactory.getCustomAcscBusScriptPluginNameList()));
		else if (scriptType == Type.AcscBranch)
			customPluginComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				UISpringFactory.getCustomAcscBranchScriptPluginNameList()));
		else if (scriptType == Type.DynamicBusDevice)
			customPluginComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				UISpringFactory.getCustomDynamicBusDeviceScriptPluginNameList()));
		if (customPluginComboBox.getItemCount() == 0) {
    		scriptingRadioButton.setSelected(true);
    		customPluginRadioButton.setEnabled(false);
		}
	}
	
    public boolean setForm2Editor() {
		IpssLogger.getLogger().info("NBCustomScriptEditPanel setForm2Editor() called");

    	if (_data.getScriptLanguage() == CaseData.ScriptLanguage_Java || !customPluginRadioButton.isEnabled()) {
    		this.scriptingRadioButton.setSelected(true);
    	    scriptingRadioButtonActionPerformed(null);
    	}
    	else if (_data.getScriptLanguage() == CaseData.ScriptLanguage_Plugin) {
    		customPluginRadioButton.setSelected(true);
    		customPluginRadioButtonActionPerformed(null);
    	}

    	return true;
	}

    public boolean saveEditor2Form(Vector<String> errMsg) throws Exception {
		IpssLogger.getLogger().info("NBCustomScriptEditPanel saveEditor2Form() called");
		boolean ok = true;

		if (customPluginRadioButton.isSelected()) {
			_data.setScriptLanguage(CaseData.ScriptLanguage_Plugin);
			_data.setScriptPluginName((String)customPluginComboBox.getSelectedItem());
       		((ICustomPluginEditor)plugin.getEditPanel()).saveEditorData(errMsg);
			_data.setScriptPluginXmlStr(plugin.getDataXmlString());
		}
		else {
			if (!scriptTextArea.getText().startsWith(ScriptJavacUtilFunc.Token_CodeReuse)) {
				_data.setScriptLanguage(CaseData.ScriptLanguage_Java);
				String tagBaseClass = "", tagBegin = "", packageName = "", code = "";
	    		if (scriptType == Type.AclfBus) {
					tagBaseClass = CoreScriptUtilFunc.Tag_AclfScriptBus_Baseclass;
					tagBegin     = CoreScriptUtilFunc.Tag_AclfScriptBus_Begin;
		    		code = CoreScriptUtilFunc.parseAclfJavaCode(scriptTextArea.getText(), 
							ScriptJavacUtilFunc.CheckCodeClassname,	tagBaseClass, tagBegin);
					packageName  = CoreScriptUtilFunc.AclfScriptingPackageName;
	    		}
	    		else if (scriptType == Type.AclfBranch) {
					tagBaseClass = CoreScriptUtilFunc.Tag_AclfScriptBranch_Baseclass;
					tagBegin     = CoreScriptUtilFunc.Tag_AclfScriptBranch_Begin;
		    		code = CoreScriptUtilFunc.parseAclfJavaCode(scriptTextArea.getText(), 
							ScriptJavacUtilFunc.CheckCodeClassname,	tagBaseClass, tagBegin);
					packageName  = CoreScriptUtilFunc.AclfScriptingPackageName;
	    		}
	    		else if (scriptType == Type.AcscBus) {
					tagBaseClass = CoreScriptUtilFunc.Tag_AcscScriptBus_Baseclass;
					tagBegin     = CoreScriptUtilFunc.Tag_AcscScriptBus_Begin;
		    		code = CoreScriptUtilFunc.parseAcscJavaCode(scriptTextArea.getText(), 
							ScriptJavacUtilFunc.CheckCodeClassname,	tagBaseClass, tagBegin);
					packageName  = CoreScriptUtilFunc.AcscScriptingPackageName;
	    		}
	    		else if (scriptType == Type.AcscBranch) {
					tagBaseClass = CoreScriptUtilFunc.Tag_AcscScriptBranch_Baseclass;
					tagBegin     = CoreScriptUtilFunc.Tag_AcscScriptBranch_Begin;
		    		code = CoreScriptUtilFunc.parseAcscJavaCode(scriptTextArea.getText(), 
							ScriptJavacUtilFunc.CheckCodeClassname,	tagBaseClass, tagBegin);
					packageName  = CoreScriptUtilFunc.AcscScriptingPackageName;
	    		}
	    		else if (scriptType == Type.DynamicBusDevice) {
		    		code = CoreScriptUtilFunc.parseCMLTag(scriptTextArea.getText(), ScriptJavacUtilFunc.CheckCodeClassname, "");
					packageName  = CoreScriptUtilFunc.ScriptDynamicBusControllerPackageName;
	    		}

	    		if (!ScriptJavacUtilFunc.checkJavaCode(code, packageName)) {
	            	errMsg.add(new String("Java compile error"));
	        		return false;
				}
				_data.setScripts(this.scriptTextArea.getText());
			}
		}

	    return ok;
    }

    /** Creates new form AclfEditPanel */
    public NBCustomScriptEditPanel() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scriptingButtonGroup = new javax.swing.ButtonGroup();
        scriptPanel = new javax.swing.JPanel();
        scriptSelectPanel = new javax.swing.JPanel();
        scriptingRadioButton = new javax.swing.JRadioButton();
        customPluginRadioButton = new javax.swing.JRadioButton();
        customPluginComboBox = new javax.swing.JComboBox();
        scriptEditPanel = new javax.swing.JPanel();
        scriptScrollPane = new javax.swing.JScrollPane();
        scriptTextArea = new javax.swing.JTextArea();

        scriptPanel.setLayout(new java.awt.BorderLayout());

        scriptSelectPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 20, 5));

        scriptingButtonGroup.add(scriptingRadioButton);
        scriptingRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        scriptingRadioButton.setSelected(true);
        scriptingRadioButton.setText("CutomScripting");
        scriptingRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scriptingRadioButtonActionPerformed(evt);
            }
        });
        scriptSelectPanel.add(scriptingRadioButton);

        scriptingButtonGroup.add(customPluginRadioButton);
        customPluginRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        customPluginRadioButton.setText("CustomPlugin");
        customPluginRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                customPluginRadioButtonActionPerformed(evt);
            }
        });
        scriptSelectPanel.add(customPluginRadioButton);

        customPluginComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        customPluginComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        customPluginComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                customPluginComboBoxActionPerformed(evt);
            }
        });
        scriptSelectPanel.add(customPluginComboBox);

        scriptPanel.add(scriptSelectPanel, java.awt.BorderLayout.NORTH);

        scriptTextArea.setColumns(80);
        scriptTextArea.setFont(new java.awt.Font("Courier New", 0, 12));
        scriptTextArea.setRows(30);
        scriptTextArea.setTabSize(3);
        scriptScrollPane.setViewportView(scriptTextArea);

        scriptEditPanel.add(scriptScrollPane);

        scriptPanel.add(scriptEditPanel, java.awt.BorderLayout.CENTER);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 669, Short.MAX_VALUE)
            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(layout.createSequentialGroup()
                    .add(0, 0, Short.MAX_VALUE)
                    .add(scriptPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 664, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 474, Short.MAX_VALUE)
            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(layout.createSequentialGroup()
                    .add(0, 0, Short.MAX_VALUE)
                    .add(scriptPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 474, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(0, 0, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents
        
    private void scriptingRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scriptingRadioButtonActionPerformed
    	if (scriptingRadioButton.isSelected()) {
    		customPluginComboBox.setEnabled(false);
    		_data.setScriptLanguage(CaseData.ScriptLanguage_Java);
        	scriptEditPanel.removeAll();
        	scriptEditPanel.add(scriptScrollPane);
    	    setScriptTextArea();
        	parent.pack();
        	this.repaint();
    	}
    }//GEN-LAST:event_scriptingRadioButtonActionPerformed

    private void customPluginRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_customPluginRadioButtonActionPerformed
    	if (customPluginRadioButton.isSelected()) {
    		customPluginComboBox.setEnabled(true);
        	_data.setScriptLanguage(CaseData.ScriptLanguage_Plugin);
    		if (_data.getScriptPluginName() != null && !_data.getScriptPluginName().trim().equals("")) {
    			customPluginComboBox.setSelectedItem(_data.getScriptPluginName());
    		}
    	    customPluginComboBoxActionPerformed(null);
    	}
    }//GEN-LAST:event_customPluginRadioButtonActionPerformed

    private void customPluginComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_customPluginComboBoxActionPerformed
		String pluginName =  (String)customPluginComboBox.getSelectedItem();
		if (this.scriptType == Type.AclfBus)
			this.plugin = UISpringFactory.getCustomAclfBusScriptPlugin(pluginName);
		else if (this.scriptType == Type.AclfBranch)
			this.plugin = UISpringFactory.getCustomAclfBranchScriptPlugin(pluginName);
		else if (this.scriptType == Type.AcscBus)
			this.plugin = UISpringFactory.getCustomAcscBusScriptPlugin(pluginName);
		else if (this.scriptType == Type.AcscBranch)
			this.plugin = UISpringFactory.getCustomAcscBranchScriptPlugin(pluginName);
		else if (this.scriptType == Type.DynamicBusDevice)
			this.plugin = UISpringFactory.getCustomDynamicBusDeviceScriptPlugin(pluginName);
    	scriptEditPanel.removeAll();
    	scriptEditPanel.add(plugin.getEditPanel());
		if (!_data.getScriptPluginXmlStr().trim().equals(""))
   			plugin.setData(_data.getScriptPluginXmlStr());
   		((ICustomPluginEditor)plugin.getEditPanel()).setData2Editor(plugin.getDesc());
    	parent.pack();
    	this.repaint();
    }//GEN-LAST:event_customPluginComboBoxActionPerformed
    
    private void setScriptTextArea() {
		if (_data.getScripts() != null && !_data.getScripts().trim().equals("")) {
    		scriptTextArea.setText(_data.getScripts());
    	}
    	else {
    		// load from the template
    		String filename = "";
    		if (scriptType == Type.AclfBus)
	    		filename = CoreScriptUtilFunc.AclfBusTemplateFilename;
    		else if (scriptType == Type.AclfBranch)
	    		filename = CoreScriptUtilFunc.AclfBranchTemplateFilename;
    		else if (scriptType == Type.AcscBus)
	    		filename = CoreScriptUtilFunc.AcscBusTemplateFilename;
    		else if (scriptType == Type.AcscBranch)
	    		filename = CoreScriptUtilFunc.AcscBranchTemplateFilename;
    		else if (scriptType == Type.DynamicBusDevice)
	    		filename = CoreScriptUtilFunc.DynamicBusDeviceTemplateFilename;
    		
    		GUIFileUtil.readFile2TextareaRativePath(filename, scriptTextArea);
    	}
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox customPluginComboBox;
    private javax.swing.JRadioButton customPluginRadioButton;
    private javax.swing.JPanel scriptEditPanel;
    private javax.swing.JPanel scriptPanel;
    private javax.swing.JScrollPane scriptScrollPane;
    private javax.swing.JPanel scriptSelectPanel;
    private javax.swing.JTextArea scriptTextArea;
    private javax.swing.ButtonGroup scriptingButtonGroup;
    private javax.swing.JRadioButton scriptingRadioButton;
    // End of variables declaration//GEN-END:variables
}
