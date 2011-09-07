package test.opf;

import com.interpss.gams.GAMS;
import com.interpss.gams.common.GAMSException;
import com.interpss.gams.common.GAMSLogger;
import com.interpss.gams.util.GAMSHelper;

public class DcOPFSample {
	 public static void main(String[] args) {
	    	GAMS.GamsDir = "c:\\Program Files (x86)\\GAMS23.7";
	    	GAMS.JNIDir = "c:/eclipse/JNI";
	
	 		try {
	   	 		GAMS.init();
	   	 			
	   	 		GAMSHelper helper = new GAMSHelper();
	   	   	    helper.CallGams("model\\dcopf.gms", 2, 1);
	   	   	    
	   	   	    helper.readResult("results.gdx", new String[] {"result"}, 
	   	        			helper.getResultReader1D());	   	   	    
	 		} catch (GAMSException e) {
		 		GAMSLogger.getLogger().severe(e.toString());
	 	 	} finally {
	   	   	    GAMS.free();
	   	 	}	
	 }
}
