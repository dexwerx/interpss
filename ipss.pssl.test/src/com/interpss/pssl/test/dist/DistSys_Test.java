 /*
  * @(#)AclfSampleTest.java   
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
  * @Date 07/15/2007
  * 
  *   Revision History
  *   ================
  *
  */

package com.interpss.pssl.test.dist;

import static org.junit.Assert.assertTrue;

import org.apache.commons.math.complex.Complex;
import org.interpss.display.AclfOutFunc;
import org.interpss.numeric.datatype.Unit.Type;
import org.junit.Test;

import com.interpss.core.acsc.BusGroundCode;
import com.interpss.core.acsc.XFormerConnectCode;
import com.interpss.dist.DistBus;
import com.interpss.dist.adpter.GeneratorAdapter;
import com.interpss.dist.adpter.TransformerAdapter;
import com.interpss.dist.adpter.UtilityAdapter;
import com.interpss.pssl.simu.IpssDist;
import com.interpss.pssl.simu.IpssDist.DistNetDSL;
import com.interpss.pssl.test.BaseTestSetup;

public class DistSys_Test extends BaseTestSetup {
	@Test
	public void singlePointTest1() {
		DistNetDSL distNet = IpssDist.createDistNetwork("Sample DistNetwork")      
        						.setBaseKva(100000.0);
		  
		UtilityAdapter util = distNet.addUtility("Bus1", 138.0, Type.kV);
		util.setVoltage(1.0, Type.PU, 0.0, Type.Deg);
		util.setMvaRating(1000.0, 800.0, Type.mVA);
		util.setX_R(100.0, 100.0);

		GeneratorAdapter gen = distNet.addGenerator("Bus2", 4160.0, Type.Volt);
		gen.setRatedKW(5.0, Type.mW);
		gen.setRatedVoltage(1.0, Type.PU);
		gen.setPFactor(0.8, Type.PU);
		gen.setZ1(new Complex(0.0, 0.1));
		gen.setZ0_2(new Complex(0.0,0.05), new Complex(0.0, 0.1));
		gen.setZUnit(Type.PU);
		
		TransformerAdapter xfr = distNet.addXformer("Bus1", "Bus2");
		xfr.setRating(10.0, Type.mVA);
		xfr.setRatedVoltage(138.0, 4.160, Type.kV);
		xfr.setZ(new Complex(0.0, 7.0), new Complex(0.0, 7.0), Type.Percent);
		xfr.setTurnRatio(1.0, 1.0, Type.PU);
		xfr.setConnect(XFormerConnectCode.WYE, XFormerConnectCode.DELTA);
		xfr.getPrimaryGrounding().setCode(BusGroundCode.UNGROUNDED);
		
		distNet.loadflow();
		//System.out.println(aclfNet.net2String());
		System.out.println(AclfOutFunc.lfResultsBusStyle(distNet.getAclfNetwork()));
	  	
	  	DistBus bus = (DistBus)distNet.getDistNetwork().getBus("Bus1");
	  	//System.out.println(bus.getAcscBus().getGenResults().getReal());
	  	//System.out.println(bus.getAcscBus().getGenResults().getImaginary());
	  	assertTrue(Math.abs(bus.getAcscBus().getGenResults().getReal() + 0.05) < 0.001);
	  	assertTrue(Math.abs(bus.getAcscBus().getGenResults().getImaginary() + 0.0349) < 0.0001);
	}
}

