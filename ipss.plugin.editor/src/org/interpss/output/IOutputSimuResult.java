 /*
  * @(#)IOutputSimuResult.java   
  *
  * Copyright (C) 2006 www.interpss.org
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
  * @Author Mike Zhou
  * @Version 1.0
  * @Date 11/27/2007
  * 
  *   Revision History
  *   ================
  *
  */

package org.interpss.output;

import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.dclf.DclfAlgorithm;

public interface IOutputSimuResult {
	/**
	 * Output Dclf analysis result
	 * 
	 * @param net an AclfNetwork object
	 * @param outFilename output filename
	 * @return
	 */
	boolean outDclfResult(DclfAlgorithm algo, String outFilename);

	/**
	 * Output Aclf analysis result
	 * 
	 * @param net an AclfAdjNetwork object
	 * @param outFilename output filename
	 * @return
	 */
	boolean outAclfResult(AclfNetwork net, String outFilename);
}
