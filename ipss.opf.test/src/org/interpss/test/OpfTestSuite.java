package org.interpss.test;

import org.interpss.test.odm.opf.OpfModelParserTest;
import org.interpss.test.odm.opf.OpfSample;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	OpfModelParserTest.class,
	OpfSample.class,
})
public class OpfTestSuite {
}
