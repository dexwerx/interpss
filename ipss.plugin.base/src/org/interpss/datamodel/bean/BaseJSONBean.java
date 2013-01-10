package org.interpss.datamodel.bean;

/**
 * Base bean class. The bean data model is intended for use in combination
 * with JSON. Wire communication efficiency is considered in the data structure
 * design while preserve readability.
 * 
 * @author mzhou
 *
 */
public abstract class BaseJSONBean {
	public String 
		id, 
		name, 
		desc;
}
