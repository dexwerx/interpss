/*
 * @(#)PSSE_ODMOutAdapterTest.java   
 *
 * Copyright (C) 2009 www.interpss.org
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
 * @Author  Stephen Hou
 * @Version 1.0
 * @Date 08/15/2009
 * 
 *   Revision History
 *   ================
 *
 */

package org.ieee.pes.odm.pss.odmout.test.psse;

import static org.junit.Assert.assertTrue;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.junit.Test;

import org.ieee.pes.odm.pss.odmout.adapter.psse.ODM2PSSEConverter;

public class PSSE_ODMOutAdapterTest {
	@Test
	public void testCase1() throws Exception {
		final LogManager logMgr = LogManager.getLogManager();
		Logger logger = Logger.getLogger("ODM->PSS/E Logger");
		logger.setLevel(Level.INFO);
		logMgr.addLogger(logger);

		ODM2PSSEConverter adapter = new ODM2PSSEConverter(logger);
		final String inputFile = "testdata/9bus_transient.xml";
		final String output = "output/";
		adapter.processInputFile(inputFile, output);

		//check system data, base power
		String testFile1 = output + "bus.txt";
		BufferedReader din = readFile(testFile1);
		String str = din.readLine();
		StringTokenizer st;
		st = new StringTokenizer(str, ",");
		st.nextToken();
		String basepower = st.nextToken();
		assertTrue(basepower.equals("100.0"));

		// check bus data, from bus Gen1
		//I, 'NAME', BASKV, IDE, GL, BL, AREA, ZONE, VM, VA, OWNER
		//String str1=findLineByElement(testFile1, "'Gen1'");
		BufferedReader din1 = readFile(testFile1);
		String str1 = findLineByElement(din1, "'Gen1'", 2);
		assertTrue(findToken(str1, 3).equals("16.5"));
		assertTrue(findToken(str1, 4).equals("3"));
		assertTrue(findToken(str1, 5).equals("0"));
		assertTrue(findToken(str1, 6).equals("0"));
		assertTrue(findToken(str1, 7).equals("1"));
		assertTrue(findToken(str1, 8).equals("10"));
		assertTrue(findToken(str1, 9).equals("1.01"));
		assertTrue(findToken(str1, 10).equals("0.0"));
		assertTrue(findToken(str1, 11).equals("1"));

		//check generation data, from bus Gen1
		//I,ID,PG,QG,QT,QB,VS,IREG,MBASE,ZR,ZX,RT,XT,GTAP,STAT,RMPCT,PT,PB,O1,F1,...,O4,F4
		String testFile2 = output + "gen.txt";
		BufferedReader din2 = readFile(testFile2);
		String str2 = findLineByElement(din2, "1", 1);
		assertTrue(findToken(str2, 3).equals("999.0"));
		assertTrue(findToken(str2, 4).equals("0.0"));
		assertTrue(findToken(str2, 5).equals("999.0"));
		assertTrue(findToken(str2, 6).equals("0.0"));
		assertTrue(findToken(str2, 7).equals("1.01"));
		assertTrue(findToken(str2, 8).equals("0"));
		assertTrue(findToken(str2, 9).equals("100.0"));
		assertTrue(findToken(str2, 10).equals("0"));
		assertTrue(findToken(str2, 11).equals("1"));
		assertTrue(findToken(str2, 12).equals("0"));
		assertTrue(findToken(str2, 13).equals("0"));
		assertTrue(findToken(str2, 14).equals("1"));
		assertTrue(findToken(str2, 15).equals("1"));
		assertTrue(findToken(str2, 16).equals("100"));
		assertTrue(findToken(str2, 17).equals("9999"));
		assertTrue(findToken(str2, 18).equals("-9999"));		
		assertTrue(findToken(str2, 19).equals("1"));
		assertTrue(findToken(str2, 20).equals("1.0"));
		assertTrue(findToken(str2, 21).equals("0"));
		assertTrue(findToken(str2, 22).equals("1.0"));
		assertTrue(findToken(str2, 23).equals("0"));
		assertTrue(findToken(str2, 24).equals("0.0"));
		assertTrue(findToken(str2, 25).equals("0"));
		
		//check load data, using data of busA
		//I, ID, STATUS, AREA, ZONE, PL, QL, IP, IQ, YP, YQ, OWNER
		String testFile3 = output + "load.txt";
		BufferedReader din3 = readFile(testFile3);
		String str3 = findLineByElement(din3, "3", 1);
		assertTrue(findToken(str3, 3).equals("1"));
		assertTrue(findToken(str3, 4).equals("1"));
		assertTrue(findToken(str3, 5).equals("10"));
		assertTrue(findToken(str3, 6).equals("125.0"));
		assertTrue(findToken(str3, 7).equals("70.0"));
		assertTrue(findToken(str3, 8).equals("0"));
		assertTrue(findToken(str3, 9).equals("0"));
		assertTrue(findToken(str3, 10).equals("0"));
		assertTrue(findToken(str3, 11).equals("0"));
		
		// check nontramsformer line data, data from branch 2-3
		//I,J,CKT,R,X,B,RATEA,RATEB,RATEC,GI,BI,GJ,BJ,ST,LEN,O1,F1,...,O4,F4
		String testFile4 = output + "branch.txt";
		BufferedReader din4 = readFile(testFile4);
		String str4 = findBranch(din4,"2","3","'1'",1,2,3);			
		assertTrue(findToken(str4, 4).equals("0.0100"));
		assertTrue(findToken(str4, 5).equals("0.0850"));
		assertTrue(findToken(str4, 6).equals("0.0880"));
		assertTrue(findToken(str4, 7).equals("0"));
		assertTrue(findToken(str4, 8).equals("0"));
		assertTrue(findToken(str4, 9).equals("0"));
		assertTrue(findToken(str4, 10).equals("0"));
		assertTrue(findToken(str4, 11).equals("0"));
		assertTrue(findToken(str4, 12).equals("0"));
		assertTrue(findToken(str4, 13).equals("0"));
		assertTrue(findToken(str4, 14).equals("1"));
		assertTrue(findToken(str4, 15).equals("0"));
		assertTrue(findToken(str4, 16).equals("1"));
		
		//check transformer data, from branch 1-2
		//

	}

	String findToken(String str, int i) throws Exception {
		String strFound = "";
		StringTokenizer st;
		st = new StringTokenizer(str, ",");
		String strGot = "";
		int j = 0;
		try {
			while (j < i) {
				if (st.hasMoreTokens()) {
					strGot = st.nextToken().trim();
					j++;
				} else {
					break;
				}

			}
			strFound = strGot;

		} catch (final Exception e) {
			e.printStackTrace();
		}

		return strFound;
	}

	BufferedReader readFile(String inFile) throws Exception {
		InputStream input = new FileInputStream(inFile);
		BufferedReader din = new BufferedReader(new InputStreamReader(input));
		return din;
	}
	
	String findBranch(BufferedReader din, String element1, String element2, 
			String element3,int i, int j, int k)throws Exception{
		String str ="";
		String s1,s2,s3;
		do{
			do{ 
				str = din.readLine();
				s1 = findToken(str, i);
				while (!s1.equals(element1)) {
					str = din.readLine();
					s1 = findToken(str, i);
				}
				s2=findToken(str,j);
			}while(!s2.equals(element2));
			s3=findToken(str,k);
		}while(!s3.equals(element3));
		return str;
	
	}

	String findLineByElement(BufferedReader din, String element, int i)
			throws Exception {
		
		String str = din.readLine();
		String s1 = findToken(str, i);
		while (!s1.equals(element)) {
			str = din.readLine();
			s1 = findToken(str, i);
		}
		return str;
	}
	
	
	
	

}
