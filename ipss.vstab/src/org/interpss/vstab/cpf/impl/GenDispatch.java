package org.interpss.vstab.cpf.impl;


import java.util.Hashtable;

import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.commons.math.linear.RealVector;
import org.interpss.vstab.cpf.GenDispPattern.GenDispPtn;

import com.interpss.common.util.IpssLogger;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.net.Bus;
/**
 * This class is for power dispatching whenever power mismatch happens between Generation and Load in a network;
 * @author Tony Huang
 *
 */
public class GenDispatch {

    private int numofGen=0;
    private Hashtable<Integer,Double> genPmax; 
    private Hashtable<Integer,Double> genP0;
    private AclfNetwork net;
    private GenDispPtn ptn;
    private Hashtable<String,Double> customGenDispTbl;
    public GenDispatch(AclfNetwork net, GenDispPtn genDispPtn){
    	this.net=net;
    	this.ptn=genDispPtn;
    	initialize();
    }
    
	public  void genDispatch(double pMismatch){
		if(this.ptn==GenDispPtn.AGC){
			genDispByAGC(pMismatch);
		}
		else if (this.ptn==GenDispPtn.RESERVE_PROPORTION){
			genDispByResvProp(pMismatch);
		}
		else if(this.ptn==GenDispPtn.CUSTOM_SPECIFIC){
			genDispByCustSpec(pMismatch);
		}
	}
	public void genDispByAGC(double pMismatch){
		throw new UnsupportedOperationException();
	}
	public void genDispByCustSpec(double pMismatch){
		throw new UnsupportedOperationException();
	}
	public  void genDispByResvProp(double pMismatch){
	{
		/*
		 * 1.run power flow first,get the result ,here the gen P AND Q are needed
		 * 2.get the active power reserve of every genBus.
		 * 3.generation dispatch according to the proportion of gen RESERVE of each gen.
		 */
		IpssLogger.getLogger().info("-- starting gen dispatch by Reservation-Proportion method-- ");

		double sumofDgenP=0;
		ArrayRealVector DgenP=new ArrayRealVector(numofGen);                                               
		AclfBus objbus =null ;
		int j=0;
		for(int i:this.genPmax.keySet()){
	    	 objbus =(AclfBus) net.getBusList().get(i);
	    	 
			    double deltaP=genPmax.get(i)-objbus.getGenP();
		        if (deltaP<=0){
		            objbus.setGenP(genPmax.get(i)); 
		            genPmax.remove(i);
		        }
		        else {DgenP.setEntry(j,  deltaP) ;}
		        j++;
	     }  
      
		//sumofDgenP=sumOfElement(DgenP);
		sumofDgenP=DgenP.getL1Norm(); // get the sum of all element ,since all are positive
		j=0;
		for(int i:this.genPmax.keySet()){
			objbus =(AclfBus) net.getBusList().get(i);
	        	double DPi=DgenP.getEntry(j)/sumofDgenP*pMismatch;
	        	objbus.setGenP(this.genP0.get(i)+DPi);
	        	j++;
	     }
		IpssLogger.getLogger().info("--end gen dispatch--");
	}
   }
	/**
	 * to perform generation dispatching with distributed slack buses, 
	 * each with its distributed factor df_i, make sure  sum(all{df_i})=1
	 * @param distFactor
	 */
   
   
	public void distSlackBus(Hashtable<String,Double> distFactor){
		throw new UnsupportedOperationException();
	}

	private void initialize(){
		this.genP0=new Hashtable<Integer, Double>(this.net.getNoBus());
		this.genPmax=new Hashtable<Integer, Double>(this.net.getNoBus());
		for(int i=0;i<this.net.getBusList().size();i++){
			AclfBus bus=(AclfBus)this.net.getBusList().get(i);
			if(bus.isGenPV()){// only PV bus is considered here
				this.numofGen++; // generator count
				this.genP0.put(i, bus.getGenP());
				if(bus.getPGenLimit()!=null){
				  this.genPmax.put(i,bus.getPGenLimit().getMax());
				}
				else {
					IpssLogger.getLogger().info("There is no PGenLimit defined in bus:"+bus.getId()
							+" ,assume 20% reservation in the following process.");
					this.genPmax.put(i,bus.getGenP()*1.20); // assume there is 20% generation reservation by default;
				}
			}
			
		}
	}


}
