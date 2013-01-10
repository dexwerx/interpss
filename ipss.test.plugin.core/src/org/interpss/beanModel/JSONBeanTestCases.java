 /*
  * @(#)CR_UserTestCases.java   
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
  * @Date 02/15/2008
  * 
  *   Revision History
  *   ================
  *
  */

package org.interpss.beanModel;

import static org.junit.Assert.assertTrue;

import org.interpss.CorePluginObjFactory;
import org.interpss.CorePluginTestSetup;
import org.interpss.datamodel.bean.aclf.AclfBranchBean;
import org.interpss.datamodel.bean.aclf.AclfBusBean;
import org.interpss.datamodel.bean.aclf.AclfCaseBean;
import org.interpss.fadapter.IpssFileAdapter;
import org.interpss.mapper.bean.aclf.BeanAclfNetMapper;
import org.interpss.numeric.datatype.Unit.UnitType;
import org.junit.Test;

import com.google.gson.Gson;
import com.interpss.CoreObjectFactory;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.aclf.adpter.AclfSwingBus;
import com.interpss.core.algo.LoadflowAlgorithm;

public class JSONBeanTestCases extends CorePluginTestSetup {
	@Test
	public void testCase1() throws Exception {
		AclfCaseBean netBean = createNetCaseData1();
		
		AclfNetwork aclfNet = new BeanAclfNetMapper()
			.map2Model(netBean)
			.getAclfNet();
		
	  	aclfNet.accept(CoreObjectFactory.createLfAlgoVisitor());
  		//System.out.println(net.net2String());
	  	
  		assertTrue(aclfNet.isLfConverged());	
  		
  		AclfBus swingBus = aclfNet.getBus("5");
		AclfSwingBus swing = swingBus.toSwingBus();
  		//System.out.println(swing.getGenResults(UnitType.PU, net.getBaseKva()));
		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getReal()-2.57943)<0.0001);
		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getImaginary()-2.2994)<0.0001);

	}	
	
	@Test
	public void testCase2() throws Exception {
		AclfCaseBean netBean = createNetCaseData2();
		
		AclfNetwork aclfNet = new BeanAclfNetMapper()
			.map2Model(netBean)
			.getAclfNet();
		
	  	aclfNet.accept(CoreObjectFactory.createLfAlgoVisitor());
  		//System.out.println(net.net2String());
	  	
  		assertTrue(aclfNet.isLfConverged());	
  		
  		AclfBus swingBus = aclfNet.getBus("5");
		AclfSwingBus swing = swingBus.toSwingBus();
  		//System.out.println(swing.getGenResults(UnitType.PU, net.getBaseKva()));
		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getReal()-2.57943)<0.0001);
		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getImaginary()-2.2994)<0.0001);

	}	
	
	private AclfCaseBean createNetCaseData1() {
		String str = "{'name':'Test case','desc':'A Aclf JSon test case','baseKva':100000.0,"+
                     "'busList':["+
                     "	{'id':'1','baseVoltage':13800.0,'area':'1','zone':'1','loadCode':'ConstP','v_msg':1.0,'v_ang':0.0,'p_gen':0.0,'q_gen':0.0,'p_load':1.6,'q_load':0.8},"+
                     "	{'id':'2','baseVoltage':13800.0,'area':'1','zone':'1','loadCode':'ConstP','v_msg':1.0,'v_ang':0.0,'p_gen':0.0,'q_gen':0.0,'p_load':2.0,'q_load':1.0},"+
                     "	{'id':'3','baseVoltage':13800.0,'area':'1','zone':'1','loadCode':'ConstP','v_msg':1.0,'v_ang':0.0,'p_gen':0.0,'q_gen':0.0,'p_load':3.7,'q_load':1.3},"+
                     "	{'id':'4','baseVoltage':1000.0,'area':'1','zone':'1','genCode':'PV','v_msg':1.05,'v_ang':0.0,'p_gen':5.0,'q_gen':0.0,'p_load':0.0,'q_load':0.0},"+
                     "	{'id':'5','baseVoltage':4000.0,'area':'1','zone':'1','genCode':'Swing','v_msg':1.05,'v_ang':0.0,'p_gen':0.0,'q_gen':0.0,'p_load':0.0,'q_load':0.0}"+
                     "   ],"+
                     "'branchList':["+
                     "	{'fromId':'1','toId':'2','cirId':'1','branchCode':'Line','r':0.04,'x':0.25,'b':0.5,'fromTurnRatio':1.0,'toTurnRatio':1.0},"+
                     "	{'fromId':'1','toId':'3','cirId':'1','branchCode':'Line','r':0.1,'x':0.35,'b':0.0,'fromTurnRatio':1.0,'toTurnRatio':1.0},"+
                     "	{'fromId':'2','toId':'3','cirId':'1','branchCode':'Line','r':0.08,'x':0.3,'b':0.5,'fromTurnRatio':1.0,'toTurnRatio':1.0},"+
                     "	{'fromId':'4','toId':'2','cirId':'1','branchCode':'Xfr','r':0.0,'x':0.015,'b':0.0,'fromTurnRatio':1.0,'toTurnRatio':1.05},"+
                     "	{'fromId':'5','toId':'3','cirId':'1','branchCode':'Xfr','r':0.0,'x':0.03,'b':0.0,'fromTurnRatio':1.0,'toTurnRatio':1.05}"+
                     "   ]"+
                     "}";
		return new Gson().fromJson(str, AclfCaseBean.class);
	}
	
	private AclfCaseBean createNetCaseData2() {
		AclfCaseBean caseBean = new AclfCaseBean();
		caseBean.baseKva = 100000.0;
		caseBean.name = "Test case";
		caseBean.desc = "A Aclf JSon test case";
		
		AclfBusBean busBean = new AclfBusBean();
		caseBean.busList.add(busBean);
		// AclfInputUtilFunc.addLoadBusTo( net, "1", 13800, 1, 1, AclfLoadCode.CONST_P, 1.6, 0.8, UnitType.PU );
		busBean.id = "1";
		busBean.baseVoltage = 13800.0;
		busBean.area = "1";
		busBean.zone = "1";
		busBean.loadCode = AclfBusBean.LoadCode.ConstP;
		busBean.p_load = 1.6;
		busBean.q_load = 0.8;
				
		busBean = new AclfBusBean();
		caseBean.busList.add(busBean);
		// AclfInputUtilFunc.addLoadBusTo( net, "2", 13800, 1, 1, AclfLoadCode.CONST_P, 2.0, 1.0, UnitType.PU );
		busBean.id = "2";
		busBean.baseVoltage = 13800.0;
		busBean.area = "1";
		busBean.zone = "1";
		busBean.loadCode = AclfBusBean.LoadCode.ConstP;
		busBean.p_load = 2.0;
		busBean.q_load = 1.0;
		
		busBean = new AclfBusBean();
		caseBean.busList.add(busBean);
		// AclfInputUtilFunc.addLoadBusTo( net, "3", 13800, 2, 1, AclfLoadCode.CONST_P, 3.7, 1.3, UnitType.PU );
		busBean.id = "3";
		busBean.baseVoltage = 13800.0;
		busBean.area = "1";
		busBean.zone = "1";
		busBean.loadCode = AclfBusBean.LoadCode.ConstP;
		busBean.p_load = 3.7;
		busBean.q_load = 1.3;
		
		busBean = new AclfBusBean();
		caseBean.busList.add(busBean);
		// AclfInputUtilFunc.addPVBusTo(   net, "4", 1000,  1, 1, 5.0, UnitType.PU, 1.05, UnitType.PU );
		busBean.id = "4";
		busBean.baseVoltage = 1000.0;
		busBean.area = "1";
		busBean.zone = "1";
		busBean.genCode = AclfBusBean.GenCode.PV;
		busBean.v_msg = 1.05;
		busBean.p_gen = 5.0;
		
		busBean = new AclfBusBean();
		caseBean.busList.add(busBean);
		// AclfInputUtilFunc.addSwingBusTo(net, "5", 4000,  2, 1, 1.05, UnitType.PU, 0.0, UnitType.Deg );	
		busBean.id = "5";
		busBean.baseVoltage = 4000.0;
		busBean.area = "1";
		busBean.zone = "1";
		busBean.genCode = AclfBusBean.GenCode.Swing;
		busBean.v_msg = 1.05;
		busBean.v_ang = 0.0;
		
		

		AclfBranchBean braBean = new AclfBranchBean();
		caseBean.branchList.add(braBean);
		//1   1   2    0.04    0.25    -0.25
		braBean.fromId = "1";
		braBean.toId = "2";
		braBean.branchCode = AclfBranchBean.BranchCode.Line;
		braBean.r = 0.04;
		braBean.x = 0.25;
		braBean.b = 0.5;
		
		braBean = new AclfBranchBean();
		caseBean.branchList.add(braBean);
		//2   1   3    0.1     0.35     0.0
		braBean.fromId = "1";
		braBean.toId = "3";
		braBean.branchCode = AclfBranchBean.BranchCode.Line;
		braBean.r = 0.1;
		braBean.x = 0.35;

		braBean = new AclfBranchBean();
		caseBean.branchList.add(braBean);
		//3   2   3    0.08    0.3     -0.25
		braBean.fromId = "2";
		braBean.toId = "3";
		braBean.branchCode = AclfBranchBean.BranchCode.Line;
		braBean.r = 0.08;
		braBean.x = 0.3;
		braBean.b = 0.5;

		braBean = new AclfBranchBean();
		caseBean.branchList.add(braBean);
		//4   4   2    0.0     0.015    1.05
		braBean.fromId = "4";
		braBean.toId = "2";
		braBean.branchCode = AclfBranchBean.BranchCode.Xfr;
		braBean.x = 0.015;
		braBean.toTurnRatio = 1.05;

		braBean = new AclfBranchBean();
		caseBean.branchList.add(braBean);
		//5   5   3    0.0     0.03     1.05
		braBean.fromId = "5";
		braBean.toId = "3";
		braBean.branchCode = AclfBranchBean.BranchCode.Xfr;
		braBean.x = 0.03;
		braBean.toTurnRatio = 1.05;
	
		System.out.println(new Gson().toJson(caseBean));
		
		return caseBean;
	}
	
}

