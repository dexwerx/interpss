 /*
  * @(#)NBZInputPanel.java   
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

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import org.interpss.editor.data.common.ScPointData;
import org.interpss.editor.data.common.ZData;
import org.interpss.editor.data.dist.DistBusData;
import org.interpss.editor.data.dist.DistNetData;
import org.interpss.editor.form.GBusForm;
import org.interpss.editor.form.GFormContainer;
import org.interpss.editor.form.GNetForm;
import org.interpss.editor.jgraph.ui.edit.IFormDataPanel;
import org.interpss.editor.ui.util.NetDataUtil;
import org.interpss.ui.SwingInputVerifyUtil;

import com.interpss.common.util.IpssLogger;


public class NBZInputPanel extends javax.swing.JPanel  implements IFormDataPanel {
	private static final long serialVersionUID = 1;

	private GFormContainer netContainer = null;
    private GBusForm  form = null;
    private DistBusData  data = null;
    private int _rows = 5;   // zTable rows
    
    private boolean editingTable = false;
    private boolean passEditing = false;

    public NBZInputPanel(int rows) {
        _rows = rows;
        initComponents();
    }

    private DefaultTableModel getScPointDataModel(){
    	return (DefaultTableModel)this.scPointZTable.getModel();
    }

    private DefaultTableModel getNormalDataModel(){
    	return (DefaultTableModel)this.normalZTable.getModel();
    }

    public void setScPointTableModelListener() {
		getScPointDataModel().addTableModelListener( new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				if (editingTable && e.getType() == TableModelEvent.UPDATE) {
					if (passEditing) {
						passEditing = false;
					}
					else {
						int i = e.getFirstRow();
						if (e.getColumn() == 1 || e.getColumn() == 2) {
							//System.out.println("modify x/r");
							double x = SwingInputVerifyUtil.getDouble(getScPointDataModel().getValueAt(i, 1));
							double r = SwingInputVerifyUtil.getDouble(getScPointDataModel().getValueAt(i, 2));
							passEditing = true;
							getScPointDataModel().setValueAt(new Double(NetDataUtil.ratio(x,r)), i, 3);
						}	
						else if (e.getColumn() == 3) {
							//System.out.println("modify r");
							double x = SwingInputVerifyUtil.getDouble(getScPointDataModel().getValueAt(i, 1));
							double x_r = SwingInputVerifyUtil.getDouble(getScPointDataModel().getValueAt(i, 3));
							passEditing = true;
							if (x_r != 0.0)
								getScPointDataModel().setValueAt(new Double(NetDataUtil.calValue(x,1.0/x_r)), i, 2);
						}	
					}	
				}
			}
		}); 
	}
    
    public void setNormalTableModelListener() {
		getNormalDataModel().addTableModelListener( new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				if (editingTable && e.getType() == TableModelEvent.UPDATE) {
					if (passEditing) {
						passEditing = false;
					}
					else {
						int i = e.getFirstRow();
						if (e.getColumn() == 1 || e.getColumn() == 2) {
							//System.out.println("modify x/r");
							double x = SwingInputVerifyUtil.getDouble(getNormalDataModel().getValueAt(i, 1));
							double r = SwingInputVerifyUtil.getDouble(getNormalDataModel().getValueAt(i, 2));
							passEditing = true;
							getScPointDataModel().setValueAt(new Double(NetDataUtil.ratio(x,r)), i, 3);
						}	
						else if (e.getColumn() == 3) {
							//System.out.println("modify r");
							double x = SwingInputVerifyUtil.getDouble(getNormalDataModel().getValueAt(i, 1));
							double x_r = SwingInputVerifyUtil.getDouble(getNormalDataModel().getValueAt(i, 3));
							passEditing = true;
							if (x_r != 0.0)
								getNormalDataModel().setValueAt(new Double(NetDataUtil.calValue(x,1.0/x_r)), i, 2);
						}	
					}	
				}
			}
		}); 
	}

    public void init(Object aNetContainer, Object aform) {
		IpssLogger.getLogger().info("NBZInputPanel init() called");

		this.netContainer = (GFormContainer)aNetContainer;
		this.form = (GBusForm)aform;
		this.data = this.form.getDistBusData();

        editPanel.remove(scPointPanel);
        editPanel.remove(normalZPanel);
        if (((GNetForm)((GFormContainer)aNetContainer).getGNetForm()).getDistNetData().getScStd().equals(DistNetData.ScStd_Generic)) {
            editPanel.add(normalZPanel, java.awt.BorderLayout.CENTER);
        	setNormalTableModelListener();
        }
        else {
            editPanel.add(scPointPanel, java.awt.BorderLayout.NORTH);
        	setScPointTableModelListener();
        }

        setForm2Editor();
	}
	
    public boolean setForm2Editor() {
		IpssLogger.getLogger().info("NBZInputPanel setForm2Editor() called");

		if (this.data.getZUnit().equals("PU"))
    		this.puUnitRadioButton.setSelected(true);
		else
    		this.perUnitRadioButton.setSelected(true);

        if (((GNetForm)netContainer.getGNetForm()).getDistNetData().getScStd().equals(DistNetData.ScStd_Generic)) {
        	setNormalTableData(netContainer, form);
        }
        else {
        	setScPointTableData(netContainer, form);
        }
    	this.editingTable = true;

    	return true;
	}
    
    public boolean saveEditor2Form(Vector errMsg) throws Exception {
		IpssLogger.getLogger().info("NBZInputPanel saveEditor2Form() called");

		boolean ok = true;
		if (this.puUnitRadioButton.isSelected()) 
			this.data.setZUnit("PU");
		else 
			this.data.setZUnit("PERCENT");

    	int scPoints;
    	DefaultTableModel tableModel;
        if (((GNetForm)netContainer.getGNetForm()).getDistNetData().getScStd().equals(DistNetData.ScStd_Generic)) {
        	scPoints = 1;
        	tableModel = getNormalDataModel();
        }
        else {
        	scPoints = ((GNetForm)this.netContainer.getGNetForm()).getDistNetData().getActiveScPoints();
        	tableModel = getScPointDataModel();
        }

        for (int i = 0; i < scPoints+2; i++) {
			double x = SwingInputVerifyUtil.getDouble(tableModel.getValueAt(i, 1));
			double r = SwingInputVerifyUtil.getDouble(tableModel.getValueAt(i, 2));
			double x_r = SwingInputVerifyUtil.getDouble(tableModel.getValueAt(i, 3));
			if ( x <= 0.0 || r < 0.0 || x_r < 0.0 ) {
				errMsg.add("x <= 0.0 or r < 0.0 or x_r < 0.0, row " + new Integer(i+1));
				ok = false;
			}
			
			if (i < scPoints) {
		        if (((GNetForm)netContainer.getGNetForm()).getDistNetData().getScStd().equals(DistNetData.ScStd_Generic)) {
					this.data.setZ1X(x);
					this.data.setZ1R(r);		        	
		        }
		        else {
		        	ZData row = this.data.getZ1(i);
					row.setX(x);
					row.setR(r);
					row.resetXR(x_r);
		        }
			}
			else if (i == scPoints) {
				// TODO: X/R case
				this.data.setZ2X(x);
				this.data.setZ2R(r);
			}
			else {
				// TODO: X/R case
				this.data.setZ0X(x);
				this.data.setZ0R(r);
			}
		}

    	if (ok)
    		this.editingTable = false;
		return ok;
    }

    private void setScPointTableData(GFormContainer netContainer, Object form) {
    	GNetForm netForm = (GNetForm)netContainer.getGNetForm();
    	DistBusData busForm = ((GBusForm)form).getDistBusData();

    	int scPoints = netForm.getDistNetData().getActiveScPoints();
    	if (busForm.getZ1List().size() != scPoints) {
			busForm.initZ_SCList(scPoints);
    	}

		getScPointDataModel().setRowCount(scPoints+2);
		int offset = 0;
		for (int i = 0; i < netForm.getDistNetData().getScPointList().size(); i++) {
			ScPointData row = (ScPointData)netForm.getDistNetData().getScPointList().get(i);
			if (row.getEnable()) {
				if (netForm.getDistNetData().getScStd().equals("ANSI")) {
					String x = "Xd\"";
					if (i == 1) x = "Xd'";
					else if (i == 2) x = "Xd";
					getScPointDataModel().setValueAt(new String(row.getName()+"("+x+")"), offset++, 0);
				}    
				else
					getScPointDataModel().setValueAt(new String("Z1("+row.getName()+")"), offset++, 0);
			}   
		}
		
		for (int i = 0; i < scPoints; i++) {
			ZData row = (ZData)busForm.getZ1List().get(i);
			getScPointDataModel().setValueAt(new Double(row.getX()), i, 1);
			getScPointDataModel().setValueAt(new Double(row.getR()), i, 2);
			getScPointDataModel().setValueAt(new Double(row.getX_R()), i, 3);
		}

	    setZ2Z0TableData(getScPointDataModel(), busForm, scPoints);
	}
	
    private void setNormalTableData(GFormContainer netContainer, Object form) {
    	DistBusData busForm = ((GBusForm)form).getDistBusData();
		getNormalDataModel().setRowCount(3);

		getNormalDataModel().setValueAt(new String("Z(1)"), 0, 0);
		getNormalDataModel().setValueAt(new Double(busForm.getZ1X()), 0, 1);
		getNormalDataModel().setValueAt(new Double(busForm.getZ1R()), 0, 2);
		getNormalDataModel().setValueAt(new Double(NetDataUtil.ratio(busForm.getZ1X(), 
    						busForm.getZ1R())), 0, 3);

	    setZ2Z0TableData(getNormalDataModel(), busForm, 1);
	}

    private void setZ2Z0TableData(DefaultTableModel dataModel, DistBusData busForm, int refPoint) {
    	dataModel.setValueAt(new String("Z(2)"), refPoint, 0);
    	dataModel.setValueAt(new Double(busForm.getZ2X()), refPoint, 1);
    	dataModel.setValueAt(new Double(busForm.getZ2R()), refPoint, 2);
    	dataModel.setValueAt(new Double(NetDataUtil.ratio(busForm.getZ2X(), busForm.getZ2R())), refPoint, 3);
    	
		dataModel.setValueAt(new String("Z(0)"), refPoint+1, 0);
		dataModel.setValueAt(new Double(busForm.getZ0X()), refPoint+1, 1);
		dataModel.setValueAt(new Double(busForm.getZ0R()), refPoint+1, 2);
		dataModel.setValueAt(new Double(NetDataUtil.ratio(busForm.getZ0X(), busForm.getZ0R())), refPoint+1, 3);
	}

    // The following code is controlled by NetBean IDE
	// ===============================================
	
    /** Creates new form NBZInputPanel */
    public NBZInputPanel() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        unitButtonGroup = new javax.swing.ButtonGroup();
        editPanel = new javax.swing.JPanel();
        scPointPanel = new javax.swing.JPanel();
        scPointTitlePanel = new javax.swing.JPanel();
        sc_nameLabel = new javax.swing.JLabel();
        sc_xLabel = new javax.swing.JLabel();
        sc_rLabel = new javax.swing.JLabel();
        sc_x_rLabel = new javax.swing.JLabel();
        scPointZTable = new javax.swing.JTable();
        normalZPanel = new javax.swing.JPanel();
        normalTitlePanel = new javax.swing.JPanel();
        n_nameLabel = new javax.swing.JLabel();
        n_xLabel = new javax.swing.JLabel();
        n_rLabel = new javax.swing.JLabel();
        x_rLabel1 = new javax.swing.JLabel();
        normalZTable = new javax.swing.JTable();
        unitPanel = new javax.swing.JPanel();
        zUnitLabel = new javax.swing.JLabel();
        puUnitRadioButton = new javax.swing.JRadioButton();
        perUnitRadioButton = new javax.swing.JRadioButton();
        endLabel = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 100, 1, 100));
        setLayout(new java.awt.BorderLayout(0, 20));

        editPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Short Circuit Impedence", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10)));
        editPanel.setMinimumSize(new java.awt.Dimension(258, 150));
        editPanel.setPreferredSize(new java.awt.Dimension(300, 150));
        editPanel.setLayout(new java.awt.BorderLayout());

        scPointPanel.setLayout(new java.awt.BorderLayout());

        scPointTitlePanel.setLayout(new java.awt.GridLayout(1, 4));

        sc_nameLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        sc_nameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        sc_nameLabel.setText("Name");
        scPointTitlePanel.add(sc_nameLabel);

        sc_xLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        sc_xLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        sc_xLabel.setText("X");
        scPointTitlePanel.add(sc_xLabel);

        sc_rLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        sc_rLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        sc_rLabel.setText("R");
        scPointTitlePanel.add(sc_rLabel);

        sc_x_rLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        sc_x_rLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        sc_x_rLabel.setText("X/R");
        scPointTitlePanel.add(sc_x_rLabel);

        scPointPanel.add(scPointTitlePanel, java.awt.BorderLayout.NORTH);

        scPointZTable.setFont(new java.awt.Font("Dialog", 0, 10));
        scPointZTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"1", null, null, null},
                {"2", null, null, null},
                {"3", null, null, null},
                {"4", null, null, null},
                {"5", null, null, null},
                {"6", null, null, null},
                {"7", null, null, null}
            },
            new String [] {
                "name", "x", "r", "x_r"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scPointZTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        scPointZTable.setCellSelectionEnabled(true);
        scPointZTable.setMinimumSize(new java.awt.Dimension(100, 112));
        scPointZTable.setName("zTable"); // NOI18N
        scPointZTable.setPreferredSize(new java.awt.Dimension(300, 20*_rows)
        );
        scPointPanel.add(scPointZTable, java.awt.BorderLayout.CENTER);

        editPanel.add(scPointPanel, java.awt.BorderLayout.NORTH);

        normalZPanel.setLayout(new java.awt.BorderLayout());

        normalTitlePanel.setLayout(new java.awt.GridLayout(1, 4));

        n_nameLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        n_nameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        n_nameLabel.setText("Name");
        normalTitlePanel.add(n_nameLabel);

        n_xLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        n_xLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        n_xLabel.setText("X");
        normalTitlePanel.add(n_xLabel);

        n_rLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        n_rLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        n_rLabel.setText("R");
        normalTitlePanel.add(n_rLabel);

        x_rLabel1.setFont(new java.awt.Font("Dialog", 0, 10));
        x_rLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        x_rLabel1.setText("X/R");
        normalTitlePanel.add(x_rLabel1);

        normalZPanel.add(normalTitlePanel, java.awt.BorderLayout.NORTH);

        normalZTable.setFont(new java.awt.Font("Dialog", 0, 10));
        normalZTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"1", null, null, null},
                {"2", null, null, null},
                {"3", null, null, null}
            },
            new String [] {
                "name", "x", "r", "x_r"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        normalZTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        normalZTable.setCellSelectionEnabled(true);
        normalZTable.setMinimumSize(new java.awt.Dimension(100, 48));
        normalZTable.setName("zTable"); // NOI18N
        normalZTable.setPreferredSize(new java.awt.Dimension(300, 20*_rows)
        );
        normalZPanel.add(normalZTable, java.awt.BorderLayout.CENTER);

        editPanel.add(normalZPanel, java.awt.BorderLayout.CENTER);

        zUnitLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        zUnitLabel.setText("Z Unit  [");
        unitPanel.add(zUnitLabel);

        unitButtonGroup.add(puUnitRadioButton);
        puUnitRadioButton.setFont(new java.awt.Font("Dialog", 0, 10));
        puUnitRadioButton.setSelected(true);
        puUnitRadioButton.setText("PU");
        puUnitRadioButton.setName("puUnitRadioButton"); // NOI18N
        unitPanel.add(puUnitRadioButton);

        unitButtonGroup.add(perUnitRadioButton);
        perUnitRadioButton.setFont(new java.awt.Font("Dialog", 0, 10));
        perUnitRadioButton.setText("%");
        perUnitRadioButton.setName("perUnitRadioButton"); // NOI18N
        unitPanel.add(perUnitRadioButton);

        endLabel.setFont(new java.awt.Font("Dialog", 0, 10));
        endLabel.setText("]");
        unitPanel.add(endLabel);

        editPanel.add(unitPanel, java.awt.BorderLayout.SOUTH);

        add(editPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel editPanel;
    private javax.swing.JLabel endLabel;
    private javax.swing.JLabel n_nameLabel;
    private javax.swing.JLabel n_rLabel;
    private javax.swing.JLabel n_xLabel;
    private javax.swing.JPanel normalTitlePanel;
    private javax.swing.JPanel normalZPanel;
    private javax.swing.JTable normalZTable;
    private javax.swing.JRadioButton perUnitRadioButton;
    private javax.swing.JRadioButton puUnitRadioButton;
    private javax.swing.JPanel scPointPanel;
    private javax.swing.JPanel scPointTitlePanel;
    private javax.swing.JTable scPointZTable;
    private javax.swing.JLabel sc_nameLabel;
    private javax.swing.JLabel sc_rLabel;
    private javax.swing.JLabel sc_xLabel;
    private javax.swing.JLabel sc_x_rLabel;
    private javax.swing.ButtonGroup unitButtonGroup;
    private javax.swing.JPanel unitPanel;
    private javax.swing.JLabel x_rLabel1;
    private javax.swing.JLabel zUnitLabel;
    // End of variables declaration//GEN-END:variables
    
}
