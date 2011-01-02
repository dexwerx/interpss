package org.interpss.sample.opf;

import org.interpss.numeric.exp.IpssNumericException;

import com.interpss.core.net.Bus;
import com.interpss.opf.OpfGenBus;
import com.interpss.opf.OpfNetwork;
import com.interpss.opf.common.visitor.IOpfGenBusVisitor;

public class OpfSample {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IpssNumericException {
		OpfNetwork net = Opf3BusTestNet.create3BusNetwork();
		
		net.forEachOpfGenBus(new IOpfGenBusVisitor() {
			public void visit(OpfGenBus opfGen) {
				System.out.println("id, coeffA : " + opfGen.getId() + ", " + opfGen.getCoeffA());
			}
		});
		
		for (Bus bus : net.getBusList()) {
			if (net.isOpfGenBus(bus)) {
				OpfGenBus opfGen = net.toOpfGenBus(bus);
				System.out.println("id, coeffA : " + opfGen.getId() + ", " + opfGen.getCoeffA());
			}
		}
	}
}
