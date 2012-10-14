
package org.interpss.sample.dep.grid.impl.single;

import org.interpss.grid.gridgain.job.base.AbstractGridGainJob;
import org.interpss.grid.gridgain.task.singleJob.AclfSingleJobTask;
import org.interpss.grid.msg.RemoteMessageTable;

public class CustomSingleLocalTask extends AclfSingleJobTask {
	private static final long serialVersionUID = 1;
	
	@Override
	protected AbstractGridGainJob createJob(RemoteMessageTable remoteMsg) {
		// plugin the custom class, where loadflow is actually 
		// implemented and run on the remote node
		return new CustomSingleRemoteJob(remoteMsg);
	}	
}
