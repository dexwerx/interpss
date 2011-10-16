package org.interpss.core.aclf;

import static org.junit.Assert.assertTrue;

import org.interpss.PluginTestSetup;
import org.junit.Test;

import com.interpss.common.datatype.UnitType;
import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.aclf.FlowInterface;
import com.interpss.core.aclf.FlowInterfaceBranch;
import com.interpss.core.aclf.FlowInterfaceLimit;
import com.interpss.core.aclf.adpter.SwingBusAdapter;
import com.interpss.core.algo.AclfMethod;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.simu.SimuObjectFactory;

public class IEEE14InterfaceCaseTest  extends PluginTestSetup {
	@Test
	public void runTest() throws Exception {
		SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.ACLF_NETWORK);
		loadCaseData("testData/aclf/IEEE-14Bus.ipss", simuCtx);
		
		AclfNetwork net = simuCtx.getAclfNet();
		FlowInterface inf = CoreObjectFactory.createInterface(net, "interface");
		
		FlowInterfaceLimit onPeak = CoreObjectFactory.createInterfaceLimit();
		inf.setOnPeakLimit(onPeak);
		onPeak.setRefDirExportLimit(1.0);
		onPeak.setOppsiteRefDirImportLimit(-1.0);
		
		FlowInterfaceLimit offPeak = CoreObjectFactory.createInterfaceLimit();
		inf.setOffPeakLimit(offPeak);
		offPeak.setRefDirExportLimit(0.5);
		offPeak.setOppsiteRefDirImportLimit(-0.5);
		
		FlowInterfaceBranch b = CoreObjectFactory.createInterfaceBranch(inf);
		b.setAclfBranch(net.getAclfBranch("0004", "0007", "1"));
		b.setWeight(1.0);
		b.setBranchDir(true);
		
		b = CoreObjectFactory.createInterfaceBranch(inf);
		b.setAclfBranch(net.getAclfBranch("0004", "0009", "1"));
		b.setWeight(1.0);
		b.setBranchDir(true);

		b = CoreObjectFactory.createInterfaceBranch(inf);
		b.setAclfBranch(net.getAclfBranch("0005", "0006", "1"));
		b.setWeight(1.0);
		b.setBranchDir(true);

		LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
	  	algo.setLfMethod(AclfMethod.NR);
	  	algo.setNonDivergent(true);
  		assertTrue(algo.loadflow());
  		AclfBus swingBus = (AclfBus)net.getBus("0001");
		SwingBusAdapter swing = swingBus.toSwingBus();
		//System.out.println(ComplexFunc.toString(swing.getGenResults(UnitType.PU)));
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getReal()-2.32394)<0.0001);
  		assertTrue(Math.abs(swing.getGenResults(UnitType.PU).getImaginary()+0.20674)<0.0001);
  		
		//System.out.println("Flow export: " + inf.flowExport());
		//System.out.println("Flow import: " + inf.flowImport());
  		assertTrue(Math.abs(inf.flowExport()-0.88234)<0.0001);
  		assertTrue(Math.abs(inf.flowImport()+0.88234)<0.0001);

  		assertTrue(!inf.onPeakViolation());
  		assertTrue(inf.offPeakViolation());
	}			
}
