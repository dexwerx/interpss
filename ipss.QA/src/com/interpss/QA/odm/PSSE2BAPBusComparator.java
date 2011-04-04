package com.interpss.QA.odm;

import java.util.Comparator;

import org.ieee.odm.schema.BusXmlType;

public class PSSE2BAPBusComparator implements Comparator<BusXmlType> {
	public int compare(BusXmlType psseBus, BusXmlType bpaBus) {
		// PSSE uses bus number, BPA uses bus name as the id
		if (psseBus.getName().trim().toLowerCase()
				.equals(bpaBus.getId().trim().toLowerCase()))
			return 0;
		else
			return 1;
	}
}
