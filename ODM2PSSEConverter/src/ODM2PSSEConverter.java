
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ODM2PSSEConverter {

	public ODM2PSSEConverter() {

		/*String inputFile_one = "testdata/07c_loadflow_1222.xml"; //loadflow data
		String inputFile_two = "testdata/07c_transient_1222.xml"; // transient data
*/		String inputFile_one = "testdata/9bus_loadflow.xml"; //loadflow data
		String inputFile_two = "testdata/9bus_transient.xml"; // transient data
		//String inputFile_one = "testdata/39bus_loadflow.xml"; //loadflow data
		//String inputFile_two = "testdata/39bus_transient.xml"; // transient data
		String outputFile = "testdata/output/";

		//get DOM new instance
		DocumentBuilderFactory domfac = DocumentBuilderFactory.newInstance();
		//get javax.xml.parsers.DocumentBuilderFactory;
		try {
			DocumentBuilder dombuilder = domfac.newDocumentBuilder();
			InputStream loadflowData = new FileInputStream(inputFile_one); // input load flow data file   
			InputStream transientData = new FileInputStream(inputFile_two);// input transient data file
			OutputStream outAreaInterchange = new FileOutputStream(outputFile
					+ "areaInterchange.txt");
			OutputStream outInterareaTransfer = new FileOutputStream(outputFile
					+ "interareaTransfer.txt");
			OutputStream zoneInformation = new FileOutputStream(outputFile
					+ "zoneInformation.txt");
			OutputStream outBus = new FileOutputStream(outputFile + "bus.txt"); // output file for storing bus data
			OutputStream outLoad = new FileOutputStream(outputFile + "load.txt"); // output file for storing load data
			OutputStream outGen = new FileOutputStream(outputFile + "gen.txt"); // output file for storing gen data
			OutputStream outSwitchShunt = new FileOutputStream(outputFile + "switchShunt.txt"); // output file for storing switch shunt data
			
			OutputStream outBranch = new FileOutputStream(outputFile
					+ "branch.txt"); // for branch data
			OutputStream outTrans = new FileOutputStream(outputFile
					+ "transformer.txt"); // for transformer data
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

			// transient data output
			OutputStream outGenerator = new FileOutputStream(outputFile
					+ "generator.txt");
			PrintStream printGenerator = new PrintStream(outGenerator);
			OutputStream outExciter = new FileOutputStream(outputFile
					+ "exciter.txt");
			PrintStream printExciter = new PrintStream(outExciter);
			OutputStream outPSS = new FileOutputStream(outputFile + "pss.txt");
			PrintStream printPSS = new PrintStream(outPSS);
			OutputStream outTG = new FileOutputStream(outputFile + "tg.txt");
			PrintStream printTG = new PrintStream(outTG);

			//£¨4£©parse XML document;  processing loadflow data
			Document doc = dombuilder.parse(loadflowData);
			//£¨5£©get the root element
			Element root = doc.getDocumentElement(); // pssstudycase 
			NodeList nodeListInStudyCase = root.getChildNodes();
			Node baseCase = findNode(nodeListInStudyCase, "pss:baseCase");
			Node busList = null;
			Node branchList = null;
			Node areaList = null;
			String basePowerMva = "0.0";
			if (baseCase != null) {
				NodeList nodeListInBaseCase = baseCase.getChildNodes();
				areaList = findNode(nodeListInBaseCase, "pss:areaList");
				busList = findNode(nodeListInBaseCase, "pss:busList");
				branchList = findNode(nodeListInBaseCase, "pss:branchList");
				Node basePower = findNode(nodeListInBaseCase, "pss:basePower");
				basePowerMva = basePower.getFirstChild().getNodeValue();
			}

			if (areaList != null) {				
				AreaData.processAreaData(areaList, printAreaInterchange,
						printInterareaTransfer, printZone);
			}
			if (busList != null) {

				BusLoadGenData.processBusLoadGenData(busList, basePowerMva,
						printBus, printLoad, printGen,printSwitShunt,outputFile);
			}
			if (branchList != null) {
				BranchData.processBranchData(branchList, basePowerMva,
						printBranch, printTrans, outputFile);
			}

			// processing transient data 
			doc = dombuilder.parse(transientData);
			root = doc.getDocumentElement(); // pssstudycase 
			nodeListInStudyCase = root.getChildNodes();
			Node transientSimulation = findNode(nodeListInStudyCase,
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
				dynamicDataList = findNode(nodeListInTransient,
						"pss:dynamicDataList");
				if (dynamicDataList != null) {
					NodeList nodeListInDynamic = dynamicDataList
							.getChildNodes();
					busDynamicDataList = findNode(nodeListInDynamic,
							"pss:busDynDataList");
					sequenceDataList = findNode(nodeListInDynamic,
							"pss:sequenceDataList");
				}
			}
			if (busDynamicDataList != null) {
				NodeList nodeListInBusDyn = busDynamicDataList.getChildNodes();
				generatorDataList = findNode(nodeListInBusDyn,
						"pss:generatorDataList");
				exciterDataList = findNode(nodeListInBusDyn,
						"pss:exciterDataList");
				turbineGovernorDataList = findNode(nodeListInBusDyn,
						"pss:turbineGovernorDataList");
				stabilizerDataList = findNode(nodeListInBusDyn,
						"pss:stabilizerDataList");
				turbineGovernorDataList = findNode(nodeListInBusDyn,
						"pss:turbineGovernorDataList");
			}
			if (generatorDataList != null) {
				GeneratorData.processGeneratorData(generatorDataList,
						printGenerator, outputFile);
			}
			if (exciterDataList != null) {
				ExciterData.processExciterData(exciterDataList, printExciter,outputFile);
			}
			if (turbineGovernorDataList != null) {
				TurbineGovernorData.processTGData(turbineGovernorDataList,
						printTG,outputFile);
			}
			if (stabilizerDataList != null) {
				PSSData.processPSSData(stabilizerDataList, printPSS,outputFile);
			}

			// process sequence data
			if (sequenceDataList != null) {
				SequenceData.processSequenceData(sequenceDataList, outputFile);
			}

			// output the final integered result;
			OutputResult.IntegerResult(outputFile);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static Node findNode(NodeList nodeList, String nodeName) {
		Node node = null;
		if (nodeList != null) {
			for (int k = 0; k < nodeList.getLength(); k++) {
				node = nodeList.item(k);
				if (node.getNodeName().equals(nodeName)) {
					return node;
				} else
					node = null;
			}
		}
		return node;
	}

	public static void main(String[] args) {
		new ODM2PSSEConverter();
	}

}
