/*
 * @(#)IDialogUtil.java   
 *
 * Copyright (C) 2006-2011 www.interpss.com
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

package org.interpss.ui;

/**
 * During calculation/simulation process, some ui interaction is needed. This interface
 * is for specify such interactions. The actual implementation might be different for 
 * different ui implementation. 
 * 
 */
import java.util.Vector;

import javax.swing.JDialog;

public interface IDialogUtil {
	void showMsgDialog(String title, Vector<String> msgList);

	void showMsgDialog(JDialog dialog, String title, Vector<String> msgList);

	void showMsgDialog(String title, String msg);
	boolean showMsgDialogWithOptions(String title, String msg);

	void showMsgDialog(JDialog dialog, String title, String msg);

	void showErrMsgDialog(String title, String msg);

	void showErrMsgDialog(JDialog dialog, String title, String msg);

	void showWarnMsgDialog(String title, String msg);

	void showWarnMsgDialog(JDialog dialog, String title, String msg);

	boolean saveCurrentProjectDialog();
}