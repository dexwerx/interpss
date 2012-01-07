/*
 * @(#)DialogUtil.java   
 *
 * Copyright (C) 2006 www.interpss.com
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

import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.interpss.common.exp.InterpssRuntimeException;
import com.interpss.common.msg.IpssMessage;
import com.interpss.common.msg.IpssMsgListener;
import com.interpss.common.msg.impl.TextMessageImpl;

public class DialogUtilImpl implements IDialogUtil, IpssMsgListener {
	public static JFrame parent = null;

	public void showMsgDialog(String title, Vector<String> msgList) {
		String msg = "";
		for (int i = 0; i < msgList.size(); i++)
			msg += msgList.get(i) + "\n";
		showMsgDialog("Input Data Error", msg);
	}

	public void showMsgDialog(JDialog dialog, String title,
			Vector<String> msgList) {
		showMsgDialog(title, msgList);
	}

	public void showMsgDialog(JDialog dialog, String title, String msg) {
		showMsgDialog(title, msg);
	}

	public void showMsgDialog(String title, String msg) {
		if (msg.length() > 100)
			msg = msg.substring(0, 100) + " ...";
		JOptionPane.showMessageDialog(parent, msg, title, JOptionPane.INFORMATION_MESSAGE);
	}

	public boolean showMsgDialogWithOptions(String title, String msg) {
		if (msg.length() > 100)
			msg = msg.substring(0, 100) + " ...";
		return JOptionPane.showConfirmDialog(parent, msg, title, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
	}

	public void showErrMsgDialog(JDialog dialog, String title, String msg) {
		showErrMsgDialog(title, msg);
	}

	public void showErrMsgDialog(String title, String msg) {
		if (msg.length() > 100)
			msg = msg.substring(0, 100) + " ...";
		JOptionPane.showMessageDialog(parent, msg, title, JOptionPane.ERROR_MESSAGE);
	}

	public void showWarnMsgDialog(JDialog dialog, String title, String msg) {
		showWarnMsgDialog(title, msg);
	}

	public void showWarnMsgDialog(String title, String msg) {
		if (msg.length() > 100)
			msg = msg.substring(0, 100) + " ...";
		JOptionPane.showMessageDialog(parent, msg, title, JOptionPane.WARNING_MESSAGE);
	}

	public boolean saveCurrentProjectDialog() {
		return JOptionPane.showConfirmDialog(
						parent,
						"The current case has been edited, \nDo you want to save to the server?",
						"Save current case", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
	}

	/**
	 * Implementation of the onMsgEvent method.
	 * 
	 * @param msg the msg object
	 */
	public void onMsgEvent(IpssMessage msg) {
		if (msg instanceof TextMessageImpl) {
			if (msg.getType() == TextMessageImpl.TYPE_WARN)
				showWarnMsgDialog("Warnning", msg.getMsgString());
			else if (msg.getType() == TextMessageImpl.TYPE_ERROR)
				showWarnMsgDialog("Error", msg.getMsgString());
		}
	}

	public boolean onMsgEventStatus(IpssMessage msg) {
		throw new InterpssRuntimeException("Method not implemented");
	}
}