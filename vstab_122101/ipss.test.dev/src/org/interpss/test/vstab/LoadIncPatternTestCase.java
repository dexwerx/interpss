package org.interpss.test.vstab;

import static org.junit.Assert.assertTrue;

import org.interpss.test.DevTestSetup;
import org.interpss.vstab.cpf.LoadIncPattern;
import org.interpss.vstab.cpf.LoadIncPattern.LoadIncPtn;
import org.junit.Test;

import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.pssl.simu.IpssAclf;
import com.interpss.simu.util.sample.SampleCases;
import com.interpss.spring.CoreCommonSpringCtx;

public class LoadIncPatternTestCase extends DevTestSetup {
	@Test
	public void testCase1() throws Exception {
       IPSSMsgHub msg = IpssAclf.getMsgHub();
		
		// create a sample 5-bus system for Loadflow 
     
  		AclfNetwork net = CoreObjectFactory.createAclfNetwork();
		SampleCases.load_LF_5BusSystem(net);
		/*
		 *   LoadBus(net, "1", 13800.0D, 1, 1, AclfLoadCode.CONST_P, 1.6000000000000001D, 0.80000000000000004D, 2);
             LoadBus(net, "2", 13800.0D, 1, 1, AclfLoadCode.CONST_P, 2.0D, 1D, 2);
             LoadBus(net, "3", 13800.0D, 2, 1, AclfLoadCode.CONST_P, 3.7000000000000002D, 1.3D, 2);
             PVBus(net, "4", 1000.0D, 1, 1, 5.0D, 2, 1.05D, 2);
             SwingBus(net, "5", 4000.0D, 2, 1, 1.05D, 2, 0D, 3);
		 */
		System.out.println(net.net2String());
		System.out.println("the number of Areas : "+net.getAreaList().size());
		System.out.println("Area1 Number: "+net.getZoneList().get(0).getNumber());
        LoadIncPattern ldPtn=new LoadIncPattern(net, msg);
        Long[] incAreaAry = {1L,2L};    
        ldPtn.defLoadIncPtn(LoadIncPtn.AREA, net, incAreaAry);
        assertTrue(ldPtn.getIncBusList().size()==3); 
        System.out.println(ldPtn.getIncBusList().size());
	}

}
