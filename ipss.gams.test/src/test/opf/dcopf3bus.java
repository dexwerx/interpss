package test.opf;

import com.interpss.gams.GAMS;
import com.interpss.gams.common.GAMSException;
import com.interpss.gams.common.GAMSLogger;
import com.interpss.gams.util.GAMSHelper;

public class dcopf3bus {
    public static void main(String[] args) {
    	GAMS.GamsDir = "c:\\Program Files (x86)\\GAMS23.7";
    	GAMS.JNIDir = "c:/eclipse/JNI";

   	 	try {
   	 		GAMS.init();
   	 		
   	 			// TODO: pass data to GAMS

   	 			GAMSHelper helper = new GAMSHelper();
   	   	        helper.CallGams("model\\3busdcopf.gms", 2, 2);

   	   	        // TODO: get GAMS result
   	 	} catch (GAMSException e) {
   	 		GAMSLogger.getLogger().severe(e.toString());
   	 	} finally {
   	        GAMS.free();
   	 	}
    }
}