package org.interpss.test.vstab;

import static org.junit.Assert.assertTrue;

import org.interpss.vstab.VStabObjectFactory;
import org.interpss.vstab.cpf.GenDispPattern;
import org.interpss.vstab.cpf.LoadIncPattern;
import org.interpss.vstab.cpf.GenDispPattern.Pattern;
import org.interpss.vstab.cpf.LoadIncPattern.LoadIncScope;
import org.interpss.vstab.cpf.LoadIncPattern.LoadIncType;
import org.interpss.vstab.cpf.impl.GenDispatch;
import org.interpss.vstab.cpf.impl.LoadIncrease;
import org.junit.Test;

import com.interpss.CoreObjectFactory;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.simu.util.sample.SampleCases;

public class GenDispatchTest {
	@Test
	public void testGenDispatch(){
		AclfNetwork net = CoreObjectFactory.createAclfNetwork();
		SampleCases.load_LF_5BusSystem(net);
	    System.out.print(net.net2String());
	    double origGenP=net.getAclfBus("4").getGenP();
		//define load Increase;
		LoadIncPattern ldPtn=new LoadIncPattern(net,LoadIncScope.NETWORK,LoadIncType.CONST_PF,null);
        LoadIncrease ldInc=VStabObjectFactory.createLoadIncrease(net, ldPtn);
        ldInc.increaseLoad(0.1);
       
        assertTrue(ldInc.getDeltaSumOfLoad()-7.3*0.1<1e-6);
        
        // gen redispatch to balance the load increase
		// define gen dispatch
		GenDispPattern pattern=new GenDispPattern(net, Pattern.BASE_CASE_DIR);        
        GenDispatch genDisp=new GenDispatch(net, pattern);
        
        genDisp.dispatchGen(0.1);
        
        assertTrue(net.getAclfBus("4").getGenP()-origGenP-7.3*0.1<1e-6); // deltaP=7.3*0.1
	}
}
