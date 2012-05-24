package org.interpss.test.path;

import java.util.HashMap;

import org.apache.commons.math.complex.Complex;
import org.intepss.path.CommunityDetection;
import org.intepss.path.IPSSActivePowerDigraph;
import org.interpss.CorePluginObjFactory;
import org.interpss.IpssPlugin;
import org.interpss.fadapter.IpssFileAdapter;

import com.interpss.CoreObjectFactory;
import com.interpss.common.exp.InterpssException;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfGenCode;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.algo.LoadflowAlgorithm;
import com.interpss.core.net.Bus;

public class CommunityTestSZCritical {

	/**
	 * @param args
	 * @throws Exception 
	 * @throws InterpssException 
	 */
	public static void main(String[] args) throws InterpssException, Exception {
		IpssPlugin.init();
		AclfNetwork net = CorePluginObjFactory.getFileAdapter(IpssFileAdapter.FileFormat.PSSE, IpssFileAdapter.Version.PSSE_30).load("d:/work/data/SZEQ0924_2_3Trans_eq.raw").getAclfNet();
		HashMap<String, Complex> originalGenPower = new HashMap<String, Complex>();
		HashMap<String, Complex> originalLoadPower = new HashMap<String, Complex>();
		for (Bus bus : net.getBusList()) {
			AclfBus thisBus = net.getAclfBus(bus.getId());
			originalGenPower.put(bus.getId(), thisBus.getGen());
			originalLoadPower.put(bus.getId(), thisBus.getLoad());
		}
		// Approaching critical load level
		double alpha = 1.0;
		double inc = 0.4;
		while (inc > 0.0001) {
			boolean isConverged = true;
			while (isConverged) {
				alpha += inc;
				for (Bus bus : net.getBusList()) {
					AclfBus thisBus = net.getAclfBus(bus.getId());
					if (thisBus.getGenCode() == AclfGenCode.GEN_PQ) {
						thisBus.setGenP(originalGenPower.get(bus.getId()).getReal() * alpha);
						thisBus.setGenQ(originalGenPower.get(bus.getId()).getImaginary() * alpha);
						thisBus.setLoadP(originalLoadPower.get(bus.getId()).getReal() * alpha);
						thisBus.setLoadQ(originalLoadPower.get(bus.getId()).getImaginary() * alpha);
					}
					else if (thisBus.getGenCode() == AclfGenCode.GEN_PV) {
						thisBus.setGenP(originalGenPower.get(bus.getId()).getReal() * alpha);
						thisBus.setGenQ(originalGenPower.get(bus.getId()).getImaginary() * alpha);
						thisBus.setLoadP(originalLoadPower.get(bus.getId()).getReal() * alpha);
						thisBus.setLoadQ(originalLoadPower.get(bus.getId()).getImaginary() * alpha);
					}
				}
			    LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm();
			    algo.setInitBusVoltage(true);
			    algo.setMaxIterations(100);
				net.accept(algo);
				isConverged = net.isLfConverged();
			}
			alpha -= inc;
			inc /= 2;
		}
		// Make sure that the loadflow can be converged near the critical point
		while (!net.isLfConverged()) {
			alpha -= inc;
			for (Bus bus : net.getBusList()) {
				AclfBus thisBus = net.getAclfBus(bus.getId());
				if (thisBus.getGenCode() == AclfGenCode.GEN_PQ) {
					thisBus.setGenP(originalGenPower.get(bus.getId()).getReal() * alpha);
					thisBus.setGenQ(originalGenPower.get(bus.getId()).getImaginary() * alpha);
					thisBus.setLoadP(originalLoadPower.get(bus.getId()).getReal() * alpha);
					thisBus.setLoadQ(originalLoadPower.get(bus.getId()).getImaginary() * alpha);
				}
				else if (thisBus.getGenCode() == AclfGenCode.GEN_PV) {
					thisBus.setGenP(originalGenPower.get(bus.getId()).getReal() * alpha);
					thisBus.setGenQ(originalGenPower.get(bus.getId()).getImaginary() * alpha);
					thisBus.setLoadP(originalLoadPower.get(bus.getId()).getReal() * alpha);
					thisBus.setLoadQ(originalLoadPower.get(bus.getId()).getImaginary() * alpha);
				}
			}
		    LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm();
		    algo.setInitBusVoltage(true);
		    algo.setMaxIterations(50);
			net.accept(algo);
		}
		IPSSActivePowerDigraph apd = new IPSSActivePowerDigraph(net);
		CommunityDetection cd = new CommunityDetection(apd.getpDigraph(), net);
//		cd.detectingCommunities();
		cd.detectingWeightedCommunities();
		cd.saveCommunityResults("community_sz_critical.txt", cd.getResult());
		cd.saveQResults("q_sz_critical.txt", cd.getqSet());
	}

}
