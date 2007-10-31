package org.meandre.workbench.client.beans;

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
public class WBDataport implements IsSerializable {

//==============
// Data Members
//==============

    /** The resource ID of the data port */
    private String _compID = null;

    /** The relative identifier of the port */
    private String sIdentifier = null;

    /** The pretty name of the data port */
    private String sName = null;

    /** The description of the data port */
    private String sDescription = null;

//==============
// Constructors
//==============

    public WBDataport() {
        this._compID = "";
        this.sIdentifier = "";
        this.sName = "";
        this.sDescription = "";
    }

    /** Creates a data port description based on the given information.
     *
     * @param res The resource locator
     * @param sIdent The relative port identifier
     * @param sName The name of the port
     * @param sDesc the description of the port
     */
    public WBDataport(String res, String sIdent, String sName, String sDesc) {
        this._compID = res;
        this.sIdentifier = sIdent;
        this.sName = sName;
        this.sDescription = sDesc;
    }


//================
// Public Methods
//================

    /** Returns the resource of this data port.
     *
     * @return The resource
     */
    public String getResourceID() {
        return _compID;
    }

    /** Returns the identifier of the data port
     *
     * @return The identifier
     */
    public String getIdentifier() {
        return sIdentifier;
    }

    /** Returns the name of the data port
     *
     * @return The name
     */
    public String getName() {
        return sName;
    }

    /** Returns the description of the data port
     *
     * @return The description
     */
    public String getDescription() {
        return sDescription;
    }

}
