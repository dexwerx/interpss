package org.ieee.odm.adapter.psse;

import static org.ieee.odm.ODMObjectFactory.odmObjFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import org.ieee.odm.ODMFileFormatEnum;
import org.ieee.odm.adapter.psse.v30.impl.PSSEV30BusDataRec;
import org.ieee.odm.adapter.psse.v30.impl.PSSEV30DcLine2TDataRec;
import org.ieee.odm.adapter.psse.v30.impl.PSSEV30GenDataRec;
import org.ieee.odm.adapter.psse.v30.impl.PSSEV30LineDataRec;
import org.ieee.odm.adapter.psse.v30.impl.PSSEV30LoadDataRec;
import org.ieee.odm.adapter.psse.v30.impl.PSSEV30XfrDataRec;
import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.AbstractModelParser;
import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.model.base.BaseDataSetter;
import org.ieee.odm.model.base.BaseJaxbHelper;
import org.ieee.odm.schema.ActivePowerUnitType;
import org.ieee.odm.schema.AreaTransferXmlType;
import org.ieee.odm.schema.ExchangeAreaXmlType;
import org.ieee.odm.schema.InterchangeXmlType;
import org.ieee.odm.schema.LoadflowNetXmlType;
import org.ieee.odm.schema.NetZoneXmlType;
import org.ieee.odm.schema.ObjectFactory;
import org.ieee.odm.schema.OwnerXmlType;
import org.ieee.odm.schema.XformerZTableXmlType;

public class PSSENetDataRec {
	public static class HeaderRec {
		public static void procLineString(String lineStr, int lineNo, PsseVersion version, 
				final LoadflowNetXmlType baseCaseNet, ObjectFactory factory) {
			if (lineNo == 1) {
				StringTokenizer st = new StringTokenizer(lineStr, ",");
				int indicator = new Integer(st.nextToken().trim()).intValue();
				// at here we have "100.00 / PSS/E-29.0 THU, JUN 20 2002 14:19"
				st = new StringTokenizer(st.nextToken(), "/");
				double baseMva = new Double(st.nextToken().trim()).doubleValue();
				baseCaseNet.setBasePower(BaseDataSetter.createPowerMvaValue(baseMva));
				
				//NameValuePairListXmlType nvList = factory.createNameValuePairListXmlType();
				//baseCaseNet.setNvPairList(nvList);
				
				BaseJaxbHelper.addNVPair(baseCaseNet, "CaseIndicator", 
						new Integer(indicator).toString());
			}
			else if (lineNo == 2) {
				// The 2nd line is treated as description
				baseCaseNet.setDesc(lineStr);
			}
			else {
				// the 3rd line is treated as the network id and network name
				baseCaseNet.setName(lineStr);
			}
		}	
		
		public static ODMFileFormatEnum getVersion(String filename) {
/* 
 *   Sample header lines
 * 
==========
 0    100.00     / RAW29 (REV 30)  THU, JUL 31 2008  13:42
                               100.0                        
==========                                                            
                                                            
==========                                                            
0,   100.00, 30, 0, 1,  60.00     / PSS(R)E 30 RAW created by rawd30  SAT, FEB 05 2011  18:16
PSS(R)E SAMPLE CASE
ALL DATA CATEGORIES WITH SEQUENCE DATA
==========

==========
0,100.0
  20110729120000,CASE_D:033111-EMSDB:DB53,CASE:012411-EMSDB:DB52, 10
VER 26   PARAMETERS INITIALIZED ON 22-Jun-2011 16:45:56 PDT
==========                                                            
 */
			try {
				final File file = new File(filename);
				final InputStream stream = new FileInputStream(file);
				final BufferedReader din = new BufferedReader(new InputStreamReader(stream));

				String lineStr = null;
				int lineNo = 0;
		      	do {
		      		lineStr = din.readLine();
		      		if (lineStr.contains("VER 26"))
		      			return ODMFileFormatEnum.PsseV26;
		      		else if (lineStr.contains("RAW29"))
		      			return ODMFileFormatEnum.PsseV30;
		      		lineNo++;
		      		
		    	} while (lineNo < 3);
			} catch (Exception e) {
				ODMLogger.getLogger().severe(e.toString());
			}			
			return ODMFileFormatEnum.PsseV30;
		}
	}

	/*
	 * Area Data I,ISW,PDES,PTOL,'ARNAM'
	 */
	public static void processAreaRec(String lineStr, PsseVersion version, 
				final LoadflowNetXmlType baseCaseNet, AclfModelParser parser) {
		StringTokenizer st = new StringTokenizer(lineStr, ",");
		int i = new Integer(st.nextToken().trim()).intValue();
		int isw = new Integer(st.nextToken().trim()).intValue();
		double pdes = new Double(st.nextToken().trim()).doubleValue();
		double ptol = new Double(st.nextToken().trim()).doubleValue();
		String arnam = st.nextToken().trim();

/*
		I,  ISW,     PDES,      PTOL, 'ARNAM'
	    1,    0,     0.000,     1.000,'NEPEX       '

*/
		if (baseCaseNet.getAreaList() == null)
			baseCaseNet.setAreaList(odmObjFactory.createNetworkXmlTypeAreaList());
		ExchangeAreaXmlType area = odmObjFactory.createExchangeAreaXmlType();
		baseCaseNet.getAreaList().getArea().add(area);
		area.setId(new Integer(i).toString());
		area.setNumber(i);
		area.setName(arnam);

		if (isw > 0) {
			area.setSwingBusId(parser.createBusRef(AbstractModelParser.BusIdPreFix+isw));
			area.setDesiredExchangePower(BaseDataSetter.createActivePowerValue(pdes, ActivePowerUnitType.MW));
			area.setExchangeErrTolerance(BaseDataSetter.createActivePowerValue(ptol, ActivePowerUnitType.MW));			
		}
	}

	/*
	 * ZoneData Format: I, ’ZONAME’
	 */
	public static void processZoneRec(String lineStr, PsseVersion version, 
				final LoadflowNetXmlType baseCaseNet, ObjectFactory factory) {
		StringTokenizer st = new StringTokenizer(lineStr, ",");
		int	i = new Integer(st.nextToken().trim()).intValue();
		String name = st.nextToken().trim();

		/*
		 * Format: I, ’ZONAME’
		 */
		if (baseCaseNet.getLossZoneList() == null)
			baseCaseNet.setLossZoneList(factory.createNetworkXmlTypeLossZoneList());
		NetZoneXmlType zone = factory.createNetZoneXmlType(); 
		baseCaseNet.getLossZoneList().getLossZone().add(zone);
		zone.setId(new Integer(i).toString());
		zone.setNumber(i);
		zone.setName(name);		
	}

	/*
	 * InterareaTransfer format: ARFROM, ARTO, TRID, PTRAN
	 */
	public static void processInterareaTransferRec(String lineStr, PsseVersion version, 
				final LoadflowNetXmlType baseCaseNet, ObjectFactory factory) {
		StringTokenizer st = new StringTokenizer(lineStr, ",");
		int	arfrom = new Integer(st.nextToken().trim()).intValue();
		int	arto = new Integer(st.nextToken().trim()).intValue();
		String	trid = st.nextToken().trim();
		double	ptran = new Double(st.nextToken().trim()).doubleValue();

		/*
			 * format: ARFROM, ARTO, TRID, PTRAN

			ARFROM "From area" number (1 through the maximum number of areas at the 
					current size level; see Table P-1).
			ARTO "To area" number (1 through the maximum number of areas at the current 
					size level; see Table P-1).
			TRID Single-character (0 through 9 or A through Z) upper case interarea transfer identifier
					used to distinguish among multiple transfers between areas ARFROM and
					ARTO. TRID = ’1’ by default.
			PTRAN MW comprising this transfer. A positive PTRAN indicates that area ARFROM is
					selling to area ARTO. PTRAN = 0.0 by default.
					
				- FromAreaNo_ToAreaNo_TRID is unique					 
		*/
		if (baseCaseNet.getInterchangeList() == null)
			baseCaseNet.setInterchangeList(factory.createLoadflowNetXmlTypeInterchangeList());
		InterchangeXmlType interchange = factory.createInterchangeXmlType();
		baseCaseNet.getInterchangeList().getInterchange().add(interchange);
		AreaTransferXmlType transfer = factory.createAreaTransferXmlType(); 
		interchange.setAreaTransfer(transfer);
		
		transfer.setFromArea(arfrom);
		transfer.setToArea(arto);
		transfer.setId(trid);
		transfer.setAmountMW(ptran);
	}

	/*
	 * Owner format : I, ’OWNAME’
	 */
	public static void processOwnerRec(String lineStr, PsseVersion version, 
				final LoadflowNetXmlType baseCaseNet, ObjectFactory factory) {
		StringTokenizer st = new StringTokenizer(lineStr, ",");
		int	i = new Integer(st.nextToken().trim()).intValue();
		String name = st.nextToken().trim();

		/*
		 * format : I, ’OWNAME’
		 */
		//if (baseCaseNet.getOwnerList() == null)
		//	baseCaseNet.setOwnerList(factory.createBaseRecordXmlTypeOwnerList());
		OwnerXmlType owner = factory.createOwnerXmlType();
		baseCaseNet.getOwnerList().add(owner);
		owner.setId(new Integer(i).toString());
		owner.setNumber(i);
		owner.setName(name);			
	}

	public static void processXfrZTableRec(String lineStr, PsseVersion version,
				final LoadflowNetXmlType baseCaseNet, ObjectFactory factory) {
		StringTokenizer st = new StringTokenizer(lineStr, ",");
		int	i = new Integer(st.nextToken().trim()).intValue();
		double[] t = new double[11], f = new double[11];
		int cnt = 0;
		while(st.hasMoreTokens()) {
			t[cnt] = new Double(st.nextToken().trim()).doubleValue();
			f[cnt++] = new Double(st.nextToken().trim()).doubleValue();
		}

		/*
		 * format V30: I, T1, F1, T2, F2, T3, F3, ... T11, F11
		 */
		if (baseCaseNet.getXfrZTable() == null)
			baseCaseNet.setXfrZTable(factory.createXformerZTableXmlType());
		XformerZTableXmlType.XformerZTableItem item = factory.createXformerZTableXmlTypeXformerZTableItem(); 
		baseCaseNet.getXfrZTable().getXformerZTableItem().add(item);
		item.setNumber(i);
		for (int n = 0; n < cnt; n++) {
			XformerZTableXmlType.XformerZTableItem.Lookup lookup = factory.createXformerZTableXmlTypeXformerZTableItemLookup(); 
			item.getLookup().add(lookup);
			lookup.setTurnRatioShiftAngle(t[n]);
			lookup.setScaleFactor(f[n]);
		}
	}
}
