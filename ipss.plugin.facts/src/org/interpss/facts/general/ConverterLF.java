package org.interpss.facts.general;

import org.apache.commons.math.complex.Complex;

import com.interpss.core.aclf.AclfNetwork;

// VSC converter model in load flow
public class ConverterLF {

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
	public Complex getSij(AclfNetwork net) {
		Complex vi = net.getAclfBus(idi).getVoltage();
		Complex vj = new Complex(0.0, 0.0);
		if (!idj.equals("GROUND"))
			vj = net.getAclfBus(idj).getVoltage();
		Complex dv = vi.subtract(vj).subtract(vsh);
		return vi.multiply(dv.conjugate()).multiply(ysh.conjugate());
	}
	
	// Equivalent complex power from bus i to bus j through this converter
	public Complex getSji(AclfNetwork net) {
		Complex vi = net.getAclfBus(idi).getVoltage();
		Complex vj = new Complex(0.0, 0.0);
		if (!idj.equals("GROUND"))
			vj = net.getAclfBus(idj).getVoltage();
		Complex dv = vj.add(vsh).subtract(vi);
		return vj.multiply(dv.conjugate()).multiply(ysh.conjugate());
	}

	public Complex getYsh() {
		return ysh;
	}

	public Complex getVsh() {
		return vsh;
	}

	public void setVsh(Complex vsh) {
		this.vsh = vsh;
	}
	
}