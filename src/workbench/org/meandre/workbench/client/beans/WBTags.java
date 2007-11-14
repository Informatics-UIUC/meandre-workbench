package org.meandre.workbench.client.beans;

//==============
// Java Imports
//==============

import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;

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
public class WBTags implements IsSerializable {

//==============
// Data Members
//==============

    /** The set of tags linked to a component
     *
     * @gwt.typeArgs <java.lang.String>
     * */
    private Set _setTags = null;

//==============
// Constructors
//==============


    /** Creates a tag description object.
     *
     * @param setTags The set of tags
     */
    public WBTags(Set setTags) {
        _setTags = setTags;
    }

    /** Creates an empty tag description object.
     *
     *
     */
    public WBTags() {
        _setTags = new HashSet();
    }

//================
// Public Methods
//================

    /** Returns the set of tags.
     *
     * @return The set of tags
     */
    public Set getTags() {
        return _setTags;
    }

    public String toString(){
        String ret = "";
        for (Iterator itty = _setTags.iterator(); itty.hasNext();){
            ret += (String)itty.next() + " ";
        }
        return ret.trim();
    }

    public void clear(){
        _setTags.clear();
    }
}
