package org.interpss.vstab.cpf;
/**
 * 
 * @author Tony Huang
 *
 */
public class GenDispPattern {
    /**
     * GenDispPtn: is to define the way how generators re-dispatch to meet the gen-load mismatch;
     *   1. AGC ,or Automatic Generation Control, is to regulate the network frequency to nominal value and to 
     *   maintain interchange power between controlled areas to the scheduled value, by automatically changing the generators' 
     *   reference output power.
     *   2. RESERVE_PROPORTION: to dispatch the gen-load mismatch among the regulated generators according to their power reserve.
     *   3. CUSTOM_SPECIFIC: to dispatch the mismatch among generating units selected by the users/operators, strictly according 
     *   to the customized rules.
     *
     */
	public enum GenDispPtn{AGC,RESERVE_PROPORTION,CUSTOM_SPECIFIC};
}
