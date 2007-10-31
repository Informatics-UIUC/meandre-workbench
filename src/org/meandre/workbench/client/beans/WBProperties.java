package org.seasr.client.beans;

//==============
// Java Imports
//==============

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Collection;

//===============
// Other Imports
//===============

import com.google.gwt.user.client.rpc.IsSerializable;



/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class WBProperties implements IsSerializable {

//==============
// Data Members
//==============

    /** The property key.
     *
     *@gwt.typeArgs <java.lang.String,java.lang.String>
     **/
    private Map htValues = null;


//==============
// Constructors
//==============

    /** Create an empty property description.
     *
     * @param htValues The values
     */
    public WBProperties() {
        this.htValues = new HashMap();
    }

    /** Create a property description.
     *
     * @param htValues The values
     */
    public WBProperties(Map htValues) {
        this.htValues = htValues;
    }

//================
// Public Methods
//================

    /** Returns the keys of the properties.
     *
     * @return The keys
     */
    public Set getKeys() {
        return htValues.keySet();
    }

    /** Returns the values of the property.
     *
     * @return The values
     */
    public Collection getValues() {
        return htValues.values();
    }

    /** Returns the values map.
      *
      * @return The values map.
      */
     public Map getValuesMap() {
         return htValues;
     }

    /** Get the value for a given property value.
     *
     * @param sKey The key of the property to retrieve
     * @return The value
     */
    public String getValue(String sKey) {
        return (String) htValues.get(sKey);
    }

    /** Add a property to the properties.
     *
     * @param sKey The key
     * @param sValue The value
     */
    public void add(String sKey, String sValue) {
        htValues.put(sKey, sValue);
    }


    /** Remove a property from the properties.
     *
     * @param sKey The key
     */
    public void remove(String sKey) {
        htValues.remove(sKey);
    }

}
