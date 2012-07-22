/*
 * @(#)ODM2PSSEConverter.java   
 *
 * Copyright (C) 2009 www.interpss.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * @Author  Stephen Hou
 * @Version 1.0
 * @Date 08/15/2009
 * 
 *   Revision History
 *   ================
 *
 */

package org.ieee.pes.odm.pss.odmout.adapter.psse;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ODM2PSSEConverter {

	
	public ODM2PSSEConverter(Logger logger) {
		
	}

	DocumentBuilderFactory domfac = DocumentBuilderFactory.newInstance();
	Document doc = null;

	public void processInputFile(String inputFile, String outputFile)
			throws Exception {
		try {
			
			OutputStream outAreaInterchange = new FileOutputStream(outputFile
					+ "areaInterchange.txt");
			OutputStream outInterareaTransfer = new FileOutputStream(outputFile
					+ "interareaTransfer.txt");
			OutputStream zoneInformation = new FileOutputStream(outputFile
					+ "zoneInformation.txt");
			OutputStream outBus = new FileOutputStream(outputFile + "bus.txt"); // output file for storing bus data
			OutputStream outLoad = new FileOutputStream(outputFile + "load.txt"); // output file for storing load data
			OutputStream outGen = new FileOutputStream(outputFile + "gen.txt"); // output file for storing gen data
			OutputStream outSwitchShunt = new FileOutputStream(outputFile
					+ "switchShunt.txt"); // output file for storing switch shunt data

			OutputStream outBranch = new FileOutputStream(outputFile
					+ "branch.txt"); // for branch data
			OutputStream outTrans = new FileOutputStream(outputFile
					+ "transformer.txt"); // for transformer data
			OutputStream outGenerator = new FileOutputStream(outputFile
					+ "generator.txt");
			PrintStream printGenerator = new PrintStream(outGenerator);
			OutputStream outExciter = new FileOutputStream(outputFile
					+ "exciter.txt");
			PrintStream printExciter = new PrintStream(outExciter);
			OutputStream outPSS = new FileOutputStream(outputFile + "pss.txt");
			PrintStream printPSS = new PrintStream(outPSS);
			OutputStream outTG = new FileOutputStream(outputFile + "tg.txt");

			
			PrintStream printAreaInterchange = new PrintStream(
					outAreaInterchange);
			PrintStream printInterareaTransfer = new PrintStream(
					outInterareaTransfer);
			PrintStream printZone = new PrintStream(zoneInformation);
			PrintStream printBus = new PrintStream(outBus);
			PrintStream printLoad = new PrintStream(outLoad);
			PrintStream printGen = new PrintStream(outGen);
			PrintStream printSwitShunt = new PrintStream(outSwitchShunt);
			PrintStream printBranch = new PrintStream(outBranch);
			PrintStream printTrans = new PrintStream(outTrans);
			PrintStream printTG = new PrintStream(outTG);
			
			InputStream inputStream = new FileInputStream(inputFile);
			DocumentBuilder dombuilder = domfac.newDocumentBuilder();
			doc = dombuilder.parse(inputStream);

			Element root = doc.getDocumentElement(); // pssstudycase 
			NodeList nodeListInStudyCase = root.getChildNodes();
			Node baseCase = Helper
					.findNode(nodeListInStudyCase, "pss:baseCase");
			Node busList = null;
			Node branchList = null;
			Node areaList = null;
			String basePowerMva = "0.0";
			if (baseCase != null) {
				NodeList nodeListInBaseCase = baseCase.getChildNodes();
				areaList = Helper.findNode(nodeListInBaseCase, "pss:areaList");
				busList = Helper.findNode(nodeListInBaseCase, "pss:busList");
				branchList = Helper.findNode(nodeListInBaseCase,
						"pss:branchList");
				Node basePower = Helper.findNode(nodeListInBaseCase,
						"pss:basePower");
				basePowerMva = basePower.getFirstChild().getNodeValue();
			}

			if (areaList != null) {
				AreaData.processAreaData(areaList, printAreaInterchange,
						printInterareaTransfer, printZone);
			}
			if (busList != null) {

				BusLoadGenData.processBusLoadGenData(busList, basePowerMva,
						printBus, printLoad, printGen, printSwitShunt,
						outputFile);
			}
			if (branchList != null) {
				BranchData.processBranchData(branchList, basePowerMva,
						printBranch, printTrans, outputFile);
			}

			// processing transient data 
			/*doc = dombuilder.parse(transientData);
			root = doc.getDocumentElement(); // pssstudycase 
			nodeListInStudyCase = root.getChildNodes();*/
			Node transientSimulation = Helper.findNode(nodeListInStudyCase,
					"pss:transientSimlation");

			Node dynamicDataList = null;
			Node busDynamicDataList = null;
			Node sequenceDataList = null;
			Node generatorDataList = null;
			Node exciterDataList = null;
			Node turbineGovernorDataList = null;
			Node stabilizerDataList = null;
			if (transientSimulation != null) {
				NodeList nodeListInTransient = transientSimulation
						.getChildNodes();
				dynamicDataList = Helper.findNode(nodeListInTransient,
						"pss:dynamicDataList");
				if (dynamicDataList != null) {
					NodeList nodeListInDynamic = dynamicDataList
							.getChildNodes();
					busDynamicDataList = Helper.findNode(nodeListInDynamic,
							"pss:busDynDataList");
					sequenceDataList = Helper.findNode(nodeListInDynamic,
							"pss:sequenceDataList");
				}
			}
			if (busDynamicDataList != null) {
				NodeList nodeListInBusDyn = busDynamicDataList.getChildNodes();
				generatorDataList = Helper.findNode(nodeListInBusDyn,
						"pss:generatorDataList");
				exciterDataList = Helper.findNode(nodeListInBusDyn,
						"pss:exciterDataList");
				turbineGovernorDataList = Helper.findNode(nodeListInBusDyn,
						"pss:turbineGovernorDataList");
				stabilizerDataList = Helper.findNode(nodeListInBusDyn,
						"pss:stabilizerDataList");
				turbineGovernorDataList = Helper.findNode(nodeListInBusDyn,
						"pss:turbineGovernorDataList");
			}
			if (generatorDataList != null) {
				GeneratorData.processGeneratorData(generatorDataList,
						printGenerator, outputFile);
			}
			if (exciterDataList != null) {
				ExciterData.processExciterData(exciterDataList, printExciter,
						outputFile);
			}
			if (turbineGovernorDataList != null) {
				TurbineGovernorData.processTGData(turbineGovernorDataList,
						printTG, outputFile);
			}
			if (stabilizerDataList != null) {
				PSSData
						.processPSSData(stabilizerDataList, printPSS,
								outputFile);
			}

			// process sequence data
			if (sequenceDataList != null) {
				SequenceData.processSequenceData(sequenceDataList, outputFile);
			}

			// output the final integered result;
			OutputResult.IntegerResult(outputFile);

		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

}
