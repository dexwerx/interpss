/*
 * @(#)IpssDBCase.java   
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

package org.interpss.output.db;

import com.interpss.common.datatype.ScriptLangEnum;
import com.interpss.common.rec.BaseDataBean;

public class IpssDBCase extends BaseDataBean {
	private static final long serialVersionUID = 1;

	protected int caseDbId = 0;
	protected int projDbId = 0;
	protected String caseName = "";

	/**
	 * @return Returns the caseName.
	 */
	public String getCaseName() {
		return caseName;
	}

	/**
	 * @param caseName The caseName to set.
	 */
	public void setCaseName(String caseName) {
		this.caseName = caseName;
	}

	/**
	 * @return Returns the projDbId.
	 */
	public int getProjDbId() {
		return projDbId;
	}

	/**
	 * @param projDbId The projDbId to set.
	 */
	public void setProjDbId(int projDbId) {
		this.projDbId = projDbId;
	}

	/**
	 * @return Returns the id.
	 */
	public int getCaseDbId() {
		return caseDbId;
	}

	/**
	 * @param id The id to set.
	 */
	public void setCaseDbId(int id) {
		this.caseDbId = id;
	}

	public ScriptLangEnum getScriptLanguageType() {
		return this.getScriptLanguage() == ScriptLanguage_Java ? ScriptLangEnum.Java :
				(this.getScriptLanguage() == ScriptLanguage_Xml ? ScriptLangEnum.Xml
						: ScriptLangEnum.Custom );
	}
}
