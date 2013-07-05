package org.ieee.odm.model.dstab;

import static org.ieee.odm.ODMObjectFactory.odmObjFactory;

import org.ieee.odm.model.acsc.BaseAcscModelParser;
import org.ieee.odm.model.base.BaseJaxbHelper;
import org.ieee.odm.schema.BranchXmlType;
import org.ieee.odm.schema.BusXmlType;
import org.ieee.odm.schema.DStabBusXmlType;
import org.ieee.odm.schema.DStabNetXmlType;
import org.ieee.odm.schema.LineDStabXmlType;
import org.ieee.odm.schema.NetworkXmlType;
import org.ieee.odm.schema.PSXfrDStabXmlType;
import org.ieee.odm.schema.ShortCircuitBusXmlType;
import org.ieee.odm.schema.ShortCircuitNetXmlType;
import org.ieee.odm.schema.XfrDStabXmlType;

public class BaseDstabModelParser <
TNetXml extends NetworkXmlType, 
TBusXml extends BusXmlType,
TLineXml extends BranchXmlType,
TXfrXml extends BranchXmlType,
TPsXfrXml extends BranchXmlType
> extends BaseAcscModelParser<TNetXml, TBusXml, TLineXml, TXfrXml, TPsXfrXml> {

	public BaseDstabModelParser() {
		super();
	}
	
	public BaseDstabModelParser(String encoding) {
		super(encoding);
	}	
	/**
	 * get the base case object of type ShortCircuitNetXmlType
	 * 
	 * @return
	 */
	public DStabNetXmlType getDstabNet() {
		return (DStabNetXmlType)getBaseCase();
	}
	
	/**
	 * create the base case object of type ShortCircuitNetXmlType
	 */
	@SuppressWarnings("unchecked")
	@Override public TNetXml createBaseCase() {
		if (getStudyCase().getBaseCase() == null) {
			DStabNetXmlType baseCase = odmObjFactory.createDStabNetXmlType();
			baseCase.setBusList(odmObjFactory.createNetworkXmlTypeBusList());
			baseCase.setBranchList(odmObjFactory.createNetworkXmlTypeBranchList());
			getStudyCase().setBaseCase(BaseJaxbHelper.network(baseCase));
		}
		return (TNetXml)getStudyCase().getBaseCase().getValue();
	}
	
	/**
	 * add a new Bus record to the base case
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override public TBusXml createBus() {
		DStabBusXmlType busRec = odmObjFactory.createDStabBusXmlType();
		busRec.setOffLine(false);
		busRec.setAreaNumber(1);
		busRec.setZoneNumber(1);
		getBaseCase().getBusList().getBus().add(BaseJaxbHelper.bus(busRec));
		return (TBusXml)busRec;
	}	
	
	
}
