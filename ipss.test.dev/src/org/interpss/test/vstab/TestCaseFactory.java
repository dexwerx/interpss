package org.interpss.test.vstab;

import org.interpss.PluginObjectFactory;
import org.interpss.custom.IpssFileAdapter;
import org.interpss.numeric.datatype.LimitType;
import org.interpss.vstab.VStabObjectFactory;
import org.interpss.vstab.cpf.CPFAlgorithm;
import org.interpss.vstab.cpf.GenDispPattern;
import org.interpss.vstab.cpf.LoadIncPattern;
import org.interpss.vstab.cpf.GenDispPattern.Pattern;
import org.interpss.vstab.cpf.LoadIncPattern.LoadIncScope;
import org.interpss.vstab.cpf.LoadIncPattern.LoadIncType;
import org.interpss.vstab.cpf.impl.GenDispatch;
import org.interpss.vstab.cpf.impl.LoadIncrease;

import com.interpss.common.util.IpssLogger;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.simu.SimuContext;

public class TestCaseFactory {
	public static AclfNetwork createNet(){
		
		return null;
		
	}
	public static CPFAlgorithm createCpfAlgo(String ieeeNetName) throws Exception{
		String filePath="testData/ieee_cdf/"+ieeeNetName+".ieee";
		IpssFileAdapter adapter = PluginObjectFactory.getFileAdapter(IpssFileAdapter.FileFormat.IEEECDF);
		SimuContext simuCtx = adapter.load(filePath);

		AclfNetwork net = simuCtx.getAclfNet();

		// set GenPLimit;
		setGenPLimit(net,0.1);
		setGenQLimit(net,0.5);
		// define the load increase 
		LoadIncPattern ldPtn=new LoadIncPattern(net,LoadIncScope.NETWORK,LoadIncType.CONST_PF,null);
		LoadIncrease ldInc=VStabObjectFactory.createLoadIncrease(net, ldPtn);
		// define gen dispatch
		GenDispPattern pattern=new GenDispPattern(net, Pattern.BASE_CASE_DIR);
		GenDispatch genDisp=new GenDispatch(net, pattern);
		// create a cpf algorithm;
		CPFAlgorithm cpfAlgo = VStabObjectFactory.createCPFAlgorithmImpl(net,ldInc,genDisp);
		return cpfAlgo;
	}
	
	 public static void setGenPLimit(AclfNetwork net, double resvRate){
		 for(int i=0;i<net.getBusList().size();i++){
             AclfBus bus=(AclfBus)net.getBusList().get(i);
             if(bus.isGenPV()){// only PV bus is considered here
                     if(bus.getPGenLimit()==null){
                             IpssLogger.getLogger().info("There is no PGenLimit defined in bus:"+bus.getId()
                                             +" ,assume 10% reservation in the following process.");
                             bus.setPGenLimit( new LimitType(bus.getGenP()*(1+resvRate),bus.getGenP()*(1-resvRate))); // assume there is 20% generation reservation by default;
                     }
             }
             
     }
		
	 }
	 /**
	  * qMax = q2pMaxRate*PGenLimit.Max;
	  * @param net
	  * @param resvRate
	  */
	 public static void setGenQLimit(AclfNetwork net, double q2pMaxRate){
		 for(int i=0;i<net.getBusList().size();i++){
             AclfBus bus=(AclfBus)net.getBusList().get(i);
             if(bus.isGenPV()){
            	 System.out.println("gen q limit"+bus.getQGenLimit().getMax());
	           bus.setQGenLimit(new LimitType(bus.getPGenLimit().getMax()*q2pMaxRate,0));
             }
		 }
	 }
	 
     
}
