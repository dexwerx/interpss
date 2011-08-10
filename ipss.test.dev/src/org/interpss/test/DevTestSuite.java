package org.interpss.test;

import org.interpss.test.facts.simult.svc.SimpleSVCTest;
import org.interpss.test.odm.acsc.Acsc_5BusTest;
import org.interpss.test.odm.dstab.DStab_Ipss5BusTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	//Acsc
	Acsc_5BusTest.class,
	
	//DStab
	DStab_Ipss5BusTest.class,	
	
	// FACTS
	SimpleSVCTest.class
})
public class DevTestSuite {
}
