package test;

import com.gams.api.gamsglobals;
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
	   	 			
	   	   	    writeModelInputData("dcopf_input.gdx");
	   	 			
	   	 		GAMSHelper helper = new GAMSHelper();
	   	   	    helper.CallGams("dcopf.gms", 2, 1);
	   	   	    
	   	   	    helper.readResult("results.gdx", new String[] {"result"}, 
	   	        			helper.getResultReader1D());	   	   	    
	 		} catch (GAMSException e) {
		 		GAMSLogger.getLogger().severe(e.toString());
	 	 	} finally {
	   	   	    GAMS.free();
	   	 	}	
	 }

	 private static void writeModelInputData(String inputFile) throws GAMSException {
		GAMS.gdxOpenWrite(inputFile, "DCOPF");

		GAMS.gdxDataWriteStrStart("BusIndex", "bus index", 1, gamsglobals.dt_set, 0);
		GAMS.gdxDataWriteIndex("bus1");
		GAMS.gdxDataWriteIndex("bus2");
		GAMS.gdxDataWriteIndex("bus3");
		GAMS.gdxDataWriteDone();
		
		GAMS.gdxDataWriteStrStart("GenIndex", "generator bus index", 1, gamsglobals.dt_set, 0);
		GAMS.gdxDataWriteIndex("gen1");
		GAMS.gdxDataWriteIndex("gen2");
		GAMS.gdxDataWriteIndex("gen3");
		GAMS.gdxDataWriteDone();
		
        GAMS.gdxDataWriteStrStart("RealPower", "real power load drawn at LSE k", 1, gamsglobals.dt_par, 0);
        GAMS.gdxDataWriteStr("bus1", 1.3266);
        GAMS.gdxDataWriteStr("bus2", 0.4422);
        GAMS.gdxDataWriteStr("bus3", 0.4422);
		GAMS.gdxDataWriteDone();
		
        GAMS.gdxDataWriteStrStart("ACoeff", "linear cost coefficient at generator i", 1, gamsglobals.dt_par, 0);
        GAMS.gdxDataWriteStr("gen1", 10.694);
        GAMS.gdxDataWriteStr("gen2", 18.100);
        GAMS.gdxDataWriteStr("gen3", 37.8896);
		GAMS.gdxDataWriteDone();
		
        GAMS.gdxDataWriteStrStart("BCoeff", "quantratic  const coefficient at generator i", 1, gamsglobals.dt_par, 0);
        GAMS.gdxDataWriteStr("gen1", 0.00463);
        GAMS.gdxDataWriteStr("gen2", 0.00612);
        GAMS.gdxDataWriteStr("gen3", 0.01433);
		GAMS.gdxDataWriteDone();
		
        GAMS.gdxDataWriteStrStart("Pgmax", "max real power output at generator i(in pu unit)", 1, gamsglobals.dt_par, 0);
        GAMS.gdxDataWriteStr("gen1", 2);
        GAMS.gdxDataWriteStr("gen2", 1.5);
        GAMS.gdxDataWriteStr("gen3", 0.2);
		GAMS.gdxDataWriteDone();
		
        GAMS.gdxDataWriteStrStart("Pgmin", "min real power output at generator i(in pu unit)", 1, gamsglobals.dt_par, 0);
        GAMS.gdxDataWriteStr("gen1", 0.2);
        GAMS.gdxDataWriteStr("gen2", 0.1);
        GAMS.gdxDataWriteStr("gen3", 0.05);
		GAMS.gdxDataWriteDone();
		
		GAMS.gdxDataWriteStrStart("BMatrix", "the [B] matrix of the network", 2, gamsglobals.dt_par, 0);
        GAMS.gdxDataWrite2D("bus1", "bus1", 7.5);   
        GAMS.gdxDataWrite2D("bus1", "bus2", 5);   
        GAMS.gdxDataWrite2D("bus1", "bus3", 2.5);
        GAMS.gdxDataWrite2D("bus2", "bus1", 5);    
        GAMS.gdxDataWrite2D("bus2", "bus2", 9);   
        GAMS.gdxDataWrite2D("bus2", "bus3", 4);
        GAMS.gdxDataWrite2D("bus3", "bus1", 2.5);   
        GAMS.gdxDataWrite2D("bus3", "bus2", 4);   
        GAMS.gdxDataWrite2D("bus3", "bus3", 6.5);        
        GAMS.gdxDataWriteDone();
        
        GAMS.gdxDataWriteStrStart("Rating", "branch rating limit in pu unit", 2, gamsglobals.dt_par, 0);
        GAMS.gdxDataWrite2D("bus1", "bus2", 0.55);   
        GAMS.gdxDataWrite2D("bus1", "bus3", 0.55);
        GAMS.gdxDataWrite2D("bus2", "bus1", 0.55);    
        GAMS.gdxDataWrite2D("bus2", "bus3", 0.55);
        GAMS.gdxDataWrite2D("bus3", "bus1", 0.55);   
        GAMS.gdxDataWrite2D("bus3", "bus2", 0.55);   
        GAMS.gdxDataWriteDone();

        GAMS.gdxDataWriteStrStart("BusGenRelation", "bus and generator relationship table", 2, gamsglobals.dt_par, 0);
        GAMS.gdxDataWrite2D("bus1", "gen1", 1);   
        GAMS.gdxDataWrite2D("bus2", "gen2", 1);   
        GAMS.gdxDataWrite2D("bus3", "gen3", 1);        
        GAMS.gdxDataWriteDone();
		
        GAMS.gdxClose();
	}
}
