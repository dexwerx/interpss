package org.interpss.custom.run;

import org.interpss.custom.IpssCustomAdapter;
import org.interpss.schema.InterPSSXmlType;
import org.interpss.schema.RunStudyCaseXmlType.AnalysisRunType;

import com.interpss.common.msg.IPSSMsgHub;

public interface ICustomRunScriptPlugin extends IpssCustomAdapter {
	/**
	 * create an InterPSS Xml document for the script running
	 * 
	 * @param scripts run scripts, a string representing an XML document
	 * @param msg Ipss msg object
	 * @return null if there is any error
	 */
	InterPSSXmlType createIpssXmlDocument(String scripts, IPSSMsgHub msg);

	/**
	 * create an InterPSS Xml document for the script running
	 * 
	 * @param type analysis run type. There might be multiple ways to build ipss Xml doc
	 * @param scripts run scripts, a string representing an XML document
	 * @param msg Ipss msg object
	 * @return null if there is any error
	 */
	InterPSSXmlType createIpssXmlDocument(AnalysisRunType.Enum type, String scripts, IPSSMsgHub msg);
}
