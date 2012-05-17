package org.ieee.odm;

import org.ieee.odm.ieeecdf.IEEECDF_ODMTest;
import org.ieee.odm.odm_xml.OdmXml_ODMTest;
import org.ieee.odm.opf.OpfSample_3Bus_ODMTest;
import org.ieee.odm.opf.ProcessOPFData_ODMTest;
import org.ieee.odm.psse.PSSEV26_ODMTest;
import org.ieee.odm.psse.PSSEV30_GuideSampleTest;
import org.ieee.odm.psse.PSSEV30_NEISO_ODMTest;
import org.ieee.odm.psse.PSSEV30_ODMTest;
import org.ieee.odm.psse.PSSEV30_SegmentTest;
import org.ieee.odm.pwd.PWD_IEEE14Bus_ODMTest;
import org.ieee.odm.pwd.PwdAdapterTest;
import org.ieee.odm.ucte.UCTE_ODMTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	FuncTest_ODMTest.class,
	
	IEEECDF_ODMTest.class,
	
	UCTE_ODMTest.class,
	//XBeanUCTE_ODMTest.class,
	
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
	
	PWD_IEEE14Bus_ODMTest.class,
	PwdAdapterTest.class,
	
	OpfSample_3Bus_ODMTest.class,
	//ProcessOPFData_ODMTest.class,

	OdmXml_ODMTest.class,
})
public class ODMAdapterTestSuite {
}
