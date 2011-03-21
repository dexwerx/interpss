package org.interpss.opf.dc.impl;

import org.interpss.opf.dc.DCOPFSolver;
import org.interpss.opf.dc.OpfAlgorithm;

import com.interpss.common.util.IpssLogger;
import com.interpss.opf.OpfNetwork;

public class OpfAlgorithmImpl implements OpfAlgorithm{
    private OpfNetwork opfNet=null;
	private DCOPFSolver dcOPFSolver=null;
	private double angleDiffPanelty=0;
	
	private double minF;
	private double minTVC;
	
//  removed, not used [Mike 3/21/2011]
//	private Hashtable<String,Double> busEqMultiplier=null;
//	private Hashtable<String,Double> genBusIneqMultiplier=null;
//	private Hashtable<String,Double> branchIneqMultiplier=null;
	
	
	@Override
	public boolean checkOPFData() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double getAngleDiffPanelty() {
		
		return this.angleDiffPanelty;
	}

//	@Override
//	public Hashtable<String, Double> getBranchIneqMultiplier() {
//		
//		return this.branchIneqMultiplier;
//	}

//	@Override
//	public Hashtable<String, Double> getBusEqMultiplier() {
//		
//		return this.busEqMultiplier;
//	}

	@Override
	public DCOPFSolver getDCOPFSolver() {
		
		return this.dcOPFSolver;
	}

//	@Override
//	public Hashtable<String, Double> getGenBusIneqMultiplier() {
//		
//		return this.genBusIneqMultiplier;
//	}

	@Override
	public double getMinF() {
		
		return this.minF;
	}

	@Override
	public OpfNetwork getOpfNetwork() {
		
		return this.opfNet;
	}

	@Override
	public boolean runDCOPF() {
		if(this.dcOPFSolver!=null){
		   return this.dcOPFSolver.solveDCOPF();
		}
		else IpssLogger.getLogger().severe("No DCOPFSolver is loaded yet, please check!");
		return false;
	}

	@Override
	public boolean runOPF() {
		
		throw new UnsupportedOperationException();
	}

	@Override
	public void setAngleDiffPanelty(double angleDiffPanelty) {
		this.angleDiffPanelty=angleDiffPanelty;
		
	}

	@Override
	public void setDCOPFSolver(DCOPFSolver solver) {
		this.dcOPFSolver=solver;
		
	}

	@Override
	public void setOpfNetwork(OpfNetwork opfNetwork) {
		this.opfNet=opfNetwork;
		
	}

	@Override
	public double getMinTVC() {
		
		return this.minTVC;
	}

}
