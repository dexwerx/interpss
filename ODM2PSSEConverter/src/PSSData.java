import java.io.File;
import java.io.PrintStream;

import org.w3c.dom.Node;

public class PSSData {
	public static void processPSSData(Node stabilizerDataList,
			PrintStream printPSS, String outputFile) {
		final String[] pssStr = new String[25];
		for (Node pss = stabilizerDataList.getFirstChild(); pss != null; pss = pss
				.getNextSibling()) {
			String pssType = "";
			for (Node nodeInPSS = pss.getFirstChild(); nodeInPSS != null; nodeInPSS = nodeInPSS
					.getNextSibling()) {
				String nodeName = nodeInPSS.getNodeName();
				if (nodeInPSS.getNodeType() == Node.ELEMENT_NODE) {
					if (nodeName.equals("pss:locatedBus")) {
						Node busName = nodeInPSS.getFirstChild();
						try {
							String name = Helper.getValue(busName);
							String str = "'" + name + "'";
							File file = new File(outputFile+"bus.txt");
							pssStr[0] = Helper.getBusId(str, file);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else if (nodeName.equals("pss:macId")) {
						pssStr[2] = Helper.getValue(nodeInPSS.getFirstChild());
					} else if (nodeName.equals("pss:stabilizerType")) {
						pssType = Helper.getValue(nodeInPSS);
						if (pssType.equals("IEE2ST")) {
							pssStr[1] = "'" + "ST2CUT" + "'";
						} else if (pssType.equals("  ")) {// TODO
							pssStr[1] = "'" + "  " + "'";
						}
					} else if (nodeName.equals("pss:stabilizerModel")) {
						Node model = nodeInPSS.getFirstChild();// pss:xxxModel
						String modelName = model.getNodeName();
						if (modelName.equals("pss:IEE2ST")) {
							for (Node node = model.getFirstChild(); node != null; node = node
									.getNextSibling()) {
								String name = node.getNodeName();
								if (name.equals("pss:firstInputSignal")) {
									String firstIn = Helper.getValue(node);
									if (firstIn.equals("rotor_speed_deviation")) {
										pssStr[3] = "6";
									} else if (firstIn
											.equals("bus_frequency_deviation")) {
										pssStr[3] = "2";
									} else if (firstIn
											.equals("generator_electrical_power")) {
										pssStr[3] = "3";
									} else if (firstIn
											.equals("generator_accelerating_power")) {
										pssStr[3] = "4";
									} else if (firstIn.equals("bus_voltage")) {
										pssStr[3] = "5";
									} else if (firstIn
											.equals("deriavative_of_bus_voltage")) {
										pssStr[3] = "6";
									} else if (firstIn.equals("0")) {
										pssStr[3] = "0";
									}
								} else if (name.equals("pss:secondInputSignal")) {
									String firstIn = Helper.getValue(node);
									if (firstIn.equals("rotor_speed_deviation")) {
										pssStr[5] = "1";
									} else if (firstIn
											.equals("bus_frequency_deviation")) {
										pssStr[5] = "2";
									} else if (firstIn
											.equals("generator_electrical_power")) {
										pssStr[5] = "3";
									} else if (firstIn
											.equals("generator_accelerating_power")) {
										pssStr[5] = "4";
									} else if (firstIn.equals("bus_voltage")) {
										pssStr[5] = "5";
									} else if (firstIn
											.equals("deriavative_of_bus_voltage")) {
										pssStr[5] = "6";
									} else if (firstIn.equals("0")) {
										pssStr[5] = "1";
									}
								} else if (name.equals("pss:firstRemoteBusId")) {
									pssStr[4] = Helper.getValue(node);
									if(pssStr[4].equals("0")){
										pssStr[4]="1";
									}
								} else if (name.equals("pss:secondRemoteBusId")) {
									pssStr[6] = Helper.getValue(node);
									if(pssStr[6].equals("0")){
										pssStr[6]="1";
									}
								} else if (name.equals("pss:K1")) {
									pssStr[7] = Helper.getValue(node);
								} else if (name.equals("pss:K2")) {
									pssStr[8] = Helper.getValue(node);
								} else if (name.equals("pss:T1")) {
									pssStr[9] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:T2")) {
									pssStr[10] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:T3")) {
									pssStr[11] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:T4")) {
									pssStr[12] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:T5")) {
									pssStr[13] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:T6")) {
									pssStr[14] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:T7")) {
									pssStr[15] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:T8")) {
									pssStr[16] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:T9")) {
									pssStr[17] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:T10")) {
									pssStr[18] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:VSMAX")) {
									pssStr[19] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:VSMIN")) {
									pssStr[20] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:VCUTOFF")) {
									pssStr[21] = Helper.getValue(node
											.getFirstChild());
									pssStr[22] = "0";
								}
							}
						}
					}

				}
			}
			// PRINT OUT PUT
			if (pssType.equals("IEE2ST")) {
				for (int i = 0; i <= 22; i++) {
					printPSS.print(pssStr[i] + "   ");
					if (i == 6 || i == 11 || i == 16 || i == 21) {
						printPSS.println("     ");
					} else if (i == 22) {
						printPSS.println("/");
					}
				}

			}
		}
	}

}
