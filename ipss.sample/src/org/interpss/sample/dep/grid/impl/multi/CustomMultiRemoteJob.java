package org.interpss.sample.dep.grid.impl.multi;

import java.io.Serializable;

import org.graphdrawing.gml.GraphType;
import org.interpss.grid.GridConstants;
import org.interpss.grid.gridgain.job.GridAclfReJob;
import org.interpss.grid.msg.RemoteMessageTable;
import org.interpss.sample.dep.gml.GmlHelper;

import com.interpss.CoreObjectFactory;
import com.interpss.core.aclf.AclfNetwork;

public class CustomMultiRemoteJob extends GridAclfReJob {
	private static final long serialVersionUID = 1;

	public CustomMultiRemoteJob(RemoteMessageTable inRemoteMsg) {
		super(inRemoteMsg);
	}
	
	@Override
	protected Serializable performGridJob(RemoteMessageTable inRemoteMsg) {
		RemoteMessageTable outRemoteResult = new RemoteMessageTable();
		try {
			// get the cached base network object 
			AclfNetwork net = CoreObjectFactory.createAclfNetwork(
					getSessionStringAttrib(GridConstants.SeKey_BaseStudyCaseNetModel));
			
			// get the GML graph object and create sub-network
			String anyString = inRemoteMsg.getStudyCaseModification();
			GraphType graph = GmlHelper.parseGraph(anyString);
			AclfNetwork subNet = GmlHelper.createSubNet(net, graph);
			System.out.println(subNet.net2String());
				
			// do whatever you want, for example, perform GS loadflow to the 
			// sub-network
			
			// an example to send some results back to the master node
			outRemoteResult.put("MyKey", "MyValue");
		} catch (Exception e) {
			outRemoteResult.put(GridConstants.MsgKEY_Rsp_bReturnStatus, Boolean.FALSE);
			outRemoteResult.addReturnMessage(e.toString());
		}
		return outRemoteResult;
	}
}
