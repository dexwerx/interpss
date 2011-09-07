package test.gams;

import com.gams.api.gamsglobals;
import com.interpss.gams.GAMS;
import com.interpss.gams.common.GAMSException;
import com.interpss.gams.common.GAMSLogger;
import com.interpss.gams.util.GAMSHelper;
import com.interpss.gams.util.IReadResult;

public class example2_1 {
    public static void main(String[] args) {
    	GAMS.GamsDir = "c:\\Program Files (x86)\\GAMS23.7";
    	GAMS.JNIDir = "c:/eclipse/JNI";

   	 	try {
   	 		GAMS.init();
   	   	
   	 		WriteModelData("demanddata.gdx");

   	 		GAMSHelper helper = new GAMSHelper();
   	   	    helper.CallGams("model\\model2.gms");

   	   	    helper.readResult("results.gdx", 
   	   	        		new String[] {"result"}, reader);   	   	        
   	 	} catch (GAMSException e) {
   	 		GAMSLogger.getLogger().severe(e.toString());
   	 	} finally {
   	        GAMS.free();
   	 	}
    }
    
    static void WriteModelData(String fngdxfile) throws GAMSException {        
        GAMS.gdxOpenWrite(fngdxfile, "Example1");
        GAMS.gdxDataWriteStrStart("Demand", "Demand data", 1, gamsglobals.dt_par, 0);

        GAMS.gdxDataWriteStr("New-York", 324.0);
        GAMS.gdxDataWriteStr("Chicago" , 299.0);
        GAMS.gdxDataWriteStr("Topeka"  , 274.0);

        GAMS.gdxDataWriteDone();
        GAMS.gdxClose();
    }
    
	private static IReadResult reader = new IReadResult() {
       	public void readResult(int dimension) {
            String[] Indx = new String[gamsglobals.maxdim];
            double[] Values = new double[gamsglobals.val_max];
            int[] N = new int[1];
            
            while(GAMS.gdxDataReadStr(Indx, Values, N) != 0){
                if(Values[gamsglobals.val_level] == 0) //skip level = 0.0 is default
                    continue;
                for(int D = 0; D < dimension; D++){
                    System.out.print(Indx[D]);
                    if(D < dimension-1)
                        System.out.print(".");
                }
                System.out.println(" = " + Values[gamsglobals.val_level]);
            }
            System.out.println("All solution values shown");
        }
    };    
}