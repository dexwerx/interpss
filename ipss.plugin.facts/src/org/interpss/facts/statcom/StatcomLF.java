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
			Complex vsh = solveConstQ(vsh1, vi, this.converter, tunedValue);
			this.converter.setVsh(vsh);
		}
		else if (type == StatcomControlType.ConstV) {	// Control of constant voltage magnitude
			
		}
		Complex vsh2 = this.converter.getVsh();
		err = (vsh1.subtract(vsh2)).abs();
	}

	// Calculate Vsh to match the tuned constant Q
	private Complex solveConstQ(Complex vsh1, Complex vi, ConverterLF converter, double tunedValue) {
		double qerr = 100.0;
		Complex vsh = vsh1;
		double vmsh = vsh.abs();
		double thetash = Math.atan2(vsh.getImaginary(), vsh.getReal());
		double vmi = vi.abs();
		double thetai = Math.atan2(vi.getImaginary(), vi.getReal());
		double gsh = converter.getYsh().getReal();
		double bsh = converter.getYsh().getImaginary();
		// Iteration by Newton method
		while (qerr > 0.0001) {
			// Active power balance equation Fp: active output of v source = 0
			double fp = vmsh * vmsh * gsh - vmi * vmsh * (gsh * Math.cos(thetai - thetash) - bsh * Math.sin(thetai - thetash));
			// Reactive power balance equation Fq: reactive injection at bus i = Qsh
			double fq = tunedValue + vmi * vmi * bsh + vmi * vmsh * (gsh * Math.sin(thetai - thetash) - bsh * Math.cos(thetai - thetash));
			// Update the mismatch
			qerr = Math.max(Math.abs(fp), Math.abs(fq));
			// Jacobian
			double jpv = 2 * vmsh * gsh - vmi * (gsh * Math.cos(thetai - thetash) - bsh * Math.sin(thetai - thetash));	// dFp/dVsh
			double jptheta = - vmi * vmsh * (gsh * Math.sin(thetai - thetash) + bsh * Math.cos(thetai - thetash));	// dFp/dThetash
			double jqv = vmi * (gsh * Math.sin(thetai - thetash) - bsh * Math.cos(thetai - thetash));	// dFq/dVsh
			double jqtheta = - vmi * vmsh * (gsh * Math.cos(thetai - thetash) + bsh * Math.sin(thetai - thetash));	// dFq/dThetash
			// TODO: Solve the mismatch equation, update Vsh and thetash
		}
		return vsh;
	}
}