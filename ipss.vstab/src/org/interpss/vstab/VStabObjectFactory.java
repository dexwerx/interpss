package org.interpss.vstab;

import org.interpss.vstab.cpf.CPFAlgorithm;
import org.interpss.vstab.cpf.impl.CPFAlgorithmImpl;
import org.interpss.vstab.cpf.impl.LambdaParam;
import com.interpss.core.aclf.AclfNetwork;

public class VStabObjectFactory {

	public static CPFAlgorithm createCPFAlgorithmImpl(AclfNetwork net) {
		LambdaParam lambda = new LambdaParam(net.getNoBus()+1,1);
		CPFAlgorithm cpf=new CPFAlgorithmImpl(net, lambda);
		return cpf;
	}

}
