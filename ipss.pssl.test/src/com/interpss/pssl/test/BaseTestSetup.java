package com.interpss.pssl.test;


import org.interpss.IpssPlugin;
import org.junit.BeforeClass;

import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.spring.CoreCommonSpringCtx;

public class BaseTestSetup {
	protected static IPSSMsgHub msg = null;
	
	@BeforeClass
	public static void setSpringAppCtx() {
		IpssPlugin.init();
		msg = CoreCommonSpringCtx.getIpssMsgHub();
	}
}

