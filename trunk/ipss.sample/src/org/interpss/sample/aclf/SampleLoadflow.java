 /*
  * @(#)SampleLoadflow.java   
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
  * @Date 09/15/2006
  * 
  *   Revision History
  *   ================
  *
  */

package org.interpss.sample.aclf;

import java.util.logging.Level;

import org.apache.commons.math.complex.Complex;
import org.interpss.display.AclfOutFunc;

import com.interpss.common.datatype.UnitType;
import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.common.util.IpssLogger;
import com.interpss.common.util.PerformanceTimer;
import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBranchCode;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfGenCode;
import com.interpss.core.aclf.AclfLoadCode;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.aclf.adj.AclfAdjNetwork;
import com.interpss.core.aclf.adj.FunctionLoad;
import com.interpss.core.aclf.adpter.LineAdapter;
import com.interpss.core.aclf.adpter.LoadBusAdapter;
import com.interpss.core.aclf.adpter.SwingBusAdapter;
import com.interpss.core.algorithm.LoadflowAlgorithm;
import com.interpss.pssl.simu.IpssAclf;


public class SampleLoadflow {

	public static void set2BusNetworkData(AclfNetwork net, IPSSMsgHub msg) {
		IpssAclf.addAclfBus("Bus1", "Bus 1", net)
				.setBaseVoltage(4000.0)
				.setGenCode(AclfGenCode.SWING)
				.setVoltageSpec(1.0, UnitType.PU, 0.0, UnitType.Deg)
				.setLoadCode(AclfLoadCode.NON_LOAD);
  		
		IpssAclf.addAclfBus("Bus2", "Bus 2", net)
  				.setBaseVoltage(4000.0)
  				.setGenCode(AclfGenCode.NON_GEN)
  				.setLoadCode(AclfLoadCode.CONST_P)
  				.setLoad(new Complex(1.0, 0.8), UnitType.PU);
  		
		IpssAclf.addAclfBranch("Bus1", "Bus2", "Branch 1", net)
				.setBranchCode(AclfBranchCode.LINE)
				.setZ(new Complex(0.05, 0.1), UnitType.PU);
	}	
	
	public static void simpleLoadflow(IPSSMsgHub msg) {
		// Create an AclfNetwork object
		AclfNetwork net = IpssAclf.createAclfNetwork("Net")
				.setBaseKva(100000.0)
				.getAclfNet();

		// set the network data
	  	set2BusNetworkData(net, msg);
	  	
	  	// create the default loadflow algorithm
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net, msg);

	  	// use the loadflow algorithm to perform loadflow calculation
	  	PerformanceTimer timer = new PerformanceTimer();
	  	timer.start();
	  	algo.loadflow();
	  	timer.logStd("Duration for loadflow: ");
	  	
	  	// output loadflow calculation results
	  	System.out.println(AclfOutFunc.loadFlowSummary(net));
    }	

	public static void loadflowWithAdjustment(IPSSMsgHub msg) {
		// Create an AclfAdjNetwork object
		AclfAdjNetwork net = IpssAclf.createAclfNetwork("Net")
				.setBaseKva(100000.0)
				.getAclfNet();

		// set the network data
	  	set2BusNetworkData(net, msg);
	  	
	  	//	  define a function load object, 
	  	//	  p = p(0)*(a + b*v + (1.0-a-b)*v*v)
	  	//	  q = q(0)*(a + b*v + (1.0-a-b)*v*v)
  		FunctionLoad fload = CoreObjectFactory.createFunctionLoad(net, "Bus2");
  		fload.getP().setA(0.3);
  		fload.getP().setB(0.5);
  		fload.getQ().setA(0.1);
  		fload.getQ().setB(0.6);
	  	
	  	// create the default loadflow algorithm
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net, msg);

	  	// use the loadflow algorithm to perform loadflow calculation
	  	algo.loadflow();
	  	
	  	// output loadflow calculation results
	  	System.out.println(AclfOutFunc.loadFlowSummary(net));

	  	// output net object info for debug purpose 
	  	System.out.println(net.net2String());
    }
	
	public static void main(String args[]) {
		// set session message to Warning level
		IPSSMsgHub msg = IpssAclf.getMsgHub();
		
		IpssLogger.getLogger().setLevel(Level.WARNING);
		
		simpleLoadflow(msg);

		loadflowWithAdjustment(msg);
	}	

	public static void simpleLoadflow1(IPSSMsgHub msg) {
		// Create an AclfNetwork object
		AclfNetwork net = CoreObjectFactory.createAclfNetwork();

		double baseKva = 100000.0;
		
		// set system basekva for loadflow calculation
		net.setBaseKva(baseKva);
	  	
		// create a AclfBus object
  		AclfBus bus1 = CoreObjectFactory.createAclfBus("Bus1");
  		// set bus name and description attributes
  		bus1.setAttributes("Bus 1", "");
  		// set bus base voltage 
  		bus1.setBaseVoltage(4000.0);
  		// set bus to be a swing bus
  		bus1.setGenCode(AclfGenCode.SWING);
  		// adapt the bus object to a swing bus object
  		SwingBusAdapter swingBus = (SwingBusAdapter)bus1.getAdapter(SwingBusAdapter.class);
  		// set swing bus attributes
  		swingBus.setVoltMag(1.0, UnitType.PU);
  		swingBus.setVoltAng(0.0, UnitType.Deg);
  		// add the bus into the network
  		net.addBus(bus1);
  		
  		AclfBus bus2 = CoreObjectFactory.createAclfBus("Bus2");
  		bus2.setAttributes("Bus 2", "");
  		bus2.setBaseVoltage(4000.0);
  		// set the bus to a non-generator bus
  		bus2.setGenCode(AclfGenCode.NON_GEN);
  		// set the bus to a constant power load bus
  		bus2.setLoadCode(AclfLoadCode.CONST_P);
  		// adapt the bus object to a Load bus object
  		LoadBusAdapter loadBus = (LoadBusAdapter)bus2.getAdapter(LoadBusAdapter.class);
  		// set load to the bus
  		loadBus.setLoad(new Complex(1.0, 0.8), UnitType.PU, baseKva);
  		net.addBus(bus2);
  		
  		// create an AclfBranch object
  		AclfBranch branch = CoreObjectFactory.createAclfBranch();
  		// set branch name, description and circuit number
  		branch.setAttributes("Branch 1", "", "1");
  		// set branch to a Line branch
  		branch.setBranchCode(AclfBranchCode.LINE);
  		// adapte the branch object to a line branch object
		LineAdapter lineBranch = (LineAdapter)branch.getAdapter(LineAdapter.class);
		// set branch parameters
  		lineBranch.setZ(new Complex(0.05, 0.1), UnitType.PU, 4000.0, baseKva, msg);
  		// add the branch from Bus1 to Bus2
  		net.addBranch(branch, "Bus1", "Bus2");
	  	
	  	// create the default loadflow algorithm
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net, msg);

	  	// use the loadflow algorithm to perform loadflow calculation
	  	algo.loadflow();
	  	
	  	// output loadflow calculation results
	  	System.out.println(AclfOutFunc.loadFlowSummary(net));
    }	
}
