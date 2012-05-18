package org.interpss.spring;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
  
@RunWith(Suite.class)
@SuiteClasses({
	DStabControllerTest.class,
	SimuAppCtxTest.class,
})
public class SpringTestSuite {
	//
}
