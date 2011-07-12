package org.interpss.test.facts.svc;

import static org.junit.Assert.*;

import org.apache.commons.math.complex.Complex;
import org.interpss.custom.IpssFileAdapter;
import org.interpss.facts.svc.LFSolverWithSVC;
import org.interpss.facts.svc.SVCControlType;
import org.interpss.facts.svc.SVCLF;
import org.interpss.spring.PluginSpringCtx;
import org.interpss.test.DevTestSetup;
import org.junit.Test;

import com.interpss.common.datatype.UnitType;
import com.interpss.common.exp.InterpssException;
import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfGenCode;
import com.interpss.core.aclf.AclfLoadCode;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.aclf.adpter.SwingBusAdapter;
import com.interpss.core.net.Bus;
import com.interpss.simu.SimuContext;

public class LFSolverWithSVCTest extends DevTestSetup {

	@Test
	public void testLFSolverWithSVCConstQ() throws InterpssException {
		AclfNetwork net = createNet();
        SVCLF myStatcom = new SVCLF("Bus2", new Complex(0.0, -5.0), SVCControlType.ConstQ, 0.5, net);
        SVCLF[] statcomArray = {myStatcom};
        LFSolverWithSVC solver = new LFSolverWithSVC(net, statcomArray);
        
        // output loadflow calculation results
        assertTrue(solver.solveLF());
        assertTrue(Math.abs(myStatcom.getSsh(net).getReal()) < 0.0001);
        assertTrue(Math.abs(myStatcom.getSsh(net).getImaginary() - 0.5) < 0.0001);
	}
	
	@Test
	public void testLFSolverWithSVCConstB() throws InterpssException {
		AclfNetwork net = createNet();
        SVCLF myStatcom = new SVCLF("Bus2", new Complex(0.0, -5.0), SVCControlType.ConstB, 2.0, net);
        SVCLF[] statcomArray = {myStatcom};
        LFSolverWithSVC solver = new LFSolverWithSVC(net, statcomArray);
        
        // output loadflow calculation results
        assertTrue(solver.solveLF());
        double vi = net.getAclfBus("Bus2").getVoltageMag();
        
        assertTrue(Math.abs(myStatcom.getSsh(net).getReal() / vi / vi) < 0.0001);
        assertTrue(Math.abs(myStatcom.getSsh(net).getImaginary() / vi / vi - (2.0)) < 0.0001);
        
	}
	
	@Test
	public void testLFSolverWithSVCConstV() throws InterpssException {
		AclfNetwork net = createNet();
        SVCLF myStatcom = new SVCLF("Bus2", new Complex(0.0, -5.0), SVCControlType.ConstV, 1.05, net);
        SVCLF[] statcomArray = {myStatcom};
        LFSolverWithSVC solver = new LFSolverWithSVC(net, statcomArray);
        
        // output loadflow calculation results
        assertTrue(solver.solveLF());
        double vi = net.getAclfBus("Bus2").getVoltageMag();
        
        assertTrue(Math.abs(myStatcom.getSsh(net).getReal() / vi / vi) < 0.0001);
        assertTrue(Math.abs(vi - 1.05) < 0.0001);
        
	}
	
	@Test
	public void testLFSolverWithSVCConstQIEEE14() throws Exception {
		IpssFileAdapter adapter = PluginSpringCtx.getCustomFileAdapter("ipssdat");
		SimuContext simuCtx = adapter.load("testData/ipssdata/ieee14.ipssdat");
		AclfNetwork net = simuCtx.getAclfNet();
		for (Bus thisBus : net.getBusList()) {
			// Test SVC on load bus
			String thisID = thisBus.getId();
			if (net.getAclfBus(thisID).getGenCode() == AclfGenCode.NON_GEN) {
				System.out.println("Testing " + thisID);
		        SVCLF myStatcom = new SVCLF(thisID, new Complex(0.0, -5.0), SVCControlType.ConstQ, -0.2, net);
		        SVCLF[] statcomArray = {myStatcom};
		        LFSolverWithSVC solver = new LFSolverWithSVC(net, statcomArray);
		        
		        // output loadflow calculation results
		        assertTrue(solver.solveLF());
		        assertTrue(Math.abs(myStatcom.getSsh(net).getReal()) < 0.0001);
		        assertTrue(Math.abs(myStatcom.getSsh(net).getImaginary() - (-0.2)) < 0.0001);
			}
		}
	}
	
	@Test
	public void testLFSolverWithSVCConstBIEEE14() throws Exception {
		IpssFileAdapter adapter = PluginSpringCtx.getCustomFileAdapter("ipssdat");
		SimuContext simuCtx = adapter.load("testData/ipssdata/ieee14.ipssdat");
		AclfNetwork net = simuCtx.getAclfNet();
		for (Bus thisBus : net.getBusList()) {
			SimuContext newSimuCtx = adapter.load("testData/ipssdata/ieee14.ipssdat");
			AclfNetwork newNet = newSimuCtx.getAclfNet();
			// Test SVC on load bus
			String thisID = thisBus.getId();
			if (newNet.getAclfBus(thisID).getGenCode() == AclfGenCode.NON_GEN) {
				System.out.println("Testing " + thisID);
		        SVCLF myStatcom = new SVCLF(thisID, new Complex(0.0, -5.0), SVCControlType.ConstB, -0.5, newNet);
		        SVCLF[] statcomArray = {myStatcom};
		        LFSolverWithSVC solver = new LFSolverWithSVC(newNet, statcomArray);
		        
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
		IpssFileAdapter adapter = PluginSpringCtx.getCustomFileAdapter("ipssdat");
		SimuContext simuCtx = adapter.load("testData/ipssdata/ieee14.ipssdat");
		AclfNetwork net = simuCtx.getAclfNet();
		for (Bus thisBus : net.getBusList()) {
			SimuContext newSimuCtx = adapter.load("testData/ipssdata/ieee14.ipssdat");
			AclfNetwork newNet = newSimuCtx.getAclfNet();
			// Test SVC on load bus
			String thisID = thisBus.getId();
			if ((net.getAclfBus(thisID).getGenCode() != AclfGenCode.SWING) && (net.getAclfBus(thisID).getGenCode() != AclfGenCode.GEN_PV)) {
				System.out.println("Testing " + thisID);
		        SVCLF myStatcom = new SVCLF(thisID, new Complex(0.0, -5.0), SVCControlType.ConstV, 0.95, newNet);
		        SVCLF[] statcomArray = {myStatcom};
		        LFSolverWithSVC solver = new LFSolverWithSVC(newNet, statcomArray);
		        
		        // output loadflow calculation results
		        assertTrue(solver.solveLF());
		        double vi = newNet.getAclfBus(thisID).getVoltageMag();
		        
		        assertTrue(Math.abs(myStatcom.getSsh(newNet).getReal() / vi / vi) < 0.0001);
		        assertTrue(Math.abs(vi - 0.95) < 0.0001);
			}
		}
	}
	
	@Test
	public void testLFSolverWithSVCConstQ57() throws Exception {
		IpssFileAdapter adapter = PluginSpringCtx.getCustomFileAdapter("ieee");
		SimuContext simuCtx = adapter.load("testData/ieee_cdf/ieee57.ieee");
		AclfNetwork net = simuCtx.getAclfNet();
		for (Bus thisBus : net.getBusList()) {
			// Test SVC on load bus
			String thisID = thisBus.getId();
			if ((net.getAclfBus(thisID).getGenCode() != AclfGenCode.SWING) && (net.getAclfBus(thisID).getGenCode() != AclfGenCode.GEN_PV)) {
				IpssFileAdapter newAdapter = PluginSpringCtx.getCustomFileAdapter("ieee");
				SimuContext newSimuCtx = newAdapter.load("testData/ieee_cdf/ieee57.ieee");
				AclfNetwork newNet = newSimuCtx.getAclfNet();
				System.out.println("Testing " + thisID);
		        SVCLF mySVC = new SVCLF(thisID, new Complex(0.0, -5.0), SVCControlType.ConstQ, -0.1, newNet);
		        SVCLF[] statcomArray = {mySVC};
		        LFSolverWithSVC solver = new LFSolverWithSVC(newNet, statcomArray);
		        
		        // output loadflow calculation results
		        assertTrue(solver.solveLF());
		        
		        assertTrue(Math.abs(mySVC.getSsh(newNet).getReal()) < 0.0001);
		        
		        assertTrue(Math.abs(mySVC.getSsh(newNet).getImaginary() - (-0.1)) < 0.0001);
			}
		}
	}
	
	@Test
	public void testLFSolverWithSVCConstQ118() throws Exception {
		IpssFileAdapter adapter = PluginSpringCtx.getCustomFileAdapter("ieee");
		SimuContext simuCtx = adapter.load("testData/ieee_cdf/ieee118.ieee");
		AclfNetwork net = simuCtx.getAclfNet();
		for (Bus thisBus : net.getBusList()) {
			// Test SVC on load bus
			String thisID = thisBus.getId();
			if ((net.getAclfBus(thisID).getGenCode() != AclfGenCode.SWING) && (net.getAclfBus(thisID).getGenCode() != AclfGenCode.GEN_PV)) {
				IpssFileAdapter newAdapter = PluginSpringCtx.getCustomFileAdapter("ieee");
				SimuContext newSimuCtx = newAdapter.load("testData/ieee_cdf/ieee118.ieee");
				AclfNetwork newNet = newSimuCtx.getAclfNet();
				System.out.println("Testing " + thisID);
		        SVCLF mySVC = new SVCLF(thisID, new Complex(0.0, -5.0), SVCControlType.ConstQ, 0.3, newNet);
		        SVCLF[] statcomArray = {mySVC};
		        LFSolverWithSVC solver = new LFSolverWithSVC(newNet, statcomArray);
		        
		        // output loadflow calculation results
		        assertTrue(solver.solveLF());
		        
		        assertTrue(Math.abs(mySVC.getSsh(newNet).getReal()) < 0.0001);
		        
		        assertTrue(Math.abs(mySVC.getSsh(newNet).getImaginary() - (0.3)) < 0.0001);
			}
		}
	}
	
	@Test
	public void testLFSolverWithSVCConstB57() throws Exception {
		IpssFileAdapter adapter = PluginSpringCtx.getCustomFileAdapter("ieee");
		SimuContext simuCtx = adapter.load("testData/ieee_cdf/ieee57.ieee");
		AclfNetwork net = simuCtx.getAclfNet();
		for (Bus thisBus : net.getBusList()) {
			// Test SVC on load bus
			String thisID = thisBus.getId();
			if ((net.getAclfBus(thisID).getGenCode() != AclfGenCode.SWING) && (net.getAclfBus(thisID).getGenCode() != AclfGenCode.GEN_PV)) {
				IpssFileAdapter newAdapter = PluginSpringCtx.getCustomFileAdapter("ieee");
				SimuContext newSimuCtx = newAdapter.load("testData/ieee_cdf/ieee57.ieee");
				AclfNetwork newNet = newSimuCtx.getAclfNet();
				System.out.println("Testing " + thisID);
		        SVCLF mySVC = new SVCLF(thisID, new Complex(0.0, -5.0), SVCControlType.ConstB, 0.1, newNet);
		        SVCLF[] statcomArray = {mySVC};
		        LFSolverWithSVC solver = new LFSolverWithSVC(newNet, statcomArray);
		        
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
		IpssFileAdapter adapter = PluginSpringCtx.getCustomFileAdapter("ieee");
		SimuContext simuCtx = adapter.load("testData/ieee_cdf/ieee118.ieee");
		AclfNetwork net = simuCtx.getAclfNet();
		for (Bus thisBus : net.getBusList()) {
			// Test SVC on load bus
			String thisID = thisBus.getId();
			if ((net.getAclfBus(thisID).getGenCode() != AclfGenCode.SWING) && (net.getAclfBus(thisID).getGenCode() != AclfGenCode.GEN_PV)) {
				IpssFileAdapter newAdapter = PluginSpringCtx.getCustomFileAdapter("ieee");
				SimuContext newSimuCtx = newAdapter.load("testData/ieee_cdf/ieee118.ieee");
				AclfNetwork newNet = newSimuCtx.getAclfNet();
				System.out.println("Testing " + thisID);
		        SVCLF mySVC = new SVCLF(thisID, new Complex(0.0, -5.0), SVCControlType.ConstB, 0.1, newNet);
		        SVCLF[] statcomArray = {mySVC};
		        LFSolverWithSVC solver = new LFSolverWithSVC(newNet, statcomArray);
		        
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
        SwingBusAdapter swingBus = bus1.toSwingBus();
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