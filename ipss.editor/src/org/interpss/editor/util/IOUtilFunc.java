 /*
  * @(#)IOUtilFunc.java   
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

package org.interpss.editor.util;

import java.io.FileInputStream;
import java.io.InputStream;

import org.interpss.editor.coreframework.GPGraphpad;
import org.interpss.editor.coreframework.GPSessionParameters;
import org.interpss.editor.form.base.BaseBranchForm;
import org.interpss.editor.jgraph.GraphSpringFactory;
import org.interpss.editor.jgraph.cells.GraphUtilFunc;
import org.interpss.editor.project.IpssGraphCodec;
import org.jgraph.JGraph;

import com.interpss.common.util.IpssLogger;

public class IOUtilFunc  {
	/**
	 * Create a JGraph object and Load a Ipss graphic file into the object
	 * 
	 * @param filename
	 * @return the JGraph object
	 */   
	public static JGraph loadIpssGraphFile(String filename) {
		GPGraphpad pad = new GPGraphpad((GPSessionParameters)null); 
		JGraph graph = GraphSpringFactory.getIpssGraph();
		
		try {
			InputStream in = new FileInputStream(filename);					
			BaseBranchForm.XmlBinding = false;
			IpssGraphCodec.getInstance(pad).read(in, graph);
			BaseBranchForm.XmlBinding = true;
			GraphUtilFunc.rebuildLabelRelationship(graph);
		} catch (Exception e) {
			IpssLogger.logErr(e);
		}
		return graph;
	}
}
