 /*
  * @(#)Dclf_Test.java   
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

package com.interpss.pssl.test.dclf;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.interpss.datatype.DblBusValue;
import com.interpss.pssl.simu.impl.PTradingHelper;
import com.interpss.pssl.test.BaseTestSetup;

public class DclfFunc_Test extends BaseTestSetup {
	@Test
	public void dclfFuncTest() {
		assertTrue(PTradingHelper.isOffPeakHour("5:00"));
		assertTrue(PTradingHelper.isOffPeakHour("22:00"));
		assertTrue(!PTradingHelper.isOffPeakHour("10:00"));
		
		List<DblBusValue> list = new ArrayList<DblBusValue>();
		list.add(new DblBusValue(1.0));
		list.add(new DblBusValue(2.0));
		list.add(new DblBusValue(0.5));
		Collections.sort(list, DblBusValue.getComparator());
	
		list.set(2, new DblBusValue(0.6));

		while (list.size() > 0 && Math.abs(list.get(list.size()-1).value) < 0.001)
	  		list.remove(list.size()-1);
		
		System.out.println(list);

		list = new ArrayList<DblBusValue>();
		list.add(new DblBusValue(1.0));
		list.add(new DblBusValue(0.0));
		list.add(new DblBusValue(0.0));
		Collections.sort(list, DblBusValue.getComparator());
	
	  	while (list.size() > 0 && Math.abs(list.get(list.size()-1).value) < 0.001)
	  		list.remove(list.size()-1);
	
		System.out.println(list);
	}
}

