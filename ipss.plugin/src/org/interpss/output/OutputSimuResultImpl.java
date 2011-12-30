 /*
  * @(#)OutputSimuResultImpl.java   
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

import static com.interpss.pssl.plugin.IpssOut.*;

import org.interpss.display.DclfOutFunc;
import org.interpss.util.FileUtil;

import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.dclf.DclfAlgorithm;

public class OutputSimuResultImpl implements IOutputSimuResult {

	@Override
	public boolean outAclfResult(AclfNetwork net, String outFilename) {
		FileUtil.writeText2File(outFilename, 
				aclfResultSummary.apply(net).toString());
		return false;
	}

	@Override
	public boolean outDclfResult(DclfAlgorithm algo, String outFilename) {
		FileUtil.writeText2File(outFilename, DclfOutFunc.dclfResults(algo, false));
		return true;
	}
}
