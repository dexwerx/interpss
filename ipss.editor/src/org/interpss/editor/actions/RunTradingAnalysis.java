package org.interpss.editor.actions;

import java.awt.event.ActionEvent;

import org.interpss.editor.SimuRunEnum;
import org.interpss.editor.coreframework.IpssAbstractActionDefault;
import org.interpss.editor.coreframework.IpssEditorDocument;
import org.interpss.editor.util.DocumentUtilFunc;
import org.interpss.editor.util.RunUtilFunc;

import com.interpss.pssl.simu.BaseDSL;

public class RunTradingAnalysis extends IpssAbstractActionDefault {
	private static final long serialVersionUID = 1;

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		RunUtilFunc.performRunAction(getCurrentDocument(), SimuRunEnum.TradingAnalysis, graphpad);
	}

	@Override
	public void update() {
		IpssEditorDocument doc = getCurrentDocument();
		setEnabled( BaseDSL.analysisEnabled(BaseDSL.IpssAnalysisType.PowerTrainding) &&
				( DocumentUtilFunc.isAclfDocument(doc) || 
				  DocumentUtilFunc.isDStabDocument(doc)));
	}
}
