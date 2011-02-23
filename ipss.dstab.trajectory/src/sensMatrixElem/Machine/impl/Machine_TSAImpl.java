package sensMatrixElem.Machine.impl;

import com.interpss.dstab.mach.impl.MachineImpl;

import sensMatrix.NetworkVariables;
import sensMatrix.StateVariables;
import sensMatrixElem.Machine.Machine_TSA;

public class Machine_TSAImpl extends MachineImpl implements Machine_TSA{
	protected double last_theta=0;
	protected double busVang;

	
	@Override
	public double calFResidual(StateVariables x) {
		throw new UnsupportedOperationException();
	}

	@Override
	public double calFx(StateVariables var1, StateVariables var2) {
		throw new UnsupportedOperationException();
	}

	@Override
	public double calFy(StateVariables var1, NetworkVariables var12) {
		throw new UnsupportedOperationException();
	}

	@Override
	public double calGResidual(NetworkVariables y) {
		throw new UnsupportedOperationException();
	}

	@Override
	public double calGx(NetworkVariables var1, StateVariables var12) {
		throw new UnsupportedOperationException();
	}

	@Override
	public double calGy(NetworkVariables var1, NetworkVariables var2) {
		throw new UnsupportedOperationException();
	}

	@Override
	public double cal_pe_busVang() {
		throw new UnsupportedOperationException();
	}

	@Override
	public double cal_pe_busVmag() {
		throw new UnsupportedOperationException();
	}

	@Override
	public double cal_pe_theta() {
		throw new UnsupportedOperationException();
	}

	@Override
	public double cal_pm_omega() {
		throw new UnsupportedOperationException();
	}

	@Override
	public double getAngleDiff() {
		return this.last_theta-this.busVang;
	}

	@Override
	public double getCos_AngDiff() {
		return Math.cos(getAngleDiff());
	}

	@Override
	public double getSin_AngDiff() {
		return Math.sin(getAngleDiff());
	}

	@Override
	public void saveStates() {
		this.last_theta=this.angle;
		
	}

}
