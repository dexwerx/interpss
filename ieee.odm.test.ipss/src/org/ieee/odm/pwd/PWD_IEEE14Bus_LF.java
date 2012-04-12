package org.ieee.odm.pwd;

import org.interpss.IpssPlugin;
import org.interpss.PluginObjectFactory;
import org.interpss.custom.IpssFileAdapter;
import org.interpss.display.AclfOutFunc;

import com.interpss.CoreObjectFactory;
import com.interpss.common.exp.InterpssException;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;


public class PWD_IEEE14Bus_LF {
	public static void main(String args[]) throws InterpssException {
		IpssPlugin.init();
		
		AclfNetwork net = PluginObjectFactory
			.getFileAdapter(IpssFileAdapter.FileFormat.PWD)
			.load("testData/pwd/ieee14.AUX")
			.getAclfNet();	

//		System.out.println(net.net2String());

		LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
		algo.loadflow();

		System.out.println(AclfOutFunc.loadFlowSummary(net));
	}	
}

