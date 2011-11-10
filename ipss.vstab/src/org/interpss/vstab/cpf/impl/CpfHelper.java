package org.interpss.vstab.cpf.impl;
/**
 * This is a helper class to conduct internal operations of Continuous Power Flow(CPF),
 * including prediction, correction and automatic step size control.
 */

import java.util.Hashtable;

import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.linear.RealVector;
import org.interpss.numeric.datatype.Matrix_xy;
import org.interpss.numeric.datatype.Vector_xy;
import org.interpss.numeric.sparse.SparseEqnMatrix2x2;
import org.interpss.vstab.cpf.CPFAlgorithm;
import org.interpss.vstab.cpf.GenDispPattern;
import org.interpss.vstab.cpf.LoadIncPattern;

import com.interpss.common.util.IpssLogger;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.common.visitor.IAclfBusVisitor;
import com.interpss.core.net.Bus;

public class CpfHelper {
	private SparseEqnMatrix2x2 lfEqn=null;
	private RealVector deltaX_Lambda=null;
	private RealVector corrResult=null;
	private LoadIncPattern loadIncPtn=null;
	private GenDispPattern genDispPtn=null;
	private Hashtable<String,Complex> ldIncDirTbl=null;
	private Hashtable<String,Double> genDirTbl=null;
	private final int DEFAULT_CONT_PARA_SORTNUM=0;
	private int contParaSortNum=DEFAULT_CONT_PARA_SORTNUM;
	private int iterationCount=0;
	private AclfNetwork net=null;
	public CpfHelper(CPFAlgorithm algo){
		this.net=algo.getAclfNetwork();
		this.contParaSortNum=algo.getCpfSolver().getSortNumOfContParam(); //choose Lambda as the continuation parameter by default
		this.loadIncPtn=algo.getLoadIncrease().getPattern();
		this.genDispPtn=algo.getGenDispatch().getPattern();
		this.ldIncDirTbl=loadIncPtn.getLoadIncDir();
	}

	public SparseEqnMatrix2x2 formAugmJacobiMatrix() {
		// 1. get the jacobian matrix (Swing bus is included in jacobi,so the dimension(N) equals to total number of buses, and index is 1 to N )
		  
		lfEqn=this.net.formJMatrix(1);
		
		int n=this.net.getNoBus(); //n=num of bus
		
		//2. augment the Jacobi with the differentiation of load flow equation 
		// to Lamda(the load  increase index) 
        if(getLdIncDirTbl()==null||getLdIncDirTbl().isEmpty()) {
        	IpssLogger.getLogger().info("No load increase data/profile is defined, set it to default");
        	setDefaultLoadIncData();// set the load increase to default pattern;
        }
		   for(Bus b:this.net.getBusList()) {
		   	if(getLdIncDirTbl().containsKey(b.getId())) {
		   		Complex dir_pq=getLdIncDirTbl().get(b.getId());
		   		Matrix_xy m=new Matrix_xy();
		   		if(b.isActive()) {
		   			m.xx=dir_pq.getReal();// dirP  // sign is opposite to ordinary J-matrix;
		   			m.yx=dir_pq.getImaginary();//dirQ
		   			lfEqn.setA(m,b.getSortNumber(), n);

		   		}
		   		
		   	}
		   	if(this.genDispPtn.getGenDirection().containsKey(b.getId())){
		   		Matrix_xy m=new Matrix_xy();
		   		m.xx=-this.genDispPtn.getGenDirection().get(b.getId()); // -GenP
		   		   lfEqn.addToA(m, b.getSortNumber(), n);
		   	}
		   	
		   }
		   Matrix_xy ek=new Matrix_xy();
		   if(this.getSortNumOfContParam()!=this.net.getNoBus()) {
			  ek.xy=1;   // vmag(i)
		   }
	       else ek.xx=1;// corresponding to lambda;
	       
		   lfEqn.setA(ek, n,this.getSortNumOfContParam());
		   if(lfEqn.getA(n, n).yy==0) {
			   Matrix_xy m_lambda=new Matrix_xy();
			   m_lambda.yy=1;
			   lfEqn.addToA(m_lambda, n, n);
			   
		   }
//		  // print J-matrix
//		    VstabFuncOut.printJmatix(lfEqn,6,2);
		   return lfEqn;
	}

    
    
	public Hashtable<String,Complex> getLdIncDirTbl() {
		if(this.loadIncPtn!=null)
		return ldIncDirTbl=this.loadIncPtn.getLoadIncDir();
		else{
			IpssLogger.getLogger().severe("no loadIncPattern defined yet! Please check");
			return null;
		}
		
	}
	public void setLdIncDirTbl(Hashtable<String,Complex> ldIncDirTbl) {
		this.ldIncDirTbl = ldIncDirTbl;
	}
	public void setDefaultLoadIncData() {
		this.ldIncDirTbl=new Hashtable(this.net.getNoBus());
		for(Bus b: this.net.getBusList()) {
			AclfBus bus=(AclfBus) b;
			if((!bus.isSwing())&(!bus.isGen())&bus.isLoad()) {
				ldIncDirTbl.put(b.getId(), new Complex(bus.getLoadP(),bus.getLoadQ()));
			}
		}
	}
	
	public void increaseLoad(double lambda) {
		//To do
	}
	/**
	 * @return sortNumber of the continuous parameter
	 * 
	 */
    public int findContParameter() {
    	if(this.iterationCount==0) {
    		this.contParaSortNum=this.net.getNoBus()+1;
    	}
    	else {
    		// contPara=max(i){|deltaXi/Xi|,|deltaL/Lambda|}
    		int maxIdx=getVectorMaxIndex(this.deltaX_Lambda.ebeDivide(this.corrResult));
    		 // if Lambda is chosen the parameter, sort number of the corresponding continuation parameter  is set to be N+1;
    		if(maxIdx>this.net.getNoBus()*2-1) {
    			this.contParaSortNum=this.net.getNoBus()+1;
    		}
    		else {
    			// select the bus voltage parameter as Cont' Parameter, find the right Sort Number
    			//while considering the storage structure (Vector_xy) and sort number index (1,2, ^ ,N)
    			if(maxIdx%2==0) {
    				this.contParaSortNum=maxIdx/2+1; 
    			}
    			else {
    				this.contParaSortNum=(maxIdx+1)/2;
    			}
    		}
    		  
    		
    	}
		return contParaSortNum;
    }
    public int getSortNumOfContParam() {
    	return this.contParaSortNum;
    	
    }
    public void setSortNumOfContParam(int newSortNumOfContParam) {
    	this.contParaSortNum=newSortNumOfContParam;
    }
    public int getIterationCount() {
    	return this.iterationCount;
    }
    public void updateIterationCount() {
    	this.iterationCount+=1;
    }
    public void setIterCountToZero() {
    	this.iterationCount=0;
    }
    private int getVectorMaxIndex(RealVector vector){
    	int maxIdx=0;
    	double max=Math.abs(vector.getEntry(0));
    	for(int i=1;i<vector.getDimension();i++) {
    		if(max<Math.abs(vector.getEntry(i))){
    			max=Math.abs(vector.getEntry(i));
    			maxIdx=i;
    		}	
    	}
    	return maxIdx;
    	
    }

    private void updateBusVoltage(final SparseEqnMatrix2x2 lfEqn,final double step) {
    	this.net.forEachAclfBus(new IAclfBusVisitor() {

			public void visit(AclfBus bus) {
				if((!bus.isSwing())&(bus.isActive())) {
					Vector_xy dv=lfEqn.getX(bus.getSortNumber());

				    double vang=bus.getVoltageAng()-step*dv.x;
				    double vmag=bus.getVoltageMag();
				    if(!bus.isGenPV()) {
					   vmag-=step*dv.y;
				     }
				 bus.setVoltage(vmag,vang);
			   }
		   }
    		
    	});
    }

    public void setLoadIncPattern(LoadIncPattern ptn){
    	this.loadIncPtn=ptn;
    	
    }
    
}
