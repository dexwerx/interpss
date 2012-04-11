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
public class NetDataProcessor extends BaseDataProcessor  {
	public NetDataProcessor(List<PowerWorldAdapter.NVPair> nvPairs, AclfModelParser parser) {
		super(nvPairs, parser);
	}
	
	public void processAreaData(String areaDataStr){
		/*
		 * DATA (AREA, [AreaNum,AreaName,BGAGC,BGAutoSS,BGAutoXF,EnforceGenMWLimits,SchedName,SAName,
           ConvergenceTol,AreaEDIncludeLossPF,BusSlack,AreaUnSpecifiedStudyMW])
		 */
		//now only consider areaNum, areaName
		
		int areaNum=-1;
		String areaName="";
		PWDHelper.parseDataFields(areaDataStr, inputNvPairs);
		for (PowerWorldAdapter.NVPair nv: inputNvPairs) {
			if (nv.name.equals("AreaNum"))
				areaNum=Integer.valueOf(nv.value);
			else if (nv.name.equals("AreaName"))
				areaName=nv.value;
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
		PWDHelper.parseDataFields(zoneDataStr, inputNvPairs);
		for (PowerWorldAdapter.NVPair nv: inputNvPairs) {
			if (nv.name.equals("ZoneNum"))
					zoneNum=Integer.valueOf(nv.value);
			else if (nv.name.equals("ZoneName"))
				zoneName=nv.value;
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
}
