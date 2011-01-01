package org.interpss.editor.project;
 
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.OutputStream;

import javax.swing.filechooser.FileFilter;

import org.interpss.editor.coreframework.GPGraphpad;
import org.interpss.editor.coreframework.IpssXmlDocument;
import org.interpss.editor.resources.Translator;
 
public class IpssXmlCodec {
	private static IpssXmlCodec _instance;
	
	/**
	 * file filter for this file format
	 */
	FileFilter fileFilter;
	
	public static IpssXmlCodec getInstance(GPGraphpad pad) {
		if (null == _instance) {
			_instance = new IpssXmlCodec(pad);
		}
		return _instance;
	}
	
	
	protected IpssXmlCodec(GPGraphpad pad) {
		fileFilter = new FileFilter() {
			/**
			 * @see javax.swing.filechooser.FileFilter#accept(File)
			 */
			public boolean accept(File f) {
				if (f == null)
					return false;
				if (f.getName() == null)
					return false;
				if (f.getName().endsWith(Translator.getString("XmlFileExtension")))
					return true;
				if (f.isDirectory())
					return true;

				return false;
			}

			/**
			 * @see javax.swing.filechooser.FileFilter#getDescription()
			 */
			public String getDescription() {
				return Translator.getString("XmlFileExtensionDescription"); 
			}
		};
	}
	
	public void write(OutputStream out, IpssXmlDocument doc) throws Exception {

		// don't try / catch this command
		// sothat we get error messages at the
		// frontend.
		// e.g. I you have not permissions to
		// write a file you should get an error message

		out = new BufferedOutputStream(out);
		out.write(doc.getText().getBytes());
		out.flush();
		out.close();
	}
	
	

}
