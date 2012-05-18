package org.interpss.ed.sample;

import org.interpss.ed.IBCoeffMatrix;
import org.interpss.ed.IEDGenUnit;
import org.interpss.ed.IEDGenUnit.CostFuncType;
import org.interpss.ed.costf.CostFuncPolynomial;

public class SampleObjectFactory {
	public static IEDGenUnit[] createEDC1() {
		/*
			FILE EDC1.DAT ------  TEST FILE WITH POLYNOMIAL CURVES , NO LOSSES
			------------------------------------------------------------------
			3  POLY  2  NOLOSS
			UNIT1  100.0  200.0  1.0 [name, low MW limit, high MW limit, fuel cost] 
			100.0
			1.0
			1.0
			UNIT2  50.0  150.0  1.0
			100.0
			1.0
			1.0
			UNIT3  200.0  450.0  1.0
			10.0
			1.0
			1.0
		 */
		int ngen = 3;
		CostFuncType type = CostFuncType.Polynomial;
		
		IEDGenUnit[] genAry = new SampleGenUnitImpl[ngen];
		try {
			genAry[0] = new SampleGenUnitImpl(type);
			SampleGenUnitImpl gen = (SampleGenUnitImpl)genAry[0];
			gen.costFunc = new CostFuncPolynomial(2);
			gen.name = "UNIT1";
			gen.pmin = 100.0;
			gen.pmax = 200.0;
			CostFuncPolynomial cfunc = (CostFuncPolynomial)gen.costFunc;
			cfunc.setFuelCost(1.0);
			cfunc.setCoeff2ndOrder(100.0, 1.0, 1.0);
			
			genAry[1] = new SampleGenUnitImpl(type);
			gen = (SampleGenUnitImpl)genAry[1];
			gen.costFunc = new CostFuncPolynomial(2);
			gen.name = "UNIT2";
			gen.pmin = 50.0;
			gen.pmax = 150.0;
			cfunc = (CostFuncPolynomial)gen.costFunc;
			cfunc.setFuelCost(1.0);
			cfunc.setCoeff2ndOrder(100.0, 1.0, 1.0);
			
			genAry[2] = new SampleGenUnitImpl(type);
			gen = (SampleGenUnitImpl)genAry[2];
			gen.costFunc = new CostFuncPolynomial(2);
			gen.name = "UNIT3";
			gen.pmin = 200.0;
			gen.pmax = 450.0;
			cfunc = (CostFuncPolynomial)gen.costFunc;
			cfunc.setFuelCost(1.0);
			cfunc.setCoeff2ndOrder(10.0, 1.0, 1.0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return genAry;
	}
	
	public static IEDGenUnit[] createExample3A() {
				/*
				FILE EX3A.DAT -------- DATA FOR EXAMPLE 3A IN TEXT
				--------------------------------------------------
				3  POLY  2  NOLOSS
				UNIT1  150.0  600.0  1.1
				510.0
				7.2
				0.00142
				UNIT2  100.0  400.0  1.0
				310.0
				7.85
				0.00194
				UNIT3  50.0  200.0  1.0
				78.0
				7.97
				0.00482
				 */
		int ngen = 3;
		CostFuncType type = CostFuncType.Polynomial;
				
		IEDGenUnit[] genAry = new SampleGenUnitImpl[ngen];
		try {
			genAry[0] = new SampleGenUnitImpl(type);
			SampleGenUnitImpl gen = (SampleGenUnitImpl)genAry[0];
			gen.costFunc = new CostFuncPolynomial(2);
			gen.name = "UNIT1";
			gen.pmin = 150.0;
			gen.pmax = 600.0;
			CostFuncPolynomial cfunc = (CostFuncPolynomial)gen.costFunc;
			cfunc.setFuelCost(1.0);
			cfunc.setCoeff2ndOrder(510.0, 7.2, 0.00142);
			
			genAry[1] = new SampleGenUnitImpl(type);
			gen = (SampleGenUnitImpl)genAry[1];
			gen.costFunc = new CostFuncPolynomial(2);
			gen.name = "UNIT2";
			gen.pmin = 100.0;
			gen.pmax = 400.0;
			cfunc = (CostFuncPolynomial)gen.costFunc;
			cfunc.setFuelCost(1.0);
			cfunc.setCoeff2ndOrder(310.0, 7.85, 0.00194);
			
			genAry[2] = new SampleGenUnitImpl(type);
			gen = (SampleGenUnitImpl)genAry[2];
			gen.costFunc = new CostFuncPolynomial(2);
			gen.name = "UNIT3";
			gen.pmin = 50.0;
			gen.pmax = 200.0;
			cfunc = (CostFuncPolynomial)gen.costFunc;
			cfunc.setFuelCost(1.0);
			cfunc.setCoeff2ndOrder(78.0, 7.97, 0.00482);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return genAry;
	}
	public static IBCoeffMatrix createBCoeffMatrixc() {
		/*
				0.005              <---- B00 coefficient
				0.005 0.005 0.01   <---- B1, B2, B3 coefficients
				0.01  0.005 0.005  <---- B11, B12, B13
				0.005 0.01  0.005  <---- B21, B22, B23
				0.005 0.005 0.02   <---- B31, B32, B33
		 */		
		SampleBCoeffMatrixImpl bcm = new SampleBCoeffMatrixImpl(3);
		bcm.b00 = 0.005;

		bcm.b0[0] = 0.005;
		bcm.b0[1] = 0.005;
		bcm.b0[2] = 0.01;

		bcm.b[0][0] = 0.01;
		bcm.b[0][1] = 0.005;
		bcm.b[0][2] = 0.005;
		
		bcm.b[1][0] = 0.005;
		bcm.b[1][1] = 0.01;
		bcm.b[1][2] = 0.005;

		bcm.b[2][0] = 0.005;
		bcm.b[2][1] = 0.005;
		bcm.b[2][2] = 0.02;
		
		return bcm;
	}
}
