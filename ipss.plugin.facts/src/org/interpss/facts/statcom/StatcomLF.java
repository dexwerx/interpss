package org.interpss.facts.statcom;

import org.apache.commons.math.complex.Complex;
import org.interpss.facts.general.ConverterLF;

import com.interpss.core.aclf.AclfNetwork;

// STATCOM model in load flow
public class StatcomLF {

	AclfNetwork net;	// Power network which the STACOM will installed in
	private String id;	// ID of the shunt compensation bus
	private ConverterLF converter;	// Equivalent admittance of the converter's Thevenin equivalent circuit
	private StatcomControlType type;	// Control type of the STATCOM
	private double tunedValue;	// Tuned value under current control type
	private double err;	// Error to the control object
	
	// Constructor
	public StatcomLF(AclfNetwork net, String id, Complex ysh, StatcomControlType type, double tunedValue) {
		super();
		this.net = net;
		this.id = id;
		this.converter = new ConverterLF(net, id, "GROUND", ysh);
		this.type = type;
		this.tunedValue = tunedValue;
	}

	public String getId() {
		return id;
	}
	
	public Complex getSsh() {
		return new Complex(-converter.getSij().getReal(), -converter.getSij().getImaginary());
	}

	public double getErr() {
		return err;
	}

	public ConverterLF getConverter() {
		return converter;
	}

	// Update vsh inside the statcom converter
	public void update() {
		Complex vi = net.getAclfBus(id).getVoltage();
		if (type == StatcomControlType.ConstB) {	// Control of constant shunt admittance 
			
		}
		else if (type == StatcomControlType.ConstQ) {	// Control of constant shunt reactive power compensation
			Complex vsh1 = this.converter.getVsh();
			Complex si = new Complex(0.0, tunedValue);
			this.converter.setVsh(vi.add((si.divide(vi)).conjugate().divide(this.converter.getYsh())));
			Complex vsh2 = this.converter.getVsh();
			err = (vsh1.subtract(vsh2)).abs();
		}
		else if (type == StatcomControlType.ConstV) {	// Control of constant voltage magnitude
			
		}
	}
}