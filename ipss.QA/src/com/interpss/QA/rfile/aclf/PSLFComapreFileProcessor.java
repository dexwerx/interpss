package com.interpss.QA.rfile.aclf;

import org.interpss.numeric.util.NumericUtil;

import com.interpss.QA.rfile.BaseCompareFileProcessor;
import com.interpss.common.datatype.UnitType;
import com.interpss.common.exp.InterpssException;
import com.interpss.common.util.IpssLogger;
import com.interpss.common.util.StringUtil;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;


public class PSLFComapreFileProcessor extends BaseCompareFileProcessor {
	private boolean busDataProcessed = false,
	                busRecordBegin = false,
	                busDataEnd = true;
	
	public PSLFComapreFileProcessor(AclfNetwork net) {
		this.net = net;
	}
	
	@Override
	public boolean processLine(String lineStr) throws InterpssException {
		// first step is to verify Base Kva
		// format: System base      100.00 MVA
		if (!baseKvaProcessed) {
			if (lineStr.contains("System base")) {
				String[] strAry = lineStr.trim().split(" +");  // X+ once or more
				IpssLogger.getLogger().info("BaseMva: " + strAry[2]);
				assert("MVA".equals(strAry[3]));
				baseKvaProcessed = true;
			}
		}
		else if (!busDataProcessed) {
//         FROM            AR ID   MW   MVAR           TO     ARCK    MW    MVAR    TAP    STP  PLOSS  QLOSS  PCT               
			if (lineStr.contains("FROM") && lineStr.contains("TO")) {
				busRecordBegin = true;
			}
			
			if (busRecordBegin) {
				/*
			    if first 8 chars are bus number, busDataBegin = true
			    if busDataBegin == true and empty line, busDataEnd = true
				*/
				if (busDataEnd && lineStr.length() > 8 &&
						StringUtil.isInt(lineStr.substring(0, 8).trim())) {
					busDataBegin = true;
					busDataEnd = false;
					totalBus++; this.busDataLineNo = 0;
					//IpssLogger.getLogger().info("Processing bus: " + totalBus);
				}
				else if (lineStr.trim().equals("")) {
					// if empty line, start another bus record
					busDataEnd = true;
				}
				
				if (busDataBegin && !busDataEnd) {
					//System.out.println(++this.busDataLineNo + ":" + lineStr);
					++this.busDataLineNo;
					if (this.busDataLineNo == 1) {
						this.busNo = new Integer(lineStr.substring(0, 8).trim()).intValue();
					}
					else if (this.busDataLineNo == 3) {
						this.busVoltage = new Double(lineStr.substring(0, 14).trim()).doubleValue();
					}
					else if (this.busDataLineNo == 4) {
						this.busAngle = new Double(lineStr.substring(0, 13).trim()).doubleValue();
					}

					if (this.busDataLineNo == 4) {
						//System.out.println("BusNo, BusV, BusAng: " + this.busNo + ", " + this.busVoltage + ", " + this.busAngle);
						AclfBus bus = this.net.getAclfBus("Bus"+this.busNo);
						assert(bus != null);
						
						if (!NumericUtil.equals(this.busVoltage, bus.getVoltageMag(), 0.0001)) {
							String msg = "Bus voltage mag mismatch: Bus-" + this.busNo + ", " + 
									     this.busVoltage + ", " + String.format("%5.4f", bus.getVoltageMag());
							//IpssLogger.getLogger().warning(msg);
							this.errMsgList.add("\n" + msg);
						}
						
						if (!NumericUtil.equals(this.busAngle, bus.getVoltageAng(UnitType.Deg), 0.1)) {
							String msg = "Bus voltage ang mismatch: Bus-" + this.busNo + ", " + 
											this.busAngle + ", " + String.format("%5.2f", bus.getVoltageAng(UnitType.Deg));
							//IpssLogger.getLogger().warning(msg);
							this.errMsgList.add("\n" + msg);
						}						
					}
				}
			}
		}
		return true;
	}
}
