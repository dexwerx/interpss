package test.ed;

import com.interpss.gams.GAMS;
import com.interpss.gams.common.GAMSException;
import com.interpss.gams.common.GAMSLogger;
import com.interpss.gams.util.GAMSHelper;

public class Ieee14Ed0 {
    public static void main(String[] args) {
    	GAMS.GamsDir = "c:\\Program Files (x86)\\GAMS23.7";
    	GAMS.JNIDir = "c:/eclipse/JNI";

   	 	try {
   	 		GAMS.init();
   	 		
   	 		GAMSHelper helper = new GAMSHelper();
   	   	    helper.CallGams("model\\ieee14_ed.gms");
   	   	        
   	   	    helper.readResult("ieee14_results.gdx", 
   	   	        		new String[] {"result"}, 
   	   	        		helper.getResultReader1D());
   	 	} catch (GAMSException e) {
   	 			GAMSLogger.getLogger().severe(e.toString());
   	 	} finally {
   	        GAMS.free();
   	 	}
    }
}