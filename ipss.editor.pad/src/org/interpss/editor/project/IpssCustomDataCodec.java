package org.interpss.editor.project;

import java.io.BufferedOutputStream;
import java.io.OutputStream;

import org.interpss.editor.EditorSpringCtx;
import org.interpss.editor.coreframework.GPGraphpad;
import org.interpss.editor.coreframework.IpssCustomDocument;
import org.interpss.editor.io.CustomFileUtility;
import org.interpss.editor.jgraph.ui.app.IAppSimuContext;

import com.interpss.simu.SimuContext;
import com.interpss.spring.CoreCommonSpringFactory;

public class IpssCustomDataCodec {
	private static IpssCustomDataCodec _instance;
	
	public static String TextFileProjExt = "_ipssproj";
	/**
	 * file filter for this file format
	 */
	// FileFilter fileFilter; no need any more
	
	public static IpssCustomDataCodec getInstance(GPGraphpad pad) {
		if (null == _instance) {
			_instance = new IpssCustomDataCodec(pad);
		}
		return _instance;
	}
	
	
	protected IpssCustomDataCodec(GPGraphpad pad) {
		/* no need any more
		fileFilter = new FileFilter() {
			public boolean accept(File f) {
				if (f == null)
					return false;
				if (f.getName() == null)
					return false;
				if (f.getName().endsWith(Translator.getString("CustomDataFileExtension")))
					return true;
				if (f.isDirectory())
					return true;

				return false;
			}
			public String getDescription() {
				return Translator.getString("CustomDataFileExtensionDescription"); 
			}
		};
		*/
	}
	
	public void write(OutputStream out, IpssCustomDocument doc) throws Exception {

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
	
// Mike	public static IAppSimuContext read(String abpath) { since this a singleton, no need to static
	public IAppSimuContext read(String abpath, String version) {
		IAppSimuContext appSimuContext = EditorSpringCtx.getAppSimuContext();
		SimuContext simuCtx = (SimuContext)appSimuContext.getSimuCtx();
		boolean ok = CustomFileUtility.loadCustomFile(abpath, version, simuCtx);
		if (ok) {
			appSimuContext.setSimuNetDataDirty(false);
		}
		else {
			appSimuContext.setSimuCtx(null);
			CoreCommonSpringFactory.getIpssMsgHub().sendWarnMsg("Custom data file loading error, filename: " + abpath);
		}
		return appSimuContext;
	}
}
