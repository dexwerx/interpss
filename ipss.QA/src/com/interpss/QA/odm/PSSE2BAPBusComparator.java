package com.interpss.QA.odm;

import org.ieee.odm.model.IODMModelParser;
import org.ieee.odm.schema.BusXmlType;
import org.ieee.odm.util.IODMComparator;

public class PSSE2BAPBusComparator implements IODMComparator<BusXmlType> {
	@Override
	public IODMModelParser getBaseParser() {
		return null;
	}
	
	public int compare(BusXmlType psseBus, BusXmlType bpaBus) {
		// PSSE uses bus number, BPA uses bus name as the id
		if (psseBus.getName().trim().toLowerCase()
				.equals(bpaBus.getName().trim().toLowerCase()))
			return 0;
		else
			return 1;
	}
}
