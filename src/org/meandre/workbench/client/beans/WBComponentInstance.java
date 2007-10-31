package org.seasr.client.beans;

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
public class WBComponentInstance implements IsSerializable {

//==============
// Data Members
//==============

    /** The resource for the executable component */
    private String resExecutableComponentInstace = null;

    /** The module pointing to the resource */
    private WBComponent resComponent = null;

    /** The name of the executable component */
    private String sName = null;

    /** The description of the executable component */
    private String sDescription = null;

    /** The instance properties */
    private WBProperties pdProperties = null;

//==============
// Constructors
//==============

    /** Create an empty executable component instance description instance
     *
     *
     */
    public WBComponentInstance() {
        this.resExecutableComponentInstace = null;
        this.resComponent = null;
        this.sName = "";
        this.sDescription = "";
        this.pdProperties = null;
    }

    /** Create a executable component instance description instance
     *
     * @param resExecutableComponentInstance The resource identifying this instance
     * @param resComponent The component this instance belongs to
     * @param sName The name of the flow
     * @param sDescription The description of the flow
     * @param description The instance properties
     */
    public WBComponentInstance(
            String resExecutableComponentInstance,
            WBComponent resComponent,
            String sName,
            String sDescription,
            WBProperties pdProperties
            ) {
        this.resExecutableComponentInstace = resExecutableComponentInstance;
        this.resComponent = resComponent;
        this.sName = sName;
        this.sDescription = sDescription;
        this.pdProperties = pdProperties;
    }


//================
// Public Methods
//================

    /** Sets the instance resource.
     *
     * @param res The instance resources
     */
    public void setExecutableComponentInstance(String res) {
        resExecutableComponentInstace = res;
    }

    /** Returns the instance resource.
     *
     * @return The instance resources
     */
    public String getExecutableComponentInstance() {
        return resExecutableComponentInstace;
    }

    /** Set the executable component resource.
     *
     * @param res The resource
     */
    public void setExecutableComponent(WBComponent res) {
        resComponent = res;
    }

    /** Returns the executable component resource.
     *
     * @return The resource
     */
    public WBComponent getExecutableComponent() {
        return resComponent;
    }

    /** Sets the components name.
     *
     * @param sName The name
     */
    public void setName(String sName) {
        this.sName = sName;
    }

    /** Returns the components name.
     *
     * @return The name
     */
    public String getName() {
        return sName;
    }

    /** Sets the executable component description.
     *
     * @param sDesc The description
     */
    public void setDescription(String sDesc) {
        this.sDescription = sDesc;
    }

    /** Returns the executable component description.
     *
     * @return The description
     */
    public String getDescription() {
        return sDescription;
    }

    /** Sets the properties for the instance.
     *
     * @param props The property description
     */
    public void setProperties(WBProperties props) {
        pdProperties = props;
    }

    /** Return the properties for the instance.
     *
     * @return The property description
     */
    public WBProperties getProperties() {
        return pdProperties;
    }

}
