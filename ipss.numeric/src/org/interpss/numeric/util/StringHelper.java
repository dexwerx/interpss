package org.interpss.numeric.util;

public class StringHelper {
	/**
	 * convert an Object[] to a String[]
	 * 
	 * @param oAry
	 * @return
	 */
	public static String[] toStrArray(Object[] oAry) {
		String[] sAry = new String[oAry.length];
		int cnt = 0;
		for (Object o : oAry)
			sAry[cnt++] = (String)o;
		return sAry;
	}
}
