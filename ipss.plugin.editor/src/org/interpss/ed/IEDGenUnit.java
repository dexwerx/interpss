package org.interpss.ed;

public interface IEDGenUnit {
	public static enum CostFuncType {Polynomial, PiecewizeIncremental, PiecewizeIO};
	
	public double getP();
	public void setP(double p);
	
	public double getPenaltyFactor();
	public void setPenaltyFactor(double p);

	public double getPmax();
	public double getPmin();

	public CostFuncType getCostFuncType();
	
	public double getIhrmax();
	public double getIhrmin();

	public double getFuelCost();
	
	public double getIOCost(int i);
	public double getIOMwPoint(int i);
	
	/**
     *  Routine to return unit incremental heat rate given unit output in MW
     *  
     * @param unitMw unit mw
     * @return unit incremental heat rate
	 */
	public double incHeatRate(double unitMw);
	
	/**
     * Routine to return unit MW given unit incremental heat rate
     * 
     * @param unitIhr unit inc heat rate
     * @param unit MW stored in unitmw
    */
	public double inverserIhr(double unitIhr) throws Exception;
	
	/**
	 * Routine to return unit production cost given unit output in mw
	 * 
	 * @param unitMw nit mw
	 * @return unit production cost
	 */
	public double productionCost(double unitMw); 
}
