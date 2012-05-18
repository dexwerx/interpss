package org.interpss.ed;

import org.interpss.EditorTestSetup;
import org.interpss.ed.EDAlgorithm.LossType;
import org.interpss.ed.EDAlgorithm.ScheduleType;
import org.interpss.ed.sample.SampleObjectFactory;
import org.junit.Test;

public class CostFuncCaseTest  extends EditorTestSetup {
	@Test
	public void EDC1Case() throws Exception {
		IEDGenUnit[] genAry = SampleObjectFactory.createEDC1();
		/*
		for (IEDGenUnit gen : genAry) {
			System.out.println(gen.toString());
		}
		*/
	}			

	@Test
	public void Example3ACase() throws Exception {
		IEDGenUnit[] genAry = SampleObjectFactory.createExample3A();
		/*
		for (IEDGenUnit gen : genAry) {
			System.out.println(gen.toString());
		}
		*/
		EDAlgorithm algo = new EDAlgorithm(LossType.NoLoss, ScheduleType.TotalGen);
		algo.LambdaSearchDispatch(3, genAry, 850.0);
	}			
}
