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
import org.ieee.odm.model.base.ModelStringUtil;

/**
 * Class for processing IEEE CDF bus data line string
 * 
 * @author mzhou
 *
 */
public class PSSELineDataParser extends BasePSSEDataParser {
	public PSSELineDataParser(PsseVersion ver) {
		super(ver);
	}	
	
	@Override public String[] getMetadata() {
		/* Format V26
		 * 
		 * 	I, J, CKT, R,X,B, RATEA,RATEB,RATEC,RATIO,ANGLE,GI,BI,GJ,BJ,ST,LEN,O1,F1,...,O4,F4
		 * 
		 * V30
		 *  I, J, CKT, R,X,B, RATEA,RATEB,RATEC,            GI,BI,GJ,BJ,ST,LEN,O1,F1,...,O4,F4
		 */
		return new String[] {
				/*
				 * V26
				 */
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
			  
				/*
				 * V30

		   //  0----------1----------2----------3----------4
			  "I",       "J",       "CKT",     "R",       "X",             
		   //  5          6          7          8          9
			  "B",      "RATEA",    "RATEB",   "RATEC",   "GI",
		   //  10         11         12         13         14
			  "BI",      "GJ",      "BJ",      "ST",       "LEN",
		   //  15         16         17         18         19
			  "O1",      "F1",      "O2",      "F2",       "O3",
		   //  20         21         22         23         24	  
			  "F3",      "O4",     "F4"
				 */
		};
	}
	
	@Override public void parseFields(final String str) throws ODMException {
		this.clearNVPairTableData();
		
  		StringTokenizer st = new StringTokenizer(str, ",");
		/*
		I,J,CKT,R,X,B,RATEA,RATEB,RATEC,GI,BI,GJ,BJ,ST,LEN,O1,F1,...,O4,F4
        */

  		int M = 17, N = 25;  // for V26
  		if (this.verion == PsseVersion.PSSE_30) {
  	  		M = 15; N = 23;
  		}
  		
  		for (int i = 0; i < M; i++) {
  			if (i == 2 && this.verion == PsseVersion.PSSE_30)
  				setValue(i, ModelStringUtil.trimQuote(st.nextToken()).trim());
  			else
  				setValue(i, st.nextToken().trim());
  		}	
  		

        //O1,F1,...,O4,F4
  		
  		for (int i = M; i < N; i++)
  			setValue(i, "0");

		if (st.hasMoreTokens()) {
			setValue(M, st.nextToken().trim());
			setValue(M+1, st.nextToken().trim());
		}
		if (st.hasMoreTokens()) {
			setValue(M+2, st.nextToken().trim());
			setValue(M+3, st.nextToken().trim());
		}
		if (st.hasMoreTokens()) {
			setValue(M+4, st.nextToken().trim());
			setValue(M+5, st.nextToken().trim());
		}
		if (st.hasMoreTokens()) {
			setValue(M+6, st.nextToken().trim());
			setValue(M+7, st.nextToken().trim());
		}
	}
}