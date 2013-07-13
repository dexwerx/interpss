package org.ieee.odm.psse;

import static org.junit.Assert.assertTrue;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.ieee.odm.adapter.IODMAdapter.NetType;
import org.ieee.odm.adapter.psse.PSSEAdapter;
import org.ieee.odm.adapter.psse.PSSEAdapter.PsseVersion;
import org.ieee.odm.common.ODMException;
import org.ieee.odm.model.dstab.DStabModelParser;
import org.ieee.odm.schema.AnalysisCategoryEnumType;
import org.ieee.odm.schema.DStabBusXmlType;
import org.ieee.odm.schema.DStabGenDataXmlType;
import org.ieee.odm.schema.Eq11MachineXmlType;
import org.ieee.odm.schema.LFGenCodeEnumType;
import org.ieee.odm.schema.LFLoadCodeEnumType;
import org.junit.Test;

public class PSSEV30_IEEE9_Dstab_Test {
	
	@Test
	public void ieee9_ODM_Dstab_test() throws ODMException{
	final LogManager logMgr = LogManager.getLogManager();
	Logger logger = Logger.getLogger("IEEE ODM Logger");
	logger.setLevel(Level.INFO);
	logMgr.addLogger(logger);
	
	PSSEAdapter adapter = new PSSEAdapter(PsseVersion.PSSE_30);
	assertTrue(adapter.parseInputFile(NetType.DStabNet, new String[]{
			"testData/psse/IEEE9Bus/ieee9.raw",
			"testData/psse/IEEE9Bus/ieee9.seq",
			"testData/psse/IEEE9Bus/ieee9_dyn_onlyGen.dyr"
	}));
	DStabModelParser dstabParser =(DStabModelParser) adapter.getModel();
	dstabParser.stdout();
	//caseContentInfo
	assertTrue(dstabParser.getStudyCase().getAnalysisCategory() == AnalysisCategoryEnumType.TRANSIENT_STABILITY);
	
	/*
	 *  <dstabBus scCode="Contributing" id="Bus1" areaNumber="1" zoneNumber="1" number="1" offLine="false" name="BUS-1       ">
                <ownerList id="1">
                    <ownership unit="PU" value="1.0"/>
                </ownerList>
                <baseVoltage unit="KV" value="16.5"/>
                <voltage unit="PU" value="1.04"/>
                <angle unit="DEG" value="0.0"/>
                <genData>
                    <dstabEquivGen code="Swing">
                        <power unit="MVA" re="71.64" im="27.05"/>
                        <desiredVoltage unit="PU" value="1.04"/>
                        <desiredAngle unit="DEG" value="0.0"/>
                        <pLimit unit="MW" max="9999.0" min="-9999.0"/>
                        <potiveZ unit="PU" re="0.0" im="0.04"/>
                        <negativeZ unit="PU" re="0.0" im="0.04"/>
                        <zeroZ unit="PU" re="0.0" im="0.04"/>
                    </dstabEquivGen>
                    <dstabContributeGen id="1" offLine="false" name="Gen:1(1)">
                        <desc>PSSE Generator 1 at Bus 1</desc>
                        <power unit="MVA" re="71.64" im="27.05"/>
                        <desiredVoltage unit="PU" value="1.04"/>
                        <qLimit unit="MVAR" max="9999.0" min="-9999.0"/>
                        <pLimit unit="MW" max="9999.0" min="-9999.0"/>
                        <mvaBase unit="MVA" value="100.0"/>
                        <sourceZ unit="PU" re="0.0" im="0.04"/>
                        <mvarVControlParticipateFactor>1.0</mvarVControlParticipateFactor>
                        <potiveZ unit="PU" re="0.0" im="0.04"/>
                        <negativeZ unit="PU" re="0.0" im="0.04"/>
                        <zeroZ unit="PU" re="0.0" im="0.04"/>
                        <eq11MachModel>
                            <desc>GENSAL</desc>
                            <H>23.64</H>
                            <D>0.0</D>
                            <ra>0.0</ra>
                            <xd>0.146</xd>
                            <xq>0.0969</xq>
                            <xd1>0.0608</xd1>
                            <Td01 unit="Sec" value="8.96"/>
                            <seFmt1>
                              <se100>0.0</se100>
                              <se120>0.0</se120>
                              <sliner>1.0</sliner>
                            </seFmt1>
                            <xq11>0.04</xq11>
                            <Tq011 unit="Sec" value="0.06"/>
                            <xd11>0.04</xd11>
                            <Td011 unit="Sec" value="0.04"/>
                        </eq11MachModel>
                    </dstabContributeGen>
                </genData>
                <loadData>
                    <dstabEquivLoad code="NoneLoad"/>
                </loadData>
            </dstabBus>
	 */

	/*
	 * Bus 1
	 * Gen GENSAL-> Eq11Machine
	 */
	 DStabBusXmlType bus1 = dstabParser.getDStabBus("Bus1");
	 assertTrue(bus1.getGenData().getEquivGen().getValue().getCode()==LFGenCodeEnumType.SWING);
	 assertTrue(bus1.getLoadData().getEquivLoad().getValue().getCode()==LFLoadCodeEnumType.NONE_LOAD);
	 
	DStabGenDataXmlType bus1Gen = (DStabGenDataXmlType) bus1.getGenData().getEquivGen().getValue();
	
	assertTrue(bus1Gen.getDesiredVoltage().getValue()==1.04);
	assertTrue(bus1Gen.getPotiveZ().getIm()==0.04);
	
	DStabGenDataXmlType contriGen = (DStabGenDataXmlType) bus1.getGenData().getContributeGen().get(0).getValue();
	assertTrue(contriGen.getMvaBase().getValue()==100.0);
	assertTrue(contriGen.getMvarVControlParticipateFactor()==1.0);
	
	Eq11MachineXmlType machine = (Eq11MachineXmlType) contriGen.getMachineModel().getValue();
	assertTrue(machine.getD()==0.0);
	assertTrue(machine.getH()==23.64);
	
	assertTrue(machine.getXl()< machine.getXd1());
	
	assertTrue(machine.getXd()==0.146);
	assertTrue(machine.getXl()==0.0336);
	assertTrue(machine.getXd1()==0.0608);
	assertTrue(machine.getXd11()==0.04);
	
	assertTrue(machine.getXd11()==machine.getXq11());
	
	assertTrue(machine.getTd011().getValue()==0.04);
	assertTrue(machine.getTq011().getValue()==0.06);
	
	
	
	
	
	}
}
