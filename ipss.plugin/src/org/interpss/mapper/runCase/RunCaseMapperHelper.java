package org.interpss.mapper.runCase;

import org.apache.commons.math.complex.Complex;
import org.interpss.numeric.datatype.Unit.Type;
import org.interpss.xml.schema.AcscFaultCategoryDataType;
import org.interpss.xml.schema.AcscFaultXmlType;

import com.interpss.core.acsc.fault.AcscBranchFault;
import com.interpss.core.acsc.fault.AcscBusFault;
import com.interpss.core.acsc.fault.SimpleFaultCode;

public class RunCaseMapperHelper {
	
	public static void acscFaultData2AcscBusFaultMapping(AcscFaultXmlType xmlData, AcscBusFault fault) {
		fault.setFaultCode(xmlData.getFaultCategory() == AcscFaultCategoryDataType.FAULT_LLG ?
						SimpleFaultCode.GROUND_LLG : (xmlData.getFaultCategory() == AcscFaultCategoryDataType.FAULT_LG) ? 
								SimpleFaultCode.GROUND_LG : (xmlData.getFaultCategory() == AcscFaultCategoryDataType.FAULT_LL) ? 
										SimpleFaultCode.GROUND_LL : SimpleFaultCode.GROUND_3P);
		if (xmlData.getZLG() != null)
			fault.setZLGFault(new Complex(xmlData.getZLG().getRe(), xmlData.getZLG().getIm()));
		if (xmlData.getZLL() != null)
			fault.setZLLFault(new Complex(xmlData.getZLL().getRe(), xmlData.getZLL().getIm()));
	}

	public static void acscFaultData2AcscBranchFaultMapping(AcscFaultXmlType xmlData, AcscBranchFault fault) {
		acscFaultData2AcscBusFaultMapping(xmlData, fault);
		fault.setDistance(xmlData.getDistance(), Type.Percent);
	}
}
