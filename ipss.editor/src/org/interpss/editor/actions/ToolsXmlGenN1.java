package org.interpss.editor.actions;

import java.awt.event.ActionEvent;

import org.interpss.editor.coreframework.IpssAbstractActionDefault;
import org.interpss.editor.coreframework.IpssEditorDocument;
import org.interpss.editor.jgraph.GraphSpringFactory;
import org.interpss.editor.jgraph.ui.app.IAppSimuContext;
import org.interpss.editor.ui.IOutputTextDialog;
import org.interpss.editor.util.DocumentUtilFunc;
import org.interpss.spring.UISpringFactory;
import org.interpss.xml.IpssXmlParser;
import org.interpss.xml.schema.AclfStudyCaseListXmlType;
import org.interpss.xml.schema.AclfStudyCaseXmlType;
import org.interpss.xml.schema.BranchChangeRecXmlType;
import org.interpss.xml.schema.BusChangeRecXmlType;
import org.interpss.xml.schema.ModificationXmlType;

import com.interpss.common.util.IpssLogger;
import com.interpss.core.aclf.AclfBus;
import com.interpss.core.net.Branch;
import com.interpss.core.net.Bus;
import com.interpss.core.net.Network;
import com.interpss.simu.SimuContext;


public class ToolsXmlGenN1 extends IpssAbstractActionDefault {
	private static final long serialVersionUID = 1;
    
	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		IAppSimuContext project;
		try {
			project = GraphSpringFactory.getIpssGraphicEditor().getCurrentAppSimuContext();
		} catch (Exception ex) {
			IpssLogger.logErr(ex);
			return;
		}		
		
		SimuContext simuCtx = (SimuContext)project.getSimuCtx();
		Network net = simuCtx.getNetwork();
		AclfStudyCaseListXmlType list =	IpssXmlParser.getFactory().createAclfStudyCaseListXmlType();
  		for (Branch bra : net.getBranchList()) {
  			AclfStudyCaseXmlType studyCase = IpssXmlParser.getFactory().createAclfStudyCaseXmlType();
  			list.getAclfStudyCase().add(studyCase);
  			String id = bra.getFromBus().getId()+"-"+bra.getToBus().getId()+"_"+bra.getCircuitNumber();
  			studyCase.setRecId("StudyCase_OpenBranch_"+id);
  			studyCase.setRecDesc("Open branch " + bra.getId());
  			ModificationXmlType mod = IpssXmlParser.getFactory().createModificationXmlType(); 
  			studyCase.setModification(mod);
  			BranchChangeRecXmlType changeRec = IpssXmlParser.getFactory().createBranchChangeRecXmlType();
  			mod.setBranchChangeRecList(IpssXmlParser.getFactory().createModificationXmlTypeBranchChangeRecList());
  			mod.getBranchChangeRecList().getBranchChangeRec().add(changeRec);
  			changeRec.setRecId("OpenBranch_"+id);
  			changeRec.setFromBusId(bra.getFromBus().getId());
  			changeRec.setToBusId(bra.getToBus().getId());
  			changeRec.setOffLine(true);
		}
  		
  		for (Bus bus : net.getBusList()) {
  			if (bus.isActive() && ((AclfBus)bus).isGen() && !((AclfBus)bus).isSwing()) {
  				AclfStudyCaseXmlType studyCase = IpssXmlParser.getFactory().createAclfStudyCaseXmlType(); 
  				list.getAclfStudyCase().add(studyCase);
  	  			studyCase.setRecId("StudyCase_GenOutage_"+bus.getId());
  	  			studyCase.setRecDesc("Generator outage at bus " + bus.getId());
  	  			ModificationXmlType mod =  IpssXmlParser.getFactory().createModificationXmlType();
  	  			studyCase.setModification(mod);
  	  			BusChangeRecXmlType changeRec =  IpssXmlParser.getFactory().createBusChangeRecXmlType(); 
  	  			mod.setBusChangeRecList(IpssXmlParser.getFactory().createModificationXmlTypeBusChangeRecList());
  	  		    mod.getBusChangeRecList().getBusChangeRec().add(changeRec);
  	  			changeRec.setRecId("GenOutage_"+bus.getId());
  	  			changeRec.setBusId(bus.getId());
  	  			changeRec.setGenOutage(true);
  			}
		}
  		
		IOutputTextDialog dialog = UISpringFactory.getOutputTextDialog("N-1 Modification Xml Document");
		dialog.display(list.toString()
						.replaceAll("xmlns:sch", "xmlns:ipss")
						.replaceAll("<sch:", "<ipss:")
						.replaceAll("</sch:", "</ipss:"));  		
	}

	public void update() {
		IpssEditorDocument doc = getCurrentDocument();
		setEnabled(DocumentUtilFunc.isSimuDocument(doc));
	}
}
