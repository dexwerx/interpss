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

package org.interpss.test.odm.dstab;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;

import org.ieee.odm.ODMObjectFactory;
import org.ieee.odm.model.dstab.DStabModelParser;
import org.interpss.mapper.odm.ODMDStabDataMapper;
import org.interpss.numeric.datatype.Unit.UnitType;
import org.interpss.test.DevTestSetup;
import org.junit.Test;

import com.interpss.SimuObjectFactory;
import com.interpss.core.acsc.fault.SimpleFaultCode;
import com.interpss.core.acsc.fault.SimpleFaultType;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.dstab.DStabilityNetwork;
import com.interpss.dstab.algo.DynamicSimuAlgorithm;
import com.interpss.dstab.devent.DynamicEvent;
import com.interpss.dstab.devent.DynamicEventType;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;

public class DStab_Ipss5BusTest extends DevTestSetup { 		
	@Test
	public void testCase() throws Exception {
		File file = new File("testdata/ieee_odm/Tran_5bus.xml");
		DStabModelParser parser = ODMObjectFactory.createDStabModelParser();
		if (parser.parse(new FileInputStream(file))) {
			//System.out.println(parser.toXmlDoc(false));

			SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.DSTABILITY_NET);
			if (!new ODMDStabDataMapper(msg)
						.map2Model(parser, simuCtx)) {
				System.out.println("Error: ODM model to InterPSS SimuCtx mapping error, please contact support@interpss.com");
				return;
			}	

			DynamicSimuAlgorithm dstabAlgo = simuCtx.getDynSimuAlgorithm();
			DStabilityNetwork dstabNet = simuCtx.getDStabilityNet();

			LoadflowAlgorithm lfAlgo = dstabAlgo.getAclfAlgorithm();
			lfAlgo.loadflow();
			assertTrue(Math.abs(dstabNet.getDStabBus("Bus-1").getVoltageMag() - 0.86011) < 0.0001);
			assertTrue(Math.abs(dstabNet.getDStabBus("Bus-1").getVoltageAng(UnitType.Deg) + 4.8) < 0.1);
			
/*
						<pss:simulationSetting>
							<pss:dstabMethod>ModifiedEuler</pss:dstabMethod>
							<pss:totalTime unit="Sec" value="10"></pss:totalTime>
							<pss:step unit="Sec" value="0.01" />
							<pss:absMachineAngle>true</pss:absMachineAngle>
							<pss:netEqnSolveConfig>
								<pss:netEqnItrNoEvent>3</pss:netEqnItrNoEvent>
								<pss:netEqnItrWithEvent>5</pss:netEqnItrWithEvent>
							</pss:netEqnSolveConfig>
							<pss:staticLoadModel>
								<pss:staticLoadType>CONST_Z</pss:staticLoadType>
							</pss:staticLoadModel>
						</pss:simulationSetting>
						<pss:dynamicEvents>
							<pss:disableDynEvents>false</pss:disableDynEvents>
							<pss:dynamicEvent id="Fault@Bus-1" name="3PhaseGroudFault" eventType="Fault">
								<pss:startTime unit="Sec" value="0.0"></pss:startTime>
								<pss:duration unit="Sec" value="0.1" />
								<pss:permanentFault>false</pss:permanentFault>
								<pss:fault>
									<pss:faultType>BusFault</pss:faultType>
									<pss:faultCategory>Fault3Phase</pss:faultCategory>
									<pss:busBranchId idRef="Bus-1"></pss:busBranchId>
									<pss:zLG im="0.0" unit="PU" re="0.0"></pss:zLG>
								</pss:fault>
							</pss:dynamicEvent>
						</pss:dynamicEvents> */

			assertTrue(Math.abs(dstabAlgo.getTotalSimuTimeSec() - 10.0) < 0.0001);
			assertTrue(Math.abs(dstabAlgo.getSimuStepSec() - 0.01) < 0.0001);
			
			DynamicEvent eventObj = dstabNet.getDynamicEvent("Fault@Bus-1");
			assertTrue(eventObj != null);
			assertTrue(eventObj.getType() == DynamicEventType.BUS_FAULT);
			assertTrue(eventObj.getStartTimeSec() == 0.0);
			assertTrue(eventObj.getDurationSec() == 0.1);
			
			assertTrue(eventObj.getBusFault().getFaultType() == SimpleFaultType.BUS_FAULT);
			assertTrue(eventObj.getBusFault().getFaultCode() == SimpleFaultCode.GROUND_3P);
			assertTrue(eventObj.getBusFault().getAcscBus().getId().equals("Bus-1"));
		}
	}
}

