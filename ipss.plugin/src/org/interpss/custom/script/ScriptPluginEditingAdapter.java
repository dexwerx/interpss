 /*
  * @(#)ScriptPluginEditingAdapter.java   
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

package org.interpss.custom.script;

import javax.swing.JPanel;

import org.interpss.ui.ICustomPluginEditor;
import org.interpss.ui.IScriptPluginEditing;

import com.interpss.common.util.XmlBeanUtil;

public class ScriptPluginEditingAdapter implements IScriptPluginEditing {  
	// the plugin data object
	private Object _data;
	
    // plugin UI Editor panel
    private ICustomPluginEditor _editPanel = null;
	
	private String name;
	private String desc;
	
	public ScriptPluginEditingAdapter(String name, String desc) {
		this.name = name;
		this.desc = desc;
	}
	
	@Override
	public void setData(Object obj) {
		_data = obj;
	}

	@Override
	public void setEditPanel(ICustomPluginEditor panel) {
		_editPanel = panel;
		
	}

    @Override
	public Object getData() {
    	return _data;
	}

	@Override
    public JPanel getEditPanel() {
        _editPanel.init(getData());
        return (JPanel)_editPanel;
    }
    
	@Override
    public String getDataXmlString() {
    	return XmlBeanUtil.toXmlString(_data);
    }

	@Override
	public void setData(String dataXmlStr) {
		_data = XmlBeanUtil.toObject(dataXmlStr, getData().getClass());
    }
    
	@Override
    public String getName() {
		return this.name;
	}

	/**
	 * Set the name, needed for Spring config
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getDesc() {
		return this.desc;
	}

	/**
	 * Set the desc, needed for Spring config
	 * 
	 * @param desc
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}
}