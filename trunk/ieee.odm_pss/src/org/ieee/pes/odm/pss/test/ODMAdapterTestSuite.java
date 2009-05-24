package org.ieee.pes.odm.pss.test;

import org.ieee.pes.odm.pss.test.bpa.BPA_ODMTest;
import org.ieee.pes.odm.pss.test.ieeecdf.IEEECDF_ODMTest;
import org.ieee.pes.odm.pss.test.ucte.UCTE_ODMTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	IEEECDF_ODMTest.class,
	
	UCTE_ODMTest.class,
	
	BPA_ODMTest.class
})
public class ODMAdapterTestSuite {
}
