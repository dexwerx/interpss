package org.interpss.vstab.cpf.impl;

import java.util.Hashtable;

import org.apache.commons.math.complex.Complex;
import org.interpss.numeric.sparse.SparseEqnMatrix2x2;
import org.interpss.vstab.cpf.CPFAlgorithm;

import com.interpss.common.util.IpssLogger;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.algo.AclfMethod;
import com.interpss.core.algo.impl.DefaultNrSolver;
import com.interpss.core.net.Bus;

public class CorrectorStepSolver extends DefaultNrSolver {
    private CPFAlgorithm cpf=null;
    private CpfHelper cpfHelper=null;
    private LoadIncrease ldInc=null;
    private double incSize=0;

    
	public CorrectorStepSolver(CPFAlgorithm cpfAlgo) {
		super(cpfAlgo.getAclfNetwork());
		this.cpf=cpfAlgo;
		this.ldInc=cpfAlgo.getLoadIncrease();
	}
	@Override
	public SparseEqnMatrix2x2 formJMatrix() {
		this.cpfHelper=new CpfHelper(cpf);
		SparseEqnMatrix2x2 lfEqn=cpfHelper.formAugmJacobiMatrix();

		return lfEqn;
	}
	/**
	 * 	calculate bus power mismatch. The mismatch stored on 
		the right-hand side of the sparse eqn
		both load increase and gen dispatch are considered
	 */
	@Override
	public void setPowerMismatch(SparseEqnMatrix2x2 lfEqn) {
        
		// to increase load 
		if(!this.cpf.getCpfSolver().isLmdaContParam()){ // if lambda is not keep constant, it will change and therefore will affect the load.
			if(this.cpf.getCpfSolver().getLambda().getValue()>1){ // we assume 100% increasement as a warming level
				IpssLogger.getLogger().warning("Warn:loading index Lambda="+this.cpf.getCpfSolver().getLambda().getValue()+
						", seems to be too large, out of normal range.");
			}

		}

		ldInc.increaseLoad(this.cpf.getCpfSolver().getLambda().getValue());
		// generation dispatching to meet the power mismatch
		this.cpf.getGenDispatch().dispatchGen(this.cpf.getCpfSolver().getLambda().getValue());

		super.setPowerMismatch(lfEqn);
//		System.out.println("-------power mismatch( deltaP, deltaQ)-------");
//		VstabFuncOut.printBVector(getAclfNet(), lfEqn);
	}

	@Override
	public void updateBusVoltage(SparseEqnMatrix2x2 lfEqn) {


		double scaleFactor=1;

        if(!this.cpf.getCpfSolver().isLmdaContParam()){
        	// need to find the optimal scale factor when lambda is NOT the parameter
        	scaleFactor=getOptimScaleFactor(lfEqn);
        	this.cpf.getCpfSolver().getLambda().update(lfEqn,scaleFactor);
        }
		// update the bus voltage using the solution results store in the sparse eqn
        super.updateBusVoltage(lfEqn,scaleFactor);
	}
	
	private double getOptimScaleFactor(SparseEqnMatrix2x2 lfEqn){
		
		double optimFactor=1;
		double base=0.1;
		double factor=1;
		// to find the optimal factor
		double totalMismatch=totoalMismatch(lfEqn,factor); // factor=0
		for(int inc=0;inc<10;inc++){
			factor=base*inc;
			double tolM=totoalMismatch(lfEqn,factor);
			if(totalMismatch>tolM){
				totalMismatch=tolM;
				optimFactor=factor;
			}
		}
		
		return optimFactor;
		
	}
    	
	private double totoalMismatch(SparseEqnMatrix2x2 lfEqn, double factor){
		double tolM=0;
		double maxMis=0;
		Hashtable<String,Double> genP=new Hashtable<String,Double>(this.getAclfNet().getNoBus());
		Hashtable<String,Double> loadP=new Hashtable<String,Double>(this.getAclfNet().getNoBus());
		Hashtable<String,Double> loadQ=new Hashtable<String,Double>(this.getAclfNet().getNoBus());
		Hashtable<String,Complex> busV=new Hashtable<String,Complex>(this.getAclfNet().getNoBus());
		// save the origin result;
		for(Bus b:this.getAclfNet().getBusList()){
			AclfBus bus=(AclfBus) b;
			busV.put(bus.getId(), bus.getVoltage());
			if(bus.isGen()){
				genP.put(bus.getId(), bus.getGenP());
			}
			if(bus.isLoad()){
				loadP.put(bus.getId(), bus.getLoadP());
				loadQ.put(bus.getId(), bus.getLoadQ());
			}
		}
		
		// apply changes
		super.updateBusVoltage(lfEqn, factor);
		this.cpf.getCpfSolver().getLambda().update(lfEqn, factor);
		ldInc.increaseLoad(this.cpf.getCpfSolver().getLambda().getValue());
		this.cpf.getGenDispatch().dispatchGen(this.cpf.getCpfSolver().getLambda().getValue());
		
		// calculate the total power mismatch
		for(Bus b:this.getAclfNet().getBusList()){
			AclfBus bus=(AclfBus) b;
			Complex mis=bus.mismatch(AclfMethod.NR);
//			if(mis.abs()>maxMis){
//				maxMis=mis.abs();
//			}
			tolM+=mis.abs()*mis.abs();
		}
//		System.out.println("Factor="+factor+", tolM="+tolM);
		// turn back to the saved state;
		this.cpf.getCpfSolver().getLambda().backToLastState();
		
		for(Bus b:this.getAclfNet().getBusList()){
			AclfBus bus=(AclfBus) b;
			bus.setVoltage(busV.get(bus.getId()));
			if(bus.isGen()){
				bus.setGenP(genP.get(bus.getId()));
			}
			if(bus.isLoad()){
				bus.setLoadP(loadP.get(bus.getId()));
				bus.setLoadQ(loadQ.get(bus.getId()));
			}
		}
		
		return tolM;//maxMis;
		
		
		
	}

	


}
