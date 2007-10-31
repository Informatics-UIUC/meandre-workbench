package org.seasr.client.beans;

//==============
// Java Imports
//==============

import java.util.HashMap;
import java.util.Collection;
import java.util.Map;

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
public class WBPropertiesDefinition extends WBProperties implements
        IsSerializable {

//==============
// Data Members
//==============

    /** The property description.
     *
     *@gwt.typeArgs <java.lang.String,java.lang.String>
     **/
    private Map _htDescriptions = null;

//==============
// Constructors
//==============

    public WBPropertiesDefinition() {
        super(new HashMap());
        _htDescriptions = new HashMap();
    }

    /** Create a property description definition.
     *
     * @param htValues The values
     * @param htDescriptions The descriptions
     */
    public WBPropertiesDefinition(Map htValues, Map htDescriptions) {
        super(htValues);
        _htDescriptions = htDescriptions;
    }




//================
// Public Methods
//================

    /** Returns the description of the property.
     *
     * @return The descriptions of the stored properties
     */
    public Collection getDescriptions() {
        return _htDescriptions.values();
    }

    /** Returns the description map.
     *
     * @return The descriptions map.
     */
    public Map getDescriptionsMap() {
        return _htDescriptions;
    }

    /** Return the description for a given key.
     *
     * @param sKey The key of the property to retrieve
     * @return The description value
     */
    public String getDescription(String sKey) {
        return (String) _htDescriptions.get(sKey);
    }

}
