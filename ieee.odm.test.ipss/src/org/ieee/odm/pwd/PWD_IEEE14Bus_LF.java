package org.ieee.odm.pwd;

import org.interpss.CorePluginObjFactory;
import org.interpss.IpssCorePlugin;
import org.interpss.display.AclfOutFunc;
import org.interpss.fadapter.IpssFileAdapter;

import com.interpss.CoreObjectFactory;
import com.interpss.common.exp.InterpssException;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;


public class PWD_IEEE14Bus_LF {
	public static void main(String args[]) throws InterpssException {
		IpssCorePlugin.init();
		
		AclfNetwork net = CorePluginObjFactory
			.getFileAdapter(IpssFileAdapter.FileFormat.PWD)
			.load("testData/pwd/ieee14.AUX")//ieee14.AUX
			.getAclfNet();	

		//System.out.println(net.net2String());

		LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
		algo.loadflow();

		System.out.println(AclfOutFunc.loadFlowSummary(net));
		
	}	
}

