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

package com.interpss.test.pslf;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.interpss.test.util.FileReader;
import com.interpss.test.util.impl.PSLF_FileProcessor;

public class PslfFileProcessorTest {
	@Test
	public void test() {
		PSLF_FileProcessor proc = new PSLF_FileProcessor(null);
		
//		String s = "       1 NORTH-01     230 1                      2 NORTH-02     230 1 1   262.4   41.7            7.269  36.34   44           ";
//		System.out.println(StringUtil.isInt(s.substring(0, 8).trim()) + "|");
		
		new FileReader("output/pslf/Sample18Bus_epc_01192011.txt")
				.processFile(proc);		

//		System.out.println("Total bus: " + proc.getTotalBus());
		assertTrue(proc.getTotalBus() == 18);
	}
}

