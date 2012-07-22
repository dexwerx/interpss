package org.interpss.vstab.cpf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.net.Bus;
import com.interpss.core.net.Area;
import com.interpss.core.net.Zone;
import java.io.File;

public class LoadIncPattern {

	protected List<Area> incAreaList=null;
	protected List<Zone> incZoneList=null;
	protected List<Bus> incBusList=null;
	private  Hashtable<String ,Bus > incLoadBusTbl=null;
	private  IPSSMsgHub _msg=null;
	private  AclfNetwork _net=null;

	
	public enum LoadIncPtn{NETWORK,AREA,ZONE,BUS,CUSTOM_SPECIFIC};
	
	public LoadIncPattern(AclfNetwork net,IPSSMsgHub msg) {
		
		this._net=net;
		this._msg=msg;
		this.incLoadBusTbl=new Hashtable<String, Bus>(net.getNoBus());
		this.incAreaList=new ArrayList<Area>();
		this.incZoneList=new ArrayList<Zone>();
		this.incBusList=new ArrayList<Bus>();
	}
	public void ldIncByNetwork() {
		addLoadBus2ListByBus(this._net.getBusList());
	}
	public void defLoadIncPtn(LoadIncPtn ptn, AclfNetwork net,Object[]increaseObj){
		this._net=net;
		long areaNumber=0;
		long zoneNumber=0;
		if(ptn==LoadIncPtn.AREA) {
			if((increaseObj.length!=0)& increaseObj instanceof Long[]) {
				for(int i=0;i<increaseObj.length;i++) {
					areaNumber=(Long) increaseObj[i];
					addLdBus2ListByArea(areaNumber);	
				}
			}
			else {
				_msg.sendErrorMsg("LoadIncPtn is by AREA, but inceaseObjList is not Area type, not consistent with ptn");
			}
			
		if(ptn==LoadIncPtn.ZONE) {
			if((increaseObj.length!=0)& increaseObj instanceof Long[]) {
				for(int i=0;i<increaseObj.length;i++) {
					zoneNumber=(Long) increaseObj[i];
					addLdBus2ListByZone(zoneNumber);	
				}
			}
			else {
				_msg.sendErrorMsg("LoadIncPtn is by ZONE, but inceaseObj is not ZONE type, not consistent with ptn");
			}
		}
		if(ptn==LoadIncPtn.BUS) {
			if((increaseObj!=null)&(increaseObj instanceof Bus[])) {
				addLoadBus2ListByBus(Arrays.asList((Bus[])increaseObj));
			}
			else {
				_msg.sendErrorMsg("LoadIncPtn is by BUS, but inceaseObj is not BUS type, not consistent with ptn");
			}
		}
			
		}
	}

	public List<Area> getIncAreaList() {
		return incAreaList;
	}
	public void setIncAreaList(List<Area> incAreaList) {
		this.incAreaList = incAreaList;
	}
	
	public List<Zone> getIncZoneList() {
		return incZoneList;
	}
	public void setIncZoneList(List<Zone> incZoneList) {
		this.incZoneList = incZoneList;
	}
	
	public void setIncBusList(List<Bus> incBusList) {
		this.incBusList = incBusList;
	}
	public List<Bus> getIncBusList() {
		return this.incBusList;
	}
	private void addLdBus2ListByArea(long areaNum) {
		for(Bus b:_net.getBusList()) {
			AclfBus bus=(AclfBus) b;
			if((b.getArea().getNumber()==areaNum)&bus.isLoad()) {
				if (this.incLoadBusTbl.containsKey(b.getId())){
					_msg.sendErrorMsg("Duplication Error,Bus "+b.getId()+"is already in incBusList");
				}
				else {
					this.incBusList.add(b);
					this.incLoadBusTbl.put(b.getId(), b);
				}
			}
				
		}
	}
	private void addLdBus2ListByZone(long zoneNum) {
		for(Bus b:_net.getBusList()) {
			
			AclfBus bus=(AclfBus) b;
			if(b.getZone().getNumber()==zoneNum&bus.isLoad()) {
				if (this.incLoadBusTbl.containsKey(b.getId())){
					_msg.sendErrorMsg("Duplication Error,Bus "+b.getId()+"is already in incBusList");
				}
				else {
					this.incBusList.add(b);
					this.incLoadBusTbl.put(b.getId(), b);
				}
			}
				
		}
	}
	private void addLoadBus2ListByBus(List<Bus> loadIncBusList) {
		if((this.incBusList!=null)&(!this.incBusList.isEmpty())) {
			for(Bus b:loadIncBusList) {
				if(!this.incLoadBusTbl.containsValue(b)){
					if(((AclfBus)b).isLoad()) {
					     this.incBusList.add(b);
					     this.incLoadBusTbl.put(b.getId(), b);
					}
					else {
						_msg.sendErrorMsg("Bus "+b.getId()+"is not a Load Bus");
					}
				}
				else {
					_msg.sendErrorMsg("Duplication Error,Bus "+b.getId()+"is already in incBusList");
				}
			}
		}
		else {
			this.incBusList=loadIncBusList;
			for(Bus b:loadIncBusList) {
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
