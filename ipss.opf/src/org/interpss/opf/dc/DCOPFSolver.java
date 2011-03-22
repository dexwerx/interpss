package org.interpss.opf.dc;


public interface DCOPFSolver {
	public boolean isDCOPFSolved();
	
    public boolean solveDCOPF();

//  removed, not used [Mike 3/21/2011]
// 	public Hashtable<String, Double> getBusEqMultiplier();
//	public Hashtable<String, Double> getGenBusIneqMultiplier();
//	public Hashtable<String, Double> getBranchIneqMultiplier();
	
	public double getMinF();
}
