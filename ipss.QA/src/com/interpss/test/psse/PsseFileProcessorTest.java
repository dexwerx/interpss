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

package com.interpss.test.psse;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.interpss.test.util.FileReader;
import com.interpss.test.util.impl.PSSE_FileProcessor;

public class PsseFileProcessorTest {
	@Test
	public void test() {
		PSSE_FileProcessor proc = new PSSE_FileProcessor(null);
/*		
		String s = "                                100.0                                     RATING   %MVA FOR TRANSFORMERS";
		System.out.println((s.length() > 38) + "|");
		System.out.println(StringUtil.isDouble(s.substring(0, 38).trim()) + "|");
*/		
		new FileReader("output/psse/200BusV30-peak_raw_01192011.txt")
				.processFile(proc);		

		System.out.println("Total bus: " + proc.getTotalBus());
		assertTrue(proc.getTotalBus() == 200);
	}
}

