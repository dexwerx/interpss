package sensMatrixElem.Machine.impl;

import sensMatrix.StateVariables;
import sensMatrix.NetworkVariables;
import sensMatrixElem.Machine.EConstMach_TSA;
import sensMatrixElem.Machine.Machine_TSA;

import com.interpss.common.util.IpssLogger;
import com.interpss.dstab.mach.EConstMachine;
import com.interpss.dstab.mach.impl.EConstMachineImpl;


public class EConstMach_TSAImpl extends EConstMachineImpl implements EConstMach_TSA   {
	
	private EConstMachine eConstMach=null;
	private double fResidual=0;
	private double gResidual=0;
	private double last_theta=0;
	private double last_omega=0;
	private double last_pm=0;
	private double last_pe=0;
	private double deltaT=0; // step size 

	public EConstMach_TSAImpl(EConstMachine eConstM){
		this.eConstMach=eConstM;
	}
	@Override
	public double calFResidual(StateVariables x) {
		if(x==StateVariables.THETA){
			double w_base=2*this.getDStabBus().getNetwork().getFrequency()*Math.PI; // omega base 
			// what is the definition of the speed
			
			return fResidual=this.angle-this.last_theta-0.5*this.deltaT*(this.speed-1)*w_base
			                  -0.5*this.deltaT*(this.last_omega-1)*w_base;
		}
		else if(x==StateVariables.OMEGA){
			double delta_P_now=this.getPm()-this.getPe()-this.getD()*this.speed;
			double delta_P_last=this.last_pm-this.last_pe-this.getD()*this.last_omega;
			double iTj=1.0/this.getH()/2;
			return fResidual=this.angle-this.last_omega-0.5*this.deltaT*iTj*(delta_P_now+delta_P_last);
		}
		else{
			IpssLogger.getLogger().severe("ERROR:Unsupported state variable:"+x.getName());
		}
		return 0;
	}

	@Override
	public double calGResidual(NetworkVariables y) {
		/*
		 * For busi which has generators
		 * G(i)=sum(Igen)+Inet(i)=0
		 */
		
		return 0;
	}
	@Override
	public double calFx(StateVariables var1, StateVariables var2) {
		
		if(var1==StateVariables.THETA){
	        if(var2==StateVariables.THETA) return 1;
	        else if(var2==StateVariables.OMEGA){
	        	
			  double w_base=2*this.getDStabBus().getNetwork().getFrequency()*Math.PI;
			  return this.deltaT*0.5*w_base; // w_base
	        }
	        else
	        	IpssLogger.getLogger().severe("Error: not support for State Variable "+var2.getName());
			
			}
		else if(var1==StateVariables.OMEGA){
			if(var2==StateVariables.THETA) return 1;
	        else if(var2==StateVariables.OMEGA){
	        	
			  double w_base=2*this.getDStabBus().getNetwork().getFrequency()*Math.PI;
			  return this.deltaT*0.5*w_base; // w_base
	        }
	        else
	        	IpssLogger.getLogger().severe("Error: not support for State Variable "+var2.getName());
			
		}

		return 0;
	}
	@Override
	public double calFy(StateVariables var1, NetworkVariables var2) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public double calGx(NetworkVariables var1, StateVariables var2) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public double calGy(NetworkVariables var1, NetworkVariables var2) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public double cal_pe_busVang() {
		
		return 0;
	}
	@Override
	public double cal_pe_busVmag() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public double cal_pe_theta() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public double cal_pm_omega() {
		// TODO Auto-generated method stub
		return 0;
	}



}
