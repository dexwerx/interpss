/*
 * @(#)BaseDataBean.java   
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

package org.interpss.db;

import java.io.Serializable;

import com.interpss.common.util.XmlBeanUtil;

/**
 * All data object are JavaBean, which could be persisted as a Xml String
 */

public class BaseDataBean implements Serializable {
	private static final long serialVersionUID = 1;

	public static final int ScriptLanguage_Java = 1;
	public static final int ScriptLanguage_Xml = 2;
	public static final int ScriptLanguage_Plugin = 3;

	public BaseDataBean() {
	}

	private String scripts = "";
	public String getScripts() {
		return this.scripts;
	}
	public void setScripts(String scripts) {
		this.scripts = scripts;
	}

	private int scriptLanguage = ScriptLanguage_Java;
	public int getScriptLanguage() {
		return this.scriptLanguage;
	}
	public void setScriptLanguage(int scriptLanguage) {
		this.scriptLanguage = scriptLanguage;
	}

	private String scriptPluginName = "";
	public String getScriptPluginName() {
		return this.scriptPluginName;
	}
	public void setScriptPluginName(String scriptPluginName) {
		this.scriptPluginName = scriptPluginName;
	}

	private String scriptPluginXmlStr = "";
	public String getScriptPluginXmlStr() {
		return this.scriptPluginXmlStr;
	}
	public void setScriptPluginXmlStr(String scriptPluginXmlStr) {
		this.scriptPluginXmlStr = scriptPluginXmlStr;
	}

	private String scriptDriver = "";
	public String getScriptDriver() {
		return this.scriptDriver;
	}
	public void setScriptDriver(String scriptDriver) {
		this.scriptDriver = scriptDriver;
	}

	/* if true, perform grid computing */
	private boolean gridComputing = false;
	public boolean isGridComputing() {
		return this.gridComputing;
	}
	public void setGridComputing(boolean b) {
		this.gridComputing = b;
	}

	/* grid node name */
	private String gridNodeName = "";
	public String getGridNodeName() {
		return this.gridNodeName;
	}
	public void setGridNodeName(String s) {
		this.gridNodeName = s;
	}

	/* timeout in long */
	private long gridTimeout = 0;
	public long getGridTimeout() {
		return this.gridTimeout;
	}
	public void setGridTimeout(long d) {
		this.gridTimeout = d;
	}

	@Override
	public String toString() {
		return XmlBeanUtil.toXmlString(this);
	}
}
