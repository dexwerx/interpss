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
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.interpss.editor.coreframework.GPGraphpad;
import org.interpss.editor.coreframework.IpssReportFile;
import org.interpss.editor.doc.IpssProject;
import org.interpss.editor.resources.Translator;
import org.interpss.editor.util.NamedInputStream;
import org.interpss.editor.util.Utilities;


public class IpssNewReportDialog extends javax.swing.JDialog {
	private JLabel dirLabel;

	private JPanel browsePanel;

	private JPanel selectpanel;

	private JLabel nameLabel;

	private JPanel mainPanel;

	private JPanel buttonPanel;

	private IpssMessagePanel messagePanel;

	private JButton cancelButton;

	private JButton okButton;

	private JButton browseButton;

	private JRadioButton fromRadioButton;

	private JRadioButton newRadioButton;

	private JTextField dirTextField;

	private JTextField nameTextField;

	private javax.swing.ButtonGroup selectButtonGroup;

	private String docName;

	private String filepath;

	private String srcFileName;

	protected GPGraphpad graphpad;

	private Frame parentFrame;

	private IpssReportFile file;

	private boolean isCancelExit;

	public IpssNewReportDialog(GPGraphpad graphpad, String title) {
		// super(graphpad.getFrame(), title, true);
		super(graphpad.getFrame(), Translator.getString("ReportDialog.Title"), true);

		this.graphpad = graphpad;
		this.parentFrame = graphpad.getFrame();

		setResizable(false);

		// initLocalData();
		initGuiComponents();
		initListeners();
		// setFontValue(initialFont);
		setBrowsePanelVisable();
		updateState();

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

		newRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				setBrowsePanelVisable();
			}
		});

		fromRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {

				setBrowsePanelVisable();
			}
		});

		browseButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				selectFile();
			}
		});

		nameTextField.addCaretListener(new CaretListener() {
			public void caretUpdate(final CaretEvent e) {
				updateState();
			}
		});

	}

	protected void setBrowsePanelVisable() {
		if (selectButtonGroup.getSelection() == fromRadioButton.getModel()) {
			dirLabel.setEnabled(true);
			dirTextField.setEnabled(true);
			browseButton.setEnabled(true);
		} else {
			dirLabel.setEnabled(false);
			dirTextField.setEnabled(false);
			browseButton.setEnabled(false);
		}
	}

	protected void initGuiComponents() {

		selectButtonGroup = new javax.swing.ButtonGroup();

		getContentPane().setLayout(
				new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		messagePanel = new IpssMessagePanel();
		getContentPane().add(messagePanel);
		messagePanel.setPreferredSize(new Dimension(0, 30));

		messagePanel.setBorder(new EmptyBorder(0, 5, 0, 0));
		messagePanel.getMessageLabel().setPreferredSize(new Dimension(0, 30));

		getContentPane().add(Box.createVerticalStrut(10));

		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBorder(new EmptyBorder(0, 5, 0, 5));
		getContentPane().add(mainPanel);

		mainPanel.add(Box.createVerticalStrut(10));

		selectpanel = new JPanel();
		mainPanel.add(selectpanel);
		final GridLayout gridLayout = new GridLayout(0, 1);
		selectpanel.setLayout(gridLayout);
		selectpanel.setBorder(new TitledBorder(null, Translator.getString("ReportDialog.Contents"),
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));

		newRadioButton = new JRadioButton();
		selectpanel.add(newRadioButton);
		newRadioButton.setSelected(true);
		newRadioButton.setMnemonic('N');
		newRadioButton.setText(Translator.getString("ReportDialog.CreateANewReport"));
		selectButtonGroup.add(newRadioButton);

		fromRadioButton = new JRadioButton();
		selectpanel.add(fromRadioButton);
		fromRadioButton.setMnemonic('I');
		fromRadioButton.setText(Translator.getString("ReportDialog.ImportAReportFromExistingSource"));

		browsePanel = new JPanel();
		selectpanel.add(browsePanel);
		browsePanel.setLayout(new BorderLayout());

		dirTextField = new JTextField();
		dirTextField.setEditable(false);
		dirTextField.setFocusAccelerator('f');
		browsePanel.add(dirTextField);

		dirLabel = new JLabel();
		dirLabel.setDisplayedMnemonic(KeyEvent.VK_F);
		browsePanel.add(dirLabel, BorderLayout.WEST);
		dirLabel.setText(Translator.getString("File")+":  ");

		browseButton = new JButton();

		browseButton.setMnemonic(Translator.getString("Browse.Mnemonic").toCharArray()[0]);
		browseButton.setText(Translator.getString("Browse"));
		browsePanel.add(browseButton, BorderLayout.EAST);
		selectButtonGroup.add(fromRadioButton);

		final JPanel panel = new JPanel();
		mainPanel.add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout());

		nameLabel = new JLabel();
		panel.add(nameLabel, BorderLayout.WEST);
		nameLabel.setDisplayedMnemonic(KeyEvent.VK_N);
		nameLabel.setText(Translator.getString("ReportDialog.ReportName"));

		nameTextField = new JTextField();
		panel.add(nameTextField, BorderLayout.CENTER);

		nameTextField.setFocusAccelerator('\r');

		buttonPanel = new JPanel();
		getContentPane().add(buttonPanel);

		okButton = new JButton();

		okButton.setMnemonic(Translator.getString("OK.Mnemonic").toCharArray()[0]);
		buttonPanel.add(okButton);
		okButton.setText(Translator.getString("OK"));

		cancelButton = new JButton();

		buttonPanel.add(cancelButton);
		cancelButton.setMnemonic(Translator.getString("Cancel.Mnemonic").toCharArray()[0]);
		cancelButton.setText(Translator.getString("Cancel"));

		// selectpanel.setVisible(false);
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

	public void setFile(IpssReportFile file) {
		this.file = file;
	}

	public IpssReportFile getFile() {
		return file;
	}

	public void setSrcFileName(String srcFileName) {
		this.srcFileName = srcFileName;
	}

	public String getSrcFileName() {
		return srcFileName;
	}

	public boolean isNewFile() {
		return selectButtonGroup.getSelection() == newRadioButton.getModel();
	}

	public boolean isExistFile() {
		return selectButtonGroup.getSelection() == fromRadioButton.getModel();
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
		setSize(new Dimension(535, 241));
		setVisible(true);
	}

	public void showMessage(String messageInfo, boolean isError) {
		messagePanel.showMessage(messageInfo, isError);
		okButton.setEnabled(!isError);
	}

	public void showMessage(String messageInfo) {
		showMessage(messageInfo, false);
	}

	public void showError(String messageInfo) {
		showMessage(messageInfo, true);
	}

	public String getFileName() {
		return Utilities.getFilePathName(getFilepath(), nameTextField.getText() + "."
				+ Translator.getString("ReportFileExtension"));
	}

	public void updateState() {
		if (nameTextField.getText().equals("")) {
			showError(Translator.getString("ReportDialog.Name.Error"));
			return;
		}
		String filePathName = getFileName();
		try {
			java.io.File myFile = new java.io.File(filePathName);
			if (myFile.exists()) {

				showError(Translator.getString("ReportDialog.File.Exists"));
				return;
			}
			// else myFilePath.mkdir();
		} catch (Exception e) {
			showError(Translator.getString("ReportDialog.CreateReportError") + e.getMessage());
			return;
		}

		showMessage(Translator.getString("ReportDialog.OK"));
		// showMessage(filePathName);
	}

	public void selectFile() {
		try {
			NamedInputStream in = org.interpss.editor.util.Utilities
					.provideInput(Translator.getString("ReportFileExtension"),
							Translator
									.getString("ReportFileExtensionDescription"));
			if (in != null) {
				setFile(org.interpss.editor.util.Utilities.OpenReportFile(
						graphpad, in.getName()));
				this.setSrcFileName(in.getName());
				dirTextField.setText(in.getName());
				if (getFile() == null) {
					showError(Translator.getString("ReportDialog.CantOpenError"));
					return;
				}
			}
		} catch (Exception ex) {
			showError(ex.toString());
			ex.printStackTrace();
		}
	}

}

