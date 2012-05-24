package com.interpss.pssl.test;


import org.interpss.IpssCorePlugin;
import org.junit.BeforeClass;

import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.pssl.simu.BaseDSL;
import com.interpss.spring.CoreCommonSpringFactory;

public class BaseTestSetup {
	protected static IPSSMsgHub msg = null;
	
	@BeforeClass
	public static void setSpringAppCtx() {
		IpssCorePlugin.init();
		msg = CoreCommonSpringFactory.getIpssMsgHub();
		BaseDSL.setMsgHub(msg);
	}
}

