package org.interpss.facts.injector.upfc;

import org.interpss.facts.general.FACTSControlType;

// All possible UPFC control types
public enum UPFCControlType implements FACTSControlType {
	ActiveAndReactivePowerFlow, VoltageShiftingPowerFlow, GeneralDirectVoltageInjection, PhaseShiftingRegulation
}