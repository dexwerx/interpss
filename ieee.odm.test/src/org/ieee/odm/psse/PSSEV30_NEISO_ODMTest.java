 /*
  * @(#)IEEECDF_ODMTest.java   
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
  * @Author Stephen Hou
  * @Version 1.0
  * @Date 02/01/2008
  * 
  *   Revision History
  *   ================
  *
  */

package org.ieee.odm.psse;

import static org.junit.Assert.assertTrue;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.ieee.odm.adapter.IODMAdapter;
import org.ieee.odm.adapter.psse.v30.PSSEV30Adapter;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.schema.DCLineData2TXmlType;
import org.ieee.odm.schema.DcLineControlModeEnumType;
import org.ieee.odm.schema.LFGenCodeEnumType;
import org.ieee.odm.schema.LineBranchXmlType;
import org.ieee.odm.schema.LoadflowBusXmlType;
import org.ieee.odm.schema.LoadflowNetXmlType;
import org.ieee.odm.schema.PSXfr3WBranchXmlType;
import org.ieee.odm.schema.VoltageUnitType;
import org.ieee.odm.schema.XfrBranchXmlType;
import org.junit.Test;

public class PSSEV30_NEISO_ODMTest { 
	@Test
	public void testCase1() throws Exception {
		final LogManager logMgr = LogManager.getLogManager();
		Logger logger = Logger.getLogger("IEEE ODM Logger");
		logger.setLevel(Level.INFO);
		logMgr.addLogger(logger);
		
		IODMAdapter adapter = new PSSEV30Adapter();
		assertTrue(adapter.parseInputFile("testData/psse/Model_testV30.raw"));
		
		AclfModelParser parser = (AclfModelParser)adapter.getModel();
		//parser.stdout();
		
		LoadflowNetXmlType net = parser.getAclfNet();
		assertTrue(net.getBasePower().getValue() == 100.0);
		/*
     <bus id="Bus1" number="1" areaNumber="1" name="'0000        '" offLine="false">
        <ownerList>
          <owner id="1"/>
        </ownerList>
        <baseVoltage value="345.0" unit="KV"/>
        <loadflowData>
          <voltage value="1.0384173" unit="PU"/>
          <angle value="3.08" unit="DEG"/>
          <genData>
            <equivGen code="NoneGen"/>
          </genData>
        </loadflowData>
      </bus>
      		 */
		LoadflowBusXmlType bus = parser.getAclfBus("Bus1");
		assertTrue(bus.getBaseVoltage().getValue() == 345.0);
		assertTrue(bus.getBaseVoltage().getUnit() == VoltageUnitType.KV);
		assertTrue(bus.getGenData().getEquivGen().getCode() == LFGenCodeEnumType.NONE_GEN);

		/*
      <bus id="Bus3" number="3" areaNumber="1" name="'0001        '" offLine="false">
        <loadflowData>
          <genData>
            <equivGen code="PV">
              <power re="0.0" im="-65.628" unit="MVA"/>
              <desiredVoltage value="1.0384173" unit="PU"/>
              <remoteVoltageControlBus idRef="Bus1"/>
              <qLimit max="441.0" min="-155.0" unit="MVAR"/>
              <pLimit max="9999.0" min="-9999.0" unit="MW"/>
            </equivGen>
            <contributeGenList>
              <contributeGen id="'1 '" name="Gen:'1 '(3)" offLine="false">
                <ratedMva value="100.0" unit="MVA"/>
                <genData>
                  <power re="0.0" im="-65.628" unit="MVA"/>
                  <desiredVoltage value="1.03842" unit="PU"/>
                  <remoteVoltageControlBus idRef="Bus1"/>
                  <qLimit max="441.0" min="-155.0" unit="MVAR"/>
                  <pLimit max="9999.0" min="-9999.0" unit="MW"/>
                </genData>
                <sourceZ re="0.0" im="1.0" unit="PU"/>
                <mvarVControlParticipateFactor>1.0</mvarVControlParticipateFactor>
              </contributeGen>
            </contributeGenList>
          </genData>
        </loadflowData>
      </bus>
        */
		bus = parser.getAclfBus("Bus3");
		assertTrue(bus.getGenData().getEquivGen().getCode() == LFGenCodeEnumType.PV);
		assertTrue(bus.getGenData().getEquivGen().getPower().getRe() == 0.0);
		assertTrue(bus.getGenData().getEquivGen().getPower().getIm() == -65.628);
		assertTrue(bus.getGenData().getEquivGen().getDesiredVoltage().getValue() == 1.03842);
		assertTrue(bus.getGenData().getEquivGen().getQLimit().getMax() == 441.0);
		assertTrue(bus.getGenData().getContributeGen().size() == 1);
		
		/*
      <branch id="Bus19_to_Bus18_cirId_2" circuitId="2" offLine="true">
        <ownerList>
          <owner id="1" ownership="1.0"/>
        </ownerList>
        <fromBus idRef="Bus19"/>
        <toBus idRef="Bus18"/>
        <loadflowData code="Line">
          <z re="0.0" im="1.0E-5" unit="PU"/>
          <meterLocation>FromSide</meterLocation>
          <branchRatingLimit>
            <mva rating1="9999.0" rating2="9999.0" rating3="9999.0" unit="MVA"/>
          </branchRatingLimit>
        </loadflowData>
      </branch>
      */
		LineBranchXmlType line = parser.getLineBranch("Bus19", "Bus18", "2");
		assertTrue(line.isOffLine());
		assertTrue(line.getZ().getIm() == 1.0E-5);
		
		/*
      <branch id="Bus26_to_Bus54_cirId_1" circuitId="1" name="        " offLine="false">
        <ownerList>
          <owner id="1" ownership="1.0"/>
        </ownerList>
        <fromBus idRef="Bus26"/>
        <toBus idRef="Bus54"/>
        <loadflowData code="Transformer" xfr3W="false">
          <z re="3.5E-4" im="0.0091" unit="PU"/>
          <fromTap value="0.975" unit="PU"/>
          <toTap value="1.0" unit="PU"/>
          <meterLocation>ToSide</meterLocation>
          <xfrInfo>
            <ratedPower12 value="100.0" unit="MVA"/>
            <dataOnSystemBase>true</dataOnSystemBase>
          </xfrInfo>
          <branchRatingLimit>
            <mva rating1="363.0" rating2="398.0" rating3="510.0" unit="MVA"/>
          </branchRatingLimit>
        </loadflowData>
      </branch>
		 */
		XfrBranchXmlType xfr = parser.getXfrBranch("Bus26", "Bus54", "1");
		assertTrue(!xfr.isOffLine());
		assertTrue(xfr.getZ().getIm() == 0.0091);
		assertTrue(xfr.getFromTurnRatio().getValue() == 0.975);
		assertTrue(xfr.getToTurnRatio().getValue() == 1.0);
		assertTrue(xfr.getXfrInfo().getRatedPower().getValue() == 100.0);
		assertTrue(xfr.getRatingLimit().getMva().getRating1() == 363.0);
		/*
	      <branch id="Bus27824_to_Bus27871_n_Bus27957_cirId_W" circuitId="W" name="D575121     " offLine="false">
          <aclf3WPSXfr wind1OffLine="false" wind2OffLine="false" wind3OffLine="false" circuitId="W" id="Bus27824_to_Bus27871_n_Bus27857_cirId_W" areaNumber="1" zoneNumber="1" offLine="false" name="D575121     ">
	        <ownerList>
	          <owner id="1" ownership="1.0"/>
	        </ownerList>
	        <fromBus idRef="Bus27824"/>
	        <toBus idRef="Bus27871"/>
	        <tertiaryBus idRef="Bus27957"/>
	        <loadflowData code="PhaseShiftXformer" xfr3W="true">
	          <z re="0.00133" im="0.11469" unit="PU"/>
	          <fromShuntY re="8.9E-4" im="-0.00448" unit="PU"/>
	          <fromTap value="0.9777805555555555" unit="PU"/>
	          <toTap value="1.0" unit="PU"/>
	          <fromAngle value="0.0" unit="DEG"/>
	          <toAngle value="0.0" unit="DEG"/>
	          <meterLocation>FromSide</meterLocation>
	          <xfrInfo>
	            <dataOnSystemBase>false</dataOnSystemBase>
	            <z23 re="0.00124" im="0.02795" unit="PU"/>
	            <z31 re="0.00108" im="0.06264" unit="PU"/>
	            <tap3 value="1.0" unit="PU"/>
	            <shiftAngle3 value="-30.0" unit="DEG"/>
	            <starVMag value="1.00436" unit="PU"/>
	            <starVAng value="-9.5049" unit="DEG"/>
	            <ratedVoltage1 value="360.0" unit="KV"/>
	            <ratedVoltage2 value="137.5" unit="KV"/>
	            <ratedVoltage3 value="34.5" unit="KV"/>
	            <ratedPower12 value="90.0" unit="MVA"/>
	            <ratedPower23 value="22.29" unit="MVA"/>
	            <ratedPower31 value="22.29" unit="MVA"/>
	            <branchRatingLimit23>
	              <mva rating1="150.0" rating2="150.0" rating3="150.0" unit="MVA"/>
	            </branchRatingLimit23>
	            <branchRatingLimit13>
	              <mva rating1="22.29" rating2="22.29" rating3="22.29" unit="MVA"/>
	            </branchRatingLimit13>
	          </xfrInfo>
	          <branchRatingLimit>
	            <mva rating1="150.0" rating2="150.0" rating3="150.0" unit="MVA"/>
	          </branchRatingLimit>
	        </loadflowData>
	      </branch>
	      */
		PSXfr3WBranchXmlType psXfr3W = parser.getPSXfr3WBranch("Bus27824", "Bus27871", "Bus27957", "W");
		//assertTrue(!psXfr3W.isOffLine());
		assertTrue(psXfr3W.getZ().getRe() == 0.00133);
		assertTrue(psXfr3W.getFromAngle().getValue() == 0.0);
		assertTrue(psXfr3W.getTertShiftAngle().getValue() == -30.0);
		assertTrue(psXfr3W.getXfrInfo().getFromRatedVoltage().getValue() == 360.0);
		assertTrue(psXfr3W.getXfrInfo().getRatedPower().getValue() == 90.0);
		assertTrue(psXfr3W.getRatingLimit23().getMva().getRating1() == 150.0);
		
		/*
      <dcLint2T id="Bus615600_to_Bus615353_cirId_1" number="1">
        <controlMode>power</controlMode>
        <lineZ re="13.75" im="0.0" unit="OHM"/>
        <powerDemand value="552.0" unit="MW"/>
        <controlOnRectifierSide>true</controlOnRectifierSide>
        <scheduledDCVoltage value="410.0" unit="KV"/>
        <meterdEnd>inverter</meterdEnd>
        <modeSwitchDCVoltage value="-1.0" unit="KV"/>
        <compoundingR re="13.75" im="0.0" unit="OHM"/>
        <powerOrCurrentMarginPU>0.1</powerOrCurrentMarginPU>
        <minDCVoltage value="0.0" unit="KV"/>
        <rectifier>
          <type>rectifier</type>
          <busId idRef="Bus615600"/>
          <numberofBridges>2</numberofBridges>
          <minFiringAngle value="15.0" unit="DEG"/>
          <maxFiringAngle value="19.0" unit="DEG"/>
          <acSideRatedVoltage value="230.0" unit="KV"/>
          <commutatingZ re="0.0" im="21.6" unit="OHM"/>
          <commutatingCapacitor>0.0</commutatingCapacitor>
          <xformerTurnRatio>0.7426</xformerTurnRatio>
          <xformerTapSetting value="0.975" unit="PU"/>
          <xformerTapLimit max="1.2" min="0.9" unit="PU"/>
          <xformerTapStepSize>0.0125</xformerTapStepSize>
        </rectifier>
        <inverter>
          <type>inverter</type>
          <busId idRef="Bus615353"/>
          <numberofBridges>2</numberofBridges>
          <minFiringAngle value="18.0" unit="DEG"/>
          <maxFiringAngle value="18.0" unit="DEG"/>
          <acSideRatedVoltage value="345.0" unit="KV"/>
          <commutatingZ re="0.0" im="19.8" unit="OHM"/>
          <commutatingCapacitor>0.0</commutatingCapacitor>
          <xformerTurnRatio>0.4714</xformerTurnRatio>
          <xformerTapSetting value="0.95221" unit="PU"/>
          <xformerTapLimit max="1.2125" min="0.9125" unit="PU"/>
          <xformerTapStepSize>1.0E-5</xformerTapStepSize>
        </inverter>
      </dcLint2T>
      		 */
		//System.out.println(parser.getObjectCache());
		DCLineData2TXmlType dcLine = parser.getDcLine2TRecord("Bus615600", "Bus615353", 1);
		assertTrue(dcLine.getControlMode() == DcLineControlModeEnumType.POWER);
		assertTrue(dcLine.getPowerDemand().getValue() == 552.0);
		assertTrue(dcLine.getPowerOrCurrentMarginPU() == 0.1);
		assertTrue(dcLine.getRectifier().getNumberofBridges() == 2);
		
		assertTrue(dcLine.getInverter().getMaxFiringAngle().getValue() == 18.0);
		assertTrue(dcLine.getInverter().getMinFiringAngle().getValue() == 18.0);
		assertTrue(dcLine.getInverter().getAcSideRatedVoltage().getValue() == 345.0);
	}
}


