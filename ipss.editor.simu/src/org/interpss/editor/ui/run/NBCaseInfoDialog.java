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
import org.ieee.odm.model.scenario.IpssScenarioHelper;
import org.ieee.odm.schema.PTradingAnalysisXmlType;
import org.interpss.editor.EditorSimuSpringCtx;
import org.interpss.editor.SimuRunEnum;
import org.interpss.editor.app.AppSimuContextImpl;
import org.interpss.editor.data.proj.CaseData;
import org.interpss.editor.data.proj.ProjData;
import org.interpss.editor.jgraph.GraphSpringAppContext;
import org.interpss.editor.jgraph.ui.IGraphicEditor;
import org.interpss.editor.ui.ICaseInfoDialog;
import org.interpss.editor.ui.IOutputTextDialog;
import org.interpss.editor.ui.RunUIUtilFunc;
import org.interpss.editor.ui.UISpringAppContext;
import org.interpss.editor.ui.util.IpssFileFilter;
import org.interpss.spring.BasePluginSpringFactory;
import org.interpss.ui.SwingInputVerifyUtil;
import org.interpss.ui.WinUtilities;
import org.interpss.util.FileUtil;
import org.interpss.xml.IpssXmlParser;
import org.interpss.xml.StudyCaseHanlder;
import org.interpss.xml.schema.AclfStudyCaseXmlType;
import org.interpss.xml.schema.AcscStudyCaseXmlType;
import org.interpss.xml.schema.ContingencyAnalysisXmlType;
import org.interpss.xml.schema.DStabStudyCaseXmlType;
import org.interpss.xml.schema.DclfStudyCaseXmlType;
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
	
	private static JFileChooser saveTextFileChooser = null;
	
	// ref to the AppInfo where the current project data are hold
	private AppSimuContextImpl _appSimuCtx = null;
	 
    private NBDclfCasePanel  _dclfCaseInfoPanel = null;
    private NBAclfCasePanel  _aclfCaseInfoPanel = null;
    private NBAcscCasePanel  _acscCaseInfoPanel = null;
    private NBDStabCasePanel _dstabCaseInfoPanel = null;
    private NBScriptingCasePanel _scrptsCaseInfoPanel = null;
    private NBPTradingCasePanel _tradingCaseInfoPanel = null;
    
    public NBCaseInfoDialog(java.awt.Frame parent) {
        super(parent, "Run AC Loadflow Analysis", true);
        initComponents();
        WinUtilities.center(this);

        _aclfCaseInfoPanel = new NBAclfCasePanel(this);
        _dclfCaseInfoPanel = new NBDclfCasePanel(this);
        _acscCaseInfoPanel = new NBAcscCasePanel(this);
        _dstabCaseInfoPanel = new NBDStabCasePanel(this);
        _scrptsCaseInfoPanel = new NBScriptingCasePanel(this);
        _tradingCaseInfoPanel = new NBPTradingCasePanel(this);

        DataVerifier verifier = new DataVerifier();
		this.casenameComboBox.setInputVerifier(verifier);
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
		if (_caseType == SimuRunEnum.Aclf) {
			this.setTitle("Run Aclf Loadflow Analysis");
			caseDataPanel.add(_aclfCaseInfoPanel);
			_aclfCaseInfoPanel.init(netContainer, _appSimuCtx.getSimuCtx(), true);
			((SimuContext)_appSimuCtx.getSimuCtx()).getMsgHub().addMsgListener(_aclfCaseInfoPanel);
		}
		else if (_caseType == SimuRunEnum.SenAnalysis) {
			this.setTitle("Run Sensitivity Analysis");
			this.runButton.setText("Save");
			caseDataPanel.add(_dclfCaseInfoPanel);
			_dclfCaseInfoPanel.init(netContainer, _appSimuCtx.getSimuCtx());
		}
		else if (_caseType == SimuRunEnum.TradingAnalysis) {
			this.setTitle("Run Trading Analysis");
			this.runButton.setText("Save");
			this.addCaseButton.setEnabled(false);
			this.deleteCaseButton.setEnabled(false);
			this.importButton.setEnabled(false);
	        caseDataPanel.add(this._tradingCaseInfoPanel);
			_tradingCaseInfoPanel.init(netContainer, _appSimuCtx.getSimuCtx());
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
	    this.addCaseButton.setEnabled(b);
	    //this.casenameComboBox.setEnabled(b);
	    this.deleteCaseButton.setEnabled(b);
	    //private javax.swing.JTextArea descTextArea;
	    this.viewXmlButton.setEnabled(b);
	    this.importButton.setEnabled(b);
	}
	
	private void loadRunXmlDocument(String filename) {
		try {
			if (_caseType != SimuRunEnum.Scripts) {
				if (_caseType == SimuRunEnum.TradingAnalysis) {
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

	/**
	*	Set form data to the editor
	*
	* @return false if there is any problem
	*/
	public boolean setForm2Editor() {
		IpssLogger.getLogger().info("NBCaseInfoDialog setForm2Editor() called");

		if (_caseType == SimuRunEnum.TradingAnalysis) {
			IpssScenarioHelper helper = new IpssScenarioHelper(this.odmParser);
			PTradingAnalysisXmlType pt = helper.getPTradingAnalysis();			
			this.casenameComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] {pt.getName()}));
			this.descTextArea.setText(pt.getDesc());
			// set the case data to the actual data editing panel
			_tradingCaseInfoPanel.setXmlCaseData(pt);
			// set the case data to the actual data editing panel
			_tradingCaseInfoPanel.setForm2Editor();
		}
		
		else if (_caseType == SimuRunEnum.Aclf) {
			String casename = getCaseName(SimuRunEnum.Aclf);
			AclfStudyCaseXmlType scase = this.studyCaseXmlDoc.getAclfStudyCase(casename);
			this.descTextArea.setText(scase.getRecDesc());
			// set the case data to the actual data editing panel
			_aclfCaseInfoPanel.setXmlCaseData(scase.getAclfAlgorithm(), this.studyCaseXmlDoc.getGridOption());
			// set the case data to the actual data editing panel
			_aclfCaseInfoPanel.setForm2Editor();
		}
		else if (_caseType == SimuRunEnum.SenAnalysis) {
			String casename = getCaseName(SimuRunEnum.SenAnalysis);
			DclfStudyCaseXmlType scase = this.studyCaseXmlDoc.getDclfStudyCase(casename);
			this.descTextArea.setText(scase.getRecDesc());
			// set the case data to the actual data editing panel
			_dclfCaseInfoPanel.setXmlCaseData(scase);
			// set the case data to the actual data editing panel
			_dclfCaseInfoPanel.setForm2Editor();
		}
		else if (_caseType == SimuRunEnum.Acsc) {
			String casename = getCaseName(SimuRunEnum.Acsc);
			AcscStudyCaseXmlType scase = this.studyCaseXmlDoc.getAcscStudyCase(casename);
			this.descTextArea.setText(scase.getRecDesc());
			// set the case data to the actual data editing panel
			_acscCaseInfoPanel.setXmlCaseDatax(scase, false);
			// set the case data to the actual data editing panel
			_acscCaseInfoPanel.setForm2Editor();
		}
		else if (_caseType == SimuRunEnum.DStab) {
			String casename = getCaseName(SimuRunEnum.DStab);
			DStabStudyCaseXmlType scase = this.studyCaseXmlDoc.getDStabStudyCase(casename);
			this.descTextArea.setText(scase.getRecDesc());
			_dstabCaseInfoPanel.setXmlCaseData(scase, this.studyCaseXmlDoc.getGridOption());
			_dstabCaseInfoPanel.setForm2Editor();
		}
		else if (_caseType == SimuRunEnum.Scripts) {
			// build the case info combo list
			this.casenameComboBox.setModel(new javax.swing.DefaultComboBoxModel(_appSimuCtx.getCasenameArray(_caseType)));
			IpssLogger.getLogger().info("Casename Array size: " + _appSimuCtx.getCasenameArray(_caseType).length);
			
			ProjData projData = (ProjData)_appSimuCtx.getProjData();
			String casename = projData.getScriptsCaseName();
			if (casename != null)
				this.casenameComboBox.setSelectedItem(casename);
			else
				casename = (String)this.casenameComboBox.getSelectedItem();
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
        return true;
	}

	private String getCaseName(SimuRunEnum caseType) {
		ProjData projData = (ProjData)_appSimuCtx.getProjData();
		String casename = "";
		if (caseType == SimuRunEnum.Aclf) {
			this.casenameComboBox.setModel(new javax.swing.DefaultComboBoxModel(this.studyCaseXmlDoc.getAclfStudyCaseNameArray()));
			casename = selectCase(projData.getAclfCaseName());
			projData.setAclfCaseName(casename);
		}
		else if (caseType == SimuRunEnum.SenAnalysis) {
			this.casenameComboBox.setModel(new javax.swing.DefaultComboBoxModel(this.studyCaseXmlDoc.getDclfStudyCaseNameArray()));
			casename = selectCase(projData.getDclfCaseName());
			projData.setDclfCaseName(casename);
		}
		else if (caseType == SimuRunEnum.TradingAnalysis) {
			this.casenameComboBox.setModel(new javax.swing.DefaultComboBoxModel(this.studyCaseXmlDoc.getTradingStudyCaseNameArray()));
			casename = selectCase(projData.getPTradingCaseName());
			projData.setPTradingCaseName(casename);
		}
		else if (caseType == SimuRunEnum.Acsc) {
			this.casenameComboBox.setModel(new javax.swing.DefaultComboBoxModel(this.studyCaseXmlDoc.getAcscStudyCaseNameArray()));
			casename = selectCase(projData.getAcscCaseName());
			projData.setAcscCaseName(casename);
		}
		else if (caseType == SimuRunEnum.DStab) {
			this.casenameComboBox.setModel(new javax.swing.DefaultComboBoxModel(this.studyCaseXmlDoc.getDStabStudyCaseNameArray()));
			casename = selectCase(projData.getDStabCaseName());
			projData.setDStabCaseName(casename);
		}
		IpssLogger.getLogger().info("Selected casename: " + casename);
		return casename;
	}

	private String selectCase(String casename) {
		this.casenameComboBox.setSelectedItem(casename);
		if (this.casenameComboBox.getSelectedIndex() == -1)
			this.casenameComboBox.setSelectedIndex(0);
		casename = (String)this.casenameComboBox.getSelectedItem();
		return casename;
	}
	
	/**
	*	Save editor screen data to the form
	*
	* @param errMsg error messages during the saving process.
	* @return false if there is any problem
	*/
    public boolean saveEditor2Form(Vector<String> errMsg) throws Exception {
		IpssLogger.getLogger().info("NBCaseInfoDialog saveEditor2Form() called");

		if (SwingInputVerifyUtil.isEmptyStr(this.casenameComboBox)) {
			errMsg.add("Casename is empty");
			return false;
		}
		
		String casename = (String)this.casenameComboBox.getSelectedItem();
		//_caseData.setCaseName(casename);
		//_caseData.setDescription(this.descTextArea.getText());
		
		if (_caseType == SimuRunEnum.TradingAnalysis) {
			IpssScenarioHelper helper = new IpssScenarioHelper(this.odmParser);
			PTradingAnalysisXmlType pt = helper.getPTradingAnalysis();			
			pt.setName(this.casenameComboBox.getSelectedItem().toString());
			pt.setDesc(this.descTextArea.getText());
			_tradingCaseInfoPanel.saveEditor2Form(errMsg);			
			// save run case xml doc
			FileUtil.writeText2File(runStudyCaseFilename, 
					this.odmParser.toXmlDoc(false));
		}
		else {
			ProjData projData = (ProjData)_appSimuCtx.getProjData();
			if (_caseType == SimuRunEnum.Aclf) {
				projData.setAclfCaseName(casename);
				if (_aclfCaseInfoPanel.getPanelSelected() == NBAclfCasePanel.ContingencyPanel) {
					this._caseType = SimuRunEnum.ContingencyAnalysis;
					IpssLogger.getLogger().info("Save Contingency anlaysis Case info");
					ContingencyAnalysisXmlType analysis = this.studyCaseXmlDoc.getContingencyAnalysis();
					_aclfCaseInfoPanel.setXmlCaseData(analysis, this.studyCaseXmlDoc.getGridOption());
					_aclfCaseInfoPanel.saveEditor2Form(errMsg);
					EditorSimuSpringCtx.getAclfRunForm().setXmlCaseData(analysis, this.studyCaseXmlDoc.getGridOption());
				}
				else {
					AclfStudyCaseXmlType scase = this.studyCaseXmlDoc.getAclfStudyCase(casename);
					if (scase == null) {
						errMsg.add("Aclf study case not found, " + casename);
						return false;
					}
					scase.setRecDesc(this.descTextArea.getText());
					_aclfCaseInfoPanel.setXmlCaseData(scase.getAclfAlgorithm(), this.studyCaseXmlDoc.getGridOption());
					_aclfCaseInfoPanel.saveEditor2Form(errMsg);
					EditorSimuSpringCtx.getAclfRunForm().setXmlCaseData(scase, this.studyCaseXmlDoc.getGridOption());
				}
			}
			else if (_caseType == SimuRunEnum.SenAnalysis) {
				DclfStudyCaseXmlType scase = this.studyCaseXmlDoc.getDclfStudyCase(casename);
				if (scase == null) {
					errMsg.add("Dclf study case not found, " + casename);
					return false;
				}
				scase.setRecDesc(this.descTextArea.getText());
				projData.setDclfCaseName(casename);
				_dclfCaseInfoPanel.setXmlCaseData(scase);
				_dclfCaseInfoPanel.saveEditor2Form(errMsg);
				EditorSimuSpringCtx.getDclfRunForm().setXmlCaseData(scase);
			}
			else if (_caseType == SimuRunEnum.Acsc) {
				AcscStudyCaseXmlType scase = this.studyCaseXmlDoc.getAcscStudyCase(casename);
				if (scase == null) {
					errMsg.add("Acsc study case not found, " + casename);
					return false;
				}
				scase.setRecDesc(this.descTextArea.getText());
				projData.setAcscCaseName(casename);
				_acscCaseInfoPanel.setXmlCaseDatax(scase, true);
				_acscCaseInfoPanel.saveEditor2Form(errMsg);
				EditorSimuSpringCtx.getAcscRunForm().setXmlCaseData(scase);
			}
			else if (_caseType == SimuRunEnum.DStab) {
				DStabStudyCaseXmlType scase = this.studyCaseXmlDoc.getDStabStudyCase(casename);
				if (scase == null) {
					errMsg.add("DStab study case not found, " + casename);
					return false;
				}
				scase.setRecDesc(this.descTextArea.getText());
				projData.setDStabCaseName(casename);
				_dstabCaseInfoPanel.setXmlCaseData(scase, this.studyCaseXmlDoc.getGridOption());
				_dstabCaseInfoPanel.saveEditor2Form(errMsg);
				//System.out.println(scase.toString());
				EditorSimuSpringCtx.getDStabRunForm().setXmlCaseData(scase, this.studyCaseXmlDoc.getGridOption());
			}
			else if (_caseType == SimuRunEnum.Scripts) {
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
        casenameComboBox = new javax.swing.JComboBox();
        descLabel = new javax.swing.JLabel();
        scrollPane = new javax.swing.JScrollPane();
        descTextArea = new javax.swing.JTextArea();
        caseDataPanel = new javax.swing.JPanel();
        controlPanel = new javax.swing.JPanel();
        runButton = new javax.swing.JButton();
        addCaseButton = new javax.swing.JButton();
        deleteCaseButton = new javax.swing.JButton();
        viewXmlButton = new javax.swing.JButton();
        importButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setModal(true);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        caseInfoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Study Case", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        caseInfoPanel.setLayout(new java.awt.GridBagLayout());

        casenameLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        casenameLabel.setText("Casename");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 10);
        caseInfoPanel.add(casenameLabel, gridBagConstraints);

        casenameComboBox.setEditable(true);
        casenameComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        casenameComboBox.setName("casenameComboBox"); // NOI18N
        casenameComboBox.setPreferredSize(new java.awt.Dimension(250, 21));
        casenameComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                casenameComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 10);
        caseInfoPanel.add(casenameComboBox, gridBagConstraints);

        descLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        descLabel.setText("Description");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 10);
        caseInfoPanel.add(descLabel, gridBagConstraints);

        scrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        descTextArea.setColumns(30);
        descTextArea.setFont(new java.awt.Font("Dialog", 0, 12));
        descTextArea.setRows(2);
        descTextArea.setName("descTextArea"); // NOI18N
        scrollPane.setViewportView(descTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 10);
        caseInfoPanel.add(scrollPane, gridBagConstraints);

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

        addCaseButton.setFont(new java.awt.Font("Dialog", 0, 12));
        addCaseButton.setText("AddCase");
        addCaseButton.setToolTipText("Add a new study case");
        addCaseButton.setName("addCaseButton"); // NOI18N
        addCaseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addCaseButtonActionPerformed(evt);
            }
        });
        controlPanel.add(addCaseButton);

        deleteCaseButton.setFont(new java.awt.Font("Dialog", 0, 12));
        deleteCaseButton.setText("DeleteCase");
        deleteCaseButton.setToolTipText("Delete the current study case");
        deleteCaseButton.setName("deleteCaseButton"); // NOI18N
        deleteCaseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteCaseButtonActionPerformed(evt);
            }
        });
        controlPanel.add(deleteCaseButton);

        viewXmlButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        viewXmlButton.setText("ViewXml");
        viewXmlButton.setToolTipText("Cancel and close the case dialog");
        viewXmlButton.setName("cancelButton"); // NOI18N
        viewXmlButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewXmlButtonActionPerformed(evt);
            }
        });
        controlPanel.add(viewXmlButton);

        importButton.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
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

    private void deleteCaseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteCaseButtonActionPerformed
		if (this.casenameComboBox.getItemCount() > 1) {
			String casename = (String)this.casenameComboBox.getSelectedItem();
			if (_caseType == SimuRunEnum.Aclf && this.studyCaseXmlDoc.getAclfStudyCase(casename) != null) {
				if (this.studyCaseXmlDoc.deleteAclfStudyCase(casename))
					IpssLogger.getLogger().info("NBCaseInfoPanel study case deleted " + casename);
				else {	
					IpssLogger.getLogger().info("NBCaseInfoPanel delte study case, case not found " + casename);
					return;  // no case deleted if return false
				}
				this.casenameComboBox.setModel(new javax.swing.DefaultComboBoxModel(this.studyCaseXmlDoc.getAclfStudyCaseNameArray()));
			}
			else if (_caseType == SimuRunEnum.SenAnalysis && this.studyCaseXmlDoc.getDclfStudyCase(casename) != null) {
				if (this.studyCaseXmlDoc.deleteDclfStudyCase(casename))
					IpssLogger.getLogger().info("NBCaseInfoPanel study case deleted " + casename);
				else {	
					IpssLogger.getLogger().info("NBCaseInfoPanel delte study case, case not found " + casename);
					return;  // no case deleted if return false
				}
				this.casenameComboBox.setModel(new javax.swing.DefaultComboBoxModel(this.studyCaseXmlDoc.getDclfStudyCaseNameArray()));
			}
			else if (_caseType == SimuRunEnum.Acsc && this.studyCaseXmlDoc.getAcscStudyCase(casename) != null) {
				if (this.studyCaseXmlDoc.deleteAcscStudyCase(casename))
					IpssLogger.getLogger().info("NBCaseInfoPanel study case deleted " + casename);
				else {	
					IpssLogger.getLogger().info("NBCaseInfoPanel delte study case, case not found " + casename);
					return;  // no case deleted if return false
				}
				this.casenameComboBox.setModel(new javax.swing.DefaultComboBoxModel(this.studyCaseXmlDoc.getAcscStudyCaseNameArray()));
			}
			else if (_caseType == SimuRunEnum.DStab && this.studyCaseXmlDoc.getDStabStudyCase(casename) != null) {
				if (this.studyCaseXmlDoc.deleteDStabStudyCase(casename))
					IpssLogger.getLogger().info("NBCaseInfoPanel study case deleted " + casename);
				else {	
					IpssLogger.getLogger().info("NBCaseInfoPanel delte study case, case not found " + casename);
					return;  // no case deleted if return false
				}
				this.casenameComboBox.setModel(new javax.swing.DefaultComboBoxModel(this.studyCaseXmlDoc.getDStabStudyCaseNameArray()));
			}
			this.casenameComboBox.setSelectedIndex(0);
		    casenameComboBoxActionPerformed(null);   // to refresh the edit screen
		}
		else {
			BasePluginSpringFactory.getEditorDialogUtil().showMsgDialog("Warnning", "The project has to have minimum one case.");
		}
    }//GEN-LAST:event_deleteCaseButtonActionPerformed

    private void addCaseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addCaseButtonActionPerformed
		IpssLogger.getLogger().info("NBCaseInfoPanel addCaseButtonActionPerformed() called");
		String casename = (String)this.casenameComboBox.getSelectedItem();	 
		if (casename == null || casename.trim() == "")
			casename = "A New " + _caseType + " Study Case";
		if (_caseType == SimuRunEnum.Aclf && this.studyCaseXmlDoc.getAclfStudyCase(casename) == null) {
			this.studyCaseXmlDoc.addNewAclfStudyCase(casename);
		}
		else if (_caseType == SimuRunEnum.SenAnalysis && this.studyCaseXmlDoc.getDclfStudyCase(casename) == null) {
			this.studyCaseXmlDoc.addNewDclfStudyCase(casename);
		}
		else if (_caseType == SimuRunEnum.Acsc && this.studyCaseXmlDoc.getAcscStudyCase(casename) == null) {
			this.studyCaseXmlDoc.addNewAcscStudyCase(casename);
		}
		else if (_caseType == SimuRunEnum.DStab && this.studyCaseXmlDoc.getDStabStudyCase(casename) == null) {
			this.studyCaseXmlDoc.addNewDStabStudyCase(casename);
		}
	    casenameComboBoxActionPerformed(null);   // to refresh the edit screen
    }//GEN-LAST:event_addCaseButtonActionPerformed

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
		Vector<String> errMsg = new Vector<String>();
		try {
        	if (!saveEditor2Form(errMsg)) {
        		BasePluginSpringFactory.getEditorDialogUtil().showMsgDialog(this, "Input Data Error", errMsg);
				return;
        	}
        } catch (Exception e) {
        	IpssLogger.logErr(e);
        	BasePluginSpringFactory.getEditorDialogUtil().showMsgDialog(this, "Input Data Error", e.toString());
			return;
        }	
        
        if (this.runButton.getText().equals("Save")) {
    		_returnOK = false;
        }
        else {
    		_returnOK = true;
    		_appSimuCtx.getProjData().setDirty(true);
    		if (_caseType == SimuRunEnum.Aclf || _caseType == SimuRunEnum.ContingencyAnalysis)
    			((SimuContext)_appSimuCtx.getSimuCtx()).getMsgHub().removeMsgListener(_aclfCaseInfoPanel);
    		GraphSpringAppContext.getIpssGraphicEditor().refreshCurrentDocumentEditorPanel();        
        }
        setVisible(false);
        dispose();
    }//GEN-LAST:event_runButtonActionPerformed

    private void casenameComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_casenameComboBoxActionPerformed
    	IpssLogger.getLogger().info("NBCaseInfoPanel casenameComboBoxActionPerformed called");
		// at least two ways can trigger this action: 1) Case List section 2) Change Case name
		String casename = (String)this.casenameComboBox.getSelectedItem();
		// make sure it is case selection event. if it is change case name event, the case won't be
		// in the projData.caseList
		ProjData projData = (ProjData)_appSimuCtx.getProjData();
		if (_caseType == SimuRunEnum.Aclf) {
			if (this.studyCaseXmlDoc.getAclfStudyCase(casename) != null) {
				projData.setAclfCaseName(casename);
				setForm2Editor();
			}
		}
		else if (_caseType == SimuRunEnum.SenAnalysis) { 
			if (this.studyCaseXmlDoc.getDclfStudyCase(casename) != null) {
				projData.setDclfCaseName(casename);
				setForm2Editor();
			}
		}
		else if (_caseType == SimuRunEnum.Acsc) { 
			if (this.studyCaseXmlDoc.getAcscStudyCase(casename) != null) {
				projData.setAcscCaseName(casename);
				setForm2Editor();
			}
		}
		else if (_caseType == SimuRunEnum.DStab) { 
			if (this.studyCaseXmlDoc.getDStabStudyCase(casename) != null) {
				projData.setDStabCaseName(casename);
				setForm2Editor();
			}
		}
		else if (_caseType == SimuRunEnum.Scripts) { 
		}
    }//GEN-LAST:event_casenameComboBoxActionPerformed

private void viewXmlButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewXmlButtonActionPerformed
	this.setAlwaysOnTop(false);
	Vector<String> errMsg = new Vector<String>();	
	try {
    	if (!saveEditor2Form(errMsg)) {
    		BasePluginSpringFactory.getEditorDialogUtil().showMsgDialog(this, "Input Data Error", errMsg);
			return;
    	}
    } catch (Exception e) {
    	IpssLogger.logErr(e);
    	BasePluginSpringFactory.getEditorDialogUtil().showMsgDialog(this, "Input Data Error", e.toString());
		return;
    }	
    
	IOutputTextDialog dialog = UISpringAppContext.getOutputTextDialog("Run Study Case Xml");
	dialog.disableFeature("busStyleRadioButton");
	dialog.disableFeature("summaryRadioButton");
	 if (_caseType == SimuRunEnum.TradingAnalysis) {
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
    private javax.swing.JButton addCaseButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel caseDataPanel;
    private javax.swing.JPanel caseInfoPanel;
    private javax.swing.JComboBox casenameComboBox;
    private javax.swing.JLabel casenameLabel;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JButton deleteCaseButton;
    private javax.swing.JLabel descLabel;
    private javax.swing.JTextArea descTextArea;
    private javax.swing.JButton importButton;
    private javax.swing.JButton runButton;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JButton viewXmlButton;
    // End of variables declaration//GEN-END:variables

    class DataVerifier extends javax.swing.InputVerifier {
    	public boolean verify(javax.swing.JComponent input) {
			if (input == null)
				return false;
       		try {
				if (input == casenameComboBox )
					return !SwingInputVerifyUtil.isEmptyStr((javax.swing.JComboBox)input);
 	       	} catch (Exception e) {
 	    		return false;
 	       	}		
			return true;
        }
    }
}
