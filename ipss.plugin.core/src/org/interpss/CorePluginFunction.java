package org.interpss;

import org.interpss.display.AclfOutFunc;
import org.interpss.display.AclfOutFunc.BusIdStyle;
import org.interpss.display.impl.AclfOut_BusStyle;
import org.interpss.display.impl.AclfOut_PSSE;

import com.interpss.common.func.IFunction;
import com.interpss.common.func.IFunction2;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.aclf.AclfNetwork;
import com.interpss.core.net.OriginalDataFormat;

/**
 * Functions for Core plugin 
 * 
 * @author mzhou
 *
 */
public class CorePluginFunction {
	/**
	 * function to format bus id for output
	 * 
	 */
	public static IFunction2<AclfBus, OriginalDataFormat, String> OutputBusId = 
		new IFunction2<AclfBus, OriginalDataFormat, String>() {
			@Override public String f(AclfBus bus, OriginalDataFormat fmt) {
				if (fmt == OriginalDataFormat.CIM)
					return "Bus" + bus.getNumber();
				return bus.getId();
			}
	};
	
	/**
	 * function to format KV for output
	 * 
	 */
	public static IFunction<Double, String> FormatKVStr = 
		new IFunction<Double, String>() {
			@Override public String f(Double kv) {
				if (kv > 1000.0)
					return String.format("%6.1f ", kv);
				else if (kv > 100.0)
					return String.format("%6.2f ", kv);
				else	
					return String.format("%6.3f ", kv);
			}
	};

  
	/**
	 * function for output LF result for Google cloud edition
	 * 
	 */
	public static IFunction<AclfNetwork, StringBuffer> OutputLF4Google = 
		new IFunction<AclfNetwork, StringBuffer>() {
			@Override public StringBuffer f(AclfNetwork net) {
				if (net.getOriginalDataFormat() == OriginalDataFormat.CIM)
					return AclfOutFunc.loadFlowSummary(net);
				return LfResultBusStyle.f(net);
			}
		};

	/**
	 * function for output LF result in PSS/E format
	 * 	
	 */
	public static IFunction2<AclfNetwork, AclfOut_PSSE.Format, StringBuffer> LfResultPsseStyle = 
		new IFunction2<AclfNetwork, AclfOut_PSSE.Format, StringBuffer>() {
			@Override public StringBuffer f(AclfNetwork net, AclfOut_PSSE.Format format) {
				return AclfOut_PSSE.lfResults(net, format);
			}
		};
	
	/**
	 * Function for output LF result in the Bus Style format
	 */
	public static IFunction<AclfNetwork, StringBuffer> LfResultBusStyle = 
		new IFunction<AclfNetwork, StringBuffer>() {
			@Override public StringBuffer f(AclfNetwork net) {
				return AclfOut_BusStyle.lfResultsBusStyle(net, BusIdStyle.BusId_No);
			}
		};

	/**
	 * Function for output LF result in the Bus Style format
	 */
	public static IFunction2<AclfNetwork, BusIdStyle, StringBuffer> LfResultBusStyle2 = 
		new IFunction2<AclfNetwork, BusIdStyle, StringBuffer>() {
			@Override public StringBuffer f(AclfNetwork net, BusIdStyle style) {
				return AclfOut_BusStyle.lfResultsBusStyle(net, style);
			}
		};
}
