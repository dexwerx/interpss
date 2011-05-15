package org.interpss.dstab.control.gov.bpa.hydro;

import java.lang.reflect.Field;

import org.interpss.dstab.control.cml.block.DelayControlBlock;
import org.interpss.dstab.control.cml.block.FilterControlBlock;
import org.interpss.dstab.control.cml.block.GainBlock;
import org.interpss.dstab.control.cml.block.IntegrationControlBlock;
import org.interpss.dstab.control.cml.block.WashoutControlBlock;

import com.interpss.dstab.controller.AnnotateGovernor;
import com.interpss.dstab.controller.annotate.AnController;
import com.interpss.dstab.controller.annotate.AnControllerField;
import com.interpss.dstab.controller.annotate.AnFunctionField;
import com.interpss.dstab.controller.block.IFunction;
import com.interpss.dstab.controller.block.adapt.FunctionAdapter;
import com.interpss.dstab.datatype.CMLFieldEnum;

/**
 * This model is corresponding to the GH type hydro turbine (a combination of speed governing and hydro turbine)
 * 
 * @author Tony Huang
 * date: 05/05/2011
 */
@AnController(
        input="mach.speed - 1.0",
        output="this.wFilterBlock.y",
        refPoint="this.gainBlock.y + this.gainFBBlock.y+this.washoutFBBlock.y+this.freqDeadBandFunc.u0",//TODO can a function accesses a u0
		display= {"str.Pm,this.output"})
public class GovBPAHydroType extends AnnotateGovernor{

	public double k1=0.92 /* k1=1/Pmax*/;
    @AnControllerField(
            type= CMLFieldEnum.StaticBlock,
            input="mach.speed - 1.0",
            parameter={"type.NoLimit", "this.k1"},
            y0="this.wFilterBlock.u0"	)
    GainBlock gainBlock;
    
    //freq dead band block
	public double freqDeadBand=0.002;
	@AnFunctionField( input="this.refPoint-this.gainBlock.y - this.gainFBBlock.y-this.washoutFBBlock.y" )
	public IFunction freqDeadBandFunc = new FunctionAdapter() {//TODO a function at the beginning of the main frame, shall the control blocks be assigned init order number;
	    public double eval(double[] dAry) { 
	    	if(Math.abs(dAry[0])<0.5*freqDeadBand) return 0;
	    	else return dAry[0];
	    }          
	};
	
	
	public double k_tg=0.5/*k_tg=1/tg */, tp=2,p_up=0.2,p_down=-0.3;
	@AnControllerField(
            type= CMLFieldEnum.ControlBlock,
            input="this.freqDeadBandFunc.y",
            parameter={"type.Limit", "this.k_tg", "this.t1","this.p_up","this.p_down"},
            y0="this.intgBlock.u0"	)
    DelayControlBlock delayBlock;
	
	public double k_it=1 ,pmax=1.7,pmin=0;
	@AnControllerField(
            type= CMLFieldEnum.ControlBlock,
            input="this.delayBlock.y",
            parameter={"type.Limit", "this.k_it", "this.pmax","this.pmin"},
            y0="this.wFilterBlock.u0"	)
    IntegrationControlBlock intBlock;
	
	
	public double kR=0.05;
    @AnControllerField(
            type= CMLFieldEnum.StaticBlock,
            input="this.intBlock.y",
            parameter={"type.NoLimit", "this.kR"},
            feedback=true)//TODO no y0?
    GainBlock rGainBlock;
    
    
    public double k_Dd = 0.3, td =5;
    @AnControllerField(
       type= CMLFieldEnum.ControlBlock,
       input= "this.intBlock.y",
       parameter={"type.NoLimit", "this.k_Dd", "this.td"},
       feedback=true)
    WashoutControlBlock washoutBlock;
	
	 public double ktw = 1.0, tw_2 = 0.1, t1 = -2*tw_2;
	    @AnControllerField(
	            type= CMLFieldEnum.ControlBlock,
	            input="this.gainBlock.y",
	            parameter={"type.NoLimit", "this.ktw", "this.t1", "this.tw_2"},
	            y0="mach.pm"	)
	FilterControlBlock wFilterBlock;
	    
	
	    
	    
	
	
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

}
