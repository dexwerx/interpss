package org.interpss.vstab.cpf.impl;

import org.interpss.vstab.cpf.CPFAlgorithm;
import org.interpss.vstab.cpf.CPFSolver;
import org.interpss.vstab.cpf.GenDispPattern;
import org.interpss.vstab.cpf.LoadIncPattern;
import org.interpss.vstab.cpf.CpfStopCriteria.AnalysisStopCriteria;

import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algorithm.impl.LoadflowAlgorithmImpl;
import com.interpss.core.common.visitor.IAclfBranchVisitor;
import com.interpss.core.common.visitor.IAclfBusVisitor;
import com.interpss.core.common.visitor.IAclfNetBVisitor;

public class CPFAlgorithmImpl extends LoadflowAlgorithmImpl implements CPFAlgorithm {

//    protected LambdaParam lambda=null;
    protected AnalysisStopCriteria stopCriteria=null;
    protected GenDispatch genDispatch=null;
    protected LoadIncrease ldInc=null;
    protected CPFSolver cpfSolver=null;
	protected int sortNumOfContPara=0; // used in whole process
    protected boolean violation=false;
    protected double DEFAULT_CPF_TOLEARANCE=1e-3;
    protected double tolerance;
    protected int maxInterations;
    protected double fixedValOfContParam=0;
    
    public CPFAlgorithmImpl (AclfNetwork net, LambdaParam lambda) {
    	this.setAclfNetwork(net);
    	//this.lambda=lambda;
    	this.sortNumOfContPara=lambda.getPosition();// by default;
    	this.fixedValOfContParam=lambda.getValue(); // by default;
        this.cpfSolver=new CPFSolverImpl(this,lambda);
    }
    
    

	@Override
	public int getMaxIterations() {
		
		return this.maxInterations;
	}


	@Override
	public boolean runCPF() {
		cpfSolver.solveCPF();
		return cpfSolver.isCPFConverged();
	}

	@Override
	public void setAnalysisStopCriteria(AnalysisStopCriteria newStopCriteria) {
		this.stopCriteria=newStopCriteria;
		
	}


	@Override
	public void setMaxIterations(int maxIter) {
		this.maxInterations=maxIter;
		
	}

	@Override
	public AnalysisStopCriteria getAnalysisStopCriteria() {
		return this.stopCriteria;
	}

	@Override
	public boolean isAnyViolation() {
	         
		
		this.getAclfNetwork().forEachAclfBranch(new IAclfBranchVisitor() {

			@Override
			public void visit(AclfBranch bra) {
				// to do judgment of any violation in any branch
				
			}
			
		});
		this.getAclfNetwork().forEachAclfBus(new IAclfBusVisitor() {

			@Override
			public void visit(AclfBus bus) {
				// to do judgment of any violation in any bus
				
			}
			
		});
		return false;
		
	}

	@Override
	public int getSortNumOfContParam() {
		
		return this.sortNumOfContPara;
	}


	@Override
	public void setSorNumofContParam(int sortNum) {
		this.sortNumOfContPara=sortNum;
		
	}

	@Override
	public CPFSolver getCpfSolver() {
		
		return this.cpfSolver;
	}



	@Override
	public boolean isLmdaContParam() {
		return this.getSortNumOfContParam()>this.getAclfNetwork().getNoBus();
	}



	@Override
	public LoadIncrease getLoadIncrease() {
		
		return this.ldInc;
	}

	@Override
	public void setLoadIncrease(LoadIncrease ldInc) {
	  this.ldInc=ldInc;
		
	}



	@Override
	public GenDispatch getGenDispatch() {
		return this.genDispatch;
	}



	@Override
	public void setGenDispatch(GenDispatch genDispatch) {
		this.genDispatch=genDispatch;
		
	}



	@Override
	public boolean visit(AclfNetwork net) {
		this.setAclfNetwork(net);
		return runCPF();
		
	}




}
