/*
 * @(#)OpfNetworkHelper.java   
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
 * This a helper class for creating matrix/vector info from the wrapped OpfNetwork object
 * to feed into the QuafProgCalculator for DC-OPF calculation
 */

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.commons.math.linear.RealMatrix;

import com.interpss.common.util.IpssLogger;
import com.interpss.core.net.Branch;
import com.interpss.core.net.Bus;
import com.interpss.opf.dclf.DclfOpfBranch;
import com.interpss.opf.dclf.DclfOpfBus;
import com.interpss.opf.dclf.DclfOpfGenBus;
import com.interpss.opf.dclf.DclfOpfNetwork;

public class EqIneqMatrixBuilder {
	public final static double DEFAULT_BIJ = 10000; 
		// 10000=1/0.0001 ,namely set the line Xij minimum to be 0.0001;
	
	private DclfOpfNetwork opfNet = null;
	
	public EqIneqMatrixBuilder(DclfOpfNetwork opfNet) {
		this.opfNet = opfNet;
		this.formBusIndexTable();
	}

	/**
	 * Form bus index, bus sortNumber field is used for bus index. This
	 * method has to be called at the first before calling any method of
	 * this help class
	 */
	private void formBusIndexTable() {
		int cnt = 0;
		for (Bus b : opfNet.getBusList()) {
			b.setSortNumber(cnt++);
		}
	}
	
	// cost matrix composed by coefficient 2*bi*(baseMVA^2)	
	public Array2DRowRealMatrix formU() {
		int numOfGen = opfNet.getNoOfGen();
		double baseMVA=opfNet.getBaseKva()/1000.0;
		Array2DRowRealMatrix U = new Array2DRowRealMatrix(numOfGen, numOfGen);
		int genIndex=0;
		try{
			for(Bus b: opfNet.getBusList()){
				if(opfNet.isOpfGenBus(b)){
					U.setEntry(genIndex, genIndex, 
							(opfNet.toOpfGenBus(b)).getCoeffB()*baseMVA*baseMVA*2);
					genIndex++;
				}
			}
		}catch(Exception e){
			//e.printStackTrace();
			IpssLogger.getLogger().severe(e.toString());
		}
		return U;
	}

	// angel difference weight matrix	
	private Array2DRowRealMatrix formAngleDiffWeightMatrix() {
		int numOfBus = opfNet.getNoActiveBus();
		Array2DRowRealMatrix angleDiffWeight = new Array2DRowRealMatrix(numOfBus, numOfBus);

		for (Bus busi : opfNet.getBusList()) {
			//double ADWij = 0;
			//double ADWii = 0;
			int i = busi.getSortNumber();
			for (Branch bra : busi.getFromBranchList()) {
				Bus busj = bra.getToBus();
				int j = busj.getSortNumber();
				double ADWij = -1;
				//ADWii++;
				angleDiffWeight.setEntry(i, j, ADWij);
				angleDiffWeight.setEntry(j, i, ADWij);

			}
			angleDiffWeight.setEntry(i, i, busi.getBranchList().size());
		}
		return (Array2DRowRealMatrix)angleDiffWeight
					.scalarMultiply(2*opfNet.getAnglePenaltyFactor());
	}
		
	// reduced angle difference weight matrix
	private Array2DRowRealMatrix formReducedADWMatrix() {
		Array2DRowRealMatrix angleDiffWeight = formAngleDiffWeightMatrix();
		try {
			int[] selectedRows = this.getNonSwingBusRows();
			return (Array2DRowRealMatrix) angleDiffWeight.getSubMatrix(selectedRows, selectedRows);
		} catch (Exception e) {
			//e.printStackTrace();
			IpssLogger.getLogger().severe(e.toString());
		}
		return null;
	}

	// G={U 0;0 Wrr}
	public Array2DRowRealMatrix formG() {
		RealMatrix U_matrix = formU();;
		RealMatrix Wrr_matrix = formReducedADWMatrix();
		int gcnt = U_matrix.getRowDimension() + Wrr_matrix.getRowDimension();
		Array2DRowRealMatrix G = new Array2DRowRealMatrix(gcnt, gcnt);
		G.setSubMatrix(U_matrix.getData(), 0, 0);
		G.setSubMatrix(Wrr_matrix.getData(), U_matrix.getRowDimension(), U_matrix.getColumnDimension());
		return G;
	}

	public ArrayRealVector formA() {
		int numOfGen = opfNet.getNoOfGen(),
				numOfBus = opfNet.getNoActiveBus();
		ArrayRealVector A = new ArrayRealVector(numOfGen + numOfBus - 1);
		A.set(0, getGenCoeffAVector());
		return A;
	}
		
	private ArrayRealVector getGenCoeffAVector(){
		int numOfGen=opfNet.getNoOfGen();
		double baseMVA=opfNet.getBaseKva()/1000.0;
		ArrayRealVector genFixCostVector =new ArrayRealVector(numOfGen);
        int i=0;
		// get the constraint data from network file ;
	    for (Bus bus:opfNet.getBusList()) {
	    	if(opfNet.isOpfGenBus(bus)){
	    		DclfOpfGenBus genOPF= opfNet.toOpfGenBus(bus);
	    		genFixCostVector.setEntry(i, genOPF.getCoeffA()*baseMVA);
	  			i++;
	    	}
	    }
		return genFixCostVector;
	}
		
	public Array2DRowRealMatrix formCeq() {
		int numOfBus=opfNet.getNoActiveBus();
		int numOfGen=opfNet.getNoOfGen();			

		Array2DRowRealMatrix genBusAdjacent = new Array2DRowRealMatrix(	numOfBus, numOfGen);
		int genCount = 0;
		for (Bus b : opfNet.getBusList()) {
			// set the elements in genBusAdjacent matrix to ONE if there is a
			// generator ;
			/*
			 * in the case of multi-gen ,all are treated as a union;
			 * not suitable for a case with multi-generators connected to one
			 * bus;
			 */
			if (opfNet.isOpfGenBus(b)) {
				genBusAdjacent.setEntry(b.getSortNumber(), genCount, 1);
				genCount++;
			}
		}
		Array2DRowRealMatrix BrTranspose = (Array2DRowRealMatrix) this.getReducedBusAdmittance().transpose();

		Array2DRowRealMatrix CeqTranspose = new Array2DRowRealMatrix(numOfBus, numOfBus + numOfGen - 1);
		CeqTranspose.setSubMatrix(genBusAdjacent.getData(), 0, 0); // Ceq'={genBusAdjacent,BrTranspose}
		CeqTranspose.setSubMatrix(BrTranspose.scalarMultiply(-1).getData(), 0, numOfGen);
		return (Array2DRowRealMatrix) CeqTranspose.transpose();
	}

	public ArrayRealVector formBeq() {
		ArrayRealVector beq = new ArrayRealVector(opfNet.getNoActiveBus());
		for (Bus b : opfNet.getBusList()) {
			DclfOpfBus acbus = (DclfOpfBus) b;
			int busIndex = acbus.getSortNumber();
			if (acbus.isLoad()) {
				beq.setEntry(busIndex, acbus.getLoadP());
			}
		}
		return beq;
	}

	public Array2DRowRealMatrix formCiq() {
    	int numOfBranch=opfNet.getNoActiveBranch();
		int numOfBus=opfNet.getNoActiveBus();
		int numOfGen=opfNet.getNoOfGen();

		double bij = DEFAULT_BIJ;
		
		int braIndex = 0;
		Array2DRowRealMatrix braAdmDiag = new Array2DRowRealMatrix(numOfBranch, numOfBranch);
		Array2DRowRealMatrix braBusAdjacent = new Array2DRowRealMatrix(numOfBranch, numOfBus - 1);
		int swingIndex = this.getSwingBusIndex();
		for (Branch bra : opfNet.getBranchList()) {
			if (bra.isAclfBranch()) {
				DclfOpfBranch aclfBra = (DclfOpfBranch) bra;
				// create branch admittance matrix
				bij = (aclfBra.getZ().getImaginary() > 0.00001) ? 1 / aclfBra
						.getZ().getImaginary() : DEFAULT_BIJ; // in case x=0;
				braAdmDiag.setEntry(braIndex, braIndex, bij);

				// create branch-bus connected or adjacent matrix;
				if (!((DclfOpfBus) bra.getFromBus()).isSwing()) {
					int fromBusIndex = bra.getFromBus().getSortNumber();
					fromBusIndex=(fromBusIndex<swingIndex)?fromBusIndex:fromBusIndex-1; // insure nonswing bus;
					braBusAdjacent.setEntry(braIndex, fromBusIndex, 1);
				}
				if (!((DclfOpfBus) bra.getToBus()).isSwing()) {
					int toBusIndex = bra.getToBus().getSortNumber();
					toBusIndex=(toBusIndex<swingIndex)?toBusIndex:toBusIndex-1; 
					braBusAdjacent.setEntry(braIndex, toBusIndex, -1);
				}
				braIndex++;
			}
		}

		int genIndex = 0;
		Array2DRowRealMatrix eyeGenP = new Array2DRowRealMatrix(numOfGen, numOfGen);
		for (Bus bus : opfNet.getBusList()) {
			if (opfNet.isOpfGenBus(bus)) {
				eyeGenP.setEntry(genIndex, genIndex, 1);
				genIndex++;
			}
		}
		Array2DRowRealMatrix DAr = braAdmDiag.multiply(braBusAdjacent);
		// Ciq'={Ot,-braAdmDiag*braBusAdjacent;
		//     -Ot,braAdmDiag*braBusAdjacent;
		//      eyeGenP,Op;
		//      -eyeGenP,-Op}

		Array2DRowRealMatrix CiqTranspose = new Array2DRowRealMatrix(
				numOfBranch * 2 + numOfGen * 2, numOfGen + numOfBus - 1);
		CiqTranspose.setSubMatrix(DAr.scalarMultiply(-1).getData(), 0, numOfGen);
		CiqTranspose.setSubMatrix(DAr.getData(), numOfBranch, numOfGen);
		CiqTranspose.setSubMatrix(eyeGenP.getData(), 2 * numOfBranch, 0);
		CiqTranspose.setSubMatrix(eyeGenP.scalarMultiply(-1).getData(), 2* numOfBranch + numOfGen, 0);
					// Ciq=CiqTranspose'
		return (Array2DRowRealMatrix) CiqTranspose.transpose();
	}

	public ArrayRealVector formBiq() {
    	int numOfBranch=opfNet.getNoActiveBranch();
		int numOfGen=opfNet.getNoOfGen();			
		
		ArrayRealVector bt = formBranchMvaConstraint();
		ArrayRealVector b_Pmax = formGenInequConstraintPmax();
		ArrayRealVector b_Pmin = formGenInequConstraintPmin();

		ArrayRealVector biq = new ArrayRealVector(numOfBranch * 2 + numOfGen * 2);
		biq.set(0, bt);
		biq.set(numOfBranch, bt);
		biq.set(numOfBranch * 2, b_Pmin);
		biq.set(numOfGen + numOfBranch * 2, b_Pmax);
		return biq;
	}

	// used for creating inequality constraint
	private ArrayRealVector formBranchMvaConstraint() {
    	int numOfBranch=opfNet.getNoActiveBranch();
		// bt corresponds to line thermal inequality constraints
		ArrayRealVector bt = new ArrayRealVector(numOfBranch); 
		double baseMVA=opfNet.getBaseKva()*0.001;
		int braIndex = 0;
		// form the bt;
		for (Branch bra : opfNet.getBranchList()) {
			if (bra.isAclfBranch()) {
				DclfOpfBranch acBranch = (DclfOpfBranch) bra;
				double ratingMva1 = acBranch.getRatingMva1()/baseMVA;
				bt.setEntry(braIndex, -ratingMva1);
				braIndex++;
			}
		}
		return bt;
	}

	private ArrayRealVector formGenInequConstraintPmax() {
	    return formGenInequConstraint(true);
	}

	private ArrayRealVector formGenInequConstraintPmin() {
	    return formGenInequConstraint(false);
	}

	private ArrayRealVector formGenInequConstraint(boolean isMax) {
		int numOfGen=opfNet.getNoOfGen();			
		// including b_Pmin, b_Pmax;
		ArrayRealVector b_Pmax = new ArrayRealVector(numOfGen); // b_Pmax corresponds to generators' upper active power limit
        int i=0;
        double baseMVA=opfNet.getBaseKva()*0.001;
		// get the constraint data from network file ;
	    for (Bus bus:opfNet.getBusList()) {
	    	  if(opfNet.isOpfGenBus(bus)){
	    		DclfOpfGenBus genOPF=opfNet.toOpfGenBus(bus);	
	  			b_Pmax.setEntry(i, isMax?
	  					-genOPF.getCapacityLimit().getMax()/baseMVA :
	  					genOPF.getCapacityLimit().getMin()/baseMVA);
	  			i++;
	    	  }
			
		}
	    return b_Pmax;
	}
		
	private Array2DRowRealMatrix getReducedBusAdmittance() {
		int numOfBus=opfNet.getNoActiveBus();

		// form the reduced bus admittance matrix by omitting the row
		// corresponding to the SWING bus ;
		// here B1 formed by InterPSS itself is under consideration to be reused
		// ;

		Array2DRowRealMatrix tempBusAdm = new Array2DRowRealMatrix(
				numOfBus, numOfBus);

		for (Bus b : opfNet.getBusList()) {
			DclfOpfBus busi = (DclfOpfBus) b;
			int i = busi.getSortNumber();
			double Bii = 0;
			for (Branch bra : busi.getBranchList()) {
				if (bra.isAclfBranch()) {
					DclfOpfBranch aclfBranch = (DclfOpfBranch) bra;
					Bus busj = bra.getToBus().getId().equals(busi.getId()) ? bra.getFromBus() : bra	.getToBus();
					int j = busj.getSortNumber();
					double Bij = 1.0 / aclfBranch.getZ().getImaginary();// aclfBranch.b1ft();
					tempBusAdm.setEntry(i, j, -Bij);
					Bii += Bij;
				}
			}
			tempBusAdm.setEntry(i, i, Bii);
		}

		Array2DRowRealMatrix busAdmReduced = new Array2DRowRealMatrix(
				numOfBus - 1, numOfBus); // reduced bus admittance matrix
		int[] selectedRows = this.getNonSwingBusRows();
		for (int index = 0; index < selectedRows.length; index++) {
			busAdmReduced.setRow(index, tempBusAdm.getRow(selectedRows[index]));
		}
		return busAdmReduced;
	}

	private int[] getNonSwingBusRows() {
		int[] NonSwingBusRows = new int[opfNet.getNoActiveBus() - 1];
		int idx = 0;

		for (int busIndex = 0; busIndex < opfNet.getNoActiveBus(); busIndex++) {
			Bus b = opfNet.getBusList().get(busIndex);
			DclfOpfBus bus = (DclfOpfBus) b;
			if (!bus.isSwing()) {
				// swingSortNum=bus.getSortNumber();
				NonSwingBusRows[idx] = busIndex;
				idx++;
			}
		}
		return NonSwingBusRows;
	}

	private int getSwingBusIndex(){
		// assume there is one swing bus in the system
		for(Bus b : opfNet.getBusList()){
			if(((DclfOpfBus)b).isSwing()){
				return b.getSortNumber();
			}
		}
		IpssLogger.getLogger().severe("No swing bus found in the system");
		return 0;
	}
}
