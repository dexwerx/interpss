package org.interpss.vstab.cpf;

/**
class structure
    
    CPFAlgorithm -> LoadflowAlgorithm
          --<> AclfNetwork (inherited)
          --<> LoadIncPattern
          --<> GenDispPattern
          --<> CPFSolver
                  ---> CPFAlgorithm
                  --<> LambdaParam
                  --<> PredictorStepSolver
                  			---> CPFAlgorithm
                  --<> CorrectorStepSolver
                  			---> CPFAlgorithm

*/

import org.interpss.vstab.cpf.CpfStopCriteria.AnalysisStopCriteria;
import org.interpss.vstab.cpf.impl.CpfHelper;
import org.interpss.vstab.cpf.impl.GenDispatch;
import org.interpss.vstab.cpf.impl.LoadIncrease;

import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;
/**
 * An interface to define the methods/operations in CPFAlgorithm, and it is extended from LoadflowAlgorithm.
 * @author Tony Huang
 *
 */
public interface CPFAlgorithm extends LoadflowAlgorithm{
	public static final String copyright = "Copyright www.interpss.org 2005-2010";
   /**
    * set the CPF analysis stop criteria, now FULL_CUREVE, MAX_POWER_POINT,FIRST_LIMIT_VIOLATION is considered
    * @param stopCriteria
    */
	public  void setAnalysisStopCriteria(AnalysisStopCriteria stopCriteria);
	/**
	 * get the Analysis Stop Criteria 
	 * @return AnalysisStopCriteria
	 */
	public  AnalysisStopCriteria getAnalysisStopCriteria();
	/**
	 * define the generation dispatching/scheduling strategy;
	 * @param genDispatch
	 */
    
    public void setGenDispatch(GenDispatch genDispatch);
    /**
     * get the GenDispatch attribute
     * @return GenDispatch
     */
    public GenDispatch getGenDispatch();
    /**
     * run CPF analysis
     * @return
     */
    public boolean runCPF();
    /**
     * to find out if there is any limit violation during the analysis;
     * @return 
     */
    public boolean isAnyViolation();

    /**
     * get the CPF Solver
     * @return CPF Solver 
     */
    public CPFSolver getCpfSolver() ;

    /**
     * set the LoadIncrease attribute, it aims to increase load by load increasing index-Lambda; Note:load increase pattern is referenced in LoadIncrease
     * @param ldInc
     */
    public void setLoadIncrease(LoadIncrease ldInc);
    /**
     * 
     * @return the LoadIncrease parameter
     */
    public LoadIncrease getLoadIncrease();
    /**
     * to visit the network and perform the CPF analysis
     * @param AclfNetwork net
     * @return boolean as a flag to indicate the CPF analysis is converged or not?
     */
    public boolean visit(AclfNetwork net);
    
    /**
     * to set the max step size during the analysis, so that stepSize*dX < maxStepSize
     * @param maxStepSize
     */
	public void setMaxStepSize(double maxStepSize);

    /**
     * 
     * @return the max step size; it satisfies the following inequation : stepSize*dX < maxStepSize
     */
	public double getMaxStepSize();

    /**
     * set the stepSize to control the states deviation in each step;
     * @param stepSize
     */

	public void setStepSize(double stepSize);
  
    /**
     * 
     * @return step size
     */
	public double getStepSize();
	/**
	 * @return minimum step size constrain
	 */
	public double getMinStepSize();
	
	public void setMinStepSize(double minStepSize);
	
    /**
     * set CpfHelper parameter
     * @param cpfHelper 
     */
	public void setCpfHelper(CpfHelper cpfHelper);
	
    /**
     * 
     * @return cpfHelper
     */
	public CpfHelper getCpfHelper();
	
	/**
	 * @param pflowTolerance the power flow tolerance to set(in P.U.)
	 */
	public void setPflowTolerance(double pflowTolerance);

	/**
	 * @return the power flow tolerance,in P.U.;
	 */
	public double getPflowTolerance();
	/**
	 * set the max CPF iterations
	 * @param maxCPFItr
	 */
	public void setCPFMaxInteration(int maxCPFItr);
	/**
	 * 
	 * @return
	 * the max CPF iterations
	 */
	public int getCPFMaxInteration();
	/**
	 * set the max power flow iterations
	 * @param maxPowerflowItr
	 */
	public void setPfMaxInteration(int maxPowerflowItr);
	/**
	 * 
	 * @return
	 * the max power flow iterations
	 */
	public int getPfMaxInteration();
	/**
	 * set the to-be-displayed PQBus while tracing the PV curve
	 * @param busID
	 */
	public void setDisplayPQBus(String[] PQBusID);
	/**
	 * 
	 * @return
	 * the display PQBus Id(s)
	 */
	public String[] getDisplayPQBus();
	/**
	 * 
	 * @return the max step size allowed for loading index(Lambda parameter) update;
	 */
	public double getMaxDeltaLambda() ;
   /**
    * set the max step size allowed for loading index(Lambda parameter)update;
    * @param maxDeltaLambda
    */
	public void setMaxDeltaLambda(double maxDeltaLambda);
	/**
	 * set TRUE to disable All Violation Checking while tracing PV curve; 
	 * otherwise set FALSE.
	 * @param booleanParam
	 */
	public void disableAllViolationCheck(boolean booleanParam);
	/**
	 * set TRUE to disable branch MVARating Violation Checking while tracing PV curve; 
	 * otherwise set FALSE.
	 * @param booleanParam
	 */
	public void disableBraMVAViolChk(boolean booleanParam);
	/**
	 * set TRUE to disable bus voltage limit Violation Checking while tracing PV curve; 
	 * otherwise set FALSE.
	 * @param booleanParam
	 */
	public void disableBusVViolChk(boolean booleanParam);
	/**
	 * check the data needed in CPF analysis; such as GenPLimit, GenQlimit
	 */
	public boolean checkDataForCPF();
	
	


}
