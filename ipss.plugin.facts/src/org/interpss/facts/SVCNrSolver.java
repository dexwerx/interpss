package org.interpss.facts;

import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algorithm.impl.DefaultNrSolver;
import com.interpss.core.sparse.dep.SparseEqnMatrix2x2;

public class SVCNrSolver extends DefaultNrSolver {

	private SVCControl[] svcAry = null;
	
    public SVCNrSolver(AclfNetwork net, SVCControl[] svcAry) {
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
            for (SVCControl svc : svcAry) {
                int i = svc.getBus().getSortNumber();
                int n = svc.getPosition();
                
                lfEqn.setAij(svc.getJnn(), n, n);
                lfEqn.setAij(svc.getJni(), n, i);
                lfEqn.setAij(svc.getJin(), i, n);
                lfEqn.addToAij(svc.getJii(), i, i);
            }
        }

        return lfEqn;
    }
    
    @Override
    public void setPowerMismatch(SparseEqnMatrix2x2 lfEqn) {
        super.setPowerMismatch(lfEqn);

        if (svcAry != null) {
            for (SVCControl svc : svcAry) {
                // bi is already added into the mismatch() method
                //int i = svc.getBus().getSortNumber();
                //Vector_xy bi = svc.getBi();
                //lfEqn.addToBi(bi,i);

                int n = svc.getPosition();
                lfEqn.setBi(svc.getBn(), n);
            }
        }
    }
    
    @Override
    public void updateBusVoltage(SparseEqnMatrix2x2 lfEqn) {
        super.updateBusVoltage(lfEqn);

        if (svcAry != null) {
            for (SVCControl svc : svcAry) 
            	svc.update(lfEqn);
        }
    }
}