package org.interpss.opf.dc;

import java.util.Hashtable;

import com.interpss.opf.OpfNetwork;

public interface OpfAlgorithm {
	
	public OpfNetwork getOpfNetwork();
	
	public void setOpfNetwork(OpfNetwork opfNetwork);
	/**
	 * perform DC-OPF analysis by calling the solver--QuadProgCalculator
	 * @return 
	 */
    public boolean runDCOPF();
    
    public boolean runOPF();
    
    public DCOPFSolver getDCOPFSolver();
    
    public void setDCOPFSolver(DCOPFSolver solver);
    /**
     * GenCode parameter and branch rating limit are required for OPF, thus they are 
     * the two most important after the normal LF data check.
     * @return true if data check passed,otherwise false 
     */
    public boolean checkOPFData();
    
    public double getMinF();
    
    public double getMinTVC();
    
	public Hashtable<String, Double> getBusEqMultiplier() ;
	
	public Hashtable<String, Double> getGenBusIneqMultiplier();
	
	public Hashtable<String, Double> getBranchIneqMultiplier();
    
    public void setAngleDiffPanelty(double angleDiffPanelty);
    
    public double getAngleDiffPanelty();
}
