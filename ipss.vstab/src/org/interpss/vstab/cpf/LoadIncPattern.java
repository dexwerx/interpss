package org.interpss.vstab.cpf;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.math.complex.Complex;

import com.interpss.common.util.IpssLogger;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.net.Bus;

/**
 * This class is to define the load increase pattern, including the scope and the increase direction of studied load buses;
 * 
 * @author Tony Huang
 *
 */
public class LoadIncPattern {

	private LoadIncScope scope;
	private LoadIncType  type;
	private Hashtable<String,Complex> ldIncDirTbl=null;
	private AclfNetwork net=null;
	private  Hashtable<String ,Bus > incLoadBusTbl=null;
	private List<Bus> incBusList=null;
	public LoadIncPattern(AclfNetwork net,LoadIncScope incScope,LoadIncType incType,Object[]incObjectList){
		this.net=net;
		ldIncByScope(incScope,incObjectList);
		defLoadIncDir(type);
	}
    /*
     * LoadIncScope : to specify where the loads will increase, or which of the analyzed network is the interested part(s) during the CPF analysis
     */
	
	public enum LoadIncScope{NETWORK,AREA,ZONE,BUS};
	
	/*
	 * LoadIncType : to define how the would-be-increased load(s) increase demand
	 */
	public enum LoadIncType{CONST_PF,ONLY_P,ONLY_Q};
	
	public void setLoadIncScope(LoadIncScope incScope){
		this.scope=incScope;
	}
	public LoadIncScope getLoadIncScope(){
		return this.scope;
	}
	
	public LoadIncType getLdIncType() {
		return type;
	}
	public void setLdIncType(LoadIncType type) {
		this.type = type;
	}
	public Hashtable<String,Complex> getLoadIncDir(){
		return this.ldIncDirTbl;
	}
    private void defLoadIncDir(LoadIncType type){
    	ldIncDirTbl.clear(); // clear the direction info saved before
		for(Bus b:incBusList){
			AclfBus bus=(AclfBus) b;
			Complex dir=new Complex(0, 0);
			if(type==LoadIncType.CONST_PF){
				dir=bus.getLoad();
			}
			else if(type==LoadIncType.ONLY_P){
				dir=new Complex(bus.getLoadP(),0);
			}
			else if(type==LoadIncType.ONLY_Q){
				dir= new Complex(0,bus.getLoadQ());
			}
			else
				IpssLogger.getLogger().severe("Error:the Load Type"+ type.toString()+" is never defined in InterPSS yet! ");
			this.ldIncDirTbl.put(bus.getId(),dir);
		}
	}
    public void customLoadIncDir(){
    	throw new UnsupportedOperationException();
    }
	
	private void ldIncByScope(LoadIncScope scope, Object[]increaseObj){
		long areaNumber=0;
		long zoneNumber=0;
		if(scope==LoadIncScope.NETWORK){
			ldIncByNetwork();	
		}
		if(scope==LoadIncScope.AREA) {
			if((increaseObj.length!=0)& increaseObj instanceof Long[]) {
				for(int i=0;i<increaseObj.length;i++) {
					areaNumber=(Long) increaseObj[i];
					addLdBus2ListByArea(areaNumber);	
				}
			}
			else {
				IpssLogger.getLogger().severe("LoadIncPtn is by AREA, but inceaseObjList is not Area type, not consistent with ptn");
			}
			
		if(scope==LoadIncScope.ZONE) {
			if((increaseObj.length!=0)& increaseObj instanceof Long[]) {
				for(int i=0;i<increaseObj.length;i++) {
					zoneNumber=(Long) increaseObj[i];
					addLdBus2ListByZone(zoneNumber);	
				}
			}
			else {
				IpssLogger.getLogger().severe("LoadIncPtn is by ZONE, but inceaseObj is not ZONE type, not consistent with ptn");
			}
		}
		if(scope==LoadIncScope.BUS) {
			if((increaseObj!=null)&(increaseObj instanceof Bus[])) {
				addLoadBus2ListByBus(Arrays.asList((Bus[])increaseObj));
			}
			else {
				IpssLogger.getLogger().severe("LoadIncPtn is by BUS, but inceaseObj is not BUS type, not consistent with ptn");
			}
		}
			
		}
	}
	
	public void setIncBusList(ArrayList<Bus> incBusList) {
		this.incBusList = incBusList;
	}
	public List<Bus> getIncBusList() {
		return this.incBusList;
	}
	
	private void ldIncByNetwork() {
		addLoadBus2ListByBus(this.net.getBusList());
	}
	private void addLdBus2ListByArea(long areaNum) {
		for(Bus b:net.getBusList()) {
			AclfBus bus=(AclfBus) b;
			if((b.getArea().getNumber()==areaNum)&bus.isLoad()&b.isActive()) {
				if (this.incLoadBusTbl.containsKey(b.getId())){
					IpssLogger.getLogger().info("Duplication Error,Bus "+b.getId()+"is already in incBusList");
				}
				else {
					this.incBusList.add(b);
					this.incLoadBusTbl.put(b.getId(), b);
				}
			}
				
		}
	}
	private void addLdBus2ListByZone(long zoneNum) {
		for(Bus b:net.getBusList()) {
			
			AclfBus bus=(AclfBus) b;
			if(b.getZone().getNumber()==zoneNum&bus.isLoad()& b.isActive()) {
				if (this.incLoadBusTbl.containsKey(b.getId())){
					IpssLogger.getLogger().info("Duplication Error,Bus "+b.getId()+"is already in incBusList");
				}
				else{
					this.incBusList.add(b);
					this.incLoadBusTbl.put(b.getId(), b);
				}
			}
				
		}
	}
	private void addLoadBus2ListByBus(List<Bus> list) {
		if((this.incBusList!=null)&(!this.incBusList.isEmpty())) {
			for(Bus b:list) {
				if(!this.incLoadBusTbl.containsValue(b)){
					if(((AclfBus)b).isLoad()& b.isActive()) {
					     this.incBusList.add(b);
					     this.incLoadBusTbl.put(b.getId(), b);
					}
					else {
						IpssLogger.getLogger().info("Bus "+b.getId()+"is not a Load Bus");
					}
				}
				else {
					IpssLogger.getLogger().info("Duplication Error,Bus "+b.getId()+"is already in incBusList");
				}
			}
		}
		else {
			this.incBusList=list;
			for(Bus b:list) {
				this.incLoadBusTbl.put(b.getId(),b);
			}
		}
	}
	
	public Hashtable<String,Bus> getIncLoadBusTbl() {
		return this.incLoadBusTbl;
	}
	public void getLdPtnFromXmlDoc(File file) {
		throw new UnsupportedOperationException();
	}

}
