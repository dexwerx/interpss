package org.interpss.datamodel.bean.aclf;

import java.util.ArrayList;
import java.util.List;

import org.interpss.datamodel.bean.BaseJSONBean;

public class AclfNetBean extends BaseJSONBean {
	public double 
		base_kva;
	
	public List<AclfBusBean> 
		bus_list;
	public List<AclfBranchBean> 
		branch_list;
	
	public AclfNetBean() { bus_list = new ArrayList<>(); branch_list = new ArrayList<>(); }
}