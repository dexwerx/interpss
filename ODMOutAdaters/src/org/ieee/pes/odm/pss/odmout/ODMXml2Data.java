/*
 * @(#)ODMXml2Data.java   
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
 * @Author Mike Zhou, Stephen Hou
 * @Version 1.0
 * @Date 08/15/2009
 * 
 *   Revision History
 *   ================
 *
 */

package org.ieee.pes.odm.pss.odmout;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.ieee.pes.odm.pss.odmout.adapter.psse.ODM2PSSEConverter;

public class ODMXml2Data {
	private final static String InOptStr = "-in";
	private final static String FmtOptStr = "-format";
	private final static String OutOptStr = "-out";
	private final static String LogOptStr = "-log";

	private final static String Token_IEEECDF = "ieeecdf";
	private final static String Token_UCTE = "ucte";
	private final static String Token_BPA = "bpa";
	private final static String Token_PSSE = "psse";

	private final static String Token_LogInfo = "info";
	private final static String Token_LogWarn = "waining";
	private final static String Token_LogDebug = "debug";

	private final static String Parm_LoggerName = "IEEE ODM Logger";

	/**
	 * Main entry point to run the adapter
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// parse cmd line parameters
		if (!parseCmdLineParameters(args)) {
			System.out.println(getHelpInfo());
			System.exit(0);
		}

		// create logger object
		Logger logger = getLogger(appParameters.getParamLowerCase(LogOptStr));

		// get input and output file names
		String inputFile = appParameters.getParam(InOptStr);
		String outFile = appParameters.getParam(OutOptStr);

		// print starting info
		printInfo(logger, "Starting");		

		try {
			// load the input file into the reader buffer						
			if (Token_IEEECDF
					.equals(appParameters.getParamLowerCase(FmtOptStr))) {
				logger.info("Output file format is IEEE Common Data Format");
				//adapter = new IeeeCDFAdapter(logger);
			} else if (Token_UCTE.equals(appParameters
					.getParamLowerCase(FmtOptStr))) {
				logger.info("Output file format is UCTE Data Format");
				//adapter = new UCTE_DEFAdapter(logger);
			} else if (Token_BPA.equals(appParameters
					.getParamLowerCase(FmtOptStr))) {
				logger.info("Output file format is BPA Format");
				//adapter = new BPAAdapter(logger);
			} else if (Token_PSSE.equals(appParameters
					.getParamLowerCase(FmtOptStr))) {
				logger.info("Output file format is PSS/E Format");
				ODM2PSSEConverter PSSEAdapter = new ODM2PSSEConverter(logger);
				PSSEAdapter.processInputFile(inputFile, outFile);			

			} else {
				logger.severe("Error: Unsupported output file data, "
						+ appParameters.getParam(FmtOptStr));
				System.err.println("Error: Unsupported output file data, "
						+ appParameters.getParam(FmtOptStr));
				System.exit(0);
			}

			logger.info("target documents saved under directory: " + outFile);
		} catch (Exception e) {
			System.err.println(e.toString());
			e.printStackTrace();
		}

		// print shutdown info
		printInfo(logger, "Shutdown");
		System.exit(0);
	}

	private static AppParameters appParameters;

	private static boolean parseCmdLineParameters(String[] args) {
		appParameters = new AppParameters();
		for (int i = 0; i < args.length; i = i + 2) {
			// simple precaution in case arguments aren't well formed
			if (args[i].indexOf("-") < 0) {
				i = i - 1;
				continue;
			}
			appParameters.setParam(args[i], args[i + 1]);
		}

		if (appParameters.getParam(LogOptStr) == null)
			appParameters.setParam(LogOptStr, Token_LogInfo);

		if (appParameters.getParam(InOptStr) == null
				|| appParameters.getParam(FmtOptStr) == null
				|| appParameters.getParam(OutOptStr) == null) {
			return false;
		}
		return true;
	}

	private static Logger getLogger(String level) {
		final LogManager logMgr = LogManager.getLogManager();
		Logger logger = Logger.getLogger(Parm_LoggerName);
		logger.setLevel(level.equals(Token_LogDebug) ? Level.FINE : (level
				.equals(Token_LogWarn) ? Level.WARNING : Level.INFO));
		logMgr.addLogger(logger);
		return logger;
	}

	private static String getHelpInfo() {
		return "java org.ieee.pes.odm.pss.ODMAdapter -in <input file> -format IEEECDF -out <output file> [-log info|warning|debug]\n"
				+ "  "
				+ FmtOptStr
				+ " IEEECDF for IEEE Common Data Format for exchanging Loadflow data\n";
	}

	private static void printInfo(Logger logger, String str) {
		logger.info("\n============================================\n"
				+ "*           InterPSS " + str + "              *\n"
				+ "============================================");
	}

}

class AppParameters {
	/**
	 * This is where we store every sort of session parameters
	 */
	private Map<String, String> appParameters;

	/**
	 * default constructor
	 */
	public AppParameters() {
		appParameters = new HashMap<String, String>();
	}

	/**
	 * set a kay-value pair
	 * 
	 * @param key
	 * @param value
	 */
	public void setParam(String key, String value) {
		appParameters.put(key, value);
	}

	/**
	 * get value by key
	 * 
	 * @param key
	 * @return
	 */
	public String getParam(String key) {
		Object object = appParameters.get(key);
		if (object != null) {
			return (String) object;
		}
		return null;
	}

	/**
	 * get value by key and turn the value string to lower case
	 * 
	 * @param key
	 * @return
	 */
	public String getParamLowerCase(String key) {
		if (getParam(key) != null)
			return getParam(key).toLowerCase();
		else
			return null;
	}
}
