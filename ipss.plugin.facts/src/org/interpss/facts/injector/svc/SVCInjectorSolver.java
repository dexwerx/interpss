package org.interpss.facts.injector.svc;

import java.util.HashMap;

import org.apache.commons.math.complex.Complex;

import com.interpss.CoreObjectFactory;
import com.interpss.common.exp.InterpssException;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;

// Solve load flow with SVC
public class SVCInjectorSolver {

	private AclfNetwork net;
	private SVCInjectorLF[] svcArray = null;
	private HashMap<SVCInjectorLF, Complex> svcLoad;

	private boolean maxBViolated = false;
	private boolean minBViolated = false;
	
	// Constructor
	public SVCInjectorSolver(AclfNetwork net, SVCInjectorLF[] svcArray) {
		super();
		this.net = net;
		this.svcArray = svcArray;
		this.svcLoad = new HashMap<SVCInjectorLF, Complex>();
		for (SVCInjectorLF thisSVC : svcArray) {
			String thisID = thisSVC.getId();
			Complex thisLoad = net.getAclfBus(thisID).getLoad();
			this.svcLoad.put(thisSVC, thisLoad);
		}
	}
	
	public boolean isMaxBViolated() {
		return maxBViolated;
	}

	public boolean isMinBViolated() {
		return minBViolated;
	}

	// Solve the load flow
	public boolean solveLF() throws InterpssException {
		boolean converged = false;
		int i = 0;
		while (!converged) {
			// 1. Update all the SVCs
    		double err = 0.0;
            for (SVCInjectorLF thisSVC : svcArray) {
            	thisSVC.update(net);	// Key point of the calculation
            	// Check possible violations
            	SVCSusceptanceViolation thisSVCBVio = new SVCSusceptanceViolation(thisSVC);
            	thisSVCBVio.checkViolate();
            	this.maxBViolated = thisSVCBVio.isMaxViolated();
            	this.minBViolated = thisSVCBVio.isMinViolated();
            	
            	System.out.println("Vi = " + net.getAclfBus(thisSVC.getId()).getVoltageMag() +  ", thetai = " + net.getAclfBus(thisSVC.getId()).getVoltageAng());
            	System.out.println("Vsh = " + thisSVC.getConverter().getVth().abs() + ", thetash = " + 
            			Math.atan2(thisSVC.getConverter().getVth().getImaginary(), thisSVC.getConverter().getVth().getReal()));
            }
            if (i++ > 100)
            	break;
			// 2. Update the network with current states of all the SVCs
			for (SVCInjectorLF thisSVC : svcArray) {
				String svcId = thisSVC.getId();
				double loadP = this.svcLoad.get(thisSVC).getReal() - thisSVC.getSsh(net).getReal();
				double loadQ = this.svcLoad.get(thisSVC).getImaginary() - thisSVC.getSsh(net).getImaginary();
				net.getAclfBus(svcId).setLoadP(loadP);
				net.getAclfBus(svcId).setLoadQ(loadQ);
			}
			// 3. Solve the traditional load flow with current injections
            LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
            algo.loadflow();
            // 4. Update the mismatch
            for (SVCInjectorLF thisSVC : svcArray) {
            	err = Math.max(err, thisSVC.getErr());
                if (err < 0.000001)
                	converged = true;
            }
		}
		return converged;
	}
	
}