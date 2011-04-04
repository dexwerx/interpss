package com.interpss.QA.odm;

import java.util.Comparator;

import org.ieee.odm.model.IODMModelParser;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.base.BaseJaxbHelper;
import org.ieee.odm.model.base.ModelStringUtil;
import org.ieee.odm.schema.BranchXmlType;
import org.ieee.odm.schema.BusXmlType;

public class PSSE2BAPBranchComparator implements Comparator<BranchXmlType> {
	AclfModelParser psseModelParder = null;
	
	public PSSE2BAPBranchComparator (IODMModelParser parser) {
		this.psseModelParder = (AclfModelParser)parser;
	}
	
	public int compare(BranchXmlType psseBra, BranchXmlType bpaBra) {
		// PSSE uses bus number, BPA uses bus name as the id
		// fromId_toId_cirId
		String braId = branchId(psseBra, this.psseModelParder);
		// toId_fromId_cirId
		String braId1 = branchId1(psseBra, this.psseModelParder);
		if (braId.toLowerCase().equals(bpaBra.getId().toLowerCase()) ||
				braId1.toLowerCase().equals(bpaBra.getId().toLowerCase()))
			return 0;
		else {
			return 1;
		}
	}
	
	// fromId_toId_cirId
	public static String branchId(BranchXmlType psseBra, AclfModelParser psseModelParder) {
		BusXmlType fromBus = psseModelParder.getBus(
				BaseJaxbHelper.getRecId(psseBra.getFromBus()));
		BusXmlType toBus = psseModelParder.getBus(
				BaseJaxbHelper.getRecId(psseBra.getToBus()));
		String braId = ModelStringUtil.formBranchId(fromBus.getName().trim(), 
				toBus.getName().trim(), psseBra.getCircuitId());
		return braId;
	}
	
	// toId_fromId_cirId
	public static String branchId1(BranchXmlType psseBra, AclfModelParser psseModelParder) {
		BusXmlType fromBus = psseModelParder.getBus(
				BaseJaxbHelper.getRecId(psseBra.getFromBus()));
		BusXmlType toBus = psseModelParder.getBus(
				BaseJaxbHelper.getRecId(psseBra.getToBus()));
		String braId = ModelStringUtil.formBranchId(toBus.getName().trim(), 
				fromBus.getName().trim(), psseBra.getCircuitId());
		return braId;
	}
}
