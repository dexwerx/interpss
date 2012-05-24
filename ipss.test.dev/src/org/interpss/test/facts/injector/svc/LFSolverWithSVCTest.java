package org.interpss.test.facts.injector.svc;

import static org.junit.Assert.assertTrue;

import org.apache.commons.math.complex.Complex;
import org.interpss.facts.general.SVCControlType;
import org.interpss.facts.injector.svc.SVCInjectorLF;
import org.interpss.facts.injector.svc.SVCInjectorSolver;
import org.interpss.fadapter.IpssFileAdapter;
import org.interpss.numeric.datatype.Unit.UnitType;
import org.interpss.spring.EditorPluginSpringFactory;
import org.interpss.test.DevTestSetup;
import org.junit.Test;

import com.interpss.CoreObjectFactory;
import com.interpss.common.exp.InterpssException;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfGenCode;
import com.interpss.core.aclf.AclfLoadCode;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.aclf.adpter.AclfSwingBus;
import com.interpss.core.net.Bus;
import com.interpss.simu.SimuContext;

public class LFSolverWithSVCTest extends DevTestSetup {

	@Test
	public void testLFSolverWithSVCConstQ() throws InterpssException {
		AclfNetwork net = createNet();
        SVCInjectorLF myStatcom = new SVCInjectorLF("Bus2", new Complex(0.0, -5.0), SVCControlType.ConstQ, 0.5, net, 2.0, -1.0);
        SVCInjectorLF[] statcomArray = {myStatcom};
        SVCInjectorSolver solver = new SVCInjectorSolver(net, statcomArray);
        
        // output loadflow calculation results
        assertTrue(solver.solveLF());
        assertTrue(Math.abs(myStatcom.getSsh(net).getReal()) < 0.0001);
        assertTrue(Math.abs(myStatcom.getSsh(net).getImaginary() - 0.5) < 0.0001);
	}
	
	@Test
	public void testLFSolverWithSVCConstB() throws InterpssException {
		AclfNetwork net = createNet();
        SVCInjectorLF myStatcom = new SVCInjectorLF("Bus2", new Complex(0.0, -5.0), SVCControlType.ConstB, 2.0, net, 2.0, -1.0);
        SVCInjectorLF[] statcomArray = {myStatcom};
        SVCInjectorSolver solver = new SVCInjectorSolver(net, statcomArray);
        
        // output loadflow calculation results
        assertTrue(solver.solveLF());
        double vi = net.getAclfBus("Bus2").getVoltageMag();
        
        assertTrue(Math.abs(myStatcom.getSsh(net).getReal() / vi / vi) < 0.0001);
        assertTrue(Math.abs(myStatcom.getSsh(net).getImaginary() / vi / vi - (2.0)) < 0.0001);
        
	}
	
	@Test
	public void testLFSolverWithSVCConstV() throws InterpssException {
		AclfNetwork net = createNet();
        SVCInjectorLF myStatcom = new SVCInjectorLF("Bus2", new Complex(0.0, -5.0), SVCControlType.ConstV, 0.9, net, 2.0, -1.0);
        SVCInjectorLF[] statcomArray = {myStatcom};
        SVCInjectorSolver solver = new SVCInjectorSolver(net, statcomArray);
        
        // output loadflow calculation results
        assertTrue(solver.solveLF());
        double vi = net.getAclfBus("Bus2").getVoltageMag();
        
        assertTrue(Math.abs(myStatcom.getSsh(net).getReal() / vi / vi) < 0.0001);
        assertTrue(Math.abs(vi - 0.9) < 0.0001);
        
	}
	
	@Test
	public void testLFSolverWithSVCConstQIEEE14() throws Exception {
		IpssFileAdapter adapter = EditorPluginSpringFactory.getCustomFileAdapter("ipssdat");
		SimuContext simuCtx = adapter.load("testData/ipssdata/ieee14.ipssdat");
		AclfNetwork net = simuCtx.getAclfNet();
		for (Bus thisBus : net.getBusList()) {
			// Test SVC on load bus
			String thisID = thisBus.getId();
			if (net.getAclfBus(thisID).getGenCode() == AclfGenCode.NON_GEN) {
				System.out.println("Testing " + thisID);
		        SVCInjectorLF myStatcom = new SVCInjectorLF(thisID, new Complex(0.0, -5.0), SVCControlType.ConstQ, -0.2, net, 2.0, -1.0);
		        SVCInjectorLF[] statcomArray = {myStatcom};
		        SVCInjectorSolver solver = new SVCInjectorSolver(net, statcomArray);
		        
		        // output loadflow calculation results
		        assertTrue(solver.solveLF());
		        assertTrue(Math.abs(myStatcom.getSsh(net).getReal()) < 0.0001);
		        assertTrue(Math.abs(myStatcom.getSsh(net).getImaginary() - (-0.2)) < 0.0001);
			}
		}
	}
	
	@Test
	public void testLFSolverWithSVCConstBIEEE14() throws Exception {
		IpssFileAdapter adapter = EditorPluginSpringFactory.getCustomFileAdapter("ipssdat");
		SimuContext simuCtx = adapter.load("testData/ipssdata/ieee14.ipssdat");
		AclfNetwork net = simuCtx.getAclfNet();
		for (Bus thisBus : net.getBusList()) {
			SimuContext newSimuCtx = adapter.load("testData/ipssdata/ieee14.ipssdat");
			AclfNetwork newNet = newSimuCtx.getAclfNet();
			// Test SVC on load bus
			String thisID = thisBus.getId();
			if (newNet.getAclfBus(thisID).getGenCode() == AclfGenCode.NON_GEN) {
				System.out.println("Testing " + thisID);
		        SVCInjectorLF myStatcom = new SVCInjectorLF(thisID, new Complex(0.0, -5.0), SVCControlType.ConstB, -0.5, newNet, 2.0, -1.0);
		        SVCInjectorLF[] statcomArray = {myStatcom};
		        SVCInjectorSolver solver = new SVCInjectorSolver(newNet, statcomArray);
		        
		        // output loadflow calculation results
		        assertTrue(solver.solveLF());
		        double vi = newNet.getAclfBus(thisID).getVoltageMag();
		        
		        assertTrue(Math.abs(myStatcom.getSsh(newNet).getReal() / vi / vi) < 0.0001);
		        assertTrue(Math.abs(myStatcom.getSsh(newNet).getImaginary() / vi / vi - (-0.5)) < 0.0001);
			}
		}
	}
	
	@Test
	public void testLFSolverWithSVCConstVIEEE14() throws Exception {
		IpssFileAdapter adapter = EditorPluginSpringFactory.getCustomFileAdapter("ipssdat");
		SimuContext simuCtx = adapter.load("testData/ipssdata/ieee14.ipssdat");
		AclfNetwork net = simuCtx.getAclfNet();
		for (Bus thisBus : net.getBusList()) {
			SimuContext newSimuCtx = adapter.load("testData/ipssdata/ieee14.ipssdat");
			AclfNetwork newNet = newSimuCtx.getAclfNet();
			// Test SVC on load bus
			String thisID = thisBus.getId();
			if ((net.getAclfBus(thisID).getGenCode() != AclfGenCode.SWING) && (net.getAclfBus(thisID).getGenCode() != AclfGenCode.GEN_PV) && 
					(net.getAclfBus(thisID).getGenCode() != AclfGenCode.CAPACITOR) && (net.getAclfBus(thisID).getLoadCode() != AclfLoadCode.NON_LOAD)) {
				System.out.println("Testing " + thisID);
		        SVCInjectorLF myStatcom = new SVCInjectorLF(thisID, new Complex(0.0, -5.0), SVCControlType.ConstV, 0.9, newNet, 2.0, -1.0);
		        SVCInjectorLF[] statcomArray = {myStatcom};
		        SVCInjectorSolver solver = new SVCInjectorSolver(newNet, statcomArray);
		        
		        // output loadflow calculation results
		        assertTrue(solver.solveLF());
		        double vi = newNet.getAclfBus(thisID).getVoltageMag();
		        
		        assertTrue(Math.abs(myStatcom.getSsh(newNet).getReal() / vi / vi) < 0.0001);
		        if (myStatcom.getType() == SVCControlType.ConstV)
		        	assertTrue(Math.abs(vi - 0.9) < 0.0001);
		        else {
		        	if (solver.isMaxBViolated())
		        		assertTrue(Math.abs(myStatcom.getB() - myStatcom.getMaxB()) < 0.0001);
		        	else if (solver.isMinBViolated())
		        		assertTrue(Math.abs(myStatcom.getB() - myStatcom.getMinB()) < 0.0001);
		        }
			}
		}
	}
	
	@Test
	public void testLFSolverWithSVCConstV57() throws Exception {
		IpssFileAdapter adapter = EditorPluginSpringFactory.getCustomFileAdapter("ieee");
		SimuContext simuCtx = adapter.load("testData/ieee_cdf/ieee57.ieee");
		AclfNetwork net = simuCtx.getAclfNet();
		for (Bus thisBus : net.getBusList()) {
			IpssFileAdapter newAdapter = EditorPluginSpringFactory.getCustomFileAdapter("ieee");
			SimuContext newSimuCtx = newAdapter.load("testData/ieee_cdf/ieee57.ieee");
			AclfNetwork newNet = newSimuCtx.getAclfNet();
			// Test SVC on load bus
			String thisID = thisBus.getId();
			if ((net.getAclfBus(thisID).getGenCode() != AclfGenCode.SWING) && (net.getAclfBus(thisID).getGenCode() != AclfGenCode.GEN_PV) && 
					(net.getAclfBus(thisID).getGenCode() != AclfGenCode.CAPACITOR) && (net.getAclfBus(thisID).getLoadCode() != AclfLoadCode.NON_LOAD)) {
				System.out.println("Testing " + thisID);
		        SVCInjectorLF myStatcom = new SVCInjectorLF(thisID, new Complex(0.0, -5.0), SVCControlType.ConstV, 0.9, newNet, 2.0, -1.0);
		        SVCInjectorLF[] statcomArray = {myStatcom};
		        SVCInjectorSolver solver = new SVCInjectorSolver(newNet, statcomArray);
		        
		        // output loadflow calculation results
		        assertTrue(solver.solveLF());
		        double vi = newNet.getAclfBus(thisID).getVoltageMag();
		        
		        assertTrue(Math.abs(myStatcom.getSsh(newNet).getReal() / vi / vi) < 0.0001);
		        if (myStatcom.getType() == SVCControlType.ConstV)
		        	assertTrue(Math.abs(vi - 0.9) < 0.0001);
		        else {
		        	if (solver.isMaxBViolated())
		        		assertTrue(Math.abs(myStatcom.getB() - myStatcom.getMaxB()) < 0.0001);
		        	else if (solver.isMinBViolated())
		        		assertTrue(Math.abs(myStatcom.getB() - myStatcom.getMinB()) < 0.0001);
		        }
			}
		}
	}
	
	@Test
	public void testLFSolverWithSVCConstV118() throws Exception {
		IpssFileAdapter adapter = EditorPluginSpringFactory.getCustomFileAdapter("ieee");
		SimuContext simuCtx = adapter.load("testData/ieee_cdf/ieee118.ieee");
		AclfNetwork net = simuCtx.getAclfNet();
		for (Bus thisBus : net.getBusList()) {
			IpssFileAdapter newAdapter = EditorPluginSpringFactory.getCustomFileAdapter("ieee");
			SimuContext newSimuCtx = newAdapter.load("testData/ieee_cdf/ieee118.ieee");
			AclfNetwork newNet = newSimuCtx.getAclfNet();
			// Test SVC on load bus
			String thisID = thisBus.getId();
			if ((net.getAclfBus(thisID).getGenCode() != AclfGenCode.SWING) && (net.getAclfBus(thisID).getGenCode() != AclfGenCode.GEN_PV) && 
					(net.getAclfBus(thisID).getGenCode() != AclfGenCode.CAPACITOR) && (net.getAclfBus(thisID).getLoadCode() != AclfLoadCode.NON_LOAD)) {
				System.out.println("Testing " + thisID);
		        SVCInjectorLF myStatcom = new SVCInjectorLF(thisID, new Complex(0.0, -5.0), SVCControlType.ConstV, 1.0, newNet, 2.0, -1.0);
		        SVCInjectorLF[] statcomArray = {myStatcom};
		        SVCInjectorSolver solver = new SVCInjectorSolver(newNet, statcomArray);
		        
		        // output loadflow calculation results
		        assertTrue(solver.solveLF());
		        double vi = newNet.getAclfBus(thisID).getVoltageMag();
		        
		        assertTrue(Math.abs(myStatcom.getSsh(newNet).getReal() / vi / vi) < 0.0001);
		        assertTrue(Math.abs(vi - 1.0) < 0.0001);
			}
		}
	}
	
	@Test
	public void testLFSolverWithSVCConstQ57() throws Exception {
		IpssFileAdapter adapter = EditorPluginSpringFactory.getCustomFileAdapter("ieee");
		SimuContext simuCtx = adapter.load("testData/ieee_cdf/ieee57.ieee");
		AclfNetwork net = simuCtx.getAclfNet();
		for (Bus thisBus : net.getBusList()) {
			// Test SVC on load bus
			String thisID = thisBus.getId();
			if ((net.getAclfBus(thisID).getGenCode() != AclfGenCode.SWING) && (net.getAclfBus(thisID).getGenCode() != AclfGenCode.GEN_PV)) {
				IpssFileAdapter newAdapter = EditorPluginSpringFactory.getCustomFileAdapter("ieee");
				SimuContext newSimuCtx = newAdapter.load("testData/ieee_cdf/ieee57.ieee");
				AclfNetwork newNet = newSimuCtx.getAclfNet();
				System.out.println("Testing " + thisID);
		        SVCInjectorLF mySVC = new SVCInjectorLF(thisID, new Complex(0.0, -5.0), SVCControlType.ConstQ, -0.1, newNet, 2.0, -1.0);
		        SVCInjectorLF[] statcomArray = {mySVC};
		        SVCInjectorSolver solver = new SVCInjectorSolver(newNet, statcomArray);
		        
		        // output loadflow calculation results
		        assertTrue(solver.solveLF());
		        
		        assertTrue(Math.abs(mySVC.getSsh(newNet).getReal()) < 0.0001);
		        
		        assertTrue(Math.abs(mySVC.getSsh(newNet).getImaginary() - (-0.1)) < 0.0001);
			}
		}
	}
	
	@Test
	public void testLFSolverWithSVCConstQ118() throws Exception {
		IpssFileAdapter adapter = EditorPluginSpringFactory.getCustomFileAdapter("ieee");
		SimuContext simuCtx = adapter.load("testData/ieee_cdf/ieee118.ieee");
		AclfNetwork net = simuCtx.getAclfNet();
		for (Bus thisBus : net.getBusList()) {
			// Test SVC on load bus
			String thisID = thisBus.getId();
			if ((net.getAclfBus(thisID).getGenCode() != AclfGenCode.SWING) && (net.getAclfBus(thisID).getGenCode() != AclfGenCode.GEN_PV)) {
				IpssFileAdapter newAdapter = EditorPluginSpringFactory.getCustomFileAdapter("ieee");
				SimuContext newSimuCtx = newAdapter.load("testData/ieee_cdf/ieee118.ieee");
				AclfNetwork newNet = newSimuCtx.getAclfNet();
				System.out.println("Testing " + thisID);
		        SVCInjectorLF mySVC = new SVCInjectorLF(thisID, new Complex(0.0, -5.0), SVCControlType.ConstQ, 0.3, newNet, 2.0, -1.0);
		        SVCInjectorLF[] statcomArray = {mySVC};
		        SVCInjectorSolver solver = new SVCInjectorSolver(newNet, statcomArray);
		        
		        // output loadflow calculation results
		        assertTrue(solver.solveLF());
		        
		        assertTrue(Math.abs(mySVC.getSsh(newNet).getReal()) < 0.0001);
		        
		        assertTrue(Math.abs(mySVC.getSsh(newNet).getImaginary() - (0.3)) < 0.0001);
			}
		}
	}
	
	@Test
	public void testLFSolverWithSVCConstB57() throws Exception {
		IpssFileAdapter adapter = EditorPluginSpringFactory.getCustomFileAdapter("ieee");
		SimuContext simuCtx = adapter.load("testData/ieee_cdf/ieee57.ieee");
		AclfNetwork net = simuCtx.getAclfNet();
		for (Bus thisBus : net.getBusList()) {
			// Test SVC on load bus
			String thisID = thisBus.getId();
			if ((net.getAclfBus(thisID).getGenCode() != AclfGenCode.SWING) && (net.getAclfBus(thisID).getGenCode() != AclfGenCode.GEN_PV)) {
				IpssFileAdapter newAdapter = EditorPluginSpringFactory.getCustomFileAdapter("ieee");
				SimuContext newSimuCtx = newAdapter.load("testData/ieee_cdf/ieee57.ieee");
				AclfNetwork newNet = newSimuCtx.getAclfNet();
				System.out.println("Testing " + thisID);
		        SVCInjectorLF mySVC = new SVCInjectorLF(thisID, new Complex(0.0, -5.0), SVCControlType.ConstB, 0.1, newNet, 2.0, -1.0);
		        SVCInjectorLF[] statcomArray = {mySVC};
		        SVCInjectorSolver solver = new SVCInjectorSolver(newNet, statcomArray);
		        
		        // output loadflow calculation results
		        assertTrue(solver.solveLF());
//		        System.out.println(mySVC.getSsh(newNet).getReal());
//		        System.out.println(mySVC.getSsh(newNet).getImaginary());
		        
		        assertTrue(Math.abs(mySVC.getSsh(newNet).getReal()) < 0.0001);
		        
		        double vmi = newNet.getAclfBus(thisID).getVoltageMag();
		        assertTrue(Math.abs(mySVC.getSsh(newNet).getImaginary() / vmi / vmi - (0.1)) < 0.0001);
			}
		}
	}
	
	@Test
	public void testLFSolverWithSVCConstB118() throws Exception {
		IpssFileAdapter adapter = EditorPluginSpringFactory.getCustomFileAdapter("ieee");
		SimuContext simuCtx = adapter.load("testData/ieee_cdf/ieee118.ieee");
		AclfNetwork net = simuCtx.getAclfNet();
		for (Bus thisBus : net.getBusList()) {
			// Test SVC on load bus
			String thisID = thisBus.getId();
			if ((net.getAclfBus(thisID).getGenCode() != AclfGenCode.SWING) && (net.getAclfBus(thisID).getGenCode() != AclfGenCode.GEN_PV)) {
				IpssFileAdapter newAdapter = EditorPluginSpringFactory.getCustomFileAdapter("ieee");
				SimuContext newSimuCtx = newAdapter.load("testData/ieee_cdf/ieee118.ieee");
				AclfNetwork newNet = newSimuCtx.getAclfNet();
				System.out.println("Testing " + thisID);
		        SVCInjectorLF mySVC = new SVCInjectorLF(thisID, new Complex(0.0, -5.0), SVCControlType.ConstB, 0.1, newNet, 2.0, -1.0);
		        SVCInjectorLF[] statcomArray = {mySVC};
		        SVCInjectorSolver solver = new SVCInjectorSolver(newNet, statcomArray);
		        
		        // output loadflow calculation results
		        assertTrue(solver.solveLF());
//		        System.out.println(mySVC.getSsh(newNet).getReal());
//		        System.out.println(mySVC.getSsh(newNet).getImaginary());
		        
		        assertTrue(Math.abs(mySVC.getSsh(newNet).getReal()) < 0.0001);
		        
		        double vmi = newNet.getAclfBus(thisID).getVoltageMag();
		        assertTrue(Math.abs(mySVC.getSsh(newNet).getImaginary() / vmi / vmi - (0.1)) < 0.0001);
			}
		}
	}
	
	private static AclfNetwork createNet() {
        // create a sample 5-bus system for Loadflow
        AclfNetwork net = CoreObjectFactory.createAclfNetwork();
        double basekVA = 100000.0;
        net.setBaseKva(basekVA);
        AclfBus bus1 = CoreObjectFactory.createAclfBus("Bus1", net);
        // set bus name and description attributes
        bus1.setAttributes("Bus 1", "");
        // set bus base voltage
        bus1.setBaseVoltage(4000.0);
        // set bus to be a swing bus
        bus1.setGenCode(AclfGenCode.SWING);
        // adapt the bus object to a swing bus object
        AclfSwingBus swingBus = bus1.toSwingBus();
        // set swing bus attributes
        swingBus.setVoltMag(1.0, UnitType.PU);
        swingBus.setVoltAng(0.0, UnitType.Deg);
        // add the bus into the network
        //net.addBus(bus1);

        AclfBus bus2 = CoreObjectFactory.createAclfBus("Bus2", net);
        bus2.setAttributes("Bus 2", "");
        bus2.setBaseVoltage(4000.0);
        bus2.setLoadCode(AclfLoadCode.CONST_P);
        bus2.setLoadP(1.0);
        bus2.setLoadQ(0.8);

        // create an AclfBranch object
        AclfBranch branch = CoreObjectFactory.createAclfBranch();
        // set branch name, description and circuit number
        branch.setAttributes("Branch 1", "", "1");
        // set branch parameters
        branch.setZ(new Complex(0.0, 0.1));
        // add the branch from Bus1 to Bus2
        net.addBranch(branch, "Bus1", "Bus2");
        
        return net;
	}

}