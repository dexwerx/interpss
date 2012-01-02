 /*
  * @(#)ICaseInfoDialog.java   
  *
  * Copyright (C) 2006 www.interpss.org
  *
  * This program is free software; you can redistribute it and/or
  * modify it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE
  * as published by the Free Software Foundation; either version 2.1
  * of the License, or (at your option) any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  *
  * @Author Mike Zhou
  * @Version 1.0
  * @Date 09/15/2006
  * 
  *   Revision History
  *   ================
  *
  */

package org.interpss.editor.ui;

import org.ieee.odm.model.ODMModelParser;
import org.interpss.editor.SimuRunEnum;
import org.interpss.editor.jgraph.ui.edit.IFormDataDialog;

public interface ICaseInfoDialog extends IFormDataDialog {
    void setCaseType(SimuRunEnum caseType);
    SimuRunEnum getCaseType();
   
    void setRunStudyCaseFilename(String filename);
    
    void setDStabOutputScriptFilename(String filename);
    
    ODMModelParser getODMParser();    
}
