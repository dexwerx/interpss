package sensMatrixElem.Machine.impl;

import java.util.logging.Logger;

import com.interpss.common.util.IpssLogger;
import com.interpss.dstab.mach.EConstMachine;
import com.interpss.dstab.mach.Eq1Machine;

import sensMatrix.StateVariables;
import sensMatrixElem.Machine.Eq1Machine_TSA;

public class Eq1Machine_TSAImpl extends Machine_TSAImpl implements Eq1Machine_TSA {
	
	public Eq1Machine_TSAImpl(Eq1Machine eq1Mach){
		super(eq1Mach);
	}
	public double calFResidual(StateVariables x){
		if(x==StateVariables.THETA||x==StateVariables.OMEGA){
			return super.calFResidual(x);
		}
		else if(x==StateVariables.EQ1){
			return 0;// to do
		}
		else
			IpssLogger.getLogger().severe("Error: invalid input state variable:"+x.getName());
		return 0;
	}

	public double calFx(StateVariables var1, StateVariables var2) {
		if(var1==StateVariables.THETA||var1==StateVariables.OMEGA){
			if(var2==StateVariables.THETA||var2==StateVariables.OMEGA){
				return super.calFx(var1, var2);
			}
			else if(var2==StateVariables.EQ1){
				switch(var1.getValue()){
				case 0: // theta;
					return 0;
				case 1: // omega;
					return -0.5*this.getDeltaT()*this.iTj*cal_pe_Eq1();//;
				}
			}
			else IpssLogger.getLogger().severe("Error: invalid input state variables var2: "+var2.getName());
			
		}
		else if(var1==StateVariables.EQ1){
			switch(var2.getValue()){
			case 0: // theta
			case 1:// omega
			case 2: // Eq1	
			default:IpssLogger.getLogger().severe("Error: invalid input state variables var2: "+var2.getName());	
			}
			
		}
		else IpssLogger.getLogger().severe("Error: invalid input state variables var1: "+var1.getName());
		return 0;
	}
	public double cal_pe_Eq1() {
		return cal_N1();
	}
	@Override
	public double cal_pe_busVang() {
		return 0;// to do 
		
	}
	/*
	 *   Syn.Gp(:,3) = DAE.V(Syn.bus).*(-Syn.c3.*ss - Syn.c1.*cc);
         Syn.Gp(:,4) = DAE.V(Syn.bus).*(-Syn.c1.*ss + Syn.c2.*cc);
         Syn.Gq(:,3) = DAE.V(Syn.bus).*(-Syn.c3.*cc + Syn.c1.*ss);
         Syn.Gq(:,4) = DAE.V(Syn.bus).*(-Syn.c1.*cc - Syn.c2.*ss);

         N1 = (Syn.Gp(:,3)-2*ra.*(Syn.Id.*Syn.c3+Syn.Iq.*Syn.c1)).*iM;
         N2 = (Syn.Gp(:,4)-2*ra.*(Syn.Id.*Syn.c1-Syn.Iq.*Syn.c2)).*iM;
	 * @return
	 */
	public double cal_N1(){
		double gp=-getVmag()*(-cal_C3()*getSin_AngDiff()-cal_C1()*getCos_AngDiff());
		return (gp+2*this.getMachine().getRa()*(getI_d()*cal_C3()+getI_q()*cal_C1()));
	}
	public double cal_N2(){
		double gp=-getVmag()*(-cal_C1()*getSin_AngDiff()+cal_C2()*getCos_AngDiff());
		return (gp+2*this.getMachine().getRa()*(getI_d()*cal_C1()+getI_q()*cal_C2()));
	}
	private double getVmag(){
		return this.getMachine().getDStabBus().getVoltageMag();
	}
	

}
