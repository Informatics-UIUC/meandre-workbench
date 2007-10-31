package org.meandre.workbench.client.beans;

//==============
// Java Imports
//==============

import java.util.Set;
import java.util.HashSet;
import java.util.Date;

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
public class WBComponent implements IsSerializable {

    //==============
    // Data Members
    //==============

    /* Runnable Types */
    static final public String s_JAVA_RUNNABLE = "java";

    /** The resource for the executable component */
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
    private Date dateCreation = null;

    /** What kind of runnable component is being described */
    private String sRunnable = null;

    /** What is the firing policy */
    private String sFiringPolicy = null;

    /** What executable format does the runnable component take */
    private String sFormat = null;

    /**
     * The set of contexts required for execution
     *
     * @gwt.typeArgs <java.lang.String>
     */
    private Set setContext = null;

    /** The resource pointing to the executable component implementation */
    private String resLocation = null;

    /** The set of input data ports DataPortDescription
     *
     * @gwt.typeArgs <org.meandre.workbench.client.beans.WBDataport>
     * */
    private java.util.Set setInputs = null;

    /** The set of output data ports DataPortDescription
     *
     * @gwt.typeArgs <org.meandre.workbench.client.beans.WBDataport>
     * */
    private Set setOutputs = null;

    /** The property descriptions WBProperties*/
    private WBPropertiesDefinition pddProperties = null;

    /** The tag description WBTags*/
    private WBTags tagDesc = null;

    public WBComponent(){
        this._id = "";
        this.sName = "";
        this.sDescription = "";
        this.sRights = "";
        this.sCreator = "";
        this.dateCreation = new Date();
        this.sRunnable = "";
        this.sFiringPolicy = "";
        this.sFormat = "";
        this.resLocation = "";
        this.setContext = new HashSet();
        this.setInputs = new HashSet();
        this.setOutputs = new HashSet();
        this.pddProperties = new WBPropertiesDefinition();
        this.tagDesc = new WBTags();
    }

    /** Describes an executable component.
     *
     * @param sID The id for the wrapped executable component
     * @param sName The name of the component
     * @param sDescription The description of the component
     * @param sRights The rights of the component
     * @param sCreator The creator
     * @param dateCreation The data of creation
     * @param sRunnable What kind of runable component it is
     * @param sFiringPolicy The firing policy
     * @param sFormat The format for the implementation of the component
     * @param setInputs The set of input data ports
     * @param setOutputs The set of output data ports
     * @param pddProperties
     * @param tagDesc
     */
    public WBComponent(
            String sID,
            String sName,
            String sDescription,
            String sRights,
            String sCreator,
            Date dateCreation,
            String sRunnable,
            String sFiringPolicy,
            String sFormat,
            String resLocation,
            Set setContext,
            Set setInputs,
            Set setOutputs,
            WBPropertiesDefinition pddProperties,
            WBTags tagDesc
            ) {
        this._id = sID;
        this.sName = sName;
        this.sDescription = sDescription;
        this.sRights = sRights;
        this.sCreator = sCreator;
        this.dateCreation = dateCreation;
        this.sRunnable = sRunnable;
        this.sFiringPolicy = sFiringPolicy;
        this.sFormat = sFormat;
        this.resLocation = resLocation;
        this.setContext = setContext;
        this.setInputs = setInputs;
        this.setOutputs = setOutputs;
        this.pddProperties = pddProperties;
        this.tagDesc = tagDesc;
    }

    public void setID(String id){
        _id = id;
    }

    /** Returns the executable component resource.
     *
     * @return The resource
     */
    public String getID() {
        return _id;
    }

    /** Returns the components name.
     *
     * @return The name
     */
    public String getName() {
        return sName;
    }

    /** Returns the executable component description.
     *
     * @return The description
     */
    public String getDescription() {
        return sDescription;
    }

    /** Returns the rights of the component.
     *
     * @return The rights
     */
    public String getRights() {
        return sRights;
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

    /** Returns the runnable type.
     *
     * @return The runnable type
     */
    public String getRunnable() {
        return sRunnable;
    }

    /** Returns the firing policy.
     *
     * @return The firing policy
     */
    public String getFiringPolicy() {
        return sFiringPolicy;
    }

    /** Returns the format of the executable component implementations.
     *
     * @return The format of the executable component
     */
    public String getFormat() {
        return sFormat;
    }

    /** The location of the executable component.
     *
     * @return The location of the executable component
     */
    public String getLocation () {
        return resLocation;
    }

    /** The set of contextes associated to the context.
     *
     * @return The context set
     */
    public Set getContext () {
        return setContext;
    }

    /** The set of data ports that define the inputs of the executable component.
     *
     * @return The set of data ports
     */
    public Set getInputs() {
        return setInputs;
    }

    /**
     * Returns number of input ports
     * @return int
     */
    public int getNumInputs(){
        return setInputs.size();
    }

    /** The set of data ports that define the outputs of the executable component.
     *
     * @return The set of data ports
     */
    public Set getOutputs() {
        return setOutputs;
    }

    /**
     * Returns number of output ports
     * @return int
     */
    public int getNumOuputs(){
        return setOutputs.size();
    }

    /** Returns the property descriptions for the descripbed executable component.
     *
     * @return The property definitions
     */
    public WBPropertiesDefinition getProperties() {
        return pddProperties;
    }

    /** Return the tags linked to the executable component.
     *
     * @return The tag set
     */
    public WBTags getTags() {
        return tagDesc;
    }

}
