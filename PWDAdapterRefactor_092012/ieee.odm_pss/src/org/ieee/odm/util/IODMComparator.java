package org.ieee.odm.util;

import java.util.Comparator;

import org.ieee.odm.model.IODMModelParser;

public interface IODMComparator<T> extends Comparator<T> {
	IODMModelParser getBaseParser();
}
