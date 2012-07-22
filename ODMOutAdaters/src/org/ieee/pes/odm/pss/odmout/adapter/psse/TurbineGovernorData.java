package org.ieee.pes.odm.pss.odmout.adapter.psse;
/*
 * @(#)TurbinGovernorData.java   
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
 * @Author Stephen Hou
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

public class TurbineGovernorData {
	public static void processTGData(Node turbineGovernorDataList,
			PrintStream printTG, String outputFile) {
		final String[] tgStr = new String[25];
		for (Node tg = turbineGovernorDataList.getFirstChild(); tg != null; tg = tg
				.getNextSibling()) {
			String tgType = "";
			for (Node nodeInTG = tg.getFirstChild(); nodeInTG != null; nodeInTG = nodeInTG
					.getNextSibling()) {
				String nodeName = nodeInTG.getNodeName();
				if (nodeInTG.getNodeType() == Node.ELEMENT_NODE) {
					if (nodeName.equals("pss:locatedBus")) {
						Node busName = nodeInTG.getFirstChild();
						try {
							String name = Helper.getValue(busName);
							String str = "'" + name + "'";
							File file = new File(outputFile+"bus.txt");
							tgStr[0] = Helper.getBusId(str, file);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else if (nodeName.equals("pss:tgId")) {
						tgStr[2] = Helper.getValue(nodeInTG.getFirstChild());
					} else if (nodeName.equals("pss:turbineGovernorType")) {
						tgType = Helper.getValue(nodeInTG);						
						if (tgType.equals("hydroGoverner")) {
							tgStr[1] = "'" + "TGOV3" + "'";
						} else if (tgType.equals("IEEE1981Type3")) {
							tgStr[1] = "'" + "IEEEG3" + "'";
						} else if (tgType.equals("hydroStreamGeneralModel")) {
							tgStr[1] = "'" + "IEEESGO" + "'";
						}
					} else if (nodeName.equals("pss:turbineGovernorModel")) {
						if (tgType.equals("hydroGoverner")) {
							tgStr[3] = "0";
							Node hydroGov = nodeInTG.getFirstChild();
							for (Node nodeInHydro = hydroGov.getFirstChild(); nodeInHydro != null; nodeInHydro = nodeInHydro
									.getNextSibling()) {
								String hydroName = nodeInHydro.getNodeName();
								if (hydroName.equals("pss:PMAX")) {
									// tgStr[9]=Helper.setFormat(Helper.getValue(nodeInHydro.getFirstChild()));
									tgStr[9] = "1.0";
								} else if (hydroName.equals("pss:PMIN")) {
									tgStr[10] = Helper.setFormat(Helper
											.getValue(nodeInHydro
													.getFirstChild()));
								} else if (hydroName.equals("pss:R")) {
									String r = Helper.getValue(nodeInHydro
											.getFirstChild());
									double R = new Double(r).doubleValue();
									double pmax = new Double(tgStr[9])
											.doubleValue();
									double k = pmax / R;
									String K = Double.toString(k);
									tgStr[3] = K;
								} else if (hydroName.equals("pss:T1")) {
									tgStr[4] = Helper.getValue(nodeInHydro
											.getFirstChild());
								} else if (hydroName.equals("pss:T2")) {
									tgStr[5] = Helper.getValue(nodeInHydro
											.getFirstChild());
								} else if (hydroName.equals("pss:T3")) {
									tgStr[6] = Helper.getValue(nodeInHydro
											.getFirstChild());
								} else if (hydroName.equals("pss:U0")) {
									// tgStr[7]=Helper.getValue(nodeInHydro.getFirstChild());
									tgStr[7] = "0.3";
								} else if (hydroName.equals("pss:UC")) {
									// tgStr[8]=Helper.getValue(nodeInHydro.getFirstChild());
									tgStr[8] = "-0.3";
								} else if (hydroName.equals("pss:turbine")) {
									Node turbineType = nodeInHydro
											.getFirstChild();// here only TB
																// is taken into
																// consideration
									String streamTur = turbineType
											.getNodeName();
									if (streamTur.equals("pss:steamTurbine")) {
										for (Node nodeInStream = turbineType
												.getFirstChild(); nodeInStream != null; nodeInStream = nodeInStream
												.getNextSibling()) {
											String name1 = nodeInStream
													.getNodeName();
											if (name1.equals("pss:TCH")) {// T4
												tgStr[11] = Helper
														.getValue(nodeInStream
																.getFirstChild());
											} else if (name1.equals("pss:FHP")) {// K1
												tgStr[12] = Helper
														.getValue(nodeInStream);
											} else if (name1.equals("pss:TRH")) {// T5
												// tgStr[13]=Helper.getValue(nodeInStream.getFirstChild());
												tgStr[13] = "9.0";
											} else if (name1.equals("pss:FIP")) {// K2
												// tgStr[14]=Helper.getValue(nodeInStream);
												tgStr[14] = "0.45";
											} else if (name1.equals("pss:TCO")) {// T6
												tgStr[15] = Helper
														.getValue(nodeInStream
																.getFirstChild());
											} else if (name1.equals("pss:FLP")) {// K3
												tgStr[16] = Helper
														.getValue(nodeInStream);
											}
											tgStr[17] = "0.0.20";// TA
											tgStr[18] = "0.45";// Tb
											tgStr[19] = "45";// Tc
											tgStr[20] = "1.0";// PRMAX
										}
									}

								}
							}
						} else if (tgType.equals("IEEE1981Type3")) {
							Node HGAT = nodeInTG.getFirstChild();
							for (Node nodeInHGAT = HGAT.getFirstChild(); nodeInHGAT != null; nodeInHGAT = nodeInHGAT
									.getNextSibling()) {
								String name2 = nodeInHGAT.getNodeName();
								if (name2.equals("pss:TG")) {
									tgStr[3] = Helper.getValue(nodeInHGAT
											.getFirstChild());
								} else if (name2.equals("pss:TP")) {
									tgStr[4] = Helper.getValue(nodeInHGAT
											.getFirstChild());
								} else if (name2.equals("pss:PMAX")) {									
									String pmax = Helper.getValue(nodeInHGAT);
									
									tgStr[7] = Helper.setFormat(pmax);
									tgStr[8] = "0";// PMIN=0;
								} else if (name2.equals("pss:U0")) {
									String U0 = Helper.getValue(nodeInHGAT);	
									tgStr[5] = Helper.setFormat(U0);
								} else if (name2.equals("pss:UC")) {
									String uc = Helper.getValue(nodeInHGAT);									
									
									tgStr[6] = Helper.setFormat(uc);
								} else if (name2.equals("pss:SIGMA")) {
									tgStr[9] = Helper.getValue(nodeInHGAT);
								} else if (name2.equals("pss:DELTA")) {
									tgStr[10] = Helper.getValue(nodeInHGAT);
								} else if (name2.equals("pss:TR")) {
									tgStr[11] = Helper.getValue(nodeInHGAT
											.getFirstChild());
								} else if (name2.equals("pss:TW")) {
									String TW = Helper.getValue(nodeInHGAT
											.getFirstChild());									
									tgStr[12] = Helper.setFormat(TW);
									tgStr[13] = "0.5";// a11
									tgStr[14] = "1.0";// a13
									tgStr[15] = "1.5";// a21
									tgStr[16] = "1";// a23
								}
								
							}
						} else if (tgType.equals("hydroStreamGeneralModel")) {
							//TODO: add GG 
						}
					}
				}
			}
			// out put
			if (tgStr[2] == null) {
				tgStr[2] = "1";
			}
			if (tgType.equals("hydroGoverner")) {
				for (int i = 0; i <= 20; i++) {

					printTG.print(tgStr[i] + "   ");
					System.out.println(tgStr[i]);
					if (i == 6 || i == 11) {
						printTG.println("     ");
					} else if (i == 20) {
						printTG.println("/");
					}
				}
			} else if (tgType.equals("IEEE1981Type3")) {
				for (int i = 0; i <= 16; i++) {
					printTG.print(tgStr[i] + "   ");
					if (i == 6 || i == 11) {
						printTG.println("     ");
					} else if (i == 16) {
						printTG.println("/");
					}
				}
			}
		}

	}
}
