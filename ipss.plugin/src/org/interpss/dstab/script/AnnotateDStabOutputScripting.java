package org.interpss.dstab.script;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

import org.interpss.numeric.util.Number2String;

import com.interpss.common.datatype.Constants;
import com.interpss.common.util.IpssLogger;
import com.interpss.common.util.StringUtil;
import com.interpss.dstab.DStabilityNetwork;
import com.interpss.dstab.datatype.BusVariableRec;
import com.interpss.dstab.datatype.MachineStateRec;

public class AnnotateDStabOutputScripting {
	enum OutType {
		Mach_Angle, Mach_Speed, Mach_Pe, Mach_Pm, Mach_Q, Mach_E, Mach_Eq1, Mach_Eq11, Mach_Ed1, Mach_Ed11, Mach_Ifd, Mach_Efd, Exc_Efd, Gov_Pm, Pss_Vs, Bus_VMag, Bus_VAng, Bus_PLoad, Bus_QLoad,
	};

	private IDStabOutputScripting anOutput = null;

	private List<Rec> displayRecList = new ArrayList<Rec>();
	private FileWriter fileWriter = null;

	private String timeStr = "";
	private String machStr = "";
	private String busStr = "";
	private String busDeviceStr = "";

	public AnnotateDStabOutputScripting(IDStabOutputScripting output) {
		this.anOutput = output;
	}

	// This method is called at the initialization stage
	public void init(DStabilityNetwork net) throws Exception {
		String filename = "";
		String[] varList = null;
		AnDStabOutputScripting an = anOutput.getAnOutputScripting();
		if (an != null) {
			filename = an.filename();
			varList = an.varList();
			parseVariableList(varList);
		}

		fileWriter = new FileWriter(new File(filename));
		String header = "Time";
		for (Rec rec : displayRecList) {
			header += ", " + rec.name;
		}
		header += "\n";
		this.fileWriter.write(header);
		this.machStr = "";
		this.busStr = "";
		this.busDeviceStr = "";
	}

	// This method is called when a machine is processed
	public boolean machStates(DStabilityNetwork net,
			Hashtable<String, Object> stateTable) {
		MachineStateRec machRec = new MachineStateRec(stateTable);
		String machId = machRec.machId;
		this.timeStr = Number2String.toStr(machRec.time, "0.000");
		for (Rec rec : displayRecList) {
			if (machId.equals(rec.id)) {
				double value = getMachValue(rec, machRec);
				this.machStr += ',' + Number2String.toStr(value, "0.0000");
			}
		}
		return true;
	}

	// This method is called when a bus object is processed
	public boolean busVariables(DStabilityNetwork net,
			Hashtable<String, Object> varTable) {
		BusVariableRec busRec = new BusVariableRec(varTable);
		String busId = busRec.busId;
		for (Rec rec : displayRecList) {
			if (busId.equals(rec.id)) {
				double value = getBusValue(rec, busRec);
				this.busStr += ',' + Number2String.toStr(value, "0.0000");
			}
		}
		return true;
	}

	// This method is called when a dynamic bus device is processed
	public boolean busDeviceStates(DStabilityNetwork net,
			Hashtable<String, Object> stateTable) {
		// output dynamic bus device here
		return true;
	}

	// This method is called when a simulation step is completed. At
	// this point, we write a line in the file
	public void endOfSimuStep() throws Exception {
		String str = this.timeStr;
		if (!this.machStr.equals(""))
			str += this.machStr;
		if (!this.busStr.equals(""))
			str += this.busStr;
		if (!this.busDeviceStr.equals(""))
			str += this.busDeviceStr;
		fileWriter.write(str + '\n');
		this.machStr = "";
		this.busStr = "";
		this.busDeviceStr = "";
	}

	public void close() throws Exception {
		fileWriter.flush();
		fileWriter.close();
	}

	private double getMachValue(Rec rec, MachineStateRec machRec) {
		if (rec.type == OutType.Mach_Angle)
			return machRec.angle;
		else if (rec.type == OutType.Mach_Speed)
			return machRec.speed;
		else if (rec.type == OutType.Mach_Pe)
			return machRec.Pe;
		else if (rec.type == OutType.Mach_Pm)
			return machRec.Pm;
		else if (rec.type == OutType.Mach_Q)
			return machRec.Q;
		else if (rec.type == OutType.Mach_E)
			return machRec.E;
		else if (rec.type == OutType.Mach_Eq1)
			return machRec.Eq1;
		else if (rec.type == OutType.Mach_Eq11)
			return machRec.Eq11;
		else if (rec.type == OutType.Mach_Ed1)
			return machRec.Ed1;
		else if (rec.type == OutType.Mach_Ed11)
			return machRec.Ed11;
		else if (rec.type == OutType.Mach_Ifd)
			return machRec.Ifd;
		else if (rec.type == OutType.Mach_Efd)
			return machRec.Efd;
		else if (rec.type == OutType.Exc_Efd)
			return machRec.excEfd;
		else if (rec.type == OutType.Gov_Pm)
			return machRec.govPm;
		else if (rec.type == OutType.Pss_Vs)
			return machRec.pssVs;

		IpssLogger.getLogger().warning("No value found for rec: " + rec);
		return 0.0;
	}

	private double getBusValue(Rec rec, BusVariableRec busRec) {
		if (rec.type == OutType.Bus_VMag)
			return busRec.vMag;
		else if (rec.type == OutType.Bus_VAng)
			return busRec.vAng;
		else if (rec.type == OutType.Bus_PLoad)
			return busRec.pLoad;
		else if (rec.type == OutType.Bus_QLoad)
			return busRec.qLoad;

		IpssLogger.getLogger().warning("No value found for rec: " + rec);
		return 0.0;
	}

	private void parseVariableList(String[] varList) throws Exception {
		for (String str : varList) {
			StringTokenizer st = new StringTokenizer(str, ",");
			String name = st.nextToken().trim();
			String id = st.nextToken().trim();
			String var = st.nextToken().trim();
			Rec rec = new Rec();
			rec.name = StringUtil.getDisplyName(name); // get name part of
			// "str.name"
			if (var.startsWith("mach.") || var.startsWith("exc.")
					|| var.startsWith("gov.") || var.startsWith("pss.")) {
				rec.id = Constants.Token_MachId + id;
			} else if (var.startsWith("bus.")) {
				rec.id = id;
			}
			if (var.equals("mach.angle"))
				rec.type = OutType.Mach_Angle;
			else if (var.equals("mach.speed"))
				rec.type = OutType.Mach_Speed;
			else if (var.equals("mach.pe"))
				rec.type = OutType.Mach_Pe;
			else if (var.equals("mach.pm"))
				rec.type = OutType.Mach_Pm;
			else if (var.equals("mach.q"))
				rec.type = OutType.Mach_Q;
			else if (var.equals("mach.e"))
				rec.type = OutType.Mach_E;
			else if (var.equals("mach.eq1"))
				rec.type = OutType.Mach_Eq1;
			else if (var.equals("mach.eq11"))
				rec.type = OutType.Mach_Eq11;
			else if (var.equals("mach.ed1"))
				rec.type = OutType.Mach_Ed1;
			else if (var.equals("mach.ed11"))
				rec.type = OutType.Mach_Ed11;
			else if (var.equals("mach.ifd"))
				rec.type = OutType.Mach_Ifd;
			else if (var.equals("mach.efd"))
				rec.type = OutType.Mach_Efd;
			else if (var.equals("exc.efd"))
				rec.type = OutType.Exc_Efd;
			else if (var.equals("gov.pm"))
				rec.type = OutType.Gov_Pm;
			else if (var.equals("pss.vs"))
				rec.type = OutType.Pss_Vs;
			else if (var.equals("bus.vmag"))
				rec.type = OutType.Bus_VMag;
			else if (var.equals("bus.vang"))
				rec.type = OutType.Bus_VAng;
			else if (var.equals("bus.pload"))
				rec.type = OutType.Bus_PLoad;
			else if (var.equals("bus.qload"))
				rec.type = OutType.Bus_QLoad;
			displayRecList.add(rec);
		}
	}

	@Override
	public String toString() {
		String str = "Output Display Record: [\n";
		for (Rec rec : displayRecList)
			str += rec + "\n";
		return str + "]";
	}

	class Rec {
		public String name;
		public String id;
		public OutType type;

		@Override
		public String toString() {
			return "name, id, type: " + name + ", " + id + ", " + type;
		}
	}
}
