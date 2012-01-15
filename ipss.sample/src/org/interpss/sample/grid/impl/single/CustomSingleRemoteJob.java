package org.interpss.sample.grid.impl.single;

import java.io.Serializable;

import org.interpss.grid.GridConstants;
import org.interpss.grid.gridgain.job.GridAclfReJob;
import org.interpss.grid.msg.RemoteMessageTable;
import org.interpss.grid.result.RemoteResultFactory;

import com.interpss.CoreObjectFactory;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;

/**
 * This is the place where actual loadflow is implemented. This class will be
 * executed in the remote grid node. The input and output are communicated through
 * the RemoteMessageTable object 
 * 
 * @author mzhou
 */

public class CustomSingleRemoteJob extends GridAclfReJob {
	private static final long serialVersionUID = 1;

	public CustomSingleRemoteJob(RemoteMessageTable inRemoteMsg) {
		super(inRemoteMsg);
	}
	
	@Override
	protected Serializable performGridJob(RemoteMessageTable inRemoteMsg) {
		RemoteMessageTable outRemoteResult = new RemoteMessageTable();
		try {
			/*
			 * de-serialize the job object
			 */
			AclfNetwork net = CoreObjectFactory.createAclfNetwork();
			net = (AclfNetwork)net.deserialize(inRemoteMsg.getSerializedAclfNet());
			
			/*
			 * Perform Loadflow calculation. One can do anyting to the AclfNetwork object
			 * here. Loadflow is just used as an example.
			 */
			LoadflowAlgorithm algo = CoreObjectFactory
						.createLoadflowAlgorithm(net);
			algo.loadflow();
	 
			/*
			 * Save results into the RemoteResult table, which will be sent to the master node
			 */
			String caseId = inRemoteMsg.getStudyCaseId();
			RemoteResultFactory.createHandler(CustomSingleRemoteJob.class)
						.saveRemoteResult(outRemoteResult, caseId, getGrid().getLocalNode().getId().toString(), 
								algo, getSession());  // Loadflow results stored in the algo object

			// an example to send some results back to the master node
			outRemoteResult.put("MyKey", "MyValue");
		} catch (Exception e) {
			outRemoteResult.put(GridConstants.MsgKEY_Rsp_bReturnStatus, Boolean.FALSE);
			outRemoteResult.addReturnMessage(e.toString());
		}
		return outRemoteResult;
	}
}
