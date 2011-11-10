package org.interpss.vstab.cpf.impl;

import java.util.Hashtable;

import org.interpss.vstab.cpf.GenDispPattern;

import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.net.Bus;

/**
 * This class is for power dispatching whenever power mismatch happens between
 * Generation and Load in a network;
 * 
 * @author Tony Huang
 * 
 */
public class GenDispatch {

	private int numofGen = 0;
	private AclfNetwork net=null;
	private GenDispPattern ptn=null;
	private Hashtable<String, Double> genDispDirection=null;
	private Hashtable<String, Double> genP0;


	public GenDispatch(AclfNetwork net, GenDispPattern genDispPtn) {
		this.net = net;
		this.setPattern(genDispPtn);
		this.genDispDirection=genDispPtn.getGenDirection();
		initialize();
	}
    /**
     * redispatching generation as load changes in such a way: genP=genP0+lambda*genP_dir
     * @param lambda--the loading parameter. 
     */
	public void dispatchGen(double lambda) {
      for(String id:genDispDirection.keySet()){
    	  AclfBus bus=this.net.getAclfBus(id);
    	  double genP=this.genP0.get(id)+lambda*this.genDispDirection.get(id);
    	  if(genP>bus.getPGenLimit().getMax()){
//    		  IpssLogger.getLogger().info("encounter pGenLimit max @ Bus #"+bus.getId());
    		  genP=bus.getPGenLimit().getMax();
    		  bus.setGenP(genP);
    	  }
    	  
//    	  System.out.println("gen Bus#"+bus.getNumber()+":  active power output="+genP);
      }
	}
    
	public void genDispByAGC(double pMismatch) {
		throw new UnsupportedOperationException();
	}

	/**
	 * to perform generation dispatching with distributed slack buses, each with
	 * its distributed factor df_i, and make sure that sum(all{df_i})=1
	 * 
	 * @param distFactor
	 */

	public void distSlackBus(Hashtable<String, Double> distFactor) {
		throw new UnsupportedOperationException();
	}
	private void initialize(){
		genP0=new Hashtable<String, Double>();
		for(Bus b:this.net.getBusList()){
			AclfBus bus=(AclfBus)b;
			if(bus.isGenPV()){// only PV bus is considered here
				this.numofGen++; // generator count
				this.genP0.put(b.getId(), bus.getGenP());
				
			}
			
		}
	}
	/**
	 * @param ptn--the Generation dispatching pattern to set
	 */
	public void setPattern(GenDispPattern ptn) {
		this.ptn = ptn;
	}
	/**
	 * @return the Generation dispatching pattern
	 */
	public GenDispPattern getPattern() {
		return ptn;
	}

}
