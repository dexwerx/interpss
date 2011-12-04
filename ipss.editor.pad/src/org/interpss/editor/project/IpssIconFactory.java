package org.interpss.editor.project;

import javax.swing.ImageIcon;

import org.interpss.editor.coreframework.GPDocument;
import org.interpss.editor.coreframework.IpssCustomDocument;
import org.interpss.editor.coreframework.IpssReportDocument;
import org.interpss.editor.coreframework.IpssTextDocument;
import org.interpss.editor.coreframework.IpssXmlDocument;
import org.interpss.editor.resources.ImageLoader;
import org.interpss.editor.resources.Translator;
import org.interpss.editor.util.Utilities;
import org.interpss.spring.PluginSpringFactory;

public class IpssIconFactory {

	public static final ImageIcon ICON_GRAPH = ImageLoader
			.getImageIcon(Translator.getString("Project.Graph")),
			ICON_XML = ImageLoader.getImageIcon(Translator
					.getString("Project.XML")), ICON_CUS = ImageLoader
					.getImageIcon(Translator.getString("Project.CUS")),
			ICON_ROOT = ImageLoader.getImageIcon(Translator
					.getString("Project.Root")),
			ICON_PROJECT = ImageLoader.getImageIcon(Translator
					.getString("Project.Folder.Close")),
			ICON_TEXT = ImageLoader.getImageIcon(Translator
					.getString("Project.Text")), ICON_REPORT = ImageLoader
					.getImageIcon(Translator.getString("Project.Report"));

	public static ImageIcon getIconbyFileName(String filename) {
		if (filename.endsWith("ipss"))
			return ICON_GRAPH;
		// if (filename.endsWith("ipssdat"))
		if (Utilities.haveExt(PluginSpringFactory
				.getCustomFileAdapterList(), org.interpss.editor.util.Utilities
				.getFileExt(filename)))
			return ICON_CUS;
		if (filename.endsWith("xml"))
			return ICON_XML;
		if (filename.endsWith("txt"))
			return ICON_TEXT;
		if (filename.endsWith("ipssrpt"))
			return ICON_REPORT;

		return null;
	}

	public static ImageIcon getIconbyClass(Object o) {
		if (o instanceof GPDocument)
			return ICON_GRAPH;
		if (o instanceof IpssCustomDocument)
			return ICON_CUS;
		if (o instanceof IpssXmlDocument)
			return ICON_XML;
		if (o instanceof IpssTextDocument)
			return ICON_TEXT;
		if (o instanceof IpssReportDocument)
			return ICON_REPORT;

		return null;
	}
}
