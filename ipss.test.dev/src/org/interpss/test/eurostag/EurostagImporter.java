package org.interpss.test.eurostag;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.math.complex.Complex;
import org.interpss.numeric.datatype.LimitType;
import org.interpss.numeric.datatype.Unit.UnitType;

import com.interpss.CoreObjectFactory;
import com.interpss.common.exp.InterpssException;
import com.interpss.core.aclf.AclfBranch;
import com.interpss.core.aclf.AclfBranchCode;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfGenCode;
import com.interpss.core.aclf.AclfLoadCode;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.aclf.adpter.AclfPVGenBus;
import com.interpss.core.aclf.adpter.AclfSwingBus;
import com.interpss.core.net.Area;

public class EurostagImporter {

	private String networkName;	// Network name in Eurostag format

	// Constructor with the network name
	public EurostagImporter(String networkName) {
		super();
		this.networkName = networkName;
	}

	// Getter method to retrieve the network name outside of the class
	public String getNetworkName() {
		return networkName;
	}
	
	// Method to convert the load flow model from Eurostag format into InterPSS format
	public AclfNetwork ImportEurostagLoadflowModel() throws InterpssException {
		// 1. Initialise the network object
		AclfNetwork network = CoreObjectFactory.createAclfNetwork();
		// 2. Import all the information from the Eurostag file
		Collection<String> originalData = importLFData(this.networkName);
		// 3. Add the information into the network object
		// 3.1. Add general information
		for (String thisData : originalData) {
			if (thisData.startsWith("9")) {
				double baseMVA = Double.parseDouble(thisData.substring(66, 74));
				network.setBaseKva(baseMVA * 1000);
			}
		}
		// 3.2. Add area information
		HashMap<String, Long> mapAreaNameIndex = new HashMap<String, Long>();
		for (String thisData : originalData) {
			if (thisData.startsWith("AA")) {
				String thisAreaName = thisData.substring(3, 5);
				Area thisArea = CoreObjectFactory.createArea(network.getAreaList().size() + 1, network);
				thisArea.setName(thisAreaName);
				network.getAreaList().add(thisArea);
				mapAreaNameIndex.put(thisArea.getName(), thisArea.getNumber());
			}
		}
		// 3.3. Add node information
		// 3.3.1. Add normal node information
		for (String thisData : originalData) {
			if (thisData.startsWith("1")) {
				// 3.3.1.1. Create the AclfBus object
				String thisBusID = thisData.substring(3, 11);
				AclfBus thisBus = CoreObjectFactory.createAclfBus(thisBusID.trim(), network);
				String thisBusAreaName = thisData.substring(1, 3);
				// 3.3.1.2. Set the bus area information
				thisBus.setArea(network.getArea(mapAreaNameIndex.get(thisBusAreaName)));
				// 3.3.1.3. Set the bus name
				thisBus.setName(thisBusID.trim());
				// 3.3.1.4. Set the generator power
				thisBus.setGenP(Double.parseDouble(thisData.substring(30, 38)));
				thisBus.setGenQ(Double.parseDouble(thisData.substring(39, 47)));
				// 3.3.1.5. Set the base voltage of this bus
				thisBus.setBaseVoltage(Double.parseDouble(thisData.substring(84, 92)) * 1000.0);
				// 3.3.1.6. Set the initial voltage of this bus
				thisBus.setVoltageMag(Double.parseDouble(thisData.substring(98, 106)));
				thisBus.setVoltageAng(Double.parseDouble(thisData.substring(107, 115)));
			}
		}
		// 3.3.2. Add special node information
		Collection<String> specialBuses = new ArrayList<String>();
		for (String thisData : originalData) {
			if (thisData.startsWith("5")) {
				String thisSpecialName = thisData.substring(3, 11);
				specialBuses.add(thisSpecialName.trim());
				AclfBus thisBus = network.getAclfBus(thisSpecialName.trim());
				switch (Integer.parseInt(thisData.substring(57, 58))) {
				case 1:	// PV bus
					thisBus.setGenCode(AclfGenCode.GEN_PV);
					AclfPVGenBus pvBus = thisBus.toPVBus();
					pvBus.setVoltMag(Double.parseDouble(thisData.substring(47, 56)), UnitType.kV);
					LimitType qLimit = new LimitType(Double.parseDouble(thisData.substring(30, 38)), 
							Double.parseDouble(thisData.substring(21, 29)));
					thisBus.setQGenLimit(qLimit);
					System.out.println("MinQ=" + thisBus.getQGenLimit().getMin() + ", MaxQ=" + thisBus.getQGenLimit().getMax());
					break;
				case 3:	// Swing bus
					thisBus.setGenCode(AclfGenCode.SWING);
					AclfSwingBus swingBus = thisBus.toSwingBus();
					swingBus.setVoltAngDeg(Double.parseDouble(thisData.substring(39, 47)));
					swingBus.setVoltMag(Double.parseDouble(thisData.substring(47, 56)), UnitType.kV);
					System.out.println("SwingV=" + swingBus.getVoltMag(UnitType.kV) + "kV");
					break;
				}
			}
		}
		// 3.3.3. Add load information
		for (String thisData : originalData) {
			if (thisData.startsWith("CH")) {
				String thisLoadName = thisData.substring(14, 22);
				AclfBus thisBus = network.getAclfBus(thisLoadName.trim());
				// Convert this AclfBus into load type
				if (!specialBuses.contains(thisLoadName.trim())) {
					thisBus.setGenCode(AclfGenCode.NON_GEN);
					thisBus.setLoadCode(AclfLoadCode.CONST_P);
					thisBus.toLoadBus();
				}
				thisBus.setLoadP(Double.parseDouble(thisData.substring(23, 31)));
				thisBus.setLoadQ(Double.parseDouble(thisData.substring(32, 40)));
			}
		}
		// 3.4. Add branch information
		// 3.4.1. Add AC lines
		for (String thisData : originalData) {
			if (thisData.startsWith("3")) {
				String sendingName = thisData.substring(2, 10).trim();
				String receivingName = thisData.substring(11, 19).trim();
				AclfBranch thisACLine = CoreObjectFactory.createAclfBranch();
				thisACLine.setCircuitNumber(thisData.substring(19, 20));
				thisACLine.setBranchCode(AclfBranchCode.LINE);
				thisACLine.setZ(new Complex(Double.parseDouble(thisData.substring(21, 29)), Double.parseDouble(thisData.substring(30, 38))));
				thisACLine.setHShuntY(new Complex(Double.parseDouble(thisData.substring(39, 47)), Double.parseDouble(thisData.substring(48, 56))));
				network.addBranch(thisACLine, sendingName, receivingName);
				System.out.println("Successfully add a new AC line between " + sendingName + " and " + receivingName);
			}
		}
		// 3.4.2. Add transformers
		for (String thisData : originalData) {
			if (thisData.startsWith("41")) {	// Type 1 transformer in Eurostag format
				String sendingName = thisData.substring(2, 10).trim();
				String receivingName = thisData.substring(11, 19).trim();
				AclfBranch thisTransformer = CoreObjectFactory.createAclfBranch();
				thisTransformer.setCircuitNumber(thisData.substring(19, 20));
				thisTransformer.setBranchCode(AclfBranchCode.XFORMER);
				thisTransformer.setZ(new Complex(Double.parseDouble(thisData.substring(21, 29)), Double.parseDouble(thisData.substring(30, 38))));
				thisTransformer.setFromShuntY(new Complex(Double.parseDouble(thisData.substring(39, 47)), 
						Double.parseDouble(thisData.substring(48, 56))));
				thisTransformer.setRatingMva1(Double.parseDouble(thisData.substring(57, 65)));
				thisTransformer.setToTurnRatio(Double.parseDouble(thisData.substring(66, 74)));
				network.addBranch(thisTransformer, sendingName, receivingName);
				System.out.println("Successfully add a new transformer between " + sendingName + " and " + receivingName);
			}
			else if (thisData.startsWith("42")) {	// Type 1 transformer in Eurostag format
				String sendingName = thisData.substring(2, 10).trim();
				String receivingName = thisData.substring(11, 19).trim();
				AclfBranch thisTransformer = CoreObjectFactory.createAclfBranch();
				thisTransformer.setCircuitNumber(thisData.substring(19, 20));
				thisTransformer.setBranchCode(AclfBranchCode.XFORMER);
				thisTransformer.setZ(new Complex(Double.parseDouble(thisData.substring(21, 29)), Double.parseDouble(thisData.substring(30, 38))));
				thisTransformer.setFromShuntY(new Complex(Double.parseDouble(thisData.substring(39, 47)), 
						Double.parseDouble(thisData.substring(48, 56))));
				thisTransformer.setRatingMva1(Double.parseDouble(thisData.substring(75, 83)));
				thisTransformer.setToTurnRatio(Double.parseDouble(thisData.substring(84, 92)));
				network.addBranch(thisTransformer, sendingName, receivingName);
				System.out.println("Successfully add a new transformer between " + sendingName + " and " + receivingName);
			}
			else if (thisData.startsWith("48")) {	// Type 8 transformer in Eurostag format
				
			}
		}
		// 4. Return the network object
		return network;
	}
	
	// Import load flow data from the Eurostag file
	private static Collection<String> importLFData(String networkName) {		
		FileReader fr = null;
		BufferedReader br = null;
		Collection<String> lfPrivateData = new ArrayList<String>();
		try {
			try {
				fr = new FileReader(networkName + ".ech");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			br = new BufferedReader(fr);
			String thisLine = br.readLine();
			while (thisLine != null) {
				lfPrivateData.add(thisLine);
				thisLine = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return lfPrivateData;
	}
	
}
