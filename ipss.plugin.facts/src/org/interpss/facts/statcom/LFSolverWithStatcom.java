package org.interpss.facts.statcom;

import java.util.HashMap;

import org.apache.commons.math.complex.Complex;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;

// Solve load flow with STATCOM
public class LFSolverWithStatcom {

	private AclfNetwork net;
	private StatcomLF[] statcomArray = null;
	private HashMap<StatcomLF, Complex> statcomLoad;
	
	// Constructor
	public LFSolverWithStatcom(AclfNetwork net, StatcomLF[] statcomArray) {
		super();
		this.net = net;
		this.statcomArray = statcomArray;
		this.statcomLoad = new HashMap<StatcomLF, Complex>();
		for (StatcomLF thisSTATCOM : statcomArray) {
			String thisID = thisSTATCOM.getId();
			Complex thisLoad = net.getAclfBus(thisID).getLoad();
			this.statcomLoad.put(thisSTATCOM, thisLoad);
		}
	}
	
	// Solve the load flow
	public boolean solveLF() throws InterpssException {
		boolean converged = false;
		int i = 0;
		while (!converged) {
			// 1. Update all the STATCOMs
    		double err = 0.0;
            for (StatcomLF thisSTATCOM : statcomArray) {
            	System.out.println("Vi = " + net.getAclfBus(thisSTATCOM.getId()).getVoltageMag() +  ", thetai = " + net.getAclfBus(thisSTATCOM.getId()).getVoltageAng());
            	thisSTATCOM.update(net);	// Key point of the calculation
            	err = Math.max(err, thisSTATCOM.getErr());
            	System.out.println("Vsh = " + thisSTATCOM.getConverter().getVsh().abs() + ", thetash = " + 
            			Math.atan2(thisSTATCOM.getConverter().getVsh().getImaginary(), thisSTATCOM.getConverter().getVsh().getReal()));
            }
            if (err < 0.000001)
            	converged = true;
            if (i++ > 100)
            	break;
			// 2. Update the network with current states of all the STATCOMs
			for (StatcomLF thisSTATCOM : statcomArray) {
				String statcomId = thisSTATCOM.getId();
				double loadP = this.statcomLoad.get(thisSTATCOM).getReal() - thisSTATCOM.getSsh(net).getReal();
				double loadQ = this.statcomLoad.get(thisSTATCOM).getImaginary() - thisSTATCOM.getSsh(net).getImaginary();
				net.getAclfBus(statcomId).setLoadP(loadP);
				net.getAclfBus(statcomId).setLoadQ(loadQ);
			}
			// 3. Solve the traditional load flow with current injections
            LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
            net.accept(algo);
		}
		return converged;
	}
	
}