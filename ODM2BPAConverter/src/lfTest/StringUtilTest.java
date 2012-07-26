package lfTest;

import org.junit.Test;

import util.StringUtil;

public class StringUtilTest {
	
	@Test
	public void testStringFormat(){
//		double loadP=100.1;
//		String loadPStr=(loadP>0.0&&loadP<1000.0)?String.format("%4.1f", loadP):
//			(loadP>=1000.0)?String.format("%5.0f", loadP):String.format("%4.0f", loadP);
//		System.out.println(loadPStr);	
//		assertTrue(StringUtil.addSpace(loadPStr,5).equals("100.1"));
//		
//		
//		double loadQ=-100.0;
//		String loadQStr=(loadQ>0.0&&loadQ<1000.0)?String.format("%4.1f", loadQ):
//			(loadQ>=1000.0)?String.format("%5.0f", loadQ):String.format("% 4.3f", loadQ);
//		System.out.println(loadQStr);	
//		assertTrue(StringUtil.addSpace(loadQStr,5).equals("-1000"));
//		System.out.println(StringUtil.format(-0.9994, 2, 0));
//		double num=0.0003;
//		num=(Math.round(num*10000));
//		num=num/10000;
//		num*=10;
//		System.out.println(num);
//		String str="Ò»¸öPANNAN50";
//		String regEx = "[\u4e00-\u9fa5]"; 
//		String tem=str;
//		tem=tem.replaceAll(regEx,"aa"); 
//		System.out.println(tem);
		double a=0.00023;
		String b=StringUtil.format(a, 4, 3);
		System.out.println(b);
	}

}
