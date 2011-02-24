package sensMatrixElem.Machine.impl;

import sensMatrix.StateVariables;
import sensMatrix.NetworkVariables;
import sensMatrixElem.Machine.EConstMach_TSA;
import sensMatrixElem.Machine.Machine_TSA;

import com.interpss.common.util.IpssLogger;
import com.interpss.dstab.mach.EConstMachine;
import com.interpss.dstab.mach.impl.EConstMachineImpl;


public class EConstMach_TSAImpl extends Machine_TSAImpl implements EConstMach_TSA   {


	public EConstMach_TSAImpl(EConstMachine eConstM){
		this.setMachine(eConstM);
		iTj=1.0/this.getMachine().getH()/2;
	}

	@Override
	public double cal_pe_busVang() {
		double M1=this.getMachine().getDStabBus().getVoltageMag()
		             *(cal_C1()*getCos_AngDiff()-cal_C3()*getSin_AngDiff());
		
		double M2=-this.getMachine().getDStabBus().getVoltageMag()
                     *(cal_C2()*getCos_AngDiff()+cal_C1()*getSin_AngDiff());
		// Pe=P_int+Ra*I^2;
		double pe_vang=-this.getMachine().getDStabBus().getVoltageMag()*
		         ((getI_d()-M2)*getCos_AngDiff()-(M1+getI_q())*getSin_AngDiff())+ // corresponding to Ud*Id+Uq*Iq
		         2*this.getMachine().getRa()*(getI_d()*M1+getI_q()*M2);// corresponding to Ra*(Id^2+Iq^2)
		
		return pe_vang;
	}
	@Override
	public double cal_pe_busVmag() {
		double M3=-(cal_C1()*getSin_AngDiff()+cal_C3()*getCos_AngDiff());
		
        double M4=cal_C2()*getSin_AngDiff()-cal_C1()*getCos_AngDiff();
        
        double pe_vmag=-getI_d()*getSin_AngDiff()-getI_q()*getCos_AngDiff()
                   -this.getMachine().getDStabBus().getVoltageMag()*(M3*getSin_AngDiff()+M4*getCos_AngDiff());
       return pe_vmag;
    }
	@Override
	public double cal_pe_theta() {
		return -cal_pe_busVang();
	}
	public double cal_k(){
		 double xdl=((EConstMachine)this.getMachine()).getXd1();
		return 1.0/(this.getMachine().getRa()*this.getMachine().getRa()+xdl*xdl);
	}
	public double cal_C1(){
		return this.getMachine().getRa()*cal_k();
	}
	public double cal_C2(){
		return ((EConstMachine)this.getMachine()).getXd1()*cal_k();
	}
	public double cal_C3(){
		return cal_C2();
	}
	public double getI_d(){
		return -this.getMachine().getDStabBus().getVoltageMag()*(cal_C1()*getSin_AngDiff()+cal_C3()*getCos_AngDiff());
	}
	public double getI_q(){
		 return this.getMachine().getDStabBus().getVoltageMag()*(cal_C2()*getSin_AngDiff()-cal_C1()*getCos_AngDiff());
	}



	



}
