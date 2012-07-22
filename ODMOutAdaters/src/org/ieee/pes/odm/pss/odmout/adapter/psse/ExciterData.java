package org.ieee.pes.odm.pss.odmout.adapter.psse;

/*
 * @(#)Exciter.java   
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
import java.io.File;
import java.io.PrintStream;

import org.w3c.dom.Node;

public class ExciterData {
	public static void processExciterData(Node exciterDataList,
			PrintStream printExciter, String outputFile) {
		final String[] excStr = new String[25];
		for (Node exciter = exciterDataList.getFirstChild(); exciter != null; exciter = exciter
				.getNextSibling()) {
			String excType = "";
			for (Node nodeInExc = exciter.getFirstChild(); nodeInExc != null; nodeInExc = nodeInExc
					.getNextSibling()) {
				String nodeName = nodeInExc.getNodeName();
				if (nodeInExc.getNodeType() == Node.ELEMENT_NODE) {
					if (nodeName.equals("pss:locatedBus")) {
						Node busName = nodeInExc.getFirstChild();
						try {
							String name = Helper.getValue(busName);
							String str = "'" + name + "'";
							File file = new File(outputFile+"bus.txt");
							excStr[0] = Helper.getBusId(str, file);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else if (nodeName.equals("pss:excId")) {
						excStr[2] = Helper.getValue(nodeInExc.getFirstChild());
					} else if (nodeName.equals("pss:exciterType")) {
						excType = Helper.getValue(nodeInExc);
						if (excType.equals("IEEE1968Type1")) {
							excStr[1] = "'" + "IEEET1" + "'";
						} else if (excType.equals("IEEETypeDC2")) {
							excStr[1] = "'" + "EXDC2" + "'";
						} else if (excType.equals("IEEE1981ST1")) {
							excStr[1] = "'" + "EXST1" + "'";
						} else if (excType.equals("BPAFJ")) {
							excStr[1] = "'" + "    " + "'";// TODO fJ
						} else if (excType.equals("IEEE1981NewExcSystem")) { // FQ
																				// FV
																				// TODO
							excStr[1] = "'" + "  " + "'";
						} else if (excType.equals("IEEE1981TypeAC2")) {
							excStr[1] = "'" + "EXAC2" + "'";
						}
					} else if (nodeName.equals("pss:exciterModel")) {
						Node model = nodeInExc.getFirstChild();// pss:xxxModel
						String modelName = model.getNodeName();
						if (modelName.equals("pss:IEEE1968Type1")) {
							for (Node node = model.getFirstChild(); node != null; node = node
									.getNextSibling()) {
								String name = node.getNodeName();
								excStr[12] = "0"; // swithc ==0
								//excStr[7] = "0"; // 
								if (name.equals("pss:TR")) {
									excStr[3] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:KA")) {
									excStr[4] = Helper.getValue(node
											.getFirstChild());									
								} else if (name.equals("pss:TA1")) {
									//excStr[5] = Helper.getValue(node
									//		.getFirstChild());
								} else if (name.equals("pss:TA")) {// TB
									 excStr[5]=Helper.getValue(node.getFirstChild());
									//excStr[6] = "1.0";
								} else if (name.equals("pss:VRMAX")) {
									excStr[6] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:VRMIN")) {
									// excStr[9]=Helper.getValue(node.getFirstChild());
									excStr[7] = "-0.0";
								} else if (name.equals("pss:KE")) {
									excStr[8] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:TE")) {
									excStr[9] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:KF")) {
									excStr[10] = Helper.getValue(node
											.getFirstChild());
									
								} else if (name.equals("pss:TF")) {
									excStr[11] = Helper.getValue(node
											.getFirstChild());									
								} else if (name.equals("pss:E1")) {
									excStr[13] = Helper.getValue(node);
								} else if (name.equals("pss:SE1")) {
									excStr[14] = Helper.getValue(node);
								} else if (name.equals("pss:E2")) {
									excStr[15] = Helper.getValue(node);
								} else if (name.equals("pss:SE2")) {
									excStr[16] = Helper.getValue(node);
								}
							}

						} else if (modelName.equals("pss:IEEETypeDC2")) {
							for (Node node = model.getFirstChild(); node != null; node = node
									.getNextSibling()) {
								String name = node.getNodeName();
								excStr[14] = "0";
								if (name.equals("pss:TR")) {
									excStr[3] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:KA")) {
									excStr[4] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:TA")) {
									excStr[5] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:TB")) {
									excStr[6] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:TC")) {
									excStr[7] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:VRMAX")) {
									excStr[8] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:VRMIN")) {
									excStr[9] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:KE")) {
									excStr[10] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:TE")) {
									excStr[11] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:KF")) {
									excStr[12] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:TF")) {
									excStr[13] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:E1")) {
									excStr[15] = Helper.getValue(node);
								} else if (name.equals("pss:SE1")) {
									excStr[16] = Helper.getValue(node);
								} else if (name.equals("pss:E2")) {
									excStr[17] = Helper.getValue(node);
								} else if (name.equals("pss:SE2")) {
									excStr[18] = Helper.getValue(node);
								}
							}

						} else if (modelName.equals("pss:IEEE1981ST1")) {
							for (Node node = model.getFirstChild(); node != null; node = node
									.getNextSibling()) {
								String name = node.getNodeName();

								if (name.equals("pss:TR")) {
									excStr[3] = Helper.getValue(node
											.getFirstChild());
									excStr[3] = "0.05";
								} else if (name.equals("pss:VIMAX")) {
									excStr[4] = Helper.getValue(node
											.getFirstChild());
									double k = new Double(excStr[4])
											.doubleValue();
									if (k >= 0.2) {
										k = 0.2;
									}
									excStr[4] = Double.toString(k);

								} else if (name.equals("pss:VIMIN")) {
									// excStr[5]=Helper.getValue(node.getFirstChild());
									excStr[5] = "-" + excStr[4];
								} else if (name.equals("pss:TC")) {
									excStr[6] = Helper.getValue(node
											.getFirstChild());
									excStr[6] = "2";
								} else if (name.equals("pss:TB")) {
									excStr[7] = Helper.getValue(node
											.getFirstChild());
									excStr[7] = "18";
								} else if (name.equals("pss:KA")) {
									excStr[8] = Helper.getValue(node
											.getFirstChild());
									excStr[8] = "55";
								} else if (name.equals("pss:TA")) {
									excStr[9] = Helper.getValue(node
											.getFirstChild());
									excStr[9] = "0.25";
								} else if (name.equals("pss:VRMAX")) {
									excStr[10] = Helper.getValue(node
											.getFirstChild());
									excStr[10] = "5";
								} else if (name.equals("pss:VRMIN")) {
									excStr[11] = Helper.getValue(node
											.getFirstChild());
									excStr[11] = "-5";
								} else if (name.equals("pss:KC")) {
									excStr[12] = Helper.getValue(node
											.getFirstChild());
									excStr[12] = "0.2";
								} else if (name.equals("pss:KF")) {
									excStr[13] = Helper.getValue(node
											.getFirstChild());
									excStr[13] = "0.2";
								} else if (name.equals("pss:TF")) {
									excStr[14] = Helper.getValue(node
											.getFirstChild());
									excStr[14] = "1.0";
								}
							}
						} else if (modelName.equals("pss:BPAFJ")) {
							System.out
									.println("Exciter type FJ is not proceesed, bus Id is "
											+ excStr[0]);
						} else if (modelName.equals("pss:IEEE1981NewExcSystem")) {

						} else if (modelName.equals("pss:IEEE1981TypeAC2")) {
							for (Node node = model.getFirstChild(); node != null; node = node
									.getNextSibling()) {
								String name = node.getNodeName();

								if (name.equals("pss:TR")) {
									excStr[3] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:TB")) {
									excStr[4] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:TC")) {
									excStr[5] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:KA")) {
									excStr[6] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:TA")) {
									excStr[7] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:VAMAX")) {
									excStr[8] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:VAMIN")) {
									excStr[9] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:KB")) {
									excStr[10] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:VRMAX")) {
									excStr[11] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:VRMIN")) {
									excStr[12] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:TE")) {
									excStr[13] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:KL")) {
									excStr[14] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:KH")) {
									excStr[15] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:KF")) {
									excStr[16] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:KC")) {
									excStr[17] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:KD")) {
									excStr[18] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:KE")) {
									excStr[19] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:VLR")) {
									excStr[20] = Helper.getValue(node
											.getFirstChild());
								} else if (name.equals("pss:E1")) {
									excStr[21] = Helper.getValue(node);
								} else if (name.equals("pss:SE1")) {
									excStr[22] = Helper.getValue(node);
								} else if (name.equals("pss:E2")) {
									excStr[23] = Helper.getValue(node);
								} else if (name.equals("pss:SE2")) {
									excStr[24] = Helper.getValue(node);
								}
							}
						}
					}
				}
			}

			//print out put
			if (excType.equals("IEEE1968Type1")) {
				for (int i = 0; i <= 16; i++) {
					printExciter.print(excStr[i] + "   ");
					if (i == 6 || i == 11) {
						printExciter.println("     ");
					} else if (i == 16) {
						printExciter.println("/");
					}
				}

			} else if (excType.equals("IEEETypeDC2")) {
				for (int i = 0; i <= 18; i++) {
					printExciter.print(excStr[i] + "   ");
					if (i == 6 || i == 11 || i == 16) {
						printExciter.println("     ");
					} else if (i == 18) {
						printExciter.println("/");
					}
				}
			} else if (excType.equals("IEEE1981ST1")) {
				for (int i = 0; i <= 14; i++) {
					printExciter.print(excStr[i] + "   ");
					if (i == 6 || i == 11) {
						printExciter.println("     ");
					} else if (i == 14) {
						printExciter.println("/");
					}
				}
			} else if (excType.equals("IEEE1981TypeAC2")) {
				for (int i = 0; i <= 24; i++) {
					printExciter.print(excStr[i] + "   ");
					if (i == 6 || i == 11 || i == 16 || i == 21) {
						printExciter.println("     ");
					} else if (i == 24) {
						printExciter.println("/");
					}
				}
			}
		}
	}
}
