package org.interpss.test.odm.opf;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.ieee.odm.adapter.IODMAdapter;
import org.ieee.odm.adapter.ieeecdf.IeeeCDFAdapter;
import org.junit.Test;
public class XMLCreatorFromOpfSchema {
//	public XMLCreatorFromOpfSchema(String origOpfFile){
//		OpfNetworkXmlType opf=(OpfNetworkXmlType) ODMObjectFactory.createOpfModelParser().createBaseCase()
//		LoadflowBusXmlType bus=ODMObjectFactory.createOpfModelParser().createAclfBus();
//		LineBranchXmlType bra=ODMObjectFactory.createOpfModelParser().createLineBranch();
//		XfrBranchXmlType bra2=ODMObjectFactory.createOpfModelParser().createXfrBranch();
//		OpfDataSetter.setLoadData(bus, code, p, q, unit);
		
//	}
	@Test
  	public void testODM() throws IOException{
		IODMAdapter adapter=new IeeeCDFAdapter();
		adapter.parseInputFile("testData/ieee_cdf/ieee30.ieee");
		String xmlString=adapter.getModel().toXmlDoc(true);
		OutputStream out=new BufferedOutputStream(new FileOutputStream("E:/ieee30bus.xml"));
		out.write(xmlString.getBytes());
		out.flush();
		out.close();
	}
}
