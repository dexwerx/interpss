
 
package org.interpss.mapper.bean.dclf;

import java.util.List;

import org.interpss.datamodel.bean.BaseBranchBean;
import org.interpss.datamodel.bean.aclf.AclfBusBean;
import org.interpss.datamodel.bean.dclf.DclfBranchResultBean;
import org.interpss.datamodel.bean.dclf.DclfBusResultBean;
import org.interpss.datamodel.bean.dclf.DclfNetResultBean;
import org.interpss.datamodel.bean.dclf.GSFResultBean;
import org.interpss.numeric.datatype.Unit.UnitType;
import org.interpss.numeric.util.Number2String;

import com.interpss.common.exp.InterpssException;
import com.interpss.common.mapper.AbstractMapper;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.dclf.DclfAlgorithm;


public class DclfResultBeanMapper extends AbstractMapper<DclfAlgorithm, DclfNetResultBean> {
	

	/**
	 * constructor
	 */
	public DclfResultBeanMapper() {		

	}

	/**
	 * map into store in the AclfNetBean object into simuCtx object
	 * 
	 * @param netBean
	 *            AclfNetBean object
	 * @return SimuContext object
	 */
	@Override
	public DclfNetResultBean map2Model(DclfAlgorithm algo)
			throws InterpssException {		
		DclfNetResultBean dclfResult = new DclfNetResultBean();

		map2Model(algo, dclfResult);

		return dclfResult;
	}

	/**
	 * map the DclfAlgorithm object into simuCtx object
	 * 
	 * @param algo
	 *            an DclfAlgorithm object, representing a dclf algorithm
	 * @param simuCtx
	 */
	@Override
	public boolean map2Model(DclfAlgorithm algo, DclfNetResultBean dclfResult) {

		AclfNetwork aclfNet = algo.getAclfNetwork();

		boolean noError = true;

		dclfResult.base_kva = aclfNet.getBaseKva();		

		for (AclfBus bus : algo.getAclfNetwork().getBusList()) {
		    DclfBusResultBean bean = new DclfBusResultBean();
			dclfResult.bus_list.add(bean);
			bean.id = bus.getId();
		    bean.base_v = bus.getBaseVoltage();
			int n = bus.getSortNumber();
			double angle = algo.getAclfNetwork().isRefBus(bus) ? 0.0 : Math
					.toDegrees(algo.getBusAngle(n));
			bean.v_ang =  format2(angle);
			bean.gen_code = bus.isGenPQ() || !bus.isGen() ? AclfBusBean.GenCode.PQ :
				(bus.isGenPV() ? AclfBusBean.GenCode.PV : AclfBusBean.GenCode.Swing);
						
			double pgen = (bus.isRefBus() ? algo.getBusPower(bus) : bus
					.getGenP());
			bean.pGen = format2(pgen);
					
			double pload = bus.getLoadP();
			bean.pLoad = format2(pload);
			double pshunt = bus.getShuntY().getReal();
			bean.shuntG = format2(pshunt);
			/*System.out.println(bean.id+", "+bean.base_v+", "+bean.v_ang+", "+bean.gen_code
			+", "+bean.pGen+", "+bean.pLoad+", "+bean.shuntG);*/
		}
		

		for (AclfBranch aclfBra : aclfNet.getBranchList()) {
			DclfBranchResultBean bean = new DclfBranchResultBean();
			dclfResult.branch_list.add(bean);
			double threshold = 1.0;
			bean.f_id = aclfBra.getFromBus().getId();
			bean.t_id = aclfBra.getToBus().getId();
			bean.cir_id = aclfBra.getCircuitNumber();
			bean.bra_code = aclfBra.isLine() ? BaseBranchBean.BranchCode.Line
					: (aclfBra.isXfr() ? BaseBranchBean.BranchCode.Xfr
							: BaseBranchBean.BranchCode.PsXfr);

			double mwFlow = algo.getBranchFlow(aclfBra, UnitType.PU);
						
			bean.lineFlow = format2(mwFlow);

			double limitMva = aclfBra.getRatingMva1()/aclfNet.getBaseMva();
			bean.limit = format2(limitMva);
			double loading = 0 ;
			if (limitMva > 0.0)
			    loading = Math.abs(100 * (mwFlow) / limitMva);
			bean.loading = format2(loading);
			boolean violation = false;
			if (limitMva > 0)
			   violation = Math.abs(mwFlow) > limitMva * threshold;

			bean.violation = 0;
			if (violation) {
				bean.violation = 1;
			}
			
			/*System.out.println(bean.f_id+", "+bean.t_id+", "+bean.cir_id+", "+bean.limit
			+", "+bean.lineFlow+", "+bean.loading+", "+bean.violation);*/
		}

		return noError;
	}	
	
	

    private double format(double x) {
		return new Double(Number2String.toStr(x)).doubleValue();
	}

	private double format2(double x) {
		return new Double(Number2String.toStr(x, "#0.0#")).doubleValue();
	}
	
}