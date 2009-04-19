/*
 * @(#)BPADynamicRecord.java   
 *
 * Copyright (C) 2006-2008 www.interpss.org
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
 * @Author Stephen Hau
 * @Version 1.0
 * @Date 02/11/2008
 * 
 *   Revision History
 *   ================
 *
 */

package org.ieee.pes.odm.pss.adapter.bpa;

import org.ieee.cmte.psace.oss.odm.pss.schema.v1.CurrentXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.FaultCategoryXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.FaultListXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.PercentXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.PowerXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.TimePeriodXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.TransientSimulationXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.VoltageXmlType;
import org.ieee.cmte.psace.oss.odm.pss.schema.v1.ZXmlType;
import org.ieee.pes.odm.pss.model.ODMData2XmlHelper;
import org.ieee.pes.odm.pss.model.StringUtil;




public class BPADynamicFaultOperationRecord {
	
public static void processFaultOperationData(String str,TransientSimulationXmlType tranSimu
		            ,BPAAdapter adapter){ 
    	
    	int mode =new Integer(str.substring(35, 37).trim()).intValue();
    	final String strAry[] = getFaultOperationDataFields(str, mode,adapter);
    	
    	if(mode==1||mode==2||mode==3||mode==-1||mode==-2||mode==-3){    		
    		
    		boolean breaker1Opened=true;    		
    		if(strAry[1].equals("")){
    			breaker1Opened=false;
    		}else{
    			breaker1Opened=true;
    		}
    		String bus1=strAry[2];
    		double bus1RatedV=0.0;    		
    		if(!strAry[3].equals("")){
    			bus1RatedV= new Double(strAry[3]).doubleValue();   			
    		}
    		boolean breaker2Opened=true;
    		if(strAry[4].equals("")){
    			breaker2Opened=false;
    		}else{
    			breaker2Opened=true;
    		}
    		String bus2=strAry[5];
    		double bus2RatedV=0.0;
    		if(!strAry[6].equals("")){
    			bus2RatedV= new Double(strAry[6]).doubleValue();   			
    		}
    		
    		String parId="";
    		if(!strAry[7].equals("")){
    			parId=strAry[7];   			
    		}
    		double operationTime=0.0;
    		if(!strAry[9].equals("")){
    			operationTime=new Double(strAry[9]);
    		}    		
    		double  r=0.0, x=0.0;
    		if(!strAry[10].equals("")){    			
    			r= new Double(strAry[10]).doubleValue();
    		}
    		if(!strAry[11].equals("")){
    			r= new Double(strAry[11]).doubleValue();
    		}
    		double faultLocation=0.0;
    		if(!strAry[12].trim().equals("")){
    			faultLocation= new Double(strAry[11]).doubleValue();
    		} 
    		// bus fault
    		if(mode==1||mode==-1||mode==2||mode==-2){ 
    			FaultListXmlType.Fault fault=
					ODMData2XmlHelper.getFaultRecord(tranSimu, FaultListXmlType.Fault.FaultType.BUS_FAULT,
							bus1, bus2);
    			//FaultListXmlType.Fault.BusFault busFault=fault.addNewBusFault();
    			
    			FaultListXmlType.Fault.BusFault busFault=
	    		 ODMData2XmlHelper.getBusFaultRecord(tranSimu, bus1, bus2);
    			
    			fault.setFaultType(FaultListXmlType.Fault.FaultType.BUS_FAULT);
				busFault.setFaultCategory(FaultCategoryXmlType.X_3_PHASE);
				// not permanent bus fault;
               if(mode==1||mode==-1){            	   
	    			
	    			if(breaker1Opened==false&&breaker2Opened==false){	    				
	    				
	    				ODMData2XmlHelper.setTimePeriodData(busFault.addNewFaultStartTime(), 
	    						operationTime, TimePeriodXmlType.Unit.CYCLE);  
	    				
	        			busFault.addNewFaultedBus().setName(bus1);
	        			ODMData2XmlHelper.setVoltageData(busFault.addNewFaultedBusRatedV(),
	        					bus1RatedV, VoltageXmlType.Unit.KV);
	        			busFault.addNewRemoteEndBus().setName(bus2);
	        			ODMData2XmlHelper.setVoltageData(busFault.addNewRemoteEndBusRatedV(),
	        					bus2RatedV, VoltageXmlType.Unit.KV);        			
	        			
	    			}else if(breaker1Opened==true&&breaker2Opened==false){	    				
	    				if(busFault!=null){
	    					ODMData2XmlHelper.setTimePeriodData(busFault.addNewFirstOperationTime(),
	        						operationTime, TimePeriodXmlType.Unit.CYCLE);
	    					busFault.setPermanentFault(true);
	            			if(mode==-1){
	            				double duration= busFault.getFirstOperationTime().getValue()-
	            				        busFault.getFaultStartTime().getValue();
	            				ODMData2XmlHelper.setTimePeriodData(busFault.addNewFaultDurationTime(), 
	            						duration, TimePeriodXmlType.Unit.CYCLE);
	            				busFault.setPermanentFault(false);
	            			} 
	    				}
	    				
	    			}else if(breaker1Opened==true&&breaker2Opened==true){
	    				if(busFault!=null){
	    					busFault.setPermanentFault(true);
	    					ODMData2XmlHelper.setTimePeriodData(busFault.addNewSecondOperationTime(),
	        						operationTime, TimePeriodXmlType.Unit.CYCLE);
	        				if(mode==-1){	        					
	            				double duration= busFault.getSecondOperationTime().getValue()-
	            				        busFault.getFaultStartTime().getValue();
	            				ODMData2XmlHelper.setTimePeriodData(busFault.addNewFaultDurationTime(), 
	            						duration, TimePeriodXmlType.Unit.CYCLE);
	            				busFault.setPermanentFault(false);
	            			}	            			
	    				}
	    			}
	    			// permanent bus fault
	    		}else if(mode==2||mode==-2){
	    			if(mode==2){	                    
	    				ODMData2XmlHelper.setTimePeriodData(busFault.addNewFaultStartTime(), 
	    						operationTime, TimePeriodXmlType.Unit.CYCLE);  
	    				busFault.setFaultCategory(FaultCategoryXmlType.X_3_PHASE);
	        			busFault.addNewFaultedBus().setName(bus1);
	        			ODMData2XmlHelper.setVoltageData(busFault.addNewFaultedBusRatedV(),
	        					bus1RatedV, VoltageXmlType.Unit.KV);
	        			busFault.addNewRemoteEndBus().setName(bus2);
	        			ODMData2XmlHelper.setVoltageData(busFault.addNewRemoteEndBusRatedV(),
	        					bus2RatedV, VoltageXmlType.Unit.KV);    
	        			busFault.setPermanentFault(true);
	        			
	    			}else{	
	    				if(busFault!=null){
	    					ODMData2XmlHelper.setTimePeriodData(busFault.addNewFirstOperationTime(),
	        						operationTime, TimePeriodXmlType.Unit.CYCLE);
	    					double duration= busFault.getFirstOperationTime().getValue()-
					        busFault.getFaultStartTime().getValue();
					        ODMData2XmlHelper.setTimePeriodData(busFault.addNewFaultDurationTime(), 
							duration, TimePeriodXmlType.Unit.CYCLE);
					        busFault.setPermanentFault(false);
	            			}
	    				}
	    			}
				if(r!=0.0||x!=0.0){
	    				ODMData2XmlHelper.setZValue(busFault.addNewFaultZ(), r, x, ZXmlType.Unit.PU);
	    		}
				// branch fault
    		}else if(mode==3||mode==-3){
    			FaultListXmlType.Fault fault=
					ODMData2XmlHelper.getFaultRecord(tranSimu, FaultListXmlType.Fault.FaultType.BRANCH_FAULT,
							bus1, bus2);
    			FaultListXmlType.Fault.BranchFault braFault=
	    			ODMData2XmlHelper.getBranchFaultRecord(tranSimu, bus1, bus2);				
				fault.setFaultType(FaultListXmlType.Fault.FaultType.BRANCH_FAULT);
				braFault.setFaultCategory(FaultCategoryXmlType.X_3_PHASE);
				
				if(breaker1Opened==false&&breaker2Opened==false){    				
        			braFault.addNewFromBus().setName(bus1);
        			ODMData2XmlHelper.setVoltageData(braFault.addNewFromBusRatedV(), 
        					bus1RatedV, VoltageXmlType.Unit.KV);
        			braFault.addNewFromBus().setName(bus2);
        			ODMData2XmlHelper.setVoltageData(braFault.addNewToBusRatedV(), 
        					bus2RatedV, VoltageXmlType.Unit.KV);
        			ODMData2XmlHelper.setTimePeriodData(braFault.addNewFaultStartTime(), 
    						operationTime, TimePeriodXmlType.Unit.CYCLE);
        			braFault.addNewFaultLocationFromFromSide().setValue(faultLocation);
        			braFault.getFaultLocationFromFromSide().setUnit(PercentXmlType.Unit.PERCENT);
        			braFault.setPermanentFault(true);
    			}else if((breaker1Opened==true&&breaker2Opened==false)||
    					 (breaker1Opened==false&&breaker2Opened==true)){    				
    				if(braFault!=null){
    					ODMData2XmlHelper.setTimePeriodData(braFault.addNewFirstOperationTime(),
        						operationTime, TimePeriodXmlType.Unit.CYCLE);
    					braFault.setPermanentFault(true);
            			if(mode==-3){
            				double duration= braFault.getFirstOperationTime().getValue()-
            				        braFault.getFaultStartTime().getValue();
            				ODMData2XmlHelper.setTimePeriodData(braFault.addNewFaultDurationTime(), 
            						duration, TimePeriodXmlType.Unit.CYCLE);
            				braFault.setPermanentFault(false);
            			}
    				}    				
    			}else if(breaker1Opened==true&&breaker2Opened==true){    				
    				if(braFault!=null){
    					braFault.setPermanentFault(true);
    					ODMData2XmlHelper.setTimePeriodData(braFault.addNewSecondOperationTime(),
        						operationTime, TimePeriodXmlType.Unit.CYCLE);
        				if(mode==-3){
        					braFault.setPermanentFault(false);
            				double duration= braFault.getSecondOperationTime().getValue()-
            				        braFault.getFaultStartTime().getValue();
            				ODMData2XmlHelper.setTimePeriodData(braFault.addNewFaultDurationTime(), 
            						duration, TimePeriodXmlType.Unit.CYCLE);
            			}            			
            		}
    			}
				if(r!=0.0||x!=0.0){
    				ODMData2XmlHelper.setZValue(braFault.addNewFaultZ(), r, x, ZXmlType.Unit.PU);
    			} 
    		}

    	}else if(mode==4||mode==-4){
    		// load change
    		if(strAry[12].equals("")){
    			FaultListXmlType.Fault.LoadChange loadChange= tranSimu.getDynamicDataList()
 		       .getFaultList().addNewFault().addNewLoadChange(); 
     		
     		   String busId=strAry[1];
     		   loadChange.addNewBusId().setName(busId);
     		
     		   double busRatedVoltage=0.0;
     		   if(!strAry[2].equals("")){
     			   busRatedVoltage=new Double(strAry[2]).doubleValue();
     			   ODMData2XmlHelper.setVoltageData(loadChange.addNewBusRatedVoltage(),
 		                   busRatedVoltage, VoltageXmlType.Unit.KV);
     		   }
     		   double operationTime=0.0;
     		  if(!strAry[5].equals("")){
        			operationTime=new Double(strAry[5]).doubleValue();
        			ODMData2XmlHelper.setTimePeriodData(loadChange.addNewOperationTime(),
        					operationTime, TimePeriodXmlType.Unit.CYCLE);
        		}
     		  double pp=0.0, qp=0.0, pc=0.0,qc=0.0,pz=0.0,qz=0.0;
     		  if(!strAry[6].equals("")){
     			 pp=new Double(strAry[6]).doubleValue();    			
     		  }     		 
     		  if(!strAry[7].equals("")){
       			 qp=new Double(strAry[7]).doubleValue();
       		  }
       		  if(pp!=0.0||qp!=0.0){
       			 ODMData2XmlHelper.setPowerData(loadChange.addNewConstantPChange(),
       					pp, qp, PowerXmlType.Unit.MVA);
       		  }
       		  if(!strAry[8].equals("")){
       			 pc=new Double(strAry[8]).doubleValue();
       		  }    		
       		  if(!strAry[9].equals("")){
       			qc=new Double(strAry[9]).doubleValue();
       		  }
       		  if(pc!=0.0||qc!=0.0){
       			 ODMData2XmlHelper.setPowerData(loadChange.addNewConstantIChange(),
       					pc, qc, PowerXmlType.Unit.MVA);
       		  }
       		  if(!strAry[10].equals("")){
       			 pz=new Double(strAry[10]).doubleValue();
       		  }
       		  if(!strAry[11].equals("")){
       			 qz=new Double(strAry[11]).doubleValue();
       		  }
       		  if(pz!=0.0||qz!=0.0){
       			 ODMData2XmlHelper.setPowerData(loadChange.addNewConstantZChange(),
       					pz, qz, PowerXmlType.Unit.MVA);
       		  }
    		}else {
    			FaultListXmlType.Fault.GenChange genChange= tranSimu.getDynamicDataList()
 		       .getFaultList().addNewFault().addNewGenChange(); 
     		
     		String busId=strAry[1];
     		genChange.addNewBusId().setName(busId);
     		
     		double busRatedVoltage=0.0;
     		if(!strAry[2].equals("")){
     			busRatedVoltage=new Double(strAry[2]).doubleValue();
     			ODMData2XmlHelper.setVoltageData(genChange.addNewBusRatedVoltage(),
 		                  busRatedVoltage, VoltageXmlType.Unit.KV);
     		}
     		String genId="";
     		if(!strAry[3].equals("")){
     			genId=strAry[3];
     			genChange.setGeneratorId(genId);
     		}
     		double operationTime=0.0;
     		if(!strAry[5].equals("")){
     			operationTime=new Double(strAry[5]).doubleValue();
     			ODMData2XmlHelper.setTimePeriodData(genChange.addNewOperationTime(),
     					operationTime, TimePeriodXmlType.Unit.CYCLE);
     		} 
     		double pg=0.0;
     		if(new Double(strAry[12]).doubleValue()<90000){
 				pg=new Double(strAry[12]).doubleValue();
 				ODMData2XmlHelper.setPowerData(genChange.addNewGenChange(),
     					pg, 0.0, PowerXmlType.Unit.MVA);
 			}else{
 				genChange.setGenOutage(true);
 			}	
     	 }
    		// dc line fault
      }else if(mode==5){
    	  int sign=new Integer(str.substring(31, 33).trim()).intValue();
    	  if(sign>0){
    		  FaultListXmlType.Fault.DcLineFault dcFault= tranSimu.getDynamicDataList().
	           getFaultList().addNewFault().addNewDcLineFault();
    		  dcFault.setPermanentFault(true);
	          String bus1=strAry[1];
	          dcFault.addNewFromACBusId().setName(bus1);
	          double bus1Vol=0.0;
	          if(!strAry[2].equals("")){
	    		  bus1Vol= new Double(strAry[2]).doubleValue();
	    		  ODMData2XmlHelper.setVoltageData(dcFault.addNewFromACRatedVol(),
	       			  bus1Vol, VoltageXmlType.Unit.KV);    	 
	    	  }
	          String bus2=strAry[3];
	    	  dcFault.addNewToACBusId().setName(bus2);
	    	  double bus2Vol=0.0;
	    	  if(!strAry[4].equals("")){
	    		  bus2Vol= new Double(strAry[4]).doubleValue();
	    		  ODMData2XmlHelper.setVoltageData(dcFault.addNewToACRatedVol(),
	       			  bus2Vol, VoltageXmlType.Unit.KV);    	 
	    	  }
	    	  int faultType= new Integer(strAry[5]).intValue();
	    	  if(faultType==1){
	    		  dcFault.setFaultType(FaultListXmlType.Fault.
	    				  DcLineFault.FaultType.FROM_BUS_BIPOLE_SHORT_CIRCUIT);
	    	  }
	    	  if(faultType==2){
	    		  dcFault.setFaultType(FaultListXmlType.Fault.
	    				  DcLineFault.FaultType.TO_BUS_BIPOLE_SHORT_CIRCUIT);
	    	  }
	    	  double faultLocation=0.0;
	    	  if(!strAry[10].equals("")){
	    		  faultLocation= new Double(strAry[10]).doubleValue();
	    	  }
	    	  if(faultType==3){
	    		  dcFault.setFaultType(FaultListXmlType.Fault.
	    				  DcLineFault.FaultType.FAULT_ON_LINE);
	    		  dcFault.addNewFaultLocationFromFromSide().setValue(faultLocation);
	    		  dcFault.getFaultLocationFromFromSide().setUnit(PercentXmlType.Unit.PERCENT);
	    	  }
	    	  if(faultType==4){
	    		  dcFault.setFaultType(FaultListXmlType.Fault.
	    				  DcLineFault.FaultType.POWER_BLOCKED);
	    	  }
	    	  if(faultType==5){
	    		  dcFault.setFaultType(FaultListXmlType.Fault.
	    				  DcLineFault.FaultType.POWER_REVERSED);
	    	  }
	    	  double changedScale=0.0;
	    	  if(!strAry[9].equals("")){
	    		  changedScale= new Double(strAry[9]).doubleValue();
	    	  }
	    	  if(faultType==7){
	    		  dcFault.setFaultType(FaultListXmlType.Fault.
	    				  DcLineFault.FaultType.POWER_CHANGE_BY_SPECIFICATION);
	    		  ODMData2XmlHelper.setPowerData(dcFault.addNewChangedPower(), 
	    				  changedScale, 0.0, PowerXmlType.Unit.MVA);    		  
	    	  }
	    	  if(faultType==8){
	    		  dcFault.setFaultType(FaultListXmlType.Fault.
	    				  DcLineFault.FaultType.CURRENT_CHANGE_BY_SPECIFICATION);
	    		  ODMData2XmlHelper.setCurrentData(dcFault.addNewChangedCurrent(), 
	    				  changedScale, CurrentXmlType.Unit.KA);    		  
	    	  }
	    	  double startTime=0.0;
	    	  if(!strAry[7].equals("")){
	    		  startTime= new Double(strAry[7]).doubleValue();
	    		  ODMData2XmlHelper.setTimePeriodData(dcFault.addNewStartTime(), 
	    				  startTime, TimePeriodXmlType.Unit.CYCLE);
	    		  
	    	  }
	    	  double faultR=0.0;
	    	  if(!strAry[8].equals("")){
	    		  faultR= new Double(strAry[8]).doubleValue();
	    		  ODMData2XmlHelper.setZValue(dcFault.addNewFaultZ(), faultR, 0.0, ZXmlType.Unit.PU);
	    		  
	    	  }   		  
    	  }else{
    		  String bus1=strAry[1];
    		  String bus2=strAry[3];
    		  FaultListXmlType.Fault.DcLineFault dcFault=
    			  ODMData2XmlHelper.getDCFaultRecord(tranSimu, bus1, bus2);
    		 dcFault.setPermanentFault(false);
    		 
    		 double clearedTime= new Double(strAry[7]).doubleValue();
    		 double durationTime=clearedTime-dcFault.getStartTime().getValue();
    		 ODMData2XmlHelper.setTimePeriodData(dcFault.addNewDurationTime(),
    				 durationTime, TimePeriodXmlType.Unit.CYCLE); 
    	  }
      }
    }

private static String[] getFaultOperationDataFields ( final String str, int mode,
                                  BPAAdapter adapter) {
	final String[] strAry = new String[13];		
	try{
		if(mode==1||mode==2||mode==3||mode==-1||mode==-2||mode==-3){
			strAry[0]=StringUtil.getStringReturnEmptyString(str,1, 2).trim();
			strAry[1]=StringUtil.getStringReturnEmptyString(str,4, 4).trim();
			strAry[2]=StringUtil.getStringReturnEmptyString(str,5, 12).trim();
			strAry[3]=StringUtil.getStringReturnEmptyString(str,13, 16).trim();
			strAry[4]=StringUtil.getStringReturnEmptyString(str,18, 18).trim();
			strAry[5]=StringUtil.getStringReturnEmptyString(str,19, 26).trim();
			strAry[6]=StringUtil.getStringReturnEmptyString(str,27, 30).trim();
			strAry[7]=StringUtil.getStringReturnEmptyString(str,32, 32).trim();
			strAry[8]=StringUtil.getStringReturnEmptyString(str,36, 37).trim();			
			strAry[9]=StringUtil.getStringReturnEmptyString(str,40, 45).trim();
			strAry[10]=StringUtil.getStringReturnEmptyString(str,46, 51).trim();
			strAry[11]=StringUtil.getStringReturnEmptyString(str,52, 57).trim();
			strAry[12]=StringUtil.getStringReturnEmptyString(str,58, 63).trim();
		}else if(mode==4){
			strAry[0]=StringUtil.getStringReturnEmptyString(str,1, 2).trim();
			strAry[1]=StringUtil.getStringReturnEmptyString(str,5, 12).trim();
			strAry[2]=StringUtil.getStringReturnEmptyString(str,13, 16).trim();
			strAry[3]=StringUtil.getStringReturnEmptyString(str,17, 17).trim();
			strAry[4]=StringUtil.getStringReturnEmptyString(str,37, 37).trim();
			strAry[5]=StringUtil.getStringReturnEmptyString(str,40, 45).trim();
			strAry[6]=StringUtil.getStringReturnEmptyString(str,46, 50).trim();
			strAry[7]=StringUtil.getStringReturnEmptyString(str,51, 55).trim();
			strAry[8]=StringUtil.getStringReturnEmptyString(str,56, 60).trim();
			strAry[9]=StringUtil.getStringReturnEmptyString(str,61, 65).trim();
			strAry[10]=StringUtil.getStringReturnEmptyString(str,66, 70).trim();
			strAry[11]=StringUtil.getStringReturnEmptyString(str,72, 75).trim();
			strAry[12]=StringUtil.getStringReturnEmptyString(str,76, 80).trim();
		}else if(mode==5){
			strAry[0]=StringUtil.getStringReturnEmptyString(str,1, 2).trim();
			strAry[1]=StringUtil.getStringReturnEmptyString(str,5, 12).trim();
			strAry[2]=StringUtil.getStringReturnEmptyString(str,13, 16).trim();
			strAry[3]=StringUtil.getStringReturnEmptyString(str,19, 26).trim();
			strAry[4]=StringUtil.getStringReturnEmptyString(str,27, 30).trim();
			strAry[5]=StringUtil.getStringReturnEmptyString(str,32, 33).trim();
			strAry[6]=StringUtil.getStringReturnEmptyString(str,37, 37).trim();
			strAry[7]=StringUtil.getStringReturnEmptyString(str,40, 45).trim();
			strAry[8]=StringUtil.getStringReturnEmptyString(str,46, 51).trim();
			strAry[9]=StringUtil.getStringReturnEmptyString(str,52, 57).trim();
			strAry[10]=StringUtil.getStringReturnEmptyString(str,58, 63).trim();			
		}
	}catch(Exception e){
		adapter.logErr(e.toString());
	}
	
	
	return strAry;
}



}
