package org.interpss.algo;

import java.util.ArrayList;
import java.util.List;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBranchCode;
import com.interpss.core.net.Branch;
import com.interpss.core.net.Bus;

public class ZeroZHelper {
	/**
	 * Find all connected zero-z buses. No zero-z branch loop is assumed. Set branch.visited
	 * to false before calling this method. The branch.visited status will be used to detect 
	 * zero-z loop.
	 * 
	 * @param staringPoint
	 * @return
	 */
	public static List<Bus> findZeroZPathBuses(Bus startingPoint)  throws InterpssException  {
		List<Bus> busList = new ArrayList<Bus>();
		busList.add(startingPoint);
		for (Branch b : startingPoint.getBranchList()) {
			if (((AclfBranch)b).getBranchCode() == AclfBranchCode.ZERO_IMPEDENCE) {
				List<Bus> list = findZeroZPathBuses(startingPoint, b);
				busList.addAll(list);
			}
		}		
		return busList;
	}

	/**
	 * recursively find all zero-z branch buses connected
	 * 
	 *      startingBus --- ZeroZ branch -->--- opposite bus
	 *      
	 *  No zero-z branch loop is assumed.    
	 * 
	 * @param staringPoint
	 * @param zeroZBranch
	 * @return
	 */
	private static List<Bus> findZeroZPathBuses(Bus staringPoint, Branch zeroZBranch) throws InterpssException {
		if (zeroZBranch.isVisited()) {
			throw new InterpssException("Zero Z branch loop detected, branch " + zeroZBranch.getId());
		}
		zeroZBranch.setVisited(true);
		List<Bus> busList = new ArrayList<Bus>();
		Bus optBus = zeroZBranch.getOppositeBus(staringPoint);
		busList.add(optBus);
		for (Branch b : optBus.getBranchList()) {
			if (!b.getId().equals(zeroZBranch.getId()) && 
					((AclfBranch)b).getBranchCode() == AclfBranchCode.ZERO_IMPEDENCE) {
				List<Bus> list = findZeroZPathBuses(optBus, b);
				busList.addAll(list);
			}
		}
		return busList;
	}
}
