package org.interpss.vstab.cpf.impl;

import org.interpss.vstab.cpf.CPFAlgorithm;
import org.interpss.vstab.cpf.CPFSolver;
import org.interpss.vstab.cpf.CpfStopCriteria.AnalysisStopCriteria;

import com.interpss.core.CoreObjectFactory;
import com.interpss.core.algorithm.LoadflowAlgorithm;

/*
 *  all //** are by Mike for code refactor
 */

public class CPFSolverImpl implements CPFSolver{
    //private AclfNetwork net=null;
    //** private IPSSMsgHub msg=null;
	/*
	 * Mike : we would like to get rid of IPSSMsgHub concept. use IpssLogger instead
	 */
//    protected LoadIncPattern _loadIncPtn=null;
//    protected GenDispPattern _genDispPtn=null;
    private boolean cpfConverged=false;
    private boolean isCpfStop=false;
	private final int DEFAULT_CONT_PARA_SORTNUM=0;
	private int contParaSortNum=DEFAULT_CONT_PARA_SORTNUM;
	private double fixedValueOfContPara=0;

	CPFAlgorithm cpfAlgo=null;
	private LambdaParam lambda=null;
    private CorrectorStepSolver corrStepSolver;
    private PredictorStepSolver predStepSolver;
    
	public CPFSolverImpl() {
		
	}
	public CPFSolverImpl(CPFAlgorithm cpfAlgo, LambdaParam lambda) {
	// public CPFSolverImpl(CPFAlgorithm cpf, IPSSMsgHub msg) {
		this.cpfAlgo= cpfAlgo;
		this.lambda=lambda;
    	this.predStepSolver = new PredictorStepSolver(cpfAlgo, lambda);
    	this.corrStepSolver = new CorrectorStepSolver(cpfAlgo, lambda);
	}

	@Override
	public boolean isCPFConverged() {
		
		return cpfConverged;
	}
	public void setCpfConverged(boolean convg) {
		this.cpfConverged=convg;
	}

	@Override
	public boolean solveCPF() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean correctorStep() {
		LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm();
		algo.setNrSolver(corrStepSolver);
		if(!this.cpfAlgo.getAclfNetwork().accept(algo)) {
			return false;
		}
		return true;
	       

	}
	@Override
	public boolean predictorStep() {
		
		return true;
	}
	
	private boolean isCpfStop() {
		if(this.cpfAlgo.getAnalysisStopCriteria()==AnalysisStopCriteria.FULL_CUREVE) {
			if(this.lambda.getValue()<0.1) 
				return this.isCpfStop=true;
		}
		else if(this.cpfAlgo.getAnalysisStopCriteria()==AnalysisStopCriteria.MAX_POWER_POINT) {
			if(this.predStepSolver.isCrossMPP) 
				return isCpfStop=true;
		}
		else{ 
			if(cpfAlgo.isAnyViolation()) 
				return isCpfStop=true;
		}
		return isCpfStop=false;
		
	}

	
	public LambdaParam getLambda() {
		return this.lambda;
	}
	
	public CorrectorStepSolver getCorrStepSolver() {
		return this.corrStepSolver;
	}
	public PredictorStepSolver getPredStepSolver() {
		return this.predStepSolver;
	}

}
