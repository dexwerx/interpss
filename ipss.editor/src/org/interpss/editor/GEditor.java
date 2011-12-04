package org.interpss.editor;

    
import java.applet.Applet;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.interpss.editor.coreframework.GPGraphpad;
import org.interpss.editor.coreframework.GPSessionParameters;
import org.interpss.editor.resources.ImageLoader;
import org.interpss.editor.resources.Translator;
import org.interpss.editor.util.SmartFrame;
import org.interpss.spring.EditorSpringFactory;
import org.interpss.ui.WinUtilities;
import org.interpss.ui.Workspace;

import com.interpss.common.util.IpssLogger;
import com.interpss.common.util.StringUtil;

/**
 * A class with some static methods (including main and init) to properly
 * instanciate a GPGraphpad (actually a GPGraphpad JPanel) which is the main
 * JPanel where mutli JGraph document and GUI are displayed. You can do it
 * either as an application, either as an applet.
 * 
 * @see org.interpss.editor.coreframework.GPGraphpad

 	-g GridGain for grid computing using GridGain
 */

public class GEditor extends Applet {
	private static final long serialVersionUID = 1;
	
	public static String Parm_GridGain = "GridGain";

	public static String Pty_CurrentWorkspace = "workspace.current";
	public static String Pty_UserWorkspace = "workspace";
	public static String Pty_SampleWorkspace = "sample_ws";

	private static GPSessionParameters sessionParameters;
	private static GPGraphpad pad = null;

	private static JWindow frame = null;

	public static GPGraphpad getGraphPad() {
		return GEditor.pad;
	}
	
	public static GPSessionParameters getSessionParameters() {
		return sessionParameters;
	}

	private static void setWorkspaceDirectory() {
		String str = IpssPropertiesLoader.getUserPty(Pty_CurrentWorkspace);
		if (str == null) {
			str = Translator.getString(Pty_UserWorkspace);
			IpssPropertiesLoader.setUserPty(Pty_CurrentWorkspace, str);
		}
		EditorSpringFactory.getAppContext().setWorkspaceDir(StringUtil.getInstallLocation() + str);
	}
	
	/*
	 * Main method for creating a JGraphpad in an application deployed either
	 * offline, either via webstart
	 */
	public static void init(String[] args) {
		// parse cmd line parameters
		parseCmdLineParameters(args);

	 	// start splash ...
		JLabel info = new JLabel(Translator.getString("Splash.Init"), SwingConstants.CENTER);

		frame = new JWindow();
		showSplash(frame, info);
		showSplashInfo(info,Translator.getString("Splash.Construct"));
		
		// set workspace
		setWorkspaceDirectory();
		
		// load ref data from DB
		EditorSpringFactory.getRefDataManager().loadAllRefData();
		showSplashInfo(info,Translator.getString("Splash.Database"));

		// set the look and feel
		try {
			// Try GTK exlicitley for Java < 1.4.2
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
		} catch (Exception exc) {
			try {
				UIManager.setLookAndFeel(Translator.getString("LookAndFeel.class"));
			} catch (Exception exc2) {
				System.err.println("Error loading LookAndFeel: " + exc);
			}
		}

		// start the main editor window
		showSplashInfo(info,Translator.getString("Splash.Start"));

		try {
			GEditor.pad = EditorSpringFactory.getGraphicEditor();
			String str = IpssPropertiesLoader.getUserPty(Pty_CurrentWorkspace);
			if (str.equals(Translator.getString("WorkSpace.Location")))
				Workspace.setCurrentType(Workspace.Type.User);
			else
				Workspace.setCurrentType(Workspace.Type.Sample);
			
			GEditor.pad.createEditorPanel(sessionParameters);
		} catch (Exception e) {
			info.setText(e.getMessage());
			e.printStackTrace();
			System.err.println(e.getMessage());
		} finally {
			frame.dispose();
		}
	}

	private static void showSplashInfo(JLabel info,String infoMesssage) {
		info.setText(infoMesssage);
		IpssLogger.getLogger().info(infoMesssage);
	}
	
	/**
	 * By default we put the GPGraphpad in a JFrame
	 * @return
	 */
	public static JFrame createFrame(GPGraphpad pad) {
		JFrame frame = new SmartFrame();
		frame.setName("MainGraphpad"); 
		frame.setIconImage(ImageLoader.getImageIcon(Translator.getString("Icon")).getImage());
		frame.setTitle(Translator.getString("Title"));
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.getContentPane().add(pad.getEditorPanel(), BorderLayout.CENTER);
		frame.addWindowListener(pad.getAppCloser());
		frame.setVisible(true);
		return frame;
	}

	/**
	 * Automatic entry point when deploying JGraphpad as an applet
	 */
	public void init() {
		sessionParameters = new GPSessionParameters();
		setLayout(new BorderLayout());
		setBackground(Color.white);
		JButton button = new JButton("Start");
		button.setIcon(ImageLoader.getImageIcon(Translator.getString("Icon")));
		add(button, BorderLayout.CENTER);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				launchFromApplet();
			}
		});
		button.setPreferredSize(getSize());
		launchFromApplet();
		button.revalidate();
	}
	
	/**
	 * This method is called when the user press the applet button and also
	 * at the applet start up. The button is especially handy to refresh the
	 * applet without having to reload the page.
	 */
	public void launchFromApplet() {
		sessionParameters.setApplet(this);
		GPGraphpad pad = new GPGraphpad(sessionParameters);
		pad.init();
		JFrame gpframe = createFrame(pad);
		gpframe.getContentPane().add(pad);
		gpframe.addWindowListener(pad.getAppCloser());
		gpframe.setVisible(true);
		tryToLoadFile(pad);
	}

	/**
	 * If a file has been specified by command line, applet or webstart
	 * argument, then we try to open it
	 * 
	 * @param pad
	 */
	public static void tryToLoadFile(GPGraphpad pad) {
		sessionParameters = pad.getSessionParameters();
		String downloadPath = sessionParameters.getParam(
				GPSessionParameters.DOWNLOADPATH, false);
		if (downloadPath != null && !downloadPath.equals("")) {
			try {
				/*
				String protocol = sessionParameters.getParam(
						GPSessionParameters.PROTOCOL, true);
				String hostName = sessionParameters.getParam(
						GPSessionParameters.HOSTNAME, true);
				String hostPort = sessionParameters.getParam(
						GPSessionParameters.HOSTPORT, true);
				URL url = new URL(protocol, hostName, Integer
						.parseInt(hostPort), downloadPath);
				 */
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(pad, ex.getLocalizedMessage(),
						Translator.getString("Error"),
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Display the splash picture at startup
	 * 
	 * @param frame
	 * @param info
	 */
	public static void showSplash(JWindow frame, JLabel info) {
		ImageIcon logoIcon = ImageLoader.getImageIcon(Translator
				.getString("Splash"));
		info.setForeground(Color.black);
		JLabel lab = new JLabel(logoIcon) {
			private static final long serialVersionUID = 1;
			public void paint(Graphics g) {
				super.paint(g);

				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);

				g2.setFont(new Font("Arial", Font.BOLD, 27));
				g2.setColor(Color.WHITE);
				Composite originalComposite = g2.getComposite();
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));

				g2.setFont(new Font("Arial", Font.BOLD, 12));

				g2.drawString(IpssPropertiesLoader.getIpssString("Prog.name.editor")+ " " + IpssPropertiesLoader.getIpssString("Prog.version"), 10, 185);

				g2.setColor(Color.WHITE);
				g2.setFont(new Font("Arial", Font.BOLD, 10));
				String copyright = Translator.getString("Copyright");
				if (copyright != null)
					g2.drawString(copyright, 10, 202);
				g2.setComposite(originalComposite);
			}
		};

		frame.getContentPane().add(lab, BorderLayout.CENTER);
		lab.setLayout(new BorderLayout());
		lab.add(info, BorderLayout.SOUTH);
		lab.setBorder(BorderFactory.createRaisedBevelBorder());
		info.setVerticalAlignment(SwingConstants.CENTER);
		info.setHorizontalAlignment(SwingConstants.CENTER);
		info.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		info.setPreferredSize(new Dimension(lab.getWidth(), 24));
		info.setForeground(Color.WHITE);
		frame.pack();
		WinUtilities.center(frame);
		frame.setVisible(true);
//		showSplashInfo(info,Translator.getString("Splash.Start"));
	}

	private static void parseCmdLineParameters(String[] args) {
		// we set up the session parameters
		sessionParameters = new GPSessionParameters();
		for (int i = 0; i < args.length; i = i + 2) {

			if (args[i].indexOf("-") < 0) {// simple precaution in case
											// arguments aren't well formed
				i = i - 1;
				continue;
			}
			sessionParameters.setParam(args[i], args[i + 1]);
		}		
	}
	
	public static void exitEditor() {
		pad.closeWorkspace(null);
		frame.dispose();
		System.exit(0);
	}
}
