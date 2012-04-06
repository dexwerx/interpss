package org.ieee.odm.pwd;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.ieee.odm.adapter.IODMAdapter;
import org.ieee.odm.adapter.pwd.PowerWorldAdapter;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.schema.LineBranchXmlType;
import org.ieee.odm.schema.LoadflowBusXmlType;
import org.ieee.odm.schema.XfrBranchXmlType;
import org.junit.Test;

public class PwdAdapterTest {
	/*
	 * 25/03/2012 now test to Xformer, since the following two parts seems to be 
	 * only supplement data, we can skip xformer and 3wxformer and continue
	 */
	
	@Test
	public void B5Rtest(){
		IODMAdapter adapter = new PowerWorldAdapter();
		assertTrue(adapter.parseInputFile("testdata/powerworld/B5R.aux"));
		AclfModelParser parser=(AclfModelParser) adapter.getModel();
		//parser.stdout();
		//check network data
		assertTrue(parser.getAclfNet().getBasePower().getValue()==100.0);
		
		assertTrue(parser.getAclfNet().getBusList().getBus().size()==6);
		assertTrue(parser.getAclfNet().getBranchList().getBranch().size()==8);
		
		//check bus data
		LoadflowBusXmlType bus1=(LoadflowBusXmlType) parser.getBus("Bus1");
		assertTrue(bus1.getGenData().getEquivGen().getDesiredVoltage().getValue()==1.0);
		assertTrue(bus1.getGenData().getEquivGen().getPower().getRe()-100.01<1E-4);
		assertTrue(bus1.getGenData().getEquivGen().getQLimit().getMax()-99998.999<1E-4);
		assertTrue(bus1.getGenData().getEquivGen().getQLimit().getMin()-(-99998.999)<1E-4);
		assertTrue(bus1.getGenData().getEquivGen().getPLimit().getMax()-800.0<1E-4);
		assertTrue(bus1.getGenData().getEquivGen().getPLimit().getMin()==0.0);
		assertTrue(bus1.getLoadData().getEquivLoad().getConstPLoad().getRe()==100.0);
		assertTrue(bus1.getLoadData().getEquivLoad().getConstPLoad().getIm()==0.0);
		//check line data
		LineBranchXmlType line=(LineBranchXmlType) parser.getAclfNet().getBranchList().getBranch().get(0).getValue();
		assertTrue(line.getZ().getRe()==0.03);
		assertTrue(line.getZ().getIm()==0.16);
		//check transformer data
		/*
		 * [BusNum,BusNum:1,LineCircuit,LineStatus,LineR,LineX,LineC,LineG,LineAMVA,LineBMVA,
             LineCMVA,LineShuntMW,LineShuntMW:1,LineShuntMVR,LineShuntMVR:1,LineXfmr,LineTap,
             LinePhase,SeriesCapStatus]
		 *   4     5 " 1" "Closed"  0.000000  0.100000  0.000000  0.000000  1000.000  1000.000  1000.000     0.000     0.000     0.000     0.000  "YES"    0.993750   0.000000 "Not Bypassed"
		 */
		XfrBranchXmlType xfr=(XfrBranchXmlType) parser.getBranch("Bus4","Bus5"," 1");
		//TODO Both b and g are zero, so MagnitizingY are not set.
//		assertTrue(xfr.getMagnitizingY().getIm()==0.0);
//		assertTrue(xfr.getMagnitizingY().getRe()==0.0);
		assertTrue(xfr.getZ().getIm()==0.100000);
		assertTrue(xfr.getZ().getRe()==0.000000);
		assertTrue(xfr.getFromTurnRatio().getValue()==0.993750);
		assertTrue(xfr.getToTurnRatio().getValue()==1.0);
		
		
		
	}
	@Test
	public void IEEE14Bustest(){
		IODMAdapter adapter = new PowerWorldAdapter();
		assertTrue(adapter.parseInputFile("testdata/powerworld/ieee14.aux"));
		AclfModelParser parser=(AclfModelParser) adapter.getModel();
		
		parser.stdout();
		
		//check network data
		assertTrue(parser.getAclfNet().getBasePower().getValue()==100.0);
		
		assertTrue(parser.getAclfNet().getBusList().getBus().size()==18);
		assertTrue(parser.getAclfNet().getBranchList().getBranch().size()==24);
		
		//check bus data
		LoadflowBusXmlType bus2=(LoadflowBusXmlType) parser.getBus("Bus2");
		assertTrue(bus2.getBaseVoltage().getValue()==132.00);
		assertTrue(bus2.isOffLine()==false);
		
		assertTrue(bus2.getGenData().getEquivGen().getDesiredVoltage().getValue()==1.045000);
		assertTrue(bus2.getGenData().getEquivGen().getPower().getRe()-40<1E-4);
		assertTrue(bus2.getGenData().getEquivGen().getQLimit().getMax()-50.0<1E-4);
		assertTrue(bus2.getGenData().getEquivGen().getQLimit().getMin()-(-40.0)<1E-4);
		assertTrue(bus2.getGenData().getEquivGen().getPLimit().getMax()-10000.0<1E-4);
		assertTrue(bus2.getGenData().getEquivGen().getPLimit().getMin()==-10000.0);
		
		assertTrue(bus2.getLoadData().getEquivLoad().getConstPLoad().getRe()==21.700);
		assertTrue(bus2.getLoadData().getEquivLoad().getConstPLoad().getIm()==12.700);
		
		//check line data
		/*BusNum,BusNum:1,LineCircuit,LineStatus,LineR,LineX,LineC,LineG
		 * 15    2 " 1" "Closed"  0.019380  0.059170  0.052800  0.000000
		 */
		LineBranchXmlType line=(LineBranchXmlType) parser.getBranch("Bus15","Bus2"," 1");
		assertTrue(line.getZ().getRe()==0.019380);
		assertTrue(line.getZ().getIm()==0.059170);
		assertTrue(line.getTotalShuntY().getIm()==0.052800);
		assertTrue(line.getTotalShuntY().getRe()==0.0);
		//check transformer data
		/*
		 * [BusNum,BusNum:1,LineCircuit,LineStatus,LineR,LineX,LineC,LineG]
		
            4     7 " 1" "Closed"  0.000000  0.209120  0.000000  0.000000     0.000     0.000     0.000     0.000     0.000     0.000     0.000     0.000    0.0000    0.0000    0.0000    0.0000 0.978000   0.000000 "Not Bypassed" "Closed" "Not Bypassed" "NO " "From" "" "Default" "YES" "No" "NO " "NO "   0.0000      1 100.0000   0.0000   0.0000 "Transformer" "" "NO" "NO" "NO" "NO" "NO " "NO " "NO " "NO " "NO " "NO "
            4     9 " 1" "Closed"  0.000000  0.556180  0.000000  0.000000     0.000     0.000     0.000     0.000     0.000     0.000     0.000     0.000    0.0000    0.0000    0.0000    0.0000 0.969000   0.000000 "Not Bypassed" "Closed" "Not Bypassed" "NO " "From" "" "Default" "YES" "No" "NO " "NO "   0.0000      1 100.0000   0.0000   0.0000 "Transformer" "" "NO" "NO" "NO" "NO" "NO " "NO " "NO " "NO " "NO " "NO "
            5     6 " 1" "Closed"  0.000000  0.252020  0.000000  0.000000     0.000     0.000     0.000     0.000     0.000     0.000     0.000     0.000    0.0000    0.0000    0.0000    0.0000 0.932000   0.000000 "Not Bypassed" "Closed" "Not Bypassed" "NO " "From" "" "Default" "YES" "No" "NO " "NO "   0.0000      1 100.0000   0.0000   0.0000 "Transformer" "" "NO" "NO" "NO" "NO" "NO " "NO " "NO " "NO " "NO " "NO "
		 */
		XfrBranchXmlType xfr=(XfrBranchXmlType) parser.getBranch("Bus4","Bus7"," 1");
		//TODO Both b and g are zero, so MagnitizingY are not set.
//		assertTrue(xfr.getMagnitizingY().getIm()==0.0);
//		assertTrue(xfr.getMagnitizingY().getRe()==0.0);
		assertTrue(xfr.getZ().getIm()==0.209120);
		assertTrue(xfr.getZ().getRe()==0.000000);
		assertTrue(xfr.getFromTurnRatio().getValue()==0.978000);
		assertTrue(xfr.getToTurnRatio().getValue()==1.0);
		
	}
}
