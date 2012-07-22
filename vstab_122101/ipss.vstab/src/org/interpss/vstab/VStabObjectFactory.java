package org.interpss.vstab;

import org.interpss.vstab.cpf.CPFAlgorithm;
import org.interpss.vstab.cpf.impl.CPFAlgorithmImpl;
import org.interpss.vstab.cpf.impl.LambdaParam;

import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.core.aclf.AclfNetwork;

/*
 *  all //** are by Mike for code refactor
 */

public class VStabObjectFactory {

	public static CPFAlgorithm createCPFAlgorithmImpl(AclfNetwork net, IPSSMsgHub msg) {
		LambdaParam lambda = new LambdaParam(net.getNoBus()+1,1);
//**		CPFAlgorithm cpf=new CPFAlgorithmImpl(net, lambda, msg);
		CPFAlgorithm cpf=new CPFAlgorithmImpl(net, lambda);
		return cpf;
	}

}
