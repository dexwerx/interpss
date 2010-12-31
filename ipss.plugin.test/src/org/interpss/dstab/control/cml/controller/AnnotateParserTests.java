 /*
  * @(#)AnnotateParserTests.java   
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

package org.interpss.dstab.control.cml.controller;

import static org.junit.Assert.assertTrue;

import org.interpss.dstab.control.cml.block.DelayControlBlock;
import org.interpss.dstab.control.cml.controller.util.DStabTestUtilFunc;
import org.interpss.dstab.control.cml.controller.util.TestAnnotateExciter;
import org.interpss.dstab.control.cml.controller.util.TestAnnotateGovernor;
import org.interpss.dstab.control.cml.controller.util.TestAnnotateStabilizer;
import org.interpss.dstab.control.cml.controller.util.TestAnnotateStabilizerComplex;
import org.interpss.dstab.ieeeModel.DStabTestSetupBase;
import org.junit.Test;

import com.interpss.dstab.DStabBus;
import com.interpss.dstab.DStabilityNetwork;
import com.interpss.dstab.controller.annotate.holder.ControlBlockFieldHolder;
import com.interpss.dstab.controller.annotate.holder.FunctionFieldHolder;
import com.interpss.dstab.controller.annotate.holder.StaticBlockFieldHolder;
import com.interpss.dstab.controller.annotate.util.AnCntlUtilFunc;
import com.interpss.dstab.controller.block.IFunction;
import com.interpss.dstab.datatype.CMLVarEnum;
import com.interpss.dstab.mach.Machine;

public class AnnotateParserTests extends DStabTestSetupBase {
	private ControlBlockFieldHolder cfield;
	private StaticBlockFieldHolder sfield;
	private FunctionFieldHolder<?> field;

	@Test
	public void exciterTestCase() throws Exception {
		DStabilityNetwork net = DStabTestUtilFunc.createTestNetwork();
		DStabBus bus = net.getDStabBus("BusId");
		Machine machine = bus.getMachine();
		/*
			public double k = 50.0, t = 0.05, vmax = 10.0, vmin = 0.0;
    		@AnControllerField(
        	type= "type.ControlBlock",
        	input="this.refPoint + pss.vs - mach.vt",
        	parameter={"type.Limit", "this.k", "this.t", "this.vmax", "this.vmin"},
        	y0="mach.efd"	)
		DelayControlBlock delayBlock;
		 */
		TestAnnotateExciter exc = new TestAnnotateExciter();
		assertTrue(exc.getDoubleField("k") == 50.0);
		
		assertTrue(exc.initStates(bus, machine, null)); 
		
		//System.out.println(exc.toString());
		assertTrue(AnCntlUtilFunc.getBlockFieldHolder("delayBlock", exc.getFieldList()) != null);

		DelayControlBlock block = (DelayControlBlock)(AnCntlUtilFunc.getBlock("delayBlock", exc.getFieldList()));
		assertTrue(block.getK() == 50.0);
		assertTrue(block.getT() == 0.05);
		assertTrue(block.getLimit().getMax() == 10.0);
		assertTrue(block.getLimit().getMin() == 0.0);

		cfield = (ControlBlockFieldHolder)(AnCntlUtilFunc.getBlockFieldHolder("delayBlock", exc.getFieldList()));
		assertTrue(cfield.getInitOrder() == 1);
		assertTrue(cfield.hasInput(CMLVarEnum.ControllerRefPoint));
		assertTrue(!cfield.hasOutput(CMLVarEnum.ControllerRefPoint));
		assertTrue(cfield.getInputExp().getRecList().length == 3);
		assertTrue(cfield.getInputExp().hasVarType(CMLVarEnum.ControllerRefPoint));
		assertTrue(cfield.getInputExp().hasVarType(CMLVarEnum.PssVs));
		assertTrue(cfield.getInputExp().hasVarType(CMLVarEnum.MachVt));

		assertTrue(cfield.getParameters().length == 5);

		assertTrue(cfield.getY0Exp().getRecList().length == 1);
		assertTrue(cfield.getY0Exp().hasVarType(CMLVarEnum.MachEfd));
		
		assertTrue(AnCntlUtilFunc.getBlockFieldHolder("seFunc", exc.getFieldList()) != null);

//		SeFunction seFunc = (SeFunction)(AnCntlUtilFunc.getBlock("seFunc", exc.getFieldList()));
//		assertTrue(seFunc.g.getSe1_0() == 50.0);
//		assertTrue(seFunc.getSe0_75() == 1.0);

		field = (FunctionFieldHolder<IFunction>)(AnCntlUtilFunc.getBlockFieldHolder("seFunc", exc.getFieldList()));
		assertTrue(field.getInputs().length == 3);
		assertTrue(field.getParameters().length == 2);

		assertTrue(AnCntlUtilFunc.getBlockFieldHolder("seFunc1", exc.getFieldList()) != null);
		field = (FunctionFieldHolder<IFunction>)(AnCntlUtilFunc.getBlockFieldHolder("seFunc1", exc.getFieldList()));
		assertTrue(field.getInputs().length == 3);
	}

	@Test
	public void governorTestCase() {
		DStabilityNetwork net = DStabTestUtilFunc.createTestNetwork();
		DStabBus bus = net.getDStabBus("BusId");
		Machine machine = bus.getMachine();
		/*
		public double ka = 10.0, ta = 0.5;
		@AnControllerField(
        	type= "type.ControlBlock",
        	input="mach.speed - 1.0",
        	parameter={"type.NoLimit", "this.ka", "this.ta"},
        	y0="this.refPoint - this.gainBlock.u0"	)
		DelayControlBlock delayBlock;

		public double ks = 1.0, pmax = 1.2, pmin = 0.0;
		@AnControllerField(
        	type= "type.StaticBlock",
        	input="this.refPoint - this.delayBlock.y",
        	parameter={"type.Limit", "this.ks", "this.pmax", "this.pmin"},
        	y0="mach.pm"	)
		GainBlock gainBlock;
		*/
		TestAnnotateGovernor gov = new TestAnnotateGovernor();
		
		assertTrue(gov.initStates(bus, machine, null));
		
		assertTrue(AnCntlUtilFunc.getBlockFieldHolder("delayBlock", gov.getFieldList()) != null);
		cfield = (ControlBlockFieldHolder)(AnCntlUtilFunc.getBlockFieldHolder("delayBlock", gov.getFieldList()));
		assertTrue(cfield.getInitOrder() == -1);
		assertTrue(cfield.getY0Exp().hasField("gainBlock"));

		assertTrue(AnCntlUtilFunc.getBlockFieldHolder("gainBlock", gov.getFieldList()) != null);
		sfield = (StaticBlockFieldHolder)(AnCntlUtilFunc.getBlockFieldHolder("gainBlock", gov.getFieldList()));
		assertTrue(sfield.getInitOrder() == 1);
		assertTrue(sfield.getInputExp().hasField("delayBlock"));
		//System.out.println(gov.toString());
	}

	@Test
	public void stabilizerTestCase() {
		DStabilityNetwork net = DStabTestUtilFunc.createTestNetwork();
		DStabBus bus = net.getDStabBus("BusId");
		Machine machine = bus.getMachine();
		/*
		public double k1 = 1.0, t1 = 0.05, t2 = 0.5;
		@AnControllerField(
        	type= "type.ControlBlock",
        	input="mach.speed - this.refPoint",
        	parameter={"type.NoLimit", "this.k1", "this.t1", "this.t2"},
        	y0="this.filterBlock2.u0"	)
		FilterControlBlock filterBlock1;

		public double k2 = 1.0, t3 = 0.05, t4 = 0.25, vmax = 0.2, vmin = -0.2;
		@AnControllerField(
        	type= "type.ControlBlock",
        	input="this.filterBlock1.y",
        	parameter={"type.Limit", "this.k2", "this.t3", "this.t4", "this.vmax", "this.vmin"},
        	y0="pss.vs"	)
		FilterControlBlock filterBlock2;
		*/
		TestAnnotateStabilizer pss = new TestAnnotateStabilizer();

		assertTrue(pss.initStates(bus, machine, null));

		assertTrue(AnCntlUtilFunc.getBlockFieldHolder("filterBlock1", pss.getFieldList()) != null);
		cfield = (ControlBlockFieldHolder)(AnCntlUtilFunc.getBlockFieldHolder("filterBlock1", pss.getFieldList()));
		assertTrue(cfield.getInitOrder() == 1);
		assertTrue(cfield.getY0Exp().hasField("filterBlock2"));

		assertTrue(AnCntlUtilFunc.getBlockFieldHolder("filterBlock2", pss.getFieldList()) != null);
		cfield = (ControlBlockFieldHolder)(AnCntlUtilFunc.getBlockFieldHolder("filterBlock2", pss.getFieldList()));
		assertTrue(cfield.getInitOrder() == 2);
		assertTrue(cfield.getInputExp().hasField("filterBlock1"));
		//System.out.println(pss.toString());
	}

	@Test
	public void stabilizerComplexTestCase() {
		DStabilityNetwork net = DStabTestUtilFunc.createTestNetwork();
		DStabBus bus = net.getDStabBus("BusId");
		Machine machine = bus.getMachine();

		//Machine mach = TestUtil.createMachine();
		TestAnnotateStabilizerComplex pss = new TestAnnotateStabilizerComplex();

		assertTrue(pss.initStates(bus, machine, null));

		assertTrue(AnCntlUtilFunc.getBlockFieldHolder("gainBlock1", pss.getFieldList()) != null);
		sfield = (StaticBlockFieldHolder)(AnCntlUtilFunc.getBlockFieldHolder("gainBlock1", pss.getFieldList()));
		assertTrue(sfield.getInitOrder() == -2);

		assertTrue(AnCntlUtilFunc.getBlockFieldHolder("gainBlock2", pss.getFieldList()) != null);
		sfield = (StaticBlockFieldHolder)(AnCntlUtilFunc.getBlockFieldHolder("gainBlock2", pss.getFieldList()));
		assertTrue(sfield.getInitOrder() == -1);
		
		assertTrue(AnCntlUtilFunc.getBlockFieldHolder("filterBlock1", pss.getFieldList()) != null);
		cfield = (ControlBlockFieldHolder)(AnCntlUtilFunc.getBlockFieldHolder("filterBlock1", pss.getFieldList()));
		assertTrue(cfield.getInitOrder() == 1);
		assertTrue(cfield.getY0Exp().hasField("filterBlock2"));

		assertTrue(AnCntlUtilFunc.getBlockFieldHolder("filterBlock2", pss.getFieldList()) != null);
		cfield = (ControlBlockFieldHolder)(AnCntlUtilFunc.getBlockFieldHolder("filterBlock2", pss.getFieldList()));
		assertTrue(cfield.getInitOrder() == 2);
		assertTrue(cfield.getInputExp().hasField("filterBlock1"));
		//System.out.println(pss.toString());
	}
}
