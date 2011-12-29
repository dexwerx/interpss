package org.interpss.dstab.output;

/**
 * Default dstab simulation action handler for processing dstab output events. 
 */

import java.util.Hashtable;

import org.interpss.db.DBManager;
import org.interpss.db.IpssDBCase;
import org.interpss.grid.GridConstants;
import org.interpss.output.ISimuRecManager;
import org.interpss.spring.PluginSpringFactory;
import org.interpss.ui.IProjectDataManager;

import com.interpss.common.datatype.Constants;
import com.interpss.common.exp.InterpssException;
import com.interpss.common.msg.IpssMessage;
import com.interpss.common.util.IpssLogger;
import com.interpss.common.util.StringUtil;
import com.interpss.dstab.common.DStabOutSymbol;
import com.interpss.dstab.common.IDStabSimuDatabaseOutputHandler;
import com.interpss.dstab.datatype.DStabSimuAction;
import com.interpss.dstab.mach.MachineControllerType;
import com.interpss.dstab.util.AbstractSimuOutputHandler;

public class DatabaseSimuOutputHandler extends AbstractSimuOutputHandler
		implements IDStabSimuDatabaseOutputHandler {
	protected IpssDBCase simuCase = null;

	private int excRecTypeId = 0;
	private int govRecTypeId = 0;
	private int pssRecTypeId = 0;
	private int machRecTypeId = 0;
	private int busRecTypeId = 0;
	private int scriptBusDeviceRecTypeId = 0;

	public DatabaseSimuOutputHandler() {
	}

	public boolean init(int projDbId, String caseName) throws InterpssException {
		IProjectDataManager projDataMgr = PluginSpringFactory.getProjectDataDBManager();
		ISimuRecManager simuRecMgr = PluginSpringFactory.getSimuRecManager();

		this.excRecTypeId = simuRecMgr.getRecTypeId(
				ISimuRecManager.REC_TYPE_DStabExcStates,
				IProjectDataManager.CaseType_DStabSimuRec);
		this.govRecTypeId = simuRecMgr.getRecTypeId(
				ISimuRecManager.REC_TYPE_DStabGovStates,
				IProjectDataManager.CaseType_DStabSimuRec);
		this.pssRecTypeId = simuRecMgr.getRecTypeId(
				ISimuRecManager.REC_TYPE_DStabPssStates,
				IProjectDataManager.CaseType_DStabSimuRec);
		this.machRecTypeId = simuRecMgr.getRecTypeId(
				ISimuRecManager.REC_TYPE_DStabMachineStates,
				IProjectDataManager.CaseType_DStabSimuRec);
		this.busRecTypeId = simuRecMgr.getRecTypeId(
				ISimuRecManager.REC_TYPE_DStabBusStates,
				IProjectDataManager.CaseType_DStabSimuRec);
		this.scriptBusDeviceRecTypeId = simuRecMgr.getRecTypeId(
				ISimuRecManager.REC_TYPE_DStabScriptBusDeviceStates,
				IProjectDataManager.CaseType_DStabSimuRec);

		this.simuCase = (IpssDBCase) projDataMgr.dbActionIpssCase(
				DBManager.SQL_ACTION_SELECT, projDbId, caseName);
		if (this.simuCase.getCaseDbId() > 0) {
			IpssLogger.getLogger().info(
					"Clear results in SimuCase tables, projDbId, caseName: "
							+ projDbId + ", " + caseName);
			simuRecMgr.deleteAllSimuRec(this.simuCase.getCaseDbId(),
					IProjectDataManager.CaseType_DStabSimuRec);
			// update the SimuCase.timeStamp to the current time
			projDataMgr.dbActionIpssCase(DBManager.SQL_ACTION_UPDATE, projDbId,
					caseName);
		} else {
			IpssLogger.getLogger().info(
					"Create DStab SimuCase, projDbId, caseName: " + projDbId
							+ ", " + caseName);
			try {
				this.simuCase = (IpssDBCase) projDataMgr.dbActionIpssCase(
						DBManager.SQL_ACTION_INSERT, projDbId, caseName);
			} catch (Exception e) {
				IpssLogger.getLogger().warning(
						"Error to create DStab SimuCase, " + e.toString());
				this.simuCase = (IpssDBCase) projDataMgr.dbActionIpssCase(
						DBManager.SQL_ACTION_SELECT, projDbId, caseName);
				projDataMgr.dbActionIpssCase(DBManager.SQL_ACTION_UPDATE,
						projDbId, caseName);
			}
		}
		return true;
	}

	public int getDBCaseId() {
		return simuCase.getCaseDbId();
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean onMsgEventStatus(IpssMessage event) {
		ISimuRecManager simuRecMgr = PluginSpringFactory.getSimuRecManager();
		DStabSimuAction e = (DStabSimuAction) event;
		try {
			int dbCaseId = getDBCaseId();
			if (e.getHashtableData() != null
					&& e.getHashtableData().get(GridConstants.SeKey_CaseId) != null) {
				// Grid computing case, where case id return from the msg
				String caseId = (String) e.getHashtableData().get(GridConstants.SeKey_CaseId);
				dbCaseId = PluginSpringFactory.getSimuRecManager().getDBCaseId(
						caseId);
			}

			if (e.getType() == DStabSimuAction.PlotStepMachineStates) {
				Hashtable<String, Object> machStates = e.getHashtableData();

				double time = StringUtil.getDouble(machStates
						.get(DStabOutSymbol.OUT_SYMBOL_TIME));
				String machId = (String) machStates
						.get(DStabOutSymbol.OUT_SYMBOL_MACH_ID);

				if (!this.isOutputFilter()
						|| this.getOutputVarIdList().contains(
								Constants.Token_FilterMachVar + machId)) {

					Hashtable<String, Object> excStates = (Hashtable<String, Object>) machStates
							.get(Constants.Token_ExciterState);
					if (excStates != null) {
						machStates.remove(MachineControllerType.EXCITER);
						DStabSimuDBRecord excRec = createSimuDBRec(dbCaseId,
								this.excRecTypeId, time);
						excRec.setElemIdStr(machId
								+ DStabSimuDBRecord.EXCITER_ID_EXT);
						excRec.setSimuRec(excStates.toString());
						simuRecMgr.dbActionSimuRec(DBManager.SQL_ACTION_INSERT,
								excRec,
								IProjectDataManager.CaseType_DStabSimuRec);
					}

					Hashtable<String, Object> govStates = (Hashtable) machStates
							.get(Constants.Token_GovernorState);
					if (govStates != null) {
						machStates.remove(MachineControllerType.GOVERNOR);
						DStabSimuDBRecord govRec = createSimuDBRec(dbCaseId,
								this.govRecTypeId, time);
						govRec.setElemIdStr(machId
								+ DStabSimuDBRecord.GOVERNER_ID_EXT);
						govRec.setSimuRec(govStates.toString());
						simuRecMgr.dbActionSimuRec(DBManager.SQL_ACTION_INSERT,
								govRec,
								IProjectDataManager.CaseType_DStabSimuRec);
					}

					Hashtable<String, Object> pssStates = (Hashtable) machStates
							.get(Constants.Token_StabilizerState);
					if (pssStates != null) {
						machStates.remove(MachineControllerType.STABILIZER);
						DStabSimuDBRecord pssRec = createSimuDBRec(dbCaseId,
								this.pssRecTypeId, time);
						pssRec.setElemIdStr(machId
								+ DStabSimuDBRecord.STABILIZER_ID_EXT);
						pssRec.setSimuRec(pssStates.toString());
						simuRecMgr.dbActionSimuRec(DBManager.SQL_ACTION_INSERT,
								pssRec,
								IProjectDataManager.CaseType_DStabSimuRec);
					}

					DStabSimuDBRecord machRec = createSimuDBRec(dbCaseId,
							this.machRecTypeId, time);
					machRec.setElemIdStr(machId);
					machRec.setSimuRec(machStates.toString());
					simuRecMgr.dbActionSimuRec(DBManager.SQL_ACTION_INSERT,
							machRec, IProjectDataManager.CaseType_DStabSimuRec);
					/*
					 * try {
					 * IpssLogger.getLogger().fine(DStabOutFunc.getStateStr(e.getHashtableData())); }
					 * catch (Exception ex) {
					 * IpssLogger.getLogger().severe("Mach state hashtable: " +
					 * e.getHashtableData()); IpssLogger.logErr(ex); }
					 */
				}
			} else if (e.getType() == DStabSimuAction.PlotStepBusStates) {
				Hashtable<String, Object> busStates = e.getHashtableData();
				double time = StringUtil.getDouble(busStates
						.get(DStabOutSymbol.OUT_SYMBOL_TIME));
				String busId = (String) busStates
						.get(DStabOutSymbol.OUT_SYMBOL_BUS_ID);
				if (!this.isOutputFilter()
						|| this.getOutputVarIdList().contains(
								Constants.Token_FilterBusVar + busId)) {
					DStabSimuDBRecord busRec = createSimuDBRec(dbCaseId,
							this.busRecTypeId, time);
					busRec.setElemIdStr(busId);
					/*
					 * if (busId.equals("0001")) System.out.println("To DB,
					 * BusId, Time: " + busId + ", " + time + " " +
					 * busStates.toString());
					 */
					busRec.setSimuRec(busStates.toString());
					simuRecMgr.dbActionSimuRec(DBManager.SQL_ACTION_INSERT,
							busRec, IProjectDataManager.CaseType_DStabSimuRec);
				}
			} else if (e.getType() == DStabSimuAction.PlotStepScriptDynamicBusDeviceStates) {
				Hashtable<String, Object> deviceStates = e.getHashtableData();
				double time = StringUtil.getDouble(deviceStates
						.get(DStabOutSymbol.OUT_SYMBOL_TIME));
				String deviceId = (String) deviceStates
						.get(DStabOutSymbol.OUT_SYMBOL_BUS_DEVICE_ID);
				if (!this.isOutputFilter()
						|| this.getOutputVarIdList().contains(deviceId)) {
					DStabSimuDBRecord scriptDBusDeviceRec = createSimuDBRec(
							dbCaseId, this.scriptBusDeviceRecTypeId, time);
					scriptDBusDeviceRec.setElemIdStr(deviceId);
					scriptDBusDeviceRec.setSimuRec(deviceStates.toString());
					// IpssLogger.getLogger().info(deviceStates.toString());
					simuRecMgr.dbActionSimuRec(DBManager.SQL_ACTION_INSERT,
							scriptDBusDeviceRec,
							IProjectDataManager.CaseType_DStabSimuRec);
				}
			}
		} catch (Exception ex) {
			IpssLogger.logErr(ex);
			PluginSpringFactory.getEditorDialogUtil().showErrMsgDialog(
					"InterPSS DB Access Error",
					ex.toString() + "\n Please contact InterPSS support");
			return false;
		}
		return true;
	}

	private DStabSimuDBRecord createSimuDBRec(int caseId, int elemId,
			double time) {
		DStabSimuDBRecord rec = new DStabSimuDBRecord();
		rec.setCaseId(caseId);
		rec.setRecTypeId(elemId);
		rec.setSimuTime(time);
		return rec;
	}
}