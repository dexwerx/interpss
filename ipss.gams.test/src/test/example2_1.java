package test;

import com.gams.api.gamsglobals;
import com.interpss.gams.GAMS;
import com.interpss.gams.common.GAMSException;
import com.interpss.gams.common.GAMSLogger;
import com.interpss.gams.util.GAMSHelper;

public class example2_1 {
    public static void main(String[] args) {
    	GAMS.GamsDir = "c:\\Program Files (x86)\\GAMS23.7";
    	GAMS.JNIDir = "c:/eclipse/JNI";

   	 	try {
   	 		GAMS.init();
   	   	
   	 		WriteModelData("demanddata.gdx");

   	 		GAMSHelper helper = new GAMSHelper();
   	   	    helper.CallGams("model2.gms", 2, 1);

   	   	    helper.getReadResult("results.gdx", 
   	   	        		new String[] {"result"}, 
   	   	        		helper.getResultReader2D());   	   	        
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
}