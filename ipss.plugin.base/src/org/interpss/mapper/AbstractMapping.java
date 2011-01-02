package org.interpss.mapper;

import java.util.logging.Logger;

import com.interpss.common.exp.InterpssException;
import com.interpss.common.msg.IPSSMsgHub;
import com.interpss.common.util.IpssLogger;

public abstract class AbstractMapping<Tf, Tt> implements IMapping<Tf, Tt> {
	/**
	 */
	protected IPSSMsgHub msg = null;

	/**
	 */
	protected AbstractMapping() {
	}
	
	/**
	 */
	protected AbstractMapping(IPSSMsgHub msg) {
		this.msg = msg;
	}

	/**
	 */
	public IPSSMsgHub getMsg() {
		return msg;
	}

	public Logger getLogger() {
		return IpssLogger.getLogger();
	}
	
	@Override
	public boolean map2Model(Tf from, Tt model) {
		// needs to be implemented by the concrete child adapters
		throw new UnsupportedOperationException();
	}

	@Override
	public Tt map2Model(Tf from) throws InterpssException {
		// needs to be implemented by the concrete child adapters
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean map2ODM(Tf from, Tt odm) {
		// needs to be implemented by the concrete child adapters
		throw new UnsupportedOperationException();
	}

	@Override
	public Tt map2ODM(Tf from)  throws InterpssException {
		// needs to be implemented by the concrete child adapters
		throw new UnsupportedOperationException();
	}
}
