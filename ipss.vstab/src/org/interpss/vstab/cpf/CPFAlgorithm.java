package org.interpss.vstab.cpf;



import org.interpss.vstab.cpf.CpfStopCriteria.AnalysisStopCriteria;
import org.interpss.vstab.cpf.impl.CorrectorStepSolver;
import org.interpss.vstab.cpf.impl.LambdaParam;
import org.interpss.vstab.cpf.impl.PredictorStepSolver;

import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algorithm.LoadflowAlgorithm;

public interface CPFAlgorithm extends LoadflowAlgorithm{
	public static final String copyright = "Copyright www.interpss.org 2005-2010";
   
	public  void setAnalysisStopCriteria(AnalysisStopCriteria stopCriteria);
	
	public  AnalysisStopCriteria getAnalysisStopCriteria();
	
    public void setLoadIncPattern(LoadIncPattern loadIncPtn);
    public LoadIncPattern getLoadIncPatten();
    
    public void setGenDispPattern(GenDispPattern genDispPtn);
    public GenDispPattern getGenDispPatten();
    
    public boolean runCPF();
    
    public boolean isAnyViolation();
    
    public int getSortNumOfContParam();
    
    public void setSorNumofContParam(int sortNum);
    
    public void setFixedValOfContPara(double fixedVal);
    
    public double getFixedValOfContPara() ;
    
    public CPFSolver getCpfSolver() ;

}
