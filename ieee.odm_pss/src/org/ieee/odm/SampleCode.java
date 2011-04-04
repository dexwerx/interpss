package org.ieee.odm;

import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.base.BaseJaxbHelper;
import org.ieee.odm.schema.BranchXmlType;
import org.ieee.odm.schema.LoadflowBusXmlType;

public class SampleCode {
	public void sample() {
		// how to access LoadflowBusXmlType from a branchXmlType
		// there is only a busRefXmlType
		// assume we have a branch and the parser object, which stores the ODM model
		BranchXmlType branch = null;
		AclfModelParser parser = null;
		// get from bus Id
		String fromId = BaseJaxbHelper.getRecId(branch.getFromBus());
		// retrieve the bus object
		LoadflowBusXmlType fromBus = parser.getAclfBus(fromId);
	}
}
