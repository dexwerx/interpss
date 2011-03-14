package org.interpss.vstab.cpf;

import java.util.Hashtable;

import com.interpss.common.util.IpssLogger;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.net.Bus;

/**
 * 
 * @author Tony Huang
 *
 */
public class GenDispPattern {
	private AclfNetwork net=null;
	private Hashtable<String,Double> genPdir=null;
    private Hashtable<Integer,Double> genPmax; 
    private Hashtable<Integer,Double> genP0;
    private Pattern pattern=null;
	public GenDispPattern(AclfNetwork net, Pattern ptn){
		this.net=net;
		this.pattern=ptn;
		initialize();
	
		
	}
    /**
     *   GenDispPtn: is to define the way how generators re-dispatch to meet the gen-load mismatch;
     *   1. AGC ,or Automatic Generation Control, is to regulate the network frequency to nominal value and to 
     *   maintain interchange power between controlled areas to the scheduled value, by automatically changing the generators' 
     *   reference output power.
     *   2. RESERVE_PROPORTION: to dispatch the gen-load mismatch among the regulated generators according to their power reserve.
     *   3. BASE_CASE_DIR: using the base case generation direction for the following analysis.
     *   4. CUSTOM_SPECIFIC: to dispatch the mismatch among generating units selected by the users/operators, strictly according 
     *   to the customized rules.
     *
     */
	public static enum Pattern{AGC,RESERVE_PROPORTION,BASE_CASE_DIR,CUSTOM_SPECIFIC};
	
    /**
     * AGC and RESERVE_PROPORTION patterns change the direction in every step,
     * thus they are not supported in this method, we only consider the remaining two types ,BASE_CASE_DIR and CUSTOM_SPECIFIC
     * 
     * @return Generation re-dispatching Direction
     */
	public Hashtable<String, Double> getGenDirection(){
		return genPdir;
	}
	public void setGenDirection(Hashtable<String,Double> genPdirection){
		this.genPdir=genPdirection;
	}
	private Hashtable<String, Double> baseCaseAsGenDir(){
		genPdir=new Hashtable<String,Double>(getNumOfGenPV());
		for(Bus b:this.net.getBusList()){
			AclfBus bus=(AclfBus) b;
			if(bus.isGenPV()){
				genPdir.put(bus.getId(), bus.getGenP());
			}
		}
		return genPdir;
	}
	public void cutomizeGenDirection(Hashtable<String,Double> genPdirection){
		for(String id: genPdirection.keySet()){
			if(!this.net.getAclfBus(id).isActive()||!this.net.getAclfBus(id).isGenPV()){
				genPdirection.remove(id);
				IpssLogger.getLogger().severe("Warming: Bus #"+this.net.getAclfBus(id).getId()+
						"is NOT an active gen PV bus, remove from the direction definition table");
				
			}
		}
		this.genPdir=genPdirection;
	}
	private void genDirByResvProp(){
		for(int i=0;i<this.net.getBusList().size();i++){
			AclfBus bus=(AclfBus)this.net.getBusList().get(i);
			if(bus.isGenPV()){// only PV bus is considered here
				if(bus.getPGenLimit()!=null){
				  this.genPdir.put(bus.getId(),bus.getPGenLimit().getMax()-bus.getGenP());
				}
				else{
					
				}
			}
		}
	}
	private int getNumOfGenPV(){
		int numPV=0;
		for(Bus b:this.net.getBusList()){
			AclfBus bus=(AclfBus) b;
			if(bus.isGenPV()) ++numPV;
		}
		return numPV;
		
	}
	/**
	 * @param pattern the pattern to set
	 */
	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}
	/**
	 * @return the pattern
	 */
	public Pattern getPattern() {
		return pattern;
	}
	private void initialize(){
		if(this.pattern==Pattern.BASE_CASE_DIR)baseCaseAsGenDir();
		else if(this.pattern==Pattern.RESERVE_PROPORTION) genDirByResvProp();
		else throw new UnsupportedOperationException();
	}
	
		
			
	   
}
