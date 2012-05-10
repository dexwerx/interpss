/*
 * @(#)FileChooserConfig.java   
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

package org.interpss.editor.io;

import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;

import org.interpss.editor.report.IpssRptViewer;
import org.interpss.editor.ui.util.IpssFileFilter;
import org.interpss.fadapter.IpssFileAdapter;


public class FileChooserConfig {
	public static String CurrentProjDir = "C:/eclipse/Interpss/ipss.test/testData/editor.jgraph/";

	private static JFileChooser openProjFileChooser = null;
	private static JFileChooser saveProjFileChooser = null;

	private static JFileChooser openRptFileChooser = null;
	private static JFileChooser saveRptFileChooser = null;

	public static final String GraphFileExt = "ipss";
	public static final String GraphFileExtDesc = "InterPSS Graphic Format";

	/**
	 * Get a file chooser for open file menu
	 * 
	 * @param adapterList custom adapter list defined in the SpingAppContext
	 * @return the chooser
	 */
	public static JFileChooser getOpenProjFileChooser(List adapterList) {
		if (openProjFileChooser == null) {
			openProjFileChooser = new JFileChooser();
			for (int i = 0; i < adapterList.size(); i++) {
				IpssFileAdapter adapter = (IpssFileAdapter) adapterList.get(i);
				openProjFileChooser.addChoosableFileFilter(new IpssFileFilter(
						adapter.getExtension(), adapter.getDescription()));
			}
			openProjFileChooser.addChoosableFileFilter(new IpssFileFilter(
					GraphFileExt, GraphFileExtDesc));
		}
		openProjFileChooser.setCurrentDirectory(new File(CurrentProjDir));
		return openProjFileChooser;
	}

	/**
	 * Get a file chooser for open report file menu
	 * 
	 * @return the chooser
	 */
	public static JFileChooser getOpenReportFileChooser() {
		if (openRptFileChooser == null) {
			openRptFileChooser = new JFileChooser();
			openRptFileChooser.addChoosableFileFilter(new IpssFileFilter(
					IpssRptViewer.REPORT_EXT, IpssRptViewer.REPORT_DESC));
		}
		openRptFileChooser.setCurrentDirectory(new File(
				IpssRptViewer.REPORT_DEFAULT_DIR));
		return openRptFileChooser;
	}

	/**
	 * Get a file chooser for save file menu
	 * 
	 * @return the chooser
	 */
	public static JFileChooser getSaveProjFileChooser() {
		if (saveProjFileChooser == null) {
			saveProjFileChooser = new JFileChooser();
			saveProjFileChooser.addChoosableFileFilter(new IpssFileFilter(
					GraphFileExt, GraphFileExtDesc));
		}
		saveProjFileChooser.setCurrentDirectory(new File(CurrentProjDir));
		return saveProjFileChooser;
	}

	/**
	 * Get a file chooser for save report menu
	 * 
	 * @return the chooser
	 */
	public static JFileChooser getSaveReportFileChooser() {
		if (saveRptFileChooser == null) {
			saveRptFileChooser = new JFileChooser();
			saveRptFileChooser.addChoosableFileFilter(new IpssFileFilter(
					IpssRptViewer.REPORT_EXT, IpssRptViewer.REPORT_DESC));
		}
		saveRptFileChooser.setCurrentDirectory(new File(
				IpssRptViewer.REPORT_DEFAULT_DIR));
		return saveRptFileChooser;
	}
}