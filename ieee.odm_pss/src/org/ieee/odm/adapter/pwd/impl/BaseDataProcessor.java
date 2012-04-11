package org.ieee.odm.adapter.pwd.impl;

import java.util.List;

import org.ieee.odm.adapter.pwd.PowerWorldAdapter;
import org.ieee.odm.model.aclf.AclfModelParser;
 /**
  * 
  * @version 0.1  04/03/2012
  * @author Tony Huang
  * 
  */
public abstract class BaseDataProcessor {
	protected List<PowerWorldAdapter.NVPair> inputNvPairs;
	protected AclfModelParser parser;
	
	public BaseDataProcessor() { }

	public BaseDataProcessor(List<PowerWorldAdapter.NVPair> nvPairs, AclfModelParser parser) {
		this.inputNvPairs = nvPairs;
		this.parser = parser;
	}
}
