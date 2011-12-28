/*
 * Copyright (C) 2001-2004 Gaudenz Alder
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

package org.interpss.editor.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.GZIPOutputStream;

import org.interpss.custom.IpssFileAdapter;
import org.interpss.editor.coreframework.GPGraphpad;
import org.interpss.editor.coreframework.GPGraphpadFile;
import org.interpss.editor.coreframework.GPPluginInvoker;
import org.interpss.editor.coreframework.IpssCustomFile;
import org.interpss.editor.coreframework.IpssReportFile;
import org.interpss.editor.coreframework.IpssTextFile;
import org.interpss.editor.coreframework.IpssXmlFile;
import org.interpss.editor.coreframework.actions.FileService;
import org.interpss.editor.coreframework.actions.PreferencesService;
import org.interpss.editor.coreframework.jgraphsubclassers.GPGraphModel;
import org.interpss.editor.doc.IpssProjectItem;
import org.interpss.editor.form.base.BaseBranchForm;
import org.interpss.editor.jgraph.GraphSpringFactory;
import org.interpss.editor.jgraph.cells.GraphUtilFunc;
import org.interpss.editor.jgraph.ui.app.IAppSimuContext;
import org.interpss.editor.jgraph.ui.app.IAppStatus;
import org.interpss.editor.jgraph.ui.form.IGFormContainer;
import org.interpss.editor.project.IpssCustomDataCodec;
import org.interpss.editor.project.IpssGraphCodec;
import org.interpss.editor.resources.Translator;
import org.interpss.spring.PluginSpringFactory;
import org.interpss.ui.IProjectDataManager;
import org.jgraph.JGraph;
import org.jgraph.graph.CellViewFactory;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

import com.interpss.common.util.IpssLogger;
import com.interpss.common.util.StringUtil;
import com.interpss.spring.CoreCommonSpringFactory;

/**
 * Utility methods. A utility method is characterized as a method which is of
 * general utility and is not specific to GPGraphpad or JGraph.
 * 
 * IMPORTANT NOTE: MORE UTILITIES CAN BE FOUND IN PREVIOUS VERSIONS OF JGRAPHPAD
 * (3 SERIES), BUT THEY HAVE BEEN REMOVED BECAUSE NOT USED.
 * 
 * For example, this would include things like generic sorting algorithms,
 * parsing routines, standard error handling methods, etc.
 * 
 * It is important that this code be optimized, and secondly you should be
 * concerned about not reinventing the wheel...before adding content here you
 * should try and find another open source project that already implements said
 * functionality in a robust manner. A good place to look is: Apache/Jakarta
 * Commons.
 * 
 * There are many methods commented out in this class as many of these methods
 * were imported from different projects but not yet currently used. Please take
 * a look here first to see if anything that you need has already been
 * implemented.
 */
public final class Utilities {

	/**
	 * The Utilities class should never be instantiated and should not have any
	 * state data associated with it, and this constructor enforces that.
	 */
	private Utilities() {
	}

	/**
	 * Take the given string and chop it up into a series of strings on
	 * whitespace boundries. This is useful for trying to get an array of
	 * strings out of the resource file.
	 */
	public static String[] tokenize(final String input) {
		return tokenize(input, " \t\n\r\f");
	}

	public static String[] tokenize(final String input, final String delim) {
		if (input == null)
			return new String[] {};
		StringTokenizer t = new StringTokenizer(input, delim);
		String cmd[];

		cmd = new String[t.countTokens()];
		int i = 0;
		while (t.hasMoreTokens()) {
			cmd[i] = t.nextToken();
			i++;
		}

		return cmd;
	}

	/**
	 * Returns a random number between 0 and max.
	 */
	public static int rnd(int max) {
		return (int) (Math.random() * max);
	}

	/**
	 * parses the pattern and tries to parse each token as a float.
	 * 
	 * @return array with the float value for each token
	 */
	public static float[] parsePattern(final String pattern) {
		StringTokenizer st = new StringTokenizer(pattern, ",");
		float[] f = new float[st.countTokens()];
		if (f.length > 0) {
			int i = 0;
			while (st.hasMoreTokens())
				f[i++] = Float.parseFloat(st.nextToken());
		}
		return f;
	}

	/**
	 * Returns the classname without the package. Example: If the input class is
	 * java.lang.String than the return value is String.
	 * 
	 * @param cl
	 *            The class to inspect
	 * @return The classname
	 * 
	 */
	public static String getClassNameWithoutPackage(Class cl) {
		// build the name for this action
		// without the package prefix
		String className = cl.getName();
		int pos = className.lastIndexOf('.') + 1;
		if (pos == -1)
			pos = 0;
		String name = className.substring(pos);
		return name;
	}

	/* moved to WinUtilities in ipss.commom package
	public static void center(Window frame) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = frame.getSize();
		frame.setLocation(screenSize.width / 2 - (frameSize.width / 2),
				screenSize.height / 2 - (frameSize.height / 2));
	}
	*/

	public static NamedInputStream provideInput(String fileExtension,
			String extensionDescription) {
		
		return provideInput(fileExtension,extensionDescription,null);
	}

	
	public static NamedInputStream provideInput(List adapterList) {
		
		return provideInput(null,null,adapterList);
	}

	public static NamedInputStream provideInput(String fileExtension,
			String extensionDescription,List adapterList) {
		PreferencesService preferences = PreferencesService
				.getInstance(GPGraphpad.class);// TODO: check if that work!
		
		ArrayList recentFiles = new ArrayList();
		File lastDir = new File(".");
		String recent = preferences.get("recent", "").trim();
		if (recent.length() > 0) {
			recentFiles.addAll(Arrays.asList(recent.split("[|]")));
			lastDir = new File((String) recentFiles.get(0)).getParentFile();
		}
		FileService fileService = FileService.getInstance(lastDir);
		try {

//			String baseDir = Translator.getString("Install.Location");
//			if (!baseDir.endsWith("/")) {
//				baseDir = baseDir + "/";
//			}
			
			String baseDir = StringUtil.getInstallLocation();
			
			String defaultDirectory = baseDir
					+ Translator.getString("Project.File.Location");

			FileService.Open open = null;
			if (adapterList==null)
			{
				ExtensionFilter fileExtensionFilter = new ExtensionFilter(extensionDescription,fileExtension);
				open = fileService.open(defaultDirectory, null,fileExtensionFilter);
			}
			else
			{
				
				open = fileService.open(defaultDirectory, null,adapterList);
			}
			

			// seashell mod
			if (open.getInputStream() == null)
				return null;

			NamedInputStream input = new NamedInputStream();
			input.setInputStream(open.getInputStream());
			input.setName(open.getName());
			return input;
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static NamedOutputStream provideOutput(String fileExtension,
			String extensionDescription, String nameToBeConfirmed,
			Boolean isZipped) {
		PreferencesService preferences = PreferencesService
				.getInstance(GPGraphpad.class);// TODO: check if that work!
		ExtensionFilter fileExtensionFilter = new ExtensionFilter(
				extensionDescription, new String[] { fileExtension });

		ArrayList recentFiles = new ArrayList();
		File lastDir = new File(".");
		String recent = preferences.get("recent", "").trim();
		if (recent.length() > 0) {
			recentFiles.addAll(Arrays.asList(recent.split("[|]")));
			lastDir = new File((String) recentFiles.get(0)).getParentFile();
		}
		FileService fileService = FileService.getInstance(lastDir);

		try {

//			String baseDir = Translator.getString("Install.Location");
//			if (!baseDir.endsWith("/")) {
//				baseDir = baseDir + "/";
//			}
			
			String baseDir = StringUtil.getInstallLocation();

			String defaultDirectory = baseDir
					+ Translator.getString("Project.File.Location");

			FileService.Save save = fileService
					.save(defaultDirectory, nameToBeConfirmed,
							fileExtensionFilter, null, fileExtension);

			if (save.getOutputStream() == null)
				return null;

			FilterOutputStream out;
			if (isZipped.booleanValue()) {
				out = new GZIPOutputStream(save.getOutputStream());
			} else {
				out = new FilterOutputStream(save.getOutputStream());
			}
			nameToBeConfirmed = save.getName();
			NamedOutputStream output = new NamedOutputStream();
			output.setName(save.getName());
			output.setOutputStream(out);
			return output;

		} catch (IOException exception) {
			exception.printStackTrace();
			return null;
		}
	}

	public static GPGraphpadFile OpenGraphFile(GPGraphpad graphpad,
			InputStream in) {
		GraphSpringFactory.getIpssGraphicEditor().getAppStatus().busyStart(
				IAppStatus.BusyIndicatorPeriod,	"Load InterPSS Graphic File ...", "");
		GPGraphpadFile file = null;
		try {
			JGraph graph = GraphSpringFactory.getIpssGraph();

			BaseBranchForm.XmlBinding = false;
			GraphModel model = IpssGraphCodec.getInstance(graphpad).read(in, graph);
			BaseBranchForm.XmlBinding = true;
			GraphUtilFunc.rebuildLabelRelationship(graph);

			// project.getSimuAppContext().setSimuNetDataDirty(true);
			IGFormContainer formContainer = ((GPGraphModel) model).getGFormContainer();
			formContainer.setDataDirty(false);
			formContainer.rebuildRelation();

			CellViewFactory cellViewFactory = (CellViewFactory) GPPluginInvoker
					.instanciateObjectForKey("ViewFactory.class");
			file = new GPGraphpadFile(new GraphLayoutCache(model, cellViewFactory));

		} catch (Exception e ) {
			PluginSpringFactory.getEditorDialogUtil().showMsgDialog(
					"InterPSS Graphic File Open Error", e.toString());
			e.printStackTrace();
		}
		GraphSpringFactory.getIpssGraphicEditor().getAppStatus().busyStop("InterPSS Graphic File loaded");
		return file;
	}
	
	public static IpssTextFile OpenTextFile(GPGraphpad graphpad,
			String filepath) throws Exception {
		GraphSpringFactory.getIpssGraphicEditor().getAppStatus().busyStart(
				IAppStatus.BusyIndicatorPeriod,	"Load Text File ...", "");

		IpssTextFile file = new IpssTextFile(filepath);

//		file.setModified(false);
//		file.setFilePathName(filepath);
		//graphpad.setStatus("Text loaded, File:" + filepath); no need anymore
		CoreCommonSpringFactory.getIpssMsgHub().sendStatusMsg("Text File:" + filepath);
		
		GraphSpringFactory.getIpssGraphicEditor().getAppStatus().busyStop("Text File loaded, " + filepath);
		return file;
	}
	
	public static IpssReportFile OpenReportFile(GPGraphpad graphpad,
			String filepath) throws Exception {
		GraphSpringFactory.getIpssGraphicEditor().getAppStatus().busyStart(
				IAppStatus.BusyIndicatorPeriod,	"Load Report File ...", "");

		IpssReportFile file = new IpssReportFile(filepath);

//		file.setModified(false);
//		file.setFilePathName(filepath);
		//graphpad.setStatus("Report loaded, File:" + filepath); no need anymore
		CoreCommonSpringFactory.getIpssMsgHub().sendStatusMsg("Report File:" + filepath);
		
		GraphSpringFactory.getIpssGraphicEditor().getAppStatus().busyStop("Report file loaded, " + filepath);
		return file;
	}
	

	public static IpssCustomFile OpenCustomFile(GPGraphpad graphpad, String abpath, String version) throws Exception {
		GraphSpringFactory.getIpssGraphicEditor().getAppStatus().busyStart(
				IAppStatus.BusyIndicatorPeriod, "Load Custom data file ...", "");
		IpssLogger.getLogger().info("Load custom file: " + abpath);
		
		IpssCustomFile file = new IpssCustomFile();

		IAppSimuContext appSimuContext = IpssCustomDataCodec.getInstance(graphpad).read(abpath, version);
		if (appSimuContext == null) {
			PluginSpringFactory.getEditorDialogUtil().showMsgDialog("InterPSS Custom Text File Open Error", "");
			return null;
		} else {
			file.setSimuAppContext(appSimuContext);
			file.setModified(false);
			file.getSimuAppContext().getProjData().setDirty(false);
			file.getSimuAppContext().getProjData().setFilepath(abpath);
			file.getSimuAppContext().getProjData().setWorkspacePath(StringUtil.getWorkspacePath(abpath));
			file.getSimuAppContext().getProjData().setProjectName(StringUtil.getFileName(abpath));
			file.setFilePathName(abpath);
			//graphpad.setStatus("Custom Data loaded, File:" + abpath); no need anymore
			CoreCommonSpringFactory.getIpssMsgHub().sendStatusMsg("Custom Data, File:" + abpath);
		}

		GraphSpringFactory.getIpssGraphicEditor().getAppStatus().busyStop("Custom Data loaded, " + abpath);
		return file;
	}

	public static IpssXmlFile OpenXmlFile(GPGraphpad graphpad,
			String filepath) throws Exception {
		GraphSpringFactory.getIpssGraphicEditor().getAppStatus().busyStart(
				IAppStatus.BusyIndicatorPeriod,	"Load XML File ...", "");

		IpssXmlFile file = new IpssXmlFile(filepath);

//		file.setModified(false);
//		file.setFilePathName(filepath);
		//graphpad.setStatus("Text loaded, File:" + filepath); no need anymore
		CoreCommonSpringFactory.getIpssMsgHub().sendStatusMsg("XML File:" + filepath);
		
		GraphSpringFactory.getIpssGraphicEditor().getAppStatus().busyStop("XML File loaded, " + filepath);
		return file;
	}

	
	// load project data from DB
	public static IAppSimuContext loadProjectData(IpssProjectItem item) throws Exception  {
		IpssLogger.getLogger().info("Load project data from DB ...");
		IAppSimuContext appSimuContext = GraphSpringFactory.getIpssGraphicEditor().getCurrentAppSimuContext();
		IProjectDataManager projManager = PluginSpringFactory
				.getProjectDataDBManager();
		projManager.loadProjectDataFromDB(item.getProjDbId(), item
				.getName(), item.getFileNameNoExt(), appSimuContext);
		IpssLogger.getLogger().info(
				"Project set to projDbId = "
						+ appSimuContext.getProjData().getProjectDbId());
		item.setProjDbId(appSimuContext.getProjData().getProjectDbId());
		return appSimuContext;
	}
	
	// Copies src file to dst file.
	// If the dst file does not exist, it is created

	public static boolean copy(String srcFile, String dstFile) {
		try // must try and catch,otherwide will compile error
		{
			// instance the File as file_in and file_out
			FileInputStream in = new FileInputStream(new java.io.File(srcFile));
			FileOutputStream out = new FileOutputStream(new java.io.File(
					dstFile));
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) != -1)
				out.write(buf, 0, len);
			in.close();
			out.close();
			return (true); // if success then return true
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return (false); // if fail then return false
		}
	}

	public static void delFile(String filePathAndName) {
		try {
			String filePath = filePathAndName;
			filePath = filePath.toString();
			java.io.File myDelFile = new java.io.File(filePath);
			myDelFile.delete();

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();

		}

	}

	public static boolean renameFile(String oldname, String newname) {
		try {

			// File (or directory) with old name
			File file = new File(oldname);

			// File (or directory) with new name
			File file2 = new File(newname);

			if (file2.exists()) {
				return false;
			}

			// Rename file (or directory)
			return file.renameTo(file2);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			return false;
		}

	}

	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}
	
	public static boolean haveExt(List adapterList, String ext){
		for (int i = 0; i < adapterList.size(); i++) {
			IpssFileAdapter adapter = (IpssFileAdapter)adapterList.get(i);
			if (adapter.getExtension().equals(ext)) return true;
		}
		return false;
		
	}

	public static String getFileName(String filePathName){
			if (filePathName==null) return Translator.getString("NewGraph");
	    	
	//		String fileSeparator=System.getProperty("file.separator");
			if(filePathName.lastIndexOf("\\") >= 0)
			{
				return filePathName.substring(filePathName.lastIndexOf("\\") + 1);
			}
			if(filePathName.lastIndexOf("/") >= 0)
			{
				return filePathName.substring(filePathName.lastIndexOf("/") + 1);
			}
	//		if(filePathName.lastIndexOf(fileSeparator) >= 0)
	//		{
	//			return filePathName.substring(filePathName.lastIndexOf(fileSeparator) + 1);
	//		}
			
	    	return filePathName;
	    }

	public static String getFileNameNoExt(String filePathName){
	    	filePathName = Utilities.getFileName(filePathName);
	    	
	    	
			if (filePathName==null) return null;
	    	
	//		String fileSeparator=System.getProperty("file.separator");
			if(filePathName.lastIndexOf(".") >= 0)
			{
				return filePathName.substring(0,filePathName.lastIndexOf("."));
			}
	
			return filePathName;
	    }

	public static String getFileExt(String filePathName){
		if (filePathName==null) return "";
		
		if(filePathName.lastIndexOf(".") >= 0)
		{
			return filePathName.substring(filePathName.lastIndexOf(".") + 1);
		}
		return filePathName;
	}

	public static String getFilePathName(String filePath,String fileName){
	    	
	    	String fileSeparator = System.getProperty("file.separator");
	
	    	if ((filePath.endsWith("\\")) || (filePath.endsWith("/"))) {
			return filePath + fileName;
		}
	    	
	//    	if (filePath.endsWith(fileSeparator)) {
	//    		return filePath + fileName;
	//    	}
	    	return filePath + fileSeparator +fileName;
	//    	return filePath + "/" +fileName;
	    	
	    }
}
