package org.interpss.facts.statcom;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfNetwork;

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
		while (!converged) {
			AclfNetwork tempNetwork = CoreObjectFactory.createAclfNetwork(net.serialize());
			// Update the network with current states of all the STATCOMs
			for (StatcomLF thisSTATCOM : statcomArray) {
				String statcomId = thisSTATCOM.getId();
				double loadP = tempNetwork.getAclfBus(statcomId).getLoadP() + thisSTATCOM.getSsh().getReal();
				double loadQ = tempNetwork.getAclfBus(statcomId).getLoadQ() + thisSTATCOM.getSsh().getImaginary();
				tempNetwork.getAclfBus(statcomId).setLoadP(loadP);
				tempNetwork.getAclfBus(statcomId).setLoadQ(loadQ);
			}
			// Solve the traditional load flow with current injections
			// Update all the STATCOMs
		}
		return converged;
	}
	
}