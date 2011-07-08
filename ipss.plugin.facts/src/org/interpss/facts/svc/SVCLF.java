package org.interpss.facts.svc;

import org.apache.commons.math.complex.Complex;
import org.interpss.facts.general.ConverterLF;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfGenCode;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;

// SVC model in load flow
public class SVCLF {

	private String id;	// ID of the shunt compensation bus
	private ConverterLF converter;	// Equivalent admittance of the converter's Thevenin equivalent circuit
	private SVCControlType type;	// Control type of the SVC
	private double tunedValue;	// Tuned value under current control type
	private double err;	// Error to the control object
	
	private AclfNetwork net;
	
	// Constructor
	public SVCLF(String id, Complex ysh, SVCControlType type, double tunedValue, AclfNetwork net) {
		super();
		this.id = id;
		this.converter = new ConverterLF(id, "GROUND", ysh);
		this.type = type;
		this.tunedValue = tunedValue;
		this.net = net;
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

	// Update vsh inside the svc converter
	public void update(AclfNetwork net) throws InterpssException {
		Complex vi = net.getAclfBus(id).getVoltage();
		Complex vsh1 = this.converter.getVsh();
		if (type == SVCControlType.ConstB) {	// Control of constant shunt admittance
			Complex vsh = solveConstB(vsh1, vi, this.converter, tunedValue);
			this.converter.setVsh(vsh);
		}
		else if (type == SVCControlType.ConstQ) {	// Control of constant shunt reactive power compensation
			Complex vsh = solveConstQ(vsh1, vi, this.converter, tunedValue);
			this.converter.setVsh(vsh);
		}
		else if (type == SVCControlType.ConstV) {	// Control of constant voltage magnitude
			Complex vsh = solveConstV(vsh1, vi, this.converter, tunedValue);
			this.converter.setVsh(vsh);
		}
		Complex vsh2 = this.converter.getVsh();
		if (type != SVCControlType.ConstV)
			err = (vsh1.subtract(vsh2)).abs();
	}

	// Calculate Vsh to match the tuned constant V
	private Complex solveConstV(Complex vsh1, Complex vi, ConverterLF converter, double tunedValue) throws InterpssException {
		// 1. Change the bus type to be PV bus, Solve the load flow, get the Qsh to be compensated
		AclfNetwork tempNetwork = CoreObjectFactory.createAclfNetwork(this.net.serialize());
		tempNetwork.getAclfBus(id).setGenCode(AclfGenCode.GEN_PV);
		tempNetwork.getAclfBus(id).setVoltageMag(tunedValue);
		double p = tempNetwork.getAclfBus(id).getLoadP();
		double q = tempNetwork.getAclfBus(id).getLoadQ();
		tempNetwork.getAclfBus(id).setGenP(-p);
		tempNetwork.getAclfBus(id).setLoadP(0.0);
		tempNetwork.getAclfBus(id).setLoadQ(0.0);
        LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(tempNetwork);
        tempNetwork.accept(algo);
		double qsh = tempNetwork.getAclfBus(id).getGenResults().getImaginary() + q;
		// 2. Calculate Vsh with constantQ control, control to Qsh
		Complex vsh = solveConstQ(converter.getVsh(), tempNetwork.getAclfBus(id).getVoltage(), converter, qsh);
		net.getAclfBus(id).setVoltage(tempNetwork.getAclfBus(id).getVoltage());	// Bus voltage should be updated, otherwise there will be a non-zero p
//		System.out.println(converter.getSij(net).getReal() + "+j" + converter.getSij(net).getImaginary());
		err = 0.0;
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
		System.out.println("thetai=" + thetai + ", thetash=" + thetash);
		return new Complex(vmsh * Math.cos(thetash), vmsh * Math.sin(thetash));
	}
}