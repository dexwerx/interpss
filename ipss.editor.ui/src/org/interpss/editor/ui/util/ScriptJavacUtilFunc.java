 /*
  * @(#)ScriptJavacUtilFunc.java   
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
  * @Date 02/15/2007
  * 
  *   Revision History
  *   ================
  *
  */

package org.interpss.editor.ui.util;

import org.interpss.editor.jgraph.GraphSpringFactory;
import org.interpss.editor.jgraph.ui.IGraphicEditor;
import org.interpss.util.FileUtil;
import org.interpss.util.MemoryJavaCompiler;

import com.interpss.common.util.IpssJavaCompiler;
import com.interpss.common.util.IpssLogger;

public class ScriptJavacUtilFunc {
	public final static String CheckCodeTempPackageName = "temp/";
	public final static String CheckCodeClassname = "CheckCode";
	
	public final static String Token_CodeReuse = "@";
	public final static String Tag_Package = "<package>";
	public final static String Tag_Classname = "<classname>";
	public final static String Tag_BaseClassname = "<baseClassname>";
	public static String Tal_ClassnameLine = "public class <classname> extends <baseClassname> {";

	/**
	 * Before saving java code, the code is saved as temp file and compiled to check any syntax error
	 * 
	 * @param baseClassname, base class name (AbstractExciter, AbstractGovernor ...)
	 * @return false if there is compiling error
	 */
	public static boolean checkJavaCode(String javacode, String packageName) {
    	IGraphicEditor editor = GraphSpringFactory.getIpssGraphicEditor();
		String filename = IpssJavaCompiler.createJavaFilename(CheckCodeClassname, 
								ScriptJavacUtilFunc.CheckCodeTempPackageName, editor.getRootDir());
		FileUtil.writeText2File(filename, javacode);	
		if (!packageName.endsWith("/"))
			packageName += "/";
		try {
			MemoryJavaCompiler.clearClassMap();
			MemoryJavaCompiler.javac(packageName+CheckCodeClassname, javacode);
		} catch (Exception e) {
			IpssLogger.logErr(e);
			return false;
		}
		return true;
	}

	/**
	 * create scripting object classname foldername/projectname/id
	 * 
	 * @param id
	 * @return
	 */
	public static String createScriptingClassname(String id) {
    	IGraphicEditor editor = GraphSpringFactory.getIpssGraphicEditor();
		String	folderName = editor.getCurrentProjectFolder();
		String	projName = editor.getCurrentProjectName();
		return IpssJavaCompiler.createClassName(id, folderName, projName);
	}
}
