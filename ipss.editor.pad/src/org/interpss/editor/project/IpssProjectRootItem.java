package org.interpss.editor.project;

import org.interpss.editor.resources.Translator;

import com.interpss.common.ui.Workspace;

public class IpssProjectRootItem {
	public static final String rootText = "My Workspace";
	
	  public IpssProjectRootItem() {
		super();
	  }
	  
	public String getRootText(){
		  
//		  int projectCount = AppContext.getInstance().getProjectCount();
//		  
//		  String countString = "(Empty)";
//		  if (projectCount>0)
//			  countString = "( "+projectCount+" project"+((projectCount>1)? "s":"")+" )";
//		  return rootText+countString;
		String text = Translator.getString("WorkSpace.Label");
		if (Workspace.getCurrentType() == Workspace.Type.Sample)
			text = Translator.getString("WorkSpace.Label.Sample");
		if (text != null)
			return text;
		return rootText;
	}
	
		public String toString() {
			return getRootText();
		}
		
}
