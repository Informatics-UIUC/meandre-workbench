package org.meandre.workbench.client.beans;

//==============
// Java Imports
//==============
import java.util.Set;
import java.util.Date;
import java.util.HashSet;

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
public class WBFlow implements IsSerializable {

    //==============
    // Data Members
    //==============

    /** Flow ID */
    private String _id = null;

    /** The name of the executable component */
    private String sName = null;

    /** The description of the executable component */
    private String sDescription = null;

    /** The rights of the executable component */
    private String sRights = null;

    /** The creator of the executable component */
    private String sCreator = null;

    /** The date of the executable component */

    /** Flow creation date */
    private Date dateCreation = null;

    /**
     * Set of executable component instances for this flow.
     *
     * @gwt.typeArgs <org.seasr.client.beans.WBComponentInstance>
     */
    private Set setExecutableComponentInstances = null;

    /**
     * Instanctiated connections between instantiated components.
     *
     * @gwt.typeArgs <org.seasr.client.beans.WBComponentConnection>
     */
    private Set setConnectorDescription = null;

    /**
     * The base URL to use in defining this flow. NOTE: this is only used on the
     * client side when preparing to save a new flow.  This string is input by
     * the user if they wish to override the default.
     */
    private String _baseURL = "";

    /**
     * Tags for this flow.
     */
    private WBTags tagDesc = null;

    public WBFlow(
            String id,
            String sName,
            String sDescription,
            String sRights,
            String sCreator,
            Date dateCreation,
            Set setExecutableComponentInstances,
            Set setConnectorDescription,
            WBTags tagsDesc
            ) {
        this._id = id;
        this.sName = sName;
        this.sDescription = sDescription;
        this.sRights = sRights;
        this.sCreator = sCreator;
        this.dateCreation = dateCreation;
        this.setExecutableComponentInstances = setExecutableComponentInstances;
        this.setConnectorDescription = setConnectorDescription;
        this.tagDesc = tagsDesc;
    }


    public WBFlow() {
        this._id = "";
        this.sName = "";
        this.sDescription = "";
        this.sRights = "";
        this.sCreator = "";
        this.dateCreation = new Date();
        this.setExecutableComponentInstances = new HashSet();
        this.setConnectorDescription = new HashSet();
        this.tagDesc = new WBTags();
    }

    /** Sets the executable component resource.
     *
     * @param res The resource
     */
    public void setFlowID(String id) {
        _id = id;
    }

    /** Returns the executable component resource.
     *
     * @return The resource
     */
    public String getFlowID() {
        return _id;
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

    /** Set the rights of the component.
     *
     * @param sRightsText The rights
     */
    public void setRights(String sRightsText) {
        sRights = sRightsText;
    }

    /** Returns the rights of the component.
     *
     * @return The rights
     */
    public String getRights() {
        return sRights;
    }

    /** Sets the creator of the component.
     *
     * @param sCreator The creator
     */
    public void setCreator(String sCreator) {
        this.sCreator = sCreator;
    }

    /** Returns the creator of the component.
     *
     * @return The creator
     */
    public String getCreator() {
        return sCreator;
    }

    /** Returns the creation date of the component.
     *
     * @return The date
     */
    public Date getCreationDate() {
        return dateCreation;
    }

    /** Sets the creation date of the component.
     *
     * @param d  The date
     */
    public void setCreationDate(Date d) {
        dateCreation = d;
    }

    /** Adds an executable component instance.
     *
     * @param ecd The executable coponent instances to add
     */
    public void addExecutableComponentInstance(
            WBComponentInstance ecd) {
        setExecutableComponentInstances.add(ecd);
    }

    /** Removes an executable component instance.
     *
     * @param res The executable coponent instances resource to remove
     */
    public void removeExecutableComponentInstance(WBComponentInstance ecd) {
        setExecutableComponentInstances.remove(ecd);
    }

    /** Adds a component connection.
     *
     * @param ecd The component connection to add.
     */
    public void addComponentConnection(
            WBComponentConnection ecd) {
            setConnectorDescription.add(ecd);
    }

    /**
     * Removes a component connection.
     *
     * @param res The component connection to remove
     */
    public void removeComponentConnection(WBComponentConnection ecd) {
        setConnectorDescription.remove(ecd);
    }

    /** Returns the set of executable component instances.
     *
     * @return The set of executable coponent instances descriptions
     */
    public Set getExecutableComponentInstances() {
        return setExecutableComponentInstances;
    }

    /** Returns the set of connector descriptions.
     *
     * @return The connector description set
     */
    public Set getConnectorDescriptions() {
        return setConnectorDescription;
    }

    /** The tags linked to the flow.
     *
     * @return The tag set.
     */
    public WBTags getTags() {
        return tagDesc;
    }

    /** The tags linked to the flow.
     *
     * @param tags The tag set.
     */
    public void setTags(WBTags tags) {
        tagDesc = tags;
    }

    /**
     * Get the base URL to use when defining this flow.
     * @return String String that represents the base URL for this flow.
     */
    public String getBaseURL(){
        return this._baseURL;
    }

    /**
     * Set the base URL to use for defining this flow.
     * @param s String The string to use as the base URL for this flow.
     */
    public void setBaseURL(String s){
        this._baseURL = s;
    }


}
