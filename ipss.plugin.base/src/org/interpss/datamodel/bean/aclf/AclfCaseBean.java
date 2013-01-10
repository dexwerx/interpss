package org.interpss.datamodel.bean.aclf;

import java.util.ArrayList;
import java.util.List;

public class AclfCaseBean {
	public String name, desc;
	public double baseKva;
	
	public List<AclfBusBean> busList;
	public List<AclfBranchBean> branchList;
	
	public AclfCaseBean() { busList = new ArrayList<>(); branchList = new ArrayList<>(); }
}