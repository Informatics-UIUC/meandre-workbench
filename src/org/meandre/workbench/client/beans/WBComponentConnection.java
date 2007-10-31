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
public class WBComponentConnection implements IsSerializable {

//==============
// Data Members
//==============

    /** The connector ID  */
    private String _connID = null;

    /** The resource ID of the source instance */
    private String resInstanceSource = null;

    /** The resource ID of the source instance data port */
    private String resInstanceDataPortSource = null;

    /** The resource ID of the target instance */
    private String resInstanceTarget = null;

    /** The resource ID of the target instance data port */
    private String resInstanceDataPortTarget = null;

//==============
// Constructors
//==============

    /** Creates an empty connector description.
     *
     */
    public WBComponentConnection() {
        this._connID = "";
        this.resInstanceSource = "";
        this.resInstanceDataPortSource = "";
        this.resInstanceTarget = "";
        this.resInstanceDataPortTarget = "";
    }

    /** Creates a connector description object with the given information.
     * @param resConnector The resource describing the connector
     * @param resInstanceSource The source instance
     * @param resInstanceDataPortSource The source instance port
     * @param resInstanceTarget The target instance
     * @param resInstanceDataPortTarget The target instance port
     */
    public WBComponentConnection(
            String resConnector,
            String resInstanceSource,
            String resInstanceDataPortSource,
            String resInstanceTarget, String resInstanceDataPortTarget
            ) {
        this._connID = resConnector;
        this.resInstanceSource = resInstanceSource;
        this.resInstanceDataPortSource = resInstanceDataPortSource;
        this.resInstanceTarget = resInstanceTarget;
        this.resInstanceDataPortTarget = resInstanceDataPortTarget;
    }

//================
// Public Methods
//================

    /** Sets the resource connector.
     *
     * @param res The source instance
     */
    public void setConnector(String res) {
        _connID = res;
    }


    /** Returns the resource connector.
     *
     * @return The source instance
     */
    public String getConnector() {
        return _connID;
    }

    /** Sets the source instance.
     *
     * @param res The source instance
     */
    public void setSourceInstance(String res) {
        resInstanceSource = res;
    }

    /** Returns the source instance.
     *
     * @return The source instance
     */
    public String getSourceInstance() {
        return resInstanceSource;
    }

    /** Sets the source instance port.
     *
     * @param res The source instance port
     */
    public void setSourceIntaceDataPort(String res) {
        resInstanceDataPortSource = res;
    }

    /** Returns the source instance port.
     *
     * @return The source instance port
     */
    public String getSourceIntanceDataPort() {
        return resInstanceDataPortSource;
    }

    /** Sets the target instance.
     *
     * @param res The target instance
     */
    public void setTargetInstance(String res) {
        resInstanceTarget = res;
    }

    /** Returns the target instance.
     *
     * @return The target instance
     */
    public String getTargetInstance() {
        return resInstanceTarget;
    }

    /** Sets the target instance port
     *
     * @param res The target instance port
     */
    public void setTargetIntaceDataPort(String res) {
        resInstanceDataPortTarget = res;
    }

    /** Returns the target instance port
     *
     * @return The target instance port
     */
    public String getTargetIntanceDataPort() {
        return resInstanceDataPortTarget;
    }

}
