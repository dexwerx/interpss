package org.interpss;

import org.interpss.core.adapter.bpa.BPASampleTestCases;
import org.interpss.core.adapter.ieee.IEEECommonFormatTest;
import org.interpss.core.adapter.ieee.IEEECommonFormat_CommaTest;
import org.interpss.core.adapter.internal.Bus1824Test;
import org.interpss.core.adapter.internal.IEEE14Test;
import org.interpss.core.adapter.ucte.UCTEFormatAusPowerTest;
import org.interpss.core.smallz.IEEE14BusBreakerTest;
import org.interpss.core.smallz.IEEE14BusBreaker_lf_Test;
import org.interpss.core.smallz.SampleSwitchBreakerModelTest;
import org.interpss.dstab.DStab_2Bus;
import org.interpss.dstab.control.cml.block.DelayControlBlockTests;
import org.interpss.dstab.control.cml.block.FilterControlBlockTests;
import org.interpss.dstab.control.cml.block.IntegrationControlBlockTests;
import org.interpss.dstab.control.cml.block.PIControlBlockTests;
import org.interpss.dstab.control.cml.block.WashoutControlBlockTests;
import org.interpss.dstab.mach.EConstMachineTest;
import org.interpss.dstab.mach.Eq1Ed1MachineTest;
import org.interpss.dstab.mach.Eq1MachineCaseTest;
import org.interpss.dstab.mach.MachineSaturationTest;
import org.interpss.dstab.mach.RoundRotorMachineTest;
import org.interpss.dstab.mach.SalientPoleMachineTest;
import org.interpss.odm.psse.v30.GuideSample_TestCase;
import org.interpss.spring.SimuAppCtxTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	// small Z branch
	SampleSwitchBreakerModelTest.class,
	IEEE14BusBreaker_lf_Test.class,
	IEEE14BusBreakerTest.class,
	
	// DStab controller building blocks
	DelayControlBlockTests.class,
	FilterControlBlockTests.class,
	IntegrationControlBlockTests.class,
	PIControlBlockTests.class,
	WashoutControlBlockTests.class,
	
	// DStab Machine
	Eq1Ed1MachineTest.class,
	EConstMachineTest.class,
	Eq1MachineCaseTest.class,
	MachineSaturationTest.class,
	RoundRotorMachineTest.class,
	SalientPoleMachineTest.class,
	
	// DStab ODM
	DStab_2Bus.class,
	
	// Spring 
	SimuAppCtxTest.class,	

	// core file adapter
	IEEECommonFormat_CommaTest.class,
	IEEECommonFormatTest.class,
	BPASampleTestCases.class,
	UCTEFormatAusPowerTest.class,
	//CR_UserTestCases.class,
	//GuideSampleTestCases.class,
	
	IEEE14Test.class,
	Bus1824Test.class,
//	Bus6384Test.class,
//	Bus11856Test.class,
	
	GuideSample_TestCase.class,
})
public class CorePluginTestSuite {
}
