package org.ieee.odm;

import org.ieee.odm.bpa.BPA_ODMTest;
import org.ieee.odm.ge.GE_ODMTest;
import org.ieee.odm.ieeecdf.IEEECDF_ODMTest;
import org.ieee.odm.odm_xml.OdmXml_Test;
import org.ieee.odm.ofp.OpfSample_3BusTest;
import org.ieee.odm.psse.PSSEV26_ODMTest;
import org.ieee.odm.psse.PSSEV30_GuideSampleTest;
import org.ieee.odm.psse.PSSEV30_NEISO_ODMTest;
import org.ieee.odm.psse.PSSEV30_ODMTest;
import org.ieee.odm.psse.PSSEV30_SegmentTest;
import org.ieee.odm.ucte.UCTE_ODMTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	FuncTestCase.class,
	
	IEEECDF_ODMTest.class,
	
	UCTE_ODMTest.class,
	//XBeanUCTE_ODMTest.class,
	
	BPA_ODMTest.class,
	
	GE_ODMTest.class,
	
//	org.ieee.odm.psse.old.XBeanPSSEV30_ODMTest.class,
//	org.ieee.odm.psse.old.XBeanPSSEV30_NEISO_ODMTest.class,
//	org.ieee.odm.psse.old.XBeanPSSEV26_ODMTest.class,
//	org.ieee.odm.psse.old.XBeanPSSEV30_GuideSampleTest.class,
//	org.ieee.odm.psse.old.XBeanPSSEV30_SegmentTest.class,

	PSSEV30_ODMTest.class,
	PSSEV30_NEISO_ODMTest.class,
	PSSEV26_ODMTest.class,
	PSSEV30_GuideSampleTest.class,
	PSSEV30_SegmentTest.class,
	
	OdmXml_Test.class,
	OpfSample_3BusTest.class,
})
public class ODMAdapterTestSuite {
}
