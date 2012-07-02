/*
 * @(#)InterPSS.java   
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
 * @Date 01/30/2007
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss;

import static com.interpss.spring.CoreCommonSpringFactory.springAppCtx;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.interpss.app.AppParameters;
import org.interpss.cmd.CmdLineRunner;
import org.interpss.editor.EditorConfig;
import org.interpss.editor.GEditor;
import org.interpss.editor.IpssPropertiesLoader;
import org.interpss.grid.gridgain.util.GridEnvHelper;
import org.interpss.numeric.sparse.base.SparseEquation;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.interpss.common.util.IpssLogger;
import com.interpss.common.util.StringUtil;
import com.interpss.pssl.simu.BaseDSL;

public class InterPSS {
	/*
	 * Main method for running InterPSS
	 */
	public static void main(String[] args) {
		// parse cmd line parameters
		if (!parseCmdLineParameters(args)) {
			System.out.println(getHelpInfo());
			System.exit(0);
		}
		
		// if -o help, print help info and exit
		if (OptHelpStr.equals(appParameters.getParamLowerCase(OptStr))) {
			System.out.println(getHelpInfo());
			System.exit(0);
		}

		// load all properties files
		IpssPropertiesLoader.loadProperties(OptCmdLineStr.equals(appParameters
				.getParamLowerCase(OptStr)));
		printInfo("Starting");

		// load String framework configuration
		IpssLogger.getLogger()
			.info("Config Spring context ...");
		/*
		SpringAppContext.springAppContextSetup(IpssPropertiesLoader
				.getIpssString(OptEditorStr.equals(appParameters.getParamLowerCase(OptStr))?
						"springframework.config.editor" : "springframework.config.cmdline"));
		*/
		//CoreCommonSpringCtx.springAppContextSetup();
		if (springAppCtx == null) {
			springAppCtx = new ClassPathXmlApplicationContext(
					new String[] {
							"org/interpss/spring/GEditorSpringCtx.xml"});
		}
		
		// process the -s option
		if (appParameters.getParam(SOptStr) != null
				&& Parm_Native.equals(appParameters.getParamLowerCase(SOptStr))) {
			IpssLogger.getLogger().info("Using native sparse solver");
			IpssCorePlugin.setSparseEqnSolver(SparseEquation.SolverType.Native);
			BaseDSL.sparseSolver = BaseDSL.SparseSolverType.Native;
		}
		else 
			IpssLogger.getLogger().info("Using default Java sparse solver");
		
		// load app constants stored in the properties files
		loadAppConstants();

		// try to start the grid engine
		if (appParameters.getParam(GOptStr) != null
				&& Parm_GridGain.equals(appParameters.getParamLowerCase(GOptStr))) {
			String gridgain_home = IpssPropertiesLoader.getEditorString(Pty_GridGainHome);
			IpssLogger.getLogger().info("Gridgain home " + gridgain_home);
			GridEnvHelper.startDefaultGrid(gridgain_home);
		}

		if (OptEditorStr.equals(appParameters.getParamLowerCase(OptStr))) {
			// 
			// run InterPSS in Editor mode
			//
			IpssLogger.getLogger()
					.info("Start InterPSS in graphic editor mode");

		 	EditorConfig.setConfigConstants();
			if (!EditorConfig.loadEditorProperties()) {
				System.err
						.println("System configuration has problems, please see the log file for details");
				JOptionPane
						.showMessageDialog(
								new JFrame(),
								"Your configuration has problems which prevent the GraphicEditor to start. Please see the log file in the log dir for details",
								"Configuration Error",
								JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}

			GEditor.init(args);
			printInfo("Started ");
		} else if (OptCmdLineStr.equals(appParameters.getParamLowerCase(OptStr))) {
			//
			// run InterPSS in CmdLine mode
			//
			IpssLogger.getLogger().info("run InterPSS in cmd line mode");
			CmdLineRunner.cmdLineRun(
					appParameters.getParamLowerCase(InOptStr),
					appParameters.getParamLowerCase(RunOptStr),
					appParameters.getParamLowerCase(XmlOptStr),
					appParameters.getParamLowerCase(OutOptStr));
			printInfo("Shutdown");
			System.exit(0);
		}
	}

	private static void loadAppConstants() {
		AppConstants.APP_BASE_DIR = StringUtil.getInstallLocation();
		IpssLogger.getLogger().info("Base Dir: " + AppConstants.APP_BASE_DIR);
		AppConstants.OUTPUT_DEFAULT_DIR = IpssPropertiesLoader
				.getIpssString("Output.Default.Location");
	}
	
	private static void printInfo(String str) {  // str has to have 8 chars
		IpssLogger.getLogger().info(
				"\n============================================\n"
				+ "*           InterPSS "+ str +"              *\n"
				+ "============================================");
	}
	private static AppParameters appParameters;

	private static boolean parseCmdLineParameters(String[] args) {
		// we set up the app parameters
		appParameters = new AppParameters();
		for (int i = 0; i < args.length; i = i + 2) {
			// simple precaution in case arguments aren't well formed
			if (args[i].indexOf("-") < 0) {
				i = i - 1;
				continue;
			}
			appParameters.setParam(args[i], args[i + 1]);
		}

		// put default value if not specified by the user
		if (appParameters.getParam(OptStr) == null)
			appParameters.setParam(OptStr, OptCmdLineStr);
		else if (!appParameters.getParamLowerCase(OptStr).equals(OptHelpStr) &&
				 !appParameters.getParamLowerCase(OptStr).equals(OptCmdLineStr) &&
				 !appParameters.getParamLowerCase(OptStr).equals(OptEditorStr)) {
			System.err.println("Wrong -o argument, " + appParameters.getParam(OptStr));
			return false;
		}
		
		if (appParameters.getParam(RunOptStr) != null) {
			if (!appParameters.getParamLowerCase(OptStr).equals(RunDclfStr) &&
				!appParameters.getParamLowerCase(OptStr).equals(RunAclfStr) ) {
				System.err.println("Wrong -run argument, " + appParameters.getParam(RunOptStr));
				return false;
			}
		}
		return true;
	}

	private final static String OptStr = "-o";
	private final static String SOptStr = "-s";
	private final static String GOptStr = "-g";
	private final static String InOptStr = "-in";
	private final static String RunOptStr = "-run";
	private final static String XmlOptStr = "-xml";
	private final static String OutOptStr = "-out";

	private final static String OptHelpStr = "help";
	private final static String OptCmdLineStr = "cmd";
	private final static String OptEditorStr = "editor";

	public final static String RunDclfStr = "dclf";
	public final static String RunAclfStr = "aclf";
	public final static String RunAcscStr = "acsc";
	public final static String RunDStabStr = "dstab";

	private final static String Parm_Native = "native";
	private final static String Parm_GridGain = "gridgain";
	private final static String Pty_GridGainHome = "grid.gridgain.home";

	private static String getHelpInfo() {
		return "java org.interpss.InterPSS [-o editor|help|cmd] [-s native] [-g gridgain] -in inputFile [-run dclf|aclf] [-xml controlFile] [-out outputFile|Console] \n"
				+ "  "	+ OptStr + " " + OptHelpStr + " for help info\n"
				+ "  " 	+ OptStr + " " + OptCmdLineStr + " defaul, running InterPSS in cmd line mode\n"
				+ "  " 	+ OptStr + " " + OptEditorStr + " running InterPSS in graphic editor mode\n"
				+ "  " 	+ SOptStr + " " + Parm_Native + " running InterPSS using native sparse solver\n"
				+ "  " 	+ GOptStr + " " + Parm_GridGain + " running InterPSS in grid computing mode\n"
				+ "  " 	+ InOptStr + " " + " simulation input file, its extension will be used to determine file loading adapter\n"
				+ "  " 	+ RunOptStr + " " + " to override InterPSS default run type, which is determined by the network object type. \n"
				+ "  " 	+ XmlOptStr + " " + " Xml file to control the run. \n"
				+ "  " 	+ OutOptStr + " " + " simulation result output file\n";
	}
}
