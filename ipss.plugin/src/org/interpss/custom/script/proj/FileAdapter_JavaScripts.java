 /*
  * @(#)FileAdapter_JavaScripts.java   
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
  * @Date 05/01/2007
  * 
  *   Revision History
  *   ================
  *
  */

package org.interpss.custom.script.proj;

import static com.interpss.common.util.IpssLogger.ipssLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;

import org.interpss.fadapter.impl.IpssFileAdapterBase;

import com.interpss.SimuObjectFactory;
import com.interpss.common.exp.InterpssException;
import com.interpss.common.exp.InterpssRuntimeException;
import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;

public class FileAdapter_JavaScripts extends IpssFileAdapterBase {
	public FileAdapter_JavaScripts(IPSSMsgHub msgHub) {
		super(msgHub);
	}
	/**
	 * Load the data in the data file, specified by the filepath, into the SimuContext object. An AclfAdjNetwork
	 * object will be created to hold the data for loadflow analysis.
	 * 
	 * @param simuCtx the SimuContext object
	 * @param filepath full path path of the input file
	 * @param msg the SessionMsg object
	 */
	@Override
	public void load(final SimuContext simuCtx, final String filepath, boolean debug, String outfile) throws InterpssException{
		try {
			final File file = new File(filepath);
			final InputStream stream = new FileInputStream(file);
			final BufferedReader din = new BufferedReader(new InputStreamReader(stream));
	      	String scripts = "", s;
	      	while ((s = din.readLine()) != null) {
	      		scripts += s + "\n";
	       	}
	      	// System.out.println(str);
	      	
			ScriptEngine engine = SimuObjectFactory.createScriptEngine();
			engine.eval(scripts);
			Object loader = getScritingObject(engine, msgHub);
			((Invocable)engine).invokeMethod(loader, "load", simuCtx, msgHub);		
		} catch (Exception e) {
			ipssLogger.severe(e.toString());
			throw new InterpssException(e.toString());
		}
	}
	
	private Object getScritingObject(ScriptEngine engine, IPSSMsgHub msg) {
		try {
			String objName = (String)((Invocable)engine).invokeFunction("getObjectName");
			Object obj = engine.get(objName);
			if (obj == null) {
				msg.sendErrorMsg("The loader scripting object not found, name:" + objName);
				return null;
			}
			return obj;
		} catch (Exception e ) {
			msg.sendErrorMsg("Error in loading the scripting object, getObjectName() method not defined," + e.toString());
		}
		return null;
	}	
	/**
	 * Create a SimuContext object and Load the data in the data file, specified by the filepath, into the object. 
	 * An AclfAdjNetwork object will be created to hold the data for loadflow analysis.
	 * 
	 * @param filepath full path path of the input file
	 * @param msg the SessionMsg object
	 * @return the created SimuContext object.
	 */
	@Override
	public SimuContext load(final String filepath) throws InterpssException {
  		final SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.NOT_DEFINED);
  		load(simuCtx, filepath, false, null);
  		return simuCtx;
	}
	
	/**
	 * This method is currently not implemented, since the loadflow results are not going to write
	 * back to a data file.
	 */
	@Override
	public boolean save(final String filepath, final SimuContext net) throws InterpssException{
		throw new InterpssRuntimeException("FileAdapter_IpssInternalFormat.save not implemented");
	}
}
