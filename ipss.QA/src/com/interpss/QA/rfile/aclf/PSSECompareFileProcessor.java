package com.interpss.QA.rfile.aclf;

import org.apache.commons.math.complex.Complex;
import org.interpss.numeric.util.NumericUtil;

import com.interpss.QA.rfile.BaseCompareFileProcessor;
import com.interpss.common.datatype.UnitType;
import com.interpss.common.exp.InterpssException;
import com.interpss.common.util.IpssLogger;
import com.interpss.common.util.StringUtil;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.net.Branch;
import com.interpss.core.net.Bus;


public class PSSECompareFileProcessor extends BaseCompareFileProcessor {
	//private static String Str_offset = " ";   // " " or ""
	//private static int Int_offset = 0;        // 0 or -1
	
	private static String Str_offset = "";   // " " or ""
	private static int Int_offset = -1;        // 0 or -1

	private static String TKN_BUSLine = Str_offset + "BUS";
	private static String TKN_LoadPQ = "TO LOAD-PQ";
	private static String TKN_BranchLine = Str_offset + " TO";
	private static String TKN_GEN = "FROM GENERATION";
	private static String TKN_SHUNT = "TO SHUNT";
	private static String TKN_SWITCHED_SHUNT = "TO SWITCHED SHUNT";
	
	private static int 	BusNo_Begin = 4 + Int_offset,
	    				BusNo_End = 11 + Int_offset,
	    				BusVolt_Begin = 63 + Int_offset,
	    				BusVolt_End = 69 + Int_offset,
	    				BusAng_Begin = 73 + Int_offset,
	    				BusAng_End = 79 + Int_offset,
	    				
	    				BusP_Begin = 36 + Int_offset,
	    		    	BusP_End = 43 + Int_offset,
	    		    	BusQ_Begin = 44 + Int_offset,
	    		    	BusQ_End = 50 + Int_offset;	    		

	private AclfBus bus = null;

	public PSSECompareFileProcessor(AclfNetwork net) {
		this.net = net;
		this.baseMva = net == null? 100.0 : net.getBaseKva()*0.001;
	}

	public PSSECompareFileProcessor(AclfNetwork net, boolean netOnly) {
		this(net);
		this.compareNetworkOnly = netOnly;
		if (netOnly) {
			for (Bus b : net.getBusList()) {
				b.setSortNumber(0);
			}
			for (Branch branch : net.getBranchList()) {
				branch.setSortNumber(0);
			}
		}
	}
	
	@Override public void postProcessing() {
		if (this.compareNetworkOnly) {
			for (Bus b : net.getBusList()) {
				if (b.getSortNumber() == 0 && b.isActive())
					addErrMsg("Bus not found in PSS/E result file, " + b.getId());
			}
			for (Branch branch : net.getBranchList()) {
				if (branch.getSortNumber() == 0 && branch.isActive())
					addErrMsg("Branch not found in PSS/E result file, " + branch.getId());
			}
		}
	}	
	
	@Override public boolean processLine(String lineStr) throws InterpssException {
		// first step is to verify Base Kva
		// format:         100.0                                     RATING   %MVA FOR TRANSFORMERS
		if (!baseKvaProcessed) {
			if (lineStr.length() > 38 && StringUtil.isDouble(lineStr.substring(0, 38).trim())) {
				double baseMva = StringUtil.getDouble(lineStr.substring(0, 38).trim());
				IpssLogger.getLogger().info("BaseMva: " + baseMva);
				baseKvaProcessed = true;
			}
		}

// BUS      2 BUS002      500.00 CKT     MW     MVAR     MVA   % 0.9920PU  -29.93  X--- LOSSES ---X X---- AREA -----X X---- ZONE -----X      2               
		if (lineStr.startsWith(TKN_BUSLine)) {
			busDataBegin = true;
			totalBus++; this.busDataLineNo = 0;
			//IpssLogger.getLogger().info("Processing bus: " + totalBus);
		}

		if (busDataBegin) {
			++this.busDataLineNo;
			//System.out.println(this.busDataLineNo + ":" + lineStr);
			if (this.busDataLineNo == 1) {
				//1: BUS      2 BUS002      500.00 CKT     MW     MVAR     MVA   % 0.9920PU  -29.93  X--- LOSSES ---X X---- AREA -----X X---- ZONE -----X      2
				String str = lineStr.substring(BusNo_Begin, BusNo_End);
				this.busNo = new Integer(str.trim()).intValue();
				str = lineStr.substring(BusVolt_Begin,BusVolt_End);
				this.busVoltage = new Double(str.trim()).doubleValue();  // 0.9920PU
				str = lineStr.substring(BusAng_Begin,BusAng_End);
				this.busAngle = new Double(str.trim()).doubleValue();
				//System.out.println(this.busNo + ", " + this.busVoltage + ", " + this.busAngle);
				bus = this.net.getAclfBus("Bus"+this.busNo);
				
				if (this.compareNetworkOnly) {
					if (bus == null)
						addErrMsg("Bus not found, "+("Bus"+this.busNo)+"  ", lineStr);
					else 
						bus.setSortNumber(1);
				}
				else {
					assert(bus != null);
					
					double volt = bus.getVoltageMag();
					if (!NumericUtil.equals(this.busVoltage, volt, 0.0001)) {
						String msg = "Bus voltage mag mismatch: Bus-" + this.busNo + ", " + 
								     this.busVoltage + ", " + String.format("%5.4f, %4.3f", volt,  
								     Math.abs(100.0*(this.busVoltage - volt)/this.busVoltage)) + "%";
						//IpssLogger.getLogger().warning(msg);
						addErrMsg(msg, lineStr);
					}

					double ang = bus.getVoltageAng(UnitType.Deg);
					if (!NumericUtil.equals(this.busAngle, ang, 0.01)) {
						String msg = "Bus voltage ang mismatch: Bus-" + this.busNo + ", " + 
										this.busAngle + ", " + String.format("%5.2f, %4.3f", ang,
										Math.abs(100.0*(this.busAngle - ang)/this.busAngle)) + "%";
						//IpssLogger.getLogger().warning(msg);
						addErrMsg(msg, lineStr);
					}
				}
			}
			
			// looking for branch info
			// TO    153 MID230      230.00  2   -301.3   -88.3   314.0  92                       6.11   54.97    1 CENTRAL         3 DISCNT_IN_A1
			
			if (lineStr.startsWith(TKN_BranchLine) && 
					!lineStr.contains(TKN_LoadPQ) &&
					!lineStr.contains(TKN_SWITCHED_SHUNT)) {
				String str = lineStr.substring(BusNo_Begin,BusNo_End);
				int toBusNo = new Integer(str.trim()).intValue();
				String toId = "Bus"+toBusNo;
				String fromId = "Bus"+this.busNo;
				AclfBranch branch = (AclfBranch)(net.getBranch(fromId, toId)==null? net.getBranch(toId, fromId): net.getBranch(fromId, toId));
				if (branch != null)
					branch.setSortNumber(1);
				else {
					addErrMsg("Branch not found - "+(fromId + "->" + toId)+"  ", lineStr);
					//System.out.println("line found: " + fromId + "->" + toId);
				}
			}
			
			if (!this.compareNetworkOnly) {
				if (this.busDataLineNo == 3 && lineStr.contains(TKN_GEN)) {
					//3:   FROM GENERATION                    980.0   187.3R  997.7 998 26.390KV               MW     MVAR    1                 3 HK
					//3:  FROM GENERATION                   9950.0  3027.0R10400.3  80 20.400KV               MW     MVAR    1                 1 ZONE-001
					this.busP = new Double(lineStr.substring(BusP_Begin, BusP_End)).doubleValue();  // Load as positive direction
					this.busQ = new Double(lineStr.substring(BusQ_Begin, BusQ_End)).doubleValue();
					Complex busPQ = bus.getGenResults();
					double mw = busPQ.getReal()*this.baseMva;
					double mvar = busPQ.getImaginary()*this.baseMva;
					if (!NumericUtil.equals(this.busP, mw, 0.1)) {
						String msg = "Bus GenP mismatch:        Bus-" + this.busNo + ", " + 
								     this.busP + ", " + String.format("%5.1f, %4.3f", mw,  
									 Math.abs(100.0*(this.busP - mw)/this.busP)) + "%" 
								     + " ->" + lineStr;
						//IpssLogger.getLogger().warning(msg);
						this.errMsgList.add("\n" + msg);
					}
					if (!NumericUtil.equals(this.busQ, mvar, 0.1)) {
						String msg = "Bus GenQ mismatch:        Bus-" + this.busNo + ", " + 
								     this.busQ + ", " + String.format("%5.1f, %4.3f", mvar,  
									 Math.abs(100.0*(this.busQ - mvar)/this.busQ)) + "%";
						//IpssLogger.getLogger().warning(msg);
						addErrMsg(msg, lineStr);
					}
				}

				if (this.busDataLineNo == 5 && lineStr.contains(TKN_LoadPQ)) {
					//5:  TO LOAD-PQ                        1750.0   -56.0  1750.9
					String[] strAry = lineStr.trim().split(" +");  // X+ once or more
					this.busP = new Double(strAry[2]).doubleValue();  // Load as positive direction
					this.busQ = new Double(strAry[3]).doubleValue();
					Complex busPQ = bus.getLoadResults();
					double mw = busPQ.getReal()*this.baseMva;
					double mvar = busPQ.getImaginary()*this.baseMva;
					if (!NumericUtil.equals(this.busP, mw, 0.1)) {
						String msg = "Bus LoadP mismatch:       Bus-" + this.busNo + ", " + 
								     this.busP + ", " + String.format("%5.1f, %4.3f", mw,  
									 Math.abs(100.0*(this.busP - mw)/this.busP)) + "%";
						//IpssLogger.getLogger().warning(msg);
						addErrMsg(msg, lineStr);
					}
					if (!NumericUtil.equals(this.busQ, mvar, 0.1)) {
						String msg = "Bus LoadQ mismatch:       Bus-" + this.busNo + ", " + 
								     this.busQ + ", " + String.format("%5.1f, %4.3f", mvar,  
									 Math.abs(100.0*(this.busQ - mvar)/this.busQ)) + "%";
						//IpssLogger.getLogger().warning(msg);
						addErrMsg(msg, lineStr);
					}
				}

				if (this.busDataLineNo == 7 && lineStr.contains(TKN_SHUNT)) {
					//7:  TO SHUNT                             0.0   114.7   114.7
					this.shuntQ = new Double(lineStr.substring(43, 51)).doubleValue();
					
					double q = -bus.getShuntY().getImaginary();
					q *= bus.getVoltageMag() * bus.getVoltageMag() * this.baseMva;				
					if (!NumericUtil.equals(this.shuntQ, q, 0.1)) {
						String msg = "Bus ShuntQ mismatch:     Bus-" + this.busNo + ", " + 
								     this.shuntQ + ", " + String.format("%5.1f, %4.3f", q,  
									 Math.abs(100.0*(this.shuntQ - q)/this.shuntQ)) + "%";
						//IpssLogger.getLogger().warning(msg);
						addErrMsg(msg, lineStr);
					}
				}
			}
		}
		return true;
	}
}
