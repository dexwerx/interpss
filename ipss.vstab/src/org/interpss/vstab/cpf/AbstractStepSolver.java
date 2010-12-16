package org.interpss.vstab.cpf;

import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.sparse.SparseEqnMatrix2x2;

public abstract class  AbstractStepSolver {
	protected AclfNetwork net=null;
	protected int sortNumofContParam=0;
	protected SparseEqnMatrix2x2 augmentedJacobi=null;
	protected double DEFAULT_CPFSOLVER_TOLEARNCE=9.95e-21;
	protected double tolerance=DEFAULT_CPFSOLVER_TOLEARNCE;
	

	
	public double getTolerance() {
		return tolerance;
	}
	public void setTolerance(double tolerance) {
		this.tolerance = tolerance;
	}

	public int getSortNumofContParam() {
		return sortNumofContParam;
	}
	public void setSortNumofContParam(int sortNumofContParam) {
		this.sortNumofContParam = sortNumofContParam;
	}

	public SparseEqnMatrix2x2 getAugmentedJacobi() {
		return augmentedJacobi;
	}
	public void setAugmentedJacobi(SparseEqnMatrix2x2 augmentedLfEqn) {
		this.augmentedJacobi = augmentedLfEqn;
	}
	
	public abstract boolean stepSolver();
	
	public abstract void updateBusVoltage();

}
