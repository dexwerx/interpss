package test;

import com.gams.api.gamsglobals;
import com.interpss.gams.GAMS;
import com.interpss.gams.common.GAMSException;
import com.interpss.gams.common.GAMSLogger;
import com.interpss.gams.file.ITemplateProcessor;
import com.interpss.gams.file.ModelIncFileCreator;
import com.interpss.gams.util.GAMSHelper;
import com.interpss.gams.util.IReadResult;

/*
 * To run this example,
 * 
 *    - put ieee14_java1.gms and ieee14_java.template in the current dir
 *    - include file ieee14_java.inc and gdx file inputdata.gdx are created during the
 *      running process
 */

public class ieee14_ed1 {
    public static void main(String[] args) {
    	GAMS.GamsDir = "c:\\Program Files (x86)\\GAMS23.7";
    	GAMS.JNIDir = "c:/eclipse/JNI";
    	
    	new ieee14_ed1().run();
    }
    
    private void run() {
 		try {
   	 		GAMS.init();
   	 			
   	 		// option-1: use include file
   	 		new ModelIncFileCreator(templateProcessor)
   	 					.createIncFile("ieee14_java.template", "ieee14_java.inc");
   	 		
   	 		// option-2: use gdx file
   	   	    writeModelInputData("inputdata.gdx");
   	 			
   	 		GAMSHelper helper = new GAMSHelper();
   	   	    helper.CallGams("ieee14_java1.gms",  
   	   	    		GAMS.OutLogLevel_List, GAMS.RunLogLevel_WARN);
   	   	        
   	   	    helper.readResult("ieee14_results.gdx", new String[] {"result"}, 
   	   	        			resultReader);
	 	} catch (GAMSException e) {
	 		GAMSLogger.getLogger().severe(e.toString());
 	 	} finally {
   	   	    GAMS.free();
   	 	}	
    }
    
    private ITemplateProcessor templateProcessor = new ITemplateProcessor() {
		public String processToken(String lineStr) {
			if (lineStr.contains("&&Set_genUnit&&")) {
				return 
				"0001, 0002, 0003, 0006,0008";
			}
			else if (lineStr.contains("&&Set_loadBus&&")) {
				return 
				"0002, 0003,0004,  0005, 0006, 0009" + "\n" +
                "0010, 0011,  0012, 0013, 0014";
			}
			else if (lineStr.contains("&&Param_lower&&")) {
				return 
                "0001        0.02" + "\n" +
                "0002        0.03" + "\n" +
                "0003        0.05" + "\n" +
                "0006        0.06" + "\n" +
                "0008        0.07";      				
			}
			else if (lineStr.contains("&&Param_upper&&")) {
				return
                "0001        2.7" + "\n" +
                "0002        0.5" + "\n" +
                "0003        0.5" + "\n" +
                "0006        0.5" + "\n" +
                "0008        0.5";    				
			}
			else if (lineStr.contains("&&Param_d&&")) {
				return
                "0002         0.217" + "\n" +
                "0003         0.942" + "\n" +
                "0004         0.478" + "\n" +
                "0005         0.076" + "\n" +
                "0006         0.112" + "\n" +
                "0009         0.295" + "\n" +
                "0010         0.09"  + "\n" +
                "0011         0.035" + "\n" +
                "0012         0.061" + "\n" +
                "0013         0.135" + "\n" +
                "0014         0.149";    				
			}
			return lineStr;
		}
	};
	
    private void writeModelInputData(String fngdxfile) throws GAMSException {        
        GAMS.gdxOpenWrite(fngdxfile, "IEEE14_ED");
        
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