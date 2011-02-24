package org.interpss.vstab.cpf.impl;

import org.interpss.vstab.cpf.CPFAlgorithm;
import org.interpss.vstab.cpf.CPFSolver;
import org.interpss.vstab.cpf.CpfStopCriteria.AnalysisStopCriteria;
import org.interpss.vstab.cpf.GenDispPattern.GenDispPtn;

import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.impl.LoadflowAlgorithmImpl;
import com.interpss.core.common.visitor.IAclfBranchVisitor;
import com.interpss.core.common.visitor.IAclfBusVisitor;
/**
 * an implementation of the interface--CPFAlgorithm, since CPFAlgorithm is extended from LoadflowAlgorithm,
 * correspondingly, it extends LoadflowAlgorithmImpl here, thus having the methods defined in LoadflowAlgorithmImpl.
 * 
 * @author Tony Huang
 *
 */

public class CPFAlgorithmImpl extends LoadflowAlgorithmImpl implements CPFAlgorithm {
    
	public final double DEFAULT_MAX_STEP_SIZE=0.1;// to control the max step increase/decrease
    protected double maxStepSize=DEFAULT_MAX_STEP_SIZE;
    public final double DEFAULT_MIN_STEP_SIZE=0.001;// to control the max step increase/decrease
    protected double minStepSize=DEFAULT_MIN_STEP_SIZE;
    public final double DEFAULT_PF_TOLEARANCE=1e-3;
   
    private double PflowTolerance=DEFAULT_PF_TOLEARANCE;
    public final int DEFAULT_CPF_MAX_ITERATIONS=50;
    private int maxIterations=DEFAULT_CPF_MAX_ITERATIONS;

    public final double DEFAULT_STEP_SIZE=0.05;  // deault step size;
    private double stepSize=0.05;
    protected AnalysisStopCriteria stopCriteria=null;
    
    protected GenDispatch genDispatch=null;
    protected LoadIncrease ldInc=null;
    protected CPFSolver cpfSolver=null;
	private CpfHelper cpfHelper=null;
	private boolean violation=false;
    
    public CPFAlgorithmImpl (AclfNetwork net, LambdaParam lambda,LoadIncrease loadInc,GenDispatch genDisp) {
    	this.setAclfNetwork(net);
		this.cpfHelper=new CpfHelper(net,loadInc.getPattern());
        this.ldInc=loadInc;
        this.cpfSolver=new CPFSolverImpl(this,lambda);
        this.genDispatch=genDisp;
    }
    
	@Override
	public int getMaxIterations() {
		
		return this.maxIterations;
	}


	@Override
	public boolean runCPF() {
		return cpfSolver.solveCPF();
		
	}

	@Override
	public void setAnalysisStopCriteria(AnalysisStopCriteria newStopCriteria) {
		this.stopCriteria=newStopCriteria;
		
	}


	@Override
	public void setMaxIterations(int maxIter) {
		this.maxIterations=maxIter;
		
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
	public CPFSolver getCpfSolver() {
		
		return this.cpfSolver;
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

	@Override
	public void setCpfHelper(CpfHelper cpfHelper) {
		this.cpfHelper = cpfHelper;
	}
  
	@Override
	public CpfHelper getCpfHelper() {
		return cpfHelper;
	}



	public void setMaxStepSize(double maxStepSize) {
		this.maxStepSize = maxStepSize;
	}



	public double getMaxStepSize() {
		return maxStepSize;
	}



	public void setStepSize(double stepSize) {
		this.stepSize = stepSize;
	}



	public double getStepSize() {
		return stepSize;
	}



	@Override
	public double getMinStepSize() {
		
		return this.minStepSize;
	}



	@Override
	public void setMinStepSize(double minStepSize) {
		this.minStepSize=minStepSize;
		
	}


	public void setPflowTolerance(double pflowTolerance) {
		PflowTolerance = pflowTolerance;
	}

	public double getPflowTolerance() {
		return PflowTolerance;
	}

	@Override
	public String[] getDisplayPQBus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDisplayPQBus(String[] busID) {
		// TODO Auto-generated method stub
		
	}
	





}
