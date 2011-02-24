package sensMatrixElem.Machine.impl;

import com.interpss.common.util.IpssLogger;
import com.interpss.dstab.mach.EConstMachine;
import com.interpss.dstab.mach.Machine;

import sensMatrix.NetworkVariables;
import sensMatrix.StateVariables;
import sensMatrixElem.Machine.Machine_TSA;

public class Machine_TSAImpl implements Machine_TSA{
	protected Machine machine=null;

	protected double fResidual=0;
	protected double gResidual=0;
	protected double last_theta=0;
	protected double last_omega=0;
	protected double last_pm=0;
	protected double last_pe=0;
	protected double last_busVmag=0;
	protected double last_busVang=0;
	protected double deltaT=0; // step size 
	protected double iTj=0;
	
	public Machine_TSAImpl(){
		
	}
	public Machine_TSAImpl(Machine mach){
		this.machine=mach;
		this.iTj=0.5*1/this.machine.getH();
		
	}
	@Override
	public Machine getMachine() {
		return machine;
	}
	@Override
	public void setMachine(Machine machine) {
		this.machine = machine;
	}
	@Override
	public double calFResidual(StateVariables x) {
		if(x==StateVariables.THETA){
			double w_base=2*this.getMachine().getDStabBus().getNetwork().getFrequency()*Math.PI; // omega base 
			// what is the definition of the speed
			
			return fResidual=this.getMachine().getAngle()-this.last_theta-0.5*this.deltaT*(this.getMachine().getSpeed()-1)*w_base
			                  -0.5*this.deltaT*(this.last_omega-1)*w_base;
		}
		else if(x==StateVariables.OMEGA){
			double delta_P_now=this.getMachine().getPm()-this.getMachine().getPe()-this.getMachine().getD()*this.getMachine().getSpeed();
			double delta_P_last=this.last_pm-this.last_pe-this.getMachine().getD()*this.last_omega;
			return fResidual=this.getMachine().getAngle()-this.last_omega-0.5*this.deltaT*iTj*(delta_P_now+delta_P_last);
		}
		else{
			IpssLogger.getLogger().severe("ERROR:Unsupported state variable:"+x.getName());
		}
		return 0;
	}
	@Override
	public double calFx(StateVariables var1, StateVariables var2) {
		
		if(var1==StateVariables.THETA){
	        if(var2==StateVariables.THETA) return 1;
	        else if(var2==StateVariables.OMEGA){
	        	
			  double w_base=2*this.getMachine().getDStabBus().getNetwork().getFrequency()*Math.PI;
			  return 0.5*this.deltaT*w_base; // w_base
	        }
	        else
	        	IpssLogger.getLogger().severe("Error: not support for State Variable "+var2.getName());
			
			}
		else if(var1==StateVariables.OMEGA){
			if(var2==StateVariables.THETA){
				
				return -0.5*this.deltaT*this.iTj*cal_pe_theta();
			}
	        else if(var2==StateVariables.OMEGA){
	        	return -0.5*this.deltaT*this.iTj*this.getMachine().getD();
          
	        }
	        else
	        	IpssLogger.getLogger().severe("Error: not support for State Variable "+var2.getName());
			
		}

		return 0;
	}
	@Override
	public double calFy(StateVariables var1, NetworkVariables var2){
		if(var1==StateVariables.THETA){
			return 0; // theta is not related to network variables
		}
		else if(var1==StateVariables.OMEGA){
			if(var2==NetworkVariables.Vang){
				return -0.5*this.deltaT*iTj*cal_pe_busVang();  // dPe/dVang;
			}
			else {
				return -0.5*this.deltaT*iTj*cal_pe_busVmag();  // dPe/dVmag;
			}
		}
		return 0;
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
	public double getAngleDiff() {
		return this.getMachine().getAngle()-this.getMachine().getDStabBus().getVoltageAng();
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
		this.last_busVang=this.getMachine().getDStabBus().getVoltageAng();
		this.last_busVmag=this.getMachine().getDStabBus().getVoltageMag();
		this.last_theta=this.getMachine().getAngle();
		this.last_omega=this.getMachine().getSpeed();
		this.last_pm=this.getMachine().getPm();
		this.last_pe=this.getMachine().getPe();
	}

	@Override
	public double getI_d() {
		throw new UnsupportedOperationException();
	}

	@Override
	public double getI_q() {
		throw new UnsupportedOperationException();
	}

	@Override
	public double cal_C1() {
		throw new UnsupportedOperationException();
	}

	@Override
	public double cal_C2() {
		throw new UnsupportedOperationException();
	}

	@Override
	public double cal_C3() {
		throw new UnsupportedOperationException();
	}

	@Override
	public double cal_k() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @param last_theta the last_theta to set
	 */
	public void setLast_theta(double last_theta) {
		this.last_theta = last_theta;
	}
	/**
	 * @return the last_theta
	 */
	public double getLast_theta() {
		return last_theta;
	}
	/**
	 * @param last_omega the last_omega to set
	 */
	public void setLast_omega(double last_omega) {
		this.last_omega = last_omega;
	}
	/**
	 * @return the last_omega
	 */
	public double getLast_omega() {
		return last_omega;
	}
	/**
	 * @param last_pm the last_pm to set
	 */
	public void setLast_pm(double last_pm) {
		this.last_pm = last_pm;
	}
	/**
	 * @return the last_pm
	 */
	public double getLast_pm() {
		return last_pm;
	}
	/**
	 * @param last_pe the last_pe to set
	 */
	public void setLast_pe(double last_pe) {
		this.last_pe = last_pe;
	}
	/**
	 * @return the last_pe
	 */
	public double getLast_pe() {
		return last_pe;
	}
	/**
	 * @param last_busVmag the last_busVmag to set
	 */
	public void setLast_busVmag(double last_busVmag) {
		this.last_busVmag = last_busVmag;
	}
	/**
	 * @return the last_busVmag
	 */
	public double getLast_busVmag() {
		return last_busVmag;
	}
	/**
	 * @param last_busVang the last_busVang to set
	 */
	public void setLast_busVang(double last_busVang) {
		this.last_busVang = last_busVang;
	}
	/**
	 * @return the last_busVang
	 */
	public double getLast_busVang() {
		return last_busVang;
	}

	/**
	 * @param deltaT the deltaT to set
	 */
	public void setDeltaT(double deltaT) {
		this.deltaT = deltaT;
	}
	/**
	 * @return the deltaT
	 */
	public double getDeltaT() {
		return deltaT;
	}

}
