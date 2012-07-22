/*
 * @(#)AreaData.java   
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
import java.io.PrintStream;

import org.w3c.dom.Node;

public class AreaData {

	public static void processAreaData(Node areaList,
			PrintStream printAreaInterchange,
			PrintStream printInterareaTransfer, PrintStream printZone) {
		// print comment
		// printAreaInterchange.println("0 /END OF TRANSFORMER DATA, BEGIN AREA
		// DATA");
		// printInterareaTransfer.println("0 /END OF ZONE DATA, BEGIN INTER-AREA
		// TRANSFER DATA");
		if (areaList != null) {
			final String[] areaInterchangeStr = new String[5];
			final String[] interareaTransferStr = new String[4];
			String[] zoneStr = new String[3];

			int transId = 1;// interarea transfer identifier used to distinguish
							// among multiple transfer
			for (Node area = areaList.getFirstChild(); area != null; area = area
					.getNextSibling()) {
				Helper.setAreaInterchangeDefautValue(areaInterchangeStr);
				Helper.setInterareaTransferDefautValue(interareaTransferStr);
				for (Node nodeInsideArea = area.getFirstChild(); nodeInsideArea != null; nodeInsideArea = nodeInsideArea
						.getNextSibling()) {
					String nodeName = nodeInsideArea.getNodeName();
					if (nodeInsideArea.getNodeType() == Node.ELEMENT_NODE) {
						if (nodeName.equals("pss:areaNumber")) {
							areaInterchangeStr[0] = Helper
									.getValue(nodeInsideArea);
						} else if (nodeName.equals("pss:areaName")) {	
							areaInterchangeStr[4] = Helper
									.getValue(nodeInsideArea);							

						} else if (nodeName.equals("pss:zoneList")) { // zones  in the area
							for (Node zone = nodeInsideArea.getFirstChild(); zone != null; zone = zone
									.getNextSibling()) {								
								Node number = zone.getFirstChild();									
								String s = Helper.getValue(number);
								zoneStr[1] = s;// zone number
								Node name = number.getNextSibling();
								zoneStr[2] = Helper.getValue(name);// zone name
								zoneStr[0] = areaInterchangeStr[4];
								for (int k = 0; k <= 2; k++) {
									printZone.print(zoneStr[k] + ", ");
									if (k == 2) {
										printZone.println();
									}
								}
							}
						} else if (nodeName.equals("pss:swingBusId")) {// swing bus in the area
							String swingBusName = Helper
									.getValue(nodeInsideArea.getFirstChild());
							areaInterchangeStr[1] = swingBusName;

						} else if (nodeName.equals("pss:alternateSwingBus")) {

						} else if (nodeName.equals("pss:totalExchangePower")) {
							areaInterchangeStr[2] = Helper
									.getValue(nodeInsideArea.getFirstChild());
						} else if (nodeName.equals("pss:exchangePower")) {
							interareaTransferStr[0] = areaInterchangeStr[0]; // from-area
																				// number
							Node toArea = nodeInsideArea.getFirstChild();
							String toAreaId = Helper.getValue(toArea);
							interareaTransferStr[1] = toAreaId;
							interareaTransferStr[2] = new Integer(transId++)
									.toString();
							Node exchangePower = toArea.getNextSibling();
							String exchange = Helper.getValue(exchangePower
									.getFirstChild());
							interareaTransferStr[3] = exchange;
						} else if (nodeName.equals("pss:exchangeErrToler")) {
							areaInterchangeStr[3] = Helper
									.getValue(nodeInsideArea);
						} else if (nodeName.equals("pss:measredOnFromSide")) {

						} else if (nodeName.equals("pss:ratedVoltage")) {

						}

					}

				}
				for (int i = 0; i <= 4; i++) {
					printAreaInterchange.print(areaInterchangeStr[i] + ", ");
					if (i == 4) {
						printAreaInterchange.println();
					}

				}
				if (!interareaTransferStr[0].equals("0")) {
					for (int j = 0; j <= 3; j++) {
						printInterareaTransfer.print(interareaTransferStr[j]
								+ ", ");
						if (j == 3) {
							printInterareaTransfer.println();
						}
					}
				}

			}

		}

	}

}
