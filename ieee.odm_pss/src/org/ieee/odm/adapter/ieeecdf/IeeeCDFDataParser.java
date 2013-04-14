/*
 * @(#)IeeeCDFAdapter.java   
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
 * @Date 02/11/2008
 * 
 *   Revision History
 *   ================
 *
 */

package org.ieee.odm.adapter.ieeecdf;

import java.util.StringTokenizer;

import org.ieee.odm.adapter.InputLineStringParser;
import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.base.ModelStringUtil;

public class IeeeCDFDataParser {
	public static InputLineStringParser getNetDataParser() {
		InputLineStringParser parser = new InputLineStringParser();
		parser.setMetadata(new String[] {"Date", "Originator", "MVA", "Year", "Season", "CaseId"});
		return parser;
	}

	/**
	 * Note: only the MVA Base is mandatory.
	 * @param str
	 * @return
	 */
	public static void parseNetDataFields(final String str, InputLineStringParser parser) {
		if (str.indexOf(',') >= 0) {
			final StringTokenizer st = new StringTokenizer(str, ",");
			int cnt = 0;
			while (st.hasMoreTokens()) {
				parser.setValue(cnt++, st.nextToken().trim());
			}
		} else {
			try {
				//Columns  2- 9   Date, in format DD/MM/YY with leading zeros.  If no date provided, use 0b/0b/0b where b is blank.
				parser.setValue(0, str.substring(1, 9));
				//Columns 11-30   Originator's name [A]
				parser.setValue(1, str.substring(10, 30));
				//Columns 32-37   MVA Base [F] *
				if(str.length()<37){
					parser.setValue(2, str.substring(31, str.length())); // in MVA
				}
				else{
					parser.setValue(2, str.substring(31, 37)); // in MVA
					//Columns 39-42   Year [I]
					parser.setValue(3, ModelStringUtil.getString(str, 38, 42));
					//Column  44      Season (S - Summer, W - Winter)
					parser.setValue(4, ModelStringUtil.getString(str, 43, 44));
					//Column  46-73   Case identification [A]
					parser.setValue(5, ModelStringUtil.getString(str, 46, 73));
				}
				
			} catch (Exception e) {
				ODMLogger.getLogger().severe("Error: Network data line has problem, " + str + "\n" + e.toString());
			}
		}
	}
	
	/**
	 * Note: only the MVA Base is mandatory.
	 * @param str
	 * @return
	 */
	private static String[] getNetDataFields(final String str) {
		final String[] strAry = new String[6];

		if (str.indexOf(',') >= 0) {
			final StringTokenizer st = new StringTokenizer(str, ",");
			int cnt = 0;
			while (st.hasMoreTokens()) {
				strAry[cnt++] = st.nextToken().trim();
			}
		} else {
			try {
				//Columns  2- 9   Date, in format DD/MM/YY with leading zeros.  If no date provided, use 0b/0b/0b where b is blank.
				strAry[0] = str.substring(1, 9);
				//Columns 11-30   Originator's name [A]
				strAry[1] = str.substring(10, 30);
				//Columns 32-37   MVA Base [F] *
				if(str.length()<37){
					strAry[2] = str.substring(31, str.length()); // in MVA
				}
				else{
					strAry[2] = str.substring(31, 37); // in MVA
					//Columns 39-42   Year [I]
					strAry[3] = ModelStringUtil.getString(str, 38, 42);
					//Column  44      Season (S - Summer, W - Winter)
					strAry[4] = ModelStringUtil.getString(str, 43, 44);
					//Column  46-73   Case identification [A]
					strAry[5] = ModelStringUtil.getString(str, 46, 73);
				}
				
			} catch (Exception e) {
				ODMLogger.getLogger().severe("Error: Network data line has problem, " + str + "\n" + e.toString());
			}
		}
		return strAry;
	}

	public static String[] getBusDataFields(final String str) {
		final String[] strAry = new String[18];

		if (str.indexOf(',') >= 0) {
			final StringTokenizer st = new StringTokenizer(str, ",");
			int cnt = 0;
			while (st.hasMoreTokens()) {
				strAry[cnt++] = st.nextToken().trim();
			}
		} else {
			//Columns  1- 4   Bus number [I] *
			strAry[0] = str.substring(0, 4).trim();

			//Columns  6-17   Name [A] (left justify) *
			strAry[1] = str.substring(5, 17).trim();

			//Columns 19-20   Load flow area number [I].  Don't use zero! *
			//Columns 21-23   Loss zone number [I]
			strAry[2] = str.substring(18, 20).trim();
			strAry[3] = str.substring(20, 23).trim();

			//Columns 77-83   Base kV [F]
			strAry[11] = str.substring(76, 83);

			//Columns 25-26   Type [I] *
			//		0 - Unregulated (load, PQ)
			//		1 - Hold MVAR generation within voltage limits, (gen, PQ)
			//		2 - Hold voltage within VAR limits (gen, PV)
			//		3 - Hold voltage and angle (swing, V-Theta; must always have one)
			strAry[4] = str.substring(24, 26).trim();

			//Columns 28-33   Final voltage, p.u. [F] *
			//Columns 34-40   Final angle, degrees [F] *
			strAry[5] = str.substring(27, 33);
			strAry[6] = str.substring(33, 40);

			//Columns 41-49   Load MW [F] *
			//Columns 50-59   Load MVAR [F] *
			strAry[7] = str.substring(40, 49);
			strAry[8] = str.substring(49, 59);

			//Columns 60-67   Generation MW [F] *
			//Columns 68-75   Generation MVAR [F] *
			strAry[9] = str.substring(59, 67);
			strAry[10] = str.substring(67, 75);

			//Columns 107-114 Shunt conductance G (per unit) [F] *
			//Columns 115-122 Shunt susceptance B (per unit) [F] *
			strAry[15] = ModelStringUtil.getString(str,107, 114);
			strAry[16] = ModelStringUtil.getString(str,115, 122);

			//Columns 85-90   Desired volts (pu) [F] (This is desired remote voltage if this bus is controlling another bus.)
			strAry[12] = ModelStringUtil.getString(str,85, 90);

			//Columns 91-98   Minimum MVAR or voltage limit [F]
			//Columns 99-106  Maximum MVAR or voltage limit [F]
			strAry[13] = ModelStringUtil.getString(str,91, 98);
			strAry[14] = ModelStringUtil.getString(str,99, 106);

			//Columns 124-127 Remote controlled bus number
			strAry[17] = ModelStringUtil.getString(str,123, 127).trim();
		}
		return strAry;
	}

	public static String[] getBranchDataFields(final String str) {
		final String[] strAry = new String[21];

		if (str.indexOf(',') >= 0) {
			final StringTokenizer st = new StringTokenizer(str, ",");
			int cnt = 0;
			while (st.hasMoreTokens()) {
				strAry[cnt++] = st.nextToken().trim();
			}
		} else {
			//        	Columns  1- 4   Tap bus number [I] *
			//      	For transformers or phase shifters, the side of the model the non-unity tap is on.
			//			Columns  6- 9   Z bus number [I] *
			//      	For transformers and phase shifters, the side of the model the device impedance is on.
			strAry[0] = str.substring(0, 4).trim();
			strAry[1] = str.substring(5, 9).trim();
			//    		IpssLogger.getLogger().fine("Branch data loaded, from-id, to-id: " + strAry[0] + ", " + strAry[1]);

			//    		Columns 11-12   Load flow area [I]
			//    		Columns 13-15   Loss zone [I]
			//    		Column  17      Circuit [I] * (Use 1 for single lines)
			strAry[2] = str.substring(10, 12).trim();
			strAry[3] = str.substring(12, 15).trim();
			strAry[4] = str.substring(16, 17).trim();

			//    		Column  19      Type [I] *
			strAry[5] = str.substring(18, 19).trim();

			//    		Columns 20-29   Branch resistance R, per unit [F] *
			//    		Columns 30-40   Branch reactance X, per unit [F] * No zero impedance lines
			//    		Columns 41-50   Line charging B, per unit [F] * (total line charging, +B)
			strAry[6] = str.substring(19, 29);
			strAry[7] = str.substring(29, 40);
			strAry[8] = str.substring(40, 50);

			//    		Columns 77-82   Transformer final turns ratio [F]
			//    		Columns 84-90   Transformer (phase shifter) final angle [F]
			strAry[14] = str.substring(76, 82);
			strAry[15] = str.substring(83, 90);

			//    		Columns 51-55   Line MVA rating No 1 [I] Left justify!
			//    		Columns 57-61   Line MVA rating No 2 [I] Left justify!
			//    		Columns 63-67   Line MVA rating No 3 [I] Left justify!
			strAry[9] = str.substring(50, 55).trim();
			strAry[10] = str.substring(56, 61).trim();
			strAry[11] = str.substring(62, 67).trim();

			int type = 0;
			if (!strAry[5].trim().equals(""))
				type = new Integer(strAry[5]).intValue();
			if (type > 1) {
				//    			Columns 69-72   Control bus number
				strAry[12] = str.substring(68, 72).trim();

				//        		Column  74      Side [I]
				strAry[13] = str.substring(73, 74).trim();

				//        		Columns 106-111 Step size [F]
				strAry[18] = str.substring(105, 111);

				//        		Columns 91-97   Maximum tap or phase shift [F]
				//        		Columns 98-104  Minimum tap or phase shift [F]
				strAry[16] = str.substring(90, 97);
				strAry[17] = str.substring(97, 104);

				//        		Columns 113-119 Minimum voltage, MVAR or MW limit [F]
				//        		Columns 120-126 Maximum voltage, MVAR or MW limit [F]
				strAry[19] = str.substring(112, 119);
				strAry[20] = str.substring(119, 126);
			}
		}
		return strAry;
	}

	public static String[] getLossZoneDataFields(final String str) {
		final String[] strAry = new String[2];

		if (str.indexOf(',') >= 0) {
			final StringTokenizer st = new StringTokenizer(str, ",");
			int cnt = 0;
			while (st.hasMoreTokens()) {
				strAry[cnt++] = st.nextToken().trim();
			}
		} else {
			strAry[0] = str.substring(0, 3).trim();
			strAry[1] = str.substring(4).trim();
		}
		return strAry;
	}

	public static String[] getInterchangeDataFields(final String str) {
		final String[] strAry = new String[7];

		if (str.indexOf(',') >= 0) {
			final StringTokenizer st = new StringTokenizer(str, ",");
			int cnt = 0;
			while (st.hasMoreTokens()) {
				strAry[cnt++] = st.nextToken().trim();
			}
		} else {
			//        	Columns  1- 2   Area number [I], no zeros! *
			strAry[0] = str.substring(0, 2).trim();

			//        	Columns  4- 7   Interchange slack bus number [I] *
			//          Columns  9-20   Alternate swing bus name [A]
			strAry[1] = str.substring(3, 7).trim();
			strAry[2] = str.substring(8, 20).trim();

			//          Columns 21-28   Area interchange export, MW [F] (+ = out) *
			//          Columns 30-35   Area interchange tolerance, MW [F] *
			strAry[3] = str.substring(20, 28);
			strAry[4] = str.substring(29, 35);

			//          Columns 38-43   Area code (abbreviated name) [A] *
			//          Columns 46-75   Area name [A]
			strAry[5] = str.substring(37, 43);
			strAry[6] = str.substring(45).trim();
		}
		return strAry;
	}

	public static String[] getTielineDataFields(final String str) {
		final String[] strAry = new String[5];

		if (str.indexOf(',') >= 0) {
			final StringTokenizer st = new StringTokenizer(str, ",");
			int cnt = 0;
			while (st.hasMoreTokens()) {
				strAry[cnt++] = st.nextToken().trim();
			}
		} else {
			//        	Columns  1- 4   Metered bus number [I] *
			//        	Columns  7-8    Metered area number [I] *
			strAry[0] = str.substring(0, 4).trim();
			strAry[1] = str.substring(6, 8).trim();

			//          Columns  11-14  Non-metered bus number [I] *
			//          Columns  17-18  Non-metered area number [I] *
			strAry[2] = str.substring(10, 14).trim();
			strAry[3] = str.substring(16, 18).trim();

			//          Column   21     Circuit number
			strAry[4] = str.substring(20, 21);
		}
		return strAry;
	}
}