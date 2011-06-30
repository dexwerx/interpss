package org.interpss.facts.statcom;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;

// Solve load flow with STATCOM
public class LFSolverWithStatcom {

	private AclfNetwork net;
	private StatcomLF[] statcomArray = null;
	
	// Constructor
	public LFSolverWithStatcom(AclfNetwork net, StatcomLF[] statcomArray) {
		super();
		this.net = net;
		this.statcomArray = statcomArray;
	}
	
	// Solve the load flow
	public boolean solveLF() throws InterpssException {
		boolean converged = false;
		int i = 0;
		while (!converged) {
			AclfNetwork tempNetwork = CoreObjectFactory.createAclfNetwork(net.serialize());
			// Test
//			LoadflowAlgorithm prealgo = CoreObjectFactory.createLoadflowAlgorithm(tempNetwork);
			
			// 1. Update the network with current states of all the STATCOMs
			for (StatcomLF thisSTATCOM : statcomArray) {
				String statcomId = thisSTATCOM.getId();
				double loadP = tempNetwork.getAclfBus(statcomId).getLoadP() - thisSTATCOM.getSsh(tempNetwork).getReal();
				double loadQ = tempNetwork.getAclfBus(statcomId).getLoadQ() - thisSTATCOM.getSsh(tempNetwork).getImaginary();
				tempNetwork.getAclfBus(statcomId).setLoadP(loadP);
				tempNetwork.getAclfBus(statcomId).setLoadQ(loadQ);
			}
			// 2. Solve the traditional load flow with current injections
            LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(tempNetwork);
            tempNetwork.accept(algo);
//            algo.loadflow();
			// 3. Update all the STATCOMs
    		double err = 0.0;
            for (StatcomLF thisSTATCOM : statcomArray) {
            	System.out.println("Vi = " + tempNetwork.getAclfBus(thisSTATCOM.getId()).getVoltageMag() +  ", thetai = " + tempNetwork.getAclfBus(thisSTATCOM.getId()).getVoltageAng());
            	thisSTATCOM.update(tempNetwork);	// Key point of the calculation
            	err = Math.max(err, thisSTATCOM.getErr());
            	System.out.println("Vsh = " + thisSTATCOM.getConverter().getVsh().abs() + ", thetash = " + 
            			Math.atan2(thisSTATCOM.getConverter().getVsh().getImaginary(), thisSTATCOM.getConverter().getVsh().getReal()));
            }
            if (err < 0.0001)
            	converged = true;
            if (i++ > 50)
            	break;
		}
		return converged;
	}
	
}