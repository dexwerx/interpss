 /*
  * @(#)CommonTestSuite.java   
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

package com.interpss.pssl.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.interpss.pssl.test.dclf.DclfGSF_Test;
import com.interpss.pssl.test.dclf.DclfLODFPaper_Test;
import com.interpss.pssl.test.dclf.DclfLODF_Test;
import com.interpss.pssl.test.dist.DistSys_Test;

@RunWith(Suite.class)
@SuiteClasses({
	DistSys_Test.class,
	
	DclfLODFPaper_Test.class,
	DclfLODF_Test.class,
	DclfGSF_Test.class,
})
public class PSSLTestSuite {
}
