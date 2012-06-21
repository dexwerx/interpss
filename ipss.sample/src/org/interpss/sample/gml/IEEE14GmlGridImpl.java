package org.interpss.sample.gml;

import java.util.List;

import org.graphdrawing.gml.GraphType;
import org.graphdrawing.gml.GraphmlType;
import org.gridgain.grid.Grid;
import org.interpss.IpssCorePlugin;
import org.interpss.fadapter.IpssFileAdapter;
import org.interpss.grid.gridgain.GridRunner;
import org.interpss.grid.gridgain.util.GridEnvHelper;
import org.interpss.grid.msg.RemoteMessageTable;
import org.interpss.sample.grid.impl.GridHelper;
import org.interpss.sample.grid.impl.multi.CustomMultiLocalTask;
import org.interpss.spring.EditorPluginSpringFactory;

import com.interpss.SimuObjectFactory;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.simu.SimuContext;
import com.interpss.simu.SimuCtxType;
import com.interpss.simu.multicase.aclf.AclfMultiStudyCase;
import com.interpss.simu.multicase.aclf.AclfStudyCase;
import com.interpss.simu.multicase.modify.Modification;
import com.interpss.spring.CoreCommonSpringFactory;

public class IEEE14GmlGridImpl {
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// InterPSS core simulation engine configuration
		CoreCommonSpringFactory.setAppContext(new String[] {IpssCorePlugin.CtxPath});

    	// Build the base case network
		IpssFileAdapter adapter = EditorPluginSpringFactory.getCustomFileAdapter("ieee");
		SimuContext simuCtx = adapter.load("testData/ieee14.ieee");
		AclfNetwork adjNet = simuCtx.getAclfNet();
		
		AclfMultiStudyCase mCaseContainer = SimuObjectFactory.createAclfMultiStudyCase(SimuCtxType.ACLF_NETWORK);
		// save the base case Network model
		mCaseContainer.setBaseNetModelString(adjNet.serialize());
		mCaseContainer.setRemoteJobCreation(true);
		
		// load the Gml file
		GraphmlType gml = GmlHelper.load("testData/gml/sample_001.gml");
		
		// retrieve all Graph objects, there may be more than one graphs
		List<GraphType> glist = GmlHelper.getGraphObjects(gml);
		
		int caseNo = 0;		
		for (GraphType graph : glist) {
			// for each Gml graph, create a grid computing study case
			String caseId = graph.getId() != null? graph.getId() : ("CaseId_");
			AclfStudyCase studyCase = SimuObjectFactory.createAclfStudyCase(caseId+(++caseNo), 
					graph.getDesc(), caseNo, mCaseContainer);
			// use the modification object to send the graph object to remote node
			Modification mod = SimuObjectFactory.createModification();
			mod.setAny(GmlHelper.toXmlString(graph));
			studyCase.setModification(mod);			
		}
		
		// perform multi-case grid computing. The case will distributed to slave grid nodes
		// in random fashion. The actual work is done by the MyAclfMultiJobTask class, which
		// has to be implemented by the implementor
		Grid grid = GridHelper.initGridEnv();
		if (grid != null) {
			long timeout = 0;
			RemoteMessageTable[] resultAry = new GridRunner(grid)
					.executeMultiJobSplitTask(CustomMultiLocalTask.class, mCaseContainer, timeout);
		}
		
		GridEnvHelper.stopDefaultGrid();		
	}
}
