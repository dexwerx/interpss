package test.ed;

import org.interpss.IpssPlugin;

import com.gams.api.gamsglobals;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.net.Bus;
import com.interpss.gams.GAMS;
import com.interpss.gams.common.GAMSException;
import com.interpss.gams.common.GAMSLogger;
import com.interpss.gams.util.GAMSHelper;
import com.interpss.gams.util.IReadResult;
import com.interpss.pssl.plugin.IpssAdapter;
import com.interpss.pssl.simu.BaseDSL;
import com.interpss.spring.CoreCommonSpringCtx;

/*
 * To run this example,
 * 
 *    - put ieee14_java1.gms and ieee14_java.template in the current dir
 *    - include file ieee14_java.inc and gdx file inputdata.gdx are created during the
 *      running process
 */

public class Ieee14Ed2 {
    public static void main(String[] args) {
    	GAMS.GamsDir = "c:\\Program Files (x86)\\GAMS23.7";
    	GAMS.JNIDir = "c:/eclipse/JNI";
    	
    	new Ieee14Ed2().run();
    }
    
    private void run() {
		IpssPlugin.init();
		BaseDSL.setMsgHub(CoreCommonSpringCtx.getIpssMsgHub());
		
		AclfNetwork net = IpssAdapter.importAclfNet("basecase/ieee14.ieee")
				.setFormat(IpssAdapter.FileFormat.IEEECommonFormat)
				.load()
				.getAclfNet();
		//System.out.println(net.net2String());
		
		try {
   	 		GAMS.init();
   	 			
   	   	    writeModelInputData("inputdata.gdx", net);
   	 			
   	 		GAMSHelper helper = new GAMSHelper();
   	   	    helper.CallGams("model\\ieee14_ed1.gms",  
   	   	    		GAMS.OutLogLevel_Log, GAMS.RunLogLevel_WARN);
   	   	        
   	   	    helper.readResult("ieee14_results.gdx", new String[] {"result"}, 
   	   	        			resultReader);
	 	} catch (GAMSException e) {
	 		GAMSLogger.getLogger().severe(e.toString());
 	 	} finally {
   	   	    GAMS.free();
   	 	}	
    }
	
    private void writeModelInputData(String fngdxfile, AclfNetwork net) throws GAMSException {        
        GAMS.gdxOpenWrite(fngdxfile, "IEEE14_ED");

        GAMS.gdxDataWriteStrStart("genUnit", "generating unit", 1, gamsglobals.dt_set, 0);
        for (Bus b : net.getBusList()) {
        	AclfBus bus = (AclfBus)b;
        	if (bus.isGen())
        		GAMS.gdxDataWriteIndex(bus.getId());
        }
        GAMS.gdxDataWriteDone();        
        
        GAMS.gdxDataWriteStrStart("loadBus", "load bus", 1, gamsglobals.dt_set, 0);
        for (Bus b : net.getBusList()) {
        	AclfBus bus = (AclfBus)b;
        	if (bus.isLoad())
        		GAMS.gdxDataWriteIndex(bus.getId());
        }
        GAMS.gdxDataWriteDone();        

        GAMS.gdxDataWriteStrStart("lowerBound", "lower bound of each generating unit", 1, gamsglobals.dt_par, 0);
        GAMS.gdxDataWriteStr("Bus1", 0.02);
        GAMS.gdxDataWriteStr("Bus2", 0.03);
        GAMS.gdxDataWriteStr("Bus3", 0.05);
        GAMS.gdxDataWriteStr("Bus6", 0.06);
        GAMS.gdxDataWriteStr("Bus8", 0.07);        
        GAMS.gdxDataWriteDone();
        
        GAMS.gdxDataWriteStrStart("upperBound", "upper bound of each generating unit", 1, gamsglobals.dt_par, 0);
        GAMS.gdxDataWriteStr("Bus1", 2.7);
        GAMS.gdxDataWriteStr("Bus2", 0.5);
        GAMS.gdxDataWriteStr("Bus3", 0.5);
        GAMS.gdxDataWriteStr("Bus6", 0.5);
        GAMS.gdxDataWriteStr("Bus8", 0.5);        
        GAMS.gdxDataWriteDone();

        GAMS.gdxDataWriteStrStart("demand", "demand at load bus j", 1, gamsglobals.dt_par, 0);
        for (Bus b : net.getBusList()) {
        	AclfBus bus = (AclfBus)b;
        	if (bus.isLoad())
        		GAMS.gdxDataWriteStr(bus.getId(), bus.getLoadP());
        }
        GAMS.gdxDataWriteDone();

        GAMS.gdxDataWriteStrStart("FactorA", "A(i) weighting factor A for generation cost function", 1, gamsglobals.dt_par, 0);
        GAMS.gdxDataWriteStr("Bus1", 500);
   		GAMS.gdxDataWriteStr("Bus2", 300);
   		GAMS.gdxDataWriteStr("Bus3", 400);
   		GAMS.gdxDataWriteStr("Bus6", 100);
   		GAMS.gdxDataWriteStr("Bus8", 50);          
        GAMS.gdxDataWriteDone();

        GAMS.gdxDataWriteStrStart("FactorB", "B(i) weighting factor A for generation cost function", 1, gamsglobals.dt_par, 0);
        GAMS.gdxDataWriteStr("Bus1", 30);
        GAMS.gdxDataWriteStr("Bus2", 10);
        GAMS.gdxDataWriteStr("Bus3", 70);
        GAMS.gdxDataWriteStr("Bus6", 50);
        GAMS.gdxDataWriteStr("Bus8", 40);         
        GAMS.gdxDataWriteDone();

        GAMS.gdxDataWriteStrStart("FactorC", "C(i) weighting factor A for generation cost function", 1, gamsglobals.dt_par, 0);
        GAMS.gdxDataWriteStr("Bus1", 0.01);
        GAMS.gdxDataWriteStr("Bus2", 0.005);
        GAMS.gdxDataWriteStr("Bus3", 0.03);
        GAMS.gdxDataWriteStr("Bus6", 0.06);
        GAMS.gdxDataWriteStr("Bus8", 0.09);         
        GAMS.gdxDataWriteDone();
        
        GAMS.gdxClose();
    }
	
	private IReadResult resultReader = new IReadResult() {
       	public void readResult(int dimension) {
       		String[] Indx = new String[gamsglobals.maxdim];
       		double[] Values = new double[gamsglobals.val_max];
       		int[] N = new int[1];
   
       		while(GAMS.gdxDataReadStr(Indx, Values, N) != 0){
       			if(Values[gamsglobals.val_level] == 0) //skip level = 0.0 is default
       				continue;
       			for(int cnt = 0; cnt < dimension; cnt++){
       				System.out.println("Bus:" + Indx[cnt] + ", p:" + Values[cnt]);
       			}
       		}
       	}
    };
}