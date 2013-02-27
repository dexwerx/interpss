package org.interpss.datamodel.bean.dclf;

import java.util.List;

import org.interpss.datamodel.bean.BaseBusBean;

public class DclfBusResultBean extends BaseBusBean {

	public double pGen, // bus generation
			pLoad; // bus load

	public double shuntG;

	public boolean validate(List<String> msgList) {
		boolean noErr = true;
		return noErr;
	}

}
