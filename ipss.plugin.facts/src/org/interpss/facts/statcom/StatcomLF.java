package org.interpss.facts.statcom;

import org.apache.commons.math.complex.Complex;
import org.interpss.facts.general.ConverterLF;

import com.interpss.core.aclf.AclfNetwork;

// STATCOM model in load flow
public class StatcomLF {

	private String id;	// ID of the shunt compensation bus
	private ConverterLF converter;	// Equivalent admittance of the converter's Thevenin equivalent circuit
	private StatcomControlType type;	// Control type of the STATCOM
	private double tunedValue;	// Tuned value under current control type
	private double err;	// Error to the control object
	
	// Constructor
	public StatcomLF(String id, Complex ysh, StatcomControlType type, double tunedValue) {
		super();
		this.id = id;
		this.converter = new ConverterLF(id, "GROUND", ysh);
		this.type = type;
		this.tunedValue = tunedValue;
	}

	public String getId() {
		return id;
	}
	
	public Complex getSsh(AclfNetwork net) {
		return new Complex(-converter.getSij(net).getReal(), -converter.getSij(net).getImaginary());
	}

	public double getErr() {
		return err;
	}

	public ConverterLF getConverter() {
		return converter;
	}

	// Update vsh inside the statcom converter
	public void update(AclfNetwork net) {
		Complex vi = net.getAclfBus(id).getVoltage();
		Complex vsh1 = this.converter.getVsh();
		if (type == StatcomControlType.ConstB) {	// Control of constant shunt admittance 
			Complex ycomp = new Complex(0.0, tunedValue);
			this.converter.setVsh(vi.multiply((new Complex(1.0, 0.0)).subtract(ycomp.divide(this.converter.getYsh()))));
		}
		else if (type == StatcomControlType.ConstQ) {	// Control of constant shunt reactive power compensation
			Complex si = new Complex(0.0, tunedValue);
			this.converter.setVsh(vi.add((si.divide(vi)).conjugate().divide(this.converter.getYsh())));
		}
		else if (type == StatcomControlType.ConstV) {	// Control of constant voltage magnitude
			
		}
		Complex vsh2 = this.converter.getVsh();
		err = (vsh1.subtract(vsh2)).abs();
	}
}