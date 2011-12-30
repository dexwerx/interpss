 /*
  * @(#)DStabControllerData.java   
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

package org.interpss.editor.data.dstab;

import org.interpss.db.BaseDataBean;

public class DStabControllerData extends BaseDataBean {
	private static final long serialVersionUID = 1;

	private String typeName = "";
	private String className = "";   // this property is not used anymore	
	private String dataXmlStr = "";

	/**
	 * @return Returns the dataXmlStr.
	 */
	public String getDataXmlStr() {
		return dataXmlStr;
	}
	/**
	 * @param dataXmlStr The dataXmlStr to set.
	 */
	public void setDataXmlStr(String dataXmlStr) {
		this.dataXmlStr = dataXmlStr;
	}
	/**
	 * @return Returns the typeName.
	 */
	public String getTypeName() {
		return typeName;
	}
	/**
	 * @param typeName The typeName to set.
	 */
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	/**
	 * @return Returns the className.
	 */
	public String getClassName() {
		return className;
	}
	/**
	 * @param className The className to set.
	 */
	public void setClassName(String className) {
		this.className = className;
	}
}
