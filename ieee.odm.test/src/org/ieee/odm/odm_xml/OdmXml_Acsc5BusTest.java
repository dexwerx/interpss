 /*
  * @(#)OdmXml_Test.java   
  *
  * Copyright (C) 2008 www.interpss.org
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
  * @Author Mike Zhou
  * @Version 1.0
  * @Date 02/01/2008
  * 
  *   Revision History
  *   ================
  *
  */

package org.ieee.odm.odm_xml;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;

import javax.xml.bind.JAXBElement;

import org.ieee.odm.model.acsc.AcscModelParser;
import org.ieee.odm.schema.BaseBranchXmlType;
import org.ieee.odm.schema.BusXmlType;
import org.ieee.odm.schema.LineShortCircuitXmlType;
import org.ieee.odm.schema.PSXfrShortCircuitXmlType;
import org.ieee.odm.schema.ShortCircuitBusXmlType;
import org.ieee.odm.schema.ShortCircuitGenDataXmlType;
import org.ieee.odm.schema.XfrShortCircuitXmlType;
import org.junit.Test;

public class OdmXml_Acsc5BusTest { 
	@Test
	public void testCase() throws Exception {
		File file = new File("testdata/ieee_odm/ODM_Acsc_5Bus.xml");
		AcscModelParser parser = new AcscModelParser();
		parser.parse(new FileInputStream(file));
		//System.out.println(parser.toXmlDoc(false));
		
		assertTrue(parser.getNet().getBasePower().getValue() == 100.0);
		
		assertTrue(parser.getNet().getBusList().getBus().size() == 5);
		assertTrue(parser.getNet().getBranchList().getBranch().size() == 5);
		
		for (JAXBElement<? extends BusXmlType> bus : parser.getNet().getBusList().getBus()) {
			ShortCircuitBusXmlType scBus = (ShortCircuitBusXmlType)bus.getValue();
			System.out.println("ScBus: " + scBus.getId() + ", ScCode: " + scBus.getScCode());
			if (scBus.getGenData() != null && scBus.getGenData().getEquivGen() != null) {
				ShortCircuitGenDataXmlType scGenData = (ShortCircuitGenDataXmlType)scBus.getGenData().getEquivGen().getValue();
				System.out.println("   ScGenData: " + scGenData.getPotiveZ().getRe() + "+j" + scGenData.getPotiveZ().getIm());
			}
		}

		for (JAXBElement<? extends BaseBranchXmlType> bra : parser.getNet().getBranchList().getBranch()) {
			BaseBranchXmlType scBra = (BaseBranchXmlType)bra.getValue();
			if (scBra instanceof LineShortCircuitXmlType) {
				LineShortCircuitXmlType scLine = (LineShortCircuitXmlType)scBra;
				System.out.println("ScLine: " + scLine.getId());
			}
			else if (scBra instanceof XfrShortCircuitXmlType) {
				XfrShortCircuitXmlType scXfr = (XfrShortCircuitXmlType)scBra;
				System.out.println("ScXfr: " + scXfr.getId());
			} 
			else if (scBra instanceof PSXfrShortCircuitXmlType) {
				PSXfrShortCircuitXmlType scPsXfr = (PSXfrShortCircuitXmlType)scBra;
				System.out.println("ScPsXfr: " + scPsXfr.getId());
			} 
		}
	}

	@Test
	public void noLfTestCase() throws Exception {
		File file = new File("testdata/ieee_odm/ODM_AcscNoLF_5Bus.xml");
		AcscModelParser parser = new AcscModelParser();
		parser.parse(new FileInputStream(file));
		//System.out.println(parser.toXmlDoc(false));
		
		assertTrue(parser.getNet().getBasePower().getValue() == 100.0);
	}
}

