package org.interpss.test.odm.opf;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.ieee.odm.ODMObjectFactory;
import org.ieee.odm.model.opf.OpfModelParser;
import org.interpss.mapper.odm.ODMOpfDataMapper;
import org.interpss.test.OpfTestSetup;

import com.interpss.opf.OpfNetwork;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.simu.SimuObjectFactory;
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
			OpfNetwork opfnet=simuCtx.getOpfNet();
			System.out.print(opfnet.net2String());
		}
	}

}
