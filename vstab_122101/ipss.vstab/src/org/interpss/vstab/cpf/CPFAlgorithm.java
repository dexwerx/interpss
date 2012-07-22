package org.interpss.vstab.cpf;

/**
	structure
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

/*

 *  all //** are by Mike for code refactor
 */

import org.interpss.vstab.cpf.CpfStopCriteria.AnalysisStopCriteria;

import com.interpss.core.algorithm.LoadflowAlgorithm;

//** public interface CPFAlgorithm implements LoadflowAlgorithm {
public interface CPFAlgorithm extends LoadflowAlgorithm {
	public static final String copyright = "Copyright www.interpss.org 2005-2010";
   
//**	public void setAclfNet(AclfNetwork net);
//    
//    public AclfNetwork getAclfNet();
//    
//	public  int getMaxIterations();
//	
//	public  void setMaxIterations(int paramInt);
	
//	public void setLambda(LambdaParam newLambda);
//	
//	public LambdaParam getLambdaParam();
	
	public  void setAnalysisStopCriteria(AnalysisStopCriteria stopCriteria);
	
	public  AnalysisStopCriteria getAnalysisStopCriteria();
	
	
    public void setLoadIncPattern(LoadIncPattern loadIncPtn);
    public LoadIncPattern getLoadIncPatten();
    
    public void setGenDispPattern(GenDispPattern genDispPtn);
    public GenDispPattern getGenDispPatten();
    
//**    public  IPSSMsgHub getMsgHub();
//    
//    public void setMsgHub(IPSSMsgHub paramIPSSMsgHub);
    
    public boolean runCPF();
    
//    public double getTolerance();
    
//**    public  double getTolerance(byte unit);
//
//    public  void setTolerance(double tol, byte unit);
    
    public boolean isAnyViolation();
    
    public int getSortNumOfContParam();
    
    public void setSorNumofContParam(int sortNum);
    
    public void setFixedValOfContPara(double fixedVal);
    
    public double getFixedValOfContPara() ;
    
    public CPFSolver getCpfSolver() ;
//    public CPFSolver createCpfSolver() ;
//    public PredictorStepSolver getPreStepSolver();
//    public CorrectorStepSolver getCorrStepSolver();
}
