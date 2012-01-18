 /*
  * @(#)ContainerHelper.java   
  *
  * Copyright (C) 2008 www.interpss.org
  *
  * This program is free software; you can redistribute it and/or
  * modify it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE
  * as published by the Free Software Foundation; either version 2.1
  * of the License, or (at your option) any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  *
  * @Author Mike Zhou
  * @Version 1.0
  * @Date 02/11/2008
  * 
  *   Revision History
  *   ================
  *
  */

package org.ieee.odm.model.base;

import static org.ieee.odm.ODMObjectFactory.odmObjFactory;

import java.util.Comparator;

import javax.xml.bind.JAXBElement;

import org.ieee.odm.schema.BaseBranchXmlType;
import org.ieee.odm.schema.BaseRecordXmlType;
import org.ieee.odm.schema.BranchXmlType;
import org.ieee.odm.schema.BreakerDistBranchXmlType;
import org.ieee.odm.schema.BusXmlType;
import org.ieee.odm.schema.ComplexXmlType;
import org.ieee.odm.schema.DStabBusXmlType;
import org.ieee.odm.schema.DStabNetXmlType;
import org.ieee.odm.schema.DcBranchXmlType;
import org.ieee.odm.schema.DcBusXmlType;
import org.ieee.odm.schema.DcNetworkXmlType;
import org.ieee.odm.schema.DistributionNetXmlType;
import org.ieee.odm.schema.FeederDistBranchXmlType;
import org.ieee.odm.schema.GeneratorDistBusXmlType;
import org.ieee.odm.schema.IDRecordXmlType;
import org.ieee.odm.schema.IDRefRecordXmlType;
import org.ieee.odm.schema.InductionMotorDistBusXmlType;
import org.ieee.odm.schema.LineBranchXmlType;
import org.ieee.odm.schema.LineDStabXmlType;
import org.ieee.odm.schema.LoadflowBusXmlType;
import org.ieee.odm.schema.LoadflowNetXmlType;
import org.ieee.odm.schema.MixedLoadDistBusXmlType;
import org.ieee.odm.schema.NameValuePairXmlType;
import org.ieee.odm.schema.NetworkXmlType;
import org.ieee.odm.schema.NonContributingDistBusXmlType;
import org.ieee.odm.schema.OpfGenBusXmlType;
import org.ieee.odm.schema.OpfNetworkXmlType;
import org.ieee.odm.schema.OwnerXmlType;
import org.ieee.odm.schema.PSXfr3WBranchXmlType;
import org.ieee.odm.schema.PSXfrBranchXmlType;
import org.ieee.odm.schema.PSXfrDStabXmlType;
import org.ieee.odm.schema.PowerXmlType;
import org.ieee.odm.schema.ReactorDistBranchXmlType;
import org.ieee.odm.schema.ShortCircuitNetXmlType;
import org.ieee.odm.schema.SynchronousMotorDistBusXmlType;
import org.ieee.odm.schema.UtilityDistBusXmlType;
import org.ieee.odm.schema.VoltageXmlType;
import org.ieee.odm.schema.XFormerDistBranchXmlType;
import org.ieee.odm.schema.Xfr3WBranchXmlType;
import org.ieee.odm.schema.XfrBranchXmlType;
import org.ieee.odm.schema.XfrDStabXmlType;
import org.ieee.odm.schema.YXmlType;
import org.ieee.odm.schema.ZXmlType;

public class BaseJaxbHelper {
	/**
	 * get the id of the id ref record
	 * 
	 * @param idRefRec
	 * @return
	 */
	public static String getRecId(IDRefRecordXmlType idRefRec) {
		return ((IDRecordXmlType)idRefRec.getIdRef()).getId();
	}

	/**
	 * warp the network object for substitutionGroup
	 * 
	 * @param net
	 * @return
	 */
	public static JAXBElement<? extends NetworkXmlType> network(NetworkXmlType net) {
		// be careful with inheritance here 
		if (net instanceof DStabNetXmlType) 
			return odmObjFactory.createDstabNet((DStabNetXmlType)net);
		else if (net instanceof OpfNetworkXmlType) 
			return odmObjFactory.createOpfNet((OpfNetworkXmlType)net);
		else if (net instanceof ShortCircuitNetXmlType) 
			return odmObjFactory.createAcscNet((ShortCircuitNetXmlType)net);
		else if (net instanceof LoadflowNetXmlType) 
			return odmObjFactory.createAclfNet((LoadflowNetXmlType)net);
		else if (net instanceof DcNetworkXmlType) 
			return odmObjFactory.createDcNet((DcNetworkXmlType)net);
		else if (net instanceof DistributionNetXmlType) 
			return odmObjFactory.createDistNet((DistributionNetXmlType)net);
		else
			return odmObjFactory.createBaseCase(net);
	}
	
	/**
	 * using the comparator, find a BusXmlType object, which is "equals" to the baseBus object
	 * 
	 * @param net
	 * @param baseBus
	 * @param comp
	 * @return
	 */
	public static BusXmlType getBus(NetworkXmlType net, BusXmlType baseBus, Comparator<BusXmlType> comp) {
		for (JAXBElement<? extends BusXmlType> b : net.getBusList().getBus()) {
			BusXmlType bus = b.getValue();
			if (comp.compare(baseBus, bus) == 0)
				return bus;
		}		
		return null;
	}
	
	/**
	 * warp the branch object for substitutionGroup
	 * 
	 * @param branch
	 * @return
	 */
	public static JAXBElement<? extends BaseBranchXmlType> branch(BaseBranchXmlType branch) {
		// be careful with inheritance here 
		if (branch instanceof LineDStabXmlType) 
			return odmObjFactory.createDstabLine((LineDStabXmlType)branch);
		else if (branch instanceof PSXfrDStabXmlType) 
			return odmObjFactory.createDstabPSXfr((PSXfrDStabXmlType)branch);
		else if (branch instanceof XfrDStabXmlType) 
			return odmObjFactory.createDstabXfr((XfrDStabXmlType)branch);
		
		else if (branch instanceof LineBranchXmlType) 
			return odmObjFactory.createAclfLine((LineBranchXmlType)branch);
		else if (branch instanceof PSXfr3WBranchXmlType) 
			return odmObjFactory.createAclf3WPSXfr((PSXfr3WBranchXmlType)branch);
		else if (branch instanceof PSXfrBranchXmlType) 
			return odmObjFactory.createAclfPSXfr((PSXfrBranchXmlType)branch);
		else if (branch instanceof Xfr3WBranchXmlType) 
			return odmObjFactory.createAclf3WXfr((Xfr3WBranchXmlType)branch);
		else if (branch instanceof XfrBranchXmlType) 
			return odmObjFactory.createAclfXfr((XfrBranchXmlType)branch);

		else if (branch instanceof DcBranchXmlType) 
			return odmObjFactory.createDcBranch((DcBranchXmlType)branch);
		
		/*
		<element name="distFeederBranch" type="pss:FeederDistBranchXmlType" substitutionGroup="pss:branch"/>
		<element name="distXfrBranch" type="pss:XFormerDistBranchXmlType" substitutionGroup="pss:branch"/>
		<element name="distReactorBranch" type="pss:ReactorDistBranchXmlType" substitutionGroup="pss:branch"/>
		<element name="distBreakerBranch" type="pss:BreakerDistBranchXmlType" substitutionGroup="pss:branch"/>
	 */
		else if (branch instanceof FeederDistBranchXmlType) 
			return odmObjFactory.createDistFeederBranch((FeederDistBranchXmlType)branch);
		else if (branch instanceof XFormerDistBranchXmlType) 
			return odmObjFactory.createDistXfrBranch((XFormerDistBranchXmlType)branch);
		else if (branch instanceof ReactorDistBranchXmlType) 
			return odmObjFactory.createDistReactorBranch((ReactorDistBranchXmlType)branch);
		else if (branch instanceof BreakerDistBranchXmlType) 
			return odmObjFactory.createDistBreakerBranch((BreakerDistBranchXmlType)branch);
		
		else
			return odmObjFactory.createBranch(branch);
	}
	
	/**
	 * using the comparator, find a BusXmlType object, which is "equals" to the baseBus object
	 * 
	 * @param net
	 * @param baseBus
	 * @param comp
	 * @return
	 */
	public static BranchXmlType getBranch(NetworkXmlType net, BranchXmlType baseBra, Comparator<BranchXmlType> comp) {
		for (JAXBElement<? extends BaseBranchXmlType> b : net.getBranchList().getBranch()) {
			BranchXmlType bra = (BranchXmlType)b.getValue();
			if (comp.compare(baseBra, bra) == 0)
				return bra;
		}		
		return null;
	}	
	
	/**
	 * warp the bus object for substitutionGroup
	 * 
	 * @param bus
	 * @return
	 */
	public static JAXBElement<? extends BusXmlType> bus(BusXmlType bus) {
		// be careful with inheritance here 
		if (bus instanceof DStabBusXmlType)
			return odmObjFactory.createDstabBus((DStabBusXmlType)bus);
		else if (bus instanceof OpfGenBusXmlType)
			return odmObjFactory.createOpfGenBus((OpfGenBusXmlType)bus);
		else if (bus instanceof LoadflowBusXmlType)
			return odmObjFactory.createAclfBus((LoadflowBusXmlType)bus);
		else if (bus instanceof DcBusXmlType)
			return odmObjFactory.createDcBus((DcBusXmlType)bus);
		/*
		<element name="distUtilityBus" type="pss:UtilityDistBusXmlType" substitutionGroup="pss:bus"/>
		<element name="distGeneratorBus" type="pss:GeneratorDistBusXmlType" substitutionGroup="pss:bus"/>
		<element name="distSynMotorBus" type="pss:SynchronousMotorDistBusXmlType" substitutionGroup="pss:bus"/>
		<element name="distIndMotorBus" type="pss:InductionMotorDistBusXmlType" substitutionGroup="pss:bus"/>
		<element name="distMixedLoadBus" type="pss:MixedLoadDistBusXmlType" substitutionGroup="pss:bus"/>
		<element name="distNonContributeSynMotorBus" type="pss:NonContributingDistBusXmlType" substitutionGroup="pss:bus"/>
	 */
		else if (bus instanceof UtilityDistBusXmlType)
			return odmObjFactory.createDistUtilityBus((UtilityDistBusXmlType)bus);
		else if (bus instanceof GeneratorDistBusXmlType)
			return odmObjFactory.createDistGeneratorBus((GeneratorDistBusXmlType)bus);
		else if (bus instanceof SynchronousMotorDistBusXmlType)
			return odmObjFactory.createDistSynMotorBus((SynchronousMotorDistBusXmlType)bus);
		else if (bus instanceof InductionMotorDistBusXmlType)
			return odmObjFactory.createDistIndMotorBus((InductionMotorDistBusXmlType)bus);
		else if (bus instanceof MixedLoadDistBusXmlType)
			return odmObjFactory.createDistMixedLoadBus((MixedLoadDistBusXmlType)bus);
		else if (bus instanceof NonContributingDistBusXmlType)
			return odmObjFactory.createDistNonContributeBus((NonContributingDistBusXmlType)bus);
		
		else
			return odmObjFactory.createBus(bus);
	}
	
	/**
	 * add the bus record into the network record, and set some default values
	 * 
	 * @param busRec
	 * @param net
	 * 
	 */
	public static void addBus2Net(BusXmlType busRec, NetworkXmlType net) {
		busRec.setOffLine(false);
		busRec.setAreaNumber(1);
		busRec.setZoneNumber(1);
		net.getBusList().getBus().add(bus(busRec));
	}
	
	/**
	 * add a name/value pair to the name/value pair List
	 * 
	 * @param nvList name/value pair list
	 * @param name name string
	 * @param value value string
	 */
	public static void addNVPair(BaseRecordXmlType rec, String name, 
					String value) {
    	NameValuePairXmlType nvPair = odmObjFactory.createNameValuePairXmlType();
    	rec.getNvPairList().add(nvPair);
    	nvPair.setName(name);
    	nvPair.setValue(value);
	}
	
	/**
	 * add an owner record to the BaseRecord
	 * 
	 * @param rec
	 * @param id
	 * @param ownership in pu
	 */
	public static void addOwner(BaseRecordXmlType rec, String id) {
		addOwner(rec, id, 1.0);
	}
	
	public static void addOwner(BaseRecordXmlType rec, String id, double ownership) {
		OwnerXmlType owner = odmObjFactory.createOwnerXmlType();
		rec.getOwnerList().add(owner);
		owner.setId(id);
		owner.setOwnership(ownership);
	}
	
	/**
	 * add an owner records to the BaseRecord
	 * 
	 * @param rec
	 * @param id
	 * @param ownership
	 */
	public static void addOwner(BaseRecordXmlType rec, 
			String id1, double ownership1,
			String id2, double ownership2,
			String id3, double ownership3,
			String id4, double ownership4) {
		if (id1 != null && ownership1 > 0.0)
			addOwner(rec, id1, ownership1);
		if (id2 != null && ownership2 > 0.0)
			addOwner(rec, id2, ownership2);
		if (id1 != null && ownership3 > 0.0)
			addOwner(rec, id3, ownership3);
		if (id1 != null && ownership4 > 0.0)
			addOwner(rec, id4, ownership4);
	}

	/**
	 * Set branch ownership
	 * 
	 * @param branchData
	 * @param oAry
	 * @param pAry
	 */
	public static void setBranchOwnership(BaseRecordXmlType rec, int[] oAry, double[] pAry) {
		for ( int i = 0; i < oAry.length; i++) {
			if (oAry[i] > 0) {
				addOwner(rec, new Integer(oAry[i]).toString(), pAry[i]);
			}
		}
	}		
	
	/**
	 * ComplexXmlType to string
	 *  
	 * @param c
	 * @return
	 */
	public static String toStr(ComplexXmlType c) {
		return c == null? "null" : "[" + c.getRe() + "+j" + c.getIm() + "]";
	}
	
	/**
	 * YXmlType to string
	 *  
	 * @param c
	 * @return
	 */
	public static String toStr(YXmlType c) {
		return c == null? "null" : "[" + c.getRe() + "+j" + c.getIm() + " " + c.getUnit() + "]";
	}

	/**
	 * ZXmlType to string
	 *  
	 * @param c
	 * @return
	 */
	public static String toStr(ZXmlType c) {
		return c == null? "null" : "[" + c.getRe() + "+j" + c.getIm() + " " + c.getUnit() + "]";
	}
	
	/**
	 * PowerXmlType to string
	 *  
	 * @param c
	 * @return
	 */
	public static String toStr(PowerXmlType c) {
		return c == null? "null" : "[" + c.getRe() + "+j" + c.getIm() + " " + c.getUnit() + "]";
	}
	
	/**
	 * VoltageXmlType to string
	 *  
	 * @param c
	 * @return
	 */
	public static String toStr(VoltageXmlType c) {
		return c == null? "null" : "[" + c.getValue() + " " + c.getUnit() + "]";
	}
}
