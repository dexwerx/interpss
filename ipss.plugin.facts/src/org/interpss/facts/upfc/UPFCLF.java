package org.interpss.facts.upfc;

import org.interpss.facts.general.ConverterLF;
import com.interpss.core.aclf.AclfNetwork;

// UPFC model in load flow
public class UPFCLF {
	
	private String idi;	// ID of the bus with the shunt converter
	private String idj;	// ID of the bus without the shunt converter
	private ConverterLF shuntConverter;	// Equivalent admittance of the shunt converter's Thevenin equivalent circuit
	private ConverterLF serialConverter;	// Equivalent admittance of the serial converter's Thevenin equivalent circuit
	private UPFCControlType type;	// Control type of the UPFC
	private double tunedValue1;	// First tuned value under current control type
	private double tunedValue2;	// Second tuned value under current control type
	
	private AclfNetwork net;
	
}