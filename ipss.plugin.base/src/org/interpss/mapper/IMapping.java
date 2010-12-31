package org.interpss.mapper;

import com.interpss.common.exp.InterpssException;

/**
 * It defines methods to map from object to InterPSS object model or ODM model
 
 * @param <Tfrom> - from Model
 * @param <Tto> - to model Model
 */
public interface IMapping<Tfrom, Tto> {
    /**
     * Mapping object to the InterPSS model
     * 
     * @param from - from Model
     * @param model - InterPSS object Model
     */
    public boolean map2Model(Tfrom from, Tto model);

    /**
     * Mapping object to the InterPSS model
     * 
     * @param from - from Model
     * @return an InterPSS Model object
     */
    public Tto map2Model(Tfrom from) throws InterpssException;

    /**
     * Mapping object to the ODM model
     * 
     * @param from - from Model
     * @param odm - ODM Model
     */
    public boolean map2ODM(Tfrom from, Tto odm);

    /**
     * Mapping object to the ODM model
     * 
     * @param from - from Model
     * @return an ODM Model object
     */
    public Tto map2ODM(Tfrom from) throws InterpssException;
}
