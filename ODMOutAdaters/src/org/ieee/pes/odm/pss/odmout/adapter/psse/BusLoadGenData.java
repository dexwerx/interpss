package org.ieee.pes.odm.pss.odmout.adapter.psse;
/*
 * @(#)BusLoadGenData.java   
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


public class BusLoadGenData {

	public static void processBusLoadGenData(Node busList, String basePowerMva,
			PrintStream printBus, PrintStream printLoad, PrintStream printGen, 
			PrintStream printSwitShunt,String outputFile) {
		// WRITE THE FIRST LINE OF EACH SECTION DATA
		printBus.println("0  " + "," + basePowerMva);
		printBus.println("PSS/E raw data");
		printBus.println("BASE CASE");

		printLoad.println("0/ END OF BUS DATA, BEGIN LOAD DATA");
		printGen.println("0/ END OF LOAD DATA, BEGIN GENERATION DATA");
		int busID = 1;
		if (busList != null) {
			// in-->bus
			final String[] busStr = new String[11];
			final String[] loadStr = new String[12];
			final String[] genStr = new String[26];
			final String[] switShuntStr= new String[12];

			for (Node bus = busList.getFirstChild(); bus != null; bus = bus
					.getNextSibling()) {
				// set bus default value
				Helper.setBusDefautValue(busStr);
				// set load default value;
				Helper.setLoadDefautValue(loadStr);
				// set gen default
				Helper.setGenDefaultValue(genStr);
				Helper.setSwitchShuntDefaultValue(switShuntStr);
				genStr[8] = basePowerMva; // system base power
				String busType = "";
				for (Node nodeInsideBus = bus.getFirstChild(); nodeInsideBus != null; nodeInsideBus = nodeInsideBus
						.getNextSibling()) {
					String nodeName = nodeInsideBus.getNodeName();
					if (nodeInsideBus.getNodeType() == Node.ELEMENT_NODE) {
						String nodeValue = nodeInsideBus.getFirstChild()
								.getNodeValue();
						if (nodeName.equals("pss:name")) {
							busStr[0] = Integer.toString(busID++);  // bus id
							busStr[1] = nodeValue;   // bus name
						} else if (nodeName.equals("pss:zone")) {
							String zoneName = nodeValue;
							String zoneId = "";
							try {
								File file = new File(outputFile+"zoneInformation.txt");
								zoneId = Helper.getZoneId(zoneName, file);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							busStr[7] = loadStr[4] = zoneId;
						} else if (nodeName.equals("pss:area")) {
							busStr[6] = nodeValue;
							loadStr[3] = nodeValue;
						} else if (nodeName.equals("pss:baseVoltage")) {
							Node baseVolMag = nodeInsideBus.getFirstChild();							
							String baseVolValue = baseVolMag.getFirstChild()
									.getNodeValue();
							busStr[2] = baseVolValue;    // base bus voltage							
						} else if (nodeName.equals("pss:loadflowBusData")) {
							for (Node nodeInsideLoadflow = nodeInsideBus
									.getFirstChild(); nodeInsideLoadflow != null; nodeInsideLoadflow = nodeInsideLoadflow
									.getNextSibling()) {
								// voltage magtitude
								if (nodeInsideLoadflow.getNodeName().equals(
										"pss:voltage")) {
									String volMag = nodeInsideLoadflow
											.getFirstChild().getFirstChild()
											.getNodeValue();
									busStr[8] = volMag;//  upper and lower limit set to 
									                                                    // the same, to keep the voltage 
									// regulated voltage, which is controlled by this generator Q
									genStr[6] = volMag;
									// remoted bus number
									genStr[7]="0";
									
									// angle
								} else if (nodeInsideLoadflow.getNodeName()
										.equals("pss:angle")) {
									String ang = nodeInsideLoadflow
											.getFirstChild().getFirstChild()
											.getNodeValue();
									busStr[9] = ang;
								} else if (nodeInsideLoadflow.getNodeName()
										.equals("pss:genData")) {
									for (Node nodeInsideGen = nodeInsideLoadflow
											.getFirstChild(); nodeInsideGen != null; nodeInsideGen = nodeInsideGen
											.getNextSibling()) {
										if (nodeInsideGen.getNodeName().equals(
												"pss:code")) {
											busType = nodeInsideGen
													.getFirstChild()
													.getNodeValue();
											if (busType.equals("SWING")) {
												busStr[3] = Integer.toString(3);
												genStr[0] = busStr[0];												
											} else if (busType.equals("PV")) {
												//busStr[3] = Integer.toString(2);
												genStr[0] = busStr[0];
												switShuntStr[0]= busStr[0];
												switShuntStr[2]=switShuntStr[3]=busStr[8];
											} else if (busType.equals("PQ")) {
												//busStr[3] = Integer.toString(1);
												loadStr[0] = busStr[0];
											}
										} else if (nodeInsideGen.getNodeName()
												.equals("pss:gen")) {
											for (Node nodeInsidegen = nodeInsideGen
													.getFirstChild(); nodeInsidegen != null; nodeInsidegen = nodeInsidegen
													.getNextSibling()) {
												if (nodeInsidegen.getNodeName()
														.equals("pss:power")) {
													String pGen = nodeInsidegen
															.getFirstChild()
															.getFirstChild()
															.getNodeValue();
													String qGen = nodeInsidegen
															.getFirstChild()
															.getNextSibling()
															.getFirstChild()
															.getNodeValue();
													genStr[2] = pGen;
													genStr[3] = qGen;
													if(!busStr[3].equals("3")){
														busStr[3] = Integer.toString(2); // set bus to a generator type
													}
													
												} else if (nodeInsidegen
														.getNodeName()
														.equals("pss:qGenLimit")) {
													Node qLimit = nodeInsidegen
															.getFirstChild();
													Node max = qLimit
															.getFirstChild();
													Node min = max
															.getNextSibling();
													String maxValue = max
															.getFirstChild()
															.getNodeValue();
													String minValue = min
															.getFirstChild()
															.getNodeValue();
													genStr[4] = maxValue;
													genStr[5] = minValue;													
												} else if (nodeInsidegen
														.getNodeName()
														.equals("pss:pGenLimit")) {
													Node pLimit = nodeInsidegen
															.getFirstChild();
													Node max = pLimit
															.getFirstChild();
													Node min = max
															.getNextSibling();
													String maxValue = max
															.getFirstChild()
															.getNodeValue();
													String minValue = min
															.getFirstChild()
															.getNodeValue();
													genStr[16] = maxValue;
													genStr[17] = minValue;
												} else if (nodeInsidegen
														.getNodeName()
														.equals("pss:vGenLimit")) {

												} else if (nodeInsidegen
														.getNodeName()
														.equals(
																"pss:desiredRemoteVoltage")) {
													Node desiredVoltage = nodeInsidegen
															.getFirstChild();
													Node remoteBus = desiredVoltage
															.getNextSibling();
													String volMag = Helper
															.getValue(desiredVoltage);
													// regulated voltage, which is controlled by this generator Q
													genStr[6] = volMag;
													// remoted bus number
													genStr[7]=Helper.getValue(remoteBus);													
												}
											}
										}
									}
								} else if (nodeInsideLoadflow.getNodeName()
										.equals("pss:loadData")) {
									Node code = nodeInsideLoadflow
											.getFirstChild();
									String loadType = code.getFirstChild()
											.getNodeValue();
									Node load = code.getNextSibling();
									String pLoad = load.getFirstChild()
											.getFirstChild().getNodeValue();
									String qLoad = load.getFirstChild()
											.getNextSibling().getFirstChild()
											.getNodeValue();
									if (loadType.equals("CONST_P")) {
										if (loadStr[0] == null) {
											loadStr[0] = busStr[0];
										}
										loadStr[5] = pLoad;
										loadStr[6] = qLoad;
									} else if (loadType.equals("CONST_I")) {
										if (loadStr[0] == null) {
											loadStr[0] = busStr[0];
										}
										loadStr[7] = pLoad;
										loadStr[8] = qLoad;
									} else if (loadType.equals("CONST_Z")) {
										if (loadStr[0] == null) {
											loadStr[0] = busStr[0];
										}
										loadStr[9] = pLoad;
										loadStr[10] = qLoad;
									}
								} else if (nodeInsideLoadflow.getNodeName()
										.equals("pss:shuntY")) {
									Node g = nodeInsideLoadflow.getFirstChild();
									String gl = Helper.getValue(g);
									Node b = g.getNextSibling();
									String bl = Helper.getValue(b);
									busStr[4] = Helper.setFormat(gl);
									busStr[5] = Helper.setFormat(bl);
								}
								if (loadStr[5].equals("0")
										&& loadStr[6].equals("0")
										&& loadStr[7].equals("0")
										&& loadStr[8].equals("0")
										&& loadStr[9].equals("0")
										&& loadStr[10].equals("0")) {
									loadStr[0] = null;
								}
							}
						}
					}
				}
				
				if(switShuntStr[0]!=null&& busType.equals("PV")&& busStr[3].equals("1")){					
					for(int i=0; i<=11; i++){
						printSwitShunt.print(switShuntStr[i] + ", ");
						if(i==11){
							printSwitShunt.println();
						}
					}
				}
				
				// output to txt
				for (int j = 0; j <= 10; j++) {
					if (j == 1) {
						printBus.print("\'" + busStr[j] + "\'" + ",   ");
					} else if (j == 10) {
						printBus.println(busStr[j] + ",   ");
					} else if (j == 0) {
						printBus.print("   " + busStr[j] + ",   ");
					} else {
						printBus.print(busStr[j] + ",   ");
					}
				}
				// out put load data
				if (loadStr[0] != null) {
					for (int k = 0; k <= 11; k++) {
						if (k == 1) {
							printLoad.print("\'" + loadStr[k] + "\'" + ",  ");
						} else if (k == 11) {
							printLoad.println(loadStr[k] + ",  ");
						} else if (k == 0) {
							printLoad.print("   " + loadStr[k] + ",  ");
						} else
							printLoad.print(loadStr[k] + ",  ");

					}
				}

				// out put gen data
				if (genStr[0] != null) {
					for (int l = 0; l <= 25; l++) {
						if (l == 0) {
							printGen.print("   " + genStr[l] + ",  ");
						} else if (l == 1) {
							printGen.print("\'" + genStr[l] + "\'" + ",  ");
						} else if (l == 25) {
							printGen.println();
						} else
							printGen.print(genStr[l] + ",  ");

					}
				}

			}
		}
	}

}
