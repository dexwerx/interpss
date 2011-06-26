package org.interpss.facts.statcom;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.math.complex.Complex;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.interpss.numeric.sparse.SparseEqnComplex;
import org.interpss.numeric.sparse.SparseEqnDouble;
import org.interpss.numeric.sparse.SparseEqnInteger;
import org.interpss.numeric.sparse.SparseEqnMatrix2x2;

import com.interpss.common.exp.InterpssException;
import com.interpss.core.aclf.Aclf3WXformer;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.aclf.JacobianMatrixType;
import com.interpss.core.aclf.hvdc.HvdcLine2T;
import com.interpss.core.aclf.netAdj.AclfNetAdjustment;
import com.interpss.core.algo.AclfMethod;
import com.interpss.core.common.visitor.IAclfBranchVisitor;
import com.interpss.core.common.visitor.IAclfBusVisitor;
import com.interpss.core.common.visitor.IAclfNetBVisitor;
import com.interpss.core.common.visitor.INetBVisitor;
import com.interpss.core.common.visitor.IPQBusLimitVisitor;
import com.interpss.core.common.visitor.IPSXfrPControlVisitor;
import com.interpss.core.common.visitor.IPVBusLimitVisitor;
import com.interpss.core.common.visitor.IRemoteQBusVisitor;
import com.interpss.core.common.visitor.ITapControlVisitor;
import com.interpss.core.datatype.Mismatch;
import com.interpss.core.net.Area;
import com.interpss.core.net.Branch;
import com.interpss.core.net.Branch3W;
import com.interpss.core.net.Bus;
import com.interpss.core.net.NameTag;
import com.interpss.core.net.Network;
import com.interpss.core.net.OriginalDataFormat;
import com.interpss.core.net.Owner;
import com.interpss.core.net.TieLine;
import com.interpss.core.net.Zone;

public class AclfNetworkWithStatcom implements AclfNetwork {

	@Override
	public boolean accept(INetBVisitor arg0) {
		return false;
	}

	@Override
	public boolean checkData() {
		return false;
	}

	@Override
	public SparseEqnInteger formAjacencyMatrix() {
		return null;
	}

	@Override
	public EList<Area> getAreaList() {
		return null;
	}

	@Override
	public double getBaseKva() {
		return 0;
	}

	@Override
	public EList<Branch> getBranchList() {
		return null;
	}

	@Override
	public EList<Bus> getBusList() {
		return null;
	}

	@Override
	public double getFrequency() {
		return 0;
	}

	@Override
	public OriginalDataFormat getOriginalDataFormat() {
		return null;
	}

	@Override
	public EList<Owner> getOwnerList() {
		return null;
	}

	@Override
	public EList<TieLine> getTieLineList() {
		return null;
	}

	@Override
	public EList<Zone> getZoneList() {
		return null;
	}

	@Override
	public void initBusNumber() {
	}

	@Override
	public boolean isAllowGroundBranch() {
		return false;
	}

	@Override
	public boolean isAllowParallelBranch() {
		return false;
	}

	@Override
	public boolean isBusNumberArranged() {
		return false;
	}

	@Override
	public boolean isBypassDataCheck() {
		return false;
	}

	@Override
	public boolean isCheckElementDuplication() {
		return false;
	}

	@Override
	public boolean isContainChildNet() {
		return false;
	}

	@Override
	public boolean isDataChecked() {
		return false;
	}

	@Override
	public boolean isSensitivityCalculated() {
		return false;
	}

	@Override
	public boolean isSwitchBreakModel() {
		return false;
	}

	@Override
	public String net2String() {
		return null;
	}

	@Override
	public void rebuildLookupTable() {
	}

	@Override
	public void setAllowGroundBranch(boolean arg0) {
	}

	@Override
	public void setAllowParallelBranch(boolean arg0) {
	}

	@Override
	public void setBaseKva(double arg0) {
	}

	@Override
	public void setBusNumberArranged(boolean arg0) {
	}

	@Override
	public void setBypassDataCheck(boolean arg0) {
	}

	@Override
	public void setCheckElementDuplication(boolean arg0) {
	}

	@Override
	public void setContainChildNet(boolean arg0) {
	}

	@Override
	public void setDataChecked(boolean arg0) {
	}

	@Override
	public void setFrequency(double arg0) {
	}

	@Override
	public void setOriginalDataFormat(OriginalDataFormat arg0) {
	}

	@Override
	public void setSensitivityCalculated(boolean arg0) {
	}

	@Override
	public void setSwitchBreakModel(boolean arg0) {
	}

	@Override
	public void setToEmpty() {
	}

	@Override
	public void copy(NameTag arg0, NameTag arg1) throws InterpssException {
	}

	@Override
	public void copyRef(NameTag arg0, NameTag arg1) throws InterpssException {
	}

	@Override
	public String getDesc() {
		return null;
	}

	@Override
	public String getId() {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public long getNumber() {
		return 0;
	}

	@Override
	public boolean isActive() {
		return false;
	}

	@Override
	public boolean isStatus() {
		return false;
	}

	@Override
	public void setDesc(String arg0) {
	}

	@Override
	public void setId(String arg0) {
	}

	@Override
	public void setName(String arg0) {
	}

	@Override
	public void setNumber(long arg0) {
	}

	@Override
	public void setStatus(boolean arg0) {
	}

	@Override
	public TreeIterator<EObject> eAllContents() {
		return null;
	}

	@Override
	public EClass eClass() {
		return null;
	}

	@Override
	public EObject eContainer() {
		return null;
	}

	@Override
	public EStructuralFeature eContainingFeature() {
		return null;
	}

	@Override
	public EReference eContainmentFeature() {
		return null;
	}

	@Override
	public EList<EObject> eContents() {
		return null;
	}

	@Override
	public EList<EObject> eCrossReferences() {
		return null;
	}

	@Override
	public Object eGet(EStructuralFeature arg0) {
		return null;
	}

	@Override
	public Object eGet(EStructuralFeature arg0, boolean arg1) {
		return null;
	}

	@Override
	public Object eInvoke(EOperation arg0, EList<?> arg1)
			throws InvocationTargetException {
		return null;
	}

	@Override
	public boolean eIsProxy() {
		return false;
	}

	@Override
	public boolean eIsSet(EStructuralFeature arg0) {
		return false;
	}

	@Override
	public Resource eResource() {
		return null;
	}

	@Override
	public void eSet(EStructuralFeature arg0, Object arg1) {
	}

	@Override
	public void eUnset(EStructuralFeature arg0) {
	}

	@Override
	public EList<Adapter> eAdapters() {
		return null;
	}

	@Override
	public boolean eDeliver() {
		return false;
	}

	@Override
	public void eNotify(Notification arg0) {
	}

	@Override
	public void eSetDeliver(boolean arg0) {
	}

	@Override
	public Object getAdapter(Class<?> arg0) {
		return null;
	}

	@Override
	public Object getAdapter(String arg0) {
		return null;
	}

	@Override
	public TieLine addTieLine(String arg0, int arg1, String arg2, int arg3,
			String arg4) {
		return null;
	}

	@Override
	public Area getArea(long arg0) {
		return null;
	}

	@Override
	public Owner getOwner(String arg0) {
		return null;
	}

	@Override
	public Owner getOwner(int arg0) {
		return null;
	}

	@Override
	public Zone getZone(long arg0) {
		return null;
	}

	@Override
	public boolean removeTieLine(String arg0) {
		return false;
	}

	@Override
	public Bus addBus(Bus arg0) {
		return null;
	}

	@Override
	public Bus addBus(Bus arg0, int arg1, int arg2, String arg3) {
		return null;
	}

	@Override
	public Bus getBus(String arg0) {
		return null;
	}

	@Override
	public Bus getBus(int arg0) {
		return null;
	}

	@Override
	public Bus getBus(String arg0, int arg1) {
		return null;
	}

	@Override
	public int getNoActiveBus() {
		return 0;
	}

	@Override
	public int getNoBus() {
		return 0;
	}

	@Override
	public boolean hasBus(String arg0) {
		return false;
	}

	@Override
	public boolean removeBus(String arg0) {
		return false;
	}

	@Override
	public boolean removeBus(Bus arg0) {
		return false;
	}

	@Override
	public Branch addBranch(Branch arg0, String arg1, String arg2) {
		return null;
	}

	@Override
	public Branch addBranch(Branch arg0, String arg1, String arg2, String arg3) {
		return null;
	}

	@Override
	public Branch addBranch(Branch arg0, String arg1, int arg2, String arg3,
			int arg4) {
		return null;
	}

	@Override
	public Branch addBranch(Branch arg0, String arg1, int arg2, String arg3,
			int arg4, String arg5) {
		return null;
	}

	@Override
	public Branch getBranch(String arg0) {
		return null;
	}

	@Override
	public Branch getBranch(String arg0, String arg1) {
		return null;
	}

	@Override
	public Branch getBranch(String arg0, String arg1, String arg2) {
		return null;
	}

	@Override
	public int getNoActiveBranch() {
		return 0;
	}

	@Override
	public int getNoBranch() {
		return 0;
	}

	@Override
	public Branch getSwitchBreakBranch(String arg0, String arg1) {
		return null;
	}

	@Override
	public boolean hasBranch(String arg0, String arg1) {
		return false;
	}

	@Override
	public boolean hasBranch(String arg0, String arg1, String arg2) {
		return false;
	}

	@Override
	public boolean removeBranch(String arg0) {
		return false;
	}

	@Override
	public boolean removeBranch(String arg0, String arg1, String arg2) {
		return false;
	}

	@Override
	public boolean removeBranch(Bus arg0, Bus arg1, String arg2) {
		return false;
	}

	@Override
	public Branch3W add3WBranch(Branch3W arg0, String arg1, String arg2,
			String arg3) {
		return null;
	}

	@Override
	public Branch3W add3WBranch(Branch3W arg0, String arg1, String arg2,
			String arg3, String arg4) {
		return null;
	}

	@Override
	public Branch3W get3WBranch(String arg0, String arg1, String arg2) {
		return null;
	}

	@Override
	public Branch3W get3WBranch(String arg0, String arg1, String arg2,
			String arg3) {
		return null;
	}

	@Override
	public boolean has3WBranch(String arg0, String arg1, String arg2) {
		return false;
	}

	@Override
	public boolean has3WBranch(String arg0, String arg1, String arg2,
			String arg3) {
		return false;
	}

	@Override
	public boolean remove3WBranch(String arg0, String arg1, String arg2,
			String arg3) {
		return false;
	}

	@Override
	public EObject deserialize(String arg0) throws InterpssException {
		return null;
	}

	@Override
	public EObject deserialize(String arg0, Network arg1)
			throws InterpssException {
		return null;
	}

	@Override
	public String serialize() {
		return null;
	}

	@Override
	public SparseEqnDouble formB11Matrix() {
		return null;
	}

	@Override
	public SparseEqnDouble formB1Matrix() {
		return null;
	}

	@Override
	public SparseEqnMatrix2x2 formJMatrix() {
		return null;
	}

	@Override
	public SparseEqnMatrix2x2 formJMatrix(int arg0) {
		return null;
	}

	@Override
	public SparseEqnMatrix2x2 formJMatrix(JacobianMatrixType arg0) {
		return null;
	}

	@Override
	public SparseEqnComplex formYMatrix() {
		return null;
	}

	@Override
	public SparseEqnDouble getB11Matrix() {
		return null;
	}

	@Override
	public SparseEqnDouble getB1Matrix() {
		return null;
	}

	@Override
	public void forEachAclfBranch(IAclfBranchVisitor arg0) {
	}

	@Override
	public void forEachAclfBus(IAclfBusVisitor arg0) {
	}

	@Override
	public void forEachPQBusLimit(IPQBusLimitVisitor arg0) {
	}

	@Override
	public void forEachPSXfrPControl(IPSXfrPControlVisitor arg0) {
	}

	@Override
	public void forEachPVBusLimit(IPVBusLimitVisitor arg0) {
	}

	@Override
	public void forEachRemoteQBus(IRemoteQBusVisitor arg0) {
	}

	@Override
	public void forEachTapControl(ITapControlVisitor arg0) {
	}

	@Override
	public boolean hasFunctionLoad() {
		return false;
	}

	@Override
	public boolean hasPQBusLimit() {
		return false;
	}

	@Override
	public boolean hasPSXfrPControl() {
		return false;
	}

	@Override
	public boolean hasPVBusLimit() {
		return false;
	}

	@Override
	public boolean hasRemoteQBus() {
		return false;
	}

	@Override
	public boolean hasTapControl() {
		return false;
	}

	@Override
	public boolean needDiscreteAdjust() {
		return false;
	}

	@Override
	public boolean needLimitAdjust(double arg0) {
		return false;
	}

	@Override
	public boolean needLimitBackoffAdjust() {
		return false;
	}

	@Override
	public boolean needPowerAdjust(double arg0) {
		return false;
	}

	@Override
	public boolean needVoltageAdjust(double arg0) {
		return false;
	}

	@Override
	public Aclf3WXformer add3WXfrBranch(Aclf3WXformer arg0, String arg1,
			String arg2, String arg3) {
		return null;
	}

	@Override
	public HvdcLine2T addHvdcLine2T(HvdcLine2T arg0, String arg1, String arg2,
			int arg3) {
		return null;
	}

	@Override
	public void calculateHvdc() {
	}

	@Override
	public AclfBranch getAclfBranch(String arg0) {
		return null;
	}

	@Override
	public AclfBranch getAclfBranch(String arg0, String arg1, String arg2) {
		return null;
	}

	@Override
	public AclfBus getAclfBus(String arg0) {
		return null;
	}

	@Override
	public HvdcLine2T getHvdcLine2T(String arg0) {
		return null;
	}

	@Override
	public boolean hasPSXfr() {
		return false;
	}

	@Override
	public boolean accept(IAclfNetBVisitor arg0) {
		return false;
	}

	@Override
	public double areaOutputPower(int arg0, byte arg1) {
		return 0;
	}

	@Override
	public AclfNetwork createSubNet(String[] arg0, String[] arg1)
			throws InterpssException {
		return null;
	}

	@Override
	public AclfNetAdjustment getAclfNetAdjust() {
		return null;
	}

	@Override
	public boolean hasBranchMavRatingViolation() {
		return false;
	}

	@Override
	public void initializeBusVoltage() {
	}

	@Override
	public boolean isLfConverged() {
		return false;
	}

	@Override
	public boolean isLfDataLoaded() {
		return false;
	}

	@Override
	public Mismatch maxMismatch(AclfMethod arg0) {
		return null;
	}

	@Override
	public void setAclfNetAdjust(AclfNetAdjustment arg0) {
	}

	@Override
	public void setLfConverged(boolean arg0) {
	}

	@Override
	public void setLfDataLoaded(boolean arg0) {
	}

	@Override
	public double smallestLoad(byte arg0) {
		return 0;
	}

	@Override
	public Complex totalLoss(byte arg0) {
		return null;
	}

}
