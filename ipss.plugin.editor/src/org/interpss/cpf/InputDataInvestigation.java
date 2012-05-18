package org.interpss.cpf;

import org.interpss.numeric.datatype.LimitType;

import com.interpss.CoreObjectFactory;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfLoadCode;

public class InputDataInvestigation {
	public void test() {
		/*
		1.About transmission line, current limit(Imax),active power limit(Pmax) and apparent power limit(Smax) are 
		  needed to be added.
		*/
			AclfBranch branch = CoreObjectFactory.createAclfBranch();
			branch.setRatingAmps(1.0);
			branch.setRatingMva1(1.0);
			branch.setRatingMva2(1.0);
			branch.setRatingMva3(1.0);
			
		/*	
		2.About Swing bus,Qmax,Qmin,Vmax,Vmin,and Loss participation coefficient(gamma) are needed to be added.
		*/
			AclfBus bus = CoreObjectFactory.createAclfBus("MyBus", null);
			LimitType limit = new LimitType(999.0, -999.0);
			bus.setQGenLimit(limit);
			limit = new LimitType(1.10, 0.90);
			bus.setVLimit(limit);
			//bus.setLossPartFactor(0.3); should be calculated

		/*	
		3.About PV bus,Vmax,Vmin,and Loss participation coefficient(gamma) are needed to be added.
		*/
			// same as ao
		/*
		4.About PQ bus, it should be determined that if conversion to impedance is allowed.
		*/
			bus.setLoadCode(AclfLoadCode.CONST_P);  // constant power load
			bus.setLoadCode(AclfLoadCode.CONST_Z);  // constant z load

		/*
		5.About load/generation increase direction, it should add a group of data to define the direction of each load 
		  and generator bus. 
		*/
	}
}
