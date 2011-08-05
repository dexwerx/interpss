package com.interpss.QA.rfile.aclf;

import org.apache.commons.math.complex.Complex;
import org.interpss.numeric.util.NumericUtil;

import com.interpss.QA.rfile.BaseCompareFileProcessor;
import com.interpss.common.datatype.UnitType;
import com.interpss.common.exp.InterpssException;
import com.interpss.common.util.IpssLogger;
import com.interpss.common.util.StringUtil;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;


public class PSSE_FileProcessor extends BaseCompareFileProcessor {
	private AclfBus bus = null;

	public PSSE_FileProcessor(AclfNetwork net) {
		this.net = net;
		this.baseMva = net == null? 100.0 : net.getBaseKva()*0.001;
	}
	
	@Override
	public boolean processLine(String lineStr) throws InterpssException {
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
		if (lineStr.startsWith(" BUS")) {
			busDataBegin = true;
			totalBus++; this.busDataLineNo = 0;
			//IpssLogger.getLogger().info("Processing bus: " + totalBus);
		}

		if (busDataBegin) {
			++this.busDataLineNo;
			//System.out.println(this.busDataLineNo + ":" + lineStr);
			if (this.busDataLineNo == 1) {
				//1: BUS      2 BUS002      500.00 CKT     MW     MVAR     MVA   % 0.9920PU  -29.93  X--- LOSSES ---X X---- AREA -----X X---- ZONE -----X      2
				String str = lineStr.substring(4,11);
				this.busNo = new Integer(str.trim()).intValue();
				str = lineStr.substring(63,69);
				this.busVoltage = new Double(str.trim()).doubleValue();  // 0.9920PU
				str = lineStr.substring(73,79);
				this.busAngle = new Double(str.trim()).doubleValue();
				//System.out.println(this.busNo + ", " + this.busVoltage + ", " + this.busAngle);
				bus = this.net.getAclfBus("Bus"+this.busNo);
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
			
			if (this.busDataLineNo == 3 && lineStr.contains("FROM GENERATION")) {
				//3:   FROM GENERATION                    980.0   187.3R  997.7 998 26.390KV               MW     MVAR    1                 3 HK
				//3:  FROM GENERATION                   9950.0  3027.0R10400.3  80 20.400KV               MW     MVAR    1                 1 ZONE-001
				this.busP = new Double(lineStr.substring(36, 43)).doubleValue();  // Load as positive direction
				this.busQ = new Double(lineStr.substring(44, 50)).doubleValue();
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
			
			if (this.busDataLineNo == 5 && lineStr.contains("TO LOAD-PQ")) {
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
			
			if (this.busDataLineNo == 7 && lineStr.contains("TO SHUNT")) {
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
		return true;
	}
}
