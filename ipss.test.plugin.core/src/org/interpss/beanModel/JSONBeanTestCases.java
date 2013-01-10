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

import org.interpss.CorePluginTestSetup;
import org.interpss.datamodel.bean.aclf.AclfBranchBean;
import org.interpss.datamodel.bean.aclf.AclfBusBean;
import org.interpss.datamodel.bean.aclf.AclfNetBean;
import org.interpss.mapper.bean.aclf.AclfNetBeanMapper;
import org.interpss.numeric.datatype.Unit.UnitType;
import org.junit.Test;

import com.google.gson.Gson;
import com.interpss.CoreObjectFactory;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.aclf.adpter.AclfSwingBus;

public class JSONBeanTestCases extends CorePluginTestSetup {
	@Test
	public void testCase1() throws Exception {
		AclfNetBean netBean = createNetCaseData1();
		
		AclfNetwork aclfNet = new AclfNetBeanMapper()
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
		AclfNetBean netBean = createNetCaseData2();
		
		AclfNetwork aclfNet = new AclfNetBeanMapper()
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
	
	private AclfNetBean createNetCaseData1() {
		String str = "{'base_kva':100000.0," +
				"'bus_list':[" +
				"	{'base_v':13800.0,'area':'1','zone':'1','load_code':'ConstP','v_msg':1.0,'v_ang':0.0,'p_gen':0.0,'q_gen':0.0,'p_load':1.6,'q_load':0.8,'id':'1'}," +
				"	{'base_v':13800.0,'area':'1','zone':'1','load_code':'ConstP','v_msg':1.0,'v_ang':0.0,'p_gen':0.0,'q_gen':0.0,'p_load':2.0,'q_load':1.0,'id':'2'}," +
				"	{'base_v':13800.0,'area':'1','zone':'1','load_code':'ConstP','v_msg':1.0,'v_ang':0.0,'p_gen':0.0,'q_gen':0.0,'p_load':3.7,'q_load':1.3,'id':'3'}," +
				"	{'base_v':1000.0,'area':'1','zone':'1','gen_code':'PV','v_msg':1.05,'v_ang':0.0,'p_gen':5.0,'q_gen':0.0,'p_load':0.0,'q_load':0.0,'id':'4'}," +
				"	{'base_v':4000.0,'area':'1','zone':'1','gen_code':'Swing','v_msg':1.05,'v_ang':0.0,'p_gen':0.0,'q_gen':0.0,'p_load':0.0,'q_load':0.0,'id':'5'}" +
				"]," +
				"'branch_list':[" +
				"	{'f_id':'1','t_id':'2','cir_id':'1','code':'Line','r':0.04,'x':0.25,'b':0.5,'f_ratio':1.0,'t_ratio':1.0,'f_ang':0.0,'t_ang':0.0}," +
				"	{'f_id':'1','t_id':'3','cir_id':'1','code':'Line','r':0.1,'x':0.35,'b':0.0,'f_ratio':1.0,'t_ratio':1.0,'f_ang':0.0,'t_ang':0.0}," +
				"	{'f_id':'2','t_id':'3','cir_id':'1','code':'Line','r':0.08,'x':0.3,'b':0.5,'f_ratio':1.0,'t_ratio':1.0,'f_ang':0.0,'t_ang':0.0}," +
				"	{'f_id':'4','t_id':'2','cir_id':'1','code':'Xfr','r':0.0,'x':0.015,'b':0.0,'f_ratio':1.0,'t_ratio':1.05,'f_ang':0.0,'t_ang':0.0}," +
				"	{'f_id':'5','t_id':'3','cir_id':'1','code':'Xfr','r':0.0,'x':0.03,'b':0.0,'f_ratio':1.0,'t_ratio':1.05,'f_ang':0.0,'t_ang':0.0}" +
				"]," +
				"'name':'Test case','desc':'A Aclf JSon test case'}";
		return new Gson().fromJson(str, AclfNetBean.class);
	}
	
	private AclfNetBean createNetCaseData2() {
		AclfNetBean netBean = new AclfNetBean();
		netBean.base_kva = 100000.0;
		netBean.name = "Test case";
		netBean.desc = "A Aclf JSon test case";
		
		AclfBusBean busBean = new AclfBusBean();
		netBean.bus_list.add(busBean);
		// AclfInputUtilFunc.addLoadBusTo( net, "1", 13800, 1, 1, AclfLoadCode.CONST_P, 1.6, 0.8, UnitType.PU );
		busBean.id = "1";
		busBean.base_v = 13800.0;
		busBean.area = "1";
		busBean.zone = "1";
		busBean.load_code = AclfBusBean.LoadCode.ConstP;
		busBean.p_load = 1.6;
		busBean.q_load = 0.8;
				
		busBean = new AclfBusBean();
		netBean.bus_list.add(busBean);
		// AclfInputUtilFunc.addLoadBusTo( net, "2", 13800, 1, 1, AclfLoadCode.CONST_P, 2.0, 1.0, UnitType.PU );
		busBean.id = "2";
		busBean.base_v = 13800.0;
		busBean.area = "1";
		busBean.zone = "1";
		busBean.load_code = AclfBusBean.LoadCode.ConstP;
		busBean.p_load = 2.0;
		busBean.q_load = 1.0;
		
		busBean = new AclfBusBean();
		netBean.bus_list.add(busBean);
		// AclfInputUtilFunc.addLoadBusTo( net, "3", 13800, 2, 1, AclfLoadCode.CONST_P, 3.7, 1.3, UnitType.PU );
		busBean.id = "3";
		busBean.base_v = 13800.0;
		busBean.area = "1";
		busBean.zone = "1";
		busBean.load_code = AclfBusBean.LoadCode.ConstP;
		busBean.p_load = 3.7;
		busBean.q_load = 1.3;
		
		busBean = new AclfBusBean();
		netBean.bus_list.add(busBean);
		// AclfInputUtilFunc.addPVBusTo(   net, "4", 1000,  1, 1, 5.0, UnitType.PU, 1.05, UnitType.PU );
		busBean.id = "4";
		busBean.base_v = 1000.0;
		busBean.area = "1";
		busBean.zone = "1";
		busBean.gen_code = AclfBusBean.GenCode.PV;
		busBean.v_msg = 1.05;
		busBean.p_gen = 5.0;
		
		busBean = new AclfBusBean();
		netBean.bus_list.add(busBean);
		// AclfInputUtilFunc.addSwingBusTo(net, "5", 4000,  2, 1, 1.05, UnitType.PU, 0.0, UnitType.Deg );	
		busBean.id = "5";
		busBean.base_v = 4000.0;
		busBean.area = "1";
		busBean.zone = "1";
		busBean.gen_code = AclfBusBean.GenCode.Swing;
		busBean.v_msg = 1.05;
		busBean.v_ang = 0.0;
		
		

		AclfBranchBean braBean = new AclfBranchBean();
		netBean.branch_list.add(braBean);
		//1   1   2    0.04    0.25    -0.25
		braBean.f_id = "1";
		braBean.t_id = "2";
		braBean.code = AclfBranchBean.BranchCode.Line;
		braBean.r = 0.04;
		braBean.x = 0.25;
		braBean.b = 0.5;
		
		braBean = new AclfBranchBean();
		netBean.branch_list.add(braBean);
		//2   1   3    0.1     0.35     0.0
		braBean.f_id = "1";
		braBean.t_id = "3";
		braBean.code = AclfBranchBean.BranchCode.Line;
		braBean.r = 0.1;
		braBean.x = 0.35;

		braBean = new AclfBranchBean();
		netBean.branch_list.add(braBean);
		//3   2   3    0.08    0.3     -0.25
		braBean.f_id = "2";
		braBean.t_id = "3";
		braBean.code = AclfBranchBean.BranchCode.Line;
		braBean.r = 0.08;
		braBean.x = 0.3;
		braBean.b = 0.5;

		braBean = new AclfBranchBean();
		netBean.branch_list.add(braBean);
		//4   4   2    0.0     0.015    1.05
		braBean.f_id = "4";
		braBean.t_id = "2";
		braBean.code = AclfBranchBean.BranchCode.Xfr;
		braBean.x = 0.015;
		braBean.t_ratio = 1.05;

		braBean = new AclfBranchBean();
		netBean.branch_list.add(braBean);
		//5   5   3    0.0     0.03     1.05
		braBean.f_id = "5";
		braBean.t_id = "3";
		braBean.code = AclfBranchBean.BranchCode.Xfr;
		braBean.x = 0.03;
		braBean.t_ratio = 1.05;
	
		//System.out.println(new Gson().toJson(netBean));
		
		return netBean;
	}
	
}

