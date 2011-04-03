package org.ieee.odm.model.aclf;

import java.util.List;

import org.ieee.odm.common.NumericUtil;
import org.ieee.odm.model.base.BaseJaxbHelper;
import org.ieee.odm.schema.BranchXmlType;
import org.ieee.odm.schema.LFGenCodeEnumType;
import org.ieee.odm.schema.LFLoadCodeEnumType;
import org.ieee.odm.schema.LineBranchXmlType;
import org.ieee.odm.schema.LoadflowBusXmlType;
import org.ieee.odm.schema.PSXfrBranchXmlType;
import org.ieee.odm.schema.XfrBranchXmlType;

public class AclfModelComparator {
	/**
	 * compare bus objects: b1 and b2. The error is store in the msgList
	 * 
	 * @param base
	 * @param bus
	 * @param msgList
	 */
	public static void compare(LoadflowBusXmlType base, LoadflowBusXmlType bus, List<String> msgList) {
		if (base.getBaseVoltage().getValue() != bus.getBaseVoltage().getValue())
			msgList.add("\nBus base voltage not equal: " + base.getId() + ", " + base.getBaseVoltage().getValue() + " (base)");
/*
                <genData>
                    <equivGen code="PV">
                        <power unit="MVA" im="0.0" re="3570.0"/>
                        <desiredVoltage unit="PU" value="1.049"/>
                        <qLimit unit="MVAR" active="false" min="0.0" max="880.8"/>
                    </equivGen>
                </genData>
*/
		if (base.getGenData() != null && bus.getGenData() != null) {
			if (base.getGenData().getEquivGen().getCode() != bus.getGenData().getEquivGen().getCode())
				msgList.add("\nBus EquivGen code not equal: " + base.getId() + ", " + base.getGenData().getEquivGen().getCode() + " (base)");
		}
		else if (base.getGenData() == null && bus.getGenData() != null 
						&& bus.getGenData().getEquivGen().getCode() != LFGenCodeEnumType.NONE_GEN ||
				 base.getGenData() != null && bus.getGenData() == null 
				 		&& base.getGenData().getEquivGen().getCode() != LFGenCodeEnumType.NONE_GEN) {
			msgList.add("\nBus EquivGen model not equal: " + base.getId() + ", " + base.getName());
		}	
			
/*
                <loadData>
                    <equivLoad code="CONST_P">
                        <constPLoad unit="MVA" im="442.6" re="700.0"/>
                    </equivLoad>
                </loadData>
*/
		if (base.getLoadData() != null && bus.getLoadData() != null) {
			if (base.getLoadData().getEquivLoad().getCode() != bus.getLoadData().getEquivLoad().getCode())
				msgList.add("\nBus EquivLoad code not equal: " + base.getId() + ", " + base.getLoadData().getEquivLoad().getCode() + " (base)");
		}
		else if (base.getLoadData() == null && bus.getLoadData() != null 
						&& bus.getLoadData().getEquivLoad().getCode() != LFLoadCodeEnumType.NONE_LOAD ||
				 base.getLoadData() != null && bus.getLoadData() == null 
				 		&& base.getLoadData().getEquivLoad().getCode() != LFLoadCodeEnumType.NONE_LOAD) {
			msgList.add("\nBus EquivLoad model not equal: " + base.getId() + ", " + base.getName());
		}
		
/*
                <shuntY unit="PU" im="-1.4" re="5.3"/>
*/
		if (base.getShuntY() != null && bus.getShuntY() != null) {
			if (!NumericUtil.equals(base.getShuntY().getIm(), bus.getShuntY().getIm()) ||
			    !NumericUtil.equals(base.getShuntY().getRe(), bus.getShuntY().getRe()))
				msgList.add("\nBus ShuntY not equal: " + base.getId() + ", " + bus.getId() 
						+ "   " + BaseJaxbHelper.toStr(base.getShuntY()) + " (base)"
						+ "   " + BaseJaxbHelper.toStr(bus.getShuntY()));
		}
		else if (base.getShuntY() == null && bus.getShuntY() != null ||
				 base.getShuntY() != null && bus.getShuntY() == null) {
			msgList.add("\nBus ShuntY model not equal: " + base.getId() + ", " + base.getName());
		}
		
	}

	/**
	 * compare aclf line objects: b1 and b2. The error is store in the msgList
	 * 
	 * @param base
	 * @param bra
	 * @param msgList
	 */
	public static void compare(BranchXmlType base, BranchXmlType bra, List<String> msgList) {
		//
		// 	LineBranchXmlType
		//
		if (base instanceof LineBranchXmlType ) {
			LineBranchXmlType baseLine = (LineBranchXmlType)base;
			LineBranchXmlType line = (LineBranchXmlType)bra;
/*
	           <z unit="PU" im="0.0181" re="0.0014"/>
*/
			if (!NumericUtil.equals(baseLine.getZ().getRe(), line.getZ().getRe()) ||
					!NumericUtil.equals(baseLine.getZ().getIm(), line.getZ().getIm())) {
				msgList.add("\nBranch Z not equal: " + base.getId() + ", " + bra.getId()
						+ "   " + BaseJaxbHelper.toStr(baseLine.getZ()) + " (base)"
						+ "   " + BaseJaxbHelper.toStr(line.getZ()));
			}
		}

		//
		// 	XfrBranchXmlType
		//

		if (base instanceof XfrBranchXmlType ) {
			XfrBranchXmlType baseXfr = (XfrBranchXmlType)base;
			XfrBranchXmlType xfr = (XfrBranchXmlType)bra;

/*
         	<z unit="PU" im="0.0066" re="8.0E-5"/>
*/
			if (!NumericUtil.equals(baseXfr.getZ().getRe(), xfr.getZ().getRe()) ||
				!NumericUtil.equals(baseXfr.getZ().getIm(), xfr.getZ().getIm())) {
				msgList.add("\nXfr Branch Z not equal: " + base.getId() + ", " + bra.getId()
							+ "   " + BaseJaxbHelper.toStr(baseXfr.getZ()) + " (base)"
							+ "   " + BaseJaxbHelper.toStr(xfr.getZ()));
			}
			
/*			
            <fromTurnRatio unit="PU" value="1.0"/>
            <toTurnRatio unit="PU" value="0.9524"/>
*/
			if (!NumericUtil.equals(baseXfr.getFromTurnRatio().getValue(), xfr.getFromTurnRatio().getValue(), 0.0001)) {
				msgList.add("\nXfr Branch fromTurnRatio not equal: " + base.getId() + ", " + bra.getId()
								+ "   " + baseXfr.getFromTurnRatio().getValue() + " (base)"
								+ "   " + xfr.getFromTurnRatio().getValue());
			}
			if (!NumericUtil.equals(baseXfr.getToTurnRatio().getValue(), xfr.getToTurnRatio().getValue(), 0.0001)) {
				msgList.add("\nXfr Branch toTurnRatio not equal: " + base.getId() + ", " + bra.getId()
								+ "   " + baseXfr.getToTurnRatio().getValue() + " (base)"
								+ "   " + xfr.getToTurnRatio().getValue());
			}
			
/*			
            <xfrInfo>
                <fromRatedVoltage unit="KV" value="26.0"/>
                <toRatedVoltage unit="KV" value="525.0"/>
            </xfrInfo>
*/
			
			if (base instanceof PSXfrBranchXmlType ) {
				
			}
		}
	}
}
