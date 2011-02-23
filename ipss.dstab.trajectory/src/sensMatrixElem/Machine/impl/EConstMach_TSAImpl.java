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
	private double last_theta=0;
	private double gResidual=0;
	private double last_omega=0;
	private double last_pm=0;
	private double last_pe=0;
	private double last_busVmag=0;
	private double last_busVang=0;
	private double busVmag=0;
	private double busVang=0;
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
		double M1=this.getDStabBus().getVoltageMag()
		          *(cal_C1()*getCos_AngDiff()-cal_C3()*getSin_AngDiff());
		double M2=-this.getDStabBus().getVoltageMag()
        *(cal_C2()*getCos_AngDiff()+cal_C1()*getSin_AngDiff());
		double pe_vang=this.getDStabBus().getVoltageMag()*
		         ((getI_d()-M2)*getCos_AngDiff()-(M1+getI_q()*getSin_AngDiff()));
		return pe_vang;
	}
	@Override
	public double cal_pe_busVmag() {
		double M3=-(cal_C1()*getSin_AngDiff()+cal_C3()*getCos_AngDiff());
		
        double M4=cal_C2()*getSin_AngDiff()-cal_C1()*getCos_AngDiff();
        
        double pe_vmag=-getI_d()*getSin_AngDiff()-getI_q()*getCos_AngDiff()
                   -this.getDStabBus().getVoltageMag()*(M3*getSin_AngDiff()+M4*getCos_AngDiff());
return pe_vmag;
}
	@Override
	public double cal_pe_theta() {
		return -cal_pe_busVang();
	}
	@Override
	public double cal_pm_omega() {
		// TODO Auto-generated method stub
		return 0;
	}
	private double cal_k(){
		return 1.0/(this.getRa()*this.getRa()+this.getXd1()*this.getXd1());
	}
	private double cal_C1(){
		return this.getRa()*cal_k();
	}
	private double cal_C2(){
		return this.getXd1()*cal_k();
	}
	private double cal_C3(){
		return cal_C2();
	}
	private double getI_d(){
		return -this.getDStabBus().getVoltageMag()*(cal_C1()*getSin_AngDiff()+cal_C3()*getCos_AngDiff());
	}
	private double getI_q(){
		 return this.getDStabBus().getVoltageMag()*(cal_C2()*getSin_AngDiff()-cal_C1()*getCos_AngDiff());
	}
	@Override
	public double getAngleDiff() {
		return this.angle-this.getDStabBus().getVoltageAng();
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
		this.last_busVang=this.getDStabBus().getVoltageAng();
		this.last_busVmag=this.getDStabBus().getVoltageMag();
		this.last_theta=this.angle;
		this.last_omega=this.speed;
		this.last_pm=this.getPm();
		this.last_pe=this.getPe();
	}

	



}
