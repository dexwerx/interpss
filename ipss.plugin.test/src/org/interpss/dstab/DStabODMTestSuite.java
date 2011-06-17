package org.interpss.dstab;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	DStab_2Bus.class,
	DStab_5BusNoRegulator.class,
})
public class DStabODMTestSuite {
}
