package org.ieee.odm.adapter.psse;

public class PSSEAcscAdapter {
	
	
	/*
	 *First record in the first valid line: Change code -- IC
	 *
	 *IC = 0 indicates initialization setting is enabled, those with
	 *no data record entered are set to be their default values.
	 *
	 *IC = 1 indicates modification/update setting is enabled, the records defined
	 *in the input sequence data will be changed to the new value, while the rest are unchanged
	 *
	 */
     
	
	
	/*
	 * post processing
	 * 
	 * 1. Gen zero sequence
	 * 
	 * During the initial input of sequence data (i.e., IC = 0 on the first data record), any machine for which
       no data record of this category is entered has its zero sequence generator impedance, ZZERO, set
       equal to ZPOS, the positive sequence generator impedance
       
       For those machines at which the step-up transformer is represented as part of the generator data
      (i.e., XTRAN is non-zero), ZZERO (i.e., RZERO + j XZERO) is not used and, in the fault analysis
       activities, the step-up transformer is assumed to be a delta wye transformer. Refer to Modeling of
       Generator Step-Up Transformers (GSU).
       
	 * 
	 * 2. bus negative sequence shunt load
	 * 
	 * For any bus where no such data record is specified, or GNEG and BNEG are both specified as zero,
       the load elements are assumed to be equal in the positive and negative sequence networks.
	 * 
	 */
	 
}
