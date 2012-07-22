import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.w3c.dom.Node;

public class SequenceData {

	public static void processSequenceData(Node sequenceDataList,
			String outputFile) {
		try {
			OutputStream outPosGen = new FileOutputStream(outputFile
					+ "posGen.txt");
			OutputStream outNegGen = new FileOutputStream(outputFile
					+ "NegGen.txt");
			OutputStream outZerGen = new FileOutputStream(outputFile
					+ "ZerGen.txt");
			OutputStream outNegLoad = new FileOutputStream(outputFile
					+ "NegLoad.txt");
			OutputStream outZerLoad = new FileOutputStream(outputFile
					+ "ZerLoad.txt");
			OutputStream outZerLine = new FileOutputStream(outputFile
					+ "ZerLine.txt");
			OutputStream outZerXfr = new FileOutputStream(outputFile
					+ "ZerXfr.txt");
			PrintStream printPosGen = new PrintStream(outPosGen);
			PrintStream printNegGen = new PrintStream(outNegGen);
			PrintStream printZerGen = new PrintStream(outZerGen);
			PrintStream printNegLoad = new PrintStream(outNegLoad);
			PrintStream printZerLoad = new PrintStream(outZerLoad);
			PrintStream printZerLine = new PrintStream(outZerLine);
			PrintStream printZerXfr = new PrintStream(outZerXfr);

			final String[] posGenStr = new String[10];
			final String[] negGenStr = new String[10];
			final String[] negLoadStr = new String[10];
			final String[] zerGenStr = new String[10];
			final String[] zerLoadStr = new String[10];
			final String[] zerLineStr = new String[10];
			final String[] zerXfrStr = new String[13];
			for (Node nodeInSeq = sequenceDataList.getFirstChild(); nodeInSeq != null; nodeInSeq = nodeInSeq
					.getNextSibling()) {
				String nodeName = nodeInSeq.getNodeName();
				if (nodeName.equals("pss:postiveSequenceDataList")) {
					for (Node posGenList = nodeInSeq.getFirstChild(); posGenList != null; posGenList = posGenList
							.getNextSibling()) {
						for (Node posGen = posGenList.getFirstChild(); posGen != null; posGen = posGen
								.getNextSibling()) {
							for (Node nodeInPosGen = posGen.getFirstChild(); nodeInPosGen != null; nodeInPosGen = nodeInPosGen
									.getNextSibling()) {
								String name = nodeInPosGen.getNodeName();
								if (name.equals("pss:busId")) {
									Node busName = nodeInPosGen.getFirstChild();
									try {
										String busname = Helper
												.getValue(busName);
										if (busname.equals("BJZ500")) {
											posGenStr[0] = "162";
										} else {
											String str = "'" + busname + "'";
											File file = new File(outputFile +"bus.txt");
											posGenStr[0] = Helper.getBusId(str,
													file);
										}
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} else if (name.equals("pss:macId")) {
									Node macId = nodeInPosGen.getFirstChild();
									posGenStr[1] = Helper.getValue(macId);
								} else if (name.equals("pss:ZRPos")) {
									Node zr = nodeInPosGen.getFirstChild();
									posGenStr[2] = Helper.getValue(zr);
								} else if (name.equals("pss:ZXPos")) {
									Node zx = nodeInPosGen.getFirstChild();
									posGenStr[3] = Helper.getValue(zx);
								}
							}
							for (int i = 0; i <= 3; i++) {
								if (i == 1) {
									printPosGen.print("\'" + posGenStr[i]
											+ "\'" + ",  ");
								} else if (i == 3) {
									printPosGen.println(posGenStr[i]);
								} else {
									printPosGen.print(posGenStr[i] + ",  ");
								}
							}
						}

					}

				} else if (nodeName.equals("pss:negativeSequenceDataList")) {
					for (Node nodeInNegList = nodeInSeq.getFirstChild(); nodeInNegList != null; nodeInNegList = nodeInNegList
							.getNextSibling()) {
						String typeName = nodeInNegList.getNodeName();
						if (typeName.equals("pss:generatorNegativeList")) {
							for (Node negGen = nodeInNegList.getFirstChild(); negGen != null; negGen = negGen
									.getNextSibling()) {
								for (Node nodeInNegGen = negGen.getFirstChild(); nodeInNegGen != null; nodeInNegGen = nodeInNegGen
										.getNextSibling()) {
									String elementName = nodeInNegGen
											.getNodeName();
									if (elementName.equals("pss:busId")) {
										Node busName = nodeInNegGen
												.getFirstChild();
										try {
											String busname = Helper
													.getValue(busName);
											if (busname.equals("BJZ500")) {
												negGenStr[0] = "162";
											} else {
												String str = "'" + busname
														+ "'";
												File file = new File(outputFile +"bus.txt");
												negGenStr[0] = Helper.getBusId(
														str, file);
											}
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									} else if (elementName.equals("pss:macId")) {
										Node macId = nodeInNegGen
												.getFirstChild();
										negGenStr[1] = Helper.getValue(macId);
									} else if (elementName.equals("pss:ZXNeg")) {
										Node zx = nodeInNegGen.getFirstChild();
										negGenStr[2] = "0";
										negGenStr[3] = Helper.getValue(zx);
									}
								}
								for (int i = 0; i <= 3; i++) {
									if (i == 1) {
										printNegGen.print("\'" + negGenStr[i]
												+ "\'" + ",  ");
									} else if (i == 3) {
										printNegGen.println(negGenStr[i]);
									} else {
										printNegGen.print(negGenStr[i] + ",  ");
									}
								}
							}

						} else if (typeName.equals("pss:shuntLoadNegativeList")) {
							for (Node negLoad = nodeInNegList.getFirstChild(); negLoad != null; negLoad = negLoad
									.getNextSibling()) {
								for (Node nodeInNegLoad = negLoad
										.getFirstChild(); nodeInNegLoad != null; nodeInNegLoad = nodeInNegLoad
										.getNextSibling()) {
									String elementName = nodeInNegLoad
											.getNodeName();
									if (elementName.equals("pss:loadLocation")) {
										String location = Helper
												.getValue(nodeInNegLoad);
										if (!location.equals("at_bus")) {
											System.out
													.println("This shunt is not located at bus,  line +");
										}
									} else if (elementName
											.equals("pss:locationId")) {
										Node busName = nodeInNegLoad
												.getFirstChild();
										try {
											String busname = Helper
													.getValue(busName);
											String str = "'" + busname + "'";
											File file = new File(outputFile +"bus.txt");
											negLoadStr[0] = Helper.getBusId(
													str, file);
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									} else if (elementName.equals("pss:rNeg")) {
										Node rNeg = nodeInNegLoad
												.getFirstChild();
										negLoadStr[1] = Helper.getValue(rNeg);
									} else if (elementName.equals("pss:xNeg")) {
										Node xNeg = nodeInNegLoad
												.getFirstChild();
										negLoadStr[2] = Helper.getValue(xNeg);
									}
								}
								for (int i = 0; i <= 2; i++) {
									if (i == 2) {
										printNegLoad.println(negLoadStr[i]);
									} else {
										printNegLoad.print(negLoadStr[i]
												+ ",  ");
									}
								}
							}
						}
					}

				} else if (nodeName.equals("pss:zeroSequenceDataList")) {
					for (Node nodeInZerList = nodeInSeq.getFirstChild(); nodeInZerList != null; nodeInZerList = nodeInZerList
							.getNextSibling()) {
						String typeName = nodeInZerList.getNodeName();
						boolean hasZeroLine = false;
						if (typeName.equals("pss:generatorZeroList")) {
							for (Node zerGen = nodeInZerList.getFirstChild(); zerGen != null; zerGen = zerGen
									.getNextSibling()) {
								for (Node nodeInZerGen = zerGen.getFirstChild(); nodeInZerGen != null; nodeInZerGen = nodeInZerGen
										.getNextSibling()) {
									String elementName = nodeInZerGen
											.getNodeName();
									if (elementName.equals("pss:busId")) {
										Node busName = nodeInZerGen
												.getFirstChild();
										try {
											String busname = Helper
													.getValue(busName);
											if (busname.equals("BJZ500")) {
												zerGenStr[0] = "162";
											} else {
												String str = "'" + busname
														+ "'";
												File file = new File(outputFile +"bus.txt");
												zerGenStr[0] = Helper.getBusId(
														str, file);
											}
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									} else if (elementName.equals("pss:macId")) {
										Node macId = nodeInZerGen
												.getFirstChild();
										zerGenStr[1] = Helper.getValue(macId);
									} else if (elementName.equals("pss:ZRZer")) {
										Node zr = nodeInZerGen.getFirstChild();
										zerGenStr[2] = Helper.getValue(zr);
									} else if (elementName.equals("pss:ZXZer")) {
										Node zx = nodeInZerGen.getFirstChild();
										zerGenStr[3] = Helper.getValue(zx);
									}
								}
								for (int i = 0; i <= 3; i++) {
									if (i == 1) {
										printZerGen.print("\'" + zerGenStr[i]
												+ "\'" + ",  ");
									} else if (i == 3) {
										printZerGen.println(zerGenStr[i]);
									} else {
										printZerGen.print(zerGenStr[i] + ",  ");
									}
								}
							}
						} else if (typeName.equals("pss:shuntLoadZeroList")) {
							for (Node zerLoad = nodeInZerList.getFirstChild(); zerLoad != null; zerLoad = zerLoad
									.getNextSibling()) {
								for (Node nodeInZerLoad = zerLoad
										.getFirstChild(); nodeInZerLoad != null; nodeInZerLoad = nodeInZerLoad
										.getNextSibling()) {
									String elementName = nodeInZerLoad
											.getNodeName();
									if (elementName.equals("pss:busId")) {
										Node busName = nodeInZerLoad
												.getFirstChild();
										try {
											String busname = Helper
													.getValue(busName);
											String str = "'" + busname + "'";
											File file = new File(outputFile +"bus.txt");
											zerLoadStr[0] = Helper.getBusId(
													str, file);
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									} else if (elementName.equals("pss:rZer")) {
										Node rNeg = nodeInZerLoad
												.getFirstChild();
										zerLoadStr[1] = Helper.getValue(rNeg);
									} else if (elementName.equals("pss:xZer")) {
										Node xNeg = nodeInZerLoad
												.getFirstChild();
										zerLoadStr[2] = Helper.getValue(xNeg);
									}
								}
								for (int i = 0; i <= 2; i++) {
									if (i == 1) {
										printZerLoad.print("\'" + zerLoadStr[i]
												+ "\'" + ",  ");
									} else if (i == 2) {
										printZerLoad.println(zerLoadStr[i]);
									} else {
										printZerLoad.print(zerLoadStr[i]
												+ ",  ");
									}
								}
							}
						} else if (typeName.equals("pss:lineZeroList")) {
							hasZeroLine = true;
							for (Node zerLine = nodeInZerList.getFirstChild(); zerLine != null; zerLine = zerLine
									.getNextSibling()) {
								for (Node nodeInZerLine = zerLine
										.getFirstChild(); nodeInZerLine != null; nodeInZerLine = nodeInZerLine
										.getNextSibling()) {
									String elementName = nodeInZerLine
											.getNodeName();
									if (elementName.equals("pss:fBusId")) {
										Node busName = nodeInZerLine
												.getFirstChild();
										try {
											String busname = Helper
													.getValue(busName);
											String str = "'" + busname + "'";
											File file = new File(outputFile +"bus.txt");
											zerLineStr[0] = Helper.getBusId(
													str, file);
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									} else if (elementName.equals("pss:tBusId")) {
										Node busName = nodeInZerLine
												.getFirstChild();
										try {
											String busname = Helper
													.getValue(busName);
											String str = "'" + busname + "'";
											File file = new File(outputFile +"bus.txt");
											zerLineStr[1] = Helper.getBusId(
													str, file);
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									} else if (elementName.equals("pss:cirId")) {
										zerLineStr[2] = Helper
												.getValue(nodeInZerLine);
									} else if (elementName
											.equals("pss:rLineZer")) {
										Node rLineZer = nodeInZerLine
												.getFirstChild();
										zerLineStr[3] = Helper
												.getValue(rLineZer);
									} else if (elementName
											.equals("pss:xLineZer")) {
										Node xLineZer = nodeInZerLine
												.getFirstChild();
										zerLineStr[4] = Helper
												.getValue(xLineZer);
									} else if (elementName.equals("pss:gfZer")) {
										Node gfZer = nodeInZerLine
												.getFirstChild();
										zerLineStr[6] = Helper.getValue(gfZer);
									} else if (elementName.equals("pss:bfZer")) {
										Node bfZer = nodeInZerLine
												.getFirstChild();
										zerLineStr[7] = Helper.getValue(bfZer);
									} else if (elementName.equals("pss:gtZer")) {
										Node gtZer = nodeInZerLine
												.getFirstChild();
										zerLineStr[8] = Helper.getValue(gtZer);
									} else if (elementName.equals("pss:btZer")) {
										Node btZer = nodeInZerLine
												.getFirstChild();
										zerLineStr[9] = Helper.getValue(btZer);
									}
									zerLineStr[5] = "0.0";
								}
								for (int i = 0; i <= 9; i++) {
									if (i == 3) {
										printZerLine.print("\'" + zerLineStr[i]
												+ "\'" + ",  ");
									} else if (i == 9) {
										printZerLine.println(zerLineStr[i]);
									} else {
										printZerLine.print(zerLineStr[i]
												+ ",  ");
									}
								}
							}
						} else if (typeName.equals("pss:xfrZeroList")) {
							for (Node zerXfr = nodeInZerList.getFirstChild(); zerXfr != null; zerXfr = zerXfr
									.getNextSibling()) {
								for (Node nodeInZerXfr = zerXfr.getFirstChild(); nodeInZerXfr != null; nodeInZerXfr = nodeInZerXfr
										.getNextSibling()) {
									String elementName = nodeInZerXfr
											.getNodeName();
									if (elementName.equals("pss:busI")) {
										Node busName = nodeInZerXfr
												.getFirstChild();
										try {
											String busname = Helper
													.getValue(busName);
											String str = "'" + busname + "'";
											File file = new File(outputFile +"bus.txt");
											zerXfrStr[0] = Helper.getBusId(str,
													file);
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									} else if (elementName.equals("pss:busJ")) {
										Node busName = nodeInZerXfr
												.getFirstChild();
										try {
											String busname = Helper
													.getValue(busName);
											String str = "'" + busname + "'";
											File file = new File(outputFile +"bus.txt");
											zerXfrStr[1] = Helper.getBusId(str,
													file);
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										zerXfrStr[2] = "0";
									} else if (elementName.equals("pss:cirId")) {
										zerXfrStr[3] = Helper
												.getValue(nodeInZerXfr);
									} else if (elementName
											.equals("pss:conectionLocation")) {
										String location = Helper
												.getValue(nodeInZerXfr);
										if (location
												.equals("between_bus1_and_bus_2")) {
											zerXfrStr[4] = "1";
										} else if (location.equals("at_bus1")) {
											zerXfrStr[4] = "2";
										} else if (location.equals("at_bus2")) {
											zerXfrStr[4] = "3";
										}
									} else if (elementName.equals("pss:X1")) {
										Node x1 = nodeInZerXfr.getFirstChild();
										zerXfrStr[8] = Helper.getValue(x1);
									}
									zerXfrStr[5] = zerXfrStr[6] = zerXfrStr[7] = zerXfrStr[9] = zerXfrStr[10] = zerXfrStr[11] = zerXfrStr[12] = "0.0";
								}
								for (int i = 0; i <= 12; i++) {
									if (i == 1) {
										printZerXfr.print("\'" + zerXfrStr[i]
												+ "\'" + ",  ");
									} else if (i == 12) {
										printZerXfr.println(zerXfrStr[i]);
									} else {
										printZerXfr.print(zerXfrStr[i] + ",  ");
									}
								}
							}
						}
						if (hasZeroLine == false) {
							try {
								File file = new File(outputFile +"branch.txt");
								String[] strZer = Helper.getZeroLine(file);
								zerLineStr[0] = strZer[0];
								zerLineStr[1] = strZer[1];
								zerLineStr[2] = strZer[2];
								zerLineStr[3] = strZer[3];//r1
								zerLineStr[4] = strZer[4];//x1
								String r1 = zerLineStr[3];
								String x1 = zerLineStr[4];
								double R1 = new Double(r1).doubleValue();
								double X1 = new Double(x1).doubleValue();
								double c = R1 * R1 - X1 * X1;
								String C = Helper.setFormat(Double.toString(c));
								double c1 = new Double(C).doubleValue();
								double a = 0.8 * R1 / c1;
								double b = 0.313 * X1 / c1;
								String A = Helper.setFormat(Double.toString(a));
								String B = Helper.setFormat(Double.toString(b));
								double a1 = new Double(A).doubleValue();
								double b1 = new Double(B).doubleValue();
								double R0 = a1 / (a1 * a1 - b1 * b1);
								double X0 = b1 / (a1 * a1 - b1 * b1);
								String r0 = Double.toString(R0);
								String x0 = Double.toString(X0);
								zerLineStr[3] = r0;//r0
								zerLineStr[4] = x0;//x0
								//zerXfrStr[5]=strZer[5];
								zerLineStr[5] = "0.0";
								zerLineStr[6] = zerLineStr[7] = zerLineStr[8] = zerLineStr[9] = "0";
								for (int i = 0; i <= 9; i++) {
									if (i == 3) {
										printZerLine.print("\'" + zerLineStr[i]
												+ "\'" + ",  ");
									} else if (i == 9) {
										printZerLine.println(zerLineStr[i]);
									} else {
										printZerLine.print(zerLineStr[i]
												+ ",  ");
									}
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
