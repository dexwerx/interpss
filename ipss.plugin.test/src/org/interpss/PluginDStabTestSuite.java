package org.interpss;

import org.interpss.dstab.control.cml.block.DelayControlBlockTests;
import org.interpss.dstab.control.cml.block.FilterControlBlockTests;
import org.interpss.dstab.control.cml.block.IntegrationControlBlockTests;
import org.interpss.dstab.control.cml.block.PIControlBlockTests;
import org.interpss.dstab.control.cml.block.WashoutControlBlockTests;
import org.interpss.dstab.ieeeModel.IEEE11ModelTest;
import org.interpss.dstab.ieeeModel.IEEE12ModelTest;
import org.interpss.dstab.ieeeModel.IEEE21ModelTest;
import org.interpss.dstab.ieeeModel.IEEE22ModelTest;
import org.interpss.schema.DStabSchemaIEEE11ModelTest;
import org.interpss.spring.DStabControllerTest;
import org.interpss.spring.SimuAppCtxTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	// DStab controller building blocks
	DelayControlBlockTests.class,
	FilterControlBlockTests.class,
	IntegrationControlBlockTests.class,
	PIControlBlockTests.class,
	WashoutControlBlockTests.class,
	
	// IEEE Models
	IEEE11ModelTest.class,
	IEEE12ModelTest.class,
	IEEE21ModelTest.class,
	IEEE22ModelTest.class,
	
	// Spring 
	DStabControllerTest.class,
	SimuAppCtxTest.class,	

	// run case xml scripts
	DStabSchemaIEEE11ModelTest.class,
})
public class PluginDStabTestSuite {
}
