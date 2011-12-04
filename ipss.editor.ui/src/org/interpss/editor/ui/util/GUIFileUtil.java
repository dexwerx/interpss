 /*
  * @(#)GUIFileUtil.java   
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

package org.interpss.editor.ui.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JTextArea;

import org.interpss.editor.jgraph.GraphSpringAppContext;
import org.interpss.util.FileUtil;

import com.interpss.common.util.IpssLogger;
import com.interpss.spring.CoreCommonSpringFactory;

public class GUIFileUtil {
	/**
	 * Read to contents of the file (text file) into the textarea
	 * 
	 * @param filename
	 * @param textArea
	 * @return
	 */
	public static boolean readFile2TextareaAbsolutePath(String filename, JTextArea textArea) {
		IpssLogger.getLogger().info("GUIFileUtil.readFile2Textarea() info from file: " + filename);
		File textfile = new File(filename);
	      FileReader reader = null;
	      try {
	    	  reader = new FileReader(textfile);
	    	  textArea.read(reader, null);
	    	  return true;
	      } catch (IOException ex) {
				IpssLogger.getLogger().info("Cannot readt file: " + filename);
	      } finally {
	    	  if (reader != null) {
	    		  try {
	    			  reader.close();
	    		  } catch (IOException x) {
	  				  CoreCommonSpringFactory.getIpssMsgHub().sendErrorMsg("Cannot close file: " + filename);
	    		  }
	    	  }
	      }		
	      return false;
	}

	/**
	 * Read to contents of the file (text file) into the textarea
	 * 
	 * @param filename, retive file path
	 * @param textArea
	 * @return
	 */
	public static boolean readFile2TextareaRativePath(String filename, JTextArea textArea) {
		String wdir = GraphSpringAppContext.getIpssGraphicEditor().getWorkspace();
		filename = wdir+System.getProperty("file.separator")+filename;		
		return readFile2TextareaAbsolutePath(filename, textArea);
	}
	
	/**
	 * Write the contents of the textarea to the file
	 * 
	 * @param filename relative path to the workspace
	 * @param textArea
	 * @return
	 */
	public static boolean writeTextarea2FileRativePath(String filename, JTextArea textArea) {
		IpssLogger.getLogger().info("GUIFileUtil.writeTextarea2File() info to file: " + filename);
		String wdir = GraphSpringAppContext.getIpssGraphicEditor().getWorkspace();
		filename = wdir+System.getProperty("file.separator")+filename;		
		return writeTextarea2FileAbsolutePath(filename, textArea);
	}
	
	/**
	 * Write the contents of the textarea to the file
	 * 
	 * @param filename abusolute path
	 * @param textArea
	 * @return
	 */
	public static boolean writeTextarea2FileAbsolutePath(String filename, JTextArea textArea) {
		return 	FileUtil.writeText2File(filename, textArea.getText());
	}
	
	/**
	 * Write the text to the file
	 * 
	 * @param filename relative path to the workspace
	 * @param textArea
	 * @return
	 */
	public static boolean writeText2FileRativePath(String filename, String text) {
		IpssLogger.getLogger().info("GUIFileUtil.writeTextarea2File() info to file: " + filename);
		String wdir = GraphSpringAppContext.getIpssGraphicEditor().getWorkspace();
		filename = wdir+System.getProperty("file.separator")+filename;		
		return FileUtil.writeText2File(filename, text);
	}
}
