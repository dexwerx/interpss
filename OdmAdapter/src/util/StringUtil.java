package util;

public class StringUtil {
	
	public static String addSpace(String s, int targetLength){
		int length=s.trim().length();
		int cnCharNum=getChineseCharNum(s);
		String space="";	
		try {
		    if(targetLength-length-cnCharNum<0)
			throw new Exception("targetLength is less than original Sring! Sring, targetL:"+s+", "+targetLength);
			} catch (Exception e) {
				e.printStackTrace();
		}
		for(int l=0;l<targetLength-length-cnCharNum;l++){
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
	 * delete extra zeros behind the decimal
	 * @param str
	 * @return String
	 */
	public static String deleteLastZero(String str){
		int length=str.length();
		while(str.contains(".") && str.endsWith("0")){
			str=str.substring(0, --length);
		}
		return str;
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
	/**
	 * return a String of a number in Fa.b(a format in BPA,such as F5.4)format 
	 * @param num a entered number
	 * @param a total number of digits
	 * @param b decimal places specified,when the entered number is integer
	 * @return string
	 */
	public static String format(double num,int a,int b){
		int i=0;//用于标志负数
		if(num<0){
			num=0-num;
			i=1;
			a--;
		}
		try {
			if(num>=Math.pow(10,a)-0.5||(num>=Math.pow(10,a-1)-0.5&&b!=0)){//异常,超出范围
				throw new Exception("num is out of range: "+((i==1)?-num:num)+"  F"+(a+i)+"."+b);
			}
		} catch (Exception e) {
				e.printStackTrace();
		}
		String str=String.valueOf(num);
		if(num==0) str="";
		if(str.length()>a || str.contains("E")){//str.contains("E")指参数num小于0.001,用科学记数法表示
			if(num>=1){
				if(a==str.indexOf(".")) str=String.valueOf(Math.round(num));//(F4.0)4444.55->4445,而(F4.1)4444.55会抛出异常
				else if(str.indexOf(".")==a-b) str=String.valueOf(Math.round(num*Math.pow(10, b)));//(F4.3)1.145->1145,(F4.2)-1.145->-115
				else{
					int m=a-1-str.indexOf(".");
					num=Math.round(num*Math.pow(10, m))/Math.pow(10, m);//四舍五入
					str=String.valueOf(num);//(F4.0)12.34->12.3 ,(F4.3)444.0->444.0(后面还有去零的方法）
				}
			}else{//小数
				if(i==1 && !str.contains("E")&& str.length()>a+i &&//满足-1<num<=-0.001
				  ((str.substring(2,3).equals("0") && a<=b) || (!str.substring(2,3).equals("0") && a==b))){
					//(F3.3)-0.0239->-24                          //(F4.3)-0.1239->-124
					str=String.valueOf(Math.round(num*Math.pow(10, b)));					
				}
				else if(a+i>b || (!str.contains("E")&&(str.length()<=b+1 ||(i==1&&!str.substring(2,3).equals("0"))))){
						//F(5.3)-0.1239->-.124		//(F5.5)0.0123->.0123	//F(4.4)-0.1239->-.12
					num=Math.round(num*Math.pow(10, a-1))/Math.pow(10, a-1);//四舍五入
					if(num==0) str="";//(F4.3)0.00023->0.0->""
					else if(num>=1) str=String.valueOf(num).substring(0,a);//(F3.0)0.9994->1.0 ,(F2.0)-0.994->-1而(F2.1)-0.994异常
					else if(String.valueOf(num).contains("E")) str=changeToDecimal(num,a);//(F6.4)0.000139->1.39E-4->.00014
					else str=String.valueOf(num).substring(1);//(F5.4)0.1860003 -> .186 ,(F5.4)0.1234 -> .1234 ,(F4.4)0.001 ->.001
				}
				else if(a+i==b){//包括str.contains("E")的情况
					str=String.valueOf(Math.round((num*Math.pow(10, b))));//(F4.4)0.00111 -> 11 ,(F6.6)-0.00035-> 3.5E-4 ->-350
				} 
			}
		}else if(str.length()!=0 && num<1) str=str.substring(1,str.length());//小于零的小数，且有足够位数(F5.5)0.23->.23
		//其他
		if(i==1) str="-"+str;
		
		str=addSpace(deleteLastZero(str),a+i);
		return str;		
	}
	//小于0.001就以科学记数法表示，将科学记数法的小数化为小数形式
	/**
	 * change a decimal fraction in scientific notation to prototype,then return a String of it.
	 * @param num a decimal fraction
	 * @param a Reserved bits
	 * @return string
	 */
	public static String changeToDecimal (double num,double a){
		int m=0;
		String str=null;
		while(String.valueOf(num).contains("E")){
			num*=10;
			m+=1;
		}
		//0.0006*10=0.00599999...,这里将其化回0.006
		num=Math.round(num*Math.pow(10, a-1-m))/Math.pow(10, a-1-m);
		str=String.valueOf(num).substring(1);
		for(;m>0;m--)
			str=str.replace(".", ".0");	
		return str;		
	}
}
