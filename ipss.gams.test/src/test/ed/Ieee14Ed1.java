package test.ed;

import com.gams.api.gamsglobals;
import com.interpss.gams.GAMS;
import com.interpss.gams.common.GAMSException;
import com.interpss.gams.common.GAMSLogger;
import com.interpss.gams.util.GAMSHelper;
import com.interpss.gams.util.IReadResult;

/*
 * To run this example,
 * 
 *    - put ieee14_java1.gms and ieee14_java.template in the current dir
 *    - include file ieee14_java.inc and gdx file inputdata.gdx are created during the
 *      running process
 */

public class Ieee14Ed1 {
    public static void main(String[] args) {
    	GAMS.GamsDir = "c:\\Program Files (x86)\\GAMS23.7";
    	GAMS.JNIDir = "c:/eclipse/JNI";
    	
    	new Ieee14Ed1().run();
    }
    
    private void run() {
 		try {
   	 		GAMS.init();
   	 			
   	   	    writeModelInputData("inputdata.gdx");
   	 			
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
	
    private void writeModelInputData(String fngdxfile) throws GAMSException {        
        GAMS.gdxOpenWrite(fngdxfile, "IEEE14_ED");

        GAMS.gdxDataWriteStrStart("genUnit", "generating unit", 1, gamsglobals.dt_set, 0);
        GAMS.gdxDataWriteIndex("0001");
        GAMS.gdxDataWriteIndex("0002");
        GAMS.gdxDataWriteIndex("0003");
        GAMS.gdxDataWriteIndex("0006");
        GAMS.gdxDataWriteIndex("0008");
        GAMS.gdxDataWriteDone();        
        
        GAMS.gdxDataWriteStrStart("loadBus", "load bus", 1, gamsglobals.dt_set, 0);
        GAMS.gdxDataWriteIndex("0002");
        GAMS.gdxDataWriteIndex("0003");
        GAMS.gdxDataWriteIndex("0004");
        GAMS.gdxDataWriteIndex("0005");
        GAMS.gdxDataWriteIndex("0006");
        GAMS.gdxDataWriteIndex("0009");
        GAMS.gdxDataWriteIndex("0010");
        GAMS.gdxDataWriteIndex("0011");
        GAMS.gdxDataWriteIndex("0012");
        GAMS.gdxDataWriteIndex("0013");
        GAMS.gdxDataWriteIndex("0014");
        GAMS.gdxDataWriteDone();        

        GAMS.gdxDataWriteStrStart("lowerBound", "lower bound of each generating unit", 1, gamsglobals.dt_par, 0);
        GAMS.gdxDataWriteStr("0001", 0.02);
        GAMS.gdxDataWriteStr("0002", 0.03);
        GAMS.gdxDataWriteStr("0003", 0.05);
        GAMS.gdxDataWriteStr("0006", 0.06);
        GAMS.gdxDataWriteStr("0008", 0.07);        
        GAMS.gdxDataWriteDone();
        
        GAMS.gdxDataWriteStrStart("upperBound", "upper bound of each generating unit", 1, gamsglobals.dt_par, 0);
        GAMS.gdxDataWriteStr("0001", 2.7);
        GAMS.gdxDataWriteStr("0002", 0.5);
        GAMS.gdxDataWriteStr("0003", 0.5);
        GAMS.gdxDataWriteStr("0006", 0.5);
        GAMS.gdxDataWriteStr("0008", 0.5);        
        GAMS.gdxDataWriteDone();

        GAMS.gdxDataWriteStrStart("demand", "demand at load bus j", 1, gamsglobals.dt_par, 0);
        GAMS.gdxDataWriteStr("0002", 0.217);
        GAMS.gdxDataWriteStr("0003", 0.942);
        GAMS.gdxDataWriteStr("0004", 0.478);
        GAMS.gdxDataWriteStr("0005", 0.076);
        GAMS.gdxDataWriteStr("0006", 0.112);
        GAMS.gdxDataWriteStr("0009", 0.295);
        GAMS.gdxDataWriteStr("0010", 0.09);
        GAMS.gdxDataWriteStr("0011", 0.035);
        GAMS.gdxDataWriteStr("0012", 0.061);
        GAMS.gdxDataWriteStr("0013", 0.135);
        GAMS.gdxDataWriteStr("0014", 0.149);         
        GAMS.gdxDataWriteDone();

        GAMS.gdxDataWriteStrStart("FactorA", "A(i) weighting factor A for generation cost function", 1, gamsglobals.dt_par, 0);
        GAMS.gdxDataWriteStr("0001", 500);
   		GAMS.gdxDataWriteStr("0002", 300);
   		GAMS.gdxDataWriteStr("0003", 400);
   		GAMS.gdxDataWriteStr("0006", 100);
   		GAMS.gdxDataWriteStr("0008", 50);          
        GAMS.gdxDataWriteDone();

        GAMS.gdxDataWriteStrStart("FactorB", "B(i) weighting factor A for generation cost function", 1, gamsglobals.dt_par, 0);
        GAMS.gdxDataWriteStr("0001", 30);
        GAMS.gdxDataWriteStr("0002", 10);
        GAMS.gdxDataWriteStr("0003", 70);
        GAMS.gdxDataWriteStr("0006", 50);
        GAMS.gdxDataWriteStr("0008", 40);         
        GAMS.gdxDataWriteDone();

        GAMS.gdxDataWriteStrStart("FactorC", "C(i) weighting factor A for generation cost function", 1, gamsglobals.dt_par, 0);
        GAMS.gdxDataWriteStr("0001", 0.01);
        GAMS.gdxDataWriteStr("0002", 0.005);
        GAMS.gdxDataWriteStr("0003", 0.03);
        GAMS.gdxDataWriteStr("0006", 0.06);
        GAMS.gdxDataWriteStr("0008", 0.09);         
        GAMS.gdxDataWriteDone();

/*
        GAMS.gdxDataWriteStrStart("table", " ...", 2, gamsglobals.dt_par, 0);
        GAMS.gdxDataWrite2D("0001", "0002", 0.02);
        GAMS.gdxDataWriteDone();
 */
        
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