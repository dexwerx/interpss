package org.interpss.vstab.cpf.impl;

import org.interpss.numeric.datatype.LimitType;
import org.interpss.vstab.cpf.CPFAlgorithm;
import org.interpss.vstab.cpf.CPFSolver;
import org.interpss.vstab.cpf.CpfStopCriteria.AnalysisStopCriteria;

import com.interpss.common.util.IpssLogger;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.impl.LoadflowAlgorithmImpl;
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
    
	public static final double DEFAULT_MAX_STEP_SIZE=0.05;// to control the max step increase/decrease
    protected double maxStepSize=DEFAULT_MAX_STEP_SIZE;
    public static final double DEFAULT_MAX_DELTA_LAMBDA=0.05;
    protected double maxDeltaLambda=DEFAULT_MAX_DELTA_LAMBDA;
    public static final double DEFAULT_MIN_STEP_SIZE=1e-5;// to control the max step increase/decrease
    protected double minStepSize=DEFAULT_MIN_STEP_SIZE;
    public final double DEFAULT_PF_TOLEARANCE=1e-3;
   
    private double PflowTolerance=DEFAULT_PF_TOLEARANCE;
    public final int DEFAULT_CPF_MAX_ITERATIONS=50;
    private int maxCPFIterations=DEFAULT_CPF_MAX_ITERATIONS;
    
    public final int DEFAULT_PF_MAX_ITERATIONS=10;
    private int maxPFIterations=DEFAULT_PF_MAX_ITERATIONS;

    public final double DEFAULT_STEP_SIZE=0.05;  // deault step size;
    private double stepSize=DEFAULT_STEP_SIZE;
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
//		this.cpfHelper=new CpfHelper(this);
        this.ldInc=loadInc;
        this.genDispatch=genDisp;
        this.cpfSolver=new CPFSolverImpl(this,lambda);
        
    }


	@Override
	public boolean runCPF() {
		if(!this.getAclfNetwork().isDataChecked()){
			this.checkDataForCPF();
		}
	      if ((!(getAclfNetwork().checkData())) && 
	    	     (!(getAclfNetwork().isBypassDataCheck()))) {
	    	      IpssLogger.getLogger().severe("Data checking error, exit CPF analysis");
	    	      return false;
	      }
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

	public double getMaxDeltaLambda() {
		return maxDeltaLambda;
	}

	public void setMaxDeltaLambda(double maxDeltaLambda) {
		this.maxDeltaLambda = maxDeltaLambda;
	}

	@Override
	public boolean checkDataForCPF() {
		boolean pass=super.checkData();
		if(!checkGenPLimitData()) pass=false;
		if(!checkLoadInceaseData()) pass=false;
		if(!checkGenDispData()) pass=false;
		return pass;
		
	}
	private boolean checkGenPLimitData(){
		boolean pass=true;
		for(int i=0;i<this.getAclfNetwork().getBusList().size();i++){
			AclfBus bus=(AclfBus)this.getAclfNetwork().getBusList().get(i);
			if(bus.isGenPV()){// only PV bus is considered here
				
				if(bus.getPGenLimit()==null){
	
					IpssLogger.getLogger().severe("There is no PGenLimit defined in bus:"+bus.getId()
							+" ,assume +/-20% reservation in the following process.");
					bus.setPGenLimit(new LimitType(bus.getGenP()*1.2,bus.getGenP()*0.8)); // assume there is 20% generation reservation by default;
				   pass=false;
				}
			}
			
		}
		return pass;
	}
	
	private boolean checkLoadInceaseData(){
		boolean pass=true;
		if(this.ldInc.getPattern().getLoadIncDir()==null) {
			IpssLogger.getLogger().severe("Load Increase direction data is NOT found! Please check");
			pass=false;
		}
		return pass;
	}
	
	private boolean checkGenDispData(){
		boolean pass=true;
		if(this.genDispatch.getPattern().getGenDirection()==null) {
			IpssLogger.getLogger().severe("Gen re-dispatching direction data is NOT found! Please check");
			pass=false;
		}
		return pass;
	}





}
