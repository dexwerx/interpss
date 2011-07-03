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
			Complex vsh = solveConstB(vsh1, vi, this.converter, tunedValue);
			this.converter.setVsh(vsh);
		}
		else if (type == StatcomControlType.ConstQ) {	// Control of constant shunt reactive power compensation
			Complex vsh = solveConstQ(vsh1, vi, this.converter, tunedValue);
			this.converter.setVsh(vsh);
		}
		else if (type == StatcomControlType.ConstV) {	// Control of constant voltage magnitude
			Complex vsh = solveConstV(vsh1, vi, this.converter, tunedValue);
			this.converter.setVsh(vsh);
		}
		Complex vsh2 = this.converter.getVsh();
		err = (vsh1.subtract(vsh2)).abs();
	}

	// Calculate Vsh to match the tuned constant V
	private Complex solveConstV(Complex vsh1, Complex vi, ConverterLF converter, double tunedValue) {
		double verr = 100.0;
		Complex vsh = vsh1;
		double vmsh = vsh.abs();
		double thetash = Math.atan2(vsh.getImaginary(), vsh.getReal());
		double vmi = vi.abs();
		double thetai = Math.atan2(vi.getImaginary(), vi.getReal());
		double gsh = converter.getYsh().getReal();
		double bsh = converter.getYsh().getImaginary();
		double rsh = gsh / (gsh * gsh + bsh * bsh);
		double xsh = -bsh / (gsh * gsh + bsh * bsh);
		double psh = vmi * vmi * gsh - vmi * vmsh * (gsh * Math.cos(thetai - thetash) + bsh * Math.sin(thetai - thetash));
		double qsh = -tunedValue * tunedValue * bsh - tunedValue * vmsh * (gsh * Math.sin(thetai - thetash) - bsh * Math.cos(thetai - thetash));
		// Iteration by Newton method
		while (verr > 0.000001) {
			Complex newVi = new Complex(tunedValue * Math.cos(thetash), tunedValue * Math.sin(thetash));
			thetai = Math.atan2(newVi.getImaginary(), newVi.getReal());
			vsh = solveConstQ(vsh1, newVi, converter, -qsh);
			vmsh = vsh.abs();
			thetash = Math.atan2(vsh.getImaginary(), vsh.getReal());
			verr = Math.abs(vsh.abs() - vsh1.abs());
			qsh = -tunedValue * tunedValue * bsh - tunedValue * vmsh * (gsh * Math.sin(thetai - thetash) - bsh * Math.cos(thetai - thetash));
			vsh1 = vsh;
		}
		return vsh;
	}

	// Calculate Vsh to match the tuned constant B
	private Complex solveConstB(Complex vsh1, Complex vi, ConverterLF converter, double tunedValue) {
		double berr = 100.0;
		Complex vsh = vsh1;
		double vmsh = vsh.abs();
		double thetash = Math.atan2(vsh.getImaginary(), vsh.getReal());
		double vmi = vi.abs();
		double thetai = Math.atan2(vi.getImaginary(), vi.getReal());
		double gsh = converter.getYsh().getReal();
		double bsh = converter.getYsh().getImaginary();
		// Iteration by Newton method
		while (berr > 0.00001) {
			// Active power balance equation Fp: active output of v source = 0
			double fp = vmsh * vmsh * gsh - vmi * vmsh * (gsh * Math.cos(thetai - thetash) - bsh * Math.sin(thetai - thetash));
			// Shunt admittance equation Fb: shunt admittance at bus i = Vi / Ishunt
			double fb = vmsh * (gsh * Math.sin(thetash - thetai) + bsh * Math.cos(thetash - thetai)) + vmi * (tunedValue - bsh);
			// Update the mismatch
			berr = Math.max(Math.abs(fp), Math.abs(fb));
			// Jacobian
			double a = 2 * vmsh * gsh - vmi * (gsh * Math.cos(thetai - thetash) - bsh * Math.sin(thetai - thetash));	// dFp/dVsh
			double b = - vmi * vmsh * (gsh * Math.sin(thetai - thetash) + bsh * Math.cos(thetai - thetash));	// dFp/dThetash
			double c = gsh * Math.sin(thetash - thetai) + bsh * Math.cos(thetash - thetai);	// dFb/dVsh
			double d = vmsh * (gsh * Math.cos(thetash - thetai) - bsh * Math.sin(thetash - thetai));	// dFb/dThetash
			// Solve the mismatch equation
			double det = a * d - b * c;
			double dvmsh = (d * fp - b * fb) / det;
			double dthetash = (-c * fp + a * fb) / det;
			// Update Vsh and thetash
			vmsh -= dvmsh;
			thetash -= dthetash;
		}
		return new Complex(vmsh * Math.cos(thetash), vmsh * Math.sin(thetash));
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
			// Reactive power balance equation Fq: reactive injection at bus i = -Qsh ("-" means injecting other than absorbing)
			double fq = -tunedValue + vmi * vmi * bsh + vmi * vmsh * (gsh * Math.sin(thetai - thetash) - bsh * Math.cos(thetai - thetash));
			// Update the mismatch
			qerr = Math.max(Math.abs(fp), Math.abs(fq));
			// Jacobian
			double a = 2 * vmsh * gsh - vmi * (gsh * Math.cos(thetai - thetash) - bsh * Math.sin(thetai - thetash));	// dFp/dVsh
			double b = - vmi * vmsh * (gsh * Math.sin(thetai - thetash) + bsh * Math.cos(thetai - thetash));	// dFp/dThetash
			double c = vmi * (gsh * Math.sin(thetai - thetash) - bsh * Math.cos(thetai - thetash));	// dFq/dVsh
			double d = - vmi * vmsh * (gsh * Math.cos(thetai - thetash) + bsh * Math.sin(thetai - thetash));	// dFq/dThetash
			// Solve the mismatch equation
			double det = a * d - b * c;
			double dvmsh = (d * fp - b * fq) / det;
			double dthetash = (-c * fp + a * fq) / det;
			// Update Vsh and thetash
			vmsh -= dvmsh;
			thetash -= dthetash;
		}
		return new Complex(vmsh * Math.cos(thetash), vmsh * Math.sin(thetash));
	}
}