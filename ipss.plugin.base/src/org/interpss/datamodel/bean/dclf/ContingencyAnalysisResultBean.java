package org.interpss.datamodel.bean.dclf;

import java.util.List;

import org.interpss.datamodel.bean.BaseBranchBean;
import org.interpss.datamodel.bean.BaseJSONBean;

public class ContingencyAnalysisResultBean extends BaseJSONBean{
	
	public BaseBranchBean constraintBranch;
	public double 
	      caLimit,
	      preFlow,
	      postFlow,
	      precentage;

	@Override
	public boolean validate(List<String> msgList) {		
		return true;
	}

}
