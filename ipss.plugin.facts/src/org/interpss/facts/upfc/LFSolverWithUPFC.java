package org.interpss.facts.upfc;

import java.util.HashMap;

import org.apache.commons.math.complex.Complex;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;

// Solve load flow with SVC
public class LFSolverWithUPFC {

	private AclfNetwork net;
	private UPFCLF[] upfcArray = null;
	private HashMap<UPFCLF, Complex> upfcLoadi;
	private HashMap<UPFCLF, Complex> upfcLoadj;

	// Constructor
	public LFSolverWithUPFC(AclfNetwork net, UPFCLF[] upfcArray) {
		super();
		this.net = net;
		this.upfcArray = upfcArray;
		this.upfcLoadi = new HashMap<UPFCLF, Complex>();
		this.upfcLoadj = new HashMap<UPFCLF, Complex>();
		for (UPFCLF thisUPFC : upfcArray) {
			String thisIDi = thisUPFC.getIdi();
			Complex thisLoadi = net.getAclfBus(thisIDi).getLoad();
			this.upfcLoadi.put(thisUPFC, thisLoadi);
			String thisIDj = thisUPFC.getIdj();
			Complex thisLoadj = net.getAclfBus(thisIDj).getLoad();
			this.upfcLoadj.put(thisUPFC, thisLoadj);
		}
	}
	
	// Solve the load flow
	public boolean solveLF() throws InterpssException {
		boolean converged = false;
		int i = 0;
		while (!converged) {
			// 1. Update all the UPFCs
    		double err = 0.0;
            for (UPFCLF thisUPFC : upfcArray) {
            	thisUPFC.update(net);	// Key point of the calculation
            	// Check possible violations
            	
            	System.out.println("Vi = " + net.getAclfBus(thisUPFC.getIdi()).getVoltageMag() +  ", thetai = " + net.getAclfBus(thisUPFC.getIdi()).getVoltageAng() + 
            			", Vj = " + net.getAclfBus(thisUPFC.getIdj()).getVoltageMag() +  ", thetaj = " + net.getAclfBus(thisUPFC.getIdi()).getVoltageAng());
            	System.out.println("Vsh = " + thisUPFC.getShuntConverter().getVth().abs() + ", thetash = " + 
            			Math.atan2(thisUPFC.getShuntConverter().getVth().getImaginary(), thisUPFC.getShuntConverter().getVth().getReal()));
            	System.out.println("Vse = " + thisUPFC.getSerialConverter().getVth().abs() + ", thetase = " + 
            			Math.atan2(thisUPFC.getSerialConverter().getVth().getImaginary(), thisUPFC.getSerialConverter().getVth().getReal()));
            }
            if (i++ > 100)
            	break;
			// 2. Update the network with current states of all the UPFCs
			for (UPFCLF thisUPFC : upfcArray) {
				// 2.1. Update bus i
				String upfcIdi = thisUPFC.getIdi();
				double loadPi = this.upfcLoadi.get(thisUPFC).getReal() + thisUPFC.getSsh(net).getReal() + thisUPFC.getSerialSij(net).getReal();
				double loadQi = this.upfcLoadi.get(thisUPFC).getImaginary() + thisUPFC.getSsh(net).getImaginary() + thisUPFC.getSerialSij(net).getImaginary();
				net.getAclfBus(upfcIdi).setLoadP(loadPi);
				net.getAclfBus(upfcIdi).setLoadQ(loadQi);
				// 2.2. Update bus j
				String upfcIdj = thisUPFC.getIdj();
				double loadPj = this.upfcLoadj.get(thisUPFC).getReal() + thisUPFC.getSerialSji(net).getReal();
				double loadQj = this.upfcLoadj.get(thisUPFC).getImaginary() + thisUPFC.getSerialSji(net).getImaginary();
				net.getAclfBus(upfcIdj).setLoadP(loadPj);
				net.getAclfBus(upfcIdj).setLoadQ(loadQj);
			}
			// 3. Solve the traditional load flow with current injections
            LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
            algo.loadflow();
            // 4. Update the mismatch
            for (UPFCLF thisUPFC : upfcArray) {
            	err = Math.max(err, thisUPFC.getErr());
                if (err < 0.000001)
                	converged = true;
            }
		}
		return converged;
	}
	
}