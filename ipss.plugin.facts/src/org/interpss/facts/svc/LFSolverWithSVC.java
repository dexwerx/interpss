package org.interpss.facts.svc;

import java.util.HashMap;

import org.apache.commons.math.complex.Complex;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;

// Solve load flow with SVC
public class LFSolverWithSVC {

	private AclfNetwork net;
	private SVCLF[] svcArray = null;
	private HashMap<SVCLF, Complex> svcLoad;
	
	// Constructor
	public LFSolverWithSVC(AclfNetwork net, SVCLF[] svcArray) {
		super();
		this.net = net;
		this.svcArray = svcArray;
		this.svcLoad = new HashMap<SVCLF, Complex>();
		for (SVCLF thisSVC : svcArray) {
			String thisID = thisSVC.getId();
			Complex thisLoad = net.getAclfBus(thisID).getLoad();
			this.svcLoad.put(thisSVC, thisLoad);
		}
	}
	
	// Solve the load flow
	public boolean solveLF() throws InterpssException {
		boolean converged = false;
		int i = 0;
		while (!converged) {
			// 1. Update all the SVCs
    		double err = 0.0;
            for (SVCLF thisSVC : svcArray) {
            	System.out.println("Vi = " + net.getAclfBus(thisSVC.getId()).getVoltageMag() +  ", thetai = " + net.getAclfBus(thisSVC.getId()).getVoltageAng());
            	thisSVC.update(net);	// Key point of the calculation
            	err = Math.max(err, thisSVC.getErr());
            	System.out.println("Vsh = " + thisSVC.getConverter().getVsh().abs() + ", thetash = " + 
            			Math.atan2(thisSVC.getConverter().getVsh().getImaginary(), thisSVC.getConverter().getVsh().getReal()));
            }
            if (err < 0.000001)
            	converged = true;
            if (i++ > 100)
            	break;
			// 2. Update the network with current states of all the STATCOMs
			for (SVCLF thisSVC : svcArray) {
				String svcId = thisSVC.getId();
				double loadP = this.svcLoad.get(thisSVC).getReal() - thisSVC.getSsh(net).getReal();
				double loadQ = this.svcLoad.get(thisSVC).getImaginary() - thisSVC.getSsh(net).getImaginary();
				net.getAclfBus(svcId).setLoadP(loadP);
				net.getAclfBus(svcId).setLoadQ(loadQ);
			}
			// 3. Solve the traditional load flow with current injections
            LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
            net.accept(algo);
		}
		return converged;
	}
	
}