package org.interpss.custom.run;

import org.interpss.xml.schema.AnalysisRunDataType;
import org.interpss.xml.schema.InterPSSXmlType;

import com.interpss.common.msg.IPSSMsgHub;

public abstract class CustomRunScriptPluginBase implements ICustomRunScriptPlugin {
	@Override
	public InterPSSXmlType createIpssXmlDocument(AnalysisRunDataType type, String scripts) {
		throw new UnsupportedOperationException();
	}

	@Override
	public InterPSSXmlType createIpssXmlDocument(String scripts, IPSSMsgHub msg) {
		throw new UnsupportedOperationException();
	}

	private String name;
	private String desc;
	
	@Override
	public String getDescription() {
		return this.desc;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public void setDescription(String s) {
		this.desc = s;
	}

	public void setName(String s) {
		this.name = s;
	}
}
