package org.interpss.dstab.output;

import org.interpss.output.BaseSimuDBRecord;

public class DStabSimuDBRecord extends BaseSimuDBRecord {
	public static final String EXCITER_ID_EXT = "_EXC";
	public static final String GOVERNER_ID_EXT = "_GOV";
	public static final String STABILIZER_ID_EXT = "_PSS";

	private String elemIdStr;
	private double simuTime;
	private String simuRec;

	/**
	 * @return Returns the elemIdStr.
	 */
	public String getElemIdStr() {
		return elemIdStr;
	}

	/**
	 * @param elemIdStr
	 *            The elemIdStr to set.
	 */
	public void setElemIdStr(String elemIdStr) {
		this.elemIdStr = elemIdStr;
	}

	/**
	 * @return Returns the simuRec.
	 */
	public String getSimuRec() {
		return simuRec;
	}

	/**
	 * @param simuRec
	 *            The simuRec to set.
	 */
	public void setSimuRec(String simuRec) {
		this.simuRec = simuRec;
	}

	/**
	 * @return Returns the simuTime.
	 */
	public double getSimuTime() {
		return simuTime;
	}

	/**
	 * @param simuTime
	 *            The simuTime to set.
	 */
	public void setSimuTime(double simuTime) {
		this.simuTime = simuTime;
	}

	@Override
	public String toString() {
		return "elemId, time, simuRec" + elemIdStr + ", " + simuTime + ", "
				+ simuRec;
	}
}
