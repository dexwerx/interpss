package org.interpss;

import org.interpss.gridgain.aclf.Aclf_IEEE14BusGridGainTest;
import org.interpss.gridgain.aclf.IEEE14ContigencyGridGainTest;
import org.interpss.gridgain.aclf.IEEE14MCase_Modification_GridTest;
import org.interpss.gridgain.aclf.IEEE14MultiCaseGridGainTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	Aclf_IEEE14BusGridGainTest.class,
	IEEE14MultiCaseGridGainTest.class,
	IEEE14MCase_Modification_GridTest.class,
	IEEE14ContigencyGridGainTest.class
	
	//DStab_5BusNoRegulatorGridGainTest.class,
	//DStab_5BusGridGainTest.class,
	
})
public class GridgainTestSuite {
}
