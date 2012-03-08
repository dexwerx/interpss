package org.interpss.mapper.odm;

import org.ieee.odm.schema.NetworkXmlType;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.aclf.AclfNetwork;

public interface IMainChildNetProcessor {
	/**
	 * get the main parent network object
	 * 
	 * @param net
	 * @return
	 */
	IMainChildNetProcessor setMainNet(AclfNetwork net);
	
	/**
	 * process the child network xml object
	 * 
	 * @param netXml
	 */
	void process(NetworkXmlType netXml) throws InterpssException;
}
