import java.io.File;
import java.io.PrintStream;

import org.w3c.dom.Node;

public class BranchData {
	public static void processBranchData(Node branchList, String basePowerMva,
			PrintStream printBranch, PrintStream printTrans, String outputFile) {

		printBranch.println("0/ END OF GENERATION DATA, BEGIN BRANCH DATA");
		printTrans.println("0/ END OF BRANCH DATA, BEGIN TRANSFORMER DATA");
		if (branchList != null) {
			final String[] branchStr = new String[23];
			final String[] transStr = new String[41];
			for (Node branch = branchList.getFirstChild(); branch != null; branch = branch
					.getNextSibling()) {

				String lineType = ""; // line or xformer
				// set branch data default value;
				Helper.setBranchDefaultValue(branchStr);

				// set transformer data default value
				Helper.setTransDefaultValue(transStr);
				transStr[22] = basePowerMva; // system base power

				for (Node nodeInsideBranch = branch.getFirstChild(); nodeInsideBranch != null; nodeInsideBranch = nodeInsideBranch
						.getNextSibling()) {
					String nodeName = nodeInsideBranch.getNodeName();
					if (nodeInsideBranch.getNodeType() == Node.ELEMENT_NODE) {
						if (nodeName.equals("pss:id")) {
							Node idRef = nodeInsideBranch.getFirstChild();
							String id = idRef.getNodeValue();
							transStr[10] = id;// branch name
						} else if (nodeName.equals("pss:fromBus")) {
							Node idRef = nodeInsideBranch.getFirstChild();
							String fBus = idRef.getFirstChild().getNodeValue();
							String relatedBusId = "";
							// find the bus id corresponding to the bus name
							try {
								String str = "'" + fBus + "'";
								File file = new File(outputFile + "bus.txt");
								relatedBusId = Helper.getBusId(str, file);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							branchStr[0] = relatedBusId;
							transStr[0] = relatedBusId;
						} else if (nodeName.equals("pss:toBus")) {
							Node idRef = nodeInsideBranch.getFirstChild();
							String tBus = idRef.getFirstChild().getNodeValue();
							String relatedBusId = "";
							// find the bus id corresponding to the bus name
							try {
								String str = "'" + tBus + "'";
								File file = new File(outputFile + "bus.txt");
								relatedBusId = Helper.getBusId(str, file);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							branchStr[1] = relatedBusId;
							transStr[1] = relatedBusId;
						} else if (nodeName.equals("pss:circuitId")) {
							String ckt = nodeInsideBranch.getFirstChild()
									.getNodeValue();
							branchStr[2] = ckt;
							transStr[3] = ckt;
						} else if (nodeName.equals("pss:loadflowBranchData")) {
							for (Node nodeInLfBranch = nodeInsideBranch
									.getFirstChild(); nodeInLfBranch != null; nodeInLfBranch = nodeInLfBranch
									.getNextSibling()) {
								String name = nodeInLfBranch.getNodeName();

								if (name.equals("pss:code")) {
									String code = nodeInLfBranch
											.getFirstChild().getNodeValue();
									lineType = code;

								} else if (name.equals("pss:lineData")) {
									for (Node nodeInLine = nodeInLfBranch
											.getFirstChild(); nodeInLine != null; nodeInLine = nodeInLine
											.getNextSibling()) {
										String nameInLine = nodeInLine
												.getNodeName();
										if (nameInLine.equals("pss:z")) {
											Node r = nodeInLine.getFirstChild();
											branchStr[3] = Helper.setFormat(r
													.getFirstChild()
													.getNodeValue());
											Node x = r.getNextSibling();
											branchStr[4] = Helper.setFormat(x
													.getFirstChild()
													.getNodeValue());
										} else if (nameInLine
												.equals("pss:totalShuntY")) {
											// Note: In PSSE, there is only a
											// total-B, no total-G
											Node g = nodeInLine.getFirstChild();
											if (g.getNextSibling() != null) {
												Node b = g.getNextSibling();
												String totalB = Helper
														.getValue(b);
												branchStr[5] = Helper
														.setFormat(totalB);
											}
											String totalG = Helper.getValue(g);
											if (!totalG.equals("0.0")) {
												Double halfG = (new Integer(
														totalG).intValue()) * 0.5;
												branchStr[9] = Helper
														.setFormat(halfG
																.toString());
												branchStr[11] = Helper
														.setFormat(halfG
																.toString());
											}
										} else if (nameInLine
												.equals("pss:fromShuntY")) {
											Node Gi = nodeInLine
													.getFirstChild();
											Node Bi = Gi.getNextSibling();
											branchStr[9] = Helper
													.setFormat(Helper
															.getValue(Gi));
											branchStr[10] = Helper
													.setFormat(Helper
															.getValue(Bi));
										} else if (nameInLine
												.equals("pss:tolShuntY")) {
											Node Gj = nodeInLine
													.getFirstChild();
											Node Bj = Gj.getNextSibling();
											branchStr[11] = Helper
													.setFormat(Helper
															.getValue(Gj));
											branchStr[12] = Helper
													.setFormat(Helper
															.getValue(Bj));
										} else if (nameInLine
												.equals("pss:Length")) {
											Node length = nodeInLine
													.getFirstChild();
											branchStr[14] = Helper
													.setFormat(Helper
															.getValue(length));
										}

									}
								} else if (name.equals("pss:xformerData")
										|| name.equals("pss:phaseShiftXfrData")) {

									for (Node nodeInXfr = nodeInLfBranch
											.getFirstChild(); nodeInXfr != null; nodeInXfr = nodeInXfr
											.getNextSibling()) {
										String nameInXfr = nodeInXfr
												.getNodeName();
										if (nameInXfr.equals("pss:z")) {
											Node r = nodeInXfr.getFirstChild();
											transStr[20] = Helper.setFormat(r
													.getFirstChild()
													.getNodeValue());
											Node x = r.getNextSibling();
											transStr[21] = Helper.setFormat(x
													.getFirstChild()
													.getNodeValue());
										} else if (nameInXfr
												.equals("pss:ratingData")) {
											Node fromVol = nodeInXfr
													.getFirstChild();
											transStr[24] = Helper
													.getValue(fromVol
															.getFirstChild());
											Node toVol = fromVol
													.getNextSibling();
											transStr[40] = Helper
													.getValue(toVol
															.getFirstChild());
										} else if (nameInXfr
												.equals("pss:fromTurnRatio")) {
											transStr[23] = Helper
													.setFormat(Helper
															.getValue(nodeInXfr));
											/*
											 * // convert the bpa xfr model to
											 * psse model double RT2= new
											 * Double(transStr[20]).doubleValue();
											 * double Ti2=new
											 * Double(transStr[23]).doubleValue();
											 * double RT1=Ti2*Ti2*RT2;
											 * transStr[20]=Double.toString(RT1);
											 * double XT2= new
											 * Double(transStr[21]).doubleValue();
											 * double XT1=Ti2*Ti2*XT2;
											 * transStr[21]=Double.toString(XT1);
											 */
										} else if (nameInXfr
												.equals("pss:toTurnRatio")) {
											transStr[39] = Helper
													.setFormat(Helper
															.getValue(nodeInXfr));
										} else if (nameInXfr
												.equals("pss:fromShuntY")) {

										} else if (nameInXfr
												.equals("pss:toShuntY")) {

										} else if (nameInXfr
												.equals("pss:tapAdjustment")) {
											boolean convert = false;
											for (Node nodeInAdj = nodeInXfr
													.getFirstChild(); nodeInAdj != null; nodeInAdj = nodeInAdj
													.getNextSibling()) {
												String nameInAdj = nodeInAdj
														.getNodeName();
												if (nameInAdj
														.equals("pss:adjustmentType")) {
													Node adjustType = nodeInXfr
															.getFirstChild();
													String adjType = Helper
															.getValue(adjustType);
													// only two control mode defined in odm
													if (adjType
															.equals("Voltage")) {
														transStr[29] = "1";// voltage control
													} else {
														transStr[29] = "2";// mvar flow control
													}
												} else if (nameInAdj
														.equals("pss:tapLimit")) {
													String max = Helper
															.getValue(nodeInAdj
																	.getFirstChild());
													String min = Helper
															.getValue(nodeInAdj
																	.getFirstChild()
																	.getNextSibling());
													if (transStr[29] == "1") {// voltage  control
														transStr[33] = max; // voltage at the controlled bus
														transStr[34] = min;
													} else if (transStr[29] == "2") {// mvar flow control
														transStr[31] = max; // off-nominal turns ratio
														transStr[32] = min;
													}
												} else if (nameInAdj
														.equals("pss:tapLimitUnit")) {
													// unit should be pu.
													String unit = Helper
															.getValue(nodeInAdj
																	.getFirstChild());
													if (unit.equals("PU")) {// all in PU system
														transStr[4] = "1"; // CW, unit
														transStr[5] = "1"; // CZ, unit
														transStr[6] = "1"; // CM, unit
													} else {
														// err display: should use pu system.
													}
												} else if (nameInAdj
														.equals("pss:tapAdjOnFromSide")) {
													String meterdSide = Helper
															.getValue(nodeInAdj);
													if (meterdSide
															.equals("true")) {
														transStr[9] = "2";// the  nonmetered side 
													} else {
														transStr[9] = "1";  //:to side
													}
												} else if (nameInAdj
														.equals("pss:voltageAdjData")) {
													String desiredVolUnit = Helper
															.getValue(nodeInAdj
																	.getFirstChild());
													if (desiredVolUnit
															.equals("KV")
															|| desiredVolUnit
																	.equals("VOLT")) {
														convert = true; // convert to pu system.
													}
													Node contBus = nodeInAdj
															.getFirstChild()
															.getNextSibling();
													String controlledBus = Helper
															.getValue(contBus);
													transStr[30] = controlledBus;// CONT
													String adjLocation = Helper
															.getValue(contBus
																	.getNextSibling());
													if (adjLocation
															.equals("NearFromBus")
															|| adjLocation
																	.equals("FromBus")) {
														int locationSign = -(new Integer(
																transStr[30])
																.intValue());
														transStr[30] = Integer
																.toString(locationSign);
													}
												} else if (nameInAdj
														.equals("pss:mvarFlowAdjData")) {
													String desiredMvarUnit = Helper
															.getValue(nodeInAdj
																	.getFirstChild());
													if (desiredMvarUnit
															.equals("MVAR")
															|| desiredMvarUnit
																	.equals("KVAR")) {
														convert = true; // convert  to pu system.
													}
												}
											}
										} else if (nameInXfr
												.equals("pss:fromAngle")) {
											String fromAngle = Helper
													.getValue(nodeInXfr
															.getFirstChild());
											transStr[25] = fromAngle;
										} else if (nameInXfr
												.equals("pss:toAngle")) {
											String fromAngle = Helper
													.getValue(nodeInXfr
															.getFirstChild());

										} else if (nameInXfr
												.equals("pss:angleAdjustment")) {
											// String
											// fromAngle=Helper.getValue(nodeInXfr.getFirstChild());

										}
									}
								} else if (name.equals("pss:ratingLimit")) {
									for (Node nodeInRating = nodeInLfBranch
											.getFirstChild(); nodeInRating != null; nodeInRating = nodeInRating
											.getNextSibling()) {
										String nameInRating = nodeInRating
												.getNodeName();
										if (nameInRating
												.equals("pss:mvaRating1")) {
											branchStr[6] = transStr[26] = Helper
													.getValue(nodeInRating);
										} else if (nameInRating
												.equals("pss:mvaRating2")) {
											branchStr[7] = transStr[27] = Helper
													.getValue(nodeInRating);
										} else if (nameInRating
												.equals("pss:mvaRating3")) {
											branchStr[8] = transStr[28] = Helper
													.getValue(nodeInRating);
										} else if (nameInRating
												.equals("pss:currentRating")) {

										} else if (nameInRating
												.equals("pss:mvaRatingUnit")) {

										} else if (nameInRating
												.equals("pss:currentRatingUnit")) {

										}
									}
								}
							}
						}
					}
					if (lineType.equals("Line")) {
						// output result
						for (int j = 0; j <= 22; j++) {
							if (j == 2) {
								printBranch.print("\'" + branchStr[j] + "\'"
										+ ",  ");
							} else if (j == 0) {
								printBranch.print("   " + branchStr[j] + ",  ");
							} else {
								printBranch.print(branchStr[j] + ",  ");
								if (j == 22) {
									printBranch.println();
								}
							}
						}
					} else if (lineType.equals("Transformer")
							|| lineType.equals("PhaseShiftXformer")) {
						// output transformer result						   
						for (int i = 0; i <= 40; i++) {
							if (i == 3) {
								printTrans.print("\'" + transStr[i] + "\'"
										+ ",  ");
							} else if (i == 0) {
								printTrans.print("   " + transStr[i] + ",  ");
							} else if (i == 10) {
								printTrans.print("\'" + transStr[i] + "\'"
										+ ",  ");
							} else if (i == 19 || i == 22 || i == 38) {
								printTrans.println(transStr[i]);
							} else {
								printTrans.print(transStr[i] + ",  ");
								if (i == 40) {
									printTrans.println();
								}
							}
						}
					}
				}
			}
		}

	}

}
