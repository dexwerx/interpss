package org.ieee.odm.adapter.pwd.impl;

import static org.ieee.odm.ODMObjectFactory.odmObjFactory;

import java.util.List;

import org.ieee.odm.adapter.pwd.PowerWorldAdapter;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.schema.NetAreaXmlType;
import org.ieee.odm.schema.NetZoneXmlType;
 /**
  * PowerWorld-TO-ODM Adapter based on power world v16 data definition
  * 
  * @version 0.1  04/03/2012
  * @author Tony Huang
  * 
  */
public class NetDataProcessor {
	public static void processAreaData(String areaDataStr, List<String> argumentFileds, AclfModelParser parser){
		/*
		 * DATA (AREA, [AreaNum,AreaName,BGAGC,BGAutoSS,BGAutoXF,EnforceGenMWLimits,SchedName,SAName,
           ConvergenceTol,AreaEDIncludeLossPF,BusSlack,AreaUnSpecifiedStudyMW])
		 */
		//now only consider areaNum, areaName
		
		int areaNum=-1;
		String areaName="";
		String[] areaData=PWDHelper.getDataFields(areaDataStr, argumentFileds);
		int i=0;
		i=argumentFileds.indexOf("AreaNum");
		areaNum=Integer.valueOf(areaData[i]);
		
		i=argumentFileds.indexOf("AreaName");
		areaName=areaData[i];
		
		NetAreaXmlType area=odmObjFactory.createNetAreaXmlType();
		area.setNumber(areaNum);
		area.setName(areaName);
		if(parser.getAclfNet().getAreaList()==null)
			parser.getAclfNet().setAreaList(odmObjFactory.createNetworkXmlTypeAreaList());
		
		parser.getAclfNet().getAreaList().getArea().add(area);
				
		
	}
	
	public static void processZoneData(String zoneDataStr, List<String> argumentFileds, AclfModelParser parser){
		/*
		 * DATA (ZONE, [ZoneNum,ZoneName,SchedName])
		 */
		int zoneNum=-1;
		String zoneName="";
		String[] zoneData=PWDHelper.getDataFields(zoneDataStr, argumentFileds);
		int i=0;
		i=argumentFileds.indexOf("ZoneNum");
		zoneNum=Integer.valueOf(zoneData[i]);
		
		i=argumentFileds.indexOf("ZoneName");
		zoneName=zoneData[i];
		
		NetZoneXmlType zone=odmObjFactory.createNetZoneXmlType();
		zone.setNumber(zoneNum);
		zone.setName(zoneName);
		if(parser.getAclfNet().getLossZoneList()==null)
			parser.getAclfNet().setLossZoneList(odmObjFactory.createNetworkXmlTypeLossZoneList());
		
		parser.getAclfNet().getLossZoneList().getLossZone().add(zone);
		
	}
	
	private void processOwnerData(String ownerDataStr, List<String> argumentFileds, AclfModelParser parser){
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
}
