package org.interpss.facts.upfc;

import org.apache.commons.math.complex.Complex;
import org.interpss.facts.general.ConverterLF;
import org.interpss.facts.svc.SVCControlType;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.aclf.AclfNetwork;

// UPFC model in load flow
public class UPFCLF {
	
	private String idi;	// ID of the bus with the shunt converter
	private String idj;	// ID of the bus without the shunt converter
	private ConverterLF shuntConverter;	// Equivalent admittance of the shunt converter's Thevenin equivalent circuit
	private ConverterLF serialConverter;	// Equivalent admittance of the serial converter's Thevenin equivalent circuit
	private UPFCControlType type;	// Control type of the UPFC
	private double tunedValue1;	// First tuned value under current control type
	private double tunedValue2;	// Second tuned value under current control type
	
	private AclfNetwork net;

	// Constructor
	public UPFCLF(String idi, String idj, ConverterLF shuntConverter, ConverterLF serialConverter, UPFCControlType type, double tunedValue1, 
			double tunedValue2, AclfNetwork net) {
		super();
		this.idi = idi;
		this.idj = idj;
		this.shuntConverter = shuntConverter;
		this.serialConverter = serialConverter;
		this.type = type;
		this.tunedValue1 = tunedValue1;
		this.tunedValue2 = tunedValue2;
		this.net = net;
	}

	public String getIdi() {
		return idi;
	}

	public String getIdj() {
		return idj;
	}
	
	// Get shunt power absorbed by the shunt converter
	public Complex getSsh(AclfNetwork net) {
		return new Complex(shuntConverter.getSij(net).getReal(), shuntConverter.getSij(net).getImaginary());
	}
	
	// Get power flow through the serial converter (i ==> j)
	public Complex getSerialSij(AclfNetwork net) {
		return new Complex(serialConverter.getSij(net).getReal(), serialConverter.getSij(net).getImaginary());
	}
	
	// Get power flow through the serial converter (j ==> i)
	public Complex getSerialSji(AclfNetwork net) {
		return new Complex(serialConverter.getSji(net).getReal(), serialConverter.getSji(net).getImaginary());
	}
	
	// Return the error to current tuning
	public double getErr() {
		double err = 100.0;
		// TODO: To be refined.
//		Complex vi = net.getAclfBus(id).getVoltage();
//		if (type == SVCControlType.ConstB) {
//			double vmi = vi.abs();
//			err = Math.abs(this.getSsh(net).getImaginary() / vmi / vmi - tunedValue);
//		}
//		else if (type == SVCControlType.ConstQ)
//			err = Math.abs(this.getSsh(net).getImaginary() - tunedValue);
//		else if (type == SVCControlType.ConstV)
//			err = Math.abs(vi.abs() - tunedValue);
		return err;
	}
	
	// Update vth inside the two converters
	public void update(AclfNetwork net) throws InterpssException {
		Complex vi = net.getAclfBus(idi).getVoltage();
		Complex vj = net.getAclfBus(idj).getVoltage();
		Complex vth1 = this.shuntConverter.getVth();
		Complex vth2 = this.serialConverter.getVth();
		if (type == UPFCControlType.ActiveAndReactivePowerFlow) {	// Control of constant serial power flow injected into the UPFC from terminal j
			Complex[] vths = solveActiveAndReactivePowerFlow(vth1, vth2, vi, vj, this.shuntConverter, this.serialConverter, tunedValue1, 
					tunedValue2);
			this.shuntConverter.setVth(vths[0]);
			this.serialConverter.setVth(vths[1]);
		}
	}

	// Calculate the two vths to match the tuned constant serial power flow
	private Complex[] solveActiveAndReactivePowerFlow(Complex vth1,	Complex vth2, Complex vi, Complex vj, ConverterLF shuntConverter, 
			ConverterLF serialConverter, double tunedValue1, double tunedValue2) {
		double pqerr = 100.0;
		Complex vsh = vth1;
		double vmsh = vsh.abs();
		double thetash = Math.atan2(vsh.getImaginary(), vsh.getReal());
		Complex vse = vth2;
		double vmse = vse.abs();
		double thetase = Math.atan2(vse.getImaginary(), vse.getReal());
		double vmi = vi.abs();
		double thetai = Math.atan2(vi.getImaginary(), vi.getReal());
		double vmj = vj.abs();
		double thetaj = Math.atan2(vj.getImaginary(), vj.getReal());
		double gsh = shuntConverter.getYth().getReal();
		double bsh = shuntConverter.getYth().getImaginary();
		double gse = serialConverter.getYth().getReal();
		double bse = serialConverter.getYth().getImaginary();
		// Iteration by Newton method
		while (pqerr > 0.0000001) {
			// Active power balance equation Fp: active output of v source = 0
			double fp = vmsh * vmsh * gsh - vmi * vmsh * (gsh * Math.cos(thetai - thetash) - bsh * Math.sin(thetai - thetash));
			// Shunt admittance equation Fb: shunt admittance at bus i = Vi / Ishunt
			double fb = vmsh * (gsh * Math.sin(thetash - thetai) + bsh * Math.cos(thetash - thetai)) + vmi * (tunedValue - bsh);
			// Update the mismatch
			pqerr = Math.max(Math.abs(fp), Math.abs(fb));
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
//			err = Math.max(Math.abs(dvmsh), Math.abs(dthetash));
		}
		return new Complex(vmsh * Math.cos(thetash), vmsh * Math.sin(thetash));
	}

}