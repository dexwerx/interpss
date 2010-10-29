/*
 * Created on 2006-4-8
 * 
 */
package org.interpss.custom.exchange.impl;

import org.apache.commons.math.complex.Complex;

import com.interpss.common.datatype.UnitType;
import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.common.util.IpssLogger;
import com.interpss.core.CoreObjectFactory;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBranchCode;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfGenCode;
import com.interpss.core.aclf.AclfLoadCode;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.aclf.adpter.LineAdapter;
import com.interpss.core.aclf.adpter.LoadBusAdapter;
import com.interpss.core.aclf.adpter.SwingBusAdapter;

public class PSATFormat_in {
	private static final int NotDefined = 0;
	private static final int BusData = 1;
	private static final int BranchData = 2;
	private static final int SwingBusData = 3;
	private static final int PQBusData = 4;

    public static AclfNetwork loadFile(java.io.BufferedReader din, IPSSMsgHub msg) throws Exception {
    	AclfNetwork  adjNet = CoreObjectFactory.createAclfAdjNetwork();
    	adjNet.setAllowParallelBranch(false);

		String str = "";
		int dataType = NotDefined;
   		while((str = din.readLine()) != null) {
   			if (str.startsWith("Bus.con")) {
   				dataType = BusData;
   			}
   			else if (str.startsWith("Line.con")) {
   				dataType = BranchData;
   			}
   			else if (str.startsWith("SW.con")) {
   				dataType = SwingBusData;
   			}
   			else if (str.startsWith("PQ.con")) {
   				dataType = PQBusData;
   			}
   			else if (str.startsWith("Varname.bus")) {
   				dataType = NotDefined;
   			}
   			else {
   				if (!str.trim().equals("") && !str.trim().startsWith("];")) {
   					// remove the end ;
   					str = str.replace(';', ' ');
   			   		switch(dataType) {
   		   				case BusData : {
   		   					processBusDataLine(str, adjNet);
   		   					break;
   		   				}
   		   				case SwingBusData : {
   		   					processSwingBusDataLine(str, adjNet);
   		   					break;
   		   				}
   		   				case PQBusData : {
   		   					processPQBusDataLine(str, adjNet);
   		   					break;
   		   				}
   		   				case BranchData : {
   		   					processBranchDataLine(str, adjNet, msg);
   		   					break;
   		   				}
   			   		}
   				}
   			}
   		}
    	return adjNet;
    }

    private static void processBusDataLine(String lineStr, AclfNetwork  adjNet) {
		IpssLogger.getLogger().info("BusData: " + lineStr);
      	java.util.StringTokenizer st = new java.util.StringTokenizer(lineStr);
      	int busNumber = 0, zoneNo = 0, areaNo = 0;
      	double baseKv=0.0, vpu=0.0, ang=0.0;
      	while (st.hasMoreTokens()) {
      		busNumber = new Integer(st.nextToken()).intValue();
      		baseKv    = new Double(st.nextToken()).doubleValue();
      		vpu       = new Double(st.nextToken()).doubleValue();
      		ang       = new Double(st.nextToken()).doubleValue();
      		// in InterPSS, an area may have 0-* zones
      		zoneNo      = new Integer(st.nextToken()).intValue();
      		areaNo      = new Integer(st.nextToken()).intValue();
      		IpssLogger.getLogger().info("busNumber,baseKv,vpu,ang,zone,area: " +
      				busNumber + "," + baseKv + "," + vpu + "," + ang + "," + zoneNo + "," + areaNo);
      	}
      	AclfBus bus = CoreObjectFactory.createAclfBus(new Integer(busNumber).toString(), 
      			areaNo, zoneNo, "1", adjNet);
    	bus.setBaseVoltage(baseKv, UnitType.kV);
    	
    	// add the bus object into the network container
    	//adjNet.addBus(bus);    	
  	}

    private static void processSwingBusDataLine(String lineStr, AclfNetwork  adjNet) throws Exception {
    	IpssLogger.getLogger().info("SwingBusData: " + lineStr);
      	java.util.StringTokenizer st = new java.util.StringTokenizer(lineStr);
      	int busNumber = 0;
      	double baseKv=0.0, baseMva, vpu=0.0, ang=0.0, qmax = 0.0, qmin = 0.0, vmax = 0.0, vmin = 0.0, p0 = 0.0, lossPCoeff = 0.0;
      	while (st.hasMoreTokens()) {
      		busNumber = new Integer(st.nextToken()).intValue();
      		baseMva   = new Double(st.nextToken()).doubleValue();
      		baseKv    = new Double(st.nextToken()).doubleValue();
      		vpu       = new Double(st.nextToken()).doubleValue();
      		ang       = new Double(st.nextToken()).doubleValue();
      		qmax      = new Double(st.nextToken()).doubleValue();
      		qmin      = new Double(st.nextToken()).doubleValue();
      		vmax      = new Double(st.nextToken()).doubleValue();
      		vmin      = new Double(st.nextToken()).doubleValue();
      		p0        = new Double(st.nextToken()).doubleValue();
      		lossPCoeff = new Double(st.nextToken()).doubleValue();
      		IpssLogger.getLogger().info("busNumber,baseMva,baseKv,vpu,ang,qmax,qmin,vmax,vmin,p0,lossPCoeff: " +
      				busNumber + "," + baseMva + "," + baseKv + "," + vpu + "," + ang + "," + 
      				qmax + "," + qmin + "," + vmax + "," + vmin + "," + p0 + "," + lossPCoeff);
      	}
      	
      	// The bus should be already in the Network
      	AclfBus bus = adjNet.getAclfBus(new Integer(busNumber).toString());
      	if (bus == null) {
      		throw new Exception("Swing Bus not found in the network, bus munber: " + busNumber);
      	}
		bus.setGenCode(AclfGenCode.SWING);
		SwingBusAdapter gen = bus.toSwingBus();
		gen.setVoltMag(vpu, UnitType.PU);
		gen.setVoltAng(ang, UnitType.Deg);
    }
    
    private static void processPQBusDataLine(String lineStr, AclfNetwork  adjNet)  throws Exception  {
    	IpssLogger.getLogger().info("PQBusData: " + lineStr);
      	java.util.StringTokenizer st = new java.util.StringTokenizer(lineStr);
      	int busNumber = 0;
      	double baseKv=0.0, baseMva, pl=0.0, ql=0.0, vmax = 0.0, vmin = 0.0;
      	boolean allowConv2Z = false;
      	while (st.hasMoreTokens()) {
      		busNumber = new Integer(st.nextToken()).intValue();
      		baseMva   = new Double(st.nextToken()).doubleValue();
      		baseKv    = new Double(st.nextToken()).doubleValue();
      		pl        = new Double(st.nextToken()).doubleValue();
      		ql        = new Double(st.nextToken()).doubleValue();
      		vmax      = new Double(st.nextToken()).doubleValue();
      		vmin      = new Double(st.nextToken()).doubleValue();
      		allowConv2Z = (new Integer(st.nextToken()).intValue()) == 1;
      		IpssLogger.getLogger().info("busNumber,baseMva,baseKv,p,q,vmax,vmin,allowConv2Z: " +
      				busNumber + "," + baseMva + "," + baseKv + "," + pl + "," + 
      				ql + "," + vmax + "," + vmin + "," + allowConv2Z);
      	}
      	
      	// The bus should be already in the Network
      	AclfBus bus = adjNet.getAclfBus(new Integer(busNumber).toString());
      	if (bus == null) {
      		throw new Exception("PQ Bus not found in the network, bus munber: " + busNumber);
      	}
	 	bus.setLoadCode(AclfLoadCode.CONST_P);
		LoadBusAdapter load = bus.toLoadBus();
		load.setLoad(new Complex(pl, ql), UnitType.PU);      	
    }

    private static void processBranchDataLine(String lineStr, AclfNetwork  adjNet, IPSSMsgHub msg) {
    	IpssLogger.getLogger().info("BranchData: " + lineStr);
      	java.util.StringTokenizer st = new java.util.StringTokenizer(lineStr);
      	int fromBusNumber = 0, toBusNumber = 0;
      	double baseKv=0.0, baseMva = 0.0, baseHz = 0.0, l = 0.0, r = 0.0, x=0.0, b=0.0, imax = 0.0, pmax = 0.0, smax = 0.0;
      	while (st.hasMoreTokens()) {
      		fromBusNumber = new Integer(st.nextToken()).intValue();
      		toBusNumber = new Integer(st.nextToken()).intValue();
      		baseMva    = new Double(st.nextToken()).doubleValue();
      		baseKv    = new Double(st.nextToken()).doubleValue();
      		baseHz    = new Double(st.nextToken()).doubleValue();
      		l       = new Double(st.nextToken()).doubleValue();
      		st.nextToken();
      		r       = new Double(st.nextToken()).doubleValue();
      		x       = new Double(st.nextToken()).doubleValue();
      		b       = new Double(st.nextToken()).doubleValue();
      		st.nextToken();
      		st.nextToken();
      		imax       = new Double(st.nextToken()).doubleValue();
      		pmax       = new Double(st.nextToken()).doubleValue();
      		smax       = new Double(st.nextToken()).doubleValue();
      		IpssLogger.getLogger().info("fromBusNumber,toBusNumber,baseMva,baseKv,baseHz,l,r,x,b,imax,pmax,smax: " +
      				fromBusNumber + "," + toBusNumber + "," + baseMva + "," + baseKv + "," + 
      				baseHz + "," + l + "," + r + "," + x + "," + b + "," + imax + "," + pmax + "," + smax);
      	}	
      	
      	AclfBranch bra = CoreObjectFactory.createAclfBranch("1", adjNet);
      	
      	// add the object into the network container
      	adjNet.addBranch(bra, new Integer(fromBusNumber).toString(), new Integer(toBusNumber).toString());    	
      	
  		// A line branch
      	if (l == 0.0) 
      		l = 1.0;
    	bra.setBranchCode(AclfBranchCode.LINE);
		LineAdapter line = bra.toLine();
    	line.getAclfBranch().setZ(new Complex(l*r,l*x), msg);
    	line.setHShuntY(new Complex(0.0,0.5*l*b), UnitType.PU, 1.0); 
    				                  // Unit is PU, no need to enter branch baseV, so put 1.0 for baseV
    }
}
