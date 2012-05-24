package org.interpss.test.lf;

import static org.junit.Assert.assertTrue;

import org.interpss.CorePluginObjFactory;
import org.interpss.IpssCorePlugin;
import org.interpss.display.AclfOutFunc;
import org.interpss.fadapter.IpssFileAdapter;
import org.junit.Test;

import com.interpss.CoreObjectFactory;
import com.interpss.common.exp.InterpssException;
import com.interpss.common.util.SerializeEMFObjectUtil;
import com.interpss.core.aclf.AclfGenCode;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;

public class NetObjectSerializationTest {
	@Test
	public void test() throws InterpssException{
	IpssCorePlugin.init();
	AclfNetwork net = CorePluginObjFactory
	.getFileAdapter(IpssFileAdapter.FileFormat.IEEECDF)
	.load("testData/ieee_cdf/ieee005.ieee")
	.getAclfNet();	

    System.out.println(net.net2String());

    LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
    algo.loadflow();

    System.out.println(AclfOutFunc.loadFlowSummary(net));
    String s=SerializeEMFObjectUtil.saveModel(net);
    
    AclfNetwork net2=(AclfNetwork) SerializeEMFObjectUtil.loadModel(s);
    net2.rebuildLookupTable();
    assertTrue(net2.getAclfBus("Bus1").getBaseVoltage()==100000.0);
    assertTrue(net2.getAclfBus("Bus1").getGenCode()==AclfGenCode.GEN_PQ);
    assertTrue(net2.getAclfBus("Bus1").getLoadP()==1.60);
	}

}
