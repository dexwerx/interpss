 /*
  * @(#)NBLoadScheduleDialog.java   
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

package org.interpss.editor.ui.edit.dist.bus;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.apache.commons.math.complex.Complex;
import org.interpss.editor.data.dist.DistBusData;
import org.interpss.editor.form.GFormContainer;
import org.interpss.editor.form.GNetForm;
import org.interpss.editor.jgraph.ui.IGraphicEditor;
import org.interpss.editor.jgraph.ui.edit.IFormDataDialog;
import org.interpss.editor.refData.LoadScheduleItem;
import org.interpss.editor.refData.LoadScheduleRefData;
import org.interpss.spring.PluginSpringFactory;
import org.interpss.ui.IRefDataManager;
import org.interpss.ui.WinUtilities;

import com.interpss.common.util.IpssLogger;
 
public class NBLoadScheduleDialog extends javax.swing.JDialog implements IFormDataDialog {
	private static final long serialVersionUID = 1;

	private GFormContainer netContainer = null;
	private DistBusData busData = null;
	
	/**
	*	Constructor
	*
	* @parem parent the parent Frame object	
	*/
	public NBLoadScheduleDialog(java.awt.Frame parent) {
        super(parent, "Load Schedule Editor", true);
        initComponents();
        WinUtilities.center(this);
    }

	public NBLoadScheduleDialog(IGraphicEditor parent) {
        this(parent.getFrame());
    }
	
	public boolean isReturnOk() {
		return true;
	}
	
	/**
	*
	* @param netContainer 
	* @param obj the DistBusData object
	*/    
	public void init(Object aNetContainer, Object obj) {
		IpssLogger.getLogger().info("NBLoadScheduleDialog.init() called");
		
		netContainer = (GFormContainer)aNetContainer;
		busData = (DistBusData)obj;
		
    	setForm2Editor();
		
		pack();
        setVisible(true);
	}

	/**
	*	Set form data to the editor
	*
	* @return false if there is any problem
	*/	
    public boolean setForm2Editor() {
		IpssLogger.getLogger().info("NBLoadScheduleDialog.setForm2Editor() called");

	    LoadScheduleRefData refData = (LoadScheduleRefData)PluginSpringFactory.getRefDataManager().
							getRefDataObject(IRefDataManager.REFDATA_LoadSchedule);
	    refDataNameComboBox.setModel(new javax.swing.DefaultComboBoxModel(refData.getScheduleNameList(
	    				((GNetForm)netContainer.getGNetForm()).getDistNetData().getLoadSchedulePeriodUnit())));
        setTableData();

	    return true;
    }
    
	/**
	*	Save editor screen data to the form
	*
	* @param errMsg error messages during the saving process.
	* @return false if there is any problem
	*/
	public boolean saveEditor2Form(Vector errMsg) throws Exception {
		IpssLogger.getLogger().info("NBLoadScheduleDialog.saveEditor2Form() called");

		saveTableData();

		return true;
    }
	
    private DefaultTableModel getScheduleDataModel(){
    	return (DefaultTableModel)this.sheduleDataTable.getModel();
    }
    
    private void setTableData() {
    	int points = ((GNetForm)netContainer.getGNetForm()).getDistNetData().getLoadSchedulePoints();
    	int row = points/3;
    	if (points % 3 > 0) row++;
    	getScheduleDataModel().setRowCount(row);

    	int cnt = 0;
    	for (int i = 0; i < row; i++) {
        	for (int j = 0; j < 3; j++) {
        		cnt++;
        		if (cnt <= points) {
        			getScheduleDataModel().setValueAt(new Integer(cnt), i, j*3);
        			Complex c = busData.getLoadSchedule(cnt-1);
        			if (c == null) c = new Complex(100.0, 100.0);
        			getScheduleDataModel().setValueAt(new Double(c.getReal()), i, j*3+1);
        			getScheduleDataModel().setValueAt(new Double(c.getImaginary()), i, j*3+2);
        		}
        		else {
        			getScheduleDataModel().setValueAt("", i, j*3);
        			getScheduleDataModel().setValueAt(new Double(0.0), i, j*3+1);
        			getScheduleDataModel().setValueAt(new Double(0.0), i, j*3+2);
        		}
    		}
    	}
	}    
    
    private void saveTableData() {
    	int points = ((GNetForm)netContainer.getGNetForm()).getDistNetData().getLoadSchedulePoints();
    	int row = points/3;
    	if (points % 3 > 0) row++;
    	
    	int cnt = 0;
    	for (int i = 0; i < row; i++) {
        	for (int j = 0; j < 3; j++) {
        		cnt++;
        		if (cnt <= points) {
        			double p = ((Double)getScheduleDataModel().getValueAt(i, j*3+1)).doubleValue();
        			double q = ((Double)getScheduleDataModel().getValueAt(i, j*3+2)).doubleValue();
        			busData.setLoadSchedule(new Complex(p,q), cnt-1);
        		}
    		}
    	}
	}  
    
	// The following code is controlled by NetBean IDE
	// ===============================================
	
    /** Creates new form NBNetEditDialog */
    public NBLoadScheduleDialog(java.awt.Frame parent, boolean modal) {
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
        dataInputPanel = new javax.swing.JPanel();
        refDataSelectPanel = new javax.swing.JPanel();
        refDataNameLabel = new javax.swing.JLabel();
        refDataNameComboBox = new javax.swing.JComboBox();
        refDataCopyButton = new javax.swing.JButton();
        loadSchedulePanel = new javax.swing.JPanel();
        tableTitlePanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        sheduleDataTable = new javax.swing.JTable();
        controlPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.BorderLayout(10, 10));

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        dataInputPanel.setLayout(new java.awt.BorderLayout());

        dataInputPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
        dataInputPanel.setFont(new java.awt.Font("Dialog", 0, 10));
        dataInputPanel.setMinimumSize(new java.awt.Dimension(258, 135));
        dataInputPanel.setPreferredSize(new java.awt.Dimension(500, 280));
        refDataSelectPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 20, 5));

        refDataSelectPanel.setName("");
        refDataNameLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        refDataNameLabel.setText("Ref Lib Data");
        refDataSelectPanel.add(refDataNameLabel);

        refDataNameComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        refDataNameComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        refDataSelectPanel.add(refDataNameComboBox);

        refDataCopyButton.setFont(new java.awt.Font("Dialog", 0, 10));
        refDataCopyButton.setText("Copy");
        refDataCopyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refDataCopyButtonActionPerformed(evt);
            }
        });

        refDataSelectPanel.add(refDataCopyButton);

        dataInputPanel.add(refDataSelectPanel, java.awt.BorderLayout.NORTH);

        loadSchedulePanel.setLayout(new java.awt.BorderLayout());

        loadSchedulePanel.setPreferredSize(new java.awt.Dimension(400, 160));
        tableTitlePanel.setLayout(new java.awt.GridLayout(1, 0));

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Point");
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tableTitlePanel.add(jLabel4);

        jLabel5.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("P(%)");
        jLabel5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tableTitlePanel.add(jLabel5);

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Q(%)");
        jLabel3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tableTitlePanel.add(jLabel3);

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Point");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tableTitlePanel.add(jLabel1);

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("P(%)");
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tableTitlePanel.add(jLabel2);

        jLabel6.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Q(%)");
        jLabel6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tableTitlePanel.add(jLabel6);

        jLabel7.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Point");
        jLabel7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tableTitlePanel.add(jLabel7);

        jLabel8.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("P(%)");
        jLabel8.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tableTitlePanel.add(jLabel8);

        jLabel9.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Q(%)");
        jLabel9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tableTitlePanel.add(jLabel9);

        loadSchedulePanel.add(tableTitlePanel, java.awt.BorderLayout.CENTER);

        sheduleDataTable.setFont(new java.awt.Font("Dialog", 0, 10));
        sheduleDataTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"1", new Double(100.0), new Double(100.0), "9", new Double(100.0), null, "17", new Double(100.0), new Double(100.0)},
                {"2", new Double(100.0), new Double(100.0), "10", new Double(100.0), null, "18", new Double(100.0), new Double(100.0)},
                {"3", new Double(100.0), new Double(100.0), "11", new Double(100.0), null, "19", new Double(100.0), new Double(100.0)},
                {"4", new Double(100.0), new Double(100.0), "12", new Double(100.0), null, "20", new Double(100.0), new Double(100.0)},
                {"5", new Double(100.0), new Double(100.0), "13", new Double(100.0), null, "21", new Double(100.0), new Double(100.0)},
                {"6", new Double(100.0), new Double(100.0), "14", new Double(100.0), null, "22", new Double(100.0), new Double(100.0)},
                {"7", new Double(100.0), new Double(100.0), "15", new Double(100.0), null, "23", new Double(100.0), new Double(100.0)},
                {"8", new Double(100.0), new Double(100.0), "16", new Double(100.0), null, "24", new Double(100.0), new Double(100.0)}
            },
            new String [] {
                "point", "pLoading", "qLoading", "point", "pLoading", "qLoading", "point", "pLoading", "qLoading"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, false, true, true, false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        sheduleDataTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        sheduleDataTable.setAutoscrolls(false);
        sheduleDataTable.setCellSelectionEnabled(true);
        sheduleDataTable.setMinimumSize(new java.awt.Dimension(100, 112));
        sheduleDataTable.setName("zTable");
        sheduleDataTable.setPreferredSize(new java.awt.Dimension(400, 130));
        loadSchedulePanel.add(sheduleDataTable, java.awt.BorderLayout.SOUTH);

        dataInputPanel.add(loadSchedulePanel, java.awt.BorderLayout.CENTER);

        controlPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 20, 10));

        controlPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 1, 1, 1));
        okButton.setFont(new java.awt.Font("Dialog", 0, 12));
        okButton.setText("Ok");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        controlPanel.add(okButton);

        cancelButton.setFont(new java.awt.Font("Dialog", 0, 12));
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        controlPanel.add(cancelButton);

        dataInputPanel.add(controlPanel, java.awt.BorderLayout.SOUTH);

        getContentPane().add(dataInputPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void refDataCopyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refDataCopyButtonActionPerformed
	    String schedule = (String)refDataNameComboBox.getSelectedItem();
	    LoadScheduleRefData refData = (LoadScheduleRefData)PluginSpringFactory.getRefDataManager().
	    									getRefDataObject(IRefDataManager.REFDATA_LoadSchedule);
	    Object[] itemList = refData.getItemList(schedule);

    	int points = ((GNetForm)netContainer.getGNetForm()).getDistNetData().getLoadSchedulePoints();
    	for (int i = 0; i < points; i++) {
    		LoadScheduleItem item = (LoadScheduleItem)itemList[i];
        	busData.setLoadSchedule(new Complex(item.getP_percent(), item.getQ_percent()), i);
    	}		
    	setTableData();
    }//GEN-LAST:event_refDataCopyButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
    	setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
		IpssLogger.getLogger().info("NBLoadScheduleDialog.okButtonActionPerformed() called");
		Vector errMsg = new Vector();
		try {
        	if (!saveEditor2Form(errMsg)) {
        		PluginSpringFactory.getEditorDialogUtil().showMsgDialog(this, "Load Schedule Data Error", errMsg);
        		IpssLogger.getLogger().info("Load Schedule Data Error" + errMsg.toString());
				return;
        	}
        } catch (Exception e) {
      		IpssLogger.logErr(e);
      		PluginSpringFactory.getEditorDialogUtil().showMsgDialog(this, "Load Schedule Data Error", e.toString());
			return;
        }	
    	setVisible(false);
    }//GEN-LAST:event_okButtonActionPerformed
    
    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
    }//GEN-LAST:event_closeDialog
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new NBLoadScheduleDialog(new javax.swing.JFrame(), true).show();
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JPanel dataInputPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel loadSchedulePanel;
    private javax.swing.JButton okButton;
    private javax.swing.JButton refDataCopyButton;
    private javax.swing.JComboBox refDataNameComboBox;
    private javax.swing.JLabel refDataNameLabel;
    private javax.swing.JPanel refDataSelectPanel;
    private javax.swing.JTable sheduleDataTable;
    private javax.swing.JPanel tableTitlePanel;
    // End of variables declaration//GEN-END:variables

	class DataVerifier extends javax.swing.InputVerifier {
    	public boolean verify(javax.swing.JComponent input) {
			try {
 	       	} catch (Exception e) {
				return false;
 	       	}				
			return true;
        }
    }
}
