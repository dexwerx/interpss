package org.interpss.opf.dc;

import com.interpss.opf.dclf.DclfOpfNetwork;

/**
 *  DCOPFSolver and its implementation follows the visitor pattern, 
        * It is applied to an OpfNetwork object
        * It implements a OPF algorithm
        * All results will be saved to the OpfNetwork object, after the calculation
 * 
 */

public interface DCOPFSolver {
    public boolean solveDCOPF(DclfOpfNetwork opfNet);

    //      moved to opfNetwork
//	public boolean isDCOPFSolved();
	

//  removed, not used [Mike 3/21/2011]
// 	public Hashtable<String, Double> getBusEqMultiplier();
//	public Hashtable<String, Double> getGenBusIneqMultiplier();
//	public Hashtable<String, Double> getBranchIneqMultiplier();
	
//  not used    
//	public double getMinF();
}
