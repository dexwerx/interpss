package com.interpss.QA;

import com.interpss.QA.rfile.FileReader;
import com.interpss.QA.rfile.IFileProcessor;
import com.interpss.QA.rfile.aclf.PSLFComapreFileProcessor;
import com.interpss.QA.rfile.aclf.PSSECompareFileProcessor;
import com.interpss.core.aclf.AclfNetwork;

public class QAObjectFactory {
	public static IFileProcessor createFileProcessor(AclfNetwork net, FileReader.Type type) {
		return createFileProcessor(net, type, false);
	}
	
	public static IFileProcessor createFileProcessor(AclfNetwork net, FileReader.Type type, boolean netOnly) {
		if (type == FileReader.Type.PSSEAclfResult)
			return new PSSECompareFileProcessor(net, netOnly);
		else if (type == FileReader.Type.BPAAclfResult)
			return new PSLFComapreFileProcessor(net, netOnly);
		else if (type == FileReader.Type.PSLFAclfResult)
			return new PSLFComapreFileProcessor(net, netOnly);
		else
			return null;
		
	}

}
