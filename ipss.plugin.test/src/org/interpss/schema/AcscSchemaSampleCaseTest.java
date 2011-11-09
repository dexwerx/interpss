 /*
  * @(#)IpssSchemaAcscSampleCaseTest.java   
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

package org.interpss.schema;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.interpss.PluginTestSetup;
import org.interpss.numeric.util.TestUtilFunc;
import org.interpss.spring.PluginSpringCtx;
import org.interpss.xml.IpssXmlParser;
import org.interpss.xml.schema.AcscStudyCaseXmlType;
import org.interpss.xml.schema.AnalysisRunDataType;
import org.junit.Test;

import com.interpss.CoreObjectFactory;
import com.interpss.core.acsc.AcscNetwork;
import com.interpss.core.acsc.fault.AcscBusFault;
import com.interpss.core.algo.SimpleFaultAlgorithm;
import com.interpss.simu.util.sample.SampleCases;

public class AcscSchemaSampleCaseTest extends PluginTestSetup {
	@Test
	public void sampleTest() throws Exception {
		File xmlFile = new File("testData/xml/RunAcscCase.xml");
  		IpssXmlParser parser = new IpssXmlParser(xmlFile);
  		//System.out.println("----->" + parser.getRootElem().toString());

	  	assertTrue(parser.getRunStudyCase().getAnalysisRunType() == AnalysisRunDataType.RUN_ACSC);

	  	AcscNetwork faultNet = CoreObjectFactory.createAcscNetwork();
		SampleCases.load_SC_5BusSystem(faultNet);
		//System.out.println(faultNet.net2String());
  		assertTrue((faultNet.getBusList().size() == 5 && faultNet.getBranchList().size() == 5));

  		SimpleFaultAlgorithm algo = CoreObjectFactory.createSimpleFaultAlgorithm(faultNet);
	  	//IpssMapper mapper = new IpssXmlMapper();
	  	for ( AcscStudyCaseXmlType scase : parser.getRunAcscStudyCase().getAcscStudyCaseList().getAcscStudyCase()) {
	  		PluginSpringCtx.getXml2ScAlgorithmMapper().map2Model(scase, algo);
	  		AcscBusFault fault = algo.getFaultList().get(0);
	  		algo.calculateBusFault((AcscBusFault)fault);
	  			/*
	  			 fault amps(1): (  0.0000 + j 32.57143) pu
	  			 fault amps(2): (  0.0000 + j  0.0000) pu
	  			 fault amps(0): (  0.0000 + j  0.0000) pu
	  			 */
	  		assertTrue(TestUtilFunc.compare(fault.getFaultResult().getSCCurrent_012(), 0.0, 0.0, 0.0, 32.57141796260087, 0.0, 0.0) );
	  	}
	}
}

