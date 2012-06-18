package org.interpss.test.odm.opf;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.ieee.odm.ODMObjectFactory;
import org.ieee.odm.model.opf.OpfModelParser;
import org.interpss.mapper.odm.ODMOpfDataMapper;
import org.interpss.test.OpfTestSetup;

import com.interpss.SimuObjectFactory;
import com.interpss.opf.dclf.DclfOpfNetwork;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
public class ODM2OpfMapperTest extends OpfTestSetup {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		File file = new File("testdata/opf/opf_3bus_test.xml");
		OpfModelParser parser = ODMObjectFactory.createOpfModelParser();
		if (parser.parse(new FileInputStream(file))) {
			//System.out.println(parser.toXmlDoc(false));
			SimuContext simuCtx = SimuObjectFactory.createSimuNetwork(SimuCtxType.OPF_NET);
			new ODMOpfDataMapper()
					.map2Model(parser, simuCtx);
			DclfOpfNetwork opfnet=(DclfOpfNetwork)simuCtx.getOpfNet();
			System.out.print(opfnet.net2String());
		}
	}

}
