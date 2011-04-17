package org.interpss.sample.grid.impl.multi;

import java.io.Serializable;

import org.graphdrawing.gml.GraphType;
import org.interpss.grid.gridgain.GridConstants;
import org.interpss.grid.gridgain.job.GridAclfJob;
import org.interpss.grid.msg.RemoteMessageTable;
import org.interpss.sample.gml.GmlHelper;

import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfNetwork;

public class CustomMultiRemoteJob extends GridAclfJob {
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
					getSessionStringAttrib(GridConstants.Key_BaseStudyCaseNetworkModel));
			
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
			outRemoteResult.put(RemoteMessageTable.KEY_bRsp_ReturnStatus, Boolean.FALSE);
			outRemoteResult.addReturnMessage(e.toString());
		}
		return outRemoteResult;
	}
}
