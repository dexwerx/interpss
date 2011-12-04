package org.interpss.test.vstab;

import static org.junit.Assert.assertTrue;

import org.interpss.PluginObjectFactory;
import org.interpss.custom.IpssFileAdapter;
import org.interpss.test.DevTestSetup;
import org.interpss.vstab.cpf.LoadIncPattern;
import org.interpss.vstab.cpf.LoadIncPattern.LoadIncScope;
import org.interpss.vstab.cpf.LoadIncPattern.LoadIncType;
import org.junit.Test;

import com.interpss.CoreObjectFactory;
import com.interpss.common.exp.InterpssException;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.net.Bus;
import com.interpss.simu.util.sample.SampleCases;


public class LoadIncPatternTestCase extends DevTestSetup {
	@Test
	public void testCase1() throws Exception {

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
//		System.out.println(net.net2String());
		
		//1. test case: increase by Area scope and constant power factor Type
		System.out.println("the number of Areas : "+net.getAreaList().size());
		System.out.println("Area1 Number: "+net.getZoneList().get(0).getNumber());
		Long[] incAreaAry = {1L,2L}; 
        LoadIncPattern ldPtn=new LoadIncPattern(net,LoadIncScope.AREA,LoadIncType.CONST_PF,incAreaAry);
        assertTrue(ldPtn.getIncBusList().size()==3); 
        assertTrue(ldPtn.getLoadIncDir().get("1").getReal()-1.6<1e-6);

        
        //2. increase by network Scope and constant power factor Type
        ldPtn=new LoadIncPattern(net,LoadIncScope.NETWORK,LoadIncType.ONLY_P,null);
        assertTrue(ldPtn.getIncBusList().size()==3); 
        assertTrue(Math.abs(ldPtn.getLoadIncDir().get("2").getImaginary()-0)<1e-6);
      
        
	}
	
	// 3. test increase  by Bus(s) Scope
	@Test
	public void testCase2() throws InterpssException, Exception{

		AclfNetwork net = CoreObjectFactory.createAclfNetwork();
    	SampleCases.load_LF_5BusSystem(net);
//		Bus[] bAry={net.getBus("2")};
		LoadIncPattern ldPtn=new LoadIncPattern(net,LoadIncScope.BUS,LoadIncType.CONST_PF,net.getBusList().toArray());
		assertTrue(ldPtn.getIncBusList().size()==3);
		assertTrue(Math.abs(ldPtn.getLoadIncDir().get("2").getReal()-2)<1e-6);

	}
	@Test
	public void testCase3() throws Exception {
		
		AclfNetwork net=PluginObjectFactory.getFileAdapter(IpssFileAdapter.FileFormat.IEEECDF).load("testData/ieee_cdf/ieee14.ieee").getAclfNet();
		Bus[] bAry={net.getBus("Bus4")};
		LoadIncPattern ldPtn=new LoadIncPattern(net,LoadIncScope.BUS,LoadIncType.CONST_PF,bAry);
		assertTrue(ldPtn.getIncBusList().size()==1);
		
	}

}
