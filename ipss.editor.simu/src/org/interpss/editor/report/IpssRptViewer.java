/*
 * @(#)IpssRptViewer.java   
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

package org.interpss.editor.report;

/**
 *	InterPSS report viewer, which also is a JPanel object
 */

import java.io.File;
import java.io.InputStream;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.util.JRClassLoader;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.view.JRSaveContributor;
import net.sf.jasperreports.view.JRViewer;

import org.interpss.editor.io.FileChooserConfig;
import org.interpss.spring.PluginSpringFactory;

import com.interpss.common.util.IpssLogger;
import com.interpss.spring.CoreCommonSpringFactory;

public class IpssRptViewer extends JRViewer {
	private static final long serialVersionUID = 1;

	public static String REPORT_DEFAULT_DIR = "";
	public static String REPORT_EXT = "ipssrpt";
	public static String REPORT_DESC = "InterPSS Report";

	private JasperPrint jasperPrint = null;

	// Singleton instance
	private static IpssRptViewer singleton;

	/**
	 *	Constructor
	 */
	public IpssRptViewer() throws Exception {
		super(null);
		getToolbar().remove(this.btnReload);
		getToolbar().remove(this.btnSave);
		/*		
		 // Export Button
		 JButton btnExport = createButton("export.gif", "Export the current report");
		 btnExport.addActionListener(new ActionListener() {
		 public void actionPerformed(ActionEvent e) {
		 exportActionPerformed();
		 }
		 });
		 getToolbar().add(btnExport, 0);

		 // Save Button
		 this.btnSave = 	createButton("save.gif", "Save the current report");
		 this.btnSave.addActionListener(new ActionListener() {
		 public void actionPerformed(ActionEvent e) {
		 saveActionPerformed();
		 }
		 });
		 getToolbar().add(this.btnSave, 0);
		
		 // Up close report view button
		 JButton btnUp = createButton("upLevel.gif", "Close the report view and move to editor view");
		 btnUp.addActionListener(new ActionListener() {
		 public void actionPerformed(ActionEvent e) {
		 //GraphSpringAppContext.getIpssGraphicEditor().closeReportView();
		 }
		 });
		 getToolbar().add(btnUp, 0);
		 */
	}

	public void loadReport(InputStream is) {
		try {
			jasperPrint = (JasperPrint) JRLoader.loadObject(is);
		} catch (Exception e) {
			IpssLogger.logErr(e);
			CoreCommonSpringFactory.getIpssMsgHub().sendErrorMsg(e.toString());
		}
		loadReport(jasperPrint);
	}

	public void loadReport(JasperPrint jrPrint) {
		jasperPrint = jrPrint;
		try {
			super.loadReport(jrPrint);
			this.refreshPage();
		} catch (Exception e) {
			IpssLogger.logErr(e);
			PluginSpringFactory.getEditorDialogUtil().showMsgDialog(
					"InterPSS Report Error", e.toString());
		}
	}

	public void loadReport(String filename) {
		IpssLogger.getLogger().info("Selected Rpt Name: " + filename);
		try {
			jasperPrint = (JasperPrint) JRLoader.loadObject(filename);
		} catch (Exception e) {
			IpssLogger.logErr(e);
			PluginSpringFactory.getEditorDialogUtil().showMsgDialog(
					"InterPSS Report Error", e.toString());
		}
		loadReport(jasperPrint);
	}

	public void printReport() {
		this.btnPrint.doClick();
	}

	public void saveActionPerformed() {
		JFileChooser fileChooser = FileChooserConfig.getSaveReportFileChooser();

		int retValue = fileChooser.showSaveDialog(this);
		if (retValue == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			save(file.getAbsolutePath());
		}
	}

	public void save(String filename) {
		try {
			JRSaver.saveObject(jasperPrint, new File(filename));
		} catch (JRException e) {
			IpssLogger.logErr(e);
			PluginSpringFactory.getEditorDialogUtil().showMsgDialog(
					"Save InterPSS Report Error", e.toString());
		}
	}

	public void exportActionPerformed() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(
				IpssRptViewer.REPORT_DEFAULT_DIR));

		JRSaveContributor rtfSaveContrib = null;
		try {
			Class rtfSaveContribClass = JRClassLoader
					.loadClassForName("net.sf.jasperreports.view.save.JRRtfSaveContributor");
			rtfSaveContrib = (JRSaveContributor) rtfSaveContribClass
					.newInstance();
			fileChooser.addChoosableFileFilter(rtfSaveContrib);
		} catch (Exception ex) {
		}

		JRSaveContributor htmlSaver = null;
		try {
			Class htmlSaverClass = JRClassLoader
					.loadClassForName("net.sf.jasperreports.view.save.JRHtmlSaveContributor");
			htmlSaver = (JRSaveContributor) htmlSaverClass.newInstance();
			fileChooser.addChoosableFileFilter(htmlSaver);
		} catch (Exception e) {
		}

		JRSaveContributor pdfSaveContrib = null;
		try {
			Class pdfSaveContribClass = JRClassLoader
					.loadClassForName("net.sf.jasperreports.view.save.JRPdfSaveContributor");
			pdfSaveContrib = (JRSaveContributor) pdfSaveContribClass
					.newInstance();
			fileChooser.addChoosableFileFilter(pdfSaveContrib);
		} catch (Exception e) {
		}

		int retValue = fileChooser.showSaveDialog(this);
		if (retValue == JFileChooser.APPROVE_OPTION) {
			FileFilter fileFilter = fileChooser.getFileFilter();
			File file = fileChooser.getSelectedFile();
			String lowerCaseFileName = file.getName().toLowerCase();

			try {
				if (fileFilter instanceof JRSaveContributor) {
					((JRSaveContributor) fileFilter).save(jasperPrint, file);
				} else {
					if (lowerCaseFileName.endsWith(".pdf")
							&& pdfSaveContrib != null) {
						pdfSaveContrib.save(jasperPrint, file);
					} else if ((lowerCaseFileName.endsWith(".html") || lowerCaseFileName
							.endsWith(".htm"))
							&& htmlSaver != null) {
						htmlSaver.save(jasperPrint, file);
					} else {
						if (!file.getName().endsWith(".ipssprt")) {
							file = new File(file.getAbsolutePath() + ".ipssprt");
						}
						JRSaver.saveObject(jasperPrint, file);
					}
				}
			} catch (JRException e) {
				IpssLogger.logErr(e);
				CoreCommonSpringFactory.getIpssMsgHub().sendErrorMsg(e.toString());
			}
		}
	}

	private JPanel getToolbar() {
		return this.tlbToolBar;
	}

	private JButton createButton(String iconFile, String helpTip) {
		JButton btn = new JButton();
		btn.setIcon(new ImageIcon(getClass().getClassLoader().getResource(
				iconFile)));
		btn.setToolTipText(helpTip);
		btn.setMargin(new java.awt.Insets(2, 2, 2, 2));
		btn.setMaximumSize(new java.awt.Dimension(23, 23));
		btn.setMinimumSize(new java.awt.Dimension(23, 23));
		btn.setPreferredSize(new java.awt.Dimension(23, 23));
		btn.setEnabled(true);
		return btn;
	}

	/**
	 * Returns an context singleton.
	 */
	public static IpssRptViewer getDefault() {
		if (singleton == null) {
			try {
				singleton = new IpssRptViewer();
			} catch (Exception e) {
				IpssLogger.logErr(e);
				CoreCommonSpringFactory.getIpssMsgHub().sendErrorMsg(e.toString());
			}
		}
		return singleton;
	}
}
