package org.interpss.vstab.util;

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.DecompositionSolver;
import org.apache.commons.math.linear.LUDecompositionImpl;
import org.apache.commons.math.linear.RealMatrix;
import org.interpss.vstab.cpf.CPFAlgorithm;

import com.interpss.common.datatype.Vector_xy;
import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.common.util.Number2String;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algorithm.AclfMethod;
import com.interpss.core.algorithm.INrSolver;
import com.interpss.core.algorithm.impl.DefaultNrSolver;
import com.interpss.core.common.visitor.IAclfBusVisitor;
import com.interpss.core.common.visitor.IAclfNetBVisitor;
import com.interpss.core.datatype.Mismatch;
import com.interpss.core.sparse.SparseEqnMatrix2x2;
import com.interpss.spring.CoreCommonSpringCtx;

public class CustomLfAlgorithm implements IAclfNetBVisitor{
	private final double Default_Max_Iteration=20;
	private double maxIteration=Default_Max_Iteration;
	private double tolerance=1e-6;
	AclfNetwork net=null;
	INrSolver Solver=null;
	
    /**
     * customized Load Flow for CPFAlgorithm, used in the corrector step;
     * @param cpf
     */
	public CustomLfAlgorithm(CPFAlgorithm cpf){
    	Solver=cpf.getCpfSolver().getCorrStepSolver();
    	net=cpf.getAclfNetwork();
    }
	
	/**
	 * Default NR solver is used until it is set to another;
	 * @param net
	 */
	public CustomLfAlgorithm(AclfNetwork net){
		this.net=net;
    	Solver=new DefaultNrSolver(net);

    }
    public CustomLfAlgorithm() {
		
	}

	public INrSolver getSolver() {
		return Solver;
	}
	public void setSolver(INrSolver nrSolver) {
		this.Solver = nrSolver;
	}
	public AclfNetwork getAclfNet() {
		
		return net;
	}

	public void setAclfNet(AclfNetwork net) {
		this.net = net;
	}
	
	public double getMaxIteration() {
		return maxIteration;
	}
	public void setMaxIteration(double maxIteration) {
		this.maxIteration = maxIteration;
	}
	public double getTolerance() {
		return tolerance;
	}
	public void setTolerance(double tolearance) {
		this.tolerance = tolearance;
	}
	public AclfNetwork getNet() {
		return net;
	}
	public void setNet(AclfNetwork net) {
		this.net = net;
	}
	public double getDefault_Max_Iteration() {
		return Default_Max_Iteration;
	}
	public IPSSMsgHub getMsg(){
		return CoreCommonSpringCtx.getIpssMsgHub();
	}
	public boolean loadflow(){
	    INrSolver solver =getSolver();
	    
	    getAclfNet().setLfConverged(false);
	    for (int itcnt = 0; itcnt <= getMaxIteration(); ++itcnt) {
	      Mismatch misBuf = getAclfNet().maxMismatch(AclfMethod.NR);
	      getMsg().sendStatusMsg("NR Itr: " + Number2String.toStr(itcnt) + 
	        "   " + misBuf.toString());

	      if (misBuf.largeThan(10000000000.0D)) {
	        getMsg().sendStatusMsg("Load Flow Diverges !");
	        return false; }
	      if (misBuf.lessThan(getTolerance())){
	        getAclfNet().setLfConverged(true);
	        getMsg().sendStatusMsg("Load Flow Converged !");
	        return true;
	      }


	      if (!(nrStep(solver)))
	        return false;
	    }
	    getMsg().sendStatusMsg("Load Flow does not Converge !");
	    return false;
	  }
	public boolean nrStep(INrSolver solver){
		SparseEqnMatrix2x2 lfEqn=solver.formJMatrix();
		solver.setPowerMismatch(lfEqn);
		// print out power mismatch
		printPowerMismatch(lfEqn);
		if(solveEqnByCommonMath(lfEqn)){
			solver.updateBusVoltage(lfEqn);
			return true;
		}
		else	
		return false;
		
	}
	private boolean solveEqnByCommonMath(SparseEqnMatrix2x2 lfEqn) {//// customized solver,different from default LU decomposition;
		double[][] temp=VstabFuncOut.SparseMatrix2Ary(lfEqn);
		double[]   b0=new double[lfEqn.getDimension()];
		double[][] A=null;
		double[] b=null;
		int row=lfEqn.getDimension();
		if(lfEqn.getDimension()>this.getAclfNet().getNoBus()*2){
			A=new double[this.getAclfNet().getNoBus()*2+1][this.getAclfNet().getNoBus()*2+1];
			row=this.getAclfNet().getNoBus()*2+1;
			for(int i=0; i<=this.getAclfNet().getNoBus()*2;i++){
				for(int j=0; j<=this.getAclfNet().getNoBus()*2;j++){
					A[i][j]=temp[i][j];
				}
			}
			
			for(int i=0;i<lfEqn.getDimension()/2;i++) { // index 1-N
				   b0[2*i]=lfEqn.getBVect_xy(i+1).x;
				   b0[2*i+1]=lfEqn.getBVect_xy(i+1).y;
		    }
			
			b=new double[row];
			for(int k=0;k<row;k++ ) b[k]=b0[k];
			
		}
		else{
			A=temp;
			b=b0;
		}

		
		RealMatrix J=new  Array2DRowRealMatrix(A);
		DecompositionSolver lu=new LUDecompositionImpl(J).getSolver();
		 Vector_xy v=null;
		if(lu.isNonSingular()){
		  double[] dxdL =lu.solve(b);
		  for(int i=0;i<lfEqn.getDimension()/2;i++) { // index 1-N
			  if(2*i+1>row-1){
				 v=new Vector_xy(dxdL[2*i],0);
			  }
			  else v=new Vector_xy(dxdL[2*i],dxdL[2*i+1]);
			  
			lfEqn.setBi(v, i+1);
		  }
		  return true;
		}
		else{
			getMsg().sendErrorMsg("Error: solver is singular");
            return false;
		}
	 }

	@Override
	public boolean visit(AclfNetwork net) {
		if(this.net!=net)  setAclfNet(net);
		return loadflow();
	}
	private void printPowerMismatch( final SparseEqnMatrix2x2 lfEqn){
		  
		   net.forEachAclfBus(new IAclfBusVisitor(){
			@Override
			public void visit(AclfBus bus) {
				int i=bus.getSortNumber();
				Vector_xy bxy=lfEqn.getBVect_xy(i);
				System.out.println(bus.getId()+"  "+bus.getGenCode().getLiteral()+":  misP="+bxy.x+",  misQ="+bxy.y);
			}
			   
		   });
		   if(lfEqn.getDimension()>net.getNoBus()*2){
			   Vector_xy v=lfEqn.getBVect_xy(net.getNoBus()+1);
			   System.out.println("B("+(net.getNoBus()+1)+") :  misLambda="+v.x+",  dummy="+v.y);
		   }
	   }
	
		
}

