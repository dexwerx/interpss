package test.opf;

import com.interpss.gams.GAMS;
import com.interpss.gams.common.GAMSException;
import com.interpss.gams.common.GAMSLogger;
import com.interpss.gams.util.GAMSHelper;

public class DcOPFSample {
	 public static void main(String[] args) {
	    	GAMS.GamsDir = "c:\\Program Files (x86)\\GAMS24.0";
	    	GAMS.JNIDir = "c:\\GANSJNI";
	    	
	    	new DcOPFSample().run();
	 }
	    	
   	private void run() {
	 		try {
	   	 		GAMS.init();
	   	 			
	   	 		GAMSHelper helper = new GAMSHelper();
	   	   	    helper.CallGams("testdata\\dcopf.gms");
	   	   	    
	   	   	    helper.readResult("results.gdx", new String[] {"result"}, 
	   	        			helper.getResultReader1D());	   	   	    
	 		} catch (GAMSException e) {
		 		GAMSLogger.getLogger().severe(e.toString());
	 	 	} finally {
	   	   	    GAMS.free();
	   	 	}	
	 }
}
