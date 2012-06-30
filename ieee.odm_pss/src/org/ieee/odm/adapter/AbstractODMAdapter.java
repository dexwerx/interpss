/*
 * @(#)UCTE_DEFAdapter.java   
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
 * @Date 02/11/2008
 * 
 *   Revision History
 *   ================
 *
 */

package org.ieee.odm.adapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.ieee.odm.common.ODMLogger;
import org.ieee.odm.model.IODMModelParser;

public abstract class AbstractODMAdapter implements IODMAdapter {
	private boolean status;
	//private Logger logger;
	private List<String> errMsgList;
	private IODMModelParser parser;
	
	public AbstractODMAdapter() {
		//this.logger = logger;
		this.status = true;                        
		this.errMsgList = new ArrayList<String>();
	}
	
	public String errMessage() {
		return errMsgList.toString();
	}

	public IODMModelParser getModel() {
		return this.parser;
	}

	public void logErr(String msg) {
		this.status = false;
		ODMLogger.getLogger().severe(msg);
		this.errMsgList.add(msg);
	}
	
	/*
	 *  single files 
	 */
	
	public boolean parseInputStream(InputStream input) {
		return parseInputStream(input, IODMModelParser.defaultEncoding);
	}
	
	public boolean parseInputStream(InputStream stream, String encoding) {
		try {
			final BufferedReader din = new BufferedReader(new InputStreamReader(stream));
			ODMLogger.getLogger().info("Parse input stream and create the parser object");
			try {
				this.parser = parseInputFile(din, encoding);
			} catch (IOException e) {
				ODMLogger.getLogger().severe(e.toString());
				this.errMsgList.add(e.toString());
				e.printStackTrace();
				return false;
			} finally { 
				din.close();
			}
		} catch (Exception e) {
			ODMLogger.getLogger().severe(e.toString());
			return false;
		} 
		return status;
	}
	
	public boolean parseInputFile(String filename) {
		try {
			final File file = new File(filename);
			final InputStream stream = new FileInputStream(file);
			ODMLogger.getLogger().info("Parse input file and create the parser object, " + filename);
			boolean b = parseInputStream(stream);
			stream.close();
			return b;
		} catch (Exception e) {
			ODMLogger.getLogger().severe(e.toString());
			this.errMsgList.add(e.toString());
			e.printStackTrace();
			return false;
		}
	}

	public boolean parseFileContent(String fileContent) {
		return parseFileContent(fileContent, IODMModelParser.defaultEncoding);
	}
	
	public boolean parseFileContent(String fileContent, String encoding) {
		try {
			final String[] strList = fileContent.split("\n");
			ODMLogger.getLogger().info("Parse input fileContent and create the parser object, first line: " + strList[0]);
			parseInputFile( new IFileReader() {
				private int cnt = 0;
				public String readLine() throws Exception {
					if (cnt < strList.length)
						return strList[cnt++];
					else
						return null;
				}
			}, encoding);
		} catch (Exception e) {
			ODMLogger.getLogger().severe(e.toString());
			this.errMsgList.add(e.toString());
			e.printStackTrace();
			return false;
		}
		return status;
	}
	
	protected IODMModelParser parseInputFile(
			final java.io.BufferedReader din, String encoding) throws Exception {
		FileReader reader = new FileReader(din);
		return parseInputFile(reader, encoding);
	}	

	/*
	 *  multiple files 
	 */
	
	/**
	 * 
	 * @param streamAry
	 * @return
	 */
	private boolean parseInputStream(IODMAdapter.NetType type, InputStream[] streamAry, String encoding) {
		try {
			final BufferedReader[] dinAry = new BufferedReader[streamAry.length];
			int cnt = 0;
			for (InputStream stream : streamAry) {
				dinAry[cnt++] = new BufferedReader(new InputStreamReader(stream));
			}
			this.parser = parseInputFile(type, dinAry, encoding);
		} catch (Exception e) {
			ODMLogger.getLogger().severe(e.toString());
			this.errMsgList.add(e.toString());
			e.printStackTrace();
			return false;
		}
		return status;
	}

	public boolean parseInputFile(NetType type, String[] filenames) {
		return parseInputFile(type, filenames, IODMModelParser.defaultEncoding);
	}
	
	public boolean parseInputFile(IODMAdapter.NetType type, String[] filenameAry, String encoding) {
		try {
			final InputStream[] streamAry = new InputStream[filenameAry.length];
			int cnt = 0;
			for (String filename : filenameAry) {
				final File file = new File(filename);
				final InputStream stream = new FileInputStream(file);
				ODMLogger.getLogger().info("Parse input file and create the parser object, " + filename);
				streamAry[cnt++] = stream;
			}
			return parseInputStream(type, streamAry, encoding);
		} catch (Exception e) {
			ODMLogger.getLogger().severe(e.toString());
			this.errMsgList.add(e.toString());
			e.printStackTrace();
			return false;
		}
	}
	
	protected IODMModelParser parseInputFile(IODMAdapter.NetType type,
			final java.io.BufferedReader[] dinAry, String encoding) throws Exception {
		IFileReader[] fAry = new FileReader[dinAry.length];
		int cnt = 0;
		for (java.io.BufferedReader din: dinAry) {
			fAry[cnt++] = new FileReader(din);
		}
		return parseInputFile(type, fAry, encoding);
	}	

	/*
	 * abstract methods to be implemented
	 */
	abstract protected IODMModelParser parseInputFile(IFileReader din, String encoding) throws Exception;
	abstract protected IODMModelParser parseInputFile(IODMAdapter.NetType type, IFileReader[] din, String encoding) throws Exception;
	

	private class FileReader implements IFileReader {
		java.io.BufferedReader din = null;
		public FileReader(java.io.BufferedReader din) { this.din = din;}
		public String readLine() throws Exception {
			return din.readLine();
		}
	}
}