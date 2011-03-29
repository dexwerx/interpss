 /*
  * @(#)AllTestSuite.java   
  *
  * Copyright (C) 2006-2010 www.interpss.org
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
  * @Date 12/03/2010
  * 
  *   Revision History
  *   ================
  *
  */

package com.interpss.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.interpss.test.psse.Bus200V30_peakTestCases;
import com.interpss.test.psse.SCGrid_TestCase1_2009TestCases;
import com.interpss.test.psse.SCGrid_TestCase2_2009TestCases;

@RunWith(Suite.class)
@SuiteClasses({
	// PEES
	//PsseFileProcessorTest.class,
	Bus200V30_peakTestCases.class,
	SCGrid_TestCase1_2009TestCases.class,
	SCGrid_TestCase2_2009TestCases.class,
	
	// PSLF
	//PslfFileProcessorTest.class,
})
public class QATestSuite {
}
