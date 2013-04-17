/*
 * @(#)PSSEBusDataParser.java   
 *
 * Copyright (C) 2006-2013 www.interpss.org
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
 * @Date 04/11/2013
 * 
 *   Revision History
 *   ================
 *
 */

package org.ieee.odm.adapter.psse.parser;

import java.util.StringTokenizer;

import org.ieee.odm.adapter.psse.PsseVersion;
import org.ieee.odm.common.ODMException;

/**
 * Class for processing IEEE CDF bus data line string
 * 
 * @author mzhou
 *
 */
public class PSSEBranchDataParser extends BasePSSEDataParser {
	public PSSEBranchDataParser(PsseVersion ver) {
		super(ver);
	}	
	
	@Override public String[] getMetadata() {
		/* Format V26
		 * 
		 * 	I,    J,    CKT, R,      X,        B,     RATEA,RATEB,RATEC,RATIO,ANGLE,GI,BI,GJ,BJ,ST  LEN,O1,F1,...,O4,F4
		 */
		return new String[] {
		   //  0----------1----------2----------3----------4
			  "I",       "J",       "CKT",     "R",       "X",             
		   //  5          6          7          8          9
			  "B",      "RATEA",    "RATEB",   "RATEC",   "RATIO",
		   //  10         11         12         13         14
			  "ANGLE",   "GI",      "BI",      "GJ",      "BJ",
		   //  15         16         17         18         19
			  "ST",       "LEN",     "O1",      "F1",     "O2",
		   //  20         21         22         23         24	  
			  "F2",       "O3",      "F3",      "O4",     "F4"
		};
	}
	
	@Override public void parseFields(final String str) throws ODMException {
  		StringTokenizer st = new StringTokenizer(str, ",");
		/*
		I,J,CKT,R,X,B,RATEA,RATEB,RATEC,GI,BI,GJ,BJ,ST,LEN,O1,F1,...,O4,F4
        */

  		for (int i = 0; i < 15; i++)
  			setValue(i, st.nextToken().trim());

        //O1,F1,...,O4,F4
  		
  		for (int i = 15; i < 23; i++)
  			setValue(i, "0");

		if (st.hasMoreTokens()) {
			setValue(15, st.nextToken().trim());
			setValue(16, st.nextToken().trim());
		}
		if (st.hasMoreTokens()) {
			setValue(17, st.nextToken().trim());
			setValue(18, st.nextToken().trim());
		}
		if (st.hasMoreTokens()) {
			setValue(19, st.nextToken().trim());
			setValue(20, st.nextToken().trim());
		}
		if (st.hasMoreTokens()) {
			setValue(21, st.nextToken().trim());
			setValue(22, st.nextToken().trim());
		}
	}
}