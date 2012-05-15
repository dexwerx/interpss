package org.interpss.test.odm.opf;

import static org.junit.Assert.assertTrue;

import org.interpss.CorePluginObjFactory;
import org.interpss.fadapter.IpssFileAdapter;
import org.junit.Test;

import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.net.Bus;
import com.interpss.simu.SimuContext;

public class TestNonGenCode {
      @Test
	  public void aNonGenCodeTest() throws Exception{
  		String filePath="testdata/opf/ieee30.ieee";
		IpssFileAdapter adapter = CorePluginObjFactory.getFileAdapter(IpssFileAdapter.FileFormat.IEEECDF);
		SimuContext simuCtx = adapter.load(filePath);

		AclfNetwork net = simuCtx.getAclfNet();
		int i=0;
		for(Bus b:net.getBusList()){
			AclfBus bus=(AclfBus) b;
			if(bus.isGen()) i++;
		}
		assertTrue(i==5);
	  }
}
