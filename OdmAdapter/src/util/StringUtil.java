package util;

public class StringUtil {
	
	public static String addSpace(String s, int targetLength){
		int length=s.trim().length();
		int cnCharNum=getChineseCharNum(s);
		String space="";	
		try {
		    if(targetLength-length+cnCharNum<0)
			throw new Exception("targetLength is less than original Sring! Sring, targetL:"+s+", "+targetLength);
			} catch (Exception e) {
				e.printStackTrace();
		}
		for(int l=0;l<targetLength-length+cnCharNum;l++){
			space+=" ";
		}
		return s.trim()+space;
	}
	public static String getSpace(int targetLength){
		String space="";	
		for(int l=0;l<targetLength;l++){
			space+=" ";
		}
		return space;
	}
	public static String replaceChineseChar(String s){
		String regEx = "[\u4e00-\u9fa5]"; 
     	String tem= s.replaceAll(regEx,"aa"); 
        return tem;
	}
	
	/**
	 * to get the Number of Chinese Characters in the input String.
	 * @param str
	 * @return the Number of Chinese Characters
	 */
	public static int getChineseCharNum(String str){
		String regEx = "[\u4e00-\u9fa5]"; 
		String tem=str;
		tem=tem.replaceAll(regEx,"aa"); 
		int n=tem.length()-str.length(); 
		return n;
	}

}
