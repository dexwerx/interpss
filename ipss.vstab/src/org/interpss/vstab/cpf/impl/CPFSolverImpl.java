package org.interpss.vstab.cpf.impl;

import org.apache.commons.math.linear.ArrayRealVector;
import org.interpss.vstab.cpf.CPFAlgorithm;
import org.interpss.vstab.cpf.CPFSolver;
import org.interpss.vstab.cpf.CpfStopCriteria.AnalysisStopCriteria;

import com.interpss.common.util.IpssLogger;
import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.core.net.Bus;

public class CPFSolverImpl implements CPFSolver{


    private boolean isCpfStop=false;
	private LambdaParam lambda=null;
	private CPFAlgorithm cpfAlgo=null;
    private CorrectorStepSolver corrStepSolver;
    private PredictorStepSolver predStepSolver;
	private int sortNumOfContPara=0;
	private int iteration=0;
    private boolean lastLFConverged=true;
    
    private double[] convg_angle=null;
    private double[] convg_Vmag=null;
    private double  convg_lambda=0;
    
    
	public CPFSolverImpl(CPFAlgorithm cpf,LambdaParam newLambda) {
		this.cpfAlgo=cpf;
		this.lambda=newLambda;
		this.corrStepSolver=new CorrectorStepSolver(cpf);
		this.predStepSolver=new PredictorStepSolver(cpf);
		this.setSorNumofContParam(this.lambda.getPosition()); // by default;
	}


	@Override
	public boolean solveCPF() {

		this.iteration=0;
		lastLFConverged=true; // as a flag
		while(this.iteration<cpfAlgo.getCPFMaxInteration()){
			 IpssLogger.getLogger().info("CPF analysis Itr:"+this.iteration);
			/*
			 * change continuation parameter after three iterations 
			 * or last corrector step not converged.
			 */
		
			if(this.iteration>3||!this.lastLFConverged){
				 IpssLogger.getLogger().info("update continuation parameter, last sort Number is #"+this.getSortNumOfContParam()+",  now is #"+getNextStepContParam());
				this.setSorNumofContParam(getNextStepContParam()); 
			}
			
		  // run preStepSolver and update network buses' voltage with solved result;
			
		  this.predStepSolver.stepSolver();
	      /*
	       * output the deltaX for test
	       */
		  System.out.println("predictive deltaXL"+this.predStepSolver.getDeltaXLambda().toString());
		  
		  //perform corrector step analysis
		  LoadflowAlgorithm algo=CoreObjectFactory.createLoadflowAlgorithm();
		  algo.setTolerance(this.cpfAlgo.getPflowTolerance());
		  algo.setMaxIterations(this.cpfAlgo.getPfMaxInteration());
		  // corrector step solver is just a customized Newton-Raphson solver;
		  algo.setNrSolver(this.corrStepSolver);
		
		  if(!this.cpfAlgo.getAclfNetwork().accept(algo)){// if corrector step is not converged
			
			  if(this.cpfAlgo.getStepSize()<1e-3 && this.iteration>1){
				  IpssLogger.getLogger().severe("predictor step size ="+this.cpfAlgo.getStepSize()+",  is small enough,yet convergance problems still remains!");
				  return false;
			  }
			// step size control in the following step if this corrector step is not converged!
			  this.predStepSolver.enableStepSizeControl(true);
			  this.backToLastConvgState(); // back to the last converged state;
			  IpssLogger.getLogger().warning("the previous Predictor step-size seems to be too large, need to be controlled");
		  }
		  else if(isCpfStopCriteriaMeet()){
			  IpssLogger.getLogger().info("one analysis Stop Criteria is meeted,CPF analysis end!");
			  return true;
		  }
		  // save the last converged network state
		  this.saveConvgState();
		  
		  this.iteration++;
		  this.lastLFConverged=this.cpfAlgo.getAclfNetwork().isLfConverged();
		}
		IpssLogger.getLogger().info("not converged within the max iteration!");
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
		return this.predStepSolver.stepSolver();
		
	}
	
	private boolean isCpfStopCriteriaMeet() {
		if(this.cpfAlgo.getAnalysisStopCriteria()==AnalysisStopCriteria.FULL_CUREVE) {
			if(this.lambda.getValue()<0.01&&this.iteration>5){
				IpssLogger.getLogger().info("full curve anlysis is finished!");
				return this.isCpfStop=true;
			}
		}
		else if(this.cpfAlgo.getAnalysisStopCriteria()==AnalysisStopCriteria.MAX_POWER_POINT) {
			if(this.predStepSolver.isCrossMaxPwrPnt()) {
				IpssLogger.getLogger().info("PV nose is encountered, analysis ended!");
				return isCpfStop=true;
			}
		}
		else{ if(cpfAlgo.isAnyViolation()) {
			IpssLogger.getLogger().info("Analysis is ended because of violation!");
			return isCpfStop=true;
		}
		}
		return isCpfStop=false;
		
	}
	@Override
	public CorrectorStepSolver getCorrStepSolver() {
		
		return this.corrStepSolver;
	}
	@Override
	public LambdaParam getLambda() {
		
		return this.lambda;
	}
	@Override
	public PredictorStepSolver getPredStepSolver() {
		
		return this.predStepSolver;
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
	public boolean isLmdaContParam() {
		return this.getSortNumOfContParam()==this.lambda.getPosition();
	}
    public int getNextStepContParam(){
    	return getMaxDeltaVIdx();
    }
    private int getMaxDeltaVIdx(){ // only Vmag  are considered here
    	ArrayRealVector temp=(ArrayRealVector) this.getPredStepSolver().getDeltaV().mapAbs();
    	return temp.getMaxIndex();
    	
    }


	@Override
	public void backToLastConvgState() {
		this.getLambda().setValue(this.convg_lambda);
		int cnt=0;
		for(Bus b:this.cpfAlgo.getAclfNetwork().getBusList()){
			AclfBus bus=(AclfBus) b;
			bus.setVoltageAng(this.convg_angle[cnt]);
			bus.setVoltageMag(this.convg_Vmag[cnt]);
			cnt++;
		}
		
	}
	private void saveConvgState(){
		
		int n=this.cpfAlgo.getAclfNetwork().getNoBus();
		this.convg_angle=new double[n];
		this.convg_Vmag=new  double[n];
		this.convg_lambda=this.getLambda().getValue();
		
		int cnt=0;
		for(Bus b:this.cpfAlgo.getAclfNetwork().getBusList()){
			AclfBus bus=(AclfBus) b;
			this.convg_angle[cnt]=bus.getVoltageAng();
			this.convg_Vmag[cnt]=bus.getVoltageMag();
			cnt++;
		}
	}
	

}
