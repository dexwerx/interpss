package org.interpss.vstab.cpf.impl;
/**
 * This is a helper class to conduct internal operations of Continuous Power Flow(CPF),
 * including prediction, correction and automatic step size control.
 */

import java.util.Hashtable;

import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.linear.RealVector;
import org.interpss.vstab.util.VstabFuncOut;

import com.interpss.common.datatype.Matrix_xy;
import com.interpss.common.datatype.Vector_xy;
import com.interpss.common.util.IpssLogger;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.common.visitor.IAclfBusVisitor;
import com.interpss.core.net.Bus;
import com.interpss.core.sparse.SparseEqnMatrix2x2;

/*
 *  all //** are by Mike for code refactor
 */

public class CpfHelper {
	private SparseEqnMatrix2x2 lfEqn=null;
	private RealVector deltaX_Lambda=null;
	private RealVector corrResult=null;
	private Hashtable<String,Complex> ldIncDirTbl=null;
	private final int DEFAULT_CONT_PARA_SORTNUM=0;
	private int contParaSortNum=DEFAULT_CONT_PARA_SORTNUM;
	private int iterationCount=0;
//**	private IPSSMsgHub msg=null;
	private AclfNetwork net=null;
	
//**	public CpfHelper(AclfNetwork net, IPSSMsgHub msg){
	public CpfHelper(AclfNetwork net){
		this.net=net;
//**		this.msg=msg;
		contParaSortNum=this.net.getNoBus()+1; // by default
	}

	public SparseEqnMatrix2x2 formAugmJacobiMatrix() {
		// 1. get the jacobian matrix (Swing bus is included in jacobi,so the dimension(N) equals to total number of buses, and index is 1 to N )
		  
		lfEqn=this.net.formJMatrix(1);
		
		int n=this.net.getNoBus(); //n=num of bus
		
		//2. augment the Jacobi with the differentiation of load flow equation 
		// to Lamda(the load  increase index) 
        if(ldIncDirTbl==null||ldIncDirTbl.isEmpty()) {
//**        	msg.sendInfoMsg("No load increasement data/profile is defined, set it to default");
        	IpssLogger.getLogger().info("No load increasement data/profile is defined, set it to default");
        	setDefaultLoadIncData();
        }
		   for(Bus b:this.net.getBusList()) {
		   	if(ldIncDirTbl.containsKey(b.getId())) {
		   		Complex dir_pq=this.ldIncDirTbl.get(b.getId());
		   		Matrix_xy m=new Matrix_xy();
		   		if(b.isActive()) {
		   			m.xx=dir_pq.getReal();// dirP  // sign is opposite to ordinary J-matrix;
		   			m.yx=dir_pq.getImaginary();//dirQ
		   			lfEqn.setAij(m,b.getSortNumber(), n+1);

		   		}
		   		
		   	}
		   	
		   }
		   Matrix_xy ek=new Matrix_xy();
		   if(this.getSortNumOfContParam()!=this.net.getNoBus()+1) {
//			   if(this.getSortNumOfContParam()%2==0)
//			   ek.xx=1; // Corresponding to vmag(i) in B vector [theta(i)];
//			   else 
			  ek.xy=-1;   // vmag(i)
		   }
	       else ek.xx=-1;// corresponding to lambda;
	       
		   lfEqn.setAij(ek, n+1,this.getSortNumOfContParam());
		   if(lfEqn.getElement(n+1, n+1).yy==0) {
			   Matrix_xy m_lambda=new Matrix_xy();
			   m_lambda.yy=1;
			   lfEqn.addToAij(m_lambda, n+1, n+1);
			   
		   }
		  // print J-matrix
		    VstabFuncOut.printJmatix(lfEqn,6,2);
		   return lfEqn;
	}

    
	public Complex getLoadIncDirection(String busId) {
		// To do
		
		return null;
		
		
	}
    
	public Hashtable<String,Complex> getLdIncDirTbl() {
		return ldIncDirTbl;
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
					Vector_xy dv=lfEqn.getBVect_xy(bus.getSortNumber());
					
					Complex v= bus.getVoltage(); 
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
    
}
