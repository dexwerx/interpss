package org.interpss.odm.acsc;

import static org.junit.Assert.assertTrue;

import org.ieee.odm.adapter.IODMAdapter.NetType;
import org.ieee.odm.adapter.psse.PSSEAdapter;
import org.ieee.odm.adapter.psse.PSSEAdapter.PsseVersion;
import org.ieee.odm.model.acsc.AcscModelParser;
import org.interpss.IpssCorePlugin;
import org.interpss.mapper.odm.ODMAcscDataMapper;
import org.interpss.numeric.exp.IpssNumericException;
import org.interpss.numeric.sparse.ISparseEqnComplex;
import org.junit.Test;

import com.interpss.CoreObjectFactory;
import com.interpss.common.exp.InterpssException;
import com.interpss.core.acsc.AcscNetwork;
import com.interpss.core.acsc.SequenceCode;
import com.interpss.core.algo.AclfMethod;
import com.interpss.core.algo.LoadflowAlgorithm;


public class IEEE9Bus_Acsc_test {
	
	@Test
	public void testIeee9Bus() throws InterpssException, IpssNumericException{
			IpssCorePlugin.init();
			PSSEAdapter adapter = new PSSEAdapter(PsseVersion.PSSE_30);
			assertTrue(adapter.parseInputFile(NetType.AcscNet, new String[]{
					"testData/psse/v30/IEEE9Bus/ieee9.raw",
					"testData/psse/v30/IEEE9Bus/ieee9.seq"
			}));
			AcscModelParser acscParser =(AcscModelParser) adapter.getModel();
			acscParser.stdout();
			
			AcscNetwork net = new ODMAcscDataMapper().map2Model(acscParser).getAcscNet();
			
			//set the order in original sequence for better testing
			for(int i=1;i<=net.getNoBus();i++){
				net.getBus("Bus"+i).setSortNumber(i-1);
			}
			net.setBusNumberArranged(true);
			
			LoadflowAlgorithm algo = CoreObjectFactory.createLoadflowAlgorithm(net);
		  	algo.setLfMethod(AclfMethod.PQ);
		  	algo.getLfAdjAlgo().setApplyAdjustAlgo(false);
		  	algo.loadflow();
	  	
	  		assertTrue( net.isLfConverged());
	  		//System.out.println(AclfOutFunc.loadFlowSummary(net));
	  		//System.out.println(net.net2String());
	  		/*
	  		 * ***********************************
	  		 *       Positive sequence
	  		 * ***********************************      
	  		 * 
	  		 */
	  		
	        ISparseEqnComplex posYMatrix = net.formYMatrix(SequenceCode.POSITIVE, false);
	        
	        //Gen Bus: Bus 1
	        //Yii: 0.0 + (-42.63668430335097i)
	        assertTrue(posYMatrix.getA(0, 0).getReal()==0);
	        assertTrue(Math.abs(posYMatrix.getA(0, 0).getImaginary()+42.6366)<1.0E-4);
	        //Yij (bus1->bus4): -0.0 + (17.636684303350968i)
	        assertTrue(posYMatrix.getA(0, 3).getReal()==0);
	        assertTrue(Math.abs(posYMatrix.getA(0, 3).getImaginary()-17.6366)<1.0E-4);
	        
	        //Load Bus: Bus 5
	        //Yii: 3.81 - j17.84
	        assertTrue(Math.abs(posYMatrix.getA(4, 4).getReal()-3.81)<1.0E-2);
	        assertTrue(Math.abs(posYMatrix.getA(4, 4).getImaginary()+17.84)<1.0E-2);
	        
	        //Y54:-1.37 + j11.60
	        assertTrue(Math.abs(posYMatrix.getA(4, 3).getReal()+1.37)<1.0E-2);
	        assertTrue(Math.abs(posYMatrix.getA(4, 3).getImaginary()-11.60)<1.0E-2);
	        
	        //Non-Gen, Non-Load: Bus7
	        //Yii 2.80 - j35.45
	        assertTrue(Math.abs(posYMatrix.getA(6, 6).getReal()-2.80)<1.0E-2);
	        assertTrue(Math.abs(posYMatrix.getA(6, 6).getImaginary()+35.45)<1.0E-2);
	       
			
	        /*
	  		 * ***********************************
	  		 *       Zero sequence
	  		 * ***********************************
	  		 * Gen Bus 1,2 and 3 is open from the sequence network
	  		 * 
	  		 */
	        
	        ISparseEqnComplex zeroYMatrix = net.formYMatrix(SequenceCode.ZERO, false);
	      //Load Bus: Bus 5
	        //Yii: 1.0211168370406916 + (-6.79069203867941i)
	        assertTrue(Math.abs(zeroYMatrix.getA(4, 4).getReal()-1.02)<1.0E-2);
	        assertTrue(Math.abs(zeroYMatrix.getA(4, 4).getImaginary()+6.79)<1.0E-2);
	        
	        //Y54: -0.5460750853242321 + (4.641638225255973i)
	        assertTrue(Math.abs(zeroYMatrix.getA(4, 3).getReal()+0.54)<1.0E-2);
	        assertTrue(Math.abs(zeroYMatrix.getA(4, 3).getImaginary()-4.64)<1.0E-2);
	        
	        //Non-Gen, Non-Load: Bus7
	        //Yii 1.1218907410149137 + (-23.641745252186816i)
	        assertTrue(Math.abs(zeroYMatrix.getA(6, 6).getReal()-1.12)<1.0E-2);
	        assertTrue(Math.abs(zeroYMatrix.getA(6, 6).getImaginary()+23.64)<1.0E-2);
	        
		}
	

}
