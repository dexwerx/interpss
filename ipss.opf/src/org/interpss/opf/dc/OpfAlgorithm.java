package org.interpss.opf.dc;

public interface OpfAlgorithm {
	/**
	 * perform DC-OPF analysis by calling the solver--QuadProgCalculator
	 * @return 
	 */
    public boolean runDCOPF();
    

    /**
     * GenCode parameter and branch rating limit are required for OPF, thus they are 
     * the two most important after the normal LF data check.
     * @return true if data check passed,otherwise false 
     */
    public boolean checkOPFData();
    
    public void setAngleDiffPanelty(double angleDiffPanelty);
    
    public double getAngleDiffPanelty();
}
