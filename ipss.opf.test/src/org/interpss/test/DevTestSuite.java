package org.interpss.test;

import org.interpss.test.odm.opf.OpfSample;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({

	// OPF
	OpfSample.class,
})
public class DevTestSuite {
}
