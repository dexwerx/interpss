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
import org.interpss.vstab.cpf.impl.GenDispatch;
import org.interpss.vstab.cpf.impl.LoadIncrease;

import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algorithm.LoadflowAlgorithm;
import com.interpss.core.common.visitor.IAclfNetBVisitor;

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
     * 
     * @return the sort Number of continuation parameter;
     */
    public int getSortNumOfContParam();
    /**
     * set the sort Number of continuation parameter;
     * @param sortNum
     */
    
    public void setSorNumofContParam(int sortNum);
    /**
     * get the CPF Solver
     * @return CPF Solver 
     */
    public CPFSolver getCpfSolver() ;
    /**
     * return TRUE if Lambda parameter is used as the continuation parameter, otherwise,False
     * @return 
     */
    public boolean isLmdaContParam();
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
     * @param netVisitor
     * @return
     */
    public boolean visit(AclfNetwork net);

}
