package org.interpss.editor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import com.interpss.common.util.IpssLogger;

public class IpssPropertiesLoader {
	public static String LogPtyFile = "properties/log.properties";
	public static String IpssPtyFile = "properties/ipss.properties";
	public static String EditorPtyFile = "properties/editor.properties";
	public static String UserPtyFile = "properties/user.properties";
	
	private static Properties appProps = new Properties();
	private static Properties editorProps = new Properties();
	private static Properties userProps = new Properties();
/*
	static {
		try {
			java.io.FileInputStream in = new java.io.FileInputStream(EditorPtyFile);
			appProps.load(in);
			in = new java.io.FileInputStream(UserPtyFile);
			userProps.load(in);
			in.close();
		} catch (FileNotFoundException e) {
			System.err.println(e);
		} catch (IOException e) {
			System.err.println(e);
		}
	}
*/	
	public static void loadProperties(boolean cmdLine) {
		IpssLogger.initLogger(LogPtyFile);
		try {
			java.io.FileInputStream in = new java.io.FileInputStream(IpssPtyFile);
			appProps.load(in);
			if (!cmdLine) {
				in = new java.io.FileInputStream(EditorPtyFile);
				editorProps.load(in);
				in = new java.io.FileInputStream(UserPtyFile);
				userProps.load(in);
				in.close();
			}
		} catch (FileNotFoundException e) {
			System.err.println(e);
		} catch (IOException e) {
			System.err.println(e);
		}
	}
	
	public static String getIpssString (final String sKey){
		return appProps.getProperty(sKey, null);
	}

	public static String getEditorString (final String sKey){
		return editorProps.getProperty(sKey, null);
	}
	
	public static String getUserPty (final String sKey){
		return userProps.getProperty(sKey, null);
	}

	public static void setUserPty (final String sKey, String sValue){
		userProps.setProperty(sKey, sValue);
		saveUserPty();
	}

	private static void saveUserPty (){
		try {
			java.io.FileOutputStream out = new java.io.FileOutputStream(UserPtyFile);
			userProps.store(out, "Saved by InterPSS GEditor"+new Date().toString());
			out.close();
		} catch (Exception e) {
			System.err.println(e);
		}
	}
}
