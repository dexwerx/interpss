import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.StringTokenizer;

import org.w3c.dom.Node;

public class Helper {

	public static String[] setAreaInterchangeDefautValue(
			String[] areaInterchangeStr) {

		// set default value		 
		areaInterchangeStr[0] = "0";// area Number
		areaInterchangeStr[1] = "0"; // swing bus number
		areaInterchangeStr[2] = "0"; // desired net interchange leaving the area
		areaInterchangeStr[3] = "10"; // Interchange tolerance
		areaInterchangeStr[4] = "        "; // area name  
		return areaInterchangeStr;

	}

	public static String[] setInterareaTransferDefautValue(
			String[] interareaTransferStr) {

		// set default value		 
		interareaTransferStr[0] = "0";// area from Number
		interareaTransferStr[1] = "0"; // area to number number
		interareaTransferStr[2] = "0"; // identifier
		interareaTransferStr[3] = "10"; // mw transfered;

		return interareaTransferStr;

	}

	public static String[] setBusDefautValue(String[] busStr) {

		// set default value
		// base kv 
		//busStr[0]: bus number (1 to 99997)
		busStr[0] = null;
		busStr[1] = "        ";// Name
		busStr[2] = "0"; // base kv
		busStr[3] = "1"; // bus type
		busStr[4] = "0"; // GL
		busStr[5] = "0"; // Bl            				
		busStr[6] = "1"; // area
		busStr[7] = "1"; // zone
		busStr[8] = "1"; // voltage magnitude
		busStr[9] = "0"; // voltage angle
		busStr[10] = "1";// owner default=1;

		return busStr;

	}

	public static String[] setLoadDefautValue(String[] loadStr) {

		// set load default value;
		//loadStr[0]:bus number, or extended bus name enclosed in single quotes
		loadStr[0] = null;
		loadStr[1] = "1"; // identifier
		loadStr[2] = "1"; // status
		loadStr[3] = "1"; // area
		loadStr[4] = "1"; // zone
		loadStr[5] = "0"; // pl
		loadStr[6] = "0"; // ql
		loadStr[7] = "0"; // IP
		loadStr[8] = "0"; // IQ
		loadStr[9] = "0"; // YP
		loadStr[10] = "0"; // YQ
		loadStr[11] = "1";// owner		
		return loadStr;
	}

	public static String[] setGenDefaultValue(String[] genStr) {
		genStr[0] = null;
		genStr[1] = "1"; //identifier
		genStr[2] = "0"; //p gen
		genStr[3] = "0"; //q gen
		genStr[4] = "9999"; // max gen q limit
		genStr[5] = "-9999"; // min gen q limit
		genStr[6] = "1"; // regulated voltage
		genStr[7] = "0"; // remote bus
		genStr[8] = "0.0"; // system base power
		genStr[9] = "0"; // zr
		genStr[10] = "1"; //zx
		genStr[11] = "0"; // rt
		genStr[12] = "0"; //xt
		genStr[13] = "1"; //step-up transformer turns ratio
		genStr[14] = "1"; // status
		genStr[15] = "100"; // percent of the total mvar to hold remoted voltage
		genStr[16] = "9999"; // max p gen output 
		genStr[17] = "-9999"; // min p gen output 
		genStr[18] = "1"; // owner 1, O1
		genStr[19] = "1.0";// F1
		genStr[20] = "0"; // owner 2, O2
		genStr[21] = "1.0";// F2
		genStr[22] = "0"; // owner 3, O3
		genStr[23] = "0.0";// F3
		genStr[24] = "0"; // owner 4, O4
		genStr[25] = "0.0";// F4

		return genStr;
	}
	
	public static String[] setSwitchShuntDefaultValue(String[] switShuntStr){
		switShuntStr[0]=null;
		switShuntStr[1]="2"; //continuous adjustment, controlling voltage locally; 
		switShuntStr[4]="0";  // 
		switShuntStr[5]="100.0"; //
		switShuntStr[6]=" ";
		switShuntStr[7]="0";
		switShuntStr[8]="1";
		switShuntStr[9]="-9999";
		switShuntStr[10]="1";
		switShuntStr[11]="9999";
		
		return switShuntStr;
		
	}

	public static String[] setBranchDefaultValue(String[] branchStr) {

		branchStr[2] = "1";// circuit identifier
		branchStr[3] = "0";// R
		branchStr[5] = "0";// B
		branchStr[6] = "0";// first current rating
		branchStr[7] = "0";// second current rating
		branchStr[8] = "0";// third current rating
		branchStr[9] = "0";// GI
		branchStr[10] = "0";//Bi
		branchStr[11] = "0";// GJ
		branchStr[12] = "0";//BJ
		branchStr[13] = "1";// ST
		branchStr[14] = "0";//line Lenth
		branchStr[15] = "1";// O1
		branchStr[16] = "1.0";//F1
		branchStr[17] = branchStr[19] = branchStr[21] = "0";
		branchStr[18] = branchStr[20] = branchStr[22] = "1.0";

		return branchStr;
	}

	public static String[] setTransDefaultValue(String[] transStr) {

		// first line
		transStr[2] = "0"; // K, two-winding 
		transStr[3] = "1"; // circuit identifier
		transStr[4] = "1"; // CW, unit 
		transStr[5] = "1"; // CZ, unit
		transStr[6] = "1"; // CM, unit
		transStr[7] = "0"; // MAG1
		transStr[8] = "0"; // MAG2
		transStr[9] = "2"; // The nonmetered end code, 2 indicates a two-winding
		transStr[10] = "       ";// name
		transStr[11] = "1";// initial status
		transStr[12] = "1";// O1
		transStr[13] = "1.0";//F1
		transStr[14] = transStr[16] = transStr[18] = "0";
		transStr[15] = transStr[17] = transStr[19] = "0.0";
		// second line
		transStr[20] = "0.0";//R1-2
		transStr[22] = "0.0"; // system base power					   
		transStr[23] = "1.0"; // WINDV1,the winding one off-nominal turn ratio 
		transStr[24] = "0.0";//NOMV1, the nominal winding one voltage in kv.
		transStr[25] = "0.0";//ANG1, the winding one phase shift angle in degrees
		transStr[26] = transStr[27] = transStr[28] = "0.0"; // the first winding's 3 ratings
		transStr[29] = "0"; //COD,transformer control mode
		transStr[30] = "0"; // CONT,the bus number of the bus whose vol is controlled					   
		transStr[31] = "1.1"; // RMA, upper limits
		transStr[32] = "0.9"; // RMI, lower limits
		transStr[33] = "1.1"; // VMA, upper limits
		transStr[34] = "0.9"; // VMI, lower limits
		transStr[35] = "33"; // NTP, the number of tap positions available;
		transStr[36] = "0"; // TAB, the number of a transformer impedance correction table
		transStr[37] = "0.0"; // CR, the load drop compensation impedance for voltage
		transStr[38] = "0.0"; // CX, the load drop compensation impedance for voltage
		transStr[39] = "1.0"; // WINDV2, the winding two off-nominal turn ratio 
		transStr[40] = "0.0"; // NOMV2,the nominal winding two voltage in kv.

		return transStr;
	}

	public static String getBusId(String s, File file) throws Exception {
		String busId = "";
		try {
			BufferedReader din = new BufferedReader(new FileReader(file));
			String str = din.readLine();
			str = din.readLine();
			str = din.readLine();
			str = din.readLine();
			String id = "";
			String name = "";
			boolean found = false;
			StringTokenizer st;
			while (found == false) {

				st = new StringTokenizer(str, ",");
				id = st.nextToken().trim();

				name = st.nextToken().trim();
				if (name.equals(s)) {
					busId = id;
					found = true;
				}
				str = din.readLine();
			}

		} catch (FileNotFoundException e) {
			// TODO: handle exception
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		}
		return busId;
	}

	public static String[] getZeroLine(File file) throws Exception {
		String[] strZer = new String[5];
		try {
			BufferedReader din = new BufferedReader(new FileReader(file));
			String str = din.readLine();
			str = din.readLine();
			StringTokenizer st;
			while (str != null) {
				st = new StringTokenizer(str, ",");
				strZer[0] = st.nextToken().trim();//f id
				strZer[1] = st.nextToken().trim();// t id
				strZer[2] = st.nextToken().trim();
				strZer[3] = st.nextToken().trim();//R
				strZer[4] = st.nextToken().trim();//x	
				str = din.readLine();
			}

		} catch (FileNotFoundException e) {
			// TODO: handle exception
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		}
		return strZer;
	}

	public static void readModifyAndWrite(File read, BufferedWriter bw,
			File file) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(read));
			//BufferedWriter bw = new BufferedWriter(new FileWriter(write));
			String str = null;
			str = br.readLine();
			str = br.readLine();

			StringTokenizer st;
			String[] gen = new String[26];
			while (str != null) {
				st = new StringTokenizer(str, ",");
				for (int i = 0; st.hasMoreTokens(); i++) {
					gen[i] = st.nextToken().trim();

				}
				BufferedReader din = new BufferedReader(new FileReader(file));
				String str1 = din.readLine();
				StringTokenizer st1;
				String I = "";
				boolean found = false;
				while (found == false && str1 != null) {
					st1 = new StringTokenizer(str1, " ");
					I = st1.nextToken().trim();
					if (I.equals(gen[0])) {
						gen[10] = st1.nextToken().trim();
						found = true;
					}
					str1 = din.readLine();
				}
				//写文件
				for (int i = 0; i <= 25; i++) {
					if (i == 1) {
						bw.write(gen[i] + ",  ");
					} else if (i == 25) {
						bw.write(gen[i] + "\r\n");
					} else {
						bw.write(gen[i] + ",   ");
					}

				}
				//继续读文件
				str = br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO: handle exception
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		}

	}

	public static String getZoneId(String s, File file) throws Exception {
		String zoneId = "";
		try {
			BufferedReader din = new BufferedReader(new FileReader(file));
			String str = din.readLine();
			String id = "";
			String name = "";
			boolean found = false;
			StringTokenizer st;
			while (found == false) {

				st = new StringTokenizer(str, ",");
				st.nextToken().trim();
				id = st.nextToken().trim();

				name = st.nextToken().trim();
				if (name.equals(s)) {
					zoneId = id;
					found = true;
				}
				str = din.readLine();
			}

		} catch (FileNotFoundException e) {
			// TODO: handle exception
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		}
		return zoneId;
	}

	public static String getValue(Node node) {

		String str = node.getFirstChild().getNodeValue();
		return str;
	}

	public static String setFormat(String str) {
		NumberFormat format = new DecimalFormat("###0.0000");
		double d = new Double(str).doubleValue();
		String s = format.format(d);
		return s;

	}
}
