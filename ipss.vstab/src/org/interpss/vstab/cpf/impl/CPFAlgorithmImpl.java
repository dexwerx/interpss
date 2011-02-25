package org.interpss.vstab.cpf.impl;

import org.interpss.vstab.cpf.CPFAlgorithm;
import org.interpss.vstab.cpf.CPFSolver;
import org.interpss.vstab.cpf.CpfStopCriteria.AnalysisStopCriteria;
import org.interpss.vstab.cpf.GenDispPattern.GenDispPtn;

import com.interpss.common.util.IpssLogger;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.impl.LoadflowAlgorithmImpl;
import com.interpss.core.common.visitor.IAclfBranchVisitor;
import com.interpss.core.common.visitor.IAclfBusVisitor;
import com.interpss.core.net.Branch;
import com.interpss.core.net.Bus;
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
    private int maxCPFIterations=DEFAULT_CPF_MAX_ITERATIONS;
    
    public final int DEFAULT_PF_MAX_ITERATIONS=10;
    private int maxPFIterations=DEFAULT_PF_MAX_ITERATIONS;

    public final double DEFAULT_STEP_SIZE=0.05;  // deault step size;
    private double stepSize=0.05;
    protected AnalysisStopCriteria stopCriteria=null;
    
    protected GenDispatch genDispatch=null;
    protected LoadIncrease ldInc=null;
    protected CPFSolver cpfSolver=null;
	private CpfHelper cpfHelper=null;
    private boolean disableAllViolationCheck=false;
    private boolean disableBraMVAViolChk=false;
    private boolean disableBusVViolChk=false;
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
        if(!this.disableAllViolationCheck){
        	IpssLogger.getLogger().info("All violation check are disabled.");
        	return false;
        }
        if(!this.disableBraMVAViolChk){
	      for(Branch bra:this.getAclfNetwork().getBranchList()){
	    	AclfBranch acBra=(AclfBranch) bra;
				if(acBra.powerFrom2To().abs()>acBra.getRatingMva1()
						||acBra.powerTo2From().abs()>acBra.getRatingMva1()){// what is the unit of PowerFrom2To
					IpssLogger.getLogger().severe("Branch#"+acBra.getId()+"voilate MVArating," +
							" with powerflow, MVARating:"+acBra.powerFrom2To().abs()+","+acBra.getRatingMva1());
					return true;
				}
	      }
        }
        
	    if(!this.disableBusVViolChk){
	      for(Bus bus:this.getAclfNetwork().getBusList()){
	    	AclfBus acBus=(AclfBus) bus;
				if(acBus.getVLimit().isViolated(acBus.getVoltageMag())){// what is the unit of PowerFrom2To
					IpssLogger.getLogger().severe("Bus#"+acBus.getId()+"voilates voltage limit," +
							" with bus Vmag, Vlmit:"+acBus.getVoltageMag()+","+acBus.getVLimit().toString());
					return true;
				}
			}
	      
		   
	    }
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
	public void disableAllViolationCheck(boolean booleanParam){
		this.disableAllViolationCheck=booleanParam;
	}
	public void disableBraMVAViolChk(boolean booleanParam){
    	this.disableBraMVAViolChk=booleanParam;
    }
	public void disableBusVViolChk(boolean booleanParam){
    	this.disableBusVViolChk=booleanParam;
    }

	@Override
	public int getCPFMaxInteration() {
		return this.maxCPFIterations;
	}

	@Override
	public int getPfMaxInteration() {
		
		return this.maxPFIterations;
	}

	@Override
	public void setCPFMaxInteration(int maxCPFItr) {
		this.maxCPFIterations=maxCPFItr;
		
	}

	@Override
	public void setPfMaxInteration(int maxPowerflowItr) {
		this.maxPFIterations=maxPowerflowItr;
		
	}





}
