package BPA;

import org.ieee.odm.model.aclf.AclfModelParser;
import org.ieee.odm.schema.LoadflowNetXmlType;

public class BpaAclfNetworkRecord {
	// head info
	private static String  _caseId="";
	private static String  _projName="";
	private static double  _mvaBase=0;
	private static String _netStr="";
	private static AclfModelParser _parser=null;
	public BpaAclfNetworkRecord(AclfModelParser parser){
		_parser=parser;
		
	}
	public String setNetHeadInfo(String caseId,String projectName,String bpaLfNetStr){
		 //process network data
		_caseId=caseId;
		_projName=projectName;
		_mvaBase=_parser.getAclfNet().getBasePower().getValue();
		String DEFAULT_HEAD_INFO=
				"(POWERFLOW,CASEID="+_caseId+",PROJECT="+_projName+")"+"\n"+
				"/MVA_BASE="+_mvaBase+"\\"+"\n"+
	            "/SOL_ITER,DECOUPLED=2,NEWTON=15,OPITM=0"+"\\"+"\n"+
	            "/P_OUTPUT_LIST,ZONES=ALL"+"\\"+"\n"+
	            "/RPT_SORT=ZONE"+"\\"+"\n"+
	            "/NEW_BASE,FILE="+_caseId+".BSE"+"\\"+"\n"+
	            "/PF_MAP,FILE ="+_caseId+".MAP"+"\\"+"\n"+
	            "/NETWORK_DATA"+"\\";
		bpaLfNetStr+=DEFAULT_HEAD_INFO+"\n";
		return bpaLfNetStr;
	}
	

	

}
