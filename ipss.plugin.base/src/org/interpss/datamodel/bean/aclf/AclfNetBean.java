package org.interpss.datamodel.bean.aclf;

import java.util.ArrayList;
import java.util.List;

public class AclfNetBean {
	public String name, desc;
	public double baseKva;
	
	public List<AclfBusBean> busList;
	public List<AclfBranchBean> branchList;
	
	public AclfNetBean() { busList = new ArrayList<>(); branchList = new ArrayList<>(); }
}