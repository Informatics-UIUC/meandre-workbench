package org.meandre.workbench.client.beans;


//==============
// Java Imports
//==============

//===============
// Other Imports
//===============

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * <p>Title: Workbench Running Flow Bean</p>
 *
 * <p>Description: A bean that holds information about a running menadre
 * flow.</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: NCSA</p>
 *
 * @author D. Searsmith
 * @version 1.0
 */

public class WBRunningFlow implements IsSerializable {

    //==============
    // Data Members
    //==============

    /** The flow ID */
    private String sFlowID;

    /** The webui link */
    private String sWebUIURL;

    //==============
    // Constructors
    //==============

    public WBRunningFlow() {}


    /** Create a bean with the information about a running flow.
     *
     * @param sID The flow ID
     * @param sURL The webUI URL
     */
    public WBRunningFlow(String sID, String sURL) {
        sFlowID = sID;
        sWebUIURL = sURL;
    }


    //================
    // Public Methods
    //================

    /** Gets the flow ID.
     *
     * @return The flow ID
     */
    public String getID() {
        return sFlowID;
    }

    /** Returns the web UI URL
     *
     * @return The web UI URL
     */
    public String getWebUIURL() {
        return sWebUIURL;
    }
}
