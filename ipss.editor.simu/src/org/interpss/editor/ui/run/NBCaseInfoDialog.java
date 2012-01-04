 /*
  * @(#)NBCaseInfoDialog.java   
  *
  * Copyright (C) 2006 www.interpss.org
  *
  * This program is free software; you can redistribute it and/or
  * modify it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE
  * as published by the Free Software Foundation; either version 2.1
  * of the License, or (at your option) any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  *
  * @Author Mike Zhou
  * @Version 1.0
  * @Date 09/15/2006
  * 
  *   Revision History
  *   ================
  *
  */

package org.interpss.editor.ui.run;

import java.io.File;
import java.util.Vector;

import javax.swing.JFileChooser;

import org.ieee.odm.model.ODMModelParser;
import org.ieee.odm.model.ext.ipss.IpssScenarioHelper;
import org.ieee.odm.schema.AclfAnalysisXmlType;
import org.ieee.odm.schema.AcscFaultAnalysisXmlType;
import org.ieee.odm.schema.DStabSimulationXmlType;
import org.ieee.odm.schema.DclfSenAnalysisXmlType;
import org.ieee.odm.schema.PTradingAnalysisXmlType;
import org.ieee.odm.schema.PTradingEDHourlyAnalysisXmlType;
import org.interpss.editor.SimuRunEnum;
import org.interpss.editor.app.AppSimuContextImpl;
import org.interpss.editor.data.proj.CaseData;
import org.interpss.editor.data.proj.ProjData;
import org.interpss.editor.jgraph.GraphSpringFactory;
import org.interpss.editor.jgraph.ui.IGraphicEditor;
import org.interpss.editor.ui.ICaseInfoDialog;
import org.interpss.editor.ui.IOutputTextDialog;
import org.interpss.editor.ui.RunUIUtilFunc;
import org.interpss.editor.ui.util.IpssFileFilter;
import org.interpss.spring.PluginSpringFactory;
import org.interpss.spring.UISpringFactory;
import org.interpss.ui.SwingInputVerifyUtil;
import org.interpss.ui.WinUtilities;
import org.interpss.util.FileUtil;
import org.interpss.xml.IpssXmlParser;
import org.interpss.xml.StudyCaseHanlder;
import org.interpss.xml.schema.InterPSSXmlType;

import com.interpss.common.util.IpssLogger;
import com.interpss.simu.SimuContext;

public class NBCaseInfoDialog extends javax.swing.JDialog implements ICaseInfoDialog {
	private static final long serialVersionUID = 1;

	private boolean  _returnOK = false;
	private SimuRunEnum _caseType = SimuRunEnum.NotDefined;
	
	private String runStudyCaseFilename;
	private StudyCaseHanlder studyCaseXmlDoc;

	private ODMModelParser odmParser = new ODMModelParser();
    public ODMModelParser getODMParser() { return this.odmParser; }    
	
	private static JFileChooser saveTextFileChooser = null;
	
	// ref to the AppInfo where the current project data are hold
	private AppSimuContextImpl _appSimuCtx = null;
	 
    private NBDclfCasePanel  		_dclfCaseInfoPanel = null;
    private NBPTradingCasePanel 	_tradingCaseInfoPanel = null;

    private NBAclfCasePanel  		_aclfCaseInfoPanel = null;
    private NBAcscCasePanel  		_acscCaseInfoPanel = null;
    private NBDStabCasePanel 		_dstabCaseInfoPanel = null;
    private NBScriptingCasePanel 	_scrptsCaseInfoPanel = null;
    
    public NBCaseInfoDialog(java.awt.Frame parent) {
        super(parent, "Run AC Loadflow Analysis", true);
        initComponents();
        WinUtilities.center(this);

        _dclfCaseInfoPanel = new NBDclfCasePanel(this);
        _tradingCaseInfoPanel = new NBPTradingCasePanel(this);
        _aclfCaseInfoPanel = new NBAclfCasePanel(this);

        _acscCaseInfoPanel = new NBAcscCasePanel(this);
        _dstabCaseInfoPanel = new NBDStabCasePanel(this);
        _scrptsCaseInfoPanel = new NBScriptingCasePanel(this);

    	initInputVerifier(new DataVerifier()); 
    }

	public NBCaseInfoDialog(IGraphicEditor parent) {
        this(parent.getFrame());
    }
	
	public void init(Object netContainer, Object appCtx) {
		IpssLogger.getLogger().info("NBCaseInfoDialog init() called");
		_appSimuCtx = (AppSimuContextImpl)appCtx;
        
		caseDataPanel.removeAll();
		setButtonStatus(true);
		this.runButton.setText("Run");

		this.importButton.setEnabled(false);
		
		/*
		 *     ODM schema format
		 */
		
		if (_caseType == SimuRunEnum.SenAnalysis) {
			this.setTitle("Run Sensitivity Analysis");
			this.runButton.setText("Save");
			caseDataPanel.add(_dclfCaseInfoPanel);
			_dclfCaseInfoPanel.init(netContainer, _appSimuCtx.getSimuCtx());
		}
		else if (_caseType == SimuRunEnum.TradingAnalysis) {
			this.setTitle("Run Trading Analysis");
			this.runButton.setText("Save");
	        caseDataPanel.add(this._tradingCaseInfoPanel);
			_tradingCaseInfoPanel.init(netContainer, _appSimuCtx.getSimuCtx());
		}
		else if (_caseType == SimuRunEnum.Aclf) {
			this.setTitle("Run Aclf Loadflow Analysis");
			caseDataPanel.add(_aclfCaseInfoPanel);
			_aclfCaseInfoPanel.init(netContainer, _appSimuCtx.getSimuCtx(), true);
			((SimuContext)_appSimuCtx.getSimuCtx()).getMsgHub().addMsgListener(_aclfCaseInfoPanel);
		}
		else if (_caseType == SimuRunEnum.Acsc) {
			this.setTitle("Run Acsc Short Circuit Analysis");
			caseDataPanel.add(_acscCaseInfoPanel);
			_acscCaseInfoPanel.init(netContainer, _appSimuCtx);
		}	
		else if (_caseType == SimuRunEnum.DStab) {
			this.setTitle("Run Transient Stability Simulation");
			caseDataPanel.add(_dstabCaseInfoPanel);
			_dstabCaseInfoPanel.init(netContainer, _appSimuCtx.getSimuCtx());
		}
		
		/*
		 *     InterPSS schema format
		 */
	
		else if (_caseType == SimuRunEnum.Scripts) {
			setButtonStatus(false);
			this.setTitle("Custom Scripting Run Case");
			caseDataPanel.add(_scrptsCaseInfoPanel);
			_scrptsCaseInfoPanel.init(netContainer, _appSimuCtx);
		}	
		
        _returnOK = false;
        
		loadRunXmlDocument(runStudyCaseFilename);
		
        pack();
        WinUtilities.center(this);
        setVisible(true);
	}
    
	private void setButtonStatus(boolean b) {
	    //this.casenameComboBox.setEnabled(b);
	    //private javax.swing.JTextArea descTextArea;
	    this.viewXmlButton.setEnabled(b);
	    this.importButton.setEnabled(b);
	}
	
	private void loadRunXmlDocument(String filename) {
		try {
			if (_caseType != SimuRunEnum.Scripts) {
				if (isODMFormat()) {
					this.odmParser = RunUIUtilFunc.loadODMXmlDoc(filename, _caseType);
				}
				else {
					InterPSSXmlType ipssXmlDoc = RunUIUtilFunc.loadIpssXmlDoc(filename, _caseType);
					this.studyCaseXmlDoc = new StudyCaseHanlder(ipssXmlDoc);
				}
			}
	        setForm2Editor();
		} catch (Exception e) {
			e.printStackTrace();
			((SimuContext)_appSimuCtx.getSimuCtx()).getMsgHub().sendErrorMsg(e.toString());
		}
	}
	
	/**
	 * When the Run button clicked, return true to perform the run case
	 * 
	 * @return if the Run button was clicked
	 */
    public boolean isReturnOk() {
        return _returnOK;
    }

    /**
     * Set the type for the current case being edited
     * 
     * @param type case type
     */
    public void setCaseType(SimuRunEnum type) {
        _caseType = type;
    }
    
    public SimuRunEnum getCaseType() {
    	return this._caseType;
    }
    
    public void setRunStudyCaseFilename(String filename) {
    	this.runStudyCaseFilename = filename;
    }
    
    public void setDStabOutputScriptFilename(String filename) {
    	_dstabCaseInfoPanel.setDStabOutputScriptFilename(filename);    
    }

    private boolean isODMFormat() {
		return _caseType == SimuRunEnum.TradingAnalysis ||
		       _caseType == SimuRunEnum.SenAnalysis ||
		       _caseType == SimuRunEnum.DStab ||
		       _caseType == SimuRunEnum.Acsc ||
		       _caseType == SimuRunEnum.Aclf;    	
    }
	/**
	*	Set form data to the editor
	*
	* @return false if there is any problem
	*/
	public boolean setForm2Editor() {
		IpssLogger.getLogger().info("NBCaseInfoDialog setForm2Editor() called");

		String casename = "NewCase";
		String casedesc = "Case Desc";

		if (isODMFormat()) {
			IpssScenarioHelper helper = new IpssScenarioHelper(this.odmParser);
			if (_caseType == SimuRunEnum.TradingAnalysis) {
				PTradingEDHourlyAnalysisXmlType ptXml = helper.getPtEDHourlyAnalysis();			
				casename = ptXml.getName();
				casedesc = ptXml.getDesc();
				// set the case data to the actual data editing panel
				_tradingCaseInfoPanel.setODMParser(this.odmParser);
				_tradingCaseInfoPanel.setXmlCaseData(ptXml);
				// set the case data to the actual data editing panel
				_tradingCaseInfoPanel.setForm2Editor();
			}
			else if (_caseType == SimuRunEnum.SenAnalysis) {
				DclfSenAnalysisXmlType senXml;
				if (helper.getSenAnalysisList().size() > 0)
					senXml = helper.getSenAnalysisList().get(0);
				else
					senXml = helper.createSenCase();
				casename = senXml.getName();
				casedesc = senXml.getDesc();
				// set the case data to the actual data editing panel
				_dclfCaseInfoPanel.setODMParser(this.odmParser);
				_dclfCaseInfoPanel.setXmlCaseData(senXml, helper.getPtEDHourlyAnalysis());
				// set the case data to the actual data editing panel
				_dclfCaseInfoPanel.setForm2Editor();
			}
			else if (_caseType == SimuRunEnum.Aclf) {
				AclfAnalysisXmlType aclfXml = helper.getAclfAnalysis();			
				casename = aclfXml.getName();
				casedesc = aclfXml.getDesc();
				// set the case data to the actual data editing panel
				_aclfCaseInfoPanel.setXmlCaseData(aclfXml, helper.getContingencyAnalysis(), helper.getGridRunOption());
				// set the case data to the actual data editing panel
				_aclfCaseInfoPanel.setForm2Editor();
			}			
			else if (_caseType == SimuRunEnum.Acsc) {
				AcscFaultAnalysisXmlType acscXml = helper.getAcscFaultAnalysis();			
				casename = acscXml.getName();
				casedesc = acscXml.getDesc();
				// set the case data to the actual data editing panel
				_acscCaseInfoPanel.setXmlCaseDatax(acscXml, false);
				// set the case data to the actual data editing panel
				_acscCaseInfoPanel.setForm2Editor();
			}
			if (_caseType == SimuRunEnum.DStab) {
				DStabSimulationXmlType dstabXml = helper.getDStabSimulation();			
				casename = dstabXml.getName();
				casedesc = dstabXml.getDesc();
				_dstabCaseInfoPanel.setXmlCaseData(dstabXml, helper.getGridRunOption());
				_dstabCaseInfoPanel.setForm2Editor();
			}
			this.casenameTextField.setText(casename);
			this.descTextArea.setText(casedesc);
		}
		else {
			/*
			 *  InterPSS schema format
			 */

			if (_caseType == SimuRunEnum.Scripts) {
				// build the case info combo list
				this.casenameTextField.setText(_appSimuCtx.getCasename(_caseType));
				
				ProjData projData = (ProjData)_appSimuCtx.getProjData();
				casename = projData.getScriptsCaseName();
				projData.setScriptsCaseName(casename);
				// retrieve the case data from appCtx.projData.caseList
				CaseData caseData = _appSimuCtx.getCaseData(casename, _caseType);
				if ( caseData == null)
					// new case situation, create a new case
					caseData = _appSimuCtx.createCaseData(casename, _caseType);
				
				this.descTextArea.setText(caseData.getDescription());
				
				_scrptsCaseInfoPanel.setCaseData(caseData);
				_scrptsCaseInfoPanel.setForm2Editor();
			}
		}
		
        return true;
	}

//	private String getCaseName(SimuRunEnum caseType) {
//		ProjData projData = (ProjData)_appSimuCtx.getProjData();
//		String casename = "";
//		if (caseType == SimuRunEnum.Aclf) {
//			this.casenameComboBox.setModel(new javax.swing.DefaultComboBoxModel(this.studyCaseXmlDoc.getAclfStudyCaseNameArray()));
//			casename = selectCase(projData.getAclfCaseName());
//			projData.setAclfCaseName(casename);
//		}
//		else if (caseType == SimuRunEnum.SenAnalysis) {
//			this.casenameComboBox.setModel(new javax.swing.DefaultComboBoxModel(this.studyCaseXmlDoc.getDclfStudyCaseNameArray()));
//			casename = selectCase(projData.getDclfCaseName());
//			projData.setDclfCaseName(casename);
//		}
//		else if (caseType == SimuRunEnum.TradingAnalysis) {
//			this.casenameComboBox.setModel(new javax.swing.DefaultComboBoxModel(this.studyCaseXmlDoc.getTradingStudyCaseNameArray()));
//			casename = selectCase(projData.getPTradingCaseName());
//			projData.setPTradingCaseName(casename);
//		}
//		else if (caseType == SimuRunEnum.Acsc) {
//			this.casenameComboBox.setModel(new javax.swing.DefaultComboBoxModel(this.studyCaseXmlDoc.getAcscStudyCaseNameArray()));
//			casename = selectCase(projData.getAcscCaseName());
//			projData.setAcscCaseName(casename);
//		}
//		else if (caseType == SimuRunEnum.DStab) {
//			this.casenameComboBox.setModel(new javax.swing.DefaultComboBoxModel(this.studyCaseXmlDoc.getDStabStudyCaseNameArray()));
//			casename = selectCase(projData.getDStabCaseName());
//			projData.setDStabCaseName(casename);
//		}
//		IpssLogger.getLogger().info("Selected casename: " + casename);
//		return casename;
//	}
//
//	private String selectCase(String casename) {
//		this.casenameComboBox.setSelectedItem(casename);
//		if (this.casenameComboBox.getSelectedIndex() == -1)
//			this.casenameComboBox.setSelectedIndex(0);
//		casename = (String)this.casenameComboBox.getSelectedItem();
//		return casename;
//	}
	
	/**
	*	Save editor screen data to the form
	*
	* @param errMsg error messages during the saving process.
	* @return false if there is any problem
	*/
    public boolean saveEditor2Form(Vector<String> errMsg) throws Exception {
		IpssLogger.getLogger().info("NBCaseInfoDialog saveEditor2Form() called");

		if (SwingInputVerifyUtil.isEmptyStr(this.casenameTextField)) {
			errMsg.add("Casename is empty");
			return false;
		}
		
		String casename = (String)this.casenameTextField.getText();
		//_caseData.setCaseName(casename);
		//_caseData.setDescription(this.descTextArea.getText());
		
		if (isODMFormat()) {
			// ODM schema
			IpssScenarioHelper helper = new IpssScenarioHelper(this.odmParser);
			if (_caseType == SimuRunEnum.TradingAnalysis) {
				PTradingAnalysisXmlType ptCase = helper.getPtEDHourlyAnalysis();			
				ptCase.setName(this.casenameTextField.getText());
				ptCase.setDesc(this.descTextArea.getText());
				_tradingCaseInfoPanel.saveEditor2Form(errMsg);			
			}
			else if (_caseType == SimuRunEnum.SenAnalysis) {
				DclfSenAnalysisXmlType dclfCase;
				if (helper.getSenAnalysisList().size() > 0)
					dclfCase = helper.getSenAnalysisList().get(0);
				else
					dclfCase = helper.createSenCase();			
				dclfCase.setName(this.casenameTextField.getText());
				dclfCase.setDesc(this.descTextArea.getText());
				_dclfCaseInfoPanel.saveEditor2Form(errMsg);
			}	
			else if (_caseType == SimuRunEnum.Aclf) {
				AclfAnalysisXmlType aclfCase = helper.getAclfAnalysis();			
				aclfCase.setName(this.casenameTextField.getText());
				aclfCase.setDesc(this.descTextArea.getText());
				this._aclfCaseInfoPanel.saveEditor2Form(errMsg);	
			}			
			else if (_caseType == SimuRunEnum.Acsc) {
				AcscFaultAnalysisXmlType acscCase = helper.getAcscFaultAnalysis();			
				acscCase.setName(this.casenameTextField.getText());
				acscCase.setDesc(this.descTextArea.getText());
				_acscCaseInfoPanel.saveEditor2Form(errMsg);
			}
			if (_caseType == SimuRunEnum.DStab) {
				DStabSimulationXmlType dstabCase = helper.getDStabSimulation();			
				dstabCase.setName(this.casenameTextField.getText());
				dstabCase.setDesc(this.descTextArea.getText());
				_dstabCaseInfoPanel.saveEditor2Form(errMsg);
			}
			FileUtil.writeText2File(runStudyCaseFilename, this.odmParser.toXmlDoc(false));
		}
		
		// ipss schema
		else {
			ProjData projData = (ProjData)_appSimuCtx.getProjData();

			if (_caseType == SimuRunEnum.Scripts) {
				projData.setScriptsCaseName(casename);
				_scrptsCaseInfoPanel.saveEditor2Form(errMsg);
				return true;
			}
			
			// save run case xml doc
			FileUtil.writeText2File(runStudyCaseFilename, 
					new IpssXmlParser().toString(this.studyCaseXmlDoc.getIpssXmlDoc()));
			
		}

        return errMsg.size() == 0;
	}
    
    /** Creates new form NBAclfCalDialog */
    public NBCaseInfoDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        caseInfoPanel = new javax.swing.JPanel();
        casenameLabel = new javax.swing.JLabel();
        casenameTextField = new javax.swing.JTextField();
        descLabel = new javax.swing.JLabel();
        scrollPane = new javax.swing.JScrollPane();
        descTextArea = new javax.swing.JTextArea();
        caseDataPanel = new javax.swing.JPanel();
        controlPanel = new javax.swing.JPanel();
        runButton = new javax.swing.JButton();
        viewXmlButton = new javax.swing.JButton();
        importButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setModal(true);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        caseInfoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Study Case", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N

        casenameLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        casenameLabel.setText("Casename");

        casenameTextField.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        casenameTextField.setText("Casename");

        descLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        descLabel.setText("Description");

        scrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        descTextArea.setColumns(30);
        descTextArea.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        descTextArea.setRows(2);
        descTextArea.setText("Case description");
        descTextArea.setName("descTextArea"); // NOI18N
        scrollPane.setViewportView(descTextArea);

        org.jdesktop.layout.GroupLayout caseInfoPanelLayout = new org.jdesktop.layout.GroupLayout(caseInfoPanel);
        caseInfoPanel.setLayout(caseInfoPanelLayout);
        caseInfoPanelLayout.setHorizontalGroup(
            caseInfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(caseInfoPanelLayout.createSequentialGroup()
                .add(caseInfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(caseInfoPanelLayout.createSequentialGroup()
                        .add(11, 11, 11)
                        .add(casenameLabel)
                        .add(18, 18, 18)
                        .add(casenameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 257, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(caseInfoPanelLayout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(descLabel)
                        .add(18, 18, 18)
                        .add(scrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 338, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        caseInfoPanelLayout.setVerticalGroup(
            caseInfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(caseInfoPanelLayout.createSequentialGroup()
                .add(5, 5, 5)
                .add(caseInfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(casenameLabel)
                    .add(casenameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(caseInfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(caseInfoPanelLayout.createSequentialGroup()
                        .add(16, 16, 16)
                        .add(descLabel))
                    .add(caseInfoPanelLayout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(scrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 0, 20);
        getContentPane().add(caseInfoPanel, gridBagConstraints);

        caseDataPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 40, 0, 40);
        getContentPane().add(caseDataPanel, gridBagConstraints);

        runButton.setFont(new java.awt.Font("Dialog", 0, 12));
        runButton.setText("Run");
        runButton.setToolTipText("Run the current case");
        runButton.setName("runButton"); // NOI18N
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });
        controlPanel.add(runButton);

        viewXmlButton.setFont(new java.awt.Font("Dialog", 0, 12));
        viewXmlButton.setText("ViewXml");
        viewXmlButton.setToolTipText("Cancel and close the case dialog");
        viewXmlButton.setName("cancelButton"); // NOI18N
        viewXmlButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewXmlButtonActionPerformed(evt);
            }
        });
        controlPanel.add(viewXmlButton);

        importButton.setFont(new java.awt.Font("Dialog", 0, 12));
        importButton.setText("Import");
        importButton.setToolTipText("Cancel and close the case dialog");
        importButton.setName("cancelButton"); // NOI18N
        importButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importButtonActionPerformed(evt);
            }
        });
        controlPanel.add(importButton);

        cancelButton.setFont(new java.awt.Font("Dialog", 0, 12));
        cancelButton.setText("Cancel");
        cancelButton.setToolTipText("Cancel and close the case dialog");
        cancelButton.setName("cancelButton"); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        controlPanel.add(cancelButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 30, 20, 30);
        getContentPane().add(controlPanel, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
		_returnOK = false;
		if (_caseType == SimuRunEnum.Aclf)
			((SimuContext)_appSimuCtx.getSimuCtx()).getMsgHub().removeMsgListener(_aclfCaseInfoPanel);
        setVisible(false);
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
		Vector<String> errMsg = new Vector<String>();
		try {
        	if (!saveEditor2Form(errMsg)) {
        		PluginSpringFactory.getEditorDialogUtil().showMsgDialog(this, "Input Data Error", errMsg);
				return;
        	}
        } catch (Exception e) {
        	IpssLogger.logErr(e);
        	PluginSpringFactory.getEditorDialogUtil().showMsgDialog(this, "Input Data Error", e.toString());
			return;
        }	
        
        if (this.runButton.getText().equals("Save")) {
    		_returnOK = false;
        }
        else {
    		_returnOK = true;
    		_appSimuCtx.getProjData().setDirty(true);
    		if (_caseType == SimuRunEnum.Aclf) {
    			((SimuContext)_appSimuCtx.getSimuCtx()).getMsgHub().removeMsgListener(_aclfCaseInfoPanel);
    			this._caseType = _aclfCaseInfoPanel.getPanelSelected() == NBAclfCasePanel.LoadflowPanel? 
    					SimuRunEnum.Aclf : SimuRunEnum.ContingencyAnalysis;
    		}
    		GraphSpringFactory.getIpssGraphicEditor().refreshCurrentDocumentEditorPanel();        
        }
        setVisible(false);
        dispose();
    }//GEN-LAST:event_runButtonActionPerformed

private void viewXmlButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewXmlButtonActionPerformed
	this.setAlwaysOnTop(false);
	Vector<String> errMsg = new Vector<String>();	
	try {
    	if (!saveEditor2Form(errMsg)) {
    		PluginSpringFactory.getEditorDialogUtil().showMsgDialog(this, "Input Data Error", errMsg);
			return;
    	}
    } catch (Exception e) {
    	IpssLogger.logErr(e);
    	PluginSpringFactory.getEditorDialogUtil().showMsgDialog(this, "Input Data Error", e.toString());
		return;
    }	
    
	IOutputTextDialog dialog = UISpringFactory.getOutputTextDialog("Run Study Case Xml");
	dialog.disableFeature("busStyleRadioButton");
	dialog.disableFeature("summaryRadioButton");
	 if (this.isODMFormat()) {
		 dialog.display(this.odmParser.toXmlDoc(false));
	 }
	 else
		 dialog.display(new IpssXmlParser().toString(this.studyCaseXmlDoc.getIpssXmlDoc()));
}//GEN-LAST:event_viewXmlButtonActionPerformed

private void importButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importButtonActionPerformed
	this.setAlwaysOnTop(false);
	JFileChooser fChooser = getSaveTextFileChooser();
	int retValue = fChooser.showOpenDialog(this);
	if (retValue == JFileChooser.APPROVE_OPTION) {
		File file = fChooser.getSelectedFile();
		String filename = file.getPath();
		loadRunXmlDocument(filename);
	}   	
}//GEN-LAST:event_importButtonActionPerformed
    
private JFileChooser getSaveTextFileChooser() {
	if (saveTextFileChooser == null) {
		saveTextFileChooser = new JFileChooser();
		saveTextFileChooser.addChoosableFileFilter(new IpssFileFilter("xml", "InterPSS Xml Document"));
		saveTextFileChooser.setCurrentDirectory(new File(IpssFileFilter.RUNXML_DEFAULT_DIR));
	}
	return saveTextFileChooser;
}	

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel caseDataPanel;
    private javax.swing.JPanel caseInfoPanel;
    private javax.swing.JLabel casenameLabel;
    private javax.swing.JTextField casenameTextField;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JLabel descLabel;
    private javax.swing.JTextArea descTextArea;
    private javax.swing.JButton importButton;
    private javax.swing.JButton runButton;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JButton viewXmlButton;
    // End of variables declaration//GEN-END:variables

	private void initInputVerifier(DataVerifier v) {
        this.casenameTextField.setInputVerifier(v);
	} 
	
    class DataVerifier extends javax.swing.InputVerifier {
    	public boolean verify(javax.swing.JComponent input) {
			if (input == null)
				return false;
       		try {
				if (input == casenameTextField )
					return !SwingInputVerifyUtil.isEmptyStr((javax.swing.JComboBox)input);
 	       	} catch (Exception e) {
 	    		return false;
 	       	}		
			return true;
        }
    }
}
