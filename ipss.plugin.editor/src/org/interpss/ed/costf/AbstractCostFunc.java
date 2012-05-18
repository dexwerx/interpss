package org.interpss.ed.costf;

public abstract class AbstractCostFunc {
	protected int curveOrder = 0;
	protected double fuelCost = 1.0;
	protected double maxIhr, minIhr;

	public double getFuelCost() {
		return this.fuelCost;
	}

	public void setFuelCost(double c) {
		this.fuelCost = c;
	}
	
	public abstract double getIhrmax(double pmax);
	public abstract double getIhrmin(double pmin);

	public abstract double incHeatRate(double unitMw);
	public abstract double inverserIhr(double unitIhr, double pmax, double pmin) throws Exception;
	public abstract double productionCost(double unitMw);
	
	public void setIhrLimit(double max, double min) {
		this.maxIhr = max;
		this.minIhr = min;
	}
	
	@Override
	public String toString() {
		String str = "";
		str += "curveOrder, fuelCost, maxIhr, minIhr: " + 
				curveOrder + ", " + fuelCost + ", " + maxIhr + ", " + minIhr + "\n"; 
		return str;
	}
}
