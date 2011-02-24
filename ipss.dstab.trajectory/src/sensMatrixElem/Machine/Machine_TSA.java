package sensMatrixElem.Machine;

import com.interpss.dstab.mach.Machine;

import sensMatrix.NetworkVariables;
import sensMatrix.StateVariables;

public interface Machine_TSA {
	
	public Machine getMachine();
	public void setMachine(Machine machine);
	/**
	 * 
	 * @param x- state variables,for EConstMachine,only Theta considered;
	 *           for three-order machine, both theta and Eq1; while for five-order mahcine, theta,Eq11 and Ed11  
	 * @return the residual of the F(i) equation
	 */
	public double calFResidual(StateVariables x);

	/**
	 * 
	 * @param xVar1- the corresponding state variable xi of  this delta_F(i);
	 * @param xVar2- another state variable xj;
	 * @return Fxij element in the Newton iteration matrix
	 */
	public double calFx(StateVariables xVar1,StateVariables xVar2);
	/**
	 * 
	 * @param xVar1--the corresponding state variable xi of  this delta_F(i);
	 * @param yVar1--a related network variable yj;
	 * @return Fyij element in the Newton iteration matrix
	 */
	public double calFy(StateVariables xVar1,NetworkVariables yVar1);

    /**
     * 
     * @return the differential of Pe(electrical power) respect to theta(machine angle):dpe/dtheta
     */
	public double cal_pe_theta();
	/**
	 * 
	 * busVang-- is the voltage angle of the bus that this machine connected to
	 * @return the differential of Pe(electrical power) respect to Vang(bus voltage angle):dPe/dVang
	 * 
	 */
	public double cal_pe_busVang();
	/**
	 * busVmag-- is the voltage magnitude of the bus that this machine connected to
	 * @return the differential of Pe(electrical power) respect to Vmag(bus voltage magnitude):dPe/dVmag
	 */
	public double cal_pe_busVmag();

	
	public double getAngleDiff();
	
	public double getSin_AngDiff();
	
	public double getCos_AngDiff();
	
	public double getI_d();
	
	public double getI_q();
	
	public void saveStates();
	
	/**
	 * [Id;Iq]=1/k*[c1 c3;c2 c1]*[E-Ud;E-Uq];
	 * 
	 * k--a coefficient for calculating the Jacobian of differential equations
	 * @return
	 */
	public double cal_k();
	/**
	 * 
	 * @return C1--a coefficient for calculating the Jacobian of differential equations
	 */
	public double cal_C1();
	/**
	 * 
	 * @return c2--a coefficient for calculating the Jacobian of differential equations
	 */
	public double cal_C2();
	/**
	 * 
	 * @return C3--a coefficient for calculating the Jacobian of differential equations
	 */
	public double cal_C3();



	
	
	
	
}
