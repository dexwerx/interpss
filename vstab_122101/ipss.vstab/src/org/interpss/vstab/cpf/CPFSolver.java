package org.interpss.vstab.cpf;

import org.interpss.vstab.cpf.impl.CorrectorStepSolver;
import org.interpss.vstab.cpf.impl.LambdaParam;
import org.interpss.vstab.cpf.impl.PredictorStepSolver;


public interface CPFSolver {

	public boolean solveCPF();
	
	public boolean isCPFConverged();
	public void setCpfConverged(boolean convg);
	
	public boolean predictorStep();
	
	public boolean correctorStep();

	public LambdaParam getLambda();
	
	public CorrectorStepSolver getCorrStepSolver();
	public PredictorStepSolver getPredStepSolver();
}
