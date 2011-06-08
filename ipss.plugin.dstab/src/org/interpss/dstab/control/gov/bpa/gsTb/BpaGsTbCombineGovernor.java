package org.interpss.dstab.control.gov.bpa.gsTb;

import java.lang.reflect.Field;

import org.interpss.dstab.control.cml.block.DelayControlBlock;
import org.interpss.dstab.control.cml.block.FilterControlBlock;
import org.interpss.dstab.control.cml.block.GainBlock;
import org.interpss.dstab.control.cml.block.IntegrationControlBlock;
import org.interpss.dstab.control.cml.block.WashoutControlBlock;
import org.interpss.dstab.control.cml.func.SeFunction;
import org.interpss.dstab.control.exc.ieee.y1968.type1.Ieee1968Type1ExciterData;
import org.interpss.dstab.control.exc.ieee.y1968.type1.NBIeee1968Type1EditPanel;

import com.interpss.dstab.DStabBus;
import com.interpss.dstab.controller.AnnotateExciter;
import com.interpss.dstab.controller.AnnotateGovernor;
import com.interpss.dstab.controller.annotate.AnController;
import com.interpss.dstab.controller.annotate.AnControllerField;
import com.interpss.dstab.controller.annotate.AnFunctionField;
import com.interpss.dstab.datatype.CMLFieldEnum;
import com.interpss.dstab.mach.Machine;


    
@AnController(
		   input="mach.speed-1",
		   output="this.fhp*this.tchDelayBlock.y+this.lambda*this.fhp*this.tchDelayBlock.y+this.fip*this.trhDelayBlock.y-this.lambda*this.fhp*this.trhDelayBlock.y+this.flp*this.tcoDelayBlock.y",
		   refPoint="this.filterBlock.y+this.t3GainBlock.u0+this.fbGainBlock.y",
		   display= {"str.Pm,this.output"})
public class BpaGsTbCombineGovernor extends AnnotateExciter {
	//1.1 GainBlock
	public double pmax0 = 1500, pmin0 = 0.0, p0 = 100;
	public double pmax = pmax0/p0, r = 0.05, k = pmax/r;
	@AnControllerField(
		 				type= CMLFieldEnum.StaticBlock,
		 				input="mach.speed-1",
		 				parameter={"type.NoLimit", "this.k"},
						y0="this.filterBlock.u0"	)
   GainBlock kGainBlock;	
  
    //1.2 filterBlock
    public double one = 1.0/*constant*/, t1 = 0.10, t2 = 0.0;
	@AnControllerField(
		 				type= CMLFieldEnum.ControlBlock,
		 				input="this.kGainBlock.y",
		 				parameter={"type.NoLimit", "this.one", "this.t2", "this.t1"},
						y0="this.refPoint-this.t3GainBlock.u0-this.fbGainBlock.y"	)
    FilterControlBlock filterBlock;	  

	
	//1.4 t3GainBlock
	public double t3 = 0.5, k3 = 1/t3;
	@AnControllerField(
		 				type= CMLFieldEnum.StaticBlock,
		 				input="this.refPoint-this.filterBlock.y-this.fbGainBlock.y",
		 				parameter={"type.NoLimit", "this.k3"},
						y0="this.pGainBlock.u0"	)
    GainBlock t3GainBlock;		
	
	//1.5 pGainBlock
	public double VELopen = 0.1, VELclose = 1.0, pup = VELopen*pmax, pdown = -VELclose*pmax;
	@AnControllerField(
		 				type= CMLFieldEnum.StaticBlock,
		 				input="this.t3GainBlock.y",
		 				parameter={"type.Limit", "this.one", "this.pup", "this.pdown"},
						y0="this.intBlock.u0"	)
    GainBlock pGainBlock;		
  
    //1.6 intBlock
    public double pmin = pmin0/p0;
	@AnControllerField(
		 				type= CMLFieldEnum.ControlBlock,
		 				input="this.pGainBlock.y",
		 				parameter={"type.NonWindup", "this.one", "this.pmax", "this.pmin"},
						y0="this.tchDelayBlock.u0"	)
    IntegrationControlBlock intBlock;	  	

	//1.7 feedback
	@AnControllerField(
		 				type= CMLFieldEnum.StaticBlock,
		 				input="this.intBlock.y",
		 				parameter={"type.NoLimit", "this.one"},
						feedback=true )
    GainBlock fbGainBlock;		

	public double fhp=0.33,fip=0.67,flp=0.0, lambda=0.0;
	//2.1 tchDelayBlock
	public double kch=1.0/*constant*/, tch=0.2;
	@AnControllerField(
	          type= CMLFieldEnum.ControlBlock,
	          input="this.intBlock.y",
	          parameter={"type.NoLimit", "this.kch", "this.tch"},
	          y0="this.trhDelayBlock.u0")
	DelayControlBlock tchDelayBlock;

	//2.2 trhDelayBlock
	public double krh=1.0/*constant*/, trh=10.0;
	@AnControllerField(
	          type= CMLFieldEnum.ControlBlock,
	          input="this.tchDelayBlock.y",
	          parameter={"type.NoLimit", "this.krh", "this.trh"},
	          y0="this.tcoDelayBlock.u0")
	DelayControlBlock trhDelayBlock;
	
	//2.3 tcoDelayBlock
	public double kco=1.0/*constant*/, tco=0.0,factor=1.0/(fhp+fip+flp);
	@AnControllerField(
	          type= CMLFieldEnum.ControlBlock,
	          input="this.trhDelayBlock.y",
	          parameter={"type.NoLimit", "this.kco", "this.tco"},
	          y0="this.factor*mach.pm")
	DelayControlBlock tcoDelayBlock;
			 	
	// UI Editor panel
	private static BpaGsTbCombineGovernorEditPanel _editPanel = new BpaGsTbCombineGovernorEditPanel();	
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
