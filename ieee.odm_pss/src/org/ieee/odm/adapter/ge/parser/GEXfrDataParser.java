/*
 * @(#)GEAreaDataParser.java   
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

package org.ieee.odm.adapter.ge.parser;

import org.ieee.odm.common.ODMException;

/**
 * Class for processing IEEE CDF bus data line string
 * 
 * @author mzhou
 *
 */
public class GEXfrDataParser extends BaseGEDataParser {
	@Override public String[] getMetadata() {
		/*
		 * V15
		 * 
		 * 		<f bus> <"f name"> <f bkv> <t bus> <"t name"> <t bkv> <"ck"> <"long id">
                <st> <type> <kreg bus> <"kreg name"> <kreg bkv> <zt> <iint bus> <"iint name"> 
                <iint bkv> <tert bus> <"tert name"> <tert bkv> <area> <zone> <tbase> <zpsr> 
                <zpsx> <zptr> <zptx> <ztsr> <ztsx> <vnomp> <vnoms> <vnomt> <anglp> <gmag> 
                <bmag> <r1> <r2> <r3> <r4> <aloss> <tmax> <tmin> <vtmax> <vtmin> <stepp> 
                <tapp> <tapfp> <tapfs> <tapft> <date_in> <date_out> <projid> <stn> 
                <r5> <r6> <r7> <r8> <o1> <p1> <o2> <p2> <o3> <p3> <o4> <p4> <o5> <p5> 
                <o6> <p6> <o7> <p7> <o8> <p8> <ohms> <tbasept> <tbasets> <angls> <anglt> 
                <rs1> <rs2> <rs3> <rt1> <rt2> <rt3> <alosss> <alosst> <rxunits> <gbunits> 
                <tunits> <rcomp> <xcomp>
		 *      
		 */
		return new String[] {
		   //  0----------1----------2----------3----------4
		     "f_bus",  "f_name",   "f_bkv",   "t_bus",   "t_name",       
		   //  5          6          7          8          9
		     "t_bkv",  "ck",       "long_id",  "st",      "type",       
		   //10          11              12           13      14
		     "kreg_bus", "kreg_name",  "kreg_bkv",   "zt", "iint_bus",  
		   //  15         16         17         18         19
		     "iint_name", "iint_bkv", "tert_bus", "tert_name",  "tert_bkv",       
		   //  20         21         22         23         24
		     "area",    "zone",    "tbase",    "zpsr",    "zpsx",       
		   //  25         26         27         28         29
		     "zptr",    "zptx",    "ztsr",     "ztsx",    "vnomp",       
		   //  30         31         32         33         34
		     "vnoms",   "vnomt",   "anglp",    "gmag",    "bmag",       
		   //  35         36         37         38         39
		     "r1",       "r2",       "r3",     "r4",     "aloss",       
		   //  40         41         42         43         44
		     "tmax",     "tmin",    "vtmax",   "vtmin",   "stepp",       
		   //  45         46         47         48         49
		     "tapp",     "tapfp",   "tapfs",   "tapft",   "date_in",       
		   //  50         51         52         53         54
		     "date_out", "projid",  "stn",     "r5",      "r6",       
		   //  55         56         57         58         59
		     "r7",       "r8",      "o1",      "p1",      "o2",       
		   //  60         61         62         63         64
		     "p2",       "o3",      "p3",      "o4",      "p4",       
		   //  65         66         67         68         69
		     "o5",       "p5",      "o6",      "p6",      "o7",       
		   //  70         71         72         73         74
		     "p7",       "o8",      "p8",      "ohms",   "tbasept",       
		   //  75         76         77         78         79
		     "tbasets", "angls",   "anglt",    "rs1",     "rs2",       
		   //  80         81         82         83         84
		     "rs3",       "rt1",    "rt2",     "rt3",    "alosss",       
		   //  85         86         87         88         89
		     "alosst",   "rxunits", "gbunits", "tunits",  "rcomp",       
		   // 90
		     "xcomp"
		};
	}
	
	@Override public void parseFields(final String str) throws ODMException {
	}
}