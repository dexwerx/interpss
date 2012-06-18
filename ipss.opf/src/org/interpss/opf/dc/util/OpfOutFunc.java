package org.interpss.opf.dc.util;

import com.interpss.core.net.Bus;
import com.interpss.opf.dclf.DclfOpfBus;
import com.interpss.opf.dclf.DclfOpfGenBus;
import com.interpss.opf.dclf.DclfOpfNetwork;

public class OpfOutFunc {
	public static String opfResultSummary(DclfOpfNetwork opfnet) {
		 final StringBuffer str = new StringBuffer("\n\n");
		 str.append("                     ---DCOPF SOLUTION RESULT SUMMARY---            \n");
		 str.append("      -----------------------------------------------------------------\n");
		 str.append("      busID    OpfGen     optimGen(PU)   optimAngle(RAD)    LMP($/MWh)\n");
		 str.append("      -----------------------------------------------------------------\n");
		 
		 for(Bus b:opfnet.getBusList()) {
			 str.append(String.format("%12s", b.getId()));
			 if(opfnet.isOpfGenBus(b)){
				 DclfOpfGenBus opfBus=(DclfOpfGenBus) b;
				 
				 str.append(String.format("%8s","True"));
				 str.append(String.format("%14.3f",opfBus.getGenP()));
				 //minTVC+=opfBus.getCoeffA()*opfBus.getGenP()+opfBus.getCoeffB()*Math.pow(opfBus.getGenP(), 2);
				 //minTotalCost=minTVC+opfBus.getFixedCost();	
			 }
			 else {
			     str.append("                      ");// just to control the output style, no meaning at all
			 }
			 DclfOpfBus bus = (DclfOpfBus)b;
			 str.append(String.format("%17.3f %15.2f", bus.getVoltageAng(), bus.getLMP()));	
			 str.append("\n");
		 }
		 
		 str.append("      -----------------------------------------------------------------\n\n");
		 str.append(String.format("Minimun Total Variable Cost: %6.3f", 
				 opfnet.getMinTotalVariableCost())+"\n");
		 str.append(String.format("Minimun Total Cost: %6.3f",
				 opfnet.getMinTotalVariableCost()+opfnet.getTotalFixedCost())+"\n");
		 return str.toString();
	}
}
