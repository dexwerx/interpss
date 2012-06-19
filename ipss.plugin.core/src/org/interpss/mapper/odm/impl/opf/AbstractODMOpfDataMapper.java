/*
 * @(#)AbstractODMOpfDataMapper.java   
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
 * @Date 02/15/2008
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.mapper.odm.impl.opf;

import static com.interpss.common.util.IpssLogger.ipssLogger;

import javax.xml.bind.JAXBElement;

import org.ieee.odm.model.opf.OpfModelParser;
import org.ieee.odm.schema.AnalysisCategoryEnumType;
import org.ieee.odm.schema.BaseBranchXmlType;
import org.ieee.odm.schema.BaseOpfNetworkXmlType;
import org.ieee.odm.schema.BusXmlType;
import org.ieee.odm.schema.LoadflowBusXmlType;
import org.ieee.odm.schema.NetworkCategoryEnumType;
import org.ieee.odm.schema.OpfDclfGenBusXmlType;
import org.ieee.odm.schema.OpfDclfNetworkXmlType;
import org.ieee.odm.schema.OpfGenBusXmlType;
import org.ieee.odm.schema.OpfNetworkEnumType;
import org.ieee.odm.schema.OpfNetworkXmlType;
import org.ieee.odm.schema.OriginalDataFormatEnumType;
import org.interpss.mapper.odm.ODMAclfNetMapper;
import org.interpss.mapper.odm.ODMHelper;
import org.interpss.mapper.odm.impl.aclf.AbstractODMAclfParserMapper;
import org.interpss.mapper.odm.impl.aclf.AclfBusDataHelper;
import org.interpss.numeric.datatype.LimitType;

import com.interpss.OpfObjectFactory;
import com.interpss.common.exp.InterpssException;
import com.interpss.opf.BaseOpfNetwork;
import com.interpss.opf.OpfBranch;
import com.interpss.opf.OpfBus;
import com.interpss.opf.OpfGenBus;
import com.interpss.opf.OpfNetwork;
import com.interpss.opf.dclf.DclfOpfBranch;
import com.interpss.opf.dclf.DclfOpfBus;
import com.interpss.opf.dclf.DclfOpfGenBus;
import com.interpss.opf.dclf.DclfOpfNetwork;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;

/**
 * abstract mapper implementation to map ODM to InterPSS object model for Opf
 * 
 * @author mzhou
 *
 * @param <Tfrom>
 */
public abstract class AbstractODMOpfDataMapper <Tfrom> extends AbstractODMAclfParserMapper<Tfrom> {
	/**
	 * constructor
	 * 
	 */
	public AbstractODMOpfDataMapper() {
	}
	
	/**
	 * transfer info stored in the parser object into simuCtx object
	 * 
	 * @param p an ODM parser object, representing an ODM xml file
	 * @param simuCtx
	 * @return
	 */
	@Override public boolean map2Model(Tfrom p, SimuContext simuCtx) {
		boolean noError = true;
		
		OpfModelParser parser = (OpfModelParser) p;
		
		if (parser.getStudyCase().getNetworkCategory() == NetworkCategoryEnumType.TRANSMISSION
				&& parser.getStudyCase().getAnalysisCategory() == AnalysisCategoryEnumType.OPF) {
			BaseOpfNetworkXmlType xmlNet = parser.getOpfNet();
			simuCtx.setNetType(SimuCtxType.OPF_NET);
			try {
				BaseOpfNetwork opfNet = null;
				if (xmlNet.getOpfNetType() == OpfNetworkEnumType.SIMPLE_DCLF) 
					opfNet = mapDclfOpfNetData((OpfDclfNetworkXmlType)xmlNet);
				else 
					opfNet = mapOpfNetData((OpfNetworkXmlType)xmlNet);
				simuCtx.setOpfNet(opfNet);

				ODMAclfNetMapper aclfNetMapper = new ODMAclfNetMapper();
				for (JAXBElement<? extends BusXmlType> bus : xmlNet.getBusList().getBus()) {
					if (bus.getValue() instanceof OpfDclfGenBusXmlType) {
						if (xmlNet.getOpfNetType() == OpfNetworkEnumType.SIMPLE_DCLF) {
							OpfDclfGenBusXmlType opfDclfGen = (OpfDclfGenBusXmlType) bus.getValue();
							mapDclfOpfGenBusData(opfDclfGen, (DclfOpfNetwork)opfNet);
						}
						else {
							OpfGenBusXmlType opfGen = (OpfGenBusXmlType) bus.getValue();
							mapOpfGenBusData(opfGen, (OpfNetwork)opfNet);
						}
					} 
					else {
						LoadflowBusXmlType busRec = (LoadflowBusXmlType) bus.getValue();
						if (xmlNet.getOpfNetType() == OpfNetworkEnumType.SIMPLE_DCLF) {
							DclfOpfBus opfDclfBus = OpfObjectFactory.createDclfOpfBus(busRec.getId(), (DclfOpfNetwork)opfNet);
							aclfNetMapper.mapAclfBusData(busRec, opfDclfBus, opfNet);
						}
						else {
							OpfBus opfBus = OpfObjectFactory.createOpfBus(busRec.getId(), (OpfNetwork)opfNet);
							aclfNetMapper.mapAclfBusData(busRec, opfBus, opfNet);
						}
					}
				}

				for (JAXBElement<? extends BaseBranchXmlType> b : xmlNet.getBranchList().getBranch()) {
					if (xmlNet.getOpfNetType() == OpfNetworkEnumType.SIMPLE_DCLF) {
						DclfOpfBranch opfDclfBranch = OpfObjectFactory.createDclfOpfBranch();
						aclfNetMapper.mapAclfBranchData(b.getValue(), opfDclfBranch, (DclfOpfNetwork)opfNet);
					}
					else {
						OpfBranch opfBranch = OpfObjectFactory.createOpfBranch();
						aclfNetMapper.mapAclfBranchData(b.getValue(), opfBranch, (DclfOpfNetwork)opfNet);
					}
				}
			} catch (InterpssException e) {
				e.printStackTrace();
				ipssLogger.severe(e.toString());
				noError = false;
			}
		} 
		else {
			ipssLogger.severe( "Error: wrong Transmission NetworkType and/or ApplicationType");
			noError = false;
		}
		
		if (parser.getStudyCase().getContentInfo() != null) {
			OriginalDataFormatEnumType ofmt = parser.getStudyCase().getContentInfo().getOriginalDataFormat();
			simuCtx.getNetwork().setOriginalDataFormat(ODMHelper.map(ofmt));		
		} 
		else {
			ipssLogger.severe( "Error: StudyCase.ContentInfo were not entered");
			noError = false;
		}
		return noError;
	}
	
	/*
	 *    OPF model mapper
	 *    ================
	 */
	private OpfNetwork mapOpfNetData(OpfNetworkXmlType xmlNet) throws InterpssException {
		OpfNetwork opfNet = OpfObjectFactory.createOpfNetwork();
		new ODMAclfNetMapper().mapAclfNetworkData(opfNet, xmlNet);
		
		// mapping details
		opfNet.setAnglePenaltyFactor(xmlNet.getAnglePenaltyFactor());	
		
		return opfNet;
	}

	public OpfGenBus mapOpfGenBusData(OpfGenBusXmlType busRec, OpfNetwork net) throws InterpssException {
		OpfGenBus opfGenBus = OpfObjectFactory.createOpfGenBus(busRec.getId(), net);
		mapBaseBusData(busRec, opfGenBus, net);

		AclfBusDataHelper helper = new AclfBusDataHelper(net, opfGenBus);
		helper.setAclfBusData(busRec);
		
		// TODO: mapping details
		
		return opfGenBus;
	}
	
	/*
	 *    DCLF OPF model mapper
	 *    =====================
	 */
	
	private DclfOpfNetwork mapDclfOpfNetData(OpfDclfNetworkXmlType xmlNet) throws InterpssException {
		DclfOpfNetwork opfNet = OpfObjectFactory.createDclfOpfNetwork();
		new ODMAclfNetMapper().mapAclfNetworkData(opfNet, xmlNet);
		opfNet.setAnglePenaltyFactor(xmlNet.getAnglePenaltyFactor());	
		return opfNet;
	}

	/**
	 * Map a bus record
	 * 
	 * @param busRec
	 * @param adjNet
	 * @return
	 * @throws Exception
	 */
	public DclfOpfGenBus mapDclfOpfGenBusData(OpfDclfGenBusXmlType busRec, DclfOpfNetwork net) throws InterpssException {
		DclfOpfGenBus opfGenBus = OpfObjectFactory.createDclfOpfGenBus(busRec.getId(), net);
		mapBaseBusData(busRec, opfGenBus, net);

		AclfBusDataHelper helper = new AclfBusDataHelper(net, opfGenBus);
		helper.setAclfBusData(busRec);
		
		/*
    		<pss:coeffA>37.8896</pss:coeffA>
    		<pss:coeffB>0.01433</pss:coeffB>
    		<pss:capacityLimit max="0.2" min="0.05" unit="PU"></pss:capacityLimit>
    		<pss:fixedCost>118.821</pss:fixedCost>  
    	*/
		opfGenBus.setCoeffA(busRec.getCoeffA());
		opfGenBus.setCoeffB(busRec.getCoeffB());
		opfGenBus.setFixedCost(busRec.getFixedCost());
		String unit=busRec.getCapacityLimit().getUnit().value();
		if(unit.equalsIgnoreCase("PU")) {// model mappering consistence, all are converted to MVA unit;
			double factor=net.getBaseKva()*0.001;
		    opfGenBus.setCapacityLimit(new LimitType(busRec.getCapacityLimit().getMax()*factor, 
				              busRec.getCapacityLimit().getMin()*factor));
		}
		else {   //the default unit is MVA
			opfGenBus.setCapacityLimit(new LimitType(busRec.getCapacityLimit().getMax(), busRec.getCapacityLimit().getMin()));
		}
		return opfGenBus;
	}
}