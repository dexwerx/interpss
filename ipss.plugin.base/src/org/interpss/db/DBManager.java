/*
 * @(#)DBManager.java   
 *
 * Copyright (C) 2006 www.interpss.com
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

package org.interpss.db;

import java.io.IOException;
import java.io.Reader;

import org.interpss.ui.Workspace;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;
import com.interpss.common.util.IpssLogger;

public class DBManager {
	public static final int SQL_ACTION_INSERT = 1;
	public static final int SQL_ACTION_SELECT = 2;
	public static final int SQL_ACTION_UPDATE = 3;
	public static final int SQL_ACTION_DELETE = 4;

	public static String SQLMAP_CONFIG_PATH = "sqlMapConfig/SqlMapConfig.xml";
	public static String SQLMAP_SAMPLE_CONFIG_PATH = "sqlMapConfig/SqlMapSampleConfig.xml";
	public static String DB_SCHEMA_VERSION = "1.00.00";
	public static String IPSS_DB_NAME = "IpssDB";
	public static String SAMPLE_DB_NAME = "SampleDB";

	private static SqlMapClient sqlMap = null;
	private static SqlMapClient sqlMapSample = null;

	public static SqlMapClient getSqlMap() {
		if (Workspace.getCurrentType() == Workspace.Type.Sample) {
			return getSqlMapSample();
		} else {
			return getSqlMapIpss();
		}
	}

	public static String getCurrentDBName() {
		if (Workspace.getCurrentType() == Workspace.Type.Sample) {
			return SAMPLE_DB_NAME;
		} else {
			return IPSS_DB_NAME;
		}
	}

	private static SqlMapClient getSqlMapIpss() {
		if (sqlMap == null) {
			try {
				Reader configReader = Resources
						.getResourceAsReader(SQLMAP_CONFIG_PATH);
				sqlMap = SqlMapClientBuilder.buildSqlMapClient(configReader);
				IpssLogger.getLogger().info("Current DB connetion: IpssDB");
				return sqlMap;
			} catch (IOException e) {
				e.printStackTrace();
				IpssLogger.logErr(e);
			}
		}
		return sqlMap;
	}

	private static SqlMapClient getSqlMapSample() {
		if (sqlMapSample == null) {
			try {
				Reader configReader = Resources
						.getResourceAsReader(SQLMAP_SAMPLE_CONFIG_PATH);
				sqlMapSample = SqlMapClientBuilder
						.buildSqlMapClient(configReader);
				IpssLogger.getLogger().info("Current DB connetion: SampleDB");
				return sqlMapSample;
			} catch (IOException e) {
				e.printStackTrace();
				IpssLogger.logErr(e);
			}
		}
		return sqlMapSample;
	}
}
