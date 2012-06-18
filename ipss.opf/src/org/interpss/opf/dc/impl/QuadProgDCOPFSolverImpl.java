/*
 * @(#)QuadProgCalculator.java   
 *
 * Copyright (C) 2006-2010 www.interpss.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * @Author Tony Huang, Mike Zhou
 * @Version 1.0
 * @Date 09/15/2010
 * 
 *   Revision History
 *   ================
 *
 */

package org.interpss.opf.dc.impl;

/**
 * This is an implementation of DC-OPF using the Quad Programming optimization algorithm.
 * It depends on the OpfNetworkHelper class to feed Opf net info into the algorithm.
 * 
 * A DCOPFSolver implementation will has to major concepts
        * EqIneqMatrixBuilder for build relevant matrixes
        * An optimization routine    * 
 */

import org.interpss.opf.dc.DCOPFSolver;
import org.interpss.opf.dc.util.Apache2ColtAdapter;

import quadprogj.QuadProgJ;

import com.interpss.core.net.Bus;
import com.interpss.opf.dclf.DclfOpfBus;
import com.interpss.opf.dclf.DclfOpfNetwork;

public class QuadProgDCOPFSolverImpl implements DCOPFSolver{
	private DclfOpfNetwork opfNet=null;
	private QuadProgJ  qpj=null;
//	private boolean isDCOPFSolved=false;
  
	// OPF result
//	private double minF=0;
	private double[] optimX=null;
	private double[] eqMultipliers=null;
	private double[] ineqMultipiers=null;

//  removed, not used [Mike 3/21/2011]
//	private Hashtable<String,Double> busEqMultiplier=null;
//	private Hashtable<String,Double> genBusIneqMultiplier=null;
//	private Hashtable<String,Double> branchIneqMultiplier=null;
	
	
	
	public QuadProgDCOPFSolverImpl(){
	}
	
	public boolean solveDCOPF(DclfOpfNetwork opfNet) {
		this.opfNet=opfNet;
		EqIneqMatrixBuilder helper = new EqIneqMatrixBuilder(opfNet);
		this.qpj= new QuadProgJ(
				Apache2ColtAdapter.trans(helper.formG()),
				Apache2ColtAdapter.trans(helper.formA()),
				Apache2ColtAdapter.trans(helper.formCeq()),
				Apache2ColtAdapter.trans(helper.formBeq()),
				Apache2ColtAdapter.trans(helper.formCiq()),
				Apache2ColtAdapter.trans(helper.formBiq()));
		
		if(solveQP()){
			saveOPFResult();
			this.opfNet.setOpfSolved(true);
		}
		else 
			this.opfNet.setOpfSolved(false);
		return this.opfNet.isOpfSolved();
	}
	
	private boolean solveQP() {
		// NOTE FOR THE SOLUTION STRUCTURE x* = qpj.getMinX()
	    // x* = (p_{G1}...p_{GI}, delta_2...delta_K) for fixed demand
		try {
			this.optimX = qpj.getMinX();
		} catch (Exception e) {
			//e.printStackTrace();
			return false;
		}

		this.eqMultipliers = qpj.getEqMultipliers();
		this.ineqMultipiers = qpj.getIneqMultipiers();
		this.opfNet.setMinF(qpj.getMinF());
		
		return true;
	}
	
	private void saveOPFResult() {
			double[] busAngle=null;

			// extract the angle value
			busAngle=new double[opfNet.getNoActiveBus()-1];
			int noOfGen = opfNet.getNoOfGen();
			for(int k=noOfGen;k<noOfGen+opfNet.getNoActiveBus()-1;k++){
				busAngle[k-noOfGen]=this.optimX[k]; // voltAngle in radians
			}
			
			// set gen P to opfNet bus object
			int genIndex=0;
			for(Bus b:opfNet.getBusList()){
				if(opfNet.isOpfGenBus(b)){
					((DclfOpfBus) b).setGenP(optimX[genIndex]);
					genIndex++;
				}
			}

			// set bus angle and LMP to opfNet bus object
			int nonSwingBusIndex=0;
			int cnt = 0;
			double baseMVA=opfNet.getBaseKva()/1000.0;
			for(Bus b: opfNet.getBusList()){
				DclfOpfBus bus=(DclfOpfBus) b;
				if(!bus.isSwing()){
					bus.setVoltageAng(busAngle[nonSwingBusIndex]);
					nonSwingBusIndex++;
				}
				bus.setLMP(getEqMultipliers()[cnt++]/baseMVA);
			}
	  }
	
	public double[] getEqMultipliers() {
		return eqMultipliers;
	}
	public double[] getIneqMultipiers() {
		return ineqMultipiers;
	}
//	public Hashtable<String, Double> getBusEqMultiplier() {
//		return busEqMultiplier;
//	}
//	public Hashtable<String, Double> getGenBusIneqMultiplier() {
//		return genBusIneqMultiplier;
//	}
//	public Hashtable<String, Double> getBranchIneqMultiplier() {
//		return branchIneqMultiplier;
//	}
//	@Override
//	public boolean isDCOPFSolved() {
//		
//		return isDCOPFSolved;
//	}
//	@Override
//	public double getMinF() {
//		return this.minF;
//	}
}
