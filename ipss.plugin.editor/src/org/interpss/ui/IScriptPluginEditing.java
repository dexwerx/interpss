 /*
  * @(#)ScriptEditing.java   
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

package org.interpss.ui;

import javax.swing.JPanel;


public interface IScriptPluginEditing {  
    /**
     * Get the data object for the scripting plugin object
     *
     * @return the data object
     */
    Object getData();
    
    /**
     * Get the data object in Xml string format for the scripting plugin object
     *
     * @return the data object xml string
     */
    String getDataXmlString();

    /**
     * Set the data object in Xml string format for the scripting plugin object
     *
     * @param dataXmlStr the data object xml string
     */
    void setData(String dataXmlStr);
    
    /**
     * Set the data object for the scripting plugin object
     *
     * @param obj the data object
     */
	void setData(Object obj);

    /**
     * Set the editing panel object for the scripting plugin object
     *
     * @param panel the panel object
     */
	void setEditPanel(ICustomPluginEditor panel);
    
    /**
     * Get the editor panel object for data editing
     *
     * @return the editor panel object
     */
    JPanel getEditPanel();
    
    /**
     * Get the plugin object name attribute
     *
     * @return the plugin name
     */
    String getName();
    
    /**
     * Get the plugin object description attribute
     *
     * @return the plugin description
     */
    String getDesc();
}