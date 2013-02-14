package org.interpss.datamodel.bean.dclf;

import java.util.ArrayList;
import java.util.List;

import org.interpss.datamodel.bean.BaseJSONBean;
import org.interpss.datamodel.bean.BaseNetBean;

public class DclfSenResultBean extends BaseJSONBean {	
	

	public List<GSFResultBean> gsf_list; 
	public List<LODFResultBean> lodf_list; 
	public List<ContingencyAnalysisResultBean> ca_List; //

	//public String contingency;
	
	public DclfSenResultBean() {
		gsf_list = new ArrayList<GSFResultBean>();
		lodf_list = new ArrayList<LODFResultBean>();
		ca_List = new ArrayList<ContingencyAnalysisResultBean>();
	}

	@Override
	public boolean validate(List<String> msgList) {		
		return true;
	}
}
