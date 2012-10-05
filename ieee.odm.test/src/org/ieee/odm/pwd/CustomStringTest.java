package org.ieee.odm.pwd;

import static org.junit.Assert.assertTrue;

import org.ieee.odm.adapter.IODMAdapter;
import org.ieee.odm.adapter.pwd.PowerWorldAdapter;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.schema.LineBranchXmlType;
import org.ieee.odm.schema.LoadflowBusXmlType;
import org.ieee.odm.schema.NameValuePairXmlType;
import org.ieee.odm.schema.XfrBranchXmlType;
import org.junit.Test;

public class CustomStringTest {
	
	@Test
	public void SixBusBase_CustromString_test(){
		IODMAdapter adapter = new PowerWorldAdapter();
		assertTrue(adapter.parseInputFile("testdata/pwd/SixBusTestCase.aux"));
		AclfModelParser parser=(AclfModelParser) adapter.getModel();
		//parser.stdout();

        //Gen custom String
		/*
		 * DATA (GEN, [CustomString,CustomString:1,CustomString:2,BusNum,GenID,GenStatus,GenMW,GenMVR,
            GenVoltSet,GenAGCAble,GenAVRAble,GenMWMin,GenMWMax,GenMVRMin,GenMVRMax,
            GenUseCapCurve,GenRegNum,GenParFac,GenRMPCT,CustomSingle,GenOPFFastStart,
            CustomSingle:1,CustomSingle:2,CustomSingle:3,CustomSingle:4])
            {
             "Sub1_14.9_G1" "G1" "DZONE_1"    11 "1"
		 */
		LoadflowBusXmlType g1=parser.getAclfBus("Bus11");
		assertTrue(g1.getNvPair().get(0).getName().equals("Station"));
		assertTrue(g1.getNvPair().get(0).getValue().equals("Sub1"));
		
		assertTrue(g1.getNvPair().get(1).getName().equals("EquimentName"));
		assertTrue(g1.getNvPair().get(1).getValue().equals("G1"));
		/*
		for(NameValuePairXmlType nv: g1.getNvPair()){
			System.out.println ("nv pair--name, value = " +nv.getName()+"  , "+nv.getValue());
		}
		*/
		
		//Branch custom string
		/*
		 * DATA (BRANCH, [CustomString,CustomString:1,CustomString:2,BusNum,BusNum:1,LineCircuit,
            {
            "Line" "Sub2_230_L25" "L25"    25    52 "1"
		 */
		//Xfr Bus25_to_Bus52_cirId_1
		/*
		 * <nvPair>
                    <name>Station</name>
                    <value>Sub2</value>
                </nvPair>
                <nvPair>
                    <name>EquimentName</name>
                    <value>L25</value>
                </nvPair>
		 */
		LineBranchXmlType L25=parser.getLineBranch("Bus25", "Bus52", "1");
		assertTrue(L25.getNvPair().get(0).getName().equals("Station"));
		assertTrue(L25.getNvPair().get(0).getValue().equals("Sub2"));
		assertTrue(L25.getNvPair().get(1).getName().equals("EquimentName"));
		assertTrue(L25.getNvPair().get(1).getValue().equals("L25"));
		
		
	}
}
