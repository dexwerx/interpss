package org.interpss.ed.sample;

import org.interpss.ed.IEDGenUnit;
import org.interpss.ed.costf.AbstractCostFunc;

public class SampleGenUnitImpl implements IEDGenUnit {
	public CostFuncType costFuncType;
	public AbstractCostFunc costFunc = null;
	public String name;
	public double p = 0.0, penFactor = 1.0;
	public double pmax, pmin;

	public SampleGenUnitImpl(CostFuncType costFuncType) {
		this.costFuncType = costFuncType;
	}
	
	@Override
	public CostFuncType getCostFuncType() {
		return this.costFuncType;
	}

	@Override
	public double getFuelCost() {
		return this.costFunc.getFuelCost();
	}

	@Override
	public double getIhrmax() {
		return this.costFunc.getIhrmax(this.pmax);
	}

	@Override
	public double getIhrmin() {
		return this.costFunc.getIhrmin(this.pmin);
	}

	@Override
	public double getIOCost(int i) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getIOMwPoint(int i) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getP() {
		return this.p;
	}

	@Override
	public double getPenaltyFactor() {
		return this.penFactor;
	}

	@Override
	public double getPmax() {
		return this.pmax;
	}

	@Override
	public double getPmin() {
		return this.pmin;
	}

	@Override
	public double incHeatRate(double unitMw) {
		return this.costFunc.incHeatRate(unitMw);
	}

	@Override
	public double inverserIhr(double unitIhr) throws Exception {
		return this.costFunc.inverserIhr(unitIhr, this.pmax, this.pmin);
	}

	@Override
	public double productionCost(double unitMw) {
		return this.costFunc.productionCost(unitMw);
	}

	@Override
	public void setP(double p) {
		this.p = p;
		
	}

	@Override
	public void setPenaltyFactor(double p) {
		this.penFactor = p;
	}

	@Override
	public String toString() {
		String str = "";
		str += "costFuncType: " + costFuncType + "\n";
		str += "costFunc: " + costFunc.toString();
		str += "name, p, penFactor: " + name + ", " + p + ", " + penFactor + "\n";
		str += "pmax, pmin: " + 
				pmax + ", " + pmin + "\n";
		return str;
	}
}
