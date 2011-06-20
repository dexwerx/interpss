package org.interpss.editor.project;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.interpss.custom.IpssFileAdapter;
import org.interpss.editor.coreframework.GPGraphpad;
import org.interpss.editor.doc.IpssProject;
import org.interpss.editor.resources.Translator;
import org.interpss.editor.util.NamedInputStream;
import org.interpss.editor.util.Utilities;
import org.interpss.spring.PluginSpringCtx;

public class IpssNewCustomDialog extends javax.swing.JDialog {
	private String docName;
	private String filepath;
	private String srcFileName;
	
	private boolean dstabFile = false;
	public boolean hasDstabFile() { return this.dstabFile; }
	private String dstabFilepath;
	private String dstabSrcFileName;

	public IpssNewCustomDialog(GPGraphpad graphpad, String title) {
		// super(graphpad.getFrame(), title, true);
		super(graphpad.getFrame(), "New Project", true);

		this.graphpad = graphpad;
		this.parentFrame = graphpad.getFrame();

		setResizable(false);

		initGuiComponents();
		initListeners();

		setBrowsePanelVisable();
		updateState();
	}
	
	private void enableDStab(boolean b) {
		this.dstabFile = b;
		dstabDirLabel.setEnabled(b);
		dstabDirTextField.setEnabled(b);
		dstabBrowseButton.setEnabled(b);
	}

	protected void initListeners() {
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				setCancelExit(true);
				setVisible(false);
			}
		});

		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				setCancelExit(false);
				setVisible(false);
			}
		});

		browseButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				selectFile(false);
			}
		});

		dstabBrowseButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				selectFile(true);
			}
		});

		nameTextField.addCaretListener(new CaretListener() {
			public void caretUpdate(final CaretEvent e) {
				updateState();
			}
		});

		adapterComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				setVersionComboBoxData();
				Object adapterName = adapterComboBox.getSelectedItem();
				enableDStab(adapterName.toString().equals("BPA Format DStab"));
			}
		});
	}

	public void selectFile(boolean dstabFile) {
		try {
			String fileExtension = adapter.getFileFilterString();
			String extensionDescription = adapter.getDescription();
			
			NamedInputStream in = org.interpss.editor.util.Utilities.provideInput(fileExtension,extensionDescription);
			
			if (in != null) {
				if (dstabFile) {
					this.setDstabSrcFileName(in.getName());
					dstabDirTextField.setText(in.getName());
					setDstabOKActable();
					if (this.getDstabSrcFileName() == null) {
						showError("Can't open the selected custom data file.");
						return;
					}
				}
				else {
					this.setSrcFileName(in.getName());
					dirTextField.setText(in.getName());
					setOKActable();
					if (this.getSrcFileName() == null) {
						showError("Can't open the selected custom data file.");
						return;
					}
				}
			}
		} catch (Exception ex) {
			showError(ex.toString());
			ex.printStackTrace();
		}
	}

	protected void setBrowsePanelVisable() {
	}


	private IpssFileAdapter getCustomFileAdapter() {
		Object adapterName = adapterComboBox.getSelectedItem();
		if (adapterName == null) {
			return null;
		}

		return PluginSpringCtx.getCustomFileAdapterByName(adapterName.toString());
	}

	private void setVersionComboBoxData() {
		adapter = getCustomFileAdapter();

		if (adapter == null) {
			setVersionEnabled(false);
			return;
		}

		String[] versionList = adapter.getVersionList();

		if (versionList != null){
			versionComboBox.setModel(new DefaultComboBoxModel(versionList));
//			adapter.setVersionSelected((versionComboBox.getSelectedItem()).toString());
		}
		else {
			setVersionEnabled(false);
		}
	}

	private void setVersionEnabled(boolean b) {
		if (b)
			versionComboBox.setEnabled(false);
		else {
			versionComboBox.removeAllItems();
			versionComboBox.setEnabled(false);
		}
	}

	public void setDocName(String docName) {
		this.docName = docName;

		if (docName != null) {
			nameTextField.setText(Utilities.getFileName(docName));
		}
	}

	public String getDocName() {
		return docName;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setSrcFileName(String srcFileName) {
		this.srcFileName = srcFileName;
	}

	public String getSrcFileName() {
		return srcFileName;
	}

	public void setDstabFilepath(String filepath) {
		this.dstabFilepath = filepath;
	}

	public String getDstabFilepath() {
		return dstabFilepath;
	}

	public void setDstabSrcFileName(String fileName) {
		this.dstabSrcFileName = fileName;
	}

	public String getDstabSrcFileName() {
		return dstabSrcFileName;
	}

	public void setCancelExit(boolean isCancelExit) {
		this.isCancelExit = isCancelExit;
	}

	public boolean isCancelExit() {
		return isCancelExit;
	}

	public void init(IpssProject project, String aDocName) {
		setFilepath(project.getProjectPath());
		setDocName(aDocName);
		// pack the form and display
		pack();
		setLocationRelativeTo(parentFrame);
		setSize(new Dimension(611, 254));
		setVisible(true);
	}

	public void showMessage(String messageInfo, boolean isError) {
		messagePanel.showMessage(messageInfo, isError);

		if (isError) {
			okButton.setEnabled(false);
			return;
		} else
			setOKActable();
	}

	private void setDstabOKActable() {
		if ((dstabDirTextField.getText() != null)
				&& (dstabDirTextField.getText().length() > 0)) {
			okButton.setEnabled(true);
		} else {
			okButton.setEnabled(false);
		}
	}

	private void setOKActable() {
		if ((dirTextField.getText() != null)
				&& (dirTextField.getText().length() > 0)) {
			okButton.setEnabled(true);
		} else {
			okButton.setEnabled(false);
		}
	}

	public void showMessage(String messageInfo) {
		showMessage(messageInfo, false);
	}

	public void showError(String messageInfo) {
		showMessage(messageInfo, true);
	}

	public String getFileName() {
		return Utilities.getFilePathName(getFilepath(), nameTextField.getText()
				+ "." + adapter.getExtension());
	}

	public String getDstabFileName() {
		return Utilities.getFilePathName(getFilepath(), nameTextField.getText()
				+ "." + adapter.getExtensionDstab());
	}

	public void updateState() {
		if (nameTextField.getText().equals("")) {
			showError(Translator.getString("CustomDialog.Name.Error"));
			return;
		}
		String filePathName = getFileName();
		try {
			java.io.File myFile = new java.io.File(filePathName);
			if (myFile.exists()) {

				showError(Translator.getString("CustomDialog.File.Exists"));
				return;
			}
			// else myFilePath.mkdir();
		} catch (Exception e) {
			showError("Create custom data file error:" + e.getMessage());
			return;
		}

		showMessage(Translator.getString("CustomDialog.OK"));
		// showMessage(filePathName);
	}

	public String getVersion() {
		return 	(String)versionComboBox.getSelectedItem();
	}

	private JComboBox versionComboBox;

	private JComboBox adapterComboBox;

	private JPanel selectpanel;

	private JLabel nameLabel;

	private JPanel mainPanel;

	private JPanel buttonPanel;

	private IpssMessagePanel messagePanel;

	private JButton cancelButton;

	private JButton okButton;

	private JPanel browsePanel;
	private JLabel dirLabel;
	private JButton browseButton;
	private JTextField dirTextField;

	private JLabel dstabDirLabel;
	private JButton dstabBrowseButton;
	private JTextField dstabDirTextField;

	private JTextField nameTextField;

	protected GPGraphpad graphpad;

	private Frame parentFrame;

	private boolean isCancelExit;
	
	private IpssFileAdapter adapter;

	protected void initGuiComponents() {

		getContentPane().setLayout(
				new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		messagePanel = new IpssMessagePanel();
		getContentPane().add(messagePanel);
		messagePanel.setPreferredSize(new Dimension(0, 30));

		messagePanel.setBorder(new EmptyBorder(0, 5, 0, 0));
		// messagePanel.getMessageLabel().setPreferredSize(new Dimension(0,
		// 30));

		getContentPane().add(Box.createVerticalStrut(10));

		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBorder(new EmptyBorder(0, 5, 0, 5));
		getContentPane().add(mainPanel);

		selectpanel = new JPanel();
		mainPanel.add(selectpanel);
		final GridLayout gridLayout = new GridLayout(0, 1);
		selectpanel.setLayout(gridLayout);
		selectpanel.setBorder(new TitledBorder(null, "Import Contexts",
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));

		browsePanel = new JPanel();
		browsePanel.setLayout(new BoxLayout(browsePanel, BoxLayout.Y_AXIS));
		selectpanel.add(browsePanel);

		final JPanel panel_adapterType = new JPanel();
		browsePanel.add(panel_adapterType);
		panel_adapterType.setLayout(new BorderLayout());

		final JLabel adapterLabel = new JLabel();
		adapterLabel.setDisplayedMnemonic(KeyEvent.VK_A);
		adapterLabel.setText("Adapter:");
		panel_adapterType.add(adapterLabel, BorderLayout.WEST);

		adapterComboBox = new JComboBox();
		panel_adapterType.add(adapterComboBox, BorderLayout.CENTER);

		browsePanel.add(Box.createVerticalStrut(10));

		final JPanel panel_version = new JPanel();
		panel_version.setLayout(new BorderLayout());
		browsePanel.add(panel_version);

		final JLabel versionLabel = new JLabel();
		versionLabel.setDisplayedMnemonic(KeyEvent.VK_V);
		versionLabel.setText("Version:");
		panel_version.add(versionLabel, BorderLayout.WEST);

		versionComboBox = new JComboBox();
		panel_version.add(versionComboBox, BorderLayout.CENTER);

		browsePanel.add(Box.createVerticalStrut(10));

		/*
		 * select file
		 */
		final JPanel panel_selectFile = new JPanel();
		browsePanel.add(panel_selectFile);
		panel_selectFile.setLayout(new BorderLayout());

		dirLabel = new JLabel();
		panel_selectFile.add(dirLabel, BorderLayout.WEST);
		dirLabel.setName("dirLabel");
		dirLabel.setDisplayedMnemonic(KeyEvent.VK_F);
		dirLabel.setText("File:  ");

		dirTextField = new JTextField();
		panel_selectFile.add(dirTextField);
		dirTextField.setName("dirTextField");
		dirTextField.setEditable(false);
		dirTextField.setFocusAccelerator('f');

		browseButton = new JButton();
		panel_selectFile.add(browseButton, BorderLayout.EAST);
		browseButton.setName("browseButton");
		browseButton.setMnemonic('B');
		browseButton.setText("Browse...");

		/*
		 * select Dstab file
		 */
		final JPanel panel_selectDStabFile = new JPanel();
		browsePanel.add(panel_selectDStabFile);
		panel_selectDStabFile.setLayout(new BorderLayout());

		dstabDirLabel = new JLabel();
		panel_selectDStabFile.add(dstabDirLabel, BorderLayout.WEST);
		dstabDirLabel.setName("dstabDirLabel");
		dstabDirLabel.setDisplayedMnemonic(KeyEvent.VK_F);
		dstabDirLabel.setText("Stability File:  ");

		dstabDirTextField = new JTextField();
		panel_selectDStabFile.add(dstabDirTextField);
		dstabDirTextField.setName("dstabDirTextField");
		dstabDirTextField.setEditable(false);
		dstabDirTextField.setFocusAccelerator('f');

		dstabBrowseButton = new JButton();
		panel_selectDStabFile.add(dstabBrowseButton, BorderLayout.EAST);
		dstabBrowseButton.setName("dstabBrowseButton");
		dstabBrowseButton.setMnemonic('D');
		dstabBrowseButton.setText("Browse...");

		enableDStab(false);
		
		final JPanel panel = new JPanel();
		mainPanel.add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout());

		nameLabel = new JLabel();
		panel.add(nameLabel, BorderLayout.WEST);
		nameLabel.setDisplayedMnemonic(KeyEvent.VK_P);
		nameLabel.setText("Custom Project Name:");

		nameTextField = new JTextField();
		panel.add(nameTextField, BorderLayout.CENTER);

		nameTextField.setFocusAccelerator('p');

		buttonPanel = new JPanel();

		getContentPane().add(Box.createVerticalStrut(10));
		getContentPane().add(buttonPanel);

		okButton = new JButton();

		okButton.setMnemonic('O');
		buttonPanel.add(okButton);
		okButton.setText("OK");

		cancelButton = new JButton();

		buttonPanel.add(cancelButton);
		cancelButton.setMnemonic('C');
		cancelButton.setText("Cancel");

		adapterComboBox.setModel(new DefaultComboBoxModel(
				PluginSpringCtx.getCustomFileAdapterNameList()));
		setVersionComboBoxData();

	}
}
