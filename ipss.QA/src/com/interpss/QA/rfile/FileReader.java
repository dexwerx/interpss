 /*
  * @(#)FileReader.java   
  *
  * Copyright (C) 2006-2010 www.interpss.org
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
  * @Date 01/20/2011
  * 
  *   Revision History
  *   ================
  *
  */

package com.interpss.QA.rfile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.interpss.common.util.IpssLogger;

public class FileReader {
	public static enum Type {PSSEAclfResult, BPAAclfResult, PSLFAclfResult};
	
	private String filepath = null;
	
	public FileReader(String filepath) {
		this.filepath = filepath;
	}
	
	public void processFile(IFileProcessor procer) {
		String str = null;
		try {
			final File file = new File(this.filepath);
			final InputStream stream = new FileInputStream(file);
			final BufferedReader din = new BufferedReader(new InputStreamReader(stream));		
			
	      	do {
	          	str = din.readLine(); 
	          	if (str != null)
	          		procer.processLine(str);
	        } while (str != null);
			
		} catch (Exception e) {
			IpssLogger.getLogger().severe(e.toString() + ", " + str);
			e.printStackTrace();
		}
	}
}
