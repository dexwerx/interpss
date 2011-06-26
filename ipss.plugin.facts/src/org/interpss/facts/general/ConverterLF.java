package org.interpss.facts.general;

import org.apache.commons.math.complex.Complex;

import com.interpss.core.aclf.AclfNetwork;

// VSC converter model in load flow
public class ConverterLF {

	private AclfNetwork net;	// Power network which the converter will be installed
	private String idi;	// ID for the first terminal
	private String idj; // ID for the second terminal
	
	private Complex vsh;	// Equivalent (controlled) voltage source of the converter's Thevenin equivalent circuit
	private Complex ysh;	// Equivalent admittance of the converter's Thevenin equivalent circuit
	
	// Constructor
	public ConverterLF(String idi, String idj, Complex ysh) {
		super();
		this.idi = idi;
		this.idj = idj;
		this.ysh = ysh;
		this.vsh = new Complex(1.0, 0.0);
	}
	
	// Equivalent complex power from bus i to bus j through this converter
	public Complex getSij() {
		double vmi = net.getAclfBus(idi).getVoltageMag();
		double thetai = net.getAclfBus(idi).getVoltageAng();
		Complex vi = new Complex(vmi * Math.cos(thetai), vmi * Math.sin(thetai));
		double vmj = net.getAclfBus(idj).getVoltageMag();
		double thetaj = net.getAclfBus(idj).getVoltageAng();
		Complex vj = new Complex(vmj * Math.cos(thetaj), vmj * Math.sin(thetaj));
		Complex dv = vi.subtract(vj).subtract(vsh);
		return vi.multiply(dv.conjugate()).multiply(ysh.conjugate());
	}
	
	// Equivalent complex power from bus i to bus j through this converter
	public Complex getSji() {
		double vmi = net.getAclfBus(idi).getVoltageMag();
		double thetai = net.getAclfBus(idi).getVoltageAng();
		Complex vi = new Complex(vmi * Math.cos(thetai), vmi * Math.sin(thetai));
		double vmj = net.getAclfBus(idj).getVoltageMag();
		double thetaj = net.getAclfBus(idj).getVoltageAng();
		Complex vj = new Complex(vmj * Math.cos(thetaj), vmj * Math.sin(thetaj));
		Complex dv = vj.add(vsh).subtract(vi);
		return vj.multiply(dv.conjugate()).multiply(ysh.conjugate());
	}
}