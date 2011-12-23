 /*
  * @(#)IOutputTextDialog.java   
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

package org.interpss.editor.ui;

import java.io.File;

import com.interpss.common.msg.IpssMsgListener;

public interface IOutputTextDialog extends IpssMsgListener {
    /**
     * Set the display dialog window title
     * 
     * @param title
     */
	void setTitle(String title);
	
	/**
	 * display the data object
	 * 
	 * @param data
	 */
    void display(Object data);

	/**
	 * display the data object
	 * 
	 * @param data
	 * @param col default 120
	 * @param row  default 30
	 */
    void display(Object aData, int col, int row);
	
    /**
     * Load the file and display to the textarea
     * 
     * @param filepath
     */
    void display(File file);
    
    /**
     * Append a string text to the dispaly area
     * 
     * @param text
     */
	void appendText(String text);
    
	/**
	 * Show the display dialog box
	 */
    void showDialog();

    /**
     * clear the display area
     */
    void clearTextArea();

    /**
     * disable certain feature, such SaveAs... button
     */
    void disableFeature(String featureName);
}
