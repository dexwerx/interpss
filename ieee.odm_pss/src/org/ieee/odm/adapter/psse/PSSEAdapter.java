/*
 * @(#)PSSEAdapter.java   
 *
 * Copyright (C) 2006-2009 www.interpss.org
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
 * @Date 02/11/2008
 * 
 *   Revision History
 *   ================
 *
 */
package org.ieee.odm.adapter.psse;

import java.util.StringTokenizer;

import org.ieee.odm.adapter.AbstractODMAdapter;
import org.ieee.odm.adapter.IFileReader;
import org.ieee.odm.adapter.psse.BasePSSEAdapter.PsseVersion;
import org.ieee.odm.common.ODMException;
import org.ieee.odm.model.IODMModelParser;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.schema.LineBranchXmlType;
import org.ieee.odm.schema.LineShortCircuitXmlType;
import org.ieee.odm.schema.LoadflowBusXmlType;
import org.ieee.odm.schema.LoadflowNetXmlType;
import org.ieee.odm.schema.PSXfrBranchXmlType;
import org.ieee.odm.schema.PSXfrShortCircuitXmlType;
import org.ieee.odm.schema.ShortCircuitBusXmlType;
import org.ieee.odm.schema.ShortCircuitNetXmlType;
import org.ieee.odm.schema.XfrBranchXmlType;
import org.ieee.odm.schema.XfrShortCircuitXmlType;

/**
 * ODM adapter for PSS/E input format
 * 
 * @author mzhou
 *
 */
public class PSSEAdapter extends AbstractODMAdapter{
	

	private PsseVersion adptrtVersion;
	
	
	public PSSEAdapter(PsseVersion ver) {
		super();
		this.adptrtVersion = ver;
	
	}

	public AclfModelParser parseAclfFile(final IFileReader din, String encoding) throws Exception {
		PSSELFAdapter<LoadflowNetXmlType, LoadflowBusXmlType, LineBranchXmlType, XfrBranchXmlType, PSXfrBranchXmlType> 
		lfAdapter = new PSSELFAdapter<>(this.adptrtVersion);
		//new PSSELFAdapter<LoadflowNetXmlType, LoadflowBusXmlType, LineBranchXmlType, XfrBranchXmlType, PSXfrBranchXmlType>(this.adptrtVersion);
	    return lfAdapter.parseLoadflowFile(din, encoding);
	
	}
	
	public IODMModelParser parseAcscFiles(final IFileReader[] din, String encoding) throws Exception {
		PSSEAcscAdapter<ShortCircuitNetXmlType, ShortCircuitBusXmlType, LineShortCircuitXmlType, XfrShortCircuitXmlType, PSXfrShortCircuitXmlType> 
		acscAdapter = new PSSEAcscAdapter<>(this.adptrtVersion);
		
		return acscAdapter.parseInputFile(NetType.AcscNet, din, encoding);
		 
	}
	
	public IODMModelParser parseDstabFiles(final IFileReader[] din, String encoding) throws Exception {
		PSSEDynAdapter dynAdapter = new PSSEDynAdapter(this.adptrtVersion);
		return dynAdapter.parseInputFile(NetType.DStabNet, din, encoding);
		
	}
	
	/**
	 * PTI use 0 to indicate end of a data set, Bus Data for example. This function checks
	 * if the input line is the end of record line
	 *
	 * @param str a input data line string
	 */
	public static boolean isEndRecLine(String str) {
		String s = str.trim();
		return s.startsWith("0") || s.startsWith("/") || s.startsWith("Q");
	}	
	
	private boolean is3WXfr(String str) {
		// for 2W xfr, line1, K = 0
  		StringTokenizer st = new StringTokenizer(str, ",");
		st.nextToken();
		st.nextToken();
		int K = new Integer(st.nextToken().trim()).intValue();
		return K != 0;
	}

	@Override
	protected IODMModelParser parseInputFile(IFileReader din, String encoding)
			throws Exception {
		return parseAclfFile(din,encoding);
	}

	@Override
	protected IODMModelParser parseInputFile(NetType type, IFileReader[] din,
			String encoding) throws Exception {
		throw new ODMException("not implemented yet");
	}
}
