package org.interpss.vstab.cpf.impl;


import java.util.Hashtable;

import org.apache.commons.math.complex.Complex;
import org.interpss.vstab.cpf.LoadIncPattern;

import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.net.Bus;
/**
 * With the predefined load increase pattern, this LoadIncreae class increase the some interested loads by Lambda;
 * @author Tony Huang
 *
 */
public class LoadIncrease {
	protected AclfNetwork net=null;
	protected LoadIncPattern ldIncPtn =null;
	protected LambdaParam lambda=null;
    private Hashtable<String,Complex> origLdTbl=null; 
	private double oldSumOfIncLoadP=0;
	private double sumOfIncLoadP=0;
	public LoadIncrease(AclfNetwork net, LoadIncPattern ptn){
		this.net=net;
		this.ldIncPtn=ptn;
        saveOrigLoad();

	}
	public void setAclfNetwork(AclfNetwork net){
		this.net=net;
		saveOrigLoad();// when a network is loaded, the original load data is saved first for later usage.
	}
	public AclfNetwork getAclfNetwork(){
		return this.net;
	}
	public LoadIncPattern getPattern() {
		return this.ldIncPtn;
	}
	public void setPattern(LoadIncPattern ldIncPtn) {
		this.ldIncPtn = ldIncPtn;
	}
	private void saveOrigLoad() {
		origLdTbl=new Hashtable<String,Complex>(this.net.getNoBus());
		for(Bus b:this.net.getBusList()){
			AclfBus bus=(AclfBus) b;
			if(bus.isLoad()){
				origLdTbl.put(bus.getId(), bus.getLoad());
			}
		}
		
	}
	public Hashtable<String, Complex> getOrigLoad(){
		return this.origLdTbl;
	}
	public void increaseLoad(double incSize) {
		oldSumOfIncLoadP=sumOfIncLoadP;
		sumOfIncLoadP=0;
//		IpssLogger.getLogger().info("----Start load increase, and incSize= "+incSize+"-----");
		for(Bus b:this.ldIncPtn.getIncBusList()){
			AclfBus bus=(AclfBus) b;
			Complex deltaLoad=this.ldIncPtn.getLoadIncDir().get(bus.getId()).multiply(incSize);
			Complex incLoad=this.getOrigLoad().get(bus.getId()).add(deltaLoad);
			bus.setLoadP(incLoad.getReal());
			bus.setLoadQ(incLoad.getImaginary());
			sumOfIncLoadP+=deltaLoad.getReal();
		}
//		IpssLogger.getLogger().info("----End load increase----");
	}
	public double getSumOfIncLoad(){
		return this.sumOfIncLoadP;
	}
	/**
	 * return the subtraction result of load increase in the last two steps: deltaSumOfLoad=sumOfIncLoadP-oldSumOfIncLoadP
	 */
    public double getDeltaSumOfLoad(){
    	return this.sumOfIncLoadP-this.oldSumOfIncLoadP;
    }
    
	
}
