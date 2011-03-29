package com.interpss.QA;

import com.interpss.QA.rfile.FileReader;
import com.interpss.QA.rfile.IFileProcessor;
import com.interpss.QA.rfile.aclf.PSLF_FileProcessor;
import com.interpss.QA.rfile.aclf.PSSE_FileProcessor;
import com.interpss.core.aclf.AclfNetwork;

public class QAObjectFactory {
	public static IFileProcessor createFileProcessor(AclfNetwork net, FileReader.Type type) {
		if (type == FileReader.Type.PSSEAclfResult)
			return new PSSE_FileProcessor(net);
		else if (type == FileReader.Type.BPAAclfResult)
			return new PSLF_FileProcessor(net);
		else if (type == FileReader.Type.PSLFAclfResult)
			return new PSLF_FileProcessor(net);
		else
			return null;
		
	}

}
