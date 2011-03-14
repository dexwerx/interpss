package org.interpss.vstab.cpf;

import java.util.Hashtable;
import java.util.List;

import org.interpss.vstab.cpf.impl.CorrectorStepSolver;
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
    /**
     * turn the network state back to the last converged one. 
     */
    public void backToLastConvgState();
    /**
     * the return List is indexed by bus index, and both the iteration time and corresponding busVmag 
     * constitute the key-value pair in the stored Hashtable<Integer,Double> ; 
     *   
     * @return the List of each bus's "point-voltageMag" along a PV curve;
     */
    public List<Hashtable<Integer, Double>> getBusPVCurveList();

    /**
     * 
     * @return a list of lambda values along a PV curve;
     */
	public List<Double> getLambdaList();
    

}


