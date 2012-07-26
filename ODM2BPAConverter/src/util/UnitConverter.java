package util;

import org.ieee.odm.schema.ActivePowerUnitType;
import org.ieee.odm.schema.CurrentUnitType;
import org.ieee.odm.schema.ReactivePowerUnitType;
import org.ieee.odm.schema.VoltageUnitType;
import org.ieee.odm.schema.YUnitType;
import org.ieee.odm.schema.ZUnitType;

public class UnitConverter {
	
	public static double convVoltUnit(double value, VoltageUnitType fromT,VoltageUnitType toT,double baseKV){
		double toValue=0;
		if(fromT==VoltageUnitType.PU){
			if(toT==VoltageUnitType.KV){
				toValue=value*baseKV;
			}
			else if(toT==VoltageUnitType.VOLT){
				toValue=value*baseKV*1000;
			}
		}
		else if(fromT==VoltageUnitType.KV){
			if(toT==VoltageUnitType.PU){
				toValue=value/baseKV;
			}
			else if(toT==VoltageUnitType.VOLT){
				toValue=value*1000;
			}
		}
		return toValue;
	}
	public static double convCurrentUnit(double value, CurrentUnitType fromT,CurrentUnitType toT){
		double toValue=0;
		if(fromT==CurrentUnitType.AMP){
			if(toT==CurrentUnitType.KA){
				toValue=value/1000;
			}
			else throw new UnsupportedOperationException("The toUnitType is un-supportted yet!");
		}
		else if(fromT==CurrentUnitType.KA){
			if(toT==CurrentUnitType.AMP){
				toValue=value*1000;
			}
			else throw new UnsupportedOperationException("The toUnitType is un-supportted yet!");
		}
		return toValue;
	}
	public static double convActivePowerUnit(double value, ActivePowerUnitType fromT,ActivePowerUnitType toT,double baseMVA){
		double toValue=0;
		if(fromT==ActivePowerUnitType.PU){
			if(toT==ActivePowerUnitType.MW){
				toValue=value*baseMVA;
			}
			else if(toT==ActivePowerUnitType.KW){
				toValue=value*baseMVA*1000;
			}
		}
		else if(fromT==ActivePowerUnitType.MW){
			if(toT==ActivePowerUnitType.PU){
				toValue=value/baseMVA;
			}
			else if(toT==ActivePowerUnitType.KW){
				toValue=value*1000;
			}
		}
		return toValue;
	}
	public static double convReactivePowerUnit(double value, ReactivePowerUnitType fromT,ReactivePowerUnitType toT,double baseMVA){
		double toValue=0;
		if(fromT==ReactivePowerUnitType.PU){
			if(toT==ReactivePowerUnitType.MVAR){
				toValue=value*baseMVA;
			}
			else throw new UnsupportedOperationException("The toUnitType is un-supportted yet!");
		}
		else if(fromT==ReactivePowerUnitType.MVAR){
			if(toT==ReactivePowerUnitType.PU){
				toValue=value/baseMVA;
			}
			else throw new UnsupportedOperationException("The toUnitType is un-supportted yet!");
		}
		return toValue;
	}
	public static double convYUnit(double value, YUnitType fromT,ReactivePowerUnitType toT,double baseMVA){
		double toValue=0;
		if(fromT==YUnitType.PU){
			if(toT==ReactivePowerUnitType.MVAR){
				toValue=value*baseMVA;
			}
			else throw new UnsupportedOperationException("The toUnitType is un-supportted yet!");
		}
			else throw new UnsupportedOperationException("The fromUnitType is un-supportted yet!");
		
		return toValue;
	}
	public static double convZUnit(double value, ZUnitType fromT,ZUnitType toT,double baseKV,double baseMVA){
		double Zbase=baseKV*baseKV/baseMVA;
		double toValue=0;
		
		if(fromT==ZUnitType.PU){
			if(toT==ZUnitType.OHM){
				toValue=value*Zbase;
			}
			else throw new UnsupportedOperationException("The toUnitType is un-supportted yet!");
		}
		if(fromT==ZUnitType.OHM){
			if(toT==ZUnitType.PU)toValue=value/Zbase;
			else throw new UnsupportedOperationException("The toUnitType is un-supportted yet!");
		}
		return toValue;
	}
	
	

}
