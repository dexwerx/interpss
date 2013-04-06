package test.opf;

import com.interpss.gams.GAMS;
import com.interpss.gams.common.GAMSException;
import com.interpss.gams.common.GAMSLogger;
import com.interpss.gams.util.GAMSHelper;

public class dcopf3bus {
    public static void main(String[] args) {
    	GAMS.GamsDir = "c:\\Program Files (x86)\\GAMS24.0";
    	GAMS.JNIDir = "C:\\GAMSJNI";
    	System.setProperty( "java.library.path", "C:\\GAMSJNI" );
   	 	try {
   	 		GAMS.init();
   	 		
   	 			// TODO: pass data to GAMS

   	 			GAMSHelper helper = new GAMSHelper();
   	   	        helper.CallGams("testdata\\3busdcopf.gms", 2, 2);

   	   	        // TODO: get GAMS result
   	 	} catch (GAMSException e) {
   	 		GAMSLogger.getLogger().severe(e.toString());
   	 	} finally {
   	        GAMS.free();
   	 	}
    }
}