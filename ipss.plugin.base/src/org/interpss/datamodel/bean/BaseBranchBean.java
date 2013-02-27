package org.interpss.datamodel.bean;

import java.util.List;

import org.interpss.datamodel.bean.datatype.BranchValueBean;
import org.interpss.datamodel.bean.datatype.ComplexBean;

public class BaseBranchBean extends BaseJSONBean {
	/**
	 * branch type code
	 */
	public static enum BranchCode {
		Line, // transmission line
		Xfr, // transformer
		PsXfr // phase-shifting transformer
	};

	public String f_id, // branch from side bus id
			t_id, // branch to side bus id
			cir_id = "1"; // branch circuit id/number

	public long f_num, t_num;

	public int status;
	
	public BranchValueBean 
	ratio,				// xfr branch turn ratio
	ang;				// PsXfr shifting angle

	public ComplexBean 
	    z,					// branch z
	    shunt_y ;     		// branch total shunt y
	public double MVARatingA, MVARatingB, MVARatingC;

	public ComplexBean flow_f2t, // branch power flow from->to
			flow_t2f, // branch power flow to->from
			loss; // branch power loss
	public double 
	cur;			// branch current in amps, for Xfr, it is at the high voltage side
	

	public BranchCode bra_code = BranchCode.Line; // branch type code

	public BaseBranchBean() {
	}

	public boolean validate(List<String> msgList) {
		return true;
	}
}
