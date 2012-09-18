package org.ieee.odm.adapter.psse.v30.impl;

import java.util.StringTokenizer;
import java.util.logging.Logger;

import org.ieee.odm.adapter.psse.PsseVersion;
import org.ieee.odm.model.aclf.AclfModelParser;

public class PSSEV30MultiSecDataRec {
	/*
		I, J, ID, DUM1, DUM2, ... DUM9

	I - "From bus" number, or extended bus name enclosed in single quotes.
	J - "To bus" number, or extended bus name enclosed in single quotes). 
		J is entered as a negative number or with a minus sign before the
		first character of the extended bus name to designate it as the metered end; otherwise,
		bus I is assumed to be the metered end.
	ID - Two-character upper case alphanumeric multisection line grouping identifier. The
		first character must be an ampersand ("&"). ID = ’&1’ by default.
	DUMi Bus numbers, or extended bus names enclosed in single quotes,
		of the "dummy buses" connected by the branches that comprise this multisection
		line grouping.	 
*/
	public static void procMultiSecString(String lineStr, PsseVersion version, AclfModelParser parser, Logger logger) {
		procMultiSecFields(lineStr, version, logger);
	}
	
	private static void procMultiSecFields(String lineStr, PsseVersion version, Logger logger) {
		StringTokenizer st;
	}	
}
