 /*
  * @(#)NBDynaEventPanel.java   
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

package org.interpss.editor.ui.run.common;

import java.util.Vector;

import javax.swing.JDialog;

import org.interpss.editor.data.dstab.DStabDEventData;
import org.interpss.editor.jgraph.ui.edit.IFormDataPanel;
import org.interpss.spring.BasePluginSpringFactory;
import org.interpss.spring.EditorSimuSpringFactory;
import org.interpss.xml.IpssXmlHelper;
import org.interpss.xml.IpssXmlParser;
import org.interpss.xml.StudyCaseHanlder;
import org.interpss.xml.schema.AcscFaultDataType;
import org.interpss.xml.schema.DynamicEventDataType;
import org.interpss.xml.schema.DynamicEventDataXmlType;
import org.interpss.xml.schema.DynamicEventXmlType;

import com.interpss.common.util.IpssLogger;

public class NBDynaEventPanel extends javax.swing.JPanel implements IFormDataPanel {
	private static final long serialVersionUID = 1;

	private NBDStabFaultDataPanel  _dstabFaultDataPanel = new NBDStabFaultDataPanel();
	private NBDStabLoadChangePanel _loadChangePanel = new NBDStabLoadChangePanel();
	private NBBranchOutagePanel _branchOutagePanel = new NBBranchOutagePanel();

//	private DStabCaseData   _caseData = null;   // current case data
//	private DStabDEventData _eventData = null;  // current event data
	
    private DynamicEventDataXmlType xmlEventData;
    private DynamicEventXmlType currentEvent;  // pointer to the current event being edited
    
	private JDialog parentDialog = null;

    /** Creates new form NBCaseInfoDialog */
    public NBDynaEventPanel(JDialog parent) {
    	parentDialog = parent;
        initComponents();

        eventInputPanel.add(_dstabFaultDataPanel);
    }
    
    public void init(Object netContainer, Object simuCtx) {
		IpssLogger.getLogger().info("NBDStabCasePanel init() called");
	    _dstabFaultDataPanel.init(netContainer, simuCtx);
	    _loadChangePanel.init(netContainer, simuCtx);
	    _branchOutagePanel.init(netContainer, simuCtx);
    }

    public void setCaseData(DynamicEventDataXmlType data) {
    	xmlEventData = data;
    	// This panel does not remember the event name lastly edited, it starts with
    	// the first event in the list. The getAnyEventData method creates a new event if no exiting event
    	this.currentEvent = getFirstEvent(data);
   		//_eventData = _caseData.getAnyEventData();
    	// update the combox for event name list
		this.eventListComboBox.setModel(new javax.swing.DefaultComboBoxModel(
					IpssXmlHelper.getRecNameArray(this.xmlEventData.getEventList().getEvent())));
		setCurrentEventData(this.currentEvent);
    }
    
    private DynamicEventXmlType getFirstEvent(DynamicEventDataXmlType data) {
    	if (data.getEventList().getEvent().size() == 0) {
    		DynamicEventXmlType event = IpssXmlParser.getFactory().createDynamicEventXmlType();
    		data.getEventList().getEvent().add(event);
    		StudyCaseHanlder.setDefaultEventData(event);
    	}
    	return  data.getEventList().getEvent().get(0);
    }
    
    private void setCurrentEventData(DynamicEventXmlType eventData) {
		// update the event data editing screen
        eventInputPanel.removeAll();
        if (eventData.getEventType() == DynamicEventDataType.LOAD_CHANGE) {
    	    _loadChangePanel.setLoadChangeData(eventData.getLoadChangeData());
            eventInputPanel.add(_loadChangePanel);
        }
        else if (eventData.getEventType() == DynamicEventDataType.FAULT) {
        	if (eventData.getFault().getFaultType() == AcscFaultDataType.BRANCH_OUTAGE) {
        		_branchOutagePanel.setDStabDEventData(eventData);
        		eventInputPanel.add(_branchOutagePanel);
        	}
        	else {
        		_dstabFaultDataPanel.setDStabDEventData(eventData);
        		eventInputPanel.add(_dstabFaultDataPanel);
        	}
        }
    }
    
    /**
	*	Set form data to the editor
	*
	* @return false if there is any problem
	*/
	public boolean setForm2Editor() {
		IpssLogger.getLogger().info("NBDynaEventPanel setForm2Editor() called");

	    setCurrentEventData(this.currentEvent);
		
        if (this.currentEvent.getRecName() != null && !this.currentEvent.getRecName().equals(""))
        	eventListComboBox.setSelectedItem(this.currentEvent.getRecName());
        
        if (this.currentEvent.getEventType() == DynamicEventDataType.LOAD_CHANGE) {
            loadChangeRadioButton.setSelected(true);        	
        	_loadChangePanel.setForm2Editor();
        }
        if (this.currentEvent.getEventType() == DynamicEventDataType.FAULT) {
        	if (this.currentEvent.getFault().getFaultType() == AcscFaultDataType.BRANCH_FAULT) {
        		branchFaultRadioButton.setSelected(true);
        	}
            else if (this.currentEvent.getFault().getFaultType() == AcscFaultDataType.BRANCH_OUTAGE) {
                branchOutageRadioButton.setSelected(true);        	
            	_branchOutagePanel.setForm2Editor();
            }
        	else {
        		busFaultRadioButton.setSelected(true);
        	}
        	_dstabFaultDataPanel.setForm2Editor();
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
		IpssLogger.getLogger().info("NBDynaEventPanel saveEditor2Form() called");

		boolean ok = true;

		String eventName = (String)eventListComboBox.getSelectedItem();
    	if (eventName.equals(DStabDEventData.NewEventName) || eventName.trim().equals("")) {
    		// no data defined
			return ok;
    	}
    	this.currentEvent.setRecName(eventName);

        if (this.currentEvent.getEventType() == DynamicEventDataType.LOAD_CHANGE) {
            if (!_loadChangePanel.saveEditor2Form(errMsg))
            	ok = false;
        }
        else if (this.currentEvent.getEventType() == DynamicEventDataType.FAULT) {
            if (!_dstabFaultDataPanel.saveEditor2Form(errMsg))
            	ok = false;

            if (this.currentEvent.getFault().getFaultType() == AcscFaultDataType.BRANCH_FAULT && !this.currentEvent.isPermanent()) {
    			if (this.currentEvent.getFault().isBranchReclosure()) {
    				if (this.currentEvent.getFault().getReclosureTime() <= (this.currentEvent.getStartTimeSec()+this.currentEvent.getDurationSec())) {
    	    			errMsg.add("Branch reclosure at time <= start+duration");
    	    			ok = false;
    				}
    			}
    		}
            else if (this.currentEvent.getFault().getFaultType() == AcscFaultDataType.BRANCH_OUTAGE) {
                if (!_branchOutagePanel.saveEditor2Form(errMsg))
                	ok = false;
            }
        }

		return ok;
	}
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        eventTypeButtonGroup = new javax.swing.ButtonGroup();
        dynamicEventPanel = new javax.swing.JPanel();
        eventListLabel = new javax.swing.JLabel();
        eventListComboBox = new javax.swing.JComboBox();
        holdingPanel = new javax.swing.JPanel();
        busFaultRadioButton = new javax.swing.JRadioButton();
        branchFaultRadioButton = new javax.swing.JRadioButton();
        branchOutageRadioButton = new javax.swing.JRadioButton();
        loadChangeRadioButton = new javax.swing.JRadioButton();
        eventInputPanel = new javax.swing.JPanel();
        controlPanel = new javax.swing.JPanel();
        addEventButton = new javax.swing.JButton();
        saveEventButton = new javax.swing.JButton();
        deleteEventButton = new javax.swing.JButton();

        eventListLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        eventListLabel.setText("Dynamic Event List     ");

        eventListComboBox.setEditable(true);
        eventListComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        eventListComboBox.setName("eventListComboBox"); // NOI18N
        eventListComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eventListComboBoxActionPerformed(evt);
            }
        });

        holdingPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Event Type", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10)));
        holdingPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));

        eventTypeButtonGroup.add(busFaultRadioButton);
        busFaultRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        busFaultRadioButton.setSelected(true);
        busFaultRadioButton.setText("Bus Fault");
        busFaultRadioButton.setName("busFaultRadioButton"); // NOI18N
        busFaultRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                busFaultRadioButtonActionPerformed(evt);
            }
        });
        holdingPanel.add(busFaultRadioButton);

        eventTypeButtonGroup.add(branchFaultRadioButton);
        branchFaultRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        branchFaultRadioButton.setText("Branch Fault");
        branchFaultRadioButton.setName("branchFaultRadioButton"); // NOI18N
        branchFaultRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                branchFaultRadioButtonActionPerformed(evt);
            }
        });
        holdingPanel.add(branchFaultRadioButton);

        eventTypeButtonGroup.add(branchOutageRadioButton);
        branchOutageRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        branchOutageRadioButton.setText("Branch Outage");
        branchOutageRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        branchOutageRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        branchOutageRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                branchOutageRadioButtonActionPerformed(evt);
            }
        });
        holdingPanel.add(branchOutageRadioButton);

        eventTypeButtonGroup.add(loadChangeRadioButton);
        loadChangeRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
        loadChangeRadioButton.setText("Load Change");
        loadChangeRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        loadChangeRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        loadChangeRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadChangeRadioButtonActionPerformed(evt);
            }
        });
        holdingPanel.add(loadChangeRadioButton);

        controlPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 10));

        addEventButton.setFont(new java.awt.Font("Dialog", 0, 12));
        addEventButton.setText("AddEvent");
        addEventButton.setName("addEventButton"); // NOI18N
        addEventButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addEventButtonActionPerformed(evt);
            }
        });
        controlPanel.add(addEventButton);

        saveEventButton.setFont(new java.awt.Font("Dialog", 0, 12));
        saveEventButton.setText("SaveEvent");
        saveEventButton.setName("saveEventButton"); // NOI18N
        saveEventButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveEventButtonActionPerformed(evt);
            }
        });
        controlPanel.add(saveEventButton);

        deleteEventButton.setFont(new java.awt.Font("Dialog", 0, 12));
        deleteEventButton.setText("DeleteEvent");
        deleteEventButton.setName("deleteEventButton"); // NOI18N
        deleteEventButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteEventButtonActionPerformed(evt);
            }
        });
        controlPanel.add(deleteEventButton);

        org.jdesktop.layout.GroupLayout dynamicEventPanelLayout = new org.jdesktop.layout.GroupLayout(dynamicEventPanel);
        dynamicEventPanel.setLayout(dynamicEventPanelLayout);
        dynamicEventPanelLayout.setHorizontalGroup(
            dynamicEventPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(dynamicEventPanelLayout.createSequentialGroup()
                .add(80, 80, 80)
                .add(dynamicEventPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(holdingPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(dynamicEventPanelLayout.createSequentialGroup()
                        .add(50, 50, 50)
                        .add(eventListLabel)
                        .add(18, 18, 18)
                        .add(eventListComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(76, 76, 76))
            .add(dynamicEventPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(eventInputPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 541, Short.MAX_VALUE)
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, dynamicEventPanelLayout.createSequentialGroup()
                .addContainerGap(137, Short.MAX_VALUE)
                .add(controlPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(133, 133, 133))
        );
        dynamicEventPanelLayout.setVerticalGroup(
            dynamicEventPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(dynamicEventPanelLayout.createSequentialGroup()
                .add(18, 18, 18)
                .add(dynamicEventPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(eventListComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(eventListLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(holdingPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(4, 4, 4)
                .add(eventInputPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
                .add(11, 11, 11)
                .add(controlPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(dynamicEventPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(dynamicEventPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void deleteEventButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteEventButtonActionPerformed
    	int cnt = 0, index = -1;
    	for (DynamicEventXmlType event: this.xmlEventData.getEventList().getEvent()) {
    		if (event.getRecName().equals(this.currentEvent.getRecName()))
    			index = cnt;
    		cnt++;		
    	}
    	if (index != -1)
    		this.xmlEventData.getEventList().getEvent().remove(index);
    	
		// if no event left, a new event will be created by the getAnyEventData() call
    	this.currentEvent = getFirstEvent(this.xmlEventData);
		// update the event name list combo box
		this.eventListComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				IpssXmlHelper.getRecNameArray(this.xmlEventData.getEventList().getEvent())));
		// update the event data screen
		setForm2Editor();
    }//GEN-LAST:event_deleteEventButtonActionPerformed

    private void addEventButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addEventButtonActionPerformed
    	this.currentEvent = IpssXmlParser.getFactory().createDynamicEventXmlType();
    	this.xmlEventData.getEventList().getEvent().add(this.currentEvent);
    	this.currentEvent.setRecName("<Not Defined>");
		// update the event name list combo box
		this.eventListComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				IpssXmlHelper.getRecNameArray(this.xmlEventData.getEventList().getEvent())));
		this.eventListComboBox.setSelectedItem(this.currentEvent.getRecName());
	    busFaultRadioButtonActionPerformed(null);
	    // set fault or load change editor panel pointing to the current eventData object
	    setCurrentEventData(this.currentEvent);
    }//GEN-LAST:event_addEventButtonActionPerformed

    private void saveEventButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveEventButtonActionPerformed
		Vector<String> errMsg = new Vector<String>();
		try {
			if (!saveEditor2Form(errMsg)) {
				BasePluginSpringFactory.getEditorDialogUtil().showMsgDialog(parentDialog, "Input Data Error", errMsg);
        		return;
			}
		} catch (Exception e) {
			IpssLogger.logErr(e);
			BasePluginSpringFactory.getEditorDialogUtil().showMsgDialog(parentDialog, "Input Data Error", e.toString());
       		return;
		}
		// event name may be modified, refresh the event list
		this.eventListComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				IpssXmlHelper.getRecNameArray(this.xmlEventData.getEventList().getEvent())));
		this.eventListComboBox.setSelectedItem(this.currentEvent.getRecName());
		// selected event may have been changed, refresh the screen
		eventListComboBoxActionPerformed(null);
    }//GEN-LAST:event_saveEventButtonActionPerformed

    private void eventListComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eventListComboBoxActionPerformed
		String eventName = (String)this.eventListComboBox.getSelectedItem();
		DynamicEventXmlType event = (DynamicEventXmlType)IpssXmlHelper
					.getRecordByName(eventName, this.xmlEventData.getEventList().getEvent());
    	if (event != null)        // event list selection changed
    		this.currentEvent = event;       
    	else {                    // event name changed
    		IpssLogger.getLogger().info("Event name changed to " + eventName);
    		this.currentEvent.setRecName(eventName);   
    	}
        setForm2Editor();
    	EditorSimuSpringFactory.getCaseInfoDialog().pack();
    }//GEN-LAST:event_eventListComboBoxActionPerformed

    private void branchFaultRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_branchFaultRadioButtonActionPerformed
    	this.currentEvent.setEventType(DynamicEventDataType.FAULT);
    	this.currentEvent.getFault().setFaultType(AcscFaultDataType.BRANCH_FAULT);
    	IpssLogger.getLogger().info("Branch fault event :" + this.currentEvent.getRecName());
    	// refresh the fault data editing screen, which is depending on the caseData.faulData object
        refreshEditorPanel();
    }//GEN-LAST:event_branchFaultRadioButtonActionPerformed

    private void busFaultRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_busFaultRadioButtonActionPerformed
    	this.currentEvent.setEventType(DynamicEventDataType.FAULT);
    	this.currentEvent.getFault().setFaultType(AcscFaultDataType.BUS_FAULT);
    	IpssLogger.getLogger().info("Bus fault event :" + this.currentEvent.getRecName());
    	// refresh the fault data editing screen, which is depending on the caseData.faulData object
        refreshEditorPanel();
    }//GEN-LAST:event_busFaultRadioButtonActionPerformed
    
    private void loadChangeRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadChangeRadioButtonActionPerformed
    	this.currentEvent.setEventType(DynamicEventDataType.LOAD_CHANGE);
    	IpssLogger.getLogger().info("LoadChange event :" + this.currentEvent.getRecName());
    	// refresh the fault data editing screen, which is depending on the object
        refreshEditorPanel();
    }//GEN-LAST:event_loadChangeRadioButtonActionPerformed

    private void branchOutageRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_branchOutageRadioButtonActionPerformed
    	this.currentEvent.setEventType(DynamicEventDataType.FAULT);
    	this.currentEvent.getFault().setFaultType(AcscFaultDataType.BRANCH_OUTAGE);
    	IpssLogger.getLogger().info("Branch outage event :" + this.currentEvent.getRecName());
    	// refresh the fault data editing screen, which is depending on the caseData.faulData object
        refreshEditorPanel();
    }//GEN-LAST:event_branchOutageRadioButtonActionPerformed

    private void refreshEditorPanel() {
        eventInputPanel.removeAll();
		if (this.currentEvent.getEventType() == DynamicEventDataType.LOAD_CHANGE) {
			eventInputPanel.add(_loadChangePanel);
		}
		else if (this.currentEvent.getEventType() == DynamicEventDataType.FAULT) {
			if (this.currentEvent.getFault().getFaultType() == AcscFaultDataType.BRANCH_OUTAGE) {
				eventInputPanel.add(_branchOutagePanel);
			}
			else {
		        eventInputPanel.add(_dstabFaultDataPanel);
	    		_dstabFaultDataPanel.refresh();
			}
		}
    	EditorSimuSpringFactory.getCaseInfoDialog().pack();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addEventButton;
    private javax.swing.JRadioButton branchFaultRadioButton;
    private javax.swing.JRadioButton branchOutageRadioButton;
    private javax.swing.JRadioButton busFaultRadioButton;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JButton deleteEventButton;
    private javax.swing.JPanel dynamicEventPanel;
    private javax.swing.JPanel eventInputPanel;
    private javax.swing.JComboBox eventListComboBox;
    private javax.swing.JLabel eventListLabel;
    private javax.swing.ButtonGroup eventTypeButtonGroup;
    private javax.swing.JPanel holdingPanel;
    private javax.swing.JRadioButton loadChangeRadioButton;
    private javax.swing.JButton saveEventButton;
    // End of variables declaration//GEN-END:variables
    
}
