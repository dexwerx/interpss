package sensMatrixElem.Machine.impl;

import sensMatrix.XVariablesEnum;
import sensMatrix.YVariablesEnum;
import sensMatrixElem.Machine.EConstMach_TSA;

import com.interpss.dstab.mach.EConstMachine;
import com.interpss.dstab.mach.impl.EConstMachineImpl;

public class EConstMach_TSAImpl extends EConstMachineImpl implements EConstMach_TSA   {
	private EConstMachine eConstMach=null;
	public EConstMach_TSAImpl(EConstMachine eConstM){
		this.eConstMach=eConstM;
	}
	@Override
	public double calFResidual(XVariablesEnum x) {
		if(x==XVariablesEnum.THETA){
			
		}
		return 0;
	}
	@Override
	public double calGResidual(YVariablesEnum y) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public double calFx(XVariablesEnum var1, XVariablesEnum var2) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public double calFy(XVariablesEnum var1, YVariablesEnum var2) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public double calGx(YVariablesEnum var1, XVariablesEnum var2) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public double calGy(YVariablesEnum var1, YVariablesEnum var2) {
		// TODO Auto-generated method stub
		return 0;
	}


}
