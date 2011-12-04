package org.interpss.test.vstab;

import static org.junit.Assert.assertTrue;

import org.interpss.vstab.VStabObjectFactory;
import org.interpss.vstab.cpf.LoadIncPattern;
import org.interpss.vstab.cpf.LoadIncPattern.LoadIncScope;
import org.interpss.vstab.cpf.LoadIncPattern.LoadIncType;
import org.interpss.vstab.cpf.impl.LambdaParam;
import org.interpss.vstab.cpf.impl.LoadIncrease;
import org.junit.Test;

import com.interpss.CoreObjectFactory;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.simu.util.sample.SampleCases;

public class LoadIncreaseTest {
	LambdaParam lambda=null;
	LoadIncrease ldInc=null;
	LoadIncPattern ldPtn=null;
	@Test
	public void testCase1() throws Exception {
		// create a sample 5-bus system for Loadflow 
		/*
		 *   LoadBus(net, "1", 13800.0D, 1, 1, AclfLoadCode.CONST_P, 1.6000000000000001D, 0.80000000000000004D, 2);
             LoadBus(net, "2", 13800.0D, 1, 1, AclfLoadCode.CONST_P, 2.0D, 1D, 2);
             LoadBus(net, "3", 13800.0D, 2, 1, AclfLoadCode.CONST_P, 3.7000000000000002D, 1.3D, 2);
             PVBus(net, "4", 1000.0D, 1, 1, 5.0D, 2, 1.05D, 2);
             SwingBus(net, "5", 4000.0D, 2, 1, 1.05D, 2, 0D, 3);
		 */
  		AclfNetwork net = CoreObjectFactory.createAclfNetwork();
		SampleCases.load_LF_5BusSystem(net);
        //1. increase by NETWORK Scope and ONLY_P Type
		
        LoadIncPattern ldPtn=new LoadIncPattern(net,LoadIncScope.NETWORK,LoadIncType.ONLY_P,null);
        assertTrue(ldPtn.getIncBusList().size()==3); 
        assertTrue(Math.abs(ldPtn.getLoadIncDir().get("2").getImaginary()-0)<1e-6);
        
        LoadIncrease ldInc=VStabObjectFactory.createLoadIncrease(net, ldPtn);
        assertTrue(Math.abs(ldInc.getOrigLoad().get("2").getReal()-2)<1e-6);
        
        LambdaParam lambda=new LambdaParam(6,1);
        ldInc.increaseLoad(lambda.getValue());
        assertTrue(Math.abs(((AclfBus)ldInc.getAclfNetwork().getBus("2")).getLoad().getReal()-4)<1e-6); // loadP=2+dirP(=2)*lambda(=1)=4
        assertTrue(Math.abs(((AclfBus)ldInc.getAclfNetwork().getBus("2")).getLoad().getImaginary()-1)<1e-6); // keep constant
        assertTrue(Math.abs(((AclfBus)ldInc.getAclfNetwork().getBus("3")).getLoad().getImaginary()-1.3)<1e-6);
        lambda.setValue(0.1);
        ldInc.increaseLoad(lambda.getValue());
        assertTrue(Math.abs(((AclfBus)ldInc.getAclfNetwork().getBus("2")).getLoad().getReal()-2.2)<1e-6);
	}    
        //2. increase by Area Scope

	@Test
	public void testCase2() throws Exception {
  		AclfNetwork net = CoreObjectFactory.createAclfNetwork();
		SampleCases.load_LF_5BusSystem(net);
        Long[] areaAry={1L};
        LoadIncPattern ldPtn=new LoadIncPattern(net,LoadIncScope.AREA,LoadIncType.CONST_PF,areaAry);
        LoadIncrease ldInc=VStabObjectFactory.createLoadIncrease(net, ldPtn);
        ldInc.setPattern(ldPtn);
        lambda=new LambdaParam(ldInc.getAclfNetwork().getNoBus()+1,0.1) ;
        ldInc.increaseLoad(lambda.getValue());
        System.out.println(ldInc.getAclfNetwork().net2String());
        assertTrue(Math.abs(((AclfBus)ldInc.getAclfNetwork().getBus("2")).getLoad().getReal()-2.2)<1e-6);
        assertTrue(Math.abs(((AclfBus)ldInc.getAclfNetwork().getBus("2")).getLoad().getImaginary()-1.1)<1e-6);
        assertTrue(Math.abs(((AclfBus)ldInc.getAclfNetwork().getBus("3")).getLoad().getImaginary()-1.3)<1e-6); // not in the Scope, keep constant
        
    
	}

}
