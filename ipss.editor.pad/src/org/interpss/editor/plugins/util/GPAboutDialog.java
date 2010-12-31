/*
 * @(#)GPAboutDialog.java	2.0 1.2 11/11/02
 *
 * 6/01/2006: I, Raphpael Valyi, changed back the header of this file to LGPL
 * because nobody changed the file significantly since the last
 * 3.0 version of GPGraphpad that was LGPL. By significantly, I mean: 
 *  - less than 3 instructions changes could honnestly have been done from an old fork,
 *  - license or copyright changes in the header don't count
 *  - automaticaly updating imports don't count,
 *  - updating systematically 2 instructions to a library specification update don't count.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.interpss.editor.plugins.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Calendar;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import org.interpss.editor.IpssPropertiesLoader;
import org.interpss.editor.resources.Translator;



//TODO makes it works with parameters!
public class GPAboutDialog extends JDialog {

	public GPAboutDialog(Frame owner, String title, ImageIcon logo) {
		super(owner, title, true);
		setSize(new Dimension(450, 360));

		JTabbedPane mainTabs = new JTabbedPane();
		JPanel aboutPanel = new JPanel();
		JPanel creditsPanel = new JPanel();
		JPanel ipssCreditsPanel = new JPanel();
		mainTabs.addTab(Translator.getString("About"), aboutPanel);
		mainTabs.addTab(Translator.getString("JGraphCredits"), creditsPanel);
		mainTabs.addTab(Translator.getString("InterPSSCredits"), ipssCreditsPanel);
		getContentPane().add(mainTabs);
		setLocationRelativeTo(owner);
		setResizable(false);

		// Construct About Panel
		JLabel lab1 = new JLabel(logo);
		JLabel lab2 = new JLabel(IpssPropertiesLoader.getIpssString("Prog.name.editor") + " " + IpssPropertiesLoader.getIpssString("Prog.version"));
		lab2.setFont(lab1.getFont().deriveFont(Font.PLAIN, 18));
		JLabel lab3 = new JLabel("Based on "+org.jgraph.JGraph.VERSION);
		lab3.setFont(lab3.getFont().deriveFont(Font.PLAIN, 12));
		JLabel lab4 =
			new JLabel("(C) 2005-"
					+ Calendar.getInstance().get(Calendar.YEAR)
					+ " interpss.org. All rights reserved.");
		lab4.setFont(lab4.getFont().deriveFont(Font.PLAIN, 12));
		JLabel lab5 =
			new JLabel("Java:"+System.getProperty("java.version")+" OS: "+System.getProperty("os.name"));
		lab5.setFont(lab5.getFont().deriveFont(Font.PLAIN, 12));
		lab1.setBounds(10, 9, 20, 24);
		lab2.setBounds(40, 5, 360, 30);
		lab3.setBounds(40, 33, 360, 25);
		lab4.setBounds(40, 250, 360, 25);
		HTMLPane text = new HTMLPane();
		text.setOpaque(false);
		text.setText(
			Translator.getString("AboutText"));
		text.setBounds(40, 65, 400, 180);
		text.setFont(lab4.getFont());
		text.setEditable(false);
		aboutPanel.setLayout(null);
		aboutPanel.add(lab1);
		aboutPanel.add(lab2);
		aboutPanel.add(lab3);
		aboutPanel.add(text);
		aboutPanel.add(lab4);

		// Construct Credits Panel
		JTextArea credits = new JTextArea();
		creditsPanel.setLayout(new BorderLayout());
		creditsPanel.add(new JScrollPane(credits), BorderLayout.CENTER);
		credits.setOpaque(false);
		credits.setText(
				"The following people and groups have made the JGraph\n"
			+ "Project possible:\n\n"
			+ "Thanks to Prof. Moira Norrie, Prof. Bernhard Plattner\n"
			+ "and Prof. Gerhard Tr�ster at the Federal Institute of \n"
			+ "Technology (www.ethz.ch) for their support!\n\n"
			+ "Beat Signer from the Global Information Systems Group\n"
			+ "was instrumental in helping to get this project off\n"
			+ "the ground. He arranged that JGraph could be handed-\n"
			+ "in as a semester work.\n\n"
			+ "The new design was strongly influenced by Men Muheim's\n"
			+ "experiments with JGraph in another project. Thanks to\n"
			+ "Men for the redesign, and willingness to accept this\n"
			+ "work as a diploma thesis.\n\n"
			+ "Christophe Avare translated GPGraphpad to French,\n"
			+ "Shinji Nakamatsu provided the Japenese, Indosian and\n"
			+ "Thai versions. Thomas Suter, Lars Gersmann, Markus\n"
			+ "Schmidt, Antonio Caliano, Martina Huber and Andri\n"
			+ "Kr�mer suggested new features or read the drafts of\n"
			+ "the documentation. Farrukh Najmi helped to put up the\n"
			+ "CVS repository, and Alex Shapiro implemented a cool\n"
			+ "automatic layout for GPGraphpad (www.touchgraph.com).\n"
			+ "Claudio Rosati and David Larsson submitted bug fixes\n"
			+ "and new features, Francesco Candeliere translated the\n"
			+ "paper to Italian, and Michael Lawley has ported the\n"
			+ "documentation to the Docbook format.\n\n"
			+ "Special thanks to Van Woods, Dennis Daniels, Hallvard\n"
			+ "Tr�tteberg, Jenya Burstein, Sven Luzar, Rapha�l Valyi\n"
			+ "and all others who are helping to improve JGraph and/or\n"
			+ "GPGraphpad up to this day!\n\n"
			+ "The website and CVS repository are kindly hosted by\n"
			+ "sourceforge.net.\n");
		credits.setCaretPosition(0);
		credits.setEditable(false);

		// Construct Credits Panel
		JTextArea ipssCredits = new JTextArea();
		ipssCreditsPanel.setLayout(new BorderLayout());
		ipssCreditsPanel.add(new JScrollPane(ipssCredits), BorderLayout.CENTER);
		ipssCredits.setOpaque(false);
		ipssCredits.setText(
			"The following people and groups have made the InterPSS\n"
			+ "Project possible:\n\n"

			+ "The current InterPSS graphic editor was implmented by\n"
			+ "ShiZhao (Richard) Zhou. Karl Zhu made contritions at\n"
			+ "the early stage of the editor development.\n"

			+ "WenJie Zhai of Hunan University, China, under\n"
			+ "Professor Dr. XingRan Li, wrote the User Guide\n"
			+ "in Chinese.\n"

			+ "Dr. Ying Chen of Tsinghua University, China, contributed\n" 
			+ "the MatLab power flow data file adapter.\n"
			);
		ipssCredits.setCaretPosition(0);
		ipssCredits.setEditable(false);
}

	// Close on escape
	protected JRootPane createRootPane() {
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		JRootPane rootPane = new JRootPane();
		rootPane.registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				setVisible(false);
			}
		}, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
		return rootPane;
	}

}
