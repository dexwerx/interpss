package org.interpss.algo;

import static com.interpss.common.util.IpssLogger.ipssLogger;

import java.util.ArrayList;
import java.util.List;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBranchCode;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.net.Branch;
import com.interpss.core.net.Bus;

/**
 * Class for Network Obsolete branches identification
 * 
 *
 */
public class ObsoleteBranchProcessor {
	private AclfNetwork aclfNet = null;
	private List<Branch> obsBranchList = null;
	private double smallZThreshold = 1.0e-10;

	/**
	 * Constructor 
	 * @param net
	 */
	public ObsoleteBranchProcessor(AclfNetwork net) {
		this.aclfNet = net;
		obsBranchList = new ArrayList<Branch>();

		for (Branch branch : net.getBranchList())
			if (branch.isActive())
				branch.setVisited(false);
		for (Bus bus : net.getBusList())
			bus.setVisited(false);
	}

	public void setSmallZThreshold(double threshold) {
		this.smallZThreshold = threshold;
	}

	public void findObsoleteBranch() throws InterpssException {

		aclfNet.markSmallZBranch(this.smallZThreshold, true, false);

		for (Bus b : this.aclfNet.getBusList()) {
			if (b.isStatus() && !b.isVisited()) {
				List<Bus> busList = ((AclfBus) b).findZeroZPathBuses(true);
				for (Bus bus : busList) {
					for (Branch br : bus.getBranchList()) {
						Bus optBus = br.getOppositeBus(bus);
						AclfBranchCode code = ((AclfBranch) br).getBranchCode();
						if (busList.contains(optBus)
								&& code.equals(AclfBranchCode.LINE)) {
							if (!obsBranchList.contains(br)) {
								String msg = "Obsolete branch found when processing bus "
										+ b.getId() + ": " + br.getId();
								ipssLogger.warning(msg);
								obsBranchList.add(br);
							}
						}
					}
				}

			}
		}

	}

	public List<Branch> getObsoleteBranches() {
		return this.obsBranchList;
	}
}

