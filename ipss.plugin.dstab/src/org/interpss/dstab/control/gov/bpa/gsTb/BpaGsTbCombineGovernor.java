package org.interpss.dstab.control.gov.bpa.gsTb;

import java.lang.reflect.Field;

import org.interpss.dstab.control.exc.ieee.y1968.type1.Ieee1968Type1ExciterData;

import com.interpss.dstab.DStabBus;
import com.interpss.dstab.controller.AnnotateGovernor;
import com.interpss.dstab.controller.annotate.AnController;
import com.interpss.dstab.mach.Machine;

public class BpaGsTbCombineGovernor extends AnnotateGovernor{
    
	
	 /**
     * Default Constructor
     *
     */
    public BpaGsTbCombineGovernor() {
		this("id", "name", "caty");
        this.setName("Bpa GS+TB combined type");
        this.setCategory("BPA");
    }
    
    /**
     * Constructor
     *
     * @param id excitor id
     * @param name excitor name
     */
    public BpaGsTbCombineGovernor(String id, String name, String caty) {
        super(id, name, caty);
        // _data is defined in the parent class. However init it here is a MUST
        _data = new BpaGsTbCombineGovernordata();
    }
    
    /**
     * Get the excitor data
     *
     * @return the data object
     */
    public BpaGsTbCombineGovernordata getData() {
        return (BpaGsTbCombineGovernordata)_data;
    }
	@Override
	public AnController getAnController() {
		return getClass().getAnnotation(AnController.class); 
	}

	@Override
	public Field getField(String fieldName) throws Exception {
		return getClass().getField(fieldName); 
	}

	@Override
	public Object getFieldObject(Field field) throws Exception {
		return field.get(this);    
		
	}
	
	@Override
	public boolean initStates(DStabBus bus, Machine mach) {
//        this.ka = getData().getKa();
//        this.ta = getData().getTa();
//        this.vrmax = getData().getVrmax();
//        this.vrmin = getData().getVrmin();
//		this.ke1 = 1.0/getData().getKe();
//		this.te_ke = getData().getTe() / getData().getKe();
//		this.e1 = getData().getE1();
//		this.seE1 = getData().getSeE1();
//		this.e2 = getData().getE2();
//		this.seE2 = getData().getSeE2();
//		this.k = getData().getKf()/getData().getTf();
//		this.tf = getData().getTf();
        return super.initStates(bus, mach);
    }


}
