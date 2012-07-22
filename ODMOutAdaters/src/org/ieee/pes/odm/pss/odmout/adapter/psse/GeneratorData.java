package org.ieee.pes.odm.pss.odmout.adapter.psse;

/*
 * @(#)Generator.java   
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.w3c.dom.Node;

public class GeneratorData {

	public static void processGeneratorData(Node generatorDataList,
			PrintStream printGenerator, String outputFile) {
		try {
			final String[] genStr = new String[17];
			final String[] genZx = new String[2];
			OutputStream outZx = new FileOutputStream(outputFile + "zx.txt");
			PrintStream printZx = new PrintStream(outZx);
			for (Node generator = generatorDataList.getFirstChild(); generator != null; generator = generator
					.getNextSibling()) {
				String genType = "";
				for (Node nodeInGen = generator.getFirstChild(); nodeInGen != null; nodeInGen = nodeInGen
						.getNextSibling()) {
					String nodeName = nodeInGen.getNodeName();
					if (nodeInGen.getNodeType() == Node.ELEMENT_NODE) {
						if (nodeName.equals("pss:locatedBus")) {
							Node busName = nodeInGen.getFirstChild();
							try {
								String name = Helper.getValue(busName);
								if (name.equals("BJZ500")) {
									genStr[0] = genZx[0] = "162";
								} else {
									String str = "'" + name + "'";
									File file = new File(outputFile + "bus.txt");
									genStr[0] = genZx[0] = Helper.getBusId(str,
											file);
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else if (nodeName.equals("pss:genId")) {
							genStr[2] = Helper.getValue(nodeInGen
									.getFirstChild());
						} else if (nodeName.equals("pss:generatorType")) {
							genType = Helper.getValue(nodeInGen);
							if (genType.equals("classicalModel")) {
								genStr[1] = "'" + "GENCLS" + "'";
							} else if (genType
									.equals("sailent_pole_subtransModel")) {
								genStr[1] = "'" + "GENSAL" + "'";
							} else if (genType
									.equals("nonsailent_pole_subtransModel")) {
								genStr[1] = "'" + "GENROU" + "'";
							}
						} else if (nodeName.equals("pss:generatorModel")) {
							Node model = nodeInGen.getFirstChild();// pss:xxxModel
							String modelName = model.getNodeName();
							if (modelName.equals("pss:classicalModel")) {
								for (Node nodeInClass = model.getFirstChild(); nodeInClass != null; nodeInClass = nodeInClass
										.getNextSibling()) {
									String name = nodeInClass.getNodeName();
									if (name.equals("pss:H")) {
										genStr[3] = Helper.getValue(nodeInClass
												.getFirstChild());
										/*
										 * double k=new
										 * Double(genStr[3]).doubleValue();
										 * if(k>=10){ k=9.9; }
										 * genStr[3]=Double.toString(k);
										 */
									} else if (name.equals("pss:D")) {
										genStr[4] = Helper.getValue(nodeInClass
												.getFirstChild());
										/*
										 * double k=new
										 * Double(genStr[4]).doubleValue();
										 * if(k>=4||k<0){ k=2.5; }
										 * genStr[4]=Double.toString(k);
										 */
									} else if (name.equals("pss:xd1")) {
										genZx[1] = Helper.getValue(nodeInClass
												.getFirstChild());
										/*
										 * double k=new
										 * Double(genZx[1]).doubleValue();
										 * if(k>=1.25){ k=1.24; }
										 * genZx[1]=Double.toString(k);
										 */
									}
								}
							}
							if (modelName
									.equals("pss:sailent_pole_subTransientModel")) {
								for (Node nodeInSubTrans = model
										.getFirstChild(); nodeInSubTrans != null; nodeInSubTrans = nodeInSubTrans
										.getNextSibling()) {
									String name = nodeInSubTrans.getNodeName();
									if (name.equals("pss:Tdo1")) {
										genStr[3] = Helper
												.getValue(nodeInSubTrans
														.getFirstChild());
										/*
										 * double k=new
										 * Double(genStr[3]).doubleValue();
										 * if(k>=10){ k=5; }
										 * genStr[3]=Double.toString(k);
										 */
									} else if (name.equals("pss:Td011")) {
										genStr[4] = Helper
												.getValue(nodeInSubTrans
														.getFirstChild());
										/*
										 * double k=new
										 * Double(genStr[4]).doubleValue();
										 * if(k>=0.2){ k=0.05; }
										 * genStr[4]=Double.toString(k);
										 */
									} else if (name.equals("pss:Tq011")) {
										genStr[5] = Helper
												.getValue(nodeInSubTrans
														.getFirstChild());
										/*
										 * double k=new
										 * Double(genStr[5]).doubleValue();
										 * if(k>=0.2||k!=new
										 * Double(genStr[4]).doubleValue()){
										 * k=0.05; }
										 * genStr[5]=Double.toString(k);
										 */
									} else if (name.equals("pss:H")) {
										genStr[6] = Helper
												.getValue(nodeInSubTrans
														.getFirstChild());
										/*
										 * double k=new
										 * Double(genStr[6]).doubleValue();
										 * if(k>=10){ k=3; }
										 * genStr[6]=Double.toString(k);
										 */
									} else if (name.equals("pss:D")) {
										genStr[7] = Helper
												.getValue(nodeInSubTrans
														.getFirstChild());
										/*
										 * double k=new
										 * Double(genStr[7]).doubleValue();
										 * if(k>=4||k<0){ k=0; }
										 * genStr[7]=Double.toString(k);
										 */
									} else if (name.equals("pss:xd")) {
										genStr[8] = Helper
												.getValue(nodeInSubTrans
														.getFirstChild());
										/*
										 * double k=new
										 * Double(genStr[8]).doubleValue();
										 * if(k>=2.5){ k=1.4; }
										 * genStr[8]=Double.toString(k);
										 */
									} else if (name.equals("pss:xq")) {
										genStr[9] = Helper
												.getValue(nodeInSubTrans
														.getFirstChild());
										// genStr[9]="1.35";
									} else if (name.equals("pss:xd1")) {
										genStr[10] = Helper
												.getValue(nodeInSubTrans
														.getFirstChild());
										/*
										 * double k=new
										 * Double(genStr[10]).doubleValue();
										 * if(k>=1.25){ k=0.3; }
										 * genStr[10]=Double.toString(k);
										 */
									} else if (name.equals("pss:xd11")) {
										genStr[11] = genZx[1] = Helper
												.getValue(nodeInSubTrans
														.getFirstChild());
										// genStr[11]="0.2";
									} else if (name.equals("pss:xr")) {
										genStr[12] = Helper
												.getValue(nodeInSubTrans
														.getFirstChild());
										// genStr[12]="0.1";
									} else if (name.equals("pss:SE1")) {
										genStr[13] = Helper
												.getValue(nodeInSubTrans);
										/*
										 * double k= new
										 * Double(genStr[13]).doubleValue();
										 * if(k<=0||k>1){ k=0.03; }
										 * genStr[13]=Double.toString(k);
										 */
									} else if (name.equals("pss:SE2")) {
										genStr[14] = Helper
												.getValue(nodeInSubTrans);
										/*
										 * double k= new
										 * Double(genStr[14]).doubleValue();
										 * if(k<=0){ k=0.4; }
										 * genStr[14]=Double.toString(k);
										 */
									}

								}
							}
							if (modelName
									.equals("pss:non_sailent_pole_subTransientModel")) {
								for (Node nodeInSubTrans = model
										.getFirstChild(); nodeInSubTrans != null; nodeInSubTrans = nodeInSubTrans
										.getNextSibling()) {
									String name = nodeInSubTrans.getNodeName();
									if (name.equals("pss:Tdo1")) {
										genStr[3] = Helper
												.getValue(nodeInSubTrans
														.getFirstChild());
										/*
										 * double k=new
										 * Double(genStr[3]).doubleValue();
										 * if(k>=10){ k=5; }
										 * genStr[3]=Double.toString(k);
										 */
									} else if (name.equals("pss:Td011")) {
										genStr[4] = Helper
												.getValue(nodeInSubTrans
														.getFirstChild());
										/*
										 * double k=new
										 * Double(genStr[4]).doubleValue();
										 * if(k>=0.2){ k=0.05; }
										 * genStr[4]=Double.toString(k);
										 */
									} else if (name.equals("pss:Tq01")) {
										genStr[5] = Helper
												.getValue(nodeInSubTrans
														.getFirstChild());
										/*
										 * double k=new
										 * Double(genStr[5]).doubleValue();
										 * if(k>1.5||k<0.2){ k=1.0; }
										 * genStr[5]=Double.toString(k);
										 */
									} else if (name.equals("pss:Tq011")) {
										genStr[6] = Helper
												.getValue(nodeInSubTrans
														.getFirstChild());
										/*
										 * double k=new
										 * Double(genStr[6]).doubleValue();
										 * if(k>=0.2||k!=new
										 * Double(genStr[4]).doubleValue()){
										 * k=0.05; }
										 * genStr[6]=Double.toString(k);
										 */
									} else if (name.equals("pss:H")) {
										genStr[7] = Helper
												.getValue(nodeInSubTrans
														.getFirstChild());
										/*
										 * double k=new
										 * Double(genStr[7]).doubleValue();
										 * if(k>=10){ k=9.9; }
										 * genStr[7]=Double.toString(k);
										 */
									} else if (name.equals("pss:D")) {
										genStr[8] = Helper
												.getValue(nodeInSubTrans
														.getFirstChild());
										/*
										 * double k=new
										 * Double(genStr[8]).doubleValue();
										 * if(k>=4||k<0){ k=2.5; }
										 * genStr[8]=Double.toString(k);
										 */
									} else if (name.equals("pss:xd")) {
										genStr[9] = Helper
												.getValue(nodeInSubTrans
														.getFirstChild());
										/*
										 * double k=new
										 * Double(genStr[9]).doubleValue();
										 * if(k>=2.5){ k=2.45; }
										 * genStr[9]=Double.toString(k);
										 */
									} else if (name.equals("pss:xq")) {
										genStr[10] = Helper
												.getValue(nodeInSubTrans
														.getFirstChild());
										// genStr[10]="1.35";
									} else if (name.equals("pss:xd1")) {
										genStr[11] = Helper
												.getValue(nodeInSubTrans
														.getFirstChild());
										/*
										 * double k=new
										 * Double(genStr[11]).doubleValue();
										 * if(k>=1.25){ k=0.3; }
										 * genStr[11]=Double.toString(k);
										 */
									} else if (name.equals("pss:xq1")) {
										genStr[12] = Helper
												.getValue(nodeInSubTrans
														.getFirstChild());
									} else if (name.equals("pss:xd11")) {
										genStr[13] = genZx[1] = Helper
												.getValue(nodeInSubTrans
														.getFirstChild());
										// genStr[13]="0.2";
									} else if (name.equals("pss:xr")) {
										genStr[14] = Helper
												.getValue(nodeInSubTrans
														.getFirstChild());
										// genStr[14]="0.1";
									} else if (name.equals("pss:SE1")) {
										genStr[15] = Helper
												.getValue(nodeInSubTrans);
										/*
										 * double k= new
										 * Double(genStr[15]).doubleValue();
										 * if(k<=0||k>1){ k=0.03; }
										 * genStr[15]=Double.toString(k);
										 */
									} else if (name.equals("pss:SE2")) {
										genStr[16] = Helper
												.getValue(nodeInSubTrans);
										/*
										 * double k= new
										 * Double(genStr[16]).doubleValue();
										 * if(k<=0){ k=0.4; }
										 * genStr[16]=Double.toString(k);
										 */
									}

								}
							}
						}
					}

				}
				if (genType.equals("classicalModel")) {
					for (int i = 0; i <= 4; i++) {
						printGenerator.print(genStr[i] + "   ");
						if (i == 4) {
							printGenerator.println("/");
						}
					}

				} else if (genType.equals("nonsailent_pole_subtransModel")) {
					for (int i = 0; i <= 16; i++) {
						printGenerator.print(genStr[i] + "   ");
						/*
						 * if(i==6||i==11){ printGenerator.println(" "); }else
						 * if(i==16){ printGenerator.println("/"); }
						 */
						if (i == 16) {
							printGenerator.println("/");
						}
					}
				} else if (genType.equals("sailent_pole_subtransModel")) {
					for (int i = 0; i <= 14; i++) {
						printGenerator.print(genStr[i] + "   ");
						/*
						 * if(i==6||i==11){ printGenerator.println(" "); }else
						 * if(i==14){ printGenerator.println("/"); }
						 */
						if (i == 14) {
							printGenerator.println("/");
						}
					}
				}
				for (int i = 0; i <= 1; i++) {
					printZx.print(genZx[i] + "   ");
					if (i == 1) {
						printZx.println();
					}
				}

			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}