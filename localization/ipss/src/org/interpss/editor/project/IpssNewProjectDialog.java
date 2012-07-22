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

import org.interpss.editor.doc.IpssProject;
import org.interpss.editor.resources.Translator;
import org.interpss.editor.util.Utilities;



public class IpssNewProjectDialog extends javax.swing.JDialog {
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

	private IpssProject project;

	private Frame parentFrame;
	
	private boolean isCancelExit;

	public IpssNewProjectDialog(Frame parent, String title) {
		super(parent, title, true);
		setResizable(false);

		this.parentFrame = parent;
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
				if (saveProject())
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
				// selectDirectory(browseButton, null);
			}
		});

		nameTextField.addCaretListener(new CaretListener() {
			public void caretUpdate(final CaretEvent e) {
				updateState();
			}
		});

	}

	protected void setBrowsePanelVisable() {
		if (selectButtonGroup.getSelection() == fromRadioButton
				.getModel()) {
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
		setTitle(Translator.getString("ProjectDialog.Title"));

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
		selectpanel.setBorder(new TitledBorder(null, Translator.getString("Contents"),
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));

		newRadioButton = new JRadioButton();
		selectpanel.add(newRadioButton);
		newRadioButton.setSelected(true);
		newRadioButton.setMnemonic('N');
		newRadioButton.setText(Translator.getString("ProjectDialog.CreateNewProjectFolderInWorkspace.Text"));		
		selectButtonGroup.add(newRadioButton);

		fromRadioButton = new JRadioButton();
		fromRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
			}
		});
		selectpanel.add(fromRadioButton);
		fromRadioButton.setMnemonic('x');
		fromRadioButton.setText(Translator.getString("ProjectDialog.CreateProjectFolderFromExistingSource.Text"));

		browsePanel = new JPanel();
		selectpanel.add(browsePanel);
		browsePanel.setLayout(new BorderLayout());

		dirTextField = new JTextField();
		dirTextField.setFocusAccelerator('d');
		browsePanel.add(dirTextField);

		dirLabel = new JLabel();
		dirLabel.setDisplayedMnemonic(KeyEvent.VK_D);
		browsePanel.add(dirLabel, BorderLayout.WEST);
		dirLabel.setText(Translator.getString("Directory")+":  ");

		browseButton = new JButton();

		browseButton.setMnemonic(Translator.getString("Browse.Mnemonic").toCharArray()[0]);
		browseButton.setText(Translator.getString("Browse"));
		browsePanel.add(browseButton, BorderLayout.EAST);
		selectButtonGroup.add(fromRadioButton);

		fromRadioButton.setEnabled(false);

		final JPanel panel = new JPanel();
		mainPanel.add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout());

		nameLabel = new JLabel();
		panel.add(nameLabel, BorderLayout.WEST);
		nameLabel.setDisplayedMnemonic(KeyEvent.VK_P);
		nameLabel.setText(Translator.getString("ProjectDialog.ProjectFolderName"));

		nameTextField = new JTextField();
		panel.add(nameTextField, BorderLayout.CENTER);

		nameTextField.setFocusAccelerator('p');

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

	public void setProject(IpssProject project) {
		this.project = project;

		if (project != null) {
			dirTextField.setText(project.getParentPath());
			nameTextField.setText(project.getProjectName());
		}
	}

	public IpssProject getProject() {
		return project;
	}
	
	public void setCancelExit(boolean isCancelExit) {
		this.isCancelExit = isCancelExit;
	}

	public boolean isCancelExit() {
		return isCancelExit;
	}

	public void init(IpssProject aProject) {
		setProject(aProject);
		// pack the form and display
		pack();
		setLocationRelativeTo(parentFrame);
		setSize(new Dimension(588, 241));
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

	public void updateState() {
		if (nameTextField.getText().equals("")) {
			showError(Translator.getString("ProjectDialog.ProjectName.Error"));
			return;
		}
		String filePathName = Utilities.getFilePathName(dirTextField.getText(),
				nameTextField.getText());
		try {
			java.io.File myFilePath = new java.io.File(filePathName);
			if (myFilePath.exists()) {
				showError(Translator.getString("ProjectDialog.FilePath.Exists"));
				return;
			}
			// else myFilePath.mkdir();
		} catch (Exception e) {
			showError(Translator.getString("CreateProjectError") + e.getMessage());
			return;
		}

		showMessage(Translator.getString("ProjectDialog.OK"));
		// showMessage(filePathName);
	}

	public boolean saveProject() {

		if (nameTextField.getText().equals("")) {
			showError(Translator.getString("ProjectDialog.ProjectName.Error"));
			return false;
		}
		String filePathName = Utilities.getFilePathName(dirTextField.getText(),
				nameTextField.getText());
		try {
			java.io.File myFilePath = new java.io.File(filePathName);
			if (myFilePath.exists()) {
				showError(Translator.getString("ProjectDialog.FilePath.Exists"));
				return false;
			} else if (!myFilePath.mkdirs()) {
				showError(Translator.getString("ProjectDialog.Error"));
				return false;
			}
			// else myFilePath.mkdir();
		} catch (Exception e) {
			showError(Translator.getString("ProjectDialog.Error")
					+ e.getMessage());
			return false;
		}

		project.setParentPath(dirTextField.getText());
		project.setProjectName(nameTextField.getText());
		return true;
	}

	//
	// public static void showDialog(Frame parent, String title, IpssProject
	// oldProject) {
	//
	// final IpssNewProjectDialog dialog = new
	// IpssNewProjectDialog(parent,title);
	//
	// dialog.pack();
	// dialog.setLocationRelativeTo(parent);
	//
	// dialog.addComponentListener(new ComponentAdapter() {
	// public void componentResized(ComponentEvent e) {
	// dialog.pack();
	// }
	// });
	// okButton.addActionListener(new ActionListener() {
	// public void actionPerformed(ActionEvent e) {
	// dialog.setVisible(false);
	// }
	// });
	//
	// cancelButton.addActionListener(new ActionListener() {
	// public void actionPerformed(ActionEvent e) {
	// dialog.setVisible(false);
	// dialog.setProject(null);
	// }
	// });
	//
	// dialog.setVisible(true);
	//
	// return dialog.getProject();
	// }// showDialog

	// static void selectDirectory(Component parent, String selectedFile) {
	// JDirectoryChooser chooser;
	//
	// if (System.getProperty("javawebstart.version") != null) {
	// chooser = new JDirectoryChooser(new FakeFileSystemView()) {
	// public void rescanCurrentDirectory() {
	// }
	// public void setCurrentDirectory(File dir) {
	// }
	// };
	// } else {
	// chooser = new JDirectoryChooser();
	// if (selectedFile != null) {
	// chooser.setSelectedFile(new File(selectedFile));
	// }
	// }
	//		    
	// JTextArea accessory = new
	// JTextArea(Translator.getString("selectDirectory.message"));
	// accessory.setLineWrap(true);
	// accessory.setWrapStyleWord(true);
	// accessory.setEditable(false);
	// accessory.setOpaque(false);
	// accessory.setFont(UIManager.getFont("Tree.font"));
	// chooser.setAccessory(accessory);
	//
	// chooser.setMultiSelectionEnabled(true);
	//
	// int choice = chooser.showOpenDialog(parent);
	// if (choice == JDirectoryChooser.APPROVE_OPTION) {
	// String filenames = "";
	// File[] selectedFiles = chooser.getSelectedFiles();
	// for (int i = 0, c = selectedFiles.length; i < c; i++) {
	// filenames += "\n" + selectedFiles[i];
	// }
	// JOptionPane.showMessageDialog(parent, Translator.getString(
	// "selectDirectory.confirm", new Object[] {filenames}));
	// } else {
	// JOptionPane.showMessageDialog(parent, Translator
	// .getString("selectDirectory.cancel"));
	// }
	// }

}
