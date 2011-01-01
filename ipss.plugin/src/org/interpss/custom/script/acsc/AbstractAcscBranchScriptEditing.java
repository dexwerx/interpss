 /*
  * @(#)AbstractAcscBranchScriptEditing.java   
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
  * @Date 11/27/2007
  * 
  *   Revision History
  *   ================
  *
  */

package org.interpss.custom.script.acsc;

import javax.swing.JPanel;

import org.interpss.custom.script.ScriptPluginEditingAdapter;
import org.interpss.ui.ICustomPluginEditor;
import org.interpss.ui.IScriptPluginEditing;

import com.interpss.core.acsc.impl.AbstractAcscBranch;


public abstract class AbstractAcscBranchScriptEditing extends AbstractAcscBranch implements IScriptPluginEditing {  
	private ScriptPluginEditingAdapter pluginAdapter = null;

	public AbstractAcscBranchScriptEditing(String name, String desc) {
		this.pluginAdapter = new ScriptPluginEditingAdapter(name, desc);;
	}
	
	@Override
	public void setData(Object obj) {
		this.pluginAdapter.setData(obj);
	}

	@Override
	public void setEditPanel(ICustomPluginEditor panel) {
		this.pluginAdapter.setEditPanel(panel);
	}

	@Override
	public Object getData() {
		return this.pluginAdapter.getData();
	}
	
	@Override
    public JPanel getEditPanel() {
        return this.pluginAdapter.getEditPanel();
    }
    
	@Override
    public String getDataXmlString() {
    	return this.pluginAdapter.getDataXmlString();
    }

	@Override
	public void setData(String dataXmlStr) {
		this.pluginAdapter.setData(dataXmlStr);
    }
    
	@Override
    public String getName() {
		return this.pluginAdapter.getName();
	}

	@Override
	public String getDesc() {
		return this.pluginAdapter.getDesc();
	}

	public void setName(String name) {
		this.pluginAdapter.setName(name);
	}

	public void setDesc(String desc) {
		this.pluginAdapter.setDesc(desc);
	}
}