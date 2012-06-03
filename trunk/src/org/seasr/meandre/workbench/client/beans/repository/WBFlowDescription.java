/**
 * University of Illinois/NCSA
 * Open Source License
 *
 * Copyright (c) 2008, Board of Trustees-University of Illinois.
 * All rights reserved.
 *
 * Developed by:
 *
 * Automated Learning Group
 * National Center for Supercomputing Applications
 * http://www.seasr.org
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal with the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimers.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimers in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the names of Automated Learning Group, The National Center for
 *    Supercomputing Applications, or University of Illinois, nor the names of
 *    its contributors may be used to endorse or promote products derived from
 *    this Software without specific prior written permission.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * CONTRIBUTORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * WITH THE SOFTWARE.
 */

package org.seasr.meandre.workbench.client.beans.repository;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Boris Capitanu
 *
 */
public class WBFlowDescription implements IsSerializable, Cloneable {

    public static final String BASE_URL = "temp://seasr.org/flow/";

    /** The resource URI for the flow component */
    private String sResFlowURI = null;

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

    /** Instantiated executable components */
    private Set<WBExecutableComponentInstanceDescription> setExecutableComponentInstances = null;

    /** Hash table to access the instantiated components */
    private HashMap<String,WBExecutableComponentInstanceDescription> htExecutableComponentInstances = null;

    /** Instantiated connections between instantiated components */
    private Set<WBConnectorDescription> setConnectorDescription = null;

    /** The tags linked to the flow */
    private WBTagsDescription tagDesc = null;

    /** Create an empty flow description instance
     *
     */
    public WBFlowDescription () {
        this(
                null,           // sResFlowComponentURI
                "",             // sName
                "",             // sDescription
                "",             // sRights
                "",             // sCreator
                new Date(),     // dateCreation
                new HashSet<WBExecutableComponentInstanceDescription>(),    // setExecutableComponentInstances
                new HashSet<WBConnectorDescription>(),                      // setConnectorDescription
                new WBTagsDescription()                                     // tagsDesc
                );
    }

    public WBFlowDescription(String name, String baseURL) {
        this();
        this.sResFlowURI = baseURL + name.replaceAll(" |\t|/", "-") + "/";
    }

    /** Create a flow description instance
     *
     * @param sResFlowComponentURI The resource identifying the flow
     * @param sName The name of the flow
     * @param sDescription The description of the flow
     * @param sRights The rights of the flow
     * @param sCreator The creator
     * @param dateCreation The date of creation
     * @param setExecutableComponentInstances The set of executable components instances used by the flow
     * @param setConnectorDescription The set of connector descriptions
     * @param tagsDesc
     */
    public WBFlowDescription (
                String sResFlowComponentURI,
                String sName,
                String sDescription,
                String sRights,
                String sCreator,
                Date dateCreation,
                Set<WBExecutableComponentInstanceDescription> setExecutableComponentInstances,
                Set<WBConnectorDescription> setConnectorDescription,
                WBTagsDescription tagsDesc
            ) {
        this.sResFlowURI = sResFlowComponentURI;
        this.sName = sName;
        this.sDescription = sDescription;
        this.sRights = sRights;
        this.sCreator = sCreator;
        this.dateCreation = dateCreation;
        this.setExecutableComponentInstances = setExecutableComponentInstances;
        this.setConnectorDescription = setConnectorDescription;
        this.tagDesc  = tagsDesc;
        // Initialize the instance map
        this.htExecutableComponentInstances = new HashMap<String,WBExecutableComponentInstanceDescription>();
        for ( WBExecutableComponentInstanceDescription ecid:setExecutableComponentInstances )
            htExecutableComponentInstances.put(ecid.getExecutableComponentInstance(),ecid);
    }

    /** Sets the executable component resource.
     *
     * @param sResURI The resource
     */
    private void setFlowURI ( String sResURI ) {
        String oldFlowURI = this.sResFlowURI;

        // update component instances
        for (WBExecutableComponentInstanceDescription ecid : getExecutableComponentInstances()) {
            String oldInstanceURI = ecid.getExecutableComponentInstance();
            if (oldInstanceURI.startsWith(oldFlowURI)) {
                ecid.setExecutableComponentInstance(sResURI + oldInstanceURI.substring(oldFlowURI.length()));
                removeExecutableComponentInstance(oldInstanceURI);
                addExecutableComponentInstance(ecid);
            } else
                Log.warn("setFlowURI: the flow being saved ('" + sName + "') does not have proper RDF triple naming scheme; " +
                        "expected to start with: '" + oldFlowURI + "'  actual: '" + oldInstanceURI + "'");
        }

        // update connector descriptions
        for (WBConnectorDescription connector : getConnectorDescriptions()) {
            String oldConnectorURI = connector.getConnector();
            String oldSrcInstanceURI = connector.getSourceInstance();
            String oldTargetInstanceURI = connector.getTargetInstance();

            if (oldConnectorURI.startsWith(oldFlowURI))
                connector.setConnector(sResURI + oldConnectorURI.substring(oldFlowURI.length()));

            if (oldSrcInstanceURI.startsWith(oldFlowURI))
                connector.setSourceInstance(sResURI + oldSrcInstanceURI.substring(oldFlowURI.length()));

            if (oldTargetInstanceURI.startsWith(oldFlowURI))
                connector.setTargetInstance(sResURI + oldTargetInstanceURI.substring(oldFlowURI.length()));
        }

        sResFlowURI = sResURI;
    }

    /** Returns the executable component resource.
     *
     * @return The resource
     */
    public String getFlowURI() {
        return sResFlowURI;
    }

    public String getNormalizedFlowURI() {
        return (sResFlowURI.endsWith("/")) ? sResFlowURI : sResFlowURI + "/";
    }

    public String getBaseURI() {
        String uri = sResFlowURI;
        while (uri.endsWith("/"))
            uri = uri.substring(0, uri.length() - 1);

        return uri.substring(0, uri.lastIndexOf('/') + 1);
    }

    public void setBaseURI(String baseURI) {
        if (!baseURI.endsWith("/")) baseURI += "/";

        setFlowURI(baseURI + getFlowURI().substring(getBaseURI().length()));
    }

    /** Sets the components name.
     *
     * @param sName The name
     */
    public void setName( String sName ) {
        if (this.sName == null || !this.sName.equals(sName)) {
            this.sName=sName;
            setFlowURI(getBaseURI() + sName.toLowerCase().replaceAll(" |\t|/|'", "-") + "/");
        }
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
    public void setDescription ( String sDesc ) {
        this.sDescription=sDesc;
    }

    /** Returns the executable component description.
     *
     * @return The description
     */
    public String getDescription () {
        return sDescription;
    }

    /** Set the rights of the component.
     *
     * @param sRightsText The rights
     */
    public void setRights ( String sRightsText ) {
        sRights =  sRightsText;
    }

    /** Returns the rights of the component.
     *
     * @return The rights
     */
    public String getRights () {
        return sRights;
    }

    /** Sets the creator of the component.
     *
     * @param sCreator The creator
     */
    public void setCreator ( String sCreator) {
        this.sCreator=sCreator;
    }

    /** Returns the creator of the component.
     *
     * @return The creator
     */
    public String getCreator () {
        return sCreator;
    }

    /** Sets the creation date of the component.
     *
     * @param date The date
     */
    public void setCreationDate  ( Date d ) {
        dateCreation = d;
    }

    /** Returns the creation date of the component.
     *
     * @return The date
     */
    public Date getCreationDate () {
        return dateCreation;
    }

    /** Returns a given executable component instance description based
     * on the provide resource. Returns null if the instance is unknown
     *
     * @param sResURI The resource to locate
     * @return The executable component instance description
     */
    public String getExecutableComponentResourceForInstance ( String sResURI ) {

        WBExecutableComponentInstanceDescription ecd = htExecutableComponentInstances.get(sResURI);

        return (ecd==null)?null:ecd.getExecutableComponent();

    }

    /** Adds an executable component instance.
     *
     * @param ecid The executable component instances to add
     */
    public void addExecutableComponentInstance ( WBExecutableComponentInstanceDescription ecid ) {
        setExecutableComponentInstances.add(ecid);
        htExecutableComponentInstances.put(ecid.getExecutableComponentInstance(), ecid);
    }

    /** Removes an executable component instance.
     *
     * @param sResURI The executable component instances resource to remove
     */
    public void removeExecutableComponentInstance ( String sResURI ) {
        WBExecutableComponentInstanceDescription ecid = htExecutableComponentInstances.get(sResURI);
        if ( ecid!=null ) {
            setExecutableComponentInstances.remove(ecid);
            htExecutableComponentInstances.remove(ecid.getExecutableComponentInstance());
        }
    }

    /** Removes an executable component instance.
     *
     * @param ecd The executable component instances to remove
     */
    public boolean removeExecutableComponentInstance ( WBExecutableComponentInstanceDescription ecd ) {
        htExecutableComponentInstances.remove(ecd.getExecutableComponentInstance());
        return setExecutableComponentInstances.remove(ecd);
    }

    /** Returns the set of executable component instances.
     *
     * @return The set of executable component instances descriptions
     */
    public Set<WBExecutableComponentInstanceDescription> getExecutableComponentInstances () {
        return setExecutableComponentInstances;
    }

    /** Returns the set of connector descriptions.
     *
     * @return The connector description set
     */
    public Set<WBConnectorDescription> getConnectorDescriptions () {
        return setConnectorDescription;
    }

    /** The tags linked to the flow.
     *
     * @return The tag set.
     */
    public WBTagsDescription getTags () {
        return tagDesc;
    }

    /** Returns a readable name for the flow.
     *
     * @return A simple text description
     */
    public String toString() {
        return sName+" ("+sResFlowURI+")";
    }

    /**
     * Returns a deep copy of this object
     */
    public WBFlowDescription clone() {
        Set<WBExecutableComponentInstanceDescription> execCompInstances =
            new HashSet<WBExecutableComponentInstanceDescription>();
        for (WBExecutableComponentInstanceDescription ecid : this.setExecutableComponentInstances)
            execCompInstances.add(ecid.clone());

        Set<WBConnectorDescription> connectorDescriptions = new HashSet<WBConnectorDescription>();
        for (WBConnectorDescription connector : this.getConnectorDescriptions())
            connectorDescriptions.add(connector.clone());

        return new WBFlowDescription(
                this.sResFlowURI,
                this.sName,
                this.sDescription,
                this.sRights,
                this.sCreator,
                (Date)this.dateCreation.clone(),
                execCompInstances,
                connectorDescriptions,
                this.tagDesc.clone());
    }
}
