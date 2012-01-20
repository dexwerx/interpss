 /*
  * @(#)RunUtilFunc.java   
  *
  * Copyright (C) 2006-2007 www.interpss.org
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
  * @Date 12/15/2007
  * 
  *   Revision History
  *   ================
  *
  */


package org.interpss.editor.ui;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

import org.ieee.odm.model.ODMModelParser;
import org.ieee.odm.schema.BranchRefXmlType;
import org.ieee.odm.schema.PTradingEDHourlyAnalysisXmlType;
import org.interpss.editor.SimuRunEnum;
import org.interpss.editor.jgraph.GraphSpringFactory;
import org.interpss.xml.IpssXmlParser;
import org.interpss.xml.schema.InterPSSXmlType;

import com.interpss.common.exp.InterpssException;
import com.interpss.common.util.IpssLogger;
import com.interpss.common.util.NetUtilFunc;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.aclf.flow.FlowInterface;
import com.interpss.core.net.Area;
import com.interpss.core.net.Branch;
import com.interpss.core.net.Bus;
import com.interpss.pssl.algo.aclf.EDHourlyLoadflow;

public class RunUIUtilFunc  {
	public static String Template_RunCase = "template/RunCaseTemplate.xml";
//	public static String Template_RunCase_Aclf = "template/RunCaseAclfTemplate.xml";
	public static String Template_RunCase_Acsc = "template/RunCaseAcscTemplate.xml";
	public static String Template_RunCase_DStab = "template/RunCaseDStabTemplate.xml";
//	public static String Template_RunCase_SenAnalysis = "template/RunCaseSenAnalysisTemplate.xml";	
//	public static String Template_RunCase_PTAnalysis = "template/RunCasePTAnalysisTemplate.xml";	
	
	public static enum NetIdType {
			LoadBus, GenBus, AllBus, 
			LineBranch, XfrBranch, AllBranch,
			AreaNo, BusInArea, GenInArea, 
			GenInAreaDFactor,
			FlowInterface}
	/**
	 * 
	 * 
	 * @param net
	 * @param type
	 * @param number areNo for BusinArea
	 * @return
	 */
	public static Set<String> getIdArray(AclfNetwork net, NetIdType type, int number) {
		Set<String> set = new TreeSet<String>();
		if (type == NetIdType.AllBus || type == NetIdType.GenBus || type == NetIdType.LoadBus)
			for (Bus bus : net.getBusList()) {
				AclfBus aclfBus = (AclfBus)bus;
				if (type == NetIdType.GenBus) {
					if (aclfBus.isGen()) 
						set.add(bus.getId());
				}	
				else if (type == NetIdType.LoadBus) {
					if (aclfBus.isLoad())
						set.add(bus.getId());
				}
				else
					set.add(bus.getId());
			}
		else if (type == NetIdType.AllBranch || type == NetIdType.LineBranch || type == NetIdType.XfrBranch)
			for (Branch bra : net.getBranchList()) {
				AclfBranch aclfBra = (AclfBranch)bra;
				if (type == NetIdType.LineBranch) {
					if (aclfBra.isLine()) 
						set.add(bra.getId());
				}
				else if (type == NetIdType.XfrBranch) {
					if (aclfBra.isXfr())
						set.add(bra.getId());
				}
				else
					set.add(bra.getId());
			}
		else if (type == NetIdType.AreaNo) {
			for ( Area area : net.getAreaList())
				set.add(new Long(area.getNumber()).toString());
		}
		else if (type == NetIdType.BusInArea) {
			for (Bus bus : net.getBusList()) {
				if (bus.getArea().getNumber() == number)
					set.add(bus.getId());
			}
		}
		else if (type == NetIdType.GenInArea) {
			for (Bus bus : net.getBusList()) {
				AclfBus aclfBus = (AclfBus)bus;
				if (bus.getArea().getNumber() == number && aclfBus.isGen())
					set.add(bus.getId());
			}
		}
		else if (type == NetIdType.GenInAreaDFactor) {
			double sum = 0.0;
			for (Bus bus : net.getBusList()) {
				AclfBus aclfBus = (AclfBus)bus;
				if (bus.getArea().getNumber() == number && aclfBus.isGen() && !aclfBus.isSwing())
					sum += aclfBus.getGenP();
			}
			if (sum == 0.0) 
				sum = 1.0;
			for (Bus bus : net.getBusList()) {
				AclfBus aclfBus = (AclfBus)bus;
				if (bus.getArea().getNumber() == number && aclfBus.isGen() && !aclfBus.isSwing()) {
					double p = aclfBus.getGenP() / sum;
					set.add(bus.getId()+"(" + String.format("%3.1f", p*100.0) + "%)");
				}
			}
		}
		else if (type == NetIdType.FlowInterface) {
			if (net.isFlowInterfaceLoaded()) {
				for ( FlowInterface inf :  net.getFlowInterfaceList()) {
					set.add(inf.getId());
				}
			}
		}
		return set;
	}
	
	public static Set<String> getIdArray(AclfNetwork net, NetIdType type) {
		return getIdArray(net, type, 0);
	}
	
	/**
	 * get percent from string of id(30.0%) format
	 * 
	 * @param s
	 * @return
	 */
	public static double getPercent_IdPercent(String s) {
		String s1 = s.substring(s.indexOf('(')+1, s.indexOf('%'));
		return new Double(s1).doubleValue();
	}
	
	/**
	 * get id from string of id(30.0%) format
	 * 
	 * @param s
	 * @return
	 */
	public static String getId_IdPercent(String s) {
		String s1 = s.substring(0, s.indexOf('('));
		return s1;
	}
	
	public static void addItemJList(javax.swing.JList jlist, String item) {
		int size = jlist.getModel().getSize();
		String[] ary = new String[size + 1];
		for (int i = 0; i < size; i++) {
			ary[i] = (String)jlist.getModel().getElementAt(i);
		}
		ary[size] = item;   
		jlist.setModel(new javax.swing.DefaultComboBoxModel(ary));    	
	}

	public static void removeItemJList(javax.swing.JList jlist) {
		String id = (String)jlist.getSelectedValue();
		if (id != null) {
			int size = jlist.getModel().getSize();
			if (size == 0)
				return;
			String[] ary = new String[size - 1];
			int cnt = 0;
			for (int i = 0; i < size; i++) {
				String s = (String)jlist.getModel().getElementAt(i);
				if (s.contains(id))
					; // skip the item
				else
					ary[cnt++] = (String)jlist.getModel().getElementAt(i);
			}
			jlist.setModel(new javax.swing.DefaultComboBoxModel(ary));    	
		}
	}
	
	public static String[] getJListItemAry(javax.swing.JList jlist) {
    	int size = jlist.getModel().getSize();
    	String[] ary = new String[size];
    	for (int i = 0; i < size; i++) {
    		ary[i] = (String)jlist.getModel().getElementAt(i);
    	}
		return ary;
	}
	
	public static void setBranchIdInfo(BranchRefXmlType branch, String braId) {
		String fromId = NetUtilFunc.findFromID(braId);
		String toId = NetUtilFunc.findToID(braId);
		String cirId = NetUtilFunc.findCirNo(braId);
		branch.setBranchId(braId);
		branch.setFromBusId(fromId);
		branch.setToBusId(toId);
		branch.setCircuitId(cirId);
	}
	
	public static ODMModelParser loadODMXmlDoc(String filename, SimuRunEnum caseType) throws Exception {
		ODMModelParser parser = new ODMModelParser();
		try {
  			File xmlFile = new File(filename);
  			if (xmlFile.exists())
  				if (parser.parse(xmlFile))
  					return parser;
  		} catch (Exception e) {
  			IpssLogger.getLogger().info("This might be caused by first time loading the file, " + e.toString());
  		}		

  		// use template file
		if (caseType == SimuRunEnum.TradingAnalysis ||
				caseType == SimuRunEnum.SenAnalysis ||
				caseType == SimuRunEnum.DStab ||
				caseType == SimuRunEnum.Acsc ||
				caseType == SimuRunEnum.Aclf) {
			filename = Template_RunCase;
		}
  		
  		String wdir = GraphSpringFactory.getIpssGraphicEditor().getWorkspace();
		filename = wdir+System.getProperty("file.separator")+filename;
		IpssLogger.getLogger().info("Loading template file: " + filename);
		File xmlFile = new File(filename);
		parser.parse(xmlFile);
		return parser;
	}
	
	/**
	 * Load IpssXmlDoc from the file. If file not existing, load the info from the template.
	 * 
	 * @param filename
	 * @param caseType
	 * @return
	 * @throws Exception
	 */
	public static InterPSSXmlType loadIpssXmlDoc(String filename, SimuRunEnum caseType) throws Exception {
  		IpssXmlParser parser;
  		try {
  			File xmlFile = new File(filename);
  			if (xmlFile.exists()) {
  				parser = new IpssXmlParser(xmlFile);
  				return parser.getRootDoc();
  			}
  		} catch (Exception e) {
  			IpssLogger.getLogger().info("This might be caused by first time loading the file, " + e.toString());
  		}
  		
  		String wdir = GraphSpringFactory.getIpssGraphicEditor().getWorkspace();
		filename = wdir+System.getProperty("file.separator")+filename;
		File xmlFile = new File(filename);
		parser = new IpssXmlParser(xmlFile);
		return parser.getRootDoc();
	}	
	
	public static boolean loadFlowInterfaceFiles(AclfNetwork net, PTradingEDHourlyAnalysisXmlType ptXml) {
		// load FlowInterface if necessary
		if (!net.isFlowInterfaceLoaded()) {
			if (ptXml.getCaseData() != null) {
				String f1 = ptXml.getCaseData().getInterfaceFile().getInterfaceFilename();
				String f2 = ptXml.getCaseData().getInterfaceFile().getLimitFilename();
				if (new File(f1).exists() && new File(f2).exists()) {
					IpssLogger.getLogger().info("Load interface file: " + f1 + ", " + f2);
					EDHourlyLoadflow hrLoadflow = new EDHourlyLoadflow(net);
					hrLoadflow.setInterfaceFilename(f1);
					hrLoadflow.setFlowInterfaceLimitFilename(f2);
					try {
						hrLoadflow.loadFlowInterface();
					} catch (InterpssException e) {
						IpssLogger.getLogger().severe(e.toString());
						return false;
					}
				}
			}
		}
		return true;
	}
}
