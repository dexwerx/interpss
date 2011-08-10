package org.interpss.test.facts.simult.svc;

import static org.junit.Assert.*;

import org.apache.commons.math.complex.Complex;
import org.interpss.custom.IpssFileAdapter;
import org.interpss.facts.general.SVCControlType;
import org.interpss.facts.simult.svc.SVCSimultLF;
import org.interpss.facts.simult.svc.SVCSimultSolver;
import org.interpss.spring.PluginSpringCtx;
import org.interpss.test.DevTestSetup;
import org.junit.Test;

import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfGenCode;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.core.net.Bus;
import com.interpss.simu.SimuContext;

public class IEEE118SVCTest extends DevTestSetup {

	@Test
	public void testLFSolverWithSVCConstQIEEE118() throws Exception {
		IpssFileAdapter adapter = PluginSpringCtx.getCustomFileAdapter("ieee");
		SimuContext simuCtx = adapter.load("testData/ieee_cdf/ieee118.ieee");
		AclfNetwork net = simuCtx.getAclfNet();
		for (Bus thisBus : net.getBusList()) {
			// Test SVC on load bus
			String thisID = thisBus.getId();
			if ((net.getAclfBus(thisID).getGenCode() != AclfGenCode.SWING) && (net.getAclfBus(thisID).getGenCode() != AclfGenCode.GEN_PV)) {
//				thisID = "6";
				System.out.println("Testing " + thisID);
				IpssFileAdapter newAdapter = PluginSpringCtx.getCustomFileAdapter("ieee");
				SimuContext newSimuCtx = newAdapter.load("testData/ieee_cdf/ieee118.ieee");
				AclfNetwork newNet = newSimuCtx.getAclfNet();
				AclfBus bus = newNet.getAclfBus(thisID);
		        SVCSimultLF mySVC = new SVCSimultLF(bus, new Complex(0.0, -5.0), SVCControlType.ConstQ, 0.2, newNet.getNoBus(), -100.0, 100.0);
		        SVCSimultLF[] statcomArray = {mySVC};
		        SVCSimultSolver solver = new SVCSimultSolver(newNet, statcomArray);

		        // create a Loadflow algo object
		        LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm();
		        // set algo NR solver to the CustomNrSolver
		        algo.setNrSolver(solver);
		        algo.setTolerance(1.0E-7);
		        
		        // run Loadflow
		        boolean lfConverged = net.accept(algo);
		        
		        assertTrue(lfConverged);
		        double vi = newNet.getAclfBus("Bus2").getVoltageMag();
		        double thetai = newNet.getAclfBus("Bus2").getVoltageAng();
		        
		        System.out.println("Vi=" + vi + ", thetai=" + thetai);

		        double vsh = mySVC.getVsh();
		        double thetash = mySVC.getThetash();

		        System.out.println("Vsh=" + vsh + ", thetash=" + thetash);
		        
		        double gsh = mySVC.getConverter().getYth().getReal();
		        double bsh = mySVC.getConverter().getYth().getImaginary();
		        
		        double pe = vsh * vsh * gsh - vi * vsh * (gsh * Math.cos(thetai - thetash) - bsh * Math.sin(thetai - thetash));
		        double qsh = vi * vi * bsh + vi * vsh * (gsh * Math.sin(thetai - thetash) - bsh * Math.cos(thetai - thetash));
		        
		        assertTrue(Math.abs(pe) < 0.0001);
		        assertTrue(Math.abs(qsh / vi / vi - mySVC.getTunedValue()) < 0.0001);
			}
		}
	}

}