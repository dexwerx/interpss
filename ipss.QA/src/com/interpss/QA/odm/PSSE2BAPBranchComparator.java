package com.interpss.QA.odm;

import org.ieee.odm.model.IODMModelParser;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.base.BaseJaxbHelper;
import org.ieee.odm.schema.BranchXmlType;
import org.ieee.odm.schema.BusXmlType;
import org.ieee.odm.util.IODMComparator;

public class PSSE2BAPBranchComparator implements IODMComparator<BranchXmlType> {
	AclfModelParser psseModelParder = null;
	AclfModelParser bpaModelParder = null;
	
	public PSSE2BAPBranchComparator (IODMModelParser psseParser, IODMModelParser bpaParser) {
		this.psseModelParder = (AclfModelParser)psseParser;
		this.bpaModelParder = (AclfModelParser)bpaParser;
	}
	
	@Override
	public IODMModelParser getBaseParser() {
		return this.psseModelParder;
	}

	public int compare(BranchXmlType psseBra, BranchXmlType bpaBra) {
		BusXmlType psseFromBus = psseModelParder.getBus(
				BaseJaxbHelper.getRecId(psseBra.getFromBus()));
		BusXmlType psseToBus = psseModelParder.getBus(
				BaseJaxbHelper.getRecId(psseBra.getToBus()));

		BusXmlType bpaFromBus = bpaModelParder.getBus(
				BaseJaxbHelper.getRecId(bpaBra.getFromBus()));
		BusXmlType bpaToBus = bpaModelParder.getBus(
				BaseJaxbHelper.getRecId(bpaBra.getToBus()));

		if (psseFromBus.getName().trim().toLowerCase().equals(bpaFromBus.getName().toLowerCase()) && 
			psseToBus.getName().trim().toLowerCase().equals(bpaToBus.getName().toLowerCase()) &&
			psseBra.getCircuitId().equals(bpaBra.getCircuitId())
				||
			psseFromBus.getName().trim().toLowerCase().equals(bpaToBus.getName().toLowerCase()) && 
			psseToBus.getName().trim().toLowerCase().equals(bpaFromBus.getName().toLowerCase()) &&
			psseBra.getCircuitId().equals(bpaBra.getCircuitId()))
			return 0;
		else {
			return 1;
		}
	}
}
