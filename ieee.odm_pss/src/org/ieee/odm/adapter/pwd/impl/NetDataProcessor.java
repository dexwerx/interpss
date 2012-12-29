package org.ieee.odm.adapter.pwd.impl;

import static org.ieee.odm.ODMObjectFactory.odmObjFactory;

import org.ieee.odm.adapter.InputLineStringParser;
import org.ieee.odm.common.ODMException;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.schema.NetAreaXmlType;
import org.ieee.odm.schema.NetZoneXmlType;
 /**
  * PowerWorld-TO-ODM Adapter based on power world v16 data definition
  * 
  * @version 0.2  09/30/2012
  * @author Tony Huang
  * 
  */
public class NetDataProcessor extends InputLineStringParser  {
	private AclfModelParser parser = null;
	
	public NetDataProcessor(AclfModelParser parser) {
		this.parser = parser;
	}
	
	public void processAreaData(String areaDataStr){
		/*
		 * DATA (AREA, [AreaNum,AreaName,BGAGC,BGAutoSS,BGAutoXF,EnforceGenMWLimits,SchedName,SAName,
           ConvergenceTol,AreaEDIncludeLossPF,BusSlack,AreaUnSpecifiedStudyMW])
		 */
		//now only consider areaNum, areaName
		
		int areaNum=-1;
		String areaName="";
		parseData(areaDataStr);
		
		try {
			areaNum=getInt("AreaNum");
			areaName=getString("AreaName");
		} catch (ODMException e) {
			e.printStackTrace();
		}
		NetAreaXmlType area=odmObjFactory.createNetAreaXmlType();
		area.setNumber(areaNum);
		area.setName(areaName);
		if(parser.getAclfNet().getAreaList()==null)
			parser.getAclfNet().setAreaList(odmObjFactory.createNetworkXmlTypeAreaList());
		
		parser.getAclfNet().getAreaList().getArea().add(area);
	}
	
	public void processZoneData(String zoneDataStr){
		/*
		 * DATA (ZONE, [ZoneNum,ZoneName,SchedName])
		 */
		int zoneNum=-1;
		String zoneName="";
		parseData(zoneDataStr);
	
		try {
			zoneNum=getInt("ZoneNum");
			zoneName=getString("ZoneName");
		} catch (ODMException e) {
			e.printStackTrace();
		}
		
				
		NetZoneXmlType zone=odmObjFactory.createNetZoneXmlType();
		zone.setNumber(zoneNum);
		zone.setName(zoneName);
		if(parser.getAclfNet().getLossZoneList()==null)
			parser.getAclfNet().setLossZoneList(odmObjFactory.createNetworkXmlTypeLossZoneList());
		
		parser.getAclfNet().getLossZoneList().getLossZone().add(zone);
		
	}
	
	public void processOwnerData(String ownerDataStr){
		//TODO
		/*
		 * 1. owner data
		 * 2. subData, judge if ownerDataStr.trim().startWith("<SubData")
		 * 
		 * add the owner data to a HashTable(ownerNum, OwnerXmlType)
		 * then for each bus/branch/gen(defined in the subdata), set the corresponding Owner
		 *
		 */
	}
	
	public void processInterfaceData(String interfaceDataStr){
		//TODO
	}
}
