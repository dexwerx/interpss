package org.interpss.facts.injector.svc;

import org.interpss.facts.general.IFACTSInequalityViolation;

// Deal with the violation of the range of the SVC's equivalent susceptance
public class SVCSusceptanceViolation implements IFACTSInequalityViolation {

	private SVCLF svc;
	private boolean maxViolated = false;
	private boolean minViolated = false;
	
	public SVCSusceptanceViolation(SVCLF svc) {
		super();
		this.svc = svc;
	}

	public boolean isMaxViolated() {
		return maxViolated;
	}

	public boolean isMinViolated() {
		return minViolated;
	}

	@Override
	public void checkViolate() {
		double b = this.svc.getB();
		if (b < this.svc.getMinB())
			violateMinimum();
		else if (b > this.svc.getMaxB())
			violateMaximum();
	}

	private void violateMaximum() {
		this.svc.setType(SVCControlType.ConstB);
		this.svc.setTunedValue(this.svc.getMaxB());
		this.maxViolated = true;
	}

	private void violateMinimum() {
		this.svc.setType(SVCControlType.ConstB);
		this.svc.setTunedValue(this.svc.getMinB());
		this.minViolated = true;
	}

}