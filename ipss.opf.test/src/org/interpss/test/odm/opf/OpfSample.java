 /*
  * @(#)IEEECDF_ODMTest.java   
  *
  * Copyright (C) 2008 www.interpss.org
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
  * @Date 02/01/2008
  * 
  *   Revision History
  *   ================
  *
  */

package org.interpss.test.odm.opf;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;

import org.ieee.odm.ODMObjectFactory;
import org.ieee.odm.model.opf.OpfModelParser;
import org.interpss.mapper.odm.ODMOpfDataMapper;
import org.interpss.opf.dc.DCOPFSolver;
import org.interpss.opf.dc.impl.QuadProgDCOPFSolverImpl;
import org.interpss.opf.dc.util.OpfOutFunc;
import org.interpss.test.OpfTestSetup;
import org.junit.Test;

import com.interpss.SimuObjectFactory;
import com.interpss.opf.dclf.DclfOpfNetwork;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;

public class OpfSample  extends OpfTestSetup { 
	@Test
	public void test3BusCase() throws Exception {
		File file = new File("testdata/opf/opf_3bus_test.xml");
		OpfModelParser parser = ODMObjectFactory.createOpfModelParser();
		if (parser.parse(new FileInputStream(file))) {
			//System.out.println(parser.toXmlDoc(false));
			
			SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.OPF_NET);
			if (!new ODMOpfDataMapper()
						.map2Model(parser, simuCtx)) {
	  	  		System.out.println("Error: ODM model to InterPSS SimuCtx mapping error, please contact support@interpss.com");
	  	  		return;
			}	
			
			DclfOpfNetwork opfNet = (DclfOpfNetwork)simuCtx.getOpfNet();
//			System.out.println(opfNet.net2String());

			QuadProgDCOPFSolverImpl solver=new QuadProgDCOPFSolverImpl();
			solver.solveDCOPF(opfNet);
			
			System.out.println(OpfOutFunc.opfResultSummary(opfNet));
//	        double baseMVA=opfNet.getBaseKva()/1000.0;
//			for(int i=0;i<solver.getEqMultipliers().length;i++){
////				System.out.println(solver.getEqMultipliers()[i]);
//				System.out.println("The LMP of BUS#"+opfNet.getBusList().get(i).getId()+" is :"+solver.getEqMultipliers()[i]/baseMVA);
//			}
		
/*          
			Minimun Total Variable Cost: 26.215
			Minimun Total Cost: 506.106
 */
		  	assertTrue(Math.abs(opfNet.getMinTotalVariableCost() - 26.215) < 0.01);			
		  	assertTrue(Math.abs(opfNet.getTotalFixedCost() - (506.106-26.215)) < 0.01);			
		}
	}
	@Test
	public void testIEEE30BusCase() throws Exception {
		/*
		 * Note:
		 * 1.Original Data source: Modified IEEE 30-Bus System, p.477-478
		 * Shahidehpour et al. 2002, "Market Operations in Electric Power Systems"
		 * 
		 * 2.the load data keep unchanged as the IEEE 30 Bus, 
		 * the same as LP-17 load in the IEEE30FixedDemand case of DC-OPFJ,
		 * for detailed,refer to http://www.econ.iastate.edu/tesfatsi/DCOPFJHome.htm
		 * 
		 */
		File file = new File("testdata/opf/ieee30ODM.xml");
		OpfModelParser parser = ODMObjectFactory.createOpfModelParser();
		if (parser.parse(new FileInputStream(file))) {
			//System.out.println(parser.toXmlDoc(false));
			
			SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.OPF_NET);
			if (!new ODMOpfDataMapper()
						.map2Model(parser, simuCtx)) {
	  	  		System.out.println("Error: ODM model to InterPSS SimuCtx mapping error, please contact support@interpss.com");
	  	  		return;
			}	
			
			DclfOpfNetwork opfNet = (DclfOpfNetwork)simuCtx.getOpfNet();
//			System.out.println(opfNet.net2String());
			
			DCOPFSolver solver=new QuadProgDCOPFSolverImpl();
			solver.solveDCOPF(opfNet);
//			
			System.out.println(OpfOutFunc.opfResultSummary(opfNet));
			
//			for(int i=0;i<solver.getEqMultipliers().length;i++){
//				System.out.println(solver.getEqMultipliers()[i]);
//			}
//			for(Bus b : opfNet.getBusList()){
//				System.out.println("The LMP of "+b.getId()+" is :"
//						+ "(" + ((OpfBus)b).getLMP() + ")");
//			}
			
//			System.out.println("----------a line ,the following are inequation multipiers----");
//			for(int i=0;i<solver.getIneqMultipiers().length;i++){
//				System.out.println(solver.getIneqMultipiers()[i]);
//			}
/*
			Minimun Total Variable Cost: 49.173
			Minimun Total Cost: 1524.816
 */
		  	assertTrue(Math.abs(opfNet.getMinTotalVariableCost() - 49.173) < 0.001);			
		  	assertTrue(Math.abs(opfNet.getTotalFixedCost() - (1524.816-49.173)) < 0.001);			
		}
	}
}

