package org.interpss.vstab;

import org.interpss.vstab.cpf.CPFAlgorithm;
import org.interpss.vstab.cpf.LoadIncPattern;
import org.interpss.vstab.cpf.LoadIncPattern.LoadIncScope;
import org.interpss.vstab.cpf.LoadIncPattern.LoadIncType;
import org.interpss.vstab.cpf.impl.CPFAlgorithmImpl;
import org.interpss.vstab.cpf.impl.LambdaParam;
import org.interpss.vstab.cpf.impl.LoadIncrease;

import com.interpss.core.aclf.AclfNetwork;
/**
 *  The object factory built for Vstab analysis, to create instance directly;
 * @author Tony Huang
 *
 */
public class VStabObjectFactory {

	public static CPFAlgorithm createCPFAlgorithmImpl(AclfNetwork net,LoadIncrease loadInc) {
		LambdaParam lambda = new LambdaParam(net.getNoBus(),0.01);// index is changed, now normally it is 0->n-1, so the lambda should be Nth paramether; 
		CPFAlgorithm cpf=new CPFAlgorithmImpl(net, lambda, loadInc);
		return cpf;
	}
	public static LoadIncPattern createLdIncPattern(AclfNetwork net,LoadIncScope incScope,LoadIncType incType,Object[]incObjectAry){
		return new LoadIncPattern(net,incScope,incType,incObjectAry);
		
	}
	public static LoadIncrease createLoadIncrease(AclfNetwork net, LoadIncPattern ptn){
		return new LoadIncrease(net,ptn);
	}

}
