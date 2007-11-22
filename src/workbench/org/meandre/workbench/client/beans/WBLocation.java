package org.meandre.workbench.client.beans;

//==============
// Java Imports
//==============

//===============
// Other Imports
//===============

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * <p>Title: Workbench Location Bean</p>
 *
 * <p>Description: Bean for holding information about a repository
 * location.</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: NCSA</p>
 *
 * @author Xavier Llor&agrave;
 * @author D. Searsmith
 * @version 1.0
 */
public class WBLocation implements IsSerializable {

    //==============
    // Data Members
    //==============

    /** The location URL */
    private String sLocation;

    /** The location description */
    private String sDescription;

    //==============
    // Constructors
    //==============

    public WBLocation() {}


    /** Creates a bean and sets the location and description.
     *
     * @param sLocation The location URL
     * @param sDescription The description
     */
    public WBLocation(String sLocation, String sDescription) {
        setLocation(sLocation);
        setDescription(sDescription);
    }

    //================
    // Public Methods
    //================


    /** Sets the URL location
     *
     * @param sLocation the sLocation to set
     */
    public void setLocation(String sLocation) {
        this.sLocation = sLocation;
    }

    /** Gets the URL location
     *
     * @return the sLocation
     */
    public String getLocation() {
        return sLocation;
    }

    /** Sets the location description
     *
     * @param description the sDescription to set
     */
    public void setDescription(String sDescription) {
        this.sDescription = sDescription;
    }

    /** Gets the location description
     *
     * @return the description
     */
    public String getDescription() {
        return sDescription;
    }
}
