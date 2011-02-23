package sensMatrixElem.Machine;

import sensMatrix.XVariablesEnum;
import sensMatrix.YVariablesEnum;

import com.interpss.dstab.mach.EConstMachine;

public interface EConstMach_TSA extends EConstMachine{
    
	/**
	 * 
	 * @param x- state variables,for EConstMachine,only Theta considered;
	 *           for three-order machine, both theta and Eq1; while for five-order mahcine, theta,Eq11 and Ed11  
	 * @return the residual of the F(i) equation
	 */
	public double calFResidual(XVariablesEnum x);
	/**
	 * 
	 * @param y- network variables,namely Ux,Uy,Ix,Iy
	 * @return the residual of the F(i) equation
	 */
	public double calGResidual(YVariablesEnum y);
	/**
	 * 
	 * @param xVar1- the corresponding state variable xi of  this delta_F(i);
	 * @param xVar2- another state variable xj;
	 * @return Fxij element in the Newton iteration matrix
	 */
	public double calFx(XVariablesEnum xVar1,XVariablesEnum xVar2);
	/**
	 * 
	 * @param xVar1--the corresponding state variable xi of  this delta_F(i);
	 * @param yVar1--a related network variable yj;
	 * @return Fyij element in the Newton iteration matrix
	 */
	public double calFy(XVariablesEnum xVar1,YVariablesEnum yVar1);
	/**
	 * 
	 * @param yVar1--the corresponding network variable yi of  this delta_G(i);
	 * @param xVar1--a related state variable xj
	 * @return Gxij--element in the Newton iteration matrix
	 */
	public double calGx(YVariablesEnum yVar1,XVariablesEnum xVar1);
	/**
	 * 
	 * @param yVar1--the corresponding network variable yi of  this delta_G(i);
	 * @param yVar2--another state variable yj
	 * @return Gyij--element in the Newton iteration matrix
	 */
	public double calGy(YVariablesEnum yVar1,YVariablesEnum yVar2);

	
	
	
	

}
