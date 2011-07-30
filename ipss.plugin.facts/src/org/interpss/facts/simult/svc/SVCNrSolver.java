package org.interpss.facts.simult.svc;

import org.interpss.numeric.sparse.SparseEqnMatrix2x2;

import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.impl.DefaultNrSolver;

public class SVCNrSolver extends DefaultNrSolver {

	private SVCLF[] svcAry = null;
	
    public SVCNrSolver(AclfNetwork net, SVCLF[] svcAry) {
        super(net);
        this.svcAry = svcAry;
    }
    
    @Override
    public SparseEqnMatrix2x2 formJMatrix() {
    	int svcLength = 0;
    	if (svcAry != null)
    		svcLength = svcAry.length;
        SparseEqnMatrix2x2 lfEqn = this.getAclfNet().formJMatrix(svcLength);

        if (svcAry != null) {
            for (SVCLF svc : svcAry) {
                int i = svc.getBus().getSortNumber();
                int n = svc.getPosition();
                
                lfEqn.setA(svc.getJnn(), n, n);
                lfEqn.setA(svc.getJni(), n, i);
                lfEqn.setA(svc.getJin(), i, n);
                lfEqn.addToA(svc.getJii(), i, i);
            }
        }

        return lfEqn;
    }
    
    @Override
    public void setPowerMismatch(SparseEqnMatrix2x2 lfEqn) {
        super.setPowerMismatch(lfEqn);

        if (svcAry != null) {
            for (SVCLF svc : svcAry) {
                // bi is already added into the mismatch() method
                //int i = svc.getBus().getSortNumber();
                //Vector_xy bi = svc.getBi();
                //lfEqn.addToBi(bi,i);

                int n = svc.getPosition();
                lfEqn.setB(svc.getBn(), n);
            }
        }
    }
    
    @Override
    public void updateBusVoltage(SparseEqnMatrix2x2 lfEqn) {
        super.updateBusVoltage(lfEqn);

        if (svcAry != null) {
            for (SVCLF svc : svcAry) 
            	svc.update(lfEqn);
        }
    }
}