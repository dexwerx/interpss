package org.interpss.sample.grid.sample;

import org.gridgain.grid.Grid;
import org.interpss.IpssCorePlugin;
import org.interpss.fadapter.IpssFileAdapter;
import org.interpss.grid.gridgain.GridRunner;
import org.interpss.grid.gridgain.job.GridAclfReJob;
import org.interpss.grid.gridgain.util.GridEnvHelper;
import org.interpss.grid.msg.RemoteMessageTable;
import org.interpss.grid.result.IRemoteResult;
import org.interpss.grid.result.RemoteResultFactory;
import org.interpss.sample.grid.impl.GridHelper;
import org.interpss.spring.EditorPluginSpringFactory;

import com.interpss.CoreObjectFactory;
import com.interpss.SimuObjectFactory;
import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.common.msg.impl.IPSSMsgHubImpl;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.core.net.Branch;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.simu.multicase.ReturnRemoteCaseOpt;
import com.interpss.simu.multicase.aclf.AclfGridOption;
import com.interpss.simu.multicase.aclf.AclfMultiStudyCase;
import com.interpss.simu.multicase.aclf.AclfStudyCase;
import com.interpss.simu.multicase.modify.BranchModification;
import com.interpss.simu.multicase.modify.Modification;

/**
 * This example assumes that at least one remote Gridgain 2.1.1 agent is running in the LAN. 
 *
 */
public class IEEE14_MultiStudyCase {
	public static String GridGainHome = "c:/Program Files (x86)/gridgain-2.1.1";

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// InterPSS core simulation engine configuration
		IpssCorePlugin.init();

    	/*
    	 * step-1 Build the base case
    	 */
		IpssFileAdapter adapter = EditorPluginSpringFactory.getCustomFileAdapter("ieee");
		SimuContext simuCtx = adapter.load("testData/ieee14.ieee");
		AclfNetwork adjNet = simuCtx.getAclfNet();
		adjNet.setId("IEEE14BusNetId");
    		
		/*
		 * step-2 Define LF algorithm
		 */
		IPSSMsgHub msg = new IPSSMsgHubImpl();
	  	LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(adjNet);
	  	algo.setNonDivergent(true);
	  	//algo.setLfMethod(AclfMethod.PQ);
	  	
	  	/*
	  	 * step-3 define multiple study cases
	  	 */
		AclfMultiStudyCase mCaseContainer = SimuObjectFactory.createAclfMultiStudyCase(SimuCtxType.ACLF_NETWORK);
		// save the base case Network model
		mCaseContainer.setBaseNetModelString(adjNet.serialize());

		// persist the Loadflow algorithm to a string
		String algoStr = algo.serialize();
		int caseNo = 0;
		for (Branch branch : adjNet.getBranchList()) {
			// make sure each studycase has an unique case id
			AclfStudyCase studyCase = SimuObjectFactory.createAclfStudyCase("caseId"+(++caseNo), 
					"Turn-off branch "+branch.getId(), 
					caseNo, mCaseContainer);
			// persist the LF algo for the study case object
			studyCase.setAclfAlgoModelString(algoStr);

			// define modification to the base case - turn-off the branch for N-1 analysis
			BranchModification braMod = SimuObjectFactory.createBranchModification(branch.getId(), adjNet);
			braMod.setOutService(true);
			
			Modification mod = SimuObjectFactory.createModification(braMod);
			studyCase.setModification(mod);
		}    		
		
		/*
		 * Step-4 define study options
		 */
		mCaseContainer.setRemoteJobCreation(true);
		AclfGridOption opt = SimuObjectFactory.createAclfGridOption();
		opt.setReturnCase(ReturnRemoteCaseOpt.ALL_STUDY_CASE);
		opt.setCalculateViolation(true);
		opt.setBusVoltageUpperLimitPU(1.1);
		opt.setBusVoltageLowerLimitPU(0.9);   
		mCaseContainer.setAclfGridOption(opt);
		
		/**
		 * Step-5 perform grid computing
		 */
		// start InterPSS grid env
		Grid grid = GridHelper.initGridEnv();
		if (grid != null) {
			long timeout = 0;
			RemoteMessageTable[] resultAry = new GridRunner(grid,	"InterPSS Grid Aclf Calculation", 
					mCaseContainer).executeMultiJobSplitTask(timeout);
			/**
			* Step-6 process results
			*/
			IRemoteResult resultHandler = RemoteResultFactory.createHandler(GridAclfReJob.class);
			for (RemoteMessageTable result : resultAry) {
				resultHandler.transferRemoteResult(mCaseContainer, result);
			}
			System.out.println(resultHandler.toString(IRemoteResult.DisplayType_NoUsed, mCaseContainer).toString());
		}
		
		System.out.println("Stop InterPSS Grid env ...");
		GridEnvHelper.stopDefaultGrid();
	}
}
