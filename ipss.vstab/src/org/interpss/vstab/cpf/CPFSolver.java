package org.interpss.vstab.cpf;

import org.interpss.vstab.cpf.impl.CorrectorStepSolver;
import org.interpss.vstab.cpf.impl.CpfHelper;
import org.interpss.vstab.cpf.impl.LambdaParam;
import org.interpss.vstab.cpf.impl.PredictorStepSolver;
/**
 * the solver for CPF analysis
 * @author Tony Huang
 *
 */

public interface CPFSolver {
   /**
    * 
    * @return boolean param to show solving CPF successfully or not;
    */
	public boolean solveCPF();
    /**
     *  run predictor Step analysis and return a boolean parameter as a flag;
     * @return 
     */
	public boolean predictorStep();
	/**
	 *  run corrector step analysis and return  a boolean parameter as a flag;
	 * @return
	 */
	public boolean correctorStep();
	/**
	 * 
	 * @return Lambda parameter;
	 */
    public LambdaParam getLambda();
    
    public CorrectorStepSolver getCorrStepSolver();
    public PredictorStepSolver getPredStepSolver();
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
     * return TRUE if Lambda parameter is used as the continuation parameter, otherwise,False
     * @return the flag whether lambda parameter is the continuation parameter;
     */
    public boolean isLmdaContParam();
    
    public void backToLastConvgState();
    
    

}


