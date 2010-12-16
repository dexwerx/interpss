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
