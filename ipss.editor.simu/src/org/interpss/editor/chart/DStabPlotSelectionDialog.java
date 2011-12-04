/*
 * @(#)DStabPlotSelectionDialog.java   
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
 * @Date 05/01/2007
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.editor.chart;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.interpss.dstab.output.DStabSimuDBRecord;
import org.interpss.editor.ui.IOutputTextDialog;
import org.interpss.editor.ui.IScriptTool;
import org.interpss.editor.ui.util.CoreScriptUtilFunc;
import org.interpss.editor.ui.util.GUIFileUtil;
import org.interpss.output.ISimuRecManager;
import org.interpss.spring.UISpringFactory;
import org.interpss.ui.WinUtilities;
import org.interpss.util.MemoryJavaCompiler;

import com.interpss.common.datatype.Constants;
import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.common.util.IpssLogger;
import com.interpss.dstab.DStabilityNetwork;
import com.interpss.simu.SimuContext;
import com.interpss.simu.util.SimuCtxHelper;
import com.interpss.spring.CoreCommonSpringFactory;

/**
 * 
 * @author mzhou
 */
public class DStabPlotSelectionDialog extends javax.swing.JDialog {
	private static final long serialVersionUID = 1;

	public static String ScriptTemplateFilename = "template/DStabPlotScriptTemplate.txt";

	private int caseId = 0;
	private SimuContext simuCtx;
	private String scriptFilename;

	final private List<String> idItemList = new ArrayList<String>();
	final private List<String> stateItemList = new ArrayList<String>();

	/** Creates new form MachStateSelDialog */
	public DStabPlotSelectionDialog(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		initComponents();
		WinUtilities.center(this);
	}

	public void init(SimuContext simuCtx, int aCaseId, String scriptFilename) {
		this.caseId = aCaseId;
		this.simuCtx = simuCtx;
		this.scriptFilename = scriptFilename;

		this.allRadioButton.setSelected(true);
		allRadioButtonActionPerformed(null);

		stateItemList.clear();
		refreashStateItemList();
		setAllStatusOff();

		if (scriptFilename != null) {
			IpssLogger.getLogger().info("scriptFilename: " + scriptFilename);
			mainTabbedPane.setEnabledAt(1, true);
			GUIFileUtil.readFile2TextareaAbsolutePath(scriptFilename,
					scriptTextArea);
			if (scriptTextArea.getText().trim().equals("")) {
				GUIFileUtil.readFile2TextareaRativePath(ScriptTemplateFilename,
						scriptTextArea);
			}
		} else {
			mainTabbedPane.setEnabledAt(1, false);
		}

		this.pack();
		this.setVisible(true);
	}

	private void refreashIdItemList() {
		idItemSelectList.setModel(new javax.swing.AbstractListModel() {
			public int getSize() {
				return idItemList.size();
			}

			public Object getElementAt(int i) {
				return idItemList.get(i);
			}
		});
	}

	private void refreashStateItemList() {
		stateItemSelectList.setModel(new javax.swing.AbstractListModel() {
			public int getSize() {
				return stateItemList.size();
			}

			public Object getElementAt(int i) {
				return stateItemList.get(i);
			}
		});
	}

	private void setAllStatusOff() {
		setBranchDeviceStatus(false);
		setBranchVariableStatus(false);
		setBusDeviceStatus(false);
		setBusVariableStatus(false);
		setExcStateStatus(false);
		setGovStateStatus(false);
		setMachStateStatus(false);
		setPssStateStatus(false);
	}

	private void setBranchDeviceStatus(boolean b) {
		addBranchDeviceStateButton.setEnabled(b);
		branchDeviceComboBox.setEnabled(b);
		branchDeviceLabel.setEnabled(b);
	}

	private void setBranchVariableStatus(boolean b) {
		branchVariableComboBox.setEnabled(b);
		branchVariableLabel.setEnabled(b);
		addBranchStateButton.setEnabled(b);
	}

	private void setBusDeviceStatus(boolean b) {
		busDeviceComboBox.setEnabled(b);
		busDeviceLabel.setEnabled(b);
		addBusDeviceStateButton.setEnabled(b);
	}

	private void setBusVariableStatus(boolean b) {
		busVariableComboBox.setEnabled(b);
		busVariableLabel.setEnabled(b);
		addBusStateButton.setEnabled(b);
	}

	private void setExcStateStatus(boolean b) {
		excStateComboBox.setEnabled(b);
		excStateLabel.setEnabled(b);
		addExcStateButton.setEnabled(b);
	}

	private void setGovStateStatus(boolean b) {
		govStateComboBox.setEnabled(b);
		govStateLabel.setEnabled(b);
		addGovStateButton.setEnabled(b);
	}

	private void setMachStateStatus(boolean b) {
		machStateComboBox.setEnabled(b);
		machStateLabel.setEnabled(b);
		addMachStateButton.setEnabled(b);
	}

	private void setPssStateStatus(boolean b) {
		pssStateComboBox.setEnabled(b);
		pssstateLabel.setEnabled(b);
		addPSSStateButton.setEnabled(b);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc=" Generated Code
	// ">//GEN-BEGIN:initComponents
	private void initComponents() {
		typeButtonGroup = new javax.swing.ButtonGroup();
		mainTabbedPane = new javax.swing.JTabbedPane();
		plotPanel = new javax.swing.JPanel();
		typeSelectPanel = new javax.swing.JPanel();
		allRadioButton = new javax.swing.JRadioButton();
		machRadioButton = new javax.swing.JRadioButton();
		busRadioButton = new javax.swing.JRadioButton();
		busDeviceRadioButton = new javax.swing.JRadioButton();
		branchRadioButton = new javax.swing.JRadioButton();
		branchDeviceRadioButton = new javax.swing.JRadioButton();
		itemSelectPanel = new javax.swing.JPanel();
		jScrollPane1 = new javax.swing.JScrollPane();
		idItemSelectList = new javax.swing.JList();
		jScrollPane2 = new javax.swing.JScrollPane();
		stateItemSelectList = new javax.swing.JList();
		plotButton = new javax.swing.JButton();
		scriptingButton = new javax.swing.JButton();
		deleteButton = new javax.swing.JButton();
		machStateLabel = new javax.swing.JLabel();
		excStateLabel = new javax.swing.JLabel();
		govStateLabel = new javax.swing.JLabel();
		pssstateLabel = new javax.swing.JLabel();
		busVariableLabel = new javax.swing.JLabel();
		busDeviceLabel = new javax.swing.JLabel();
		branchVariableLabel = new javax.swing.JLabel();
		branchDeviceLabel = new javax.swing.JLabel();
		machStateComboBox = new javax.swing.JComboBox();
		excStateComboBox = new javax.swing.JComboBox();
		govStateComboBox = new javax.swing.JComboBox();
		pssStateComboBox = new javax.swing.JComboBox();
		busVariableComboBox = new javax.swing.JComboBox();
		busDeviceComboBox = new javax.swing.JComboBox();
		branchVariableComboBox = new javax.swing.JComboBox();
		branchDeviceComboBox = new javax.swing.JComboBox();
		addMachStateButton = new javax.swing.JButton();
		addExcStateButton = new javax.swing.JButton();
		addGovStateButton = new javax.swing.JButton();
		addPSSStateButton = new javax.swing.JButton();
		addBusStateButton = new javax.swing.JButton();
		addBusDeviceStateButton = new javax.swing.JButton();
		addBranchStateButton = new javax.swing.JButton();
		addBranchDeviceStateButton = new javax.swing.JButton();
		machStateLabel1 = new javax.swing.JLabel();
		machStateLabel2 = new javax.swing.JLabel();
		scriptingPanel = new javax.swing.JPanel();
		jScrollPane3 = new javax.swing.JScrollPane();
		scriptTextArea = new javax.swing.JTextArea();
		closeButton = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Transient Stability Simulation Plotting Dialog");
		mainTabbedPane.setFont(new java.awt.Font("Dialog", 0, 12));
		typeSelectPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
				null, "Variable Type",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION,
				new java.awt.Font("Dialog", 0, 10)));
		typeButtonGroup.add(allRadioButton);
		allRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
		allRadioButton.setSelected(true);
		allRadioButton.setText("All");
		allRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,
				0, 0, 0));
		allRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
		allRadioButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				allRadioButtonActionPerformed(evt);
			}
		});

		typeButtonGroup.add(machRadioButton);
		machRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
		machRadioButton.setText("Machine");
		machRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(
				0, 0, 0, 0));
		machRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
		machRadioButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				machRadioButtonActionPerformed(evt);
			}
		});

		typeButtonGroup.add(busRadioButton);
		busRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
		busRadioButton.setText("Bus");
		busRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,
				0, 0, 0));
		busRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
		busRadioButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				busRadioButtonActionPerformed(evt);
			}
		});

		typeButtonGroup.add(busDeviceRadioButton);
		busDeviceRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
		busDeviceRadioButton.setText("Dynamic Bus Device");
		busDeviceRadioButton.setBorder(javax.swing.BorderFactory
				.createEmptyBorder(0, 0, 0, 0));
		busDeviceRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
		busDeviceRadioButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						busDeviceRadioButtonActionPerformed(evt);
					}
				});

		typeButtonGroup.add(branchRadioButton);
		branchRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
		branchRadioButton.setText("Branch");
		branchRadioButton.setBorder(javax.swing.BorderFactory
				.createEmptyBorder(0, 0, 0, 0));
		branchRadioButton.setEnabled(false);
		branchRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
		branchRadioButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						branchRadioButtonActionPerformed(evt);
					}
				});

		typeButtonGroup.add(branchDeviceRadioButton);
		branchDeviceRadioButton.setFont(new java.awt.Font("Dialog", 0, 12));
		branchDeviceRadioButton.setText("Dynamic Branch Device");
		branchDeviceRadioButton.setBorder(javax.swing.BorderFactory
				.createEmptyBorder(0, 0, 0, 0));
		branchDeviceRadioButton.setEnabled(false);
		branchDeviceRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
		branchDeviceRadioButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						branchDeviceRadioButtonActionPerformed(evt);
					}
				});

		org.jdesktop.layout.GroupLayout typeSelectPanelLayout = new org.jdesktop.layout.GroupLayout(
				typeSelectPanel);
		typeSelectPanel.setLayout(typeSelectPanelLayout);
		typeSelectPanelLayout
				.setHorizontalGroup(typeSelectPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								typeSelectPanelLayout
										.createSequentialGroup()
										.add(29, 29, 29)
										.add(allRadioButton)
										.add(30, 30, 30)
										.add(machRadioButton)
										.add(29, 29, 29)
										.add(busRadioButton)
										.add(30, 30, 30)
										.add(busDeviceRadioButton)
										.add(23, 23, 23)
										.add(branchRadioButton)
										.add(23, 23, 23)
										.add(
												branchDeviceRadioButton,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addContainerGap()));
		typeSelectPanelLayout
				.setVerticalGroup(typeSelectPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								typeSelectPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												typeSelectPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(
																branchDeviceRadioButton,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.add(
																branchRadioButton,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.add(
																busDeviceRadioButton,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.add(
																busRadioButton,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.add(
																machRadioButton,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.add(
																allRadioButton,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE))
										.addContainerGap()));

		idItemSelectList.setFont(new java.awt.Font("Dialog", 0, 12));
		idItemSelectList.setModel(new javax.swing.AbstractListModel() {
			String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4",
					"Item 5" };

			public int getSize() {
				return strings.length;
			}

			public Object getElementAt(int i) {
				return strings[i];
			}
		});
		idItemSelectList
				.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		idItemSelectList.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				idItemSelectListMouseClicked(evt);
			}
		});

		jScrollPane1.setViewportView(idItemSelectList);

		stateItemSelectList.setFont(new java.awt.Font("Dialog", 0, 10));
		stateItemSelectList.setModel(new javax.swing.AbstractListModel() {
			String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4",
					"Item 5" };

			public int getSize() {
				return strings.length;
			}

			public Object getElementAt(int i) {
				return strings[i];
			}
		});
		stateItemSelectList.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				stateItemSelectListMouseClicked(evt);
			}
		});

		jScrollPane2.setViewportView(stateItemSelectList);

		plotButton.setFont(new java.awt.Font("Dialog", 0, 10));
		plotButton.setText("P");
		plotButton
				.setToolTipText("Plot the selected state/variable, only one at a time.");
		plotButton.setMargin(new java.awt.Insets(2, 5, 2, 5));
		plotButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				plotButtonActionPerformed(evt);
			}
		});

		scriptingButton.setFont(new java.awt.Font("Dialog", 0, 10));
		scriptingButton.setText("S");
		scriptingButton
				.setToolTipText("Scripting the selected state/variable(s), multiple selection allowed");
		scriptingButton.setMargin(new java.awt.Insets(2, 5, 2, 5));
		scriptingButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				scriptingButtonActionPerformed(evt);
			}
		});

		deleteButton.setFont(new java.awt.Font("Dialog", 0, 10));
		deleteButton.setText("X");
		deleteButton.setToolTipText("Delete the selected state/variable(s)");
		deleteButton.setMargin(new java.awt.Insets(2, 5, 2, 5));
		deleteButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				deleteButtonActionPerformed(evt);
			}
		});

		machStateLabel.setFont(new java.awt.Font("Dialog", 0, 12));
		machStateLabel.setText("Mach States");

		excStateLabel.setFont(new java.awt.Font("Dialog", 0, 12));
		excStateLabel.setText("Exc States");

		govStateLabel.setFont(new java.awt.Font("Dialog", 0, 12));
		govStateLabel.setText("Gov States");

		pssstateLabel.setFont(new java.awt.Font("Dialog", 0, 12));
		pssstateLabel.setText("PSS States");

		busVariableLabel.setFont(new java.awt.Font("Dialog", 0, 12));
		busVariableLabel.setText("Bus Variable");

		busDeviceLabel.setFont(new java.awt.Font("Dialog", 0, 12));
		busDeviceLabel.setText("Bus Device");

		branchVariableLabel.setFont(new java.awt.Font("Dialog", 0, 12));
		branchVariableLabel.setText("Branch Variable");

		branchDeviceLabel.setFont(new java.awt.Font("Dialog", 0, 12));
		branchDeviceLabel.setText("Branch Device");

		machStateComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
		machStateComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

		excStateComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
		excStateComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

		govStateComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
		govStateComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

		pssStateComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
		pssStateComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

		busVariableComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
		busVariableComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

		busDeviceComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
		busDeviceComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

		branchVariableComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
		branchVariableComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

		branchDeviceComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
		branchDeviceComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

		addMachStateButton.setFont(new java.awt.Font("Dialog", 0, 12));
		addMachStateButton.setText(">");
		addMachStateButton.setMargin(new java.awt.Insets(2, 5, 2, 5));
		addMachStateButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						addMachStateButtonActionPerformed(evt);
					}
				});

		addExcStateButton.setFont(new java.awt.Font("Dialog", 0, 12));
		addExcStateButton.setText(">");
		addExcStateButton.setMargin(new java.awt.Insets(2, 5, 2, 5));
		addExcStateButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						addExcStateButtonActionPerformed(evt);
					}
				});

		addGovStateButton.setFont(new java.awt.Font("Dialog", 0, 12));
		addGovStateButton.setText(">");
		addGovStateButton.setMargin(new java.awt.Insets(2, 5, 2, 5));
		addGovStateButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						addGovStateButtonActionPerformed(evt);
					}
				});

		addPSSStateButton.setFont(new java.awt.Font("Dialog", 0, 12));
		addPSSStateButton.setText(">");
		addPSSStateButton.setMargin(new java.awt.Insets(2, 5, 2, 5));
		addPSSStateButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						addPSSStateButtonActionPerformed(evt);
					}
				});

		addBusStateButton.setFont(new java.awt.Font("Dialog", 0, 12));
		addBusStateButton.setText(">");
		addBusStateButton.setMargin(new java.awt.Insets(2, 5, 2, 5));
		addBusStateButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						addBusStateButtonActionPerformed(evt);
					}
				});

		addBusDeviceStateButton.setFont(new java.awt.Font("Dialog", 0, 12));
		addBusDeviceStateButton.setText(">");
		addBusDeviceStateButton.setMargin(new java.awt.Insets(2, 5, 2, 5));
		addBusDeviceStateButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						addBusDeviceStateButtonActionPerformed(evt);
					}
				});

		addBranchStateButton.setFont(new java.awt.Font("Dialog", 0, 12));
		addBranchStateButton.setText(">");
		addBranchStateButton.setMargin(new java.awt.Insets(2, 5, 2, 5));
		addBranchStateButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						addBranchStateButtonActionPerformed(evt);
					}
				});

		addBranchDeviceStateButton.setFont(new java.awt.Font("Dialog", 0, 12));
		addBranchDeviceStateButton.setText(">");
		addBranchDeviceStateButton.setMargin(new java.awt.Insets(2, 5, 2, 5));
		addBranchDeviceStateButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						addBranchDeviceStateButtonActionPerformed(evt);
					}
				});

		machStateLabel1.setFont(new java.awt.Font("Dialog", 0, 12));
		machStateLabel1.setText("Mach/Bus/Branch Id");

		machStateLabel2.setFont(new java.awt.Font("Dialog", 0, 12));
		machStateLabel2.setText("State/Variable");

		org.jdesktop.layout.GroupLayout itemSelectPanelLayout = new org.jdesktop.layout.GroupLayout(
				itemSelectPanel);
		itemSelectPanel.setLayout(itemSelectPanelLayout);
		itemSelectPanelLayout
				.setHorizontalGroup(itemSelectPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								itemSelectPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												itemSelectPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(machStateLabel1)
														.add(
																jScrollPane1,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																116,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.add(31, 31, 31)
										.add(
												itemSelectPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(excStateLabel)
														.add(govStateLabel)
														.add(pssstateLabel)
														.add(busVariableLabel)
														.add(busDeviceLabel)
														.add(
																branchVariableLabel)
														.add(branchDeviceLabel)
														.add(machStateLabel))
										.add(35, 35, 35)
										.add(
												itemSelectPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.TRAILING,
																false)
														.add(
																itemSelectPanelLayout
																		.createSequentialGroup()
																		.add(
																				machStateComboBox,
																				0,
																				144,
																				Short.MAX_VALUE)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED))
														.add(
																excStateComboBox,
																0,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.add(
																govStateComboBox,
																0,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.add(
																pssStateComboBox,
																0,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.add(
																busVariableComboBox,
																0,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.add(
																busDeviceComboBox,
																0,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.add(
																branchVariableComboBox,
																0,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.add(
																branchDeviceComboBox,
																0,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												itemSelectPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(
																addBranchStateButton)
														.add(
																addBranchDeviceStateButton)
														.add(
																addBusDeviceStateButton)
														.add(addBusStateButton)
														.add(addPSSStateButton)
														.add(addGovStateButton)
														.add(addExcStateButton)
														.add(addMachStateButton))
										.add(51, 51, 51)
										.add(
												itemSelectPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(machStateLabel2)
														.add(
																itemSelectPanelLayout
																		.createParallelGroup(
																				org.jdesktop.layout.GroupLayout.TRAILING,
																				false)
																		.add(
																				itemSelectPanelLayout
																						.createSequentialGroup()
																						.add(
																								plotButton)
																						.addPreferredGap(
																								org.jdesktop.layout.LayoutStyle.RELATED)
																						.add(
																								scriptingButton)
																						.addPreferredGap(
																								org.jdesktop.layout.LayoutStyle.RELATED,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE)
																						.add(
																								deleteButton))
																		.add(
																				org.jdesktop.layout.GroupLayout.LEADING,
																				jScrollPane2,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																				155,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
										.addContainerGap(20, Short.MAX_VALUE)));
		itemSelectPanelLayout
				.setVerticalGroup(itemSelectPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								itemSelectPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												itemSelectPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(
																itemSelectPanelLayout
																		.createSequentialGroup()
																		.add(
																				itemSelectPanelLayout
																						.createParallelGroup(
																								org.jdesktop.layout.GroupLayout.BASELINE)
																						.add(
																								machStateLabel1)
																						.add(
																								machStateLabel2))
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				itemSelectPanelLayout
																						.createParallelGroup(
																								org.jdesktop.layout.GroupLayout.LEADING)
																						.add(
																								org.jdesktop.layout.GroupLayout.TRAILING,
																								itemSelectPanelLayout
																										.createSequentialGroup()
																										.add(
																												jScrollPane2,
																												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																												223,
																												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
																										.addPreferredGap(
																												org.jdesktop.layout.LayoutStyle.RELATED,
																												7,
																												Short.MAX_VALUE)
																										.add(
																												itemSelectPanelLayout
																														.createParallelGroup(
																																org.jdesktop.layout.GroupLayout.BASELINE)
																														.add(
																																plotButton)
																														.add(
																																scriptingButton)
																														.add(
																																deleteButton)))
																						.add(
																								jScrollPane1,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								253,
																								Short.MAX_VALUE))
																		.addContainerGap())
														.add(
																org.jdesktop.layout.GroupLayout.TRAILING,
																itemSelectPanelLayout
																		.createSequentialGroup()
																		.add(
																				itemSelectPanelLayout
																						.createParallelGroup(
																								org.jdesktop.layout.GroupLayout.BASELINE)
																						.add(
																								machStateLabel,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								24,
																								Short.MAX_VALUE)
																						.add(
																								machStateComboBox,
																								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
																						.add(
																								addMachStateButton,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								25,
																								Short.MAX_VALUE))
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				itemSelectPanelLayout
																						.createParallelGroup(
																								org.jdesktop.layout.GroupLayout.BASELINE)
																						.add(
																								excStateLabel,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE)
																						.add(
																								excStateComboBox)
																						.add(
																								addExcStateButton,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								25,
																								Short.MAX_VALUE))
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				itemSelectPanelLayout
																						.createParallelGroup(
																								org.jdesktop.layout.GroupLayout.BASELINE)
																						.add(
																								govStateLabel,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE)
																						.add(
																								govStateComboBox)
																						.add(
																								addGovStateButton,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								25,
																								Short.MAX_VALUE))
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				itemSelectPanelLayout
																						.createParallelGroup(
																								org.jdesktop.layout.GroupLayout.BASELINE)
																						.add(
																								pssstateLabel,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE)
																						.add(
																								pssStateComboBox)
																						.add(
																								addPSSStateButton,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								25,
																								Short.MAX_VALUE))
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				itemSelectPanelLayout
																						.createParallelGroup(
																								org.jdesktop.layout.GroupLayout.BASELINE)
																						.add(
																								busVariableLabel,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE)
																						.add(
																								busVariableComboBox)
																						.add(
																								addBusStateButton,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								25,
																								Short.MAX_VALUE))
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				itemSelectPanelLayout
																						.createParallelGroup(
																								org.jdesktop.layout.GroupLayout.BASELINE)
																						.add(
																								busDeviceLabel,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE)
																						.add(
																								busDeviceComboBox)
																						.add(
																								addBusDeviceStateButton,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								25,
																								Short.MAX_VALUE))
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				itemSelectPanelLayout
																						.createParallelGroup(
																								org.jdesktop.layout.GroupLayout.BASELINE)
																						.add(
																								branchVariableLabel,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE)
																						.add(
																								branchVariableComboBox)
																						.add(
																								addBranchStateButton))
																		.add(
																				7,
																				7,
																				7)
																		.add(
																				itemSelectPanelLayout
																						.createParallelGroup(
																								org.jdesktop.layout.GroupLayout.BASELINE)
																						.add(
																								branchDeviceLabel,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE)
																						.add(
																								branchDeviceComboBox)
																						.add(
																								addBranchDeviceStateButton,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE))
																		.add(
																				19,
																				19,
																				19)))));

		org.jdesktop.layout.GroupLayout plotPanelLayout = new org.jdesktop.layout.GroupLayout(
				plotPanel);
		plotPanel.setLayout(plotPanelLayout);
		plotPanelLayout
				.setHorizontalGroup(plotPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								plotPanelLayout
										.createSequentialGroup()
										.add(
												plotPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(
																plotPanelLayout
																		.createSequentialGroup()
																		.add(
																				20,
																				20,
																				20)
																		.add(
																				itemSelectPanel,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
														.add(
																plotPanelLayout
																		.createSequentialGroup()
																		.addContainerGap()
																		.add(
																				typeSelectPanel,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
										.addContainerGap(
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));
		plotPanelLayout.setVerticalGroup(plotPanelLayout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				plotPanelLayout.createSequentialGroup().addContainerGap().add(
						typeSelectPanel,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 56,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(
								org.jdesktop.layout.LayoutStyle.RELATED).add(
								itemSelectPanel,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE).addContainerGap()));
		mainTabbedPane.addTab("Curve Plot", plotPanel);

		scriptTextArea.setColumns(20);
		scriptTextArea.setRows(5);
		jScrollPane3.setViewportView(scriptTextArea);

		org.jdesktop.layout.GroupLayout scriptingPanelLayout = new org.jdesktop.layout.GroupLayout(
				scriptingPanel);
		scriptingPanel.setLayout(scriptingPanelLayout);
		scriptingPanelLayout
				.setHorizontalGroup(scriptingPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								scriptingPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												jScrollPane3,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												686, Short.MAX_VALUE)
										.addContainerGap()));
		scriptingPanelLayout
				.setVerticalGroup(scriptingPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								scriptingPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												jScrollPane3,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												359, Short.MAX_VALUE)
										.addContainerGap()));
		mainTabbedPane.addTab("Scripting", scriptingPanel);

		closeButton.setFont(new java.awt.Font("Dialog", 0, 12));
		closeButton.setText("  Close  ");
		closeButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				closeButtonActionPerformed(evt);
			}
		});

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout
				.setHorizontalGroup(layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								layout
										.createSequentialGroup()
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(
																layout
																		.createSequentialGroup()
																		.addContainerGap()
																		.add(
																				mainTabbedPane))
														.add(
																layout
																		.createSequentialGroup()
																		.add(
																				314,
																				314,
																				314)
																		.add(
																				closeButton,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																				95,
																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
										.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				layout.createSequentialGroup().add(mainTabbedPane,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 411,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(15,
						15, 15).add(closeButton).addContainerGap(19,
						Short.MAX_VALUE)));
		pack();
	}// </editor-fold>//GEN-END:initComponents

	private void stateItemSelectListMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_stateItemSelectListMouseClicked
		IpssLogger.getLogger().info(
				"state list mouse clicked, item: "
						+ stateItemSelectList.getSelectedValue());
	}// GEN-LAST:event_stateItemSelectListMouseClicked

	private void idItemSelectListMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_idItemSelectListMouseClicked
		IpssLogger.getLogger().info(
				"item list mouse clicked, item: "
						+ idItemSelectList.getSelectedValue());
		String elemId = (String) idItemSelectList.getSelectedValue();
		setAllStatusOff();
		stateItemList.clear();
		this.refreashStateItemList();
		if (elemId.startsWith(Constants.Token_MachId)) {
			IpssLogger.getLogger().info(
					"setStateComboList for machId: " + elemId);
			setExcStateStatus(true);
			setGovStateStatus(true);
			setMachStateStatus(true);
			setPssStateStatus(true);
			this.machStateComboBox
					.setModel(new javax.swing.DefaultComboBoxModel(
							ChartManager
									.getStatesNameList(
											this.caseId,
											elemId,
											ISimuRecManager.REC_TYPE_DStabMachineStates)));
			this.excStateComboBox
					.setModel(new javax.swing.DefaultComboBoxModel(ChartManager
							.getStatesNameList(this.caseId, elemId
									+ DStabSimuDBRecord.EXCITER_ID_EXT,
									ISimuRecManager.REC_TYPE_DStabExcStates)));
			this.govStateComboBox
					.setModel(new javax.swing.DefaultComboBoxModel(ChartManager
							.getStatesNameList(this.caseId, elemId
									+ DStabSimuDBRecord.GOVERNER_ID_EXT,
									ISimuRecManager.REC_TYPE_DStabGovStates)));
			this.pssStateComboBox
					.setModel(new javax.swing.DefaultComboBoxModel(ChartManager
							.getStatesNameList(this.caseId, elemId
									+ DStabSimuDBRecord.STABILIZER_ID_EXT,
									ISimuRecManager.REC_TYPE_DStabPssStates)));
		} else if (elemId.startsWith(Constants.Token_DBusDeviceId)) {
			IpssLogger.getLogger().info(
					"setStateComboList for busDeviceId: " + elemId);
			setBusDeviceStatus(true);
			this.busDeviceComboBox
					.setModel(new javax.swing.DefaultComboBoxModel(
							ChartManager
									.getStatesNameList(
											this.caseId,
											elemId,
											ISimuRecManager.REC_TYPE_DStabScriptBusDeviceStates)));
		} else if (elemId.startsWith(Constants.Token_BusId)) {
			IpssLogger.getLogger().info(
					"setStateComboList for busId: " + elemId);
			setBusVariableStatus(true);
			String busId = elemId.replaceAll(Constants.Token_BusId, "");
			this.busVariableComboBox
					.setModel(new javax.swing.DefaultComboBoxModel(ChartManager
							.getStatesNameList(this.caseId, busId,
									ISimuRecManager.REC_TYPE_DStabBusStates)));
		}
	}// GEN-LAST:event_idItemSelectListMouseClicked

	private void addBranchDeviceStateButtonActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_addBranchDeviceStateButtonActionPerformed
		this.stateItemList.add((String) branchDeviceComboBox.getSelectedItem());
		refreashStateItemList();
	}// GEN-LAST:event_addBranchDeviceStateButtonActionPerformed

	private void addBranchStateButtonActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_addBranchStateButtonActionPerformed
		this.stateItemList.add((String) branchVariableComboBox
				.getSelectedItem());
		refreashStateItemList();
	}// GEN-LAST:event_addBranchStateButtonActionPerformed

	private void addBusDeviceStateButtonActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_addBusDeviceStateButtonActionPerformed
		IpssLogger.getLogger().info("Add bus device button clicked");
		String elemId = (String) idItemSelectList.getSelectedValue();
		this.stateItemList.add((String) busDeviceComboBox.getSelectedItem()
				+ "(" + elemId + ")");
		refreashStateItemList();
	}// GEN-LAST:event_addBusDeviceStateButtonActionPerformed

	private void addBusStateButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_addBusStateButtonActionPerformed
		IpssLogger.getLogger().info("Add bus button clicked");
		String elemId = (String) idItemSelectList.getSelectedValue();
		this.stateItemList.add((String) busVariableComboBox.getSelectedItem()
				+ "(" + elemId + ")");
		refreashStateItemList();
	}// GEN-LAST:event_addBusStateButtonActionPerformed

	private void addPSSStateButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_addPSSStateButtonActionPerformed
		String elemId = (String) idItemSelectList.getSelectedValue();
		this.stateItemList.add((String) pssStateComboBox.getSelectedItem()
				+ "(" + elemId + DStabSimuDBRecord.STABILIZER_ID_EXT + ")");
		refreashStateItemList();
	}// GEN-LAST:event_addPSSStateButtonActionPerformed

	private void addGovStateButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_addGovStateButtonActionPerformed
		String elemId = (String) idItemSelectList.getSelectedValue();
		this.stateItemList.add((String) govStateComboBox.getSelectedItem()
				+ "(" + elemId + DStabSimuDBRecord.GOVERNER_ID_EXT + ")");
		refreashStateItemList();
	}// GEN-LAST:event_addGovStateButtonActionPerformed

	private void addExcStateButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_addExcStateButtonActionPerformed
		String elemId = (String) idItemSelectList.getSelectedValue();
		this.stateItemList.add((String) excStateComboBox.getSelectedItem()
				+ "(" + elemId + DStabSimuDBRecord.EXCITER_ID_EXT + ")");
		refreashStateItemList();
	}// GEN-LAST:event_addExcStateButtonActionPerformed

	private void allRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_allRadioButtonActionPerformed
		IpssLogger.getLogger().info("all button clicked");
		updateItemSelectList(simuCtx, SimuCtxHelper.DStabElemType_All);
	}// GEN-LAST:event_allRadioButtonActionPerformed

	private void updateItemSelectList(SimuContext aSimuCtx, int elemType) {
		Object[] strList = SimuCtxHelper.getDStabElemIdArray(aSimuCtx,
				elemType);
		idItemList.clear();
		for (Object obj : strList)
			idItemList.add((String) obj);
		refreashIdItemList();
	}

	private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_deleteButtonActionPerformed
		Object[] items = stateItemSelectList.getSelectedValues();
		if (items.length > 0) {
			for (Object obj : items) {
				stateItemList.remove((String) obj);
			}
			refreashStateItemList();
		}
	}// GEN-LAST:event_deleteButtonActionPerformed

	private void scriptingButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_scriptingButtonActionPerformed
		Object[] strList = stateItemSelectList.getSelectedValues();
		IPSSMsgHub msg = CoreCommonSpringFactory.getIpssMsgHub();
		if (strList.length > 0) {
			List<String> nameList = DStabPlotDialogRecord
					.getStateNameList(strList);
			List<Hashtable<String, String>> valueList = DStabPlotDialogRecord
					.createValueList(caseId, strList);

			String javacode = scriptTextArea.getText();
			// System.out.println(javacode);
			try {
				IScriptTool tool = (IScriptTool) MemoryJavaCompiler.javac(
						CoreScriptUtilFunc.OutDStabResultClassName, javacode);
				IOutputTextDialog dialog = UISpringFactory
						.getOutputTextDialog("State/Varible Output");
				tool.outDStabResult2TextDialog(dialog, nameList, valueList);
			} catch (Exception e) {
				IpssLogger.logErr(e);
			}
		} else {
			msg.sendWarnMsg("Please select a varible/state to script plotting");
		}
	}// GEN-LAST:event_scriptingButtonActionPerformed

	private void plotButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_plotButtonActionPerformed
		String str = (String) stateItemSelectList.getSelectedValue();
		IPSSMsgHub msg = CoreCommonSpringFactory.getIpssMsgHub();
		if (str != null) {
			DStabPlotDialogRecord rec = DStabPlotDialogRecord
					.parseStateSelection(str);
			IpssLogger.getLogger().info(
					"ElemId, stateName: " + rec.elemId + ", " + rec.stateName);
			DStabilityNetwork net = simuCtx.getDStabilityNet();
			String yDataLabel = "";
			String elemId = "";
			if (rec.recType.equals(ISimuRecManager.REC_TYPE_DStabMachineStates)) {
				yDataLabel = ChartManager.getMachDataLabel(net
						.getMachine(rec.machId), rec.stateName, net
						.getFrequency(), net.getBaseKva());
				elemId = rec.elemId;
			} else if (rec.recType
					.equals(ISimuRecManager.REC_TYPE_DStabExcStates)
					|| rec.recType
							.equals(ISimuRecManager.REC_TYPE_DStabGovStates)
					|| rec.recType
							.equals(ISimuRecManager.REC_TYPE_DStabPssStates)) {
				yDataLabel = ChartManager.getExcDataLabel(net
						.getMachine(rec.machId), rec.stateName);
				elemId = rec.elemId;
			} else if (rec.recType
					.equals(ISimuRecManager.REC_TYPE_DStabBusStates)) {
				elemId = rec.busId;
				yDataLabel = ChartManager.getBusDataLabel(net
						.getDStabBus(rec.busId), rec.stateName, net
						.getBaseKva());
			} else if (rec.recType
					.equals(ISimuRecManager.REC_TYPE_DStabScriptBusDeviceStates)) {
				elemId = rec.elemId;
				yDataLabel = rec.stateName;
			}
			if (!elemId.equals(""))
				ChartManager.plotStateCurve(caseId, elemId, rec.stateName,
						yDataLabel, rec.recType);
			else
				msg.sendWarnMsg("State/variable plotting not implemented yet");
		} else {
			msg.sendWarnMsg("Please select a varible/state to plotting");
		}
	}// GEN-LAST:event_plotButtonActionPerformed

	private void addMachStateButtonActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_addMachStateButtonActionPerformed
		IpssLogger.getLogger().info("Add mach button clicked");
		String elemId = (String) idItemSelectList.getSelectedValue();
		this.stateItemList.add((String) machStateComboBox.getSelectedItem()
				+ "(" + elemId + ")");
		refreashStateItemList();
	}// GEN-LAST:event_addMachStateButtonActionPerformed

	private void branchDeviceRadioButtonActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_branchDeviceRadioButtonActionPerformed
		IpssLogger.getLogger().info("Branch device button clicked");
		updateItemSelectList(simuCtx,
				SimuCtxHelper.DStabElemType_BranchDevice);
	}// GEN-LAST:event_branchDeviceRadioButtonActionPerformed

	private void branchRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_branchRadioButtonActionPerformed
		IpssLogger.getLogger().info("Branch button clicked");
		updateItemSelectList(simuCtx, SimuCtxHelper.DStabElemType_Branch);
	}// GEN-LAST:event_branchRadioButtonActionPerformed

	private void busDeviceRadioButtonActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_busDeviceRadioButtonActionPerformed
		IpssLogger.getLogger().info("Bus device button clicked");
		updateItemSelectList(simuCtx, SimuCtxHelper.DStabElemType_BusDevice);
	}// GEN-LAST:event_busDeviceRadioButtonActionPerformed

	private void busRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_busRadioButtonActionPerformed
		IpssLogger.getLogger().info("Bus button clicked");
		updateItemSelectList(simuCtx, SimuCtxHelper.DStabElemType_Bus);
	}// GEN-LAST:event_busRadioButtonActionPerformed

	private void machRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_machRadioButtonActionPerformed
		IpssLogger.getLogger().info("machine button clicked");
		updateItemSelectList(simuCtx, SimuCtxHelper.DStabElemType_Mach);
	}// GEN-LAST:event_machRadioButtonActionPerformed

	private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_closeButtonActionPerformed
		if (this.scriptFilename != null) {
			// save scripts
			GUIFileUtil.writeTextarea2FileAbsolutePath(scriptFilename,
					scriptTextArea);
		}
		this.setVisible(false);
		dispose();
	}// GEN-LAST:event_closeButtonActionPerformed

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new DStabPlotSelectionDialog(new javax.swing.JFrame(), true)
						.setVisible(true);
			}
		});
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton addBranchDeviceStateButton;
	private javax.swing.JButton addBranchStateButton;
	private javax.swing.JButton addBusDeviceStateButton;
	private javax.swing.JButton addBusStateButton;
	private javax.swing.JButton addExcStateButton;
	private javax.swing.JButton addGovStateButton;
	private javax.swing.JButton addMachStateButton;
	private javax.swing.JButton addPSSStateButton;
	private javax.swing.JRadioButton allRadioButton;
	private javax.swing.JComboBox branchDeviceComboBox;
	private javax.swing.JLabel branchDeviceLabel;
	private javax.swing.JRadioButton branchDeviceRadioButton;
	private javax.swing.JRadioButton branchRadioButton;
	private javax.swing.JComboBox branchVariableComboBox;
	private javax.swing.JLabel branchVariableLabel;
	private javax.swing.JComboBox busDeviceComboBox;
	private javax.swing.JLabel busDeviceLabel;
	private javax.swing.JRadioButton busDeviceRadioButton;
	private javax.swing.JRadioButton busRadioButton;
	private javax.swing.JComboBox busVariableComboBox;
	private javax.swing.JLabel busVariableLabel;
	private javax.swing.JButton closeButton;
	private javax.swing.JButton deleteButton;
	private javax.swing.JComboBox excStateComboBox;
	private javax.swing.JLabel excStateLabel;
	private javax.swing.JComboBox govStateComboBox;
	private javax.swing.JLabel govStateLabel;
	private javax.swing.JList idItemSelectList;
	private javax.swing.JPanel itemSelectPanel;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JScrollPane jScrollPane3;
	private javax.swing.JRadioButton machRadioButton;
	private javax.swing.JComboBox machStateComboBox;
	private javax.swing.JLabel machStateLabel;
	private javax.swing.JLabel machStateLabel1;
	private javax.swing.JLabel machStateLabel2;
	private javax.swing.JTabbedPane mainTabbedPane;
	private javax.swing.JButton plotButton;
	private javax.swing.JPanel plotPanel;
	private javax.swing.JComboBox pssStateComboBox;
	private javax.swing.JLabel pssstateLabel;
	private javax.swing.JTextArea scriptTextArea;
	private javax.swing.JButton scriptingButton;
	private javax.swing.JPanel scriptingPanel;
	private javax.swing.JList stateItemSelectList;
	private javax.swing.ButtonGroup typeButtonGroup;
	private javax.swing.JPanel typeSelectPanel;
	// End of variables declaration//GEN-END:variables

}
