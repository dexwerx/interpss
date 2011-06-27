package org.interpss.facts.statcom;

import org.apache.commons.math.complex.Complex;
import org.interpss.facts.general.ConverterLF;

import com.interpss.core.aclf.AclfNetwork;

// STATCOM model in load flow
public class StatcomLF {

	AclfNetwork net;	// Power network which the STACOM will installed in
	private String id;	// ID of the shunt compensation bus
	private ConverterLF converter;	// Equivalent admittance of the converter's Thevenin equivalent circuit
	private StatcomControlType type;
	
	// Constructor
	public StatcomLF(AclfNetwork net, String id, Complex ysh, StatcomControlType type) {
		super();
		this.net = net;
		this.id = id;
		this.converter = new ConverterLF(net, id, "GROUND", ysh);
		this.type = type;
	}

	public String getId() {
		return id;
	}
	
	public Complex getSsh() {
		return converter.getSij();
	}
}