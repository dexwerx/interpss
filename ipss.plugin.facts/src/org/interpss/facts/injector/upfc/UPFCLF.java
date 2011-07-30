package org.interpss.facts.injector.upfc;

import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealMatrixImpl;
import org.interpss.facts.general.ConverterLF;
import org.interpss.numeric.sparse.SparseEqnComplex;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfGenCode;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;

// UPFC model in load flow
@SuppressWarnings("deprecation")
public class UPFCLF {
	
	private String idi;	// ID of the bus with the shunt converter
	private String idj;	// ID of the bus without the shunt converter
	private String vidj;
	private ConverterLF shuntConverter;	// Equivalent admittance of the shunt converter's Thevenin equivalent circuit
	private ConverterLF serialConverter;	// Equivalent admittance of the serial converter's Thevenin equivalent circuit
	private UPFCControlType type;	// Control type of the UPFC
	private double tunedValue1;	// First tuned value under current control type
	private double tunedValue2;	// Second tuned value under current control type
	private double tunedVi;	// Tuned voltage at the bus with the shunt converter
	
	private AclfNetwork net;

	// Constructor
	public UPFCLF(String branchID, boolean atFromBus, Complex ysh, Complex yse, UPFCControlType type, double tunedValue1, double tunedValue2, double tunedVi, AclfNetwork net) {
		super();
		AclfBranch thisBranch = net.getAclfBranch(branchID);
		if (atFromBus) {
			this.idi = thisBranch.getFromBusId();
			this.idj = thisBranch.getToBusId();
		}
		else {
			this.idj = thisBranch.getFromBusId();
			this.idi = thisBranch.getToBusId();
		}
		// Modify the topology of the original network
		// ** Create the virual bus
		vidj = "UPFC_" + idi + "_" + idj;
		AclfBus virtualBus = CoreObjectFactory.createAclfBus(vidj, net);
		virtualBus.setBaseVoltage(net.getAclfBus(idi).getBaseVoltage());
		// ** Set the terminal of the original transmission line to be the virtual bus
		if (atFromBus)
			thisBranch.setFromBus(virtualBus);
		else
			thisBranch.setToBus(virtualBus);
		// ** Install a virtual transmission line to avoid the disconnectivity
		AclfBranch virtualBranch = CoreObjectFactory.createAclfBranch();
		virtualBranch.setAttributes("Virtual Branch", "", "1");
		virtualBranch.setZ(new Complex(0.0, 9999.9));
		net.addBranch(virtualBranch, idi, idj);
		this.shuntConverter = new ConverterLF(idi, "GROUND", ysh);
		this.serialConverter = new ConverterLF(idi, vidj, yse);
		this.type = type;
		this.tunedValue1 = tunedValue1;
		this.tunedValue2 = tunedValue2;
		this.tunedVi = tunedVi;
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
		if (type == UPFCControlType.ActiveAndReactivePowerFlow) {
			double perr = Math.abs(getSerialSji(net).getReal() - tunedValue1);
			double qerr = Math.abs(getSerialSji(net).getImaginary() - tunedValue2);
			err = Math.max(perr, qerr);
		}
		return err;
	}
	
	public ConverterLF getShuntConverter() {
		return shuntConverter;
	}

	public ConverterLF getSerialConverter() {
		return serialConverter;
	}

	public String getVidj() {
		return vidj;
	}

	// Update vth inside the two converters
	public void update(AclfNetwork net) throws InterpssException {
		Complex vi = net.getAclfBus(idi).getVoltage();
		Complex vvj = net.getAclfBus(vidj).getVoltage();
		Complex vth1 = this.shuntConverter.getVth();
		Complex vth2 = this.serialConverter.getVth();
		if (type == UPFCControlType.ActiveAndReactivePowerFlow) {	// Control of constant serial power flow injected into the UPFC from terminal j
			Complex[] vths = solveActiveAndReactivePowerFlow(vth1, vth2, vi, vvj, this.shuntConverter, this.serialConverter, tunedValue1, 
					tunedValue2);
			this.shuntConverter.setVth(vths[0]);
			this.serialConverter.setVth(vths[1]);
		}
	}

	// Calculate the two vths to match the tuned constant serial power flow
	private Complex[] solveActiveAndReactivePowerFlow(Complex vth1,	Complex vth2, Complex vi, Complex vvj, ConverterLF shuntConverter, 
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
		double vmj = vvj.abs();
		double thetaj = Math.atan2(vvj.getImaginary(), vvj.getReal());
		double gsh = shuntConverter.getYth().getReal();
		double bsh = shuntConverter.getYth().getImaginary();
		double gse = serialConverter.getYth().getReal();
		double bse = serialConverter.getYth().getImaginary();
		// Get the calculated Q injection by set bus i to be a PV bus
		AclfGenCode oldGenCode = net.getAclfBus(idi).getGenCode();
		if (oldGenCode != AclfGenCode.GEN_PV && oldGenCode != AclfGenCode.SWING)
			net.getAclfBus(idi).setGenCode(AclfGenCode.GEN_PV);
		else {
			System.out.println("This bus cannot be tuned to the target value.");
			tunedVi = net.getAclfBus(idi).getVoltageMag();
		}
		net.getAclfBus(idi).setVoltageMag(tunedVi);
//		System.out.println(net.net2String());
        LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
        algo.setId("Load flow with UPFC");
        SparseEqnComplex m = net.formYMatrix();
        System.out.println(m);
        algo.loadflow();
        // Calculated Q injection
		double qin = net.getAclfBus(idi).getGenResults().getImaginary() - net.getAclfBus(idi).getLoadQ();
		// Iteration by Newton method
		while (pqerr > 0.0000001) {
			// expression of F(X)
			double[] fx = new double[4];
			// Q injection balancing function: Qin - Qsh - Qse = 0
			fx[0] = qin + vmi * vmi * (bsh + bse) + vmi * vmj * (gse * Math.sin(thetai - thetaj) - bse * Math.cos(thetai - thetaj)) + 
					vmi * vmsh * (gsh * Math.sin(thetai - thetash) - bsh * Math.cos(thetai - thetash)) + 
					vmi * vmse * (gse * Math.sin(thetai - thetase) - bse * Math.cos(thetai - thetase));
			pqerr = Math.abs(fx[0]);
			// Active power balance equation: active power exchange through the DC link is lossless: PEsh - PEse = 0
			fx[1] = gsh * (vmsh * vmi * Math.cos(thetash - thetai) - vmsh * vmsh) + bsh * vmsh * vmi * Math.sin(thetash - thetai) - 
					gse * (vmse * vmj * Math.cos(thetase - thetaj) + vmse * vmse - vmse * vmi * Math.cos(thetase - thetai)) - 
					bse * (vmse * vmj * Math.sin(thetase - thetaj) - vmse * vmi * Math.sin(thetase - thetai));
			if (pqerr < Math.abs(fx[1]))
				pqerr = Math.abs(fx[1]);
			// Controlled active power equation
			fx[2] = vmj * vmj * gse - vmi * vmj * (gse * Math.cos(thetaj - thetai) + bse * Math.sin(thetaj - thetai)) + 
					vmj * vmse * (gse * Math.cos(thetaj - thetase) + bse * Math.sin(thetaj - thetase)) - tunedValue1;
			if (pqerr < Math.abs(fx[2]))
				pqerr = Math.abs(fx[2]);
			// Controlled reactive power equation
			fx[3] = -vmj * vmj * bse - vmi * vmj * (gse * Math.sin(thetaj - thetai) - bse * Math.cos(thetaj - thetai)) + 
					vmj * vmse * (gse * Math.sin(thetaj - thetase) - bse * Math.cos(thetaj - thetase)) - tunedValue2;
			if (pqerr < Math.abs(fx[3]))
				pqerr = Math.abs(fx[3]);
			// Jacobian
			double[][] jaco = new double[4][4];
			jaco[0][0] = vmi * (gsh * Math.sin(thetai - thetash) - bsh * Math.cos(thetai - thetash));	// d(dqin)/dvsh
			jaco[0][1] = vmi * vmsh * (-gsh * Math.cos(thetai - thetash) - bsh * Math.sin(thetai - thetash));	// d(dqin)/dthetash
			jaco[0][2] = vmi * (gse * Math.sin(thetai - thetase) - bse * Math.cos(thetai - thetase));	// d(dqin)/dvse
			jaco[0][3] = vmi * vmse * (-gse * Math.cos(thetai - thetase) - bse * Math.sin(thetai - thetase));	// d(dqin)/dthetase
			jaco[1][0] = gsh * (vmi * Math.cos(thetash - thetai) - 2 * vmsh) + bsh * vmi * Math.sin(thetash - thetai);	// d(dpe)/dvsh
			jaco[1][1] = -gsh * vmsh * vmi * Math.sin(thetash - thetai) + bsh * vmsh * vmi * Math.cos(thetash - thetai);	// d(dpe)/dthetash
			jaco[1][2] = -gse * (vmj * Math.cos(thetase - thetaj) + 2 * vmse - vmi * Math.cos(thetase - thetai)) - 
					bse * (vmj * Math.sin(thetase - thetaj) - vmi * Math.sin(thetase - thetai));	// d(dpe)/dvse
			jaco[1][3] = gse * (vmse * vmj * Math.sin(thetase - thetaj) - vmse * vmi * Math.sin(thetase - thetai)) - 
					bse * (vmse * vmj * Math.cos(thetase - thetaj) - vmse * vmi * Math.cos(thetase - thetai));	// d(dpe)/dthetase
			jaco[2][0] = 0.0;	// d(dpji)/dvsh
			jaco[2][1] = 0.0;	// d(dpji)/dthetash
			jaco[2][2] = vmj * (gse * Math.cos(thetaj - thetase) + bse * Math.sin(thetaj - thetase));	// d(dpji)/dvse
			jaco[2][3] = vmj * vmse * (gse * Math.sin(thetaj - thetase) - bse * Math.cos(thetaj - thetase));	// d(dpji)/dvse
			jaco[3][0] = 0.0;	// d(dqji)/dvsh
			jaco[3][1] = 0.0;	// d(dqji)/dthetash
			jaco[3][2] = vmj * (gse * Math.sin(thetaj - thetase) - bse * Math.cos(thetaj - thetase));	// d(dqji)/dvse
			jaco[3][3] = -vmj * vmse * (gse * Math.cos(thetaj - thetase) + bse * Math.sin(thetaj - thetase));	// d(dqji)/dvse
			// Solve the mismatch equation
			RealMatrix jacoMatrix = new RealMatrixImpl(jaco);
//			System.out.println(jacoMatrix.toString());
			double[] dx = jacoMatrix.solve(fx);
			// Update Vsh and Vse
			vmsh -= dx[0];
			thetash -= dx[1];
			vmse -= dx[2];
			thetase -= dx[3];
//			err = Math.max(Math.abs(dvmsh), Math.abs(dthetash));
		}
		Complex[] vths = new Complex[2];
		vths[0] = new Complex(vmsh * Math.cos(thetash), vmsh * Math.sin(thetash));
		vths[1] = new Complex(vmse * Math.cos(thetase), vmse * Math.sin(thetase));
		// Roll back the gen type of this bus
		net.getAclfBus(idi).setGenCode(oldGenCode);
		return vths;
	}

}